package net.force2dev.fysix.level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves levels to JSON files
 */
public class LevelSaver {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    /**
     * Save a level to a JSON file
     */
    public static void saveToFile(Level level, Path filePath) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            saveToWriter(level, writer);
        }
    }
    
    /**
     * Save a level to a Writer
     */
    public static void saveToWriter(Level level, Writer writer) {
        JsonObject json = levelToJson(level);
        gson.toJson(json, writer);
    }
    
    /**
     * Convert a level to JSON string
     */
    public static String levelToJsonString(Level level) {
        JsonObject json = levelToJson(level);
        return gson.toJson(json);
    }
    
    private static JsonObject levelToJson(Level level) {
        JsonObject root = new JsonObject();
        root.addProperty("version", "1.0");
        
        JsonObject levelJson = new JsonObject();
        
        levelJson.addProperty("name", level.getName());
        levelJson.addProperty("description", level.getDescription());
        levelJson.addProperty("width", level.getWidth());
        levelJson.addProperty("height", level.getHeight());
        levelJson.addProperty("backgroundType", level.getBackgroundType());
        
        // Default environment
        levelJson.add("defaultEnvironment", environmentToJson(level.getDefaultEnvironment()));
        
        // Walls
        JsonArray wallsArray = new JsonArray();
        for (Wall wall : level.getWalls()) {
            wallsArray.add(wallToJson(wall));
        }
        levelJson.add("walls", wallsArray);
        
        // Spawn points
        JsonArray spawnsArray = new JsonArray();
        for (SpawnPoint sp : level.getSpawnPoints()) {
            spawnsArray.add(spawnPointToJson(sp));
        }
        levelJson.add("spawnPoints", spawnsArray);
        
        // Gravity wells
        JsonArray wellsArray = new JsonArray();
        for (GravityWell gw : level.getGravityWells()) {
            wellsArray.add(gravityWellToJson(gw));
        }
        levelJson.add("gravityWells", wellsArray);
        
        // Zones
        JsonArray zonesArray = new JsonArray();
        for (EnvironmentZone zone : level.getZones()) {
            zonesArray.add(zoneToJson(zone));
        }
        levelJson.add("zones", zonesArray);
        
        root.add("level", levelJson);
        return root;
    }
    
    private static JsonObject environmentToJson(EnvironmentSettings env) {
        JsonObject json = new JsonObject();
        json.addProperty("friction", env.getFriction());
        json.addProperty("resistance", env.getResistance());
        json.addProperty("gravityX", env.getGravityX());
        json.addProperty("gravityY", env.getGravityY());
        return json;
    }
    
    private static JsonObject wallToJson(Wall wall) {
        JsonObject json = new JsonObject();
        json.addProperty("id", wall.getId());
        json.addProperty("collisionType", wall.getCollisionType());
        json.addProperty("damageOnCollision", wall.getDamageOnCollision());
        json.add("points", pointsToJson(wall.getPoints()));
        return json;
    }
    
    private static JsonObject spawnPointToJson(SpawnPoint sp) {
        JsonObject json = new JsonObject();
        json.addProperty("x", sp.getX());
        json.addProperty("y", sp.getY());
        json.addProperty("angle", sp.getAngle());
        json.addProperty("team", sp.getTeam());
        json.addProperty("spawnType", sp.getSpawnType());
        return json;
    }
    
    private static JsonObject gravityWellToJson(GravityWell gw) {
        JsonObject json = new JsonObject();
        json.addProperty("x", gw.getX());
        json.addProperty("y", gw.getY());
        json.addProperty("mass", gw.getMass());
        json.addProperty("radius", gw.getRadius());
        json.addProperty("affectRadius", gw.getAffectRadius());
        return json;
    }
    
    private static JsonObject zoneToJson(EnvironmentZone zone) {
        JsonObject json = new JsonObject();
        json.addProperty("friction", zone.getFriction());
        json.addProperty("gravityX", zone.getGravityX());
        json.addProperty("gravityY", zone.getGravityY());
        json.addProperty("resistance", zone.getResistance());
        json.add("points", pointsToJson(zone.getPoints()));
        return json;
    }
    
    private static JsonArray pointsToJson(java.util.List<Point> points) {
        JsonArray array = new JsonArray();
        for (Point p : points) {
            JsonObject pointJson = new JsonObject();
            pointJson.addProperty("x", p.x());
            pointJson.addProperty("y", p.y());
            array.add(pointJson);
        }
        return array;
    }
}

