package net.force2dev.fysix.ui;

import java.awt.geom.AffineTransform;

/**
 * Helper class for rendering optimizations
 * Reuses AffineTransform objects to reduce allocations
 */
public class RenderHelper {
    private static final ThreadLocal<AffineTransform> transformCache = 
        ThreadLocal.withInitial(() -> new AffineTransform());
    
    /**
     * Get a reusable AffineTransform (reset to identity)
     */
    public static AffineTransform getTransform() {
        AffineTransform t = transformCache.get();
        t.setToIdentity();
        return t;
    }
    
    /**
     * Create a new AffineTransform (for cases where we need to keep it)
     */
    public static AffineTransform createTransform() {
        return new AffineTransform();
    }
}

