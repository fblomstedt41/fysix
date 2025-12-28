package net.force2dev.fysix.game;

import net.force2dev.fysix.engine.FysixCollisionDetector;
import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.level.Level;
import net.force2dev.fysix.level.Wall;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles collision between game objects and level walls
 */
public class WallCollisionHandler {
    private List<FysixObject> wallObjects;
    
    public WallCollisionHandler(Level level) {
        wallObjects = createWallObjects(level);
    }
    
    /**
     * Create FysixObject representations of walls for collision detection
     */
    private List<FysixObject> createWallObjects(Level level) {
        List<FysixObject> walls = new ArrayList<>();
        
        for (Wall wall : level.getWalls()) {
            Polygon wallPoly = wall.toPolygon();
            if (wallPoly.npoints > 0) {
                FysixObject wallObj = new FysixObject();
                
                // Create polygon at origin, we'll check collision with offset
                wallObj.setBoundingArea(wallPoly);
                wallObj.setPosition(new javax.vecmath.Point2d(0, 0)); // Walls are in world space
                
                walls.add(wallObj);
            }
        }
        
        return walls;
    }
    
    /**
     * Check if an object collides with any wall
     * @param obj The object to check
     * @return The wall that was hit, or null if no collision
     */
    public Wall checkCollision(FysixObject obj, Level level) {
        for (int i = 0; i < wallObjects.size() && i < level.getWalls().size(); i++) {
            FysixObject wallObj = wallObjects.get(i);
            Wall wall = level.getWalls().get(i);
            
            // Create a temporary polygon with the wall's position
            Polygon wallPoly = wall.toPolygon();
            if (wallPoly.npoints == 0) continue;
            
            // Translate wall polygon to check collision
            Polygon translatedWall = new Polygon();
            for (int j = 0; j < wallPoly.npoints; j++) {
                translatedWall.addPoint(wallPoly.xpoints[j], wallPoly.ypoints[j]);
            }
            
            // Create a temporary object with translated polygon for collision check
            FysixObject tempWallObj = new FysixObject();
            tempWallObj.setBoundingArea(translatedWall);
            tempWallObj.setPosition(new javax.vecmath.Point2d(0, 0));
            
            // Check collision using the collision detector
            if (checkPointInPolygon(obj.getPosition().x, obj.getPosition().y, wallPoly)) {
                return wall;
            }
        }
        
        return null;
    }
    
    /**
     * Check if a point is inside a polygon (simple point-in-polygon test)
     */
    private boolean checkPointInPolygon(double x, double y, Polygon poly) {
        if (poly.npoints < 3) return false;
        
        boolean inside = false;
        int j = poly.npoints - 1;
        
        for (int i = 0; i < poly.npoints; i++) {
            double xi = poly.xpoints[i];
            double yi = poly.ypoints[i];
            double xj = poly.xpoints[j];
            double yj = poly.ypoints[j];
            
            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            
            if (intersect) {
                inside = !inside;
            }
            
            j = i;
        }
        
        return inside;
    }
    
    /**
     * Handle collision response - push object away from wall
     */
    public void handleCollision(FysixObject obj, Wall wall) {
        // Simple response: reverse velocity and move away slightly
        javax.vecmath.Vector2d vel = obj.getVelocity();
        vel.scale(-0.5); // Bounce back with reduced speed
        
        // Limit max velocity after collision
        if (vel.length() > 200) {
            vel.normalize();
            vel.scale(200);
        }
    }
    
    /**
     * Get all wall objects (for rendering or other purposes)
     */
    public List<FysixObject> getWallObjects() {
        return wallObjects;
    }
}

