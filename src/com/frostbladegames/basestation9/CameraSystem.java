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


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;

import android.util.Log;
//import com.frostbladegames.droidconstruct.GameObjectGroups.CurrentState;

/**
 * Manages the position of the camera based on a target game object.
 */
public class CameraSystem extends BaseObject {
//    private static final float X_FOLLOW_DISTANCE = 0.0f;
//    private static final float Y_UP_FOLLOW_DISTANCE = 90.0f; 
//    private static final float Y_DOWN_FOLLOW_DISTANCE = 0.0f; 
//    
//    private static final float MAX_INTERPOLATE_TO_TARGET_DISTANCE = 300.0f;
//    private static final float INTERPOLATE_TO_TARGET_TIME = 1.0f;
//    
//    private static int SHAKE_FREQUENCY = 40;
//    
//    private static float BIAS_SPEED = 400.0f;
    
	private final Vector3 mLevel01IntroOffset = new Vector3(55.625f, 0.0f, -55.625f, 0.0f);
	
    private GameObject mTarget;
	private GameObject mDroid;	// Used for Intro
    
//    private float mShakeTime;
//    private float mShakeMagnitude;
//    private float mShakeOffsetY;
//    private float mTargetChangedTime;
    
//    private Vector3 mCurrentCameraPosition;
    private Vector3 mFocusPosition;
//    private Vector3 mFocalPosition;
//    private Vector3 mPreInterpolateCameraPosition;
//    private Vector3 mTargetPosition;
//    private Vector3 mBias;
    
    private Vector3 mViewangle;
//    private float mViewangleY;
    private float mFarBackgroundAngle;
    
    public CameraSystem() {
        super();
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "CameraSystem <constructor>");	
        }
        
//        mCurrentCameraPosition = new Vector3();
        mFocusPosition = new Vector3();
//        mPreInterpolateCameraPosition = new Vector3();
//        mTargetPosition = new Vector3();
//        mBias = new Vector3();
        
        mViewangle = new Vector3();
        
        mFarBackgroundAngle = 303.0f;	// default
    }
    
    @Override
    public void reset() {
        mTarget = null;
    	mDroid = null;
        
//        mShakeTime = 0.0f;
//        mShakeMagnitude = 0.0f;
        
        mFocusPosition.zero();
//        mCurrentCameraPosition.zero();

//        mTargetChangedTime = 0.0f;
        
//        mViewangleY = 0.0f;
        
//        mPreInterpolateCameraPosition.zero();
//        mTargetPosition.zero();
        
        mFarBackgroundAngle = 303.0f;	// default
    }
    
    void setDroid(GameObject droid) {
    	mDroid = droid;
    }
    
    void setTargetDroid() {
        mTarget = mDroid; 
//        mCurrentCameraPosition.set(mDroid.currentPosition);
    }
    
    void setTarget(GameObject target) {
    	// FIXME RE-ENABLE
//        if (target != null && mTarget != target) {
//            mPreInterpolateCameraPosition.set(mCurrentCameraPosition);
//            mPreInterpolateCameraPosition.subtract(target.getPosition());
//            if (mPreInterpolateCameraPosition.length2() < 
//                    MAX_INTERPOLATE_TO_TARGET_DISTANCE * MAX_INTERPOLATE_TO_TARGET_DISTANCE) {
//                final TimeSystem time = sSystemRegistry.timeSystem;
//                mTargetChangedTime = time.getGameTime();
//                mPreInterpolateCameraPosition.set(mCurrentCameraPosition);
//            } else {
//            	mTargetChangedTime = 0.0f;
//                mCurrentCameraPosition.set(target.getPosition());
//            }
//        }
        
//        mCurrentCameraPosition.set(target.currentPosition);
        
        mTarget = target;
        
    }
    
    public GameObject getTarget() {
		return mTarget;
	}
    
//    void shake(float duration, float magnitude) {
//        mShakeTime = duration;
//        mShakeMagnitude = magnitude;
//    }
    
