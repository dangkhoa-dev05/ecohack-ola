package com.ecoquest.backend.service.vision

import com.fasterxml.jackson.databind.JsonNode
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
 * JIRA-421 — thin Azure AI Vision client.
 *
 * Calls the "Image Analysis" REST endpoint and returns a parsed [VisionAnalysis]
 * containing tags, caption and detected objects. When the endpoint or key are
 * not configured (empty) the client returns a deterministic mock result so the
 * rest of the pipeline can run during Day 1 / offline development.
 */
@Service
class AzureVisionClient(
    @Value("\${azure.vision.endpoint:}") private val endpoint: String,
    @Value("\${azure.vision.key:}") private val apiKey: String,
    @Value("\${azure.vision.api-version:2023-02-01-preview}") private val apiVersion: String,
    @Value("\${azure.vision.features:tags,description,objects}") private val features: String,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(AzureVisionClient::class.java)

    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    fun isConfigured(): Boolean = endpoint.isNotBlank() && apiKey.isNotBlank()

    /**
     * Analyzes [imageUrl] using Azure AI Vision. Returns a parsed result or
     * [VisionAnalysis.mock] when the client is not configured or the remote
     * call fails.
     */
    fun analyze(imageUrl: String): VisionAnalysis {
        if (imageUrl.isBlank()) {
            return VisionAnalysis.empty(source = "empty-input")
        }

        if (!isConfigured()) {
            log.debug("Azure Vision not configured, returning mock analysis for {}", imageUrl)
            return VisionAnalysis.mock(imageUrl)
        }

        return try {
            val uri = buildAnalyzeUri()
            val body = objectMapper.writeValueAsString(mapOf("url" to imageUrl))

            val request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(10))
                .header("Ocp-Apim-Subscription-Key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() in 200..299) {
                parseResponse(response.body())
            } else {
                log.warn(
                    "Azure Vision returned HTTP {} for {}: {}",
                    response.statusCode(), imageUrl, response.body().take(500)
                )
                VisionAnalysis.mock(imageUrl, source = "azure-vision-error-${response.statusCode()}")
            }
        } catch (ex: Exception) {
            log.warn("Azure Vision call failed for {}: {}", imageUrl, ex.message)
            VisionAnalysis.mock(imageUrl, source = "azure-vision-exception")
        }
    }

    private fun buildAnalyzeUri(): URI {
        val base = endpoint.trimEnd('/')
        return URI.create(
            "$base/computervision/imageanalysis:analyze" +
                "?api-version=$apiVersion" +
                "&features=$features"
        )
    }

    private fun parseResponse(raw: String): VisionAnalysis {
        val root: JsonNode = objectMapper.readTree(raw)

        val tags = root.path("tagsResult").path("values")
            .mapNotNull { tag ->
                val name = tag.path("name").asText(null) ?: return@mapNotNull null
                val confidence = tag.path("confidence").asDouble(0.0)
                VisionTag(name, confidence)
            }

        val caption = root.path("captionResult").path("text").asText(null)
            ?: root.path("descriptionResult").path("values").firstOrNull()?.path("text")?.asText(null)

        val objects = root.path("objectsResult").path("values")
            .mapNotNull { obj ->
                val name = obj.path("tags").firstOrNull()?.path("name")?.asText(null)
                    ?: obj.path("name").asText(null)
                    ?: return@mapNotNull null
                val confidence = obj.path("tags").firstOrNull()?.path("confidence")?.asDouble(0.0)
                    ?: obj.path("confidence").asDouble(0.0)
                VisionObject(name, confidence)
            }

        return VisionAnalysis(
            caption = caption,
            tags = tags,
            objects = objects,
            source = "azure-vision"
        )
    }
}
