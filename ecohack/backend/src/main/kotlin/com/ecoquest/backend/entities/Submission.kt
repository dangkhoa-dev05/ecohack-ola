package com.ecoquest.backend.entities

import com.ecoquest.backend.enums.RejectionReason
import com.ecoquest.backend.enums.SubmissionStatus
import java.time.Instant

data class Submission(
    val id: String,
    val taskId: String,
    val userId: String,
    val imageUrl: String?,
    val lat: Double?,
    val lng: Double?,
    val status: SubmissionStatus = SubmissionStatus.PENDING_REVIEW,
    val rejectionReason: RejectionReason? = null
)
