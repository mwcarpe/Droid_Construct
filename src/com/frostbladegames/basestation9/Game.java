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

import java.util.Timer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.R;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * High-level setup object for the Droid Construct game engine.
 * This class sets up the core game engine objects and threads.  It also passes events to the
 * game thread from the main UI thread.
 */
public class Game extends AllocationGuard {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;
	
    // ISOMETRIC for Move and Fire angle adjustment to align with gluLookAt() at 45 degrees
    private static final float ISOMETRIC = -45.0f;
    
    // mdpi parameters
    private static final int MOVE_FIRE_BUTTON_WIDTH_MDPI = 128;
    private static final int MOVE_FIRE_BUTTON_HEIGHT_MDPI = 128;
    private static final int MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI = -5;
    private static final int MOVE_FIRE_BUTTON_BOTTOM_OFFSET_MDPI = -5;
    private static final int MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_MDPI = 20;
    private static final int MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI = 20;
    
    private static final int MOVE_FIRE_BUTTON_TOP_WIDTH_MDPI = 64;
    private static final int MOVE_FIRE_BUTTON_TOP_HEIGHT_MDPI = 64;
    
    private static final int MOVE_FIRE_BUTTON_TOP_OFFSET_X_MDPI = -32;
    private static final int MOVE_FIRE_BUTTON_TOP_OFFSET_Y_MDPI = -32;
    
    private static final int WEAPON_BUTTON_WIDTH_MDPI = 64;
    private static final int WEAPON_BUTTON_HEIGHT_MDPI = 64;
    private static final int WEAPON_BUTTON_LEFT_TOUCH_BUFFER_MDPI = 7;
    private static final int WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI = 7;
    
    private static final int WEAPON_BUTTON_LEFT_OFFSET_MDPI = 5;
    private static final int WEAPON_BUTTON_BOTTOM_OFFSET_MDPI = 10;    
    
    private static final int VIEWANGLE_BAR_BASE_WIDTH_MDPI = 128;
    private static final int VIEWANGLE_BAR_BASE_HEIGHT_MDPI = 128;
    private static final int VIEWANGLE_BAR_BASE_LEFT_RIGHT_OFFSET_MDPI = -5;
    private static final int VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_MDPI = -5;
    private static final int VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER_MDPI = 44;
    private static final int VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_MDPI = 24;
    
    private static final int VIEWANGLE_BAR_BUTTON_WIDTH_MDPI = 64;
    private static final int VIEWANGLE_BAR_BUTTON_HEIGHT_MDPI = 64;
    private static final int VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI = 24;
    private static final int VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT_MDPI = 40;
    
    private static final int DROID_ASTRONAUT_SNAPSHOT_WIDTH_MDPI = 32;
    private static final int DROID_ASTRONAUT_SNAPSHOT_HEIGHT_MDPI = 32;
    private static final int DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_MDPI = 10;
	private static final int DROID_SNAPSHOT_TOP_OFFSET_MDPI = 16;
	private static final int ASTRONAUT_SNAPSHOT_TOP_OFFSET_MDPI = 48;
//	private static final int DROID_SNAPSHOT_TOP_OFFSET_MDPI = 48;
//	private static final int ASTRONAUT_SNAPSHOT_TOP_OFFSET_MDPI = 88;
    private static final int ASTRONAUT_SNAPSHOT_BOTTOM_BUFFER_MDPI = 10;
    private static final int NUMBER_SPACE_WIDTH_MDPI = 18;
//    private static final int NUMBER_SPACE_WIDTH_MDPI = 32;
    
    // hdpi parameters
    private static final int MOVE_FIRE_BUTTON_WIDTH_HDPI = 256;
    private static final int MOVE_FIRE_BUTTON_HEIGHT_HDPI = 256;
    private static final int MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI = -30;
    private static final int MOVE_FIRE_BUTTON_BOTTOM_OFFSET_HDPI = -30;
    private static final int MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI = 44;
    private static final int MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI = 44;
//    private static final int MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI = 62;
//    private static final int MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI = 62;
    
    private static final int MOVE_FIRE_BUTTON_TOP_WIDTH_HDPI = 128;
    private static final int MOVE_FIRE_BUTTON_TOP_HEIGHT_HDPI = 128;
    
    private static final int MOVE_FIRE_BUTTON_TOP_OFFSET_X_HDPI = -64;
    private static final int MOVE_FIRE_BUTTON_TOP_OFFSET_Y_HDPI = -64;
    
    private static final int WEAPON_BUTTON_WIDTH_HDPI = 128;
    private static final int WEAPON_BUTTON_HEIGHT_HDPI = 128;
    private static final int WEAPON_BUTTON_LEFT_TOUCH_BUFFER_HDPI = 27;
    private static final int WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI = 27;
    
    private static final int WEAPON_BUTTON_LEFT_OFFSET_HDPI = 15;
//    private static final int WEAPON_BUTTON_LEFT_OFFSET_HDPI = 10;
    private static final int WEAPON_BUTTON_BOTTOM_OFFSET_HDPI = 10;
    
    private static final int VIEWANGLE_BAR_BASE_WIDTH_HDPI = 256;
    private static final int VIEWANGLE_BAR_BASE_HEIGHT_HDPI = 256;
    private static final int VIEWANGLE_BAR_BASE_LEFT_RIGHT_OFFSET_HDPI = 0;
    private static final int VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_HDPI = 0;
    private static final int VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER_HDPI = 88;
    private static final int VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_HDPI = 48;
    
    private static final int VIEWANGLE_BAR_BUTTON_WIDTH_HDPI = 128;
    private static final int VIEWANGLE_BAR_BUTTON_HEIGHT_HDPI = 128;
    private static final int VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI = 48;
    private static final int VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT_HDPI = 80;
    
    private static final int DROID_ASTRONAUT_SNAPSHOT_WIDTH_HDPI = 64;
    private static final int DROID_ASTRONAUT_SNAPSHOT_HEIGHT_HDPI = 64;
    private static final int DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_HDPI = 10;
	private static final int DROID_SNAPSHOT_TOP_OFFSET_HDPI = 24;
//	private static final int DROID_SNAPSHOT_TOP_OFFSET_HDPI = 48;
	private static final int ASTRONAUT_SNAPSHOT_TOP_OFFSET_HDPI = 88;
//    private static final int DROID_SNAPSHOT_TOP_OFFSET_HDPI = 20;
    private static final int ASTRONAUT_SNAPSHOT_BOTTOM_BUFFER_HDPI = 10;
    private static final int NUMBER_SPACE_WIDTH_HDPI = 30;
//    private static final int NUMBER_SPACE_WIDTH_HDPI = 52;
    
    // xhdpi parameters
    private static final int MOVE_FIRE_BUTTON_WIDTH_XHDPI = 256;
    private static final int MOVE_FIRE_BUTTON_HEIGHT_XHDPI = 256;
    private static final int MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI = 10;
//    private static final int MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI = -30;
    private static final int MOVE_FIRE_BUTTON_BOTTOM_OFFSET_XHDPI = 10;
//    private static final int MOVE_FIRE_BUTTON_BOTTOM_OFFSET_XHDPI = -30;
    private static final int MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_XHDPI = 11;
    private static final int MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI = 11;
    
    private static final int MOVE_FIRE_BUTTON_TOP_WIDTH_XHDPI = 128;
    private static final int MOVE_FIRE_BUTTON_TOP_HEIGHT_XHDPI = 128;
    
    private static final int MOVE_FIRE_BUTTON_TOP_OFFSET_X_XHDPI = -64;
    private static final int MOVE_FIRE_BUTTON_TOP_OFFSET_Y_XHDPI = -64;
    
    private static final int WEAPON_BUTTON_WIDTH_XHDPI = 128;
    private static final int WEAPON_BUTTON_HEIGHT_XHDPI = 128;
    private static final int WEAPON_BUTTON_LEFT_TOUCH_BUFFER_XHDPI = 16;
    private static final int WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI = 16;
    
    private static final int WEAPON_BUTTON_LEFT_OFFSET_XHDPI = -10;
//    private static final int WEAPON_BUTTON_LEFT_OFFSET_XHDPI = 40;
////    private static final int WEAPON_BUTTON_LEFT_OFFSET_XHDPI = 15;
    private static final int WEAPON_BUTTON_BOTTOM_OFFSET_XHDPI = 40;
//    private static final int WEAPON_BUTTON_BOTTOM_OFFSET_XHDPI = 10;
    
    private static final int VIEWANGLE_BAR_BASE_WIDTH_XHDPI = 256;
    private static final int VIEWANGLE_BAR_BASE_HEIGHT_XHDPI = 256;
    private static final int VIEWANGLE_BAR_BASE_LEFT_RIGHT_OFFSET_XHDPI = 0;
    private static final int VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_XHDPI = 0;
    private static final int VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER_XHDPI = 78;
    private static final int VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_XHDPI = 17;
    
    private static final int VIEWANGLE_BAR_BUTTON_WIDTH_XHDPI = 128;
    private static final int VIEWANGLE_BAR_BUTTON_HEIGHT_XHDPI = 128;
    private static final int VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI = 17;
    private static final int VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT_XHDPI = 111;
    
    private static final int DROID_ASTRONAUT_SNAPSHOT_WIDTH_XHDPI = 64;
    private static final int DROID_ASTRONAUT_SNAPSHOT_HEIGHT_XHDPI = 64;
    private static final int DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_XHDPI = 50;
//    private static final int DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_XHDPI = 10;
	private static final int DROID_SNAPSHOT_TOP_OFFSET_XHDPI = 50;
//	private static final int DROID_SNAPSHOT_TOP_OFFSET_XHDPI = 24;
////	private static final int DROID_SNAPSHOT_TOP_OFFSET_XHDPI = 48;
	private static final int ASTRONAUT_SNAPSHOT_TOP_OFFSET_XHDPI = 200;
//	private static final int ASTRONAUT_SNAPSHOT_TOP_OFFSET_XHDPI = 88;
//    private static final int DROID_SNAPSHOT_TOP_OFFSET_XHDPI = 20;
    private static final int ASTRONAUT_SNAPSHOT_BOTTOM_BUFFER_XHDPI = 0;
    private static final int NUMBER_SPACE_WIDTH_XHDPI = 45;
//    private static final int NUMBER_SPACE_WIDTH_XHDPI = 30;
////    private static final int NUMBER_SPACE_WIDTH_XHDPI = 52;
	
//    private static final int MOVE_FIRE_BUTTON_WIDTH = 128;
//    private static final int MOVE_FIRE_BUTTON_HEIGHT = 128;
//    private static final int MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET = -5;
//    private static final int MOVE_FIRE_BUTTON_BOTTOM_OFFSET = -5;
//    private static final int MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER = 20;
//    private static final int MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER = 20;
//    
//    private static final int MOVE_FIRE_BUTTON_TOP_WIDTH = 64;
//    private static final int MOVE_FIRE_BUTTON_TOP_HEIGHT = 64;
////    private static final int MOVE_BUTTON_TOP_CENTER_X = 59;
////    private static final int MOVE_BUTTON_TOP_CENTER_Y = 59;
////    private static final int FIRE_BUTTON_TOP_CENTER_X = 421;
////    private static final int FIRE_BUTTON_TOP_CENTER_Y = 59;
//    
//    private static final int MOVE_BUTTON_TOP_OFFSET_X = -32;
//    private static final int MOVE_BUTTON_TOP_OFFSET_Y = -32;
//    private static final int FIRE_BUTTON_TOP_OFFSET_X = -32;
//    private static final int FIRE_BUTTON_TOP_OFFSET_Y = -32;
//    
//    private static final int WEAPON_BUTTON_WIDTH = 64;
//    private static final int WEAPON_BUTTON_HEIGHT = 64;
//    private static final int WEAPON_BUTTON_LEFT_TOUCH_BUFFER = 7;
//    private static final int WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER = 7;
//    
//    private static final int WEAPON1_BUTTON_LEFT_OFFSET = 0;
//    private static final int WEAPON1_BUTTON_BOTTOM_OFFSET = 10;    
//
//    private static final int WEAPON2_BUTTON_LEFT_OFFSET = 0;
//    private static final int WEAPON2_BUTTON_BOTTOM_OFFSET = 10;
//    
//    private static final int VIEWANGLE_BAR_BASE_WIDTH = 128;
//    private static final int VIEWANGLE_BAR_BASE_HEIGHT = 128;
//    private static final int VIEWANGLE_BAR_BASE_LEFT_RIGHT_OFFSET = -5;
//    private static final int VIEWANGLE_BAR_BASE_BOTTOM_OFFSET = -5;
//    private static final int VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER = 44;
//    private static final int VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER = 24;
//    
//    private static final int VIEWANGLE_BAR_BUTTON_WIDTH = 64;
//    private static final int VIEWANGLE_BAR_BUTTON_HEIGHT = 64;
////    private static final int VIEWANGLE_BAR_BUTTON_LEFT_RIGHT_OFFSET = 0;
////    private static final int VIEWANGLE_BAR_BUTTON_BOTTOM_OFFSET = 0;
////    private static final int VIEWANGLE_BAR_BUTTON_LEFT_TOUCH_BUFFER = 12;
////    private static final int VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER = 24;
//    private static final int VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT = 40;
    
//    private long mLastTime;
    
    private GameThread mGameThread;
    private Thread mGame;
    private ObjectManager mGameRoot;
    
//    private int mDifficultyLevel;
    
    private GameRenderer mRenderer;
//    private DroidGLSurfaceView mSurfaceView;
    private boolean mRunning;
//    /*** If mPauseStopped = true, in process of Game resuming back to Foreground */
//    private boolean mPauseStopped;
    private boolean mBootstrapComplete;
    private boolean mGameRestart;
    
    private LevelTree.Level mPendingLevel;
    private LevelTree.Level mCurrentLevel;
    private LevelTree.Level mLastLevel;
    
    private boolean mGLDataLoaded;
//    private ContextParameters mContextParameters;
    
    private boolean mObjectsSpawned;
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    private int mViewWidth;
    private int mViewHeight;
//    private float mInverseViewScaleY;
    private int mScreenDensity;
    
    private Type mWeaponActiveType;
    private Type mWeaponInventoryType;
    
    private float mMoveFireButtonTouchWidth;
    private float mMoveFireButtonTouchHeight;
    private float mMoveFireButtonTouchRadius;
    private float mMoveButtonTouchLeftX;
    private float mMoveButtonTouchBottomY;
//    private float mMoveButtonTouchWidth;
//    private float mMoveButtonTouchHeight;
    private float mMoveButtonCenterX;
    private float mMoveButtonCenterY;
    private float mMoveButtonTopRenderLeftX;
    private float mMoveButtonTopRenderBottomY;
//    private float mMoveButtonTopCenterX;
//    private float mMoveButtonTopCenterY;
    
    private float mFireButtonTouchLeftX;
    private float mFireButtonTouchBottomY;
//    private float mFireButtonTouchWidth;
//    private float mFireButtonTouchHeight;
    private float mFireButtonCenterX;
    private float mFireButtonCenterY;
    private float mFireButtonTopRenderLeftX;
    private float mFireButtonTopRenderBottomY;
//    private float mFireButtonTopCenterX;
//    private float mFireButtonTopCenterY;
    
    private float mWeaponButtonTouchWidth;
    private float mWeaponButtonTouchHeight;
    private float mWeapon1ButtonTouchLeftX;
    private float mWeapon1ButtonTouchBottomY;
    private float mWeapon2ButtonTouchLeftX;
    private float mWeapon2ButtonTouchBottomY;
//    private float mWeapon3ButtonTouchLeftX;
//    private float mWeapon3ButtonTouchBottomY;
    
    private float mViewangleBarBaseTouchLeftX;
    private float mViewangleBarBaseTouchBottomY; 
    private float mViewangleBarBaseTouchWidth;
    private float mViewangleBarBaseTouchHeight; 
    private float mViewangleBarButtonTouchLeftX;
    private float mViewangleBarButtonTouchBottomY; 
    private float mViewangleBarButtonTouchWidth;
    private float mViewangleBarButtonTouchHeight; 
//    private float mViewangleBarButtonRenderBottomY;
    
//    private boolean mTempMovePress;
//    private boolean mTempFirePress;
//    private boolean mTempWeapon1Press;
//    private boolean mTempWeapon2Press;
//    private boolean mTempViewangleBarPress;
    
    private int mMovePointerId;
    private int mFirePointerId;
    private int mWeapon1PointerId;
    private int mWeapon2PointerId;
    private int mViewangleBarPointerId;
    
//    private MediaPlayer mBackgroundMusic;
//    private int mMusicLevel;
//	private boolean mMusicPlaying;
	private boolean mSoundSystemActive;
	
	private long mLevelStartTime;
	private boolean mLevelLoaded;
	
	private float mLastTime;
	
	private int mLevelRow;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public Game() {
        super();
        
//    	mLastTime = SystemClock.uptimeMillis();
        
//        mDifficultyLevel = 1;
    	
        mRunning = false;
//        mPauseStopped = false;
        mBootstrapComplete = false;
        mGameRestart = false;
        
        mGLDataLoaded = false;
//        mContextParameters = new ContextParameters();
        
        mObjectsSpawned = false;
        
        mWeaponActiveType = Type.INVALID;
        mWeaponInventoryType = Type.INVALID;
        
//        mTempMovePress = false;
//        mTempFirePress = false;
//        mTempWeapon1Press = false;
//        mTempWeapon2Press = false;
//        mTempViewangleBarPress = false;
        
        mMovePointerId = -1;
        mFirePointerId = -1;
        mWeapon1PointerId = -1;
        mWeapon2PointerId = -1;
        mViewangleBarPointerId = -1;
        
//        mMusicPlaying = false;
        mSoundSystemActive = false;
        mLevelLoaded = false;
        
        mLastTime = 0.032f;		// initial timer value to receive first touch event
        
        mLevelRow = 0;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }

