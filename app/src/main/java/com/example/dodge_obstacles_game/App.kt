package com.example.dodge_obstacles_game

import android.app.Application
import com.example.dodge_obstacles_game.utilities.SharedPreferencesManager
import com.example.dodge_obstacles_game.utilities.SignalManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
        SignalManager.init(this)
    }
}