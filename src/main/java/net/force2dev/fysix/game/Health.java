package net.force2dev.fysix.game;

/**
 * Represents health and damage system for game objects
 */
public class Health {
    private int maxHealth;
    private int currentHealth;
    private boolean isDead;
    private long lastDamageTime; // For invulnerability frames
    
    public Health(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.isDead = false;
        this.lastDamageTime = 0;
    }
    
    /**
     * Apply damage to this health object
     * @param damage Amount of damage to apply
     * @return true if the object died from this damage
     */
    public boolean takeDamage(int damage) {
        if (isDead) return false;
        
        currentHealth -= damage;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
        
        lastDamageTime = System.currentTimeMillis();
        
        if (currentHealth <= 0) {
            isDead = true;
            return true;
        }
        
        return false;
    }
    
    /**
     * Heal this health object
     */
    public void heal(int amount) {
        if (isDead) return;
        
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }
    
    /**
     * Reset health to full
     */
    public void reset() {
        currentHealth = maxHealth;
        isDead = false;
        lastDamageTime = 0;
    }
    
    /**
     * Check if this object is invulnerable (just took damage)
     * @param invulnerabilityDurationMs Duration of invulnerability in milliseconds
     * @return true if currently invulnerable
     */
    public boolean isInvulnerable(long invulnerabilityDurationMs) {
        if (lastDamageTime == 0) return false;
        return (System.currentTimeMillis() - lastDamageTime) < invulnerabilityDurationMs;
    }
    
    // Getters and setters
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { 
        this.maxHealth = maxHealth;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }
    
    public int getCurrentHealth() { return currentHealth; }
    
    public boolean isDead() { return isDead; }
    
    public double getHealthPercentage() {
        if (maxHealth == 0) return 0.0;
        return (double) currentHealth / (double) maxHealth;
    }
}

