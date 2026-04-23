package com.ecoquest.backend.service.openai

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * JIRA-423 — thin Azure OpenAI "chat completions" client.
 *
 * [chat] returns the assistant's reply text, or `null` when the client is not
 * configured or the remote call fails. Callers are expected to fall back to a
 * mock/canned response when `null` is returned.
 */
@Service
class AzureOpenAIClient(
    @Value("\${azure.openai.endpoint:}") private val endpoint: String,
    @Value("\${azure.openai.key:}") private val apiKey: String,
    @Value("\${azure.openai.deployment:gpt-4o-mini}") private val deployment: String,
    @Value("\${azure.openai.api-version:2024-02-15-preview}") private val apiVersion: String,
    @Value("\${azure.openai.system-prompt:You are a helpful assistant.}") private val systemPrompt: String,
    @Value("\${azure.openai.max-tokens:300}") private val maxTokens: Int,
    @Value("\${azure.openai.temperature:0.7}") private val temperature: Double,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(AzureOpenAIClient::class.java)

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    fun isConfigured(): Boolean =
        endpoint.isNotBlank() && apiKey.isNotBlank() && deployment.isNotBlank()

    /**
     * Sends [userMessage] to Azure OpenAI. Returns `null` on any failure so
     * callers can decide how to degrade gracefully.
     */
    fun chat(userMessage: String): String? {
        if (!isConfigured()) {
            log.debug("Azure OpenAI not configured, skipping remote call")
            return null
        }
        if (userMessage.isBlank()) return null

        return try {
            val uri = URI.create(
                "${endpoint.trimEnd('/')}/openai/deployments/$deployment/chat/completions" +
                    "?api-version=$apiVersion"
            )

            val payload = mapOf(
                "messages" to listOf(
                    mapOf("role" to "system", "content" to systemPrompt),
                    mapOf("role" to "user", "content" to userMessage)
                ),
                "temperature" to temperature,
                "max_tokens" to maxTokens
            )

            val request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(15))
                .header("api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() !in 200..299) {
                log.warn(
                    "Azure OpenAI returned HTTP {}: {}",
                    response.statusCode(), response.body().take(500)
                )
                return null
            }

            val root = objectMapper.readTree(response.body())
            val reply = root.path("choices")
                .firstOrNull()
                ?.path("message")
                ?.path("content")
                ?.asText()
                ?.takeIf { it.isNotBlank() }

            reply
        } catch (ex: Exception) {
            log.warn("Azure OpenAI call failed: {}", ex.message)
            null
        }
    }
}
