package net.force2dev.fysix.effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;

import net.force2dev.fysix.ui.RenderHelper;

/**
 * Manages all visual effects (particles, screen shake, etc.)
 */
public class EffectManager {
    private ParticleSystem particleSystem;
    private double screenShakeX = 0;
    private double screenShakeY = 0;
    private double screenShakeDecay = 5.0; // How fast shake decays
    
    // Screen shake settings
    private static final double MAX_SHAKE = 10.0;
    
    public EffectManager() {
        this.particleSystem = new ParticleSystem();
    }
    
    /**
     * Create explosion effect
     */
    public void createExplosion(double x, double y, Color color) {
        particleSystem.createExplosion(x, y, color, 30);
        addScreenShake(0.15); // Small shake for explosion
    }
    
    /**
     * Create large explosion (for player death)
     */
    public void createLargeExplosion(double x, double y) {
        // Multi-colored explosion
        particleSystem.createExplosion(x, y, Color.RED, 20);
        particleSystem.createExplosion(x, y, Color.ORANGE, 15);
        particleSystem.createExplosion(x, y, Color.YELLOW, 10);
        addScreenShake(0.3); // Larger shake
    }
    
    /**
     * Create engine trail effect
     */
    public void createEngineTrail(double x, double y, double angle, double speed) {
        // Bright blue/cyan trail for engine - make it more visible
        Color trailColor = new Color(100, 200, 255, 200); // Brighter and more opaque
        particleSystem.createEngineTrail(x, y, angle, speed, trailColor);
    }
    
    /**
     * Create hit sparks
     */
    public void createHitSparks(double x, double y) {
        particleSystem.createHitSparks(x, y, 8);
    }
    
    /**
     * Add screen shake
     * @param intensity 0.0 to 1.0
     */
    public void addScreenShake(double intensity) {
        screenShakeX += (Math.random() - 0.5) * MAX_SHAKE * intensity;
        screenShakeY += (Math.random() - 0.5) * MAX_SHAKE * intensity;
        
        // Clamp shake
        if (screenShakeX > MAX_SHAKE) screenShakeX = MAX_SHAKE;
        if (screenShakeX < -MAX_SHAKE) screenShakeX = -MAX_SHAKE;
        if (screenShakeY > MAX_SHAKE) screenShakeY = MAX_SHAKE;
        if (screenShakeY < -MAX_SHAKE) screenShakeY = -MAX_SHAKE;
    }
    
    /**
     * Update all effects
     * @param deltaTimeSeconds Time since last update
     */
    public void update(double deltaTimeSeconds) {
        particleSystem.update(deltaTimeSeconds);
        
        // Decay screen shake
        if (screenShakeX != 0 || screenShakeY != 0) {
            screenShakeX *= (1.0 - screenShakeDecay * deltaTimeSeconds);
            screenShakeY *= (1.0 - screenShakeDecay * deltaTimeSeconds);
            
            // Snap to zero if very small
            if (Math.abs(screenShakeX) < 0.1) screenShakeX = 0;
            if (Math.abs(screenShakeY) < 0.1) screenShakeY = 0;
        }
    }
    
    /**
     * Render all particles
     */
    public void render(Graphics2D g2d, double scaleFactor, double viewX, double viewY) {
        // Save original transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Render particles with world-to-screen transform
        // Reset to identity first, then apply scaling
        AffineTransform particleTransform = new AffineTransform();
        particleTransform.scale(scaleFactor, scaleFactor);
        particleTransform.translate(-viewX / scaleFactor, -viewY / scaleFactor);
        g2d.setTransform(particleTransform);
        
        // Render particles
        for (Particle p : particleSystem.getParticles()) {
            double alpha = p.getLife(); // Fade as life decreases
            
            // Particle position is in world coordinates
            double worldX = p.getX();
            double worldY = p.getY();
            
            // Apply alpha to color
            Color c = p.getColor();
            int alphaInt = (int)(alpha * 255);
            if (alphaInt < 1) continue; // Skip fully transparent particles
            
            // Create color with alpha
            Color particleColor;
            if (c.getAlpha() < 255) {
                // Color already has alpha, combine with life alpha
                particleColor = new Color(
                    c.getRed(),
                    c.getGreen(),
                    c.getBlue(),
                    Math.min(255, (int)(c.getAlpha() * alpha))
                );
            } else {
                particleColor = new Color(
                    c.getRed(),
                    c.getGreen(),
                    c.getBlue(),
                    alphaInt
                );
            }
            
            g2d.setColor(particleColor);
            double size = p.getSize();
            
            // Draw particle - position is already in world coordinates
            g2d.fillOval(
                (int)(worldX - size / 2),
                (int)(worldY - size / 2),
                (int)Math.max(1, size),
                (int)Math.max(1, size)
            );
        }
        
        // Restore original transform
        g2d.setTransform(originalTransform);
    }
    
    /**
     * Get screen shake offset for camera
     */
    public Point2d getScreenShakeOffset() {
        return new Point2d(screenShakeX, screenShakeY);
    }
    
    /**
     * Clear all effects
     */
    public void clear() {
        particleSystem.clear();
        screenShakeX = 0;
        screenShakeY = 0;
    }
}