    /** 
     * Creates core game objects and constructs the game engine object graph.  Note that the
     * game does not actually begin running after this function is called (see start() below).
     * Also note that textures are not loaded from the resource pack by this function, as OpenGl
     * isn't yet available.
     * @param context
     */
    public void bootstrap(Context context, int viewWidth, int viewHeight, int gameWidth, int gameHeight, int density) {
//    public void bootstrap(Context context, int viewWidth, int viewHeight, int gameWidth, int gameHeight) {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game bootstrap() START");	
        }
		
        if (!mBootstrapComplete) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game bootstrap() !mBootstrapComplete");	
            }

        	// TODO Is this part of main Game Graph loop system and initiates GL state each loop?
            // Create core systems
            BaseObject.sSystemRegistry.openGLSystem = new OpenGLSystem(null);
            
            GameParameters.gamePause = false;
//            BaseObject.sSystemRegistry.setGamePause(false);
            setGameRestart(false);
//            mGameRestart = false;
    
            // TODO Re-enable - CustomToastSystem
//            BaseObject.sSystemRegistry.customToastSystem = new CustomToastSystem(context);
            
            mViewWidth = viewWidth;
            mViewHeight = viewHeight;
            
//            ContextParameters params = mContextParameters;
            
            // viewWidth, viewHeight for Touch Events; gameWidth, gameHeight for Render
            GameParameters.viewWidth = viewWidth;
            GameParameters.viewHeight = viewHeight;
            GameParameters.gameWidth = gameWidth;
            GameParameters.gameHeight = gameHeight;
            
            // viewScaleX, viewScaleY used for scaling Touch region and scaling png for Render based on mdpi, hdpi, other
            float viewScaleX = (float)viewWidth / gameWidth;
            float viewScaleY = (float)viewHeight / gameHeight;
//            mInverseViewScaleY = 1 / viewScaleY;
            
            GameParameters.viewScaleX = viewScaleX;
            GameParameters.viewScaleY = viewScaleY;
            
            if (GameParameters.debug) {
                Log.i("GameFlow", "Game bootstrap() Density = " + density);	
            }
            
            // Density: MDPI 160, HDPI 240, XHDPI 320
            if (density > 170 && density < 260) {			// Allow for device density variance
//            if (viewScaleX > 1.1f) {			// Allow for float variance
            	mScreenDensity = 1;
            	GameParameters.screenDensity = 1;	// set to hdpi flag
            } else if (density >= 260) {
            	mScreenDensity = 2;
            	GameParameters.screenDensity = 2;	// set to xhdpi flag
            }
            
//            Log.i("TouchCounter", "Game bootstrap() screenDensity; gameWidth,Height; viewWidth,Height = " + 
//            		GameParameters.screenDensity + "; " +
//            		gameWidth + ", " + gameHeight + "; " +
//            		viewWidth + ", " + viewHeight);
             
            mRenderer = new GameRenderer(context, this, gameWidth, gameHeight, viewScaleX, viewScaleY);
            
            float moveButtonRenderLeftX = 0.0f;
            float moveButtonRenderBottomY = 0.0f;
            float moveButtonTopOffsetX = 0.0f;
            float moveButtonTopOffsetY = 0.0f;
            float fireButtonRenderLeftX = 0.0f;
            float fireButtonRenderBottomY = 0.0f;
            float fireButtonTopOffsetX = 0.0f;
            float fireButtonTopOffsetY = 0.0f;
            float weapon1ButtonRenderLeftX = 0.0f;
            float weapon1ButtonRenderBottomY = 0.0f;
            float weapon2ButtonRenderLeftX = 0.0f;
            float weapon2ButtonRenderBottomY = 0.0f;
            float viewangleBarBaseRenderLeftX = 0.0f;
            float viewangleBarBaseRenderBottomY = 0.0f;
            float viewangleBarButtonRenderLeftX = 0.0f;
            float viewangleBarButtonRenderBottomY = 0.0f;
            float droidSnapshotRenderBottomY = 0.0f;
            float astronautSnapshotRenderBottomY = 0.0f;
            
            if (mScreenDensity == 0) {
//            if (GameParameters.screenDensity == 0) {
            	// Load mdpi render and touch dimensions
                mMoveFireButtonTouchWidth = MOVE_FIRE_BUTTON_WIDTH_MDPI - (MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_MDPI * 2);
                mMoveFireButtonTouchHeight = MOVE_FIRE_BUTTON_HEIGHT_MDPI - (MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI * 2);
                mMoveFireButtonTouchRadius = mMoveFireButtonTouchWidth / 2;
                
                moveButtonRenderLeftX = MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI;
                mMoveButtonTouchLeftX = moveButtonRenderLeftX + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
                moveButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET_MDPI;
                mMoveButtonTouchBottomY = moveButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI;
                mMoveButtonCenterX = mMoveButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);
                mMoveButtonCenterY = mMoveButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);
                
                mMoveButtonTopRenderLeftX = moveButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_TOP_WIDTH_MDPI) / 2);
                mMoveButtonTopRenderBottomY = moveButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT_MDPI - MOVE_FIRE_BUTTON_TOP_HEIGHT_MDPI) / 2);
                
                moveButtonTopOffsetX = MOVE_FIRE_BUTTON_TOP_OFFSET_X_MDPI;
                moveButtonTopOffsetY = MOVE_FIRE_BUTTON_TOP_OFFSET_Y_MDPI;
            	
                fireButtonRenderLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI;
//                fireButtonRenderLeftX = gameWidth - MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI;
                mFireButtonTouchLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
//                mFireButtonTouchLeftX = gameWidth - MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
//                mFireButtonTouchLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_MDPI - MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
                fireButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET_MDPI;
                mFireButtonTouchBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI;
                mFireButtonCenterX = mFireButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);
                mFireButtonCenterY = mFireButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);
                
                mFireButtonTopRenderLeftX = fireButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH_MDPI - MOVE_FIRE_BUTTON_TOP_WIDTH_MDPI) / 2);
                mFireButtonTopRenderBottomY = fireButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT_MDPI - MOVE_FIRE_BUTTON_TOP_HEIGHT_MDPI) / 2);
                
                fireButtonTopOffsetX = MOVE_FIRE_BUTTON_TOP_OFFSET_X_MDPI;
                fireButtonTopOffsetY = MOVE_FIRE_BUTTON_TOP_OFFSET_Y_MDPI;
                
                mWeaponButtonTouchWidth = WEAPON_BUTTON_WIDTH_MDPI - (WEAPON_BUTTON_LEFT_TOUCH_BUFFER_MDPI * 2);
                mWeaponButtonTouchHeight = WEAPON_BUTTON_HEIGHT_MDPI - (WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI * 2);
                
                weapon1ButtonRenderLeftX = fireButtonRenderLeftX + WEAPON_BUTTON_LEFT_OFFSET_MDPI;
//              weapon1ButtonRenderLeftX = fireButtonRenderLeftX;
                mWeapon1ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_WIDTH_MDPI + WEAPON_BUTTON_LEFT_OFFSET_MDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
//                mWeapon1ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_WIDTH_MDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
                weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_MDPI - MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI;
//            	weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_MDPI -
//            			WEAPON_BUTTON_HEIGHT_MDPI + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI;
            	mWeapon1ButtonTouchBottomY = weapon1ButtonRenderBottomY + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI;
                
            	weapon2ButtonRenderLeftX = fireButtonRenderLeftX + WEAPON_BUTTON_WIDTH_MDPI - WEAPON_BUTTON_LEFT_OFFSET_MDPI;
//                weapon2ButtonRenderLeftX = weapon1ButtonRenderLeftX + WEAPON_BUTTON_WIDTH_MDPI;
            	mWeapon2ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_LEFT_OFFSET_MDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_MDPI;
//                mWeapon2ButtonTouchLeftX = mWeapon1ButtonTouchLeftX + WEAPON_BUTTON_WIDTH_MDPI;
                weapon2ButtonRenderBottomY = weapon1ButtonRenderBottomY;
                mWeapon2ButtonTouchBottomY = mWeapon1ButtonTouchBottomY;
                
                mViewangleBarBaseTouchWidth = VIEWANGLE_BAR_BASE_WIDTH_MDPI - (VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER_MDPI * 2);
                mViewangleBarBaseTouchHeight = VIEWANGLE_BAR_BASE_HEIGHT_MDPI - (VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_MDPI * 2);
                
                viewangleBarBaseRenderLeftX = fireButtonRenderLeftX;
                mViewangleBarBaseTouchLeftX = mFireButtonCenterX - (mViewangleBarBaseTouchWidth / 2);
                viewangleBarBaseRenderBottomY = viewHeight - VIEWANGLE_BAR_BASE_HEIGHT_MDPI;
//                viewangleBarBaseRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_MDPI;
                mViewangleBarBaseTouchBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_MDPI;
                
                viewangleBarButtonRenderLeftX = viewangleBarBaseRenderLeftX + (VIEWANGLE_BAR_BASE_WIDTH_MDPI / 2) - (VIEWANGLE_BAR_BUTTON_WIDTH_MDPI / 2);
                // Initial offset at bottom of viewangleBarBase
                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY - (VIEWANGLE_BAR_BUTTON_HEIGHT_MDPI / 2) +
                		VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER_MDPI;
//                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_MDPI + 
//                		VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_MDPI - (VIEWANGLE_BAR_BUTTON_HEIGHT_MDPI / 2);
//                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + (VIEWANGLE_BAR_BASE_HEIGHT_MDPI / 2) - (VIEWANGLE_BAR_BUTTON_HEIGHT_MDPI / 2);
                
                droidSnapshotRenderBottomY = mViewHeight - DROID_SNAPSHOT_TOP_OFFSET_MDPI - DROID_ASTRONAUT_SNAPSHOT_HEIGHT_MDPI;
                astronautSnapshotRenderBottomY = mViewHeight - ASTRONAUT_SNAPSHOT_TOP_OFFSET_MDPI - DROID_ASTRONAUT_SNAPSHOT_HEIGHT_MDPI;
            	
            } else if (mScreenDensity == 1) {
//            } else if (GameParameters.screenDensity == 1) {
            	// Load hdpi render and touch dimensions
                mMoveFireButtonTouchWidth = MOVE_FIRE_BUTTON_WIDTH_HDPI - (MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI * 2);
                mMoveFireButtonTouchHeight = MOVE_FIRE_BUTTON_HEIGHT_HDPI - (MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI * 2);
                mMoveFireButtonTouchRadius = mMoveFireButtonTouchWidth / 2;
                
                moveButtonRenderLeftX = MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI;
                mMoveButtonTouchLeftX = moveButtonRenderLeftX + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
                moveButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET_HDPI;
                mMoveButtonTouchBottomY = moveButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI;
                mMoveButtonCenterX = mMoveButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);
                mMoveButtonCenterY = mMoveButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);
                
                mMoveButtonTopRenderLeftX = moveButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_TOP_WIDTH_HDPI) / 2);
                mMoveButtonTopRenderBottomY = moveButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT_HDPI - MOVE_FIRE_BUTTON_TOP_HEIGHT_HDPI) / 2);
                
                moveButtonTopOffsetX = MOVE_FIRE_BUTTON_TOP_OFFSET_X_HDPI;
                moveButtonTopOffsetY = MOVE_FIRE_BUTTON_TOP_OFFSET_Y_HDPI;
            	
                fireButtonRenderLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI;
//                fireButtonRenderLeftX = gameWidth - MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI;
                mFireButtonTouchLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
//                mFireButtonTouchLeftX = gameWidth - MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
//                mFireButtonTouchLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_HDPI - MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
                fireButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET_HDPI;
                mFireButtonTouchBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI;
                mFireButtonCenterX = mFireButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);
                mFireButtonCenterY = mFireButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);
                
                mFireButtonTopRenderLeftX = fireButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH_HDPI - MOVE_FIRE_BUTTON_TOP_WIDTH_HDPI) / 2);
                mFireButtonTopRenderBottomY = fireButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT_HDPI - MOVE_FIRE_BUTTON_TOP_HEIGHT_HDPI) / 2);
                
                fireButtonTopOffsetX = MOVE_FIRE_BUTTON_TOP_OFFSET_X_HDPI;
                fireButtonTopOffsetY = MOVE_FIRE_BUTTON_TOP_OFFSET_Y_HDPI;
                
                mWeaponButtonTouchWidth = WEAPON_BUTTON_WIDTH_HDPI - (WEAPON_BUTTON_LEFT_TOUCH_BUFFER_HDPI * 2);
                mWeaponButtonTouchHeight = WEAPON_BUTTON_HEIGHT_HDPI - (WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI * 2);
                
                weapon1ButtonRenderLeftX = fireButtonRenderLeftX + WEAPON_BUTTON_LEFT_OFFSET_HDPI;
//                weapon1ButtonRenderLeftX = fireButtonRenderLeftX;
                mWeapon1ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_WIDTH_HDPI + WEAPON_BUTTON_LEFT_OFFSET_HDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
//                mWeapon1ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_WIDTH_HDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
            	weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_HDPI - MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI;
//            	weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_HDPI -
//            			WEAPON_BUTTON_HEIGHT_HDPI + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI;
            	mWeapon1ButtonTouchBottomY = weapon1ButtonRenderBottomY + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI;
                
            	weapon2ButtonRenderLeftX = fireButtonRenderLeftX + WEAPON_BUTTON_WIDTH_HDPI - WEAPON_BUTTON_LEFT_OFFSET_HDPI;
//                weapon2ButtonRenderLeftX = weapon1ButtonRenderLeftX + WEAPON_BUTTON_WIDTH_HDPI;
            	mWeapon2ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_LEFT_OFFSET_HDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_HDPI;
//                mWeapon2ButtonTouchLeftX = mWeapon1ButtonTouchLeftX + WEAPON_BUTTON_WIDTH_HDPI;
                weapon2ButtonRenderBottomY = weapon1ButtonRenderBottomY;
                mWeapon2ButtonTouchBottomY = mWeapon1ButtonTouchBottomY;
                
                mViewangleBarBaseTouchWidth = VIEWANGLE_BAR_BASE_WIDTH_HDPI - (VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER_HDPI * 2);
                mViewangleBarBaseTouchHeight = VIEWANGLE_BAR_BASE_HEIGHT_HDPI - (VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_HDPI * 2);
                
                viewangleBarBaseRenderLeftX = fireButtonRenderLeftX;
                mViewangleBarBaseTouchLeftX = mFireButtonCenterX - (mViewangleBarBaseTouchWidth / 2);
                viewangleBarBaseRenderBottomY = viewHeight - VIEWANGLE_BAR_BASE_HEIGHT_HDPI;
//                viewangleBarBaseRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_HDPI;
                mViewangleBarBaseTouchBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_HDPI;
                
                viewangleBarButtonRenderLeftX = viewangleBarBaseRenderLeftX + (VIEWANGLE_BAR_BASE_WIDTH_HDPI / 2) - (VIEWANGLE_BAR_BUTTON_WIDTH_HDPI / 2);
                // Initial offset at bottom of viewangleBarBase
                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY - (VIEWANGLE_BAR_BUTTON_HEIGHT_HDPI / 2) +
                		VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER_HDPI;
//                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_HDPI + 
//                		(VIEWANGLE_BAR_BASE_HEIGHT_HDPI / 2) - (VIEWANGLE_BAR_BUTTON_HEIGHT_HDPI / 2);
//                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_HDPI + 
//                		VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_HDPI - (VIEWANGLE_BAR_BUTTON_HEIGHT_HDPI / 2);
//                viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + (VIEWANGLE_BAR_BASE_HEIGHT_HDPI / 2) - (VIEWANGLE_BAR_BUTTON_HEIGHT_HDPI / 2);
                
                droidSnapshotRenderBottomY = mViewHeight - DROID_SNAPSHOT_TOP_OFFSET_HDPI - DROID_ASTRONAUT_SNAPSHOT_HEIGHT_HDPI;
                astronautSnapshotRenderBottomY = mViewHeight - ASTRONAUT_SNAPSHOT_TOP_OFFSET_HDPI - DROID_ASTRONAUT_SNAPSHOT_HEIGHT_HDPI;
            } else if (mScreenDensity == 2) {
//              } else if (GameParameters.screenDensity == 1) {
              	// Load hdpi render and touch dimensions
                  mMoveFireButtonTouchWidth = MOVE_FIRE_BUTTON_WIDTH_XHDPI - (MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_XHDPI * 2);
                  mMoveFireButtonTouchHeight = MOVE_FIRE_BUTTON_HEIGHT_XHDPI - (MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI * 2);
                  mMoveFireButtonTouchRadius = mMoveFireButtonTouchWidth / 2;
                  
                  moveButtonRenderLeftX = MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI;
                  mMoveButtonTouchLeftX = moveButtonRenderLeftX + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
                  moveButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET_XHDPI;
                  mMoveButtonTouchBottomY = moveButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI;
                  mMoveButtonCenterX = mMoveButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);
                  mMoveButtonCenterY = mMoveButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);
                  
                  mMoveButtonTopRenderLeftX = moveButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_TOP_WIDTH_XHDPI) / 2);
                  mMoveButtonTopRenderBottomY = moveButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT_XHDPI - MOVE_FIRE_BUTTON_TOP_HEIGHT_XHDPI) / 2);
                  
                  moveButtonTopOffsetX = MOVE_FIRE_BUTTON_TOP_OFFSET_X_XHDPI;
                  moveButtonTopOffsetY = MOVE_FIRE_BUTTON_TOP_OFFSET_Y_XHDPI;
              	
                  fireButtonRenderLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI;
//                  fireButtonRenderLeftX = gameWidth - MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI;
                  mFireButtonTouchLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
