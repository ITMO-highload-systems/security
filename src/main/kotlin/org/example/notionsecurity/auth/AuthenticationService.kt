package org.example.notionsecurity.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.notionsecurity.JwtService
import org.example.notionsecurity.auth.dto.AuthenticationRequest
import org.example.notionsecurity.auth.dto.AuthenticationResponse
import org.example.notionsecurity.auth.dto.RegisterRequest
import org.example.notionsecurity.token.Token
import org.example.notionsecurity.token.TokenRepository
import org.example.notionsecurity.user.User
import org.example.notionsecurity.user.UserRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.function.Consumer


@Service
class AuthenticationService(
    private val repository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository
) {

    fun register(request: RegisterRequest): AuthenticationResponse {
        val user = User(null, request.email, passwordEncoder.encode(request.password), request.role)
        if(repository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("user already exists")
        }
        val savedUser = repository.save<User>(user)
        val jwtToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        saveUserToken(savedUser.getId()!!, jwtToken)
        return AuthenticationResponse(accessToken = jwtToken, refreshToken = refreshToken)
    }

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        val user: User = repository.findByEmail(request.email) ?: throw Exception()
        val jwtToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        revokeAllUserTokens(user)
        saveUserToken(user.getId()!!, jwtToken)
        return AuthenticationResponse(accessToken = jwtToken, refreshToken = refreshToken)
    }

    private fun saveUserToken(userId: Int, jwtToken: String) {
        val token = Token(null, jwtToken, false, false, userId)
        tokenRepository.save<Token>(token)
    }

    private fun revokeAllUserTokens(user: User) {
        val validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId())
        if (validUserTokens.isEmpty()) return
        validUserTokens.forEach(Consumer { token: Token ->
            token.expired = true
            token.revoked = true
        })
        tokenRepository.saveAll(validUserTokens)
    }

    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val userEmail: String?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }
        val refreshToken = authHeader.substring(7)
        userEmail = jwtService.extractUsername(refreshToken)
        if (userEmail != null) {
            val user: User = repository.findByEmail(userEmail) ?: throw Exception()
            if (jwtService.isTokenValid(refreshToken, user)) {
                val accessToken = jwtService.generateToken(user)
                revokeAllUserTokens(user)
                saveUserToken(user.getId()!!, accessToken)
                val authResponse = AuthenticationResponse(accessToken, refreshToken)
                ObjectMapper().writeValue(response.outputStream, authResponse)
            }
        }
    }

    fun isTokenValid(fullToken: String): Boolean {
        try {
            if (fullToken.startsWith("Bearer ")) {
                val token = fullToken.substring(7)
                val userEmail = jwtService.extractUsername(token) ?: return false
                val userDetails = userDetailsService.loadUserByUsername(userEmail)
                val tokenInDb = tokenRepository.findByToken(token)
                val isValid = (tokenInDb != null) && !tokenInDb.expired && !tokenInDb.revoked
                return isValid && jwtService.isTokenValid(token, userDetails)
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    fun isUserExist(username: String): Boolean {
        userRepository.findByEmail(username) ?: return false
        return true
    }
}