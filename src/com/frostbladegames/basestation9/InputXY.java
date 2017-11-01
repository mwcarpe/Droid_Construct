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



public class InputXY {
	private InputButton mXAxis;
	private InputButton mYAxis;
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	private InputButton mZAxis;
	private InputButton mRotate;
	
//	private int mWeaponButtonNum;
//	public boolean weaponSwap;
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	public InputXY() {
		mXAxis = new InputButton();
		mYAxis = new InputButton();
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		mZAxis = new InputButton();
		mRotate = new InputButton();
		
//		weaponSwap = false;
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public InputXY(InputButton xAxis, InputButton yAxis) {
		mXAxis = xAxis;
		mYAxis = yAxis;
		
//		weaponSwap = false;
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public InputXY(InputButton xAxis, InputButton yAxis, InputButton zAxis, InputButton rotate) {
		mXAxis = xAxis;
		mYAxis = yAxis;
		mZAxis = zAxis;
		mRotate = rotate;
		
//		weaponSwap = false;
	}
	
	public final void press(float currentTime) {
		mXAxis.press(currentTime);
		mYAxis.press(currentTime);
	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	public final void press(float currentTime, float x, float y) {
		mXAxis.press(currentTime, x);
		mYAxis.press(currentTime, y);
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public final void press(float currentTime, float x, float y, float z, float r) {
		mXAxis.press(currentTime, x);
		mYAxis.press(currentTime, y);
		mZAxis.press(currentTime, z);
		mRotate.press(currentTime, r);
	}
	
//	public final void press(float currentTime, float x, float y, int weaponButtonNum) {
//		mXAxis.press(currentTime, x);
//		mYAxis.press(currentTime, y);
//		mWeaponButtonNum = weaponButtonNum;
//	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	public final void release() {
		mXAxis.release();
		mYAxis.release();
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		mZAxis.release();
		mRotate.release();
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public boolean getTriggered(float time) {
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		return mXAxis.getTriggered(time) || mYAxis.getTriggered(time) || 
			mZAxis.getTriggered(time) || mRotate.getTriggered(time);
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		return mXAxis.getTriggered(time) || mYAxis.getTriggered(time);
	}
	
	public boolean getPressed() {
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		return mXAxis.getPressed() || mYAxis.getPressed() || mZAxis.getPressed() ||
			mRotate.getPressed();
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		return mXAxis.getPressed() || mYAxis.getPressed();
	}
	
	public final void setVector(Vector3 vector) {
		vector.x = mXAxis.getMagnitude();
		vector.y = mYAxis.getMagnitude();
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		vector.z = mZAxis.getMagnitude();
		vector.r = mRotate.getMagnitude();
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public final float getX() {
		return mXAxis.getMagnitude();
	}
	
	public final float getY() {
		return mYAxis.getMagnitude();
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public final float getZ() {
		return mZAxis.getMagnitude();
	}
	
	public final float getR() {
		return mRotate.getMagnitude();
	}
	
//	public final int getWeaponButtonNum() {
//		return mWeaponButtonNum;
//	}
//	
//	public final void setWeaponButtonNum(int weaponButtonNum) {
//		mWeaponButtonNum = weaponButtonNum;
//	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	public final float getLastPressedTime() {
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		return Math.max(Math.max(mXAxis.getLastPressedTime(), mYAxis.getLastPressedTime()),
				Math.max(mZAxis.getLastPressedTime(), mRotate.getLastPressedTime()));
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		return Math.max(mXAxis.getLastPressedTime(), mYAxis.getLastPressedTime());
	}
	
	public final void releaseX() {
		mXAxis.release();
	}
	
	public final void releaseY() {
		mYAxis.release();
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public final void releaseZ() {
		mZAxis.release();
	}
	
	public final void releaseR() {
		mRotate.release();
	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */

	public void setMagnitude(float x, float y) {
		mXAxis.setMagnitude(x);
		mYAxis.setMagnitude(y);
	}
	
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public void setMagnitude(float x, float y, float z, float r) {
		mXAxis.setMagnitude(x);
		mYAxis.setMagnitude(y);
		mZAxis.setMagnitude(z);
		mRotate.setMagnitude(r);
		
//		DebugLog.d("Object", "InputXY setMagnitude() AFTER x,z,r = " + mXAxis.getMagnitude() + 
//				", " + mZAxis.getMagnitude() + ", " + mRotate.getMagnitude());
	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	public void reset() {
		mXAxis.reset();
		mYAxis.reset();
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		mZAxis.reset();
		mRotate.reset();
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public void clone(InputXY other) {
		if (other.getPressed()) {
			/* XXX Droid Code © 2012 FrostBlade LLC - Start */
			press(other.getLastPressedTime(), other.getX(), other.getY(),
					other.getZ(), other.getR());
			/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//			press(other.getLastPressedTime(), other.getX(), other.getY());
		} else {
			release();
		}
	}
}
