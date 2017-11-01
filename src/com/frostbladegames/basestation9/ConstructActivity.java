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

import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
//import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
// FIXME Re-enable License Checker
//import com.android.vending.licensing.AESObfuscator;
//import com.android.vending.licensing.LicenseChecker;
//import com.android.vending.licensing.LicenseCheckerCallback;
//import com.android.vending.licensing.ServerManagedPolicy;
//import com.android.vending.licensing.LicenseCheckerCallback.ApplicationErrorCode;
import android.widget.ImageView;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
//import com.frostbladegames.basestation9.R;

/**
 * Core activity for the game.  Sets up a surface view for OpenGL, bootstraps
 * the game engine, and manages UI events.  Also manages game progression,
 * transitioning to other activites, save game, and input events.
 * DroidConstruct Activity, setting and initiating
 * the OpenGL ES Renderer Class @see GLSurfaceView.java
 */
public class ConstructActivity extends Activity {
//public class ConstructActivity extends Activity implements OnTouchListener {
////public class ConstructActivity extends Activity {
	// FIXME Re-enable License Checker
//	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuhB9/bOw6WlRh166P2MchPCZNQKXe4XVn0L5mQE5CPQF6nF1M0V6qRzglFQF7r39gF/YBkIGWypwfoZcQopeyO3oNuqIFRfuROkNCCQ5W6xu2FS7d4mO/hLXf8si8luDcjIWui5ccjaGh3NogH2dKNuivxC+ehHmq0G6XBYfaSg/pP1LKmAXtbZNqq4cdW6HOIxL5UsFHUkvXEVAOOzkFrhDX01/1bbSz8gh+icsGqcqaKznn6BgZdkK+iSXdjpGIx6iTwxt8hFAnMNCQR9jj/O0sXx9w4TWSQnA5ePQEYHj+hKR1yVB0/Kw4rSJ2Svx5UxuEwnCIt+o22BZZ1tUcQIDAQAB";
//	
//	private static final byte[] SALT;
//
//	static {
//	    Random random = new Random();
//	    random.setSeed(System.currentTimeMillis());
//	    byte[] buf = new byte[20];
//	    random.nextBytes(buf);
//	    SALT = buf;
//	}
    
    private static final int ACTIVITY_CHANGE_LEVELS = 0;
//    private static final int ACTIVITY_CONVERSATION = 1;
//    private static final int ACTIVITY_DIARY = 2;
//    private static final int ACTIVITY_ANIMATION_PLAYER = 3;

    private static final int CHANGE_LEVEL_ID = Menu.FIRST;
//    private static final int TEST_ANIMATION_ID = CHANGE_LEVEL_ID + 1;
//    private static final int TEST_DIARY_ID = CHANGE_LEVEL_ID + 2;
//    private static final int METHOD_TRACING_ID = CHANGE_LEVEL_ID + 3;
    
    private static final int MAX_POINTER_COUNT = 3;
    
    private static final int ROLL_TO_FACE_BUTTON_DELAY = 400;
    
    private static final String PREFERENCE_DIFFICULTY_EASY = "New Model (Easy)";
    private static final String PREFERENCE_DIFFICULTY_MEDIUM = "Efficient (Medium)";
    private static final String PREFERENCE_DIFFICULTY_HARD = "Machine (Hard)";
//    private static final String PREFERENCE_DIFFICULTY_HARD = "Insane";
    
//    public static final String PREFERENCE_LEVEL_ROW = "levelRow";
//    public static final String PREFERENCE_LEVEL_INDEX = "levelIndex";
    public static final String PREFERENCE_LEVEL_NUM_ACTIVE = "levelNumActive";
//    public static final String PREFERENCE_LEVEL_COMPLETED = "levelCompleted";
    public static final String PREFERENCE_TOTAL_KILL_COLLECT_POINTS = "totalKillCollectPoints";
    public static final String PREFERENCE_TOTAL_COLLECT_NUM = "totalCollectNum";
    public static final String PREFERENCE_LEVEL_WEAPON_ACTIVE = "levelWeaponActive";
    public static final String PREFERENCE_LEVEL_WEAPON_INVENTORY = "levelWeaponInventory";
    public static final String PREFERENCE_SET_DIFFICULTY = "setDifficulty";
    public static final String PREFERENCE_MUSIC_ENABLED = "enableMusic";
    public static final String PREFERENCE_SOUND_ENABLED = "enableSound";
    public static final String PREFERENCE_SESSION_ID = "session";
    public static final String PREFERENCE_LAST_VERSION = "lastVersion";
    public static final String PREFERENCE_ENABLE_DEBUG = "enableDebug";
    
    public static final String PREFERENCE_NAME = "BaseStationPrefs";
    
    public static final int RETURN_MAINMENU_DIALOG = 0;
//    public static final int QUIT_GAME_DIALOG = 0;
    public static final int LICENSE_ALLOW = 1;
    public static final int LICENSE_DONT_ALLOW = 2;
    
    // FIXME Is this still required?
    // If the version is a negative number, debug features (logging and a debug menu)
    // are enabled.
    // VERSION enables/disables DebugLog mode
    public static final int VERSION = 1;
//    public static final int VERSION = -1;
    
    // FIXME 12/2/12 ADDED
    private static Context sContext;
    // FIXME END 12/2/12 ADDED

    private DroidGLSurfaceView mGLSurfaceView;
//    private GLSurfaceView mGLSurfaceView;
    private Game mGame;
//    private boolean mMethodTracing;
    private int mLevelSelected;
//    private int mLevelRow;
//    private int mLevelIndex;
    private int mLevelNumActive;
//    private int mLevelCompleted;
    
    private int mTotalKillCollectPoints;
    private int mTotalCollectNum;
    
    private int mWeaponActive;
    private int mWeaponInventory;
    
    private String mSetDifficulty;
    private int mDifficultyLevel;
    
    // TODO Re-enable - SensorManager
//    private SensorManager mSensorManager;

    private SharedPreferences.Editor mPrefsEditor;
    
    // FIXME TEMP TEST DELETE
//    private Object mObject = new Object();
    
    private long mLastTouchTime = 0L;
    private long mLastRollTime = 0L;
    
    private View mLevelLoadingMessage = null;
//    private View mLoadingMessage = null;
    private View mPauseMessage = null;
//    private View mWaitMessage = null;

    private Animation mLoadingAnimation = null;
//    private Animation mWaitFadeAnimation = null;
    
    private long mSessionId = 0L;
    
    private int mViewWidth;
    private int mViewHeight;
    private int mGameWidth;
    private int mGameHeight;
    
    private MotionEvent mEvent;
    
    private int mMovePointerId;
    private int mFirePointerId;
    private int mWeapon1PointerId;
    private int mWeapon2PointerId;
    private int mViewangleBarPointerId;
    
    private float[] mTouchX = new float[3];
    private float[] mTouchY = new float[3];
    
//    private Handler mLicenseHandler;
    
    // FIXME Re-enable LicenseChecker
//    private LicenseCheckerCallback mLicenseCheckerCallback;
//    private LicenseChecker mChecker;
    
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//    	.detectAll()
//    	.penaltyLog()
//    	.penaltyDeath()
//    	.build());
//    	
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .penaltyDeath()
//        .build());
//    	
////    	StrictMode.enableDefaults();
    	
        super.onCreate(savedInstanceState);
        
//        mLicenseHandler = new Handler();
        
        // FIXME LICENSE CHECK DISABLED
//        if (!GameParameters.debug) {
//            // TODO May want to make the deviceId stronger by adding TelephonyManager string or other
//            String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
//            
//            // Construct the LicenseCheckerCallback. The library calls this when done.
//            mLicenseCheckerCallback = new ConstructActivityLicenseCheckerCallback();
//            
//            // Construct the LicenseChecker with a Policy.
////            mLicenseChecker = new LicenseChecker(this, new ServerManagedPolicy(this, null), BASE64_PUBLIC_KEY);
//            mChecker = new LicenseChecker(this, new ServerManagedPolicy(
//            		this, new AESObfuscator(SALT, getPackageName(), deviceId)),
//            		BASE64_PUBLIC_KEY  // Your public licensing key.
//            		);
//            
//            mChecker.checkAccess(mLicenseCheckerCallback);	
//        }
        
        // FIXME 12/2/12 ADDED
        ConstructActivity.sContext = getApplicationContext();
        // FIXME END 12/2/12 ADDED
        
        if (GameParameters.debug) {
    		Log.i("GameFlow", "ConstructActivity onCreate()");	
        }
		
//		ProgressDialog mDialog = new ProgressDialog(getApplicationContext());
//      mDialog.setMessage("Loading...");
//      mDialog.setCancelable(false);
//      mDialog.show();
        
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//        final boolean debugLevelUnlock = prefs.getBoolean(PREFERENCE_ENABLE_DEBUG, false);
        
//        setContentView(R.layout.main_loading);
        setContentView(R.layout.main);
        mGLSurfaceView = (DroidGLSurfaceView) findViewById(R.id.glsurfaceview);
        
