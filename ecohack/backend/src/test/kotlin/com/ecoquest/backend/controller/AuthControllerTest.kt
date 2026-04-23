package com.ecoquest.backend.controller

import com.ecoquest.backend.service.JwtService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtService: JwtService

    @Test
    fun `login returns success for valid credentials`() {
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"demo@ecoquest.app","password":"pass123"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.token").isNotEmpty)
            .andExpect(jsonPath("$.data.user.id").value("user_001"))
    }

    @Test
    fun `login returns error for invalid credentials`() {
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"demo@ecoquest.app","password":"wrong"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Invalid email or password"))
    }

    @Test
    fun `login rejects invalid request body`() {
        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"","password":""}""")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").exists())
    }

    @Test
    fun `me requires authentication`() {
        mockMvc.perform(get("/me"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Authentication required"))
    }

    @Test
    fun `me returns authenticated user`() {
        val token = jwtService.generateToken("user_001")

        mockMvc.perform(
            get("/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("user_001"))
    }

    @Test
    fun `me returns unauthorized for invalid token`() {
        mockMvc.perform(
            get("/me")
                .header("Authorization", "Bearer not-a-real-token")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Authentication required"))
    }

    @Test
    fun `login token can access protected endpoint`() {
        val loginResponse = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"demo@ecoquest.app","password":"pass123"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andReturn()

        val token = loginResponse.extractToken()

        mockMvc.perform(
            get("/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("user_001"))
            .andExpect(jsonPath("$.data.email").value("demo@ecoquest.app"))
    }

    private fun MvcResult.extractToken(): String {
        val body = response.contentAsString
        val tokenRegex = Regex(""""token"\s*:\s*"([^"]+)"""")
        return requireNotNull(tokenRegex.find(body)?.groupValues?.get(1)) {
            "JWT token missing from login response: $body"
        }
    }
}
