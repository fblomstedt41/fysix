package net.force2dev.fysix.level;

/**
 * Default environment settings for a level
 */
public class EnvironmentSettings {
    private double friction;
    private double resistance;
    private double gravityX;
    private double gravityY;
    
    public EnvironmentSettings() {
        this(0.995, 1.0, 0.0, 0.0);
    }
    
    public EnvironmentSettings(double friction, double resistance, double gravityX, double gravityY) {
        this.friction = friction;
        this.resistance = resistance;
        this.gravityX = gravityX;
        this.gravityY = gravityY;
    }
    
    // Getters and setters
    public double getFriction() { return friction; }
    public void setFriction(double friction) { this.friction = friction; }
    
    public double getResistance() { return resistance; }
    public void setResistance(double resistance) { this.resistance = resistance; }
    
    public double getGravityX() { return gravityX; }
    public void setGravityX(double gravityX) { this.gravityX = gravityX; }
    
    public double getGravityY() { return gravityY; }
    public void setGravityY(double gravityY) { this.gravityY = gravityY; }
}

