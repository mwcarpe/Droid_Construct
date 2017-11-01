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

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;


/**
 * Thread for SplashScreenActivity
 */
public class SplashScreenThread implements Runnable {
	// FIXME After memory optimizations, change FINISH_TIME to 5000 or less. Currently crashes.
	private static final int FINISH_TIME = 10000;  // Allow enough time for SplashScreenActivity Handler Runnable to finish()
//	private static final int FINISH_TIME = 5000;
	
	private long mLastTime;
    private boolean mPaused;
    
    private DroidGLSurfaceView mGLSurfaceView;
  
	private ObjectManager mSplashRoot;
//	private ObjectManager mGameRoot;
	private SplashScreenRenderer mRenderer;
//	private GameRenderer mRenderer;
	private Object mPauseLock;
  
	protected boolean mActive = true;
	protected int mSplashTime;
//	protected int mFinishTime = 10000;	// Allow enough time for SplashScreenActivity Handler Runnable to finish()
////	protected int mFinishTime = 4000;
	private SoundPool mSoundPool;
	private int mSoundId;
	private boolean mLoaded = false;
	private boolean mSoundPlayed = false;
  
	public SplashScreenThread(SplashScreenRenderer renderer, Context context) {
//	public SplashScreenThread(GameRenderer renderer, Context context) {
		mLastTime = SystemClock.uptimeMillis();
		mPaused = false;
		
		mSplashTime = 0;
      
		mRenderer = renderer;
		mPauseLock = new Object();
		
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
//            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                    int status) {
                mLoaded = true;
            }
        });
        mSoundId = mSoundPool.load(context, R.raw.sound_splashscreen, 1);
	}

	public void run() {
		Log.i("GameFow", "SplashScreenThread run()");
		
		mLastTime = SystemClock.uptimeMillis();
////		mFinished = false;
//		int waited = 0;

		while (mActive && (mSplashTime < FINISH_TIME)) {
//		while (mActive && (mSplashTime < mFinishTime)) {
//		while (mActive && (waited < mSplashTime)) {
////            sleep(100);
//            if(mActive) {
//                waited += 100;
//            }
            
			if (mSplashRoot != null) {
				mRenderer.waitDrawingComplete();
	          
				final long time = SystemClock.uptimeMillis();
				final long timeDelta = time - mLastTime;
				long finalDelta = timeDelta;
				
				mSplashTime += timeDelta;
				
//	        	if (mLoaded && mSplashTime > 1000) {
////	        	if (mLoaded && waited > 1500) {
//	            	if (!mSoundPlayed) {
//	                    mSoundPool.play(mSoundId, 1.0f, 1.0f, 2, 0, 1.0f);
//	                    mSoundPlayed = true;
//	            	}
//	        	}
	          
				/* Checks that beyond current loop tolerance for next 16msec loop.
				 * Allows 4msec buffer (> 12msec) before finalDelta < 16msec blocking check. */
				if (timeDelta > 12) {
					float secondsDelta = (time - mLastTime) * 0.001f;
					if (secondsDelta > 0.1f) {
						secondsDelta = 0.1f;
					}
	                  
					mLastTime = time;
					
		        	if (mLoaded && mSplashTime > 1000) {
//			        	if (mLoaded && waited > 1500) {
		            	if (!mSoundPlayed) {
		                    mSoundPool.play(mSoundId, 1.0f, 1.0f, 2, 0, 1.0f);
		                    mSoundPlayed = true;
		            	}
			        }
	  
//					mSplashRoot.update(secondsDelta, null, false);
					mSplashRoot.update(secondsDelta, null);
	  
					CameraSystem camera = mSplashRoot.sSystemRegistry.cameraSystem;
					float x = 0.0f;
					float y = 0.0f;
					float z = 0.0f;
					float viewangleX = 0.0f;
					float viewangleY = 0.0f;
					float viewangleZ = 0.0f;
					if (camera != null) {
						x = camera.getFocusPositionX();
	                  	y = camera.getFocusPositionY();
	                  	z = camera.getFocusPositionZ();
	                  	
	                  	Vector3 viewangle = camera.getViewangle();
	                  	viewangleX = viewangle.x;
	                  	viewangleY = viewangle.y;
	                  	viewangleZ = viewangle.z;
					}
	                  
					BaseObject.sSystemRegistry.splashScreenRenderSystem.swap(mRenderer, x, y, z, 
							viewangleX, viewangleY, viewangleZ, mSplashTime);
//					BaseObject.sSystemRegistry.splashScreenRenderSystem.swap(mRenderer, x, y, z, 
//							viewangleX, viewangleY, viewangleZ, waited);
//					BaseObject.sSystemRegistry.renderSystem.swap(mRenderer, x, y, z, 
//							viewangleX, viewangleY, viewangleZ);
	                  
					final long endTime = SystemClock.uptimeMillis();
	                  
					finalDelta = endTime - time;
					
                    mGLSurfaceView.requestRender();
				}
	              
				// If the game logic completed in less than 16ms, that means it's running
				// faster than 60fps, which is our target frame rate.  In that case we should
				// yield to the rendering thread, at least for the remaining frame. 
				if (finalDelta < 16) {              	
					try {
						Thread.sleep(16 - finalDelta);
					} catch (InterruptedException e) {
						// Interruptions here are no big deal.
					}
				}
	          
				final long endTime = SystemClock.uptimeMillis();
				finalDelta = endTime - time;
	          
	            synchronized(mPauseLock) {
	                if (mPaused) {
	                  	SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
	                  	if (sound != null) {
	                  		sound.pauseAll();
	//                  		BaseObject.sSystemRegistry.inputSystem.releaseAllKeys();
	                  	}
	                    while (mPaused) {
	                        try {
	                        	mPauseLock.wait();
	                        } catch (InterruptedException e) {
	                            // No big deal if this wait is interrupted.
	                        }
	                    }
	                }
	            }
			} else {
				Log.e("GameFlow", "SplashScreenThread run() mSplashRoot = null");
			}
		}
		
		if (GameParameters.debug) {
			Log.i("GameFlow", "SplashScreenThread run() mActive = false");	
		}
	
		// Make sure our dependence on the render system is cleaned up.
		BaseObject.sSystemRegistry.splashScreenRenderSystem.emptyQueues(mRenderer);
//		BaseObject.sSystemRegistry.renderSystem.emptyQueues(mRenderer);
		
		// FIXME RE-ENABLE?
//		stopSplash();
		
//        Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
//        startActivity(i);
	}
	
	public void stopActive() {
		if (GameParameters.debug) {
	        Log.i("GameFlow", "SplashScreenThread stopActive()");	
		}
        
//		mActive = false;
	  	synchronized (mPauseLock) {
	  		mPaused = false;
	  		mActive = false;
	  		mSplashTime = 0;
	//          mFinished = true;
	        mPauseLock.notifyAll();
	  	}
	}

	public void stopSplash() {
		if (GameParameters.debug) {
			Log.i("GameFlow", "SplashScreenThread stopSplash()");	
		}
		
//		mRenderer.stopSplash();
	  	synchronized (mPauseLock) {
	          mPaused = false;
	          mActive = false;
	          mSplashTime = 0;
	//          mFinished = true;
	          mPauseLock.notifyAll();
	  	}
	}
  
  public void pauseGame() {
	  if (GameParameters.debug) {
		  Log.i("GameFlow", "SplashScreenThread pauseGame()");  
	  }
	  
      synchronized (mPauseLock) {
          mPaused = true;
      }
  }

  public void resumeGame() {
	  if (GameParameters.debug) {
		  Log.i("GameFlow", "SplashScreenThread resumeGame()");  
	  }
	  
      synchronized (mPauseLock) {
          mPaused = false;
          mPauseLock.notifyAll();
      }
  }
  
  public boolean getPaused() {
	  if (GameParameters.debug) {
		  Log.i("GameFlow", "SplashScreenThread getPaused()");  
	  }
	  
      return mPaused;
  }
  
  public void setSurfaceView(DroidGLSurfaceView surfaceView) {
  	mGLSurfaceView = surfaceView;
  }

	public void setSplashRoot(ObjectManager splashRoot) {
		mSplashRoot = splashRoot;
	}
}