//                  mFireButtonTouchLeftX = gameWidth - MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
//                  mFireButtonTouchLeftX = viewWidth - MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET_XHDPI - MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
                  fireButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET_XHDPI;
                  mFireButtonTouchBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI;
                  mFireButtonCenterX = mFireButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);
                  mFireButtonCenterY = mFireButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);
                  
                  mFireButtonTopRenderLeftX = fireButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH_XHDPI - MOVE_FIRE_BUTTON_TOP_WIDTH_XHDPI) / 2);
                  mFireButtonTopRenderBottomY = fireButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT_XHDPI - MOVE_FIRE_BUTTON_TOP_HEIGHT_XHDPI) / 2);
                  
                  fireButtonTopOffsetX = MOVE_FIRE_BUTTON_TOP_OFFSET_X_XHDPI;
                  fireButtonTopOffsetY = MOVE_FIRE_BUTTON_TOP_OFFSET_Y_XHDPI;
                  
                  mWeaponButtonTouchWidth = WEAPON_BUTTON_WIDTH_XHDPI - (WEAPON_BUTTON_LEFT_TOUCH_BUFFER_XHDPI * 2);
                  mWeaponButtonTouchHeight = WEAPON_BUTTON_HEIGHT_XHDPI - (WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI * 2);
                  
                  weapon1ButtonRenderLeftX = fireButtonRenderLeftX + WEAPON_BUTTON_LEFT_OFFSET_XHDPI;
//                  weapon1ButtonRenderLeftX = fireButtonRenderLeftX;
                  mWeapon1ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_WIDTH_XHDPI + WEAPON_BUTTON_LEFT_OFFSET_XHDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
//                  mWeapon1ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_WIDTH_XHDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
              	weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_XHDPI - MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI;
//              	weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_XHDPI -
//              			WEAPON_BUTTON_HEIGHT_XHDPI + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI;
              	mWeapon1ButtonTouchBottomY = weapon1ButtonRenderBottomY + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI;
                  
              	weapon2ButtonRenderLeftX = fireButtonRenderLeftX + WEAPON_BUTTON_WIDTH_XHDPI - WEAPON_BUTTON_LEFT_OFFSET_XHDPI;
//                  weapon2ButtonRenderLeftX = weapon1ButtonRenderLeftX + WEAPON_BUTTON_WIDTH_XHDPI;
              	mWeapon2ButtonTouchLeftX = mFireButtonCenterX - WEAPON_BUTTON_LEFT_OFFSET_XHDPI + WEAPON_BUTTON_LEFT_TOUCH_BUFFER_XHDPI;
//                  mWeapon2ButtonTouchLeftX = mWeapon1ButtonTouchLeftX + WEAPON_BUTTON_WIDTH_XHDPI;
                  weapon2ButtonRenderBottomY = weapon1ButtonRenderBottomY;
                  mWeapon2ButtonTouchBottomY = mWeapon1ButtonTouchBottomY;
                  
                  mViewangleBarBaseTouchWidth = VIEWANGLE_BAR_BASE_WIDTH_XHDPI - (VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER_XHDPI * 2);
                  mViewangleBarBaseTouchHeight = VIEWANGLE_BAR_BASE_HEIGHT_XHDPI - (VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_XHDPI * 2);
                  
                  viewangleBarBaseRenderLeftX = fireButtonRenderLeftX;
                  mViewangleBarBaseTouchLeftX = mFireButtonCenterX - (mViewangleBarBaseTouchWidth / 2);
                  viewangleBarBaseRenderBottomY = viewHeight - VIEWANGLE_BAR_BASE_HEIGHT_XHDPI;
//                  viewangleBarBaseRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT_XHDPI;
                  mViewangleBarBaseTouchBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_XHDPI;
                  
                  viewangleBarButtonRenderLeftX = viewangleBarBaseRenderLeftX + (VIEWANGLE_BAR_BASE_WIDTH_XHDPI / 2) - (VIEWANGLE_BAR_BUTTON_WIDTH_XHDPI / 2);
                  // Initial offset at bottom of viewangleBarBase
                  viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY - (VIEWANGLE_BAR_BUTTON_HEIGHT_XHDPI / 2) +
                  		VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER_XHDPI;
//                  viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_XHDPI + 
//                  		(VIEWANGLE_BAR_BASE_HEIGHT_XHDPI / 2) - (VIEWANGLE_BAR_BUTTON_HEIGHT_XHDPI / 2);
//                  viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_OFFSET_XHDPI + 
//                  		VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER_XHDPI - (VIEWANGLE_BAR_BUTTON_HEIGHT_XHDPI / 2);
//                  viewangleBarButtonRenderBottomY = viewangleBarBaseRenderBottomY + (VIEWANGLE_BAR_BASE_HEIGHT_XHDPI / 2) - (VIEWANGLE_BAR_BUTTON_HEIGHT_XHDPI / 2);
                  
                  droidSnapshotRenderBottomY = mViewHeight - DROID_SNAPSHOT_TOP_OFFSET_XHDPI - DROID_ASTRONAUT_SNAPSHOT_HEIGHT_XHDPI;
                  astronautSnapshotRenderBottomY = mViewHeight - ASTRONAUT_SNAPSHOT_TOP_OFFSET_XHDPI - DROID_ASTRONAUT_SNAPSHOT_HEIGHT_XHDPI;
              }
            
//            Log.i("TouchCounter", "Game bootstrap() mMoveButtonRenderLeftX,BottomY; mFireButtonRenderLeftX,BottomY; " +
//            		"mWeapon1RenderLeftX,BottomY; mWeapon2RenderLeftX,BottomY; mViewAngleBarBaseRenderLeftX,BottomY = " + 
//            		moveButtonRenderLeftX + ", " + moveButtonRenderBottomY + "; " +
//            		fireButtonRenderLeftX + ", " + fireButtonRenderBottomY + "; " +
//            		weapon1ButtonRenderLeftX + ", " + weapon1ButtonRenderBottomY + "; " +
//            		weapon2ButtonRenderLeftX + ", " + weapon2ButtonRenderBottomY + "; " +
//            		viewangleBarBaseRenderLeftX + ", " + viewangleBarBaseRenderBottomY);
//            
//            Log.i("TouchCounter", "Game bootstrap() mMoveFireButtonTouchWidth; mMoveButtonTouchLeftX,BottomY; mMoveButtonCenterX,Y; mFireButtonTouchLeftX,BottomY; mFireButtonCenterX,Y; " +
//            		"mWeaponButtonTouchWidth; mWeapon1TouchLeftX,BottomY; mWeapon2TouchLeftX,BottomY; mViewangleBarBaseTouchWidth; mViewAngleBarBaseTouchLeftX,BottomY = " + 
//            		mMoveFireButtonTouchWidth + "; " + 
//            		mMoveButtonTouchLeftX + ", " + mMoveButtonTouchBottomY + "; " +
//            		mMoveButtonCenterX + ", " + mMoveButtonCenterY + "; " +
//            		mFireButtonTouchLeftX + ", " + mFireButtonTouchBottomY + "; " +
//            		mFireButtonCenterX + ", " + mFireButtonCenterY + "; " +
//            		mWeaponButtonTouchWidth + "; " + mWeapon1ButtonTouchLeftX + ", " + mWeapon1ButtonTouchBottomY + "; " +
//            		mWeapon2ButtonTouchLeftX + ", " + mWeapon2ButtonTouchBottomY + "; " +
//            		mViewangleBarBaseTouchWidth + "; " +
//            		mViewangleBarBaseTouchLeftX + ", " + mViewangleBarBaseTouchBottomY);
            
//            mMoveFireButtonTouchWidth = (MOVE_FIRE_BUTTON_WIDTH - (MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER * 2)) * viewScaleX;
//            mMoveFireButtonTouchHeight = (MOVE_FIRE_BUTTON_HEIGHT - (MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER * 2)) * viewScaleY;
//            mMoveFireButtonTouchRadius = mMoveFireButtonTouchWidth / 2;
//            
//            // Render Buttons x,z are not scaled here since they're scaled by hdpi folder. Touch Buttons x,z are scaled here.
//            float moveButtonRenderLeftX = MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET;
//            mMoveButtonTouchLeftX = (moveButtonRenderLeftX + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER) * viewScaleX;
//            float moveButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET;
//            mMoveButtonTouchBottomY = (moveButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER) * viewScaleY;
//            mMoveButtonCenterX = mMoveButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);  // already scaled
//            mMoveButtonCenterY = mMoveButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);  // already scaled
//            
//            mMoveButtonTopRenderLeftX = moveButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH - MOVE_FIRE_BUTTON_TOP_WIDTH) / 2);
//            mMoveButtonTopRenderBottomY = moveButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT - MOVE_FIRE_BUTTON_TOP_HEIGHT) / 2);
//            
//            float moveButtonTopOffsetX = MOVE_BUTTON_TOP_OFFSET_X;
//            float moveButtonTopOffsetY = MOVE_BUTTON_TOP_OFFSET_Y;
//            
//            float droidAstronautSnapshotRenderLeftX = MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET + 20;  // adjust for MoveFireButton offset
//        	
//            float fireButtonRenderLeftX = gameWidth - MOVE_FIRE_BUTTON_LEFT_RIGHT_OFFSET - MOVE_FIRE_BUTTON_WIDTH;
//            mFireButtonTouchLeftX = (fireButtonRenderLeftX + MOVE_FIRE_BUTTON_LEFT_TOUCH_BUFFER) * viewScaleX;
//            float fireButtonRenderBottomY = MOVE_FIRE_BUTTON_BOTTOM_OFFSET;
//            mFireButtonTouchBottomY = (fireButtonRenderBottomY + MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER) * viewScaleY;
//            mFireButtonCenterX = mFireButtonTouchLeftX + (mMoveFireButtonTouchWidth / 2);  // already scaled
//            mFireButtonCenterY = mFireButtonTouchBottomY + (mMoveFireButtonTouchHeight / 2);  // already scaled
//            
//            mFireButtonTopRenderLeftX = fireButtonRenderLeftX + ((MOVE_FIRE_BUTTON_WIDTH - MOVE_FIRE_BUTTON_TOP_WIDTH) / 2);
//            mFireButtonTopRenderBottomY = fireButtonRenderBottomY + ((MOVE_FIRE_BUTTON_HEIGHT - MOVE_FIRE_BUTTON_TOP_HEIGHT) / 2);
//            
//            float fireButtonTopOffsetX = FIRE_BUTTON_TOP_OFFSET_X;
//            float fireButtonTopOffsetY = FIRE_BUTTON_TOP_OFFSET_Y;
//            
//            mWeaponButtonTouchWidth = (WEAPON_BUTTON_WIDTH - (WEAPON_BUTTON_LEFT_TOUCH_BUFFER * 2)) * viewScaleX;
//            mWeaponButtonTouchHeight = (WEAPON_BUTTON_HEIGHT - (WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER * 2)) * viewScaleY;
//            
//            float weapon1ButtonRenderLeftX = fireButtonRenderLeftX;
//            mWeapon1ButtonTouchLeftX = (weapon1ButtonRenderLeftX + WEAPON_BUTTON_LEFT_TOUCH_BUFFER) * viewScaleX;
//        	float weapon1ButtonRenderBottomY = fireButtonRenderBottomY + MOVE_FIRE_BUTTON_HEIGHT -
//        		MOVE_FIRE_BUTTON_BOTTOM_TOUCH_BUFFER - WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER + WEAPON2_BUTTON_BOTTOM_OFFSET;
//        	mWeapon1ButtonTouchBottomY = (weapon1ButtonRenderBottomY + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER) * viewScaleY;
//            
//            float weapon2ButtonRenderLeftX = weapon1ButtonRenderLeftX + WEAPON_BUTTON_WIDTH;
//            mWeapon2ButtonTouchLeftX = (weapon2ButtonRenderLeftX + WEAPON_BUTTON_LEFT_TOUCH_BUFFER) * viewScaleX;
//            float weapon2ButtonRenderBottomY = weapon1ButtonRenderBottomY;
//            mWeapon2ButtonTouchBottomY = (weapon2ButtonRenderBottomY + WEAPON_BUTTON_BOTTOM_TOUCH_BUFFER) * viewScaleY;
//            
//            float viewangleBarBaseRenderLeftX = gameWidth - VIEWANGLE_BAR_BASE_LEFT_RIGHT_OFFSET - VIEWANGLE_BAR_BASE_WIDTH;
//            mViewangleBarBaseTouchLeftX = (viewangleBarBaseRenderLeftX + VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER) * viewScaleX;
//            float viewangleBarBaseRenderBottomY = gameHeight - VIEWANGLE_BAR_BASE_BOTTOM_OFFSET - VIEWANGLE_BAR_BASE_HEIGHT;
//            mViewangleBarBaseTouchBottomY = (viewangleBarBaseRenderBottomY + VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER) * viewScaleY;
//            mViewangleBarBaseTouchWidth = (VIEWANGLE_BAR_BASE_WIDTH - (VIEWANGLE_BAR_BASE_LEFT_TOUCH_BUFFER * 2)) * viewScaleX;
//            mViewangleBarBaseTouchHeight = (VIEWANGLE_BAR_BASE_HEIGHT - (VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER * 2)) * viewScaleY;
//            
//            float viewangleBarButtonRenderLeftX = gameWidth - VIEWANGLE_BAR_BASE_LEFT_RIGHT_OFFSET -
//            	(VIEWANGLE_BAR_BASE_WIDTH / 2) -(VIEWANGLE_BAR_BUTTON_WIDTH / 2);
////            mViewangleBarButtonTouchLeftX = (viewangleBarButtonRenderLeftX + VIEWANGLE_BAR_BUTTON_LEFT_TOUCH_BUFFER) * viewScaleX;
//            float viewangleBarButtonRenderBottomY = gameHeight - VIEWANGLE_BAR_BASE_BOTTOM_OFFSET -
//        		(VIEWANGLE_BAR_BASE_HEIGHT - VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER) -
//        		(VIEWANGLE_BAR_BUTTON_HEIGHT / 2);  // Initial offset at bottom of viewangleBarBase
////            mViewangleBarButtonRenderBottomY = gameHeight - VIEWANGLE_BAR_BASE_BOTTOM_OFFSET -
////            	VIEWANGLE_BAR_BASE_BOTTOM_TOUCH_BUFFER -(VIEWANGLE_BAR_BUTTON_HEIGHT / 2);
////            mViewangleBarButtonTouchBottomY = (mViewangleBarButtonRenderBottomY + VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER) * viewScaleY;
//            
////            mViewangleBarButtonTouchWidth = (VIEWANGLE_BAR_BUTTON_WIDTH - (VIEWANGLE_BAR_BUTTON_LEFT_TOUCH_BUFFER * 2)) * viewScaleX;
////            mViewangleBarButtonTouchHeight = (VIEWANGLE_BAR_BUTTON_HEIGHT - (VIEWANGLE_BAR_BUTTON_BOTTOM_TOUCH_BUFFER * 2)) * viewScaleY;
//
////            // Button defaults in center of Bar at 40. 80 possible set points, but only 9 specific height settings.
////            mViewangleBarButtonRenderBottomY -= 40;
    
            // TODO Is shortTermTextureLibrary still required?
            // Short-term textures are cleared between levels.
//            TextureLibrary shortTermTextureLibrary = new TextureLibrary();
//            BaseObject.sSystemRegistry.shortTermTextureLibrary = shortTermTextureLibrary;
            
            // FIXME DELETED 11/11/12
//            // Long-term textures persist between levels.
//            TextureLibrary longTermTextureLibrary = new TextureLibrary();
//            BaseObject.sSystemRegistry.longTermTextureLibrary = longTermTextureLibrary;
////          // FIXME Study to re-enable reset() for Object destroy() Optimization
//            BaseObject.sSystemRegistry.registerForReset(longTermTextureLibrary);
            
//            // The buffer library manages hardware VBOs.
//            BaseObject.sSystemRegistry.bufferLibrary = new BufferLibrary();
            
            SoundSystem sound = new SoundSystem();
            BaseObject.sSystemRegistry.soundSystem = sound;
//            BaseObject.sSystemRegistry.soundSystem = new SoundSystem();
            mSoundSystemActive = true;
            BaseObject.sSystemRegistry.registerForReset(sound);
            
            // The root of the game graph.
            MainLoop gameRoot = new MainLoop();
    
            InputSystem input = new InputSystem();
            BaseObject.sSystemRegistry.inputSystem = input;
            BaseObject.sSystemRegistry.registerForReset(input);
            
            LevelSystem level = new LevelSystem();
            BaseObject.sSystemRegistry.levelSystem = level;
            // levelSystem reset() called directly in stop()
            
//            BaseObject.sSystemRegistry.hitPointPool = new HitPointPool();

            GameObjectManager gameManager = new GameObjectManager(GameParameters.viewWidth * 2);
            BaseObject.sSystemRegistry.gameObjectManager = gameManager;
            // gameManager reset() not required. Handled in GameObjectManager destroyAll().
            
            GameObjectFactory objectFactory = new GameObjectFactory();
            BaseObject.sSystemRegistry.gameObjectFactory = objectFactory;
            objectFactory.context = context;
//            Log.i("GameFlow", "Game bootstrap() objectFactory.setWeapons(mWeaponActiveType, mWeaponInventoryType)");
            objectFactory.setWeapons(mWeaponActiveType, mWeaponInventoryType);
            // registerForReset(objectFactory) is last system called (see below)
