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
import com.example.dodge_obstacles_game.logic.GameManager
import com.example.dodge_obstacles_game.utilities.Constants
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager
import com.example.dodge_obstacles_game.utilities.TimeFormatter
import com.example.dodge_obstacles_game.interfaces.TiltCallback
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
    private var controlMode = Constants.CONTROL_MODES.BUTTONS
    private lateinit var tiltDetector: TiltDetector

    // ───────────────── Animation ─────────────────
    private val pikachuFrames = listOf(
        R.drawable.pikachu_walk001,
        R.drawable.pikachu_walk002,
        R.drawable.pikachu_walk001,
        R.drawable.pikachu_walk004
    )
    private var pikachuFrameIndex = 0

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

        // ✅ READ CONTROL MODE FROM PREFERENCES
        controlMode = SharedPreferencesManager.getInstance()
            .getString(Constants.SP_KEYS.CONTROL_MODE, Constants.CONTROL_MODES.BUTTONS)

        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        initViews()

        startTime = System.currentTimeMillis()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        resumeGame()

        if (controlMode == Constants.CONTROL_MODES.TILT) {
            tiltDetector.start()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseGame()
        tiltDetector.stop()
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
        if (controlMode == Constants.CONTROL_MODES.BUTTONS) {
            enableButtonControls()
        } else {
            enableTiltControls()
        }

        refreshUI()
    }

    // ───────────────── Controls ─────────────────
    private fun enableButtonControls() {
        btnLeft.visibility = View.VISIBLE
        btnRight.visibility = View.VISIBLE

        btnLeft.setOnClickListener {
            gameManager.movePlayerLeft()
            checkDamage()
            refreshUI()
        }

        btnRight.setOnClickListener {
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

                advancePikachuAnimation()
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
    }

    private fun resumeGame() {
        if (!gameManager.isGameOver && !timerOn) {
            startTime = System.currentTimeMillis()
            startTimer()
        }
    }

    private fun updateTimerUI(now: Long) {
        val elapsed = accumulatedTime + (now - startTime)
        main_LBL_time.text = TimeFormatter.formatTime(elapsed)
    }

    // ───────────────── Rendering ─────────────────
    private fun refreshUI() {
        if (gameManager.isGameOver && !hasNavigated) {
            hasNavigated = true
            stopTimer()
            val finalTime = accumulatedTime + (System.currentTimeMillis() - startTime)
            changeActivity(gameManager.score, TimeFormatter.formatTime(finalTime))
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
        grid[Constants.GameConfig.PLAYER_ROW][gameManager.playerCol]
            .setImageResource(pikachuFrames[pikachuFrameIndex])
    }

    private fun drawObjects() {
        for (obj in gameManager.getObjects()) {
            grid[obj.row][obj.col]
                .setImageResource(obj.drawableRes)
        }
    }

    private fun advancePikachuAnimation() {
        pikachuFrameIndex = (pikachuFrameIndex + 1) % pikachuFrames.size
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
