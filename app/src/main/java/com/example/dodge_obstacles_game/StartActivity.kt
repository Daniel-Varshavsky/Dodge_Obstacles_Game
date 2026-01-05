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

        val options = arrayOf(
            "Buttons - Easy",
            "Buttons - Normal",
            "Buttons - Hard",
            "Tilt"
        )

        val currentMode = sp.getString(
            Constants.SP_KEYS.GAME_MODE,
            Constants.GAME_MODE.BUTTONS_NORMAL
        )

        val checkedItem = when (currentMode) {
            Constants.GAME_MODE.BUTTONS_EASY -> 0
            Constants.GAME_MODE.BUTTONS_NORMAL -> 1
            Constants.GAME_MODE.BUTTONS_HARD -> 2
            Constants.GAME_MODE.TILT -> 3
            else -> 1
        }

        AlertDialog.Builder(this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        )
            .setTitle("Game Settings")
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->

                val selectedMode = when (which) {
                    0 -> Constants.GAME_MODE.BUTTONS_EASY
                    1 -> Constants.GAME_MODE.BUTTONS_NORMAL
                    2 -> Constants.GAME_MODE.BUTTONS_HARD
                    else -> Constants.GAME_MODE.TILT
                }

                sp.putString(Constants.SP_KEYS.GAME_MODE, selectedMode)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


}
