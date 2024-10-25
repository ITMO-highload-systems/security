package org.example.notionsecurity.auth.dto


data class AuthenticationRequest(
    var password: String,
    val email: String
) {

}
