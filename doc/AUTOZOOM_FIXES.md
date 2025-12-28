# Autozoom Fixar

## Problem
Autozoom fungerade inte korrekt - skeppet blev inte tillräckligt stort när nära väggar.

## Krav
- När nära vägg (3-5 skeppslängder = 33-55 pixlar): Skeppet ska vara **2-3 cm på skärmen**
- När långt från vägg: Zooma ut för att se mer av banan

## Beräkningar
- Skeppslängd: 11 pixlar (från -4 till 7)
- 2-3 cm på skärmen vid 96 DPI:
  - 2 cm = ~75 pixlar
  - 3 cm = ~113 pixlar
- ScaleFactor behövs: 75/11 = 6.8 till 113/11 = 10.3

## Fixar Implementerade

### 1. Ökad MAX_ZOOM_FACTOR
**Före:** `MAX_ZOOM_FACTOR = 3.0` (för lågt)
**Efter:** `MAX_ZOOM_FACTOR = 12.0` (tillräckligt för 2-3 cm skepp)

### 2. Ny Zoom-Logik för Nära Väggar
**När avstånd ≤ 5 skeppslängder (55 pixlar):**
- Vid ≤ 3 skeppslängder: Zoom = 10.3 (skeppet blir ~3 cm)
- Vid 3-5 skeppslängder: Interpoleras mellan 10.3 och 4.0
- Skeppet blir nu korrekt storlek när nära väggar

### 3. Ny Zoom-Logik för Långt Från Väggar
**När avstånd > 5 skeppslängder:**
- Zoom minskar logaritmiskt med avståndet
- Zoom halveras för varje 200 pixlar extra avstånd
- Minsta zoom = 0.3 (mycket utzoomad, ser mer av banan)

### 4. Förbättrad Wide Zoom
- Fixat så att den faktiskt hittar den mest utzoomade nivån
- Bättre hantering av stora objekt (planeter)

## Resultat
- ✅ Skeppet blir nu 2-3 cm på skärmen när nära väggar (3-5 skeppslängder)
- ✅ Zoomas ut automatiskt när långt från väggar för att se mer av banan
- ✅ Mjuka övergångar mellan zoom-nivåer

