package com.ecoquest.backend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.blankOrNullString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
class MvpFlowIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `mvp flow works end-to-end with auth`() {
        // 1) login
        val loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"demo@ecoquest.app","password":"pass123"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.token").isNotEmpty)
            .andReturn()

        val loginJson = objectMapper.readTree(loginResult.response.contentAsString)
        val token = loginJson.path("data").path("token").asText()
        val authHeader = "Bearer $token"

        // 2) protected APIs are reachable with token
        mockMvc.perform(
            get("/tasks/daily")
                .header("Authorization", authHeader)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(greaterThan(0)))

        // 3) init + complete submission (approved path)
        val initResult = mockMvc.perform(
            post("/submissions/init")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"taskId":"task_001","latitude":10.7769,"longitude":106.7009}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.submissionId").isNotEmpty)
            .andReturn()

        val initJson = objectMapper.readTree(initResult.response.contentAsString)
        val submissionId = initJson.path("data").path("submissionId").asText()

        mockMvc.perform(
            post("/submissions/$submissionId/complete")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"imageUrl":"https://example.com/proof.jpg","capturedAt":"${Instant.now()}"}"""
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("APPROVED"))
            .andExpect(jsonPath("$.data.rewardCredits").value(greaterThan(0)))
            .andExpect(jsonPath("$.data.streak").value(greaterThanOrEqualTo(1)))

        // 4) reward state reflected in /me and /me/stats
        mockMvc.perform(
            get("/me")
                .header("Authorization", authHeader)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("user_001"))
            .andExpect(jsonPath("$.data.credits").value(greaterThanOrEqualTo(300)))

        mockMvc.perform(
            get("/me/stats")
                .header("Authorization", authHeader)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.credits").value(greaterThanOrEqualTo(300)))
            .andExpect(jsonPath("$.data.streak").value(greaterThanOrEqualTo(1)))

        // 5) leaderboard includes merged live state
        mockMvc.perform(
            get("/api/v1/leaderboard")
                .header("Authorization", authHeader)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(greaterThan(0)))

        // 6) assistant explains rejection reason (new endpoint)
        mockMvc.perform(
            post("/assistant/explain-rejection")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"rejectionReason":"MISSING_IMAGE","rejectionMessage":"Submission has no image URL","taskId":"task_001"}"""
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.reply", not(blankOrNullString())))
    }
}
