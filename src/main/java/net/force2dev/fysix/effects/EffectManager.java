package net.force2dev.fysix.effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import net.force2dev.fysix.engine.ShockwaveForceProvider;

/**
 * Manages all visual effects (particles, screen shake, shockwaves, etc.)
 * Also provides shockwave forces to physics engine
 */
public class EffectManager implements ShockwaveForceProvider {
    private ParticleSystem particleSystem;
    private List<Shockwave> shockwaves;
    private double screenShakeX = 0;
    private double screenShakeY = 0;
    private double screenShakeDecay = 5.0; // How fast shake decays
    
    // Screen shake settings
    private static final double MAX_SHAKE = 10.0;
    
    public EffectManager() {
        this.particleSystem = new ParticleSystem();
        this.shockwaves = new ArrayList<>();
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
     * Create shockwave effect from impact
     * @param x Impact X coordinate
     * @param y Impact Y coordinate
     * @param impactVelocity Velocity magnitude at impact (determines intensity)
     */
    public void createShockwave(double x, double y, double impactVelocity) {
        shockwaves.add(new Shockwave(x, y, impactVelocity));
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
        
        // Update shockwaves and remove expired ones
        Iterator<Shockwave> iter = shockwaves.iterator();
        while (iter.hasNext()) {
            Shockwave wave = iter.next();
            if (!wave.update(deltaTimeSeconds)) {
                iter.remove();
            }
        }
        
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
     * Render all particles and shockwaves
     */
    public void render(Graphics2D g2d, double scaleFactor, double viewX, double viewY) {
        // Save original transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Render particles and shockwaves with world-to-screen transform
        // Reset to identity first, then apply scaling
        AffineTransform worldTransform = new AffineTransform();
        worldTransform.scale(scaleFactor, scaleFactor);
        worldTransform.translate(-viewX / scaleFactor, -viewY / scaleFactor);
        g2d.setTransform(worldTransform);
        
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
        
        // Render shockwaves with shader-graph style smoothstep effect
        for (Shockwave wave : shockwaves) {
            Color waveColor = wave.getColor();
            if (waveColor.getAlpha() < 5) continue; // Skip very transparent waves
            
            double radius = wave.getCurrentRadius();
            double centerX = wave.getX();
            double centerY = wave.getY();
            double maxRadius = wave.getMaxRadius();
            double intensity = wave.getIntensity();
            
            // Draw wave front with smoothstep-like falloff (shader graph style)
            // Create a visible ring at the wave front with smooth transitions
            int numRings = 8; // More rings for smoother shader-like effect
            double waveFrontWidth = 25.0; // Width of visible wave front
            
            for (int i = 0; i < numRings; i++) {
                // Focus rings around the wave front
                double ringOffset = (i - numRings / 2.0) * (waveFrontWidth / numRings);
                double ringRadius = radius + ringOffset;
                
                if (ringRadius < 0 || ringRadius > maxRadius) continue;
                
                // Calculate alpha using smoothstep-like falloff
                // Peak intensity at wave front, smooth falloff on both sides
                double distFromFront = Math.abs(ringOffset);
                double normalizedDist = distFromFront / (waveFrontWidth / 2.0);
                
                // Smoothstep-like function: smooth falloff from center
                double smoothFactor = 1.0 - Math.max(0, Math.min(1, normalizedDist));
                smoothFactor = smoothFactor * smoothFactor * (3.0 - 2.0 * smoothFactor); // Hermite interpolation
                
                // Scale by base color alpha, intensity, and life
                double ringAlpha = waveColor.getAlpha() * smoothFactor * (0.7 + intensity * 0.3);
                ringAlpha = Math.max(0, Math.min(255, ringAlpha));
                
                if (ringAlpha < 3) continue;
                
                Color ringColor = new Color(
                    waveColor.getRed(),
                    waveColor.getGreen(),
                    waveColor.getBlue(),
                    (int)ringAlpha
                );
                g2d.setColor(ringColor);
                
                // Thinner stroke for more subtle, shader-like appearance
                java.awt.Stroke oldStroke = g2d.getStroke();
                g2d.setStroke(new java.awt.BasicStroke(1.2f));
                g2d.drawOval(
                    (int)(centerX - ringRadius),
                    (int)(centerY - ringRadius),
                    (int)(ringRadius * 2),
                    (int)(ringRadius * 2)
                );
                g2d.setStroke(oldStroke);
            }
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
     * Calculate total distortion/displacement at a given world position
     * from all active shockwaves (for visual warp effect)
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Array of [offsetX, offsetY] total displacement, or [0, 0] if none
     */
    public double[] getDistortion(double worldX, double worldY) {
        double totalOffsetX = 0;
        double totalOffsetY = 0;
        int activeWaves = 0;
        
        for (Shockwave wave : shockwaves) {
            double[] distortion = wave.getDistortion(worldX, worldY);
            if (distortion != null) {
                totalOffsetX += distortion[0];
                totalOffsetY += distortion[1];
                activeWaves++;
            }
        }
        
        // Average if multiple waves affect the same point
        if (activeWaves > 0) {
            return new double[] { totalOffsetX, totalOffsetY };
        }
        
        return new double[] { 0, 0 };
    }
    
    /**
     * Get all active shockwaves (for rendering or other purposes)
     */
    public List<Shockwave> getShockwaves() {
        return shockwaves;
    }
    
    /**
     * Calculate total physics force at a given world position from all active shockwaves
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Array of [forceX, forceY] total force in pixels/sec², or [0, 0] if none
     */
    public double[] getForce(double worldX, double worldY) {
        double totalForceX = 0;
        double totalForceY = 0;
        
        for (Shockwave wave : shockwaves) {
            double[] force = wave.getForce(worldX, worldY);
            if (force != null) {
                totalForceX += force[0];
                totalForceY += force[1];
            }
        }
        
        return new double[] { totalForceX, totalForceY };
    }
    
    /**
     * Clear all effects
     */
    public void clear() {
        particleSystem.clear();
        shockwaves.clear();
        screenShakeX = 0;
        screenShakeY = 0;
    }
}

