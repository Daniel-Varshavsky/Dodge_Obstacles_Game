# Pikachu Run ⚡

A fun and simple Android game where you control Pikachu to dodge incoming Pokéballs!

## Features

- **Classic Gameplay:** Simple grid-based movement to dodge obstacles falling from the top.
- **Dynamic Animation:** Pikachu features a walking animation during gameplay.
- **Multiple Obstacles:** Various types of Pokéballs (Pokéball, Great Ball, Ultra Ball, Master Ball) will challenge you.
- **Lives System:** You start with 3 lives. Colliding with a Pokéball will cost you a life and trigger a vibration.
- **Score & Timer:** Keep track of your score and how long you've survived.
- **Game States:** Includes a Start screen, Game screen, and a Score screen to review your performance.

## How to Play

1. **Start the Game:** Open the app and tap "Start" to begin.
2. **Move Pikachu:** Use the **Left (◀)** and **Right (▶)** buttons at the bottom of the screen to switch lanes.
3. **Dodge:** Avoid the falling Pokéballs.
4. **Game Over:** The game ends when you lose all 3 lives. Try to get the highest score possible!

## Tech Stack

- **Language:** Kotlin
- **Architecture:** Grid-based logic with a `GameManager` for state handling.
- **UI:** XML Layouts with Material Design components.
- **Concurrency:** Kotlin Coroutines for the game loop and timer.
- **Haptics:** Android Vibrator API for collision feedback.

## Project Structure

- `com.example.dodge_obstacles_game.MainActivity`: The main game screen and UI logic.
- `com.example.dodge_obstacles_game.logic.GameManager`: Manages game state, movement, and collision detection.
- `com.example.dodge_obstacles_game.model.Enemy`: Data class representing the falling obstacles.
- `com.example.dodge_obstacles_game.utilities`: Helper classes for formatting time and managing constants.

## Installation

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Build and run the app on an emulator or a physical Android device.

---
*Gotta dodge 'em all!*
