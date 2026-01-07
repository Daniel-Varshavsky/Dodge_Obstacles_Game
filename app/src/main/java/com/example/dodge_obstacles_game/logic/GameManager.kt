package com.example.dodge_obstacles_game.logic

import com.example.dodge_obstacles_game.R
import com.example.dodge_obstacles_game.model.thrownObject
import com.example.dodge_obstacles_game.model.thrownType
import com.example.dodge_obstacles_game.animations.enemyAnimations
import com.example.dodge_obstacles_game.model.enemyState
import com.example.dodge_obstacles_game.model.gamePhase
import com.example.dodge_obstacles_game.utilities.Constants.GameConfig
import kotlin.random.Random

class GameManager(private val lifeCount: Int = 3) {

    var score: Int = 0
        private set

    var lives: Int = lifeCount
        private set

    var playerCol: Int = GameConfig.COLS / 2
        private set

    var phase: gamePhase = gamePhase.RUNNING
        private set

    private val objects = mutableListOf<thrownObject>()

    private var damageTakenThisTurn = false
    private var healedThisTurn = false

    private val potionDrawable = R.drawable.potion  // ← change if needed

    private var turnsUntilNextEnemy = Random.nextInt(1, 3)
    private val potionSpawnChance = 0.15f

    private var capturingEnemy: thrownObject? = null

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
        if (phase == gamePhase.CAPTURE) return

        score += 10
        spawnObjects()

        val updatedObjects = mutableListOf<thrownObject>()

        for (obj in objects) {
            obj.row++

            if (obj.row < GameConfig.ROWS) {
                updatedObjects.add(obj)
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
                    type = thrownType.ENEMY,
                    animationSet = enemyAnimations.ALL.random(),
                    frameIndex = 0,
                    captureLoopsRemaining = 0
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
        val collided = objects.filter { it.row == GameConfig.PLAYER_ROW && it.col == playerCol }

        for (obj in collided) {
            if (obj.type == thrownType.ENEMY && obj.state == enemyState.CAPTURE) continue

            handleCollision(obj)

            // Only remove potions and non-capturing enemies
            if (obj.type == thrownType.POTION || obj.state != enemyState.CAPTURE) {
                objects.remove(obj)
            }
        }
    }

    private fun handleCollision(obj: thrownObject) {
        if (phase == gamePhase.CAPTURE) return

        when (obj.type) {
            thrownType.ENEMY -> startCapture(obj)
            thrownType.POTION -> heal()
        }
    }

    private fun startCapture(enemy: thrownObject) {
        if (phase == gamePhase.CAPTURE) return

        phase = gamePhase.CAPTURE
        takeDamage()

        enemy.state = enemyState.CAPTURE
        enemy.frameIndex = 0

        // If the player dies, force 3 capture loops
        enemy.captureLoopsRemaining = if (lives == 0) 3 else Random.nextInt(0, 3)

        capturingEnemy = enemy
    }


    fun fastDropStep(): Boolean {
        var hasFallingObjects = false
        val updatedObjects = mutableListOf<thrownObject>()

        for (obj in objects) {
            if (obj.state == enemyState.CAPTURE) {
                updatedObjects.add(obj)
                continue
            }

            obj.row++
            if (obj.row < GameConfig.ROWS) {
                updatedObjects.add(obj)
                hasFallingObjects = true
            }
        }

        objects.clear()
        objects.addAll(updatedObjects)

        checkPlayerCollision()

        return hasFallingObjects
    }

    fun finishCapture() {
        phase = gamePhase.RUNNING
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

    fun consumeCaptureStart(): thrownObject? {
        val enemy = capturingEnemy
        capturingEnemy = null
        return enemy
    }

    fun getObjects(): List<thrownObject> = objects.toList()
}
