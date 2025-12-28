# Prestanda & Rendering Analys - Fysix Game Engine

## 🔍 Översikt

Detta dokument analyserar prestanda, rendering, main loop och jämför med moderna game engine best practices för högpresterande spel (2025).

---

## ❌ KRITISKA PROBLEM

### 1. Main Loop - Fel Timing Strategy

**Nuvarande implementation:**
```java
while (running) {
    long currentTime = System.currentTimeMillis();
    long deltaTime = currentTime - lastFrameTime;
    lastFrameTime = currentTime;
    Thread.sleep(10);  // ❌ FEL!
    
    // ... update logic ...
    fe.Tick(env);
    // ... rendering ...
}
```

**Problem:**
1. **Thread.sleep(10)** - Försöker fixera till ~100 FPS, men detta är **fel sätt**
   - Thread.sleep() är inte precis (varierar med OS scheduling)
   - Delta time beräknas FÖRE sleep, inte efter → felaktig timing
   - Ingen frame rate limiting eller vsync integration
   - Spelar går i fast 100 FPS oavsett vad (eller lågare om sleep tar längre tid)

2. **Double deltaTime calculation** - DeltaTime beräknas två gånger:
   - I FysixMain (rad 138)
   - I FysixEngine.Tick() (rad 36) - använder sin egen lastTime
   - **Resulterar i inkonsekvent timing!**

3. **System.currentTimeMillis()** - Använder millisekunder istället för nanoseconds
   - 1ms precision → max 1000 FPS (teoretiskt, men oprecis)
   - Moderna spel använder `System.nanoTime()` för mikrosekundprecision

### 2. Rendering - Dubbel buffring OK, men ineffektiv

**Bra:**
- ✅ Använder BufferStrategy (2 buffers) - korrekt double buffering
- ✅ `setIgnoreRepaint(true)` - förhindrar OS-triggered repaints
- ✅ Disposes Graphics2D korrekt

**Problem:**
1. **Ingen vsync** - Ingen synkronisering med skärmens refresh rate
2. **Ineffektiv clearing** - `fillRect()` för hela skärmen varje frame
3. **Ingen dirty rectangle** - Ritar om allt varje frame
4. **Många Transform operationer** - Skapar ny AffineTransform för varje objekt
5. **Ingen batching** - Varje objekt ritas individuellt

### 3. Physics Engine - O(n²) Complexitet

**Nuvarande implementation:**
```java
// Gravity: O(n²)
for (int i = 0; i < fWorld.objects.size(); i++) {
    for (int j = i+1; j < fWorld.objects.size(); j++) {
        // Calculate gravity between all pairs
    }
}

// Collision: O(n²)
for (int i = 0; i < fWorld.objects.size(); i++) {
    for (int j = i+1; j < fWorld.objects.size(); j++) {
        FysixCollisionDetector.checkCollision(fo1, fo2);
    }
}
```

**Problem:**
- Med 100 objekt → 10,000 collision checks per frame
- Med 1000 objekt → 1,000,000 collision checks per frame
- **Exponentiell tillväxt!**

**Lösning:** Spatial partitioning (Quadtree, Spatial Hash, etc.)

### 4. Memory Allocations - För många temporära objekt

**Problem:**
```java
// I varje frame, för varje objekt:
Vector2d newVelocity = new Vector2d(0,0);  // ❌ Ny allocation
Vector2d move = (Vector2d) fo.getVelocity().clone(); // ❌ Ny allocation
move.scale(deltaTime / 1000.0);
```

**Effekt:**
- Skapar hundratals temporära Vector2d objekt per frame
- Triggar garbage collection ofta
- Förorsakar stuttering (GC pauses)

**Lösning:** Object pooling eller reusable objects

### 5. Delta Time Inconsistency

**FysixEngine.Tick()** använder sin egen timing:
```java
private long lastTime = System.currentTimeMillis();
public void Tick(Environment env) {
    long deltaTime = System.currentTimeMillis() - lastTime;  // ❌ Egen timing!
    lastTime += deltaTime;
}
```

**Problem:**
- FysixEngine har sin egen lastTime, men FysixMain också
- Om Tick() anropas flera gånger per frame → fel deltaTime
- Ingen separation mellan fixed timestep (physics) och variable timestep (rendering)

---

## ⚠️ MEDELSTORA PROBLEM

### 6. Input Polling - Ineffektiv

**Nuvarande:**
- Pollar input varje frame (OK för nuvarande setup)
- Men använder Hashtable (äldre, långsammare än HashMap)
- Ingen input buffering eller event queue

### 7. Rendering - För många draw calls

**Nuvarande:**
- Varje polygon, varje projektil, varje objekt = separat draw call
- Ingen batching av liknande objekt
- Varje objekt skapar ny AffineTransform

**Optimering:**
- Batch rendering av projektiler (samma färg/form)
- Reuse AffineTransform objekt
- Group rendering by state (color, etc.)

### 8. No Frame Rate Limiting

**Problem:**
- Ingen max FPS limit
- Ingen vsync option
- Kan köra så snabbt som möjligt → slösar CPU/GPU
- Varierande FPS → inkonsekvent gameplay

