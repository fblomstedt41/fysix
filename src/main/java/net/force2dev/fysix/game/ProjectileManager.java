package net.force2dev.fysix.game;

import net.force2dev.fysix.engine.FysixCollisionDetector;
import net.force2dev.fysix.engine.FysixEngine;
import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.level.Level;
import net.force2dev.fysix.level.Wall;

import javax.vecmath.Point2d;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages all projectiles in the game
 */
public class ProjectileManager {
    private List<Projectile> projectiles;
    private FysixEngine physicsEngine;
    
    /**
     * Callback interface for projectile hits
     */
    public interface HitCallback {
        void onHit(double x, double y, boolean hitPlayer);
    }
    
    private HitCallback hitCallback;
    
    public ProjectileManager(FysixEngine physicsEngine) {
        this.physicsEngine = physicsEngine;
        this.projectiles = new ArrayList<>();
    }
    
    /**
     * Set callback for when projectiles hit something
     */
    public void setHitCallback(HitCallback callback) {
        this.hitCallback = callback;
    }
    
    /**
     * Create a new projectile
     */
    public Projectile createProjectile(double x, double y, double angle, double speed, int damage, int ownerId) {
        FysixObject projObj = (FysixObject) physicsEngine.AddObject(x, y, 0.000000001, null);
        
        javax.vecmath.Vector2d vel = new javax.vecmath.Vector2d(Math.cos(angle), Math.sin(angle));
        vel.scale(speed);
        projObj.setVelocity(vel);
        projObj.setDirection(angle);
        
        // Set bounding area (small line for bullet)
        java.awt.Polygon ba = new java.awt.Polygon();
        ba.addPoint(-1, 0);
        ba.addPoint(1, 0);
        projObj.setBoundingArea(ba);
        
        Projectile proj = new Projectile(projObj, damage, ownerId, 10000); // 10 second lifetime
        projectiles.add(proj);
        
        return proj;
    }
    
    /**
     * Update projectiles and check collisions
     */
    public void update(List<Player> players, Level level) {
        Iterator<Projectile> iter = projectiles.iterator();
        while (iter.hasNext()) {
            Projectile proj = iter.next();
            
            // Remove expired projectiles
            if (proj.isExpired()) {
                physicsEngine.RemoveObject(proj.getPhysicsObject());
                iter.remove();
                continue;
            }
            
            // Check collision with players
            FysixObject projObj = proj.getPhysicsObject();
            for (Player player : players) {
                if (!player.isAlive() || player.getPlayerId() == proj.getOwnerId()) {
                    continue; // Skip dead players and owner
                }
                
                FysixObject playerObj = player.getShipObject();
                if (playerObj != null && FysixCollisionDetector.checkCollision(projObj, playerObj)) {
                    // Hit player!
                    Point2d hitPos = projObj.getPosition();
                    if (player.takeDamage(proj.getDamage())) {
                        // Player died
                        Player owner = findPlayerById(players, proj.getOwnerId());
                        if (owner != null) {
                            owner.registerKill(100); // 100 points per kill
                        }
                    }
                    player.registerHit();
                    
                    // Notify hit callback
                    if (hitCallback != null) {
                        hitCallback.onHit(hitPos.x, hitPos.y, true);
                    }
                    
                    // Remove projectile
                    physicsEngine.RemoveObject(projObj);
                    proj.hit();
                    iter.remove();
                    break;
                }
            }
            
            // Check collision with walls
            if (level != null) {
                for (Wall wall : level.getWalls()) {
                    if (checkProjectileWallCollision(projObj, wall)) {
                        // Hit wall
                        Point2d hitPos = projObj.getPosition();
                        
                        // Notify hit callback
                        if (hitCallback != null) {
                            hitCallback.onHit(hitPos.x, hitPos.y, false);
                        }
                        
                        physicsEngine.RemoveObject(projObj);
                        proj.hit();
                        iter.remove();
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Check if projectile collides with a wall
     */
    private boolean checkProjectileWallCollision(FysixObject projObj, Wall wall) {
        Polygon wallPoly = wall.toPolygon();
        if (wallPoly.npoints < 3) return false;
        
        // Check if projectile point is inside wall polygon
        double px = projObj.getPosition().x;
        double py = projObj.getPosition().y;
        
        return pointInPolygon(px, py, wallPoly);
    }
    
    /**
     * Point-in-polygon test
     */
    private boolean pointInPolygon(double x, double y, Polygon poly) {
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
    
    private Player findPlayerById(List<Player> players, int playerId) {
        for (Player p : players) {
            if (p.getPlayerId() == playerId) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Get all active projectiles
     */
    public List<Projectile> getProjectiles() {
        return projectiles;
    }
    
    /**
     * Clear all projectiles
     */
    public void clear() {
        for (Projectile proj : projectiles) {
            physicsEngine.RemoveObject(proj.getPhysicsObject());
        }
        projectiles.clear();
    }
}

