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

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

import com.frostbladegames.basestation9.GameObjectGroups.Type;

/**
 * A very simple manager for orthographic in-game UI elements.
 * XXX: This should probably manage a number of hud objects in keeping with the component-centric
 * architecture of this engine.  The current code is monolithic and should be refactored.
 */
public class HudSystem extends BaseObject {
//    private static final int FUEL_BAR_EDGE_PADDING = 15;
//    private static final float FUEL_DECREASE_BAR_SPEED = 0.75f;
//    private static final float FUEL_INCREASE_BAR_SPEED = 2.0f;
//	private static final int DROID_HUD_TOP_EDGE_PADDING = 48;
//	private static final int ASTRONAUT_HUD_TOP_EDGE_PADDING = 88;
//	private static final int ASTRONAUT_HUD_TOP_EDGE_PADDING = 90;
//	private static final int DROID_HUD_TOP_EDGE_PADDING = 4;
//	private static final int ASTRONAUT_HUD_TOP_EDGE_PADDING = 36;
//	private static final int DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING = 120;
//    private static final int COLLECTABLE_EDGE_PADDING = 4;
    private static final int MAX_TOTAL_KILL_COLLECT_DIGITS = 6;
    private static final int MAX_TOTAL_COLLECT_DIGITS = 4;
//    private static final int MAX_TOTAL_KILL_COLLECT_DIGITS = 4;
//    private static final int MAX_TOTAL_COLLECT_DIGITS = 3;
//    private static final int MAX_DIGITS = 4;
	
	private DrawableBitmap mMoveButtonBase;
	private DrawableBitmap mMoveButtonTop;
	private DrawableBitmap mFireButtonBase;
	private DrawableBitmap mFireButtonTop;
	private DrawableBitmap mWeaponButtonEmpty;
	private DrawableBitmap mWeaponButtonLaserStd;
	private DrawableBitmap mWeaponButtonLaserStdActive;
	private DrawableBitmap mWeaponButtonLaserPulse;
	private DrawableBitmap mWeaponButtonLaserPulseActive;
	private DrawableBitmap mWeaponButtonLaserEmp;
	private DrawableBitmap mWeaponButtonLaserEmpActive;
	private DrawableBitmap mWeaponButtonLaserGrenade;
	private DrawableBitmap mWeaponButtonLaserGrenadeActive;
	private DrawableBitmap mWeaponButtonLaserRocket;
	private DrawableBitmap mWeaponButtonLaserRocketActive;
	private DrawableBitmap mViewangleBarBase;
	private DrawableBitmap mViewangleBarBaseTransparent;
	private DrawableBitmap mViewangleBarButton;
	private DrawableBitmap mViewangleBarButtonTransparent;
	
    private DrawableBitmap[] mDigits = {
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap(),
            new DrawableBitmap()
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0),
//            new DrawableBitmap(0, 0)
    };
	
//    private Type mButtonWeapon1GameObjectType;
//    private Type mButtonWeapon2GameObjectType;
////    private GameObject mButtonWeapon1GameObject;
////    private GameObject mButtonWeapon2GameObject;
    
//    private float mGameWidthDelta;
    private float mInverseViewScaleX;
    private float mInverseViewScaleY;
    
    // Boolean hudDummy is not used. Only for polymorphism since 3D .set(x, z) also exists.
    private boolean mHudDummy;
    
    private Vector3 mMoveButtonBaseLocation;
    private float mMoveButtonTopOffsetX;
    private float mMoveButtonTopOffsetY;
    private Vector3 mMoveButtonTopLocation;
    private boolean mMoveButtonActive;
    private boolean mMoveButtonPressed;
    
    private Vector3 mFireButtonBaseLocation;
    private float mFireButtonTopOffsetX;
    private float mFireButtonTopOffsetY;
    private Vector3 mFireButtonTopLocation;
    private boolean mFireButtonPressed;
    
    private Vector3 mWeapon1ButtonLocation;
    private Vector3 mWeapon2ButtonLocation;
    
    private Vector3 mViewangleBarBaseLocation;
    private float mViewangleBarButtonOffsetY;
    private Vector3 mViewangleBarButtonLocation;
    private boolean mViewangleBarBaseTouched;
    private float mViewangleBarBaseLastTouch;
    
    private int mNumberSpaceWidth;
    
//    private DrawableBitmap mFuelDrawable;
//    private DrawableBitmap mFuelBackgroundDrawable;
//    private float mFuelPercent;
//    private float mFuelTargetPercent;
    
    private Texture mFadeTexture;
    private float mFadeStartTime;
    private float mFadeDuration;
    private boolean mFadeIn;
    private boolean mFading;
    private int mFadePendingEventType;
	private int mFadePendingEventIndex;
    
	private DrawableBitmap mDroidGreenDrawable;
	private DrawableBitmap mDroidYellowDrawable;
	private DrawableBitmap mDroidRedDrawable;
	private DrawableBitmap mAstronautDrawable;
    
	public int droidHitPoints;
    public int totalKillCollectPoints;
    public int totalCollectNum;
    public boolean totalKillCollectPointsDigitsChanged;
    public boolean totalCollectNumDigitsChanged;

    private Vector3 mDroidHudLocation;
    private Vector3 mAstronautHudLocation;
    private int[] mTotalKillCollectPointsDigits;
    private int[] mTotalCollectNumDigits;
    
    public boolean levelIntro;
//    private boolean mLevelIntro;
    
//  private DrawableBitmap mRubyDrawable;
//  private DrawableBitmap mCoinDrawable;
    
//  private int mCoinCount;
//  private int mRubyCount;
    
//    private Vector3 mCoinLocation;
//    private Vector3 mRubyLocation;
//    private int[] mCoinDigits;
//    private int[] mRubyDigits;
//    private boolean mCoinDigitsChanged;
//    private boolean mRubyDigitsChanged;
    
    private int mFPS;
    private Vector3 mFPSLocation;
    private int[] mFPSDigits;
    private boolean mFPSDigitsChanged;
    private boolean mShowFPS;
    
    private DrawableBitmap[] mDigitDrawables;
//    private DrawableBitmap mXDrawable;
	
