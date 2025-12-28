package net.force2dev.fysix.game;

import net.force2dev.fysix.engine.FysixObject;
import net.force2dev.fysix.equipment.Shield;
import net.force2dev.fysix.equipment.Weapon;
import net.force2dev.fysix.level.SpawnPoint;

/**
 * Represents a player in the game
 */
public class Player {
    private String name;
    private int playerId;
    private int team;
    private FysixObject shipObject;
    private Health health;
    private Score score;
    private Weapon weapon;
    private Shield shield;
    private SpawnPoint lastSpawnPoint;
    private boolean isAlive;
    
    // Respawn
    private long respawnTime;
    private static final long RESPAWN_DELAY_MS = 3000; // 3 seconds
    
    public Player(String name, int playerId, int team) {
        this.name = name;
        this.playerId = playerId;
        this.team = team;
        this.health = new Health(100);
        this.score = new Score();
        this.isAlive = false;
        this.respawnTime = 0;
    }
    
    /**
     * Spawn the player at a spawn point
     */
    public void spawn(SpawnPoint spawnPoint, FysixObject shipObject) {
        this.lastSpawnPoint = spawnPoint;
        this.shipObject = shipObject;
        this.isAlive = true;
        this.health.reset();
        
        if (shipObject != null) {
            shipObject.getPosition().x = spawnPoint.getX();
            shipObject.getPosition().y = spawnPoint.getY();
            shipObject.setDirection(spawnPoint.getAngle());
            shipObject.setVelocity(new javax.vecmath.Vector2d(0, 0));
            shipObject.setAcceleration(new javax.vecmath.Vector2d(0, 0));
        }
    }
    
    /**
     * Handle player death
     */
    public void die() {
        this.isAlive = false;
        this.respawnTime = System.currentTimeMillis() + RESPAWN_DELAY_MS;
        this.score.addDeath();
        
        if (shipObject != null) {
            // Stop the ship
            shipObject.setVelocity(new javax.vecmath.Vector2d(0, 0));
            shipObject.setAcceleration(new javax.vecmath.Vector2d(0, 0));
        }
    }
    
    /**
     * Check if player can respawn
     */
    public boolean canRespawn() {
        return !isAlive && System.currentTimeMillis() >= respawnTime;
    }
    
    /**
     * Apply damage to the player
     * @param damage Amount of damage
     * @return true if player died
     */
    public boolean takeDamage(int damage) {
        if (!isAlive || health.isInvulnerable(1000)) { // 1 second invulnerability after spawn
            return false;
        }
        
        // Apply damage to shield first if available
        if (shield != null && shield.isActive()) {
            int shieldDamage = shield.takeDamage(damage);
            damage = shieldDamage; // Remaining damage after shield absorbs some
        }
        
        // Apply remaining damage to health
        if (damage > 0 && health.takeDamage(damage)) {
            die();
            return true;
        }
        
        return false;
    }
    
    /**
     * Fire weapon
     */
    public void fireWeapon() {
        if (!isAlive || weapon == null) return;
        weapon.fire();
        score.addShotFired();
    }
    
    /**
     * Register a hit
     */
    public void registerHit() {
        score.addShotHit();
    }
    
    /**
     * Register a kill
     */
    public void registerKill(int points) {
        score.addKill(points);
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getPlayerId() { return playerId; }
    
    public int getTeam() { return team; }
    public void setTeam(int team) { this.team = team; }
    
    public FysixObject getShipObject() { return shipObject; }
    public void setShipObject(FysixObject shipObject) { this.shipObject = shipObject; }
    
    public Health getHealth() { return health; }
    public Score getScore() { return score; }
    
    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    
    public Shield getShield() { return shield; }
    public void setShield(Shield shield) { this.shield = shield; }
    
    public boolean isAlive() { return isAlive; }
    
    public SpawnPoint getLastSpawnPoint() { return lastSpawnPoint; }
    
    public long getRespawnTime() { return respawnTime; }
}

