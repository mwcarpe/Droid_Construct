/*
 * Copyright © 2012 FrostBlade LLC
 */

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frostbladegames.basestation9;

import android.util.Log;
//import java.lang.reflect.Array;
//import java.util.Comparator;

import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
//import com.frostbladegames.droidconstruct.GameObjectGroups.HitType;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;

/**
 * A system for calculating collisions between moving game objects.  This system accepts collision
 * volumes from game objects each frame and performs a series of tests to see which of them
 * overlap. If an intersection is detected both objects receive notification via a
 * HitReactionComponent, if one has been specified.
 */
public class GameObjectCollisionSystem extends BaseObject {
	// FIXME Optimize these static final int sizes
    private static final int MAX_COLLIDING_OBJECTS = 256;
    private static final int COLLISION_RECORD_POOL_SIZE = 256;
    private static final float PI_OVER_180 = 0.0174532925f;
//    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;
    
    // FIXME RE-ENABLE Comparator and modify for DynamicCollisionVolumeComparator and BackgroundCollisionVolumeComparator
//    private static final CollisionVolumeComparator sCollisionVolumeComparator 
//        = new CollisionVolumeComparator();

    // mDynamicRecordPool and mBackgroundRecordPool allocate() records as required. Make sure to release() when finished.
    private FixedSizeArray<DynamicCollisionVolumeRecord> mDynamicObjects;
    private DynamicCollisionVolumeRecordPool mDynamicRecordPool;
    
    private FixedSizeArray<BackgroundCollisionVolumeRecord> mBackgroundObjects;
    private BackgroundCollisionVolumeRecordPool mBackgroundRecordPool;
    
//    // Only one Background Collision per Loop
//    private boolean backgroundCollisionLoop;
    
    private float [] mAxisX;
    private float [] mAxisZ;
    
    private LineSegment mClosestBackgroundLineSegment;
    
    private GameObject mCollisionWeaponGameObject = null;
    
    // FIXME TEMP DELETE
    private int mMaxBackgroundCount;
    private int mMaxDynamicCount;
	private float mCounterTimer;
    
    public GameObjectCollisionSystem() {
        super();
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "GameObjectCollisionSystem <constructor>");	
    	}
        
        mDynamicObjects = new FixedSizeArray<DynamicCollisionVolumeRecord>(MAX_COLLIDING_OBJECTS);
        mBackgroundObjects = new FixedSizeArray<BackgroundCollisionVolumeRecord>(MAX_COLLIDING_OBJECTS);
        // FIXME Comparator disabled, so FixedSizeArray will default to standard Array sorter if sort() called. Study.
//        mObjects.setComparator(sCollisionVolumeComparator);
        mDynamicRecordPool = new DynamicCollisionVolumeRecordPool(COLLISION_RECORD_POOL_SIZE);
        mBackgroundRecordPool = new BackgroundCollisionVolumeRecordPool(COLLISION_RECORD_POOL_SIZE);
        
