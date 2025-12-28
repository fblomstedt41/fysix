package net.force2dev.fysix.level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads levels from JSON files
 */
public class LevelLoader {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    /**
     * Load a level from a JSON file
     */
    public static Level loadFromFile(Path filePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            return loadFromReader(reader);
        }
    }
    
    /**
     * Load a level from an InputStream (useful for loading from resources)
     */
    public static Level loadFromStream(InputStream stream) throws IOException {
        try (Reader reader = new InputStreamReader(stream)) {
            return loadFromReader(reader);
        }
    }
    
    /**
     * Load a level from a Reader
     */
    public static Level loadFromReader(Reader reader) {
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        return parseLevel(json);
    }
    
    /**
     * Load a level from a JSON string
     */
    public static Level loadFromString(String jsonString) {
        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        return parseLevel(json);
    }
    
    private static Level parseLevel(JsonObject json) {
        JsonObject levelJson = json.has("level") ? json.getAsJsonObject("level") : json;
        
        Level level = new Level();
        
        if (levelJson.has("name")) level.setName(levelJson.get("name").getAsString());
        if (levelJson.has("description")) level.setDescription(levelJson.get("description").getAsString());
        if (levelJson.has("width")) level.setWidth(levelJson.get("width").getAsInt());
        if (levelJson.has("height")) level.setHeight(levelJson.get("height").getAsInt());
        if (levelJson.has("backgroundType")) level.setBackgroundType(levelJson.get("backgroundType").getAsString());
        
        // Parse default environment
        if (levelJson.has("defaultEnvironment")) {
            level.setDefaultEnvironment(parseEnvironmentSettings(levelJson.getAsJsonObject("defaultEnvironment")));
        }
        
        // Parse walls
        if (levelJson.has("walls")) {
            level.setWalls(parseWalls(levelJson.getAsJsonArray("walls")));
        }
        
        // Parse spawn points
        if (levelJson.has("spawnPoints")) {
            level.setSpawnPoints(parseSpawnPoints(levelJson.getAsJsonArray("spawnPoints")));
        }
        
        // Parse gravity wells
        if (levelJson.has("gravityWells")) {
            level.setGravityWells(parseGravityWells(levelJson.getAsJsonArray("gravityWells")));
        }
        
        // Parse environment zones
        if (levelJson.has("zones")) {
            level.setZones(parseZones(levelJson.getAsJsonArray("zones")));
        }
        
        return level;
    }
    
    private static EnvironmentSettings parseEnvironmentSettings(JsonObject json) {
        EnvironmentSettings env = new EnvironmentSettings();
        if (json.has("friction")) env.setFriction(json.get("friction").getAsDouble());
        if (json.has("resistance")) env.setResistance(json.get("resistance").getAsDouble());
        if (json.has("gravityX")) env.setGravityX(json.get("gravityX").getAsDouble());
        if (json.has("gravityY")) env.setGravityY(json.get("gravityY").getAsDouble());
        return env;
    }
    
    private static List<Wall> parseWalls(JsonArray array) {
        List<Wall> walls = new ArrayList<>();
        for (JsonElement elem : array) {
            JsonObject wallJson = elem.getAsJsonObject();
            Wall wall = new Wall();
            
            if (wallJson.has("id")) wall.setId(wallJson.get("id").getAsString());
            if (wallJson.has("collisionType")) wall.setCollisionType(wallJson.get("collisionType").getAsString());
            if (wallJson.has("damageOnCollision")) wall.setDamageOnCollision(wallJson.get("damageOnCollision").getAsInt());
            
            if (wallJson.has("points")) {
                wall.setPoints(parsePoints(wallJson.getAsJsonArray("points")));
            }
            
            walls.add(wall);
        }
        return walls;
    }
    
    private static List<SpawnPoint> parseSpawnPoints(JsonArray array) {
        List<SpawnPoint> spawnPoints = new ArrayList<>();
        for (JsonElement elem : array) {
            JsonObject spJson = elem.getAsJsonObject();
            SpawnPoint sp = new SpawnPoint();
            
            if (spJson.has("x")) sp.setX(spJson.get("x").getAsDouble());
            if (spJson.has("y")) sp.setY(spJson.get("y").getAsDouble());
            if (spJson.has("angle")) sp.setAngle(spJson.get("angle").getAsDouble());
            if (spJson.has("team")) sp.setTeam(spJson.get("team").getAsInt());
            if (spJson.has("spawnType")) sp.setSpawnType(spJson.get("spawnType").getAsString());
            
            spawnPoints.add(sp);
        }
        return spawnPoints;
    }
    
    private static List<GravityWell> parseGravityWells(JsonArray array) {
        List<GravityWell> wells = new ArrayList<>();
        for (JsonElement elem : array) {
            JsonObject gwJson = elem.getAsJsonObject();
            GravityWell gw = new GravityWell();
            
            if (gwJson.has("x")) gw.setX(gwJson.get("x").getAsDouble());
            if (gwJson.has("y")) gw.setY(gwJson.get("y").getAsDouble());
            if (gwJson.has("mass")) gw.setMass(gwJson.get("mass").getAsDouble());
            if (gwJson.has("radius")) gw.setRadius(gwJson.get("radius").getAsDouble());
            if (gwJson.has("affectRadius")) gw.setAffectRadius(gwJson.get("affectRadius").getAsDouble());
            
            wells.add(gw);
        }
        return wells;
    }
    
    private static List<EnvironmentZone> parseZones(JsonArray array) {
        List<EnvironmentZone> zones = new ArrayList<>();
        for (JsonElement elem : array) {
            JsonObject zoneJson = elem.getAsJsonObject();
            EnvironmentZone zone = new EnvironmentZone();
            
            if (zoneJson.has("friction")) zone.setFriction(zoneJson.get("friction").getAsDouble());
            if (zoneJson.has("gravityX")) zone.setGravityX(zoneJson.get("gravityX").getAsDouble());
            if (zoneJson.has("gravityY")) zone.setGravityY(zoneJson.get("gravityY").getAsDouble());
            if (zoneJson.has("resistance")) zone.setResistance(zoneJson.get("resistance").getAsDouble());
            
            if (zoneJson.has("points")) {
                zone.setPoints(parsePoints(zoneJson.getAsJsonArray("points")));
            }
            
            zones.add(zone);
        }
        return zones;
    }
    
    private static List<Point> parsePoints(JsonArray array) {
        List<Point> points = new ArrayList<>();
        for (JsonElement elem : array) {
            JsonObject pointJson = elem.getAsJsonObject();
            int x = pointJson.get("x").getAsInt();
            int y = pointJson.get("y").getAsInt();
            points.add(new Point(x, y));
        }
        return points;
    }
}

