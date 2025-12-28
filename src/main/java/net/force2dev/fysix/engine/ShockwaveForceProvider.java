package net.force2dev.fysix.engine;

/**
 * Interface for providing shockwave forces to physics engine
 * Allows physics engine to apply forces from visual effects without tight coupling
 */
public interface ShockwaveForceProvider {
    /**
     * Get the force (in pixels/sec²) at a given world position
     * @param x World X coordinate
     * @param y World Y coordinate
     * @return Array of [forceX, forceY], or null if no force at this position
     */
    double[] getForce(double x, double y);
}