        mLevelLoadingMessage = findViewById(R.id.level_loading);
//        mLoadingMessage = findViewById(R.id.message_loading);
        mPauseMessage = findViewById(R.id.pausedMessage);
//        mWaitMessage = findViewById(R.id.pleaseWaitMessage);
        
        mLoadingAnimation = AnimationUtils.loadAnimation(this, R.anim.wait_message_fade);
//        mWaitFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.wait_message_fade);
        
        //mGLSurfaceView.setGLWrapper(new GLErrorLogger());
//        mGLSurfaceView.setEGLConfigChooser(false); // 16 bit, no z-buffer
        //mGLSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        
        mGame = new Game();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        int density = dm.densityDpi;

        mViewWidth = dm.widthPixels;
        mViewHeight = dm.heightPixels;
        mGameWidth = 480;	// default value; calculate actual value below
        mGameHeight = 320;
        if (mViewWidth != mGameWidth) {
        	float ratio =((float)mViewWidth) / mViewHeight;
        	mGameWidth = (int)(mGameHeight * ratio);
        }
//        int viewWidth = dm.widthPixels;
//        int viewHeight = dm.heightPixels;
//        int gameWidth = 480;
//        int gameHeight = 320;
//        if (viewWidth != gameWidth) {
//        	float ratio =((float)viewWidth) / viewHeight;
//        	gameWidth = (int)(gameHeight * ratio);
//        }
        
//        mGame.bootstrap(this, viewWidth, viewHeight, gameWidth, gameHeight);
//        mGLSurfaceView.setRenderer(mGame.getRenderer());
        
        // FIXME TEMP DELETE
    	GameParameters.constructActivityCounter = 0;
//    	GameParameters.constructActivityCounterSleep = 0;
//        GameParameters.droidSurfaceViewCounter = 0;
    	GameParameters.gameCounter = 0;
//    	GameParameters.gameTimerCounter = 0;
    	GameParameters.gameCounterTouchDownMove = 0;
    	GameParameters.gameCounterTouchUp = 0;
//    	GameParameters.gameCounterTouchUpMaxTimes = 0;
    	GameParameters.droidBottomComponentCounter = 0;
    	GameParameters.renderCounter = 0;
    	GameParameters.constructTouchSleepCounter = 0;
    	GameParameters.gameThreadSleepCounter = 0;
    	GameParameters.rendererWaitDrawCompleteCounter = 0;
    	GameParameters.drawQueueWaitCounter = 0;
    	GameParameters.drawQueueHudWaitCounter = 0;
        
    	mLevelSelected = 0;
//        mLevelRow = 0;
//        mLevelIndex = 0;
    	mLevelNumActive = 0;
//        mLevelCompleted = 0;
        
        mWeaponActive = 101;	// Default to droid_weapon_laser_std
        mWeaponInventory = 0;	// Default to no weapon in inventory
        
        mDifficultyLevel = 1;
        
        mPrefsEditor = prefs.edit();
//        mLevelRow = prefs.getInt(PREFERENCE_LEVEL_ROW, 0);
//        mLevelIndex = prefs.getInt(PREFERENCE_LEVEL_INDEX, 0);
        mLevelNumActive = prefs.getInt(PREFERENCE_LEVEL_NUM_ACTIVE, 0);
//        mLevelCompleted = prefs.getInt(PREFERENCE_LEVEL_COMPLETED, 0);
////        int completed = prefs.getInt(PREFERENCE_LEVEL_COMPLETED, 0);
        
        Intent levelSelectIntent = getIntent();
        mLevelSelected = levelSelectIntent.getIntExtra("LevelSelected", 0);
//        int levelSelected = levelSelectIntent.getIntExtra("LevelSelected", 0);
        
        mTotalKillCollectPoints = prefs.getInt(PREFERENCE_TOTAL_KILL_COLLECT_POINTS, 0);
        mTotalCollectNum = prefs.getInt(PREFERENCE_TOTAL_COLLECT_NUM, 0);
        
        mWeaponActive = prefs.getInt(PREFERENCE_LEVEL_WEAPON_ACTIVE, 101);
        mWeaponInventory = prefs.getInt(PREFERENCE_LEVEL_WEAPON_INVENTORY, 0);
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "ConstructActivity onCreate() mLevelNumActive = " + mLevelNumActive);
            Log.i("GameFlow", "ConstructActivity onCreate() LevelSelectActivity getIntent() LevelSelected = " + mLevelSelected);
            Log.i("GameFlow", "ConstructActivity onCreate() mTotalKillCollectPoints, mTotalCollectNum = " + 
            		mTotalKillCollectPoints + ", " + mTotalCollectNum);
            Log.i("GameFlow", "ConstructActivity onCreate() mWeaponActive, mWeaponInventory = " + 
            		mWeaponActive + ", " + mWeaponInventory);
        }
        
        mGame.setWeapons(mWeaponActive, mWeaponInventory);
        
        mSetDifficulty = prefs.getString(PREFERENCE_SET_DIFFICULTY, "Efficient (Medium)");
        if (mSetDifficulty.equals(PREFERENCE_DIFFICULTY_EASY)) {
        	mDifficultyLevel = 0;
        } else if (mSetDifficulty.equals(PREFERENCE_DIFFICULTY_HARD)) {
        	mDifficultyLevel = 2;
        }
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "ConstructActivity onCreate() mSetDifficulty, mDifficultyLevel = " + mSetDifficulty + ", " +
            		mDifficultyLevel);
        }
        
//        showLoadingMessage();
////        ProgressDialog progressDialog = new ProgressDialog(ConstructActivity.this);
//////        progressDialog.setContentView(R.layout.main);
////        progressDialog.setTitle("Loading. Please wait...");
////        progressDialog.setMessage("Loading. Please wait...");
////        progressDialog.setCancelable(true);
////        progressDialog.show();        
//////        ProgressDialog progressDialog = ProgressDialog.show(ConstructActivity.this, "", "Loading. Please wait...", true);
        
        mGame.setDifficultyLevel(mDifficultyLevel);
        
        mGame.bootstrap(this, mViewWidth, mViewHeight, mGameWidth, mGameHeight, density);
//        mGame.bootstrap(this, mViewWidth, mViewHeight, mGameWidth, mGameHeight);
////        mGame.bootstrap(this, viewWidth, viewHeight, gameWidth, gameHeight);
        
        mGame.setSurfaceView(mGLSurfaceView);
        
        mGLSurfaceView.setRenderer(mGame.getRenderer());
        // FIXME 12/15/12 300pm ADDED
        mGLSurfaceView.setRenderMode(DroidGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        // FIXME END 12/15/12 300pm ADDED
        
//        mGLSurfaceView.setGame(mGame);
        
//        setContentView(R.layout.main);
        
        final boolean musicEnabled = prefs.getBoolean(PREFERENCE_MUSIC_ENABLED, true);
        mGame.setMusicEnabled(musicEnabled);
        final boolean soundEnabled = prefs.getBoolean(PREFERENCE_SOUND_ENABLED, true);
        mGame.setSoundEnabled(soundEnabled);
        
        // This activity uses the media stream.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        mGame.setPoints(mTotalKillCollectPoints, mTotalCollectNum);
        
        if (GameParameters.debug) {
    		Log.i("GameFlow", "ConstructActivity onCreate() LevelTree.isLoaded() = " + LevelTree.isLoaded());	
        }
        
        /* Android activity lifecycle rules make it possible for this activity to be created
         * and come to the foreground without the MainMenu Activity ever running, so in that
         * case we need to make sure that this static data is valid. */
        if (!LevelTree.isLoaded()) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "ConstructActivity onCreate() !LevelTree.isLoaded()");	
            }
    		
    		LevelTree.loadLevelTree(R.xml.level_tree, this);
    		
//        } else {
//    		Log.i("GameFlow", "ConstructActivity onCreate() LevelTree.isLoaded() therefore clearLevelTree()");
//    		
//        	LevelTree.clearLevelTree();
//        	
//        	LevelTree.loadLevelTree(R.xml.level_tree, this);
        }
        
//        if (!LevelTree.levelIsValid(mLevelRow, mLevelIndex)) {
//        	// bad data?  Let's try to recover.
//        	
//            if (GameParameters.debug) {
//        		Log.i("GameFlow", "ConstructActivity onCreate() !LevelTree.levelIsValid()");
//            }
//        	
//        	// is the row valid?
//        	if (LevelTree.rowIsValid(mLevelRow)) {
//        		// In that case, just start the row over.
//        		mLevelIndex = 0;
//        		mLevelCompleted = 0;
////        		completed = 0;
//        	} else if (LevelTree.rowIsValid(mLevelRow - 1)) {
//        		// If not, try to back up a row.
//        		mLevelRow--;
//        		mLevelIndex = 0;
//        		mLevelCompleted = 0;
////        		completed = 0;
//        	}
//        	
//        	if (!LevelTree.levelIsValid(mLevelRow, mLevelIndex)) {
//	        	// if all else fails, start the game over.
//	        	mLevelRow = 0;
//	        	mLevelIndex = 0;
//        		mLevelCompleted = 0;
////	        	completed = 0;
//        	}
//        }
        
