# Fysix - Förbättringsplan 2025
## Modernisering med retro-känsla (sent 80-tal/tidigt 90-tal pixel-shooter)

---

## 🎯 VISION
Behålla den autentiska pixel-shooter känslan från klassiska spel som:
- Asteroids (1979)
- Gravitar (1982)
- Thrust (1986)
- xpilot (1990)
- Terminal Velocity (1995)

Men med moderna tekniker för utveckling, performance och användbarhet.

---

## 🔧 MODERNA KODFÖRBÄTTRINGAR (2025)

### 1. Java Version & Moderna Features
**Nuvarande:** Java 8 (2014)
**Föreslaget:** Java 17 LTS eller Java 21 LTS

**Fördelar:**
- Records för data-klasser (immutable data)
- Pattern matching
- Sealed classes för typ-säkerhet
- Text blocks för multi-line strings
- Bättre performance (GC, optimeringar)
- Moderna API:er

**Exempel - Record för Level data:**
```java
public record LevelData(
    String name,
    int width,
    int height,
    List<Polygon> walls,
    List<SpawnPoint> spawnPoints,
    List<GravityWell> gravityWells,
    EnvironmentSettings environment
) {}

public record SpawnPoint(double x, double y, double angle) {}
```

### 2. Dependency Injection & Modern Arkitektur
**Föreslaget:** 
- Lightweight DI (Google Guice eller Manifold)
- Separera Game State från rendering
- Event-driven architecture (redan strukturerat, utnyttja det!)

**Struktur:**
```
com.force2dev.fysix
├── core/
│   ├── game/
│   │   ├── GameState.java
│   │   ├── GameMode.java (Deathmatch, Race, CTF)
│   │   └── PlayerManager.java
│   ├── physics/
│   │   ├── PhysicsEngine.java (förbättrad)
│   │   └── CollisionResolver.java
│   └── events/
│       └── GameEventBus.java
├── graphics/
│   ├── RetroRenderer.java (behåller retro-stil)
│   ├── PixelArtSprite.java
│   └── ShaderEffects.java (optional, för CRT-filter)
├── level/
│   ├── Level.java
│   ├── LevelLoader.java
│   ├── editor/
│   │   ├── LevelEditor.java
│   │   └── LevelEditorUI.java
│   └── serialization/
│       └── LevelJSONSerializer.java
└── audio/
    └── RetroSoundManager.java
```

### 3. Konfiguration & Data Format
**Nuvarande:** Hårdkodat i FysixMain
**Föreslaget:** JSON/YAML för konfiguration

**Nya filformat:**
- `levels/*.json` - Level definitioner
- `config/game.json` - Spelinställningar
- `config/ships.json` - Fartygsdefinitioner
- `config/weapons.json` - Vapendefinitioner

**Exempel level.json:**
```json
{
  "name": "Asteroid Field",
  "width": 5400,
  "height": 5400,
  "background": "space",
  "gravityWells": [
    {
      "x": 2600,
      "y": 2600,
      "mass": 1600000,
      "radius": 100
    }
  ],
  "walls": [
    {
      "type": "polygon",
      "points": [[0, 0], [5400, 0], [5400, 5400], [0, 5400]],
      "collisionType": "boundary"
    }
  ],
  "spawnPoints": [
    {"x": 200, "y": 200, "angle": 0, "team": 1},
    {"x": 5200, "y": 5200, "angle": 3.14, "team": 2}
  ],
  "environment": {
    "friction": 0.995,
    "resistance": 1.0
  }
}
```

### 4. Resource Management
**Föreslaget:**
- Asset loading system
- Texture atlas för sprites
- Sound pool för ljud
- Proper cleanup och memory management

### 5. Testing & Code Quality
**Föreslaget:**
- JUnit 5 (istället för 4)
- Mockito för mocking
- Integrationstester för physics engine
- Performance profiling tools

---

## 🎮 LEVEL EDITOR SYSTEM

### Krav
1. **Visual Editor** - Klicka och drag för att skapa banor
2. **Spara/Ladda** - JSON format (mänskligt läsbart)
3. **Testning** - Spela banan direkt från editorn
4. **Versionshantering** - Enkelt att dela banor