//        backgroundCollisionLoop = false;
        
        mAxisX = new float[4];
        mAxisZ = new float[4];
        
        mClosestBackgroundLineSegment = new LineSegment();
    }
    
    @Override
    public void reset() {
        final int dynamicCount = mDynamicObjects.getCount();
        for (int i = 0; i < dynamicCount; i++) {
            mDynamicRecordPool.release(mDynamicObjects.get(i));
        }
        mDynamicObjects.clear();
        
        final int backgroundCount = mBackgroundObjects.getCount();
        for (int i = 0; i < backgroundCount; i++) {
            mBackgroundRecordPool.release(mBackgroundObjects.get(i));
        }
        mBackgroundObjects.clear();
        
        mCollisionWeaponGameObject = null;
    }
    
    /** 
     * Adds a Dynamic GameObject and its Collision Volume to the Collision System for one frame.
     * Once registered for collisions the object may attack other object volumes or receive hit
     * from other object volumes.
     * @param object  The object to consider for collision.
     * @param reactionComponent  A HitReactionComponent to notify when a collision occurs.
     * If null, the collision will still occur but no HitReaction notification will be sent.
     * @param collisionVolume  A list of volumes that can hit other game objects.  May be null.
     */
    public void registerForDynamicCollisions(GameObject gameObject, HitReactionComponent reactionComponent, 
            OBBCollisionVolume collisionVolume) {    	
        DynamicCollisionVolumeRecord record = mDynamicRecordPool.allocate();
        
        if (record != null && gameObject != null && collisionVolume != null) {
            record.gameObject = gameObject;
            record.collisionVolume = collisionVolume;
            record.reactionComponent = reactionComponent;
            mDynamicObjects.add(record);
        } else {
        	if (record == null) {
        		Log.e("Object", "GameObjectCollisionSystem registerForDynamicCollisions() record = null");
        	}
        	if (gameObject == null) {
        		Log.e("Object", "GameObjectCollisionSystem registerForDynamicCollisions() gameObject = null");
        	}
        	if (collisionVolume == null) {
        		Log.e("Object", "GameObjectCollisionSystem registerForDynamicCollisions() collisionVolume = null");
        	}
        }
    }
    
    /** 
     * Adds a Background GameObject and its Collision Volumes to the Collision System for one frame.
     * Once registered for collisions the object may attack other object volumes, but not receive.
     * @param object  The object to consider for collision.
     * @param reactionComponent  A HitReactionComponent to notify when a collision occurs.
     * If null, the collision will still occur but no HitReaction notification will be sent.
     * @param collisionVolumes  A list of volumes that can hit other game objects.  May be null.
     */
    public void registerForBackgroundCollisions(GameObject gameObject, HitReactionComponent reactionComponent, 
    		FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes) {    	
        BackgroundCollisionVolumeRecord record = mBackgroundRecordPool.allocate();
        
        if (record != null && gameObject != null && collisionVolumes != null) {            
            record.gameObject = gameObject;
            record.collisionVolumes = collisionVolumes;
            record.reactionComponent = reactionComponent;
            mBackgroundObjects.add(record);

        } else {
        	if (record == null) {
        		Log.e("Object", "GameObjectCollisionSystem registerForBackgroundCollisions() record = null");
        	}
        	if (gameObject == null) {
        		Log.e("Object", "GameObjectCollisionSystem registerForBackgroundCollisions() gameObject = null");
        	}
        	if (collisionVolumes == null) {
        		Log.e("Object", "GameObjectCollisionSystem registerForBackgroundCollisions() collisionVolumes = null");
        	}
        }
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
        /* FIXME RE-ENABLE sort(). Comparator disabled, so FixedSizeArray will default
         * to standard Array sorter if sort() called. */
        // Sort the objects by their x position.
//        mObjects.sort(true);
    	
//    	backgroundCollisionLoop = false;
    	
    	mMaxBackgroundCount =  0;
    	mMaxDynamicCount = 0;
        
    	// Check Dynamic GameObjects for Collision Detection against Background and Other Dynamic GameObjects
        final int dynamicRecord1Count = mDynamicObjects.getCount();
        final int backgroundCount = mBackgroundObjects.getCount();
               
        for (int i = 0; i < dynamicRecord1Count; i++) {
            final DynamicCollisionVolumeRecord dynamicRecord1 = mDynamicObjects.get(i);
            GameObject dynamicGameObject1 = dynamicRecord1.gameObject;
       	
//            if (!dynamicGameObject1.invincible) {
        	// Dynamic Receive vs Background Attack
            OBBCollisionVolume dynamicRecord1CollisionVolume = dynamicRecord1.collisionVolume;
            CollisionRectangle dynamicRecord1Rectangle = 
	            	dynamicRecord1CollisionVolume.moveRectangle(dynamicGameObject1.currentPosition);
//	            CollisionRectangle dynamicRecord1Rectangle = 
//	            	dynamicRecord1CollisionVolume.rotateRectangle(dynamicGameObject1.currentPosition);
            
            // Set mAxis[0] using FrontLeft(1), BackLeft(0) and mAxis[1] using FrontLeft(1), FrontRight(2)
            setAxis(0, dynamicRecord1Rectangle, 1, 0);
            setAxis(1, dynamicRecord1Rectangle, 1, 2);
            
//	            dynamicGameObject1.invincibleBackgroundCollision = false;
            
            // DynamicGameObject1 and BackgroundCollisionVolume Collision Check
			float collisionTestBefore = 0.0f;	// Check for Collision Before as comparison with Collision After
			float collisionTestAfter = 0.0f;	// Check for Collision After. Negative value means Collision.
			float intersectionX = 0.0f;
			float intersectionZ = 0.0f;
			float closestCollision = 10.0f;	// Closest collision to BackgroundCollisionVolume.  Start value greater than min Collision Distance.
//			float closestM6 = 10.0f;	// Shortest magnitude between DynamicGameObject.currentPosition and Intersect Point.
			float closestIntersectionX = 0.0f;
			float closestIntersectionZ = 0.0f;
			CurrentState closestCurrentStateHitType = CurrentState.NO_HIT;
			int closestPlatformBackgroundRecordId = 0;
			int closestPlatformBackgroundCollisionVolumeId = 0;
//			int closestElevatorBackgroundRecordId = 0;
//			int closestElevatorBackgroundCollisionVolumeId = 0;
			
			// FIXME TEST ONLY. Put back to loop below.
			GameObject backgroundGameObject = null;
			int closestBackgroundGameObjectId = 0;

            for (int j = 0; j < backgroundCount; j++) {
            	final BackgroundCollisionVolumeRecord background = mBackgroundObjects.get(j);
//            	background = mBackgroundObjects.get(j);

            	backgroundGameObject = background.gameObject;
//            	GameObject backgroundGameObject = background.gameObject;

            	int backgroundVolumeCount = background.collisionVolumes.getCount();
        		
   				boolean collisionTypeCheck = backgroundCollisionTypeCheck(dynamicGameObject1, backgroundGameObject);
//   				boolean collisionTypeCheck = backgroundCollisionTypeCheck(dynamicGameObject1);
   				
   				if (collisionTypeCheck) {					
   					mMaxBackgroundCount++;
   					
	            	for (int a = 0; a < backgroundVolumeCount; a++) {
	            		LineSegmentCollisionVolume backgroundCollisionVolume = background.collisionVolumes.get(a);

	            		LineSegment backgroundLineSegment;
	            		
	            		if (backgroundCollisionVolume.movePlatformType) {
	            			// Elevator Collision
		            		backgroundLineSegment = backgroundCollisionVolume.moveLineSegment(backgroundCollisionVolume.platform.currentPosition);
		            		
	            		} else {
	            			// Background Collision
		            		backgroundLineSegment = backgroundCollisionVolume.getLineSegment();
	            		}
	            		
	            		/* v1 is Motion Vector for DynamicGameObject
	            		 * v2 is Vector for LineSegment
	            		 * v3 is Ghost Vector from DynamicGameObject previousPosition (Before) and currentPosition (After) to LineSegment StartPoint
	            		 * v4 is Vector from LineSegment StartPoint to Intersection Point
	            		 * v5 is Vector from LineSegment EndPoint to Intersection Point
	            		 * m is Magnitude of Vector (e.g. v1 -> m1)
	            		 * vx, vz is Vector X and Vector Z for a Vector (e.g. v1 -> v1x, v1z)
	            		 * dx, dz is Normalized Unit Vector (e.g. v1 -> d1x, d1z)
	            		 * lnvx, lnvz is Left Normal of a Vector (e.g. v1 -> lnv1x, lnv1z)
	            		 * lndx, lndz is Normalized Left Unit Normal of a Vector (e.g. v1 -> lnd1x, lnd1z)
	            		 * t is tangent, the point of intersection */
//	            		 * v6 is Vector from DynamicGameObject previousPosition to Intersection Point
	                	
	            		// BOUNDARY USING PREVIOUS POSITION
	                	// Test if within StartPoint and EndPoint boundaries of LineSegment
	                	float d2x = backgroundLineSegment.getUnitVectorX();
	                	float d2z = backgroundLineSegment.getUnitVectorZ();
	                	
	                	// For Boundary, use v3 previousPosition
	                	float lnv3x = getLeftNormalX(dynamicGameObject1, backgroundLineSegment);
	                	float lnv3z = getLeftNormalZ(dynamicGameObject1, backgroundLineSegment);
	                	
	                	float perpProduct1 = (lnv3x * d2x) + (lnv3z * d2z);
	                	
	                	float lnv1x = getLeftNormalX(dynamicGameObject1);
	                	float lnv1z = getLeftNormalZ(dynamicGameObject1);
	                	
	                	float perpProduct2 = (lnv1x * d2x) + (lnv1z * d2z);
	                	
	                	float t = 0;
	                	if (perpProduct2 != 0) {
	                		t = perpProduct1 / perpProduct2;
	                	}
	                	
	                	float v1x = getVectorX(dynamicGameObject1);
	                	float v1z = getVectorZ(dynamicGameObject1);
	                	
	                	// Check projected intersectionX,Z from DynamicGameObject Previous Position and Current Position
	                	intersectionX = dynamicGameObject1.previousPosition.x + (v1x * t);
	                	intersectionZ = dynamicGameObject1.previousPosition.z + (v1z * t);
	                	
	                	// Calculate Magnitudes. note: Magnitude always positive, so do not need to test as Math.abs().
	                	float m2 = backgroundLineSegment.magnitude;
	                	float m4 = getMagnitude(backgroundLineSegment.x1, backgroundLineSegment.z1, intersectionX, intersectionZ);
	                	float m5 = getMagnitude(backgroundLineSegment.x2, backgroundLineSegment.z2, intersectionX, intersectionZ);
//	                	float m6 = getMagnitude(dynamicGameObject1.previousPosition.x, dynamicGameObject1.previousPosition.z, 
//	                			intersectionX, intersectionZ);
////	                	float m6 = getMagnitude(dynamicGameObject1.currentPosition.x, dynamicGameObject1.currentPosition.z, 
////	                			intersectionX, intersectionZ);
	                	
	                	// COLLISION TEST COMPARING PREVIOUS POSITION AND CURRENT POSITION
	            		float v3xAfter = backgroundLineSegment.x1 - dynamicGameObject1.currentPosition.x;
	            		float v3zAfter = backgroundLineSegment.z1 - dynamicGameObject1.currentPosition.z;
	            		
	                	float lnd2x = backgroundLineSegment.getLeftUnitNormalX();
	                	float lnd2z = backgroundLineSegment.getLeftUnitNormalZ();
	            		
	            		float v3xBefore = backgroundLineSegment.x1 - dynamicGameObject1.previousPosition.x;
	            		float v3zBefore = backgroundLineSegment.z1 - dynamicGameObject1.previousPosition.z;
//	            		float v3xBefore = 0.0f;
//	            		float v3zBefore = 0.0f;
//	            		
//	            		// If Droid on moving object, need to counter-adjust backgroundLineSegment for move after previousPosition
//	                	if (Math.abs(dynamicGameObject1.xMoveMagnitude) > 0.01f) {	// Check vs 0.01f to allow Float tolerance
//	                		v3xBefore = (backgroundLineSegment.x1 - dynamicGameObject1.xMoveMagnitude) - 
//	                				dynamicGameObject1.previousPosition.x;
//	                		
//	                		if (Math.abs(dynamicGameObject1.zMoveMagnitude) > 0.01f) {
//	                			v3zBefore = (backgroundLineSegment.z1 - dynamicGameObject1.zMoveMagnitude) - 
//	                					dynamicGameObject1.previousPosition.z;
//	                		} else {
//	                			v3zBefore = backgroundLineSegment.z1 - dynamicGameObject1.previousPosition.z;
//	                		}
//	                		
//	                	} else if (Math.abs(dynamicGameObject1.zMoveMagnitude) > 0.1f) {
//	                		v3xBefore = backgroundLineSegment.x1 - dynamicGameObject1.previousPosition.x;
//	                		v3zBefore = (backgroundLineSegment.z1 - dynamicGameObject1.zMoveMagnitude) - 
//                					dynamicGameObject1.previousPosition.z;
//	                	} else {
//	                		v3xBefore = backgroundLineSegment.x1 - dynamicGameObject1.previousPosition.x;
//		            		v3zBefore = backgroundLineSegment.z1 - dynamicGameObject1.previousPosition.z;
//	                	}
	                	
	                	// collisionTest detects whether DynamicObject has collided with LineSegment. If collisionTestAfter < 0, then Collision.
	                	collisionTestBefore = (v3xBefore * lnd2x) + (v3zBefore * lnd2z);
	                	collisionTestAfter = (v3xAfter * lnd2x) + (v3zAfter * lnd2z);
	                	
//	                	if (GameParameters.debug && dynamicGameObject1.group == Group.DROID && 
//	                			backgroundGameObject.group == Group.PLATFORM_ELEVATOR && Math.abs(collisionTestAfter) < 2.0f) {
//    	   	        		Log.i("BackgroundCollision", "GameObjectCollisionSystem update() Droid previousPosition.x,z; currentPosition.x,z =" +
//    	   	        				" [" + dynamicGameObject1.gameObjectId + "] " +
//    	   	        				dynamicGameObject1.previousPosition.x + ", " + dynamicGameObject1.previousPosition.z + "; " +
//    	   	        				dynamicGameObject1.currentPosition.x + ", " + dynamicGameObject1.currentPosition.z);
//		            		Log.i("BackgroundCollision", "GameObjectCollisionSystem update() [D] [B] [LS] backgroundLineSegment x1,z1;x2,z2 =" +
//		    						" [" + dynamicGameObject1.gameObjectId + "] " +
//		    						" [" + backgroundGameObject.gameObjectId + "] " + " [" + backgroundLineSegment.lineSegmentId + "] " +
//		            				backgroundLineSegment.x1 + ", " + backgroundLineSegment.z1 + "; " +
//		            				backgroundLineSegment.x2 + ", " + backgroundLineSegment.z2);
//		    				Log.i("BackgroundCollision", "GameObjectCollisionSystem update() [LS] intersectionX,Z; m2,m4,m5; collistionTestBefore,After  =" +
//		    						" [" + backgroundLineSegment.lineSegmentId + "] " +
//		    						intersectionX + ", " + intersectionZ + "; " +
//			            			m2 + ", " + m4 + ", " + m5 + "; " +
//			            			collisionTestBefore + ", " + collisionTestAfter);
//	                	}
	                	
	                	// FIXME Re-test <= method
	                	// Boundary Check
	                	if (m4 <= m2 && m5 <= m2) {
//	                	if (m4 < m2 && m5 < m2) {
	                		
	                		/* Collision Test 
	                		 * Allow variances for collisionTestBefore and collisionTestAfter in event of wall pass-thru, exactly 0.0f, etc */
		    				if (collisionTestBefore >= collisionTestAfter && collisionTestBefore > -0.1f) {
//			                	if (GameParameters.debug && dynamicGameObject1.group == Group.DROID && 
//			                			backgroundGameObject.group == Group.PLATFORM_ELEVATOR) {
//			                		Log.i("BackgroundCollision", "GameObjectCollisionSystem update() collisionTestBefore >= collisionTestAfter && collisionTestBefore > -0.1f");	
//			                	}
		    					
			    				if (collisionTestAfter <= 0.0f && collisionTestAfter < closestCollision) {
//				                	if (GameParameters.debug && dynamicGameObject1.group == Group.DROID && 
//				                			backgroundGameObject.group == Group.PLATFORM_ELEVATOR) {
//				    					Log.i("BackgroundCollision", "GameObjectCollisionSystem update() collisionTestAfter <= 0.0f && collisionTestAfter < closestCollision");	
//				                	}
		    						
			                		closestCollision = collisionTestAfter;
			                		closestIntersectionX = intersectionX;
			                		closestIntersectionZ = intersectionZ;
			                		closestCurrentStateHitType = backgroundCollisionVolume.currentStateHitType;
			                		mClosestBackgroundLineSegment.set(backgroundLineSegment);
			                		
			                		// In the event, Collision is with an Elevator or PlatformSection
			            			closestPlatformBackgroundRecordId = j;
			            			closestPlatformBackgroundCollisionVolumeId = a;
			                		
			                		// FIXME TEMP ONLY. DELETE.
			                		closestBackgroundGameObjectId = backgroundGameObject.gameObjectId;
		    					}
		    				}
		    	
//		    				if (collisionTestBefore >= 0.0f && collisionTestAfter < 0.0f) {
////		    				if (collisionTestAfter > 0.0f) {
////		    					// ignore
////		    				} else if ((collisionTestBefore >= 0.0f && collisionTestAfter < 0.0f) ||
////	                				(collisionTestAfter < 0.0f && Math.abs(collisionTestAfter) < 1.0f && 
////	                						Math.abs(collisionTestAfter) < Math.abs(closestCollision))) {
////		                	// Range Check and Collision Test
////		                	if ((Math.abs(collisionTestAfter) < 1.0f) && collisionTestBefore > 0.0f && collisionTestAfter < 0.0f) {
////		                	if (m6 < 1.0f && m6 < closestM6) {					    				
////		                		closestM6 = m6;
//		                		closestCollision = collisionTestAfter;
//		                		closestIntersectionX = intersectionX;
//		                		closestIntersectionZ = intersectionZ;
//		                		closestCurrentStateHitType = backgroundCollisionVolume.currentStateHitType;
//		                		mClosestBackgroundLineSegment.set(backgroundLineSegment);
//		                		
//		                		// In the event, Collision is with an Elevator or PlatformSection
//		            			closestPlatformBackgroundRecordId = j;
//		            			closestPlatformBackgroundCollisionVolumeId = a;
////			            		if (backgroundCollisionVolume.currentStateHitType == CurrentState.ELEVATOR ||
////			            				backgroundCollisionVolume.currentStateHitType == CurrentState.ELEVATOR_EXIT) {
////			            			closestElevatorBackgroundRecordId = j;
////			            			closestElevatorBackgroundCollisionVolumeId = a;
////			            		}
//		                		
//		                		// FIXME TEMP ONLY. DELETE.
//		                		closestBackgroundGameObjectId = backgroundGameObject.gameObjectId;
	                	}
		        	}
            	}
            }
            
            // FIXME Re-test <= method
			if (closestCollision < 0.0f) {	
//			if (closestCollision <= 0.0f) {				
				// GameObject Groups: BACKGROUND, DROID, DROID_LASER, ENEMY, ASTRONAUT
				if (dynamicGameObject1.group == Group.DROID) {
//                	if (GameParameters.debug) {
//                		Log.i("BackgroundCollision", "GameObjectCollisionSystem update() COLLISION closestCurrentStateHitType = " + closestCurrentStateHitType);	
//                	}
					
                	if (closestCurrentStateHitType == CurrentState.BOUNCE) {
                		// Set DynamicGameObject back to point of intersection to prevent wall pass-thru
                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);
                		
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.FALL) {
                		// Set DynamicGameObject back to point of intersection to ensure correct Fall calculations
                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);

                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.FALL, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.ELEVATOR) {
//                		Log.i("Elevator", "GameObjectCollisionSystem update() Collision CurrentState.ELEVATOR ENABLE01, DISABLE00)");
                		
                		final BackgroundCollisionVolumeRecord background = mBackgroundObjects.get(closestPlatformBackgroundRecordId);
                		LineSegmentCollisionVolume backgroundCollisionVolume = background.collisionVolumes.get(closestPlatformBackgroundCollisionVolumeId);
	            		
                		GameObject elevatorGameObject = backgroundCollisionVolume.platform;
                		
                		GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
                		
	          			factory.enableElevatorCollision01(elevatorGameObject);
	          			factory.disableElevatorCollision00(elevatorGameObject);
//	          			factory.enableElevatorCollision02(elevatorGameObject);
    					
    					// Set Elevator receiveHitDynamic() for both Elevator elevatorGameObject and Droid dynamicGameObject1
    					background.reactionComponent.receivedHitDynamic(elevatorGameObject, CurrentState.ELEVATOR, dynamicGameObject1);
                		
//                		// If Platform Elevator and Droid already in Elevator state, then exit Elevator
//                		if (elevatorGameObject.currentState == CurrentState.ELEVATOR) {
//                			factory.disableElevatorCollision03(elevatorGameObject);
//                			factory.enableElevatorCollision00(elevatorGameObject);
//                			
//                			background.reactionComponent.receivedHitDynamic(elevatorGameObject, CurrentState.MOVE, dynamicGameObject1);
//                		} else {
//
//    	          			factory.disableElevatorCollision00(elevatorGameObject);
//    	          			factory.enableElevatorCollision01(elevatorGameObject);
////    	          			factory.enableElevatorCollision02(elevatorGameObject);
//        					
//        					// Set Elevator receiveHitDynamic() for both Elevator elevatorGameObject and Droid dynamicGameObject1
//        					background.reactionComponent.receivedHitDynamic(elevatorGameObject, CurrentState.ELEVATOR, dynamicGameObject1);
//                		}
	          			
//	    				if (!elevatorGameObject.platformCompleted) {
//	                		GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//		          			factory.disableElevatorCollision00(elevatorGameObject);
//		          			factory.enableElevatorCollision01(elevatorGameObject);
//		          			factory.enableElevatorCollision02(elevatorGameObject);
//		          			
////	    					dynamicRecord1.reactionComponent.receivedHitDynamic(dynamicGameObject1, CurrentState.ELEVATOR, dpElevatorDynamicGameObject);
//	    					
//	    					// Set Elevator receiveHitDynamic() for both Elevator elevatorGameObject and Droid dynamicGameObject1
//	    					background.reactionComponent.receivedHitDynamic(elevatorGameObject, CurrentState.ELEVATOR, dynamicGameObject1);
//	    				}
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.ELEVATOR_EXIT) {
//                		Log.i("Elevator", "GameObjectCollisionSystem update() Collision CurrentState.ELEVATOR_EXIT ENABLE00, DISABLE02/03)");
                		
                		final BackgroundCollisionVolumeRecord background = mBackgroundObjects.get(closestPlatformBackgroundRecordId);
                		LineSegmentCollisionVolume backgroundCollisionVolume = background.collisionVolumes.get(closestPlatformBackgroundCollisionVolumeId);
	            		
                		GameObject elevatorGameObject = backgroundCollisionVolume.platform;
                		
                		GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
                		
                		if (elevatorGameObject.elevatorStartpoint) {
                			factory.enableElevatorCollision00(elevatorGameObject);
                			factory.disableElevatorCollision02(elevatorGameObject);
                			
                			elevatorGameObject.elevatorStartpoint = false;
//                			dynamicGameObject1.elevatorStartpoint = false;
                		} else {
                			factory.enableElevatorCollision00(elevatorGameObject);
                			factory.disableElevatorCollision03(elevatorGameObject);
                			
                			elevatorGameObject.elevatorStartpoint = true;
//                			dynamicGameObject1.elevatorStartpoint = true;
                		}
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.PLATFORM_SECTION_START) {
                		final BackgroundCollisionVolumeRecord background = mBackgroundObjects.get(closestPlatformBackgroundRecordId);
                		LineSegmentCollisionVolume backgroundCollisionVolume = background.collisionVolumes.get(closestPlatformBackgroundCollisionVolumeId);
	            		
                		GameObject platformGameObject = backgroundCollisionVolume.platform;
                		
                		dynamicRecord1.reactionComponent.receivedHitDynamic(dynamicGameObject1, CurrentState.PLATFORM_SECTION_START, platformGameObject);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.PLATFORM_SECTION_END) {
                		final BackgroundCollisionVolumeRecord background = mBackgroundObjects.get(closestPlatformBackgroundRecordId);
                		LineSegmentCollisionVolume backgroundCollisionVolume = background.collisionVolumes.get(closestPlatformBackgroundCollisionVolumeId);
	            		
                		GameObject platformGameObject = backgroundCollisionVolume.platform;
                		
                		dynamicRecord1.reactionComponent.receivedHitDynamic(dynamicGameObject1, CurrentState.PLATFORM_SECTION_END, platformGameObject);
                	}
                	
//                	if (closestCurrentStateHitType == CurrentState.PLATFORM_SECTION_EXIT) {
//                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.PLATFORM_SECTION_EXIT, mClosestBackgroundLineSegment);
//                	}
                	
                	if (closestCurrentStateHitType == CurrentState.GAME_END) {
//                		// Set DynamicGameObject back to point of intersection to prevent wall pass-thru
//                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);
                		
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.GAME_END, mClosestBackgroundLineSegment);
                	}
				} 
				
				if (dynamicGameObject1.group == Group.DROID_WEAPON) {
					/* FIXME Add CurrentState.BOUNCE for Weapon in event Weapon dropped inside Wall, then put back at
					 * previousPosition where Droid last held the Weapon before drop */
					// ignore
				}
				
				if (dynamicGameObject1.group == Group.DROID_LASER) {	   							
                	if (closestCurrentStateHitType == CurrentState.BOUNCE) {
                		dynamicGameObject1.hitReactType = Type.ELECTRIC_RING;
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.DEAD, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.FALL) {
                		// Ignore
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.ELEVATOR) {
                		// Ignore
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.GAME_END) {
                		// Ignore
                	}
				}
				
				if (dynamicGameObject1.group == Group.ENEMY) {
                	if (closestCurrentStateHitType == CurrentState.BOUNCE) {
                		// Set DynamicGameObject back to point of intersection to prevent wall pass-thru
                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);
                		
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.FALL) {
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.ELEVATOR || closestCurrentStateHitType == CurrentState.ELEVATOR_ENEMY_BARRIER ||
                			closestCurrentStateHitType == CurrentState.ELEVATOR_EXIT) {
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.GAME_END) {
                		// Set DynamicGameObject back to point of intersection to prevent wall pass-thru
                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);
                		
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
				}
				
				if (dynamicGameObject1.group == Group.ASTRONAUT) {
                	if (closestCurrentStateHitType == CurrentState.BOUNCE) {
                		// Set DynamicGameObject back to point of intersection to prevent wall pass-thru
                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);
                		
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.FALL) {
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.ELEVATOR || closestCurrentStateHitType == CurrentState.ELEVATOR_ENEMY_BARRIER ||
                			closestCurrentStateHitType == CurrentState.ELEVATOR_EXIT) {
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.BOUNCE, mClosestBackgroundLineSegment);
                	}
                	
                	if (closestCurrentStateHitType == CurrentState.GAME_END) {
                		// Set DynamicGameObject back to point of intersection to prevent wall pass-thru
                		dynamicGameObject1.setCurrentPosition(closestIntersectionX, closestIntersectionZ);
                		
                		dynamicRecord1.reactionComponent.receivedHitBackground(dynamicGameObject1, CurrentState.COLLECT, mClosestBackgroundLineSegment);
                	}
				}
			}
                	
			// DynamicGameObject1 and DynamicGameObject2 Collision Check
            final int dynamicRecord2Count = mDynamicObjects.getCount();
            
            for (int j = i + 1; j < dynamicRecord2Count; j++) {
                final DynamicCollisionVolumeRecord dynamicRecord2 = mDynamicObjects.get(j);
                GameObject dynamicGameObject2 = dynamicRecord2.gameObject;
       				
   				boolean collisionTypeCheck = dynamicCollisionTypeCheck(dynamicGameObject1, dynamicGameObject2);
            	
   				if (collisionTypeCheck) {
//            	if ((collisionTypeCheck && 
//            			dynamicGameObject1.currentState != CurrentState.DEAD && dynamicGameObject2.currentState != CurrentState.DEAD && 
//            			!dynamicGameObject1.gameObjectInactive && !dynamicGameObject2.gameObjectInactive)) {
//            	if (collisionTypeCheck) {
   					
            		mMaxDynamicCount++;
            		
	                OBBCollisionVolume dynamicRecord2CollisionVolume = dynamicRecord2.collisionVolume;
	                CollisionRectangle dynamicRecord2Rectangle = 
    	                	dynamicRecord2CollisionVolume.moveRectangle(dynamicGameObject2.currentPosition);
//	    	                CollisionRectangle dynamicRecord2Rectangle = 
//	    	                	dynamicRecord2CollisionVolume.rotateRectangle(dynamicGameObject2.currentPosition);

	                // Set mAxis[2] using FrontLeft(1), BackLeft(0) and mAxis[3] using FrontLeft(1), FrontRight(2)
	                setAxis(2, dynamicRecord2Rectangle, 1, 0);
	                setAxis(3, dynamicRecord2Rectangle, 1, 2);

	            	// Project dynamicRecord1Rectangle and dynamicRecord2Rectangle onto Axis1-4
	                float dynamic1Min = 0.0f;
	                float dynamic1Max = 0.0f;
	                float dynamic2Min = 0.0f;
	                float dynamic2Max = 0.0f;
	            	boolean collision = true;
	            	
	                for(int k = 0; k < 4; k++) {
	                	dynamic1Min = (mAxisX[k] * dynamicRecord1Rectangle.pointX[0]) + 
                			(mAxisZ[k] * dynamicRecord1Rectangle.pointZ[0]);
	                	dynamic1Max = dynamic1Min;
		                
	                	dynamic2Min = (mAxisX[k] * dynamicRecord2Rectangle.pointX[0]) + 
	            			(mAxisZ[k] * dynamicRecord2Rectangle.pointZ[0]);
	                	dynamic2Max = dynamic2Min;
		                
	                	for(int m = 1; m < 4; m++) {
	                		float dotProduct = (mAxisX[k] * dynamicRecord1Rectangle.pointX[m]) +
                				(mAxisZ[k] * dynamicRecord1Rectangle.pointZ[m]);
	                		
	                		if (dotProduct < dynamic1Min) {
	                			dynamic1Min = dotProduct;
	                		} else if (dotProduct > dynamic1Max) {
	                			dynamic1Max = dotProduct;
	                		}
	                	}
	                	
	                	for(int m = 1; m < 4; m++) {
	                		float dotProduct = (mAxisX[k] * dynamicRecord2Rectangle.pointX[m]) +
                				(mAxisZ[k] * dynamicRecord2Rectangle.pointZ[m]);

	                		if (dotProduct < dynamic2Min) {
	                			dynamic2Min = dotProduct;
	                		} else if (dotProduct > dynamic2Max) {
	                			dynamic2Max = dotProduct;
	                		}
	                	}
	                	
	                	if(dynamic1Min < dynamic2Min) {
	                		if(dynamic1Max < dynamic2Min) {
	                			collision = false;
	                			break;
	                		}
	                	} else {
	                		if(dynamic2Max < dynamic1Min) {
	                			collision = false;
	                			break;
	                		}
	                	}
	                }
	                
	                if(collision) {	                    
	                	// Determine GameObject HitType and call that object's HitReactionComponent
	                	CurrentState object1HitType = collisionHitType(dynamicGameObject1, dynamicGameObject2);
	                	CurrentState object2HitType = collisionHitType(dynamicGameObject2, dynamicGameObject1);
	                	
	                	if (object1HitType != CurrentState.NO_HIT) {
	                		dynamicRecord1.reactionComponent.receivedHitDynamic(dynamicGameObject1, object1HitType, dynamicGameObject2);
	                	}
	                	
	                	if (object2HitType != CurrentState.NO_HIT) {
	                		dynamicRecord2.reactionComponent.receivedHitDynamic(dynamicGameObject2, object2HitType, dynamicGameObject1);
	                	}
	                }
            	}
//       		}
            }
