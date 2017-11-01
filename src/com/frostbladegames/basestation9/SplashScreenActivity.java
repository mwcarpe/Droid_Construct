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

import com.frostbladegames.basestation9.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


/**
 * SplashScreenActivity intro before MainMenuActivity and ConstructActivity
 */
public class SplashScreenActivity extends Activity {
//    public static final int QUIT_GAME_DIALOG = 0;
	
    private static Context sContext;
    
  private DroidGLSurfaceView mGLSurfaceView;
  private SplashScreenGame mGame;
//  private Game mGame;

  private Animation mWaitFadeAnimation = null;
  
  /** Called when the activity is first created. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      SplashScreenActivity.sContext = getApplicationContext();
      
  	if (GameParameters.debug) {
        Log.i("GameFlow", "SplashScreenActivity onCreate()");	
  	}
      
      setContentView(R.layout.splash);
//      setContentView(R.layout.main);
      mGLSurfaceView = (DroidGLSurfaceView) findViewById(R.id.glsurfaceview);
      
      mWaitFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.wait_message_fade);
      
      mGame = new SplashScreenGame();
//      mGame = new Game();
//      mGame.setSurfaceView(mGLSurfaceView);
      DisplayMetrics dm = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(dm);

      int viewWidth = dm.widthPixels;
      int viewHeight = dm.heightPixels;
      int gameWidth = 480;
      int gameHeight = 320;
      if (viewWidth != gameWidth) {
      	float ratio =((float)viewWidth) / viewHeight;
      	gameWidth = (int)(gameHeight * ratio);
      }
      
      mGame.bootstrap(this, viewWidth, viewHeight, gameWidth, gameHeight);
      
      mGame.setSurfaceView(mGLSurfaceView);
      
      mGLSurfaceView.setRenderer(mGame.getRenderer());
      
      mGLSurfaceView.setRenderMode(DroidGLSurfaceView.RENDERMODE_WHEN_DIRTY);
      
      GameParameters.splashScreen = true;

//      // This activity uses the media stream.
//      setVolumeControlStream(AudioManager.STREAM_MUSIC);
      
      new Handler().postDelayed(new Runnable() {
//      	@Override
      	public void run() {
      		finish();
      	}
      }, 8000);
  }

  @Override
  protected void onPause() {
      super.onPause();
      
      if (GameParameters.debug) {
          Log.i("GameFlow", "SplashScreenActivity onPause()");  
      }
      
//      finish();
      
      if (mGame.isRunning()) {
    	  if (GameParameters.debug) {
    	      	Log.i("GameFlow", "SplashScreenActivity onPause() mGame.isRunning() == TRUE");  
    	  }
  		
  		if (!mGame.isPaused()) {
  			if (GameParameters.debug) {
  	  			Log.i("GameFlow", "SplashScreenActivity onPause() mGame.isPaused() == FALSE");	
  			}
  			
  			mGame.onPause();
              
              // FIXME 12/2/12 TEMP DELETE. RE-ENABLE.
//              mGame.getRenderer().onPause();	// hack!
              // FIXME END 12/2/12 TEMP DELETE.
  		} else {
  			if (GameParameters.debug) {
  	  			Log.i("GameFlow", "SplashScreenActivity onPause() mGame.isPaused() == TRUE");	
  			}
  		}
  		
  		mGame.onSurfaceLost();
  		
        mGLSurfaceView.onPause();
  		
      } else {
    	  if (GameParameters.debug) {
    	      	Log.i("GameFlow", "SplashScreenActivity onPause() mGame.isRunning() == FALSE");  
    	  }
      }
      
//      if (mGame.isRunning()) {
//          mGame.onPause();
//          mGLSurfaceView.onPause();
//          mGame.getRenderer().onPause();	// hack!
//      }
      
////      mGame.onPause();
//      mGLSurfaceView.onPause();
//      mGame.getRenderer().onPause();	// hack!
      
//      finish();
  }
  
  @Override
  protected void onStop() {
	  super.onStop();
	  
//	  Log.i("GameFlow", "SplashScreenActivity onStop()");
	  
//	  finish();
////      mGame.stop();
	  
//		if (mGame.isRunning()) {
//			mGame.onSurfaceLost();
//		}
  }

  @Override
  protected void onResume() {
      super.onResume();
      
      if (GameParameters.debug) {
          Log.i("GameFlow", "SplashScreenActivity onResume()");  
      }
      
      if (mGame.isRunning()) {
          if (GameParameters.debug) {
            	Log.i("GameFlow", "SplashScreenActivity onResume() mGame.isRunning() == TRUE");  
          }
      	
  		mGLSurfaceView.onResume();
      	
      	if (mGame.isPaused()) {
      		if (GameParameters.debug) {
          		Log.i("GameFlow", "SplashScreenActivity onResume() mGame.isPaused() == TRUE");	
      		}
      		
      		SplashScreenActivity.sContext = getApplicationContext();
          	
//          	mGame.getRenderer().onResume();

              
              mGame.onResume(this, true);
////              mGame.onResume(this, false);
              
  		} else {
  			if (GameParameters.debug) {
  	  			Log.i("GameFlow", "SplashScreenActivity onResume() mGame.isPaused() == FALSE");	
  			}
  		}
      } else {
    	  if (GameParameters.debug) {
    	      	Log.i("GameFlow", "SplashScreenActivity onResume() mGame.isRunning() == FALSE");  
    	  }
      }
      
//      if (mGame.isRunning()) {
//          mGLSurfaceView.onResume();
//          mGame.onResume(this, false);
//      }
  }
  
  @Override
  protected void onDestroy() {
	  if (GameParameters.debug) {
		  Log.i("GameFlow", "SplashScreenActivity onDestroy()");  
	  }
	  
	  SplashScreenActivity.sContext = null;
	  
      mGame.stop();
      
      super.onDestroy(); 
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
//	  mGame.onTouchEvent(event);
	  
  	// FIXME Re-enable and debug crash upon SplashScreenActivity and SplashScreenGame onTouchEvent()
	  finish();
    
	  return true;
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
  	boolean result = false;
	if (keyCode == KeyEvent.KEYCODE_BACK) {
//		mGame.stop();
		finish();
		result = true;
//		final long time = System.currentTimeMillis();
//		showDialog(QUIT_GAME_DIALOG);
	} else if (keyCode == KeyEvent.KEYCODE_MENU) {
		result = true;
	}
    return result;
	  
//      boolean result = true;
////      boolean result = false;
//
////  	if (keyCode == KeyEvent.KEYCODE_BACK) {
////  		result = true;
////  	} else if (keyCode == KeyEvent.KEYCODE_MENU) {
////  		result = true;
////  	}
//  	
//      return result;
  }
   
  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
	  boolean result = true;
//	  boolean result = false;
	  
//	  if (keyCode == KeyEvent.KEYCODE_BACK) {
//		  result = true;
//	  } else if (keyCode == KeyEvent.KEYCODE_MENU){ 
//	  }
	  
      return result;
  }
  
  public static Context getAppContext() {
	  return SplashScreenActivity.sContext;
  }
  
//  public void goToMainMenu() {
//// 	 Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
//// 	 startActivity(i);
// 	 finish();
//  }
  
//  /*
//   *  When the game thread needs to stop its own execution (to go to a new level, or restart the
//   *  current level), it registers a runnable on the main thread which orders the action via this
//   *  function.
//   */
//  public void onGameFlowEvent(int eventCode, int index) {
//     switch (eventCode) {             
//     case GameFlowEvent.EVENT_END_GAME: 
//    	 Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
//    	 startActivity(i);
//    	 finish();
//    	 break;
//     }
//  }
  
//  @Override
//  protected Dialog onCreateDialog(int id) {
//      Dialog dialog = null;
//      if (id == QUIT_GAME_DIALOG) {
//      	
//          dialog = new AlertDialog.Builder(this)
//              .setTitle(R.string.quit_game_dialog_title)
//              .setPositiveButton(R.string.quit_game_dialog_ok, new DialogInterface.OnClickListener() {
//                  public void onClick(DialogInterface dialog, int whichButton) {
//                  	finish();
//                  }
//              })
//              .setNegativeButton(R.string.quit_game_dialog_cancel, null)
//              .setMessage(R.string.quit_game_dialog_message)
//              .create();
//      }
//      return dialog;
//  }
}
