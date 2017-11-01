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

//import com.frostbladegames.droidconstruct.DebugLog;

import android.os.SystemClock;
import android.view.KeyEvent;


public class InputGameInterface extends BaseObject {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float MOVE_SPEED = 0.0001f;
//    private static final float MOVE_SPEED = 0.0025f;
    // FIXME DROID_MOVE_SPEED TEMP ONLY. Make sure value is same as DroidBottomComponent value. Replace with variable speed system.
    private static final float DROID_MOVE_SPEED = 0.1f;
    
	private static final float ORIENTATION_DEAD_ZONE_MIN = 0.03f;
	private static final float ORIENTATION_DEAD_ZONE_MAX = 0.1f;
	private static final float ORIENTATION_DEAD_ZONE_SCALE = 0.75f;

	private static final float ROLL_TIMEOUT = 0.1f;
	private static final float ROLL_RESET_DELAY = 0.075f;
	
    // Raw trackball input is filtered by this value. Increasing it will 
    // make the control more twitchy, while decreasing it will make the control more precise.
    private static final float ROLL_FILTER = 0.4f;
    private static final float ROLL_DECAY = 8.0f;
	
    private static final float KEY_FILTER = 0.25f;
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    // ISOMETRIC must be between 0.0f and -90.0f for move calculation to work correctly. gluLookAt() is 45 degrees.
    private static final float ISOMETRIC = -45.0f;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
	private InputButton mJumpButton = new InputButton();
	private InputButton mAttackButton = new InputButton();
	private InputXY mDirectionalPad = new InputXY();
	private InputXY mTilt = new InputXY();
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	private InputXY mMoveButton = new InputXY();
	private InputXY mFireButton = new InputXY();
	private InputButton mWeapon1Button = new InputButton();
	private InputButton mWeapon2Button = new InputButton();
//	private InputXY mHeading = new InputXY();
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	// FIXME Delete Temp Code
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    private float mX;
    private float mY;
    private float mZ;
    private float mR;
    
    private float mMoveButtonCenterX;
    private float mMoveButtonCenterY;
    private float mFireButtonCenterX;
    private float mFireButtonCenterY;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
		
	private int mLeftKeyCode = KeyEvent.KEYCODE_DPAD_LEFT;
	private int mRightKeyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
	private int mJumpKeyCode = KeyEvent.KEYCODE_SPACE;
	private int mAttackKeyCode = KeyEvent.KEYCODE_SHIFT_LEFT;
		
	private float mOrientationDeadZoneMin = ORIENTATION_DEAD_ZONE_MIN;
	private float mOrientationDeadZoneMax = ORIENTATION_DEAD_ZONE_MAX;
	private float mOrientationDeadZoneScale = ORIENTATION_DEAD_ZONE_SCALE;
	private float mOrientationSensitivity = 1.0f;
	private float mOrientationSensitivityFactor = 1.0f;
	private float mMovementSensitivity = 1.0f;


	private boolean mUseClickButtonForAttack = true;
	private boolean mUseOrientationForMovement = false;
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	private float mLastTime;	// TEMP ONLY
	
    private long mLastMoveTime;
    private long mLastFireTime;
    private long mLastWeapon1Time;
    private long mLastWeapon2Time;
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	private float mLastRollTime;
	
	public InputGameInterface() {
		super();
		reset();
	}

