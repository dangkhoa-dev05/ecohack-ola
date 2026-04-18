package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.submission.*
import com.ecoquest.backend.service.SubmissionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/submissions")
class SubmissionController(
    private val submissionService: SubmissionService
) {
    private val mockUserId = "user_001"

    @PostMapping("/init")
    fun initSubmission(@RequestBody request: InitSubmissionRequest): ApiResponse<InitSubmissionResponse> {
        return try {
            ApiResponse.success(submissionService.init(mockUserId, request))
        } catch (e: Exception) {
            ApiResponse.error(e.message ?: "Failed to initialise submission")
        }
    }

    @PostMapping("/{id}/complete")
    fun completeSubmission(
        @PathVariable id: String,
        @RequestBody request: CompleteSubmissionRequest
    ): ApiResponse<SubmissionDto> {
        return try {
            ApiResponse.success(submissionService.complete(id, request))
        } catch (e: Exception) {
            ApiResponse.error(e.message ?: "Failed to complete submission")
        }
    }

    @GetMapping("/{id}")
    fun getSubmission(@PathVariable id: String): ApiResponse<SubmissionDto> {
        return try {
            ApiResponse.success(submissionService.getById(id))
        } catch (e: Exception) {
            ApiResponse.error(e.message ?: "Submission not found")
        }
    }

    @GetMapping
    fun listSubmissions(
        @RequestParam(defaultValue = "user_001") userId: String
    ): ApiResponse<List<SubmissionSummaryDto>> {
        return ApiResponse.success(submissionService.listByUser(userId))
    }
}
