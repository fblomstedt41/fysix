# Kodanalys - Fysix

**Analysdatum:** 2025  
**Projektversion:** 1.0-SNAPSHOT  
**Java Version:** 17

---

## 📋 Översikt

Fysix är en retro-stil pixel-shooter med fysik-baserad gameplay, inspirerad av klassiska spel som Asteroids (1979) och Gravitar (1982). Projektet är byggt i Java med AWT för rendering och använder en egenutvecklad fysikmotor.

---

## 🏗️ Arkitektur och Design

### Styrkor ✅

1. **Modulär struktur**
   - Tydlig separation mellan engine, game logic, UI, och rendering
   - Bra paketstruktur (`engine/`, `game/`, `level/`, `ui/`, `effects/`)
   - Singleton pattern för `FysixEngine` (även om dependency injection vore bättre)

2. **Fysikmotor**
   - Egenutvecklad 2D fysikmotor med gravitation, kollision, och rörelse
   - Spatial Hash för optimerad kollisionsdetektion (O(n²) → O(n))
   - Stöd för olika miljötyper med resistans och acceleration

3. **Game System**
   - Komplett game state management (MENU, PLAYING, PAUSED, GAME_OVER, VICTORY)
   - Health/Damage system med invulnerability frames
   - Weapon/Shield system
   - Score tracking med kills, deaths, accuracy

4. **Level System**
   - JSON-baserade levels med LevelLoader/LevelSaver
   - Stöd för walls, spawn points, gravity wells, environment zones
   - Flexibel och utbyggbar struktur

5. **Visual Effects**
   - Partikelsystem för explosioner, engine trails, sparks
   - Screen shake effects
   - Auto-zoom kamera som följer spelaren

### Problemområden ⚠️

1. **FysixMain.java - För stor huvudklass**
   - **Problem:** 639 rader kod i en klass
   - **Konsekvens:** Svår att underhålla, testa och förstå
   - **Rekommendation:** Refaktorera till flera klasser:
     - `GameController` - Hanterar game loop och state transitions
     - `PlayerController` - Hanterar spelarinput och movement
     - `CameraController` - Hanterar kamera och zoom
     - `MenuController` - Hanterar menu logic

2. **InputManager - Förlegad implementation**
   - **Problem:** Använder `Hashtable` (deprecated för ny kod) och raw types
   - **Kod:**
     ```java
     private static Hashtable keyToMap = new Hashtable(); // Raw type!
     ```
   - **Rekommendation:** 
     ```java
     private static Map<Integer, String> keyToMap = new HashMap<>();
     private static Map<String, Boolean> mapStatus = new HashMap<>();
     ```

3. **FysixObject - Metodnamn inconsistency**
   - **Problem:** Fel metodnamn i getter/setter
     ```java
     public FysixObject setParent() {  // Borde vara getParent()
         return parent;
     }
     public void getParent(FysixObject Parent) {  // Borde vara setParent()
         this.parent = Parent;
     }
     ```

4. **Memory Management**
   - **Problem:** Projektiler och partiklar kan ackumuleras om de inte tas bort korrekt
   - **Status:** Delvis åtgärdat med object pooling (`ObjectPool`, `Vector2dPool`)
   - **Rekommendation:** Fortsätt använda pooling för fler objekt-typer

5. **Error Handling**
   - **Problem:** Många `try-catch` block som bara loggar fel utan proper recovery
   - **Exempel:** I `FysixMain.java` - level loading faller tillbaka till default, men ger minimal feedback

6. **Code Duplication**
   - Menu navigation logic finns dubblerad för main menu och settings menu
   - Render loop har repetitiv transform-kod för olika objekt-typer

---

## 🔍 Teknisk Detaljanalys

### Dependencies

**Bra val:**
- ✅ **JBox2D** (2.2.1.1) - Fysikbibliotek (används dock inte aktivt, egen motor används)
- ✅ **Gson** (2.10.1) - JSON serialization för levels
- ✅ **SLF4J/Logback** - Logging framework
- ✅ **Java VecMath** (1.5.2) - 2D/3D matematik