	@Override
	public void reset() {
		mJumpButton.release();
		mAttackButton.release();
		mDirectionalPad.release();
		mTilt.release();
		
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		mMoveButton.release();
		mFireButton.release();
		mWeapon1Button.release();
		mWeapon2Button.release();
//		mHeading.release();
		
		mLastMoveTime = SystemClock.uptimeMillis();
		mLastFireTime = SystemClock.uptimeMillis();
		mLastWeapon1Time = SystemClock.uptimeMillis();
		mLastWeapon2Time = SystemClock.uptimeMillis();
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	@Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
		// FIXME RE-ENABLE IF InputGameInterface WILL STILL BE USED
//    	if (sDebugLog) {
//    		DebugLog.d("Loop", "InputGameInterface update()");
//    	}
//    	
//    	// FIXME What is difference in Game Play in using gameTime vs SystemClock.uptimeMillis()?
//		final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//		final long time = SystemClock.uptimeMillis();
//		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		
//		InputSystem input = sSystemRegistry.inputSystem;
//		
//		final InputXY touchMove = input.getTouchMove();
//		final InputXY touchFire = input.getTouchFire();
//		final InputButton touchWeapon1 = input.getTouchWeapon1();
//		final InputButton touchWeapon2 = input.getTouchWeapon2();
//		
//		float touchMoveX = touchMove.getX();
//		float touchMoveY = touchMove.getY();
//		
//		float touchFireX = touchFire.getX();
//		float touchFireY = touchFire.getY();
//		
//		if (sDebugLog) {
//	        DebugLog.d("Object", "InputGameInterface update() touchMoveX, touchMoveY = " + touchMoveX + ", " + touchMoveY);
//	        DebugLog.d("Object", "InputGameInterface update() touchFireX, touchFireY = " + touchFireX + ", " + touchFireY);
//	        DebugLog.d("Object", "InputGameInterface update() touchMove.getPressed() = " + touchMove.getPressed());
//	        DebugLog.d("Object", "InputGameInterface update() touchFire.getPressed() = " + touchFire.getPressed());	
//		}	
//        
//        boolean movePressed = touchMove.getPressed();
//        boolean firePressed = touchFire.getPressed();
//        boolean weapon1Pressed = touchWeapon1.getPressed();
//        boolean weapon2Pressed = touchWeapon2.getPressed();
//        
//        // FIXME Refactor this double-checking of if movePressed || firePressed, then if movePressed && firePressed, etc
//		if(movePressed || firePressed) {
////        if((movePressed && (time - mLastMoveTime) > 1000) || (firePressed && (time - mLastFireTime) > 1000)) {
//			
////			float moveX = mMoveButton.getX();
////			float moveZ = mMoveButton.getZ();
////			float moveR = mMoveButton.getR();
////			
////			if (sDebugLog) {
////		        DebugLog.d("Object", "InputGameInterface update() ORIGINAL moveX,Z,R = " + moveX + ", " + moveZ + ", " + moveR);	
////			}
////			
////			// FIXME Re-confirm where mFireButton x,z are set based on Move x,z
////			float fireX = mFireButton.getX();
////			float fireZ = mFireButton.getZ();
////			float fireR = mFireButton.getR();
////			
////			if (sDebugLog) {
////		        DebugLog.d("Object", "InputGameInterface update() ORIGINAL fireX,Z,R = " + fireX + ", " + fireZ + ", " + fireR);	
////			}
////			
//////			final float distance2 = (((touchMoveX - mMoveButtonCenterX) * (touchMoveX - mMoveButtonCenterX)) + 
//////					((touchMoveY - mMoveButtonCenterY) * (touchMoveY - mMoveButtonCenterY))) * MOVE_SPEED;
////////			final float distance = (float)Math.sqrt(((touchMoveX - mMoveButtonCenterX) * (touchMoveX - mMoveButtonCenterX)) + 
////////					((touchMoveY - mMoveButtonCenterY) * (touchMoveY - mMoveButtonCenterY))) * MOVE_SPEED;
//			
//			if(movePressed && firePressed) {
////			if((movePressed && (time - mLastMoveTime) > 1000) && (firePressed && (time - mLastFireTime) > 1000)) {
//				/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//				mLastMoveTime = time;
//				mLastFireTime = time;
//				/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//				
//				float moveR = buttonAngleCalc(touchMoveX, touchMoveY, mMoveButtonCenterX, mMoveButtonCenterY);
//				
////			    DebugLog.d("Object", "InputGameInterface update() moveR moveButtonAngleCalc before ISOMETRIC = " + moveR);	
//				
//				moveR = moveR + ISOMETRIC;  // Isometric angle adjustment included
//				
////			    DebugLog.d("Object", "InputGameInterface update() moveR moveButtonAngleCalc after ISOMETRIC = " + moveR);	
//				
//				// FIXME Find way to move new moveX, moveZ and distanceX, distanceZ to mMoveButton.press() for DroidBottomComponent read
//				float moveX = (float)Math.sin(moveR * PI_OVER_180);
//				float moveZ = (float)Math.cos(moveR * PI_OVER_180);
////				moveX -= (float)Math.sin(moveR * PI_OVER_180);
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180);
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * DROID_MOVE_SPEED;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * DROID_MOVE_SPEED;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * distance2;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * distance2;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * distance;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * distance;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * 0.05f;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * 0.05f;
//				
//				if (sDebugLog) {
//			        DebugLog.d("Object", "InputGameInterface update() NEW moveX,Z,R = " + moveX + ", " + moveZ + ", " + moveR);	
//				}
//				
//				float fireR = buttonAngleCalc(touchFireX, touchFireY, mFireButtonCenterX, mFireButtonCenterY);
//				fireR = fireR + ISOMETRIC;  // Isometric angle adjustment included
//				
//				mMoveButton.press(gameTime, moveX, 0.0f, moveZ, moveR);
//				
//				mFireButton.press(gameTime, moveX, 0.0f, moveZ, fireR);
//				
//				mLastTime = gameTime;
//			} else if(movePressed) {
////			} else if (movePressed && (time - mLastMoveTime) > 1000) {	
//				/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//				mFireButton.release();
////				mLastMoveTime = time;
//				/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//				
//				float moveR = buttonAngleCalc(touchMoveX, touchMoveY, mMoveButtonCenterX, mMoveButtonCenterY);
//				
////			    DebugLog.d("Object", "InputGameInterface update() moveR moveButtonAngleCalc before ISOMETRIC = " + moveR);	
//		        
//				moveR = moveR + ISOMETRIC;  // Isometric angle adjustment included
//				
////			    DebugLog.d("Object", "InputGameInterface update() moveR moveButtonAngleCalc after ISOMETRIC = " + moveR);
//
//				float moveX = (float)Math.sin(moveR * PI_OVER_180);
//				float moveZ = (float)Math.cos(moveR * PI_OVER_180);
////				moveX -= (float)Math.sin(moveR * PI_OVER_180);
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180);
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * DROID_MOVE_SPEED;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * DROID_MOVE_SPEED;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * distance2;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * distance2;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * distance;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * distance;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * 0.1f;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * 0.1f;
////				moveX -= (float)Math.sin(moveR * PI_OVER_180) * 0.05f;
////				moveZ -= (float)Math.cos(moveR * PI_OVER_180) * 0.05f;
//				
//				if (sDebugLog) {
//			        DebugLog.d("Object", "InputGameInterface update() NEW moveX,Z,R = " + moveX + ", " + moveZ + ", " + moveR);	
//				}
//				
//				mMoveButton.press(gameTime, moveX, 0.0f, moveZ, moveR);
//				
//				if (sDebugLog) {
//			        DebugLog.d("Object", "InputGameInterface update() gameTime, mLastTime = " + gameTime + ", " + mLastTime);	
//				}
//				final float temp = gameTime - mLastTime;
//				if (sDebugLog) {
//			        DebugLog.d("Object", "InputGameInterface update() timeDiff = " + temp);	
//				}
//				
//				/* TODO Try to re-enable way for Droid to continue facing in fire direction for 1 sec after mFireButton is touchUp().
//				 * Initially check for fireR = 0.0f, which means haven't started firing yet? */
//				mFireButton.press(gameTime, moveX, 0.0f, moveZ, moveR);
////				if ((gameTime - mLastTime) < 1.0f) {
////					// Continue Top facing in Fire direction
////					float fireR = mFireButton.getR();
////					
////					mFireButton.press(gameTime, moveX, 0.0f, moveZ, fireR);
////				} else {
////					// Change Top back to Move direction
////					mFireButton.press(gameTime, moveX, 0.0f, moveZ, moveR);
////					
////					mLastTime = gameTime;
////				}
//			} else {
//				/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//				mMoveButton.release();
////				mLastFireTime = time;
//				/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//				
//				float fireR = buttonAngleCalc(touchFireX, touchFireY, mFireButtonCenterX, mFireButtonCenterY);
//				fireR = fireR + ISOMETRIC;  // Isometric angle adjustment included
//				
//				mFireButton.press(gameTime, 0.0f, 0.0f, 0.0f, fireR);
////				mFireButton.press(gameTime, moveX, 0.0f, moveZ, fireR);
////				mFireButton.press(gameTime, fireX, 0.0f, fireZ, fireR);
//				
//				if (sDebugLog) {
//			        DebugLog.d("Object", "InputGameInterface update() fire only mFireButton.press fireR = " + fireR);	
//				}
//				
//				mLastTime = gameTime;
//			}
//		} else {
//			mMoveButton.release();
//			mFireButton.release();
//		}
//		
//		// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//		if (weapon1Pressed) {
////        if (weapon1Pressed && (time - mLastWeapon1Time) > 1000) {
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        	mWeapon2Button.release();
////        	mLastWeapon1Time = time;
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//			
//			mWeapon1Button.press(gameTime);
////			mWeapon1Button.press(gameTime, 0.0f, 0.0f);
//		} else if (weapon2Pressed) {
////        } else if (weapon2Pressed && (time - mLastWeapon2Time) > 1000) {	
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        	mWeapon1Button.release();
////        	mLastWeapon2Time = time;
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//			
//			mWeapon2Button.press(gameTime);
//		} else {
//			mWeapon1Button.release();
//			mWeapon2Button.release();
//		}
		
		
		
		
		// XXX SUCCESSFUL CODE FOR MOVE. FIRE droid_top ONLY MOVES AFTER MOVE PRESSED AGAIN.
