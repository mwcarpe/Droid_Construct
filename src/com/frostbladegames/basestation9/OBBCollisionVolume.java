/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;

import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;

/**
 * An Oriented Bounding Box (OBB) Rectangular Collision Volume
 */
public class OBBCollisionVolume extends AllocationGuard {
    private static final float PI_OVER_180 = 0.0174532925f;
    
    // FIXME TEMP. Test and optimize game speed for mRotatedRectangle vs mMovedRectangle.  Performance vs Accuracy.
    private CollisionRectangle mRectangle;
    private CollisionRectangle mRotatedRectangle;
    private CollisionRectangle mMovedRectangle;
    
//    public HitType hitType;
    
    public OBBCollisionVolume() {		
    	mRectangle = new CollisionRectangle();
		mRotatedRectangle = new CollisionRectangle();
		mMovedRectangle = new CollisionRectangle();
    	
//        hitType = HitType.NO_HIT;
    }
    
    public OBBCollisionVolume(float width, float depth) {    	
    	mRectangle = new CollisionRectangle();
		mRotatedRectangle = new CollisionRectangle();
		mMovedRectangle = new CollisionRectangle();

    	float halfWidth = width * 0.5f;
		float halfDepth = depth * 0.5f;
//    	float halfWidth = width / 2.0f;
//		float halfDepth = depth / 2.0f;
    	
		setRectanglePoint(0, -halfWidth, -halfDepth);
		setRectanglePoint(1, -halfWidth, halfDepth);
		setRectanglePoint(2, halfWidth, halfDepth);
		setRectanglePoint(3, halfWidth, -halfDepth);
		
//        hitType = HitType.NO_HIT;
    }
    
//    public OBBCollisionVolume(float width, float depth) {  
////    public OBBCollisionVolume(float width, float depth, CurrentState hit) {  
////    public OBBCollisionVolume(float width, float depth, HitType hit) {        
//    	mRectangle = new CollisionRectangle();
//		mRotatedRectangle = new CollisionRectangle();
//		mMovedRectangle = new CollisionRectangle();
//  	
//    	float halfWidth = width / 2.0f;
//		float halfDepth = depth / 2.0f;
//  	
//		setRectanglePoint(0, -halfWidth, -halfDepth);
//		setRectanglePoint(1, -halfWidth, halfDepth);
//		setRectanglePoint(2, halfWidth, halfDepth);
//		setRectanglePoint(3, halfWidth, -halfDepth);
//		
////        hitType = hit;
//    }
    
    public CollisionRectangle moveRectangle(Vector3 position) {
	  	float rectangleX;
	  	float rectangleZ;
		
		for(int i = 0; i < 4; i++) {
			rectangleX = mRectangle.pointX[i];
			rectangleZ = mRectangle.pointZ[i];
			
			mMovedRectangle.pointX[i] = rectangleX + position.x;
			mMovedRectangle.pointZ[i] = rectangleZ + position.z;
		}
    	
    	return mMovedRectangle;
    }
    
    public CollisionRectangle rotateRectangle(Vector3 position) {    	
      	float r = position.r;
      	float rectangleX;
      	float rectangleZ;
    	
    	for(int i = 0; i < 4; i++) {
    		rectangleX = mRectangle.pointX[i];
    		rectangleZ = mRectangle.pointZ[i];
    		
    		mRotatedRectangle.pointX[i] = ((rectangleX * (float)Math.cos(r * PI_OVER_180)) +
    	      		(rectangleZ * (float)Math.sin(r * PI_OVER_180))) + position.x;
    		mRotatedRectangle.pointZ[i] = ((rectangleX * (-(float)Math.sin(r * PI_OVER_180))) +
    	      		(rectangleZ * (float)Math.cos(r * PI_OVER_180))) + position.z;
    	}
    	
    	return mRotatedRectangle;
    }
    
    private void setRectanglePoint(int i, float halfWidth, float halfDepth) {		
    	mRectangle.pointX[i] = halfWidth;
    	mRectangle.pointZ[i] = halfDepth;
    }
    
    public CollisionRectangle getCollisionRectangle() {
    	return mRectangle;
    }
    
    public void setCollisionVolume(float width, float depth) { 
//    public void setCollisionVolume(float width, float depth, CurrentState hit) { 
//    public void setCollisionVolume(float width, float depth, HitType hit) {  	
    	float halfWidth = width * 0.5f;
		float halfDepth = depth * 0.5f;
//    	float halfWidth = width / 2.0f;
//		float halfDepth = depth / 2.0f;
  	
		setRectanglePoint(0, -halfWidth, -halfDepth);
		setRectanglePoint(1, -halfWidth, halfDepth);
		setRectanglePoint(2, halfWidth, halfDepth);
		setRectanglePoint(3, halfWidth, -halfDepth);
		
//        hitType = hit;
    }
}
