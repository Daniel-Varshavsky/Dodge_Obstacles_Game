package com.example.dodge_obstacles_game.model

data class thrownObject(
    var row: Int,
    val col: Int,
    val drawableRes: Int? = null,
    val type: thrownType,
    val animationSet: enemyAnimationSet? = null,

    // animation state
    var state: enemyState = enemyState.MOVE,
    var frameIndex: Int = 0,
    var captureLoopsRemaining: Int = 0
)