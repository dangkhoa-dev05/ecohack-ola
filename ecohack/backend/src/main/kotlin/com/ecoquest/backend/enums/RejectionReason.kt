package com.ecoquest.backend.enums

/**
 * Reasons a submission can be rejected by the verification pipeline.
 *
 * Each entry carries:
 *  - [label]   : short human-readable description for UIs / assistant replies.
 *  - [hint]    : actionable tip the assistant falls back to when Azure OpenAI
 *                is unavailable (JIRA-423 fallback mock).
 */
enum class RejectionReason(
    val label: String,
    val hint: String
) {
    MISSING_IMAGE(
        label = "No photo attached",
        hint = "Make sure you've uploaded a photo of the completed task before submitting."
    ),
    MISSING_LOCATION(
        label = "Location not shared",
        hint = "Allow the app to access your GPS so we can confirm the task was done on-site."
    ),
    INVALID_LOCATION(
        label = "Location coordinates are invalid",
        hint = "The GPS reading looks off. Step outside or wait for a clear signal, then retry."
    ),
    STALE_TIMESTAMP(
        label = "Photo is too old",
        hint = "Please take a fresh photo right after completing the task so the timestamp is recent."
    ),
    VISION_MISMATCH(
        label = "Photo does not match the task",
        hint = "The image doesn't show the expected activity. Re-take the photo showing the task clearly."
    );

    companion object {
        fun fromNameOrNull(name: String?): RejectionReason? =
            name?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}
