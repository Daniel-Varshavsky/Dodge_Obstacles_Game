package com.example.dodge_obstacles_game.logic

import com.example.dodge_obstacles_game.R
import com.example.dodge_obstacles_game.model.thrownObject
import com.example.dodge_obstacles_game.model.thrownType
import com.example.dodge_obstacles_game.utilities.Constants.GameConfig
import kotlin.random.Random

class GameManager(private val lifeCount: Int = 3) {

    var score: Int = 0
        private set

    var lives: Int = lifeCount
        private set

    var playerCol: Int = GameConfig.COLS / 2
        private set

    private val objects = mutableListOf<thrownObject>()

    private var damageTakenThisTurn = false
    private var healedThisTurn = false

    private val enemyDrawables = listOf(
        R.drawable.pokeball,
        R.drawable.greatball,
        R.drawable.ultraball,
        R.drawable.masterball
    )

    private val potionDrawable = R.drawable.potion  // ← change if needed

    private var turnsUntilNextEnemy = Random.nextInt(1, 3)
    private val potionSpawnChance = 0.15f

    val isGameOver: Boolean
        get() = lives <= 0

    /* ---------------- Player movement ---------------- */

    fun movePlayerLeft() {
        if (playerCol > 0) {
            playerCol--
            checkPlayerCollision()
        }
    }

    fun movePlayerRight() {
        if (playerCol < GameConfig.COLS - 1) {
            playerCol++
            checkPlayerCollision()
        }
    }

    /* ---------------- Game loop ---------------- */

    fun nextTurn() {
        spawnObjects()

        val updatedObjects = mutableListOf<thrownObject>()

        for (obj in objects) {
            obj.row++

            when {
                obj.row == GameConfig.PLAYER_ROW && obj.col == playerCol -> {
                    handleCollision(obj)
                }

                obj.row < GameConfig.ROWS -> {
                    updatedObjects.add(obj)
                }

                obj.type == thrownType.ENEMY -> {
                    score++
                }
            }
        }

        objects.clear()
        objects.addAll(updatedObjects)

        checkPlayerCollision()
    }

    /* ---------------- Spawning ---------------- */

    private fun spawnObjects() {
        val occupiedCols = mutableSetOf<Int>()

        spawnEnemyIfNeeded(occupiedCols)
        spawnPotionIfNeeded(occupiedCols)
    }

    private fun spawnEnemyIfNeeded(occupiedCols: MutableSet<Int>) {
        if (turnsUntilNextEnemy <= 0) {
            val col = generateFreeColumn(occupiedCols)

            objects.add(
                thrownObject(
                    row = 0,
                    col = col,
                    drawableRes = enemyDrawables.random(),
                    type = thrownType.ENEMY
                )
            )

            occupiedCols.add(col)
            turnsUntilNextEnemy = Random.nextInt(1, 3)
        } else {
            turnsUntilNextEnemy--
        }
    }

    private fun spawnPotionIfNeeded(occupiedCols: MutableSet<Int>) {
        if (Random.nextFloat() < potionSpawnChance) {
            val col = generateFreeColumn(occupiedCols)

            objects.add(
                thrownObject(
                    row = 0,
                    col = col,
                    drawableRes = potionDrawable,
                    type = thrownType.POTION
                )
            )

            occupiedCols.add(col)
        }
    }

    private fun generateFreeColumn(occupiedCols: Set<Int>): Int {
        val freeCols = (0 until GameConfig.COLS).filterNot { it in occupiedCols }
        return if (freeCols.isNotEmpty()) freeCols.random()
        else Random.nextInt(GameConfig.COLS)
    }

    /* ---------------- Collision handling ---------------- */

    private fun checkPlayerCollision() {
        val obj = objects.firstOrNull {
            it.row == GameConfig.PLAYER_ROW && it.col == playerCol
        }

        if (obj != null) {
            handleCollision(obj)
            objects.remove(obj)
        }
    }

    private fun handleCollision(obj: thrownObject) {
        when (obj.type) {
            thrownType.ENEMY -> takeDamage()
            thrownType.POTION -> heal()
        }
    }

    private fun takeDamage() {
        if (lives > 0) {
            lives--
            damageTakenThisTurn = true
        }
    }

    private fun heal() {
        if (lives < lifeCount) {
            lives++
            healedThisTurn = true
        }
    }

    /* ---------------- UI hooks ---------------- */

    fun consumeDamageFlag(): Boolean {
        val result = damageTakenThisTurn
        damageTakenThisTurn = false
        return result
    }

    fun consumeHealFlag(): Boolean {
        val result = healedThisTurn
        healedThisTurn = false
        return result
    }

    fun getObjects(): List<thrownObject> = objects.toList()
}
