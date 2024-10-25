package org.example.notionsecurity.token

import org.springframework.data.annotation.Id


data class Token(
    @Id
    var id: Int?,

    var token: String,

    var revoked: Boolean,

    var expired: Boolean,

    var userId: Int
) {

}
