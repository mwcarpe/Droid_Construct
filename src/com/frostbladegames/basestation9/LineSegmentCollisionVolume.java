/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;

/**
 * A Line Segment collision volume.  This code tests against Rectangles when calculating collisions.  
 */
public class LineSegmentCollisionVolume extends AllocationGuard {
//public class LineSegmentCollisionVolume extends CollisionVolume {
    private static final float PI_OVER_180 = 0.0174532925f;	// convert Degrees to Radians
    
    private LineSegment mLineSegment;
    private LineSegment mPerpendicularSegment;
    private LineSegment mMovedLineSegment;
    private LineSegment mMovedPerpendicularSegment;
    
    public CurrentState currentStateHitType;
//    public HitType hitType;
    
    public boolean movePlatformType;
    
    public GameObject platform;
    
//    public boolean moveElevatorType;
//    
//    public GameObject elevator;
    
    public LineSegmentCollisionVolume() {
//        super();
        
        mLineSegment = new LineSegment();
        mPerpendicularSegment = new LineSegment();
        mMovedLineSegment = new LineSegment();
        mMovedPerpendicularSegment = new LineSegment();
        
//        mParallelSegment = new LineSegment();
//        mParallelAxis = new Vector3();
//        mPerpendicularAxis = new Vector3();
        
        currentStateHitType = CurrentState.NO_HIT;
//        hitType = HitType.NO_HIT;
        
        movePlatformType = false;
        
        platform = null;
    }
    
    public LineSegmentCollisionVolume(LineSegment lineSegment) {
//        super();
        
        mLineSegment = new LineSegment(lineSegment);
        
        /* XXX Was trying to rotate mPerpendicular around center if this was Collision and Bounce 
         * calculation issue, but Bounce only uses mLineSegment */
//        float centerX = (lineSegment.x1 + lineSegment.x2) / 2;
//        float centerZ = (lineSegment.z1 + lineSegment.z2) / 2;
//        
//        float cx1 = lineSegment.x1 - centerX;
//        float cz1 = lineSegment.z1 - centerZ;
//        float cx2 = lineSegment.x2 - centerX;
//        float cz2 = lineSegment.z2 - centerZ;

        final float perpX1 = (lineSegment.x1*(float)Math.cos(90.0f * PI_OVER_180)) + 
        		lineSegment.z1*(float)Math.sin(90.0f * PI_OVER_180);
        final float perpZ1 = (lineSegment.x1*(-(float)Math.sin(90.0f * PI_OVER_180))) + 
        		lineSegment.z1*(float)Math.cos(90.0f * PI_OVER_180);
        final float perpX2 = (lineSegment.x2*(float)Math.cos(90.0f * PI_OVER_180)) + 
        		lineSegment.z2*(float)Math.sin(90.0f * PI_OVER_180);
        final float perpZ2 = (lineSegment.x2*(-(float)Math.sin(90.0f * PI_OVER_180))) + 
        		lineSegment.z2*(float)Math.cos(90.0f * PI_OVER_180);
        
        mPerpendicularSegment = new LineSegment(perpX1, perpZ1, perpX2, perpZ2, lineSegment.lineSegmentId);

        mMovedLineSegment = new LineSegment(mLineSegment);
        mMovedPerpendicularSegment = new LineSegment(mPerpendicularSegment);
        
//        mParallelAxis = new Vector3();
//        mPerpendicularAxis = new Vector3();
//		
//		final float parallelR = lineSegment.r + 90.0f;
//		final float parallelX1 = lineSegment.x1 - (float)Math.sin(parallelR * PI_OVER_180) * 2.0f;
//		final float parallelZ1 = lineSegment.z1 - (float)Math.cos(parallelR * PI_OVER_180) * 2.0f;
//		final float parallelX2 = lineSegment.x2 - (float)Math.sin(parallelR * PI_OVER_180) * 2.0f;
//		final float parallelZ2 = lineSegment.z2 - (float)Math.cos(parallelR * PI_OVER_180) * 2.0f;
//		
//		mParallelSegment = new LineSegment(parallelX1, parallelZ1, parallelX2, parallelZ2, lineSegment.r, lineSegment.lineSegmentId);
//        
//    	final float perpRotationX = mLineSegment.x2 - mLineSegment.x1;
//    	final float perpRotationZ = mLineSegment.z2 - mLineSegment.z1;
//        
//    	mPerpendicularAxis.x = (perpRotationX * (float)Math.cos(90.0f * PI_OVER_180)) +
//			(perpRotationZ * (float)Math.sin(90.0f * PI_OVER_180));
//    	mPerpendicularAxis.z = (perpRotationX * (-(float)Math.sin(90.0f * PI_OVER_180))) +
//			(perpRotationZ * (float)Math.cos(90.0f * PI_OVER_180));
//    	
//    	final float parRotationX = mLineSegment.x2 - mParallelSegment.x2;
//    	final float parRotationZ = mLineSegment.z2 - mParallelSegment.z2;
//    	
//    	mParallelAxis.x = (parRotationX * (float)Math.cos(90.0f * PI_OVER_180)) +
//			(parRotationZ * (float)Math.sin(90.0f * PI_OVER_180));
//    	mParallelAxis.z = (parRotationX * (-(float)Math.sin(90.0f * PI_OVER_180))) +
//			(parRotationZ * (float)Math.cos(90.0f * PI_OVER_180));
        
        currentStateHitType = CurrentState.NO_HIT;
//        hitType = HitType.NO_HIT;
        
        movePlatformType = false;
        
        platform = null;
    }
    
