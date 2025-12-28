package net.force2dev.fysix.level;

/**
 * Represents a gravity well (planet, asteroid, etc.) that affects other objects
 */
public class GravityWell {
    private double x;
    private double y;
    private double mass;
    private double radius; // Visual radius
    private double affectRadius; // Gravity affect area
    
    public GravityWell() {
        this(0, 0, 0, 0, 0);
    }
    
    public GravityWell(double x, double y, double mass, double radius, double affectRadius) {
        this.x = x;
        this.y = y;
        this.mass = mass;
        this.radius = radius;
        this.affectRadius = affectRadius;
    }
    
    // Getters and setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = mass; }
    
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    
    public double getAffectRadius() { return affectRadius; }
    public void setAffectRadius(double affectRadius) { this.affectRadius = affectRadius; }
}

