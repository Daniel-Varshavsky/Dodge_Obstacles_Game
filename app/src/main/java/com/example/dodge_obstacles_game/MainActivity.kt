package com.example.dodge_obstacles_game

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.dodge_obstacles_game.animations.playerAnimations.pikachuFrames
import com.example.dodge_obstacles_game.logic.GameManager
import com.example.dodge_obstacles_game.utilities.Constants
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager
import com.example.dodge_obstacles_game.utilities.TimeFormatter
import com.example.dodge_obstacles_game.interfaces.TiltCallback
import com.example.dodge_obstacles_game.model.enemyState
import com.example.dodge_obstacles_game.model.gamePhase
import com.example.dodge_obstacles_game.model.thrownObject
import com.example.dodge_obstacles_game.model.thrownType
import com.example.dodge_obstacles_game.utilities.SignalManager
import com.example.dodge_obstacles_game.utilities.TiltDetector
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.toString

class MainActivity : AppCompatActivity() {

    // ───────────────── UI ─────────────────
    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var main_LBL_time: MaterialTextView
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    private lateinit var btnLeft: MaterialButton
    private lateinit var btnRight: MaterialButton

    // ───────────────── Game ─────────────────
    private lateinit var grid: Array<Array<AppCompatImageView>>
    private lateinit var gameManager: GameManager

    // ───────────────── Timer ─────────────────
    private var timerJob: Job? = null
    private var accumulatedTime = 0L
    private var startTime = 0L
    private var timerOn = false

    private var currentDelay = Constants.Timer.DELAY

    private val MIN_DELAY = 150L   // fastest
    private val MAX_DELAY = 1_000L   // slowest
    private val DELAY_STEP = 100L

    // ───────────────── Control Mode ─────────────────
    private lateinit var gameMode: String
    private var tiltDetector: TiltDetector? = null
    private var controlsEnabled = true

    // ───────────────── Animation ─────────────────
    private var pikachuFrameIndex = 0
    private var playerVisible = true
    private var hasNavigated = false

    // ───────────────── Lifecycle ─────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ READ GAME MODE FROM PREFERENCES
        gameMode = SharedPreferencesManager.getInstance()
            .getString(
                Constants.SP_KEYS.GAME_MODE,
                Constants.GAME_MODE.BUTTONS_NORMAL
            )


        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()

