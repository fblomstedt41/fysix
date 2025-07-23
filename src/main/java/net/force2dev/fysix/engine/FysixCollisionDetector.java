package net.force2dev.fysix.engine;

import java.awt.Polygon;

import javax.vecmath.Vector2d;

public class FysixCollisionDetector {

	public static boolean checkCollision(FysixObject fo1, FysixObject fo2) {
		Vector2d relVel = new Vector2d();
		relVel.sub(fo1.getVelocity(), fo2.getVelocity());
		Polygon p1 = fo1.getBoundingArea();
		p1.translate((int)fo1.getPosition().x, (int)fo1.getPosition().y);
		Polygon p2 = fo2.getBoundingArea();
		p2.translate((int)fo2.getPosition().x, (int)fo2.getPosition().y);
		PolygonCollisionResult res = PolygonCollision(p1, p2, relVel);
		p1.translate((int)-fo1.getPosition().x, (int)-fo1.getPosition().y);
		p2.translate((int)-fo2.getPosition().x, (int)-fo2.getPosition().y);
		return (res.doesIntersect); // || res.willIntersect);
	}

	private static class ProjectionResult {
		double min;
		double max;
	}

	private static ProjectionResult ProjectPolygon(Vector2d axis, Polygon polygon) {
		ProjectionResult res = new ProjectionResult();
		Vector2d projVec = new Vector2d(polygon.xpoints[0], polygon.ypoints[0]);
		double dotProduct = axis.dot(projVec);
		res.max = dotProduct;
		res.min = dotProduct;

		for (int i = 0; i < polygon.npoints; i++) {
			projVec.x = polygon.xpoints[i];
			projVec.y = polygon.ypoints[i];
			dotProduct = axis.dot(projVec);
			if (dotProduct < res.min) {
				res.min = dotProduct;
			} else if (dotProduct > res.max) {
				res.max = dotProduct;
			}
		}

		return res;
	}
	
	private static double IntervalDistance(ProjectionResult resA, ProjectionResult resB) {
	    if (resA.min < resB.min) {
	        return resB.min - resA.max;
	    } else {
	        return resA.min - resB.max;
	    }
	}

	private static class PolygonCollisionResult {
	    boolean willIntersect;
	    boolean doesIntersect;
	    //Vector2d minimumTranslationVector;
	}
	
	// Check if polygon A is going to collide with polygon B.
	// The last parameter is the *relative* velocity 
	// of the polygons (i.e. velocityA - velocityB)
	private static PolygonCollisionResult PolygonCollision(Polygon polygonA, 
	                              Polygon polygonB, Vector2d velocity) {
	    PolygonCollisionResult result = new PolygonCollisionResult();
	    result.doesIntersect = true;
	    result.willIntersect = true;

	    int edgeCountA = polygonA.npoints;
	    int edgeCountB = polygonB.npoints;
	    //double minIntervalDistance = Double.MAX_VALUE;
	    //Vector2d translationAxis = new Vector2d();
	    Vector2d edge = new Vector2d();

	    // Loop through all the edges of both polygons
	    for (int edgeIndex = 1; edgeIndex <= edgeCountA + edgeCountB; edgeIndex++) {
	        if (edgeIndex <= edgeCountA) {
		    	int wrapIdx = edgeIndex % edgeCountA;
	            edge.x = polygonA.xpoints[wrapIdx] - polygonA.xpoints[edgeIndex-1];
	            edge.y = polygonA.ypoints[wrapIdx] - polygonA.ypoints[edgeIndex-1];
	        } else {
		    	int wrapIdx = (edgeIndex - edgeCountA) % edgeCountB;
	            edge.x = polygonB.xpoints[wrapIdx] - polygonB.xpoints[edgeIndex - edgeCountA - 1];
	            edge.y = polygonB.ypoints[wrapIdx] - polygonB.ypoints[edgeIndex - edgeCountA - 1];
	        }

	        // ===== 1. Find if the polygons are currently intersecting =====

	        // Find the axis perpendicular to the current edge
	        Vector2d axis = new Vector2d(-edge.y, edge.x);
	        axis.normalize();

	        // Find the projection of the polygon on the current axis
	        //float minA = 0; float minB = 0; float maxA = 0; float maxB = 0;
	        ProjectionResult resA = ProjectPolygon(axis, polygonA);
	        ProjectionResult resB = ProjectPolygon(axis, polygonB);

	        // Check if the polygon projections are currentlty intersecting
	        if (IntervalDistance(resA, resB) > 0)
	            result.doesIntersect = false;

	        // ===== 2. Now find if the polygons *will* intersect =====

	        // Project the velocity on the current axis
	        double velocityProjection = axis.dot(velocity);

	        // Get the projection of polygon A during the movement
	        if (velocityProjection < 0) {
	            resA.min += velocityProjection;
	        } else {
	            resA.max += velocityProjection;
	        }

	        // Do the same test as above for the new projection
	        double intervalDistance = IntervalDistance(resA, resB);
	        if (intervalDistance > 0) result.willIntersect = false;

	        // If the polygons are not intersecting and won't intersect, exit the loop
	        if (!result.doesIntersect && !result.willIntersect) break;

	        // Check if the current interval distance is the minimum one. If so store
	        // the interval distance and the current distance.
	        // This will be used to calculate the minimum translation vector
//	        intervalDistance = Math.abs(intervalDistance);
//	        if (intervalDistance < minIntervalDistance) {
//	            minIntervalDistance = intervalDistance;
//	            translationAxis = axis;
//
//	            Vector2d d = polygonA.Center - polygonB.Center;
//	            if (d.DotProduct(translationAxis) < 0)
//	                translationAxis = -translationAxis;
//	        }
	    }

	    // The minimum translation vector
	    // can be used to push the polygons appart.
//	    if (result.WillIntersect)
//	        result.MinimumTranslationVector = 
//	               translationAxis * minIntervalDistance;
	    
	    return result;
	}
}