//    // FIXME TEMP. DELETE.
//    private float mTouchCounterTimer;
    
    public HudSystem(int width, int height) {
//    public HudSystem() {
        super();
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "HudSystem <constructor>");	
    	}
        
        // FIXME ADDED 11/11/2
    	mMoveButtonBase = new DrawableBitmap();
    	mMoveButtonTop = new DrawableBitmap();
    	mFireButtonBase = new DrawableBitmap();
    	mFireButtonTop = new DrawableBitmap();
    	mWeaponButtonEmpty = new DrawableBitmap();
    	mWeaponButtonLaserStd = new DrawableBitmap();
    	mWeaponButtonLaserStdActive = new DrawableBitmap();
    	mWeaponButtonLaserPulse = new DrawableBitmap();
    	mWeaponButtonLaserPulseActive = new DrawableBitmap();
    	mWeaponButtonLaserEmp = new DrawableBitmap();
    	mWeaponButtonLaserEmpActive = new DrawableBitmap();
    	mWeaponButtonLaserGrenade = new DrawableBitmap();
    	mWeaponButtonLaserGrenadeActive = new DrawableBitmap();
    	mWeaponButtonLaserRocket = new DrawableBitmap();
    	mWeaponButtonLaserRocketActive = new DrawableBitmap();
    	mViewangleBarBase = new DrawableBitmap();
    	mViewangleBarBaseTransparent = new DrawableBitmap();
    	mViewangleBarButton = new DrawableBitmap();
    	mViewangleBarButtonTransparent = new DrawableBitmap();
    	
    	mDroidGreenDrawable = new DrawableBitmap();
    	mDroidYellowDrawable = new DrawableBitmap();
    	mDroidRedDrawable = new DrawableBitmap();
    	mAstronautDrawable = new DrawableBitmap();
//    	mMoveButtonBase = new DrawableBitmap(width, height);
//    	mMoveButtonTop = new DrawableBitmap(width, height);
//    	mFireButtonBase = new DrawableBitmap(width, height);
//    	mFireButtonTop = new DrawableBitmap(width, height);
//    	mWeaponButtonEmpty = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserStd = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserStdActive = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserPulse = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserPulseActive = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserEmp = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserEmpActive = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserGrenade = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserGrenadeActive = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserRocket = new DrawableBitmap(width, height);
//    	mWeaponButtonLaserRocketActive = new DrawableBitmap(width, height);
//    	mViewangleBarBase = new DrawableBitmap(width, height);
//    	mViewangleBarBaseTransparent = new DrawableBitmap(width, height);
//    	mViewangleBarButton = new DrawableBitmap(width, height);
//    	mViewangleBarButtonTransparent = new DrawableBitmap(width, height);
//    	
//    	mDroidGreenDrawable = new DrawableBitmap(width, height);
//    	mDroidYellowDrawable = new DrawableBitmap(width, height);
//    	mDroidRedDrawable = new DrawableBitmap(width, height);
//    	mAstronautDrawable = new DrawableBitmap(width, height);
//    	
//    	for(int i = 0; i < 10; i++) {
//    		DrawableBitmap digit = mDigits[i];
//    		digit.setViewSize(width, height);
//    	}
        // FIXME END 11/11/12
        
        mHudDummy = false;
        
        mMoveButtonBaseLocation = new Vector3();
        mMoveButtonTopLocation = new Vector3();
        mFireButtonBaseLocation = new Vector3();
        mFireButtonTopLocation = new Vector3();
        mWeapon1ButtonLocation = new Vector3();
        mWeapon2ButtonLocation = new Vector3();
        mViewangleBarBaseLocation = new Vector3();
        mViewangleBarButtonLocation = new Vector3();
        
        mViewangleBarBaseTouched = false;
        
        mDroidHudLocation = new Vector3();
        mAstronautHudLocation = new Vector3();
//        mCoinLocation = new Vector3();
//        mRubyLocation = new Vector3();
        mFPSLocation = new Vector3();
        mDigitDrawables = new DrawableBitmap[10];
        mTotalKillCollectPointsDigits = new int[MAX_TOTAL_KILL_COLLECT_DIGITS];
        mTotalCollectNumDigits = new int[MAX_TOTAL_COLLECT_DIGITS];
//        mCoinDigits = new int[MAX_DIGITS];
//        mRubyDigits = new int[MAX_DIGITS];
        mFPSDigits = new int[4];
        
        reset();
    }
    
    @Override
    public void reset() {    	
////        mFuelDrawable = null;
//        mFadeTexture = null;
////        mFuelPercent = 1.0f;
////        mFuelTargetPercent = 1.0f;
//        mFading = false;
//        mMoveButtonBase = null;
//        mMoveButtonTop = null;
//        mFireButtonBase = null;
//        mFireButtonTop = null;
//        mWeaponButtonEmpty = null;
//        mWeaponButtonLaserStd = null;
//        mWeaponButtonLaserStdActive = null;
//        mWeaponButtonLaserPulse = null;
//        mWeaponButtonLaserPulseActive = null;
//        mWeaponButtonLaserEmp = null;
//        mWeaponButtonLaserEmpActive = null;
//        mWeaponButtonLaserGrenade = null;
//        mWeaponButtonLaserGrenadeActive = null;
//        mWeaponButtonLaserRocket = null;
//        mWeaponButtonLaserRocketActive = null;
//        mViewangleBarBase = null;
//        mViewangleBarBaseTransparent = null;
//        mViewangleBarButton = null;
//        mViewangleBarButtonTransparent = null;
        
//        mButtonWeapon1GameObjectType = Type.INVALID;
//        mButtonWeapon2GameObjectType = Type.INVALID;
////        mButtonWeapon1GameObject = null;
////        mButtonWeapon2GameObject = null;
        mMoveButtonActive = true;
        mMoveButtonPressed = false;
        mFireButtonPressed = false;
        droidHitPoints = 0;
        totalKillCollectPoints = 0;
        totalCollectNum = 0;
//        mCoinCount = 0;
//        mRubyCount = 0;
        mTotalKillCollectPointsDigits[0] = 0;
        mTotalKillCollectPointsDigits[1] = -1;
        mTotalCollectNumDigits[0] = 0;
        mTotalCollectNumDigits[1] = -1;
        totalKillCollectPointsDigitsChanged = true;
        totalCollectNumDigitsChanged = true;
//        mCoinDigits[0] = 0;
//        mCoinDigits[1] = -1;
//        mRubyDigits[0] = 0;
//        mRubyDigits[1] = -1;
//        mCoinDigitsChanged = true;
//        mRubyDigitsChanged = true;
        
        levelIntro = false;
//        mLevelIntro = false;
        
        mFPS = 0;
        mFPSDigits[0] = 0;
        mFPSDigits[1] = -1;
        mFPSDigitsChanged = true;
        mShowFPS = false;
        for (int x = 0; x < mDigitDrawables.length; x++) {
            mDigitDrawables[x] = null;
        }
//        mXDrawable = null;
        mFadePendingEventType = GameFlowEvent.EVENT_INVALID;
        mFadePendingEventIndex = 0;
    }

    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