---

## ✅ VAD SOM ÄR BRA

1. **BufferStrategy** - Korrekt användning av double buffering
2. **Delta time concept** - Använder delta time (fast felaktigt implementerat)
3. **Separation of concerns** - Physics, rendering, game logic är separerade
4. **Enkel arkitektur** - Lätt att förstå och underhålla

---

## 🎯 MODERNA BEST PRACTICES (2025)

### Fixad Timestep Loop (Rekommenderad)

```java
// Fixad timestep för physics, variable för rendering
private static final double FIXED_TIMESTEP = 1.0 / 60.0; // 60 FPS physics
private static final long NANOS_PER_SECOND = 1_000_000_000L;

double accumulator = 0.0;
long previousTime = System.nanoTime();

while (running) {
    long currentTime = System.nanoTime();
    double frameTime = (currentTime - previousTime) / (double) NANOS_PER_SECOND;
    previousTime = currentTime;
    
    // Cap frame time to avoid spiral of death
    frameTime = Math.min(frameTime, 0.25); // Max 250ms
    
    accumulator += frameTime;
    
    // Fixed timestep physics
    while (accumulator >= FIXED_TIMESTEP) {
        updatePhysics(FIXED_TIMESTEP);
        accumulator -= FIXED_TIMESTEP;
    }
    
    // Variable timestep rendering (with interpolation)
    double alpha = accumulator / FIXED_TIMESTEP;
    render(alpha);
    
    // VSync eller frame limiting
    syncFrameRate();
}
```

### VSync Integration

```java
// I Renderer.EndRender():
public void EndRender() {
    g2d.dispose();
    
    if (!bufferStrategy.contentsLost()) {
        BufferCapabilities caps = bufferStrategy.getCapabilities();
        if (caps.isPageFlipping()) {
            bufferStrategy.show(); // Automatisk vsync med page flipping
        } else {
            Toolkit.getDefaultToolkit().sync(); // Force vsync
            bufferStrategy.show();
        }
    }
}
```

### Spatial Partitioning för Collision

```java
// Quadtree eller Spatial Hash
public class SpatialHash {
    private Map<Integer, List<FysixObject>> cells = new HashMap<>();
    private int cellSize = 100;
    
    public void insert(FysixObject obj) {
        int cellX = (int)(obj.getPosition().x / cellSize);
        int cellY = (int)(obj.getPosition().y / cellSize);
        int hash = cellX * 1000 + cellY;
        cells.computeIfAbsent(hash, k -> new ArrayList<>()).add(obj);
    }
    
    public List<FysixObject> queryNearby(Point2d pos, double radius) {
        // Returnera endast objekt i närliggande celler
        // Reducerar collision checks från O(n²) till O(n) i genomsnitt
    }
}
```

### Object Pooling

```java
public class Vector2dPool {
    private final Stack<Vector2d> pool = new Stack<>();
    
    public Vector2d obtain() {
        if (pool.isEmpty()) {
            return new Vector2d();
        }
        return pool.pop();
    }
    
    public void free(Vector2d v) {
        v.set(0, 0); // Reset
        pool.push(v);
    }
}

// Användning:
Vector2d vel = pool.obtain();
// ... använd vel ...
pool.free(vel); // Returnera till pool
```

---

## 📊 PRESTANDAJÄMFÖRELSE

### Nuvarande Implementation

| Aspekt | Status | FPS (teoretiskt) |
|--------|--------|------------------|
| Main Loop | ❌ Fel timing | ~100 (fast oprecis) |
| Rendering | ⚠️ Okej men ineffektiv | Varierande |
| Physics | ❌ O(n²) | Degraderar med objektantal |
| Memory | ❌ Många allocations | GC stuttering |
| VSync | ❌ Ingen | N/A |

### Modern Implementation (Förväntat)

| Aspekt | Status | FPS (förväntat) |
|--------|--------|-----------------|
| Main Loop | ✅ Fixad timestep | 60 FPS (stable) |
| Rendering | ✅ Optimerad | 60+ FPS |
| Physics | ✅ Spatial partitioning | 60 FPS (stable) |
| Memory | ✅ Object pooling | Minimal GC |
| VSync | ✅ Enabled | 60 FPS (synced) |

---

## 🔧 REKOMMENDERADE FIXAR

### Prioritet 1: Fixa Main Loop

1. **Ta bort Thread.sleep(10)**
2. **Använd System.nanoTime()**
3. **Implementera fixad timestep för physics**
4. **Implementera vsync eller frame limiting**

### Prioritet 2: Optimera Rendering

1. **Batch rendering** - Gruppera liknande draw calls
2. **Reuse AffineTransform** - Skapa en gång, återanvänd
3. **Dirty rectangles** - Rita endast ändrade områden (optional för retro-spel)
4. **VSync integration**

### Prioritet 3: Optimera Physics

1. **Spatial partitioning** - Quadtree eller Spatial Hash
2. **Broad phase / Narrow phase** collision detection
3. **Fixed timestep physics**

### Prioritet 4: Memory Optimering

1. **Object pooling** för Vector2d, temporära objekt
2. **Minimize allocations** i hot paths
3. **Pre-allocate collections** där möjligt

