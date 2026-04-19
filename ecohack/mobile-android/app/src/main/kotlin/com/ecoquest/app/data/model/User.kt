package com.ecoquest.app.data.model

data class User(
    val id: String,
    val displayName: String,
    val email: String? = null,
    val level: Int,
    val xp: Int = 0,
    val credits: Int,
    val streak: Int,
    val avatarUrl: String? = null
)

fun UserDto.toUser(
    email: String? = null,
    avatarUrl: String? = null
): User {
    return User(
        id = id,
        displayName = displayName,
        email = email,
        level = level,
        xp = 0,
        credits = credits,
        streak = streak,
        avatarUrl = avatarUrl
    )
}

fun xpRequiredForNextLevel(level: Int): Int {
    val normalizedLevel = level.coerceAtLeast(1)
    return 100 + ((normalizedLevel - 1) * 50)
}

fun User.applyTaskReward(rewardCredits: Int): User {
    var updatedLevel = level.coerceAtLeast(1)
    var updatedXp = (xp + rewardCredits).coerceAtLeast(0)

    while (updatedXp >= xpRequiredForNextLevel(updatedLevel)) {
        updatedXp -= xpRequiredForNextLevel(updatedLevel)
        updatedLevel += 1
    }

    return copy(
        level = updatedLevel,
        xp = updatedXp,
        credits = credits + rewardCredits
    )
}
