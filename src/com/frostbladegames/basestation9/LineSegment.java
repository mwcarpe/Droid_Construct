/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;



/** Line Segment class.
* line = x1,z1 StartPoint and x2,z2 EndPoint 
* angle = r
*/
public class LineSegment {
	public float x1;
	public float z1;
	public float x2;
	public float z2;
//	public float r;
	
	public float magnitude;
	
//	public float collisionTestBefore;
	
    // FIXME TEMP ONLY
    public int lineSegmentId;
	
	public LineSegment() {

	}
	
	public LineSegment(float otherX1, float otherZ1, float otherX2, float otherZ2, int id) {
//	public LineSegment(float otherX1, float otherZ1, float otherX2, float otherZ2, float otherR, int id) {
		x1 = otherX1;
		z1 = otherZ1;
		x2 = otherX2;
		z2 = otherZ2;
//		r = otherR;
		
		float otherVx = otherX2 - otherX1;
		float otherVz = otherZ2 - otherZ1;
		magnitude = (float)Math.sqrt((otherVx * otherVx) + (otherVz * otherVz));
		
//		collisionTestBefore = 10.0f;	// Generic high initializer
		
		lineSegmentId = id;
	}
	
	public LineSegment(LineSegment other) {
		x1 = other.x1;
		z1 = other.z1;
		x2 = other.x2;
		z2 = other.z2;
//		r = other.r;
		
		float otherVx = other.x2 - other.x1;
		float otherVz = other.z2 - other.z1;
		magnitude = (float)Math.sqrt((otherVx * otherVx) + (otherVz * otherVz));
		
//		collisionTestBefore = 10.0f;	// Generic high initializer
		
		lineSegmentId = other.lineSegmentId;
	}
	
	public void set(float otherX1, float otherZ1, float otherX2, float otherZ2) {
//	public void set(float otherX1, float otherZ1, float otherX2, float otherZ2, float otherR) {
		x1 = otherX1;
		z1 = otherZ1;
		x2 = otherX2;
		z2 = otherZ2;
//		r = otherR;
		
		float otherVx = otherX2 - otherX1;
		float otherVz = otherZ2 - otherZ1;
		magnitude = (float)Math.sqrt((otherVx * otherVx) + (otherVz * otherVz));
	}
	
	public void set(LineSegment other) {
		x1 = other.x1;
		z1 = other.z1;
		x2 = other.x2;
		z2 = other.z2;
//		r = other.r;
		
		float otherVx = other.x2 - other.x1;
		float otherVz = other.z2 - other.z1;
		magnitude = (float)Math.sqrt((otherVx * otherVx) + (otherVz * otherVz));
		
		lineSegmentId = other.lineSegmentId;
	}
	
    public final float backgroundDistanceStart(Vector3 cameraFocus) {
        float dx1 = x1 - cameraFocus.x;
        float dz1 = z1 - cameraFocus.z;

        return (dx1 * dx1) + (dz1 * dz1);
    }
    
    public final float backgroundDistanceEnd(Vector3 cameraFocus) {
    	float dx2 = x2 - cameraFocus.x;
    	float dz2 = z2 - cameraFocus.z;

        return (dx2 * dx2) + (dz2 * dz2);
    }
    
    // Line Segment Unit Vector x value (Normalized x Vector)
    public float getUnitVectorX() {
    	if (magnitude != 0 ) {
        	return (x2 - x1) / magnitude;
    	} else {
    		return 0.0f;
    	}

//    	return (x2 - x1);
    }
    
    // Line Segment Unit Vector z value (Normalized z Vector)
    public float getUnitVectorZ() {
    	if (magnitude != 0 ) {
        	return (z2 - z1) / magnitude;
    	} else {
    		return 0.0f;
    	}
    	
//    	return (z2 - z1);
    }
    
    public float getLeftNormalX() {
    	return (z2 - z1);
    }
    
    public float getLeftNormalZ() {
    	return -(x2 - x1);
    }
    
    public float getRightNormalX() {
    	return -(z2 - z1);
    }
    
    public float getRightNormalZ() {
    	return (x2 - x1);
    }
    
    public float getLeftUnitNormalX() {
    	if (magnitude != 0 ) {
        	return (z2 - z1) / magnitude;
    	} else {
    		return 0.0f;
    	}
    }
    
//    public float getLeftUnitNormalX(float zOffset) {
//    	if (magnitude != 0 ) {
//        	return (z2 - z1) / magnitude;
//    	} else {
//    		return 0.0f;
//    	}
//    }
    
    public float getLeftUnitNormalZ() {
    	if (magnitude != 0 ) {
        	return -(x2 - x1) / magnitude;
    	} else {
    		return 0.0f;
    	}
    }
    
//    public float getLeftUnitNormalZ(float xOffset) {
//    	if (magnitude != 0 ) {
//        	return -(x2 - x1) / magnitude;
//    	} else {
//    		return 0.0f;
//    	}
//    }
    
    public float getRightUnitNormalX() {
    	if (magnitude != 0 ) {
        	return -(z2 - z1) / magnitude;
    	} else {
    		return 0.0f;
    	}
        	
//    	return -(z2 - z1);
    }
    
    public float getRightUnitNormalZ() {
    	if (magnitude != 0 ) {
        	return (x2 - x1) / magnitude;
    	} else {
    		return 0.0f;
    	}
    	
//    	return (x2 - x1);
    }
    
//    public final float lengthStartPoint() {
//        return (float) Math.sqrt(lengthStartPoint2());
//    }
//
//    public final float lengthStartPoint2() {
//        return (x1 * x1) + (z1 * z1);
//    }
//
//    public final float normalizeStartPoint() {
//        final float magnitude = lengthStartPoint();
//
//        // XXX: I'm choosing safety over speed here.
//        if (magnitude != 0.0f) {
//            x1 /= magnitude;
////            y /= magnitude;
//            z1 /= magnitude;
//        }
//        return magnitude;
//    }
}
