# Pikachu Run - Dodge Obstacles Game

A Pokemon-themed endless runner game for Android where you control Pikachu to dodge falling Pokeballs while collecting healing potions. Features multiple difficulty modes, tilt controls, location-based leaderboards, authentic sound effects, and animated capture sequences.

## 🎮 Game Overview

In Pikachu Run, you control Pikachu on a 9x5 grid as various Pokeballs (Pokeball, Great Ball, Ultra Ball, Master Ball) fall from the top of the screen. Your goal is to avoid being captured while collecting healing potions to restore lives. When captured, you'll experience an animated capture sequence with Pokeball shaking effects, just like in the Pokemon games!

## ✨ Features

### 🎯 Core Gameplay
- **Grid-based Movement**: Navigate Pikachu on a 5-column, 9-row playing field
- **Falling Obstacles**: Dodge 4 types of animated Pokeballs with unique movement patterns
- **Healing Items**: Collect potions to restore lost lives
- **Capture Sequences**: Experience authentic Pokemon-style capture animations when caught
- **Audio Feedback**: Immersive sound effects for shaking, captures, and releases
- **Lives System**: Start with 3 lives, lose one per capture, gain lives from potions

### 🎮 Control Options
- **Button Controls**: 
  - Easy Mode (slower speed)
  - Normal Mode (default speed) 
  - Hard Mode (faster speed)
- **Tilt Controls**: Use device accelerometer for movement and speed control
  - Tilt left/right to move Pikachu
  - Tilt forward/backward to adjust game speed

### 📊 Progression & Scoring
- **Scoring System**: Earn 10 points per game tick
- **Time Tracking**: Precise game duration recording
- **Difficulty Scaling**: Game speed increases over time in tilt mode

### 🏆 Leaderboard System
- **Multiple Categories**: Separate leaderboards for each game mode
- **Location Integration**: GPS coordinates saved with scores
- **Top 10 Rankings**: Best scores preserved per difficulty
- **Interactive Map**: Click leaderboard entries to view score locations

### 🎨 Audio-Visual Features
- **Animated Sprites**: Walking Pikachu animations
- **Pokeball Animations**: Unique movement and capture sequences for each ball type
- **Sound Effects**: Authentic Pokemon-style audio feedback
  - Pokeball shaking sounds during capture sequences
  - Successful capture confirmation sound
  - Pokemon release/escape sound
- **Professional UI**: Material Design components throughout
- **Themed Layouts**: Pokemon-inspired color schemes and backgrounds

## 🛠️ Technical Architecture

### Core Components
- **MainActivity**: Main game loop and rendering engine
- **GameManager**: Core game logic and state management  
- **TiltDetector**: Accelerometer-based motion controls
- **SingleSoundPlayer**: Threaded audio system for Pokemon-style sound effects
- **LeaderboardActivity**: Score tracking with map integration
- **Fragment-based UI**: Modular leaderboard and map components

### Data Management
- **SharedPreferences**: Local data persistence
- **Location Services**: GPS integration for score tracking
- **JSON Serialization**: Efficient leaderboard data storage

### Animation System
- **Frame-based Animation**: Smooth character and object animations
- **State Management**: Complex capture sequence orchestration
- **Coroutine-based**: Non-blocking animation execution

## 📱 Requirements

- **Android Version**: API 24+ (Android 7.0)
- **Permissions**: 
  - Location access (for leaderboard features)
  - Vibration (for haptic feedback)
- **Hardware**: 
  - Accelerometer (for tilt controls)
  - GPS (for location features)

## 🚀 Installation

1. Clone or download the project
2. Open in Android Studio
3. Ensure Google Play Services is configured for Maps API
4. Add your Google Maps API key to the project
5. Build and install on your Android device

## 🎯 Game Modes

### Button Controls
- **Easy**: Slower game speed for beginners
- **Normal**: Standard gameplay experience  
- **Hard**: Fast-paced challenge mode

### Tilt Controls
- **Dynamic Speed**: Tilt device forward/backward to control game speed
- **Motion Control**: Tilt left/right for character movement
- **Immersive Experience**: Full motion-based gameplay

## 🏅 Leaderboard Features

### Score Categories
Each game mode maintains its own leaderboard:
- Button Easy
- Button Normal  
- Button Hard
- Tilt Mode

### Location Integration
- Scores automatically tagged with GPS coordinates
- Interactive map showing score locations
- Click any leaderboard entry to view its location on the map

### Ranking System
- Sorted by score (descending)
- Time used as tiebreaker (descending for endless runner)
- Top 10 scores preserved per category

## 🎮 Gameplay Tips

1. **Movement Strategy**: Plan moves ahead - Pokeballs follow predictable paths
2. **Potion Priority**: Always collect potions when safe to do so
3. **Capture Survival**: When captured, the sequence length varies - some are shorter!
4. **Mode Selection**: Try tilt mode for dynamic speed control
5. **Score Optimization**: Survival time directly impacts final score

## 🎨 Assets & Theming

### Visual Design
- Custom Pokemon-themed sprites
- Authentic Pokeball designs and animations
- Material Design UI components
- Color-coded difficulty indicators

### Audio Design
- Pokemon-style capture sequence sounds
- Pokeball shaking audio effects
- Success and failure audio cues
- Non-blocking threaded audio playback

### Animation Details
- 4-frame Pikachu walking cycle
- 8-frame Pokeball movement animations  
- 13-frame capture sequence animations
- Smooth transitions between game states
- Synchronized audio-visual feedback

### Technical Implementation

### Architecture Patterns
- **MVVM Components**: Clean separation of concerns
- **Fragment Architecture**: Modular UI components
- **Coroutine Management**: Efficient asynchronous operations
- **Singleton Patterns**: Centralized managers for shared resources
- **Threaded Audio System**: Non-blocking sound effect playback

### Performance Optimizations
- **Object Pooling**: Efficient sprite management
- **Frame Rate Control**: Consistent 60fps gameplay
- **Memory Management**: Proper lifecycle handling
- **Battery Optimization**: Smart sensor management
- **Audio Threading**: Background sound processing for smooth gameplay

## 🔧 Configuration

### Game Balance
- Pokeball spawn frequency: Every 1-2 turns
- Potion spawn chance: 15%
- Lives system: 3 maximum lives
- Capture loops: 0-3 random (3 when game over)

### Timing Controls
- Default speed: 500ms per turn
- Easy mode: 1000ms per turn  
- Hard mode: 150ms per turn
- Tilt speed range: 150ms - 1000ms

## 📄 License & Disclaimer

This is an educational project and fan tribute to the Pokemon franchise. Not affiliated with Nintendo, Game Freak, or The Pokemon Company. All Pokemon-related assets are used under fair use for educational purposes.

---

*Gotta catch 'em all... or in this case, gotta dodge 'em all!* ⚡🎮