### Implementation

#### Level Editor Features:

**1. Polygon Editor**
- Klicka för att lägga till hörn
- Dra för att flytta hörn
- Ta bort hörn med högerklick
- Preview av collision boxes
- Snapping till grid (valfritt)

**2. Spawn Points**
- Placera spawn-punkter visuellt
- Konfigurera vinkel och team
- Preview av spawn-område

**3. Gravity Wells**
- Placera planeter/asteroider
- Justera massa och radie
- Visualisera gravitationsfält

**4. Environment Zones**
- Definiera områden med olika friction
- Olika gravitation i olika zoner
- Visualisera zoner med färger

**5. Level Properties**
- Namn och beskrivning
- Dimensioner
- Bakgrundstextur/stil
- Speltyp (Deathmatch, Race, CTF)

#### Teknisk Implementation

**LevelEditor.java** - Huvudklass
```java
public class LevelEditor {
    private Level currentLevel;
    private EditorMode mode; // WALL, SPAWN, GRAVITY, etc.
    private List<EditorTool> tools;
    
    public void saveLevel(String filename);
    public void loadLevel(String filename);
    public void testLevel(); // Startar spelet med banan
}
```

**EditorUI.java** - Swing/JavaFX UI
- Toolbar med verktyg
- Properties panel
- Canvas för redigering
- Layer system (walls, spawns, gravity, etc.)

**Level Serialization**
```java
public class LevelSerializer {
    public Level fromJSON(String json);
    public String toJSON(Level level);
    public void export(Level level, Path file);
    public Level import(Path file);
}
```

#### File Format (JSON)
Se exempel ovan under "Konfiguration & Data Format"

---

## 🎨 RETRO-GRAFIK FÖRBÄTTRINGAR

### Behåll retro-känslan, förbättra tekniken:

**1. Pixel-Perfect Rendering**
- Integer-baserad koordinatsystem (inga sub-pixel)
- Nearest-neighbor scaling (inga smooth scaling)
- Crisp lines (1-2px bredd)
- Begränsad färgpalett (64-256 färger)

**2. CRT Filter (Optional)**
- Scanlines
- Curvature effect
- Vignetting
- Color bleed
- **Kan aktiveras/avaktiveras** - låt spelare välja

**3. Sprite System**
- Sprite sheets för animationer
- Rotation och scaling av sprites
- Partikelsystem (redan delvis implementerat!)
- Explosion-effekter

**4. Palette Cycling**
- Klassisk retro-effekt
- Animera bakgrunder med palette shifts
- Energi-visning med cycling

**5. Pixel Art Sprite Editor** (Bonus)
- Inbyggd sprite editor
- 8x8, 16x16, 32x32 sprites
- Export/import sprites

### Färgpalett Förslag
```
Retro Space Palette:
- Mörkblå: #1a1a2e (bakgrund)
- Mörkgrå: #16213e
- Cyan: #0f3460
- Ljusblå: #533483
- Orange: #e94560 (explosioner, skott)
- Vit: #ffffff (skott, highlights)
- Gul: #ffd700 (energi, poäng)
- Grön: #00ff00 (spelare)
- Röd: #ff0000 (fiender, skada)
```

---

## 🔊 LJUDFÖRBÄTTRINGAR

### Retro Sound Design
**Nuvarande:** Begränsat ljudstöd
**Föreslaget:** 

1. **Chip-tune / 8-bit style ljud**
   - Blipp/blopp för skott
   - Synth-baserade explosioner
   - Melodier för menyer

2. **Ljudbibliotek**
   - SFX Library (thrust, shoot, explosion, pickup, etc.)
   - Background music (optional, kan stängas av)
   - Sound pooling för performance

3. **Ljudverktyg**
   - Integration med retro sound tools (ChipTone, Bfxr)
   - Generate procedurally

---

## 🎯 GAMEPLAY-FÖRBÄTTRINGAR (Behåller retro-känslan)

### 1. Ship Types (Retro-stil)
Implementera de planerade fartygstyperna med retro-utseende:

