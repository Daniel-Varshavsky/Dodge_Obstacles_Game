package com.example.dodge_obstacles_game

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.dodge_obstacles_game.animations.playerAnimations.pikachuFrames
import com.example.dodge_obstacles_game.interfaces.TiltCallback
import com.example.dodge_obstacles_game.logic.GameManager
import com.example.dodge_obstacles_game.model.captureState
import com.example.dodge_obstacles_game.model.enemyState
import com.example.dodge_obstacles_game.model.thrownObject
import com.example.dodge_obstacles_game.model.thrownType
import com.example.dodge_obstacles_game.utilities.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    /* ───────────────────────── UI REFERENCES ───────────────────────── */
    private lateinit var main_LBL_score: MaterialTextView
    private lateinit var main_LBL_time: MaterialTextView
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var btnLeft: MaterialButton
    private lateinit var btnRight: MaterialButton
    private lateinit var grid: Array<Array<AppCompatImageView>>

    /* ───────────────────────── GAME STATE ───────────────────────── */
    private lateinit var gameManager: GameManager

    /* ───────────────────────── TIMER / PROGRESSION ───────────────────────── */
    private var timerJob: Job? = null
    private var captureJob: Job? = null
    private var timerOn = false
    private var startTime = 0L
    private var accumulatedTime = 0L
    private var currentDelay = Constants.Timer.DELAY

    /* ───────────────────────── CONTROL MODE ───────────────────────── */
    private lateinit var gameMode: String
    private var tiltDetector: TiltDetector? = null
    private var controlsEnabled = true

    /* ───────────────────────── VISUAL STATE ───────────────────────── */
    private var pikachuFrameIndex = 0
    private var playerVisible = true
    private var hasNavigated = false
    private var pausedCaptureState: captureState? = null
    private var currentCaptureEnemy: thrownObject? = null
    private var inFastApproach = false

    /* ───────────────────────── SOUND PLAYER ───────────────────────── */
    private lateinit var soundPlayer: SingleSoundPlayer

    /* ───────────────────────── ACTIVITY LIFECYCLE ───────────────────────── */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initializeUI()
        readGameMode()
        gameManager = GameManager(main_IMG_hearts.size)
        configureControls()

        startTime = System.currentTimeMillis()
        startTimer()

        soundPlayer = SingleSoundPlayer(this)
    }

    override fun onResume() {
        super.onResume()
        resumeGame()
        resumeCaptureSequenceIfNeeded()
    }

    override fun onPause() {
        super.onPause()
        pauseGame()
        saveCaptureStateIfActive()
    }

    /* ───────────────────────── INITIALIZATION ───────────────────────── */
    private fun initializeUI() {
        applySystemInsets()
        findViews()
    }

    private fun applySystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

        grid = Array(Constants.GameConfig.ROWS) { row ->
            Array(Constants.GameConfig.COLS) { col ->
                findViewById(
                    resources.getIdentifier("cell_${row}_${col}", "id", packageName)
                )
            }
        }
    }

    private fun readGameMode() {
        gameMode = SharedPreferencesManager.getInstance()
            .getString(Constants.SP_KEYS.GAME_MODE, Constants.GAME_MODE.BUTTONS_NORMAL)
    }

    private fun configureControls() {
        when (gameMode) {
            Constants.GAME_MODE.TILT -> enableTiltControls()
            Constants.GAME_MODE.BUTTONS_EASY -> {
                currentDelay = Constants.Timer.MAX_DELAY
                enableButtonControls()
            }
            Constants.GAME_MODE.BUTTONS_HARD -> {
                currentDelay = Constants.Timer.MIN_DELAY
                enableButtonControls()
            }
            else -> { // BUTTONS_NORMAL
                currentDelay = Constants.Timer.DELAY
                enableButtonControls()
            }
        }
        refreshUI()
    }

    /* ───────────────────────── INPUT HANDLING ───────────────────────── */
    private fun enableButtonControls() {
        btnLeft.visibility = View.VISIBLE
        btnRight.visibility = View.VISIBLE

        btnLeft.setOnClickListener {
            if (!controlsEnabled) return@setOnClickListener
            gameManager.movePlayerLeft()
            postPlayerAction()
        }

        btnRight.setOnClickListener {
            if (!controlsEnabled) return@setOnClickListener
            gameManager.movePlayerRight()
            postPlayerAction()
        }
    }

    private fun enableTiltControls() {
        btnLeft.visibility = View.INVISIBLE
        btnRight.visibility = View.INVISIBLE

        tiltDetector = TiltDetector(this, object : TiltCallback {
            override fun onTiltLeft() { gameManager.movePlayerLeft(); postPlayerAction() }
            override fun onTiltRight() { gameManager.movePlayerRight(); postPlayerAction() }
            override fun onTiltForward() {
                currentDelay = (currentDelay - Constants.Timer.DELAY_STEP)
                    .coerceAtLeast(Constants.Timer.MIN_DELAY)
            }
            override fun onTiltBackward() {
                currentDelay = (currentDelay + Constants.Timer.DELAY_STEP)
                    .coerceAtMost(Constants.Timer.MAX_DELAY)
            }
        })
    }

    private fun postPlayerAction() {
        checkDamage()
        refreshUI()
    }

    /* ───────────────────────── TIMER MANAGEMENT ───────────────────────── */
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

    private fun stopTimer() {
        timerOn = false
        timerJob?.cancel()
    }

    private fun updateTimerUI(now: Long) {
        val elapsed = accumulatedTime + (now - startTime)
        main_LBL_time.text = TimeFormatter.formatTime(elapsed)
    }

    /* ───────────────────────── UI RENDERING ───────────────────────── */
    private fun refreshUI() {
        gameManager.consumeCaptureStart()?.let { runCaptureSequence(it); return }
        clearGrid()
        drawPlayer()
        drawObjects()
        updateHearts()
        main_LBL_score.text = gameManager.score.toString()
    }

    private fun clearGrid() {
        for (row in grid) for (cell in row) cell.setImageDrawable(null)
    }

    private fun drawPlayer() {
        if (!playerVisible) return
        grid[Constants.GameConfig.PLAYER_ROW][gameManager.playerCol]
            .setImageResource(pikachuFrames[pikachuFrameIndex])
        pikachuFrameIndex = (pikachuFrameIndex + 1) % pikachuFrames.size
    }

    private fun drawObjects() {
        for (obj in gameManager.getObjects()) {
            val cell = grid[obj.row][obj.col]
            when (obj.type) {
                thrownType.POTION -> cell.setImageResource(obj.drawableRes!!)
                thrownType.ENEMY -> when (obj.state) {
                    enemyState.MOVE -> {
                        val frames = obj.animationSet!!.moveFrames
                        cell.setImageResource(frames[obj.frameIndex])
                        obj.frameIndex = (obj.frameIndex + 1) % frames.size
                    }
                    enemyState.CAPTURE -> cell.setImageResource(obj.animationSet!!.captureFrames[obj.frameIndex])
                    enemyState.RELEASE -> cell.setImageResource(obj.animationSet!!.releaseFrame)
                }
            }
        }
    }

    /* ───────────────────────── CAPTURE SEQUENCE ───────────────────────── */
    private fun runCaptureSequence(enemy: thrownObject, resumeState: captureState? = null) {
        currentCaptureEnemy = enemy
        captureJob = lifecycleScope.launch {
            enterCaptureMode()
            playerVisible = false

            // FAST_APPROACH PHASE
            if (resumeState?.phase != captureState.Phase.CAPTURE_ANIMATION) {
                inFastApproach = true
                while (isActive && gameManager.fastApproachStep()) {
                    refreshUI()
                    delay(30)
                }
                refreshUI()
                delay(30)
                inFastApproach = false
            }

            enemy.state = enemyState.CAPTURE
            val frames = enemy.animationSet!!.captureFrames
            val remainingLoops = resumeState?.loopIndex ?: enemy.captureLoopsRemaining
            var startFrame = resumeState?.frameIndex ?: 0

            for (loop in 0 until remainingLoops) {

                soundPlayer.playSound(R.raw.sound_shake)

                for (i in startFrame until frames.size) {
                    if (!isActive) return@launch
                    enemy.frameIndex = i
                    refreshUI()
                    if (i == frames.lastIndex) {
                        if (gameManager.isGameOver && enemy.captureLoopsRemaining == 1) {
                            delay(300L)
                            soundPlayer.playSound(R.raw.sound_capture)
                            delay(700L)
                        } else
                            delay(300L)
                    }
                    else
                        delay(50L)
                }
                enemy.captureLoopsRemaining--
                startFrame = 0
            }

            // RELEASE PHASE
            if (gameManager.lives > 0) {
                soundPlayer.playSound(R.raw.sound_release)
                enemy.state = enemyState.RELEASE
                refreshUI()
                delay(300)
            }

            enemy.state = enemyState.MOVE
            playerVisible = true
            gameManager.finishCapture()
            exitCaptureMode()
            currentCaptureEnemy = null

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

    private fun resumeCaptureSequenceIfNeeded() {
        pausedCaptureState?.let { state ->
            captureJob = lifecycleScope.launch { runCaptureSequence(state.enemy, state) }
            pausedCaptureState = null
        }
    }

    private fun saveCaptureStateIfActive() {
        currentCaptureEnemy?.let { enemy ->
            val phase = if (captureJob?.isActive == true && inFastApproach)
                captureState.Phase.FAST_APPROACH
            else
                captureState.Phase.CAPTURE_ANIMATION

            pausedCaptureState = captureState(
                enemy = enemy,
                phase = phase,
                loopIndex = enemy.captureLoopsRemaining,
                frameIndex = enemy.frameIndex
            )
        }
        captureJob?.cancel()
        captureJob = null
    }

    /* ───────────────────────── PLAYER FEEDBACK ───────────────────────── */
    private fun updateHearts() {
        for (i in main_IMG_hearts.indices)
            main_IMG_hearts[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
    }

    private fun checkDamage() {
        if (gameManager.consumeDamageFlag()) notifyLifeLost()
        if (gameManager.consumeHealFlag()) notifyLifeHealed()
    }

    private fun notifyLifeLost() {
        SignalManager.getInstance().toast(
            if (gameManager.lives == 0) "You were caught!"
            else "You were caught, but managed to escape!",
            SignalManager.ToastLength.SHORT
        )
        SignalManager.getInstance().vibrate()
    }

    private fun notifyLifeHealed() {
        SignalManager.getInstance().toast(
            "You used a potion and healed a life!",
            SignalManager.ToastLength.SHORT
        )
    }

    /* ───────────────────────── NAVIGATION ───────────────────────── */
    private fun changeActivity(score: Int, timeMillis: String) {
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra(Constants.BundleKeys.SCORE_KEY, score)
        intent.putExtra(Constants.BundleKeys.TIME_KEY, timeMillis)
        startActivity(intent)
        finish()
    }
}