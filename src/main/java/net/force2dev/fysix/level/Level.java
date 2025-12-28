package net.force2dev.fysix.level;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete game level with all its components
 */
public class Level {
    private String name;
    private String description;
    private int width;
    private int height;
    private List<Wall> walls;
    private List<SpawnPoint> spawnPoints;
    private List<GravityWell> gravityWells;
    private List<EnvironmentZone> zones;
    private EnvironmentSettings defaultEnvironment;
    private String backgroundType; // "space", "asteroid", etc.
    
    public Level() {
        this("Untitled Level", "", 5400, 5400);
    }
    
    public Level(String name, String description, int width, int height) {
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.walls = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();
        this.gravityWells = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.defaultEnvironment = new EnvironmentSettings();
        this.backgroundType = "space";
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public List<Wall> getWalls() { return walls; }
    public void setWalls(List<Wall> walls) { this.walls = walls != null ? walls : new ArrayList<>(); }
    
    public List<SpawnPoint> getSpawnPoints() { return spawnPoints; }
    public void setSpawnPoints(List<SpawnPoint> spawnPoints) { this.spawnPoints = spawnPoints != null ? spawnPoints : new ArrayList<>(); }
    
    public List<GravityWell> getGravityWells() { return gravityWells; }
    public void setGravityWells(List<GravityWell> gravityWells) { this.gravityWells = gravityWells != null ? gravityWells : new ArrayList<>(); }
    
    public List<EnvironmentZone> getZones() { return zones; }
    public void setZones(List<EnvironmentZone> zones) { this.zones = zones != null ? zones : new ArrayList<>(); }
    
    public EnvironmentSettings getDefaultEnvironment() { return defaultEnvironment; }
    public void setDefaultEnvironment(EnvironmentSettings defaultEnvironment) { this.defaultEnvironment = defaultEnvironment; }
    
    public String getBackgroundType() { return backgroundType; }
    public void setBackgroundType(String backgroundType) { this.backgroundType = backgroundType; }
    
    /**
     * Get a spawn point for a specific team, or any spawn point if team is 0
     */
    public SpawnPoint getSpawnPoint(int team) {
        if (spawnPoints.isEmpty()) {
            // Default spawn in center
            return new SpawnPoint(width / 2.0, height / 2.0, 0, team, "player");
        }
        
        // Find spawn point for team
        for (SpawnPoint sp : spawnPoints) {
            if (team == 0 || sp.getTeam() == team) {
                return sp;
            }
        }
        
        // Fallback to first spawn point
        return spawnPoints.get(0);
    }
}