//        Log.i("Loop", "HudSystem update()");
    	
        final RenderSystemHud render = sSystemRegistry.renderSystemHud;
//        final RenderSystem render = sSystemRegistry.renderSystem;
//        final VectorPool pool = sSystemRegistry.vectorPool;
//        final ContextParameters params = sSystemRegistry.contextParameters;
        
        // TODO Re-enable - DrawableFactory
//        final DrawableFactory factory = sSystemRegistry.drawableFactory;
//
        final GameObjectManager manager = sSystemRegistry.gameObjectManager;
        
        if (manager != null && !levelIntro) {
//        if (manager != null && manager.getPlayer() != null && !levelIntro) {	
	        if (mMoveButtonBase != null) {	            
//	        	// FIXME Is this additional pointer still necessary? A:  if bitmap.resize() still req, then yes should use.
//	            DrawableBitmap bitmap = mMoveButtonBase;
//	            
	            // FIXME DELETED 11/11/12
//	            // FIXME Is this necessary?. Test if called every loop or just 1x.
//	            if (bitmap.getWidth() == 0) {
//	                // first time init
//	                Texture tex = bitmap.getTexture();
//	                bitmap.resize(tex.width, tex.height);
//	            }

	            render.scheduleForDraw(mMoveButtonBase, mMoveButtonBaseLocation, null, 0.0f, SortConstants.HUD, false, 99);
//	            render.scheduleForDraw(bitmap, mMoveButtonBaseLocation, null, 0.0f, SortConstants.HUD, false, 99); 
//	            render.scheduleForDraw(bitmap, mMoveButtonLocation, SortConstants.HUD, false, 99);   
	        }
	        
	        if (mMoveButtonTop != null) {	
//	        	// FIXME Is this additional pointer still necessary? A:  if bitmap.resize() still req, then yes should use.
//	            DrawableBitmap bitmap = mMoveButtonTop;
//	            
//	            // FIXME Deleted 10/20/12
//	            // FIXME Is this necessary?. Test if called every loop or just 1x.
//	            if (bitmap.getWidth() == 0) {
//	                // first time init
//	                Texture tex = bitmap.getTexture();
//	                bitmap.resize(tex.width, tex.height);
//	            }

	            render.scheduleForDraw(mMoveButtonTop, mMoveButtonTopLocation, null, 0.0f, SortConstants.HUD, false, 99);
//	            render.scheduleForDraw(bitmap, mMoveButtonTopLocation, null, 0.0f, SortConstants.HUD, false, 99);  
		    }
	        
	        if (mFireButtonBase != null) {
//		        if (mFireButtonEnabledDrawable != null && mFireButtonDepressedDrawable != null) {
		            
//	        	// FIXME Is this additional pointer still necessary? A:  if bitmap.resize() still req, then yes should use.
//	        	DrawableBitmap bitmap = mFireButtonBase;
////		            DrawableBitmap bitmap = mFireButtonEnabledDrawable;
//		            
////		            if (mFireButtonPressed) {
////		                bitmap = mFireButtonDepressedDrawable;
////		            } 
//		            
//	        	// FIXME Deleted 10/20/12
//	        	if (bitmap.getWidth() == 0) {
//	        		// first time init
//	        		Texture tex = bitmap.getTexture();
//	        		bitmap.resize(tex.width, tex.height);
////		                bitmap.setWidth((int)(tex.width * STOMP_BUTTON_SCALE));
////		                bitmap.setHeight((int)(tex.height * STOMP_BUTTON_SCALE));
//	        	}
		            
	        	render.scheduleForDraw(mFireButtonBase, mFireButtonBaseLocation, null, 0.0f, SortConstants.HUD, false, 99);
//	        	render.scheduleForDraw(bitmap, mFireButtonBaseLocation, null, 0.0f, SortConstants.HUD, false, 99); 
//		            render.scheduleForDraw(bitmap, mFireButtonLocation, SortConstants.HUD, false, 99);   
	        }
	        
	        if (mFireButtonTop != null) {	
//	        	// FIXME Is this additional pointer still necessary? A:  if bitmap.resize() still req, then yes should use.
//	            DrawableBitmap bitmap = mFireButtonTop;
//	            
//	            // FIXME Deleted 10/20/12
//	            // FIXME Is this necessary?. Test if called every loop or just 1x.
//	            if (bitmap.getWidth() == 0) {
//	                // first time init
//	                Texture tex = bitmap.getTexture();
//	                bitmap.resize(tex.width, tex.height);
//	            }

	            render.scheduleForDraw(mFireButtonTop, mFireButtonTopLocation, null, 0.0f, SortConstants.HUD, false, 99);
//	            render.scheduleForDraw(bitmap, mFireButtonTopLocation, null, 0.0f, SortConstants.HUD, false, 99);  
		    }
	        
	        GameObject weapon1GameObject = manager.getWeapon1GameObject();
	        GameObject weapon2GameObject = manager.getWeapon2GameObject();
	        
	        // Set Weapon Button 1 DrawableBitmap
	        DrawableBitmap drawableBitmapWeapon1 = null;
	        
	        if (weapon1GameObject != null) {
//		        if (mButtonWeapon1GameObject != null) {
		        switch (weapon1GameObject.type) {
//		        switch (mButtonWeapon1GameObjectType) {
//			        switch (mButtonWeapon1GameObject.type) {		        
		        case DROID_WEAPON_LASER_STD:
		        	if (weapon1GameObject.activeWeapon) {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserStdActive;
		        	} else {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserStd;
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_PULSE:
		        	if (weapon1GameObject.activeWeapon) {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserPulseActive;
		        	} else {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserPulse;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_EMP:
		        	if (weapon1GameObject.activeWeapon) {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserEmpActive;
		        	} else {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserEmp;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_GRENADE:
		        	if (weapon1GameObject.activeWeapon) {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserGrenadeActive;
		        	} else {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserGrenade;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_ROCKET:
		        	if (weapon1GameObject.activeWeapon) {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserRocketActive;
		        	} else {
			        	drawableBitmapWeapon1 = mWeaponButtonLaserRocket;	
		        	}
		        	break;
		        	
		        case INVALID:
		        	drawableBitmapWeapon1 = mWeaponButtonEmpty;
		        	break;
		        	
		        default:
		        	drawableBitmapWeapon1 = mWeaponButtonEmpty;
		        	break;
		        }
	        } else {	        	
	        	drawableBitmapWeapon1 = mWeaponButtonEmpty;
	        }	
//		        } else {	        	
//		        	drawableBitmapWeapon1 = mWeaponButtonEmpty;
//		        }	
	        
//	        // FIXME Deleted 10/20/12
//        	if (drawableBitmapWeapon1.getWidth() == 0) {
//        		// first time init
//        		Texture tex = drawableBitmapWeapon1.getTexture();
//        		drawableBitmapWeapon1.resize(tex.width, tex.height);
//        	}
        	
	        render.scheduleForDraw(drawableBitmapWeapon1, mWeapon1ButtonLocation, null, 0.0f, SortConstants.HUD, false, 99);

	        // Set Weapon Button 2 DrawableBitmap
	        DrawableBitmap drawableBitmapWeapon2 = null;
	        
	        if (weapon2GameObject != null) {
//		        if (mButtonWeapon2GameObject != null) {
		        switch (weapon2GameObject.type) {
//		        switch (mButtonWeapon2GameObjectType) {
//			        switch (mButtonWeapon2GameObject.type) {
		        case DROID_WEAPON_LASER_STD:
		        	if (weapon2GameObject.activeWeapon) {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserStdActive;
		        	} else {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserStd;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_PULSE:
		        	if (weapon2GameObject.activeWeapon) {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserPulseActive;
		        	} else {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserPulse;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_EMP:
		        	if (weapon2GameObject.activeWeapon) {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserEmpActive;
		        	} else {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserEmp;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_GRENADE:
		        	if (weapon2GameObject.activeWeapon) {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserGrenadeActive;
		        	} else {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserGrenade;	
		        	}
		        	break;
		        	
		        case DROID_WEAPON_LASER_ROCKET:
		        	if (weapon2GameObject.activeWeapon) {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserRocketActive;
		        	} else {
			        	drawableBitmapWeapon2 = mWeaponButtonLaserRocket;	
		        	}
		        	break;
		        	
		        case INVALID:
		        	drawableBitmapWeapon2 = mWeaponButtonEmpty;
		        	break;
		        	
		        default:
		        	drawableBitmapWeapon2 = mWeaponButtonEmpty;
		        	break;
		        }
	        } else {	        	
	        	drawableBitmapWeapon2 = mWeaponButtonEmpty;
	        }	
//		        } else {	        	
//		        	drawableBitmapWeapon2 = mWeaponButtonEmpty;
//		        }	
	        
//	        // FIXME Deleted 10/20/12
//        	if (drawableBitmapWeapon2.getWidth() == 0) {
//        		// first time init
//        		Texture tex = drawableBitmapWeapon2.getTexture();
//        		drawableBitmapWeapon2.resize(tex.width, tex.height);
//        	}
	        
	        render.scheduleForDraw(drawableBitmapWeapon2, mWeapon2ButtonLocation, null, 0.0f, SortConstants.HUD, false, 99);
	        
	        if (mDroidGreenDrawable != null && mDroidYellowDrawable != null && mDroidRedDrawable != null) {
	        	float offset = 0.0f;
	        	
	        	switch(droidHitPoints) {
	        	case 1:
//	        		// FIXME Deleted 10/20/12
//		            if (mDroidRedDrawable.getWidth() == 0) {
//		                // first time init
//		                Texture tex = mDroidRedDrawable.getTexture();
//		                mDroidRedDrawable.resize(tex.width, tex.height);
////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - DROID_HUD_TOP_EDGE_PADDING;
//////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) - tex.width / 2.0f;
//////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - COLLECTABLE_EDGE_PADDING;
//		            }
		            
			        render.scheduleForDraw(mDroidRedDrawable, mDroidHudLocation, null, 0.0f, SortConstants.HUD, false, 99);
		            offset = mDroidRedDrawable.getWidth() * 0.75f;
	        		break;
	        		
	        	case 2:
//	        		// FIXME Deleted 10/20/12
//		            if (mDroidYellowDrawable.getWidth() == 0) {
//		                // first time init
//		                Texture tex = mDroidYellowDrawable.getTexture();
//		                mDroidYellowDrawable.resize(tex.width, tex.height);
////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - DROID_HUD_TOP_EDGE_PADDING;
//////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) - tex.width / 2.0f;
//////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - COLLECTABLE_EDGE_PADDING;
//		            }
		            
			        render.scheduleForDraw(mDroidYellowDrawable, mDroidHudLocation, null, 0.0f, SortConstants.HUD, false, 99);
		            offset = mDroidYellowDrawable.getWidth() * 0.75f;
	        		break;
	        		
	        	case 3:
//	        		// FIXME Deleted 10/20/12
//		            if (mDroidGreenDrawable.getWidth() == 0) {
//		                // first time init
//		                Texture tex = mDroidGreenDrawable.getTexture();
//		                mDroidGreenDrawable.resize(tex.width, tex.height);
////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - DROID_HUD_TOP_EDGE_PADDING;
//////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) - tex.width / 2.0f;
//////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - COLLECTABLE_EDGE_PADDING;
//		            }
		            
			        render.scheduleForDraw(mDroidGreenDrawable, mDroidHudLocation, null, 0.0f, SortConstants.HUD, false, 99);
		            offset = mDroidGreenDrawable.getWidth() * 0.75f;
	        		break;
	        		
	        	default:
//	        		// FIXME Deleted 10/20/12
//		            if (mDroidRedDrawable.getWidth() == 0) {
//		                // first time init
//		                Texture tex = mDroidRedDrawable.getTexture();
//		                mDroidRedDrawable.resize(tex.width, tex.height);
////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - DROID_HUD_TOP_EDGE_PADDING;
//////		                mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) - tex.width / 2.0f;
//////		                mDroidHudLocation.y = GameParameters.gameHeight - tex.height - COLLECTABLE_EDGE_PADDING;
//		            }
		            
			        render.scheduleForDraw(mDroidRedDrawable, mDroidHudLocation, null, 0.0f, SortConstants.HUD, false, 99);
		            offset = mDroidRedDrawable.getWidth() * 0.75f;
	        		break;
	        	}

	            if (totalKillCollectPointsDigitsChanged) {
	            	intToDigitArray(totalKillCollectPoints, mTotalKillCollectPointsDigits);
	            	totalKillCollectPointsDigitsChanged = false;
	            }
	            
//	            Log.i("HudDigit", "HudSystem update() mDroidHudLocation.x; offset = " + mDroidHudLocation.x + "; " + offset);
	            
	            /* FIXME Properly set temp Vector3 + offset for drawNumber(). 
	             * Do not temp adjust actual mDroidHudLocation, which can have variance due to float. */
	            mDroidHudLocation.x += offset;
	            drawNumber(mDroidHudLocation, mTotalKillCollectPointsDigits, true);
	            mDroidHudLocation.x -= offset;
	        }
	        
	        if (mAstronautDrawable != null) {
//	        	// FIXME Deleted 10/20/12
//	            if (mAstronautDrawable.getWidth() == 0) {
//	                // first time init
//	                Texture tex = mAstronautDrawable.getTexture();
//	                mAstronautDrawable.resize(tex.width, tex.height);
////	                mAstronautHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////	                mAstronautHudLocation.y = GameParameters.gameHeight - tex.height - ASTRONAUT_HUD_TOP_EDGE_PADDING;
//	            }
	            
		        render.scheduleForDraw(mAstronautDrawable, mAstronautHudLocation, null, 0.0f, SortConstants.HUD, false, 99);
	            if (totalCollectNumDigitsChanged) {
	            	intToDigitArray(totalCollectNum, mTotalCollectNumDigits);
	            	totalCollectNumDigitsChanged = false;
	            }
	            
	            /* FIXME Properly set temp Vector3 + offset for drawNumber(). 
	             * Do not temp adjust actual mAstronautHudLocation, which can have variance due to float. */
	            final float offset = mAstronautDrawable.getWidth() * 0.75f;
	            mAstronautHudLocation.x += offset;
	            drawNumber(mAstronautHudLocation, mTotalCollectNumDigits, true);    
	            mAstronautHudLocation.x -= offset;
	        }
	        
	        if (mViewangleBarBase != null && mViewangleBarBaseTransparent != null) {
	        	DrawableBitmap bitmap;
	        	if (mViewangleBarBaseTouched) {
	        		bitmap = mViewangleBarBase;
	        	} else {
	        		bitmap = mViewangleBarBaseTransparent;
	        	}
		            
//	        	// FIXME Deleted 10/20/12
//	        	if (bitmap.getWidth() == 0) {
//	        		// first time init
//	        		Texture tex = bitmap.getTexture();
//	        		bitmap.resize(tex.width, tex.height);
//	        	}
		            
	        	render.scheduleForDraw(bitmap, mViewangleBarBaseLocation, null, 0.0f, SortConstants.HUD, false, 99);    
	        }
	        
	        if (mViewangleBarButton != null && mViewangleBarButtonTransparent != null) {
	        	DrawableBitmap bitmap;
	        	if (mViewangleBarBaseTouched) {
	        		bitmap = mViewangleBarButton;
	        	} else {
	        		bitmap = mViewangleBarButtonTransparent;
	        	}
	            
//	        	// FIXME Deleted 10/20/12
//	            if (bitmap.getWidth() == 0) {
//	                // first time init
//	                Texture tex = bitmap.getTexture();
//	                bitmap.resize(tex.width, tex.height);
//	            }
	        	
//	        	// FIXME TEMP. DELETE.
//	        	final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//	        	if (gameTime > (mTouchCounterTimer + 5.0f)) {
//	        		Log.i("TouchCounter", "HudSystem update() mViewangleBarButtonLocation.y = " + mViewangleBarButtonLocation.y);
//	        		
//	        		mTouchCounterTimer = gameTime;
//	        	}

	            render.scheduleForDraw(bitmap, mViewangleBarButtonLocation, null, 0.0f, SortConstants.HUD, false, 99);  
		    }
	        
	        TimeSystem time = sSystemRegistry.timeSystem;
	        if (mViewangleBarBaseLastTouch < (time.getGameTime() - 3.0f)) {
		        mViewangleBarBaseTouched = false;	
	        }
        }
        // FIXME END 11/11/12
        
        // TODO Re-enable - mShowFPS
//        if (mShowFPS) {
//        	if (mFPSDigitsChanged) {
//            	int count = intToDigitArray(mFPS, mFPSDigits);
//            	mFPSDigitsChanged = false;
//                mFPSLocation.set(params.gameWidth - ((count + 1) * mDigitDrawables[0].getWidth()), 20.0f);
//
//            }
//            drawNumber(mFPSLocation, mFPSDigits, false);
//        }
    }
    
	private void drawNumber(Vector3 location, int[] digits, boolean drawX) {
    	final RenderSystemHud render = sSystemRegistry.renderSystemHud;
//    	final RenderSystem render = sSystemRegistry.renderSystem;
      
    	// FIXME DELETED 11/11/12
//      	if (mDigitDrawables[0].getWidth() == 0) {
//          	// first time init
//          	for (int x = 0; x < mDigitDrawables.length; x++) {
//              	Texture tex = mDigitDrawables[x].getTexture();
//              	mDigitDrawables[x].resize(tex.width, tex.height);
//          	}
//      	}
////      	if (mXDrawable.getWidth() == 0) {
////          	// first time init
////          	Texture tex = mXDrawable.getTexture();
////          	mXDrawable.resize(tex.width, tex.height);
////      	}
    	// FIXME END 11/11/12
      
    	// FIXME MODIFIED TO mNumberSpaceWidth 11/27/12
//    	// FIXME MODIFIED 11/11/12
//      	final float characterWidth = mDigits[0].getWidth() / 2.0f;
////      	final float characterWidth = mDigitDrawables[0].getWidth() / 2.0f;
      	
      	float offset = 0.0f;
      
//      	if (mXDrawable != null && drawX) {
//          	render.scheduleForDraw(mXDrawable, location, null, 0.0f, SortConstants.HUD, false, 99); 
////          	render.scheduleForDraw(mXDrawable, location, SortConstants.HUD, false, 99); 
//          	location.x += characterWidth;
//          	offset += characterWidth;
//       	}
      	
      	// Set initial space buffer from Droid/Astronaut Heads
      	location.x += mNumberSpaceWidth * 0.4;
      	offset += mNumberSpaceWidth * 0.4;
      
      	for (int x = 0; x < digits.length && digits[x] != -1; x++) {
          	int index = digits[x];
          	DrawableBitmap digit = mDigits[index];
//          	DrawableBitmap digit = mDigitDrawables[index];
          	if (digit != null) {
              	render.scheduleForDraw(digit, location, null, 0.0f, SortConstants.HUD, false, 99);
//              	render.scheduleForDraw(digit, location, SortConstants.HUD, false, 99);
              	location.x += mNumberSpaceWidth;
              	offset += mNumberSpaceWidth;
//              	location.x += characterWidth;
//              	offset += characterWidth;
              	
//              	Log.i("HudDigit", "HudSystem drawNumber() characterWidth; location.x; offset = " +
//              			characterWidth + "; " + location.x + "; " + offset);
          	}
      	}
      
      	location.x -= offset; 
      	// FIXME END 11/11/12
      	// FIXME END 11/27/12
    }

    public int intToDigitArray(int value, int[] digits) {
    	int characterCount = 1;
    	
    	if (value >= 100000) {
    		characterCount = 6;
    	} else if (value >= 10000) {
    		characterCount = 5;
    	} else if (value >= 1000) {
    		characterCount = 4;
    	} else if (value >= 100) {
    		characterCount = 3;
    	} else if (value >= 10) {
    		characterCount = 2;
    	}
//    	if (value >= 1000) {
//    		characterCount = 4;
//    	} else if (value >= 100) {
//    		characterCount = 3;
//    	} else if (value >= 10) {
//    		characterCount = 2;
//    	}
      
  		int remainingValue = value;
  		int count = 0;
	    do {
	        int index = remainingValue != 0 ? remainingValue % 10 : 0;
	        remainingValue *= 0.1f;
//	        remainingValue /= 10;
	        digits[characterCount - 1 - count] = index;
	        count++;
	    } while (remainingValue > 0 && count < digits.length);
	    
	    if (count < digits.length) {
	    	digits[count] = -1;
	    }
	    
	    return count;
  	}
  
  	public void startFade(boolean in, float duration) {
	  mFadeStartTime = sSystemRegistry.timeSystem.getRealTime();
    	mFadeDuration = duration;
    	mFadeIn = in;
    	mFading = true;
    }

  	public void clearFade() {
    	mFading = false;
	}

	public boolean isFading() {
		return mFading;
	}

//	public void updateInventory(InventoryComponent.UpdateRecord newInventory) {
//		mCoinDigitsChanged = (mCoinCount != newInventory.coinCount);
//		mRubyDigitsChanged = (mRubyCount != newInventory.rubyCount);
//	
//	    mCoinCount = newInventory.coinCount;
//	    mRubyCount = newInventory.rubyCount;
//	}
  
	public void sendGameEventOnFadeComplete(int eventType, int eventIndex) {
		mFadePendingEventType = eventType;
		mFadePendingEventIndex = eventIndex;
	}
	
//	public GameObject getButtonWeapon1GameObject() {
//		return mButtonWeapon1GameObject;
//	}
//	
//	public void setButtonWeapon1GameObject(GameObject weapon1) {
//		mButtonWeapon1GameObject = weapon1;
//	}
//	
//	public GameObject getButtonWeapon2GameObject() {
//		return mButtonWeapon2GameObject;
//	}
//	
//	public void setButtonWeapon2GameObject(GameObject weapon2) {
//		mButtonWeapon2GameObject = weapon2;
//	}
	
//	public Type getButtonWeapon1GameObjectType() {
//		return mButtonWeapon1GameObjectType;
////		Type weapon1Type = Type.INVALID;
////		
////		if (mButtonWeapon1GameObject != null) {
////			weapon1Type = mButtonWeapon1GameObject.type;
////		}
////		return weapon1Type;
//	}
//	
//	public void setButtonWeapon1GameObjectType(Type weapon1Type) {
//		mButtonWeapon1GameObjectType = weapon1Type;
//	}
//	
//	public Type getButtonWeapon2GameObjectType() {
//		return mButtonWeapon2GameObjectType;
////		Type weapon2Type = Type.INVALID;
////		
////		if (mButtonWeapon2GameObject != null) {
////			weapon2Type = mButtonWeapon2GameObject.type;
////		}
////		return weapon2Type;
//	}
//	
//	public void setButtonWeapon2GameObjectType(Type weapon2Type) {
//		mButtonWeapon2GameObjectType = weapon2Type;
//	}
    
//    public void setFuelPercent(float percent) {
//        mFuelTargetPercent = percent;
//    }
//    
//    public void setFuelDrawable(DrawableBitmap fuel, DrawableBitmap background) {
//        mFuelDrawable = fuel;
//        mFuelBackgroundDrawable = background;
//    }
	
	// FIXME ADDED 11/11/12
	public void loadGLTexture(GL10 gl, Context context) {
//		Log.i("HudTest", "HudSystem loadGLTexture()");
		
        mMoveButtonBase.loadGLTexture(gl, context, R.drawable.move_button_base);
        mMoveButtonTop.loadGLTexture(gl, context, R.drawable.move_button_top);
        mFireButtonBase.loadGLTexture(gl, context, R.drawable.fire_button_base);
        mFireButtonTop.loadGLTexture(gl, context, R.drawable.fire_button_top);
        mWeaponButtonEmpty.loadGLTexture(gl, context, R.drawable.weapon_button_empty);
        mWeaponButtonLaserStd.loadGLTexture(gl, context, R.drawable.weapon_button_laser_std);
        mWeaponButtonLaserStdActive.loadGLTexture(gl, context, R.drawable.weapon_button_laser_std_active);
        mWeaponButtonLaserPulse.loadGLTexture(gl, context, R.drawable.weapon_button_laser_pulse);
        mWeaponButtonLaserPulseActive.loadGLTexture(gl, context, R.drawable.weapon_button_laser_pulse_active);
        mWeaponButtonLaserEmp.loadGLTexture(gl, context, R.drawable.weapon_button_laser_emp);
        mWeaponButtonLaserEmpActive.loadGLTexture(gl, context, R.drawable.weapon_button_laser_emp_active);
        mWeaponButtonLaserGrenade.loadGLTexture(gl, context, R.drawable.weapon_button_laser_grenade);
        mWeaponButtonLaserGrenadeActive.loadGLTexture(gl, context, R.drawable.weapon_button_laser_grenade_active);
        mWeaponButtonLaserRocket.loadGLTexture(gl, context, R.drawable.weapon_button_laser_rocket);
        mWeaponButtonLaserRocketActive.loadGLTexture(gl, context, R.drawable.weapon_button_laser_rocket_active);
        mViewangleBarBase.loadGLTexture(gl, context, R.drawable.viewangle_bar_base);
        mViewangleBarBaseTransparent.loadGLTexture(gl, context, R.drawable.viewangle_bar_base_transparent);
        mViewangleBarButton.loadGLTexture(gl, context, R.drawable.viewangle_bar_button);
        mViewangleBarButtonTransparent.loadGLTexture(gl, context, R.drawable.viewangle_bar_button_transparent);
        
        for (int i = 0; i < 10; i++) {
        	DrawableBitmap digit = mDigits[i];
        	
        	switch(i) {
        	case 0:
        		digit.loadGLTexture(gl, context, R.drawable.hud_0);
        		break;
        		
        	case 1:
        		digit.loadGLTexture(gl, context, R.drawable.hud_1);
        		break;
        		
        	case 2:
        		digit.loadGLTexture(gl, context, R.drawable.hud_2);
        		break;
        		
        	case 3:
        		digit.loadGLTexture(gl, context, R.drawable.hud_3);
        		break;
        		
        	case 4:
        		digit.loadGLTexture(gl, context, R.drawable.hud_4);
        		break;
        		
        	case 5:
        		digit.loadGLTexture(gl, context, R.drawable.hud_5);
        		break;
        		
        	case 6:
        		digit.loadGLTexture(gl, context, R.drawable.hud_6);
        		break;
        		
        	case 7:
        		digit.loadGLTexture(gl, context, R.drawable.hud_7);
        		break;
        		
        	case 8:
        		digit.loadGLTexture(gl, context, R.drawable.hud_8);
        		break;
        		
        	case 9:
        		digit.loadGLTexture(gl, context, R.drawable.hud_9);
        		break;
        		
        	default:
        		break;
        	}
        }
        
        mDroidGreenDrawable.loadGLTexture(gl, context, R.drawable.hud_droid_headshot_green);
        mDroidYellowDrawable.loadGLTexture(gl, context, R.drawable.hud_droid_headshot_yellow);
        mDroidRedDrawable.loadGLTexture(gl, context, R.drawable.hud_droid_headshot_red);
        mAstronautDrawable.loadGLTexture(gl, context, R.drawable.hud_astronaut_headshot);
	}
	// FIXME END 11/11/12
    
    public void setFadeTexture(Texture texture) {
        mFadeTexture = texture;
    }
    
    public void setButtonDrawables(DrawableBitmap moveButtonBase, DrawableBitmap moveButtonTop, 
    		DrawableBitmap fireButtonBase, DrawableBitmap fireButtonTop, 
    		DrawableBitmap weaponButtonEmpty, 
    		DrawableBitmap weaponButtonLaserStd, DrawableBitmap weaponButtonLaserStdActive,
    		DrawableBitmap weaponButtonLaserPulse, DrawableBitmap weaponButtonLaserPulseActive,
    		DrawableBitmap weaponButtonLaserEmp, DrawableBitmap weaponButtonLaserEmpActive,
    		DrawableBitmap weaponButtonLaserGrenade, DrawableBitmap weaponButtonLaserGrenadeActive,
    		DrawableBitmap weaponButtonLaserRocket, DrawableBitmap weaponButtonLaserRocketActive,
    		DrawableBitmap viewangleBarBase, DrawableBitmap viewangleBarBaseTransparent,
    		DrawableBitmap viewangleBarButton, DrawableBitmap viewangleBarButtonTransparent) {
    	
        mMoveButtonBase = moveButtonBase;
        mMoveButtonTop = moveButtonTop;
        mFireButtonBase = fireButtonBase;
        mFireButtonTop = fireButtonTop;
        mWeaponButtonEmpty = weaponButtonEmpty;
        mWeaponButtonLaserStd = weaponButtonLaserStd;
        mWeaponButtonLaserStdActive = weaponButtonLaserStdActive;
        mWeaponButtonLaserPulse = weaponButtonLaserPulse;
        mWeaponButtonLaserPulseActive = weaponButtonLaserPulseActive;
        mWeaponButtonLaserEmp = weaponButtonLaserEmp;
        mWeaponButtonLaserEmpActive = weaponButtonLaserEmpActive;
        mWeaponButtonLaserGrenade = weaponButtonLaserGrenade;
        mWeaponButtonLaserGrenadeActive = weaponButtonLaserGrenadeActive;
        mWeaponButtonLaserRocket = weaponButtonLaserRocket;
        mWeaponButtonLaserRocketActive = weaponButtonLaserRocketActive;
        mViewangleBarBase = viewangleBarBase;
        mViewangleBarBaseTransparent = viewangleBarBaseTransparent;
        mViewangleBarButton = viewangleBarButton;
        mViewangleBarButtonTransparent = viewangleBarButtonTransparent;
    }
    
    public void setDigitDrawables(DrawableBitmap[] digits) {
//    public void setDigitDrawables(DrawableBitmap[] digits, DrawableBitmap xMark) {
//        mXDrawable = xMark;
        for (int x = 0; x < mDigitDrawables.length && x < digits.length; x++) {
            mDigitDrawables[x] = digits[x];
        }
    }
    
    public void setCollectableDrawables(DrawableBitmap droidGreen, DrawableBitmap droidYellow, DrawableBitmap droidRed,
    		DrawableBitmap astronaut) {
//    public void setCollectableDrawables(DrawableBitmap coin, DrawableBitmap ruby) {
    	mDroidGreenDrawable = droidGreen;
    	mDroidYellowDrawable = droidYellow;
    	mDroidRedDrawable = droidRed;
    	mAstronautDrawable = astronaut;
//        mCoinDrawable = coin;
//        mRubyDrawable = ruby;
    }
    
    public void setButtonState(boolean pressed, boolean attackPressed) {
        mMoveButtonPressed = pressed;
        mFireButtonPressed = attackPressed;
    }
    
    public void setFPS(int fps) {
    	mFPSDigitsChanged = (fps != mFPS);
    	mFPS = fps;
    }
    
    public void setShowFPS(boolean show) {
    	mShowFPS = show;
    }
    
//    public void setGameWidthDelta(float gameWidthDelta) {
//    	mGameWidthDelta = gameWidthDelta;
//    }
    
//    public void setInverseViewScale(float inverseViewScaleX, float inverseViewScaleY) {
//    	mInverseViewScaleX = inverseViewScaleX;
//    	mInverseViewScaleY = inverseViewScaleY;
//    }
    
    public void setMoveButtonBaseLocation(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mMoveButtonBaseLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mMoveButtonBaseLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);

//    	// DroidHud and AstronautHud use the same Left Edge as MoveButtonBase plus 20 padding for 128x128 vs 32x32 png
//    	mDroidHudLocation.x = buttonLeftEdgeX + 20;
//        mDroidHudLocation.y = GameParameters.gameHeight - DROID_HUD_TOP_EDGE_PADDING;
////        mDroidHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////        mDroidHudLocation.y = GameParameters.gameHeight - tex.height - DROID_HUD_TOP_EDGE_PADDING;
//        
//        
//        mAstronautHudLocation.x = buttonLeftEdgeX + 20;
//        mAstronautHudLocation.y = GameParameters.gameHeight - ASTRONAUT_HUD_TOP_EDGE_PADDING;
////        mAstronautHudLocation.x = (GameParameters.gameWidth / 2.0f) + DROID_ASTRONAUT_HUD_LEFT_EDGE_PADDING;
////        mAstronautHudLocation.y = GameParameters.gameHeight - tex.height - ASTRONAUT_HUD_TOP_EDGE_PADDING;
    }
    
    public void setMoveButtonTopOffset(float moveButtonTopOffsetX, float moveButtonTopOffsetY) {
    	mMoveButtonTopOffsetX = moveButtonTopOffsetX;
    	mMoveButtonTopOffsetY = moveButtonTopOffsetY;
    }
    
    public void setMoveButtonTopLocationDefault(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mMoveButtonTopLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mMoveButtonTopLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setMoveButtonTopLocationTouch(float leftX, float bottomY) {
    	mMoveButtonTopLocation.set((leftX + mMoveButtonTopOffsetX), 
    			(bottomY + mMoveButtonTopOffsetY), mHudDummy);
//    	mMoveButtonTopLocation.set(((leftX * mInverseViewScaleX) + mMoveButtonTopOffsetX), 
//    			((bottomY * mInverseViewScaleY) + mMoveButtonTopOffsetY), mHudDummy);
////    	mMoveButtonTopLocation.set(((leftX * mInverseViewScaleX) + mMoveButtonTopOffsetX), 
////    			((bottomY * mInverseViewScaleY) + mMoveButtonTopOffsetY));
    }
    
    public void setFireButtonBaseLocation(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mFireButtonBaseLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mFireButtonBaseLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setFireButtonTopOffset(float fireButtonTopOffsetX, float fireButtonTopOffsetY) {
    	mFireButtonTopOffsetX = fireButtonTopOffsetX;
    	mFireButtonTopOffsetY = fireButtonTopOffsetY;
    }
    
    public void setFireButtonTopLocationDefault(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mFireButtonTopLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mFireButtonTopLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setFireButtonTopLocationTouch(float leftX, float bottomY) {
    	mFireButtonTopLocation.set((leftX + mFireButtonTopOffsetX), 
    			(bottomY + mFireButtonTopOffsetY), mHudDummy);
//    	mFireButtonTopLocation.set(((leftX * mInverseViewScaleX) + mFireButtonTopOffsetX), 
//    			((bottomY * mInverseViewScaleY) + mFireButtonTopOffsetY), mHudDummy);
////    	mFireButtonTopLocation.set(((leftX * mInverseViewScaleX) + mFireButtonTopOffsetX), 
////    			((bottomY * mInverseViewScaleY) + mFireButtonTopOffsetY));
    }
    
    public void setWeapon1ButtonLocation(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mWeapon1ButtonLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mWeapon1ButtonLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setWeapon2ButtonLocation(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mWeapon2ButtonLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mWeapon2ButtonLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setViewangleBarBaseLocation(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mViewangleBarBaseLocation.set(buttonLeftEdgeX, buttonBottomEdgeY, mHudDummy);
//    	mViewangleBarBaseLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setViewangleBarButtonOffsetY(float offsetY) {
    	mViewangleBarButtonOffsetY = offsetY;
    }
    
    public void setViewangleBarButtonLocationX(float buttonLeftEdgeX) {
//    public void setViewangleBarButtonLocationDefault(float buttonLeftEdgeX, float buttonBottomEdgeY) {
    	mViewangleBarButtonLocation.x = buttonLeftEdgeX;
//    	mViewangleBarButtonLocation.set(buttonLeftEdgeX, buttonBottomEdgeY);
    }
    
    public void setViewangleBarButtonLocationY(float bottomY) {
    	mViewangleBarButtonLocation.y =  mViewangleBarButtonOffsetY + bottomY;	// mInverseViewScaleY already applied in Game class
    	mViewangleBarBaseTouched = true;
    	
    	TimeSystem time = sSystemRegistry.timeSystem;
    	mViewangleBarBaseLastTouch = time.getGameTime();
    }
    
    public void setDroidAstronautSnapshotLocation(float buttonLeftEdgeX, float droidBottomY, float astronautBottomY) {
//    public void setDroidAstronautSnapshotLocation(float buttonLeftEdgeX, int droidTopBuffer, int astronautTopBuffer) {
    	mDroidHudLocation.x = buttonLeftEdgeX;
//    	mDroidHudLocation.x = buttonLeftEdgeX + 20;
    	mDroidHudLocation.y = droidBottomY;
//    	mDroidHudLocation.y = GameParameters.viewHeight - droidTopBuffer;
//        mDroidHudLocation.y = GameParameters.viewHeight - DROID_HUD_TOP_EDGE_PADDING;
//        mDroidHudLocation.y = GameParameters.gameHeight - DROID_HUD_TOP_EDGE_PADDING;
        
        mAstronautHudLocation.x = buttonLeftEdgeX;
//        mAstronautHudLocation.x = buttonLeftEdgeX + 20;
        mAstronautHudLocation.y = astronautBottomY;
//        mAstronautHudLocation.y = GameParameters.viewHeight - astronautTopBuffer;
//        mAstronautHudLocation.y = GameParameters.viewHeight - ASTRONAUT_HUD_TOP_EDGE_PADDING;
//        mAstronautHudLocation.y = GameParameters.gameHeight - ASTRONAUT_HUD_TOP_EDGE_PADDING;
    }
    
    public void setNumberSpaceWidth(int width) {
    	mNumberSpaceWidth = width;
    }
    
    
//    public void setLevelIntroTrue() {
//    	mLevelIntro = true;
//    }
}
