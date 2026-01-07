package com.example.dodge_obstacles_game.model

data class enemyAnimationSet(
    val moveFrames: List<Int>,
    val captureFrames: List<Int>,
    val releaseFrame: Int
)
