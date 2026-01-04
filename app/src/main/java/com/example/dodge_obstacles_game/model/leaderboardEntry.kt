package com.example.dodge_obstacles_game.model

data class leaderboardEntry(
    val name: String,
    val score: Int?,
    val time: String?,
    val latitude: Double? = null,
    val longitude: Double? = null
)
