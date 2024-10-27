package org.example.notionsecurity.exception

data class ApiException(
    val status: Int,
    val message: String?
)