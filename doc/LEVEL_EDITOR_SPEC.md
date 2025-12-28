# Level Editor - Specifikation och Design

## Översikt
Ett visuellt verktyg för att skapa, redigera och testa banor för Fysix.
Behåller retro-känslan men med moderna utvecklingsverktyg.

---

## Funktionalitet

### Core Features
1. **Visual Editing**
   - Klicka och dra för att skapa polygoner (väggar)
   - Placera spawn-punkter visuellt
   - Placera gravity wells (planeter/asteroider)
   - Definiera environment zones (friction, gravity)

2. **Save/Load**
   - JSON-format (läsbart, versionerat)
   - Export/Import funktion
   - Automatisk backup

3. **Testing**
   - Testa banan direkt från editorn
   - Snabb iteration
   - Preview mode

4. **Level Management**
   - Lista över banor
   - Duplicera banor
   - Ta bort banor
   - Rename banor

### Advanced Features (Fas 2)
- Multi-layer editing
- Snap to grid
- Copy/paste objekt
- Undo/redo
- Minimap
- Zoom in/out
- Grid overlay

---

## Data Structure

### Level.java
```java
package net.force2dev.fysix.level;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private String name;
    private String description;
    private int width;
    private int height;
    private List<Wall> walls;
    private List<SpawnPoint> spawnPoints;
    private List<GravityWell> gravityWells;
    private List<EnvironmentZone> zones;
    private EnvironmentSettings defaultEnvironment;
    private String backgroundType; // "space", "asteroid", etc.
    
    // Constructors, getters, setters...
}
```

### Wall.java
```java
package net.force2dev.fysix.level;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

public class Wall {
    private String id;
    private List<Point> points;
    private String collisionType; // "solid", "boundary", "damage"
    private int damageOnCollision; // 0 = no damage
    
    public Polygon toPolygon() {
        // Convert to AWT Polygon
    }
}
```

### SpawnPoint.java
```java
package net.force2dev.fysix.level;

public class SpawnPoint {
    private double x;
    private double y;
    private double angle; // radians
    private int team; // 0 = neutral, 1, 2, etc.
    private String spawnType; // "player", "enemy", "neutral"
}
```

### GravityWell.java
```java
package net.force2dev.fysix.level;

public class GravityWell {
    private double x;
    private double y;
    private double mass;
    private double radius; // Visual radius
    private double affectRadius; // Gravity affect area
}
```

### EnvironmentZone.java
```java
package net.force2dev.fysix.level;

import java.awt.Polygon;

public class EnvironmentZone {
    private Polygon area;
    private double friction; // 0.0 - 1.0
    private double gravityX;
    private double gravityY;
    private double resistance; // Air resistance
}
```

### EnvironmentSettings.java
```java
package net.force2dev.fysix.level;

public class EnvironmentSettings {
    private double friction;
    private double resistance;
    private double gravityX;
    private double gravityY;
}
```

---

## JSON Format

### Example level.json
```json
{
  "version": "1.0",
  "level": {
    "name": "Asteroid Field Alpha",
    "description": "A classic space battle arena",
    "width": 5400,
    "height": 5400,
    "backgroundType": "space",
    "defaultEnvironment": {
      "friction": 0.995,
      "resistance": 1.0,
      "gravityX": 0.0,
      "gravityY": 0.0
    },
    "walls": [
      {
        "id": "boundary_1",
        "points": [
          {"x": 0, "y": 0},
          {"x": 5400, "y": 0},
          {"x": 5400, "y": 5400},
          {"x": 0, "y": 5400}
        ],
        "collisionType": "boundary",
        "damageOnCollision": 0
      },
      {
        "id": "wall_center",
        "points": [
          {"x": 1200, "y": 1200},
          {"x": 1800, "y": 1200},
          {"x": 2400, "y": 900},
          {"x": 3000, "y": 900},
          {"x": 3600, "y": 1200},
          {"x": 4200, "y": 1200},
          {"x": 4200, "y": 1800},
          {"x": 4500, "y": 2400},
          {"x": 4500, "y": 3000},
          {"x": 4200, "y": 3600},
          {"x": 4200, "y": 4200},
          {"x": 3600, "y": 4200},
          {"x": 3000, "y": 4500},
          {"x": 2400, "y": 4500},
          {"x": 1800, "y": 4200},
          {"x": 1200, "y": 4200},
          {"x": 1200, "y": 3600},
          {"x": 900, "y": 3000},
          {"x": 900, "y": 2400},
          {"x": 1200, "y": 1800}
        ],
        "collisionType": "solid",
        "damageOnCollision": 0
      }
    ],
    "spawnPoints": [
      {
        "x": 200,
        "y": 200,
        "angle": 0.0,
        "team": 1,
        "spawnType": "player"
      },
      {
        "x": 5200,
        "y": 5200,
        "angle": 3.14159,
        "team": 2,
        "spawnType": "player"
      }
    ],
    "gravityWells": [
      {
        "x": 2600,
        "y": 2600,
        "mass": 1600000,
        "radius": 100,
        "affectRadius": 500
      },
      {
        "x": 2780,
        "y": 2550,
        "mass": 50,
        "radius": 10,
        "affectRadius": 100
      }
    ],
    "zones": [
      {
        "points": [
          {"x": 1000, "y": 1000},
          {"x": 2000, "y": 1000},
          {"x": 2000, "y": 2000},
          {"x": 1000, "y": 2000}
        ],
        "friction": 0.95,
        "gravityX": 0.0,
        "gravityY": -50.0,
        "resistance": 0.8
      }
    ]
  }
}
```

