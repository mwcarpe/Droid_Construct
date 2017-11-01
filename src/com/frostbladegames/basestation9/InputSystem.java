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


/** 
 * Manages input from the touch screen.  Reduces frequent UI messages to
 * an average direction over a short period of time.
 */
public class InputSystem extends BaseObject {
	public boolean touchMovePress;
	public boolean touchFirePress;
	public boolean touchWeapon1Press;
	public boolean touchWeapon2Press;
	public boolean touchViewangleBarPress;
	
//	public Vector3 touchMoveXY = new Vector3();
//	public Vector3 touchFireXY = new Vector3();
	
	public Vector3 movePosition = new Vector3();
	public Vector3 firePosition = new Vector3();
//	public Vector3 viewangleBarPosition = new Vector3();
	
//	private InputXY mTouchMove = new InputXY();
//	private InputXY mTouchFire = new InputXY();
//	private InputButton mTouchWeapon1 = new InputButton();
//	private InputButton mTouchWeapon2 = new InputButton();

//	private InputXY mTouchScreen = new InputXY();
//	private InputXY mOrientationSensor = new InputXY();
//	private InputXY mTrackball = new InputXY();
//    private InputKeyboard mKeyboard = new InputKeyboard();
	
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    private float mLastMove;
//    private float mLastFire;
//    private float mLastWeapon;
//    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
               
    public InputSystem() {
        super();
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "InputSystem <constructor>");	
    	}
        
        reset();
    }
    
    @Override
    public void reset() {
    	touchMovePress = false;
    	touchFirePress = false;
    	touchWeapon1Press = false;
    	touchWeapon2Press = false;
    	touchViewangleBarPress = false;
    	
//    	touchMoveXY.zero();
//    	touchFireXY.zero();
    	
//    	moveDirection.zero();
//    	fireDirection.zero();

//    	mTouchMove.reset();
//    	mTouchFire.reset();
//    	mTouchWeapon1.reset();
//    	mTouchWeapon2.reset();
//    	mTrackball.reset();
//    	mTouchScreen.reset();
//    	mKeyboard.resetAll();
//    	mOrientationSensor.reset();
    }

//    public void roll(float x, float y) {
//        TimeSystem time = sSystemRegistry.timeSystem;
//    	mTrackball.press(time.getGameTime(), mTrackball.getX() + x, mTrackball.getY() + y);
//    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public void touchMoveDown(float x, float y) {
//    	DebugLog.d("Touch", "InputSystem touchMoveDown()");
//    	
//// 	   ContextParameters params = sSystemRegistry.contextParameters;
//  	   	final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//// 	   TimeSystem time = sSystemRegistry.timeSystem;
// 	   
////		DebugLog.d("InputSystem", "touchMoveDown() params.gameHeight = " + params.gameHeight);
// 	   
//  	   	mTouchMove.press(gameTime, x, y);
////  	   	if ((gameTime - mLastMove) < 0.1f) {
////  	   		// ignore
////  	     	mTouchMove.release();
////  	   	} else {
////  	   		mTouchMove.press(gameTime, x, y);
////  	   		mLastMove = gameTime;
////  	   	}
//// 	   mTouchMove.press(time.getGameTime(), x, y);
//// 	   // Change the origin of the touch location from the top-left to the bottom-left to match
//// 	   // OpenGL space.
//// 	   // XXX UNIFY THIS
//// 	   // TODO Take out "height - y" from Droid code
//// 	   mTouchMove.press(time.getGameTime(), x, params.gameHeight - y);   
//     }
//     
//    public void touchMoveUp() {
////     public void touchMoveUp(float x, float y) {
//    	DebugLog.d("Touch", "InputSystem touchMoveUp()");
//     	
//     	// XXX record up location?
//     	mTouchMove.release();
//     }
//     
//     public void touchFireDown(float x, float y) {
//     	DebugLog.d("Touch", "InputSystem touchFireDown()");
//     	
////  	   ContextParameters params = sSystemRegistry.contextParameters;
//    	 final float gameTime = sSystemRegistry.timeSystem.getGameTime();
////  	   TimeSystem time = sSystemRegistry.timeSystem;
//  	   
//    	 mTouchFire.press(gameTime, x, y);
////    	 if ((gameTime - mLastFire) < 0.1f) {
////    		 // ignore
////        	 mTouchFire.release();
////    	 } else {
////    		 mTouchFire.press(gameTime, x, y);
////    		 mLastFire = gameTime;
////    	 }
////  	   	mTouchFire.press(time.getGameTime(), x, y);
////  	   // Change the origin of the touch location from the top-left to the bottom-left to match
////  	   // OpenGL space.
////  	   // XXX UNIFY THIS
////  	   // TODO Take out "height - y" from Droid code
////  	   mTouchFire.press(time.getGameTime(), x, params.gameHeight - y);   
//     }
//      
//     public void touchFireUp() {
////      public void touchFireUp(float x, float y) {
//     	DebugLog.d("Touch", "InputSystem touchFireUp()");
//      	
//    	 // XXX record up location?
//    	 mTouchFire.release();
//     }
//     
//     public void touchWeapon1Down() {
////     public void touchWeapon1Down(float x, float y) {
//     	DebugLog.d("Touch", "InputSystem touchWeapon1Down()");
//    	 
//    	 final float gameTime = sSystemRegistry.timeSystem.getGameTime();
////  	   TimeSystem time = sSystemRegistry.timeSystem;
//  	   
//    	 mTouchWeapon1.press(gameTime);
////    	 mTouchWeapon1.press(gameTime, x, y);
////    	 if ((gameTime - mLastWeapon) < 0.1f) {
//// 	   			// ignore
////    	      	mTouchWeapon.release();
////    	 } else {
////    		 mTouchWeapon.press(gameTime, x, y);
////    		 mLastWeapon = gameTime;
////    	 }
////  	   mTouchWeapon.press(time.getGameTime(), x, y, weaponButtonType);
//     }
//      
//     public void touchWeapon1Up() {
//     	DebugLog.d("Touch", "InputSystem touchWeapon1Up()");
//     	
//      	mTouchWeapon1.release();
//     }
//     
//     public void touchWeapon2Down() {
////     public void touchWeapon2Down(float x, float y) {
//     	DebugLog.d("Touch", "InputSystem touchWeapon2Down()");
//    	 
//    	 final float gameTime = sSystemRegistry.timeSystem.getGameTime();
////  	   TimeSystem time = sSystemRegistry.timeSystem;
//  	   
//    	 mTouchWeapon2.press(gameTime);
////    	 mTouchWeapon2.press(gameTime, x, y);
////    	 if ((gameTime - mLastWeapon) < 0.1f) {
//// 	   			// ignore
////    	      	mTouchWeapon.release();
////    	 } else {
////    		 mTouchWeapon.press(gameTime, x, y);
////    		 mLastWeapon = gameTime;
////    	 }
////  	   mTouchWeapon.press(time.getGameTime(), x, y, weaponButtonType);
//     }
//      
//     public void touchWeapon2Up() {
//     	DebugLog.d("Touch", "InputSystem touchWeapon2Up()");
//     	
//      	mTouchWeapon2.release();
//     }
      /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
