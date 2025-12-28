package net.force2dev.fysix.effects;

import java.awt.Color;

/**
 * Represents a single particle for visual effects
 */
public class Particle {
    private double x, y;
    private double vx, vy; // Velocity
    private double life; // 0.0 to 1.0, where 0.0 = dead, 1.0 = full life
    private Color color;
    private double size;
    private double decayRate; // How fast particle fades (per second)
    
    public Particle(double x, double y, double vx, double vy, Color color, double size, double lifetime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.size = size;
        this.life = 1.0;
        this.decayRate = 1.0 / lifetime; // Lifetime in seconds
    }
    
    /**
     * Update particle (position, life, etc.)
     * @param deltaTimeSeconds Time since last update in seconds
     * @return true if particle is still alive
     */
    public boolean update(double deltaTimeSeconds) {
        // Update position
        x += vx * deltaTimeSeconds;
        y += vy * deltaTimeSeconds;
        
        // Decay life
        life -= decayRate * deltaTimeSeconds;
        
        // Apply gravity/friction
        vy += 100.0 * deltaTimeSeconds; // Gravity
        vx *= (1.0 - 0.5 * deltaTimeSeconds); // Air resistance
        vy *= (1.0 - 0.5 * deltaTimeSeconds);
        
        return life > 0.0;
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getLife() { return life; }
    public Color getColor() { return color; }
    public double getSize() { return size; }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }
}

