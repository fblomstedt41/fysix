package net.force2dev.fysix.equipment;

/**
 * Represents a weapon that can fire projectiles
 */
public class Weapon {
    public enum WeaponType {
        CANNON,      // Basic projectile
        SPREAD,      // Multiple projectiles
        LASER,       // Instant hit
        MISSILE,     // Homing missile
        ROCKET       // Explosive rocket
    }
    
    private WeaponType type;
    private int damage;
    private double projectileSpeed;
    private long fireRate; // milliseconds between shots
    private long lastFireTime;
    private int ammo; // -1 for infinite
    private int maxAmmo;
    private boolean canFire;
    
    public Weapon(WeaponType type, int damage, double projectileSpeed, long fireRate, int ammo) {
        this.type = type;
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.fireRate = fireRate;
        this.lastFireTime = 0;
        this.ammo = ammo;
        this.maxAmmo = ammo;
        this.canFire = true;
    }
    
    /**
     * Create a default cannon weapon
     */
    public static Weapon createDefaultCannon() {
        return new Weapon(WeaponType.CANNON, 10, 450.0, 100, -1); // Infinite ammo
    }
    
    /**
     * Attempt to fire the weapon
     * @return true if weapon fired successfully
     */
    public boolean fire() {
        if (!canFire) return false;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime < fireRate) {
            return false; // Still on cooldown
        }
        
        if (ammo == 0) {
            return false; // Out of ammo
        }
        
        lastFireTime = currentTime;
        if (ammo > 0) {
            ammo--;
        }
        
        return true;
    }
    
    /**
     * Check if weapon can fire now
     */
    public boolean canFire() {
        if (!canFire) return false;
        if (ammo == 0) return false;
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastFireTime) >= fireRate;
    }
    
    /**
     * Reload ammo
     */
    public void reload(int amount) {
        if (maxAmmo > 0) {
            ammo = Math.min(ammo + amount, maxAmmo);
        }
    }
    
    /**
     * Set ammo to max
     */
    public void reloadFull() {
        if (maxAmmo > 0) {
            ammo = maxAmmo;
        }
    }
    
    // Getters and setters
    public WeaponType getType() { return type; }
    public void setType(WeaponType type) { this.type = type; }
    
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    
    public double getProjectileSpeed() { return projectileSpeed; }
    public void setProjectileSpeed(double projectileSpeed) { this.projectileSpeed = projectileSpeed; }
    
    public long getFireRate() { return fireRate; }
    public void setFireRate(long fireRate) { this.fireRate = fireRate; }
    
    public int getAmmo() { return ammo; }
    public void setAmmo(int ammo) { this.ammo = ammo; }
    
    public int getMaxAmmo() { return maxAmmo; }
    public void setMaxAmmo(int maxAmmo) { 
        this.maxAmmo = maxAmmo;
        if (ammo > maxAmmo) {
            ammo = maxAmmo;
        }
    }
    
    public boolean isCanFire() { return canFire; }
    public void setCanFire(boolean canFire) { this.canFire = canFire; }
    
    public double getAmmoPercentage() {
        if (maxAmmo <= 0) return 1.0; // Infinite ammo
        if (maxAmmo == 0) return 0.0;
        return (double) ammo / (double) maxAmmo;
    }
}
