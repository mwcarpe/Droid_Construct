/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import com.frostbladegames.basestation9.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Activity to Select Level
 */
public class LevelSelectActivity extends Activity {
//    public static final String PREFERENCE_LEVEL_ROW = "levelRow";
//    public static final String PREFERENCE_LEVEL_INDEX = "levelIndex";
    public static final String PREFERENCE_LEVEL_NUM_ACTIVE = "levelNumActive";
//    public static final String PREFERENCE_LEVEL_COMPLETED = "levelCompleted";
    public static final String PREFERENCE_NAME = "BaseStationPrefs";
    
//    private int mLevelRow;
//    private int mLevelIndex;
    
    private boolean mDebugMode = false;
    
    private int mLevelSelected;
	
    private View mLevel01Button;
    private View mLevel02Button;
    private View mLevel03Button;
    private View mLevel04Button;
    private View mLevel05Button;
    private View mLevel06Button;
    private View mLevel07Button;
    private View mLevel08Button;
    private View mLevel09Button;
//    private View mLoading;
    
    private Animation mFadeOutAnimation;
    private Animation mAlternateFadeOutAnimation;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        
        setContentView(R.layout.level_select);
//        setContentView(R.layout.newgame);
        
//        mLevelRow = 0;
//        mLevelIndex = 0;
        
//        int levelRow = prefs.getInt(PREFERENCE_LEVEL_ROW, 0);
//        int levelIndex = prefs.getInt(PREFERENCE_LEVEL_INDEX, 0);
        int levelNumActive = prefs.getInt(PREFERENCE_LEVEL_NUM_ACTIVE, 0);
//        int levelCompleted = prefs.getInt(PREFERENCE_LEVEL_COMPLETED, 0);
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "LevelSelectActivity onCreate() levelNumActive = " + levelNumActive);
    	}
        
        if (levelNumActive == 0) {
//        if (levelCompleted == 0) {
////        if (levelRow == 0) {
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", 0);
        	startActivity(i);
        }
        
        mLevel01Button = findViewById(R.id.button_level01);
        mLevel02Button = findViewById(R.id.button_level02);
        mLevel03Button = findViewById(R.id.button_level03);
        mLevel04Button = findViewById(R.id.button_level04);
        mLevel05Button = findViewById(R.id.button_level05);
        mLevel06Button = findViewById(R.id.button_level06);
        mLevel07Button = findViewById(R.id.button_level07);
        mLevel08Button = findViewById(R.id.button_level08);
        mLevel09Button = findViewById(R.id.button_level09);
        
