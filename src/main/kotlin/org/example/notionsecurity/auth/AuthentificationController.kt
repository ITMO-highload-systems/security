package org.example.notionsecurity.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.notionsecurity.auth.dto.AuthenticationRequest
import org.example.notionsecurity.auth.dto.AuthenticationResponse
import org.example.notionsecurity.auth.dto.RegisterRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
class AuthenticationController(private val service: AuthenticationService) {

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(service.register(request))
    }

    @PostMapping("/authenticate")
    fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(service.authenticate(request))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        service.refreshToken(request, response)
    }

    @GetMapping("/is-token-valid/{token}")
    fun isTokenValid(
        @PathVariable token: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(service.isTokenValid(token))
    }

    @GetMapping("/is-user-exist/{username}")
    fun isUserExist(
        @PathVariable username: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(service.isUserExist(username))
    }

}
