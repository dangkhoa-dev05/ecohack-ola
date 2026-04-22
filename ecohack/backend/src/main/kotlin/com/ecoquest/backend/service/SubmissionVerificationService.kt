package com.ecoquest.backend.service

import com.ecoquest.backend.entities.Submission
import com.ecoquest.backend.enums.RejectionReason
import com.ecoquest.backend.service.verification.RuleOutcome
import com.ecoquest.backend.service.verification.VerificationRule
import com.ecoquest.backend.service.vision.AzureVisionClient
import com.ecoquest.backend.service.vision.VisionAnalysis
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * JIRA-422 — Verification pipeline v1.
 *
 * 1. Run lightweight client-side checks (image present, GPS, timestamp).
 * 2. If they pass, call Azure Vision (JIRA-421) to analyze the image.
 * 3. Run the final rule that combines Vision output with the task context.
 *
 * Rules are injected so the set can be extended without changing this class.
 */
@Service
class SubmissionVerificationService(
    private val rules: List<VerificationRule>,
    private val visionClient: AzureVisionClient
) {
    private val log = LoggerFactory.getLogger(SubmissionVerificationService::class.java)

    private val orderedRules: List<VerificationRule> = rules.sortedBy { it.priority }

    fun verify(submission: Submission): VerificationResult {
        var vision: VisionAnalysis? = null

        for (rule in orderedRules) {
            if (rule.priority >= VISION_STAGE_THRESHOLD && vision == null) {
                vision = submission.imageUrl
                    ?.takeIf { it.isNotBlank() }
                    ?.let(visionClient::analyze)
            }

            val outcome = rule.check(submission, vision)
            if (outcome is RuleOutcome.Fail) {
                log.info(
                    "Submission {} rejected by rule {}: {}",
                    submission.id, rule.name, outcome.message
                )
                return VerificationResult(
                    approved = false,
                    reason = outcome.reason.name,
                    ruleName = rule.name,
                    message = outcome.message,
                    vision = vision
                )
            }
        }

        return VerificationResult(
            approved = true,
            reason = null,
            ruleName = null,
            message = null,
            vision = vision
        )
    }

    companion object {
        /** Rules with priority >= this threshold run after Vision analysis. */
        private const val VISION_STAGE_THRESHOLD = 100
    }
}

data class VerificationResult(
    val approved: Boolean,
    val reason: String?,
    val ruleName: String?,
    val message: String?,
    val vision: VisionAnalysis?
) {
    val rejectionReason: RejectionReason?
        get() = reason?.let { runCatching { RejectionReason.valueOf(it) }.getOrNull() }
}
