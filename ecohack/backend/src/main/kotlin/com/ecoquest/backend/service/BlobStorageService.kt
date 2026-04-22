package com.ecoquest.backend.service

import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class BlobStorageService {

    fun generateUploadUrl(submissionId: String): Triple<String, String, String> {
        val baseUrl   = "https://ecoquestblob.blob.core.windows.net/task-images"
        val blobUrl   = "$baseUrl/$submissionId.jpg"
        val uploadUrl = "$blobUrl?sv=mock-sas-token&sp=w&se=mock-expiry"
        val expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES).toString()

        return Triple(uploadUrl, blobUrl, expiresAt)
    }
}
