package com.example.dodge_obstacles_game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class StartActivity : AppCompatActivity() {

    private lateinit var start_BTN_play: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        start_BTN_play = findViewById(R.id.start_BTN_play)

        start_BTN_play.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
