package net.force2dev.fysix.camera;

import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.level.Level;
import net.force2dev.fysix.level.Wall;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.Dimension;
import java.awt.Polygon;
import java.util.List;

/**
 * Manages automatic camera zoom based on nearby walls and large objects
 */
public class AutoZoomManager {
    private static final double SHIP_LENGTH = 11.0; // Ship length in pixels (from -4 to 7 = 11 pixels)
    
    // Zoom targets:
    // When near wall (3-5 ship lengths = 33-55 pixels): ship should be 2-3 cm on screen
    // At 96 DPI: 2 cm = ~75 pixels, 3 cm = ~113 pixels
    // So scaleFactor needed: 75/11 = 6.8 to 113/11 = 10.3
    private static final double NEAR_WALL_DISTANCE_MIN = SHIP_LENGTH * 3.0; // 33 pixels (3 ship lengths)
    private static final double NEAR_WALL_DISTANCE_MAX = SHIP_LENGTH * 5.0; // 55 pixels (5 ship lengths)
    private static final double NEAR_WALL_SCREEN_SIZE_MIN = 75.0; // 2 cm at 96 DPI
    private static final double NEAR_WALL_SCREEN_SIZE_MAX = 113.0; // 3 cm at 96 DPI
    private static final double ZOOM_FOR_NEAR_WALL = NEAR_WALL_SCREEN_SIZE_MAX / SHIP_LENGTH; // ~10.3
    
    private static final double SMOOTH_FACTOR = 0.3; // How fast zoom changes (0-1, higher = faster) - increased for more responsive zoom
    private static final double MIN_ZOOM_FACTOR = 0.3; // Minimum zoom (zoomed out, see more of level)
    private static final double MAX_ZOOM_FACTOR = 12.0; // Maximum zoom (zoomed in, for near walls)
    
    private double currentTargetZoom = 1.0;
    private double currentZoom = 1.0;
    private final Dimension screenSize;
    
    public AutoZoomManager(Dimension screenSize) {
        this.screenSize = screenSize;
    }
    
    /**
     * Calculate optimal zoom level based on player position
     * @param playerPosition Player's position
     * @param walls Level walls
     * @param largeObjects Large objects (planets, etc.)
     * @param screenSize Screen size
     * @return Optimal zoom factor
     */
    public double calculateOptimalZoom(
            Point2d playerPosition,
            List<Wall> walls,
            List<FysixObject> largeObjects,
            Dimension screenSize) {
        
        // Check if player is near a large object (planet)
        boolean nearPlanet = false;
        for (FysixObject obj : largeObjects) {
            if (obj.getBoundingArea() == null || obj.getBoundingArea().npoints == 0) continue;
            
            double distance = playerPosition.distance(obj.getPosition());
            // Estimate planet radius from bounding area
            double objRadius = 50.0; // Default
            Polygon bounds = obj.getBoundingArea();
            if (bounds != null && bounds.npoints > 0) {
                for (int i = 0; i < bounds.npoints; i++) {
                    double dx = bounds.xpoints[i];
                    double dy = bounds.ypoints[i];
                    double distFromCenter = Math.sqrt(dx * dx + dy * dy);
                    if (distFromCenter > objRadius) {
                        objRadius = distFromCenter;
                    }
                }
            }
            if (objRadius < 10.0) objRadius = 50.0;
            
            double distanceToPlanetEdge = Math.max(0.1, distance - objRadius);
            if (distanceToPlanetEdge <= NEAR_WALL_DISTANCE_MAX) {
                nearPlanet = true;
                break;
            }
        }
        
        double normalZoom = calculateNormalZoom(playerPosition, walls, screenSize);
        double wideZoom = calculateWideZoom(playerPosition, largeObjects, screenSize);
        
        // If near planet, use wideZoom as the standard (don't zoom out more)
        // Otherwise, use the minimum to ensure everything is visible
        if (nearPlanet) {
            // When near planet, use wideZoom as standard - don't zoom out more than that
            return Math.min(normalZoom, wideZoom); // Still use min to respect wall proximity
        } else {
            // When far from planet, use normal zoom (based on walls)
            // But if wideZoom is valid and lower, use it to show planet in distance
            return Math.min(normalZoom, wideZoom);
        }
    }
    
