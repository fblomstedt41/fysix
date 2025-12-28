# Månens Omloppsbana

## Fysik: Cirkulär Omloppsbana

För en stabil cirkulär omloppsbana runt en planet krävs att centripetalkraften matchar gravitationskraften:

**F_centripetal = m * v² / r**
**F_gravitation = G * M * m / r²**

För jämvikt:
```
m * v² / r = G * M * m / r²
v² / r = G * M / r²
v² = G * M / r
v = sqrt(G * M / r)
```

där:
- **v** = omloppshastighet (pixlar per sekund)
- **G** = gravitationskonstant = 0.0001 (från FysixEngine)
- **M** = planetens massa = 1600000
- **r** = avståndet mellan centrum av planeten och centrum av månen

## Beräkningar för Default Arena

**Planeten:**
- Position: (2600, 2600)
- Massa: 1600000

**Månen:**
- Position: (2780, 2550)
- Massa: 50

**Avstånd:**
```
r = sqrt((2780-2600)² + (2550-2600)²)
  = sqrt(180² + (-50)²)
  = sqrt(32400 + 2500)
  = sqrt(34900)
  ≈ 186.8 pixlar
```

**Omloppshastighet:**
```
v = sqrt(0.0001 * 1600000 / 186.8)
  = sqrt(160 / 186.8)
  = sqrt(0.856)
  ≈ 0.925 pixlar per sekund
```

**Hastighetsriktning:**
- Från planeten till månen: (180, -50)
- Normaliserad riktning: (0.963, -0.268)
- Vinkelrät riktning (90° moturs): (-dy, dx) = (50, 180)
- Normaliserad vinkelrät: (0.268, 0.963)
- Hastighetsvektor: (0.925 * 0.268, 0.925 * 0.963) ≈ (0.248, 0.891)

## Implementering

1. ✅ Identifiera månen (lägsta massa bland GravityWells)
2. ✅ Identifiera planeten (högsta massa bland GravityWells)
3. ✅ Beräkna avstånd r
4. ✅ Beräkna omloppshastighet v = sqrt(G * M / r)
5. ✅ Beräkna vinkelrät riktning (90° moturs från planet-mån riktning)
6. ✅ Sätt månen's initial hastighet vinkelrätt mot planet-mån linjen
7. ✅ Sätt planetens hastighet till 0 (den är så mycket större att den praktiskt taget är statisk)

## Notering

I koden rör sig både planeten och månen enligt Newtons gravitationslag, men eftersom planeten är så mycket större (1600000 vs 50), kommer den praktiskt taget stå still. För en perfekt cirkulär omloppsbana skulle även planeten röra sig en liten bit, men effekten är försumbar i detta fall.

**Formeln fungerar för alla cirkulära omloppsbanor:**
- v = sqrt(G * M / r) ger exakt rätt hastighet för en stabil cirkulär bana
- Riktningen måste vara vinkelrät mot riktningen mellan planet och måne
- Om hastigheten är för låg → månen faller mot planeten
- Om hastigheten är för hög → månen flyger iväg
- Med exakt rätt hastighet → perfekt cirkulär omloppsbana

