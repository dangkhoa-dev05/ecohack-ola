package com.ecoquest.backend.service

import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

/**
 * Mock reward service (JIRA-411).
 *
 * Responsibilities (mock / in-memory):
 *  - Grant credits to a user when a submission is approved.
 *  - Maintain a daily streak counter per user:
 *      * first activity            -> streak = 1
 *      * activity on the next day  -> streak + 1
 *      * activity on the same day  -> streak unchanged
 *      * gap of 2+ days            -> streak reset to 1
 *
 * No database is involved; all state lives in ConcurrentHashMap instances so
 * the service is safe to call from multiple request threads.
 */
@Service
class RewardService(
    private val clock: Clock = Clock.system(ZoneId.systemDefault())
) {

    private val userCredits = ConcurrentHashMap<String, Int>()
    private val userStreaks = ConcurrentHashMap<String, Int>()
    private val userLastActivity = ConcurrentHashMap<String, LocalDate>()

    /**
     * Called when a submission is approved. Grants [credits] and updates the
     * user's streak based on the current date.
     */
    fun onSubmissionApproved(userId: String, credits: Int): RewardResult {
        val totalCredits = addCredits(userId, credits)
        val streak = bumpStreak(userId)
        return RewardResult(
            creditsAwarded = credits,
            totalCredits = totalCredits,
            streak = streak
        )
    }

    /**
     * Grants credits without touching the streak. Kept for callers that only
     * need the credit side-effect (e.g. bonus rewards, tests).
     */
    fun grantReward(userId: String, credits: Int): RewardResult {
        val totalCredits = addCredits(userId, credits)
        return RewardResult(
            creditsAwarded = credits,
            totalCredits = totalCredits,
            streak = userStreaks.getOrDefault(userId, 0)
        )
    }

    fun getCredits(userId: String): Int = userCredits.getOrDefault(userId, 0)

    fun getStreak(userId: String): Int = userStreaks.getOrDefault(userId, 0)

    private fun addCredits(userId: String, credits: Int): Int =
        userCredits.merge(userId, credits, Int::plus) ?: credits

    private fun bumpStreak(userId: String): Int {
        val today = LocalDate.now(clock)
        val last = userLastActivity[userId]
        val newStreak = when {
            last == null -> 1
            last == today -> userStreaks.getOrDefault(userId, 1)
            last.plusDays(1) == today -> userStreaks.getOrDefault(userId, 0) + 1
            else -> 1
        }
        userStreaks[userId] = newStreak
        userLastActivity[userId] = today
        return newStreak
    }
}

data class RewardResult(
    val creditsAwarded: Int,
    val totalCredits: Int,
    val streak: Int
)