//    public void touchDown(float x, float y) {
//	   ContextParameters params = sSystemRegistry.contextParameters;
//	   TimeSystem time = sSystemRegistry.timeSystem;
//	   // Change the origin of the touch location from the top-left to the bottom-left to match
//	   // OpenGL space.
//	   // XXX UNIFY THIS
//	   // TODO Take out "height - y" from Droid code
//	   mTouchScreen.press(time.getGameTime(), x, params.gameHeight - y);   
//    }
//    
//    public void touchUp(float x, float y) {
//    	// XXX record up location?
//    	mTouchScreen.release();
//    }
    
//    public void setOrientation(float azimuth, float pitch, float roll) {   
//        //DebugLog.d("Orientation", "Pitch: " + pitch + "  Roll: " + roll);
//        final float correctedPitch = -pitch / 180.0f;
//        final float correctedRoll = -roll / 90.0f;
//        //DebugLog.d("Orientation", "Pitch: " + correctedPitch + "  Roll: " + correctedRoll);
//
//        TimeSystem time = sSystemRegistry.timeSystem;
//        mOrientationSensor.press(time.getGameTime(), correctedPitch, correctedRoll); 
//    }
    
//    public void keyDown(int keycode) {
//    	TimeSystem time = sSystemRegistry.timeSystem;
//        final float gameTime = time.getGameTime();
//        mKeyboard.press(gameTime, keycode);
//    }
    
//    public void keyUp(int keycode) {
//    	mKeyboard.release(keycode);
//    }
    
//    public void releaseAllKeys() {
////    	mTrackball.releaseX();
////    	mTrackball.releaseY();
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	mTouchMove.release();
//    	mTouchFire.release();
//    	mTouchWeapon1.release();
//    	mTouchWeapon2.release();
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////    	mTouchScreen.release();
////    	mKeyboard.releaseAll();
////    	mOrientationSensor.release();
//    }
//
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//	public InputXY getTouchMove() {
//		return mTouchMove;
//	}
//	
//	public InputXY getTouchFire() {
//		return mTouchFire;
//	}
//	
//	public InputButton getTouchWeapon1() {
//		return mTouchWeapon1;
//	}
//	
//	public InputButton getTouchWeapon2() {
//		return mTouchWeapon2;
//	}
	
//	public InputXY getTouchWeapon1() {
//		return mTouchWeapon1;
//	}
//	
//	public InputXY getTouchWeapon2() {
//		return mTouchWeapon2;
//	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
//	public InputXY getTouchScreen() {
//		return mTouchScreen;
//	}

//	public InputXY getOrientationSensor() {
//		return mOrientationSensor;
//	}

//	public InputXY getTrackball() {
//		return mTrackball;
//	}

//	public InputKeyboard getKeyboard() {
//		return mKeyboard;
//	}
}
