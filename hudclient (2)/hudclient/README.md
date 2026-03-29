# HudClient — Fabric 1.21.4

A clean, fully-featured Fabric HUD mod. No hacks, no unfair advantages — just the information 
Minecraft already exposes, displayed beautifully.

---

## Features

| Module | Description | Default Keybind |
|---|---|---|
| **FPS Counter** | Color-coded (green >100, yellow >60, red <60) | — |
| **Coordinates** | XYZ + Block position, updates every tick | — |
| **Direction** | Cardinal direction + exact yaw degrees | — |
| **Speed** | Horizontal movement speed in m/s | — |
| **Biome** | Current biome name | — |
| **Game Time** | In-game clock (HH:MM) + day/night | — |
| **Entity Counter** | Total / mobs / players / items near you | — |
| **CPS Counter** | Left and right clicks per second | — |
| **Keystrokes** | W A S D + Space + LMB/RMB display | — |
| **Waypoints** | Named world positions, sorted by distance | **B** = add at feet |
| **Config Screen** | In-game toggle for every module | **F8** |
| **HUD Toggle** | Show/hide entire HUD instantly | **F4** |

---

## Requirements

- **Minecraft**: 1.21.4 (Java Edition)
- **Fabric Loader**: 0.16.9 or later  
  → https://fabricmc.net/use/installer/
- **Fabric API**: 0.119.3+1.21.4 or later  
  → https://modrinth.com/mod/fabric-api
- **Java**: 21 or later

---

## Building from Source

### 1. Prerequisites
- JDK 21+ installed  
  → https://adoptium.net/
- Git (optional)

### 2. Clone / download this project
```bash
git clone <your-repo-url>
cd hudclient
```

### 3. Build
```bash
# On Linux/macOS:
chmod +x gradlew
./gradlew build

# On Windows:
gradlew.bat build
```

The compiled `.jar` will be at:
```
build/libs/hudclient-1.0.0.jar
```

### 4. Install
1. Copy `hudclient-1.0.0.jar` into your `.minecraft/mods/` folder  
2. Make sure `fabric-api-*.jar` is also in `mods/`  
3. Launch Minecraft with the Fabric profile

---

## In-Game Usage

### Keybinds (all rebindable in Options → Controls)
| Key | Action |
|-----|--------|
| `F4` | Toggle entire HUD on/off |
| `B` | Add a waypoint at your current position |
| `F8` | Open the configuration screen |

### Config Screen (F8)
Every module can be toggled on/off individually:
- FPS Display, Coordinates, Direction, Biome, Game Time
- Speed, Entity Counter, Keystrokes, Waypoints, CPS Counter
- Backgrounds, Text Shadows

### Waypoints
- Press **B** to drop a waypoint at your feet (auto-named "WP 1", "WP 2", etc.)
- Waypoints are dimension-aware (Overworld / Nether / End)
- Saved to `.minecraft/config/hudclient_waypoints.json`
- Up to 5 closest shown on HUD at once (configurable in JSON)
- Remove individual waypoints from the F8 config screen

### Config File
Located at `.minecraft/config/hudclient.json`  
Edit it directly to change colors, positions, and other advanced settings.

---

## HUD Layout (default)

```
Top-left:                        Top-right: (waypoints)
  FPS: 144                         ■ Spawn [0,64,0] 280m
  XYZ: -248.3 / 64.0 / 1047.8     ■ Diamond Mine [...] 14m
  Block: -249 / 64 / 1047
  Facing: South (+Z)  (180.0°)
  Speed: 4.32 m/s
  Biome: forest
  Time: 12:00 (Day)
  CPS: L:8  R:2

Middle-left (entity panel):
  Entities: 47
  Mobs: 12
  Players: 3
  Items: 8

Bottom-left (keystrokes):
  [ ] [W] [ ]
  [A] [S] [D]
  [   SPACE   ]
  [LMB] [RMB]
```

---

## Project Structure

```
hudclient/
├── src/main/java/com/hudclient/
│   ├── HudClientMod.java          ← Mod entrypoint, keybinds
│   ├── config/
│   │   ├── HudConfig.java         ← All settings + JSON save/load
│   │   └── HudConfigScreen.java   ← In-game config UI (F8)
│   ├── hud/
│   │   ├── HudRenderer.java       ← Main render loop
│   │   └── KeystrokeRenderer.java ← WASD + mouse display
│   ├── mixin/
│   │   └── MouseMixin.java        ← CPS click tracking
│   └── waypoints/
│       ├── Waypoint.java          ← Waypoint data
│       └── WaypointManager.java   ← Load/save/add/remove
├── src/main/resources/
│   ├── fabric.mod.json
│   ├── hudclient.mixins.json
│   └── assets/hudclient/lang/en_us.json
├── build.gradle
├── gradle.properties              ← Change MC/loader versions here
└── settings.gradle
```

---

## Customizing Colors

In `hudclient.json`, colors are stored as ARGB hex integers:
- `0xFFFFFFFF` = opaque white
- `0xFF55FF55` = green (good FPS)
- `0xFFFF5555` = red (low FPS)
- `0x88000000` = semi-transparent black (background)

To change the FPS color to cyan: `"colorFps": -16711681` (= `0xFF00FFFF`)

---

## License
MIT — use it, fork it, improve it.

---

## First-Time Build Setup (Important)

The `gradle/wrapper/gradle-wrapper.jar` is not included in this zip for size reasons.
Run this one command first to download it automatically:

```bash
# On Linux/macOS — downloads the wrapper jar and builds
gradle wrapper --gradle-version 8.8
./gradlew build

# OR: If you already have Gradle 8.x installed globally:
gradle build
```

**On Windows:**
```cmd
gradle wrapper --gradle-version 8.8
gradlew.bat build
```

If you don't have Gradle installed, download it from https://gradle.org/releases/ 
and add it to your PATH, then run the commands above.
