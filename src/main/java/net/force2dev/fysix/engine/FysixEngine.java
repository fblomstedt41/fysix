package net.force2dev.fysix.engine;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.force2dev.fysix.physics.SpatialHash;

public class FysixEngine {

    private static FysixEngine engine;
    private FysixWorld fWorld = new FysixWorld();
    private SpatialHash spatialHash;
    private boolean useSpatialHash = true; // Enable spatial partitioning

    public static FysixEngine GetContext() {
        if (engine == null) {
            engine = new FysixEngine();
        }
        return engine;
    }

	public IControllable AddObject(double posX, double posY, double mass, IEventCallback cb) {
        FysixObject newObj = new FysixObject();
        newObj.setMass(mass);
        newObj.setPosition(new Point2d(posX, posY));
        newObj.registerEventCallback(cb);
        fWorld.addObject(newObj);
        
        // Update spatial hash if enabled
        if (useSpatialHash && spatialHash != null) {
            spatialHash.insert(newObj);
        }
        
        return newObj;
    }
	
    public void RemoveObject(IControllable obj) {
        fWorld.removeObject((FysixObject) obj);
        // Note: Spatial hash will be rebuilt each frame, so no need to remove here
    }
    
    /**
     * Initialize spatial hash for collision optimization
     */
    public void initializeSpatialHash(int cellSize, int worldWidth, int worldHeight) {
        this.spatialHash = new SpatialHash(cellSize, worldWidth, worldHeight);
        this.useSpatialHash = true;
    }
    
    public void setUseSpatialHash(boolean use) {
        this.useSpatialHash = use;
    }

	/**
	 * Update physics simulation
	 * @param env Environment settings
	 * @param deltaTimeSeconds Delta time in seconds (not milliseconds!)
	 */
	public void Tick(Environment env, double deltaTimeSeconds) {
		Tick(env, deltaTimeSeconds, null);
	}
	