//    public boolean shaking() {
//        return mShakeTime > 0.0f;
//    }

    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
//        Log.i("Loop", "CameraSystem update()");
        
//        mShakeOffsetY = 0.0f;
        
        // TODO Re-enable mShakeTime calculation
//        if (mShakeTime > 0.0f) {
//            mShakeTime -= timeDelta;
//            mShakeOffsetY = (float) (Math.sin(mShakeTime * SHAKE_FREQUENCY) * mShakeMagnitude);
//        }
        
        // FIXME RE-ENABLE
//        if (mTarget != null) {
//        	mTargetPosition.set(mTarget.getCenteredPositionX(), mTarget.getCenteredPositionY());
//            final Vector3 targetPosition = mTargetPosition;
//            
//            if (mTargetChangedTime > 0.0f) {
//                final TimeSystem time = sSystemRegistry.timeSystem;
//                final float delta = time.getGameTime() - mTargetChangedTime;
//                
//                mCurrentCameraPosition.x = Lerp.ease(mPreInterpolateCameraPosition.x, 
//                        targetPosition.x, INTERPOLATE_TO_TARGET_TIME, delta);
//                
//                mCurrentCameraPosition.y = Lerp.ease(mPreInterpolateCameraPosition.y, 
//                        targetPosition.y, INTERPOLATE_TO_TARGET_TIME, delta);
//                
//                if (delta > INTERPOLATE_TO_TARGET_TIME) {
//                    mTargetChangedTime = -1;
//                }
//            } else {
//            	
//            	// Only respect the bias if the target is moving.  No camera motion without 
//            	// player input!
//            	if (mBias.length2() > 0.0f && mTarget.getVelocity().length2() > 1.0f) {
//                	mBias.normalize();
//                	mBias.multiply(BIAS_SPEED * timeDelta);
//                	mCurrentCameraPosition.add(mBias);
//                }
//            	
//                final float xDelta = targetPosition.x - mCurrentCameraPosition.x;
//                if (Math.abs(xDelta) > X_FOLLOW_DISTANCE) {
//                    mCurrentCameraPosition.x = targetPosition.x - (X_FOLLOW_DISTANCE * Utils.sign(xDelta));
//                }
//                
//               
//                final float yDelta = targetPosition.y - mCurrentCameraPosition.y;
//                if (yDelta > Y_UP_FOLLOW_DISTANCE) {
//                    mCurrentCameraPosition.y = targetPosition.y - Y_UP_FOLLOW_DISTANCE;
//                } else if (yDelta < -Y_DOWN_FOLLOW_DISTANCE) {
//                    mCurrentCameraPosition.y = targetPosition.y + Y_DOWN_FOLLOW_DISTANCE;
//                }
//            }
//        	mBias.zero();
//        }
        
        if (GameParameters.levelRow == 1 && mTarget.currentState == CurrentState.LEVEL_START) {
//        	mCurrentCameraPosition.set(mLevel01IntroOffset);
        	mFocusPosition.set(mLevel01IntroOffset);
        } else {
//        	mCurrentCameraPosition.set(mTarget.currentPosition);
        	mFocusPosition.set(mTarget.currentPosition);
        }
    	
//    	mCurrentCameraPosition.set(mTarget.currentPosition);
//    	mFocalPosition.set(mTarget.currentPosition);
////    	mCurrentCameraPosition.set(mTarget.position);
////    	mFocalPosition.set(mTarget.position);
////    	mCurrentCameraPosition.set(mTarget.getPosition());
////    	mFocalPosition.set(mTarget.getPosition());
        
        // FIXME RE-ENABLE