    /**
     * Calculate zoom level for normal gameplay
     * When near wall (3-5 ship lengths): zoom in so ship is 2-3 cm on screen
     * When far from wall: zoom out to see more of the level
     */
    private double calculateNormalZoom(Point2d playerPosition, List<Wall> walls, Dimension screenSize) {
        double minDistance = Double.MAX_VALUE;
        
        // Find minimum distance to any wall
        // NOTE: We include boundary walls in zoom calculation because they're often the nearest walls
        for (Wall wall : walls) {
            Polygon wallPoly = wall.toPolygon();
            if (wallPoly.npoints == 0) continue;
            
            // Check distance to each wall segment
            for (int i = 0; i < wallPoly.npoints; i++) {
                int next = (i + 1) % wallPoly.npoints;
                Point2d p1 = new Point2d(wallPoly.xpoints[i], wallPoly.ypoints[i]);
                Point2d p2 = new Point2d(wallPoly.xpoints[next], wallPoly.ypoints[next]);
                
                double dist = pointToLineSegmentDistance(playerPosition, p1, p2);
                if (dist < minDistance && dist > 0) {
                    minDistance = dist;
                }
            }
        }
        
        if (minDistance == Double.MAX_VALUE || minDistance <= 0) {
            // No walls found - use default zoomed out view
            return MIN_ZOOM_FACTOR * 2.0; // Slightly zoomed out
        }
        
        // scaleFactor works like: 1 pixel in world = scaleFactor pixels on screen
        // Higher scaleFactor = more zoomed in (ship appears larger)
        // Lower scaleFactor = more zoomed out (see more of level)
        
        double zoom;
        if (minDistance <= NEAR_WALL_DISTANCE_MAX) {
            // Near wall (3-5 ship lengths): zoom in so ship is 2-3 cm on screen
            // Interpolate between max zoom at min distance and medium zoom at max distance
            if (minDistance <= NEAR_WALL_DISTANCE_MIN) {
                // Very close (<= 3 ship lengths): use maximum zoom
                zoom = ZOOM_FOR_NEAR_WALL;
            } else {
                // Between 3-5 ship lengths: interpolate
                double t = (minDistance - NEAR_WALL_DISTANCE_MIN) / 
                           (NEAR_WALL_DISTANCE_MAX - NEAR_WALL_DISTANCE_MIN);
                // Interpolate from max zoom (ZOOM_FOR_NEAR_WALL) to medium zoom (4.0)
                zoom = ZOOM_FOR_NEAR_WALL * (1.0 - t) + 4.0 * t;
            }
        } else {
            // Far from wall: zoom out to see more of level
            // Use a logarithmic scale so zoom decreases smoothly as distance increases
            // At distance = NEAR_WALL_DISTANCE_MAX, zoom = 4.0
            // As distance increases, zoom approaches MIN_ZOOM_FACTOR
            double excessDistance = minDistance - NEAR_WALL_DISTANCE_MAX;
            // Scale factor: zoom decreases by half for every 200 pixels of distance
            double decayRate = 200.0;
            zoom = 4.0 * Math.pow(0.5, excessDistance / decayRate);
            // Clamp to minimum
            zoom = Math.max(MIN_ZOOM_FACTOR, zoom);
        }
        
        // Clamp to reasonable values
        return Math.max(MIN_ZOOM_FACTOR, Math.min(MAX_ZOOM_FACTOR, zoom));
    }
    
