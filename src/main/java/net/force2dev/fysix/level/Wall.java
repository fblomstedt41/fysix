package net.force2dev.fysix.level;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wall or collision boundary in the level
 */
public class Wall {
    private String id;
    private List<Point> points;
    private String collisionType; // "solid", "boundary", "damage"
    private int damageOnCollision; // 0 = no damage
    
    public Wall() {
        this("", new ArrayList<>(), "solid", 0);
    }
    
    public Wall(String id, List<Point> points, String collisionType, int damageOnCollision) {
        this.id = id;
        this.points = points != null ? points : new ArrayList<>();
        this.collisionType = collisionType;
        this.damageOnCollision = damageOnCollision;
    }
    
    /**
     * Convert to AWT Polygon for collision detection
     */
    public Polygon toPolygon() {
        Polygon polygon = new Polygon();
        for (Point p : points) {
            polygon.addPoint(p.x(), p.y());
        }
        return polygon;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }
    
    public String getCollisionType() { return collisionType; }
    public void setCollisionType(String collisionType) { this.collisionType = collisionType; }
    
    public int getDamageOnCollision() { return damageOnCollision; }
    public void setDamageOnCollision(int damageOnCollision) { this.damageOnCollision = damageOnCollision; }
}

