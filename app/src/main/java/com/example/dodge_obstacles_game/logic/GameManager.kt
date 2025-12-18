package com.example.dodge_obstacles_game.logic

import com.example.dodge_obstacles_game.R
import com.example.dodge_obstacles_game.model.Enemy
import kotlin.random.Random

class GameManager(val lifeCount: Int = 3) {

    var score: Int = 0
        private set
    var lives: Int = lifeCount
        private set
    var playerCol: Int = 1
        private set

    private val enemies = mutableListOf<Enemy>()

    private val enemyDrawables = listOf(
        R.drawable.pokeball,
        R.drawable.greatball,
        R.drawable.ultraball,
        R.drawable.masterball
    )
    private var turnsUntilNextEnemy: Int = Random.nextInt(1, 3)

    val isGameOver: Boolean
        get() = lives <= 0

    fun movePlayerLeft() {
        if (playerCol > 0) playerCol--
    }

    fun movePlayerRight() {
        if (playerCol < 2) playerCol++
    }

    fun nextTurn() {
        if (turnsUntilNextEnemy <= 0) {
            val col = Random.nextInt(0, 3)
            val drawable = enemyDrawables.random()
            enemies.add(Enemy(row = 0, col = col, drawableRes = drawable))
            turnsUntilNextEnemy = Random.nextInt(1, 3)
        } else {
            turnsUntilNextEnemy--
        }

        val newEnemies = mutableListOf<Enemy>()

        for (enemy in enemies) {
            enemy.row++

            if (enemy.row == 4 && enemy.col == playerCol) {
                lives--
            } else if (enemy.row < 5) {
                newEnemies.add(enemy)
            } else {
                score++
            }
        }

        enemies.clear()
        enemies.addAll(newEnemies)
    }

    fun getEnemies(): List<Enemy> = enemies.toList()
}