//        LevelTree.updateCompletedState(mLevelRow, completed);
        
        if (mLevelNumActive == 0) {
//        if (mLevelCompleted == 0) {
////        if (mLevelRow == 0) {
        	// Intro
            mGame.setLevelRow(0);
            mGame.setPendingLevel(LevelTree.get(0, 0));
        } else {
            mGame.setLevelRow(mLevelSelected);
            mGame.setPendingLevel(LevelTree.get(mLevelSelected, 0));
//            mGame.setLevelRow(levelSelected);
//            mGame.setPendingLevel(LevelTree.get(levelSelected, 0));
////            mGame.setPendingLevel(LevelTree.get(levelSelected, mLevelIndex));	
        }
        
        // Display Level Loading Splashscreen
        switch (mLevelSelected) {
//        switch (levelSelected) {
////        switch (mLevelRow) {
        case 0:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level00_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 1:
        	showLevelLoadingMessage();
        	break;
        	
        case 2:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level02_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 3:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level03_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 4:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level04_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 5:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level05_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 6:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level06_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 7:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level07_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 8:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level08_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        case 9:
        	((ImageView)mLevelLoadingMessage).setImageDrawable(getResources().getDrawable(R.drawable.level09_splashscreen));
        	showLevelLoadingMessage();
        	break;
        	
        default:
        	// ignore
        	break;
        }
//        showLevelLoadingMessage();
////        showLoadingMessage();
//////        showWaitMessage();
////////        mGame.setLevelRow(mLevelRow);
////////        
////////        mGame.setPendingLevel(LevelTree.get(mLevelRow, mLevelIndex));
//////////        if (LevelTree.get(mLevelRow, mLevelIndex).showWaitMessage) {
//////////    		showWaitMessage();
//////////        } else {
//////////    		hideWaitMessage();
//////////        }
        
//        mGame.setLevelStartTime();

//        // This activity uses the media stream.
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        mSessionId = prefs.getLong(PREFERENCE_SESSION_ID, System.currentTimeMillis());
        
        mMovePointerId = -1;
        mFirePointerId = -1;
        mWeapon1PointerId = -1;
        mWeapon2PointerId = -1;
        mViewangleBarPointerId = -1;
        
//        hideLoadingMessage();
////        progressDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        if (GameParameters.debug) {
    		Log.i("GameFlow", "ConstructActivity onPause()");	
        }
		
//		finish();
        
        if (mGame.isRunning()) {
            if (GameParameters.debug) {
            	Log.i("GameFlow", "ConstructActivity onPause() mGame.isRunning() == TRUE");
            }
        	
        	// FIXME 12/15/12 1110am TEMP DISABLE. RE-ENABLE.
//    		showPauseMessage();
////          hidePauseMessage();
        	// FIXME END 12/15/12 1110am TEMP DISABLE. RE-ENABLE.
    		
    		if (!mGame.isPaused()) {
                if (GameParameters.debug) {
        			Log.i("GameFlow", "ConstructActivity onPause() mGame.isPaused() == FALSE");	
                }
    			
    			mGame.onPause();
    			
    			// FIXME 12/10/12 Moved to outside of if(!mGame.isPaused())
//                mGLSurfaceView.onPause();
    			// FIXME END 12/10/12 Moved to outside of if(!mGame.isPaused())
                
    			// FIXME 12/12/12 Moved to outside of if(!mGame.isPaused())
                // FIXME 12/2/12 TEMP DELETE. RE-ENABLE.
//                mGame.getRenderer().onPause();	// hack!
                // FIXME END 12/2/12 TEMP DELETE.
    			// FIXME END 12/12/12 Moved to outside of if(!mGame.isPaused())
    		} else {
                if (GameParameters.debug) {
        			Log.i("GameFlow", "ConstructActivity onPause() mGame.isPaused() == TRUE");	
                }
    		}
    		
    		// FIXME 12/11/12 Reverse order
    		mGame.onSurfaceLost();
    		
    		// FIXME 12/15/12 1110am DISABLE
    		// FIXME 12/15/12 RE-ENABLE
            // FIXME 12/13/12 RE-DELETE
			// FIXME 12/12/12 Moved to outside of if(!mGame.isPaused())
//            mGame.getRenderer().onPause();	// hack!
			// FIXME END 12/12/12 Moved to outside of if(!mGame.isPaused())
            // FIXME END 12/13/12 RE-DELETE
    		// FIXME END 12/15/12 RE-ENABLE
    		// FIXME END 12/15/12 1110am DISABLE
    		
			// FIXME 12/10/12 Moved to outside of if(!mGame.isPaused())
            mGLSurfaceView.onPause();
			// FIXME END 12/10/12 Moved to outside of if(!mGame.isPaused())
    		
//    		mGame.onSurfaceLost();
    		// FIXME END 12/11/12 Reverse order
    		
        } else {
            if (GameParameters.debug) {
            	Log.i("GameFlow", "ConstructActivity onPause() mGame.isRunning() == FALSE");	
            }
        }
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	
    	// FIXME 12/15/12 1110am. DELETE. Similar to GLSurfaceView override onPause(), does this Log.i() cause crash?
//		Log.i("GameFlow", "ConstructActivity onStop()");
    	// FIXME 12/15/12 1110am. DELETE. Similar to GLSurfaceView override onPause(), does this Log.i() cause crash?
		
		// FIXME 12/4/12 RE-TEST WHAT RESIDES IN MEMORY
//		// FIXME 12/2/12 RE-ADDED
//		finish();
//		// FIXME END 12/2/12 RE-ADDED
		
		// FIXME 12/10/12 MOVED TO onPause()
//		// FIXME 12/2/12 ADDED
//		if (mGame.isRunning()) {
//			mGame.onSurfaceLost();
//		}
//		// FIXME END 12/2/12 ADDED
		// FIXME END 12/10/12 MOVED TO onPause()
        
//		// FIXME 12/2/12 TEMP DELETE. RE-ENABLE.
//////		finish();
////		
////        if (mGame.isRunning()) {
////        	Log.i("GameFlow", "ConstructActivity onStop() mGame.isRunning() == TRUE");
////        	
////    		showPauseMessage();
//////          hidePauseMessage();
////    		
////    		if (!mGame.isPauseStopped()) {
//////    		if (!mGame.isPaused()) {
////    			Log.i("GameFlow", "ConstructActivity onStop() mGame.isPauseStopped() == FALSE");
////    			
////    			mGame.onPauseStopped();
//////    			mGame.onPause();
////                mGLSurfaceView.onPause();
////                mGame.getRenderer().onPause();	// hack!
////    		} else {
////    			Log.i("GameFlow", "ConstructActivity onStop() mGame.isPauseStopped() == TRUE");
////    		}
////        } else {
////        	Log.i("GameFlow", "ConstructActivity onStop() mGame.isRunning() == FALSE");
////        }
//		// FIXME END 12/2/12 TEMP DELETE
		// FIXME END 12/4/12 RE-TEST
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (GameParameters.debug) {
    		Log.i("GameFlow", "ConstructActivity onResume()");	
        }
        
