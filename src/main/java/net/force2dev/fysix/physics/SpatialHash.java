package net.force2dev.fysix.physics;

import net.force2dev.fysix.engine.FysixObject;

import javax.vecmath.Point2d;
import java.util.*;

/**
 * Spatial hash for efficient collision detection
 * Reduces collision checks from O(n²) to O(n) average case
 */
public class SpatialHash {
    private final Map<Integer, List<FysixObject>> cells = new HashMap<>();
    private final int cellSize;
    private final int worldWidth;
    private final int worldHeight;
    
    public SpatialHash(int cellSize, int worldWidth, int worldHeight) {
        this.cellSize = cellSize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    
    /**
     * Clear all cells
     */
    public void clear() {
        cells.clear();
    }
    
    /**
     * Insert an object into the spatial hash
     */
    public void insert(FysixObject obj) {
        Point2d pos = obj.getPosition();
        int cellX = (int)(pos.x / cellSize);
        int cellY = (int)(pos.y / cellSize);
        int hash = cellX * 10000 + cellY; // Simple hash function
        
        cells.computeIfAbsent(hash, k -> new ArrayList<>()).add(obj);
    }
    
    /**
     * Get all objects in the same cell as the given position
     */
    public List<FysixObject> getObjectsAt(Point2d pos) {
        int cellX = (int)(pos.x / cellSize);
        int cellY = (int)(pos.y / cellSize);
        int hash = cellX * 10000 + cellY;
        
        List<FysixObject> objects = cells.get(hash);
        return objects != null ? objects : Collections.emptyList();
    }
    
    /**
     * Get all objects in cells near the given position (including adjacent cells)
     */
    public List<FysixObject> getNearbyObjects(Point2d pos, double radius) {
        List<FysixObject> nearby = new ArrayList<>();
        Set<Integer> checkedCells = new HashSet<>();
        
        int cellX = (int)(pos.x / cellSize);
        int cellY = (int)(pos.y / cellSize);
        
        // Check current cell and adjacent cells (3x3 grid)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int hash = (cellX + dx) * 10000 + (cellY + dy);
                
                if (!checkedCells.contains(hash)) {
                    checkedCells.add(hash);
                    List<FysixObject> cellObjects = cells.get(hash);
                    if (cellObjects != null) {
                        // Filter by actual distance
                        for (FysixObject obj : cellObjects) {
                            double dist = pos.distance(obj.getPosition());
                            if (dist <= radius) {
                                nearby.add(obj);
                            }
                        }
                    }
                }
            }
        }
        
        return nearby;
    }
    
    /**
     * Get all potential collision pairs (objects in same or adjacent cells)
     */
    public List<CollisionPair> getPotentialCollisionPairs() {
        List<CollisionPair> pairs = new ArrayList<>();
        Set<String> checkedPairs = new HashSet<>();
        
        for (Map.Entry<Integer, List<FysixObject>> entry : cells.entrySet()) {
            List<FysixObject> objects = entry.getValue();
            
            // Check pairs within the same cell
            for (int i = 0; i < objects.size(); i++) {
                for (int j = i + 1; j < objects.size(); j++) {
                    FysixObject obj1 = objects.get(i);
                    FysixObject obj2 = objects.get(j);
                    String pairKey = getPairKey(obj1, obj2);
                    
                    if (!checkedPairs.contains(pairKey)) {
                        checkedPairs.add(pairKey);
                        pairs.add(new CollisionPair(obj1, obj2));
                    }
                }
            }
        }
        
        // Also check adjacent cells (simplified - could be optimized further)
        // For now, we rely on getNearbyObjects for cross-cell checks
        
        return pairs;
    }
    
    private String getPairKey(FysixObject obj1, FysixObject obj2) {
        // Create a unique key for a pair of objects
        int hash1 = System.identityHashCode(obj1);
        int hash2 = System.identityHashCode(obj2);
        return hash1 < hash2 ? hash1 + "," + hash2 : hash2 + "," + hash1;
    }
    
    /**
     * Represents a potential collision pair
     */
    public static class CollisionPair {
        public final FysixObject obj1;
        public final FysixObject obj2;
        
        public CollisionPair(FysixObject obj1, FysixObject obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
        }
    }
}

