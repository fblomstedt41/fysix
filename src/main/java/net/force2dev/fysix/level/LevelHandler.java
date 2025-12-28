
package net.force2dev.fysix.level;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles loading and management of game levels
 */
public class LevelHandler {
    private Level currentLevel;
    private static final String LEVELS_DIR = "levels";
    
    /**
     * Load a level from file
     */
    public Level loadLevel(String filename) throws IOException {
        Path levelPath = Paths.get(LEVELS_DIR, filename);
        currentLevel = LevelLoader.loadFromFile(levelPath);
        return currentLevel;
    }
    
    /**
     * Load a level from a Level object
     */
    public void setLevel(Level level) {
        this.currentLevel = level;
    }
    
    /**
     * Get the current level
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }
    
    /**
     * Create a default test level (for backwards compatibility)
     */
    public Level createDefaultLevel() {
        Level level = new Level("Default Arena", "Classic space battle arena", 5400, 5400);
        
        // Create boundary wall
        Wall boundary = new Wall();
        boundary.setId("boundary");
        boundary.setCollisionType("boundary");
        boundary.getPoints().add(new Point(0, 0));
        boundary.getPoints().add(new Point(5400, 0));
        boundary.getPoints().add(new Point(5400, 5400));
        boundary.getPoints().add(new Point(0, 5400));
        level.getWalls().add(boundary);
        
        // Add spawn points
        level.getSpawnPoints().add(new SpawnPoint(200, 200, 0, 1, "player"));
        level.getSpawnPoints().add(new SpawnPoint(5200, 5200, Math.PI, 2, "player"));
        
        // Add a planet in the center
        level.getGravityWells().add(new GravityWell(2600, 2600, 1600000, 100, 500));
        
        currentLevel = level;
        return level;
    }
}
