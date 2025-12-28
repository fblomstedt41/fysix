# Autozoom Debugging

## Problem
Autozoom fungerar inte - inget händer när spelaren närmar sig väggar.

## Analys

### Identifierat Problem
I `calculateNormalZoom()` hoppade koden över boundary walls:
```java
if ("boundary".equals(wall.getCollisionType())) continue;
```

Boundary walls är ofta de närmaste väggarna till spelaren (de omger hela banan). Om boundary walls hoppas över så hittar koden aldrig några väggar när spelaren är nära banans kanter, vilket betyder att:
- `minDistance` förblir `Double.MAX_VALUE`
- Koden returnerar default `MIN_ZOOM_FACTOR * 2.0 = 0.6` (utzoomad)
- Zoom ändras aldrig när spelaren närmar sig väggar

### Lösning
**FIXAT:** Boundary walls inkluderas nu i zoom-beräkningen. De är viktiga för att veta när spelaren är nära en vägg. Vi behöver inte hoppa över dem i zoom-beräkningen - de är lika relevanta som andra väggar för att bestämma när spelaren är nära en vägg.

### Ändring
Tog bort raden som hoppade över boundary walls i `calculateNormalZoom()`:
```java
// Borttaget: if ("boundary".equals(wall.getCollisionType())) continue;
```

Nu beräknas avståndet till alla väggar, inklusive boundary walls, vilket gör att autozoom fungerar korrekt när spelaren närmar sig väggar.