//            BaseObject.sSystemRegistry.registerForReset(objectFactory);
            
            CameraSystem camera = new CameraSystem();
            BaseObject.sSystemRegistry.cameraSystem = camera;
            BaseObject.sSystemRegistry.registerForReset(camera);
    
            gameRoot.add(gameManager);
    
            /* Camera must come after the game manager so that the camera target moves
               before the camera centers. */
            gameRoot.add(camera);
            
            SpecialEffectSystem specialEffect = new SpecialEffectSystem();
            gameRoot.add(specialEffect);
            BaseObject.sSystemRegistry.specialEffectSystem = specialEffect;
//          // FIXME Study to re-enable reset() for Object destroy() Optimization
            BaseObject.sSystemRegistry.registerForReset(specialEffect);
//          // specialEffect reset() not required

            GameObjectCollisionSystem dynamicCollision = new GameObjectCollisionSystem();
            gameRoot.add(dynamicCollision);
            BaseObject.sSystemRegistry.gameObjectCollisionSystem = dynamicCollision;
//      	// FIXME Study to re-enable reset() for Object destroy() Optimization
            BaseObject.sSystemRegistry.registerForReset(dynamicCollision);
            
            RenderSystem renderer = new RenderSystem();
            BaseObject.sSystemRegistry.renderSystem = renderer;
            // renderer reset() not required
            
            RenderSystemHud rendererHud = new RenderSystemHud();
            BaseObject.sSystemRegistry.renderSystemHud = rendererHud;
            // rendererHud reset() not required
            
            BaseObject.sSystemRegistry.vectorPool = new VectorPool();
            // VectorPool reset() not required
          
            HudSystem hud = new HudSystem((int)mViewWidth, (int)mViewHeight);
            // FIXME DELETED 11/11/12
//            HudSystem hud = new HudSystem();
//            hud.setButtonDrawables(
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.move_button_base), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.move_button_top), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.fire_button_base), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.fire_button_top), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_empty), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_std), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_std_active), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_pulse), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_pulse_active), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                    		R.drawable.weapon_button_laser_emp), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_emp_active), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_grenade), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_grenade_active), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_rocket), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                            R.drawable.weapon_button_laser_rocket_active), 0, 0),
//            		new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//            				R.drawable.viewangle_bar_base), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                    		R.drawable.viewangle_bar_base_transparent), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                       		R.drawable.viewangle_bar_button), 0, 0),
//                    new DrawableBitmap(longTermTextureLibrary.allocateTexture(
//                       		R.drawable.viewangle_bar_button_transparent), 0, 0));
            // FIXME END 11/11/12
            
//            hud.setGameWidthDelta(gameWidth - 480);   // Add delta of gameWidth for wider screens
//            hud.setInverseViewScale((1 / viewScaleX), (1 / viewScaleY));
            hud.setMoveButtonBaseLocation(moveButtonRenderLeftX, moveButtonRenderBottomY);
            hud.setMoveButtonTopOffset(moveButtonTopOffsetX, moveButtonTopOffsetY);
            hud.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
            hud.setFireButtonBaseLocation(fireButtonRenderLeftX, fireButtonRenderBottomY);
            hud.setFireButtonTopOffset(fireButtonTopOffsetX, fireButtonTopOffsetY);
            hud.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
            hud.setWeapon1ButtonLocation(weapon1ButtonRenderLeftX, weapon1ButtonRenderBottomY);
            hud.setWeapon2ButtonLocation(weapon2ButtonRenderLeftX, weapon2ButtonRenderBottomY);
//            hud.setDroidAstronautSnapshotLocation(droidAstronautSnapshotRenderLeftX);
            hud.setViewangleBarBaseLocation(viewangleBarBaseRenderLeftX, viewangleBarBaseRenderBottomY);
            hud.setViewangleBarButtonLocationX(viewangleBarButtonRenderLeftX);
            hud.setViewangleBarButtonOffsetY(viewangleBarButtonRenderBottomY);
            
            if (mScreenDensity == 0) {
//            if (GameParameters.screenDensity == 0) {
            	hud.setViewangleBarButtonLocationY(VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT_MDPI);
            	hud.setDroidAstronautSnapshotLocation(DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_MDPI,DROID_SNAPSHOT_TOP_OFFSET_MDPI, ASTRONAUT_SNAPSHOT_TOP_OFFSET_MDPI);
            	hud.setNumberSpaceWidth(NUMBER_SPACE_WIDTH_MDPI);
            } else if (mScreenDensity == 1) {
//            } else if (GameParameters.screenDensity == 1) {
            	hud.setViewangleBarButtonLocationY(VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT_HDPI);
            	hud.setDroidAstronautSnapshotLocation(DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_HDPI, droidSnapshotRenderBottomY, astronautSnapshotRenderBottomY);
//            	hud.setDroidAstronautSnapshotLocation(DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_HDPI,DROID_SNAPSHOT_TOP_OFFSET_HDPI, ASTRONAUT_SNAPSHOT_TOP_OFFSET_HDPI);
            	hud.setNumberSpaceWidth(NUMBER_SPACE_WIDTH_HDPI);
            } else if (mScreenDensity == 2) {
//              } else if (GameParameters.screenDensity == 1) {
              	hud.setViewangleBarButtonLocationY(VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT_XHDPI);
              	hud.setDroidAstronautSnapshotLocation(DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_XHDPI, droidSnapshotRenderBottomY, astronautSnapshotRenderBottomY);
//              	hud.setDroidAstronautSnapshotLocation(DROID_ASTRONAUT_SNAPSHOT_LEFT_OFFSET_XHDPI,DROID_SNAPSHOT_TOP_OFFSET_XHDPI, ASTRONAUT_SNAPSHOT_TOP_OFFSET_XHDPI);
              	hud.setNumberSpaceWidth(NUMBER_SPACE_WIDTH_XHDPI);
              }
//            hud.setViewangleBarButtonLocationY(VIEWANGLE_BAR_BUTTON_LOCATION_DEFAULT);
////            hud.setViewangleBarButtonLocationDefault(viewangleBarButtonRenderLeftX, 
////            		(viewangleBarButtonRenderBottomY - VIEWANGLE_BAR_BUTTON_TOP_OFFSET_DEFAULT));
            
            // FIXME DELETE 11/11/12
//            hud.setFadeTexture(longTermTextureLibrary.allocateTexture(R.drawable.black));
//
//            Texture[] digitTextures = {
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_0),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_1),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_2),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_3),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_4),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_5),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_6),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_7),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_8),
//                    longTermTextureLibrary.allocateTexture(R.drawable.hud_9)
//            };
//            
//            DrawableBitmap[] digits = {
//                    new DrawableBitmap(digitTextures[0], 0, 0),
//                    new DrawableBitmap(digitTextures[1], 0, 0),
//                    new DrawableBitmap(digitTextures[2], 0, 0),
//                    new DrawableBitmap(digitTextures[3], 0, 0),
//                    new DrawableBitmap(digitTextures[4], 0, 0),
//                    new DrawableBitmap(digitTextures[5], 0, 0),
//                    new DrawableBitmap(digitTextures[6], 0, 0),
//                    new DrawableBitmap(digitTextures[7], 0, 0),
//                    new DrawableBitmap(digitTextures[8], 0, 0),
//                    new DrawableBitmap(digitTextures[9], 0, 0)
//            };
//            
//            hud.setDigitDrawables(digits);
//            hud.setCollectableDrawables(
//                    new DrawableBitmap(
//                            longTermTextureLibrary.allocateTexture(R.drawable.hud_droid_headshot_green), 0, 0),
//                    new DrawableBitmap(
//                            longTermTextureLibrary.allocateTexture(R.drawable.hud_droid_headshot_yellow), 0, 0),
//                    new DrawableBitmap(
//                            longTermTextureLibrary.allocateTexture(R.drawable.hud_droid_headshot_red), 0, 0), 
//                    new DrawableBitmap(
//                            longTermTextureLibrary.allocateTexture(R.drawable.hud_astronaut_headshot), 0, 0));
            // FIXME END 11/11/12
            
            BaseObject.sSystemRegistry.hudSystem = hud;
//          // FIXME Study to re-enable reset() for Object destroy() Optimization
            BaseObject.sSystemRegistry.registerForReset(hud);
            
//          if (ConstructActivity.VERSION < 0) {
//        		hud.setShowFPS(true);
//        	}
            gameRoot.add(hud);
            
//          // FIXME Study to re-enable reset() for Object destroy() Optimization
            // registerForReset(objectFactory) is last system called 
            BaseObject.sSystemRegistry.registerForReset(objectFactory);
    
            // TODO Re-enable - Vibration
//            BaseObject.sSystemRegistry.vibrationSystem = new VibrationSystem();
            
//            mRenderer.setButtons(mViewWidth, mViewHeight, mInverseViewScaleY,
//            		mMoveFireButtonTouchWidth, mMoveFireButtonTouchHeight, mMoveFireButtonTouchRadius,
//            		mMoveButtonTouchLeftX, mMoveButtonTouchBottomY, mMoveButtonCenterX, mMoveButtonCenterY,
//            		mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY, 
//            		mFireButtonTouchLeftX, mFireButtonTouchBottomY, mFireButtonCenterX, mFireButtonCenterY,
//            		mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY,
//            		mWeaponButtonTouchWidth, mWeaponButtonTouchHeight,
//            		mWeapon1ButtonTouchLeftX, mWeapon1ButtonTouchBottomY,
//            		mWeapon2ButtonTouchLeftX, mWeapon2ButtonTouchBottomY,
//            		mViewangleBarBaseTouchLeftX, mViewangleBarBaseTouchBottomY,
//            		mViewangleBarBaseTouchWidth, mViewangleBarBaseTouchHeight,
//            		mViewangleBarButtonTouchLeftX, mViewangleBarButtonTouchBottomY,
//            		mViewangleBarButtonTouchWidth, mViewangleBarButtonTouchHeight);
    
            mGameRoot = gameRoot;
            
//      		mGameRoot.setGamePause(false);
            
            mGameThread = new GameThread(mRenderer);
            mGameThread.setGameRoot(mGameRoot);
            
            mCurrentLevel = null;
            
            mBootstrapComplete = true;
            
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game bootstrap() END");	
            }
        }
    }
    
//    public synchronized void requestNewLevel() {
//		Log.i("GameFlow", "Game requestNewLevel()");
//    	
//    	// tell the Renderer to call us back when the
//    	// render thread is ready to manage some texture memory.
//    	mRenderer.requestCallback();
//    }
    
    public synchronized void restartLevel() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game restartLevel()");	
        }
		
        final LevelTree.Level level = mCurrentLevel;
        stop();
        
        // FIXME Test that manager.destroyAll(), reset(), etc is working correctly with all objects
        // Destroy all game objects and respawn them.  No need to destroy other systems.
        GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
        manager.destroyAll();
        manager.commitUpdates();
        
        // Reset systems that need it.
        BaseObject.sSystemRegistry.reset();
        
     // FIXME RE-ENABLE
//    	Context context = BaseObject.sSystemRegistry.gameObjectFactory.context;
//        mRenderer.setContext(context);
//        
//        BaseObject.sSystemRegistry.levelSystem.loadLevel(level,
//          		context.getResources().openRawResource(level.resource), mGameRoot);
//        
////        LevelSystem levelSystem = BaseObject.sSystemRegistry.levelSystem;
////        levelSystem.incrementAttemptsCount();
        
        mRenderer.spawnObjects(BaseObject.sSystemRegistry.levelSystem);
//        levelSystem.spawnObjects();
        
        mObjectsSpawned = true;
      
        mCurrentLevel = level;
        mPendingLevel = null;
        start();
    }
    
    protected synchronized void goToLevel(LevelTree.Level level) {
//    protected synchronized void goToLevel() {  
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game goToLevel()");	
        }
    	
    	Context context = BaseObject.sSystemRegistry.gameObjectFactory.context;

//        mRenderer.setContext(context);
//		mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
//		mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
//		mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
        
//        int levelId = R.raw.level01_build;
////        int levelId = R.raw.level_test;
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "Game goToLevel level = " + level.name);	
        }
        
        BaseObject.sSystemRegistry.levelSystem.loadLevel(level,
          		context.getResources().openRawResource(level.resource), mGameRoot);
//        BaseObject.sSystemRegistry.levelSystem.loadLevel(level,
//          		params.context.getResources().openRawResource(level.resource), mGameRoot);
//        BaseObject.sSystemRegistry.levelSystem.loadLevel(mGameRoot, levelId, context);

        // FIXME DELETED 10/17/12
//        mRenderer.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
////        mRenderer.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
////        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
////        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
        
//        mRenderer.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
////        mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
        
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        mRenderer.spawnObjects(BaseObject.sSystemRegistry.levelSystem);
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
        mObjectsSpawned = true;
        
        // FIXME DELETED 10/20/12
//        mGLDataLoaded = true;
        
        mCurrentLevel = level;
        mPendingLevel = null;
        
        TimeSystem time = BaseObject.sSystemRegistry.timeSystem;
        time.reset();
        
        HudSystem hud = BaseObject.sSystemRegistry.hudSystem;
//        if (hud != null) {
//            hud.startFade(true, 1.0f);
//        }
        
        // TODO Re-enable - CustomToastSystem
//        CustomToastSystem toast = BaseObject.sSystemRegistry.customToastSystem;
//        if (toast != null) {
//        	if (level.inThePast) {
//        		toast.toast(context.getString(R.string.memory_playback_start), Toast.LENGTH_LONG);
//        	} else {
//        		if (mLastLevel != null && mLastLevel.inThePast) {
//            		toast.toast(context.getString(R.string.memory_playback_complete), Toast.LENGTH_LONG);
//        		}
//        	}
//        }
        
        mLastLevel = level;
        
        start();
//        // 3 Second Runnable to allow sufficient display time of Level Loading Splashscreen
//        Handler handlerTimer = new Handler();        
//        handlerTimer.postDelayed(new Runnable() {
//            public void run() {
//            	start();            
//            }
//        }, 3000);
    }

//    protected synchronized void stopLevel() {
//		Log.i("GameFlow", "Game stopLevel()");
//  	
//    	stop();
//    	
//    	// FIXME Move manager.destroyAll() and .commitUpdates() to stop()? Add GameObjectFactory reset()
//    	// to set all local GameObject and DrawableDroid = null
//      	GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
//      	manager.destroyAll();
//      	manager.commitUpdates();
//      
//      	// XXX: it's not strictly necessary to clear the static data here, but if I don't do it
//      	// then two things happen: first, the static data will refer to junk Texture objects, and
//      	// second, memory that may not be needed for the next level will hang around.  One solution
//      	// would be to break up the texture library into static and non-static things, and
//      	// then selectively clear static game components based on their usefulness next level,
//      	// but this is way simpler.
//      	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
////      factory.clearStaticData();
//      	factory.sanityCheckPools();
//      
////    	Reset the level
//      	BaseObject.sSystemRegistry.levelSystem.reset();
//      
//      	// Reset systems that need it.
//      	BaseObject.sSystemRegistry.reset();
//      
////      // Dump the short-term texture objects only.
////      mRenderer.flushTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
//////      mSurfaceView.flushTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
////      BaseObject.sSystemRegistry.shortTermTextureLibrary.removeAll(); 
////      
////      mRenderer.flushBuffers(BaseObject.sSystemRegistry.bufferLibrary);
//////      mSurfaceView.flushBuffers(BaseObject.sSystemRegistry.bufferLibrary);
////      BaseObject.sSystemRegistry.bufferLibrary.removeAll();
//    }
    
    /** Starts the game running. */
    public void start() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game start()");	
        }
    	
    	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
    	
        if (!mRunning) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game start() mRunning = FALSE");	
            }
    		
    		// FIXME Optimize this to mGame.sleep(3000 - timeSoFar), Handler on UI Thread, Runnable, or other
    		// Timer to allow sufficient display of Level Loading Splashscreen
    		mLevelStartTime = System.currentTimeMillis();
    		if (mLevelRow == 0) {
        		while(System.currentTimeMillis() < (mLevelStartTime + 5000)) {
        			// wait
        		}
    		} else {
        		while(System.currentTimeMillis() < (mLevelStartTime + 2000)) {
        			// wait
        		}
    		}
        	
            assert mGame == null;
            // Now's a good time to run the GC.
            Runtime r = Runtime.getRuntime();
            r.gc();
            mGame = new Thread(mGameThread);
            mGame.setName("Game");
            mGame.start();
            mRunning = true;
            AllocationGuard.sGuardActive = false;
            
            // hideLoadingMessage()
        	LevelSystem level = ObjectRegistry.sSystemRegistry.levelSystem;
        	if (level != null) {
        		level.sendGameEvent(GameFlowEvent.EVENT_START_INTRO, 0, false);
        	}
            
//            GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//        	Context context = factory.context;
////        	Context context = BaseObject.sSystemRegistry.gameObjectFactory.context;
            
        	if (!factory.musicPlaying){
//        	if (!mMusicPlaying){
        		switch(mLevelRow) {
//        		switch(mMusicLevel) {        		
        		case 0:
        			factory.startBackgroundMusic(R.raw.sound_voice_intruder_alert_background);
        			break;
        			
        		case 1:
        			factory.startBackgroundMusic(R.raw.sound_music_level01);
//                	mBackgroundMusic = MediaPlayer.create(context, R.raw.sound_level_01);
//                  	mBackgroundMusic.setVolume(1.0f, 1.0f);
//                  	mBackgroundMusic.setLooping(true);
//                  	mBackgroundMusic.start();
//                	mMusicPlaying = true;
        			break;
        			
        		case 2:
        			factory.startBackgroundMusic(R.raw.sound_music_level02);
        			break;
        			
        		case 3:
        			factory.startBackgroundMusic(R.raw.sound_music_level03);
        			break;
        			
        		case 4:
        			factory.startBackgroundMusic(R.raw.sound_music_level04);
        			break;
        			
        		case 5:
        			factory.startBackgroundMusic(R.raw.sound_music_level05);
        			break;
        			
        		case 6:
        			factory.startBackgroundMusic(R.raw.sound_music_level06);
        			break;
        			
        		case 7:
        			factory.startBackgroundMusic(R.raw.sound_music_level07);
        			break;
        			
        		case 8:
        			factory.startBackgroundMusic(R.raw.sound_music_level08);
        			break;
        			
        		case 9:
        			factory.startBackgroundMusic(R.raw.sound_music_level09);
        			break;
        			
        		default:
        			break;
        		}
        		
//            	mBackgroundMusic = MediaPlayer.create(context, R.raw.sound_level_01);
////            	mBackgroundMusic = MediaPlayer.create(context, R.raw.sound_background_02);
////            	mBackgroundMusic = MediaPlayer.create(context, R.raw.sound_background_01);
//              	mBackgroundMusic.setVolume(1.0f, 1.0f);
//              	mBackgroundMusic.setLooping(true);
////              mBackgroundMusic.setVolume(1000, 1000);
//              	mBackgroundMusic.start();
////            	mBackgroundMusic = sound.load(R.raw.sound_background_01);
////            	sound.play(mBackgroundMusic, true, SoundSystem.PRIORITY_MUSIC, 0.5f, 1.0f);
//            	mMusicPlaying = true;
        	}