//        // Preferences may have changed while we were paused.
//        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        
        if (mGame.isRunning()) {
            if (GameParameters.debug) {
            	Log.i("GameFlow", "ConstructActivity onResume() mGame.isRunning() == TRUE");	
            }
        	
            mGLSurfaceView.onResume();
//        	mGame.getRenderer().onResume();
            
            // FIXME 12/2/12 ADDED
            ConstructActivity.sContext = getApplicationContext();
            // FIXME END 12/2/12 ADDED
            
            // FIXME 12/15/12 1110am. TEMP DISABLE. RE-ENABLE.
            // FIXME 12/14/12 TEMP ADD HERE. MOVE TO if(mGame.isPaused())
//            mGame.onResume(this, true);
            // FIXME END 12/14/12 TEMP ADD HERE. MOVE TO if(mGame.isPaused())
            // FIXME END 12/15/12 1110am. TEMP DISABLE. RE-ENABLE.
        	
        	if (mGame.isPaused()) {
        	// FIXME 12/2/12 TEMP DELETE. RE-ENABLE.
//        	if (mGame.isPauseStopped()) {
//        		Log.i("GameFlow", "ConstructActivity onResume() mGame.isPauseStopped() == TRUE");
//        		
//        		mGame.setPauseStopped(false);
//        		
//        		// Go to showPauseMessage and await user unpause
////                mGLSurfaceView.onResume();
////                mGame.onResume(this, false);
//                
//        	} else if (mGame.isPaused()) {
//        		Log.i("GameFlow", "ConstructActivity onResume() mGame.isPauseStopped() == FALSE; mGame.isPaused() == TRUE");
        	// FIXME END 12/2/12 TEMP DELETE.
                if (GameParameters.debug) {
            		Log.i("GameFlow", "ConstructActivity onResume() mGame.isPaused() == TRUE");	
                }
        		
//                // FIXME 12/2/12 ADDED
//                ConstructActivity.sContext = getApplicationContext();
//                // FIXME END 12/2/12 ADDED
            	
//                mGLSurfaceView.onResume();
////            	mGame.getRenderer().onResume();

        		// FIXME 12/15/12 420pm Changed back to manualResume pass-thru = TRUE due to CRASH
        		// FIXME 12/15/12 415pm Changed to manualResume pass-thru = FALSE
        		// FIXME 12/15/12 RE-ENABLED
        		// FIXME 12/12/12 Moved to Game onSurface Ready
                mGame.onResume(this, true);
//                mGame.onResume(this, false);
        		// FIXME END 12/12/12 Moved to Game onSurface Ready
        		// FIXME END 12/15/12 RE-ENABLED
        		// FIXME END 12/15/12 415pm Changed to manualResume pass-thru = FALSE
        		// FIXME END 12/15/12 420pm Changed back to manualResume pass-thru = TRUE due to CRASH
                
        		// FIXME 12/16/12 920pm MOVED
        		// FIXME 12/15/12 500pm RE-ENABLED. Doesn't appear related to Renderer lock issue.
        		// FIXME 12/15/12 455pm DELETED. Cannot show until Renderer Re-started?
        		// FIXME 12/15/12 415pm RE-ENABLE but for showPauseMessage() since coming to Foregound still Paused
        		// FIXME 12/8/12 TEMP DELETE. RE-ENABLE.
        		showPauseMessage();
////            	hidePauseMessage();
            	// FIXME END 12/8/12 TEMP DELETE.
        		// FIXME END 12/15/12 415pm RE-ENABLE but for showPauseMessage() since coming to Foregound still Paused
        		// FIXME END 12/15/12 455pm DELETED. Cannot show until Renderer Re-started?
        		// FIXME END 12/15/12 500pm RE-ENABLED. Doesn't appear related to Renderer lock issue.
        		// FIXME END 12/16/12 920pm MOVED
                
//              // FIXME 12/8/12 ADD
//              mGame.onResumeFromBackground();
//              // FIXME END 12/8/12 ADD
                
    		} else {
//    			Log.i("GameFlow", "ConstructActivity onResume() mGame.isPauseStopped && mGame.isPaused() == FALSE");
                if (GameParameters.debug) {
        			Log.i("GameFlow", "ConstructActivity onResume() mGame.isPaused() == FALSE");	
                }
    		}
        } else {
            if (GameParameters.debug) {
            	Log.i("GameFlow", "ConstructActivity onResume() mGame.isRunning() == FALSE");	
            }
        }
        
//        final boolean musicEnabled = prefs.getBoolean(PREFERENCE_MUSIC_ENABLED, true);
//        mGame.setMusicEnabled(musicEnabled);
//        final boolean soundEnabled = prefs.getBoolean(PREFERENCE_SOUND_ENABLED, true);
//        mGame.setSoundEnabled(soundEnabled);
    }
    
    @Override
    protected void onDestroy() {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "ConstructActivity onDestroy()");	
        }
		
		// FIXME Hack. Fix logic so that gamePause only set to true when Activity loses focus.
		GameParameters.gamePause = false;
		
		// FIXME 12/2/12 ADDED
		ConstructActivity.sContext = null;
		// FIXME END 12/2/12 ADDED
        
        mGame.stop();
        
        super.onDestroy(); 
        
        // FIXME 12/4/12 TEMP. RE-ENABLE.
//        mChecker.onDestroy();
        // FIXME END 12/4/12 TEMP.
    }
    
////    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//    	// FIXME TEMP. DELETE.
//    	GameParameters.constructActivityCounter++;
//    	
//    	if (!mGame.isPaused() && mGame.isRunning()) {
//    		GameParameters.constructActivityCounterNotPaused++;
//    		
//    		Log.i("TouchEvent", "ConstructActivity onTouchEvent() event = " + event);
//    		
//    		mGame.onTouchEvent(event);
//	    	
//	        final long time = System.currentTimeMillis();
//	        final long timeDelta = time - mLastTouchTime;
//	        
//	        if (timeDelta < 32) {
////	        if (event.getAction() == MotionEvent.ACTION_MOVE && timeDelta < 32) {
//	        	GameParameters.constructActivityCounterSleep++;
//	        	
//		        // Sleep so that the main thread doesn't get flooded with UI events.
//		        try {
//		        	/* XXX Appears 32 is optimal, however continue to test whether 32 vs other value.
//		        	 * Note: 1000 was too high- increased some delays due to long sleep. 
//		        	 * Not often triggered at < 16, so probably 16 wouldn't sleep often and could impact touch overload. */
//		        	Thread.sleep(32 - timeDelta);
////		        	Thread.sleep(128);
//////		        	Thread.sleep(64);
////////		        	Thread.sleep(32);
//////////		        	Thread.sleep(32 - timeDelta);
////////////		        	Thread.sleep(64 - timeDelta);
////////////		        	Thread.sleep(320 - timeDelta);
////////////		        	Thread.sleep(1000 - timeDelta);
////////////		            Thread.sleep(32);
//		        	
//		        } catch (InterruptedException e) {
//		            // No big deal if this sleep is interrupted.
//		        }
//		        
//		        mGame.getRenderer().waitDrawingComplete();
//	        }
//	        
//	        mLastTouchTime = time;
//    	}
//        return true;
//    }
    
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//    	// FIXME TEMP. DELETE.
//    	GameParameters.constructActivityCounter++;
//    	
//    	if (!mGame.isPaused() && mGame.isRunning()) {
//    		GameParameters.constructActivityCounterNotPaused++;
//    		
//    		Log.i("TouchEvent", "ConstructActivity dispatchTouchEvent() event = " + event);
//    		
//    		mGame.onTouchEvent(event);
//    		
//	        final long time = System.currentTimeMillis();
//	        final long timeDelta = time - mLastTouchTime;
//	        
//	        if (timeDelta < 32) {
//	        	GameParameters.constructActivityCounterSleep++;
//	        	
//	        	// Sleep so that the main thread doesn't get flooded with UI events.
//	            try {
//	            	Thread.sleep(32 - timeDelta);
//	            } catch (InterruptedException e) {
//	                // No big deal if this sleep is interrupted.
//	            }
//	            
//	            mGame.getRenderer().waitDrawingComplete();
//	        }
//
//	        mLastTouchTime = time;
//    	}
//    	
//    	return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// FIXME TEMP. DELETE.
    	GameParameters.constructActivityCounter++;
    	
    	// FIXME 12/19/12 100am DELETED
    	// FIXME 12/18/12 1020pm ADDED
//		final int action = event.getAction();
//		final int mask = MotionEvent.ACTION_MASK;
    	// FIXME END 12/18/12 1020pm ADDED
    	// FIXME END 12/19/12 100am DELETED
    	
    	// FIXME 12/28/12 745pm ADDED
    	mEvent = MotionEvent.obtainNoHistory(event);
//    	event.recycle();
    	// FIXME END 12/28/12 745pm ADDED
    	
    	// FIXME 12/28/12 745pm MODIFIED
    	// FIXME 12/19/12 100am Changed back to original code
		// FIXME 12/19/12 1240am MODIFIED
    	// FIXME 12/18/12 1145pm TEMP DISABLED. RE-ENABLE.
    	// FIXME 12/18/12 655am MODIFIED
    	mGame.onTouchEvent(mEvent);
////		mGame.onTouchEvent(action, mask);
//    	mGame.onTouchEvent(event);
    	// FIXME END 12/18/12 1145pm TEMP DISABLED. RE-ENABLE.
		// FIXME END 12/19/12/1240am MODIFIED
    	// FIXME END 12/19/12 100am Changed back to original code
    	// FIXME END 12/28/12 745pm MODIFIED
    	
    	// Sleep so that the main thread doesn't get flooded with UI events.
        try {
        	GameParameters.constructTouchSleepCounter++;
        	
        	Thread.sleep(32);
        	
        } catch (InterruptedException e) {
            // No big deal if this sleep is interrupted.
        }
        
    	// FIXME 12/19/12 100am Changed back to original code
        // FIXME 12/18/12 1020pm DELETED
        mGame.getRenderer().waitDrawingComplete();
        // FIXME END 12/18/12 1020pm DELETED
    	// FIXME END 12/19/12 100am Changed back to original code
    	
