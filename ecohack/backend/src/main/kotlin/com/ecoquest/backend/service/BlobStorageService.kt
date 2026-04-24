package com.ecoquest.backend.service

import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.sas.BlobSasPermission
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class BlobStorageService(
    @Value("\${azure.storage.connection-string:}") private val connectionString: String,
    @Value("\${azure.storage.container-name:task-images}") private val containerName: String
) {

    private val SAS_EXPIRY_MINUTES = 15L

    /**
     * Returns Triple(uploadUrl, blobUrl, expiresAt).
     * uploadUrl — SAS URL the Android client PUTs the image to directly.
     * blobUrl   — permanent public URL stored in the submission record.
     * expiresAt — ISO-8601 expiry of the SAS token.
     *
     * Falls back to a mock URL when AZURE_STORAGE_CONNECTION_STRING is not configured,
     * so local development still works without Azure credentials.
     */
    fun generateUploadUrl(submissionId: String): Triple<String, String, String> {
        val expiresAt = Instant.now().plus(SAS_EXPIRY_MINUTES, ChronoUnit.MINUTES)

        if (connectionString.isBlank()) {
            val baseUrl = "https://ecoquestblob.blob.core.windows.net/$containerName"
            val blobUrl = "$baseUrl/$submissionId.jpg"
            val uploadUrl = "$blobUrl?sv=mock-sas-token&sp=cw&se=mock-expiry"
            return Triple(uploadUrl, blobUrl, expiresAt.toString())
        }

        val blobClient = BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient()
            .getBlobContainerClient(containerName)
            .getBlobClient("$submissionId.jpg")

        val expiry = OffsetDateTime.ofInstant(expiresAt, ZoneOffset.UTC)
        val permission = BlobSasPermission()
            .setWritePermission(true)
            .setCreatePermission(true)
        val sasValues = BlobServiceSasSignatureValues(expiry, permission)
        val sasToken = blobClient.generateSas(sasValues)

        val blobUrl = blobClient.blobUrl
        val uploadUrl = "$blobUrl?$sasToken"

        return Triple(uploadUrl, blobUrl, expiresAt.toString())
    }
}
