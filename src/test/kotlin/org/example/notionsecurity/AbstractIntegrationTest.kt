package org.example.notionsecurity

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeAll
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class AbstractIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        @ServiceConnection
        internal var postgres = PostgreSQLContainer("postgres:latest")

        @BeforeAll
        @JvmStatic
        fun setup() {
            postgres.start()
        }
    }
}