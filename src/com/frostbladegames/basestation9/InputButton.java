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



public class InputButton {
	private boolean mDown;
	private float mLastPressedTime;
	private float mDownTime;
	private float mMagnitude;
	
	public void press(float currentTime) {
		if (!mDown) {
			mDown = true;
			mDownTime = currentTime;
		} 
		
		mLastPressedTime = currentTime;
	}
	
	public void press(float currentTime, float magnitude) {
		if (!mDown) {
			mDown = true;
			mDownTime = currentTime;
		}
		
		mMagnitude = magnitude;
		mLastPressedTime = currentTime;
		
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//		if ((currentTime - mLastPressedTime) < 0.5f) {
//			// ignore
//			mDown = false;
//		} else {
//			mDown = true;
//			mMagnitude = magnitude;
//			mLastPressedTime = currentTime;
//		}
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public void release() {
		mDown = false;
	}

	public final boolean getPressed() {
		return mDown;
	}
	
	public final boolean getTriggered(float currentTime) {
		return mDown && currentTime - mDownTime <= BaseObject.sSystemRegistry.timeSystem.getFrameDelta() * 2.0f;
	}
	
	public final float getPressedDuration(float currentTime) {
		return currentTime - mDownTime;
	}
	
	public final float getLastPressedTime() {
		return mLastPressedTime;
	}
	
	public final float getMagnitude() {
		// TODO Did this change break something?
//		float magnitude = 0.0f;
//		if (mDown) {
//			magnitude = mMagnitude;
//		}
//		return magnitude;
		return mMagnitude;
	}
	
	public final void setMagnitude(float magnitude) {
		mMagnitude = magnitude;
	}
	
	public final void reset() {
		mDown = false;
		mMagnitude = 0.0f;
		mLastPressedTime = 0.0f;
		mDownTime = 0.0f;
	}
}