//        	SoundSystem sound = sSystemRegistry.soundSystem;
//            mBackgroundMusic = MediaPlayer.create(mContextParameters.context, R.raw.sound_background_01);
//            mBackgroundMusic.setVolume(10, 10);
////            mBackgroundMusic.setVolume(1000, 1000);
//            mBackgroundMusic.start();
            /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        } else {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game start() mRunning = TRUE");	
            }
    		
            mGameThread.resumeGame();
            
            if (!factory.musicPlaying) {
            	factory.startBackgroundMusic();
//            	mBackgroundMusic.start();
//            	mMusicPlaying = true;
            }
//            if (!mMusicPlaying) {
//            	mBackgroundMusic.start();
//            	mMusicPlaying = true;
//            }
        }
    }
    
    public void stop() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game stop()");	
        }
		
        if (mRunning) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game stop() mRunning = TRUE");	
            }
    		
        	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
        	
            if (factory.musicPlaying) {
            	factory.stopBackgroundMusic();
            }
            
            // FIXME RE-ENABLE? Seems to cause lockup. May need to be after mGame = null.
//            if (mSoundSystemActive) {
//            	SoundSystem soundSystem = BaseObject.sSystemRegistry.soundSystem;
//            	soundSystem.reset();
//            	mSoundSystemActive = false;
//            }
            
            if (mGameThread.getPaused()) {
                mGameThread.resumeGame();
            }
            mGameThread.stopGame();
            
            try {
                mGame.join();
            } catch (InterruptedException e) {
                mGame.interrupt();
            }
            mGame = null;
            mRunning = false;
//            mPauseStopped = false;
            
            mCurrentLevel = null;
            AllocationGuard.sGuardActive = false;
            
//            SpecialEffectSystem specialEffect = BaseObject.sSystemRegistry.specialEffectSystem;
//            specialEffect.clear();
            
          	GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
          	manager.destroyAll();
          	manager.commitUpdates();
          
          	// XXX: it's not strictly necessary to clear the static data here, but if I don't do it
          	// then two things happen: first, the static data will refer to junk Texture objects, and
          	// second, memory that may not be needed for the next level will hang around.  One solution
          	// would be to break up the texture library into static and non-static things, and
          	// then selectively clear static game components based on their usefulness next level,
          	// but this is way simpler.
//          	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//          factory.clearStaticData();
            if (GameParameters.debug) {
              	factory.sanityCheckPools();	
            }
          	
          	mRenderer.setGL(null);
          
          	// FIXME 12/14/12 Still need to completely clean all Objects for stop()
//        	Reset the level
          	BaseObject.sSystemRegistry.levelSystem.reset();
          
          	// Reset systems that need it.
          	BaseObject.sSystemRegistry.reset();
        } else {
            if (GameParameters.debug) {
            	Log.i("GameFlow", "Game stop() mRunning = FALSE");	
            }
        }
    }
    
	public boolean isRunning() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game isRunning() mRunning = " + mRunning);	
        }
		
		return (mRunning && mGameThread != null);
	}
	
	public boolean isPaused() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game isPaused() mRunning = " + mRunning + "; " + "mGameThread.getPaused() = " + mGameThread.getPaused());	
        }
		
		return (mRunning && mGameThread != null && mGameThread.getPaused());
	}
	
	public boolean isGamePause() {
		return GameParameters.gamePause;
//		return BaseObject.sSystemRegistry.getGamePause();
//		return mGameRoot.getGamePause();
	}
	
//	public boolean isPauseStopped() {
//		return (mRunning && mPauseStopped && mGameThread != null && mGameThread.getPaused());
//	}
    
    public void onPause() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game onPause()");	
        }
  	
    	if (mRunning) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game onPause() mRunning = TRUE");	
            }
    		
    		GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
    		
            if (factory.musicPlaying) {
            	factory.pauseBackgroundMusic();
            }
//            if (mMusicPlaying) {
//            	mBackgroundMusic.pause();
//            	mMusicPlaying = false;
//            }
            
            // FIXME 12/15/12 345pm RE-ENABLE direct call to mGameThread.pauseGame()
            // FIXME 12/15/12 1110am TEMP DISABLE. RE-ENABLE.
            // FIXME 12/15/12 MODIFIED
            // FIXME 12/13/12 TEMP DISABLE
//            mGameThread.requestGameThreadPause();
    		mGameThread.pauseGame();
            // FIXME END 12/13/12 TEMP DISABLE
            // FIXME END 12/15/12 MODIFIED
            // FIXME END 12/15/12 1110am TEMP DISABLE. RE-ENABLE.
            // FIXME END 12/15/12 345pm RE-ENABLE direct call to mGameThread.pauseGame()
            
//            BaseObject.sSystemRegistry.setGamePause(true);
////            BaseObject.sSystemRegistry.gamePause = true;
    		
    	} else {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game onPause() mRunning = FALSE");	
            }
    	}
    }
    
//    public void onPauseStopped() {
//		Log.i("GameFlow", "Game onPauseStopped()");
//  	
//    	if (mRunning) {
//    		Log.i("GameFlow", "Game onPauseStopped() mRunning");
//    		
//    		GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//    		
//            if (factory.musicPlaying) {
//            	factory.pauseBackgroundMusic();
//            }
////            if (mMusicPlaying) {
////            	mBackgroundMusic.pause();
////            	mMusicPlaying = false;
////            }
//            
//    		mGameThread.pauseGame();
//    		
//    		mPauseStopped = true;
//    	} else {
//    		Log.i("GameFlow", "Game onPauseStopped() !mRunning");
//    	}
//    }
    
//    public void setPauseStopped(boolean pauseState) {
//    	mPauseStopped = pauseState;
//    }
  
	public void onResume(Context context, boolean manualResume) {
//	public void onResume(Context context, boolean force) {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game onResume()");	
        }
  	
		// FIXME Delete force, since now always set to true
		if (manualResume && mRunning) {
//		if (force && mRunning) {
	        if (GameParameters.debug) {
				Log.i("GameFlow", "Game onResume() mRunning = TRUE; manualResume = TRUE");	
	        }
			
			// FIXME 12/15/12 345pm RE-ENABLE direct call to mGameThread.resumeGame()
            // FIXME 12/15/12 1110am. TEMP DISABLE. RE-ENABLE.
			// FIXME 12/15/12 RE-ENABLED
			// FIXME 12/15/12 TEST ONLY. RE-ENABLE.
//			// FIXME 12/15/12 ADDED
			mGameThread.resumeGame();
//			// FIXME END 12/15/12 ADDED
			// FIXME END 12/15/12 TEST ONLY. RE-ENABLE.
			// FIXME END 12/15/12 RE-ENABLED
			
			// FIXME 12/15/12 440pm MODIFIED
//			GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//        	if (!factory.musicPlaying) {
//        		factory.startBackgroundMusic();
//        	}
//			// FIXME 12/13/12 TEMP DISABLE. RE-ENABLE.
//            if (BaseObject.sSystemRegistry.gamePause) {
//            	Log.i("GameFlow", "Game onResume() gamePause = TRUE; therefore call mRenderer.requestCallback()");
//            	
//        		// FIXME 12/15/12 TEMP DISABLE. RE-ENABLE.
//            	mRenderer.requestCallback();
//        		// FIXME END 12/15/12 TEMP DISABLE. RE-ENABLE.
//            	
//            } else {
//            	Log.i("GameFlow", "Game onResume() gamePause = FALSE; therefore call factory.startBackgroundMusic()");
//            	
//    			GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//            	if (!factory.musicPlaying) {
//            		factory.startBackgroundMusic();
//            	}
//            }
//            // FIXME END 12/15/12 1110am. TEMP DISABLE. RE-ENABLE.
//			// FIXME END 12/15/12 345pm RE-ENABLE direct call to mGameThread.resumeGame()
//            
			if(GameParameters.gamePause) {
//			if(mGameRoot.getGamePause()) {
//			if(BaseObject.sSystemRegistry.getGamePause()) {
		        if (GameParameters.debug) {
					Log.i("GameFlow", "Game onResume() gamePause = TRUE");	
		        }
				
				// FIXME 12/16/12 900am MODIFIED
				// FIXME 12/15/12 550pm DELETED
				// FIXME 12/15/12 540pm MODIFIED
				// FIXME 12/15/12 530pm RE-ADDED
				// FIXME 12/15/12 520pm DELETED
	        	// FIXME 12/15/12 430pm ADDED
				setGameRestart(true);
//	        	mGameThread.setGameThreadPause(true);
////	        	mGameThread.requestGameThreadPause();
	        	// FIXME END 12/15/12 430pm ADDED
				// FIXME END 12/15/12 520pm DELETED
				// FIXME END 12/15/12 530pm RE-ADDED
				// FIXME END 12/15/12 540pm MODIFIED
				// FIXME END 12/15/12 555pm DELETED
				// FIXME END 12/16/12 900am MODIFIED
			} else {
		        if (GameParameters.debug) {
					Log.i("GameFlow", "Game onResume() gamePause = FALSE");	
		        }
				
				GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
	        	if (!factory.musicPlaying) {
	        		factory.startBackgroundMusic();
	        	}	
			}
			
			BaseObject.sSystemRegistry.gameObjectFactory.context = ConstructActivity.getAppContext();
			// FIXME END 12/15/12 440pm MODIFIED
            
//			mGameThread.resumeGame();
//			
//			if(!BaseObject.sSystemRegistry.gamePause) {
////			if(!mPauseStopped) {
//				GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//				
//	            if (!factory.musicPlaying) {
//	            	factory.startBackgroundMusic();
//	            }
//			}
////			GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
////			
////            if (!factory.musicPlaying) {
////            	factory.startBackgroundMusic();
////            }
//////            if (!mMusicPlaying) {
//////            	mBackgroundMusic.start();
//////            	mMusicPlaying = true;
//////            }
			// FIXME 12/13/12 TEMP DISABLE. RE-ENABLE.
            
//    		mPauseStopped = false;
    		
//  		} else {
//			Log.i("GameFlow", "Game onResume() mRunning = FALSE and/or manualResume = FALSE");
//  			
//  			// FIXME GameRenderer mContext NOT USED. Should not use GameObjectFactory mContext. Therefore, DELETE.
////	        mRenderer.setContext(ConstructActivity.getAppContext());
////	        mRenderer.setContext(context);
//	        // Don't explicitly resume the game here.  We'll do that in
//	        // the SurfaceReady() callback, which will prevent the game
//	        // starting before the render thread is ready to go.
//	        BaseObject.sSystemRegistry.gameObjectFactory.context = ConstructActivity.getAppContext();
////	        BaseObject.sSystemRegistry.gameObjectFactory.context = context;
//////	        BaseObject.sSystemRegistry.contextParameters.context = context;
  		}
	}
	
//	public void onResumeFromBackground() {
//		Log.i("GameFlow", "Game onResumeFromBackground()");
//  	
//		if (isPaused()) {
//			Log.i("GameFlow", "Game onResumeFromBackground() mGame.isPaused() = TRUE");
//			
//			mGameThread.resumeGame();
//			
//			onPause();
//  		}
//	}
  
  	public void onSurfaceCreated(GL10 gl) {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game onSurfaceCreated()");	
        }
		
		// XXX: this is dumb.  SurfaceView doesn't need to control everything here.
		// GL should just be passed to this function and then set up directly.
  		
//		Log.i("HudTest", "Game onSurfaceCreated() mGLDataLoaded = " + mGLDataLoaded);
//		Log.i("HudTest", "Game onSurfaceCreated() mGLDataLoaded, mGameThread.getPaused(), mRunning = " +
//				mGLDataLoaded + ", " + mGameThread.getPaused() + ", " + mRunning);
		
  	    if (!mGLDataLoaded) {
//  	    if (!mGLDataLoaded && mGameThread.getPaused() && mRunning && mPendingLevel == null) {
//  		if (!mGLDataLoaded && mGameThread.getPaused() && mRunning) {
  	    	
  	        if (GameParameters.debug) {
  	  	    	Log.i("GameFlow", "Game onSurfaceCreated() mGLDataLoaded = FALSE");	
  	        }
  	    	
  	    	// FIXME 12/2/12 MODIFIED
  	        // FIXME ADDED 11/11/12
  	        HudSystem hudSystem = BaseObject.sSystemRegistry.hudSystem;
//  	        Context context = BaseObject.sSystemRegistry.gameObjectFactory.context;
  	        
  	        hudSystem.loadGLTexture(gl, ConstructActivity.getAppContext());
//  	        hudSystem.loadGLTexture(gl, context);
  	    	// FIXME END 11/11/12
  	        
  	        // FIXME 12/4/12 ADDED
  	        if (mObjectsSpawned) {
  	          if (GameParameters.debug) {
    	        	Log.i("GameFlow", "Game onSurfaceCreated() mObjectsSpawned = TRUE");  
  	          }
  	        	
  	  	        GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
  	  	        GL11 gl11 = (GL11)gl;
  	  	        factory.reloadDrawables(gl11);
//  	  	        GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
//  	  	        GL11 gl11 = (GL11)gl;
//  	  	        manager.reloadDrawableObjects(gl11);	
  	        }
  	        // FIXME END 12/4/12 ADDED
  	        
  	        // FIXME Find correct solution to reload, then re-enable
//  	        if(mObjectsSpawned) {
//  	  	        GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
//  	  	        manager.reloadDrawableObjects((GL11)gl);
//  	        }
  	        // FIXME END 12/2/12 MODIFIED
    	
  	        // FIXME DELETED 11/11/12. Currently only longTermTextureLibrary used for HUD buttons. Optimize code.
//        	mRenderer.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
//////        	mRenderer.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
//////        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
//////        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
////    	
//////        	mRenderer.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
////////        mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
////        	
//////        	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//////        	GL11 gl11 = (GL11)gl;
//////        	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//////        	factory.setGL(gl11);
//////        	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        	// FIXME END 11/11/12
        	
        	mGLDataLoaded = true;
        	
  		} else {
	          if (GameParameters.debug) {
	    			Log.i("GameFlow", "Game onSurfaceCreated() mGLDataLoaded = TRUE; therefore no GL Object reload");  
	          }
  		}
  	}
  
  	public void onSurfaceReady() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game onSurfaceReady()");	
        }
    
//  		// Temporary code only
//  		if (!mLevelLoaded) {
//  	  		goToLevel();
//  	  		mLevelLoaded = true;
//  		}

    	if (mPendingLevel != null && mPendingLevel != mCurrentLevel) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game onSurfaceReady() mPendingLevel != null && mPendingLevel != mCurrentLevel; therefore goToLevel(mPendingLevel) after checking for stop()");	
            }
    		
	        if (mRunning) {
	            if (GameParameters.debug) {
		        	Log.i("GameFlow", "Game onSurfaceReady() mRunning == TRUE; therefore stop()");	
	            }
	        			
	        	stop();
//	            stopLevel();
	        }
	        
	        goToLevel(mPendingLevel);
	        
	    // FIXME 12/13/12 RE-ENABLED
	    // FIXME 12/12/12 RE-ENABLED then DISABLE
	    // FIXME 12/8/12 DELETED
    	} else if (mRunning && mGameRestart) {
//    	} else if (mRunning && !BaseObject.sSystemRegistry.gamePause) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "Game onSurfaceReady() mRunning = TRUE and mGameStart = TRUE, therefore call mRenderer.requestCallback()");	
            }
    		
    		// FIXME 12/16/12 900am MODIFIED
    		// FIXME 12/15/12 TEMP DISABLE. RE-ENABLE.
    		// FIXME 12/15/12 RE-ENABLE
    		// FIXME 12/15/12 TEMP DISABLE. RE-ENABLE.
    		mRenderer.requestCallback();
//    		mGameThread.pauseGame();
    		// FIXME END 12/15/12 TEMP DISABLE. RE-ENABLE.
    		// FIXME END 12/15/12 RE-ENABLE
    		// FIXME END 12/15/12 TEMP DISABLE. RE-ENABLE.
    		// FIXME END 12/16/12 900am MODIFIED
    		
    		setGameRestart(false);
//    		mGameRestart = false;
    		
//    	} else if (BaseObject.sSystemRegistry.gamePause == true) {      
//    		Log.i("GameFlow", "Game onPause() gamePause = TRUE, therefore call GameThread.gamePause() and set gamePause = FALSE");
//    		
//    		mGameThread.pauseGame();
//    		
//            BaseObject.sSystemRegistry.gamePause = false;
//    	} else if (mRunning && mGameThread.getPaused()) {
//    		Log.i("GameFlow", "Game onSurfaceReady() mRunning && mGameThread.getPaused(); therefore mGameThread.resumeGame()");
//    		
//  			mGameThread.resumeGame();
////    	} else if (mRunning && !mGameThread.getPaused() && mPauseStopped) {
////    		Log.i("GameFlow", "Game onSurfaceReady() mRunning && !mGameThread.getPaused() && mPauseStopped; therefore Re-Pause Game");
////    		
////  			onPause();
  		// FIXME END 12/8/12 DELETED
		// FIXME END 12/12/12 RE-ENABLED then DISABLE
  		// FIXME END 12/13/12 RE-ENABLED
    	} 
    }
  
  	public void onSurfaceLost() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "Game onSurfaceLost()");	
        }
      
		// FIXME DELETED 11/11/12