        startTime = System.currentTimeMillis()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        resumeGame()
    }

    override fun onPause() {
        super.onPause()
        pauseGame()
    }

    // ───────────────── Setup ─────────────────
    private fun findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score)
        main_LBL_time = findViewById(R.id.main_LBL_time)

        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )

        btnLeft = findViewById(R.id.btnLeft)
        btnRight = findViewById(R.id.btnRight)

        grid = Array(Constants.GameConfig.ROWS) { row ->
            Array(Constants.GameConfig.COLS) { col ->
                val resId = resources.getIdentifier(
                    "cell_${row}_${col}",
                    "id",
                    packageName
                )
                findViewById(resId)
            }
        }
    }

    private fun initViews() {
        when (gameMode) {
            Constants.GAME_MODE.TILT -> {
                enableTiltControls()
            }

            Constants.GAME_MODE.BUTTONS_EASY -> {
                currentDelay = MAX_DELAY
                enableButtonControls()
            }

            Constants.GAME_MODE.BUTTONS_HARD -> {
                currentDelay = MIN_DELAY
                enableButtonControls()
            }

            else -> { // BUTTONS_NORMAL
                currentDelay = Constants.Timer.DELAY
                enableButtonControls()
            }
        }


        refreshUI()
    }

    // ───────────────── Controls ─────────────────
    private fun enableButtonControls() {
        btnLeft.visibility = View.VISIBLE
        btnRight.visibility = View.VISIBLE

        btnLeft.setOnClickListener {
            if (!controlsEnabled) return@setOnClickListener

            gameManager.movePlayerLeft()
            checkDamage()
            refreshUI()
        }

        btnRight.setOnClickListener {
            if (!controlsEnabled) return@setOnClickListener

            gameManager.movePlayerRight()
            checkDamage()
            refreshUI()
        }
    }


    private fun enableTiltControls() {
        btnLeft.visibility = View.INVISIBLE
        btnRight.visibility = View.INVISIBLE

        tiltDetector = TiltDetector(
            this,
            object : TiltCallback {

                override fun onTiltLeft() {
                    gameManager.movePlayerLeft()
                    checkDamage()
                    refreshUI()
                }

                override fun onTiltRight() {
                    gameManager.movePlayerRight()
                    checkDamage()
                    refreshUI()
                }

                override fun onTiltForward() {
                    currentDelay =
                        (currentDelay - DELAY_STEP).coerceAtLeast(MIN_DELAY)
                }

                override fun onTiltBackward() {
                    currentDelay =
                        (currentDelay + DELAY_STEP).coerceAtMost(MAX_DELAY)
                }
            })
    }


    // ───────────────── Timer ─────────────────
    private fun startTimer() {
        timerOn = true
        timerJob = lifecycleScope.launch {
            while (timerOn) {
                val now = System.currentTimeMillis()
                updateTimerUI(now)

                gameManager.nextTurn()
                checkDamage()
                refreshUI()

                delay(currentDelay)
            }
        }
    }

    private fun pauseGame() {
        if (!timerOn) return
        accumulatedTime += System.currentTimeMillis() - startTime
        timerOn = false
        timerJob?.cancel()
        timerJob = null
        tiltDetector?.stop()
    }

    private fun resumeGame() {
        if (!gameManager.isGameOver && !timerOn) {
            startTime = System.currentTimeMillis()
            startTimer()
        }

        if (gameMode == Constants.GAME_MODE.TILT) {
            tiltDetector?.start()
        }
    }

    private fun updateTimerUI(now: Long) {
        val elapsed = accumulatedTime + (now - startTime)
        main_LBL_time.text = TimeFormatter.formatTime(elapsed)
    }

    // ───────────────── Rendering ─────────────────
    private fun refreshUI() {
        gameManager.consumeCaptureStart()?.let { enemy ->
            runCaptureSequence(enemy)
            return
        }

        clearGrid()
        drawPlayer()
        drawObjects()
        updateHearts()
        main_LBL_score.text = gameManager.score.toString()
    }

    private fun stopTimer() {
        timerOn = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun clearGrid() {
        for (row in grid)
            for (cell in row)
                cell.setImageDrawable(null)
    }

    private fun drawPlayer() {
        if (!playerVisible) return

        grid[Constants.GameConfig.PLAYER_ROW][gameManager.playerCol]
            .setImageResource(pikachuFrames[pikachuFrameIndex])

        pikachuFrameIndex = (pikachuFrameIndex + 1) % pikachuFrames.size
    }


    private fun drawObjects() {
        for (obj in gameManager.getObjects()) {
            val imageView = grid[obj.row][obj.col]

            when (obj.type) {
                thrownType.POTION -> {
                    imageView.setImageResource(obj.drawableRes!!)
                }

                thrownType.ENEMY -> {
                    when (obj.state) {
                        enemyState.MOVE -> {
                            val frames = obj.animationSet!!.moveFrames
                            imageView.setImageResource(frames[obj.frameIndex])
                            obj.frameIndex = (obj.frameIndex + 1) % frames.size
                        }

                        enemyState.CAPTURE -> {
                            val frames = obj.animationSet!!.captureFrames
                            imageView.setImageResource(frames[obj.frameIndex])
                        }

                        enemyState.RELEASE -> {
                            imageView.setImageResource(obj.animationSet!!.releaseFrame)
                        }
                    }
                }
            }
        }
    }

    private fun runCaptureSequence(enemy: thrownObject) {
        lifecycleScope.launch {
            enterCaptureMode()
            playerVisible = false

            // FAST DROP LOOP
            while (gameManager.fastDropStep()) {
                refreshUI()
                delay(30)
            }
            refreshUI()
            delay(30)

            // CAPTURE ANIMATION: use state + frameIndex
            enemy.state = enemyState.CAPTURE
            enemy.frameIndex = 0

            // first frame pause
            refreshUI()
            delay(300)

            repeat(enemy.captureLoopsRemaining) {
                val frames = enemy.animationSet!!.captureFrames
                for (i in frames.indices) {
                    enemy.frameIndex = i
                    refreshUI()
                    if (i == frames.lastIndex) {
                        if (gameManager.isGameOver && enemy.captureLoopsRemaining == 1)
                            delay(1000)
                        else
                            delay(300)
                    }
                    else
                        delay(50)
                }

                enemy.captureLoopsRemaining--
            }

            // Only show RELEASE if player still has lives
            if (gameManager.lives > 0) {
                enemy.state = enemyState.RELEASE
                enemy.frameIndex = 0
                refreshUI()
                delay(300)
            }

            // back to normal
            enemy.state = enemyState.MOVE
            playerVisible = true
            gameManager.finishCapture()

            exitCaptureMode()

            if (gameManager.isGameOver && !hasNavigated) {
                hasNavigated = true
                stopTimer()
                val finalTime = accumulatedTime + (System.currentTimeMillis() - startTime)
                changeActivity(gameManager.score, TimeFormatter.formatTime(finalTime))
            }
        }
    }


    private fun enterCaptureMode() {
        pauseGame()
        controlsEnabled = false
    }

    private fun exitCaptureMode() {
        controlsEnabled = true
        resumeGame()
    }


    private fun updateHearts() {
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility =
                if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
    }

    // ───────────────── Damage ─────────────────
    private fun checkDamage() {
        if (gameManager.consumeDamageFlag()) {
            notifyLifeLost()
        }
        if (gameManager.consumeHealFlag()) {
            notifyLifeHealed()
        }
    }

    private fun notifyLifeLost() {
        val text =
            if (gameManager.lives == 0)
                "You were caught!"
            else
                "You were caught, but managed to escape!"

        SignalManager
            .getInstance()
            .toast(text, SignalManager.ToastLength.SHORT)

        SignalManager
            .getInstance()
            .vibrate()
    }

    private fun notifyLifeHealed() {
        val text = "You used a potion and healed a life!"

        SignalManager
            .getInstance()
            .toast(text, SignalManager.ToastLength.SHORT)
    }

    // ───────────────── Navigation ─────────────────
    private fun changeActivity(score: Int, timeMillis: String) {
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtra(Constants.BundleKeys.TIME_KEY, timeMillis)
        startActivity(intent)
        finish()
    }
}
