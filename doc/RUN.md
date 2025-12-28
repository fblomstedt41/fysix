# Så här kör du Fysix

## Snabbstart

### Alternativ 1: Kör direkt med Maven (rekommenderat)

```bash
cd /Users/fredrikblomstedt/development/fysix
mvn exec:java -Dexec.mainClass="net.force2dev.fysix.FysixMain"
```

### Alternativ 2: Bygg och kör JAR-fil

```bash
# Bygg projektet
mvn clean package

# Kör med JAR (alla dependencies inkluderade)
java -jar target/fysix-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Alternativ 3: Använd den gamla batch-filen (om du är på Windows)

Om du är på Windows kan du använda den gamla batch-filen, men den behöver uppdateras för Java 17.

## Krav

- Java 17 eller senare (JDK eller JRE)
- Maven (om du vill bygga själv)

Kolla din Java-version:
```bash
java -version
```

## Felsökning

Om du får fel om att Java inte hittas:
- Se till att Java är installerat
- Se till att JAVA_HOME är satt korrekt
- Använd fullständig sökväg till `java`-kommandot

Om level-filen inte hittas:
- Spelet skapar automatiskt en default level om JSON-filen saknas
- Detta är OK, spelet fungerar ändå!

## Kontroller

- **Pil Nedåt** → Thrust (gasa framåt)
- **Vänster Pil** → Rotera vänster
- **Höger Pil** → Rotera höger  
- **Pil Upp** → Skjut
- **F1** → Zoom in
- **F2** → Zoom ut
- **F3** → Toggle TV-effekt
- **P** → Pausa spelet
- **ESC** → Avsluta

---

Lycka till! 🚀