//    	if (mGame.isRunning() && !mGame.isPaused()) {    		
////    		Log.i("TouchEvent", "ConstructActivity onTouchEvent() event = " + event);
//    		
//    		mGame.onTouchEvent(event);
//    		
////    		final int action = event.getAction();
//    		
////    		InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
//    		
//    		/* XXX TEST CODE 11/29/12 for simple touchX[i],touchY[i] method. However, couldn't allow for 
//      		 * continued Move,Fire Press until ACTION_UP Event. It could only detect and hold within getToucheWithinRegionXX() */
////    		// FIXME NEW CODE
////    		int pointerCount = event.getPointerCount();
////    		
////    		// 3 Multitouch Pointers Max allowed (Move, Fire, Weapon). Additional Pointers disregarded.
////    		if (pointerCount > MAX_POINTER_COUNT) {
////    			pointerCount = MAX_POINTER_COUNT;
////    		}
////    		
////    		// FIXME If mTouchX and mTouchY Arrays too heavy, can change to mTouchX1, etc, use if statements
////    		// and overload mGame.touchInput(mTouchX1, mTouchY1), mGame.touchInput(mTouchX1, mTouchX2, mTouchY1, mTouchY2), etc
////    		for (int i = 0; i < pointerCount; i++) {
////    			mTouchX[i] = event.getX(i);
////    			mTouchY[i] = mViewHeight - event.getY(i);
////    		}
////    		
////    		mGame.touchInput(mTouchX, mTouchY, pointerCount);
//    		// XXX END TEST CODE 11/29/12
//    		
////    		int pointerCount = event.getPointerCount();
////    		if (pointerCount > 1) {
////    			GameParameters.gameCounterTouchDownMove++;
////    		} else if (GameParameters.gameCounterTouchDownMove > 0) {
////    			GameParameters.gameCounterTouchUp++;
////    		}
//    		
//        	// Sleep so that the main thread doesn't get flooded with UI events.
//            try {
//            	Thread.sleep(32);
//            	
//            } catch (InterruptedException e) {
//                // No big deal if this sleep is interrupted.
//            }
//            
//            mGame.getRenderer().waitDrawingComplete();
//            
////    		final long time = System.currentTimeMillis();
//////	        final long timeDelta = time - mLastTouchTime;
////	        
////    		if ((time - mLastTouchTime) < 32) {
////	        	// Sleep so that the main thread doesn't get flooded with UI events.
////	            try {
////	            	Thread.sleep(32);
//////	            	Thread.sleep(32 - timeDelta);
////	            	
////	            } catch (InterruptedException e) {
////	                // No big deal if this sleep is interrupted.
////	            }
////		        
////	            // FIXME TEST ONLY. RE-ENABLE.
//////		        mGame.getRenderer().waitDrawingComplete();
////    		}
////    		
////    		mLastTouchTime = time;
//    		
////    		// FIXME TEMP ONLY FOR COUNT. DELETE.
////    		final int action = event.getAction();
////    		switch (action & MotionEvent.ACTION_MASK) {
////    		case MotionEvent.ACTION_UP:
////    			GameParameters.gameCounterTouchUp++;
////    			break;
////    		
////    		case MotionEvent.ACTION_DOWN:
////    			GameParameters.gameCounterTouchDownMove++;
////        		break;
////    		
////    		case MotionEvent.ACTION_MOVE:
////    			GameParameters.gameCounterTouchDownMove++;
////            	break;
////    		}
////    		// END DELETE
//            
//            // FIXME ORIGINAL REPLICA CODE
////        	mGame.onTouchEvent(event);
////        	
////            final long time = System.currentTimeMillis();
////            if (event.getAction() == MotionEvent.ACTION_MOVE && time - mLastTouchTime < 32) {
////    	        // Sleep so that the main thread doesn't get flooded with UI events.
////    	        try {
////    	            Thread.sleep(32);
////    	        } catch (InterruptedException e) {
////    	            // No big deal if this sleep is interrupted.
////    	        }
////    	        mGame.getRenderer().waitDrawingComplete();
////            }
////            mLastTouchTime = time;
////            return true;
//            // END ORIGINAL REPLICA CODE
//    		
////    		float down1X;
////    		float down2X;
////    		float down1Y;
////    		float down2Y;
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
////        		float down1X = event.getX();
////        		float down1Y = mViewHeight - event.getY();
//////        		float downX = event.getX();
//////        		float downY = mViewHeight - event.getY();
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
////    		mGame.onTouchEvent(event);
//    		
////	        final long time = System.currentTimeMillis();
////	        final long timeDelta = time - mLastTouchTime;
//	        
////	        if (timeDelta < 32) {
//////	        	GameParameters.constructActivityCounterSleep++;
////	        	
////	        	// Sleep so that the main thread doesn't get flooded with UI events.
////	            try {
////	            	Thread.sleep(32);
//////	            	Thread.sleep(32 - timeDelta);
//////	            	Thread.sleep(200);
////	            } catch (InterruptedException e) {
////	                // No big deal if this sleep is interrupted.
////	            }
////	            
//////	            mGame.getRenderer().waitDrawingComplete();
////	        }
//
////	        mLastTouchTime = time;
//	        
////            synchronized(GameParameters.syncObject) {
////                try {
////                	GameParameters.syncObject.wait(500);
////                } catch (InterruptedException e) {
////                    // No big deal if this wait is interrupted.
////                }
////            }
//	        
////            synchronized(mObject) {
////                try {
////                	mObject.wait(200);
//////                	mObject.wait(128);
////                } catch (InterruptedException e) {
////                    // No big deal if this wait is interrupted.
////                }
////            }
//	    	
////	        final long time = System.currentTimeMillis();
////	        final long timeDelta = time - mLastTouchTime;
////	        
////	        if (timeDelta < 32) {
//////	        if (event.getAction() == MotionEvent.ACTION_MOVE && timeDelta < 32) {
////	        	GameParameters.constructActivityCounterSleep++;
////	        	
////		        // Sleep so that the main thread doesn't get flooded with UI events.
////		        try {
////		        	/* XXX Appears 32 is optimal, however continue to test whether 32 vs other value.
////		        	 * Note: 1000 was too high- increased some delays due to long sleep. 
////		        	 * Not often triggered at < 16, so probably 16 wouldn't sleep often and could impact touch overload. */
////		        	Thread.sleep(32 - timeDelta);
//////		        	Thread.sleep(128);
////////		        	Thread.sleep(64);
//////////		        	Thread.sleep(32);
////////////		        	Thread.sleep(32 - timeDelta);
//////////////		        	Thread.sleep(64 - timeDelta);
//////////////		        	Thread.sleep(320 - timeDelta);
//////////////		        	Thread.sleep(1000 - timeDelta);
//////////////		            Thread.sleep(32);
////		        	
////		        } catch (InterruptedException e) {
////		            // No big deal if this sleep is interrupted.
////		        }
////		        
////		        mGame.getRenderer().waitDrawingComplete();
////	        }
////	        
////	        mLastTouchTime = time;
//            
//    	}
    	// FIXME END 12/18/12 655am MODIFIED
    	
    	return true;
//    	return false;
//    	return super.onTouchEvent(event);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity onKeyDown()");	
        }
    	
    	boolean result = false;
//    	boolean result = true;
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		result = true;
    		if (mGame.isPaused()) {
    			// Ignore
    		} else if (mGame.isRunning()) {
//    		} else {
    			savePoints();
    			
    			showDialog(RETURN_MAINMENU_DIALOG);
//    			final long time = System.currentTimeMillis();
//        		if ((time - mLastRollTime) > ROLL_TO_FACE_BUTTON_DELAY) {
//        			showDialog(RETURN_MAINMENU_DIALOG);
////        			showDialog(QUIT_GAME_DIALOG);
////        			result = true;
//        		}
    		}
    	} else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (GameParameters.debug) {
            	Log.i("GameFlow", "ConstructActivity onKeyDown() KEYCODE_MENU");	
            }
        	
    		result = true;
    		if (mGame.isPaused() && !mGame.isGamePause()) {
                if (GameParameters.debug) {
        			Log.i("GameFlow", "ConstructActivity onKeyDown() KEYCODE_MENU mGame.isPaused() = TRUE");	
                }
    			
    			hidePauseMessage();

//    			mGame.setPauseStopped(false);

    			mGame.onResume(this, true);
    			
    		} else if (mGame.isRunning() && !mGame.isGamePause()) {
//    		} else {
                if (GameParameters.debug) {
        			Log.i("GameFlow", "ConstructActivity onKeyDown() KEYCODE_MENU mGame.isRunning() = TRUE; mGame.isPaused() = FALSE");	
                }
    			
    			showPauseMessage();
    			mGame.onPause();
//    			final long time = System.currentTimeMillis();
//    	        if ((time - mLastRollTime) > ROLL_TO_FACE_BUTTON_DELAY) {
//    	        	showPauseMessage();
//    	        	mGame.onPause();
//    	        }
    		}
