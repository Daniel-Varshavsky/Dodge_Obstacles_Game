package com.example.dodge_obstacles_game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dodge_obstacles_game.utilities.Constants
import com.google.android.material.button.MaterialButton
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager

class StartActivity : AppCompatActivity() {

    private lateinit var start_BTN_play: MaterialButton
    private lateinit var start_BTN_settings: MaterialButton
    private lateinit var start_BTN_leaderboard: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        findViews()
        initViews()
    }

    private fun findViews() {
        start_BTN_play = findViewById(R.id.start_BTN_play)
        start_BTN_settings = findViewById(R.id.start_BTN_settings)
        start_BTN_leaderboard = findViewById(R.id.start_BTN_leaderboard)
    }

    private fun initViews() {
        start_BTN_play.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        start_BTN_leaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        start_BTN_settings.setOnClickListener {
            showControlSettingsDialog()
        }
    }

    private fun showControlSettingsDialog() {

        val sp = SharedPreferencesManager.getInstance()

        val currentMode = sp.getString(
            Constants.SP_KEYS.CONTROL_MODE,
            Constants.CONTROL_MODES.BUTTONS
        )

        val options = arrayOf("Buttons", "Tilt")
        val checkedItem =
            if (currentMode == Constants.CONTROL_MODES.TILT) 1 else 0

        AlertDialog.Builder(this)
            .setTitle("Choose control method")
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->

                val selectedMode =
                    if (which == 1)
                        Constants.CONTROL_MODES.TILT
                    else
                        Constants.CONTROL_MODES.BUTTONS

                sp.putString(Constants.SP_KEYS.CONTROL_MODE, selectedMode)
                dialog.dismiss()

                // 🔽 Only show difficulty for BUTTONS
                if (selectedMode == Constants.CONTROL_MODES.BUTTONS) {
                    showDifficultyDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDifficultyDialog() {

        val sp = SharedPreferencesManager.getInstance()

        val currentDifficulty = sp.getString(
            Constants.SP_KEYS.DIFFICULTY,
            Constants.DIFFICULTY.NORMAL
        )

        val options = arrayOf("Easy", "Normal", "Hard")

        val checkedItem = when (currentDifficulty) {
            Constants.DIFFICULTY.EASY -> 0
            Constants.DIFFICULTY.HARD -> 2
            else -> 1
        }

        AlertDialog.Builder(this)
            .setTitle("Choose difficulty")
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->

                val difficulty = when (which) {
                    0 -> Constants.DIFFICULTY.EASY
                    2 -> Constants.DIFFICULTY.HARD
                    else -> Constants.DIFFICULTY.NORMAL
                }

                sp.putString(Constants.SP_KEYS.DIFFICULTY, difficulty)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


}
