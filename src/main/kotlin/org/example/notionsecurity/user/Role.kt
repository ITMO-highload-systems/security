package org.example.notionsecurity.user

import org.springframework.security.core.authority.SimpleGrantedAuthority


enum class Role {
    ADMIN,
    USER;

    fun getAuthorities(): List<SimpleGrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + this.name))
    }
}