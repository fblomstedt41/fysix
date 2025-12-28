package net.force2dev.fysix.effects;

import java.awt.Color;

/**
 * Represents a shockwave effect that expands from an impact point
 * Used for visual feedback when objects collide with walls
 */
public class Shockwave {
    private double x;
    private double y;
    private double currentRadius;
    private double maxRadius;
    private double speed; // Expansion speed
    private double life;
    private double maxLife;
    private double intensity; // 0.0 to 1.0, based on impact velocity
    private Color color;
    
    // Visual properties
    private static final Color BASE_COLOR = new Color(100, 150, 255, 120); // Light blue with more transparency
    private static final double MIN_DURATION = 2.0; // seconds
    private static final double MAX_DURATION = 4.0; // seconds
    private static final double MIN_MAX_RADIUS = 50.0; // pixels
    private static final double MAX_MAX_RADIUS = 300.0; // pixels
    private static final double BASE_EXPANSION_SPEED = 80.0; // pixels per second
    private static final double DISTORTION_MAX_RANGE = 400.0; // Max distance for distortion effect
    private static final double MAX_DISTORTION_OFFSET = 8.0; // Maximum pixel displacement
    private static final double PHYSICS_MAX_RANGE = 500.0; // Max distance for physics effect
    private static final double MAX_PHYSICS_FORCE = 200.0; // Maximum force applied (pixels/sec²)
    private static final double WAVE_FRONT_WIDTH = 25.0; // Width of the wave front (for smoothstep)
    
    /**
     * Create a shockwave at the impact point
     * @param x Impact X coordinate
     * @param y Impact Y coordinate
     * @param impactVelocity Velocity magnitude at impact (determines intensity and size)
     */
    public Shockwave(double x, double y, double impactVelocity) {
        this.x = x;
        this.y = y;
        this.currentRadius = 0;
        
        // Intensity based on velocity (normalize to 0-1 range)
        // Assume typical collision velocity is 0-400 pixels/second
        this.intensity = Math.min(1.0, impactVelocity / 400.0);
        
        // Duration based on intensity (stronger impacts last longer)
        this.maxLife = MIN_DURATION + (MAX_DURATION - MIN_DURATION) * intensity;
        this.life = maxLife;
        
        // Max radius based on intensity (stronger impacts spread further)
        this.maxRadius = MIN_MAX_RADIUS + (MAX_MAX_RADIUS - MIN_MAX_RADIUS) * intensity;
        
        // Expansion speed increases with intensity
        this.speed = BASE_EXPANSION_SPEED * (1.0 + intensity * 0.5);
        
        // Color intensity based on impact (more transparent for subtler effect)
        int alpha = (int)(120 * (0.4 + intensity * 0.6)); // More transparent overall
        int blue = (int)(150 + intensity * 50); // More blue for stronger impacts
        this.color = new Color(BASE_COLOR.getRed(), BASE_COLOR.getGreen(), 
                              Math.min(255, blue), alpha);
    }
    
    /**
     * Update the shockwave
     * @param deltaTimeSeconds Time since last update
     * @return true if still alive, false if expired
     */
    public boolean update(double deltaTimeSeconds) {
        // Expand the radius
        currentRadius += speed * deltaTimeSeconds;
        
        // Clamp to max radius
        if (currentRadius > maxRadius) {
            currentRadius = maxRadius;
        }
        
        // Decrease life
        life -= deltaTimeSeconds;
        
        return life > 0;
    }
    
    /**
     * Get current radius
     */
    public double getCurrentRadius() {
        return currentRadius;
    }
    
    /**
     * Get max radius
     */
    public double getMaxRadius() {
        return maxRadius;
    }
    
    /**
     * Get X position
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get Y position
     */
    public double getY() {
        return y;
    }
    
