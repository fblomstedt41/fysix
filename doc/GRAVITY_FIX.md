# Gravitationsfixar - Spelversion

## Problem Identifierade
1. ❌ Gravititationen applicerades på velocity men skrevs över
2. ❌ Gravitationskonstanten var för liten (0.0001) - realistisk men inte bra för spel
3. ❌ Månens omloppshastighet var för låg (0.925 pixlar/sek) - för liten för att se
4. ❌ Gravititationen applicerades bara mellan tunga objekt, inte på alla objekt

## Lösningar Implementerade

### 1. Gravititationen Appliceras Korrekt ✅
**Före:** Gravititationen applicerades på velocity direkt, men skrevs sedan över
**Efter:** Gravititationen läggs till i total acceleration i velocity update-loopen

### 2. Starkare Gravitation för Spel ✅
**Före:** G = 0.0001 (realistisk men för svag)
**Efter:** G = 50.0 (500,000x starkare för bra spelkänsla!)

### 3. Ökad Omloppshastighet för Månen ✅
**Före:** v = sqrt(0.0001 * 1600000 / 186.8) ≈ 0.925 pixlar/sek (för lite)
**Efter:** v = sqrt(50.0 * 1600000 / 186.8) ≈ 654 pixlar/sek (tydligt synlig!)

### 4. Gravititationen Påverkar Alla Objekt ✅
**Före:** Gravititationen applicerades bara mellan objekt med massa >= 1.0
**Efter:** Alla objekt påverkas av gravitationen från tunga objekt (massa >= 100.0)

## Formler

### Gravititationsacceleration
```
a = G * M / r²
```
där:
- G = 50.0 (spel-justerad konstant)
- M = planetens massa
- r = avståndet till planeten

### Omloppshastighet (cirkulär bana)
```
v = sqrt(G * M / r)
```

## Resultat

✅ **Spelaren dras nu in mot planeten** när de flyger nära
✅ **Månen roterar synbart** runt planeten
✅ **Gravitationen är kännbar** men inte överväldigande
✅ **Alla objekt påverkas** av gravitationen från planeter

## Notering

Gravitationskonstanten G = 50.0 är **inte realistisk** men är anpassad för bra spelkänsla. Den ger en kännbar gravitationseffekt utan att vara för stark eller svag.

