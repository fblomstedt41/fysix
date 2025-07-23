package net.force2dev.fysix.engine;

import javax.vecmath.*;

/**
 *
 * @author xnvmakl
 */
public interface IControllable {

    public Point2d getPosition();
    
    public void setMass(double m);
    
    public Vector2d getDirection();

    public void setDirection(Vector2d dir);

    public Vector2d getAcceleration();

    public void setAcceleration(Vector2d acc);

    public void fireEvent(FysixEvent ev);
}
