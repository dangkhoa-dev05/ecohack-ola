package com.ecoquest.backend.service.verification

import com.ecoquest.backend.entities.Submission
import com.ecoquest.backend.enums.RejectionReason
import com.ecoquest.backend.service.vision.VisionAnalysis
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Component
class ImagePresenceRule : VerificationRule {
    override val priority: Int = 10
    override val name: String = "image-present"

    override fun check(submission: Submission, vision: VisionAnalysis?): RuleOutcome {
        val url = submission.imageUrl
        return if (url.isNullOrBlank()) {
            RuleOutcome.Fail(RejectionReason.MISSING_IMAGE, "Submission has no image URL")
        } else {
            RuleOutcome.Pass
        }
    }
}

@Component
class GpsPresenceRule : VerificationRule {
    override val priority: Int = 20
    override val name: String = "gps-present"

    override fun check(submission: Submission, vision: VisionAnalysis?): RuleOutcome {
        val lat = submission.lat
        val lng = submission.lng

        if (lat == null || lng == null) {
            return RuleOutcome.Fail(RejectionReason.MISSING_LOCATION, "Submission is missing GPS coordinates")
        }
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            return RuleOutcome.Fail(
                RejectionReason.INVALID_LOCATION,
                "GPS coordinates out of range: lat=$lat lng=$lng"
            )
        }
        return RuleOutcome.Pass
    }
}

@Component
class TimestampFreshnessRule(
    @Value("\${verification.timestamp.max-age-minutes:1440}") private val maxAgeMinutes: Long,
    private val clock: Clock = Clock.systemUTC()
) : VerificationRule {
    override val priority: Int = 30
    override val name: String = "timestamp-fresh"

    override fun check(submission: Submission, vision: VisionAnalysis?): RuleOutcome {
        val capturedAt = submission.capturedAt ?: return RuleOutcome.Pass
        val now = Instant.now(clock)
        val age = Duration.between(capturedAt, now)

        if (age.isNegative && age.abs().toMinutes() > MAX_FUTURE_SKEW_MINUTES) {
            return RuleOutcome.Fail(
                RejectionReason.STALE_TIMESTAMP,
                "Capture timestamp is in the future (${capturedAt})"
            )
        }
        if (age.toMinutes() > maxAgeMinutes) {
            return RuleOutcome.Fail(
                RejectionReason.STALE_TIMESTAMP,
                "Capture timestamp older than $maxAgeMinutes minutes"
            )
        }
        return RuleOutcome.Pass
    }

    companion object {
        private const val MAX_FUTURE_SKEW_MINUTES = 5L
    }
}

/**
 * Combines client-side checks with Azure Vision output: after the image has
 * been analyzed, the tags/objects must contain at least one keyword relevant
 * to the task (trash for cleanup, plant/tree for planting, etc.). If Vision
 * produced nothing (not configured / failed), this rule is a no-op so the
 * pipeline still approves valid mock submissions.
 */
@Component
class VisionMatchRule : VerificationRule {
    override val priority: Int = 100
    override val name: String = "vision-match"

    override fun check(submission: Submission, vision: VisionAnalysis?): RuleOutcome {
        if (vision == null) return RuleOutcome.Pass
        if (vision.tags.isEmpty() && vision.objects.isEmpty()) return RuleOutcome.Pass

        val expected = KEYWORDS_BY_TASK_PREFIX.entries
            .firstOrNull { submission.taskId.startsWith(it.key) }
            ?.value
            ?: DEFAULT_KEYWORDS

        return if (vision.hasAnyTag(expected)) {
            RuleOutcome.Pass
        } else {
            RuleOutcome.Fail(
                RejectionReason.VISION_MISMATCH,
                "Image does not appear to show expected subject (${expected.joinToString()})"
            )
        }
    }

    companion object {
        private val DEFAULT_KEYWORDS = listOf("outdoor", "nature", "person", "plant", "trash")

        private val KEYWORDS_BY_TASK_PREFIX = mapOf(
            "task_001" to listOf("trash", "litter", "garbage", "waste"),
            "task_002" to listOf("tree", "plant", "sapling", "seedling", "garden"),
            "task_003" to listOf("bottle", "plastic", "recycle", "recycling", "bin"),
            "task_004" to listOf("water", "river", "beach", "shore", "ocean")
        )
    }
}
