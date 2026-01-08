package com.example.dodge_obstacles_game.utilities

class Constants {

    object SP_KEYS {
        const val DATA_FILE = "GAME_PREFS"
        const val GAME_MODE = "GAME_MODE"
        const val LEADERBOARD_PREFIX = "LEADERBOARD_"
    }

    object GAME_MODE {
        const val BUTTONS_EASY = "BUTTONS_EASY"
        const val BUTTONS_NORMAL = "BUTTONS_NORMAL"
        const val BUTTONS_HARD = "BUTTONS_HARD"
        const val TILT = "TILT"
    }

    object GameConfig {
        const val ROWS = 9
        const val COLS = 5
        const val PLAYER_ROW = ROWS - 1
    }

    object Timer {
        const val DELAY: Long = 500L //default game speed
        const val MIN_DELAY = 150L // fastest game speed
        const val MAX_DELAY = 1_000L // slowest game speed
        const val DELAY_STEP = 100L // tilt speed step
    }

    object BundleKeys {
        const val SCORE_KEY: String = "SCORE_KEY"
        const val TIME_KEY: String = "TIME_KEY"

    }
}