////		final InputButton[] keys = input.getKeyboard().getKeys();
////		final InputXY orientation = input.getOrientationSensor();
//		
//		// TODO Disable keys and trackball code?
//		// tilt is easy
////		mTilt.clone(orientation);
//		
//		// FIXME Delete Test Code
////		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
////		final float tempGameTime = sSystemRegistry.timeSystem.getGameTime();
////		
////		mX += 1.0f;
////		mY = 0.0f;
////		mZ = 0.0f;
////		mR += 10.0f;
////		
////		mMoveButton.press(tempGameTime, mX, mY, mZ, mR);
////		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		
//		
////		// update movement inputs
////		if (mUseOrientationForMovement) {
////			mDirectionalPad.clone(orientation);
////			mDirectionalPad.setMagnitude(
////					filterOrientationForMovement(orientation.getX()), 
////					filterOrientationForMovement(orientation.getY()));
////		} else {
////			// keys or trackball
////			final InputXY trackball = input.getTrackball();
////			final InputButton left = keys[mLeftKeyCode];
////			final InputButton right = keys[mRightKeyCode];
////			final float leftPressedTime = left.getLastPressedTime();
////			final float rightPressedTime = right.getLastPressedTime();
////			
////			final float gameTime = sSystemRegistry.timeSystem.getGameTime();
////			
////			if (trackball.getLastPressedTime() > Math.max(leftPressedTime, rightPressedTime)) {
////				/* The trackball never goes "up", so force it to turn off if it wasn't triggered in the last frame.
////				  What follows is a bunch of code to filter trackball events into something like a dpad event.
////				  The goals here are:
////				  - For roll events that occur in quick succession to accumulate.
////				  - For roll events that occur with more time between them, lessen the impact of older events
////				  - In the absence of roll events, fade the roll out over time. */
////				if (gameTime - trackball.getLastPressedTime() < ROLL_TIMEOUT) {
////					float newX;
////					float newY;
////					final float delay = Math.max(ROLL_RESET_DELAY, timeDelta);
////					if (gameTime - mLastRollTime <= delay) {
////						newX = mDirectionalPad.getX() + (trackball.getX() * ROLL_FILTER * mMovementSensitivity);
////						newY = mDirectionalPad.getY() + (trackball.getY() * ROLL_FILTER * mMovementSensitivity);
////					} else {
////						float oldX = mDirectionalPad.getX() != 0.0f ? mDirectionalPad.getX() / 2.0f : 0.0f;
////						float oldY = mDirectionalPad.getX() != 0.0f ? mDirectionalPad.getX() / 2.0f : 0.0f;
////						newX = oldX + (trackball.getX() * ROLL_FILTER * mMovementSensitivity);
////						newY = oldY + (trackball.getX() * ROLL_FILTER * mMovementSensitivity);
////					}
////					
////					mDirectionalPad.press(gameTime, newX, newY);
////					mLastRollTime = gameTime;
////					trackball.release();
////				} else {
////					float x = mDirectionalPad.getX();
////					float y = mDirectionalPad.getY();
////					if (x != 0.0f) {
////						int sign = Utils.sign(x);
////						x = x - (sign * ROLL_DECAY * timeDelta);
////						if (Utils.sign(x) != sign) {
////							x = 0.0f;
////						}
////					}
////					
////					if (y != 0.0f) {
////						int sign = Utils.sign(y);
////						y = y - (sign * ROLL_DECAY * timeDelta);
////						if (Utils.sign(x) != sign) {
////							y = 0.0f;
////						}
////					}
////					
////					if (x == 0 && y == 0) {
////						mDirectionalPad.release();
////					} else {
////						mDirectionalPad.setMagnitude(x, y);
////					}
////				}
////			} else {
////				float xMagnitude = 0.0f;
////				float yMagnitude = 0.0f;
////				float pressTime = 0.0f;
////				// left and right are mutually exclusive
////				if (leftPressedTime > rightPressedTime) {
////					xMagnitude = -left.getMagnitude() * KEY_FILTER * mMovementSensitivity;
////					pressTime = leftPressedTime;
////				} else {
////					xMagnitude = right.getMagnitude() * KEY_FILTER * mMovementSensitivity;
////					pressTime = rightPressedTime;
////				}
////				
////				if (xMagnitude != 0.0f) {
////					mDirectionalPad.press(pressTime, xMagnitude, yMagnitude);
////				} else {
////					mDirectionalPad.release();
////				}
////			}
////		}
//		
//
//		// update other buttons
////		final InputButton jumpKey = keys[mJumpKeyCode];
////		final InputXY touch = input.getTouchScreen();
//		
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//		final InputXY touchMove = input.getTouchMove();
//		final InputXY touchFire = input.getTouchFire();
//		
//		float touchMoveX = touchMove.getX();
//		float touchMoveY = touchMove.getY();
//		
//        DebugLog.d("InputGameInterface", "update() touchMoveX, touchMoveY = " + touchMoveX + ", " + touchMoveY);
//		
//		float touchFireX = touchFire.getX();
//		float touchFireY = touchFire.getY();
//		
//        DebugLog.d("InputGameInterface", "update() touchFireX, touchFireY = " + touchFireX + ", " + touchFireY);
//		
//        // TODO Re-enable. Temp code. Optimize.
////		GameObject testObject = (GameObject)parent;
////		
////		Vector3 testPosition = testObject.getPosition();
////		
////        float newX = testPosition.x;
////        float newY = testPosition.y;
////        float newZ = testPosition.z;
////        float newR = testPosition.r;
////        
////        newX += 1.0f;
////        newZ += 1.0f;
////        
////		Vector3 returnPosition = new Vector3(newX, newY, newZ, newR);
////
////        testObject.setPosition(returnPosition);
//        
////        DebugLog.d("InputGameInterface", "update() touch.getPressed() = " + touch.getPressed());
////        
////        DebugLog.d("InputGameInterface", "update() FLY_BUTTON getTouchedWithinRegion() = " + 
////        		getTouchedWithinRegion(
////        				touchX, 
////        				touchY,
////                        ButtonConstants.FLY_BUTTON_REGION_X, 
////                        ButtonConstants.FLY_BUTTON_REGION_Y, 
////                        ButtonConstants.FLY_BUTTON_REGION_WIDTH, 
////                        ButtonConstants.FLY_BUTTON_REGION_HEIGHT));
////        
////        DebugLog.d("InputGameInterface", "update() STOMP_BUTTON getTouchedWithinRegion() = " + 
////        		getTouchedWithinRegion(
////        				touchX, 
////        				touchY,
////                        ButtonConstants.STOMP_BUTTON_REGION_X, 
////                        ButtonConstants.STOMP_BUTTON_REGION_Y, 
////                        ButtonConstants.STOMP_BUTTON_REGION_WIDTH, 
////                        ButtonConstants.STOMP_BUTTON_REGION_HEIGHT));
//		
//        DebugLog.d("InputGameInterface", "update() touchMove.getPressed() = " + touchMove.getPressed());
//        
//		if(touchMove.getPressed()) {
//			
//	        DebugLog.d("InputGameInterface", "update() Move setting change");
//        
////		if(touch.getPressed() && getTouchedWithinRegion(
////				touchX, 
////				touchY,
////                ButtonConstants.FLY_BUTTON_REGION_X, 
////                ButtonConstants.FLY_BUTTON_REGION_Y, 
////                ButtonConstants.FLY_BUTTON_REGION_WIDTH, 
////                ButtonConstants.FLY_BUTTON_REGION_HEIGHT)) {
//			
//			
//        	// TODO Increase speed as touch moves towards outer edge of circle
//			// TODO Find better way to detect button touch area and draw buttons
//        	/* 
//        	 * Move Bottom
//        	 * mYRot has 315 degree adjustment for isometric angle.
//        	 */
////			GameObject object = (GameObject)parent;
//			
//			final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//			
////			mX += 1.0f;
////			mY = 0.0f;
////			mZ = 0.0f;
////			mR += 10.0f;
//			
//			// TODO Enable y positioning (e.g. ramp)
////			float x = object.getPosition().x;
//////			float y = object.getPosition().y;
////			float z = object.getPosition().z;
//			
//			float x = mMoveButton.getX();
//			float z = mMoveButton.getZ();
//
//        	float r = moveButtonAngleCalc(touchMoveX, touchMoveY);
//        	
//        	/* FIXME Calculate exact move angle in order to have exact fire angle 
//        	 * Fix glitch when facing 90 degrees directly left, but Droid moves downward (other left angles OK) */
//        	r = 165.0f - r;  // Isometric angle adjustment included
////        	r = 180.0f - r;
//        	
//			// Isometric angle adjustment
////			r -= 50.0f;
//
//        	// TODO Add variable for speed from 0.03f to 0.1f as touch further from MoveButton center
////			x -= (float)Math.sin((180.0f - r) * ButtonConstants.PI_OVER_180) * 0.05f;
////			z -= (float)Math.cos((180.0f - r) * ButtonConstants.PI_OVER_180) * 0.05f;
//			x -= (float)Math.sin(r * ButtonConstants.PI_OVER_180) * 0.05f;
//			z -= (float)Math.cos(r * ButtonConstants.PI_OVER_180) * 0.05f;
//			
//			mMoveButton.press(gameTime, x, 0.0f, z, r);
//			
////			Vector3 position = new Vector3(x, 0.0f, z, rotate);
////			object.setPosition(position);
//			
//			// FIXME Modify for mMoveButton?
////			if (xMagnitude != 0.0f) {
////				mMoveButton.press(pressTime, xMagnitude, yMagnitude);
////			} else {
////				mMoveButton.release();
////			}
//						
//			// FIXME Add Multi-touch pointerCount boolean in ContructActivity, Game, or other onTouch()
////    		int pointerCount = event.getPointerCount();
////    		
////			if(pointerCount == 1) {
////				float x = event.getX();
////				float y = event.getY();
////				
////	        	// XXX Fix heading calculations. Change to more realistic rotation. Increase rotation speed.
////	        	// TODO Create inner circle area for turn only (ie no move)
////	        	// TODO Increase speed as touch moves towards outer edge of circle
////				// TODO Find better way to detect button touch area
////	        	/* 
////	        	 * Move Bottom
////	        	 * mYRot has 315 degree adjustment for isometric angle.
////	        	 */
////	        	if (x <= mMoveButtonRightX && x >= mMoveButtonLeftX &&
////	        			y <= mMoveButtonBottomY && y >= mMoveButtonTopY) {
////		        	mHeadingBottom = moveButtonAngleCalc(x, y);
////		
////					mXPosBottom -= (float)Math.sin(mHeadingBottom * mPiOver180) * 0.05f;
////					mZPosBottom -= (float)Math.cos(mHeadingBottom * mPiOver180) * 0.05f;
////	        	}
////	        	
////	        	// TODO Find better way to detect button touch area
////	        	// Fire Top
////	        	if (x <= mFireButtonRightX && x >= mFireButtonLeftX &&
////	        			y <= mFireButtonBottomY && y >= mFireButtonTopY) {
////		        	mHeadingTop = fireButtonAngleCalc(x, y);
////		
////					mXPosTop -= (float)Math.sin(mHeadingTop * mPiOver180) * 0.05f;
////					mZPosTop -= (float)Math.cos(mHeadingTop * mPiOver180) * 0.05f;
////	        	}
////			} else {
////				int firstIndex = event.getX(0) < event.getX(1) ? 0 : 1;
////				int secondIndex = event.getX(0) < event.getX(1) ? 1 : 0;
////				
////				float x1 = event.getX(firstIndex);
////				float y1 = event.getY(firstIndex);
////				float x2 = event.getX(secondIndex);
////				float y2 = event.getY(secondIndex);
////				
////	        	// XXX Fix heading calculations. Change to more realistic rotation. Increase rotation speed.
////	        	// TODO Create inner circle area for turn only (ie no move)
////	        	// TODO Increase speed as touch moves towards outer edge of circle
////	        	/* 
////	        	 * Move Bottom
////	        	 * mYRot has 315 degree adjustment for isometric angle.
////	        	 */
////	        	if (x1 <= mMoveButtonRightX && x1 >= mMoveButtonLeftX &&
////	        			y1 <= mMoveButtonBottomY && y1 >= mMoveButtonTopY) {
////		        	mHeadingBottom = moveButtonAngleCalc(x1, y1);
////		
////					mXPosBottom -= (float)Math.sin(mHeadingBottom * mPiOver180) * 0.05f;
////					mZPosBottom -= (float)Math.cos(mHeadingBottom * mPiOver180) * 0.05f;
////	        	}
////
////	        	// Fire Top
////	        	if (x2 <= mFireButtonRightX && x2 >= mFireButtonLeftX &&
////	        			y2 <= mFireButtonBottomY && y2 >= mFireButtonTopY) {
////		        	mHeadingTop = fireButtonAngleCalc(x2, y2);
////		
////					mXPosTop -= (float)Math.sin(mHeadingTop * mPiOver180) * 0.05f;
////					mZPosTop -= (float)Math.cos(mHeadingTop * mPiOver180) * 0.05f;
////	        	}
////			}
//			
////			float headingCalc = moveButtonAngleCalc(regionX, regionY);
////			
////			mHeading.setMagnitude(headingCalc, 0);
////
////			GameObject object = (GameObject)parent;
////			Vector3 heading = new Vector3(headingCalc, 0);
////			object.setHeading(heading);
////			
////			float translateX = mDirectionalPad.getX() -
////					(float)Math.sin(headingCalc * ButtonConstants.PI_OVER_180) * 0.05f;
////			float translateY = mDirectionalPad.getY() -
////					(float)Math.cos(headingCalc * ButtonConstants.PI_OVER_180) * 0.05f;
////			
////			/* TODO This only changes setMagnitude. Need to set mXPosTop -= and mZPosTop -= 
////			   How to change flow down to GameObject and Vector2 changes?*/
////			mDirectionalPad.setMagnitude(translateX, translateY);
////			Vector3 position = new Vector3(translateX, translateY);
////			object.setPosition(position);
////			
////			// TODO Re-enable - Release
//////			if (x == 0 && y == 0) {
//////				mDirectionalPad.release();
//////			} else {
//////				mDirectionalPad.setMagnitude(x, y);
//////			}
//		}
//		
//		/* TODO Will need to check for scale and location of Fire button 
//		   when moved to right side of screen */
//        DebugLog.d("InputGameInterface", "update() touchFire.getPressed() = " + touchFire.getPressed());
//        
//		if(touchFire.getPressed() || touchMove.getPressed()) {
//			
//	        DebugLog.d("InputGameInterface", "update() Fire setting change");
//        
////		if(touch.getPressed() && getTouchedWithinRegion(
////				touchX, 
////				touchY,
////                ButtonConstants.STOMP_BUTTON_REGION_X, 
////                ButtonConstants.STOMP_BUTTON_REGION_Y, 
////                ButtonConstants.STOMP_BUTTON_REGION_WIDTH, 
////                ButtonConstants.STOMP_BUTTON_REGION_HEIGHT)) {
//			
//			final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//			
//			// droid_top (Fire) will have same x,y position as droid_bottom (Move)
//			float x = mMoveButton.getX();
//			float z = mMoveButton.getZ();
//			
////			float x = mFireButton.getX();
////			float z = mFireButton.getZ();
//
//        	float r = fireButtonAngleCalc(touchFireX, touchFireY);
//        	
//        	/* FIXME Calculate exact move angle in order to have exact fire angle 
//        	 * Fix glitch when facing 90 degrees directly left, but Droid moves downward (other left angles OK) */
//        	r = 165.0f - r;  // Isometric angle adjustment included
////        	r = 180.0f - r;
//        	
//			// Isometric angle adjustment
////			r -= 50.0f;
//
//        	// TODO Add variable for speed from 0.03f to 0.1f as touch further from MoveButton center
////			x -= (float)Math.sin((180.0f - r) * ButtonConstants.PI_OVER_180) * 0.05f;
////			z -= (float)Math.cos((180.0f - r) * ButtonConstants.PI_OVER_180) * 0.05f;
////			x -= (float)Math.sin(r * ButtonConstants.PI_OVER_180) * 0.05f;
////			z -= (float)Math.cos(r * ButtonConstants.PI_OVER_180) * 0.05f;
//			
//			mFireButton.press(gameTime, x, 0.0f, z, r);
//		}
//		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		
//		// XXX Replica original code
////		if (jumpKey.getPressed()) {
////			mJumpButton.press(jumpKey.getLastPressedTime(), jumpKey.getMagnitude());
////		} else if (touch.getPressed() && getTouchedWithinRegion(
////							touch.getX(), 
////							touch.getY(),
////	                        ButtonConstants.FLY_BUTTON_REGION_X, 
////	                        ButtonConstants.FLY_BUTTON_REGION_Y, 
////	                        ButtonConstants.FLY_BUTTON_REGION_WIDTH, 
////	                        ButtonConstants.FLY_BUTTON_REGION_HEIGHT)) {
////			if (!mJumpButton.getPressed()) {
////				mJumpButton.press(touch.getLastPressedTime(), 1.0f);
////			}
////		} else {
////			mJumpButton.release();
////		}
////		
////		final InputButton attackKey = keys[mAttackKeyCode];
////		final InputButton clickButton = keys[KeyEvent.KEYCODE_DPAD_CENTER]; // special case
////		
////		if (mUseClickButtonForAttack && clickButton.getPressed()) {
////			mAttackButton.press(clickButton.getLastPressedTime(), clickButton.getMagnitude());
////		} else if (attackKey.getPressed()) {
////			mAttackButton.press(attackKey.getLastPressedTime(), attackKey.getMagnitude());
////		} else if (touch.getPressed() && getTouchedWithinRegion(
////							touch.getX(), 
////							touch.getY(),
////	                        ButtonConstants.STOMP_BUTTON_REGION_X, 
////	                        ButtonConstants.STOMP_BUTTON_REGION_Y, 
////	                        ButtonConstants.STOMP_BUTTON_REGION_WIDTH, 
////	                        ButtonConstants.STOMP_BUTTON_REGION_HEIGHT)) {
////			// TODO Change to repeating Fire
////			/* Since touch events come in constantly, we only want to press the attack button
////			   here if it's not already down.  That makes it act like the other buttons (down once then up). */
////			if (!mAttackButton.getPressed()) {
////				mAttackButton.press(touch.getLastPressedTime(), 1.0f);
////			}
////		} else {
////			mAttackButton.release();
////		}
	}
	
	private float filterOrientationForMovement(float magnitude) {
		float scaledMagnitude = magnitude * mOrientationSensitivityFactor;
		
		return deadZoneFilter(scaledMagnitude, mOrientationDeadZoneMin, mOrientationDeadZoneMax, mOrientationDeadZoneScale);
	}
	
	private float deadZoneFilter(float magnitude, float min, float max, float scale) {
		float smoothedMagnatude = magnitude;
    	if (Math.abs(magnitude) < min) {
    		smoothedMagnatude = 0.0f;	// dead zone
    	} else if (Math.abs(magnitude) < max) {
    		smoothedMagnatude *= scale;
    	}
    	
    	return smoothedMagnatude;
	}
	
	public final InputXY getDirectionalPad() {
		return mDirectionalPad;
	}
	
	public final InputXY getTilt() {
		return mTilt;
	}
	
	public final InputButton getJumpButton() {
		return mJumpButton;
	}
	
	public final InputButton getAttackButton() {
		return mAttackButton;
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public final InputXY getMoveButton() {
		return mMoveButton;
	}
	
	public final InputXY getFireButton() {
		return mFireButton;
	}
	
	public final InputButton getWeapon1Button() {
		return mWeapon1Button;
	}
	
	public final InputButton getWeapon2Button() {
		return mWeapon2Button;
	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	public void setKeys(int left, int right, int jump, int attack) {
		mLeftKeyCode = left;
		mRightKeyCode = right;
		mJumpKeyCode = jump;
		mAttackKeyCode = attack;
	}
	
	public void setUseClickForAttack(boolean click) {
		mUseClickButtonForAttack = click;
	}
	
	public void setUseOrientationForMovement(boolean orientation) {
		mUseOrientationForMovement = orientation;
	}
	
	public void setOrientationMovementSensitivity(float sensitivity) {
		mOrientationSensitivity = sensitivity;
		mOrientationSensitivityFactor = 2.9f * sensitivity + 0.1f;
	}

	public void setMovementSensitivity(float sensitivity) {
		mMovementSensitivity  = sensitivity;
	}
	
	public void setMoveFireCenters(float moveCenterX, float moveCenterY, float fireCenterX, float fireCenterY) {
	    mMoveButtonCenterX = moveCenterX;
	    mMoveButtonCenterY = moveCenterY;
	    mFireButtonCenterX = fireCenterX;
	    mFireButtonCenterY = fireCenterY;
	}
	
	public void setMoveButton(float x, float y, float z, float r) {
		mMoveButton.setMagnitude(x, y, z, r);
	}
	
	public void setFireButton(float x, float y, float z, float r) {
		mFireButton.setMagnitude(x, y, z, r);
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	/**
	 * Calculates the angle at which an Object should be Facing
	 * @param x
	 * @param y
	 * @param centerX
	 * @param centerY
	 * @return
	 */
	private float buttonAngleCalc(float x, float y, float centerX, float centerY) {
		float buttonAngle = 0.0f;;
		
		// TODO Change to manual Radians to Degrees calc (ref mPiOver180)
		// 2D Angle Calculation in Degrees
		if (x <= centerX) {	        
			if (y >= centerY) {
				if ((y - centerY) == 0) {
					// Set to minimal denominator
					buttonAngle = (float)Math.toDegrees(Math.atan(
							(centerX - x) /
							0.01f));
				} else {
					buttonAngle = (float)Math.toDegrees(Math.atan(
							(centerX - x) /
							(y - centerY)));
				}
			} else {
				if ((centerX - x) == 0) {
					// Set to minimal denominator
					buttonAngle = 90.0f + (float)Math.toDegrees(Math.atan(
							(centerY - y) /
							0.01f));
				} else {
					buttonAngle = 90.0f + (float)Math.toDegrees(Math.atan(
							(centerY - y) /
							(centerX - x)));
				}
			}
		} else {
			if (y < centerY) {
				buttonAngle = 180.0f + (float)Math.toDegrees(Math.atan(
						(x - centerX) /
						(centerY - y)));
			} else {
				buttonAngle = 270.0f + (float)Math.toDegrees(Math.atan(
						(y - centerY) /
						(x - centerX)));
			}
		}
		return buttonAngle;
	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
}