**Observationer:**
- ⚠️ JBox2D är en dependency men används inte - kan tas bort eller användas istället för egen motor
- ⚠️ VecMath är gammal (1.5.2) - överväg modernare alternativ eller egen Vector2D klass

### Performance

**Optimeringar som finns:**
- ✅ Spatial Hash för kollisionsdetektion
- ✅ Frame limiter (60 FPS)
- ✅ Object pooling för Vector2d
- ✅ Delta time-baserad uppdatering

**Potentiella flaskhalsar:**
- ⚠️ Gravitationsberäkningar är O(n²) - varje objekt kollar alla andra objekt
  - Kan optimeras med spatial hash även för gravitation
- ⚠️ Rendering loop bygger nya AffineTransform objekt varje frame
  - Kan cacha transforms eller återanvända

### Kollisionsdetektion

**Implementation:**
- Polygon-baserad SAT (Separating Axis Theorem) i `FysixCollisionDetector`
- Spatial Hash för att minska antalet collision checks
- Fallback till O(n²) brute force för få objekt

**Problem:**
- ⚠️ Kollisioner markerar bara objekt som röda (debug), ingen faktisk collision response utöver wall collisions
- ⚠️ TODO-kommentar i `FysixEngine.java` indikerar ofullständig elastic collision implementation

### Renderer

**Funktioner:**
- Fullscreen support med display mode selection
- Buffer strategy för smooth rendering
- TV-effekt (AnalogTV) - retro CRT-känsla
- VSync support

**Problem:**
- ⚠️ TV-effekt är kommenterad bort i rendering (`if (false/*useTv*/)`)
- ⚠️ TODO-kommentarer för test rendering
- ⚠️ Hardcoded display modes kan vara problematiskt på olika skärmar

---

## 🐛 Kända Buggar och Problem

### Identifierade från kodgenomgång:

1. **FysixObject.getParent/setParent metodnamn**
   - Metodnamnen är omvända - getParent() returnerar men heter setParent()

2. **Wall collision wrapping logic**
   - Komplex boundary-checking kod i FysixMain kan ha edge cases
   - Lines 494-505: Wrapping logic verkar ha potentiella bugs

3. **Gravity calculation range**
   - Hardcoded värden (100000, 2500.0, etc.) gör det svårt att balansera
   - Borde vara konfigurerbara parametrar

4. **Input debouncing**
   - Menu input använder manuell debouncing med boolean flags
   - Kan förbättras med time-based debouncing eller input queue

---

## 📊 Code Quality Metrics

### Positiva Aspekter:
- ✅ Bra klassnamngivning (tydliga, beskrivande namn)
- ✅ Kommentarer finns på svenska och engelska (blandat)
- ✅ JavaDoc finns delvis (bristfällig i många klasser)
- ✅ Modulär struktur
- ✅ Separation of concerns (till stor del)

### Förbättringsområden:
- ⚠️ **Code Coverage:** Ingen synlig testkod
- ⚠️ **Documentation:** JavaDoc saknas i många klasser
- ⚠️ **Consistency:** Blandning av svenska/engelska i kommentarer och variabelnamn
- ⚠️ **Magic Numbers:** Många hardcoded värden (120, 100000, 2500.0, etc.)
- ⚠️ **Error Handling:** Minimal error handling, många exceptions ignoreras

---

## 🎯 Rekommendationer

### Prioritet 1 - Högsta (Kritiska för underhållbarhet)

1. **Refaktorera FysixMain**
   - Dela upp i mindre, testbara komponenter
   - Flytta game loop logic till `GameController`
   - Separera input handling från rendering

2. **Fixa InputManager**
   - Byt ut `Hashtable` mot `HashMap`
   - Lägg till generics för type safety
   - Överväg att göra det icke-statisk (dependency injection)

3. **Fixa FysixObject metodnamn**
   - Byt namn på `setParent()` → `getParent()`
   - Byt namn på `getParent(FysixObject)` → `setParent(FysixObject)`

