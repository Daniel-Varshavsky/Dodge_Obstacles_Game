package com.example.dodge_obstacles_game.model

data class thrownObject(
    var row: Int,
    val col: Int,
    val drawableRes: Int,
    val type: thrownType
)