package org.example.notionsecurity.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler


@EnableWebSecurity
@Configuration
class SecurityFilterChainConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val logoutHandler: LogoutHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/auth/register", hasRole("ADMIN"))
                authorize("/auth/**", permitAll)
                authorize("/actuator/**", permitAll)
                authorize("/v3/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthFilter)
            logout {
                logoutUrl = "/auth/logout"
                addLogoutHandler(logoutHandler)
                logoutSuccessHandler =
                    LogoutSuccessHandler { _, _, _ -> SecurityContextHolder.clearContext() }

            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }

        }
        return http.build()
    }


}