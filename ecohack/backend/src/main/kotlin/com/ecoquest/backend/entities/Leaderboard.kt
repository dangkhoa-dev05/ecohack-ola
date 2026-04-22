package com.ecoquest.backend.entities

data class Leaderboard(
    val rank: Int,
    val displayName: String,
    val credits: Int,
    val level: Int
)