---

## 💡 SPECIFIKA KODFIXAR

### Fix 1: Main Loop

```java
// FÖRE:
Thread.sleep(10);
long deltaTime = currentTime - lastFrameTime;

// EFTER:
long deltaTimeNanos = System.nanoTime() - lastFrameTimeNanos;
lastFrameTimeNanos = System.nanoTime();
double deltaTime = deltaTimeNanos / 1_000_000_000.0; // Sekunder

// Frame limiting (optional):
if (deltaTime < TARGET_FRAME_TIME) {
    long sleepTime = (long)((TARGET_FRAME_TIME - deltaTime) * 1_000_000_000);
    Thread.sleep(sleepTime / 1_000_000, (int)(sleepTime % 1_000_000));
}
```

### Fix 2: FysixEngine Timing

```java
// FÖRE:
private long lastTime = System.currentTimeMillis();
public void Tick(Environment env) {
    long deltaTime = System.currentTimeMillis() - lastTime;
    lastTime += deltaTime;
}

// EFTER:
// Ta bort egen timing, använd parameter:
public void Tick(Environment env, double deltaTimeSeconds) {
    // Använd deltaTimeSeconds direkt
}
```

### Fix 3: Object Pooling Example

```java
// I FysixEngine:
private final Vector2dPool vectorPool = new Vector2dPool();

public void Tick(Environment env, double deltaTime) {
    for (FysixObject fo : fWorld.objects) {
        Vector2d newVel = vectorPool.obtain(); // Reuse
        // ... calculations ...
        fo.setVelocity(newVel);
        // Note: Don't free here, object owns it now
    }
}
```

---

## 🎮 JÄMFÖRELSE MED MODERNA ENGINES

### Unity/Unreal Engine Pattern

1. **Fixed Update Loop** för physics (50-60 Hz)
2. **Variable Update Loop** för rendering
3. **Job System** för parallellisering
4. **Spatial partitioning** (Octree, BSP, etc.)
5. **Object pooling** överallt
6. **Command buffer** för rendering
7. **VSync** per default

### LibGDX Pattern (Java Game Framework)

1. **ApplicationListener** med `render(deltaTime)`
2. **SpriteBatch** för batching
3. **Box2D** för physics (spatial partitioning inbyggt)
4. **Object pooling** utilities
5. **VSync** stöd

### Fysix Nuvarande

- ❌ Ingen fixad timestep
- ❌ Ingen spatial partitioning
- ❌ Ingen object pooling
- ❌ Ingen batching
- ⚠️ Double buffering (bra!)
- ❌ Ingen vsync

---

## 📈 PRESTANDAESTIMAT

### Nuvarande Setup

**Med få objekt (< 50):**
- ~100 FPS (på grund av Thread.sleep(10))
- Men oprecis, varierande

**Med många objekt (100+):**
- FPS sjunker på grund av O(n²) collision
- GC stuttering på grund av många allocations
- ~30-60 FPS (beroende på objektantal)

### Efter Optimeringar

**Med spatial partitioning:**
- Stable 60 FPS även med 1000+ objekt
- Minimal GC pga object pooling
- Jämn performance

---

## 🎯 SLUTSATS

### Är detta en bra game engine?

**Kort svar:** **Nej, inte för högpresterande spel.**

**För detaljer:**
- ✅ Arkitektur är OK (separerade system)
- ✅ Double buffering är korrekt
- ❌ Main loop timing är felaktig
- ❌ Prestanda kommer att degraderas med objektantal
- ❌ Ingen optimering för stora scenarion

### Är det som man gör i nutida main-loops?

**Kort svar:** **Nej, det här är 2000-tals stil.**

**Moderna engines (2025):**
- Använder fixad timestep för physics
- Variable timestep för rendering
- System.nanoTime() för precision
- VSync integration
- Spatial partitioning
- Object pooling
- Parallellisering (job systems)

**Fysix använder:**
- Thread.sleep() för frame limiting ❌
- System.currentTimeMillis() ❌
- O(n²) collision ❌
- Många allocations ❌

---

## ✅ REKOMMENDATIONER

### För Retro-Style Spel (Fysix)

**Prioritet 1 - Måste fixas:**
1. ✅ Fixa main loop timing (ta bort Thread.sleep, använd nanoTime)
2. ✅ Fixa deltaTime inconsistency (en källa för timing)
3. ✅ Implementera vsync eller frame limiting

**Prioritet 2 - Bör fixas:**
4. ⚠️ Spatial partitioning (om många objekt)
5. ⚠️ Object pooling för Vector2d (om performance problem)

**Prioritet 3 - Nice to have:**
6. Batch rendering optimeringar
7. Parallellisering (för stora scenarion)

### För Moderna Högpresterande Spel

Behöver **komplett rewrite** med:
- Modern rendering pipeline (Vulkan/Metal/DirectX12)
- Job system för parallellisering
- ECS (Entity Component System) arkitektur
- Moderna optimeringar

**Men:** För ett retro pixel-shooter spel är detta OK efter prioritet 1 fixar!

---

*Analysdatum: 2025-12-27*