//    	} else {
//		    result = mGame.onKeyDownEvent(keyCode);
//		    // Sleep so that the main thread doesn't get flooded with UI events.
//		    try {
//		        Thread.sleep(4);
//		    } catch (InterruptedException e) {
//		        // No big deal if this sleep is interrupted.
//		    }
    	}
        return result;
    }
     
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	boolean result = false;
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		result = true;
    	} else if (keyCode == KeyEvent.KEYCODE_MENU){ 
//	        if (VERSION < 0) {
//	        	result = false;	// Allow the debug menu to come up in debug mode.
//	        }
//    	} else {
//    		result = mGame.onKeyUpEvent(keyCode);
//	        // Sleep so that the main thread doesn't get flooded with UI events.
//	        try {
//	            Thread.sleep(4);
//	        } catch (InterruptedException e) {
//	            // No big deal if this sleep is interrupted.
//	        }
    	}
        return result;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity onCreateOptionsMenu()");	
        }
        
        boolean handled = false;
        // Only allow the debug menu in development versions.
        if (VERSION < 0) {
	        menu.add(0, CHANGE_LEVEL_ID, 0, R.string.change_level);
	        handled = true;
        }
        
        return handled;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity onMenuItemSelected()");	
        }
    	
        Intent i;
        switch(item.getItemId()) {
        case CHANGE_LEVEL_ID:
            i = new Intent(this, LevelSelectActivity.class);
//            i = new Intent(this, LevelSelectActivityOld.class);
            startActivityForResult(i, ACTIVITY_CHANGE_LEVELS);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "ConstructActivity onActivityResult() requestCode, resultCode = " + requestCode + ", " + resultCode);	
        }
        
        GameObjectManager manager;
        Type weaponActiveType;
        Type weaponInventoryType;
        
        if (requestCode == ACTIVITY_CHANGE_LEVELS) {
	        if (resultCode == RESULT_OK) {
	        	int levelRow = intent.getExtras().getInt("row");
//	            mLevelRow = intent.getExtras().getInt("row");
//	            mLevelIndex = intent.getExtras().getInt("index");
	        	
	            if (GameParameters.debug) {
	                Log.i("GameFlow", "ConstructActivity onActivityResult() levelRow = " + levelRow);	
	            }
	            
//	            HudSystem hud = ObjectRegistry.sSystemRegistry.hudSystem;
//	            mTotalKillCollectPoints = hud.totalKillCollectPoints;
//	            mTotalCollectNum = hud.totalCollectNum;
	            
	            manager = ObjectRegistry.sSystemRegistry.gameObjectManager;
	            weaponActiveType = manager.getWeapon1GameObjectType();
	            
	            if(weaponActiveType != null) {
	                switch(weaponActiveType) {
	                case DROID_WEAPON_LASER_STD:
	                	mWeaponActive = 101;
	                	break;
	                	
	                case DROID_WEAPON_LASER_PULSE:
	                	mWeaponActive = 102;
	                	break;
	                	
	                case DROID_WEAPON_LASER_EMP:
	                	mWeaponActive = 103;
	                	break;
	                	
	                case DROID_WEAPON_LASER_GRENADE:
	                	mWeaponActive = 104;
	                	break;
	                	
	                case DROID_WEAPON_LASER_ROCKET:
	                	mWeaponActive = 105;
	                	break;
	                	
	                case INVALID:
	                	mWeaponActive = 0;
	                	break;
	                	
	                default:
	                	mWeaponActive = 101;
	                	break;	
	                }
	            } else {
	         	   mWeaponActive = 101;
	            }

	            
	            weaponInventoryType = manager.getWeapon2GameObjectType();
	            
	            if(weaponInventoryType != null) {
	                switch(weaponInventoryType) {
	                case DROID_WEAPON_LASER_STD:
	                	mWeaponInventory = 101;
	                	break;
	                	
	                case DROID_WEAPON_LASER_PULSE:
	                	mWeaponInventory = 102;
	                	break;
	                	
	                case DROID_WEAPON_LASER_EMP:
	                	mWeaponInventory = 103;
	                	break;
	                	
	                case DROID_WEAPON_LASER_GRENADE:
	                	mWeaponInventory = 104;
	                	break;
	                	
	                case DROID_WEAPON_LASER_ROCKET:
	                	mWeaponInventory = 105;
	                	break;
	                	
	                case INVALID:
	                	mWeaponInventory = 0;
	                	break;
	                	
	                default:
	                	mWeaponInventory = 0;
	                	break;	
	                }
	            } else {
	         	   mWeaponInventory = 0;
	            }
	            
	            saveGame();
	            
	            mGame.setPendingLevel(LevelTree.get(levelRow, 0));  
//	            mGame.setPendingLevel(LevelTree.get(mLevelRow, mLevelIndex));    
//	            if (LevelTree.get(mLevelRow, mLevelIndex).showWaitMessage) {
//            		showWaitMessage();
//	            } else {
//            		hideWaitMessage();
//	            }
	        }
//        } else if (requestCode == ACTIVITY_ANIMATION_PLAYER) {
//        	// on finishing animation playback, force a level change.
//        	onGameFlowEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0);
        }
    }
    
    /*
     *  When the game thread needs to stop its own execution (to go to a new level, or restart the
     *  current level), it registers a runnable on the main thread which orders the action via this
     *  function.
     */
    public void onGameFlowEvent(int eventCode, int index) {
        if (GameParameters.debug) {
    		Log.i("GameFlow", "ConstructActivity onGameFlowEvent()");	
        }
        
        GameObjectManager manager;
        Type weaponActiveType;
        Type weaponInventoryType;
		
       switch (eventCode) {
       // FIXME RE-ENABLE AnimationPlayerActivity
//       case GameFlowEvent.EVENT_SHOW_ANIMATION:
//       		i = new Intent(this, AnimationPlayerActivity.class);
//       		i.putExtra("animation", index);
//       		startActivityForResult(i, ACTIVITY_ANIMATION_PLAYER);
//       		break;
       
       case GameFlowEvent.EVENT_START_INTRO:
           if (GameParameters.debug) {
        	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_START_INTRO");   
           }
   		
    	   hideLevelLoadingMessage();
//    	   hideLoadingMessage();
    	   break;
       
       
       case GameFlowEvent.EVENT_PAUSE_GAME:
           if (GameParameters.debug) {
        	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_PAUSE_GAME");   
           }
           
           savePoints();
   		
    	   mGame.onPause();
    	   break;
       
       case GameFlowEvent.EVENT_RESTART_LEVEL:
           if (GameParameters.debug) {
        	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_RESTART_LEVEL");   
           }
           
           mWeaponActive = 101;
           mWeaponInventory = 0;
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_RESTART_LEVEL saveGame()");   
           }
           
           saveGame();
//         savePoints();
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_RESTART_LEVEL mGame.stop()");   
           }
           
           // FIXME 1/1/13 825am ADDED
           mGame.stop();
           // FIXME END 1/1/13 825am ADDED
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_RESTART_LEVEL finish()");   
           }
           
//           super.finish();
           finish();
   		
//    	   mGame.restartLevel();
    	   break;
//       	   if (LevelTree.get(mLevelRow, mLevelIndex).restartable) {
//       		   mGame.restartLevel();
//       		   break;
//       	   }
//       	   // else, fall through and go to the next level.
        	   
       case GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL:
           if (GameParameters.debug) {
        	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL mLevelSelected, mLevelNumActive BEFORE = " +
        			   mLevelSelected + ", " + mLevelNumActive);   
           }
    	  
//    	   // FIXME TEMP TEST. DELETE.
//           LevelTree.get(mLevelRow, mLevelIndex).completed = true;
//           final LevelTree.LevelGroup currentGroup = LevelTree.levelGroups.get(mLevelRow);
////           final LevelTree.LevelGroup currentGroup = LevelTree.levels.get(mLevelRow);
//           final int count = currentGroup.levels.size();
//           boolean groupCompleted = true;
//
//           for (int x = 0; x < count; x++) {
//        	   if (currentGroup.levels.get(x).completed == false) {
//        		   // We haven't completed the group yet.
//        		   mLevelIndex = x;
//        		   groupCompleted = false;
//        		   break;
//        	   }
//           }
//           
//           if (groupCompleted) {
//               mLevelIndex = 0;
//               mLevelRow++;
//           }
//           
//    	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL mLevelRow, mLevelCompleted AFTER = " +
//    			   mLevelRow + ", " + mLevelCompleted);
//           
//           saveGame();
//           
//           Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL saveGame()");
//           
//           finish();
//           
//           Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL finish()");
//           
//           if (mLevelRow < LevelTree.levelGroups.size()) {
////           if (mLevelRow < LevelTree.levels.size()) {
//        	   final LevelTree.Level currentLevel = LevelTree.get(mLevelRow, mLevelIndex);
//        	   if (LevelTree.levelGroups.get(mLevelRow).levels.size() > 1) {
////        	   if (currentLevel.inThePast || LevelTree.levels.get(mLevelRow).levels.size() > 1) {
//        		   // go to the level select.
//        		   Intent i = new Intent(this, LevelSelectActivity.class);
//                   startActivityForResult(i, ACTIVITY_CHANGE_LEVELS);
//        	   } else {
//        		   // go directly to the next level
//                   mGame.setPendingLevel(currentLevel);
//
//                   mGame.requestNewLevel();
//        	   }
//        	   saveGame();
//        	   
//           } else {
//               // We beat the game!
//        	   mLevelRow = 0;
//        	   mLevelIndex = 0;
//        	   saveGame();
//               mGame.stop();
//               finish();
//           }
           
