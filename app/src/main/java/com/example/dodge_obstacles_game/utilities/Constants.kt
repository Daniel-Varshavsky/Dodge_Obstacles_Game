package com.example.dodge_obstacles_game.utilities

class Constants {

    object SP_KEYS {
        const val DATA_FILE = "GAME_PREFS"
        const val CONTROL_MODE = "CONTROL_MODE"
        const val DIFFICULTY = "DIFFICULTY"
        const val LEADERBOARD = "LEADERBOARD"
    }

    object CONTROL_MODES {
        const val BUTTONS = "BUTTONS"
        const val TILT = "TILT"
    }

    object DIFFICULTY {
        const val EASY = "EASY"
        const val NORMAL = "NORMAL"
        const val HARD = "HARD"
    }

    object GameConfig {
        const val ROWS = 9
        const val COLS = 5
        const val PLAYER_ROW = ROWS - 1
    }

    object Timer {
        const val DELAY: Long = 500L
    }

    object BundleKeys {
        const val SCORE_KEY: String = "SCORE_KEY"
        const val TIME_KEY: String = "TIME_KEY"

    }
}