//  		// TODO Currently only longTermTextureLibrary used for HUD buttons. Optimize code.
////  		BaseObject.sSystemRegistry.shortTermTextureLibrary.invalidateAll();
//      	BaseObject.sSystemRegistry.longTermTextureLibrary.invalidateAll();
      
//      BaseObject.sSystemRegistry.bufferLibrary.invalidateHardwareBuffers();

      	mGLDataLoaded = false;
      	
      	if (mRunning) {
//      	if (isPaused()) {
            if (GameParameters.debug) {
          		Log.i("GameFlow", "Game onSurfaceLost() mRunning() = TRUE");	
            }
      		
      		GameParameters.gamePause = true;
//      		mGameRoot.setGamePause(true);
//          	BaseObject.sSystemRegistry.setGamePause(true);
//      	mPauseStopped = true;	
          	
            if (GameParameters.debug) {
              	Log.i("GameFlow", "Game onSurfaceLost() BaseObject.gamePause = " + GameParameters.gamePause);	
            }
          	
//          	mRenderer.requestCallback();
      	}
  	}
  	
  	// FIXME 12/16/12 900am ADDED
  	public void onRendererRestart() {
        if (GameParameters.debug) {
      		Log.i("GameFlow", "Game onRendererRestart()");	
        }
  		
//  		mRenderer.waitDrawingComplete();
  				
  		mGameThread.pauseGame();
  		
  		GameParameters.gamePause = false;
//  		mGameRoot.setGamePause(false);
//  		BaseObject.sSystemRegistry.setGamePause(false);
  	}
  	// FIXME END 12/16/12 900am ADDED
    
    public boolean onTouchEvent(MotionEvent event) {
//        public void onTouchEvent(int action, int mask) {
////  	public void onTouchEvent(MotionEvent event) {
////  	public void touchInput(float[] touchX, float[] touchY, int pointerCount) {
  		
//    	mGameThread.touchEvent(action, mask);
    	
    	// FIXME 12/19/12 1240am TEMP DISABLE. RE-ENABLE.
  		// ORIGINAL ONTOUCHEVENT() CODE
    	// FIXME TEMP. DELETE.
    	GameParameters.gameCounter++;
    	
    	HudSystem hudSystem = BaseObject.sSystemRegistry.hudSystem;
    	
//    	// FIXME TEMP ONLY. DELETE.
//		float tempDownX = event.getX();
//		float tempDownY = mViewHeight - event.getY();
//		Log.i("TouchCounter", "Game touchInput() downX,downY = " +
//				tempDownX + ", " + tempDownY);
//		// END TEMP ONLY. DELETE.
    	
        if (mRunning) {
//        if (mRunning && !hudSystem.levelIntro) {  
//        	int pointerCount = event.getPointerCount();
//        	
//        	Log.i("TouchEvent", "Game onTouchEvent() pointerCount = " + pointerCount);
    		
    		final int action = event.getAction();
    		
    		InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
    		
    		switch (action & MotionEvent.ACTION_MASK) {
    		
    		case MotionEvent.ACTION_UP:
    			GameParameters.gameCounterTouchUp++;
    			
    			inputSystem.reset();
    			
    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
    			
    			mMovePointerId = -1;
    			mFirePointerId = -1;
    			mWeapon1PointerId = -1;
    			mWeapon2PointerId = -1;
    			mViewangleBarPointerId = -1;
    			
    			break;
    		
    		case MotionEvent.ACTION_DOWN:
    			GameParameters.gameCounterTouchDownMove++;
    			
        		float downX = event.getX();
        		float downY = mViewHeight - event.getY();
        		
    	        if (getTouchedWithinRegionMove(downX, downY)) {        					        		
		        	touchMoveDown(inputSystem, downX, downY);
		        	
	    			hudSystem.setMoveButtonTopLocationTouch(downX, downY);
		        		
		        	mMovePointerId = event.getPointerId(0);
    	        } else if (getTouchedWithinRegionFire(downX, downY)) {        					        		
        			touchFireDown(inputSystem, downX, downY);
        			
	    			hudSystem.setFireButtonTopLocationTouch(downX, downY);
		        		
		        	mFirePointerId = event.getPointerId(0);
		        } else if (getTouchedWithinRegionWeapon1(downX, downY)) {        					        		
		        	inputSystem.touchWeapon1Press = true;
		        		
		        	mWeapon1PointerId = event.getPointerId(0);
		        } else if (getTouchedWithinRegionWeapon2(downX, downY)) {		        		
		        	inputSystem.touchWeapon2Press = true;
		        		
		        	mWeapon2PointerId = event.getPointerId(0);
		        } else if (getTouchedWithinRegionViewangleBar(downX, downY)) {
		        	touchViewangleBarDown(hudSystem, downY);
		        		
		        	mViewangleBarPointerId = event.getPointerId(0);
		        }
        		
        		break;
    		
    		case MotionEvent.ACTION_MOVE:
    			GameParameters.gameCounterTouchDownMove++;
    			
    			if (mMovePointerId > -1 && mFirePointerId > -1) {
        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
        			
            		float touchMoveX = event.getX(pointerIndexMove);
            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
            		float touchFireX = event.getX(pointerIndexFire);
            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
            		
//            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
//            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
//            		
//        	        if (touchMove && touchFire) {    		        		
            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
            			
    	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
    	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//            			
//            		} else if (touchMove) {
//            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//            			inputSystem.touchFirePress = false;
//            			
//    	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//    	    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//            			
//            		} else if (touchFire) {
//            			touchFireDown(inputSystem, touchFireX, touchFireY);
//            			inputSystem.touchMovePress = false;
//            			
//            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//    	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//            		}
            	} else if (mMovePointerId > -1) {
        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
        			
            		float touchMoveX = event.getX(pointerIndexMove);
            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
            		
//        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		
            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
            			
    	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//            		} else {
//            			inputSystem.touchMovePress = false;
//            			
//            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//            		}
            	} else if (mFirePointerId > -1) {
        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
        			
              		float touchFireX = event.getX(pointerIndexFire);
            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
            		
//        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {    		        		
            			touchFireDown(inputSystem, touchFireX, touchFireY);
            			
    	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//    		        } else {
//    		        	inputSystem.touchFirePress = false;
//    		        	
//    		        	hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//    		        }
            	}
    			
//    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//    			if (mWeapon1PointerId > -1) {
//        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
//        			
//              		float x = event.getX(pointerIndexWeapon1);
//            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
//            		
//        	        if (getTouchedWithinRegionWeapon1(x, y)) {    		        		
//            			inputSystem.touchWeapon1Press = true;
//    		        }
//            	} else if (mWeapon2PointerId > -1) {
//        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
//        			
//              		float x = event.getX(pointerIndexWeapon2);
//            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
//            		
//        	        if (getTouchedWithinRegionWeapon2(x, y)) {    		        		
//            			inputSystem.touchWeapon2Press = true;
//    		        }
//            	}
    			
    			if (mViewangleBarPointerId > -1) {
        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
        			
              		float x = event.getX(pointerIndexViewangleBar);
            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
            		
    				if (getTouchedWithinRegionViewangleBar(x, y)) {
    		        	touchViewangleBarDown(hudSystem, y);
    				}
    			}
    			
            	break;
    		
    		case MotionEvent.ACTION_POINTER_UP:
    			GameParameters.gameCounterTouchUp++;
    			
    			final int pointerIndexUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

            	final int pointerUpId = event.getPointerId(pointerIndexUp);
            	
            	if (pointerUpId == mMovePointerId) {            		
            		// TODO Since will set to false in <GameObject>Component after each update(), is this setting necessary?
            		inputSystem.touchMovePress = false;
            		
	    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
            		
        			mMovePointerId = -1;
            	} else if (pointerUpId == mFirePointerId) {            		
            		inputSystem.touchFirePress = false;
            		
	    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	    			
        			mFirePointerId = -1;
            	} else if (pointerUpId == mWeapon1PointerId) {            		
            		inputSystem.touchWeapon1Press = false;

        			mWeapon1PointerId = -1;
            	} else if (pointerUpId == mWeapon2PointerId) {            		
            		inputSystem.touchWeapon2Press = false;

        			mWeapon2PointerId = -1;
            	} else if (pointerUpId == mViewangleBarPointerId) {
            		mViewangleBarPointerId = -1;
    			}
            	
            	if (mMovePointerId > -1 && mFirePointerId > -1) {
        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
        			
               		float touchMoveX = event.getX(pointerIndexDownMove);
            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
               		float touchFireX = event.getX(pointerIndexDownFire);
            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
            		
            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
            		
        	        if (touchMove && touchFire) {    		        		
            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
            			
            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
            			
            		} else if (touchMove) {
            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
            			
            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
            			
            		} else if (touchFire) {
            			touchFireDown(inputSystem, touchFireX, touchFireY);
            			
            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
            			
            		} 
            	} else if (mMovePointerId > -1) {
        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
        			
               		float touchMoveX = event.getX(pointerIndexDownMove);
            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));

        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		                  			
            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
            			
            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
        	        } else {
        	        	hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
        	        }
    			} else if (mFirePointerId > -1) {
        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
        			
            		float touchFireX = event.getX(pointerIndexFire);
            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
            		
        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {    		        		
            			touchFireDown(inputSystem, touchFireX, touchFireY);
            			
            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
            		} else {
            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
            		}
    			}
    			
    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
    			if (mWeapon1PointerId > -1) {
        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
        			
              		float x = event.getX(pointerIndexWeapon1);
            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
            		
        	        if (getTouchedWithinRegionWeapon1(x, y)) {    		        		
            			inputSystem.touchWeapon1Press = true;
    		        }
            	} else if (mWeapon2PointerId > -1) {
        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
        			
              		float x = event.getX(pointerIndexWeapon2);
            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
            		
        	        if (getTouchedWithinRegionWeapon2(x, y)) {    		        		
            			inputSystem.touchWeapon2Press = true;
    		        }
            	}
    			
    			if (mViewangleBarPointerId > -1) {
        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
        			
              		float x = event.getX(pointerIndexViewangleBar);
            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
            		
    				if (getTouchedWithinRegionViewangleBar(x, y)) {
    		        	touchViewangleBarDown(hudSystem, y);
    				}
    			}
    			
            	break;
    		
    		case MotionEvent.ACTION_POINTER_DOWN:
    			GameParameters.gameCounterTouchDownMove++;
    			
    			final int pointerIndexDown = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            	final int pointerDownId = event.getPointerId(pointerIndexDown);
    			
          		float pointerDownX = event.getX(pointerIndexDown);
        		float pointerDownY = mViewHeight - (event.getY(pointerIndexDown));
            	
    	        if (mMovePointerId == -1 && getTouchedWithinRegionMove(pointerDownX, pointerDownY)) {
    	        	
    	        	mMovePointerId = pointerDownId;
    	        	
	    			if (mFirePointerId > -1) {
	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
	        			
	            		float touchFireX = event.getX(pointerIndexFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
	            		
	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
	            			touchMoveFireDown(inputSystem, pointerDownX, pointerDownY, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(pointerDownX, pointerDownY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} else {	            			
	            			touchMoveDown(inputSystem, pointerDownX, pointerDownY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(pointerDownX, pointerDownY);
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            		}
	    			} else {
	        			touchMoveDown(inputSystem, pointerDownX, pointerDownY);
	        			
	        			hudSystem.setMoveButtonTopLocationTouch(pointerDownX, pointerDownY);
	    			}
	    			
	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
	    			if (mWeapon1PointerId > -1) {
	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon1);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
	            		
	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
	            			inputSystem.touchWeapon1Press = true;
	    		        }
	            	} else if (mWeapon2PointerId > -1) {
	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon2);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
	            		
	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
	            			inputSystem.touchWeapon2Press = true;
	    		        }
	            	}
	    			
	    			if (mViewangleBarPointerId > -1) {
	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
	        			
	              		float x = event.getX(pointerIndexViewangleBar);
	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
	            		
	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
	    		        	touchViewangleBarDown(hudSystem, y);
	    				}
	    			}
    	        } else if (mFirePointerId == -1 && getTouchedWithinRegionFire(pointerDownX, pointerDownY)) {
    	        	
		        	mFirePointerId = pointerDownId;
		        	
	    			if (mMovePointerId > -1) {
	        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
	        			
	            		float touchMoveX = event.getX(pointerIndexMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
	            		
	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {
	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, pointerDownX, pointerDownY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationTouch(pointerDownX, pointerDownY);
	            			
	            		} else {	            			
	            			touchFireDown(inputSystem, pointerDownX, pointerDownY);
	            			
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            			hudSystem.setFireButtonTopLocationTouch(pointerDownX, pointerDownY);
	            		}
	    			} else {
	        			touchFireDown(inputSystem, pointerDownX, pointerDownY);
	        			
	        			hudSystem.setFireButtonTopLocationTouch(pointerDownX, pointerDownY);
	    			}
	    			
	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
	    			if (mWeapon1PointerId > -1) {
	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon1);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
	            		
	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
	            			inputSystem.touchWeapon1Press = true;
	    		        }
	            	} else if (mWeapon2PointerId > -1) {
	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon2);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
	            		
	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
	            			inputSystem.touchWeapon2Press = true;
	    		        }
	            	}
	    			
	    			if (mViewangleBarPointerId > -1) {
	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
	        			
	              		float x = event.getX(pointerIndexViewangleBar);
	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
	            		
	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
	    		        	touchViewangleBarDown(hudSystem, y);
	    				}
	    			}
		        } else if (mWeapon1PointerId == -1 && getTouchedWithinRegionWeapon1(pointerDownX, pointerDownY)) {		        	
		        	mWeapon1PointerId = pointerDownId;
		        	mWeapon2PointerId = -1;		// Default to mWeapon1PointerId
		        		
		        	inputSystem.touchWeapon1Press = true;
		        	
	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
	               		float touchFireX = event.getX(pointerIndexDownFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
	            		
	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
	            		
	        	        if (touchMove && touchFire) {	    		        		
	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} else if (touchMove) {
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            			
	            		} else if (touchFire) {
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} 
	            	} else if (mMovePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));

	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            		} else {
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            		}
	    			} else if (mFirePointerId > -1) {
	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
	        			
	            		float touchFireX = event.getX(pointerIndexFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
	            		
	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            		} else {
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            		}
	    			}
	            	
	    			if (mViewangleBarPointerId > -1) {
	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
	        			
	              		float x = event.getX(pointerIndexViewangleBar);
	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
	            		
	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
	    		        	touchViewangleBarDown(hudSystem, y);
	    				}
	    			}
		        } else if (mWeapon1PointerId == -1 && mWeapon2PointerId == -1 && getTouchedWithinRegionWeapon2(pointerDownX, pointerDownY)) {       				        		
		        	mWeapon2PointerId = pointerDownId;
		        	
		        	inputSystem.touchWeapon2Press = true;
		        	
	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
	               		float touchFireX = event.getX(pointerIndexDownFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
	            		
	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
	            		
	        	        if (touchMove && touchFire) {	    		        		
	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} else if (touchMove) {
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            			
	            		} else if (touchFire) {
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} 
	            	} else if (mMovePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));

	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            		} else {
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            		}
	    			} else if (mFirePointerId > -1) {
	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
	        			
	            		float touchFireX = event.getX(pointerIndexFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
	            		
	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	        	        } else {
	        	        	hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	        	        }
	    			}
	            	
	    			if (mViewangleBarPointerId > -1) {
	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
	        			
	              		float x = event.getX(pointerIndexViewangleBar);
	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
	            		
	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
	    		        	touchViewangleBarDown(hudSystem, y);
	    				}
	    			}
		        } else if (mViewangleBarPointerId == -1 && getTouchedWithinRegionViewangleBar(pointerDownX, pointerDownY)) {
	        		
		        	mViewangleBarPointerId = pointerDownId;
		        	
		        	touchViewangleBarDown(hudSystem, pointerDownY);
		        	
	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
	               		float touchFireX = event.getX(pointerIndexDownFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
	            		
	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
	            		
	        	        if (touchMove && touchFire) {	    		        		
	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} else if (touchMove) {
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            			
	            		} else if (touchFire) {
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} 
	            	} else if (mMovePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));

	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            		} else {
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            		}
	    			} else if (mFirePointerId > -1) {
	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
	        			
	            		float touchFireX = event.getX(pointerIndexFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
	            		
	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	        	        } else {
	        	        	hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	        	        }
	    			}
	            	
	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
	    			if (mWeapon1PointerId > -1) {
	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon1);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
	            		
	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
	            			inputSystem.touchWeapon1Press = true;
	    		        }
	            	} else if (mWeapon2PointerId > -1) {
	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon2);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
	            		
	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
	            			inputSystem.touchWeapon2Press = true;
	    		        }
	            	}
		        } else {
	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
	               		float touchFireX = event.getX(pointerIndexDownFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
	            		
	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
	            		
	        	        if (touchMove && touchFire) {	    		        		
	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} else if (touchMove) {
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            			
	            		} else if (touchFire) {
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            			
	            		} 
	            	} else if (mMovePointerId > -1) {
	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
	        			
	               		float touchMoveX = event.getX(pointerIndexDownMove);
	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));

	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
	            			
	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
	            		} else {
	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
	            		}
	    			} else if (mFirePointerId > -1) {
	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
	        			
	            		float touchFireX = event.getX(pointerIndexFire);
	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
	            		
	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
	            			touchFireDown(inputSystem, touchFireX, touchFireY);
	            			
	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
	            		} else {
	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
	            		}
	    			}
	    			
	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
	    			if (mWeapon1PointerId > -1) {
	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon1);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
	            		
	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
	            			inputSystem.touchWeapon1Press = true;
	    		        }
	            	} else if (mWeapon2PointerId > -1) {
	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
	        			
	              		float x = event.getX(pointerIndexWeapon2);
	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
	            		
	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
	            			inputSystem.touchWeapon2Press = true;
	    		        }
	            	}
	    			
	    			if (mViewangleBarPointerId > -1) {
	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
	        			
	              		float x = event.getX(pointerIndexViewangleBar);
	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
	            		
	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
	    		        	touchViewangleBarDown(hudSystem, y);
	    				}
	    			}
		        }

            	break;
            	
            default:
            	break;
    		} // end of switch statement
