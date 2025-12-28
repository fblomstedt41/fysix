package net.force2dev.fysix.level;

/**
 * Represents a spawn point where players or objects can spawn
 */
public class SpawnPoint {
    private double x;
    private double y;
    private double angle; // radians
    private int team; // 0 = neutral, 1, 2, etc.
    private String spawnType; // "player", "enemy", "neutral"
    
    public SpawnPoint() {
        this(0, 0, 0, 0, "player");
    }
    
    public SpawnPoint(double x, double y, double angle, int team, String spawnType) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.team = team;
        this.spawnType = spawnType;
    }
    
    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }
    
    public int getTeam() { return team; }
    public void setTeam(int team) { this.team = team; }
    
    public String getSpawnType() { return spawnType; }
    public void setSpawnType(String spawnType) { this.spawnType = spawnType; }
}

