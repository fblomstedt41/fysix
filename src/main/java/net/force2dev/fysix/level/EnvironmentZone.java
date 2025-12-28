package net.force2dev.fysix.level;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an area with custom environment settings (friction, gravity, etc.)
 */
public class EnvironmentZone {
    private List<Point> points;
    private double friction;
    private double gravityX;
    private double gravityY;
    private double resistance;
    
    public EnvironmentZone() {
        this(new ArrayList<>(), 0.995, 0.0, 0.0, 1.0);
    }
    
    public EnvironmentZone(List<Point> points, double friction, double gravityX, double gravityY, double resistance) {
        this.points = points != null ? points : new ArrayList<>();
        this.friction = friction;
        this.gravityX = gravityX;
        this.gravityY = gravityY;
        this.resistance = resistance;
    }
    
    /**
     * Convert to AWT Polygon for area checking
     */
    public Polygon toPolygon() {
        Polygon polygon = new Polygon();
        for (Point p : points) {
            polygon.addPoint(p.x(), p.y());
        }
        return polygon;
    }
    
    // Getters and setters
    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }
    
    public double getFriction() { return friction; }
    public void setFriction(double friction) { this.friction = friction; }
    
    public double getGravityX() { return gravityX; }
    public void setGravityX(double gravityX) { this.gravityX = gravityX; }
    
    public double getGravityY() { return gravityY; }
    public void setGravityY(double gravityY) { this.gravityY = gravityY; }
    
    public double getResistance() { return resistance; }
    public void setResistance(double resistance) { this.resistance = resistance; }
}

