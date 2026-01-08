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

    /* ───────────────────────── CORE GAME STATE ───────────────────────── */

    var score = 0
        private set

    var lives = lifeCount
        private set

    var playerCol = GameConfig.COLS / 2
        private set

    var phase = gamePhase.RUNNING
        private set

    private val objects = mutableListOf<thrownObject>()


    /* ───────────────────────── TURN FLAGS (UI SIGNALS) ───────────────────────── */

    private var damageTakenThisTurn = false
    private var healedThisTurn = false
    private var capturingEnemy: thrownObject? = null


    /* ───────────────────────── SPAWNING CONFIG ───────────────────────── */

    private val potionDrawable = R.drawable.potion
    private val potionSpawnChance = 0.15f
    private var turnsUntilNextEnemy = Random.nextInt(1, 3)


    val isGameOver: Boolean
        get() = lives <= 0


    /* ───────────────────────── PLAYER MOVEMENT ───────────────────────── */

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


    /* ───────────────────────── MAIN GAME LOOP ───────────────────────── */

    fun nextTurn() {
        if (phase == gamePhase.CAPTURE) return

        score += 10
        spawnObjects()
        advanceObjects()
        checkPlayerCollision()
    }

    private fun advanceObjects() {
        val updated = mutableListOf<thrownObject>()

        for (obj in objects) {
            obj.row++
            if (obj.row < GameConfig.ROWS) updated.add(obj)
        }

        objects.clear()
        objects.addAll(updated)
    }


    /* ───────────────────────── OBJECT SPAWNING ───────────────────────── */

    private fun spawnObjects() {
        val occupiedCols = mutableSetOf<Int>()
        spawnEnemyIfNeeded(occupiedCols)
        spawnPotionIfNeeded(occupiedCols)
    }

    private fun spawnEnemyIfNeeded(occupiedCols: MutableSet<Int>) {
        if (turnsUntilNextEnemy-- > 0) return

        val col = generateFreeColumn(occupiedCols)
        objects.add(
            thrownObject(
                row = 0,
                col = col,
                type = thrownType.ENEMY,
                animationSet = enemyAnimations.ALL.random()
            )
        )

        occupiedCols.add(col)
        turnsUntilNextEnemy = Random.nextInt(1, 3)
    }

    private fun spawnPotionIfNeeded(occupiedCols: MutableSet<Int>) {
        if (Random.nextFloat() >= potionSpawnChance) return

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

    private fun generateFreeColumn(occupiedCols: Set<Int>): Int =
        (0 until GameConfig.COLS)
            .filterNot { it in occupiedCols }
            .randomOrNull()
            ?: Random.nextInt(GameConfig.COLS)


    /* ───────────────────────── COLLISION & DAMAGE ───────────────────────── */

    private fun checkPlayerCollision() {
        val collided = objects.filter {
            it.row == GameConfig.PLAYER_ROW && it.col == playerCol
        }

        for (obj in collided) {
            if (obj.type == thrownType.ENEMY && obj.state == enemyState.CAPTURE) continue

            handleCollision(obj)

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


    /* ───────────────────────── CAPTURE FLOW ───────────────────────── */

    private fun startCapture(enemy: thrownObject) {
        if (phase == gamePhase.CAPTURE) return

        phase = gamePhase.CAPTURE
        takeDamage()

        enemy.state = enemyState.CAPTURE
        enemy.frameIndex = 0
        enemy.captureLoopsRemaining =
            if (lives == 0) 3 else Random.nextInt(0, 4)

        capturingEnemy = enemy
    }

    fun fastApproachStep(): Boolean {
        val updated = mutableListOf<thrownObject>()
        var hasObjectsRemaining = false

        for (obj in objects) {
            if (obj.state == enemyState.CAPTURE) {
                updated.add(obj)
                continue
            }

            obj.row++
            if (obj.row < GameConfig.ROWS) {
                updated.add(obj)
                hasObjectsRemaining = true
            }
        }

        objects.clear()
        objects.addAll(updated)
        checkPlayerCollision()

        return hasObjectsRemaining
    }

    fun finishCapture() {
        phase = gamePhase.RUNNING
    }


    /* ───────────────────────── LIFE MANAGEMENT ───────────────────────── */

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


    /* ───────────────────────── UI CONSUMPTION HOOKS ───────────────────────── */

    fun consumeDamageFlag(): Boolean =
        damageTakenThisTurn.also { damageTakenThisTurn = false }

    fun consumeHealFlag(): Boolean =
        healedThisTurn.also { healedThisTurn = false }

    fun consumeCaptureStart(): thrownObject? =
        capturingEnemy.also { capturingEnemy = null }

    fun getObjects(): List<thrownObject> = objects.toList()
}
