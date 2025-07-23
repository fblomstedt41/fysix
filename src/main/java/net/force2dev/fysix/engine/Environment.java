package net.force2dev.fysix.engine;

import javax.vecmath.*;

/**
 *
 * @author xnvmakl
 */
public abstract class Environment {
    public abstract Vector2d getEnvironmentAccelerationAtPoint(Point2d p);
    public abstract double getEnvironmentalResistanceAtPoint(Point2d p);
}