//           HudSystem hud = ObjectRegistry.sSystemRegistry.hudSystem;
//           mTotalKillCollectPoints = hud.totalKillCollectPoints;
//           mTotalCollectNum = hud.totalCollectNum;
           
           manager = ObjectRegistry.sSystemRegistry.gameObjectManager;
           weaponActiveType = manager.getWeapon1GameObjectType();
           
           if(weaponActiveType != null) {
               switch(weaponActiveType) {
               case DROID_WEAPON_LASER_STD:
               	mWeaponActive = 101;
               	break;
               	
               case DROID_WEAPON_LASER_PULSE:
               	mWeaponActive = 102;
               	break;
               	
               case DROID_WEAPON_LASER_EMP:
               	mWeaponActive = 103;
               	break;
               	
               case DROID_WEAPON_LASER_GRENADE:
               	mWeaponActive = 104;
               	break;
               	
               case DROID_WEAPON_LASER_ROCKET:
               	mWeaponActive = 105;
               	break;
               	
               case INVALID:
               	mWeaponActive = 0;
               	break;
               	
               default:
               	mWeaponActive = 101;
               	break;	
               }
           } else {
        	   mWeaponActive = 101;
           }

           
           weaponInventoryType = manager.getWeapon2GameObjectType();
           
           if(weaponInventoryType != null) {
               switch(weaponInventoryType) {
               case DROID_WEAPON_LASER_STD:
               	mWeaponInventory = 101;
               	break;
               	
               case DROID_WEAPON_LASER_PULSE:
               	mWeaponInventory = 102;
               	break;
               	
               case DROID_WEAPON_LASER_EMP:
               	mWeaponInventory = 103;
               	break;
               	
               case DROID_WEAPON_LASER_GRENADE:
               	mWeaponInventory = 104;
               	break;
               	
               case DROID_WEAPON_LASER_ROCKET:
               	mWeaponInventory = 105;
               	break;
               	
               case INVALID:
               	mWeaponInventory = 0;
               	break;
               	
               default:
               	mWeaponInventory = 0;
               	break;	
               }
           } else {
        	   mWeaponInventory = 0;
           }
    	   
//           LevelTree.get(mLevelRow, mLevelIndex).completed = true;
//           final LevelTree.LevelGroup currentGroup = LevelTree.levelGroups.get(mLevelRow);
//           final int count = currentGroup.levels.size();
//           boolean groupCompleted = true;
//           for (int x = 0; x < count; x++) {
//        	   if (currentGroup.levels.get(x).completed == false) {
//            		   // We haven't completed the group yet.
//            		   mLevelIndex = x;
//            		   groupCompleted = false;
//            		   break;
//        	   }
//           }
//           
//           if (groupCompleted) {
//               mLevelIndex = 0;
//               mLevelRow++;
//           }         
//
//   		   mLevelCompleted = LevelTree.packCompletedLevels(mLevelRow);
//   		   
//           LevelTree.updateCompletedState(mLevelRow, mLevelCompleted);
           
           if (mLevelSelected >= mLevelNumActive) {
//           if (mLevelSelected > mLevelCompleted) {
        	   mLevelNumActive++;
//               mLevelCompleted = mLevelSelected;   
           }
           
           if (GameParameters.debug) {
        	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL mLevelNumActive AFTER = " +
        			   mLevelNumActive);   
           }
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL saveGame()");   
           }
           
           saveGame();
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL mGame.stop()");   
           }
           
           // FIXME 1/1/13 825am ADDED
           mGame.stop();
           // FIXME END 1/1/13 825am ADDED
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_GO_TO_NEXT_LEVEL finish()");   
           }
           
//           super.finish();
           finish();
           
//           if (mLevelRow < LevelTree.levelGroups.size()) {
//        	   final LevelTree.Level currentLevel = LevelTree.get(mLevelRow, mLevelIndex);
//        	   
//        	   // FIXME Determine when to use LevelSelectActivity vs go Directly to Next Level
//        	   if (LevelTree.levelGroups.get(mLevelRow).levels.size() > 1) {
////        	   if (currentLevel.inThePast || LevelTree.levels.get(mLevelRow).levels.size() > 1) {
//        		   // go to the level select.
//        		   Intent i = new Intent(this, LevelSelectActivity.class);
////        		   Intent i = new Intent(this, LevelSelectActivityOld.class);
//                   startActivityForResult(i, ACTIVITY_CHANGE_LEVELS);
//        	   } else {
//        		   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() Next Level mLevelRow = " + mLevelRow);
//        		   
//        		   // go directly to the next level
//        		   mGame.setLevelRow(mLevelRow);
//        		   
//        		   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() Next Level currentLevel name, resource = " + 
//        				   currentLevel.name + ", " + currentLevel.resource);
//        		   
//                   mGame.setPendingLevel(currentLevel);
////                   if (currentLevel.showWaitMessage) {
////                	   showWaitMessage();
////                   } else {
////                	   hideWaitMessage();
////                   }
//                   
//                   mGame.requestNewLevel();
//                   /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//                   /* FIXME mGame.requestNewLevel() didn't work, so tried to directly call mGame.goToLevel(),
//                    * but no GL instance ready until onSurfaceReady() is called. */
////                   mGame.goToLevel(currentLevel);
//                   /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//        	   }
//        	   saveGame();
//        	   
//           } else {
//               // We beat the game!
//        	   mLevelRow = 0;
//        	   mLevelIndex = 0;
//        	   saveGame();
////             mGame.stop();
//               finish();
//           }
           break;
               
       case GameFlowEvent.EVENT_END_GAME:
           if (GameParameters.debug) {
        	   Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_END_GAME");   
           }
           
           manager = ObjectRegistry.sSystemRegistry.gameObjectManager;
           weaponActiveType = manager.getWeapon1GameObjectType();
           
           if(weaponActiveType != null) {
               switch(weaponActiveType) {
               case DROID_WEAPON_LASER_STD:
               	mWeaponActive = 101;
               	break;
               	
               case DROID_WEAPON_LASER_PULSE:
               	mWeaponActive = 102;
               	break;
               	
               case DROID_WEAPON_LASER_EMP:
               	mWeaponActive = 103;
               	break;
               	
               case DROID_WEAPON_LASER_GRENADE:
               	mWeaponActive = 104;
               	break;
               	
               case DROID_WEAPON_LASER_ROCKET:
               	mWeaponActive = 105;
               	break;
               	
               case INVALID:
               	mWeaponActive = 0;
               	break;
               	
               default:
               	mWeaponActive = 101;
               	break;	
               }
           } else {
        	   mWeaponActive = 101;
           }

           
           weaponInventoryType = manager.getWeapon2GameObjectType();
           
           if(weaponInventoryType != null) {
               switch(weaponInventoryType) {
               case DROID_WEAPON_LASER_STD:
               	mWeaponInventory = 101;
               	break;
               	
               case DROID_WEAPON_LASER_PULSE:
               	mWeaponInventory = 102;
               	break;
               	
               case DROID_WEAPON_LASER_EMP:
               	mWeaponInventory = 103;
               	break;
               	
               case DROID_WEAPON_LASER_GRENADE:
               	mWeaponInventory = 104;
               	break;
               	
               case DROID_WEAPON_LASER_ROCKET:
               	mWeaponInventory = 105;
               	break;
               	
               case INVALID:
               	mWeaponInventory = 0;
               	break;
               	
               default:
               	mWeaponInventory = 0;
               	break;	
               }
           } else {
        	   mWeaponInventory = 0;
           }
           
//           if (mLevelSelected >= mLevelNumActive) {
////           if (mLevelSelected > mLevelCompleted) {
//        	   mLevelNumActive++;
////               mLevelCompleted = mLevelSelected;   
//           }
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_END_GAME saveGame()");   
           }
           
           saveGame();
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_END_GAME mGame.stop()");   
           }
           
           // FIXME 1/1/13 825am ADDED
           mGame.stop();
           // FIXME END 1/1/13 825am ADDED
           
           if (GameParameters.debug) {
               Log.i("GameFlow", "ConstructActivity onGameFlowEvent() EVENT_END_GAME finish()");   
           }
      		
//           super.finish();
           finish();