    /**
     * Calculate zoom level for wide view when large objects (planets) are nearby
     * When player is 3-5 ship lengths from planet, zoom so that planet, moon, and ship are visible
     * Returns the zoom level needed to show player and large objects together
     */
    private double calculateWideZoom(Point2d playerPosition, List<FysixObject> largeObjects, Dimension screenSize) {
        double widestZoom = MAX_ZOOM_FACTOR; // Start with maximum zoom, then find minimum needed
        boolean foundLargeObject = false;
        
        // Check all large objects (planets) within a reasonable distance
        double maxDistanceToCheck = 3000.0; // Check planets up to 3000 pixels away
        
        for (FysixObject obj : largeObjects) {
            double distance = playerPosition.distance(obj.getPosition());
            
            // Only consider objects that are reasonably close
            if (distance > maxDistanceToCheck) continue;
            
            // Estimate object size (use bounding area)
            Polygon bounds = obj.getBoundingArea();
            if (bounds == null || bounds.npoints == 0) continue;
            
            // Calculate approximate radius of object
            // Bounding area polygon points are relative to object center (0,0)
            // So we just need to find max distance from (0,0) to any polygon point
            double objRadius = 0;
            for (int i = 0; i < bounds.npoints; i++) {
                double dx = bounds.xpoints[i];
                double dy = bounds.ypoints[i];
                double distFromCenter = Math.sqrt(dx * dx + dy * dy);
                if (distFromCenter > objRadius) {
                    objRadius = distFromCenter;
                }
            }
            
            // If object radius is very small, use a default minimum
            // This handles cases where bounding area might not be set correctly
            if (objRadius < 10.0) {
                objRadius = 50.0; // Default minimum for planets
            }
            
            // Special case: if player is 3-5 ship lengths from planet (near planet)
            // We want to show: player, planet, and moon (if moon exists)
            // Distance from player to planet center is 'distance'
            // Distance from player to planet edge is approximately: distance - objRadius
            
            double distanceToPlanetEdge = Math.max(0.1, distance - objRadius);
            
            // Check if player is in the "near planet" range (3-5 ship lengths from planet edge)
            // This means we're within 3-5 ship lengths from the planet's surface
            if (distanceToPlanetEdge <= NEAR_WALL_DISTANCE_MAX) {
                // When near planet (3-5 ship lengths), we want a zoom that shows:
                // - The player
                // - The planet (with some padding)
                // - Potential moon (if nearby)
                
                // Required world distance: distance to planet + planet radius + padding for moon visibility
                // Assume moon could be ~200 pixels from planet, so add that
                double requiredWorldDistance = distance + objRadius + 200.0; // Enough to see planet and potential moon
                
                // Use ~80% of smaller screen dimension to ensure comfortable view with padding
                double referenceScreenSize = Math.min(screenSize.width, screenSize.height) * 0.8;
                
                // zoom = screenSize / worldSize (lower = more zoomed out, higher = more zoomed in)
                double zoom = referenceScreenSize / requiredWorldDistance;
                
                if (zoom < widestZoom) {
                    widestZoom = zoom;
                    foundLargeObject = true;
                }
            } else {
                // When far from planet, we still want to show it if it's reasonably close
                // But use a more zoomed out view
                double requiredWorldDistance = distance + objRadius + SHIP_LENGTH * 3;
                double referenceScreenSize = Math.min(screenSize.width, screenSize.height) * 0.6;
                double zoom = referenceScreenSize / requiredWorldDistance;
                
                if (zoom < widestZoom) {
                    widestZoom = zoom;
                    foundLargeObject = true;
                }
            }
        }
        
        // If no large objects found, return a value that won't interfere with normal zoom
        if (!foundLargeObject) {
            return MAX_ZOOM_FACTOR; // This will be ignored in Math.min() in calculateOptimalZoom
        }
        
        // Clamp to reasonable values - don't zoom out more than needed
        // Minimum zoom should be around 0.5-1.0 for comfortable viewing
        return Math.max(0.5, Math.min(MAX_ZOOM_FACTOR, widestZoom));
    }
    
    /**
     * Calculate distance from point to line segment
     */
    private double pointToLineSegmentDistance(Point2d point, Point2d lineStart, Point2d lineEnd) {
        Vector2d line = new Vector2d(lineEnd.x - lineStart.x, lineEnd.y - lineStart.y);
        Vector2d pointVec = new Vector2d(point.x - lineStart.x, point.y - lineStart.y);
        
        double lineLengthSq = line.lengthSquared();
        if (lineLengthSq < 0.0001) {
            // Line segment is actually a point
            return point.distance(lineStart);
        }
        
        double t = Math.max(0, Math.min(1, pointVec.dot(line) / lineLengthSq));
        Vector2d projection = new Vector2d(line);
        projection.scale(t);
        projection.add(lineStart);
        
        return point.distance(new Point2d(projection.x, projection.y));
    }
    
    /**
     * Update zoom with smooth interpolation
     * @param targetZoom Target zoom level
     * @param deltaTimeSeconds Time since last update in seconds
     * @return Current zoom level (smoothly interpolated)
     */
    public double updateZoom(double targetZoom, double deltaTimeSeconds) {
        currentTargetZoom = targetZoom;
        
        // Smooth interpolation (exponential smoothing)
        // This gives smooth transitions without overshooting
        double smoothing = 1.0 - Math.pow(1.0 - SMOOTH_FACTOR, deltaTimeSeconds * 60.0); // Normalize to 60 FPS
        currentZoom = currentZoom + (currentTargetZoom - currentZoom) * smoothing;
        
        return currentZoom;
    }
    
    /**
     * Get current zoom level
     */
    public double getCurrentZoom() {
        return currentZoom;
    }
    
    /**
     * Set zoom directly (for manual override)
     */
    public void setZoom(double zoom) {
        currentZoom = zoom;
        currentTargetZoom = zoom;
    }
}