---

## Editor UI Design

### Main Window Layout
```
┌─────────────────────────────────────────────────────────────┐
│ Fysix Level Editor                              [X]         │
├──────────────┬──────────────────────────────────────────────┤
│              │                                              │
│   TOOLBAR    │          CANVAS AREA                         │
│              │          (Editable Level View)               │
│  [Wall]      │                                              │
│  [Spawn]     │         ┌──────────────────┐                │
│  [Gravity]   │         │                  │                │
│  [Zone]      │         │   Level Preview  │                │
│  [Select]    │         │                  │                │
│              │         │                  │                │
│              │         └──────────────────┘                │
│              │                                              │
│   LAYERS     │                                              │
│              │                                              │
│  ☑ Walls     │                                              │
│  ☑ Spawns    │                                              │
│  ☑ Gravity   │                                              │
│  ☐ Zones     │                                              │
│              │                                              │
├──────────────┴──────────────────────────────────────────────┤
│ PROPERTIES                                                   │
│ Name: [Asteroid Field Alpha        ]                        │
│ Width: [5400]  Height: [5400]                              │
│ Background: [space ▼]                                      │
│                                                              │
│ Selected: Wall "wall_center"                                │
│ Points: 20                                                   │
│ Collision: [solid ▼]                                        │
└─────────────────────────────────────────────────────────────┘
```

### Menu Bar
```
File    Edit    View    Tools    Help
├─ New Level
├─ Open Level...
├─ Save Level
├─ Save As...
├─ ──────────
├─ Export...
├─ Import...
├─ ──────────
└─ Exit
```

---

## Implementation Plan

### Phase 1: Core Data Structures (Week 1)
- [ ] Create Level, Wall, SpawnPoint, GravityWell classes
- [ ] Create JSON serialization/deserialization
- [ ] Unit tests for serialization
- [ ] Load existing hardcoded level to JSON

### Phase 2: Basic Editor (Week 2-3)
- [ ] Simple Swing/JavaFX window
- [ ] Canvas for drawing level
- [ ] Click to add polygon points
- [ ] Basic save/load functionality
- [ ] Load level in game

### Phase 3: Advanced Editing (Week 4)
- [ ] Drag to move points
- [ ] Right-click to delete
- [ ] Add spawn points
- [ ] Add gravity wells
- [ ] Properties panel

### Phase 4: Polish (Week 5)
- [ ] Grid overlay
- [ ] Zoom in/out
- [ ] Minimap
- [ ] Test level from editor
- [ ] Better UI/UX

---

## Code Structure

```
src/main/java/net/force2dev/fysix/
├── level/
│   ├── Level.java
│   ├── Wall.java
│   ├── SpawnPoint.java
│   ├── GravityWell.java
│   ├── EnvironmentZone.java
│   ├── EnvironmentSettings.java
│   ├── LevelLoader.java          // Load from JSON
│   ├── LevelSaver.java           // Save to JSON
│   └── editor/
│       ├── LevelEditor.java      // Main editor class
│       ├── EditorCanvas.java     // Drawing canvas
│       ├── EditorTool.java       // Tool interface
│       ├── WallTool.java
│       ├── SpawnTool.java
│       ├── GravityTool.java
│       ├── SelectTool.java
│       └── EditorUI.java         // Main UI window
└── ...
```

---

## Technical Considerations

### Rendering in Editor
- Use same rendering code as game (code reuse!)
- Or simplified version for editor
- Visual feedback (hover, selection)

### Coordinate System
- World coordinates (same as game)
- Screen-to-world conversion
- Camera/Viewport for navigation

### Performance
- Only render visible objects
- Efficient polygon drawing
- Responsive UI (separate thread for heavy ops)

### Validation
- Validate level data on save
- Check for invalid polygons (self-intersecting, etc.)
- Warn about missing spawn points
- Check boundaries

---

## User Workflow

### Creating a New Level
1. File → New Level
2. Set dimensions (width, height)
3. Add boundary walls (or auto-generate)
4. Add interior walls/polygons
5. Add spawn points
6. Add gravity wells (optional)
7. Add environment zones (optional)
8. Test level
9. Save level

### Editing Existing Level
1. File → Open Level
2. Select tool (Wall, Spawn, etc.)
3. Click to add/modify
4. Edit properties in panel
5. Test changes
6. Save

### Testing Level
1. Tools → Test Level (or Ctrl+T)
2. Game starts with current level
3. Test gameplay
4. Return to editor to adjust
5. Iterate

---

## Future Enhancements

- **Template System** - Start from templates
- **Random Generation** - Procedural level generation
- **Level Pack** - Bundle multiple levels
- **Online Sharing** - Share levels online
- **Version Control** - Built-in versioning
- **Collaboration** - Multiple people edit (advanced)

---

*Detaljerad specifikation för Level Editor implementation*

