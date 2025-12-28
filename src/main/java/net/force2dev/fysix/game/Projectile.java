package net.force2dev.fysix.game;

import net.force2dev.fysix.engine.FysixObject;

/**
 * Represents a projectile (bullet, missile, etc.)
 */
public class Projectile {
    private FysixObject physicsObject;
    private int damage;
    private int ownerId; // ID of player who fired this
    private long lifetime; // milliseconds
    private long creationTime;
    private boolean isActive;
    
    public Projectile(FysixObject physicsObject, int damage, int ownerId, long lifetime) {
        this.physicsObject = physicsObject;
        this.damage = damage;
        this.ownerId = ownerId;
        this.lifetime = lifetime;
        this.creationTime = System.currentTimeMillis();
        this.isActive = true;
    }
    
    /**
     * Check if projectile should be removed (lifetime expired)
     */
    public boolean isExpired() {
        if (!isActive) return true;
        long age = System.currentTimeMillis() - creationTime;
        return age >= lifetime;
    }
    
    /**
     * Mark projectile as hit (so it can be removed)
     */
    public void hit() {
        this.isActive = false;
    }
    
    // Getters
    public FysixObject getPhysicsObject() { return physicsObject; }
    public int getDamage() { return damage; }
    public int getOwnerId() { return ownerId; }
    public boolean isActive() { return isActive; }
}

