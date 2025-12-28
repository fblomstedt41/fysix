# Fixar för Ljud och Partikeleffekter

## Problem
1. Inga ljud hörs
2. Inga partiklar syns från motorn (engine trail)
3. Inga explosioner när skott träffar väggar

## Lösningar

### 1. Explosioner vid Väggträffar ✅
**Före:** Endast hit sparks skapades när projektiler träffade väggar
**Efter:** Orange explosion skapas när projektiler träffar väggar

**Kod:**
```java
if (hitPlayer) {
    // Hit on player - create sparks
    effectManager.createHitSparks(x, y);
    PlaySound.hit();
} else {
    // Hit on wall - create explosion
    effectManager.createExplosion(x, y, Color.ORANGE);
    PlaySound.hit();
}
```

### 2. Förbättrade Engine Trail Partiklar ✅
**Problem:** Partiklar var för små, för korta och skapades för sällan

**Fixar:**
- Skapar nu **4 partiklar per frame** istället för 1 (mycket mer synliga)
- Partikelstorlek: **2-5 pixlar** (tidigare 1-3 pixlar)
- Livstid: **0.3-0.7 sekunder** (tidigare 0.2-0.5 sekunder)
- Färg: **Ljusare blå/cyan** med högre opacitet (200 istället för 150)

**Resultat:** Engine trail är nu mycket mer synlig och ger bättre visuell feedback när spelaren använder thrust.

### 3. Ljudsystem (Grundläggande) ✅
**Före:** PlaySound var bara stub-metoder med TODO-kommentarer

**Efter:** 
- Lägger till grundläggande audio feedback med `Toolkit.beep()` för explosioner och respawn
- Kommentarer förklarar att riktigt ljudsystem kräver ett audio-bibliotek
- Metoderna fungerar men använder system-beep som placeholder

**Notering:** För riktiga ljudeffekter behöver ett audio-bibliotek integreras (t.ex. javax.sound.sampled eller ett tredjepartsbibliotek).

## Resultat

✅ **Explosioner vid väggträffar** - Orange explosioner syns när projektiler träffar väggar
✅ **Explosioner vid spelardöd** - Stor multi-färgad explosion (red/orange/yellow) som redan fanns
✅ **Synliga engine trail partiklar** - Blå/cyan partiklar syns tydligt bakom skeppet när thrust används
✅ **Grundläggande ljudfeedback** - System-beep för vissa händelser (kan förbättras med audio-bibliotek)