**Scout (Lätt)**
- Liten, snabb, svår att träffa
- Svaga vapen, liten sköld
- Bra för snabb gameplay

**Fighter (Balanserad)**
- Medium storlek, snabb
- Bra vapen, medium sköld
- Standard choice

**Defender (Tung)**
- Stor, långsam, robust
- Tung beväpning, stark sköld
- Tank-stil

**Bomber (Särskilt)**
- Medium storlek
- Explosiva vapen, bra sköld
- Area damage

### 2. Weapon Types (Retro-stil)
**Basic Cannon**
- Snabb, direkt skada
- Klassisk "pew pew"

**Spread Shot**
- Flera projektiler
- Retro arcade-känsla

**Homing Missile**
- Följer mål (enkelt AI)
- Visuell spår-effekt

**Laser (Rail Gun)**
- Instant hit
- Energi-visning

### 3. Power-ups (Klassisk retro)
- Speed boost (tillfällig)
- Shield recharge
- Weapon upgrade (tillfällig)
- Multi-shot
- Invincibility (kort tid)

### 4. Game Modes (Retro-arcade style)
**Deathmatch**
- Kill count eller time limit
- Respawn på spawn points
- Scoreboard

**Survival**
- Vågor av fiender
- Ökande svårighet
- High score tracking

**Race**
- Checkpoints
- Time trial
- Ghost ships (tidigare försök)

---

## 🔧 TEKNISKA FÖRBÄTTRINGAR

### 1. Performance Optimeringar
- **Spatial partitioning** för kollisionsdetektion (quadtree)
- **Object pooling** för projektiler (redan delvis behövs!)
- **Dirty rectangles** för rendering (optional)
- **Delta time fixes** (redan finns, förbättra)

### 2. Memory Management
- Proper cleanup av projektiler
- Resource unloading
- Garbage collection optimering

### 3. Debug Tools
- Physics visualization
- Collision box overlay
- Performance metrics (FPS, object count)
- Debug console (optional)

### 4. Build System
**Nuvarante:** Maven (bra!)
**Förbättringar:**
- Gradle wrapper (optional, Maven är också bra)
- GitHub Actions för CI/CD
- Automated testing
- Release builds med obfuskering (optional)

---

## 📦 MODERNA DEPENDENCIES

### Förslag på nya dependencies:

```xml
<dependencies>
    <!-- JSON Processing (Modern) -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    
    <!-- Logging (Istället för System.out.println) -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.14</version>
    </dependency>
    
    <!-- Optional: UI Framework för Level Editor -->
    <!-- JavaFX (bundlat med Java 17+) eller Swing -->
    
    <!-- Optional: Retro Sound Library -->
    <!-- javax.sound.sampled är nog tillräckligt -->
</dependencies>
```

---

## 🚀 IMPLEMENTERINGSPLAN (Prioriterad)

### Fas 1: Grundläggande Modernisering (2-4 veckor)
1. ✅ Uppgradera till Java 17+
2. ✅ Refaktorera FysixMain (dela upp i klasser)
3. ✅ Implementera Level serialization (JSON)
4. ✅ Skapa Level class och LevelLoader
5. ✅ Basic level editor (text-baserad först)

### Fas 2: Level Editor (3-5 veckor)
1. ✅ Visual level editor UI
2. ✅ Polygon editor
3. ✅ Spawn point editor
4. ✅ Save/Load funktionalitet
5. ✅ Test-level från editor

### Fas 3: Core Gameplay (4-6 veckor)
1. ✅ Health/Damage system
2. ✅ Respawn system
3. ✅ Score system
4. ✅ Basic game states (menu, game, game over)
5. ✅ Projectile collision med skada

### Fas 4: Retro Polish (2-3 veckor)
1. ✅ Sprite system
2. ✅ Ljudförbättringar
3. ✅ Partikel-effekter
4. ✅ CRT filter (optional)
5. ✅ Färgpalett och pixel-art stil

### Fas 5: Game Modes & Content (3-4 veckor)
1. ✅ Deathmatch mode
2. ✅ Ship types
3. ✅ Weapon types
4. ✅ Power-ups
5. ✅ AI/Bots (grundläggande)

