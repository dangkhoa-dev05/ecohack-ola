package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.submission.CompleteSubmissionRequest
import com.ecoquest.backend.dto.submission.InitSubmissionRequest
import com.ecoquest.backend.dto.submission.InitSubmissionResponse
import com.ecoquest.backend.dto.submission.SubmissionDto
import com.ecoquest.backend.entities.Submission
import com.ecoquest.backend.enums.SubmissionStatus
import com.ecoquest.backend.service.RewardService
import com.ecoquest.backend.service.SubmissionVerificationService
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/submissions")
class SubmissionController(
    private val verificationService: SubmissionVerificationService,
    private val rewardService: RewardService
) {
    private val submissions = ConcurrentHashMap<String, SubmissionRecord>()

    @PostMapping("/init")
    fun initSubmission(@RequestBody request: InitSubmissionRequest): ApiResponse<InitSubmissionResponse> {
        val id = "sub_${System.currentTimeMillis()}"
        submissions[id] = SubmissionRecord(
            taskId = request.taskId,
            latitude = request.latitude,
            longitude = request.longitude
        )
        return ApiResponse.success(
            InitSubmissionResponse(
                submissionId = id,
                uploadUrl = "https://ecoquestblob.blob.core.windows.net/task-images/mock-upload-url"
            )
        )
    }

    @PostMapping("/{id}/complete")
    fun completeSubmission(
        @PathVariable id: String,
        @RequestBody request: CompleteSubmissionRequest
    ): ApiResponse<SubmissionDto> {
        val record = submissions[id]
            ?: return ApiResponse.error("Submission $id not found")

        val submission = Submission(
            id = id,
            taskId = record.taskId,
            userId = "user_001",
            imageUrl = request.imageUrl,
            lat = record.latitude,
            lng = record.longitude
        )

        val result = verificationService.verify(submission)

        return if (result.approved) {
            val taskCredits = TASK_REWARDS[record.taskId] ?: 50
            val reward = rewardService.grantReward(submission.userId, taskCredits)

            submissions[id] = record.copy(status = SubmissionStatus.APPROVED)

            ApiResponse.success(
                SubmissionDto(
                    id = id,
                    taskId = record.taskId,
                    status = SubmissionStatus.APPROVED.name,
                    rewardCredits = reward.creditsAwarded
                )
            )
        } else {
            submissions[id] = record.copy(status = SubmissionStatus.REJECTED)

            ApiResponse.success(
                SubmissionDto(
                    id = id,
                    taskId = record.taskId,
                    status = SubmissionStatus.REJECTED.name,
                    rewardCredits = 0,
                    rejectionReason = result.reason
                )
            )
        }
    }

    @GetMapping("/{id}")
    fun getSubmission(@PathVariable id: String): ApiResponse<SubmissionDto> {
        val record = submissions[id]
            ?: return ApiResponse.error("Submission $id not found")

        return ApiResponse.success(
            SubmissionDto(
                id = id,
                taskId = record.taskId,
                status = record.status.name,
                rewardCredits = 0
            )
        )
    }

    companion object {
        private val TASK_REWARDS = mapOf(
            "task_001" to 50,
            "task_002" to 100,
            "task_003" to 30,
            "task_004" to 80
        )
    }
}

private data class SubmissionRecord(
    val taskId: String,
    val latitude: Double?,
    val longitude: Double?,
    val status: SubmissionStatus = SubmissionStatus.PENDING_REVIEW
)
