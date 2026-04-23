package com.ecoquest.backend.service

import com.ecoquest.backend.dto.submission.*
import com.ecoquest.backend.entities.Submission
import com.ecoquest.backend.enums.RejectionReason
import com.ecoquest.backend.enums.SubmissionStatus
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.concurrent.ConcurrentHashMap

@Service
class SubmissionService(
    private val blobStorageService: BlobStorageService,
    private val verificationService: SubmissionVerificationService,
    private val rewardService: RewardService
) {

    private val store = ConcurrentHashMap<String, SubmissionRecord>()

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

        val id = "sub_${System.currentTimeMillis()}_${Integer.toHexString(store.size)}"
        val now = Instant.now().toString()

        val (uploadUrl, blobUrl, expiresAt) = blobStorageService.generateUploadUrl(id)

        val record = SubmissionRecord(
            id = id,
            userId = userId,
            taskId = request.taskId,
            latitude = request.latitude,
            longitude = request.longitude,
            status = SubmissionStatus.PENDING_UPLOAD,
            uploadBlobUrl = blobUrl,
            createdAt = now,
            updatedAt = now
        )
        store[id] = record

        return InitSubmissionResponse(
            submissionId = id,
            uploadUrl = uploadUrl,
            uploadUrlExpiresAt = expiresAt
        )
    }

    fun complete(userId: String, id: String, request: CompleteSubmissionRequest): SubmissionDto {
        val record = requireOwnedRecord(userId, id)
        val now = Instant.now().toString()

        val imageUrl = request.imageUrl
        val capturedAt: Instant = request.capturedAt?.let {
            try {
                Instant.parse(it)
            } catch (_: DateTimeParseException) {
                null
            }
        } ?: Instant.now()

        val submission = Submission(
            id = id,
            taskId = record.taskId,
            userId = record.userId,
            imageUrl = imageUrl,
            lat = record.latitude,
            lng = record.longitude,
            capturedAt = capturedAt
        )

        val result = verificationService.verify(submission)

        return if (result.approved) {
            val credits = taskRewards[record.taskId] ?: 50
            val reward = rewardService.onSubmissionApproved(record.userId, credits)

            val updated = record.copy(
                status = SubmissionStatus.APPROVED,
                imageUrl = imageUrl,
                rewardCredits = reward.creditsAwarded,
                updatedAt = now
            )
            store[id] = updated

            SubmissionDto(
                id = id,
                taskId = record.taskId,
                status = SubmissionStatus.APPROVED.name,
                rewardCredits = reward.creditsAwarded,
                rejectionReason = null,
                rejectionReasonLabel = null,
                rejectionMessage = null,
                streak = reward.streak,
                createdAt = record.createdAt,
                updatedAt = now
            )
        } else {
            val rejection = RejectionReason.fromNameOrNull(result.reason)
            val message = result.message
                ?: rejection?.label

            val updated = record.copy(
                status = SubmissionStatus.REJECTED,
                imageUrl = imageUrl,
                rejectionReason = result.reason,
                rejectionMessage = message,
                updatedAt = now
            )
            store[id] = updated

            SubmissionDto(
                id = id,
                taskId = record.taskId,
                status = SubmissionStatus.REJECTED.name,
                rewardCredits = 0,
                rejectionReason = result.reason,
                rejectionReasonLabel = rejection?.label,
                rejectionMessage = message,
                streak = null,
                createdAt = record.createdAt,
                updatedAt = now
            )
        }
    }

    fun getById(userId: String, id: String): SubmissionDto {
        val record = requireOwnedRecord(userId, id)

        return SubmissionDto(
            id = record.id,
            taskId = record.taskId,
            status = record.status.name,
            rewardCredits = record.rewardCredits,
            rejectionReason = record.rejectionReason,
            rejectionReasonLabel = RejectionReason.fromNameOrNull(record.rejectionReason)?.label,
            rejectionMessage = record.rejectionMessage,
            createdAt = record.createdAt,
            updatedAt = record.updatedAt
        )
    }

    fun listByUser(userId: String): List<SubmissionSummaryDto> =
        store.values
            .asSequence()
            .filter { it.userId == userId }
            .sortedByDescending { it.createdAt }
            .map {
                SubmissionSummaryDto(
                    id = it.id,
                    taskId = it.taskId,
                    status = it.status.name,
                    rewardCredits = it.rewardCredits,
                    createdAt = it.createdAt
                )
            }
            .toList()

    private fun requireOwnedRecord(userId: String, id: String): SubmissionRecord {
        val record = store[id] ?: error("Submission $id not found")
        if (record.userId != userId) {
            error("Submission $id not found")
        }
        return record
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
    val rejectionMessage: String? = null,
    val createdAt: String,
    val updatedAt: String
)
