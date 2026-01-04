package com.example.dodge_obstacles_game

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dodge_obstacles_game.ui.HighScoreFragment
import com.example.dodge_obstacles_game.ui.MapFragment
import com.example.dodge_obstacles_game.interfaces.Callback_HighScoreClicked

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var main_FRAME_list: FrameLayout
    private lateinit var main_FRAME_map: FrameLayout

    private lateinit var mapFragment: MapFragment
    private lateinit var highScoreFragment: HighScoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leaderboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
    }

    private fun findViews() {
        main_FRAME_list = findViewById(R.id.main_FRAME_list)
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
    }

    private fun initViews() {
        // Map Fragment
        mapFragment = MapFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_map, mapFragment)
            .commit()

        // HighScore Fragment
        highScoreFragment = HighScoreFragment()
        HighScoreFragment.highScoreItemClicked = object : Callback_HighScoreClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.zoom(lat, lon)
            }
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_list, highScoreFragment)
            .commit()
    }
}
