package com.example.dodge_obstacles_game.model

data class captureState(
val enemy: thrownObject,
val phase: Phase,
val loopIndex: Int,
val frameIndex: Int
) {
    enum class Phase { FAST_APPROACH, CAPTURE_ANIMATION }
}