//            }
    	}
        
        return true;
  		
//  		// NEW ONTOUCHEVENT() CODE
////    	// FIXME TEMP. DELETE.
////    	GameParameters.gameCounter++;
////    	
////    	HudSystem hudSystem = BaseObject.sSystemRegistry.hudSystem;
////	    
////	    final float gameTime = ObjectRegistry.sSystemRegistry.timeSystem.getGameTime();
////    	
////		if (gameTime > (mLastTime + 0.032f)) {
////			GameParameters.gameTimerCounter++;
////
////		if (mRunning) {
//////	        if (mRunning && !hudSystem.levelIntro) {  
////	        	int pointerCount = event.getPointerCount();
////	        	
//////	        	Log.i("TouchEvent", "Game onTouchEvent() pointerCount = " + pointerCount);
////	    		
////	    		final int action = event.getAction();
////	    		
////	    		InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
////	    		
////	    		float x = 0.0f;
////	    		float y = 0.0f;
////	    		
////	    		switch (action & MotionEvent.ACTION_MASK) {
////	    		
////	    		case MotionEvent.ACTION_UP:
////	    			GameParameters.gameCounterTouchUp++;
////	    			
////	    			// Reset all touchPress = false
////	    			inputSystem.reset();
////	    			mTempMovePress = false;
////	    			mTempFirePress = false;
////	    			mTempWeapon1Press = false;
////	    			mTempWeapon2Press = false;
////	    			mTempViewangleBarPress = false;
////	    			
////	    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
////	    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
////	    			
////	    			break;
////	    		
////	    		case MotionEvent.ACTION_DOWN:
////	    			GameParameters.gameCounterTouchDownMove++;
////	    			
////	        		x = event.getX();
////	        		y = mViewHeight - event.getY();
////	        		
////	    	        if (getTouchedWithinRegionMove(x, y)) {
////	    	        	mTempMovePress = true;
////	    	        	inputSystem.touchMovePress = true;
////	    	        	
////			        	touchMoveDown(inputSystem, x, y);
////			        	
////		    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////	    	        } else if (getTouchedWithinRegionFire(x, y)) {
////	    	        	mTempFirePress = true;
////	    	        	inputSystem.touchFirePress = true;
////	    	        	
////	        			touchFireDown(inputSystem, x, y);
////	        			
////		    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////			        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////			        	mTempWeapon1Press = true;
////			        	inputSystem.touchWeapon1Press = true;
////
////			        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////			        	mTempWeapon2Press = true;
////			        	inputSystem.touchWeapon2Press = true;
////
////			        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////			        	mTempViewangleBarPress = true;
////			        	inputSystem.touchViewangleBarPress = true;
////			        	
////			        	touchViewangleBarDown(hudSystem, y);
////
////			        }
////	    	        
////	    	        if (!mTempMovePress) {
////	    	        	inputSystem.touchMovePress = false;
////	    	        }
////	    	        if (!mTempFirePress) {
////	    	        	inputSystem.touchFirePress = false;
////	    	        }
////	    	        if (!mTempWeapon1Press) {
////	    	        	inputSystem.touchWeapon1Press = false;
////	    	        }
////	    	        if (!mTempWeapon2Press) {
////	    	        	inputSystem.touchWeapon2Press = false;
////	    	        }
////	    	        if (!mTempViewangleBarPress) {
////	    	        	inputSystem.touchViewangleBarPress = false;
////	    	        }
////	    	        	
////	    			mTempMovePress = false;
////	    			mTempFirePress = false;
////	    			mTempWeapon1Press = false;
////	    			mTempWeapon2Press = false;
////	    			mTempViewangleBarPress = false;
////	        		
////	        		break;
////	    		
////	    		case MotionEvent.ACTION_MOVE:
////	    			GameParameters.gameCounterTouchDownMove++;
////	    			
////	    			for (int i = 0; i < pointerCount; i++) {
////	    				
////		        		x = event.getX(i);
////		        		y = mViewHeight - event.getY(i);
////		        		
////		    	        if (getTouchedWithinRegionMove(x, y)) {
////		    	        	mTempMovePress = true;
////		    	        	inputSystem.touchMovePress = true;
////		    	        	
////				        	touchMoveDown(inputSystem, x, y);
////				        	
////			    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////		    	        } else if (getTouchedWithinRegionFire(x, y)) {
////		    	        	mTempFirePress = true;
////		    	        	inputSystem.touchFirePress = true;
////		    	        	
////		        			touchFireDown(inputSystem, x, y);
////		        			
////			    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////				        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////				        	mTempWeapon1Press = true;
////				        	inputSystem.touchWeapon1Press = true;
////
////				        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////				        	mTempWeapon2Press = true;
////				        	inputSystem.touchWeapon2Press = true;
////
////				        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////				        	mTempViewangleBarPress = true;
////				        	inputSystem.touchViewangleBarPress = true;
////				        	
////				        	touchViewangleBarDown(hudSystem, y);
////
////				        }
////	    			}
////	    			
////	    	        if (!mTempMovePress) {
////	    	        	inputSystem.touchMovePress = false;
////	    	        }
////	    	        if (!mTempFirePress) {
////	    	        	inputSystem.touchFirePress = false;
////	    	        }
////	    	        if (!mTempWeapon1Press) {
////	    	        	inputSystem.touchWeapon1Press = false;
////	    	        }
////	    	        if (!mTempWeapon2Press) {
////	    	        	inputSystem.touchWeapon2Press = false;
////	    	        }
////	    	        if (!mTempViewangleBarPress) {
////	    	        	inputSystem.touchViewangleBarPress = false;
////	    	        }
////	    			
////	    			mTempMovePress = false;
////	    			mTempFirePress = false;
////	    			mTempWeapon1Press = false;
////	    			mTempWeapon2Press = false;
////	    			mTempViewangleBarPress = false;
////	    			
////	            	break;
////	    		
////	    		case MotionEvent.ACTION_POINTER_UP:
////	    			GameParameters.gameCounterTouchUp++;
////	    			
////	    			for (int i = 0; i < pointerCount; i++) {
////	    				
////		        		x = event.getX(i);
////		        		y = mViewHeight - event.getY(i);
////		        		
////		    	        if (getTouchedWithinRegionMove(x, y)) {
////		    	        	mTempMovePress = true;
////		    	        	inputSystem.touchMovePress = true;
////		    	        	
////				        	touchMoveDown(inputSystem, x, y);
////				        	
////			    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////		    	        } else if (getTouchedWithinRegionFire(x, y)) {
////		    	        	mTempFirePress = true;
////		    	        	inputSystem.touchFirePress = true;
////		    	        	
////		        			touchFireDown(inputSystem, x, y);
////		        			
////			    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////				        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////				        	mTempWeapon1Press = true;
////				        	inputSystem.touchWeapon1Press = true;
////
////				        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////				        	mTempWeapon2Press = true;
////				        	inputSystem.touchWeapon2Press = true;
////
////				        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////				        	mTempViewangleBarPress = true;
////				        	inputSystem.touchViewangleBarPress = true;
////				        	
////				        	touchViewangleBarDown(hudSystem, y);
////
////				        }
////	    			}
////	    			
////	    	        if (!mTempMovePress) {
////	    	        	inputSystem.touchMovePress = false;
////	    	        }
////	    	        if (!mTempFirePress) {
////	    	        	inputSystem.touchFirePress = false;
////	    	        }
////	    	        if (!mTempWeapon1Press) {
////	    	        	inputSystem.touchWeapon1Press = false;
////	    	        }
////	    	        if (!mTempWeapon2Press) {
////	    	        	inputSystem.touchWeapon2Press = false;
////	    	        }
////	    	        if (!mTempViewangleBarPress) {
////	    	        	inputSystem.touchViewangleBarPress = false;
////	    	        }
////	    			
////	    			mTempMovePress = false;
////	    			mTempFirePress = false;
////	    			mTempWeapon1Press = false;
////	    			mTempWeapon2Press = false;
////	    			mTempViewangleBarPress = false;
////	    			
////	            	break;
////	    		
////	    		case MotionEvent.ACTION_POINTER_DOWN:
////	    			GameParameters.gameCounterTouchDownMove++;
////	    			
////	    			for (int i = 0; i < pointerCount; i++) {
////	    				
////		        		x = event.getX(i);
////		        		y = mViewHeight - event.getY(i);
////		        		
////		    	        if (getTouchedWithinRegionMove(x, y)) {
////		    	        	mTempMovePress = true;
////		    	        	inputSystem.touchMovePress = true;
////		    	        	
////				        	touchMoveDown(inputSystem, x, y);
////				        	
////			    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////		    	        } else if (getTouchedWithinRegionFire(x, y)) {
////		    	        	mTempFirePress = true;
////		    	        	inputSystem.touchFirePress = true;
////		    	        	
////		        			touchFireDown(inputSystem, x, y);
////		        			
////			    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////				        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////				        	mTempWeapon1Press = true;
////				        	inputSystem.touchWeapon1Press = true;
////
////				        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////				        	mTempWeapon2Press = true;
////				        	inputSystem.touchWeapon2Press = true;
////
////				        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////				        	mTempViewangleBarPress = true;
////				        	inputSystem.touchViewangleBarPress = true;
////				        	
////				        	touchViewangleBarDown(hudSystem, y);
////
////				        }
////	    			}
////	    			
////	    	        if (!mTempMovePress) {
////	    	        	inputSystem.touchMovePress = false;
////	    	        }
////	    	        if (!mTempFirePress) {
////	    	        	inputSystem.touchFirePress = false;
////	    	        }
////	    	        if (!mTempWeapon1Press) {
////	    	        	inputSystem.touchWeapon1Press = false;
////	    	        }
////	    	        if (!mTempWeapon2Press) {
////	    	        	inputSystem.touchWeapon2Press = false;
////	    	        }
////	    	        if (!mTempViewangleBarPress) {
////	    	        	inputSystem.touchViewangleBarPress = false;
////	    	        }
////	    			
////	    			mTempMovePress = false;
////	    			mTempFirePress = false;
////	    			mTempWeapon1Press = false;
////	    			mTempWeapon2Press = false;
////	    			mTempViewangleBarPress = false;
////
////	            	break;
////	            	
////	            default:
////	            	break;
////	    		} // end of switch statement
////	    	}
////			
////			mLastTime = gameTime;
//			
////			touchAccept = true;
////		}
//        
////        return true;
//////        return touchAccept;
  		
  		/* XXX TEST CODE 11/29/12 for simple touchX[i],touchY[i] method. However, couldn't allow for 
  		 * continued Move,Fire Press until ACTION_UP Event. It could only detect and hold within getToucheWithinRegionXX() */
//    	GameParameters.gameCounter++;
//    	
//    	InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
//    	HudSystem hudSystem = BaseObject.sSystemRegistry.hudSystem;
//    	
//        if (mRunning) {  
////        if (mRunning && !hudSystem.levelIntro) {  
//        	
//        	for (int i = 0; i < pointerCount; i++) {
//        		Log.i("TouchCounter", "Game touchInput() pointerCount#; touchX, touchY = " +
//        				i + "; " + touchX[i] + ", " + touchY[i]);
//        		
//    	        if (getTouchedWithinRegionMove(touchX[i], touchY[i])) {        					        		
//    	        	touchMoveDown(inputSystem, touchX[i], touchY[i]);
//    	        	
//        			hudSystem.setMoveButtonTopLocationTouch(touchX[i], touchY[i]);
//
//    	        } else if (getTouchedWithinRegionFire(touchX[i], touchY[i])) {        					        		
//        			touchFireDown(inputSystem, touchX[i], touchY[i]);
//        			
//        			hudSystem.setFireButtonTopLocationTouch(touchX[i], touchY[i]);
//    	        		
//    	        } else if (getTouchedWithinRegionWeapon1(touchX[i], touchY[i])) {        					        		
//    	        	inputSystem.touchWeapon1Press = true;
//
//    	        } else if (getTouchedWithinRegionWeapon2(touchX[i], touchY[i])) {		        		
//    	        	inputSystem.touchWeapon2Press = true;
//
//    	        } else if (getTouchedWithinRegionViewangleBar(touchX[i], touchY[i])) {
//    	        	touchViewangleBarDown(hudSystem, touchY[i]);
//
//    	        }	
//        	}
////    		final int action = event.getAction();
////    		
//////    		InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
////    		
////    		switch (action & MotionEvent.ACTION_MASK) {
////    		
////    		case MotionEvent.ACTION_UP:
////    			GameParameters.gameCounterTouchUp++;
////    			
//////    			inputSystem.reset();
//////    			
//////    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//////    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
////    			
////    			mMovePointerId = -1;
////    			mFirePointerId = -1;
////    			mWeapon1PointerId = -1;
////    			mWeapon2PointerId = -1;
////    			mViewangleBarPointerId = -1;
////    			
////    			break;
////    		
////    		case MotionEvent.ACTION_DOWN:
////    			GameParameters.gameCounterTouchDownMove++;
////    			
////        		float downX = event.getX();
////        		float downY = mViewHeight - event.getY();
////        		
////    	        if (getTouchedWithinRegionMove(downX, downY)) {        					        		
//////		        	touchMoveDown(inputSystem, downX, downY);
//////		        	
//////	    			hudSystem.setMoveButtonTopLocationTouch(downX, downY);
////		        		
////		        	mMovePointerId = event.getPointerId(0);
////    	        } else if (getTouchedWithinRegionFire(downX, downY)) {        					        		
//////        			touchFireDown(inputSystem, downX, downY);
//////        			
//////	    			hudSystem.setFireButtonTopLocationTouch(downX, downY);
////		        		
////		        	mFirePointerId = event.getPointerId(0);
////		        } else if (getTouchedWithinRegionWeapon1(downX, downY)) {        					        		
//////		        	inputSystem.touchWeapon1Press = true;
////		        		
////		        	mWeapon1PointerId = event.getPointerId(0);
////		        } else if (getTouchedWithinRegionWeapon2(downX, downY)) {		        		
//////		        	inputSystem.touchWeapon2Press = true;
////		        		
////		        	mWeapon2PointerId = event.getPointerId(0);
////		        } else if (getTouchedWithinRegionViewangleBar(downX, downY)) {
//////		        	touchViewangleBarDown(hudSystem, downY);
////		        		
////		        	mViewangleBarPointerId = event.getPointerId(0);
////		        }
////        		
////        		break;
////    		
////    		case MotionEvent.ACTION_MOVE:
////    			GameParameters.gameCounterTouchDownMove++;
////    			
////    			if (mMovePointerId > -1 && mFirePointerId > -1) {
////        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
////        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
////        			
////            		float touchMoveX = event.getX(pointerIndexMove);
////            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
////            		float touchFireX = event.getX(pointerIndexFire);
////            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
////            		   		        		
//////        			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//////        			
//////	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//////	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
////
////            	} else if (mMovePointerId > -1) {
////        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
////        			
////            		float touchMoveX = event.getX(pointerIndexMove);
////            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
////            		  		        		
//////        			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//////        			
//////	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
////
////            	} else if (mFirePointerId > -1) {
////        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
////        			
////              		float touchFireX = event.getX(pointerIndexFire);
////            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
////            				        		
//////        			touchFireDown(inputSystem, touchFireX, touchFireY);
//////        			
//////	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
////
////            	}
////    			
////    			if (mViewangleBarPointerId > -1) {
////        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
////        			
////              		float x = event.getX(pointerIndexViewangleBar);
////            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
////            		
////    				if (getTouchedWithinRegionViewangleBar(x, y)) {
////    		        	touchViewangleBarDown(hudSystem, y);
////    				}
////    			}
////    			
////            	break;
////    		}
        	// XXX END TEST CODE 11/29/12
