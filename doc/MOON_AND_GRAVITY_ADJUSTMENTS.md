# Justeringar av Månens Hastighet och Gravitationens Räckvidd

## Problem
1. Månen roterade för fort runt planeten
2. Gravitationen påverkade skeppet redan vid ~20 skeppslängder (220 pixlar) - för långt

## Lösningar

### 1. Månens Rotation 10x Långsammare ✅
**Före:** Månen roterade med full omloppshastighet
**Efter:** Månens hastighet divideras med 10 för 10x långsammare rotation

**Kod:**
```java
double orbitalVelocity = Math.sqrt(G * planetMass / distance);
// Make moon rotate 10 times slower for better visual effect
orbitalVelocity = orbitalVelocity / 10.0;
```

**Resultat:** Månen roterar nu 10 gånger långsammare runt planeten, vilket ger en mer lugn och tydlig visuell effekt.

### 2. Gravitationens Räckvidd: 5-7 Skeppslängder ✅
**Före:** Gravitationen påverkade objekt upp till 2000-3000 pixlar bort (~180-270 skeppslängder!)
**Efter:** Gravitationen påverkar objekt inom 7 skeppslängder (77 pixlar)

**Kod:**
```java
// Gravity should affect objects within 5-7 ship lengths (55-77 pixels)
// Using 7 ship lengths as maximum range for reasonable gameplay feel
double SHIP_LENGTH = 11.0; // Ship length in pixels
double maxDistance = SHIP_LENGTH * 7.0; // 77 pixels = 7 ship lengths
```

**Resultat:** 
- Gravitationen börjar påverka när spelaren är ungefär 5-7 skeppslängder från planeten
- Mycket mer lokaliserad och spelvänlig känsla
- Gravitationen känns inte längre först vid alldeles för långt avstånd

## Beräkningar

**Skeppslängd:** 11 pixlar (från -4 till 7)

**5-7 Skeppslängder:**
- 5 skeppslängder = 55 pixlar
- 7 skeppslängder = 77 pixlar

**Max Räckvidd:** 77 pixlar (7 skeppslängder)

## Resultat

✅ **Månen roterar nu 10x långsammare** - mer lugn och visuellt tilltalande
✅ **Gravitationen påverkar vid 5-7 skeppslängder** - mycket mer lokaliserad och spelvänlig
✅ **Bättre gameplay-känsla** - gravitationen känns när det är relevant, inte för tidigt

