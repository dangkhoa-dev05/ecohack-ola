package com.ecoquest.backend.controller

import com.ecoquest.backend.common.ApiResponse
import com.ecoquest.backend.dto.submission.*
import com.ecoquest.backend.service.SubmissionService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/submissions")
class SubmissionController(
    private val submissionService: SubmissionService
) {

    @PostMapping("/init")
    fun initSubmission(
        authentication: Authentication,
        @RequestBody request: InitSubmissionRequest
    ): ApiResponse<InitSubmissionResponse> {
        return try {
            ApiResponse.success(submissionService.init(authentication.name, request))
        } catch (e: Exception) {
            ApiResponse.error(e.message ?: "Failed to initialise submission")
        }
    }

    @PostMapping("/{id}/complete")
    fun completeSubmission(
        authentication: Authentication,
        @PathVariable id: String,
        @RequestBody request: CompleteSubmissionRequest
    ): ApiResponse<SubmissionDto> {
        return try {
            ApiResponse.success(submissionService.complete(authentication.name, id, request))
        } catch (e: Exception) {
            ApiResponse.error(e.message ?: "Failed to complete submission")
        }
    }

    @GetMapping("/{id}")
    fun getSubmission(
        authentication: Authentication,
        @PathVariable id: String
    ): ApiResponse<SubmissionDto> {
        return try {
            ApiResponse.success(submissionService.getById(authentication.name, id))
        } catch (e: Exception) {
            ApiResponse.error(e.message ?: "Submission not found")
        }
    }

    @GetMapping
    fun listSubmissions(
        authentication: Authentication
    ): ApiResponse<List<SubmissionSummaryDto>> {
        return ApiResponse.success(submissionService.listByUser(authentication.name))
    }
}