### Fas 6: Polish & Release (2-3 veckor)
1. ✅ Balancing
2. ✅ Menyer och UI
3. ✅ Dokumentation
4. ✅ Release build

**Totalt: ~16-25 veckor** (beroende på omfattning)

---

## 🎨 VISUAL STYLE GUIDE

### Design Principles
1. **Pixel Perfect** - Ingen anti-aliasing på sprites
2. **Limited Palette** - 64-256 färger
3. **High Contrast** - Tydliga konturer
4. **Clear Readability** - Tydliga spelobjekt
5. **Retro Aesthetic** - Men inte "fake retro"

### Typografi
- Monospace font för text (Courier, Monaco, eller pixel font)
- Tydlig, läsbar
- Stora siffror för score

### Animation
- Sprite-based animation
- 8-12 frames för explosioner
- 2-4 frames för idle animation
- Snabb, responsive rörelse

---

## 📝 KODSTIL & BEST PRACTICES

### Modern Java Conventions (2025)
- **Immutable objects** där möjligt (Records, final fields)
- **Optional** för nullable values
- **Streams API** för collection operations
- **Lambda expressions** för callbacks
- **Try-with-resources** för resource management
- **Package-private** för intern API

### Retro-Game Specific
- **Integer coordinates** (inga floats för position)
- **Fixed-point math** för physics (optional, float är också OK)
- **Deterministic** physics (för multiplayer/replay)

---

## 🎯 SPECIFIKA FÖRSLAG FÖR LEVEL EDITOR

### Minimal Viable Editor (Start här!)

**Version 1: Text-baserad**
- Redigera JSON direkt
- Validering
- Preview i spelet

**Version 2: Enkel GUI**
- Swing eller JavaFX
- Klicka för att lägga till polygoner
- Basic properties panel
- Save/Load

**Version 3: Full Editor**
- Drag & drop
- Multi-layer support
- Snap to grid
- Copy/paste
- Undo/redo

### Editor UI Mockup (Text-baserad)
```
┌─────────────────────────────────────┐
│ Fysix Level Editor                  │
├─────────────────────────────────────┤
│ [New] [Open] [Save] [Test] [Export]│
├─────────────────────────────────────┤
│                                     │
│  Canvas Area (5400x5400)            │
│  - Click to add polygon point       │
│  - Right-click to remove            │
│  - Drag to move                     │
│                                     │
├─────────────────────────────────────┤
│ Tools:                              │
│ [Wall] [Spawn] [Gravity] [Zone]    │
│                                     │
│ Properties:                         │
│ Name: [Asteroid Field        ]      │
│ Width: [5400] Height: [5400]        │
│                                     │
└─────────────────────────────────────┘
```

---

## 🔄 MIGRATION PLAN

### Steg-för-steg migration från nuvarande kod:

1. **Behåll fungerande kod** - Migrera gradvis
2. **Skapa nya klasser** - Börja med Level system
3. **Refaktorera FysixMain** - Flytta logik till separata klasser
4. **Testa kontinuerligt** - Se till att allt fungerar efter varje steg
5. **Dokumentera** - Kommentera ändringar

### Backwards Compatibility
- Behåll gamla hårdkodade banan tills Level system fungerar
- Gradvis migration
- Feature flags för nya features

---

## 🎮 FINAL THOUGHTS

### Var försiktig med:
- ❌ För mycket modernisering som förstör retro-känslan
- ❌ Over-engineering (KISS principle!)
- ❌ För komplexa system (behåll det enkelt)

### Fokusera på:
- ✅ Retro-känsla och gameplay
- ✅ Enkel, läsbar kod
- ✅ Funktionalitet över perfektion
- ✅ Nöjdhet och kul att spela

### Inspiration:
- Kolla på moderna retro-spel som:
  - **FTL: Faster Than Light** (retro-stil, moderna tekniker)
  - **Celeste** (pixel-art, smooth gameplay)
  - **Enter the Gungeon** (retro-shooter, modern polish)

---

*Uppdaterad: 2025*
*Version: 1.0*

