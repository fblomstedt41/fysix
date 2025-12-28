# Gravitationsproblem - Analys

## Problem Identifierade

### 1. Hårdkodad Affect Radius ❌
**Problem:** Koden använder hårdkodad `len < 250` istället för `affectRadius` från GravityWell
```java
if(len>0 && len < 250){ // Affect area...
```

**Lösning:** Använd `affectRadius` från GravityWell objektet

### 2. Felaktig Vinkelberäkning ❌
**Problem:** Använder komplicerad `atan(A/B)` med många if-satser
```java
double A = Math.abs(foB.getPosition().y-foA.getPosition().y);
double B = Math.abs(foB.getPosition().x-foA.getPosition().x);
double a = Math.atan(A/B);
// ... många if-satser för att fixa kvadranten
```

**Lösning:** Använd `Math.atan2()` som hanterar kvadranter automatiskt

### 3. Felaktig Gravitationsriktning ❌
**Problem:** 
```java
Vector2d gravAcc = new Vector2d(gravForce*Math.cos(-a), -gravForce*Math.sin(a));
```
Använder `-a` i cos men `a` i sin, och negativ sin - detta ger fel riktning.

**Lösning:** Använd normaliserad riktningsvektor från dist

### 4. Endast Applicerar på Lågmass-objekt ❌
**Problem:** Gravitationen appliceras bara på `foA` (lågmass-objektet)
```java
if(fo1.getMass() <= fo2.getMass()){
    foA = fo1;  // Lågmass
    foB = fo2;  // Högmass
}
// ... gravitation appliceras bara på foA
```

**Lösning:** Applicera gravitation på båda objekt (Newtons tredje lag)

### 5. Ingen Skillnad på Planet vs Spelare ❌
**Problem:** Alla objekt behandlas lika, men planeterna borde vara statiska eller ha speciell hantering

**Lösning:** Borde markera planeterna som statiska (ingen velocity update) eller skippa dem i gravity loop

### 6. Beroende av Objekt i fWorld ❌
**Problem:** Gravititationen fungerar bara mellan objekt i fWorld, men planeterna läggs till där så det borde fungera... MEN spelaren läggs också till där så båda borde vara med.

## Lösning

1. Fixa vinkelberäkningen med atan2()
2. Använd normaliserad riktningsvektor
3. Öka affect radius eller gör den konfigurerbar
4. Fixa gravitationsriktningen
5. Möjligen markera planeterna som statiska

