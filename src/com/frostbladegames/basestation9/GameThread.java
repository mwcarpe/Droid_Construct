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

import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;


/** 
 * The GameThread contains the main loop for the game engine logic.  It invokes the game graph,
 * manages synchronization of input events, and handles the draw queue swap with the rendering
 * thread.
 */
public class GameThread implements Runnable {
    private static final float PROFILE_REPORT_DELAY = 10.0f;
//    private static final float PROFILE_REPORT_DELAY = 1.0f;
    
    private boolean debugLog = false;
    
    private boolean mFinished;
    private boolean mPaused;
//    private boolean mPaused = false;
    private long mLastTime;
    
    private boolean mGameThreadPauseRequested;
    private int mGameThreadPauseCount;
    
    private DroidGLSurfaceView mGLSurfaceView;
    
    private ObjectManager mGameRoot;
    private GameRenderer mRenderer;
    private Object mPauseLock;
    
//    private Object mInputLock;
//    
//    private int mAction;
//    private int mMask;
    
    private long mProfileTime;
    private int mProfileFrames;
    
    // FIXME TEMP. DELETE.
    private long mTouchCounterTimer;
    
//    // FIXME TEMP ONLY. DELETE.
//    private long mGameThreadTotalTime;
//    private long mGameThreadAvgTime;
//    private int mGameThreadCount;
//    private long mObjectManagerTotalTime;
//    private long mObjectManagerAvgTime;
//    private int mObjectManagerCount;
//    private long mRenderSystemSwapTotalTime;
//    private long mRenderSystemSwapAvgTime;
//    private int mRenderSystemSwapCount;
//    private long mRendererWaitDrawingCompleteTotalTime;
//    private long mRendererWaitDrawingCompleteAvgTime;
//    private int mRendererWaitDrawingCompleteCount;
//    // END DELETE
    
    public GameThread(GameRenderer renderer) {
    	debugLog = DebugLog.getDebugLog();
    	
        mFinished = false;
        mPaused = false;
        mLastTime = SystemClock.uptimeMillis();
        
        mGameThreadPauseRequested = false;
        
        mRenderer = renderer;
        mPauseLock = new Object();
        
//        mInputLock = new Object();
    }