    /**
     * Get color with current alpha based on remaining life
     */
    public Color getColor() {
        double lifePercent = life / maxLife;
        // Fade out as life decreases
        int alpha = (int)(color.getAlpha() * lifePercent);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    /**
     * Get intensity (0.0 to 1.0)
     */
    public double getIntensity() {
        return intensity;
    }
    
    /**
     * Get life remaining (0.0 to maxLife)
     */
    public double getLife() {
        return life;
    }
    
    /**
     * Smoothstep function (like in shader graphs) for smooth transitions
     * Returns 0 for x <= edge0, 1 for x >= edge1, and smooth interpolation in between
     */
    private double smoothstep(double edge0, double edge1, double x) {
        double t = Math.max(0.0, Math.min(1.0, (x - edge0) / (edge1 - edge0)));
        return t * t * (3.0 - 2.0 * t); // Hermite interpolation
    }
    
    /**
     * Calculate wave strength at a given distance using smoothstep (like shader graph)
     * Creates a smooth ramp up and down around the wave front
     * @param distance Distance from wave center
     * @return Strength value 0.0 to 1.0
     */
    public double getWaveStrength(double distance) {
        // Only affect objects near the wave front
        double waveFrontStart = currentRadius - WAVE_FRONT_WIDTH;
        double waveFrontEnd = currentRadius + WAVE_FRONT_WIDTH;
        
        if (distance < waveFrontStart || distance > waveFrontEnd) {
            return 0.0;
        }
        
        // Use smoothstep for smooth ramp up and down (like shader graph)
        double rampUp = smoothstep(waveFrontStart, currentRadius, distance);
        double rampDown = 1.0 - smoothstep(currentRadius, waveFrontEnd, distance);
        
        // Combine for peak at wave front, smooth falloff on both sides
        return rampUp * rampDown;
    }
    
    /**
     * Calculate distortion/displacement at a given point
     * Creates a "ripple" effect with smoothstep-like transitions (shader graph style)
     * @param pointX X coordinate to check
     * @param pointY Y coordinate to check
     * @return Array of [offsetX, offsetY] displacement, or null if out of range
     */
    public double[] getDistortion(double pointX, double pointY) {
        double dx = pointX - x;
        double dy = pointY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Only affect objects within distortion range
        if (distance > DISTORTION_MAX_RANGE || distance < currentRadius - WAVE_FRONT_WIDTH * 2) {
            return null; // Too far or hasn't reached yet
        }
        
        // Calculate wave strength using smoothstep
        double waveStrength = getWaveStrength(distance);
        if (waveStrength <= 0.0) {
            return null;
        }
        
        // Scale by intensity and life
        double distortionStrength = waveStrength * intensity * (life / maxLife);
        
        // Normalize direction from wave center to point
        if (distance < 0.1) {
            return null; // At center, no direction
        }
        
        double dirX = dx / distance;
        double dirY = dy / distance;
        
        // Displace perpendicular to the wave (tangential direction for ripple effect)
        // Rotate 90 degrees: (x, y) -> (-y, x) for tangential motion
        double perpX = -dirY;
        double perpY = dirX;
        
        // Calculate offset magnitude with smooth falloff
        double offsetMagnitude = MAX_DISTORTION_OFFSET * distortionStrength;
        
        // Radial component for "push outward" effect before wave front
        double radialOffset = 0;
        if (distance < currentRadius) {
            // Inside wave front - slight pull inward
            radialOffset = (currentRadius - distance) / 100.0;
        } else {
            // Ahead of wave front - push outward
            radialOffset = -(distance - currentRadius) / 150.0;
        }
        
        return new double[] {
            perpX * offsetMagnitude + dirX * radialOffset * distortionStrength * 2.0,
            perpY * offsetMagnitude + dirY * radialOffset * distortionStrength * 2.0
        };
    }
    
    /**
     * Calculate physics force at a given point
     * Pushes objects away from the wave center when the wave passes
     * @param pointX X coordinate to check
     * @param pointY Y coordinate to check
     * @return Array of [forceX, forceY] in pixels/sec², or null if out of range
     */
    public double[] getForce(double pointX, double pointY) {
        double dx = pointX - x;
        double dy = pointY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Only affect objects within physics range
        if (distance > PHYSICS_MAX_RANGE || distance < currentRadius - WAVE_FRONT_WIDTH * 3) {
            return null; // Too far or hasn't reached yet
        }
        
        // Calculate wave strength using smoothstep
        double waveStrength = getWaveStrength(distance);
        if (waveStrength <= 0.0) {
            return null;
        }
        
        // Scale force by intensity and life (fade out as wave dies)
        double forceStrength = waveStrength * intensity * (life / maxLife);
        
        // Normalize direction from wave center to point (push outward)
        if (distance < 0.1) {
            return null; // At center, no direction
        }
        
        double dirX = dx / distance;
        double dirY = dy / distance;
        
        // Calculate force magnitude (stronger for more intense impacts)
        double forceMagnitude = MAX_PHYSICS_FORCE * forceStrength;
        
        return new double[] {
            dirX * forceMagnitude,
            dirY * forceMagnitude
        };
    }
}

