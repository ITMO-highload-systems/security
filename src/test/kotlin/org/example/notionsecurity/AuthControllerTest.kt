package org.example.notionsecurity

import org.example.notionsecurity.auth.dto.AuthenticationResponse
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Clock
import java.time.Instant
import kotlin.test.Test

class AuthControllerTest : AbstractIntegrationTest() {
    @MockBean
    lateinit var clock: Clock
    var now: Instant = Instant.now()

    @BeforeEach
    fun before() {
        now.plusSeconds(1)
        `when`(clock.instant()).thenReturn(now)
    }


    @Test
    fun `auth as admin`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mapOf("email" to "admin@gmail.com", "password" to "password")))

        ).andExpect(MockMvcResultMatchers.status().isOk)

    }


    @Test
    fun `register user forbidden if no auth`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        mapOf(
                            "email" to "admin@gmail.com",
                            "password" to "password",
                            "role" to "USER"
                        )
                    )
                )

        ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `register user forbidden for user`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        mapOf(
                            "email" to "admin@gmail.com",
                            "password" to "password",
                            "role" to "USER"
                        )
                    )
                )

        ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `register user forbidden if user exist`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        mapOf(
                            "email" to "admin@gmail.com",
                            "password" to "password",
                            "role" to "USER"
                        )
                    )
                )

        ).andExpect(MockMvcResultMatchers.status().isBadRequest).andReturn()
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `register user allowed`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    mapper.writeValueAsString(
                        mapOf(
                            "email" to "registerUserAllowed@gmail.com",
                            "password" to "password",
                            "role" to "USER"
                        )
                    )
                )

        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()
    }

    @Test
    fun `refresh token if no auth`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string(""))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `refresh token success`() {
        val refreshToken = mapper.readValue(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("email" to "admin@gmail.com", "password" to "password")))

            ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString,
            AuthenticationResponse::class.java
        ).refreshToken
        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer $refreshToken")

        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `is user exist true`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/auth/is-user-exist/admin@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("true"))
    }

    @Test
    fun `is user exist false`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/auth/is-user-exist/aboba@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("false"))
    }

    @Test
    fun `is token valid true`() {
        val accessToken = mapper.readValue(
            mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("email" to "admin@gmail.com", "password" to "password")))

            ).andExpect(MockMvcResultMatchers.status().isOk).andReturn().response.contentAsString,
            AuthenticationResponse::class.java
        ).accessToken
        mockMvc.perform(
            MockMvcRequestBuilders.get("/auth/is-token-valid/Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("true"))
    }

    @Test
    fun `is token valid false`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/auth/is-token-valid/Bearer aboba@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("false"))
    }

    @Test
    fun `is token valid false wrong type`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/auth/is-token-valid/aboba@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("false"))
    }


}