    public LineSegmentCollisionVolume(LineSegment lineSegment, CurrentState hitType) {
//    public LineSegmentCollisionVolume(LineSegment lineSegment, HitType hit) {
//        super(hit);

//		DebugLog.d("AttackReceive", "LineSegmentCollisionVolume <contructor>(lineSegment, hit)");
        
        mLineSegment = new LineSegment(lineSegment);
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        final float perpX1 = (lineSegment.x1*(float)Math.cos(90.0f * PI_OVER_180)) + 
        		lineSegment.z1*(float)Math.sin(90.0f * PI_OVER_180);
        final float perpZ1 = (lineSegment.x1*(-(float)Math.sin(90.0f * PI_OVER_180))) + 
        		lineSegment.z1*(float)Math.cos(90.0f * PI_OVER_180);
        final float perpX2 = (lineSegment.x2*(float)Math.cos(90.0f * PI_OVER_180)) + 
        		lineSegment.z2*(float)Math.sin(90.0f * PI_OVER_180);
        final float perpZ2 = (lineSegment.x2*(-(float)Math.sin(90.0f * PI_OVER_180))) + 
        		lineSegment.z2*(float)Math.cos(90.0f * PI_OVER_180);
        
        mPerpendicularSegment = new LineSegment(perpX1, perpZ1, perpX2, perpZ2, lineSegment.lineSegmentId);
        
        mMovedLineSegment = new LineSegment(mLineSegment);
        mMovedPerpendicularSegment = new LineSegment(mPerpendicularSegment);
        
        currentStateHitType = hitType;
        
        movePlatformType = false;
        
        platform = null;
    }
    
    public LineSegmentCollisionVolume(LineSegment lineSegment, CurrentState hitType, GameObject platformObject, boolean moveType) {          
          mLineSegment = new LineSegment(lineSegment);

          final float perpX1 = (lineSegment.x1*(float)Math.cos(90.0f * PI_OVER_180)) + 
          		lineSegment.z1*(float)Math.sin(90.0f * PI_OVER_180);
          final float perpZ1 = (lineSegment.x1*(-(float)Math.sin(90.0f * PI_OVER_180))) + 
          		lineSegment.z1*(float)Math.cos(90.0f * PI_OVER_180);
          final float perpX2 = (lineSegment.x2*(float)Math.cos(90.0f * PI_OVER_180)) + 
          		lineSegment.z2*(float)Math.sin(90.0f * PI_OVER_180);
          final float perpZ2 = (lineSegment.x2*(-(float)Math.sin(90.0f * PI_OVER_180))) + 
          		lineSegment.z2*(float)Math.cos(90.0f * PI_OVER_180);
          
          mPerpendicularSegment = new LineSegment(perpX1, perpZ1, perpX2, perpZ2, lineSegment.lineSegmentId);
          
          mMovedLineSegment = new LineSegment(mLineSegment);
          mMovedPerpendicularSegment = new LineSegment(mPerpendicularSegment);
          
          currentStateHitType = hitType;
          
          movePlatformType = moveType;
          
          platform = platformObject;
//          elevator = new GameObject(platform);
      }
    
    public LineSegment moveLineSegment(Vector3 position) {
	  	mMovedLineSegment.x1 = mLineSegment.x1 + position.x;
	  	mMovedLineSegment.z1 = mLineSegment.z1 + position.z;
	  	mMovedLineSegment.x2 = mLineSegment.x2 + position.x;
	  	mMovedLineSegment.z2 = mLineSegment.z2 + position.z;
    	
    	return mMovedLineSegment;
    }
    
    public LineSegment movePerpendicularSegment(Vector3 position) {
    	mMovedPerpendicularSegment.x1 = mPerpendicularSegment.x1 + position.x;
    	mMovedPerpendicularSegment.z1 = mPerpendicularSegment.z1 + position.z;
    	mMovedPerpendicularSegment.x2 = mPerpendicularSegment.x2 + position.x;
    	mMovedPerpendicularSegment.z2 = mPerpendicularSegment.z2 + position.z;
    	
    	return mMovedPerpendicularSegment;
    }
    
    public LineSegment getLineSegment() {
    	return mLineSegment;
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public LineSegment getPerpendicularSegment() {
    	return mPerpendicularSegment;
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
//    public LineSegment getParallelSegment() {
//    	return mParallelSegment;
//    }
//    
//    public Vector3 getParallelAxis() {
//    	return mParallelAxis;
//    }
//    
//    public Vector3 getPerpendicularAxis() {
//    	return mPerpendicularAxis;
//    }
}
