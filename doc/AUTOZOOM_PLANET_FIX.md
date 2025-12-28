# Autozoom Fix för Planeter

## Problem
1. Autozoom för stora objekt (planeter) fungerade inte korrekt
2. När spelaren är 3-5 skeppslängder från planeten vill man att planeten, månen och skeppet ska synas
3. Denna zoom-nivå ska vara standard när nära planeten - inte zooma ut mer

## Lösningar

### 1. Förbättrad calculateWideZoom ✅
**Problem:** calculateWideZoom beräknade inte objektets storlek korrekt och hade fel logik

**Fixar:**
- Korrekt beräkning av objektets radius från bounding area
- Speciell hantering när spelaren är 3-5 skeppslängder från planeten
- När nära planeten: zoom så att planet, måne och skepp syns (med ~200 pixlar padding för månen)
- När långt från planeten: mer utzoomad vy

### 2. Prioritering av Wide Zoom när Nära Planeten ✅
**Logik:**
- Kontrollerar om spelaren är nära en planet (3-5 skeppslängder från planetens yta)
- Om nära planeten: använder wideZoom som standard (men tar fortfarande minimum med normalZoom för att respektera närhet till väggar)
- Om långt från planeten: använder normalZoom (baserat på väggar) men kan fortfarande använda wideZoom om det ger bättre vy

### 3. Beräkning av Avstånd till Planetens Yta ✅
**Före:** Använde avstånd till planetens centrum
**Efter:** Använder avstånd till planetens yta (centrum - radius)

Detta gör att när spelaren är 3-5 skeppslängder från planetens yta, aktiveras den special hanterade zoom-nivån.

## Resultat

✅ **När nära planeten (3-5 skeppslängder):** Planet, måne och skepp syns i bilden
✅ **Zoom-nivån är standard när nära planeten** - zoomar inte ut mer
✅ **Bättre beräkning av objektets storlek** - fungerar korrekt nu
✅ **Korrekt hantering av inre och yttre väggar** - respekterar fortfarande närhet till väggar

