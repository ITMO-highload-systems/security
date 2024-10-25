package org.example.notionsecurity.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Table(name = "_user")
data class User(
    @Id
    private val id: Int?,
    private val email: String,
    private val password: String,
    private val role: Role
) : UserDetails {

    fun getId(): Int? = id

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return role.getAuthorities()
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