////    	   mGame.stop();
           
           break;
       }
    }
    
    public void savePoints() {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity savePoints()");	
        }
    	
        HudSystem hud = ObjectRegistry.sSystemRegistry.hudSystem;
        mTotalKillCollectPoints = hud.totalKillCollectPoints;
        mTotalCollectNum = hud.totalCollectNum;
    	
    	if (mPrefsEditor != null) {
    		mPrefsEditor.putInt(PREFERENCE_TOTAL_KILL_COLLECT_POINTS, mTotalKillCollectPoints);
    		mPrefsEditor.putInt(PREFERENCE_TOTAL_COLLECT_NUM, mTotalCollectNum);
    		mPrefsEditor.commit();
    	}
    }
    
    protected void saveGame() {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity saveGame()");	
        }
        
        HudSystem hud = ObjectRegistry.sSystemRegistry.hudSystem;
        mTotalKillCollectPoints = hud.totalKillCollectPoints;
        mTotalCollectNum = hud.totalCollectNum;
    	
    	if (mPrefsEditor != null) {
//    		final int completed = LevelTree.packCompletedLevels(mLevelRow);
//    		mPrefsEditor.putInt(PREFERENCE_LEVEL_ROW, mLevelRow);
//    		mPrefsEditor.putInt(PREFERENCE_LEVEL_INDEX, mLevelIndex);
    		mPrefsEditor.putInt(PREFERENCE_LEVEL_NUM_ACTIVE, mLevelNumActive);
//    		mPrefsEditor.putInt(PREFERENCE_LEVEL_COMPLETED, mLevelCompleted);
////    		mPrefsEditor.putInt(PREFERENCE_LEVEL_COMPLETED, completed);
    		mPrefsEditor.putInt(PREFERENCE_TOTAL_KILL_COLLECT_POINTS, mTotalKillCollectPoints);
    		mPrefsEditor.putInt(PREFERENCE_TOTAL_COLLECT_NUM, mTotalCollectNum);
    		mPrefsEditor.putInt(PREFERENCE_LEVEL_WEAPON_ACTIVE, mWeaponActive);
    		mPrefsEditor.putInt(PREFERENCE_LEVEL_WEAPON_INVENTORY, mWeaponInventory);
    		mPrefsEditor.putLong(PREFERENCE_SESSION_ID, mSessionId);
    		mPrefsEditor.commit();
    	}
    }
    
    protected void showLevelLoadingMessage() {
//      protected void showLoadingMessage() {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity showLevelLoadingMessage()");	
        }
    	
      	if (mLevelLoadingMessage != null) {
      		mLevelLoadingMessage.setVisibility(View.VISIBLE);
      		
//      	mLevelLoadingMessage.postDelayed(new Runnable() {
//
//				public void run() {
//					// TODO Auto-generated method stub
//					
//	      			mLevelLoadingMessage.setVisibility(View.GONE);
//				}
//
//      	}, 3000);
      	}
      }
      
      protected void hideLevelLoadingMessage() {
//      protected void hideLoadingMessage() {
      	if (mLevelLoadingMessage != null) {
      		mLevelLoadingMessage.setVisibility(View.GONE);
      	}
      }
    
//    protected void showLoadingMessage() {
////    protected void showLoadingMessage() {
//    	if (mLoadingMessage != null) {
//    		mLoadingMessage.setVisibility(View.VISIBLE);
//    		mLoadingMessage.startAnimation(mLoadingAnimation);
//    	}
//    }
//    
//    protected void hideLoadingMessage() {
////    protected void hideLoadingMessage() {
//    	if (mLoadingMessage != null) {
//    		mLoadingMessage.setVisibility(View.GONE);
//    		mLoadingMessage.clearAnimation();
//    	}
//    }
    
      public void showPauseMessage() {
//    protected void showPauseMessage() {
          if (GameParameters.debug) {
          	Log.i("GameFlow", "ConstructActivity showPauseMessage()");  
          }
    	
    	if (mPauseMessage != null) {
    		mPauseMessage.setVisibility(View.VISIBLE);
    	}
    }
    
    protected void hidePauseMessage() {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity hidePauseMessage()");
        }
    	
    	if (mPauseMessage != null) {
    		mPauseMessage.setVisibility(View.GONE);
    	}
    }
    
//    protected void showWaitMessage() {
//    	if (mWaitMessage != null) {
//    		mWaitMessage.setVisibility(View.VISIBLE);
//    		mWaitMessage.startAnimation(mWaitFadeAnimation);
//    	}
//    }
//    
//    protected void hideWaitMessage() {
//    	if (mWaitMessage != null) {
//    		mWaitMessage.setVisibility(View.GONE);
//    		mWaitMessage.clearAnimation();
//    	}
//    }
    
    // FIXME 12/2/12 ADDED
    public static Context getAppContext() {
    	return ConstructActivity.sContext;
    }
    // FIXME END 12/2/12 ADDED
    
    // TODO Create custom Return to Main Menu Game Dialog
    @Override
    protected Dialog onCreateDialog(int id) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "ConstructActivity onCreateDialog()");	
        }
    	
        Dialog dialog = null;

        switch(id) {
        case RETURN_MAINMENU_DIALOG:
            dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.returnmainmenu_game_dialog_title)
            .setPositiveButton(R.string.returnmainmenu_game_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	finish();
                }
            })
            .setNegativeButton(R.string.returnmainmenu_game_dialog_cancel, null)
            .setMessage(R.string.returnmainmenu_game_dialog_message)
            .create();
        	break;
        	
        case LICENSE_ALLOW:
            dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.license_verification_dialog_title)
            .setPositiveButton(R.string.license_allow_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// allow app to play
                }
            })
//            .setNegativeButton(R.string.returnmainmenu_game_dialog_cancel, null)
            .setMessage(R.string.license_allow_dialog_message)
            .create();
        	break;
        	
        case LICENSE_DONT_ALLOW:
            dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.license_verification_dialog_title)
            .setPositiveButton(R.string.license_dont_allow_dialog_close_app, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	finish();
                }
            })
//            .setNegativeButton(R.string.returnmainmenu_game_dialog_cancel, null)
            .setMessage(R.string.license_dont_allow_dialog_message)
            .create();
        	break;
        	
        default:
        	break;
        }
        
//        if (id == RETURN_MAINMENU_DIALOG) {        	
//            dialog = new AlertDialog.Builder(this)
//	            .setTitle(R.string.returnmainmenu_game_dialog_title)
//	            .setPositiveButton(R.string.returnmainmenu_game_dialog_ok, new DialogInterface.OnClickListener() {
//	                public void onClick(DialogInterface dialog, int whichButton) {
//	                	finish();
//	                }
//	            })
//	            .setNegativeButton(R.string.returnmainmenu_game_dialog_cancel, null)
//	            .setMessage(R.string.returnmainmenu_game_dialog_message)
//	            .create();
//        }
        
        return dialog;
    }
    
//    private void displayResult(final String result) {
//        mLicenseHandler.post(new Runnable() {
//            public void run() {
//                mStatusText.setText(result);
//                setProgressBarIndeterminateVisibility(false);
//                mCheckLicenseButton.setEnabled(true);
//            }
//        });
//    }
    
    // FIXME Re-enable License Checker
//    private class ConstructActivityLicenseCheckerCallback implements LicenseCheckerCallback {
//
//        public void allow() {
////        public void allow(int reason) {
//            if (isFinishing()) {
//                // Don't update UI if Activity is finishing.
//                return;
//            }
//            
//            if (GameParameters.debug) {
//                Log.i("GameFlow", "ConstructActivityLicenseCheckerCallback allow() LICENSE_ALLOW");	
//            }
//            
//            // XXX Not Required. Pops up upon every Game Start.
//            // Should allow user access.
////            showDialog(LICENSE_ALLOW);
//////            displayResult(getString(R.string.license_allow_dialog_message));
//        }
//
//        public void dontAllow() {
////        public void dontAllow(int reason) {
//            if (isFinishing()) {
//                // Don't update UI if Activity is finishing.
//                return;
//            }
//            
//            if (GameParameters.debug) {
//                Log.i("GameFlow", "ConstructActivityLicenseCheckerCallback dontAllow() LICENSE_DONT_ALLOW");	
//            }
//            
//            showDialog(LICENSE_DONT_ALLOW);
////            displayResult(getString(R.string.license_dont_allow_dialog_message));
//            
////            if (reason == Policy.RETRY) {
////                // If the reason received from the policy is RETRY, it was probably
////                // due to a loss of connection with the service, so we should give the
////                // user a chance to retry. So show a dialog to retry.
////                showDialog(DIALOG_RETRY);
////            } else {
////                // Otherwise, the user is not licensed to use this app.
////                // Your response should always inform the user that the application
////                // is not licensed, but your behavior at that point can vary. You might
////                // provide the user a limited access version of your app or you can
////                // take them to Google Play to purchase the app.
////                showDialog(DIALOG_GOTOMARKET);
////            }
//        }
//    	
//        public void applicationError(ApplicationErrorCode errorCode) {
//            if (isFinishing()) {
//                // Don't update UI if Activity is finishing.
//                return;
//            }
//            
//            dontAllow();
////            showDialog(LICENSE_DONT_ALLOW);
//////            displayResult(getString(R.string.license_dont_allow_dialog_message));
//        }
//    }
}