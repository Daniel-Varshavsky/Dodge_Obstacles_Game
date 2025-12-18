package com.example.dodge_obstacles_game

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.example.dodge_obstacles_game.logic.GameManager
import com.example.dodge_obstacles_game.utilities.Constants
import com.example.dodge_obstacles_game.utilities.TimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var main_LBL_time: MaterialTextView
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>

    private lateinit var btnLeft: MaterialButton
    private lateinit var btnRight: MaterialButton

    private lateinit var grid: Array<Array<AppCompatImageView>>

    private lateinit var gameManager: GameManager

    private var timerJob: Job? = null
    private var accumulatedTime: Long = 0
    private var startTime: Long = 0
    private var timerOn: Boolean = false

    private val pikachuFrames = listOf(
        R.drawable.pikachu_walk001,
        R.drawable.pikachu_walk002,
        R.drawable.pikachu_walk001,
        R.drawable.pikachu_walk004
    )

    private var pikachuFrameIndex = 0

    private var previousHearts: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        gameManager = GameManager(lifeCount = main_IMG_hearts.size)
        initViews()
        startTime = System.currentTimeMillis()
        startTimer()
    }

    override fun onPause() {
        super.onPause()
        pauseGame()
    }

    override fun onResume() {
        super.onResume()
        resumeGame()
    }

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

        grid = arrayOf(
            arrayOf(findViewById(R.id.cell_0_0), findViewById(R.id.cell_0_1), findViewById(R.id.cell_0_2)),
            arrayOf(findViewById(R.id.cell_1_0), findViewById(R.id.cell_1_1), findViewById(R.id.cell_1_2)),
            arrayOf(findViewById(R.id.cell_2_0), findViewById(R.id.cell_2_1), findViewById(R.id.cell_2_2)),
            arrayOf(findViewById(R.id.cell_3_0), findViewById(R.id.cell_3_1), findViewById(R.id.cell_3_2)),
            arrayOf(findViewById(R.id.cell_4_0), findViewById(R.id.cell_4_1), findViewById(R.id.cell_4_2))
        )
    }

    private fun initViews() {

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

        refreshUI()
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

    private fun startTimer() {
        timerOn = true

        timerJob = lifecycleScope.launch {
            while (timerOn) {
                val now = System.currentTimeMillis()
                updateTimerUI(now)

                gameManager.nextTurn()
                checkDamage()

                if (gameManager.lives < previousHearts) {
                    notifyLifeLost()
                }

                advancePikachuAnimation()
                refreshUI()

                delay(Constants.Timer.DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerOn = false
        timerJob?.cancel()
        timerJob = null
    }

    private fun updateTimerUI(now: Long) {
        val elapsed = accumulatedTime + (now - startTime)
        main_LBL_time.text = TimeFormatter.formatTime(elapsed)
    }

    private fun refreshUI() {

        if (gameManager.isGameOver) {
            stopTimer()
            val finalElapsedTime =
                accumulatedTime + (System.currentTimeMillis() - startTime)

            changeActivity(
                gameManager.score,
                TimeFormatter.formatTime(finalElapsedTime)
            )
            return
        }

        clearGrid()
        drawPlayer()
        drawEnemies()
        updateHearts()

        main_LBL_score.text = gameManager.score.toString()
    }

    private fun clearGrid() {
        for (row in grid)
            for (cell in row)
                cell.setImageDrawable(null)
    }

    private fun advancePikachuAnimation() {
        pikachuFrameIndex = (pikachuFrameIndex + 1) % pikachuFrames.size
    }

    private fun drawPlayer() {
        grid[4][gameManager.playerCol]
            .setImageResource(pikachuFrames[pikachuFrameIndex])
    }

    private fun drawEnemies() {
        for (enemy in gameManager.getEnemies()) {
            grid[enemy.row][enemy.col]
                .setImageResource(enemy.drawableRes)
        }
    }

    private fun updateHearts() {
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility =
                if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun checkDamage() {
        if (gameManager.consumeDamageFlag()) {
            notifyLifeLost()
            previousHearts = gameManager.lives
        }
    }

    private fun notifyLifeLost() {
        previousHearts--;
        if (previousHearts == 0)
            Toast.makeText(
                this,
                "You were caught!",
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(
                this,
                "You were caught, but managed to escape!",
                Toast.LENGTH_SHORT
            ).show()

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    300,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(300)
        }
    }

    private fun changeActivity(score: Int, timeMillis: String) {
        val intent = Intent(this, ScoreActivity::class.java)
        val bundle = Bundle()

        bundle.putInt(Constants.BundleKeys.SCORE_KEY, score)
        bundle.putString(Constants.BundleKeys.TIME_KEY, timeMillis)

        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}