//        	}            
            /* This is a little tricky.  Since we always sweep forward in the list 
             * it's safe to invalidate the current record after we've tested it.  
             * This way we don't have to iterate over the object list twice. */
            mDynamicRecordPool.release(dynamicRecord1);
        }
        
//        final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//    	if (gameTime > (mCounterTimer + 10.0f)) {
//    		final int totalMaxCollisionCount = mMaxBackgroundCount + mMaxDynamicCount;
//    		Log.i("GameObjectCounter", "Max Background, Dynamic, Total Collision Checks (One Loop) = " + 
//    				mMaxBackgroundCount + ", " + mMaxDynamicCount + ", " + totalMaxCollisionCount);
//    		
//    		mCounterTimer = gameTime;
//    	}

        for (int i = 0; i < backgroundCount; i++) {
            mBackgroundRecordPool.release(mBackgroundObjects.get(i));
        }
        
        mDynamicObjects.clear();
        mBackgroundObjects.clear();
    }
    
    private boolean backgroundCollisionTypeCheck(GameObject dynamicObject, GameObject backgroundObject) {
//    private boolean backgroundCollisionTypeCheck(GameObject dynamicObject) {
    	boolean collisionTypeCheck = false;
    	
  		switch(dynamicObject.group) {
  		case DROID:
  			if(dynamicObject.currentState == CurrentState.PLATFORM_SECTION_START ||
  					dynamicObject.currentState == CurrentState.PLATFORM_SECTION_END) {
//  			if((backgroundObject.group == Group.PLATFORM_SECTION_START || backgroundObject.group == Group.PLATFORM_SECTION_END) &&
//  					(dynamicObject.currentState == CurrentState.PLATFORM_SECTION_START ||
//  					dynamicObject.currentState == CurrentState.PLATFORM_SECTION_END)) {
  				
  				// Ignore
  			} else {
  				collisionTypeCheck = true;
  			}
//  			collisionTypeCheck = true;
  			break;
  			
  		case DROID_LASER:
  			collisionTypeCheck = true;
  			break;
  			
  		case ENEMY:
  			collisionTypeCheck = true;
  			break;
  			
//  		case ENEMY_LASER:
//  			collisionTypeCheck = true;
//  			break;
  			
  		case ASTRONAUT:
  			collisionTypeCheck = true;
  			break;
  			
  		default:
  			break;
  		}
    	
    	return collisionTypeCheck;
    }
    
    private boolean dynamicCollisionTypeCheck(GameObject object1, GameObject object2) {
    	boolean collisionTypeCheck = false;
    	
    	if (object1.currentState != CurrentState.DEAD && object2.currentState != CurrentState.DEAD && 
    			!object1.gameObjectInactive && !object2.gameObjectInactive) {
      		switch(object1.group) {
      		case DROID:
      			switch(object2.group) {
      			case DROID_WEAPON:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY_LASER:
      				collisionTypeCheck = true;
      				break;
      				
      			case ASTRONAUT:
      				collisionTypeCheck = true;
      				break;
      				
      			// Droid does not require PLATFORM_LEVEL_START Collision Check
//      			case PLATFORM_LEVEL_START:
//      				collisionTypeCheck = true;
//      				break;
      				
      			case PLATFORM_LEVEL_END:
      				collisionTypeCheck = true;
      				break;
      				
      			case PLATFORM_SECTION_START:
      				if (object1.currentState != CurrentState.PLATFORM_SECTION_START || 
      					object1.currentState != CurrentState.PLATFORM_SECTION_END) {
      					collisionTypeCheck = true;
      				}
//      				collisionTypeCheck = true;
      				break;
      				
      			case PLATFORM_SECTION_END:
      				if (object1.currentState != CurrentState.PLATFORM_SECTION_START || 
  						object1.currentState != CurrentState.PLATFORM_SECTION_END) {
      					collisionTypeCheck = true;
      				}
//      				collisionTypeCheck = true;
      				break;
      				
      			case PLATFORM_ELEVATOR:
      				collisionTypeCheck = true;
      				break;
      				
//      			case PLATFORM:
//      				collisionCheckTest = true;
//      				break;
      				
      			case ITEM:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case DROID_WEAPON:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case DROID_LASER:
      			switch(object2.group) {
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			case ITEM:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case ENEMY:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
      			case DROID_LASER:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				if(object1.collisionAttackRadius && object2.collisionAttackRadius) {
      					collisionTypeCheck = true;
      				}
      				break;
      				
      			case ASTRONAUT:
      				if(object1.collisionAttackRadius && object2.collisionAttackRadius) {
      					collisionTypeCheck = true;
      				}
      				break;
      				
      			// Enemy in Patrol Mode at Level Start. Make sure Enemy does not Move towards Droid during stateLevelStart().
//      			case PLATFORM_LEVEL_START:
//      				collisionTypeCheck = true;
//      				break;
      				
      			case PLATFORM_LEVEL_END:
      				collisionTypeCheck = true;
      				break;
      				
      			case PLATFORM_SECTION_START:
      				collisionTypeCheck = true;
      				break;
      				
      			case PLATFORM_SECTION_END:
      				collisionTypeCheck = true;
      				break;
      				
//      			case PLATFORM_ELEVATOR:
//      				collisionTypeCheck = true;
//      				break;
      				
      			case ITEM:
      				collisionTypeCheck = true;
      				break;
      				
      			// FIXME TEMP. DELETE.
      			case SPECIAL_EFFECT:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case ENEMY_LASER:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
      			case ASTRONAUT:
      				if(object2.collisionAttackRadius) {
      					collisionTypeCheck = true;
      				}
      				break;
      				
      			case ITEM:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case ASTRONAUT:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				if(object1.collisionAttackRadius && object2.collisionAttackRadius) {
      					collisionTypeCheck = true;
      				}
      				break;
      				
      			case ENEMY_LASER:
      				if(object1.collisionAttackRadius) {
      					collisionTypeCheck = true;
      				}
      				break;
      				
      			case ASTRONAUT:
      				if(object1.collisionAttackRadius && object2.collisionAttackRadius) {
      					collisionTypeCheck = true;
      				}
      				break;
      				
      			// Make sure Astronaut does not Move towards Droid during stateLevelStart().
//      			case PLATFORM_LEVEL_START:
//    				collisionTypeCheck = true;
//    				break;
      				
    			case PLATFORM_LEVEL_END:
    				collisionTypeCheck = true;
    				break;
    				
    			case PLATFORM_SECTION_START:
    				collisionTypeCheck = true;
    				break;
    				
    			case PLATFORM_SECTION_END:
    				collisionTypeCheck = true;
    				break;
    				
//    			case PLATFORM_ELEVATOR:
//    				collisionTypeCheck = true;
//    				break;
    				
      			case ITEM:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		// PLATFORM_LEVEL_START not required
//      		case PLATFORM_LEVEL_START:
//      			switch(object2.group) {
//      			case DROID:
//      				collisionTypeCheck = true;
//      				break;
//      				
//      			case ENEMY:
//      				collisionTypeCheck = true;
//      				break;
//      				
//      			case ASTRONAUT:
//      				collisionTypeCheck = true;
//      				break;
//      				
//      			default:
//      				break;
//      			}
//      			
//      			break;
      			
      		case PLATFORM_LEVEL_END:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			case ASTRONAUT:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case PLATFORM_SECTION_START:
      			switch(object2.group) {
      			case DROID:
      				if (object2.currentState != CurrentState.PLATFORM_SECTION_START || 
  						object2.currentState != CurrentState.PLATFORM_SECTION_END) {
      					collisionTypeCheck = true;
      				}
//      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			case ASTRONAUT:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case PLATFORM_SECTION_END:
      			switch(object2.group) {
      			case DROID:
      				if (object2.currentState != CurrentState.PLATFORM_SECTION_START || 
  						object2.currentState != CurrentState.PLATFORM_SECTION_END) {
      					collisionTypeCheck = true;
      				}
//      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			case ASTRONAUT:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case PLATFORM_ELEVATOR:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
//      			case ENEMY:
//      				collisionTypeCheck = true;
//      				break;
//      				
//      			case ASTRONAUT:
//      				collisionTypeCheck = true;
//      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		case ITEM:
      			switch(object2.group) {
      			case DROID:
      				collisionTypeCheck = true;
      				break;
      				
      			case DROID_LASER:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			case ENEMY_LASER:
      				collisionTypeCheck = true;
      				break;
      				
      			case ASTRONAUT:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		// FIXME TEMP. DELETE.
      		case SPECIAL_EFFECT:
      			switch(object2.group) {
      			case ENEMY:
      				collisionTypeCheck = true;
      				break;
      				
      			default:
      				break;
      			}
      			
      			break;
      			
      		default:
      			break;
      		}
    	}
    	
    	return collisionTypeCheck;
    }
    
    /** Determine the HitType for object1 as a result of Collision */
    private CurrentState collisionHitType(GameObject object1, GameObject object2) {
//    private HitType collisionHitType(GameObject object1, GameObject object2) {
    	CurrentState object1HitType = CurrentState.NO_HIT;
//    	HitType object1HitType = CurrentState.NO_HIT;

    	switch(object1.group) {
    	case DROID:
    		switch(object2.group) {
    		case DROID_WEAPON:
    			mCollisionWeaponGameObject = object2;
    			// NO_HIT for Droid
    			break;
    			
    		case ENEMY:
    			object1HitType = CurrentState.HIT;
//    			object1HitType = CurrentState.DEAD;
    			break;
    			
    		case ENEMY_LASER:
    			switch(object2.type) {
    			case ENEMY_LASER_STD:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case ENEMY_LASER_EMP:
    				object1HitType = CurrentState.FROZEN;
    				break;
    				
    			case ENEMY_BOSS_LASER_STD:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case ENEMY_BOSS_LASER_EMP:
    				object1HitType = CurrentState.FROZEN;
    				break;
    				
    			case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			default:
    				object1HitType = CurrentState.HIT;
    				break;
    			}
//    			object1HitType = CurrentState.HIT;
    			break;
    			
			case PLATFORM_LEVEL_END:
				object1HitType = CurrentState.LEVEL_END;	
//				if (!object2.platformCompleted) {
//					object1HitType = CurrentState.LEVEL_END;	
//				}
				break;
				
			case PLATFORM_SECTION_START:
				object1HitType = CurrentState.PLATFORM_SECTION_START;
//				if (!object2.platformCompleted) {
//					object1HitType = CurrentState.PLATFORM_SECTION_START;	
//				}
				break;
				
			case PLATFORM_SECTION_END:
				object1HitType = CurrentState.PLATFORM_SECTION_END;	
//				if (!object2.platformCompleted) {
////				if (!object1.platformCompleted) {
//					object1HitType = CurrentState.PLATFORM_SECTION_END;	
////					object1HitType = CurrentState.TELEPORT;	
//				}
				break;
				
			case PLATFORM_ELEVATOR:
				object1HitType = CurrentState.ELEVATOR;	
//				if (!object2.platformCompleted) {
//    				object1HitType = CurrentState.ELEVATOR;	
//				}
				break;
    			
    		case ITEM:
    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		default:
    			break;
    		}
    		
    		break;
    		
    	case DROID_LASER:
    		switch(object2.group) {
    		case ENEMY:
    			switch(object1.type) {
    			case DROID_LASER_STD:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case DROID_LASER_PULSE:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case DROID_LASER_EMP:
        			object1.hitReactType = Type.ELECTRICITY;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case DROID_LASER_GRENADE:
        			object1.hitReactType = Type.EXPLOSION_LARGE;
        			object1HitType = CurrentState.WAIT_FOR_DEAD;
//        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case DROID_LASER_ROCKET:
        			object1.hitReactType = Type.EXPLOSION_RING;
        			object1HitType = CurrentState.WAIT_FOR_DEAD;
//        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			default:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    			}
    			
    			break;
    			
    		case ITEM:
    			// Ignore hitReactType
    			object1HitType = CurrentState.DEAD;
    			
//    			switch(object1.type) {
//    			case DROID_LASER_STD:
//        			object1.hitReactType = Type.EXPLOSION;
//        			object1HitType = CurrentState.DEAD;
//    				break;
//    				
//    			case DROID_LASER_PULSE:
//        			object1.hitReactType = Type.EXPLOSION;
//        			object1HitType = CurrentState.DEAD;
//    				break;
//    				
//    			case DROID_LASER_EMP:
//        			object1.hitReactType = Type.EXPLOSION;
//        			object1HitType = CurrentState.DEAD;
//    				break;
//    				
//    			case DROID_LASER_GRENADE:
//        			object1.hitReactType = Type.EXPLOSION;
//        			object1HitType = CurrentState.WAIT_FOR_DEAD;
////        			object1HitType = CurrentState.DEAD;
//    				break;
//    				
//    			case DROID_LASER_ROCKET:
//        			object1.hitReactType = Type.EXPLOSION;
//        			object1HitType = CurrentState.WAIT_FOR_DEAD;
////        			object1HitType = CurrentState.DEAD;
//    				break;
//    				
//    			default:
//        			object1.hitReactType = Type.EXPLOSION;
//        			object1HitType = CurrentState.DEAD;
//    				break;
//    			}
    			break;
    			
    		default:
    			break;
    		}
    		
    		break;
    		
    	case ENEMY:
    		switch(object2.group) {
    		case DROID:
    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case DROID_LASER:
    			switch(object2.type) {
    			case DROID_LASER_STD:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case DROID_LASER_PULSE:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case DROID_LASER_EMP:
    				object1HitType = CurrentState.FROZEN;
    				break;
    				
    			case DROID_LASER_GRENADE:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case DROID_LASER_ROCKET:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			default:
    				object1HitType = CurrentState.HIT;
    				break;
    			}
    			
//    			object1HitType = CurrentState.HIT;
    			break;
    			
    		case ENEMY:
    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case ASTRONAUT:
    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		// Not required
//			case PLATFORM_LEVEL_START:
//				object1HitType = CurrentState.BOUNCE;
//				break;
    			
			case PLATFORM_LEVEL_END:
				object1HitType = CurrentState.BOUNCE;
				break;
				
			case PLATFORM_SECTION_START:
				object1HitType = CurrentState.BOUNCE;
				break;
				
			case PLATFORM_SECTION_END:
				object1HitType = CurrentState.BOUNCE;
				break;
				
			case PLATFORM_ELEVATOR:
				object1HitType = CurrentState.BOUNCE;
				break;
    			
//    		case PLATFORM:
//    			switch(object2.type) {
//    			case PLATFORM_LEVEL_END:
//    				object1HitType = CurrentState.BOUNCE;
//    				break;
//    				
//    			case PLATFORM_SECTION_END:
//    				object1HitType = CurrentState.BOUNCE;
//    				break;
//    				
//    			case PLATFORM_ELEVATOR:
//    				object1HitType = CurrentState.BOUNCE;
//    				break;
//    				
//    			default:
//    				break;
//    			}
//    			break;
    			
    		case ITEM:
    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case SPECIAL_EFFECT:
    			object1HitType = CurrentState.HIT;
    			break;
    			
    		default:
    			break;
    		}
    		
    		break;
    		
    	case ENEMY_LASER:
    		switch(object2.group) {
    		case DROID:
    			switch(object1.type) {
    			case ENEMY_LASER_STD:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_LASER_EMP:
        			object1.hitReactType = Type.ELECTRICITY;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_BOSS_LASER_STD:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_BOSS_LASER_EMP:
        			object1.hitReactType = Type.ELECTRICITY;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
        			object1.hitReactType = Type.EXPLOSION_RING;
        			object1HitType = CurrentState.WAIT_FOR_DEAD;
    				break;
    				
    			default:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    			}
//    			object1.hitReactType = Type.EXPLOSION;
//    			object1HitType = CurrentState.DEAD;
    			break;
    			
    		case ASTRONAUT:
    			switch(object1.type) {
    			case ENEMY_LASER_STD:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_LASER_EMP:
        			object1.hitReactType = Type.ELECTRICITY;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_BOSS_LASER_STD:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_BOSS_LASER_EMP:
        			object1.hitReactType = Type.ELECTRICITY;
        			object1HitType = CurrentState.DEAD;
    				break;
    				
    			case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
        			object1.hitReactType = Type.EXPLOSION_RING;
        			object1HitType = CurrentState.WAIT_FOR_DEAD;
    				break;
    				
    			default:
        			object1.hitReactType = Type.EXPLOSION;
        			object1HitType = CurrentState.DEAD;
    				break;
    			}
//    			object1.hitReactType = Type.EXPLOSION;
//    			object1HitType = CurrentState.DEAD;
    			break;
    			
    		case ITEM:
    			// Ignore hitReactType
    			object1HitType = CurrentState.DEAD;
    			break;
    			
    		default:
    			break;
    		}
    		
    		break;
    		
    	case ASTRONAUT:
    		switch(object2.group) {
    		case DROID:
           		if (object2.hitPoints > 0) {
           			object2.hitPoints = sSystemRegistry.gameObjectFactory.MAX_DROID_LIFE;	
           		}
//           		if (object2.hitPoints > 0 && object2.hitPoints < 3) {
//           			object2.hitPoints++;	
//           		}
    			object1HitType = CurrentState.COLLECT;
    			break;
    			
    		case ENEMY:
    			object1HitType = CurrentState.HIT;
//    			object1HitType = CurrentState.DEAD;
    			break;
    			
    		case ENEMY_LASER:
    			switch(object2.type) {
    			case ENEMY_LASER_STD:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case ENEMY_LASER_EMP:
    				object1HitType = CurrentState.FROZEN;
    				break;
    				
    			case ENEMY_BOSS_LASER_STD:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			case ENEMY_BOSS_LASER_EMP:
    				object1HitType = CurrentState.FROZEN;
    				break;
    				
    			case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
    				object1HitType = CurrentState.HIT;
    				break;
    				
    			default:
    				object1HitType = CurrentState.HIT;
    				break;
    			}
//    			object1HitType = CurrentState.DEAD;
    			break;
    			
    		case ASTRONAUT:
    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		// Not required
//			case PLATFORM_LEVEL_START:
//				object1HitType = CurrentState.BOUNCE;
//				break;
    			
			case PLATFORM_LEVEL_END:
				object1HitType = CurrentState.BOUNCE;
				break;
				
			case PLATFORM_SECTION_START:
				object1HitType = CurrentState.BOUNCE;
				break;
				
			case PLATFORM_SECTION_END:
				object1HitType = CurrentState.BOUNCE;
				break;
				
			case PLATFORM_ELEVATOR:
				object1HitType = CurrentState.BOUNCE;
				break;
    			
//    		case PLATFORM:
//    			switch(object2.type) {
//    			case PLATFORM_LEVEL_END:
//    				object1HitType = CurrentState.BOUNCE;
//    				break;
//    				
//    			case PLATFORM_SECTION_END:
//    				object1HitType = CurrentState.BOUNCE;
//    				break;
//    				
//    			case PLATFORM_ELEVATOR:
//    				object1HitType = CurrentState.BOUNCE;
//    				break;
//    				
//    			default:
//    				break;
//    			}
//    			break;
    			
    		case ITEM:
    			object1HitType = CurrentState.BOUNCE;
    			break;		
    			
    		default:
    			break;
    		}
    		
    		break;
    		
    	// Not required
//		case PLATFORM_LEVEL_START:
//    		switch(object2.group) {
//    		case DROID:
//				object1HitType = CurrentState.NO_HIT;
////				if (!object1.platformCompleted) {
////    				object1HitType = CurrentState.LEVEL_END;	
////				}
//				break;
//				
//    		case ENEMY:
//    			object1HitType = CurrentState.NO_HIT;
////    			object1HitType = CurrentState.BOUNCE;
//    			break;
//    			
//    		case ASTRONAUT:
//    			object1HitType = CurrentState.NO_HIT;
////    			object1HitType = CurrentState.BOUNCE;
//    			break;
//				
//    		default:
//    			break;
//    		}
//    		
//    		break;
    		
		case PLATFORM_LEVEL_END:
    		switch(object2.group) {
    		case DROID:
				object1HitType = CurrentState.LEVEL_END;
//				if (!object1.platformCompleted) {
//    				object1HitType = CurrentState.LEVEL_END;	
//				}
				break;
				
    		case ENEMY:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case ASTRONAUT:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
				
    		default:
    			break;
    		}
    		
    		break;
    		
		case PLATFORM_SECTION_START:
    		switch(object2.group) {
    		case DROID:
				object1HitType = CurrentState.PLATFORM_SECTION_START;
//				if (!object1.platformCompleted) {
//    				object1HitType = CurrentState.PLATFORM_SECTION_START;
//				}
				break;
				
    		case ENEMY:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case ASTRONAUT:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
				
    		default:
    			break;
    		}
    		
    		break;
    		
		case PLATFORM_SECTION_END:
    		switch(object2.group) {
    		case DROID:
    			object1HitType = CurrentState.PLATFORM_SECTION_END;
//				if (!object1.platformCompleted) {
////				if (!object1.elevatorCompleted) {
//    				object1HitType = CurrentState.PLATFORM_SECTION_END;
////    				object1HitType = CurrentState.TELEPORT;
//				}
    			break;
    			
    		case ENEMY:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case ASTRONAUT:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
				
    		default:
    			break;
    		}
    		
    		break;
    		
		case PLATFORM_ELEVATOR:
    		switch(object2.group) {
    		case DROID:
				object1HitType = CurrentState.ELEVATOR;	
//				if (!object1.platformCompleted) {
//    				object1HitType = CurrentState.ELEVATOR;	
//				}
				break;
				
    		case ENEMY:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
    			
    		case ASTRONAUT:
    			object1HitType = CurrentState.NO_HIT;
//    			object1HitType = CurrentState.BOUNCE;
    			break;
				
    		default:
    			break;
    		}
    		
    		break;
    		
//		case PLATFORM:
//    		switch(object2.group) {
//    		case DROID:
//    			switch(object1.type) {				
//    			case PLATFORM_ELEVATOR:
//    				if (!object1.elevatorCompleted) {
//        				object1HitType = CurrentState.ELEVATOR;	
//    				}
//    				break;
//    			default:
//    				break;
//    			}
//				
//    		default:
//    			break;
//    		}
    		
		case SPECIAL_EFFECT:
    		switch(object2.group) {
    		case ENEMY:
    			object1HitType = CurrentState.NO_HIT;
    			break;

    		default:
    			break;
    		}
    		
    		break;
    	
    	default:
    		break;
    	}
    	
    	return object1HitType;
    }
    
//	private boolean backgroundCollisionCheck(GameObject receiveGameObject, LineSegment backgroundLineSegment) {
//		boolean collision = false;
//		
//    	float receiveVx = receiveGameObject.getVelocityX();
//    	float receiveVz = receiveGameObject.getVelocityZ();
//    	
//    	float lineDx = backgroundLineSegment.getUnitVectorX();
//    	float lineDz = backgroundLineSegment.getUnitVectorZ();
//    	
//    	float dp1 = (receiveVx * lineDx) + (receiveVz * lineDz);
//    	
//    	float projection1X = dp1 * lineDx;
//    	float projection1Z = dp1 * lineDz;
//    	
//    	// Background LineSegments use Right-Side Collision Boundaries and LeftUnitNormals for Bounce
//    	float lineLnX = backgroundLineSegment.getLeftUnitNormalX();
//    	float lineLnZ = backgroundLineSegment.getLeftUnitNormalZ();
//    	
//    	float dp2 = (receiveVx * lineLnX) + (receiveVz * lineLnZ);
//    	
//    	if (receiveGameObject.gameObjectId == 14) {
//        	Log.i("CollisionCheck", "GameObjectCollisionSystem backgroundCollisionCheck dp2 = " + dp2);
//        	
//        	if (dp2 < 0.0f) {
//            	Log.i("CollisionCheck", "GameObjectCollisionSystem backgroundCollisionCheck collision = true");
//        	} else {
//            	Log.i("CollisionCheck", "GameObjectCollisionSystem backgroundCollisionCheck collision = false");
//        	}	
//    	}
//    	
//    	if (dp2 < 0.0f) {
//    		// FIXME RE-ENABLE
////    		collision = true;
//    		
//        	float projection2X = dp2 * lineLnX;
//        	float projection2Z = dp2 * lineLnZ;
//        	
//        	projection2X *= -1.0f;
//        	projection2Z *= -1.0f;
//
//        	float bounceVx = projection1X + projection2X;
//        	float bounceVz = projection1Z + projection2Z;
//        	
//        	receiveGameObject.currentPosition.r = backgroundCollisionAngleCalc(bounceVx, bounceVz);
//        	
//        	receiveGameObject.setBouncePosition(bounceVx, 0.0f, bounceVz, 0.0f);
//    	}
//    	
//    	return collision;
//	}
//	
//	private float backgroundCollisionAngleCalc(float bounceVx, float bounceVz) {
//		float collisionAngle = 0.0f;
//
//		// Angle in Degrees
//		if (bounceVx <= 0.0f) {
//			if (bounceVz <= 0.0f) {
//				if (bounceVz == 0) {
//					// Set to minimal denominator
//					collisionAngle = (float)Math.atan(
//							(-bounceVx) /
//							0.001f) * ONE_EIGHTY_OVER_PI;
//				} else {
//					collisionAngle = (float)Math.atan(
//							(-bounceVx) /
//							(-bounceVz)) * ONE_EIGHTY_OVER_PI;
//				}
//			} else {
//				if ((0.0f - bounceVx) == 0) {
//					// Set to minimal denominator
//					collisionAngle = 90.0f + (float)Math.atan(
//							(bounceVz) /
//							0.001f) * ONE_EIGHTY_OVER_PI;
//				} else {
//					collisionAngle = 90.0f + (float)Math.atan(
//							(bounceVz) /
//							(-bounceVx)) * ONE_EIGHTY_OVER_PI;
//				}
//			}
//		} else {
//			if (bounceVz > 0.0f) {
//				collisionAngle = 180.0f + (float)Math.atan(
//						(bounceVx) /
//						(bounceVz)) * ONE_EIGHTY_OVER_PI;
//			} else {
//				collisionAngle = 270.0f + (float)Math.atan(
//						(-bounceVz) /
//						(bounceVx)) * ONE_EIGHTY_OVER_PI;
//			}
//		}
//		return collisionAngle;
//	}
    
    private void setAxis(int a, Vector3 lineSegmentAxis) {    	
    	mAxisX[a] = lineSegmentAxis.x;
    	mAxisZ[a] = lineSegmentAxis.z;
    }
    
    private void setAxis(int a, LineSegment lineSegment) {    	
    	mAxisX[a] = lineSegment.x2 - lineSegment.x1;
    	mAxisZ[a] = lineSegment.z2 - lineSegment.z1;
    }
    
    private void setAxis(int a, CollisionRectangle rectangle, int i, int j) {    	
    	mAxisX[a] = rectangle.pointX[i] - rectangle.pointX[j];
    	mAxisZ[a] = rectangle.pointZ[i] - rectangle.pointZ[j];
    }
    
    // Line Segment Magnitude using Vector X and Vector Z
    private float getMagnitude(float vx, float vz) {
    	float magnitude = (float)Math.sqrt((vx * vx) + (vz * vz));
    	return magnitude;
    }
    
    // Line Segment Magnitude using StartPoint and EndPoint
    private float getMagnitude(float x1, float z1, float x2, float z2) {
    	float vx = x2 - x1;
    	float vz = z2 - z1;
    	
    	float magnitude = (float)Math.sqrt((vx * vx) + (vz * vz));
    	return magnitude;
    }

    
    // DynamicGameObject Magnitude using previousPosition and currentPosition
    private float getMagnitude(GameObject dynamicGameObject) {
    	float vx = dynamicGameObject.currentPosition.x - dynamicGameObject.previousPosition.x;
    	float vz = dynamicGameObject.currentPosition.z - dynamicGameObject.previousPosition.z;
    			
    	float magnitude = (float)Math.sqrt((vx * vx) + (vz * vz));
    	return magnitude;
    }
    
    // DynamicGameObject Vector x
    private float getVectorX(GameObject dynamicGameObject) {
//    	float velocityX = dynamicGameObject.currentPosition.x - dynamicGameObject.previousPosition.x;
//    	
//    	// Normalize velocityX in event of Bounce, other
//    	if (velocityX > 0.09f) {
//    		velocityX = 0.09f;
//    	} else if (velocityX < -0.09f) {
//    		velocityX = -0.09f;
//    	}
//    	
//    	return velocityX;
    	return (dynamicGameObject.currentPosition.x - dynamicGameObject.previousPosition.x);
    }
    
    // DynamicGameObject Vector z
    private float getVectorZ(GameObject dynamicGameObject) {
//    	float velocityZ = dynamicGameObject.currentPosition.z - dynamicGameObject.previousPosition.z;
//    	
//    	// Normalize velocityZ in event of Bounce, other
//    	if (velocityZ > 0.09f) {
//    		velocityZ = 0.09f;
//    	} else if (velocityZ < -0.09f) {
//    		velocityZ = -0.09f;
//    	}
//    	
//    	return velocityZ;
    	return (dynamicGameObject.currentPosition.z - dynamicGameObject.previousPosition.z);
    }
    
    // Line Segment Unit Vector x  (Normalized x Vector)
    private float getUnitVectorX(float x1, float x2, float magnitude) {
    	if (magnitude != 0 ) {
        	return (x2 - x1) / magnitude;
    	} else {
    		return 0.0f;
    	}
    }
    
    // Line Segment Unit Vector x value using GameObject previousPosition and currentPosition (Normalized x Vector)
    private float getUnitVectorX(GameObject dynamicGameObject) {
    	float vx = dynamicGameObject.currentPosition.x - dynamicGameObject.previousPosition.x;
    	float vz = dynamicGameObject.currentPosition.z - dynamicGameObject.previousPosition.z;
    			
    	float magnitude = (float)Math.sqrt((vx * vx) + (vz * vz));
    	
    	if (magnitude != 0 ) {
        	return vx / magnitude;
    	} else {
    		return 0.0f;
    	}
    }
    
    // Line Segment Unit Vector z value (Normalized z Vector)
    private float getUnitVectorZ(float z1, float z2, float magnitude) {
    	if (magnitude != 0 ) {
        	return (z2 - z1) / magnitude;
    	} else {
    		return 0.0f;
    	}
    }
    
    // Line Segment Unit Vector z value using GameObject previousPosition and currentPosition (Normalized z Vector)
    private float getUnitVectorZ(GameObject dynamicGameObject) {
    	float vx = dynamicGameObject.currentPosition.x - dynamicGameObject.previousPosition.x;
    	float vz = dynamicGameObject.currentPosition.z - dynamicGameObject.previousPosition.z;
    			
    	float magnitude = (float)Math.sqrt((vx * vx) + (vz * vz));
    	
    	if (magnitude != 0 ) {
        	return vz / magnitude;
    	} else {
    		return 0.0f;
    	}
    }
    
    // Left Normal X for GameObject previousPosition and currentPosition
    private float getLeftNormalX(GameObject dynamicGameObject) {
    	return (dynamicGameObject.currentPosition.z - dynamicGameObject.previousPosition.z);
    }
    
    // Left Normal X from GameObject previousPosition to LineSegment StartPoint
    private float getLeftNormalX(GameObject dynamicGameObject, LineSegment backgroundLineSegment) {
    	return (backgroundLineSegment.z1 - dynamicGameObject.previousPosition.z);
    }
    
    // Left Normal Z for GameObject previousPosition and currentPosition
    private float getLeftNormalZ(GameObject dynamicGameObject) {
    	return (-(dynamicGameObject.currentPosition.x - dynamicGameObject.previousPosition.x));
    }
    
    // Left Normal Z from GameObject previousPosition to LineSegment StartPoint
    private float getLeftNormalZ(GameObject dynamicGameObject, LineSegment backgroundLineSegment) {
    	return (-(backgroundLineSegment.x1 - dynamicGameObject.previousPosition.x));
    }
    
    public GameObject getCollisionWeaponGameObject() {
    	return mCollisionWeaponGameObject;
    }
    
    public void setCollisionWeaponGameObjectNull() {
    	mCollisionWeaponGameObject = null;
    }
    
    /** A record of a Dynamic GameObject and its associated collision info.  */
    private class DynamicCollisionVolumeRecord extends AllocationGuard {
    	// FIXME Re-verify that these public Objects are OK as used, otherwise change to private with get/set
        public GameObject gameObject;
        public HitReactionComponent reactionComponent;
        public OBBCollisionVolume collisionVolume;
        
        public void reset() {
            gameObject = null;
            collisionVolume = null;
            reactionComponent = null;
        }
    }
    
    /** A pool of Dynamic Collision Volume Records.  */
    private class DynamicCollisionVolumeRecordPool extends TObjectPool<DynamicCollisionVolumeRecord> {

        public DynamicCollisionVolumeRecordPool(int count) {
            super(count);
        }
        
        @Override
        protected void fill() {
            for (int x = 0; x < getSize(); x++) {
                getAvailable().add(new DynamicCollisionVolumeRecord());
            }
        }

        @Override
        public void release(Object entry) {
            ((DynamicCollisionVolumeRecord)entry).reset();
            super.release(entry);
        }
    }
    
    /** A record of a Background GameObject and its associated collision info.  */
    private class BackgroundCollisionVolumeRecord extends AllocationGuard {
    	// FIXME Re-verify that these public Objects are OK as used, otherwise change to private with get/set
        public GameObject gameObject;
        public HitReactionComponent reactionComponent;
        public FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes;
        
        public void reset() {
            gameObject = null;
            collisionVolumes = null;
            reactionComponent = null;
        }
    }
    
    /** A pool of Background Collision Volume Records.  */
    private class BackgroundCollisionVolumeRecordPool extends TObjectPool<BackgroundCollisionVolumeRecord> {

        public BackgroundCollisionVolumeRecordPool(int count) {
            super(count);
        }
        
        @Override
        protected void fill() {
            for (int x = 0; x < getSize(); x++) {
                getAvailable().add(new BackgroundCollisionVolumeRecord());
            }
        }

        @Override
        public void release(Object entry) {
            ((BackgroundCollisionVolumeRecord)entry).reset();
            super.release(entry);
        }
    }
    
    // FIXME Comparator disabled, so FixedSizeArray will default to standard Array sorter if sort() called. Study if required.
//    /** 
//     * Comparator for game objects that considers the world position of the object's bounding
//     * volume and sorts objects from left to right on the x axis. */
//    public static final class CollisionVolumeComparator implements Comparator<CollisionVolumeRecord> {
////        private static CollisionVolume.FlipInfo sCompareFlip = new CollisionVolume.FlipInfo();
//    	
//        public int compare(CollisionVolumeRecord object1, CollisionVolumeRecord object2) {
//        	
//            int result = 0;
//            if (object1 == null && object2 != null) {
//                result = 1;
//            } else if (object1 != null && object2 == null) {
//                result = -1;
//            } else if (object1 != null && object2 != null) {
////                sCompareFlip.flipX = (object1.object.facingDirection.x < 0.0f);
//////                sCompareFlip.flipY = (object1.object.facingDirection.y < 0.0f);
////                sCompareFlip.flipZ = (object1.object.facingDirection.z < 0.0f);
////                sCompareFlip.parentWidth = object1.object.width;
////                sCompareFlip.parentHeight = object1.object.height;
////                sCompareFlip.parentDepth = object1.object.depth;
//                
////                final float minX1 = object1.object.position.x 
////                + object1.boundingVolume.getMinX();
//            	
//////                final float minX1 = object1.object.getPosition().x 
//////                + object1.boundingVolume.getMinX();
//////                final float minX1 = object1.object.getPosition().x 
//////                    + object1.boundingVolume.getMinXPosition(sCompareFlip);
////                
//////                sCompareFlip.flipX = (object2.object.facingDirection.x < 0.0f);
////////                sCompareFlip.flipY = (object2.object.facingDirection.y < 0.0f);
//////                sCompareFlip.flipZ = (object2.object.facingDirection.z < 0.0f);
//////                sCompareFlip.parentWidth = object2.object.width;
//////                sCompareFlip.parentHeight = object2.object.height;
//////                sCompareFlip.parentDepth = object2.object.depth;
//                
////                final float minX2 = object2.object.position.x 
////                + object2.boundingVolume.getMinX();
//            	
//////                final float minX2 = object2.object.getPosition().x 
//////                + object2.boundingVolume.getMinX();
//////                final float minX2 = object2.object.getPosition().x 
//////                    + object2.boundingVolume.getMinXPosition(sCompareFlip);
//                
////                final float delta = minX1 - minX2;
////                
////                if (delta < 0.0f) {
////                    result = -1;
////                } else if (delta > 0.0f) {
////                    result = 1;
////                }
//            }
//            return result;
//        }
//    }
}
