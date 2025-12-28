package net.force2dev.fysix.effects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Manages a collection of particles for visual effects
 */
public class ParticleSystem {
    private List<Particle> particles;
    private Random random;
    
    public ParticleSystem() {
        this.particles = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Create explosion effect at position
     */
    public void createExplosion(double x, double y, Color baseColor, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            // Random angle
            double angle = random.nextDouble() * 2.0 * Math.PI;
            // Random velocity (faster particles)
            double speed = 50 + random.nextDouble() * 150;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            // Vary color slightly
            Color color = varyColor(baseColor, 50);
            
            // Random size
            double size = 2 + random.nextDouble() * 4;
            
            // Random lifetime (0.3 to 0.8 seconds)
            double lifetime = 0.3 + random.nextDouble() * 0.5;
            
            particles.add(new Particle(x, y, vx, vy, color, size, lifetime));
        }
    }
    
    /**
     * Create engine exhaust/trail effect
     */
    public void createEngineTrail(double x, double y, double angle, double speed, Color color) {
        // Create particles behind the ship (opposite direction of movement)
        double oppositeAngle = angle + Math.PI;
        
        // Random spread around the exhaust direction
        double spread = 0.3; // radians
        double particleAngle = oppositeAngle + (random.nextDouble() - 0.5) * spread;
        
        // Velocity opposite to ship movement, with some randomness
        double particleSpeed = speed * 0.3 + random.nextDouble() * 20;
        double vx = Math.cos(particleAngle) * particleSpeed;
        double vy = Math.sin(particleAngle) * particleSpeed;
        
        // Small particles for engine trail - make them more visible
        double size = 2 + random.nextDouble() * 3; // Slightly larger (2-5 pixels)
        double lifetime = 0.3 + random.nextDouble() * 0.4; // Longer lived (0.3-0.7 seconds)
        
        particles.add(new Particle(x, y, vx, vy, color, size, lifetime));
    }
    
    /**
     * Create hit spark effect
     */
    public void createHitSparks(double x, double y, int sparkCount) {
        for (int i = 0; i < sparkCount; i++) {
            double angle = random.nextDouble() * 2.0 * Math.PI;
            double speed = 30 + random.nextDouble() * 50;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            
            // Yellow/orange sparks
            Color sparkColor = new Color(255, 200 + random.nextInt(55), 0);
            
            double size = 1 + random.nextDouble() * 2;
            double lifetime = 0.1 + random.nextDouble() * 0.2; // Very short lived
            
            particles.add(new Particle(x, y, vx, vy, sparkColor, size, lifetime));
        }
    }
    
    /**
     * Update all particles
     * @param deltaTimeSeconds Time since last update
     */
    public void update(double deltaTimeSeconds) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            if (!p.update(deltaTimeSeconds)) {
                iter.remove(); // Remove dead particles
            }
        }
    }
    
    /**
     * Get all active particles
     */
    public List<Particle> getParticles() {
        return particles;
    }
    
    /**
     * Clear all particles
     */
    public void clear() {
        particles.clear();
    }
    
    /**
     * Vary a color randomly
     */
    private Color varyColor(Color base, int variation) {
        int r = Math.max(0, Math.min(255, base.getRed() + random.nextInt(variation * 2) - variation));
        int g = Math.max(0, Math.min(255, base.getGreen() + random.nextInt(variation * 2) - variation));
        int b = Math.max(0, Math.min(255, base.getBlue() + random.nextInt(variation * 2) - variation));
        return new Color(r, g, b);
    }
}

