package net.force2dev.fysix.engine;

import java.awt.Color;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 *
 * @author xnvmakl
 */
public class FysixObject implements IControllable {

    private Vector2d direction = new Vector2d(0, 0);
    private Vector2d acceleration = new Vector2d(0, 0);
    private Point2d position = new Point2d(0, 0);
    private Vector2d velocity = new Vector2d(0, 0);
    private double mass = 0.0;
    private FysixObject parent = null;
	private Vector childObjects = new Vector();
    private Polygon boundingArea = new Polygon();
	private Vector callbacks = new Vector();
    private boolean visible;
    
    public Color color = Color.GREEN;

    public Vector2d getDirection() {
        return direction;
    }

    public void setDirection(Vector2d dir) {
        direction = dir;
    }
    
    public void setDirection(double angle) {
    	Matrix3d mat = new Matrix3d();
    	mat.rotZ(angle);
    	Vector3d tmp = new Vector3d(1, 0, 0);
    	mat.transform(tmp);
    	direction = new Vector2d(tmp.x, tmp.y);
    }

    public Vector2d getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2d acc) {
        acceleration = acc;
    }

    public Point2d getPosition() {
        return position;
    }

    public void setPosition(Point2d Position) {
        this.position = Position;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2d Velocity) {
        this.velocity = Velocity;
    }

    public double getMass() {
        return mass;
    }
    
    public void setMass(double m) {
        mass = m;
    }

    public FysixObject setParent() {
        return parent;
    }

    public void getParent(FysixObject Parent) {
        this.parent = Parent;
    }

	public Vector getChildObjects() {
        return childObjects;
    }

	public void addChildObject(FysixObject obj) {
        childObjects.add(obj);
    }

    public void removeChildObject(FysixObject obj) {
        childObjects.remove(obj);
    }

	public void registerEventCallback(IEventCallback cb) {
        if (cb != null) {
            callbacks.add(cb);
        }
    }

	public void fireEvent(FysixEvent ev) {
        for (Iterator i = callbacks.iterator(); i.hasNext();) {
            IEventCallback cb = (IEventCallback) i.next();
            cb.eventFired(ev);
        }
    }

    public Polygon getBoundingArea() {
        return boundingArea;
    }

    public void setBoundingArea(Polygon BoundingArea) {
        this.boundingArea = BoundingArea;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
