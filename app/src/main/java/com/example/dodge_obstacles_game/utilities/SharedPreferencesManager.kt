package com.example.dodge_obstacles_game.utilities

import android.content.Context
import kotlin.also
import com.example.dodge_obstacles_game.model.leaderboardEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        Constants.SP_KEYS.DATA_FILE,
        Context.MODE_PRIVATE
    )

    companion object {
        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun init(context: Context): SharedPreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context.applicationContext)
                    .also { instance = it }
            }
        }

        fun getInstance(): SharedPreferencesManager {
            return instance ?: throw IllegalStateException(
                "SharedPreferencesManager must be initialized by calling init(context) first"
            )
        }
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveLeaderboardEntry(
        gameMode: String,
        entry: leaderboardEntry
    ) {
        val gson = Gson()
        val type = object : TypeToken<MutableList<leaderboardEntry>>() {}.type

        val key = Constants.SP_KEYS.LEADERBOARD_PREFIX + gameMode
        val currentJson = getString(key, "[]")

        val list: MutableList<leaderboardEntry> =
            gson.fromJson(currentJson, type)

        list.add(entry)

        // Sort: score DESC, time DESC (endless runner)
        list.sortWith(
            compareByDescending<leaderboardEntry> { it.score ?: 0 }
                .thenByDescending { it.time ?: "" }
        )

        putString(key, gson.toJson(list.take(10)))
    }

    fun getLeaderboard(gameMode: String): List<leaderboardEntry> {
        val key = Constants.SP_KEYS.LEADERBOARD_PREFIX + gameMode
        val json = getString(key, "[]")
        return Gson().fromJson(json, Array<leaderboardEntry>::class.java).toList()
    }

}