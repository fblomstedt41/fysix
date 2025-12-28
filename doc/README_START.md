# Hur man startar Fysix

## Kompilera och bygga

```bash
mvn clean package
```

Detta skapar:
- `target/fysix-1.0-SNAPSHOT.jar` - Standard JAR (behöver classpath för dependencies)
- `target/fysix-1.0-SNAPSHOT-jar-with-dependencies.jar` - Komplett JAR med alla dependencies

## Kör spelet

### Med jar-with-dependencies (rekommenderat):

```bash
java -jar target/fysix-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Eller direkt med Maven:

```bash
mvn exec:java -Dexec.mainClass="net.force2dev.fysix.FysixMain"
```

## Kontroller

- **Pil Nedåt** - Thrust (gasa)
- **Vänster Pil** - Rotera vänster
- **Höger Pil** - Rotera höger
- **Pil Upp** - Skjut
- **F1** - Zoom in
- **F2** - Zoom ut
- **F3** - Toggle TV-effekt
- **P** - Pausa
- **ESC** - Avsluta

## Level-filer

Spelet försöker ladda `levels/default_arena.json`. Om filen inte finns skapas en default level automatiskt.

Level-filer ska ligga i katalogen `levels/` i projektets root.

## Vad som är nytt

### ✅ Nya Features:
- **Health/Damage System** - Spelare har health och kan ta skada
- **Score System** - Poäng, kills, deaths, accuracy tracking
- **Weapon System** - Vapen med fire rate och damage
- **Shield System** - Sköldar som absorberar skada och recharge
- **Projectile System** - Förbättrad projektilhantering med kollision
- **Level System** - JSON-baserade levels
- **HUD** - Health bar, shield bar, score display
- **Game States** - Paus, Game Over, Victory
- **Respawn System** - Automatisk respawn efter död

### ⚠️ Kända Begränsningar:
- Main menu är inte implementerad (spelet startar direkt)
- Enkel single-player för nu
- Ingen AI/Bots ännu
- Wall collision för projektiler är delvis implementerad

---

*Ha så kul med Fysix!*