    public void run() {
    	if (GameParameters.debug) {
        	Log.i("GameFow", "GameThread run()");	
    	}
    	
        mLastTime = SystemClock.uptimeMillis();
        
        mFinished = false;
        
        while (!mFinished) {
//        	Log.i("GameFlow", "GameThread run() while(!mFinished)");
        	
            if (mGameRoot != null) {
//            	// FIXME TEMP. DELETE.
//            	final long gameThreadTime = SystemClock.uptimeMillis();
//            	mGameThreadCount++;
            	
//            	final long rendererWaitDrawingCompleteTime = SystemClock.uptimeMillis();
//            	mRendererWaitDrawingCompleteCount++;
                mRenderer.waitDrawingComplete();
//                mRendererWaitDrawingCompleteTotalTime += SystemClock.uptimeMillis() - rendererWaitDrawingCompleteTime;
//                mRendererWaitDrawingCompleteAvgTime = mRendererWaitDrawingCompleteTotalTime / mRendererWaitDrawingCompleteCount;
                
//                // FIXME TEMP TEST DELETE
//                // Get Touch Event
//                synchronized(GameParameters.syncObject) {
//                	GameParameters.syncObject.notify();
//                }
                
                final long time = SystemClock.uptimeMillis();
                final long timeDelta = time - mLastTime;
                long finalDelta = timeDelta;
                
//                // FIXME 12/20/12 745am TEMP ADD. DELETE.
            	if (GameParameters.debug && time > (mTouchCounterTimer + 10000l)) {
            		Log.i("TouchCounter", "constructTouch, gameTouch, touchDownMove, touchUp, objectManager, render = " +
            				GameParameters.constructActivityCounter + ", " + GameParameters.gameCounter + ", " +
            				GameParameters.gameCounterTouchDownMove + ", " + GameParameters.gameCounterTouchUp + ", " +
            				GameParameters.droidBottomComponentCounter + ", " + GameParameters.renderCounter);
            		
            		Log.i("TouchCounter", "Counters: constructTouchSleep, gameThreadSleep, rendererWaitDrawComplete, drawQueueWait, drawQueueHudWait = " +
            				GameParameters.constructTouchSleepCounter + ", " + GameParameters.gameThreadSleepCounter + ", " +
            				GameParameters.rendererWaitDrawCompleteCounter + ", " + GameParameters.drawQueueWaitCounter + ", " +
            				GameParameters.drawQueueHudWaitCounter);
            		
            		mTouchCounterTimer = time;
            	}	
//                // FIXME END 12/20/12 745am TEMP ADD. DELETE.
                
//            	Log.e("Loop", "GameThread run() mLastTime, time; timeDelta BEFORE timeDelta check = " + mLastTime + 
//            			", " + time + "; " + timeDelta);
                
                /* Checks that beyond current loop tolerance for next 16msec loop.
                 * Allows 4msec buffer (> 12msec) before finalDelta < 16msec blocking check. */
                if (timeDelta > 12) {
//                /* Checks that beyond current loop tolerance for next 32msec loop.
//                 * Allows 8msec buffer (> 24msec) before finalDelta < 32msec blocking check. */
//                if (timeDelta > 24) {
                    float secondsDelta = (time - mLastTime) * 0.001f;
                    if (secondsDelta > 0.1f) {
                        secondsDelta = 0.1f;
                    }
                    
                    mLastTime = time;
                    
                    // FIXME 12/19/12 100am DELETED
                    // FIXME 12/19/12 1240am ADDED
//                    synchronized (mInputLock) {
//                		switch (mAction & mMask) {
//                		case MotionEvent.ACTION_MOVE:
//                			GameParameters.gameCounterTouchDownMove++;
//                			break;
//                			
//                		case MotionEvent.ACTION_DOWN:
//                			GameParameters.gameCounterTouchDownMove++;
//                			break;
//                			
//                		case MotionEvent.ACTION_UP:
//                			GameParameters.gameCounterTouchUp++;
//                			break;
//                			
//                		case MotionEvent.ACTION_POINTER_DOWN:
//                			GameParameters.gameCounterTouchDownMove++;
//                			break;
//                			
//                		case MotionEvent.ACTION_POINTER_UP:
//                			GameParameters.gameCounterTouchUp++;
//                			break;
//                		}
//                    }
                    // FIXME END 12/19/12 1240am ADDED
                    // FIXME END 12/19/12 100am DELETED
                    
//                	final long objectManagerTime = SystemClock.uptimeMillis();
//                	mObjectManagerCount++;
//                    mGameRoot.update(secondsDelta, null, false);
                    mGameRoot.update(secondsDelta, null);
//                    mObjectManagerTotalTime += SystemClock.uptimeMillis() - objectManagerTime;
//                    mObjectManagerAvgTime = mObjectManagerTotalTime / mObjectManagerCount;
    
                    CameraSystem camera = mGameRoot.sSystemRegistry.cameraSystem;
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
//                    	viewangleY = camera.getViewangleY();
                    }
                    
//                	final long rendererSwapTime = SystemClock.uptimeMillis();
//                	mRenderSystemSwapCount++;
                    BaseObject.sSystemRegistry.renderSystem.swap(mRenderer, x, y, z, 
                    		viewangleX, viewangleY, viewangleZ);
//                    BaseObject.sSystemRegistry.renderSystem.swap(mRenderer, x, y, z, viewangleY);
//                    mRenderSystemSwapTotalTime += SystemClock.uptimeMillis() - rendererSwapTime;
//                    mRenderSystemSwapAvgTime = mRenderSystemSwapTotalTime / mRenderSystemSwapCount;
                    
                    BaseObject.sSystemRegistry.renderSystemHud.swap(mRenderer);
                    
                	// FIXME 12/23/12 110pm TEMP DISABLE. RE-ENABLE.
//                    final long endTime = SystemClock.uptimeMillis();
//                    
//                    finalDelta = endTime - time;
                	// FIXME END 12/23/12 110pm TEMP DISABLE. RE-ENABLE.
                    
                    mGLSurfaceView.requestRender();
                    
//                    mProfileTime += finalDelta;
//                    mProfileFrames++;
//                    if (mProfileTime > PROFILE_REPORT_DELAY * 1000) {
//                        final long averageFrameTime = mProfileTime / mProfileFrames;
//                        final int fps = 1000 / (int)averageFrameTime;
//                        
//                        Log.e("Loop", "GameThread run() Avg GameObject Frame Time: " + averageFrameTime + "  FPS: " + fps);
////                        if (debugLog) {
////                            DebugLog.d("Loop", "GameThread Avg Frame Time, FPS = " + averageFrameTime + ", " + fps);	
////                        }
//
//                        mProfileTime = 0;
//                        mProfileFrames = 0;
////                        mGameRoot.sSystemRegistry.hudSystem.setFPS(1000 / (int)averageFrameTime);
//                    }
//                }
                
                }
                
//            	Log.e("Loop", "GameThread run() mLastTime, time; timeDelta AFTER timeDelta check = " + mLastTime + 
//            			", " + time + "; " + timeDelta);
//            	long endTime = finalDelta + time;  // TEMP
//                Log.e("Loop", "GameThread run() time, endTime; finalDelta BEFORE finalDelta check = " + time + 
//                		", " + endTime + "; " + finalDelta);
                
                // If the game logic completed in less than 16ms, that means it's running
                // faster than 60fps, which is our target frame rate.  In that case we should
                // yield to the rendering thread, at least for the remaining frame. 
                if (finalDelta < 16) {
//                // If the game logic completed in less than 32ms, that means it's running
//                // faster than 30fps, which is our target frame rate.  In that case we should
//                // yield to the rendering thread, at least for the remaining frame. 
//                if (finalDelta < 32) { 
                    try {
                    	GameParameters.gameThreadSleepCounter++;
                    	
                    	Thread.sleep(16 - finalDelta);
//                        Thread.sleep(32 - finalDelta);
                    } catch (InterruptedException e) {
                        // Interruptions here are no big deal.
                    }
                }
                
//                mGameThreadTotalTime += SystemClock.uptimeMillis() - gameThreadTime;
//                mGameThreadAvgTime = mGameThreadTotalTime / mGameThreadCount;
                
                // XXX Profile FPS. Re-enable for debug as required.
//            	if (GameParameters.debug) {
//                    final long endTime = SystemClock.uptimeMillis();
//                    finalDelta = endTime - time;
//                    mProfileTime += finalDelta;
//                    mProfileFrames++;
//                    if (mProfileTime > PROFILE_REPORT_DELAY * 1000) {
//                    	if (mProfileFrames > 0) {
//                            final long averageFrameTime = mProfileTime / mProfileFrames;
//                            if ((int)averageFrameTime > 0) {
//                                final int fps = 1000 / (int)averageFrameTime;
//                                
//                                Log.i("Loop", "GameThread run() Avg GameObject Frame Time: " + averageFrameTime + "  FPS: " + fps);
//                            }
//                    	}
//
//                        mProfileTime = 0;
//                        mProfileFrames = 0;
//                    }	
//            	}
                
//                endTime = finalDelta + time;  // TEMP
//                Log.e("Loop", "GameThread run() time, endTime, finalDelta AFTER finalDelta check = " + time + 
//                		", " + endTime + ", " + finalDelta);
                
                // FIXME 12/15/12 450pm Moved to below
//                if (mGameThreadPauseRequested) {
//                	// FIXME 12/15/12 430pm MODIFIED
//                	mGameThreadPauseRequested = false;
//                	pauseGame();
////                	// Allow enough time for Renderer to Pause
////            		if (mGameThreadPauseCount >= 60) {
//////            		if (mGameThreadPauseCount >= 5) {
////            			Log.i("GameFlow", "GameThread run() mGameThreadPauseRequested = TRUE and mGameThreadPauseCount = " +
////            					mGameThreadPauseCount + "; therefore call GameThread.pauseGame()");
////            			
////            			// FIXME 12/15/12 DELETE. No apparent benefit. In fact, crashed more often.
////            			// FIXME 12/15/12 ADDED
//////            			mRenderer.waitDrawingComplete();
////            			// FIXME END 12/15/12 ADDED
////            			// FIXME 12/15/12 DELETE. No apparent benefit. In fact, crashed more often.
////            			
////            			pauseGame();
////            			
//////                		BaseObject.sSystemRegistry.setGamePause(false);
//////                		
//////                		mGame.setGameRestart(true);
//////                		
//////                    	mGame.onSurfaceReady();
////                    	
////            			mGameThreadPauseRequested = false;
////                    	
////            			mGameThreadPauseCount = 0;
////                    	
////            		} else {
////            			mGameThreadPauseCount++;
////            		}
//                	// FIXME END 12/15/12 430pm MODIFIED
//                }
                // FIXME END 12/15/12 450pm Moved to below
                
                synchronized(mPauseLock) {
//                	Log.i("GameFlow", "GameThread run() synchronized (mPauseLock)");
                	
                	// FIXME 12/15/12 545pm DELETED
                	// FIXME 12/15/12 540pm MODIFIED
                	// FIXME 12/15/12 535pm RE-ENABLED
                	// FIXME 12/15/12 515pm DELETED
                	// FIXME 12/15/12 510pm ADDED
//                    if (mGameThreadPauseRequested) {
//                    	setGameThreadPause(false);
//                    	
//                    	pauseGame();
////                    	mGameThreadPauseRequested = false;
////                    	
////                    	mPaused = true;
//                    }
                	// FIXME END 12/15/12 510pm ADDED
                	// FIXME END 12/15/12 515pm DELETED
                	// FIXME END 12/15/12 535pm RE-ENABLED
                	// FIXME END 12/15/12 540pm MODIFIED
                	// FIXME END 12/15/12 545pm DELETED
                	
                    if (mPaused) {
                    	SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
                    	if (sound != null) {
                    		sound.pauseAll();
//                    		BaseObject.sSystemRegistry.inputSystem.releaseAllKeys();
                    	}
                        while (mPaused) {
                        	if (GameParameters.debug) {
                            	Log.i("GameFlow", "GameThread run() while(mPaused); mPauseLock.wait()");	
                        	}
                        	
                            try {
                            	mPauseLock.wait();
                            } catch (InterruptedException e) {
                                // No big deal if this wait is interrupted.
                            }
                        }
                    }
                }
                
                // FIXME 12/15/12 550pm DELETED
                // FIXME 12/15/12 545pm MODIFIED
                // FIXME 12/15/12 510pm MOVED to synchronized(mPauseLock)
                // FIXME 12/15/12 505pm MODIFIED
                // FIXME 12/15/12 450pm Moved from above
//                if (mGameThreadPauseRequested) {
//                	setGameThreadPause(false);
//                	
//                	pauseGame();
//                }
////                if (mGameThreadPauseRequested) {
////                	// FIXME 12/15/12 430pm MODIFIED
//////                	mGameThreadPauseRequested = false;
////                	// FIXME 12/15/12 500pm TEMP DISABLE FOR TEST ONLY. RE-ENABLE.
//////                	pauseGame();
////                	// FIXME END 12/15/12 500pm TEMP DISABLE FOR TEST ONLY. RE-ENABLE.
//////                	// Allow enough time for Renderer to Pause
////            		if (mGameThreadPauseCount >= 30) {
//////            		if (mGameThreadPauseCount >= 5) {
////            			Log.i("GameFlow", "GameThread run() mGameThreadPauseRequested = TRUE and mGameThreadPauseCount = " +
////            					mGameThreadPauseCount + "; therefore call GameThread.pauseGame()");
////            			
////            			// FIXME 12/15/12 DELETE. No apparent benefit. In fact, crashed more often.
////            			// FIXME 12/15/12 ADDED
////            			mRenderer.waitDrawingComplete();
////            			// FIXME END 12/15/12 ADDED
////            			// FIXME 12/15/12 DELETE. No apparent benefit. In fact, crashed more often.
////            			
////            			pauseGame();
////            			
//////                		BaseObject.sSystemRegistry.setGamePause(false);
//////                		
//////                		mGame.setGameRestart(true);
//////                		
//////                    	mGame.onSurfaceReady();
////                    	
////            			mGameThreadPauseRequested = false;
////                    	
////            			mGameThreadPauseCount = 0;
////                    	
////            		} else {
////            			mGameThreadPauseCount++;
////            		}
////                	// FIXME END 12/15/12 430pm MODIFIED
////                }
                // FIXME END 12/15/12 450pm Moved from above
                // FIXME END 12/15/12 505pm MODIFIED
                // FIXME END 12/15/12 510pm MOVED to synchronized(mPauseLock)
                // FIXME END 12/15/12 545pm MODIFIED
                // FIXME END 12/15/12 550pm DELETED

            } else {
            	Log.e("GameFlow", "GameThread run() mGameRoot = null");
            }
        }
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "GameThread run() mFinished = true");	
    	}
    	
        // Make sure our dependence on the render system is cleaned up.
        BaseObject.sSystemRegistry.renderSystem.emptyQueues(mRenderer);
        
        // FIXME 12/20/12 735am ADDED
        BaseObject.sSystemRegistry.renderSystemHud.emptyQueues(mRenderer);
        // FIXME END 12/20/12 735am ADDED
    }
    
    public void pauseGame() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameThread pauseGame()");	
    	}
    	
        synchronized (mPauseLock) {
        	if (GameParameters.debug) {
            	Log.i("GameFlow", "GameThread pauseGame() synchonized(mPauseLock)");	
        	}
        	
            mPaused = true;
        }
    }

    public void stopGame() {
    	if (GameParameters.debug) {
            Log.i("GameFlow", "GameThread stopGame()");	
    	}
    	
    	synchronized (mPauseLock) {
        	if (GameParameters.debug) {
        		Log.i("GameFlow", "GameThread stopGame() synchonized(mPauseLock)");	
        	}
    		
            mPaused = false;
            mFinished = true;
            mPauseLock.notifyAll();
    	}
    }

    public void resumeGame() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameThread resumeGame()");	
    	}
    	
        synchronized (mPauseLock) {
        	if (GameParameters.debug) {
            	Log.i("GameFlow", "GameThread resumeGame() synchonized(mPauseLock)");	
        	}
        	
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
    
    public boolean getPaused() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameThread getPaused() mPaused = " + mPaused);	
    	}
    	
        return mPaused;
    }
    
    // FIXME 12/15/12 540pm MODIFIED
    public void setGameThreadPause(boolean setPause) {
//    public void requestGameThreadPause() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameThread requestGameThreadPause()");	
    	}
  	
    	// FIXME 12/15/12 525pm MODIFIED
        synchronized (mPauseLock) {
        	if (GameParameters.debug) {
            	Log.i("GameFlow", "GameThread requestGameThreadPause() synchonized(mPauseLock)");	
        	}
        	
        	mGameThreadPauseRequested = setPause;
//        	mGameThreadPauseRequested = true;
            mPauseLock.notifyAll();
        }
//    	mGameThreadPauseRequested = true;
    	// FIXME END 12/15/12 525pm MODIFIED
    }
    // FIXME END 12/15/12 540pm MODIFIED
    
//    public void touchEvent(int action, int mask) {
//    	synchronized (mInputLock) {
//    		mAction = action;
//    		mMask = mask;
//    	}
//    }
    
    // FIXME 12/15/12 300pm ADDED
    public void setSurfaceView(DroidGLSurfaceView surfaceView) {
    	mGLSurfaceView = surfaceView;
    }
    // FIXME END 12/15/12 300pm ADDED

    public void setGameRoot(ObjectManager gameRoot) {
//        DebugLog.d("GameThread", "setGameRoot()");
    	
        mGameRoot = gameRoot;
    }
}
