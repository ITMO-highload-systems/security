package org.example.notionsecurity.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.notionsecurity.token.Token
import org.example.notionsecurity.token.TokenRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutService(private val tokenRepository: TokenRepository) : LogoutHandler {

    override fun logout(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val authHeader = request!!.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }
        val jwt = authHeader.substring(7)
        val storedToken = tokenRepository.findByToken(jwt)
        if (storedToken != null) {
            storedToken.expired = true
            storedToken.revoked = true
            tokenRepository.save<Token>(storedToken)
            SecurityContextHolder.clearContext()
        }
    }
}
