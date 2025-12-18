package com.example.dodge_obstacles_game

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.example.dodge_obstacles_game.utilities.Constants

class ScoreActivity : AppCompatActivity() {


    private lateinit var score_LBL_title: MaterialTextView

    private lateinit var score_BTN_newGame: MaterialButton
    private lateinit var score_BTN_back: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
    }

    private fun findViews() {
        score_LBL_title = findViewById(R.id.score_LBL_title)
        score_BTN_newGame = findViewById(R.id.score_BTN_newGame)
        score_BTN_back = findViewById(R.id.score_BTN_back)
    }

    private fun initViews() {
        val bundle: Bundle? = intent.extras

        val score = bundle?.getInt(Constants.BundleKeys.SCORE_KEY, 0)
        val timeMillis = bundle?.getString(Constants.BundleKeys.TIME_KEY, "00:00:00")

        score_LBL_title.text = buildString {
            append("Game Over!\nScore: ")
            append(score)
            append("\nTime: ")
            append(timeMillis)
        }
        score_BTN_newGame.setOnClickListener { view: View ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        score_BTN_back.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}