	/**
	 * Update physics simulation with shockwave effects
	 * @param env Environment settings
	 * @param deltaTimeSeconds Delta time in seconds (not milliseconds!)
	 * @param shockwaveForceProvider Provider for shockwave forces (can be null)
	 */
	public void Tick(Environment env, double deltaTimeSeconds, ShockwaveForceProvider shockwaveForceProvider) {
        // Cap delta time to prevent spiral of death
        deltaTimeSeconds = Math.min(deltaTimeSeconds, 0.25); // Max 250ms
        
        /* TODO:
         *  - Calculate elastic collision velocity
         *  - Calculate gravity affect acceleration on each object
         *  - Calculate new velocity on each object
         *    * Consider environmental parameters
         *  - Do collision detection (using new velocity)
         *  - Update position on each object
         */
        
        // Update velocities: apply environment acceleration, object acceleration, and gravity
        for (Iterator i = fWorld.getAllObjects(); i.hasNext(); ) {
            FysixObject fo = (FysixObject) i.next();
            Vector2d totAcc = env.getEnvironmentAccelerationAtPoint(fo.getPosition());
            
            // Add object's own acceleration (from thrust, etc.)
            totAcc.add(fo.getAcceleration());
            
            // Add shockwave forces if available
            if (shockwaveForceProvider != null) {
                double[] shockwaveForce = shockwaveForceProvider.getForce(
                    fo.getPosition().x, 
                    fo.getPosition().y
                );
                if (shockwaveForce != null && (shockwaveForce[0] != 0 || shockwaveForce[1] != 0)) {
                    // Convert force to acceleration (F = ma, so a = F/m)
                    // For light objects (like player ship), use minimum mass to avoid excessive acceleration
                    double effectiveMass = Math.max(fo.getMass(), 50.0); // Minimum mass for gameplay
                    Vector2d shockwaveAcc = new Vector2d(
                        shockwaveForce[0] / effectiveMass,
                        shockwaveForce[1] / effectiveMass
                    );
                    totAcc.add(shockwaveAcc);
                }
            }
            
            // Add gravitational acceleration from all heavy objects (planets, etc.)
            // This makes gravity work for ALL objects, not just heavy ones
            for (int j = 0; j < fWorld.objects.size(); j++) {
                FysixObject heavyObj = (FysixObject) fWorld.objects.get(j);
                
                // Skip self and objects with low mass (they don't have significant gravity)
                if (fo == heavyObj || heavyObj.getMass() < 100.0) continue;
                
                // Calculate distance vector from fo to heavyObj
                Vector2d dist = new Vector2d();
                dist.sub(heavyObj.getPosition(), fo.getPosition());
                double distance = dist.length();
                
                // Determine gravity range based on mass
                // For gameplay: player should feel gravity at 5-7 ship lengths (55-77 pixels)
                // For planets: need larger range to keep moons in orbit (2000+ pixels)
                double SHIP_LENGTH = 11.0; // Ship length in pixels
                double maxDistance;
                
                if (heavyObj.getMass() > 100000) {
                    // Large planets: use large range for moons and gameplay
                    maxDistance = 2500.0; // Large enough for moon orbits (~186 pixels) + extra
                } else {
                    // Smaller objects: use gameplay-focused range
                    maxDistance = SHIP_LENGTH * 7.0; // 77 pixels = 7 ship lengths
                }
                
                if (distance > 0.1 && distance < maxDistance) {
                    // Game-appropriate gravity: stronger and more noticeable than realistic
                    // For gameplay, we want gravity to be felt but not overwhelming
                    double G = 50.0; // Much stronger G for game feel (was 0.0001 - too weak!)
                    
                    // Calculate gravitational acceleration: a = G * M / r^2
                    // This affects all objects the same regardless of their mass (like in real gravity)
                    double accelMag = (G * heavyObj.getMass()) / (distance * distance);
                    
                    // Normalize direction vector
                    Vector2d direction = new Vector2d(dist);
                    direction.normalize();
                    
                    // Add to total acceleration
                    Vector2d gravityComponent = new Vector2d(direction);
                    gravityComponent.scale(accelMag);
                    totAcc.add(gravityComponent);
                }
            }
            
            // v = v0 + a * dt
            Vector2d newVelocity = (Vector2d) fo.getVelocity().clone();
            Vector2d accDelta = (Vector2d) totAcc.clone();
            accDelta.scale(deltaTimeSeconds);
            newVelocity.add(accDelta);
            
            // Apply environmental resistance
            double resistance = env.getEnvironmentalResistanceAtPoint(fo.getPosition());
            newVelocity.scale(resistance);
            
            fo.setVelocity(newVelocity);
        }
        
        // Collision detection - use spatial hash if enabled for better performance
        if (useSpatialHash && spatialHash != null && fWorld.objects.size() > 10) {
            // Rebuild spatial hash each frame
            spatialHash.clear();
            for (int i = 0; i < fWorld.objects.size(); i++) {
                FysixObject obj = (FysixObject) fWorld.objects.get(i);
                spatialHash.insert(obj);
            }
            
            // Check collisions using spatial hash (O(n) average instead of O(n²))
            List<SpatialHash.CollisionPair> pairs = spatialHash.getPotentialCollisionPairs();
                    for (SpatialHash.CollisionPair pair : pairs) {
                        FysixObject fo1 = pair.obj1;
                        FysixObject fo2 = pair.obj2;

                        // Only set color for collision debugging if objects don't have colors set already
                        // This prevents overwriting planet/moon colors
                        if (FysixCollisionDetector.checkCollision(fo1, fo2)) {
                            // Only set to red if color is still the default green
                            if (fo1.color == Color.GREEN) fo1.color = Color.RED;
                            if (fo2.color == Color.GREEN) fo2.color = Color.RED;
                        }
                        // Don't set to green - preserve existing colors
                    }
        } else {
            // Fallback to O(n²) brute force for small number of objects
            for (int i = 0; i < fWorld.objects.size(); i++) {
                FysixObject fo1 = (FysixObject) fWorld.objects.get(i);
                for (int j = i+1; j < fWorld.objects.size(); j++) {
                        FysixObject fo2 = (FysixObject) fWorld.objects.get(j);
                        // Only set color for collision debugging if objects don't have colors set already
                        if (FysixCollisionDetector.checkCollision(fo1, fo2)) {
                            // Only set to red if color is still the default green
                            if (fo1.color == Color.GREEN) fo1.color = Color.RED;
                            if (fo2.color == Color.GREEN) fo2.color = Color.RED;
                        }
                        // Don't set to green - preserve existing colors
                }
            }
        }               
        
        for (Iterator i = fWorld.getAllObjects(); i.hasNext(); ) {
            FysixObject fo = (FysixObject) i.next();
            
            // s = v * dt
            Vector2d move = (Vector2d) fo.getVelocity().clone();
            move.scale(deltaTimeSeconds);
            fo.getPosition().add(move);
            fWorld.updateObject(fo);
        }
    }
}
