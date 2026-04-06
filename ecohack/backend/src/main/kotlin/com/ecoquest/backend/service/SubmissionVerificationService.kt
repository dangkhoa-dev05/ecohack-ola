package com.ecoquest.backend.service

import com.ecoquest.backend.entities.Submission
import org.springframework.stereotype.Service

@Service
class SubmissionVerificationService {

    fun verify(submission: Submission): VerificationResult {
        if (submission.imageUrl.isNullOrBlank()) {
            return VerificationResult(
                approved = false,
                reason = "MISSING_IMAGE"
            )
        }

        if (submission.lat == null || submission.lng == null) {
            return VerificationResult(
                approved = false,
                reason = "MISSING_LOCATION"
            )
        }

        return VerificationResult(
            approved = true,
            reason = null
        )
    }
}

data class VerificationResult(
    val approved: Boolean,
    val reason: String?
)