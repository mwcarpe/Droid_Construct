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

public final class GameParameters {	    
//    public static final float PI_OVER_180 = 0.0174532925f;
	
	public final static boolean debug = false;
    
    public static int viewWidth;
    public static int viewHeight;
	public static int gameWidth;
	public static int gameHeight;
	
	public static float viewScaleX;
	public static float viewScaleY;
	
	// mdpi=0, hdpi=1, xhdpi=2
	public static int screenDensity;
	
	public static int difficulty;
	
	public static int levelRow;
	
	public static boolean supportsDrawTexture;
	public static boolean supportsVBOs;
	
	public static boolean splashScreen;
	
	public static boolean splashScreenPlayed;
	
    // Lighting Value 0 = Low Lighting, 1 = Very Low Lighting, 2 = Zero Lighting
	public static int lightingValue;
	
	public static boolean light2Enabled;
	
	public static boolean gamePause;
	
	// FIXME TEMP. DELETE.
	public static int constructActivityCounter;
//	public static int constructActivityCounterSleep;
//	public static int droidSurfaceViewCounter;
	public static int gameCounter;
//	public static int gameTimerCounter;
	public static int gameCounterTouchDownMove;
	public static int gameCounterTouchUp;
//	public static int gameCounterTouchUpMaxTimes;
	public static int droidBottomComponentCounter;	// represents ObjectManager Counter; reports Log.i every 10 seconds
	public static int renderCounter;
	
	public static int constructTouchSleepCounter;
	public static int gameThreadSleepCounter;
	public static int rendererWaitDrawCompleteCounter;
	public static int drawQueueWaitCounter;
	public static int drawQueueHudWaitCounter;
	// FIXME END TEMP. DELETE.
	
//	// FIXME TEMP TEST DELETE
//	public static Object syncObject = new Object();
	
	public GameParameters() {
		supportsDrawTexture = false;
		supportsVBOs = false;
		
		splashScreen = false;
		
		splashScreenPlayed = false;
		
		lightingValue = 0;
		
		light2Enabled = false;
		
		gamePause = false;
	}
}
