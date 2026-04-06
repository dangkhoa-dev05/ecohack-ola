package com.ecoquest.backend.service

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class RewardService {

    private val userCredits = ConcurrentHashMap<String, Int>()

    fun grantReward(userId: String, credits: Int): RewardResult {
        val newTotal = userCredits.merge(userId, credits, Int::plus) ?: credits
        return RewardResult(creditsAwarded = credits, totalCredits = newTotal)
    }

    fun getCredits(userId: String): Int = userCredits.getOrDefault(userId, 0)
}

data class RewardResult(
    val creditsAwarded: Int,
    val totalCredits: Int
)
