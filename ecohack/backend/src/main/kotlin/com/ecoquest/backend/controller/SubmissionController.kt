package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.CompleteSubmissionRequest
import com.ecoquest.backend.dto.InitSubmissionRequest
import com.ecoquest.backend.dto.InitSubmissionResponse
import com.ecoquest.backend.dto.SubmissionDto
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/submissions")
class SubmissionController {

    @PostMapping("/init")
    fun initSubmission(@RequestBody request: InitSubmissionRequest): ApiResponse<InitSubmissionResponse> {
        return ApiResponse.success(
            InitSubmissionResponse(
                submissionId = "sub_${System.currentTimeMillis()}",
                uploadUrl = "https://ecoquestblob.blob.core.windows.net/task-images/mock-upload-url"
            )
        )
    }

    @PostMapping("/{id}/complete")
    fun completeSubmission(
        @PathVariable id: String,
        @RequestBody request: CompleteSubmissionRequest
    ): ApiResponse<SubmissionDto> {
        return ApiResponse.success(
            SubmissionDto(
                id = id,
                taskId = "task_001",
                status = "PENDING_REVIEW",
                rewardCredits = 50
            )
        )
    }
}