//        mLoading = findViewById(R.id.message_loading);
        
        mLevel01Button.setOnClickListener(sLevel01ButtonListener);
        
        if (!mDebugMode) {
        	switch(levelNumActive) {
//        	switch(levelCompleted) {
////        	switch(levelRow) {
        	case 0:
        		if (GameParameters.debug) {
        			Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case 0");	
        		}
          	
        		// ignore
        		break;
          	
        	case 1:
        		if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case 1");	
        		}
          	
        		// mLevel01Button already set to unpress
        		break;
          	
        	case 2:
        		if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case 2");  
        		}
          	
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		break;
          	
        	case 3:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		break;
          	
        	case 4:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        		((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		mLevel04Button.setOnClickListener(sLevel04ButtonListener);
        		break;
          	
        	case 5:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        		((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
        		((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		mLevel04Button.setOnClickListener(sLevel04ButtonListener);
        		mLevel05Button.setOnClickListener(sLevel05ButtonListener);
        		break;
          	
        	case 6:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        		((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
        		((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
        		((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		mLevel04Button.setOnClickListener(sLevel04ButtonListener);
        		mLevel05Button.setOnClickListener(sLevel05ButtonListener);
        		mLevel06Button.setOnClickListener(sLevel06ButtonListener);
        		break;
          	
        	case 7:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        		((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
        		((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
        		((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
        		((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		mLevel04Button.setOnClickListener(sLevel04ButtonListener);
        		mLevel05Button.setOnClickListener(sLevel05ButtonListener);
        		mLevel06Button.setOnClickListener(sLevel06ButtonListener);
        		mLevel07Button.setOnClickListener(sLevel07ButtonListener);
        		break;
          	
        	case 8:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        		((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
        		((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
        		((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
        		((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
        		((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		mLevel04Button.setOnClickListener(sLevel04ButtonListener);
        		mLevel05Button.setOnClickListener(sLevel05ButtonListener);
        		mLevel06Button.setOnClickListener(sLevel06ButtonListener);
        		mLevel07Button.setOnClickListener(sLevel07ButtonListener);
        		mLevel08Button.setOnClickListener(sLevel08ButtonListener);
        		break;
              
        	case 9:
        		((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        		((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        		((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
        		((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
        		((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
        		((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
        		((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
        		((ImageView)mLevel09Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level09_unpress));
          	
        		mLevel02Button.setOnClickListener(sLevel02ButtonListener);
        		mLevel03Button.setOnClickListener(sLevel03ButtonListener);
        		mLevel04Button.setOnClickListener(sLevel04ButtonListener);
        		mLevel05Button.setOnClickListener(sLevel05ButtonListener);
        		mLevel06Button.setOnClickListener(sLevel06ButtonListener);
        		mLevel07Button.setOnClickListener(sLevel07ButtonListener);
        		mLevel08Button.setOnClickListener(sLevel08ButtonListener);
        		mLevel09Button.setOnClickListener(sLevel09ButtonListener);
        		break;
          	
        	default:
        		if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case default");  
        		}
        		
        		// mLevel01Button already set to unpress
        		break;
        	}
          
          mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
          mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
          
        } else {
        	// Debug Mode
            ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
            ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
            ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
            ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
            ((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
            ((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
            ((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
            ((ImageView)mLevel09Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level09_unpress));
        	
            mLevel02Button.setOnClickListener(sLevel02ButtonListener);
            mLevel03Button.setOnClickListener(sLevel03ButtonListener);
            mLevel04Button.setOnClickListener(sLevel04ButtonListener);
            mLevel05Button.setOnClickListener(sLevel05ButtonListener);
            mLevel06Button.setOnClickListener(sLevel06ButtonListener);
            mLevel07Button.setOnClickListener(sLevel07ButtonListener);
            mLevel08Button.setOnClickListener(sLevel08ButtonListener);
            mLevel09Button.setOnClickListener(sLevel09ButtonListener);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
//        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        
//        int levelRow = prefs.getInt(PREFERENCE_LEVEL_ROW, 0);
//        int levelIndex = prefs.getInt(PREFERENCE_LEVEL_INDEX, 0);
        int levelNumActive = prefs.getInt(PREFERENCE_LEVEL_NUM_ACTIVE, 0);
//        int levelCompleted = prefs.getInt(PREFERENCE_LEVEL_COMPLETED, 0);
        
  	  if (GameParameters.debug) {
          Log.i("GameFlow", "LevelSelectActivity onResume() levelNumActive = " + levelNumActive);  
  	  }
        
        if (!mDebugMode) {
            switch(levelNumActive) {
//            switch(levelCompleted) {
////            switch(levelRow) {
          case 0:
          	  if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case 0");  
          	  }
          	
          	// ignore
          	break;
          	
          case 1:
          	  if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case 1");  
          	  }
          	
          	// mLevel01Button already set to unpress
          	break;
          	
          case 2:
          	  if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case 2");  
          	  }
          	
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
          	break;
          	
          case 3:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              break;
          	
          case 4:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
              ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              mLevel04Button.setOnClickListener(sLevel04ButtonListener);
              break;
          	
          case 5:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
              ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
              ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              mLevel04Button.setOnClickListener(sLevel04ButtonListener);
              mLevel05Button.setOnClickListener(sLevel05ButtonListener);
              break;
          	
          case 6:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
              ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
              ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
              ((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              mLevel04Button.setOnClickListener(sLevel04ButtonListener);
              mLevel05Button.setOnClickListener(sLevel05ButtonListener);
              mLevel06Button.setOnClickListener(sLevel06ButtonListener);
              break;
          	
          case 7:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
              ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
              ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
              ((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
              ((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              mLevel04Button.setOnClickListener(sLevel04ButtonListener);
              mLevel05Button.setOnClickListener(sLevel05ButtonListener);
              mLevel06Button.setOnClickListener(sLevel06ButtonListener);
              mLevel07Button.setOnClickListener(sLevel07ButtonListener);
              break;
          	
          case 8:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
              ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
              ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
              ((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
              ((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
              ((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              mLevel04Button.setOnClickListener(sLevel04ButtonListener);
              mLevel05Button.setOnClickListener(sLevel05ButtonListener);
              mLevel06Button.setOnClickListener(sLevel06ButtonListener);
              mLevel07Button.setOnClickListener(sLevel07ButtonListener);
              mLevel08Button.setOnClickListener(sLevel08ButtonListener);
              break;
              
          case 9:
              ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
              ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
              ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
              ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
              ((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
              ((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
              ((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
              ((ImageView)mLevel09Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level09_unpress));
          	
              mLevel02Button.setOnClickListener(sLevel02ButtonListener);
              mLevel03Button.setOnClickListener(sLevel03ButtonListener);
              mLevel04Button.setOnClickListener(sLevel04ButtonListener);
              mLevel05Button.setOnClickListener(sLevel05ButtonListener);
              mLevel06Button.setOnClickListener(sLevel06ButtonListener);
              mLevel07Button.setOnClickListener(sLevel07ButtonListener);
              mLevel08Button.setOnClickListener(sLevel08ButtonListener);
              mLevel09Button.setOnClickListener(sLevel09ButtonListener);
              break;
          	
          default:
          	  if (GameParameters.debug) {
                	Log.i("GameFlow", "LevelSelectActivity onCreate() switch(levelCompleted) case default");  
          	  }
          	
          	break;
          }
          
          mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
          mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
          
        } else {
        	// Debug Mode
            ((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
            ((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
            ((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
            ((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
            ((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
            ((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
            ((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
            ((ImageView)mLevel09Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level09_unpress));
        	
            mLevel02Button.setOnClickListener(sLevel02ButtonListener);
            mLevel03Button.setOnClickListener(sLevel03ButtonListener);
            mLevel04Button.setOnClickListener(sLevel04ButtonListener);
            mLevel05Button.setOnClickListener(sLevel05ButtonListener);
            mLevel06Button.setOnClickListener(sLevel06ButtonListener);
            mLevel07Button.setOnClickListener(sLevel07ButtonListener);
            mLevel08Button.setOnClickListener(sLevel08ButtonListener);
            mLevel09Button.setOnClickListener(sLevel09ButtonListener);
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
//        mLoading.setVisibility(View.INVISIBLE);
        
        switch (mLevelSelected) {
        case 1:
        	((ImageView)mLevel01Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level01_unpress));
        	break;
        	
        case 2:
        	((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_unpress));
        	break;
        	
        case 3:
        	((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_unpress));
        	break;
        	
        case 4:
        	((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_unpress));
        	break;
        	
        case 5:
        	((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_unpress));
        	break;
        	
        case 6:
        	((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_unpress));
        	break;
        	
        case 7:
        	((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_unpress));
        	break;
        	
        case 8:
        	((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_unpress));
        	break;
        	
        case 9:
        	((ImageView)mLevel09Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level09_unpress));
        	break;
        	
        default:
        	break;
        }
        
//        finish();
    } 
    
    private View.OnClickListener sLevel01ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel01Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level01_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 1;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        	
//            mFadeOutAnimation.setAnimationListener(new StartConstructActivityAfterAnimation(i));
//            mLevel02Button.startAnimation(mFadeOutAnimation);
//            mLevel03Button.startAnimation(mAlternateFadeOutAnimation);
//            mLevel04Button.startAnimation(mAlternateFadeOutAnimation);
//            mLevel05Button.startAnimation(mAlternateFadeOutAnimation);
//            mLevel06Button.startAnimation(mAlternateFadeOutAnimation);
//            mLevel07Button.startAnimation(mAlternateFadeOutAnimation);
//            mLevel08Button.startAnimation(mAlternateFadeOutAnimation);
//            mLevel09Button.startAnimation(mAlternateFadeOutAnimation);
        	
//        	finish();
        }
    };
    
    private View.OnClickListener sLevel02ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel02Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level02_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 2;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel03ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel03Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level03_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 3;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel04ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel04Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level04_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 4;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel05ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel05Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level05_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 5;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel06ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel06Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level06_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 6;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel07ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel07Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level07_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 7;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel08ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel08Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level08_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 8;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
    private View.OnClickListener sLevel09ButtonListener = new View.OnClickListener() {
        public void onClick(View v) {        	
        	((ImageView)mLevel09Button).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_level09_press));
//        	mLoading.setVisibility(View.VISIBLE);
        	
        	mLevelSelected = 9;
        	
        	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
        	i.putExtra("LevelSelected", mLevelSelected);
        	startActivity(i);
        }
    };
    
//    @Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	boolean result = false;
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			result = true;
//		}
//		return result;
//	}
//
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		boolean result = false;
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			result = true;
//		}
//		return result;
//	}
    
//    protected void generateLevelList(boolean onlyAllowThePast) {
//    	final int count = LevelTree.levelGroups.size();
//        boolean oneBranchUnlocked = false;
//        for (int x = 0; x < count; x++) {
//            boolean anyUnlocksThisBranch = false;
//            final LevelTree.LevelGroup group = LevelTree.levelGroups.get(x);
//            for (int y = 0; y < group.levels.size(); y++) {
//                LevelTree.Level level = group.levels.get(y);
//                boolean enabled = false;
//                if (!level.completed && !oneBranchUnlocked) {
//                    enabled = true;
//                    anyUnlocksThisBranch = true;
//                }
//                if (enabled || level.completed || !onlyAllowThePast) {
////                if (enabled || level.completed || !onlyAllowThePast || (onlyAllowThePast && level.inThePast)) {
//                	addItem(level, x, y, enabled);
//                }
//            }
//            if (anyUnlocksThisBranch) {
//                oneBranchUnlocked = true;
//            }
//        }
//    }
//    
//    protected void unlockNext() {
//    	final int count = LevelTree.levelGroups.size();
//        for (int x = 0; x < count; x++) {
//            final LevelTree.LevelGroup group = LevelTree.levelGroups.get(x);
//            for (int y = 0; y < group.levels.size(); y++) {
//                LevelTree.Level level = group.levels.get(y);
//                if (!level.completed) {
//                	level.completed = true;
//                    return;
//                }
//            }
//           
//        }
//    }
//
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        if (!mLevelSelected) {
//	        super.onListItemClick(l, v, position, id);
//	        LevelMetaData selectedLevel = mLevelData.get(position);
//	        if (selectedLevel.enabled) {
//	        	mLevelSelected = true;
//	            Intent intent = new Intent();
//	
//	            intent.putExtra("resource", selectedLevel.level.resource);
//	            intent.putExtra("row", selectedLevel.x);
//	            intent.putExtra("index", selectedLevel.y);
//	            TextView text = (TextView)v.findViewById(R.id.title);
//	            if (text != null) {
//	            	text.startAnimation(mButtonFlickerAnimation);
//	            	mButtonFlickerAnimation.setAnimationListener(new EndActivityAfterAnimation(intent));
//	            } else {
//	                setResult(RESULT_OK, intent);
//	            	finish();
//	            }
//	        }
//        }
//    }
//    
//    private void addItem(LevelTree.Level level, int x, int y, boolean enabled) {
//        LevelMetaData data = new LevelMetaData();
//        data.level = level;
//        data.x = x;
//        data.y = y;
//        data.enabled = enabled;
//        mLevelData.add(data);
//    }
    
	protected class StartConstructActivityAfterAnimation implements Animation.AnimationListener {
        private Intent mIntent;
        
        StartConstructActivityAfterAnimation(Intent intent) {
            mIntent = intent;
        }  

        public void onAnimationEnd(Animation animation) {     	
//        	mLevel02Button.setVisibility(View.INVISIBLE);
//        	mLevel02Button.clearAnimation();
            startActivity(mIntent);            
        }

        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
    }
	
//    protected class EndActivityAfterAnimation implements Animation.AnimationListener {
//        private Intent mIntent;
//        
//        EndActivityAfterAnimation(Intent intent) {
//            mIntent = intent;
//        }
//            
//
//        public void onAnimationEnd(Animation animation) {
//            setResult(RESULT_OK, mIntent);
//            finish();
//        }
//
//        public void onAnimationRepeat(Animation animation) {
//            // TODO Auto-generated method stub
//            
//        }
//
//        public void onAnimationStart(Animation animation) {
//            // TODO Auto-generated method stub
//            
//        }
//        
//    }
}
