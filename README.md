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