### Prioritet 2 - Medium (Viktiga för kvalitet)

4. **Lägg till Unit Tests**
   - Börja med kärnsystem (PhysicsEngine, CollisionDetector)
   - Använd JUnit 5 (redan i dependencies)

5. **Förbättra Error Handling**
   - Proper exception handling med recovery strategies
   - Logging för debugging
   - Användare-vänliga felmeddelanden

6. **Extraktera Magic Numbers**
   - Skapa `GameConstants` eller `Config` klass
   - Använd för gravitation, speeds, sizes, etc.

### Prioritet 3 - Låg (Nice to have)

7. **Modernisera till Java 17 Features**
   - Använd Records för data classes (Level, Point, etc.)
   - Pattern matching där applicerbart
   - Switch expressions

8. **Förbättra Documentation**
   - JavaDoc för alla public metoder
   - README med architecture overview
   - Design decisions dokumentation

9. **Performance Profiling**
   - Profilera game loop för att identifiera flaskhalsar
   - Optimeringsmöjligheter i rendering
   - Memory leak detection

---

## 📈 Arkitekturförslag

### Föreslagen ny struktur:

```
net.force2dev.fysix/
├── core/
│   ├── game/
│   │   ├── GameController.java      # Game loop
│   │   ├── GameManager.java         # Game state management
│   │   └── PlayerController.java    # Player input & control
│   ├── physics/
│   │   ├── PhysicsEngine.java       # Renamed from FysixEngine
│   │   ├── CollisionDetector.java   # Renamed from FysixCollisionDetector
│   │   └── SpatialHash.java         # (existing)
│   └── events/
│       └── EventBus.java            # Event-driven architecture
├── graphics/
│   ├── Renderer.java                # (existing)
│   ├── Camera.java                  # Extract from FysixMain
│   └── effects/
│       └── EffectManager.java       # (existing)
├── input/
│   └── InputManager.java            # Modernized
├── level/
│   └── [existing structure]
└── ui/
    └── [existing structure]
```

### Design Patterns att överväga:

1. **Command Pattern** - För input handling
2. **Observer Pattern** - För events (spelare dör, poäng uppdateras, etc.)
3. **Strategy Pattern** - För olika game modes
4. **Factory Pattern** - För att skapa projektiler, effekter, etc.
5. **Component System** - För game objects (ECS-lite approach)

---

## 🔐 Säkerhet och Best Practices

### Säkerhetsaspekter:
- ✅ Inga kända säkerhetsproblem (single-player game)
- ⚠️ JSON parsing med Gson - säkerhetsproblem vid loading av externa levels
- ⚠️ Inga input validations för level data

### Best Practices:
- ⚠️ Använd dependency injection istället för Singletons där möjligt
- ⚠️ Immutable objects där det är möjligt (Records i Java 17)
- ⚠️ Fail-fast principle - validera input tidigt
- ⚠️ Resource management - stäng streams, dispose graphics objects

---

## 📝 Sammanfattning

### Styrkor:
1. Tydlig modulär struktur
2. Komplett game system med health, weapons, shields
3. Flexibelt level system med JSON support
4. Bra visual effects och partikelsystem
5. Performance-optimeringar med spatial hash

### Huvudsakliga Problem:
1. FysixMain är för stor och behöver refaktorering
2. Förlegad kod (Hashtable, raw types)
3. Bristande test coverage
4. Magic numbers och hardcoded värden
5. Fel metodnamn i FysixObject

### Övergripande Betyg:
**B+** - Bra struktur och funktionalitet, men behöver refaktorering för underhållbarhet.

---

## 🚀 Nästa Steg

1. Refaktorera FysixMain (högsta prioritet)
2. Modernisera InputManager
3. Lägg till grundläggande unit tests
4. Fixa metodnamn buggar
5. Extraktera magic numbers till konstanter

---

*Analys utförd genom kodgenomgång, läsning av dokumentation, och strukturell analys av kodbasen.*