//        }
    }
  	
	private final boolean getTouchedWithinRegionMove(float x, float y) {
		 return (x >= mMoveButtonTouchLeftX &&
				 y >= mMoveButtonTouchBottomY &&
				 x <= mMoveButtonTouchLeftX + mMoveFireButtonTouchWidth &&
				 y <= mMoveButtonTouchBottomY + mMoveFireButtonTouchHeight);
	}
	
	private final boolean getTouchedWithinRegionFire(float x, float y) {
		 return (x >= mFireButtonTouchLeftX &&
				 y >= mFireButtonTouchBottomY &&
				 x <= mFireButtonTouchLeftX + mMoveFireButtonTouchWidth &&
				 y <= mFireButtonTouchBottomY + mMoveFireButtonTouchHeight);
	}
	
	private final boolean getTouchedWithinRegionWeapon1(float x, float y) {
		 return (x >= mWeapon1ButtonTouchLeftX &&
				 y >= mWeapon1ButtonTouchBottomY &&
				 x <= mWeapon1ButtonTouchLeftX + mWeaponButtonTouchWidth &&
				 y <= mWeapon1ButtonTouchBottomY + mWeaponButtonTouchHeight);
	}
	
	private final boolean getTouchedWithinRegionWeapon2(float x, float y) {
		 return (x >= mWeapon2ButtonTouchLeftX &&
				 y >= mWeapon2ButtonTouchBottomY &&
				 x <= mWeapon2ButtonTouchLeftX + mWeaponButtonTouchWidth &&
				 y <= mWeapon2ButtonTouchBottomY + mWeaponButtonTouchHeight);
	}
	
	private final boolean getTouchedWithinRegionViewangleBar(float x, float y) {
		 return (x >= mViewangleBarBaseTouchLeftX &&
				 y >= mViewangleBarBaseTouchBottomY &&
				 x <= mViewangleBarBaseTouchLeftX + mViewangleBarBaseTouchWidth &&
				 y <= mViewangleBarBaseTouchBottomY + mViewangleBarBaseTouchHeight);
	}
		
	private void touchMoveDown(InputSystem inputSystem, float touchMoveX, float touchMoveY) {	
//		Log.i("Loop", "Game touchMoveDown() touchMoveX,touchMoveY = " + touchMoveX + ", " + touchMoveY);
		
		float moveR = buttonAngleCalc(touchMoveX, touchMoveY, mMoveButtonCenterX, mMoveButtonCenterY);
		moveR = moveR + ISOMETRIC;  // Isometric angle adjustment included
		
		float moveMultiplier = 1.0f;
		float vx = mMoveButtonCenterX - touchMoveX;
		float vy = mMoveButtonCenterY - touchMoveY;
		float m = (float)Math.sqrt((vx * vx) + (vy * vy));
		if (m > (mMoveFireButtonTouchRadius * 0.66f)) {
			moveMultiplier = 3.0f;
		} else if (m > (mMoveFireButtonTouchRadius * 0.33f)) {
			moveMultiplier = 2.0f;
		}
		
		// Calculate Motion Vector vx (moveX) and vz (moveZ) components
		float moveX = (float)Math.sin(moveR * PI_OVER_180) * moveMultiplier;
		float moveZ = (float)Math.cos(moveR * PI_OVER_180) * moveMultiplier;
//		float moveX = (float)Math.sin(moveR * PI_OVER_180);
//		float moveZ = (float)Math.cos(moveR * PI_OVER_180);
		
		inputSystem.movePosition.set(moveX, 0.0f, moveZ, moveR);
		inputSystem.touchMovePress = true;
//		mMoveButton.press(moveX, 0.0f, moveZ, moveR);
//		mMoveButton.press(gameTime, moveX, 0.0f, moveZ, moveR);
		
//		Log.i("Loop", "Game touchMoveDown() moveX,moveZ,moveR = " + moveX + ", " + moveZ + ", " + moveR);
	}
	
	private void touchFireDown(InputSystem inputSystem, float touchFireX, float touchFireY) {		
//		Log.i("Loop", "Game touchFireDown() touchFireX,touchFireY = " + touchFireX + ", " + touchFireY);
		
		float fireR = buttonAngleCalc(touchFireX, touchFireY, mFireButtonCenterX, mFireButtonCenterY);
		fireR = fireR + ISOMETRIC;  // Isometric angle adjustment included
		
		inputSystem.firePosition.set(0.0f, 0.0f, 0.0f, fireR);
		inputSystem.touchFirePress = true;
//		mFireButton.press(moveX, 0.0f, moveZ, fireR);
//		mFireButton.press(gameTime, moveX, 0.0f, moveZ, fireR);
		
//		Log.i("Loop", "Game touchFireDown() fireR = " + fireR);
	}
	
	private void touchMoveFireDown(InputSystem inputSystem, float touchMoveX, float touchMoveY, float touchFireX, float touchFireY) {
//		Log.i("Loop", "Game touchMoveFireDown() touchMoveX,touchMoveY;touchFireX,touchFireY = " + touchMoveX + ", " + touchMoveY + "; " + touchFireX + ", " + touchFireY);
		
		float moveR = buttonAngleCalc(touchMoveX, touchMoveY, mMoveButtonCenterX, mMoveButtonCenterY);	
		moveR = moveR + ISOMETRIC;  // Isometric angle adjustment included	
		
		float moveMultiplier = 1.0f;
		float vx = mMoveButtonCenterX - touchMoveX;
		float vy = mMoveButtonCenterY - touchMoveY;
		float m = (float)Math.sqrt((vx * vx) + (vy * vy));
		if (m > (mMoveFireButtonTouchRadius * 0.66f)) {
			moveMultiplier = 3.0f;
		} else if (m > (mMoveFireButtonTouchRadius * 0.33f)) {
			moveMultiplier = 2.0f;
		}

		float moveX = (float)Math.sin(moveR * PI_OVER_180) * moveMultiplier;
		float moveZ = (float)Math.cos(moveR * PI_OVER_180) * moveMultiplier;
//		float moveX = (float)Math.sin(moveR * PI_OVER_180);
//		float moveZ = (float)Math.cos(moveR * PI_OVER_180);
		
		float fireR = buttonAngleCalc(touchFireX, touchFireY, mFireButtonCenterX, mFireButtonCenterY);
		fireR = fireR + ISOMETRIC;  // Isometric angle adjustment included
		
		inputSystem.movePosition.set(moveX, 0.0f, moveZ, moveR);
		inputSystem.touchMovePress = true;
		
		inputSystem.firePosition.set(moveX, 0.0f, moveZ, fireR);
		inputSystem.touchFirePress = true;
//		mFireButton.press(moveX, 0.0f, moveZ, fireR);
//		mFireButton.press(gameTime, moveX, 0.0f, moveZ, fireR);
		
//		Log.i("Loop", "Game touchMoveFireDown() moveX,moveZ,moveR; fireR = " + moveX + ", " + moveZ + ", " + moveR + "; " + fireR);
	}
	
	private void touchViewangleBarDown(HudSystem hudSystem, float touchY) {
		CameraSystem camera = BaseObject.sSystemRegistry.cameraSystem;
		
		float viewY = touchY - mViewangleBarBaseTouchBottomY;
//		float viewY = (touchY - mViewangleBarBaseTouchBottomY) * mInverseViewScaleY;
		float x = 0.0f;
		float y = 0.0f;
		float z = 0.0f;
		
        if (mScreenDensity == 0) {
    		/* viewY range 0.0f to 80.0f
    		 * 5 possible HudSystem ViewangleBarButton settings: 8.0f, 24.0f, 40.0f (default), 56.0f, 72.0f
    		 * Set gluLookAt() angle setting.
    		 * Multiply x2 for hdpi. */ 
//    		viewY range 0.0f to 80.0f (take inverse of viewScaleY)
    		if (viewY < 16.0f) {
    			viewY = 8.0f;
    			x = -7.8f;
    			y = 6.75f;
    			z = 7.8f;
//    			x = -7.0f;
//    			y = 6.0f;
//    			z = 7.0f;
    		} else if (viewY < 32.0f) {
    			viewY = 24.0f;
    			x = -6.5f;
    			y = 9.25f;
    			z = 6.5f;
//    			x = -6.0f;
//    			y = 8.0f;
//    			z = 6.0f;
    		} else if (viewY < 48.0f) {
    			viewY = 40.0f;	// default
    			x = -5.0f;
    			y = 11.0f;
    			z = 5.0f;
//    			x = -4.5f;
//    			y = 10.0f;
//    			z = 4.5f;
    		} else if (viewY < 64.0f) {
    			viewY = 56.0f;
    			x = -4.0f;
    			y = 11.75f;
    			z = 4.0f;
//    			x = -3.5f;
//    			y = 10.5f;
//    			z = 3.5f;
    		} else {
    			viewY = 72.0f;
    			x = -2.75f;
    			y = 12.3f;
    			z = 2.75f;
//    			x = -2.5f;
//    			y = 11.0f;
//    			z = 2.5f;
    		}
        } else if (mScreenDensity == 1) {
    		/* viewY range 0.0f to 80.0f
    		 * 5 possible HudSystem ViewangleBarButton settings: 16.0f, 48.0f, 80.0f (default), 112.0f, 144.0f
    		 * Set gluLookAt() angle setting.
    		 * Multiply x2 for hdpi. */ 
//    		viewY range 0.0f to 80.0f (take inverse of viewScaleY)
    		if (viewY < 32.0f) {
    			viewY = 16.0f;
//    		if (viewY < 16.0f) {
//    			viewY = 8.0f;
    			x = -7.8f;
    			y = 6.75f;
    			z = 7.8f;
//    			x = -7.0f;
//    			y = 6.0f;
//    			z = 7.0f;
    		} else if (viewY < 64.0f) {
    			viewY = 48.0f;
//    		} else if (viewY < 32.0f) {
//    			viewY = 24.0f;
    			x = -6.5f;
    			y = 9.25f;
    			z = 6.5f;
//    			x = -6.0f;
//    			y = 8.0f;
//    			z = 6.0f;
    		} else if (viewY < 96.0f) {
    			viewY = 80.0f;	// default
//    		} else if (viewY < 48.0f) {
//    			viewY = 40.0f;	// default
    			x = -5.0f;
    			y = 11.0f;
    			z = 5.0f;
//    			x = -4.5f;
//    			y = 10.0f;
//    			z = 4.5f;
    		} else if (viewY < 128.0f) {
    			viewY = 112.0f;
//    		} else if (viewY < 64.0f) {
//    			viewY = 56.0f;
    			x = -4.0f;
    			y = 11.75f;
    			z = 4.0f;
//    			x = -3.5f;
//    			y = 10.5f;
//    			z = 3.5f;
    		} else {
    			viewY = 144.0f;
//    			viewY = 72.0f;
    			x = -2.75f;
    			y = 12.3f;
    			z = 2.75f;
//    			x = -2.5f;
//    			y = 11.0f;
//    			z = 2.5f;
    		}
        } else if (mScreenDensity == 2) {
    		/* viewY range 0.0f to 80.0f
    		 * 5 possible HudSystem ViewangleBarButton settings: 8.0f, 24.0f, 111.0f (default), 56.0f, 72.0f
    		 * Set gluLookAt() angle setting.
    		 * Multiply x2 for hdpi. */ 
//    		viewY range 0.0f to 80.0f (take inverse of viewScaleY)
    		if (viewY < 32.0f) {
    			viewY = 16.0f;
//    		if (viewY < 16.0f) {
//    			viewY = 8.0f;
    			x = -7.8f;
    			y = 6.75f;
    			z = 7.8f;
//    			x = -7.0f;
//    			y = 6.0f;
//    			z = 7.0f;
    		} else if (viewY < 72.0f) {
    			viewY = 56.0f;
//    		} else if (viewY < 32.0f) {
//    			viewY = 24.0f;
    			x = -6.5f;
    			y = 9.25f;
    			z = 6.5f;
//    			x = -6.0f;
//    			y = 8.0f;
//    			z = 6.0f;
    		} else if (viewY < 127.0f) {
    			viewY = 111.0f;	// default
//    		} else if (viewY < 48.0f) {
//    			viewY = 40.0f;	// default
    			x = -5.0f;
    			y = 11.0f;
    			z = 5.0f;
//    			x = -4.5f;
//    			y = 10.0f;
//    			z = 4.5f;
    		} else if (viewY < 182.0f) {
    			viewY = 166.0f;
//    		} else if (viewY < 64.0f) {
//    			viewY = 56.0f;
    			x = -4.0f;
    			y = 11.75f;
    			z = 4.0f;
//    			x = -3.5f;
//    			y = 10.5f;
//    			z = 3.5f;
    		} else {
    			viewY = 206.0f;
//    			viewY = 72.0f;
    			x = -2.75f;
    			y = 12.3f;
    			z = 2.75f;
//    			x = -2.5f;
//    			y = 11.0f;
//    			z = 2.5f;
    		}
        }
		
//		// Multiply x2 for hdpi due to bitmap re-scaling calculation (normally would be x1.6)
//		if (mScreenDensity == 1) {
//			viewY *= 2;
//		}
		
//		Log.i("TouchCounter", "touchViewangleBarDown() touchY; viewY = " + touchY + "; " + viewY);
		
		hudSystem.setViewangleBarButtonLocationY(viewY);
		camera.setViewangle(x, y, z);
	}
	
	/**
	 * Calculates the angle at which an Object should be Facing
	 * @param x
	 * @param y
	 * @param centerX
	 * @param centerY
	 * @return
	 */
	private float buttonAngleCalc(float x, float y, float centerX, float centerY) {
		float buttonAngle = 0.0f;

		// 2D Angle Calculation in Degrees
		if (x <= centerX) {	        
			if (y >= centerY) {
				if ((y - centerY) == 0) {
					// Set to minimal denominator
					buttonAngle = (float)Math.atan(
							(centerX - x) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					buttonAngle = (float)Math.atan(
							(centerX - x) /
							(y - centerY)) * ONE_EIGHTY_OVER_PI;
				}
			} else {
				if ((centerX - x) == 0) {
					// Set to minimal denominator
					buttonAngle = 90.0f + (float)Math.atan(
							(centerY - y) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					buttonAngle = 90.0f + (float)Math.atan(
							(centerY - y) /
							(centerX - x)) * ONE_EIGHTY_OVER_PI;
				}
			}
		} else {
			if (y < centerY) {
				buttonAngle = 180.0f + (float)Math.atan(
						(x - centerX) /
						(centerY - y)) * ONE_EIGHTY_OVER_PI;
			} else {
				buttonAngle = 270.0f + (float)Math.atan(
						(y - centerY) /
						(x - centerX)) * ONE_EIGHTY_OVER_PI;
			}
		}
		return buttonAngle;
	}
    
//    public boolean onKeyDownEvent(int keyCode) {
////        DebugLog.d("Game", "onKeyDownEvent()");
//    	
//        boolean result = false;
//        if (mRunning) {
//            BaseObject.sSystemRegistry.inputSystem.keyDown(keyCode);
//        }
//        return result;
//    }
    
//    public boolean onKeyUpEvent(int keyCode) {
////        DebugLog.d("Game", "onKeyUpEvent()");
//    	
//        boolean result = false;
//        if (mRunning) {
//        	BaseObject.sSystemRegistry.inputSystem.keyUp(keyCode);
//        }
//        return result;
//    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public void setLevelRow(int levelRow) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "Game setLevelRow() levelRow = " + levelRow);	
        }
    	
    	mLevelRow = levelRow;
    	
    	GameParameters.levelRow = levelRow;
    	
//    	mRenderer.setLevelRow(levelRow);
    	
//    	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//    	factory.setLevelRow(levelRow);
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public void setPendingLevel(LevelTree.Level level) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "Game setPendingLevel()");	
        }
    	
        mPendingLevel = level;
    }
    
    public GameRenderer getRenderer() {
        return mRenderer;
    }  
    
    public void setGameRestart(boolean restart) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "Game setGameRestart() restart = " + restart);	
        }
    	
    	mGameRestart = restart;
    }
    
    public void setSurfaceView(DroidGLSurfaceView surfaceView) {
//    	mSurfaceView = surfaceView;
    	
    	mGameThread.setSurfaceView(surfaceView);
    }
    
    public void setWeapons(int weaponActive, int weaponInventory) {
        if (weaponActive == 0) {
        	// ignore
        } else {
        	mWeaponActiveType = Type.indexToType(weaponActive);
        }
        
        if (weaponInventory == 0) {
        	// ignore
        } else {
            mWeaponInventoryType = Type.indexToType(weaponInventory);
        }
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "Game setWeapons() mWeaponActiveType, mWeaponInventoryType = " +
            		mWeaponActiveType + ", " + mWeaponInventoryType);	
        }
    }
    
    public void setDifficultyLevel(int level) {
//    	mDifficultyLevel = level;
    	
    	GameParameters.difficulty = level;
    }
    
	public void setMusicEnabled(boolean soundEnabled) {
		GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
		factory.setMusicEnabled(soundEnabled);
	}

	public void setSoundEnabled(boolean soundEnabled) {
		SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
		sound.setSoundEnabled(soundEnabled);
	}
	
//	public void setControlOptions(boolean clickAttack, boolean tiltControls, int tiltSensitivity, int movementSensitivity) {
////        DebugLog.d("Game", "setControlOptions()");
//		
//		BaseObject.sSystemRegistry.inputGameInterface.setUseClickForAttack(clickAttack);
//		BaseObject.sSystemRegistry.inputGameInterface.setUseOrientationForMovement(tiltControls);
//		BaseObject.sSystemRegistry.inputGameInterface.setOrientationMovementSensitivity((tiltSensitivity / 100.0f));
//		BaseObject.sSystemRegistry.inputGameInterface.setMovementSensitivity((movementSensitivity / 100.0f));
//	}
	
//	public void setSafeMode(boolean safe) {
//        DebugLog.d("Game", "setSafeMode()");
//		
//		mSurfaceView.setSafeMode(safe);
//	}
	
	public void setPoints(int killCollectPoints, int collectNum) {
		HudSystem hud = BaseObject.sSystemRegistry.hudSystem;
		hud.totalKillCollectPoints = killCollectPoints;
		hud.totalCollectNum = collectNum;
	}
	
//	public void setLevelStartTime() {
//		mLevelStartTime = System.currentTimeMillis();
//	}
	
	public float getGameTime() {
		return BaseObject.sSystemRegistry.timeSystem.getGameTime();
	}
	
    // XXX Delete - Replica EventRecorder
//	public Vector2 getLastDeathPosition() {
//		return BaseObject.sSystemRegistry.eventRecorder.getLastDeathPosition();
//	}

//	public void setKeyConfig(int leftKey, int rightKey, int jumpKey,
//			int attackKey) {
////        DebugLog.d("Game", "setKeyConfig()");
//		
//		BaseObject.sSystemRegistry.inputGameInterface.setKeys(leftKey, rightKey, jumpKey, attackKey);
//	}
}