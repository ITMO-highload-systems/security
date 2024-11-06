package org.example.notionsecurity.auth.dto

import org.example.notionsecurity.user.Role

data class RegisterRequest(
    val email: String,
    val password: String,
    val role: Role,
)