//        mFocalPosition.x = (float) Math.floor(mCurrentCameraPosition.x);
//        mFocalPosition.x = snapFocalPointToWorldBoundsX(mFocalPosition.x);
//        
//        mFocalPosition.y = (float) Math.floor(mCurrentCameraPosition.y + mShakeOffsetY);
//        mFocalPosition.y = snapFocalPointToWorldBoundsY(mFocalPosition.y);
    }
    
    /** Returns the x position of the camera's look-at point. */
    public float getFocusPositionX() {
        return mFocusPosition.x;
    }
    
    /** Returns the y position of the camera's look-at point. */
    public float getFocusPositionY() {
        return mFocusPosition.y;
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    /** Returns the z position of the camera's look-at point. */
    public float getFocusPositionZ() {
        return mFocusPosition.z;
    }
    
    public void setFocusPosition() {
//    public void setFocusPositionDroid() {
    	mFocusPosition.set(mTarget.currentPosition);
//    	mFocusPosition.set(mDroid.currentPosition);
    }
    
    public void setFocusPosition(Vector3 position) {
    	mFocusPosition.set(position);
    }
    
//    public void setCameraPosition(Vector3 position) {
//    	mCurrentCameraPosition.set(position);
//    	mFocalPosition.set(position);
//    }
//    
//    public void setCameraPosition(float x, float y, float z, float r) {
//    	mCurrentCameraPosition.set(x, y, z, r);
//    	mFocalPosition.set(x, y, z, r);
//    }
    
//    public CurrentState getTargetCurrentState() {
//    	return mTarget.currentState;
//    }
    
    public Vector3 getViewangle() {
    	return mViewangle;
    }
    
    public void setViewangle(float x, float y, float z) {
    	mViewangle.set(x, y, z);
    	
    	if (GameParameters.levelRow == 0) {
    		// Intro
    		mFarBackgroundAngle = 0.0f;
    	} else {
        	// Set FarBackgroundAngle based on gluLookAt() angle setting:  2.0f, 6.0f, 10.0f (default), 14.0f, 18.0f
        	switch ((int)y) {
        	case 2:
        		mFarBackgroundAngle = 350.0f;
        		break;
        		
        	case 6:
        		mFarBackgroundAngle = 329.0f;
        		break;
        		
        	case 10:
        		mFarBackgroundAngle = 303.0f;
        		break;
        		
        	case 14:
        		mFarBackgroundAngle = 288.0f;
        		break;
        		
        	case 18:
        		mFarBackgroundAngle = 277.0f;
        		break;
        		
        	default:
        		mFarBackgroundAngle = 303.0f;
        		break;
        	}
    	}
    }
    
    public void setViewangle(Vector3 viewAngle) {
    	mViewangle.set(viewAngle);
    	
    	if (GameParameters.levelRow == 0) {
    		// Intro
    		mFarBackgroundAngle = 0.0f;
    	} else {
        	// Set FarBackgroundAngle based on gluLookAt() angle setting:  2.0f, 6.0f, 10.0f (default), 14.0f, 18.0f
        	switch ((int)viewAngle.y) {
        	case 2:
        		mFarBackgroundAngle = 350.0f;
        		break;
        		
        	case 6:
        		mFarBackgroundAngle = 329.0f;
        		break;
        		
        	case 10:
        		mFarBackgroundAngle = 303.0f;
        		break;
        		
        	case 14:
        		mFarBackgroundAngle = 288.0f;
        		break;
        		
        	case 18:
        		mFarBackgroundAngle = 277.0f;
        		break;
        		
        	default:
        		mFarBackgroundAngle = 303.0f;
        		break;
        	}
    	}
    }
//    public float getViewangleY() {
//    	return mViewangleY;
//    }
//    
//    public void setViewangleY(float y) {
//    	mViewangleY = y;
//    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public float getFarBackgroundAngle() {
    	return mFarBackgroundAngle;
    }
    
//    public boolean pointVisible(Vector3 point, float radius) {
//        boolean visible = false;
//        final float width = GameParameters.gameWidth / 2.0f;
//        final float height = GameParameters.gameHeight / 2.0f;
////        final float width = sSystemRegistry.contextParameters.gameWidth / 2.0f;
////        final float height = sSystemRegistry.contextParameters.gameHeight / 2.0f;
//        if (Math.abs(mFocalPosition.x - point.x) < (width + radius)) {
//            if (Math.abs(mFocalPosition.y - point.y) < (height + radius)) {
//                visible = true;
//            }
//        }
//        return visible;
//    }

