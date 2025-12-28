package net.force2dev.fysix.game;

/**
 * Represents player score and statistics
 */
public class Score {
    private int score;
    private int kills;
    private int deaths;
    private int shotsFired;
    private int shotsHit;
    
    public Score() {
        this.score = 0;
        this.kills = 0;
        this.deaths = 0;
        this.shotsFired = 0;
        this.shotsHit = 0;
    }
    
    /**
     * Add points to score
     */
    public void addScore(int points) {
        this.score += points;
    }
    
    /**
     * Register a kill
     */
    public void addKill(int points) {
        this.kills++;
        addScore(points);
    }
    
    /**
     * Register a death
     */
    public void addDeath() {
        this.deaths++;
    }
    
    /**
     * Register a shot fired
     */
    public void addShotFired() {
        this.shotsFired++;
    }
    
    /**
     * Register a shot hit
     */
    public void addShotHit() {
        this.shotsHit++;
    }
    
    /**
     * Get accuracy percentage
     */
    public double getAccuracy() {
        if (shotsFired == 0) return 0.0;
        return (double) shotsHit / (double) shotsFired * 100.0;
    }
    
    /**
     * Get kill/death ratio
     */
    public double getKDRatio() {
        if (deaths == 0) return kills;
        return (double) kills / (double) deaths;
    }
    
    /**
     * Reset all statistics
     */
    public void reset() {
        this.score = 0;
        this.kills = 0;
        this.deaths = 0;
        this.shotsFired = 0;
        this.shotsHit = 0;
    }
    
    // Getters
    public int getScore() { return score; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getShotsFired() { return shotsFired; }
    public int getShotsHit() { return shotsHit; }
}

