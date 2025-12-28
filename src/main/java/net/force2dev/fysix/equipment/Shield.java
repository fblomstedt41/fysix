package net.force2dev.fysix.equipment;

/**
 * Represents a shield that protects the ship
 */
public class Shield {
    private int maxShield;
    private int currentShield;
    private double rechargeRate; // shield points per second
    private long lastRechargeTime;
    private long rechargeDelay; // milliseconds before recharge starts after taking damage
    
    public Shield(int maxShield, double rechargeRate, long rechargeDelay) {
        this.maxShield = maxShield;
        this.currentShield = maxShield;
        this.rechargeRate = rechargeRate;
        this.rechargeDelay = rechargeDelay;
        this.lastRechargeTime = System.currentTimeMillis();
    }
    
    /**
     * Create a default shield
     */
    public static Shield createDefaultShield() {
        return new Shield(50, 5.0, 2000); // 50 shield, 5 per second, 2 second delay
    }
    
    /**
     * Take damage to the shield
     * @param damage Amount of damage
     * @return Remaining damage after shield absorbs (0 if all absorbed)
     */
    public int takeDamage(int damage) {
        if (currentShield <= 0) {
            return damage; // No shield left, all damage goes through
        }
        
        int absorbed = Math.min(damage, currentShield);
        currentShield -= absorbed;
        lastRechargeTime = System.currentTimeMillis(); // Reset recharge timer
        
        return damage - absorbed; // Return remaining damage
    }
    
    /**
     * Update shield recharge (call this every frame/tick)
     */
    public void update(long deltaTimeMs) {
        if (currentShield >= maxShield) {
            return; // Already at max
        }
        
        long timeSinceLastDamage = System.currentTimeMillis() - lastRechargeTime;
        if (timeSinceLastDamage < rechargeDelay) {
            return; // Still in recharge delay
        }
        
        // Recharge based on recharge rate
        double rechargeAmount = (rechargeRate * deltaTimeMs) / 1000.0;
        currentShield = Math.min(maxShield, (int) (currentShield + rechargeAmount));
    }
    
    /**
     * Check if shield is active (has any shield points)
     */
    public boolean isActive() {
        return currentShield > 0;
    }
    
    /**
     * Get shield percentage
     */
    public double getShieldPercentage() {
        if (maxShield == 0) return 0.0;
        return (double) currentShield / (double) maxShield;
    }
    
    /**
     * Fully recharge shield
     */
    public void rechargeFull() {
        currentShield = maxShield;
    }
    
    // Getters and setters
    public int getMaxShield() { return maxShield; }
    public void setMaxShield(int maxShield) { 
        this.maxShield = maxShield;
        if (currentShield > maxShield) {
            currentShield = maxShield;
        }
    }
    
    public int getCurrentShield() { return currentShield; }
    
    public double getRechargeRate() { return rechargeRate; }
    public void setRechargeRate(double rechargeRate) { this.rechargeRate = rechargeRate; }
    
    public long getRechargeDelay() { return rechargeDelay; }
    public void setRechargeDelay(long rechargeDelay) { this.rechargeDelay = rechargeDelay; }
}