//    /** Snaps a coordinate against the bounds of the world so that it may not pass out
//     * of the visible area of the world.
//     * @param worldX An x-coordinate in world units.
//     * @return An x-coordinate that is guaranteed not to expose the edges of the world.
//     */
//    public float snapFocalPointToWorldBoundsX(float worldX) {
//    	
//        float focalPositionX = worldX;
//        final float width = GameParameters.gameWidth;
////        final float width = sSystemRegistry.contextParameters.gameWidth;
//        // TODO Create temporary code for LevelSystem
//        final LevelSystem level = sSystemRegistry.levelSystem;
//        if (level != null) {
//            final float worldPixelWidth = Math.max(level.getLevelWidth(), width);
//            final float rightEdge = focalPositionX + (width / 2.0f);
//            final float leftEdge = focalPositionX - (width / 2.0f);
//    
//            if (rightEdge > worldPixelWidth) {
//                focalPositionX = worldPixelWidth - (width / 2.0f);
//            } else if (leftEdge < 0) {
//                focalPositionX = width / 2.0f;
//            }
//        }
//        return focalPositionX;
//    }

//    /** Snaps a coordinate against the bounds of the world so that it may not pass out
//     * of the visible area of the world.
//     * @param worldY A y-coordinate in world units.
//     * @return A y-coordinate that is guaranteed not to expose the edges of the world.
//     */
//    public float snapFocalPointToWorldBoundsY(float worldY) {
//    	
//        float focalPositionY = worldY;
//
//        final float height = sSystemRegistry.contextParameters.gameHeight;
//        final LevelSystem level = sSystemRegistry.levelSystem;
//        if (level != null) {
//            final float worldPixelHeight = Math.max(level.getLevelHeight(), sSystemRegistry.contextParameters.gameHeight);
//            final float topEdge = focalPositionY + (height / 2.0f);
//            final float bottomEdge = focalPositionY - (height / 2.0f);
//    
//            if (topEdge > worldPixelHeight) {
//                focalPositionY = worldPixelHeight - (height / 2.0f);
//            } else if (bottomEdge < 0) {
//                focalPositionY = height / 2.0f;
//            }
//        }
//        
//        return focalPositionY;
//    }
    
//    /** Snaps a coordinate against the bounds of the world so that it may not pass out
//     * of the visible area of the world.
//     * @param worldZ A z-coordinate in world units.
//     * @return A z-coordinate that is guaranteed not to expose the edges of the world.
//     */
//    public float snapFocalPointToWorldBoundsZ(float worldZ) {
//    	
//        float focalPositionZ = worldZ;
//
//        // XXX Change to gameDepth setting?
//        final float height = GameParameters.gameHeight;
////        final float height = sSystemRegistry.contextParameters.gameHeight;
//        final LevelSystem level = sSystemRegistry.levelSystem;
//        if (level != null) {
//            final float worldPixelHeight = Math.max(level.getLevelHeight(), GameParameters.gameHeight);
////            final float worldPixelHeight = Math.max(level.getLevelHeight(), sSystemRegistry.contextParameters.gameHeight);
//            final float topEdge = focalPositionZ + (height / 2.0f);
//            final float bottomEdge = focalPositionZ - (height / 2.0f);
//    
//            if (topEdge > worldPixelHeight) {
//                focalPositionZ = worldPixelHeight - (height / 2.0f);
//            } else if (bottomEdge < 0) {
//                focalPositionZ = height / 2.0f;
//            }
//        }
//        
//        return focalPositionZ;
//    }

//	public void addCameraBias(Vector3 bias) {
//		
//		final float x = bias.x - mFocalPosition.x;
//		final float y = bias.y - mFocalPosition.y;
//		final float biasX = mBias.x;
//		final float biasY = mBias.y;
//		mBias.set(x, y);
//		mBias.normalize();
//		mBias.add(biasX, biasY);
//	}
}
