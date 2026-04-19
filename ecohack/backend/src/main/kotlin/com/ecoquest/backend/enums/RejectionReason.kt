package com.ecoquest.backend.enums

enum class RejectionReason {
    MISSING_IMAGE,
    MISSING_LOCATION,
    INVALID_LOCATION,
    STALE_TIMESTAMP,
    VISION_MISMATCH
}