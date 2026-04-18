package com.ecoquest.backend.service

import com.ecoquest.backend.dto.submission.*
import com.ecoquest.backend.entities.Submission
import com.ecoquest.backend.enums.SubmissionStatus
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.HashMap

@Service
class SubmissionService(
    private val blobStorageService: BlobStorageService,
    private val verificationService: SubmissionVerificationService,
    private val rewardService: RewardService
) {

    private val store = HashMap<String, SubmissionRecord>()

    private val taskRewards = mapOf(
        "task_001" to 50,
        "task_002" to 100,
        "task_003" to 30,
        "task_004" to 80
    )

    fun init(userId: String, request: InitSubmissionRequest): InitSubmissionResponse {
        if (request.taskId.isBlank()) {
            error("taskId is required")
        }
        if (request.latitude == null || request.longitude == null) {
            error("latitude and longitude are required")
        }

        val id = "sub_${System.currentTimeMillis()}"
        val now = Instant.now().toString()

        val (uploadUrl, blobUrl, expiresAt) = blobStorageService.generateUploadUrl(id)

        val record = SubmissionRecord(
            id            = id,
            userId        = userId,
            taskId        = request.taskId,
            latitude      = request.latitude,
            longitude     = request.longitude,
            status        = SubmissionStatus.PENDING_UPLOAD,
            uploadBlobUrl = blobUrl,
            createdAt     = now,
            updatedAt     = now
        )
        store[id] = record

        return InitSubmissionResponse(
            submissionId       = id,
            uploadUrl          = uploadUrl,
            uploadUrlExpiresAt = expiresAt
        )
    }

    fun complete(id: String, request: CompleteSubmissionRequest): SubmissionDto {
        val record = store[id] ?: error("Submission $id not found")
        val now = Instant.now().toString()

        val imageUrl = if (!request.imageUrl.isNullOrBlank()) {
            request.imageUrl
        } else {
            record.uploadBlobUrl
        }

        val submission = Submission(
            id       = id,
            taskId   = record.taskId,
            userId   = record.userId,
            imageUrl = imageUrl,
            lat      = record.latitude,
            lng      = record.longitude
        )

        val result = verificationService.verify(submission)

        if (result.approved) {
            val credits = taskRewards[record.taskId] ?: 50
            rewardService.grantReward(record.userId, credits)

            store[id] = record.copy(
                status        = SubmissionStatus.APPROVED,
                imageUrl      = imageUrl,
                rewardCredits = credits,
                updatedAt     = now
            )

            return SubmissionDto(
                id              = id,
                taskId          = record.taskId,
                status          = "APPROVED",
                rewardCredits   = credits,
                rejectionReason = null,
                createdAt       = record.createdAt,
                updatedAt       = now
            )
        } else {
            store[id] = record.copy(
                status          = SubmissionStatus.REJECTED,
                imageUrl        = imageUrl,
                rejectionReason = result.reason,
                updatedAt       = now
            )

            return SubmissionDto(
                id              = id,
                taskId          = record.taskId,
                status          = "REJECTED",
                rewardCredits   = 0,
                rejectionReason = result.reason,
                createdAt       = record.createdAt,
                updatedAt       = now
            )
        }
    }

    fun getById(id: String): SubmissionDto {
        val record = store[id] ?: error("Submission $id not found")

        return SubmissionDto(
            id              = record.id,
            taskId          = record.taskId,
            status          = record.status.name,
            rewardCredits   = record.rewardCredits,
            rejectionReason = record.rejectionReason,
            createdAt       = record.createdAt,
            updatedAt       = record.updatedAt
        )
    }

    fun listByUser(userId: String): List<SubmissionSummaryDto> {
        val result = mutableListOf<SubmissionSummaryDto>()

        for (record in store.values) {
            if (record.userId == userId) {
                result.add(
                    SubmissionSummaryDto(
                        id            = record.id,
                        taskId        = record.taskId,
                        status        = record.status.name,
                        rewardCredits = record.rewardCredits,
                        createdAt     = record.createdAt
                    )
                )
            }
        }

        result.sortByDescending { it.createdAt }
        return result
    }
}

data class SubmissionRecord(
    val id: String,
    val userId: String,
    val taskId: String,
    val latitude: Double?,
    val longitude: Double?,
    val status: SubmissionStatus,
    val uploadBlobUrl: String,
    val imageUrl: String? = null,
    val rewardCredits: Int = 0,
    val rejectionReason: String? = null,
    val createdAt: String,
    val updatedAt: String
)
