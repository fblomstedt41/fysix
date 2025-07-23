package net.force2dev.fysix.engine;

import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Point2d;

public class FysixWorld {

	public Vector objects = new Vector(100);
	
	public void addObject(FysixObject fo) {
		objects.add(fo);
	}
		
	public void removeObject(FysixObject fo) {
		objects.remove(fo);
	}
	
	public void updateObject(FysixObject fo) {
		// TBD
	}
	
	public Iterator getAllObjects() {
		return objects.iterator();
	}

	public Iterator getNearObjects(Point2d p) {
		return objects.iterator();
	}
}
