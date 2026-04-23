package com.ecoquest.backend.service.vision

/**
 * Basic parsed view of an Azure AI Vision "Image Analysis" response.
 *
 * Only the fields the rest of the backend actually needs (caption, tags and
 * objects) are exposed so the rule engine can reason about them without
 * depending on Azure's raw schema.
 */
data class VisionAnalysis(
    val caption: String?,
    val tags: List<VisionTag>,
    val objects: List<VisionObject>,
    /** Where the result came from: "azure-vision", "mock", "empty-input", ... */
    val source: String
) {
    fun hasAnyTag(keywords: Collection<String>): Boolean {
        if (keywords.isEmpty()) return true
        val lowered = keywords.map { it.lowercase().trim() }
        return tags.any { tag -> lowered.any { kw -> tag.name.lowercase().contains(kw) } } ||
            objects.any { obj -> lowered.any { kw -> obj.name.lowercase().contains(kw) } } ||
            lowered.any { kw -> caption?.lowercase()?.contains(kw) == true }
    }

    companion object {
        fun empty(source: String) = VisionAnalysis(
            caption = null,
            tags = emptyList(),
            objects = emptyList(),
            source = source
        )

        /**
         * Deterministic fallback used when Azure Vision is unreachable or not
         * configured. Returns a plausible set of eco-friendly tags so the
         * verification pipeline can still approve mock submissions.
         */
        fun mock(imageUrl: String, source: String = "mock"): VisionAnalysis = VisionAnalysis(
            caption = "A person performing an outdoor environmental activity",
            tags = listOf(
                VisionTag("outdoor", 0.95),
                VisionTag("person", 0.92),
                VisionTag("nature", 0.88),
                VisionTag("plant", 0.80),
                VisionTag("trash", 0.72),
                VisionTag("recycling", 0.65)
            ),
            objects = listOf(
                VisionObject("person", 0.9),
                VisionObject("plant", 0.75)
            ),
            source = source
        )
    }
}

data class VisionTag(
    val name: String,
    val confidence: Double
)

data class VisionObject(
    val name: String,
    val confidence: Double
)
