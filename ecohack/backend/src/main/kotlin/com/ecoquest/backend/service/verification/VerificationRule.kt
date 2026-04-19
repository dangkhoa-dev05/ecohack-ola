package com.ecoquest.backend.service.verification

import com.ecoquest.backend.entities.Submission
import com.ecoquest.backend.enums.RejectionReason
import com.ecoquest.backend.service.vision.VisionAnalysis

/**
 * JIRA-422 — A single check run against a [Submission] during verification.
 *
 * Rules are ordered by [priority] (lowest runs first). A rule may inspect the
 * optional [VisionAnalysis] produced upstream to combine client-side metadata
 * (timestamp, GPS) with Azure Vision tags/objects.
 */
interface VerificationRule {
    val priority: Int
    val name: String
    fun check(submission: Submission, vision: VisionAnalysis?): RuleOutcome
}

sealed class RuleOutcome {
    object Pass : RuleOutcome()
    data class Fail(val reason: RejectionReason, val message: String) : RuleOutcome()
}
