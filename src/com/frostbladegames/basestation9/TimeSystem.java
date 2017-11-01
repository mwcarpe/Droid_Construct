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


//import com.frostbladegames.droidconstruct.DebugLog;

/**
 * Maintains a canonical time step, in seconds, for the entire game engine.  This time step
 * represents real changes in time but is only updated once per frame.
 */
// XXX: time distortion effects could go here, or they could go into a special object manager.
public class TimeSystem extends BaseObject {
    private static final float EASE_DURATION = 0.5f;
	
    private float mGameTime;
    private float mRealTime;
    private float mFreezeDelay;
    private float mGameFrameDelta;
    private float mRealFrameDelta;
    
    private float mTargetScale;
    private float mScaleDuration;
    private float mScaleStartTime;
    private boolean mEaseScale;
    
    private float mTimerIntro;
    private float mLastTime;
    private boolean mChangeViewIntro;
    private boolean mSpotlightIntro;
    
    private boolean mIntroCompleted;
    
    public TimeSystem() {
        super();
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "TimeSystem <constructor>");	
        }
        
        reset();
    }
    
    @Override
    public void reset() {
//        DebugLog.d("TimeSystem", "reset()");
    	
        mGameTime = 0.0f; 
        mRealTime = 0.0f;
        mFreezeDelay = 0.0f;
        mGameFrameDelta = 0.0f;
        mRealFrameDelta = 0.0f;
        
        mTargetScale = 1.0f;
        mScaleDuration = 0.0f;
        mScaleStartTime = 0.0f;
        mEaseScale = false;
        
        mTimerIntro = 0.0f;
        mLastTime = 0.0f;
        mChangeViewIntro = false;
        mSpotlightIntro = false;
        
        mIntroCompleted = false;
    }

    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
//    	Log.i("Loop", "TimeSystem update()");
    	
    	mRealTime += timeDelta;
    	mRealFrameDelta = timeDelta;
    	
    	// FIXME Is mFreezeDelay or mScaleXXX code required for BS9?
        if (mFreezeDelay > 0.0f) {
            mFreezeDelay -= timeDelta;
            mGameFrameDelta = 0.0f;
        } else {
        	float scale = 1.0f;
        	if (mScaleStartTime > 0.0f) {
        		final float scaleTime = mRealTime - mScaleStartTime;
        		if (scaleTime > mScaleDuration) {
        			mScaleStartTime = 0;
        		} else {
        			if (mEaseScale) {
        				if (scaleTime <= EASE_DURATION) {
        					// ease in
        					scale = Lerp.ease(1.0f, mTargetScale, EASE_DURATION, scaleTime);
        				} else if (mScaleDuration - scaleTime < EASE_DURATION) {
        					// ease out
        					final float easeOutTime = EASE_DURATION - (mScaleDuration - scaleTime);
        					scale = Lerp.ease(mTargetScale, 1.0f, EASE_DURATION, easeOutTime);
        				} else {
        					scale = mTargetScale;
        				}
        			} else {
        				scale = mTargetScale;
        			}
        		}
            }
        	 
            mGameTime += (timeDelta * scale);
            mGameFrameDelta = (timeDelta * scale);
        }
        
        // FIXME Move to GameObjectManager (ref Level04 Lighting)
        if (GameParameters.splashScreen) {	
        	// Ignore
        } else {
            if (GameParameters.levelRow == 0) {
        		if (mTimerIntro > 6.0f) {
        			LevelSystem level = sSystemRegistry.levelSystem;
        			if (level != null && !mIntroCompleted) {
            			level.sendGameEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0, true);
//            			level.sendGameEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0, false);
//            			level.sendGameEvent(GameFlowEvent.EVENT_END_GAME, 0, false);	
            			
            			mIntroCompleted = true;
        			}

        		} else if (!mChangeViewIntro && mTimerIntro > 3.0f) {
        	    	CameraSystem cameraSystem = sSystemRegistry.cameraSystem;
        	    	cameraSystem.setTargetDroid();
//        	    	cameraSystem.setFocusPosition();
//        	    	cameraSystem.setFocusPositionDroid();
        	    	cameraSystem.setViewangle(-1.2f, 0.5f, 1.2f);	// y has 0.5f buffer to offset gluLookAt() default parameters
        	    	
        	    	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	    	factory.setBackgroundMusicVolume(0.25f, 0.25f);
        	    	
        	    	mChangeViewIntro = true;
        	    	
        		} else if (!mSpotlightIntro && mTimerIntro > 4.0f) {
        			GameParameters.light2Enabled = true;
        			
        			mSpotlightIntro = true;
        		}
        		
        		mTimerIntro += mGameTime - mLastTime;
        		mLastTime = mGameTime;
            }	
        }
    }

    public float getGameTime() {
        return mGameTime;
    }
    
    public float getRealTime() {
        return mRealTime;
    }
    
    public float getTimerIntro() {
        return mTimerIntro;
    }
    
    public float getFrameDelta() {
        return mGameFrameDelta;
    }
    
    public float getRealTimeFrameDelta() {
        return mRealFrameDelta;
    }
    
    public void freeze(float seconds) {
        mFreezeDelay = seconds;
    }
    
    public void appyScale(float scaleFactor, float duration, boolean ease) {
    	mTargetScale = scaleFactor;
    	mScaleDuration = duration;
    	mEaseScale = ease;
    	if (mScaleStartTime <= 0.0f) {
    		mScaleStartTime = mRealTime;
    	}
    }
}
