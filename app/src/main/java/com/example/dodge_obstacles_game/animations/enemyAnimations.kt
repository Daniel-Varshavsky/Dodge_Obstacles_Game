package com.example.dodge_obstacles_game.animations

import com.example.dodge_obstacles_game.R
import com.example.dodge_obstacles_game.model.enemyAnimationSet

object enemyAnimations {

    val POKEBALL = enemyAnimationSet(
        moveFrames = listOf(
            R.drawable.pokeball_1,
            R.drawable.pokeball_2,
            R.drawable.pokeball_3,
            R.drawable.pokeball_4,
            R.drawable.pokeball_5,
            R.drawable.pokeball_6,
            R.drawable.ball_underneath,
            R.drawable.pokeball_8
        ),
        captureFrames = listOf(
            R.drawable.pokeball_1,
            R.drawable.pokeball_10,
            R.drawable.pokeball_11,
            R.drawable.pokeball_12,
            R.drawable.pokeball_11,
            R.drawable.pokeball_10,
            R.drawable.pokeball_1,
            R.drawable.pokeball_13,
            R.drawable.pokeball_14,
            R.drawable.pokeball_15,
            R.drawable.pokeball_14,
            R.drawable.pokeball_13,
            R.drawable.pokeball_1
        ),
        releaseFrame = R.drawable.pokeball_9
    )

    val GREATBALL = enemyAnimationSet(
        moveFrames = listOf(
            R.drawable.greatball_1,
            R.drawable.greatball_2,
            R.drawable.greatball_3,
            R.drawable.greatball_4,
            R.drawable.greatball_5,
            R.drawable.greatball_6,
            R.drawable.ball_underneath,
            R.drawable.greatball_8
        ),
        captureFrames = listOf(
            R.drawable.greatball_1,
            R.drawable.greatball_10,
            R.drawable.greatball_11,
            R.drawable.greatball_12,
            R.drawable.greatball_11,
            R.drawable.greatball_10,
            R.drawable.greatball_1,
            R.drawable.greatball_13,
            R.drawable.greatball_14,
            R.drawable.greatball_15,
            R.drawable.greatball_14,
            R.drawable.greatball_13,
            R.drawable.greatball_1
        ),
        releaseFrame = R.drawable.greatball_9
    )

    val ULTRABALL = enemyAnimationSet(
        moveFrames = listOf(
            R.drawable.ultraball_1,
            R.drawable.ultraball_2,
            R.drawable.ultraball_3,
            R.drawable.ultraball_4,
            R.drawable.ultraball_5,
            R.drawable.ultraball_6,
            R.drawable.ball_underneath,
            R.drawable.ultraball_8
        ),
        captureFrames = listOf(
            R.drawable.ultraball_1,
            R.drawable.ultraball_10,
            R.drawable.ultraball_11,
            R.drawable.ultraball_12,
            R.drawable.ultraball_11,
            R.drawable.ultraball_10,
            R.drawable.ultraball_1,
            R.drawable.ultraball_13,
            R.drawable.ultraball_14,
            R.drawable.ultraball_15,
            R.drawable.ultraball_14,
            R.drawable.ultraball_13,
            R.drawable.ultraball_1
        ),
        releaseFrame = R.drawable.ultraball_9
    )

    val MASTERBALL = enemyAnimationSet(
        moveFrames = listOf(
            R.drawable.masterball_1,
            R.drawable.masterball_2,
            R.drawable.masterball_3,
            R.drawable.masterball_4,
            R.drawable.masterball_5,
            R.drawable.masterball_6,
            R.drawable.ball_underneath,
            R.drawable.masterball_8
        ),
        captureFrames = listOf(
            R.drawable.masterball_1,
            R.drawable.masterball_10,
            R.drawable.masterball_11,
            R.drawable.masterball_12,
            R.drawable.masterball_11,
            R.drawable.masterball_10,
            R.drawable.masterball_1,
            R.drawable.masterball_13,
            R.drawable.masterball_14,
            R.drawable.masterball_15,
            R.drawable.masterball_14,
            R.drawable.masterball_13,
            R.drawable.masterball_1
        ),
        releaseFrame = R.drawable.masterball_9
    )

    val ALL = listOf(
        POKEBALL,
        GREATBALL,
        ULTRABALL,
        MASTERBALL
    )
}