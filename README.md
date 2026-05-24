# pacman-java
# 🎮 Pac-Man Java Game

A classic Pac-Man game built with Java Swing. Eat pellets, avoid ghosts, and use power-ups to achieve the highest score!

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Swing](https://img.shields.io/badge/GUI-Swing-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 👨‍💻 Author

**Saif Yousuf**

---

## ✨ Features

- 🎮 Smooth Pac-Man and ghost animations
- 👤 Login system with local user accounts
- ⚡ Power-ups: Speed Boost, Invisibility, Ghost Eater
- 🏆 Per-user high score saving
- 🎨 Animated gradient background
- ✨ Particle effects when eating items
- 🔊 Sound effects (system beep)
- 💾 Persistent user database
- 🌀 Screen shake effect on death
- 📊 Level progression system

---

## 🎯 Game Controls

| Key | Action |
|-----|--------|
| ⬆️ Up Arrow | Move Up |
| ⬇️ Down Arrow | Move Down |
| ⬅️ Left Arrow | Move Left |
| ➡️ Right Arrow | Move Right |

---

## ⚡ Power-Ups

| Power-Up | Appearance | Effect | Duration |
|----------|------------|--------|----------|
| 🟡 Ghost Eater | Yellow pulsing circle | Eat ghosts for bonus points | ~9 seconds |
| 🔵 Speed Boost | Cyan square with 'S' | Pac-Man moves 50% faster | ~15 seconds |
| 🟣 Invisibility | Purple square with '?' | Ghosts cannot see you | ~12.5 seconds |

---

## 📊 Scoring System

| Item | Points |
|------|--------|
| Food pellet | 10 |
| Power pellet | 50 |
| Ghost (1st in combo) | 200 |
| Ghost (2nd in combo) | 400 |
| Ghost (3rd in combo) | 800 |
| Ghost (4th in combo) | 1600 |
| Level complete bonus | 1000 × level |

---

## 🎮 Ghost AI

Each ghost has unique behavior:

| Ghost | Color | Behavior |
|-------|-------|----------|
| Blinky | Red | Directly chases Pac-Man |
| Pinky | Pink | Ambushes 4 tiles ahead |
| Inky | Blue | Flanking behavior |
| Clyde | Orange | Alternates chase/scatter |

---

## 🔐 Login System

### Default Account
| Field | Value |
|-------|-------|
| Username | `player` |
| Password | `pacman` |

### Features
- Create new account with "Sign Up" button
- Passwords stored locally in `users.txt`
- High scores saved per user (`highscore_username.txt`)

---

## 🚀 How to Run

### Prerequisites
- Java 17 or higher installed
- Git (optional)

### Step 1: Clone or Download

**Using Git:**
```bash
git clone https://github.com/Saifyousuf/Pac-Man-game.git
cd pacman-java
# Pac-Man-game
---------------------------------------------------------------------------------
pacman-java/
│
├── App.java                 # Main entry point with login
├── PacMan.java              # Complete game logic
├── LoginDialog.java         # Login/Signup dialog
│
├── images/                  # Game assets
│   ├── wall.png
│   ├── blueGhost.png
│   ├── orangeGhost.png
│   ├── pinkGhost.png
│   ├── redGhost.png
│   ├── pacmanUp.png
│   ├── pacmanDown.png
│   ├── pacmanLeft.png
│   └── pacmanRight.png
│
├── users.txt                # User credentials (auto-generated)
├── highscore_*.txt          # Per-user high scores (auto-generated)
│
├── README.md                # This file
└── .gitignore               # Git ignore rules
-------------------------------------------------------------------------------------
MIT License

Copyright (c) 2024 Saif Yousuf

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
