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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

// TODO Re-enable - Animation, Collision
//import com.frostbladegames.droidconstruct.AnimationComponent.PlayerAnimations;
//import com.frostbladegames.droidconstruct.EnemyAnimationComponent.EnemyAnimations;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.animation.Animation;
//import com.frostbladegames.droidconstruct.GameObjectGroups.HitType;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;

import com.frostbladegames.basestation9.GameObjectGroups.BottomMoveType;
import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.SoundSystem.Sound;
import com.frostbladegames.basestation9.R;

/** A class for generating game objects at runtime.
 * This should really be replaced with something that is data-driven, but it is hard to do data
 * parsing quickly at runtime.  For the moment this class is full of large functions that just
 * patch pointers between objects, but in the future those functions should either be 
 * a) generated from data at compile time, or b) described by data at runtime.
 */
public class GameObjectFactory extends BaseObject {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;

	// FIXME Optimize number of Laser and Enemy Objects, and resulting Game Objects
	private static final int MAX_BACKGROUND_OBJECTS = 32;	// Max 9 Sections + 9 Wall_Laser + 9 Wall_Post
	private static final int MAX_BACKGROUND_COLLISION_OBJECTS = 48;	// Max 9 Sections w/ 1 set CollisionVolumes ea + Max 36 Platform CollisionVolumes
	private static final int MAX_FAR_BACKGROUND_OBJECTS = 1;
	private static final int MAX_DROID_OBJECTS = 1;		// Max 1 Droid
    private static final int MAX_WEAPON_OBJECTS = 8;	// Max 8 Weapon Types = 5 Droid + 3 Enemy Top Weapons
    private static final int MAX_LASER_OBJECTS = 10;	// Max 10 Each per Weapon/Laser Type
//    private static final int MAX_LASER_OBJECTS = 40;	// Max 40 Each per Weapon/Laser Type
    private static final int MAX_ENEMY_OBJECTS = 400;	// Max 42 Enemies Per Section x 9 Sections
//    private static final int MAX_ENEMY_OBJECTS = 384;	// Max 42 Enemies Per Section x 9 Sections
    private static final int MAX_ENEMY_LASER_OBJECTS = 20;	// Max 20 Enemy Laser Objects Active at any point in Game
//    private static final int MAX_ENEMY_LASER_OBJECTS = 80;	// Max 80 Enemy Laser Objects Active at any point in Game
    private static final int MAX_ENEMY_BOSS_OBJECTS = 1;	// Max 1 Boss Per Level
    private static final int MAX_ENEMY_BOSS_LASER_OBJECTS = 8;	// Max 8 Boss Laser Objects Active at any point in Game
    private static final int MAX_ASTRONAUT_OBJECTS = 112;	// Max 10 Astronauts Per Section x 9 Sections
//    private static final int MAX_ASTRONAUT_OBJECTS = 96;	// Max 10 Astronauts Per Section x 9 Sections
    private static final int MAX_PLATFORM_OBJECTS = 32;		// Allow for special levels w/ multiple start/end teleport platforms
    private static final int MAX_ITEM_OBJECTS = 32;	// Max 32 Items Per Level (Max 3 Items Per Section + buffer)
    private static final int MAX_SPECIAL_EFFECT_SETS = 16;	// Max 16 Special Effect Objects Per Set
//    private static final int MAX_SPECIAL_EFFECT_OBJECTS = 16;	// Max 16 Special Effect Objects Per Set (x 4 Steps x 4 Per Level as below)
    
    /* Droid and Enemy Objects * 2 for Bottom and Top. Max 2 Enemy Laser Object Types.
     * Astronaut Top + 4 Animation Steps.  Special Effects Sets * 4 Objects Per Set * 6 Types. */
    private static final int MAX_GAME_OBJECTS = MAX_BACKGROUND_OBJECTS + MAX_BACKGROUND_COLLISION_OBJECTS + MAX_FAR_BACKGROUND_OBJECTS + 
    	(MAX_DROID_OBJECTS * 2) + MAX_WEAPON_OBJECTS + (MAX_LASER_OBJECTS * MAX_WEAPON_OBJECTS) + (MAX_ENEMY_OBJECTS * 2) +
    	(MAX_ENEMY_LASER_OBJECTS * 2) + (MAX_ENEMY_BOSS_OBJECTS * 2) + MAX_ENEMY_BOSS_LASER_OBJECTS + (MAX_ASTRONAUT_OBJECTS * 5) +
    	MAX_PLATFORM_OBJECTS + MAX_ITEM_OBJECTS + (MAX_SPECIAL_EFFECT_SETS * 4 * 6);
    
//    private static final int MAX_LASER_TYPES = 10;
    
    public static final int MAX_DROID_LIFE = 3;
    
    // Activation and Attack Radius
    private static final float ACTIVATION_RADIUS_SCREEN = 10.0f;
    private static final float ACTIVATION_RADIUS_NORMAL = 20.0f;
//    private static final float ACTIVATION_RADIUS_NORMAL = 25.0f;
    private static final float ACTIVATION_RADIUS_ALWAYS = -1.0f;
    private static final float ATTACK_RADIUS = 10.0f;
//	private static final float VIEWSCREEN_RADIUS = 10.0f;  // = (3D Background Rectangle Width 1.25 * # Rectangles Across Width 9.2 * Ratio (1.8/1.5)) / 2
////	private static final float VIEWSCREEN_RADIUS = 6.8f;
	
    // Enemy HitPoints
	private static final int HIT_POINTS_ENEMY_EM_OT = 3;
	private static final int HIT_POINTS_ENEMY_EM_OW = 2;
	private static final int HIT_POINTS_ENEMY_EM_SL = 3;
	private static final int HIT_POINTS_ENEMY_HD_FL = 1;
	private static final int HIT_POINTS_ENEMY_HD_OL = 2;
	private static final int HIT_POINTS_ENEMY_HD_OT = 2;
	private static final int HIT_POINTS_ENEMY_HD_OW = 2;
	private static final int HIT_POINTS_ENEMY_HD_SL = 2;
	private static final int HIT_POINTS_ENEMY_HD_TL = 2;
	private static final int HIT_POINTS_ENEMY_HD_TT = 3;
	private static final int HIT_POINTS_ENEMY_HD_TW = 2;
	private static final int HIT_POINTS_ENEMY_LC_FM = 5;
	private static final int HIT_POINTS_ENEMY_LC_OT = 3;
	private static final int HIT_POINTS_ENEMY_LC_SL = 3;
	private static final int HIT_POINTS_ENEMY_LC_TT = 4;
	private static final int HIT_POINTS_ENEMY_LS_FM = 5;
	private static final int HIT_POINTS_ENEMY_LS_TT = 4;
	private static final int HIT_POINTS_ENEMY_TA_FL = 2;
	private static final int HIT_POINTS_ENEMY_EM_OW_BOSS = 15;
	private static final int HIT_POINTS_ENEMY_EM_SL_BOSS = 35;
	private static final int HIT_POINTS_ENEMY_HD_TL_BOSS = 10;
	private static final int HIT_POINTS_ENEMY_HD_TT_BOSS = 40;
	private static final int HIT_POINTS_ENEMY_LC_OT_BOSS = 20;
	private static final int HIT_POINTS_ENEMY_LC_SL_BOSS = 45;
	private static final int HIT_POINTS_ENEMY_LS_TT_BOSS = 30;
	private static final int HIT_POINTS_ENEMY_TA_FL_BOSS = 25;
	private static final int HIT_POINTS_ENEMY_DR_TT_BOSS = 50;
	
	private int mHitPointsEnemyEMOT;
	private int mHitPointsEnemyEMOW;
	private int mHitPointsEnemyEMSL;
	private int mHitPointsEnemyHDFL;
	private int mHitPointsEnemyHDOL;
	private int mHitPointsEnemyHDOT;
	private int mHitPointsEnemyHDOW;
	private int mHitPointsEnemyHDSL;
	private int mHitPointsEnemyHDTL;
	private int mHitPointsEnemyHDTT;
	private int mHitPointsEnemyHDTW;
	private int mHitPointsEnemyLCFM;
	private int mHitPointsEnemyLCOT;
	private int mHitPointsEnemyLCSL;
	private int mHitPointsEnemyLCTT;
	private int mHitPointsEnemyLSFM;
	private int mHitPointsEnemyLSTT;
	private int mHitPointsEnemyTAFL;
	private int mHitPointsEnemyEMOWBoss;
	private int mHitPointsEnemyEMSLBoss;
	private int mHitPointsEnemyHDTLBoss;
	private int mHitPointsEnemyHDTTBoss;
	private int mHitPointsEnemyLCOTBoss;
	private int mHitPointsEnemyLCSLBoss;
	private int mHitPointsEnemyLSTTBoss;
	private int mHitPointsEnemyTAFLBoss;
	private int mHitPointsEnemyDRTTBoss;
	
    // Enemy Magnitude
	private static final float MAGNITUDE_ENEMY_EM_OT = 0.03f;
	private static final float MAGNITUDE_ENEMY_EM_OW = 0.04f;
	private static final float MAGNITUDE_ENEMY_EM_SL = 0.02f;
	private static final float MAGNITUDE_ENEMY_HD_FL = 0.05f;
	private static final float MAGNITUDE_ENEMY_HD_OL = 0.03f;
	private static final float MAGNITUDE_ENEMY_HD_OT = 0.03f;
	private static final float MAGNITUDE_ENEMY_HD_OW = 0.04f;
	private static final float MAGNITUDE_ENEMY_HD_SL = 0.02f;
	private static final float MAGNITUDE_ENEMY_HD_TL = 0.03f;
	private static final float MAGNITUDE_ENEMY_HD_TT = 0.025f;
	private static final float MAGNITUDE_ENEMY_HD_TW = 0.04f;
	private static final float MAGNITUDE_ENEMY_LC_FM = 0.00001f;
	private static final float MAGNITUDE_ENEMY_LC_OT = 0.03f;
	private static final float MAGNITUDE_ENEMY_LC_SL = 0.02f;
	private static final float MAGNITUDE_ENEMY_LC_TT = 0.025f;
	private static final float MAGNITUDE_ENEMY_LS_FM = 0.00001f;
	private static final float MAGNITUDE_ENEMY_LS_TT = 0.025f;
	private static final float MAGNITUDE_ENEMY_TA_FL = 0.06f;
	private static final float MAGNITUDE_ENEMY_EM_OW_BOSS = 0.06f;
	private static final float MAGNITUDE_ENEMY_EM_SL_BOSS = 0.035f;
	private static final float MAGNITUDE_ENEMY_HD_TL_BOSS = 0.04f;
	private static final float MAGNITUDE_ENEMY_HD_TT_BOSS = 0.04f;
	private static final float MAGNITUDE_ENEMY_LC_OT_BOSS = 0.05f;
	private static final float MAGNITUDE_ENEMY_LC_SL_BOSS = 0.035f;
	private static final float MAGNITUDE_ENEMY_LS_TT_BOSS = 0.04f;
	private static final float MAGNITUDE_ENEMY_TA_FL_BOSS = 0.075f;
	private static final float MAGNITUDE_ENEMY_DR_TT_BOSS = 0.06f;
	
//	private static final float MAGNITUDE_ENEMY_EM_OT = 0.04f;
//	private static final float MAGNITUDE_ENEMY_EM_OW = 0.05f;
//	private static final float MAGNITUDE_ENEMY_EM_SL = 0.025f;
//	private static final float MAGNITUDE_ENEMY_HD_FL = 0.06f;
//	private static final float MAGNITUDE_ENEMY_HD_OL = 0.03f;
//	private static final float MAGNITUDE_ENEMY_HD_OT = 0.04f;
//	private static final float MAGNITUDE_ENEMY_HD_OW = 0.05f;
//	private static final float MAGNITUDE_ENEMY_HD_SL = 0.025f;
//	private static final float MAGNITUDE_ENEMY_HD_TL = 0.03f;
//	private static final float MAGNITUDE_ENEMY_HD_TT = 0.03f;
//	private static final float MAGNITUDE_ENEMY_HD_TW = 0.05f;
//	private static final float MAGNITUDE_ENEMY_LC_FM = 0.00001f;
//	private static final float MAGNITUDE_ENEMY_LC_OT = 0.04f;
//	private static final float MAGNITUDE_ENEMY_LC_SL = 0.025f;
//	private static final float MAGNITUDE_ENEMY_LC_TT = 0.03f;
//	private static final float MAGNITUDE_ENEMY_LS_FM = 0.00001f;
//	private static final float MAGNITUDE_ENEMY_LS_TT = 0.03f;
//	private static final float MAGNITUDE_ENEMY_TA_FL = 0.08f;
//	private static final float MAGNITUDE_ENEMY_EM_OW_BOSS = 0.07f;
//	private static final float MAGNITUDE_ENEMY_EM_SL_BOSS = 0.045f;
//	private static final float MAGNITUDE_ENEMY_HD_TL_BOSS = 0.05f;
//	private static final float MAGNITUDE_ENEMY_HD_TT_BOSS = 0.05f;
//	private static final float MAGNITUDE_ENEMY_LC_OT_BOSS = 0.06f;
//	private static final float MAGNITUDE_ENEMY_LC_SL_BOSS = 0.045f;
//	private static final float MAGNITUDE_ENEMY_LS_TT_BOSS = 0.05f;
//	private static final float MAGNITUDE_ENEMY_TA_FL_BOSS = 0.1f;
//	private static final float MAGNITUDE_ENEMY_DR_TT_BOSS = 0.07f;
	
	private float mMagnitudeEnemyEMOT;
	private float mMagnitudeEnemyEMOW;
	private float mMagnitudeEnemyEMSL;
	private float mMagnitudeEnemyHDFL;
	private float mMagnitudeEnemyHDOL;
	private float mMagnitudeEnemyHDOT;
	private float mMagnitudeEnemyHDOW;
	private float mMagnitudeEnemyHDSL;
	private float mMagnitudeEnemyHDTL;
	private float mMagnitudeEnemyHDTT;
	private float mMagnitudeEnemyHDTW;
	private float mMagnitudeEnemyLCFM;
	private float mMagnitudeEnemyLCOT;
	private float mMagnitudeEnemyLCSL;
	private float mMagnitudeEnemyLCTT;
	private float mMagnitudeEnemyLSFM;
	private float mMagnitudeEnemyLSTT;
	private float mMagnitudeEnemyTAFL;
	private float mMagnitudeEnemyEMOWBoss;
	private float mMagnitudeEnemyEMSLBoss;
	private float mMagnitudeEnemyHDTLBoss;
	private float mMagnitudeEnemyHDTTBoss;
	private float mMagnitudeEnemyLCOTBoss;
	private float mMagnitudeEnemyLCSLBoss;
	private float mMagnitudeEnemyLSTTBoss;
	private float mMagnitudeEnemyTAFLBoss;
	private float mMagnitudeEnemyDRTTBoss;
	
	// GameObject Offsets
	// Weapon GameObject Offsets
	private final Vector3 mDroidWeaponLaserStdOffset = new Vector3(0.242f, 0.0f, -0.424f, 0.0f);
	private final Vector3 mDroidWeaponLaserPulseOffset = new Vector3(0.242f, 0.0f, -0.521f, 0.0f);
	private final Vector3 mDroidWeaponLaserEmpOffset = new Vector3(0.242f, 0.0f, -0.328f, 0.0f);
	private final Vector3 mDroidWeaponLaserGrenadeOffset = new Vector3(0.242f, 0.0f, -0.376f, 0.0f);
	private final Vector3 mDroidWeaponLaserRocketOffset = new Vector3(0.242f, 0.0f, -0.671f, 0.0f);
	// Droid Laser GameObject Offsets
	private final Vector3 mDroidLaserStdOffset = new Vector3(0.242f, 0.0f, -0.769f, 0.0f);
	private final Vector3 mDroidLaserPulseOffset = new Vector3(0.242f, 0.0f, -0.927f, 0.0f);
	private final Vector3 mDroidLaserEmpOffset = new Vector3(0.242f, 0.0f, -0.544f, 0.0f);
	private final Vector3 mDroidLaserGrenadeOffset = new Vector3(0.242f, 0.0f, -0.585f, 0.0f);
	private final Vector3 mDroidLaserRocketOffset = new Vector3(0.242f, 0.0f, -1.352f, 0.0f);
	// Enemy GameObject Offsets
	// Allows Flying Enemy Bottom z Rotate
	private final Vector3 mEnemyHDFLOffset = new Vector3(0.0f, 0.581f, 0.266f, 0.0f);
	private final Vector3 mEnemyTAFLOffset = new Vector3(0.0f, 0.581f, 0.266f, 0.0f);
	private final Vector3 mEnemyTAFLBossOffset = new Vector3(0.0f, 0.584f, 0.395f, 0.0f);
	// Enemy Laser GameObject Offsets
	private final Vector3 mEnemyLaserStdOffset = new Vector3(0.0f, 0.0f, -0.561f, 0.0f);
	private final Vector3 mEnemyLaserEmpOffset = new Vector3(0.0f, 0.0f, -0.430f, 0.0f);
	// Enemy Boss Laser GameObject Offsets
	private final Vector3 mEnemyBossLaserStdOffset = new Vector3(0.0f, 0.0f, -0.840f, 0.0f);
	private final Vector3 mEnemyBossLaserEmpOffset = new Vector3(0.0f, 0.0f, -0.575f, 0.0f);
	private final Vector3 mEnemyBossLaserSmallFlyingEnemyOffset = new Vector3(0.313f, 0.0f, -1.854f, 0.0f);
    
    private static final ComponentPoolComparator sComponentPoolComparator = new ComponentPoolComparator();
    private FixedSizeArray<GameComponentPool> mComponentPools;
    private GameComponentPool mPoolSearchDummy;
    private GameObjectPool mGameObjectPool;
    
    public Context context;
    
	private static FloatBuffer mZeroBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
	private static FloatBuffer mLowBuffer = FloatBuffer.wrap(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
	private static FloatBuffer mMidBuffer = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
	private static FloatBuffer mHighBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
	private static FloatBuffer mMaxBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
	
	private static FloatBuffer mBlueLaserBuffer = FloatBuffer.wrap(new float[]{0.128f, 0.207f, 0.7f, 1.0f});
	private static FloatBuffer mRedLaserBuffer = FloatBuffer.wrap(new float[]{0.7f, 0.1f, 0.1f, 1.0f});
	private static FloatBuffer mGreenLaserBuffer = FloatBuffer.wrap(new float[]{0.128f, 0.7f, 0.207f, 1.0f});
    
	private static FloatBuffer mLight0PositionBuffer = FloatBuffer.wrap(new float[]{0.0f, 5.0f, 5.0f, 0.0f});
	private static FloatBuffer mLight1PositionBuffer = FloatBuffer.wrap(new float[]{-3.0f, 1.5f, 1.0f, 1.0f});
	private static FloatBuffer mLightSpotDirection = FloatBuffer.wrap(new float[]{0.0f, 0.0f, -1.0f});
	private static FloatBuffer mLightSpotCutoff = FloatBuffer.wrap(new float[]{4.0f});

	private static FloatBuffer mMatShininessZeroBuffer = FloatBuffer.wrap(new float[]{0.0f});
	private static FloatBuffer mMatShininessLowBuffer = FloatBuffer.wrap(new float[]{5.0f});
	private static FloatBuffer mMatShininessMidBuffer = FloatBuffer.wrap(new float[]{50.0f});
	private static FloatBuffer mMatShininessHighBuffer = FloatBuffer.wrap(new float[]{100.0f});
	private static FloatBuffer mMatShininessMaxBuffer = FloatBuffer.wrap(new float[]{128.0f});
    
    private int mGameObjectIdCount = 0;
    
    // FIXME TEMP ONLY
    private int mLineSegmentIdCount = 0;
    
    // FIXME DELETE TEMP
    private int mBackgroundCountTemp = 1;
    
    private boolean mSplashScreenCheck;
    
    private Type mWeaponActiveType;
    private Type mWeaponInventoryType;
    
//    private DrawableDroid mWallLaserBlueDrawableDroidObject;
//    private DrawableDroid mWallPostGoldDrawableDroidObject;
    
    // TODO Replace GameObjectFactory mDroidBottomGameObject with GameObjectManager droidBottomGameObject?
    // Game Objects
    private GameObject mDroidBottomGameObject;
    private GameObject mDroidTopGameObject;
    
    private GameObject mEnemyEMOTBottomGameObject;
    private GameObject mEnemyEMOWBottomGameObject;
    private GameObject mEnemyEMSLBottomGameObject;
    private GameObject mEnemyHDFLBottomGameObject;
    private GameObject mEnemyHDOLBottomGameObject;
    private GameObject mEnemyHDOTBottomGameObject;
    private GameObject mEnemyHDOWBottomGameObject;
    private GameObject mEnemyHDSLBottomGameObject;
    private GameObject mEnemyHDTLBottomGameObject;
    private GameObject mEnemyHDTTBottomGameObject;
    private GameObject mEnemyHDTWBottomGameObject;
    private GameObject mEnemyLCFMBottomGameObject;
    private GameObject mEnemyLCOTBottomGameObject;
    private GameObject mEnemyLCSLBottomGameObject;
    private GameObject mEnemyLCTTBottomGameObject;
    private GameObject mEnemyLSFMBottomGameObject;
    private GameObject mEnemyLSTTBottomGameObject;
    private GameObject mEnemyTAFLBottomGameObject;
    private GameObject mEnemyBossBottomGameObject;
    
    private FixedSizeArray<GameObject> mEnemyLaserStdGameObjectArray;
    private FixedSizeArray<GameObject> mEnemyLaserEmpGameObjectArray;
    
    private GameObject mFinalLevel09Astronaut1;
    private GameObject mFinalLevel09Astronaut2;
    private GameObject mFinalLevel09Astronaut3;
    private GameObject mFinalLevel09Astronaut4;
    private GameObject mFinalLevel09Astronaut5;
    
    private int mFinalLevel09AstronautCount;
    
    // Game Objects - Astronaut Appendages
    private FixedSizeArray<GameObject> mAstronautAnimationArray;
    
    // LevelStart, LevelEnd, SectionStart, SectionEnd, Elevator
    // nextSectionPosition for SectionStart as temp ref from next SectionEnd
    private Vector3 mNextSectionPositionTemp;
    // previousSection GameObject for SectionEnd as temp ref from previous SectionStart
    private GameObject mPreviousSectionGameObjectTemp;
    
    // Elevator GameObjects and Collision Volumes for up to 5 Elevators per Level
//    private int mElevatorCount;
    private GameObject mElevatorCollisionGameObject00A;
    private GameObject mElevatorCollisionGameObject01A;
    private GameObject mElevatorCollisionGameObject02A;
    private GameObject mElevatorCollisionGameObject03A;    
    private GameObject mElevatorCollisionGameObject00B;
    private GameObject mElevatorCollisionGameObject01B;
    private GameObject mElevatorCollisionGameObject02B;
    private GameObject mElevatorCollisionGameObject03B; 
    private GameObject mElevatorCollisionGameObject00C;
    private GameObject mElevatorCollisionGameObject01C;
    private GameObject mElevatorCollisionGameObject02C;
    private GameObject mElevatorCollisionGameObject03C; 
    private GameObject mElevatorCollisionGameObject00D;
    private GameObject mElevatorCollisionGameObject01D;
    private GameObject mElevatorCollisionGameObject02D;
    private GameObject mElevatorCollisionGameObject03D; 
    private GameObject mElevatorCollisionGameObject00E;
    private GameObject mElevatorCollisionGameObject01E;
    private GameObject mElevatorCollisionGameObject02E;
    private GameObject mElevatorCollisionGameObject03E; 
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes00A;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes01A;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes02A;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes03A;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes00B;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes01B;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes02B;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes03B;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes00C;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes01C;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes02C;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes03C;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes00D;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes01D;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes02D;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes03D;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes00E;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes01E;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes02E;
    private FixedSizeArray<LineSegmentCollisionVolume> mElevatorCollisionVolumes03E;
    
    private GameObject mFinalLevel09BeaconGameObject;
    
    // Drawable Objects
    // Drawable Objects - Splashscreen
    private DrawableDroid mSplashLogoDrawableDroidObject;
    private DrawableDroid mSplashBackgroundDrawableDroidObject;
    
    // Drawable Objects - FarBackground
    private DrawableFarBackground mFarBackgroundDrawableDroidObject;
    
    // Drawable Objects - Background
    private DrawableDroid mBackgroundSection00DrawableDroidObject;
    private DrawableDroid mBackgroundSection01DrawableDroidObject;
    private DrawableDroid mBackgroundSection02DrawableDroidObject;
    private DrawableDroid mBackgroundSection03DrawableDroidObject;
    private DrawableDroid mBackgroundSection04DrawableDroidObject;
    private DrawableDroid mBackgroundSection05DrawableDroidObject;
    private DrawableDroid mBackgroundSection06DrawableDroidObject;
    private DrawableDroid mBackgroundSection07DrawableDroidObject;
    private DrawableDroid mBackgroundSection08DrawableDroidObject;
    private DrawableDroid mBackgroundSection09DrawableDroidObject;
    
    // Drawable Objects - BackgroundWall
    private DrawableDroid mBackgroundWallLaserSection01DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection02DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection03DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection04DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection05DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection06DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection07DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection08DrawableDroidObject;
    private DrawableDroid mBackgroundWallLaserSection09DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection01DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection02DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection03DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection04DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection05DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection06DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection07DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection08DrawableDroidObject;
    private DrawableDroid mBackgroundWallPostSection09DrawableDroidObject;
    
    // Drawable Objects - Droid
    private DrawableDroid mDroidBottomDrawableDroidObject;
    private DrawableDroid mDroidTopDrawableDroidObject;
    
    // Drawable Objects - Droid Weapons
    private DrawableDroid mDroidWeaponLaserStdDrawableDroidObject;
    private DrawableDroid mDroidWeaponLaserPulseDrawableDroidObject;
    private DrawableDroid mDroidWeaponLaserEmpDrawableDroidObject;
    private DrawableDroid mDroidWeaponLaserGrenadeDrawableDroidObject;
    private DrawableDroid mDroidWeaponLaserRocketDrawableDroidObject;
    
    // Drawable Objects - Droid Lasers
    private DrawableDroid mDroidLaserStdDrawableDroidObject;
    private DrawableDroid mDroidLaserPulseDrawableDroidObject;
    private DrawableDroid mDroidLaserEmpDrawableDroidObject;
    private DrawableDroid mDroidLaserGrenadeDrawableDroidObject;
    private DrawableDroid mDroidLaserRocketDrawableDroidObject;
    
    // Drawable Objects - Enemies
    private DrawableDroid mEnemyEMOTBottomDrawableDroidObject;
    private DrawableDroid mEnemyEMOTTopDrawableDroidObject;
    private DrawableDroid mEnemyEMOWBottomDrawableDroidObject;
    private DrawableDroid mEnemyEMOWTopDrawableDroidObject;
    private DrawableDroid mEnemyEMOWBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyEMOWBossTopDrawableDroidObject;
    private DrawableDroid mEnemyEMSLBottomDrawableDroidObject;
    private DrawableDroid mEnemyEMSLTopDrawableDroidObject;
    private DrawableDroid mEnemyEMSLBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyEMSLBossTopDrawableDroidObject;
    private DrawableDroid mEnemyHDFLBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDFLTopDrawableDroidObject;
    private DrawableDroid mEnemyHDOLBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDOLTopDrawableDroidObject;
    private DrawableDroid mEnemyHDOTBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDOTTopDrawableDroidObject;
    private DrawableDroid mEnemyHDOWBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDOWTopDrawableDroidObject;
    private DrawableDroid mEnemyHDSLBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDSLTopDrawableDroidObject;
    private DrawableDroid mEnemyHDTLBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDTLTopDrawableDroidObject;
    private DrawableDroid mEnemyHDTLBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDTLBossTopDrawableDroidObject;
    private DrawableDroid mEnemyHDTTBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDTTTopDrawableDroidObject;
    private DrawableDroid mEnemyHDTTBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDTTBossTopDrawableDroidObject;
    private DrawableDroid mEnemyHDTWBottomDrawableDroidObject;
    private DrawableDroid mEnemyHDTWTopDrawableDroidObject;
    private DrawableDroid mEnemyLCFMBottomDrawableDroidObject;
    private DrawableDroid mEnemyLCFMTopDrawableDroidObject;
    private DrawableDroid mEnemyLCOTBottomDrawableDroidObject;
    private DrawableDroid mEnemyLCOTTopDrawableDroidObject;
    private DrawableDroid mEnemyLCOTBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyLCOTBossTopDrawableDroidObject;
    private DrawableDroid mEnemyLCSLBottomDrawableDroidObject;
    private DrawableDroid mEnemyLCSLTopDrawableDroidObject;
    private DrawableDroid mEnemyLCSLBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyLCSLBossTopDrawableDroidObject;
    private DrawableDroid mEnemyLCTTBottomDrawableDroidObject;
    private DrawableDroid mEnemyLCTTTopDrawableDroidObject;
    private DrawableDroid mEnemyLSFMBottomDrawableDroidObject;
    private DrawableDroid mEnemyLSFMTopDrawableDroidObject;
    private DrawableDroid mEnemyLSTTBottomDrawableDroidObject;
    private DrawableDroid mEnemyLSTTTopDrawableDroidObject;
    private DrawableDroid mEnemyLSTTBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyLSTTBossTopDrawableDroidObject;
    private DrawableDroid mEnemyTAFLBottomDrawableDroidObject;
    private DrawableDroid mEnemyTAFLTopDrawableDroidObject;
    private DrawableDroid mEnemyTAFLBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyTAFLBossTopDrawableDroidObject;
    private DrawableDroid mEnemyDRTTBossBottomDrawableDroidObject;
    private DrawableDroid mEnemyDRTTBossTopDrawableDroidObject;
    
    // Drawable Objects - Enemy Lasers    
    private DrawableDroid mEnemyLaserStdDrawableDroidObject;
    private DrawableDroid mEnemyLaserEmpDrawableDroidObject;
    private DrawableDroid mEnemyBossLaserDrawableDroidObject;
    
    // Drawable Objects - Astronauts
    private DrawableDroid mAstronautPrivateTopDrawableDroidObject;
    private DrawableDroid mAstronautSergeantTopDrawableDroidObject;
    private DrawableDroid mAstronautCaptainTopDrawableDroidObject;
    private DrawableDroid mAstronautGeneralTopDrawableDroidObject;
    private DrawableDroid mAstronautBottomFrame1DrawableDroidObject;
    private DrawableDroid mAstronautBottomFrame2DrawableDroidObject;
    private DrawableDroid mAstronautBottomFrame3DrawableDroidObject;
    private DrawableDroid mAstronautBottomFrame4DrawableDroidObject;
    
    // Drawable Objects - Platforms
    private DrawableDroid mPlatformLevelStartDrawableDroidObject;
    private DrawableDroid mPlatformLevelEndDrawableDroidObject;
    private DrawableDroid mPlatformSectionStartDrawableDroidObject;
    private DrawableDroid mPlatformSectionEndDrawableDroidObject;
    private DrawableDroid mPlatformElevator0DegDrawableDroidObject;
    private DrawableDroid mPlatformElevator45DegDrawableDroidObject;
    
    // Drawable Objects - Items
    private DrawableDroid mItemCrateWoodDrawableDroidObject;
    private DrawableDroid mItemCrateMetalDrawableDroidObject;
    private DrawableDroid mItemLightBeaconDrawableDroidObject;
    private DrawableDroid mItemPistonEngineDrawableDroidObject;
    private DrawableDroid mItemTableDrawableDroidObject;
    private DrawableDroid mItemTableComputerDrawableDroidObject;
    private DrawableDroid mItemSpaceshipDrawableDroidObject;
    private DrawableDroid mItemShapeshipDoorDrawableDroidObject;
    
    // Drawable Objects - Special Effects
    private DrawableDroid mExplosionFrame1DrawableDroidObject;
    private DrawableDroid mExplosionFrame2DrawableDroidObject;
    private DrawableDroid mExplosionFrame3DrawableDroidObject;
    private DrawableDroid mExplosionFrame4DrawableDroidObject;
    private DrawableDroid mElectricRingFrame1DrawableDroidObject;
    private DrawableDroid mElectricRingFrame2DrawableDroidObject;
    private DrawableDroid mElectricRingFrame3DrawableDroidObject;
    private DrawableDroid mElectricRingFrame4DrawableDroidObject;
    private DrawableDroid mElectricityFrame1DrawableDroidObject;
    private DrawableDroid mElectricityFrame2DrawableDroidObject;
    private DrawableDroid mElectricityFrame3DrawableDroidObject;
    private DrawableDroid mElectricityFrame4DrawableDroidObject;
    private DrawableDroid mExplosionLargeFrame1DrawableDroidObject;
    private DrawableDroid mExplosionLargeFrame2DrawableDroidObject;
    private DrawableDroid mExplosionLargeFrame3DrawableDroidObject;
    private DrawableDroid mExplosionLargeFrame4DrawableDroidObject;
    private DrawableDroid mExplosionRingFrame1DrawableDroidObject;
    private DrawableDroid mExplosionRingFrame2DrawableDroidObject;
    private DrawableDroid mExplosionRingFrame3DrawableDroidObject;
    private DrawableDroid mExplosionRingFrame4DrawableDroidObject;
    private DrawableDroid mTeleportRingFrame1DrawableDroidObject;
    private DrawableDroid mTeleportRingFrame2DrawableDroidObject;
    private DrawableDroid mTeleportRingFrame3DrawableDroidObject;
    private DrawableDroid mTeleportRingFrame4DrawableDroidObject;
    
//    // Object Activation Radius
//    private float mScreenActivationRadius;  // = radius * 1.0
////    private float mMinimumActivationRadius;  // = radius * 0.5
//    private float mEnemyActivationRadius;	// = radius * 1.25
//    private float mNormalActivationRadius;  // = radius * 3.0
////    private float mWideActivationRadius;  // = radius * 3.0
//    private float mAlwaysActive;
    
    private MediaPlayer mBackgroundMusic;
    private boolean mMusicEnabled;
	public boolean musicPlaying;
    
    public GameObjectFactory() {
        super();       
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "GameObjectFactory <constructor>");	
    	}
        
//        Log.i("Object", "GameObjectFactory <constructor> new GameObjectPool() START");
    	
    	mSplashScreenCheck = false;
        
        mGameObjectPool = new GameObjectPool(MAX_GAME_OBJECTS);
        
//        Log.i("Object", "GameObjectFactory <constructor> new GameObjectPool() END");
        
        // FIXME Is this required?
        final int objectTypeCount = Type.OBJECT_COUNT.ordinal();
        
        mNextSectionPositionTemp = new Vector3();
        
//        mElevatorCount = 0;
        
        int hpDifficulty = 1;
        float magnitudeDifficulty = 1.0f;
        
        if (GameParameters.difficulty == 1) {
        	hpDifficulty = 2;
        	magnitudeDifficulty = 1.5f;
        } else if (GameParameters.difficulty == 2) {
        	hpDifficulty = 3;
        	magnitudeDifficulty = 2.0f;
        }
        
    	mHitPointsEnemyEMOT = HIT_POINTS_ENEMY_EM_OT * hpDifficulty;
    	mHitPointsEnemyEMOW = HIT_POINTS_ENEMY_EM_OW * hpDifficulty;
    	mHitPointsEnemyEMSL = HIT_POINTS_ENEMY_EM_SL * hpDifficulty;
    	mHitPointsEnemyHDFL = HIT_POINTS_ENEMY_HD_FL * hpDifficulty;
    	mHitPointsEnemyHDOL = HIT_POINTS_ENEMY_HD_OL * hpDifficulty;
    	mHitPointsEnemyHDOT = HIT_POINTS_ENEMY_HD_OT * hpDifficulty;
    	mHitPointsEnemyHDOW = HIT_POINTS_ENEMY_HD_OW * hpDifficulty;
    	mHitPointsEnemyHDSL = HIT_POINTS_ENEMY_HD_SL * hpDifficulty;
    	mHitPointsEnemyHDTL = HIT_POINTS_ENEMY_HD_TL * hpDifficulty;
    	mHitPointsEnemyHDTT = HIT_POINTS_ENEMY_HD_TT * hpDifficulty;
    	mHitPointsEnemyHDTW = HIT_POINTS_ENEMY_HD_TW * hpDifficulty;
    	mHitPointsEnemyLCFM = HIT_POINTS_ENEMY_LC_FM * hpDifficulty;
    	mHitPointsEnemyLCOT = HIT_POINTS_ENEMY_LC_OT * hpDifficulty;
    	mHitPointsEnemyLCSL = HIT_POINTS_ENEMY_LC_SL * hpDifficulty;
    	mHitPointsEnemyLCTT = HIT_POINTS_ENEMY_LC_TT * hpDifficulty;
    	mHitPointsEnemyLSFM = HIT_POINTS_ENEMY_LS_FM * hpDifficulty;
    	mHitPointsEnemyLSTT = HIT_POINTS_ENEMY_LS_TT * hpDifficulty;
    	mHitPointsEnemyTAFL = HIT_POINTS_ENEMY_TA_FL * hpDifficulty;
    	mHitPointsEnemyEMOWBoss = HIT_POINTS_ENEMY_EM_OW_BOSS * hpDifficulty;
    	mHitPointsEnemyEMSLBoss = HIT_POINTS_ENEMY_EM_SL_BOSS * hpDifficulty;
    	mHitPointsEnemyHDTLBoss = HIT_POINTS_ENEMY_HD_TL_BOSS * hpDifficulty;
    	mHitPointsEnemyHDTTBoss = HIT_POINTS_ENEMY_HD_TT_BOSS * hpDifficulty;
    	mHitPointsEnemyLCOTBoss = HIT_POINTS_ENEMY_LC_OT_BOSS * hpDifficulty;
    	mHitPointsEnemyLCSLBoss = HIT_POINTS_ENEMY_LC_SL_BOSS * hpDifficulty;
    	mHitPointsEnemyLSTTBoss = HIT_POINTS_ENEMY_LS_TT_BOSS * hpDifficulty;
    	mHitPointsEnemyTAFLBoss = HIT_POINTS_ENEMY_TA_FL_BOSS * hpDifficulty;
    	mHitPointsEnemyDRTTBoss = HIT_POINTS_ENEMY_DR_TT_BOSS * hpDifficulty;
    	
    	mMagnitudeEnemyEMOT = MAGNITUDE_ENEMY_EM_OT * magnitudeDifficulty;
    	mMagnitudeEnemyEMOW = MAGNITUDE_ENEMY_EM_OW * magnitudeDifficulty;
    	mMagnitudeEnemyEMSL = MAGNITUDE_ENEMY_EM_SL * magnitudeDifficulty;
    	mMagnitudeEnemyHDFL = MAGNITUDE_ENEMY_HD_FL * magnitudeDifficulty;
    	mMagnitudeEnemyHDOL = MAGNITUDE_ENEMY_HD_OL * magnitudeDifficulty;
    	mMagnitudeEnemyHDOT = MAGNITUDE_ENEMY_HD_OT * magnitudeDifficulty;
    	mMagnitudeEnemyHDOW = MAGNITUDE_ENEMY_HD_OW * magnitudeDifficulty;
    	mMagnitudeEnemyHDSL = MAGNITUDE_ENEMY_HD_SL * magnitudeDifficulty;
    	mMagnitudeEnemyHDTL = MAGNITUDE_ENEMY_HD_TL * magnitudeDifficulty;
    	mMagnitudeEnemyHDTT = MAGNITUDE_ENEMY_HD_TT * magnitudeDifficulty;
    	mMagnitudeEnemyHDTW = MAGNITUDE_ENEMY_HD_TW * magnitudeDifficulty;
    	mMagnitudeEnemyLCFM = MAGNITUDE_ENEMY_LC_FM * magnitudeDifficulty;
    	mMagnitudeEnemyLCOT = MAGNITUDE_ENEMY_LC_OT * magnitudeDifficulty;
    	mMagnitudeEnemyLCSL = MAGNITUDE_ENEMY_LC_SL * magnitudeDifficulty;
    	mMagnitudeEnemyLCTT = MAGNITUDE_ENEMY_LC_TT * magnitudeDifficulty;
    	mMagnitudeEnemyLSFM = MAGNITUDE_ENEMY_LS_FM * magnitudeDifficulty;
    	mMagnitudeEnemyLSTT = MAGNITUDE_ENEMY_LS_TT * magnitudeDifficulty;
    	mMagnitudeEnemyTAFL = MAGNITUDE_ENEMY_TA_FL * magnitudeDifficulty;
    	mMagnitudeEnemyEMOWBoss = MAGNITUDE_ENEMY_EM_OW_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyEMSLBoss = MAGNITUDE_ENEMY_EM_SL_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyHDTLBoss = MAGNITUDE_ENEMY_HD_TL_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyHDTTBoss = MAGNITUDE_ENEMY_HD_TT_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyLCOTBoss = MAGNITUDE_ENEMY_LC_OT_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyLCSLBoss = MAGNITUDE_ENEMY_LC_SL_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyLSTTBoss = MAGNITUDE_ENEMY_LS_TT_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyTAFLBoss = MAGNITUDE_ENEMY_TA_FL_BOSS * magnitudeDifficulty;
    	mMagnitudeEnemyDRTTBoss = MAGNITUDE_ENEMY_DR_TT_BOSS * magnitudeDifficulty;
    	
    	mFinalLevel09AstronautCount = 1;
        
//        mScreenActivationRadius = VIEWSCREEN_RADIUS;
////        mMinimumActivationRadius = VIEWSCREEN_RADIUS * 0.5f;
//        mEnemyActivationRadius = VIEWSCREEN_RADIUS * 1.25f;
//        mNormalActivationRadius = VIEWSCREEN_RADIUS * 3.0f;
////        mNormalActivationRadius = VIEWSCREEN_RADIUS * 1.5f;
////        mWideActivationRadius = VIEWSCREEN_RADIUS * 3.0f;
//////        mWideActivationRadius = VIEWSCREEN_RADIUS * 2.0f;
//        mAlwaysActive = -1.0f;
        
        mWeaponActiveType = Type.INVALID;
        mWeaponInventoryType = Type.INVALID;
        
        mMusicEnabled = true;
        musicPlaying = false;
        
        // XXX: I wish there was a way to do this automatically, but the ClassLoader doesn't seem
        // to provide access to the currently loaded class list.  There's some discussion of walking
        // the actual class file objects and using forName() to instantiate them, but that sounds
        // really heavy-weight.  For now I'll rely on (sucky) manual enumeration.
        class ComponentClass {
            public Class<?> type;
            public int poolSize;
            public ComponentClass(Class<?> classType, int size) {
                type = classType;
                poolSize = size;
            }
        }
        
//        Log.i("Object", "GameObjectFactory <constructor> ComponentClass[] componentTypes = {new <GameComponent> START");
        
        // FIXME Optimize ComponentClass pool sizes for performance improvement. Not all reserve #'s may be required.
        ComponentClass[] componentTypes = {
        		new ComponentClass(SplashScreenComponent.class, 2),
                new ComponentClass(BackgroundCollisionComponent.class, MAX_BACKGROUND_COLLISION_OBJECTS),
//                new ComponentClass(CameraBiasComponent.class, 8),
                new ComponentClass(DynamicCollisionComponent.class, (MAX_DROID_OBJECTS + MAX_WEAPON_OBJECTS + 
                		(MAX_LASER_OBJECTS * MAX_WEAPON_OBJECTS * 2) + 	// Allow for GameObject + Area DynamicCollisionComponents
                		MAX_ENEMY_OBJECTS + (MAX_ENEMY_LASER_OBJECTS * 2) + MAX_ENEMY_BOSS_OBJECTS + MAX_ENEMY_BOSS_LASER_OBJECTS + 
                		MAX_ASTRONAUT_OBJECTS + MAX_PLATFORM_OBJECTS + MAX_ITEM_OBJECTS) + (MAX_SPECIAL_EFFECT_SETS * 4 * 2)),
                new ComponentClass(HitReactionComponent.class, (MAX_BACKGROUND_COLLISION_OBJECTS + MAX_DROID_OBJECTS + 
                		(MAX_LASER_OBJECTS * MAX_WEAPON_OBJECTS) +
                		MAX_ENEMY_OBJECTS + (MAX_ENEMY_LASER_OBJECTS * 2) + MAX_ENEMY_BOSS_OBJECTS + MAX_ENEMY_BOSS_LASER_OBJECTS +
                		MAX_ASTRONAUT_OBJECTS + MAX_PLATFORM_OBJECTS + MAX_ITEM_OBJECTS) + (MAX_SPECIAL_EFFECT_SETS * 4 * 2)),
//                new ComponentClass(InventoryComponent.class, 128),
                new ComponentClass(DroidBottomComponent.class, MAX_DROID_OBJECTS),
                new ComponentClass(DroidTopComponent.class, MAX_DROID_OBJECTS),
                new ComponentClass(WeaponComponent.class, MAX_WEAPON_OBJECTS),
                new ComponentClass(LaserComponent.class, (MAX_LASER_OBJECTS * 5)),  // Max Droid Laser Objects
                new ComponentClass(EnemyBottomComponent.class, MAX_ENEMY_OBJECTS + MAX_ENEMY_BOSS_OBJECTS),
                new ComponentClass(EnemyTopComponent.class, MAX_ENEMY_OBJECTS + MAX_ENEMY_BOSS_OBJECTS),
                new ComponentClass(EnemyLaserComponent.class, ((MAX_ENEMY_LASER_OBJECTS * 2) + MAX_ENEMY_BOSS_LASER_OBJECTS)),  // Max Enemy Laser Objects
//                new ComponentClass(EnemyLaserComponent.class, (MAX_LASER_OBJECTS * 3)),  // Max Enemy Laser Objects
                new ComponentClass(AstronautTopComponent.class, MAX_ASTRONAUT_OBJECTS),
                new ComponentClass(PlatformComponent.class, MAX_PLATFORM_OBJECTS),
                new ComponentClass(ItemComponent.class, MAX_ITEM_OBJECTS),
                new ComponentClass(RenderComponent.class, MAX_GAME_OBJECTS),
                new ComponentClass(RenderFarBackgroundComponent.class, MAX_FAR_BACKGROUND_OBJECTS),
        };
        
//        Log.i("Object", "GameObjectFactory <constructor> ComponentClass[] componentTypes = {new <GameComponent> END");
        
//        Log.i("Object", "GameObjectFactory <constructor> ComponentClass[] mComponentPools = new FixedSizeArray() START componentTypes.length = " + componentTypes.length);
        
        mComponentPools = new FixedSizeArray<GameComponentPool>(componentTypes.length, sComponentPoolComparator);
        for (int x = 0; x < componentTypes.length; x++) {
            ComponentClass component = componentTypes[x];
            mComponentPools.add(new GameComponentPool(component.type, component.poolSize));
        }
        mComponentPools.sort(true);
        
//        Log.i("Object", "GameObjectFactory <constructor> ComponentClass[] mComponentPools = new FixedSizeArray() END");
        
//        Log.i("Object", "GameObjectFactory <constructor> ComponentClass[] mPoolSearchDummy = new GameComponentPool() START");
        
        mPoolSearchDummy = new GameComponentPool(Object.class, 1);
        
//        Log.i("Object", "GameObjectFactory <constructor> ComponentClass[] mPoolSearchDummy = new GameComponentPool() END");
        
        // reset() not required in GameObjectFactory <constructor>
    }
    
    @Override
    public void reset() {
    	mSplashScreenCheck = false;
    	
        mComponentPools = null;
        mPoolSearchDummy = null;
        mGameObjectPool = null;
        
        // FIXME Is it necessary to call gl11.glDeleteBuffers() on each DrawableDroid Object?
        
        // Drawable Objects
        // Drawable Objects - Splashscreen
        mSplashLogoDrawableDroidObject = null;
        mSplashBackgroundDrawableDroidObject = null;
        
        // Drawable Objects - FarBackground
        mFarBackgroundDrawableDroidObject = null;
        
        // Drawable Objects - Background
        mBackgroundSection00DrawableDroidObject = null;
        mBackgroundSection01DrawableDroidObject = null;
        mBackgroundSection02DrawableDroidObject = null;
        mBackgroundSection03DrawableDroidObject = null;
        mBackgroundSection04DrawableDroidObject = null;
        mBackgroundSection05DrawableDroidObject = null;
        mBackgroundSection06DrawableDroidObject = null;
        mBackgroundSection07DrawableDroidObject = null;
        mBackgroundSection08DrawableDroidObject = null;
        mBackgroundSection09DrawableDroidObject = null;
        
        // Drawable Objects - BackgroundWall
        mBackgroundWallLaserSection01DrawableDroidObject = null;
        mBackgroundWallLaserSection02DrawableDroidObject = null;
        mBackgroundWallLaserSection03DrawableDroidObject = null;
        mBackgroundWallLaserSection04DrawableDroidObject = null;
        mBackgroundWallLaserSection05DrawableDroidObject = null;
        mBackgroundWallLaserSection06DrawableDroidObject = null;
        mBackgroundWallLaserSection07DrawableDroidObject = null;
        mBackgroundWallLaserSection08DrawableDroidObject = null;
        mBackgroundWallLaserSection09DrawableDroidObject = null;
        mBackgroundWallPostSection01DrawableDroidObject = null;
        mBackgroundWallPostSection02DrawableDroidObject = null;
        mBackgroundWallPostSection03DrawableDroidObject = null;
        mBackgroundWallPostSection04DrawableDroidObject = null;
        mBackgroundWallPostSection05DrawableDroidObject = null;
        mBackgroundWallPostSection06DrawableDroidObject = null;
        mBackgroundWallPostSection07DrawableDroidObject = null;
        mBackgroundWallPostSection08DrawableDroidObject = null;
        mBackgroundWallPostSection09DrawableDroidObject = null;
        
        // Drawable Objects - Droid
        mDroidBottomDrawableDroidObject = null;
        mDroidTopDrawableDroidObject = null;
        
        // Drawable Objects - Droid Weapons
        mDroidWeaponLaserStdDrawableDroidObject = null;
        mDroidWeaponLaserPulseDrawableDroidObject = null;
        mDroidWeaponLaserEmpDrawableDroidObject = null;
        mDroidWeaponLaserGrenadeDrawableDroidObject = null;
        mDroidWeaponLaserRocketDrawableDroidObject = null;
        
        // Drawable Objects - Droid Lasers
        mDroidLaserStdDrawableDroidObject = null;
        mDroidLaserPulseDrawableDroidObject = null;
        mDroidLaserEmpDrawableDroidObject = null;
        mDroidLaserGrenadeDrawableDroidObject = null;
        mDroidLaserRocketDrawableDroidObject = null;
        
        // Drawable Objects - Enemies
        mEnemyEMOTBottomDrawableDroidObject = null;
        mEnemyEMOTTopDrawableDroidObject = null;
        mEnemyEMOWBottomDrawableDroidObject = null;
        mEnemyEMOWTopDrawableDroidObject = null;
        mEnemyEMOWBossBottomDrawableDroidObject = null;
        mEnemyEMOWBossTopDrawableDroidObject = null;
        mEnemyEMSLBottomDrawableDroidObject = null;
        mEnemyEMSLTopDrawableDroidObject = null;
        mEnemyEMSLBossBottomDrawableDroidObject = null;
        mEnemyEMSLBossTopDrawableDroidObject = null;
        mEnemyHDFLBottomDrawableDroidObject = null;
        mEnemyHDFLTopDrawableDroidObject = null;
        mEnemyHDOLBottomDrawableDroidObject = null;
        mEnemyHDOLTopDrawableDroidObject = null;
        mEnemyHDOTBottomDrawableDroidObject = null;
        mEnemyHDOTTopDrawableDroidObject = null;
        mEnemyHDOWBottomDrawableDroidObject = null;
        mEnemyHDOWTopDrawableDroidObject = null;
        mEnemyHDSLBottomDrawableDroidObject = null;
        mEnemyHDSLTopDrawableDroidObject = null;
        mEnemyHDTLBottomDrawableDroidObject = null;
        mEnemyHDTLTopDrawableDroidObject = null;
        mEnemyHDTLBossBottomDrawableDroidObject = null;
        mEnemyHDTLBossTopDrawableDroidObject = null;
        mEnemyHDTTBottomDrawableDroidObject = null;
        mEnemyHDTTTopDrawableDroidObject = null;
        mEnemyHDTTBossBottomDrawableDroidObject = null;
        mEnemyHDTTBossTopDrawableDroidObject = null;
        mEnemyHDTWBottomDrawableDroidObject = null;
        mEnemyHDTWTopDrawableDroidObject = null;
        mEnemyLCFMBottomDrawableDroidObject = null;
        mEnemyLCFMTopDrawableDroidObject = null;
        mEnemyLCOTBottomDrawableDroidObject = null;
        mEnemyLCOTTopDrawableDroidObject = null;
        mEnemyLCOTBossBottomDrawableDroidObject = null;
        mEnemyLCOTBossTopDrawableDroidObject = null;
        mEnemyLCSLBottomDrawableDroidObject = null;
        mEnemyLCSLTopDrawableDroidObject = null;
        mEnemyLCSLBossBottomDrawableDroidObject = null;
        mEnemyLCSLBossTopDrawableDroidObject = null;
        mEnemyLCTTBottomDrawableDroidObject = null;
        mEnemyLCTTTopDrawableDroidObject = null;
        mEnemyLSFMBottomDrawableDroidObject = null;
        mEnemyLSFMTopDrawableDroidObject = null;
        mEnemyLSTTBottomDrawableDroidObject = null;
        mEnemyLSTTTopDrawableDroidObject = null;
        mEnemyLSTTBossBottomDrawableDroidObject = null;
        mEnemyLSTTBossTopDrawableDroidObject = null;
        mEnemyTAFLBottomDrawableDroidObject = null;
        mEnemyTAFLTopDrawableDroidObject = null;
        mEnemyTAFLBossBottomDrawableDroidObject = null;
        mEnemyTAFLBossTopDrawableDroidObject = null;
        mEnemyDRTTBossBottomDrawableDroidObject = null;
        mEnemyDRTTBossTopDrawableDroidObject = null;
        
        // Drawable Objects - Enemy Lasers    
        mEnemyLaserStdDrawableDroidObject = null;
        mEnemyLaserEmpDrawableDroidObject = null;
        mEnemyBossLaserDrawableDroidObject = null;
        
        // Drawable Objects - Astronauts
        mAstronautPrivateTopDrawableDroidObject = null;
        mAstronautSergeantTopDrawableDroidObject = null;
        mAstronautCaptainTopDrawableDroidObject = null;
        mAstronautGeneralTopDrawableDroidObject = null;
        mAstronautBottomFrame1DrawableDroidObject = null;
        mAstronautBottomFrame2DrawableDroidObject = null;
        mAstronautBottomFrame3DrawableDroidObject = null;
        mAstronautBottomFrame4DrawableDroidObject = null;
        
        // FIXME Is it necessary to Empty Array first before setting equal to null?
        mAstronautAnimationArray = null;
        
        // Drawable Objects - Platforms
        mPlatformLevelStartDrawableDroidObject = null;
        mPlatformLevelEndDrawableDroidObject = null;
        mPlatformSectionStartDrawableDroidObject = null;
        mPlatformSectionEndDrawableDroidObject = null;
        mPlatformElevator0DegDrawableDroidObject = null;
        mPlatformElevator45DegDrawableDroidObject = null;
        
        // Drawable Objects - Items
        mItemCrateWoodDrawableDroidObject = null;
        mItemCrateMetalDrawableDroidObject = null;
        mItemLightBeaconDrawableDroidObject = null;
        mItemPistonEngineDrawableDroidObject = null;
        mItemTableDrawableDroidObject = null;
        mItemTableComputerDrawableDroidObject = null;
        mItemSpaceshipDrawableDroidObject = null;
        mItemShapeshipDoorDrawableDroidObject = null;
        
        // Drawable Objects - Special Effects
        mExplosionFrame1DrawableDroidObject = null;
        mExplosionFrame2DrawableDroidObject = null;
        mExplosionFrame3DrawableDroidObject = null;
        mExplosionFrame4DrawableDroidObject = null;
        mElectricRingFrame1DrawableDroidObject = null;
        mElectricRingFrame2DrawableDroidObject = null;
        mElectricRingFrame3DrawableDroidObject = null;
        mElectricRingFrame4DrawableDroidObject = null;
        mElectricityFrame1DrawableDroidObject = null;
        mElectricityFrame2DrawableDroidObject = null;
        mElectricityFrame3DrawableDroidObject = null;
        mElectricityFrame4DrawableDroidObject = null;
        mExplosionLargeFrame1DrawableDroidObject = null;
        mExplosionLargeFrame2DrawableDroidObject = null;
        mExplosionLargeFrame3DrawableDroidObject = null;
        mExplosionLargeFrame4DrawableDroidObject = null;
        mExplosionRingFrame1DrawableDroidObject = null;
        mExplosionRingFrame2DrawableDroidObject = null;
        mExplosionRingFrame3DrawableDroidObject = null;
        mExplosionRingFrame4DrawableDroidObject = null;
        mTeleportRingFrame1DrawableDroidObject = null;
        mTeleportRingFrame2DrawableDroidObject = null;
        mTeleportRingFrame3DrawableDroidObject = null;
        mTeleportRingFrame4DrawableDroidObject = null;
        
        mNextSectionPositionTemp.zero();
        
        mFinalLevel09AstronautCount = 1;
        
        context = null;
    }
    
    protected GameComponentPool getComponentPool(Class<?> componentType) {
//    	Log.i("Object", "GameObjectFactory getComponentPool()");

        GameComponentPool pool = null;
        mPoolSearchDummy.objectClass = componentType;
        final int index = mComponentPools.find(mPoolSearchDummy, false);
        if (index != -1) {
            pool = mComponentPools.get(index);
        }
        return pool;
    }
    
    protected GameComponent allocateComponent(Class<?> componentType) {
//    	Log.i("Object", "GameObjectFactory allocateComponent()");
    	
        GameComponentPool pool = getComponentPool(componentType);
        assert pool != null;
        GameComponent component = null;
        if (pool != null) {
            component = pool.allocate();
        }
        return component;
    }
    
    protected void releaseComponent(GameComponent component) {
//    	Log.i("Object", "GameObjectFactory releaseComponent()");
    	
        GameComponentPool pool = getComponentPool(component.getClass());
        assert pool != null;
        if (pool != null) {
            component.reset();
            component.shared = false;
            pool.release(component);
        }
    }
    
    protected boolean componentAvailable(Class<?> componentType, int count) {
//    	Log.i("Object", "GameObjectFactory componentAvailable()");
    	
    	boolean canAllocate = false;
        GameComponentPool pool = getComponentPool(componentType);
        assert pool != null;
        if (pool != null) {
        	canAllocate = pool.getAllocatedCount() + count < pool.getSize();
        }
        return canAllocate;
    }
    
    public void destroy(GameObject object) {    	
        object.commitUpdates();
        final int componentCount = object.getCount();
        
        Log.i("Object", "GameObjectFactory destroy() componentCount =" + " [" + object.gameObjectId + "] " + 
        		componentCount);
        
        // Release the GameObject Components (e.g. DroidBottomComponent, RenderComponent)
        for (int x = 0; x < componentCount; x++) {
            GameComponent component = (GameComponent)object.get(x);
            if (!component.shared) {
                releaseComponent(component);
            }
        }
        object.removeAll();
        object.commitUpdates();
        mGameObjectPool.release(object);
    }
    
    public void sanityCheckPools() {
    	final int outstandingObjects = mGameObjectPool.getAllocatedCount();
    	if (outstandingObjects != 0) {
    		Log.e("Object", "GameObjectFactory sanityCheckPools() Outstanding mGameObjectPool Allocations! (" 
                    + outstandingObjects + ")");	
    		assert false;
    	} else {
      		Log.i("Object", "GameObjectFactory sanityCheckPools() No Outstanding mGameObjectPool Allocations");
    	}
      
    	final int componentPoolCount = mComponentPools.getCount();
    	
    	if (componentPoolCount == 0) {
      		Log.i("Object", "GameObjectFactory sanityCheckPools() No Outstanding mComponentPools Allocations");
    	}
    	
    	for (int x = 0; x < componentPoolCount; x++) {
    		final int outstandingComponents = mComponentPools.get(x).getAllocatedCount();
          
          	if (outstandingComponents != 0) {
          		Log.e("Object", "GameObjectFactory sanityCheckPools() Outstanding mComponentPools " 
                        + mComponentPools.get(x).objectClass.getSimpleName()
                        + " allocations! (" + outstandingComponents + ")");
//          		Log.d("Sanity Check", "Outstanding " 
//                      + mComponentPools.get(x).objectClass.getSimpleName()
//                      + " allocations! (" + outstandingComponents + ")");
              //assert false;
          	} else {
          		Log.i("Object", "GameObjectFactory sanityCheckPools() No Outstanding GameComponent Allocations");
          	}
    	}
    	
		// FIXME COMMENT OUT. GameObject Test Code only.
		Log.i("Object", "GameObjectFactory sanityCheckPools() manager.checkObjects() END LEVEL");
		GameObjectManager manager = sSystemRegistry.gameObjectManager;
		manager.checkObjects();
    }
    
	public void spawnWorld(GL11 gl11, LevelObjects levelObjects) {
		mGameObjectIdCount = 0;
		
		HudSystem hud = sSystemRegistry.hudSystem;
		hud.levelIntro = false;
		
        // Walk the world and spawn objects to specified locations
		int num = levelObjects.objectType.length;
		
		for (int i = 0; i < num; i++) {
			Group group = Group.indexToType(levelObjects.objectGroup[i]);
            Type type = Type.indexToType(levelObjects.objectType[i]);
            
            Log.i("Object", "GameObjectFactory spawnWorld() group and type = " + 
					" (" + group + ")" + " (" + type + ")");
            
            switch(group) {
            case INVALID:
            	break;
            	
            case SPLASHSCREEN:
            	mSplashScreenCheck = true;
            	
        		spawnSplashScreen(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r); 
            	break;
            	
        	case BACKGROUND_01:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_02:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_03:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_04:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_05:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_06:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_07:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_08:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_09:
        		spawnBackground(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_01:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_02:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_03:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_04:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_05:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_06:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_07:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_08:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case BACKGROUND_WALL_09:
        		spawnBackgroundWall(gl11, group, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);        		
        		break;
        		
        	case FAR_BACKGROUND:
        		spawnFarBackground(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
        		break;
        	
            case DROID:	
        		spawnDroid(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
                break;
                
            case DROID_WEAPON:
        		spawnDroidWeapon(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r,
        				false,
        				false);
            	break;
                
            case ENEMY:
        		spawnEnemy(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
                break;
            	
            case ENEMY_LASER:
            	break;
            	
            case ASTRONAUT:
        		spawnAstronaut(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
            case PLATFORM_LEVEL_START:
        		spawnPlatformLevelStart(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
            case PLATFORM_LEVEL_END:
        		spawnPlatformLevelEnd(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
            case PLATFORM_SECTION_START:
        		spawnPlatformSectionStart(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
            case PLATFORM_SECTION_END:
        		spawnPlatformSectionEnd(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
            case PLATFORM_ELEVATOR:
        		spawnPlatformElevator(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
//            case PLATFORM:
//        		spawnPlatform(gl11, type,
//        				levelObjects.objectLocation[i].x,
//        				levelObjects.objectLocation[i].y,
//        				levelObjects.objectLocation[i].z,
//        				levelObjects.objectLocation[i].r);
//            	break;
            	
            case ITEM:
        		spawnItem(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
            	
            case SPECIAL_EFFECT:
        		spawnSpecialEffect(gl11, type,
        				levelObjects.objectLocation[i].x,
        				levelObjects.objectLocation[i].y,
        				levelObjects.objectLocation[i].z,
        				levelObjects.objectLocation[i].r);
            	break;
                
            default:
                break;
            }
		}
		
		if (!mSplashScreenCheck) {
			// Set WeaponActive
			spawnDroidWeapon(gl11, mWeaponActiveType,
					0.0f,
					0.0f,
					0.0f,
					0.0f,
					true,
					false);
			
			// Set WeaponInventory
			if(mWeaponInventoryType != Type.INVALID) {
				spawnDroidWeapon(gl11, mWeaponInventoryType,
						0.0f,
						0.0f,
						0.0f,
						0.0f,
						false,
						true);
			}	
		}
		
		if (GameParameters.debug) {
			Log.i("Object", "GameObjectFactory spawnWorld() manager.checkObjects() START LEVEL");
			GameObjectManager manager = sSystemRegistry.gameObjectManager;
			manager.checkObjects();	
		}
    }
	
	// FIXME Change all spawn() methods to private?
	public void spawnSplashScreen(GL11 gl11, Type type, float x, float y, float z, float r) {
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
    		// spawn FrostBlade Games Logo
	    	GameObject splashLogo = mGameObjectPool.allocate();
	    	
	    	if (splashLogo != null) {		    	
	    		splashLogo.group = Group.SPLASHSCREEN;
	    		splashLogo.type = type;
	    		splashLogo.currentState = CurrentState.LEVEL_START;
	    		splashLogo.previousState = CurrentState.LEVEL_START;
		      
	    		splashLogo.initialCurrentPosition(x, y, z, r);
		    	
	    		splashLogo.activationRadius = ACTIVATION_RADIUS_ALWAYS;
		    	
		      	CameraSystem camera = sSystemRegistry.cameraSystem;
		        HudSystem hud = sSystemRegistry.hudSystem;

	        	camera.setTarget(splashLogo);
		    	camera.setFocusPosition(splashLogo.currentPosition);
	        	camera.setViewangle(0.0f, 1.0f, 7.0f);		// Default for SplashScreen
	        	hud.levelIntro = true;
		      
	        	splashLogo.gameObjectId = mGameObjectIdCount;
		      
//	    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//	        	splashLogo.objectInstantiation();
	        	
	        	if(mSplashLogoDrawableDroidObject == null) {
	        		mSplashLogoDrawableDroidObject = new DrawableDroid();
	        	}
		      
		    	if (GameParameters.supportsVBOs && gl11 != null) {
		    		mSplashLogoDrawableDroidObject.loadObjectVBO(gl11, R.raw.frostbladegames_logo_vbo, context);
//		    		splashLogo.drawableDroid.loadObjectVBO(gl11, R.raw.frostbladegames_logo_vbo, context);
		    	} else {
		    		Log.e("VBO", "GameObjectFactory spawnSplashScreen() gl11 is null!");
		      	}
		    	
		    	splashLogo.drawableDroid = mSplashLogoDrawableDroidObject;
		    	
		    	splashLogo.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	splashLogo.drawableDroid.loadLight1Buffers(mMidBuffer, mHighBuffer, mHighBuffer, 
		    			mLight1PositionBuffer, mLightSpotDirection, mLightSpotCutoff);
		    	splashLogo.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
		    	
		      	SplashScreenComponent splash = (SplashScreenComponent)allocateComponent(SplashScreenComponent.class);
		      	splash.priority = SortConstants.PLAYER;
		      	
		      	splashLogo.add(splash);
		      	
		      	manager.add(splashLogo);
//		      	manager.setPlayer(splashLogo);
		      
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnSplashScreen() Logo object ID and type = " + 
			        		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		      
		      	mGameObjectIdCount++;
	    	} else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
	    	
    		// spawn FrostBlade Games Logo Background
	    	GameObject splashBackground = mGameObjectPool.allocate();
	    	
	    	if (splashBackground != null) {		    	
	    		splashBackground.group = Group.SPLASHSCREEN;
	    		splashBackground.type = Type.SPLASHSCREEN_BACKGROUND;
	    		splashBackground.currentState = CurrentState.LEVEL_START;
	    		splashBackground.previousState = CurrentState.LEVEL_START;
		      
	    		splashBackground.initialCurrentPosition(x, y, z, r);
		    	
	    		splashBackground.activationRadius = ACTIVATION_RADIUS_ALWAYS;
		      
	    		splashBackground.gameObjectId = mGameObjectIdCount;
		      
//	    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//	    		splashBackground.objectInstantiation();
	    		
	    		if(mSplashBackgroundDrawableDroidObject == null) {
	    			mSplashBackgroundDrawableDroidObject = new DrawableDroid();
	    		}
		      
		    	if (GameParameters.supportsVBOs && gl11 != null) {
		    		mSplashBackgroundDrawableDroidObject.loadObjectVBO(gl11, R.raw.frostbladegames_background_vbo, context);
//		    		splashBackground.drawableDroid.loadObjectVBO(gl11, R.raw.frostbladegames_background_vbo, context);
		    	} else {
		    		Log.e("VBO", "GameObjectFactory spawnSplashScreen() gl11 is null!");
		      	}
		    	
		    	splashBackground.drawableDroid = mSplashBackgroundDrawableDroidObject;
		    	
		    	splashBackground.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mLowBuffer, 
		    			mLight0PositionBuffer);
		    	splashBackground.drawableDroid.loadMaterialBuffers(mLowBuffer, mMidBuffer, mLowBuffer, 
		    			mMatShininessZeroBuffer, mZeroBuffer);
		    	
		      	SplashScreenComponent background = (SplashScreenComponent)allocateComponent(SplashScreenComponent.class);
		      	background.priority = SortConstants.PLAYER;
		      	
		      	splashBackground.add(background);
		      	
		      	manager.add(splashBackground);
		      
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnSplashScreen() Logo Background object ID and type = " + 
			        		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		      
		      	mGameObjectIdCount++;
	    	} else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
	}
	
    public void spawnFarBackground(GL11 gl11, Type type, float x, float y, float z, float r) {    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
	        GameObject object = mGameObjectPool.allocate();
	        
	        if (object != null) {	        
		        object.group = Group.FAR_BACKGROUND;
		        object.type = type;
		        
		        object.activationRadius = ACTIVATION_RADIUS_ALWAYS; 
		        
		        object.gameObjectId = mGameObjectIdCount;
		        
		        // Far Background position is controlled by Camera Position + Offset Distance from Droid.
		        RenderFarBackgroundComponent render = (RenderFarBackgroundComponent)allocateComponent(RenderFarBackgroundComponent.class);
		        // FIXME Test and change back to SortConstants.BACKGROUND_START
		        render.priority = SortConstants.FOREGROUND;
//		        render.priority = SortConstants.BACKGROUND_START;
		        render.setDistanceFromCamera(x, y, z);
//		        render.setDistanceFromCamera(x, z);
		        
		        if(mFarBackgroundDrawableDroidObject == null) {
		        	mFarBackgroundDrawableDroidObject = new DrawableFarBackground();
		        }
		        
		        if (GameParameters.supportsVBOs && gl11 != null) {
			        int farBackgroundTexture = getFarBackgroundTexture(gl11, type);
			        if (farBackgroundTexture != -1) {
			        	mFarBackgroundDrawableDroidObject.loadGLTexture(gl11, farBackgroundTexture, context);
			        	mFarBackgroundDrawableDroidObject.loadObjectVBO(gl11, R.raw.far_background_vbo, context);
//			        	render.drawableFarBackground.loadGLTexture(gl11, farBackgroundTexture, context);
//				        render.drawableFarBackground.loadObjectVBO(gl11, R.raw.far_background_vbo, context);
			        } else {
			        	  Log.e("VBO", "GameObjectFactory spawnFarBackground() farBackgroundTexture is null!");
			        }
//			        render.drawableFarBackground.loadGLTexture(gl11, R.drawable.far_background01, context);
//			        render.drawableFarBackground.loadObjectVBO(gl11, R.raw.far_background01_vbo, context);
//			        render.drawableFarBackground.loadObjectVBO(gl11, R.raw.far_background01_test2_vbo, context);
//			        render.drawableFarBackground.loadObjectVBO(gl11, R.raw.far_background01_test_vbo, context);

//			        render.drawableFarBackground.loadObjectVBO(gl11, GameParameters.levelRow);
		        } else {
		        	Log.e("VBO", "GameObjectFactory spawnFarBackground() gl11 is null!");           
		      	}
		        
		        render.drawableFarBackground = mFarBackgroundDrawableDroidObject;
		        
		        render.drawableFarBackground.loadLight0Buffers(mZeroBuffer, mMaxBuffer, mMaxBuffer, 
		    			mLight0PositionBuffer);
		        render.drawableFarBackground.loadMaterialBuffers(mZeroBuffer, mMaxBuffer, mMaxBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		
		        object.add(render);
		        
		        manager.add(object);
		        
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnFarBackground() object ID and type = " + 
			        		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		        
		        mGameObjectIdCount++;
	        } else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	        }
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
    
    public void spawnBackground(GL11 gl11, Group group, Type type, float x1, float z1, float x2, float z2) {  	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
	        GameObject object = mGameObjectPool.allocate();
	        
	        if (object != null) {
	        	// Boolean used by GameObjectManager
	        	object.backgroundGroup = true;
		        
		        object.group = group;
		        object.type = type;
		        
		        // Background position always 0,0,0,0
		        object.currentPosition.set(0.0f, 0.0f, 0.0f, 0.0f);
		        
		        // Background uses Startpoint x1,z1 and Endpoint x2,z2 for Background Activation Radius check
		        object.backgroundRadius.set(x1, z1, x2, z2);
//		        object.backgroundRadius.set(x1, z1, x2, z2, 0.0f); 
		        
		        object.gameObjectId = mGameObjectIdCount;
		        
//	    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		        object.objectInstantiation();
		        
		        // TODO Change to level_section xml file read, rather than getLevelSection(), getLevelSectionVBO(), getLevelCollision()?
		        switch(type) {
		        case SECTION_00:
		        	if(mBackgroundSection00DrawableDroidObject == null) {
		        		mBackgroundSection00DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection00DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
//				            object.drawableDroid.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection00DrawableDroidObject;
		        	break;
		        	
		        case SECTION_01:
		        	if(mBackgroundSection01DrawableDroidObject == null) {
		        		mBackgroundSection01DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection01DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection01DrawableDroidObject;
		        	break;
		        	
		        case SECTION_02:
		        	if(mBackgroundSection02DrawableDroidObject == null) {
		        		mBackgroundSection02DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection02DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection02DrawableDroidObject;
		        	break;
		        	
		        case SECTION_03:
		        	if(mBackgroundSection03DrawableDroidObject == null) {
		        		mBackgroundSection03DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection03DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection03DrawableDroidObject;
		        	break;
		        	
		        case SECTION_04:
		        	if(mBackgroundSection04DrawableDroidObject == null) {
		        		mBackgroundSection04DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection04DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection04DrawableDroidObject;
		        	break;
		        	
		        case SECTION_05:
		        	if(mBackgroundSection05DrawableDroidObject == null) {
		        		mBackgroundSection05DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection05DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection05DrawableDroidObject;
		        	break;
		        	
		        case SECTION_06:
		        	if(mBackgroundSection06DrawableDroidObject == null) {
		        		mBackgroundSection06DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection06DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection06DrawableDroidObject;
		        	break;
		        	
		        case SECTION_07:
		        	if(mBackgroundSection07DrawableDroidObject == null) {
		        		mBackgroundSection07DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection07DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection07DrawableDroidObject;
		        	break;
		        	
		        case SECTION_08:
		        	if(mBackgroundSection08DrawableDroidObject == null) {
		        		mBackgroundSection08DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection08DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection08DrawableDroidObject;
		        	break;
		        	
		        case SECTION_09:
		        	if(mBackgroundSection09DrawableDroidObject == null) {
		        		mBackgroundSection09DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
				        if (levelSectionVBO != -1) {
				        	mBackgroundSection09DrawableDroidObject.loadObjectVBO(gl11, levelSectionVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundSection09DrawableDroidObject;
		        	break;
		        	
		        default:
		        	break;
		        }
		        
		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mHighBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mHighBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
		        	        
//		        if (GameParameters.supportsVBOs && gl11 != null) {
//			        int levelSectionVBO = getLevelSectionVBO(gl11, object, group, type);
//			        if (levelSectionVBO != -1) {
//			            object.drawableDroid.loadObjectVBO(gl11, levelSectionVBO, context);
//			        } else {
//			        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
//			        }
//		        } else {
//		            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
//		      	}
		        
		        // Light and Material settings in getLevelSectionVBO()
		        
		        RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
		        render.priority = SortConstants.FOREGROUND;
		        
		        BackgroundCollisionComponent backgroundCollision = 
		        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
		        
		    	// TODO Replace code with .dat file DataInputStream method
//		    	int numSegments = 0;
//		    	
//				List<String> vLines = null;
//				List<String> fLines = null;
//				StringTokenizer vToken;
//				StringTokenizer fToken;
		        
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnBackground() [B] and type =" + 
			         		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
				
		        int levelSectionCollision = getLevelSectionCollision(group, type);
		    	
		        if (levelSectionCollision != -1) {
		        	FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes = 
		        			loadBackgroundCollisionVolumes(levelSectionCollision);
			        
			        backgroundCollision.setCollisionVolumes(collisionVolumes);
		        } else {
		        	Log.e("Object", "GameObjectFactory spawnBackground() levelSectionCollision == NULL");
		        }
		        
		        HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//		        hitReact.setBounceOnHit(false);
		        hitReact.setPauseOnAttack(false);
		      
		        backgroundCollision.setHitReactionComponent(hitReact);
		
		        object.add(render);
		        object.add(hitReact);
		        object.add(backgroundCollision);
		        
		        manager.add(object);
		        
		        mGameObjectIdCount++;
	        } else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	        }
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
    
    public void spawnBackgroundWall(GL11 gl11, Group group, Type type, float x1, float z1, float x2, float z2) {   	
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
  	        GameObject object = mGameObjectPool.allocate();
  	        
  	        if (object != null) {
  	        	// Boolean used by GameObjectManager
  	        	object.backgroundGroup = true;
  		        
  		        object.group = group;
  		        object.type = type;
  		        
  		        // Background position always 0,0,0,0
  		        object.currentPosition.set(0.0f, 0.0f, 0.0f, 0.0f);
  		        
  		        // Background uses Startpoint x1,z1 and Endpoint x2,z2 for Background Activation Radius check
  		        object.backgroundRadius.set(x1, z1, x2, z2);
//  		        object.backgroundRadius.set(x1, z1, x2, z2, 0.0f);
  		        
  		        object.gameObjectId = mGameObjectIdCount;
  		        
//	    		object.objectInstantiation();
  		        
		        // TODO Change to level_section xml file read, rather than getLevelSection(), getLevelSectionVBO(), getLevelCollision()?
		        switch(type) {		        	
		        case WALL_LASER_01:
		        	if(mBackgroundWallLaserSection01DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection01DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection01DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection01DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection01DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_02:
		        	if(mBackgroundWallLaserSection02DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection02DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection02DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection02DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection02DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_03:
		        	if(mBackgroundWallLaserSection03DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection03DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection03DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection03DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection03DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_04:
		        	if(mBackgroundWallLaserSection04DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection04DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection04DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection04DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection04DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_05:
		        	if(mBackgroundWallLaserSection05DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection05DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection05DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection05DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection05DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_06:
		        	if(mBackgroundWallLaserSection06DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection06DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection06DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection06DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection06DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_07:
		        	if(mBackgroundWallLaserSection07DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection07DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection07DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection07DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection07DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_08:
		        	if(mBackgroundWallLaserSection08DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection08DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection08DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection08DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection08DrawableDroidObject;
		        	break;
		        	
		        case WALL_LASER_09:
		        	if(mBackgroundWallLaserSection09DrawableDroidObject == null) {
		        		mBackgroundWallLaserSection09DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallLaserSection09DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallLaserSection09DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallLaserSection09DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_01:
		        	if(mBackgroundWallPostSection01DrawableDroidObject == null) {
		        		mBackgroundWallPostSection01DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection01DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection01DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection01DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_02:
		        	if(mBackgroundWallPostSection02DrawableDroidObject == null) {
		        		mBackgroundWallPostSection02DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection02DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection02DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection02DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_03:
		        	if(mBackgroundWallPostSection03DrawableDroidObject == null) {
		        		mBackgroundWallPostSection03DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection03DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection03DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection03DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_04:
		        	if(mBackgroundWallPostSection04DrawableDroidObject == null) {
		        		mBackgroundWallPostSection04DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection04DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection04DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection04DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_05:
		        	if(mBackgroundWallPostSection05DrawableDroidObject == null) {
		        		mBackgroundWallPostSection05DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection05DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection05DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection05DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_06:
		        	if(mBackgroundWallPostSection06DrawableDroidObject == null) {
		        		mBackgroundWallPostSection06DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection06DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection06DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection06DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_07:
		        	if(mBackgroundWallPostSection07DrawableDroidObject == null) {
		        		mBackgroundWallPostSection07DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection07DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection07DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection07DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_08:
		        	if(mBackgroundWallPostSection08DrawableDroidObject == null) {
		        		mBackgroundWallPostSection08DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection08DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection08DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection08DrawableDroidObject;
		        	break;
		        	
		        case WALL_POST_09:
		        	if(mBackgroundWallPostSection09DrawableDroidObject == null) {
		        		mBackgroundWallPostSection09DrawableDroidObject = new DrawableDroid();
		        	}
		        	
			        if (GameParameters.supportsVBOs && gl11 != null) {
				        int backgroundWallVBO = getBackgroundWallVBO(gl11, mBackgroundWallPostSection09DrawableDroidObject, group, type);
				        if (backgroundWallVBO != -1) {
				        	mBackgroundWallPostSection09DrawableDroidObject.loadObjectVBO(gl11, backgroundWallVBO, context);
				        } else {
				        	Log.e("VBO", "GameObjectFactory spawnBackground() levelSectionVBO is null!");
				        }
			        } else {
			            Log.e("VBO", "GameObjectFactory spawnBackground() gl11 is null!");         
			      	}
			        
			        object.drawableDroid = mBackgroundWallPostSection09DrawableDroidObject;
		        	break;
		        	
		        default:
		        	break;
		        }
  		        
//		        if (GameParameters.supportsVBOs && gl11 != null) {
//			        int backgroundWallVBO = getBackgroundWallVBO(gl11, object, group, type);
//			        if (backgroundWallVBO != -1) {
//			        	object.drawableDroid.loadObjectVBO(gl11, backgroundWallVBO, context);
//			        } else {
//			        	  Log.e("VBO", "GameObjectFactory spawnBackgroundWall() backgroundWallVBO is null!");
//			        }
//		          } else {
//		        	  Log.e("VBO", "GameObjectFactory spawnBackgroundWall() gl11 is null!");           
//		      	}
		        
		        // Light and Material settings in getBackgroundWallVBO()
  		        
  		        RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
  		        render.priority = SortConstants.FOREGROUND;
  		
  		        object.add(render);
  		        
  		        manager.add(object);
  		        
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnBackgroundWall() object ID and type = " + 
			         		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
  		        
  		        mGameObjectIdCount++;
  	        } else {
  	    		Log.e("GameObjectFactory", "GameObject = NULL");
  	        }
      	} else {
      		Log.e("GameObjectFactory", "GameObjectManager = NULL");
      	}
    }
    
    public void spawnDroid(GL11 gl11, Type type, float x, float y, float z, float r) {
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
    		// spawn DroidBottom
	    	GameObject objectBottom = mGameObjectPool.allocate();
	    	
	    	if (objectBottom != null) {		    	
		    	objectBottom.group = Group.DROID;
		    	objectBottom.type = type;
		    	
		    	objectBottom.initialCurrentPosition(x, y, z, r);
		    	
		    	objectBottom.magnitude = 0.03f;
		    	
		      	objectBottom.hitPoints = MAX_DROID_LIFE;
//		      	objectBottom.hitPoints = DroidBottomComponent.MAX_DROID_LIFE;
		    	
		    	objectBottom.activationRadius = ACTIVATION_RADIUS_ALWAYS;
		    	
		    	CameraSystem cameraSystem = sSystemRegistry.cameraSystem;
		        HudSystem hud = sSystemRegistry.hudSystem;

		    	objectBottom.gameObjectId = mGameObjectIdCount;
		      
		    	// FIXME 12/5/12 MODIFY
//	    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		    	objectBottom.objectInstantiation();
		    	
		    	if (mDroidBottomDrawableDroidObject == null) {
		    		mDroidBottomDrawableDroidObject = new DrawableDroid();
		    	}
		    	
		        if (GameParameters.supportsVBOs && gl11 != null) {
		        	mDroidBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_bottom_vbo, context);
//		    		objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.droid_bottom_vbo, context);
		          } else {
		              	Log.e("VBO", "GameObjectFactory spawnDroidBottom() gl11 is null!");         
		      	}
		        
		        objectBottom.drawableDroid = mDroidBottomDrawableDroidObject;
		        // FIXME END 12/5/12 MODIFY
		    	
		    	manager.droidBottomGameObject = objectBottom;
		    	
		    	// Set Droid Bottom for Droid Top state reference
		    	mDroidBottomGameObject = objectBottom;
		    	
		    	cameraSystem.setTarget(objectBottom);
//		    	cameraSystem.setDroid(objectBottom);
		    	
		    	objectBottom.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		    	
		    	if (GameParameters.levelRow == 0) {
			    	objectBottom.currentState = CurrentState.INTRO;
			    	objectBottom.previousState = CurrentState.INTRO;
			    	
			    	objectBottom.drawableDroid.loadLight0Buffers(mZeroBuffer, mZeroBuffer, mZeroBuffer, 
			    			mLight0PositionBuffer);
			    	
			    	// Use A Specific Item (e.g. Crate) Location for cameraSystem.setTarget()
			    	
			    	// Set Droid for later cameraSystem.setTargetDroid() target change
			    	cameraSystem.setDroid(objectBottom);
			    	
		        	cameraSystem.setViewangle(0.0f, 1.0f, 10.f);		// Setting for Intro
		        	hud.levelIntro = true;
		    	} else {
			    	objectBottom.currentState = CurrentState.LEVEL_START;
			    	objectBottom.previousState = CurrentState.LEVEL_START;
			    	
			    	objectBottom.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
			    			mLight0PositionBuffer);

			    	cameraSystem.setTarget(objectBottom);
			    	cameraSystem.setFocusPosition(objectBottom.currentPosition);
			    	
		        	cameraSystem.setViewangle(-5.0f, 11.0f, 5.0f);	// Default for Game Play
//		        	cameraSystem.setViewangle(-4.5f, 10.0f, 4.5f);	// Default for Game Play
		    	}
		    	
		      	RenderComponent renderBottom = (RenderComponent)allocateComponent(RenderComponent.class);
		      	// Change this to enum Group DROID?
		      	renderBottom.priority = SortConstants.PLAYER;
		      
		      	DroidBottomComponent droidBottom = (DroidBottomComponent)allocateComponent(DroidBottomComponent.class);
	    		SoundSystem sound = sSystemRegistry.soundSystem;
	    		if (sound != null) {
	    			droidBottom.setPowerupSound(sound.load(R.raw.sound_droid_powerup));
			      	droidBottom.setBounceSound(sound.load(R.raw.sound_gameobject_bounce));
			      	droidBottom.setHitSound(sound.load(R.raw.sound_droid_hit));
			      	droidBottom.setFallSound(sound.load(R.raw.sound_droid_fall));
//			      	droidBottom.setLevelEndSound(sound.load(R.raw.sound_platform_teleport));
			      	droidBottom.setDeathSound(sound.load(R.raw.sound_droid_death));
			      	if (GameParameters.levelRow == 9) {
			      		droidBottom.setGameEndSound(sound.load(R.raw.sound_astronaut_collect));
			      	}
	    		}   
		      	
		    	DynamicCollisionComponent dynamicCollision = 
		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
		      
		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.580f, 0.580f);
//		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.849f, 0.849f);
//		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.849f, 0.849f, HitType.BOUNCE);
		      
		    	dynamicCollision.setCollisionVolume(collisionVolume);
		    
		    	HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
		    	// FIXME Change to Enemy Destruction Animation Sequence or add direct to EnemyBottomComponent or HitReactionComponent 
//		    	hitReact.setSpawnOnDealHit(HitType.HIT, Type.DROID_LASER_STD, false, true);
		    	// FIXME Add hitReact.setSpawnOnDeath()
		    
		    	dynamicCollision.setHitReactionComponent(hitReact);
		      
		      	objectBottom.add(droidBottom);
		      	objectBottom.add(renderBottom);
		      	if (GameParameters.levelRow != 0) {	
			    	objectBottom.add(hitReact);
			    	objectBottom.add(dynamicCollision);
		      	}
		      	
		      	manager.add(objectBottom);
//		      	manager.setPlayer(objectBottom);
		      
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnDroid() objectBottom object ID and type = " + 
			        		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		      
		      	mGameObjectIdCount++;
	    	} else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
	      	
	      	// spawn DroidTop
	        GameObject objectTop = mGameObjectPool.allocate();
	        
	        if (objectTop != null) {	        	
	        	// TODO Are these settings required for <GameObject>Top incl Droid and Enemy?
		        objectTop.group = Group.DROID;
		    	objectTop.type = type;
	
		        objectTop.currentPosition.set(mDroidBottomGameObject.currentPosition);
		        
		      	objectBottom.hitPoints = MAX_DROID_LIFE;
//		      	objectBottom.hitPoints = DroidBottomComponent.MAX_DROID_LIFE;
		        
		        objectTop.activationRadius = ACTIVATION_RADIUS_ALWAYS;
		        
		        objectTop.gameObjectId = mGameObjectIdCount;
		        
		    	// FIXME 12/5/12 MODIFY
//	    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		    	objectTop.objectInstantiation();
		    	
		    	if (mDroidTopDrawableDroidObject == null) {
		    		mDroidTopDrawableDroidObject = new DrawableDroid();
		    	}
		    	
		        if (GameParameters.supportsVBOs && gl11 != null) {
		        	mDroidTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_top_vbo, context);
//		    		objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.droid_top_vbo, context);
		          } else {
		            Log.e("VBO", "GameObjectFactory spawnDroidTop() gl11 is null!");         
		      	}
		        
		        objectTop.drawableDroid = mDroidTopDrawableDroidObject;
		        // FIXME END 12/5/12 MODIFY
		        
		    	if (GameParameters.levelRow == 0) {
		    		objectTop.drawableDroid.loadLight0Buffers(mZeroBuffer, mZeroBuffer, mZeroBuffer, 
			    			mLight0PositionBuffer);
		    		objectTop.drawableDroid.loadMaterialBuffers(mZeroBuffer, mZeroBuffer, mZeroBuffer, 
			    			mMatShininessZeroBuffer, mZeroBuffer);
		    	} else {
		    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
			    			mLight0PositionBuffer);
		    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
			    			mMatShininessLowBuffer, mZeroBuffer);
		    	}
		        
		        DroidTopComponent droidTop = (DroidTopComponent)allocateComponent(DroidTopComponent.class);
		        droidTop.setBottomGameObject(mDroidBottomGameObject);
		        
		    	// Set Droid Top for Weapon state reference
		    	mDroidTopGameObject = objectTop;
		        
		        RenderComponent renderTop = (RenderComponent)allocateComponent(RenderComponent.class);
		        renderTop.priority = SortConstants.PLAYER;
		        
			    objectTop.add(droidTop);
			    objectTop.add(renderTop);
		        
		      	manager.add(objectTop);
		        
				if (GameParameters.debug) {
			        Log.i("Object", "GameObjectFactory spawnDroid() objectTop object ID and type = " + 
			          		" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		        
		        mGameObjectIdCount++;
	        } else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
  
    public void spawnDroidWeapon(GL11 gl11, Type type, float x, float y, float z, float r, boolean active, boolean inventory) {
//    public void spawnDroidWeapon(GL11 gl11, Type type, float x, float y, float z, float r) {
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
	    	GameObject weaponObject = mGameObjectPool.allocate();
	    	
	    	if (weaponObject != null) {
		    	weaponObject.group = Group.DROID_WEAPON;
		    	weaponObject.type = type;
		    	
		    	weaponObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
		      
		    	weaponObject.gameObjectId = mGameObjectIdCount;

//		    	weaponObject.objectInstantiation();
		      
		    	WeaponComponent weapon = (WeaponComponent)allocateComponent(WeaponComponent.class);
		    	
	    		if (active) {
//	    		if (mWeaponActiveType == weaponObject.type) {
		    		weaponObject.activeWeapon = true;
		    		
		    		final float posX = mDroidBottomGameObject.currentPosition.x;
		    		final float posY = mDroidBottomGameObject.currentPosition.y;
		    		final float posZ = mDroidBottomGameObject.currentPosition.z;
		    		final float posR = mDroidBottomGameObject.currentPosition.r;
		    		weaponObject.currentPosition.set(posX, posY, posZ, posR);
			        
		    		manager.setWeapon1GameObject(weaponObject);
//		    		sSystemRegistry.hudSystem.setButtonWeapon1GameObjectType(weaponObject.type);
//		    		sSystemRegistry.hudSystem.setButtonWeapon1GameObject(weaponObject);
		    		
	    		} else if (inventory) {
//	    		} else if (mWeaponInventoryType == weaponObject.type) {
		    		weaponObject.inventoryWeapon = true;
		    		
		    		final float posX = mDroidBottomGameObject.currentPosition.x;
		    		final float posY = mDroidBottomGameObject.currentPosition.y;
		    		final float posZ = mDroidBottomGameObject.currentPosition.z;
		    		final float posR = mDroidBottomGameObject.currentPosition.r;
		    		weaponObject.currentPosition.set(posX, posY, posZ, posR);
			        
		    		manager.setWeapon2GameObject(weaponObject);
//		    		sSystemRegistry.hudSystem.setButtonWeapon2GameObjectType(weaponObject.type);
//		    		sSystemRegistry.hudSystem.setButtonWeapon2GameObject(weaponObject);
		    		
	    		} else {
	    			weaponObject.currentPosition.set(x, y, z, r);
	    		}	
		    	
//		    	OBBCollisionVolume weaponCollisionVolume;
		    	
	    		SoundSystem sound = sSystemRegistry.soundSystem;
	    		
				if (GameParameters.debug) {
			    	Log.i("Object", "GameObjectFactory spawnDroidWeapon() object ID and type = " + 
			    			" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		      
		    	mGameObjectIdCount++;
		    	
		    	switch(type) {
		    	case DROID_WEAPON_LASER_STD:
		    		if(mDroidWeaponLaserStdDrawableDroidObject == null) {
		    			mDroidWeaponLaserStdDrawableDroidObject = new DrawableDroid();
		    			
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mDroidWeaponLaserStdDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_weapon_laser_std_vbo, context);
//				    		weaponObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_weapon_laser_std_vbo, context);
				    	} else {
				    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser gl11 is null!");
				    	}
		    		}
		    		
		    		weaponObject.drawableDroid = mDroidWeaponLaserStdDrawableDroidObject;
		    		
//		    		weaponCollisionVolume = new OBBCollisionVolume(0.5f, 0.5f);		// Approx width of DroidTop
		    		
			    	weapon.setObjectTypeToSpawn(Type.DROID_LASER_STD);
			    	
			    	weapon.setWeaponOffset(mDroidWeaponLaserStdOffset);
		    		
		    		weapon.setLaserOffset(mDroidLaserStdOffset);
		    		
		    		if (mDroidLaserStdDrawableDroidObject == null) {
			    		FixedSizeArray<GameObject> laserStdGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.DROID_LASER;
					    		laserObject.type = Type.DROID_LASER_STD;
					    		
					    		// laserObject.hitReactType set in GameObjectCollisionSystem only in event of Collision
//					    		laserObject.hitReactType = Type.EXPLOSION;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_SCREEN;
//					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mDroidLaserStdDrawableDroidObject == null) {
						    		mDroidLaserStdDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mDroidLaserStdDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_laser_std_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_laser_std_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnDroidLaser() droid_laser_std gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mDroidLaserStdDrawableDroidObject = new DrawableDroid();
//						    		mDroidLaserStdDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mDroidLaserStdDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mDroidLaserStdDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		LaserComponent laser = (LaserComponent)allocateComponent(LaserComponent.class);
					    		
						    	laserObject.magnitude = 0.5f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		laserStdGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnDroidWeapon() droid_laser_std object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
			    		manager.setLaserGameObjectArray(Type.DROID_LASER_STD, MAX_LASER_OBJECTS, laserStdGameObjectArray);	
		    		}
		    		
//		    		weaponObject.activeWeapon = true;
//		    		
//		    		final float posX = mDroidBottomGameObject.currentPosition.x;
//		    		final float posY = mDroidBottomGameObject.currentPosition.y;
//		    		final float posZ = mDroidBottomGameObject.currentPosition.z;
//		    		final float posR = mDroidBottomGameObject.currentPosition.r;
//		    		weaponObject.currentPosition.set(posX, posY, posZ, posR);
//			        
//		    		sSystemRegistry.hudSystem.setButtonWeapon1GameObject(weaponObject);
		    		
		    		break;
		    		
		    	case DROID_WEAPON_LASER_PULSE:
		    		if(mDroidWeaponLaserPulseDrawableDroidObject == null) {
		    			mDroidWeaponLaserPulseDrawableDroidObject = new DrawableDroid();
		    			
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mDroidWeaponLaserPulseDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_weapon_laser_pulse_vbo, context);
				    	} else {
				    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser gl11 is null!");
				    	}
		    		}
		    		
		    		weaponObject.drawableDroid = mDroidWeaponLaserPulseDrawableDroidObject;
		    		
//		    		if (GameParameters.supportsVBOs && gl11 != null) {
//			    		weaponObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_weapon_laser_pulse_vbo, context);
//			    	} else {
//			    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser_pulse gl11 is null!");
//			    	}
		    		
//		    		weaponObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mLight0PositionBuffer);
//		    		weaponObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mMatShininessLowBuffer, mZeroBuffer);
		    		
//		    		weaponCollisionVolume = new OBBCollisionVolume(0.5f, 0.5f);		// Approx width of DroidTop
		    		
			    	weapon.setObjectTypeToSpawn(Type.DROID_LASER_PULSE);
			    	
			    	weapon.setWeaponOffset(mDroidWeaponLaserPulseOffset);
		    		
		    		weapon.setLaserOffset(mDroidLaserPulseOffset);
		    		
		    		if (mDroidLaserPulseDrawableDroidObject == null) {
			    		FixedSizeArray<GameObject> laserPulseGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.DROID_LASER;
					    		laserObject.type = Type.DROID_LASER_PULSE;
					    		
//					    		laserObject.hitReactType = Type.EXPLOSION;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_SCREEN;
//					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mDroidLaserPulseDrawableDroidObject == null) {
						    		mDroidLaserPulseDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mDroidLaserPulseDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_laser_pulse_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_laser_pulse_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnDroidLaser() droid_laser_pulse gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mDroidLaserPulseDrawableDroidObject = new DrawableDroid();
//						    		mDroidLaserPulseDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mDroidLaserPulseDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mDroidLaserPulseDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		LaserComponent laser = (LaserComponent)allocateComponent(LaserComponent.class);		
						    	
						    	laserObject.magnitude = 0.5f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_pulse));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		laserPulseGameObjectArray.add(laserObject);
					  		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnDroidWeapon droid_laser_pulse object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}

			    		manager.setLaserGameObjectArray(Type.DROID_LASER_PULSE, MAX_LASER_OBJECTS, laserPulseGameObjectArray);	
		    		}
		    		
//		    		weaponObject.currentPosition.set(x, y, z, r);
		    		
		    		break;
		    		
		    	case DROID_WEAPON_LASER_EMP:
		    		if(mDroidWeaponLaserEmpDrawableDroidObject == null) {
		    			mDroidWeaponLaserEmpDrawableDroidObject = new DrawableDroid();
		    			
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mDroidWeaponLaserEmpDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_weapon_laser_emp_vbo, context);
				    	} else {
				    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser gl11 is null!");
				    	}
		    		}
		    		
		    		weaponObject.drawableDroid = mDroidWeaponLaserEmpDrawableDroidObject;
		    		
//		    		if (GameParameters.supportsVBOs && gl11 != null) {
//			    		weaponObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_weapon_laser_emp_vbo, context);
//			    	} else {
//			    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser_emp gl11 is null!");
//			    	}
		    		
//		    		weaponObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mLight0PositionBuffer);
//		    		weaponObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mMatShininessLowBuffer, mZeroBuffer);
		    		
//		    		weaponCollisionVolume = new OBBCollisionVolume(0.5f, 0.5f);		// Approx width of DroidTop
		    		
			    	weapon.setObjectTypeToSpawn(Type.DROID_LASER_EMP);
			    	
			    	weapon.setWeaponOffset(mDroidWeaponLaserEmpOffset);
		    		
		    		weapon.setLaserOffset(mDroidLaserEmpOffset);
		    		
		    		if (mDroidLaserEmpDrawableDroidObject == null) {
			    		FixedSizeArray<GameObject> laserEmpGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.DROID_LASER;
					    		laserObject.type = Type.DROID_LASER_EMP;
					    		
//					    		laserObject.hitReactType = Type.EXPLOSION;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_SCREEN;
//					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mDroidLaserEmpDrawableDroidObject == null) {
						    		mDroidLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mDroidLaserEmpDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_laser_emp_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_laser_emp_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnDroidLaser() droid_laser_emp gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mDroidLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		mDroidLaserEmpDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mDroidLaserEmpDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mDroidLaserEmpDrawableDroidObject;
						    			
							    laserObject.drawableDroid.setLaserGroup(true);
					    		
							    laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
								    			mLight0PositionBuffer);
							    laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
								    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		LaserComponent laser = (LaserComponent)allocateComponent(LaserComponent.class);		
						    	
						    	laserObject.magnitude = 0.4f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
//					    		laserHitReact.setSpawnOnDealHit(HitType.HIT, Type.DROID_LASER_STD, false, true);
					    		
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_emp));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		laserEmpGameObjectArray.add(laserObject);
					  		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnDroidWeapon() droid_laser_emp object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}

			    		manager.setLaserGameObjectArray(Type.DROID_LASER_EMP, MAX_LASER_OBJECTS, laserEmpGameObjectArray);	
		    		}
		    		
//		    		weaponObject.currentPosition.set(x, y, z, r);
		    		
		    		break;
		    		
		    	case DROID_WEAPON_LASER_GRENADE:
		    		if(mDroidWeaponLaserGrenadeDrawableDroidObject == null) {
		    			mDroidWeaponLaserGrenadeDrawableDroidObject = new DrawableDroid();
		    			
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mDroidWeaponLaserGrenadeDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_weapon_laser_grenade_vbo, context);
				    	} else {
				    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser gl11 is null!");
				    	}
		    		}
		    		
		    		weaponObject.drawableDroid = mDroidWeaponLaserGrenadeDrawableDroidObject;
		    		
//		    		if (GameParameters.supportsVBOs && gl11 != null) {
//			    		weaponObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_weapon_laser_grenade_vbo, context);
//			    	} else {
//			    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser_grenade gl11 is null!");
//			    	}
		    		
//		    		weaponCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);		// Hit Radius
		    		
			    	weapon.setObjectTypeToSpawn(Type.DROID_LASER_GRENADE);
			    	
			    	weapon.setWeaponOffset(mDroidWeaponLaserGrenadeOffset);
		    		
		    		weapon.setLaserOffset(mDroidLaserGrenadeOffset);
		    		
		    		if (mDroidLaserGrenadeDrawableDroidObject == null) {
			    		FixedSizeArray<GameObject> laserGrenadeGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.DROID_LASER;
					    		laserObject.type = Type.DROID_LASER_GRENADE;
					    		
//					    		laserObject.hitReactType = Type.EXPLOSION;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_SCREEN;
//					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mDroidLaserGrenadeDrawableDroidObject == null) {
						    		mDroidLaserGrenadeDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mDroidLaserGrenadeDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_laser_grenade_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_laser_grenade_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnDroidLaser() droid_laser_grenade gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mDroidLaserGrenadeDrawableDroidObject = new DrawableDroid();
//						    		mDroidLaserGrenadeDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mDroidLaserGrenadeDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mDroidLaserGrenadeDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		LaserComponent laser = (LaserComponent)allocateComponent(LaserComponent.class);
					    		
						    	laserObject.magnitude = 0.1f;
						    	
						    	// GameObject Collision Radius.  Always Enabled.
					    		DynamicCollisionComponent laserDynamicCollision = 
						    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
						          
						    	OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
//					    		// Area Collision Radius. Enabled in LaserComponent.
//					    		DynamicCollisionComponent areaLaserDynamicCollision = 
//						    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
//					    		
//					    		OBBCollisionVolume areaLaserCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);
//					          
//					    		areaLaserDynamicCollision.setCollisionVolume(areaLaserCollisionVolume);
//					    		
//					    		laser.setAreaLaserDynamicCollisionComponent(areaLaserDynamicCollision);
					          
					    		// Hit Reaction
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_rocket));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
//					    		areaLaserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		laserGrenadeGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnDroidWeapon() droid_laser_grenade object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
			    		manager.setLaserGameObjectArray(Type.DROID_LASER_GRENADE, MAX_LASER_OBJECTS, laserGrenadeGameObjectArray);	
		    		}
		    		
//		    		weaponObject.currentPosition.set(x, y, z, r);
		    		
		    		break;
		    		
		    	case DROID_WEAPON_LASER_ROCKET:
		    		if(mDroidWeaponLaserRocketDrawableDroidObject == null) {
		    			mDroidWeaponLaserRocketDrawableDroidObject = new DrawableDroid();
		    			
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mDroidWeaponLaserRocketDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_weapon_laser_rocket_vbo, context);
				    	} else {
				    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser gl11 is null!");
				    	}
		    		}
		    		
		    		weaponObject.drawableDroid = mDroidWeaponLaserRocketDrawableDroidObject;
		    		
//		    		if (GameParameters.supportsVBOs && gl11 != null) {
//			    		weaponObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_weapon_laser_rocket_vbo, context);
//			    	} else {
//			    		Log.e("VBO", "GameObjectFactory spawnDroidWeapon() droid_weapon_laser_rocket gl11 is null!");
//			    	}
		    		
//		    		weaponCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);		// Hit Radius
		    		
			    	weapon.setObjectTypeToSpawn(Type.DROID_LASER_ROCKET);
			    	
			    	weapon.setWeaponOffset(mDroidWeaponLaserRocketOffset);
		    		
		    		weapon.setLaserOffset(mDroidLaserRocketOffset);
		    		
		    		if (mDroidLaserRocketDrawableDroidObject == null) {
			    		FixedSizeArray<GameObject> laserRocketGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.DROID_LASER;
					    		laserObject.type = Type.DROID_LASER_ROCKET;
					    		
//					    		laserObject.hitReactType = Type.EXPLOSION;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_SCREEN;
//					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mDroidLaserRocketDrawableDroidObject == null) {
						    		mDroidLaserRocketDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mDroidLaserRocketDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_laser_rocket_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.droid_laser_rocket_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnDroidLaser() droid_laser_rocket gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mDroidLaserRocketDrawableDroidObject = new DrawableDroid();
//						    		mDroidLaserRocketDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mDroidLaserRocketDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mDroidLaserRocketDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		LaserComponent laser = (LaserComponent)allocateComponent(LaserComponent.class);
					    		
						    	laserObject.magnitude = 0.4f;
						    	
						    	// GameObject Collision Radius.  Always Enabled.
					    		DynamicCollisionComponent laserDynamicCollision = 
						    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
						          
						    	OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
//					    		// Area Collision Radius. Enabled in LaserComponent.
//					    		DynamicCollisionComponent areaLaserDynamicCollision = 
//						    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
//					    		
//					    		OBBCollisionVolume areaLaserCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);
//					          
//					    		areaLaserDynamicCollision.setCollisionVolume(areaLaserCollisionVolume);
//					    		
//					    		laser.setAreaLaserDynamicCollisionComponent(areaLaserDynamicCollision);
					          
					    		// Hit Reaction
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_rocket));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
//					    		areaLaserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		laserRocketGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnDroidWeapon() droid_laser_rocket object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
			    		manager.setLaserGameObjectArray(Type.DROID_LASER_ROCKET, MAX_LASER_OBJECTS, laserRocketGameObjectArray);	
		    		}
		    		
//		    		weaponObject.currentPosition.set(x, y, z, r);
		    		
		    		break;
		    		
		    	default:
//		    		weaponCollisionVolume = null;
		    		break;
		    	}
		    	
	    		weaponObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		weaponObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
	    		
//	            Log.i("GameFlow", "GameObjectFactory spawnDroidWeapon() weaponObject.type, mWeaponActiveType, mWeaponInventoryType = " +
//	            		weaponObject.type + ", " + mWeaponActiveType + ", " + mWeaponInventoryType);
	    		
		    	OBBCollisionVolume weaponCollisionVolume = new OBBCollisionVolume(0.5f, 0.5f);		// Approx width of DroidTop
		    	
		        weapon.setTopGameObject(mDroidTopGameObject);
		    	
		    	RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
		    	render.priority = SortConstants.PLAYER;
		    	
	    		DynamicCollisionComponent dynamicCollision = 
	    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
	          
	    		dynamicCollision.setCollisionVolume(weaponCollisionVolume);
		      
		    	weaponObject.add(weapon);
		    	weaponObject.add(dynamicCollision);
	      		weaponObject.add(render);
		    	
		    	manager.add(weaponObject);

	    	} else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
  	
    public void spawnEnemy(GL11 gl11, Type type, float x, float y, float z, float r) {	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
    		// spawn EnemyBottom
	    	GameObject objectBottom = mGameObjectPool.allocate();
	    	
	    	if (objectBottom != null) {
		    	objectBottom.group = Group.ENEMY;
		    	objectBottom.type = type;
		    	
		    	if (GameParameters.levelRow == 0) {
			    	objectBottom.currentState = CurrentState.INTRO;
			    	objectBottom.previousState = CurrentState.INTRO;
		    	} else {
			    	objectBottom.currentState = CurrentState.LEVEL_START;
			    	objectBottom.previousState = CurrentState.LEVEL_START;	
		    	}
//		    	objectBottom.currentState = manager.droidBottomGameObject.currentState;
//		    	objectBottom.previousState = manager.droidBottomGameObject.previousState;
		      
		    	objectBottom.currentPosition.set(x, y, z, r);
		      
		    	objectBottom.activationRadius = ACTIVATION_RADIUS_NORMAL;
//		    	objectBottom.activationRadius = ACTIVATION_RADIUS_NORMAL;
		    	
		    	objectBottom.attackRadius = ATTACK_RADIUS;
		      
		    	objectBottom.gameObjectId = mGameObjectIdCount;
		    	
		    	DynamicCollisionComponent dynamicCollision = 
			    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
			      
			    OBBCollisionVolume collisionVolume;
		    	
		    	RenderComponent renderBottom = (RenderComponent)allocateComponent(RenderComponent.class);
		    	renderBottom.priority = SortConstants.GENERAL_ENEMY;
		    	
	    		SoundSystem sound = sSystemRegistry.soundSystem;
		    	
		    	switch(type) {
		    	case ENEMY_EM_OT:
			    	if (mEnemyEMOTBottomDrawableDroidObject == null) {
			    		mEnemyEMOTBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMOTBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onetread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_onetread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMOTBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMOTBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyEMOTBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyEMOTBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyEMOT;
//			    	objectBottom.magnitude = 0.04f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyEMOT;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyEMOTBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_EM_OW:
			    	if (mEnemyEMOWBottomDrawableDroidObject == null) {
			    		mEnemyEMOWBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMOWBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMOWBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMOWBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyEMOWBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyEMOWBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyEMOW;
//			    	objectBottom.magnitude = 0.05f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyEMOW;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.600f, 0.600f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyEMOWBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_EM_OW_BOSS:
			    	if (mEnemyEMOWBossBottomDrawableDroidObject == null) {
			    		mEnemyEMOWBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMOWBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyEMOWBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyEMOWBoss;
//			    	objectBottom.magnitude = 0.07f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyEMOWBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.750f, 0.750f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_EM_SL:
			    	if (mEnemyEMSLBottomDrawableDroidObject == null) {
			    		mEnemyEMSLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMSLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMSLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMSLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyEMSLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyEMSLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyEMSL;
//			    	objectBottom.magnitude = 0.025f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPIDER_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyEMSL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyEMSLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_EM_SL_BOSS:
			    	if (mEnemyEMSLBossBottomDrawableDroidObject == null) {
			    		mEnemyEMSLBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMSLBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyEMSLBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyEMSLBoss;
//			    	objectBottom.magnitude = 0.045f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPIDER_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyEMSLBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.080f, 1.080f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_FL:
			    	if (mEnemyHDFLBottomDrawableDroidObject == null) {
			    		mEnemyHDFLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDFLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_flying_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_flying_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDFLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDFLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDFLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDFLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDFL;
//			    	objectBottom.magnitude = 0.06f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.FLY;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDFL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.600f, 0.600f);
			    	
			    	renderBottom.setDrawOffset(mEnemyHDFLOffset);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDFLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_OL:
			    	if (mEnemyHDOLBottomDrawableDroidObject == null) {
			    		mEnemyHDOLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDOLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_oneleg_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_oneleg_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDOLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDOLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDOLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDOLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDOL;
//			    	objectBottom.magnitude = 0.03f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPRING_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDOL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.730f, 0.730f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDOLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_OT:
			    	if (mEnemyHDOTBottomDrawableDroidObject == null) {
			    		mEnemyHDOTBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDOTBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_onetread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_onetread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDOTBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDOTBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDOTBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDOTBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDOT;
//			    	objectBottom.magnitude = 0.04f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDOT;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDOTBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_OW:
			    	if (mEnemyHDOWBottomDrawableDroidObject == null) {
			    		mEnemyHDOWBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDOWBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_onewheel_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_onewheel_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDOWBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDOWBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDOWBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDOWBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDOW;
//			    	objectBottom.magnitude = 0.05f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDOW;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.570f, 0.570f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDOWBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_SL:
			    	if (mEnemyHDSLBottomDrawableDroidObject == null) {
			    		mEnemyHDSLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDSLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_spiderlegs_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_spiderlegs_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDSLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDSLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDSLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDSLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDSL;
//			    	objectBottom.magnitude = 0.025f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPIDER_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDSL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDSLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_TL:
			    	if (mEnemyHDTLBottomDrawableDroidObject == null) {
			    		mEnemyHDTLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDTLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDTLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDTL;
//			    	objectBottom.magnitude = 0.03f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPRING_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDTL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.800f, 0.800f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDTLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_TL_BOSS:
			    	if (mEnemyHDTLBossBottomDrawableDroidObject == null) {
			    		mEnemyHDTLBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTLBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDTLBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDTLBoss;
//			    	objectBottom.magnitude = 0.05f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPRING_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDTLBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.120f, 1.120f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_TT:
			    	if (mEnemyHDTTBottomDrawableDroidObject == null) {
			    		mEnemyHDTTBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTTBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twotread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twotread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTTBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTTBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDTTBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDTTBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDTT;
//			    	objectBottom.magnitude = 0.03f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDTT;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.780f, 0.780f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDTTBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_TT_BOSS:
			    	if (mEnemyHDTTBossBottomDrawableDroidObject == null) {
			    		mEnemyHDTTBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTTBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twotread_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twotread_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDTTBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDTTBoss;
//			    	objectBottom.magnitude = 0.05f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDTTBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.070f, 1.070f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_HD_TW:
			    	if (mEnemyHDTWBottomDrawableDroidObject == null) {
			    		mEnemyHDTWBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTWBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twotread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twotread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTWBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTWBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyHDTWBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyHDTWBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyHDTW;
//			    	objectBottom.magnitude = 0.05f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyHDTW;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.640f, 0.640f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyHDTWBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LC_FM:
			    	if (mEnemyLCFMBottomDrawableDroidObject == null) {
			    		mEnemyLCFMBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCFMBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_floormount_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_floormount_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCFMBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCFMBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLCFMBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLCFMBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = MAGNITUDE_ENEMY_LC_FM;	// Avoid divide by zero calculations
//			    	objectBottom.magnitude = 0.00001f;	// Avoid divide by zero calculations
			      
			    	objectBottom.bottomMoveType = BottomMoveType.MOUNT;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLCFM;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.750f, 0.750f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyLCFMBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LC_OT:
			    	if (mEnemyLCOTBottomDrawableDroidObject == null) {
			    		mEnemyLCOTBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCOTBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCOTBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCOTBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLCOTBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLCOTBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLCOT;
//			    	objectBottom.magnitude = 0.04f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLCOT;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyLCOTBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LC_OT_BOSS:
			    	if (mEnemyLCOTBossBottomDrawableDroidObject == null) {
			    		mEnemyLCOTBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCOTBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLCOTBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLCOTBoss;
//			    	objectBottom.magnitude = 0.06f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLCOTBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.940f, 0.940f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    	
		    	case ENEMY_LC_SL:
			    	if (mEnemyLCSLBottomDrawableDroidObject == null) {
			    		mEnemyLCSLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCSLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCSLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCSLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLCSLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLCSLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLCSL;
//			    	objectBottom.magnitude = 0.025f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPIDER_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLCSL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyLCSLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LC_SL_BOSS:
			    	if (mEnemyLCSLBossBottomDrawableDroidObject == null) {
			    		mEnemyLCSLBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCSLBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLCSLBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLCSLBoss;
//			    	objectBottom.magnitude = 0.045f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.SPIDER_LEGS;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLCSLBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.080f, 1.080f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LC_TT:
			    	if (mEnemyLCTTBottomDrawableDroidObject == null) {
			    		mEnemyLCTTBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCTTBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_twotread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_twotread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCTTBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCTTBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLCTTBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLCTTBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLCTT;
//			    	objectBottom.magnitude = 0.03f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLCTT;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.780f, 0.780f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyLCTTBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LS_FM:
			    	if (mEnemyLSFMBottomDrawableDroidObject == null) {
			    		mEnemyLSFMBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLSFMBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasersword_floormount_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasersword_floormount_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLSFMBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLSFMBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLSFMBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLSFMBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = MAGNITUDE_ENEMY_LS_FM;	// Avoid divide by zero calculations
//			    	objectBottom.magnitude = 0.00001f;	// Avoid divide by zero calculations
			      
			    	objectBottom.bottomMoveType = BottomMoveType.MOUNT;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLSFM;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.4f, 1.4f);
//			      	collisionVolume = new OBBCollisionVolume(0.550f, 0.550f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyLSFMBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LS_TT:
			    	if (mEnemyLSTTBottomDrawableDroidObject == null) {
			    		mEnemyLSTTBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLSTTBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLSTTBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLSTTBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLSTTBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLSTTBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLSTT;
//			    	objectBottom.magnitude = 0.03f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLSTT;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.4f, 1.4f);
//			      	collisionVolume = new OBBCollisionVolume(0.580f, 0.580f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyLSTTBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_LS_TT_BOSS:
			    	if (mEnemyLSTTBossBottomDrawableDroidObject == null) {
			    		mEnemyLSTTBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLSTTBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLSTTBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLSTTBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyLSTTBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyLSTTBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyLSTTBoss;
//			    	objectBottom.magnitude = 0.05f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyLSTTBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.8f, 1.8f);
//			      	collisionVolume = new OBBCollisionVolume(0.870f, 0.870f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_TA_FL:
			    	if (mEnemyTAFLBottomDrawableDroidObject == null) {
			    		mEnemyTAFLBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyTAFLBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyTAFLBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyTAFLBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyTAFLBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyTAFLBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyTAFL;
//			    	objectBottom.magnitude = 0.08f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.FLY;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyTAFL;
			      	
			      	collisionVolume = new OBBCollisionVolume(0.800f, 0.800f);
			    	
			    	renderBottom.setDrawOffset(mEnemyTAFLOffset);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyTAFLBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_TA_FL_BOSS:
			    	if (mEnemyTAFLBossBottomDrawableDroidObject == null) {
			    		mEnemyTAFLBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyTAFLBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyTAFLBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyTAFLBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyTAFLBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyTAFLBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyTAFLBoss;
//			    	objectBottom.magnitude = 0.1f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.FLY;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyTAFLBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.100f, 1.100f);
			    	
			    	renderBottom.setDrawOffset(mEnemyTAFLBossOffset);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	case ENEMY_DR_TT_BOSS:
			    	if (mEnemyDRTTBossBottomDrawableDroidObject == null) {
			    		mEnemyDRTTBossBottomDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectBottom.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyDRTTBossBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_droid_twotread_boss_bottom_vbo, context);
//			    			objectBottom.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_droid_twotread_boss_bottom_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyDRTTBossBottomDrawableDroidObject = new DrawableDroid();
//			    		mEnemyDRTTBossBottomDrawableDroidObject = objectBottom.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectBottom.drawableDroid = mEnemyDRTTBossBottomDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectBottom.drawableDroid = mEnemyDRTTBossBottomDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectBottom.magnitude = mMagnitudeEnemyDRTTBoss;
//			    	objectBottom.magnitude = 0.07f;
			      
			    	objectBottom.bottomMoveType = BottomMoveType.WHEELS_TREAD;
			    	
			      	objectBottom.hitPoints = mHitPointsEnemyDRTTBoss;
			      	
			      	collisionVolume = new OBBCollisionVolume(1.100f, 1.100f);
			    	
			    	renderBottom.setDrawOffset(0.0f, 0.0f, 0.0f);
			    	
			    	// Set latest Bottom for next Top state reference
			    	mEnemyBossBottomGameObject = objectBottom;
			    	
		    		break;
		    		
		    	default:
		    		collisionVolume = new OBBCollisionVolume(0.779f, 0.779f);
		    		break;
		    	}
		    	
	    		objectBottom.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectBottom.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		    	
		    	EnemyBottomComponent enemyBottom = (EnemyBottomComponent)allocateComponent(EnemyBottomComponent.class);
		    	
		    	if (objectBottom.bottomMoveType == BottomMoveType.SPIDER_LEGS) {
//		    	if (objectBottom.bottomMoveType == BottomMoveType.SPIDER_LEGS || objectBottom.type == Type.ENEMY_EM_OW_BOSS ||
//		    			objectBottom.type == Type.ENEMY_EM_SL_BOSS || objectBottom.type == Type.ENEMY_HD_TL_BOSS ||
//		    			objectBottom.type == Type.ENEMY_HD_TT_BOSS || objectBottom.type == Type.ENEMY_LC_OT_BOSS ||
//		    			objectBottom.type == Type.ENEMY_LC_SL_BOSS || objectBottom.type == Type.ENEMY_LS_TT_BOSS ||
//		    			objectBottom.type == Type.ENEMY_DR_TT_BOSS) {
		    		if (sound != null) {
		    			enemyBottom.setBounceSound(sound.load(R.raw.sound_gameobject_bounce));
		    			enemyBottom.setHitSound(sound.load(R.raw.sound_enemy_spider_hit));
		    			enemyBottom.setFallSound(sound.load(R.raw.sound_enemy_fly_death));
		    			enemyBottom.setDeathSound(sound.load(R.raw.sound_enemy_spider_death));
		    		}
		    	} else if (objectBottom.bottomMoveType == BottomMoveType.FLY) {
//		    	} else if (objectBottom.bottomMoveType == BottomMoveType.FLY || objectBottom.type == Type.ENEMY_TA_FL_BOSS) {
		    		if (sound != null) {
		    			enemyBottom.setBounceSound(sound.load(R.raw.sound_gameobject_bounce));
		    			enemyBottom.setHitSound(sound.load(R.raw.sound_enemy_fly_hit));
		    			enemyBottom.setFallSound(sound.load(R.raw.sound_enemy_fly_death));
		    			enemyBottom.setDeathSound(sound.load(R.raw.sound_enemy_fly_death));
		    		}
		    	} else {
		    		if (sound != null) {			    			
		    			enemyBottom.setBounceSound(sound.load(R.raw.sound_gameobject_bounce));
		    			enemyBottom.setHitSound(sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit));
		    			enemyBottom.setFallSound(sound.load(R.raw.sound_enemy_fly_death));
		    			enemyBottom.setDeathSound(sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_death));
		    		}
		    	}
		      
//		    	DynamicCollisionComponent dynamicCollision = 
//		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
//		      
//		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.579f, 0.579f);
		      
		    	dynamicCollision.setCollisionVolume(collisionVolume);
		    
		    	HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
		    
		    	dynamicCollision.setHitReactionComponent(hitReact);
		
		    	objectBottom.add(enemyBottom);
		    	objectBottom.add(renderBottom);
		    	objectBottom.add(hitReact);
		    	objectBottom.add(dynamicCollision);
		    	
		    	manager.add(objectBottom);
		      
		    	mGameObjectIdCount++;
	    	} else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
	    	
    		// spawn EnemyTop
	        GameObject objectTop = mGameObjectPool.allocate();
	        
	        if (objectTop != null) {
		        objectTop.group = Group.ENEMY;
		    	objectTop.type = type;
		        
		        objectTop.activationRadius = objectBottom.activationRadius;
		        
		        objectTop.gameObjectId = mGameObjectIdCount;
		        
		        EnemyTopComponent enemyTop = (EnemyTopComponent)allocateComponent(EnemyTopComponent.class);
		        RenderComponent renderTop = (RenderComponent)allocateComponent(RenderComponent.class);
		        
		        switch(type) {
		    	case ENEMY_EM_OT:
			    	if (mEnemyEMOTTopDrawableDroidObject == null) {
			    		mEnemyEMOTTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMOTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onetread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_onetread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMOTTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMOTTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyEMOTTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyEMOTTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyEMOTBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyEMOTBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_EMP);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserEmpOffset);
		    		
			        if (mEnemyLaserEmpGameObjectArray == null) {
				        mEnemyLaserEmpGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserEmpOTGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_EMP;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserEmpDrawableDroidObject == null) {
						    		mEnemyLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserEmpDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_emp_ot_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_emp_ot_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_emp_ot gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserEmpDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserEmpDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserEmpDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_emp));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserEmpGameObjectArray.add(laserObject);
//					    		laserEmpOTGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_emp object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
			    		
				        manager.setLaserGameObjectArray(Type.ENEMY_LASER_EMP, MAX_LASER_OBJECTS, mEnemyLaserEmpGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_EMP, MAX_LASER_OBJECTS, laserEmpOTGameObjectArray);
			        }
			    	
		    		break;
		    		
		    	case ENEMY_EM_OW:
			    	if (mEnemyEMOWTopDrawableDroidObject == null) {
			    		mEnemyEMOWTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMOWTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMOWTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMOWTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyEMOWTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyEMOWTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyEMOWBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyEMOWBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_EMP);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserEmpOffset);
		    		
			        if (mEnemyLaserEmpGameObjectArray == null) {
			        	mEnemyLaserEmpGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserEmpOWGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_EMP;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserEmpDrawableDroidObject == null) {
						    		mEnemyLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserEmpDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_emp_ow_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_emp_ow_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_emp_ow gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserEmpDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserEmpDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserEmpDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_emp));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserEmpGameObjectArray.add(laserObject);
//					    		laserEmpOWGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_emp object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_EMP, MAX_LASER_OBJECTS, mEnemyLaserEmpGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_EMP, MAX_LASER_OBJECTS, laserEmpOWGameObjectArray);
			        }			        
			    	
		    		break;
		    		
		    	case ENEMY_EM_OW_BOSS:
			    	if (mEnemyEMOWBossTopDrawableDroidObject == null) {
			    		mEnemyEMOWBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMOWBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_onewheel_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMOWBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMOWBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyEMOWBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyEMOWBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_BOSS_LASER_EMP);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyBossLaserEmpOffset);
		    		
		    		FixedSizeArray<GameObject> laserEmpOWBossGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_BOSS_LASER_OBJECTS);
		    		
		    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
		    			GameObject laserObject = mGameObjectPool.allocate();
				    	
				    	if (laserObject != null) {
				    		laserObject.group = Group.ENEMY_LASER;
				    		laserObject.type = Type.ENEMY_BOSS_LASER_EMP;
					      
				    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
					      
				    		laserObject.gameObjectId = mGameObjectIdCount;
					    	
					    	if (mEnemyBossLaserDrawableDroidObject == null) {
					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//					    		laserObject.objectInstantiation();
		    			
					    		if (GameParameters.supportsVBOs && gl11 != null) {
					    			mEnemyBossLaserDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_emp_ow_boss_vbo, context);
//					    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_emp_ow_boss_vbo, context);
					    		} else {
					    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_emp_ow gl11 is null!");
					    		}
					    		
//					    		laserObject.drawableDroid.setLaserGroup(true);
//					    		
//					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
//					    		
//					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		mEnemyBossLaserDrawableDroidObject = laserObject.drawableDroid;
//					    		
//					    	} else {
//					    		laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;	
//					    		
					    	}
					    	
					    	laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;
					    	
				    		laserObject.drawableDroid.setLaserGroup(true);
				    		
				    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mLight0PositionBuffer);
				    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mMatShininessLowBuffer, mZeroBuffer);
					    	
				    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
				    		laserRender.priority = SortConstants.PROJECTILE;
				      
				    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
				    		
					    	laserObject.magnitude = 0.15f;
				          
				    		DynamicCollisionComponent laserDynamicCollision = 
				    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				          
				    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.15f, 0.15f);
				          
				    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
				          
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				    		
				    		SoundSystem sound = sSystemRegistry.soundSystem;
				    		if (sound != null) {
				    			laser.setFireSound(sound.load(R.raw.sound_laser_emp));
				    		}
				        
				    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
				      
				    		laserObject.add(laser);
				    		laserObject.add(laserRender);
				    		laserObject.add(laserHitReact);
				    		laserObject.add(laserDynamicCollision);
				    		
				    		laserEmpOWBossGameObjectArray.add(laserObject);
				    		
							if (GameParameters.debug) {
					    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_emp object ID and type = " + 
					    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
							}
				          
				    		mGameObjectIdCount++;
				    	} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
		    		}
			    	
		    		manager.setLaserGameObjectArray(Type.ENEMY_BOSS_LASER_EMP, MAX_LASER_OBJECTS, laserEmpOWBossGameObjectArray);			        
			    	
		    		break;
		    		
		    	case ENEMY_EM_SL:
			    	if (mEnemyEMSLTopDrawableDroidObject == null) {
			    		mEnemyEMSLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMSLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMSLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMSLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyEMSLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyEMSLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyEMSLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyEMSLBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_EMP);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserEmpOffset);
		    		
			        if (mEnemyLaserEmpGameObjectArray == null) {
			        	mEnemyLaserEmpGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserEmpSLGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_EMP;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserEmpDrawableDroidObject == null) {
						    		mEnemyLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserEmpDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_emp_sl_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_emp_sl_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_emp_sl gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserEmpDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserEmpDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserEmpDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserEmpDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_emp));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserEmpGameObjectArray.add(laserObject);
//					    		laserEmpSLGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_emp object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
				        manager.setLaserGameObjectArray(Type.ENEMY_LASER_EMP, MAX_LASER_OBJECTS, mEnemyLaserEmpGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_EMP, MAX_LASER_OBJECTS, laserEmpSLGameObjectArray); 
			        }
			    	
		    		break;
		    		
		    	case ENEMY_EM_SL_BOSS:
			    	if (mEnemyEMSLBossTopDrawableDroidObject == null) {
			    		mEnemyEMSLBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyEMSLBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_emp_spiderlegs_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyEMSLBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyEMSLBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyEMSLBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyEMSLBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_BOSS_LASER_EMP);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyBossLaserEmpOffset);
		    		
		    		FixedSizeArray<GameObject> laserEmpSLBossGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_BOSS_LASER_OBJECTS);
		    		
		    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
		    			GameObject laserObject = mGameObjectPool.allocate();
				    	
				    	if (laserObject != null) {
				    		laserObject.group = Group.ENEMY_LASER;
				    		laserObject.type = Type.ENEMY_BOSS_LASER_EMP;
					      
				    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
					      
				    		laserObject.gameObjectId = mGameObjectIdCount;
					    	
					    	if (mEnemyBossLaserDrawableDroidObject == null) {
					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//					    		laserObject.objectInstantiation();
		    			
					    		if (GameParameters.supportsVBOs && gl11 != null) {
					    			mEnemyBossLaserDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_emp_sl_boss_vbo, context);
//					    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_emp_sl_boss_vbo, context);
					    		} else {
					    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_emp_sl gl11 is null!");
					    		}
					    		
//					    		laserObject.drawableDroid.setLaserGroup(true);
//					    		
//					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
//					    		
//					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		mEnemyBossLaserDrawableDroidObject = laserObject.drawableDroid;
//					    		
//					    	} else {
//					    		laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;	
//					    		
					    	}
					    	
					    	laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;
					    	
				    		laserObject.drawableDroid.setLaserGroup(true);
				    		
				    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mLight0PositionBuffer);
				    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mMatShininessLowBuffer, mZeroBuffer);
					    	
				    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
				    		laserRender.priority = SortConstants.PROJECTILE;
				      
				    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
				    		
					    	laserObject.magnitude = 0.15f;
				          
				    		DynamicCollisionComponent laserDynamicCollision = 
				    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				          
				    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.15f, 0.15f);
				          
				    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
				          
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				    		
				    		SoundSystem sound = sSystemRegistry.soundSystem;
				    		if (sound != null) {
				    			laser.setFireSound(sound.load(R.raw.sound_laser_emp));
				    		}
				        
				    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
				      
				    		laserObject.add(laser);
				    		laserObject.add(laserRender);
				    		laserObject.add(laserHitReact);
				    		laserObject.add(laserDynamicCollision);
				    		
				    		laserEmpSLBossGameObjectArray.add(laserObject);
				    		
							if (GameParameters.debug) {
					    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_emp object ID and type = " + 
					    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");
							}
				          
				    		mGameObjectIdCount++;
				    	} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
		    		}
			    	
		    		manager.setLaserGameObjectArray(Type.ENEMY_BOSS_LASER_EMP, MAX_LASER_OBJECTS, laserEmpSLBossGameObjectArray); 
			    	
		    		break;
		    		
		    	case ENEMY_HD_FL:
			    	if (mEnemyHDFLTopDrawableDroidObject == null) {
			    		mEnemyHDFLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDFLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_flying_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_flying_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDFLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDFLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDFLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDFLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDFLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDFLBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_OL:
			    	if (mEnemyHDOLTopDrawableDroidObject == null) {
			    		mEnemyHDOLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDOLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_oneleg_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_oneleg_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDOLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDOLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDOLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDOLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDOLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDOLBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_OT:
			    	if (mEnemyHDOTTopDrawableDroidObject == null) {
			    		mEnemyHDOTTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDOTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_onetread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_onetread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDOTTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDOTTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDOTTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDOTTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDOTBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDOTBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_OW:
			    	if (mEnemyHDOWTopDrawableDroidObject == null) {
			    		mEnemyHDOWTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDOWTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_onewheel_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_onewheel_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDOWTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDOWTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDOWTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDOWTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDOWBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDOWBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_SL:
			    	if (mEnemyHDSLTopDrawableDroidObject == null) {
			    		mEnemyHDSLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDSLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_spiderlegs_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_spiderlegs_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDSLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDSLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDSLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDSLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDSLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDSLBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_TL:
			    	if (mEnemyHDTLTopDrawableDroidObject == null) {
			    		mEnemyHDTLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDTLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDTLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDTLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDTLBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_TL_BOSS:
			    	if (mEnemyHDTLBossTopDrawableDroidObject == null) {
			    		mEnemyHDTLBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTLBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twolegs_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTLBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTLBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDTLBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDTLBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_TT:
			    	if (mEnemyHDTTTopDrawableDroidObject == null) {
			    		mEnemyHDTTTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twotread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twotread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTTTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTTTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDTTTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDTTTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDTTBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDTTBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_TT_BOSS:
			    	if (mEnemyHDTTBossTopDrawableDroidObject == null) {
			    		mEnemyHDTTBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTTBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twotread_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twotread_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTTBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTTBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDTTBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDTTBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_HD_TW:
			    	if (mEnemyHDTWTopDrawableDroidObject == null) {
			    		mEnemyHDTWTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyHDTWTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_twotread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_head_twotread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyHDTWTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyHDTWTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyHDTWTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyHDTWTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyHDTWBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyHDTWBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_LC_FM:
			    	if (mEnemyLCFMTopDrawableDroidObject == null) {
			    		mEnemyLCFMTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCFMTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_floormount_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_floormount_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCFMTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCFMTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLCFMTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLCFMTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyLCFMBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyLCFMBottomGameObject.currentPosition);   
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_STD);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserStdOffset);
		    		
			        if (mEnemyLaserStdGameObjectArray == null) {
			        	mEnemyLaserStdGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserStdFMGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_STD;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserStdDrawableDroidObject == null) {
						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserStdDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_std_fm_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_std_fm_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_fm gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserStdDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserStdGameObjectArray.add(laserObject);
//					    		laserStdFMGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
			    		
				        manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, mEnemyLaserStdGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, laserStdFMGameObjectArray);
			        }
			    	
		    		break;
		    		
		    	case ENEMY_LC_OT:
			    	if (mEnemyLCOTTopDrawableDroidObject == null) {
			    		mEnemyLCOTTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCOTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCOTTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCOTTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLCOTTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLCOTTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyLCOTBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyLCOTBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_STD);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserStdOffset);
		    		
			        if (mEnemyLaserStdGameObjectArray == null) {
			        	mEnemyLaserStdGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserStdOTGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_STD;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserStdDrawableDroidObject == null) {
						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserStdDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_std_ot_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_std_ot_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_ot gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserStdDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserStdGameObjectArray.add(laserObject);
//					    		laserStdOTGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
				        manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, mEnemyLaserStdGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, laserStdOTGameObjectArray);
			        }
			    	
		    		break;
		    		
		    	case ENEMY_LC_OT_BOSS:
			    	if (mEnemyLCOTBossTopDrawableDroidObject == null) {
			    		mEnemyLCOTBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCOTBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_onetread_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCOTBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCOTBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLCOTBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLCOTBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_BOSS_LASER_STD);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyBossLaserStdOffset);
		    		
		    		FixedSizeArray<GameObject> laserStdOTBossGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_BOSS_LASER_OBJECTS);
		    		
		    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
		    			GameObject laserObject = mGameObjectPool.allocate();
				    	
				    	if (laserObject != null) {
				    		laserObject.group = Group.ENEMY_LASER;
				    		laserObject.type = Type.ENEMY_BOSS_LASER_STD;
					      
				    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
					      
				    		laserObject.gameObjectId = mGameObjectIdCount;
					    	
					    	if (mEnemyBossLaserDrawableDroidObject == null) {
					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//					    		laserObject.objectInstantiation();
		    			
					    		if (GameParameters.supportsVBOs && gl11 != null) {
					    			mEnemyBossLaserDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_std_ot_boss_vbo, context);
//					    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_std_ot_boss_vbo, context);
					    		} else {
					    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_ot gl11 is null!");
					    		}
					    		
//					    		laserObject.drawableDroid.setLaserGroup(true);
//					    		
//					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
//					    		
//					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		mEnemyBossLaserDrawableDroidObject = laserObject.drawableDroid;
//					    		
//					    	} else {
//					    		laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;	
//					    		
					    	}
					    	
					    	laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;
					    	
				    		laserObject.drawableDroid.setLaserGroup(true);
				    		
				    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mLight0PositionBuffer);
				    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mMatShininessLowBuffer, mZeroBuffer);
					    	
				    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
				    		laserRender.priority = SortConstants.PROJECTILE;
				      
				    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
				    		
					    	laserObject.magnitude = 0.15f;
				          
				    		DynamicCollisionComponent laserDynamicCollision = 
				    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				          
				    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.15f, 0.15f);
				          
				    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
				          
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				    		
				    		SoundSystem sound = sSystemRegistry.soundSystem;
				    		if (sound != null) {
				    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
				    		}
				        
				    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
				      
				    		laserObject.add(laser);
				    		laserObject.add(laserRender);
				    		laserObject.add(laserHitReact);
				    		laserObject.add(laserDynamicCollision);
				    		
				    		laserStdOTBossGameObjectArray.add(laserObject);
				    		
							if (GameParameters.debug) {
					    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
					    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
							}
				          
				    		mGameObjectIdCount++;
				    	} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
		    		}
			    	
		    		manager.setLaserGameObjectArray(Type.ENEMY_BOSS_LASER_STD, MAX_LASER_OBJECTS, laserStdOTBossGameObjectArray);
			    	
		    		break;
		    	
		    	case ENEMY_LC_SL:
			    	if (mEnemyLCSLTopDrawableDroidObject == null) {
			    		mEnemyLCSLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCSLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCSLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCSLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLCSLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLCSLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyLCSLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyLCSLBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_STD);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserStdOffset);
		    		
			        if (mEnemyLaserStdGameObjectArray == null) {
			        	mEnemyLaserStdGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserStdSLGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_STD;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserStdDrawableDroidObject == null) {
						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserStdDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_std_sl_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_std_sl_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_sl gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserStdDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;	
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserStdGameObjectArray.add(laserObject);
//					    		laserStdSLGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
				        manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, mEnemyLaserStdGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, laserStdSLGameObjectArray);
			        }
			    	
		    		break;
		    		
		    	case ENEMY_LC_SL_BOSS:
			    	if (mEnemyLCSLBossTopDrawableDroidObject == null) {
			    		mEnemyLCSLBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCSLBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_spiderlegs_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCSLBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCSLBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLCSLBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLCSLBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_BOSS_LASER_STD);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyBossLaserStdOffset);
		    		
		    		FixedSizeArray<GameObject> laserStdSLBossGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_BOSS_LASER_OBJECTS);
		    		
		    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
		    			GameObject laserObject = mGameObjectPool.allocate();
				    	
				    	if (laserObject != null) {
				    		laserObject.group = Group.ENEMY_LASER;
				    		laserObject.type = Type.ENEMY_BOSS_LASER_STD;
					      
				    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
					      
				    		laserObject.gameObjectId = mGameObjectIdCount;
					    	
					    	if (mEnemyBossLaserDrawableDroidObject == null) {
					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//					    		laserObject.objectInstantiation();
		    			
					    		if (GameParameters.supportsVBOs && gl11 != null) {
					    			mEnemyBossLaserDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_std_sl_boss_vbo, context);
//					    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_std_sl_boss_vbo, context);
					    		} else {
					    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_sl gl11 is null!");
					    		}
					    		
//					    		laserObject.drawableDroid.setLaserGroup(true);
//					    		
//					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
//					    		
//					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		mEnemyBossLaserDrawableDroidObject = laserObject.drawableDroid;
//					    		
//					    	} else {
//					    		laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;	
//					    		
					    	}
					    	
					    	laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;
					    	
				    		laserObject.drawableDroid.setLaserGroup(true);
				    		
				    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mLight0PositionBuffer);
				    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mMatShininessLowBuffer, mZeroBuffer);
					    	
				    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
				    		laserRender.priority = SortConstants.PROJECTILE;
				      
				    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
				    		
					    	laserObject.magnitude = 0.15f;
				          
				    		DynamicCollisionComponent laserDynamicCollision = 
				    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				          
				    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.15f, 0.15f);
				          
				    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
				          
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				    		
				    		SoundSystem sound = sSystemRegistry.soundSystem;
				    		if (sound != null) {
				    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
				    		}
				        
				    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
				      
				    		laserObject.add(laser);
				    		laserObject.add(laserRender);
				    		laserObject.add(laserHitReact);
				    		laserObject.add(laserDynamicCollision);
				    		
				    		laserStdSLBossGameObjectArray.add(laserObject);
				    		
							if (GameParameters.debug) {
					    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
					    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
							}
				          
				    		mGameObjectIdCount++;
				    	} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
		    		}
			    	
		    		manager.setLaserGameObjectArray(Type.ENEMY_BOSS_LASER_STD, MAX_LASER_OBJECTS, laserStdSLBossGameObjectArray);
			    	
		    		break;
		    		
		    	case ENEMY_LC_TT:
			    	if (mEnemyLCTTTopDrawableDroidObject == null) {
			    		mEnemyLCTTTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLCTTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasercannon_twotread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasercannon_twotread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLCTTTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLCTTTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLCTTTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLCTTTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyLCTTBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyLCTTBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_LASER_STD);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyLaserStdOffset);
		    		
			        if (mEnemyLaserStdGameObjectArray == null) {
			        	mEnemyLaserStdGameObjectArray = new FixedSizeArray<GameObject>(MAX_ENEMY_LASER_OBJECTS);
//			    		FixedSizeArray<GameObject> laserStdTTGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
			    		
			    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
			    			GameObject laserObject = mGameObjectPool.allocate();
					    	
					    	if (laserObject != null) {
					    		laserObject.group = Group.ENEMY_LASER;
					    		laserObject.type = Type.ENEMY_LASER_STD;
						      
					    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
						      
					    		laserObject.gameObjectId = mGameObjectIdCount;
						    	
						    	if (mEnemyLaserStdDrawableDroidObject == null) {
						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						    		laserObject.objectInstantiation();
			    			
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mEnemyLaserStdDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_std_tt_vbo, context);
//						    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_std_tt_vbo, context);
						    		} else {
						    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_tt gl11 is null!");
						    		}
						    		
//						    		laserObject.drawableDroid.setLaserGroup(true);
//						    		
//						    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mLight0PositionBuffer);
//						    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//							    			mMatShininessLowBuffer, mZeroBuffer);
//						    		
//						    		mEnemyLaserStdDrawableDroidObject = new DrawableDroid();
//						    		mEnemyLaserStdDrawableDroidObject = laserObject.drawableDroid;
//						    		
//						    	} else {
//						    		laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;	
//						    		
						    	}
						    	
						    	laserObject.drawableDroid = mEnemyLaserStdDrawableDroidObject;
						    	
					    		laserObject.drawableDroid.setLaserGroup(true);
					    		
					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mLight0PositionBuffer);
					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
						    			mMatShininessLowBuffer, mZeroBuffer);
						    	
					    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
					    		laserRender.priority = SortConstants.PROJECTILE;
					      
					    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
					    		
						    	laserObject.magnitude = 0.15f;
					          
					    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    		OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.1f, 0.1f);
					          
					    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
					          
					    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
					    		
					    		SoundSystem sound = sSystemRegistry.soundSystem;
					    		if (sound != null) {
					    			laser.setFireSound(sound.load(R.raw.sound_laser_std));
					    		}
					        
					    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
					      
					    		laserObject.add(laser);
					    		laserObject.add(laserRender);
					    		laserObject.add(laserHitReact);
					    		laserObject.add(laserDynamicCollision);
					    		
					    		mEnemyLaserStdGameObjectArray.add(laserObject);
//					    		laserStdTTGameObjectArray.add(laserObject);
					    		
								if (GameParameters.debug) {
						    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
						    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
								}
					          
					    		mGameObjectIdCount++;
					    	} else {
					    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
					    	}
			    		}
				    	
				        manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, mEnemyLaserStdGameObjectArray);
//			    		manager.setLaserGameObjectArray(Type.ENEMY_LASER_STD, MAX_LASER_OBJECTS, laserStdTTGameObjectArray);
			        }
			        
		    		break;
		    		
		    	case ENEMY_LS_FM:
			    	if (mEnemyLSFMTopDrawableDroidObject == null) {
			    		mEnemyLSFMTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLSFMTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasersword_floormount_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasersword_floormount_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLSFMTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLSFMTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLSFMTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLSFMTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyLSFMBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyLSFMBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_LS_TT:
			    	if (mEnemyLSTTTopDrawableDroidObject == null) {
			    		mEnemyLSTTTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLSTTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLSTTTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLSTTTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLSTTTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLSTTTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyLSTTBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyLSTTBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_LS_TT_BOSS:
			    	if (mEnemyLSTTBossTopDrawableDroidObject == null) {
			    		mEnemyLSTTBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyLSTTBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_lasersword_twotread_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyLSTTBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyLSTTBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyLSTTBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyLSTTBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_TA_FL:
			    	if (mEnemyTAFLTopDrawableDroidObject == null) {
			    		mEnemyTAFLTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyTAFLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyTAFLTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyTAFLTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyTAFLTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyTAFLTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyTAFLBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyTAFLBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_TA_FL_BOSS:
			    	if (mEnemyTAFLBossTopDrawableDroidObject == null) {
			    		mEnemyTAFLBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyTAFLBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_threearm_flying_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyTAFLBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyTAFLBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyTAFLBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyTAFLBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			    	
		    		break;
		    		
		    	case ENEMY_DR_TT_BOSS:
			    	if (mEnemyDRTTBossTopDrawableDroidObject == null) {
			    		mEnemyDRTTBossTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mEnemyDRTTBossTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_droid_twotread_boss_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_droid_twotread_boss_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
			    		}
			          
//			    		mEnemyDRTTBossTopDrawableDroidObject = new DrawableDroid();
//			    		mEnemyDRTTBossTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mEnemyDRTTBossTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnEnemy() COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mEnemyDRTTBossTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnEnemy() object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			        enemyTop.setBottomGameObject(mEnemyBossBottomGameObject);
			        
			        objectTop.currentPosition.set(mEnemyBossBottomGameObject.currentPosition);
			        
			        enemyTop.setObjectTypeToSpawn(Type.ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY);
		    		
			        enemyTop.setEnemyLaserOffset(mEnemyBossLaserSmallFlyingEnemyOffset);
		    		
		    		FixedSizeArray<GameObject> laserStdTTBossGameObjectArray = new FixedSizeArray<GameObject>(MAX_LASER_OBJECTS);
		    		
		    		for (int i = 0; i < MAX_LASER_OBJECTS; i++) {
		    			GameObject laserObject = mGameObjectPool.allocate();
				    	
				    	if (laserObject != null) {
				    		laserObject.group = Group.ENEMY_LASER;
				    		laserObject.type = Type.ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY;
					      
				    		laserObject.activationRadius = ACTIVATION_RADIUS_NORMAL;
					      
				    		laserObject.gameObjectId = mGameObjectIdCount;
					    	
					    	if (mEnemyBossLaserDrawableDroidObject == null) {
					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//					    		laserObject.objectInstantiation();
		    			
					    		if (GameParameters.supportsVBOs && gl11 != null) {
					    			mEnemyBossLaserDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_laser_sfe_dr_tt_boss_vbo, context);
//					    			laserObject.drawableDroid.loadObjectVBO(gl11, R.raw.enemy_laser_sfe_dr_tt_boss_vbo, context);
					    		} else {
					    			Log.e("VBO", "GameObjectFactory spawnEnemy() enemy_laser_std_tt gl11 is null!");
					    		}
					    		
//					    		laserObject.drawableDroid.setLaserGroup(true);
//					    		
//					    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
//					    		
//					    		mEnemyBossLaserDrawableDroidObject = new DrawableDroid();
//					    		mEnemyBossLaserDrawableDroidObject = laserObject.drawableDroid;
//					    		
//					    	} else {
//					    		laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;	
//					    		
					    	}
					    	
					    	laserObject.drawableDroid = mEnemyBossLaserDrawableDroidObject;
					    	
				    		laserObject.drawableDroid.setLaserGroup(true);
				    		
				    		laserObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mLight0PositionBuffer);
				    		laserObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mMatShininessLowBuffer, mZeroBuffer);
					    	
				    		RenderComponent laserRender = (RenderComponent)allocateComponent(RenderComponent.class);
				    		laserRender.priority = SortConstants.PROJECTILE;
				      
				    		EnemyLaserComponent laser = (EnemyLaserComponent)allocateComponent(EnemyLaserComponent.class);
				    		
					    	laserObject.magnitude = 0.15f;
				    		
					    	// GameObject Collision Radius.  Always Enabled.
				    		DynamicCollisionComponent laserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
					          
					    	OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.15f, 0.15f);
				          
				    		laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
				          
				    		// Area Collision Radius. Enabled in LaserComponent.
				    		DynamicCollisionComponent areaLaserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				    		
				    		OBBCollisionVolume areaLaserCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);
				          
				    		areaLaserDynamicCollision.setCollisionVolume(areaLaserCollisionVolume);
				    		
				    		laser.setAreaLaserDynamicCollisionComponent(areaLaserDynamicCollision);
				          
				    		// Hit Reaction
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				    		
				    		SoundSystem sound = sSystemRegistry.soundSystem;
				    		if (sound != null) {
				    			laser.setFireSound(sound.load(R.raw.sound_laser_rocket));
				    		}
				        
				    		laserDynamicCollision.setHitReactionComponent(laserHitReact);
				    		areaLaserDynamicCollision.setHitReactionComponent(laserHitReact);
				    		
//				    		DynamicCollisionComponent laserDynamicCollision = 
//					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
//					          
//					    	OBBCollisionVolume laserCollisionVolume = new OBBCollisionVolume(0.15f, 0.15f);
//					          
//					    	laserDynamicCollision.setCollisionVolume(laserCollisionVolume);
//					          
//					    	HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
//					    		
//					    	SoundSystem sound = sSystemRegistry.soundSystem;
//					    	if (sound != null) {
//					    		laser.setFireSound(sound.load(R.raw.sound_laser_emp));
//					    	}
//					        
//					    	laserDynamicCollision.setHitReactionComponent(laserHitReact);
				      
				    		laserObject.add(laser);
				    		laserObject.add(laserRender);
				    		laserObject.add(laserHitReact);
				    		laserObject.add(laserDynamicCollision);
				    		
				    		laserStdTTBossGameObjectArray.add(laserObject);
				    		
							if (GameParameters.debug) {
					    		Log.i("Object", "GameObjectFactory spawnEnemy() enemy_laser_std object ID and type = " + 
					    				" [" + mGameObjectIdCount + "] " + " (" + laserObject.type + ")");	
							}
				          
				    		mGameObjectIdCount++;
				    	} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
		    		}
			    	
		    		manager.setLaserGameObjectArray(Type.ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY, MAX_LASER_OBJECTS, laserStdTTBossGameObjectArray);
			        
		    		break;
		        }
		        
		        renderTop.priority = SortConstants.GENERAL_ENEMY;
		        
	    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		        
		        objectTop.add(enemyTop);
		        objectTop.add(renderTop);
		        
		        manager.add(objectTop);
		        
		        mGameObjectIdCount++;
		           
	        } else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
    
    public void spawnAstronaut(GL11 gl11, Type type, float x, float y, float z, float r) {	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
    		// spawn AstronautTop
	    	GameObject objectTop = mGameObjectPool.allocate();
	    	
	    	if (objectTop != null) {
		    	objectTop.group = Group.ASTRONAUT;
		    	objectTop.type = type;
		    	
		    	if (GameParameters.levelRow == 0) {
		    		objectTop.currentState = CurrentState.INTRO;
		    		objectTop.previousState = CurrentState.INTRO;
		    	} else {
		    		objectTop.currentState = CurrentState.LEVEL_START;
		    		objectTop.previousState = CurrentState.LEVEL_START;	
		    	}
//		    	objectTop.currentState = manager.droidBottomGameObject.currentState;
//		    	objectTop.previousState = manager.droidBottomGameObject.previousState;
		    	
//		    	if (GameParameters.levelRow == 0) {
//		    		objectTop.currentState = CurrentState.INTRO;
//		    	} else {
//		    		objectTop.currentState = CurrentState.LEVEL_START;
//		    	}
		      
		    	objectTop.currentPosition.set(x, y, z, r);
		    	
		      	objectTop.hitPoints = 3;
//		      	objectTop.hitPoints = 1;
		      
		    	objectTop.activationRadius = ACTIVATION_RADIUS_NORMAL;
		    	
		    	objectTop.attackRadius = ATTACK_RADIUS;
		      
		    	objectTop.gameObjectId = mGameObjectIdCount;
		    	
		    	AstronautTopComponent astronautTop = (AstronautTopComponent)allocateComponent(AstronautTopComponent.class);
		    	
		    	switch(type) {
		    	case ASTRONAUT_PRIVATE:
			    	if (mAstronautPrivateTopDrawableDroidObject == null) {
			    		mAstronautPrivateTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mAstronautPrivateTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_private_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_private_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnAstronaut() type gl11 is null! " + "[" + type + "]");
			    		}
			    		
////			    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mAstronautPrivateTopDrawableDroidObject = new DrawableDroid();
//			    		mAstronautPrivateTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Private Top NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mAstronautPrivateTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Private Top COPY object ID and type = " + 
//			    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mAstronautPrivateTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Private Top object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectTop.magnitude = 0.025f;
			    	
		    		break;
		    		
		    	case ASTRONAUT_SERGEANT:
			    	if (mAstronautSergeantTopDrawableDroidObject == null) {
			    		mAstronautSergeantTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mAstronautSergeantTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_sergeant_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_sergeant_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnAstronaut() type gl11 is null! " + "[" + type + "]");
			    		}
			    		
////			    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mAstronautSergeantTopDrawableDroidObject = new DrawableDroid();
//			    		mAstronautSergeantTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Sergeant Top NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mAstronautSergeantTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Sergeant Top COPY object ID and type = " + 
//			    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mAstronautSergeantTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Sergeant Top object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectTop.magnitude = 0.025f;
			    	
		    		break;
		    		
		    	case ASTRONAUT_CAPTAIN:
			    	if (mAstronautCaptainTopDrawableDroidObject == null) {
			    		mAstronautCaptainTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mAstronautCaptainTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_captain_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_captain_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnAstronaut() type gl11 is null! " + "[" + type + "]");
			    		}
			    		
////			    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mAstronautCaptainTopDrawableDroidObject = new DrawableDroid();
//			    		mAstronautCaptainTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Captain Top NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mAstronautCaptainTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Captain Top COPY object ID and type = " + 
//			    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mAstronautCaptainTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnAstronaut() Captain Top object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	objectTop.magnitude = 0.025f;
			    	
		    		break;
		    		
		    	case ASTRONAUT_GENERAL:
			    	if (mAstronautGeneralTopDrawableDroidObject == null) {
			    		mAstronautGeneralTopDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectTop.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mAstronautGeneralTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_general_top_vbo, context);
//			    			objectTop.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_general_top_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnAstronaut() type gl11 is null! " + "[" + type + "]");
			    		}
			    		
////			    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mAstronautGeneralTopDrawableDroidObject = new DrawableDroid();
//			    		mAstronautGeneralTopDrawableDroidObject = objectTop.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() General Top NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectTop.drawableDroid = mAstronautGeneralTopDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnAstronaut() General Top COPY object ID and type = " + 
//			    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectTop.drawableDroid = mAstronautGeneralTopDrawableDroidObject;
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnAstronaut() General Top object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
					}
			    	
			    	objectTop.magnitude = 0.025f;
			    	
		    		break;
		    	}
		    	
	    		objectTop.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectTop.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		    	
		    	mGameObjectIdCount++;
		      	
	    		SoundSystem sound = sSystemRegistry.soundSystem;
	    		if (sound != null) {
	    			astronautTop.setBounceSound(sound.load(R.raw.sound_gameobject_bounce));
	    			astronautTop.setCollectSound(sound.load(R.raw.sound_astronaut_collect));
	    			astronautTop.setDeathSound(sound.load(R.raw.sound_astronaut_death));
	    		}
		      
		    	RenderComponent astronautRenderTop = (RenderComponent)allocateComponent(RenderComponent.class);
		    	astronautRenderTop.priority = SortConstants.NPC;
		      
		    	DynamicCollisionComponent astronautDynamicCollision = 
		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
		      
		    	// FIXME TEMP dimensions based on Enemy. Re-calculate width, depth for Astronaut type
		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.579f, 0.579f);
//		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.579f, 0.579f, HitType.BOUNCE);
		      
		    	astronautDynamicCollision.setCollisionVolume(collisionVolume);
		    
		    	HitReactionComponent astronautHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//		    	astronautHitReact.setSpawnOnDealHit(HitType.HIT, Type.DROID_LASER_STD, false, true);
		    
		    	astronautDynamicCollision.setHitReactionComponent(astronautHitReact);
		    	
	    		// spawn Astronaut Appendages. 4 Total Animation Frames and 8 Increments Per Frame.
				AnimationSet animationSet = new AnimationSet(4, 8);
				animationSet.type = type;
				
				if (mAstronautAnimationArray == null) {
					mAstronautAnimationArray = new FixedSizeArray<GameObject>(4);
					
					for (int j = 0; j < 4; j++) {
						GameObject gameObject = mGameObjectPool.allocate();
						
						if (gameObject != null) {
							gameObject.group = Group.ASTRONAUT;
							gameObject.type = type;
							gameObject.activationRadius = objectTop.activationRadius;
							gameObject.gameObjectId = mGameObjectIdCount;
							gameObject.hitPoints = 3;
//							gameObject.hitPoints = 1;
							
//							gameObject.objectInstantiation();
							
							switch(j) {
							case 0:
								if(mAstronautBottomFrame1DrawableDroidObject == null) {
									mAstronautBottomFrame1DrawableDroidObject = new DrawableDroid();
									
						    		if (GameParameters.supportsVBOs && gl11 != null) {
						    			mAstronautBottomFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnAstronaut() Frame1 gl11 is null!");
						    		}
								}
								
								gameObject.drawableDroid = mAstronautBottomFrame1DrawableDroidObject;
					    		
//					    		gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
					    		
								break;
								
							case 1:
								if(mAstronautBottomFrame2DrawableDroidObject == null) {
									mAstronautBottomFrame2DrawableDroidObject = new DrawableDroid();
									
									if (GameParameters.supportsVBOs && gl11 != null) {
										mAstronautBottomFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnAstronaut() Frame2 gl11 is null!");
						    		}
								}
								
								gameObject.drawableDroid = mAstronautBottomFrame2DrawableDroidObject;
								
//					    		gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
					    		
								break;
								
							case 2:	  
								if(mAstronautBottomFrame3DrawableDroidObject == null) {
									mAstronautBottomFrame3DrawableDroidObject = new DrawableDroid();
									
									if (GameParameters.supportsVBOs && gl11 != null) {
										mAstronautBottomFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnAstronaut() Frame3 gl11 is null!");
						    		}
								}
								
								gameObject.drawableDroid = mAstronautBottomFrame3DrawableDroidObject;
								
//					    		gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
					    		
								break;
								
							case 3:
								if(mAstronautBottomFrame4DrawableDroidObject == null) {
									mAstronautBottomFrame4DrawableDroidObject = new DrawableDroid();
									
									if (GameParameters.supportsVBOs && gl11 != null) {
										mAstronautBottomFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.astronaut_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.astronaut_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnAstronaut() Frame4 gl11 is null!");
						    		}
								}
								
								gameObject.drawableDroid = mAstronautBottomFrame4DrawableDroidObject;
								
//					    		gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mLight0PositionBuffer);
//					    		gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//						    			mMatShininessLowBuffer, mZeroBuffer);
					    		
								break;
							}
							
				    		gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mLight0PositionBuffer);
				    		gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
					    			mMatShininessLowBuffer, mZeroBuffer);
							
							mAstronautAnimationArray.add(gameObject);
							
							if (GameParameters.debug) {
					    		Log.i("Object", "GameObjectFactory spawnAstronaut() Appendages object ID and type = " + 
				    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
							}
							
							mGameObjectIdCount++;
						} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
				}
				
				animationSet.addAnimationArray(mAstronautAnimationArray);
				
				astronautTop.setAnimationSet(animationSet);
		
		    	objectTop.add(astronautTop);
		    	objectTop.add(astronautRenderTop);
		    	objectTop.add(astronautHitReact);
		    	objectTop.add(astronautDynamicCollision);
		    	
		    	// Check for Special Final Level09 Astronaut and do not enable until Final View
		    	if (GameParameters.levelRow == 9) {
		    		if (r > 700.0f) {
		    			switch (mFinalLevel09AstronautCount) {
		    			case 1:
		    				mFinalLevel09Astronaut1 = objectTop;
		    				mFinalLevel09AstronautCount++;
		    				break;
		    			case 2:
		    				mFinalLevel09Astronaut2 = objectTop;
		    				mFinalLevel09AstronautCount++;
		    				break;
		    			case 3:
		    				mFinalLevel09Astronaut3 = objectTop;
		    				mFinalLevel09AstronautCount++;
		    				break;
		    			case 4:
		    				mFinalLevel09Astronaut4 = objectTop;
		    				mFinalLevel09AstronautCount++;
		    				break;
		    			case 5:
		    				mFinalLevel09Astronaut5 = objectTop;
		    				break;
		    			default:
		    				break;
		    			}
		    		} else {
		    			manager.add(objectTop);
		    		}
		    	} else {
		    		manager.add(objectTop);
		    	}
//		    	manager.add(objectTop);
		    	
	    	} else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
    
    public void spawnPlatformLevelStart(GL11 gl11, Type type, float x, float y, float z, float r) {
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
      		// spawn PlatformLevelStart
  	    	GameObject objectPlatform = mGameObjectPool.allocate();
  	    	
  	    	if (objectPlatform != null) {
  	    		objectPlatform.group = Group.PLATFORM_LEVEL_START;
  	    		objectPlatform.type = type;
  		    	
  	    		objectPlatform.currentPosition.set(x, y, z, r);
  		    	
  	    		objectPlatform.activationRadius = ACTIVATION_RADIUS_NORMAL;
//  	    		objectPlatform.activationRadius = mWideActivationRadius;
  	    		
		    	if (GameParameters.levelRow == 0) {
		    		objectPlatform.currentState = CurrentState.INTRO;
		    		objectPlatform.previousState = CurrentState.INTRO;
		    	} else {
	  	    		objectPlatform.currentState = CurrentState.LEVEL_START;
	  	    		objectPlatform.previousState = CurrentState.LEVEL_START;	
		    	}
//  	    		objectPlatform.currentState = manager.droidBottomGameObject.currentState;
//  	    		objectPlatform.previousState = manager.droidBottomGameObject.previousState;
  		      
  		    	objectPlatform.gameObjectId = mGameObjectIdCount;
  		    	
//		        BackgroundCollisionComponent backgroundCollision = 
//			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  		    	
  		    	DynamicCollisionComponent platformDynamicCollision = 
  	  		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
  		    	
  		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume();
  		    	
  		    	HitReactionComponent platformHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  		    	
  		    	PlatformComponent platform = (PlatformComponent)allocateComponent(PlatformComponent.class);
	    		SoundSystem sound = sSystemRegistry.soundSystem;
  		    	
		    	if (mPlatformLevelStartDrawableDroidObject == null) {
		    		mPlatformLevelStartDrawableDroidObject = new DrawableDroid();
//		    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		    		objectPlatform.objectInstantiation();
		          
		    		if (GameParameters.supportsVBOs && gl11 != null) {
		    			mPlatformLevelStartDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_level_start_vbo, context);
//		    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_level_start_vbo, context);
		    		} else {
		    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
		    		}
		    		
//		    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mLight0PositionBuffer);
//		    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mMatShininessLowBuffer, mZeroBuffer);
//		          
//		    		mPlatformLevelStartDrawableDroidObject = new DrawableDroid();
//		    		mPlatformLevelStartDrawableDroidObject = objectPlatform.drawableDroid;
//		          
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformLevelStart NEW object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//		    	} else {
//		    		objectPlatform.drawableDroid = mPlatformLevelStartDrawableDroidObject;
//		        
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformLevelStart COPY object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
		    	}
		    	
		    	objectPlatform.drawableDroid = mPlatformLevelStartDrawableDroidObject;
		    	
	    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		    	
				if (GameParameters.debug) {
		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformLevelStart object ID and type = " + 
		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		    	
		    	collisionVolume.setCollisionVolume(2.750f, 2.750f);	// full size 3.750f, 3.750f; 0.5f in from each side
//		    	collisionVolume.setCollisionVolume(2.750f, 2.750f, HitType.BOUNCE);	// full size 3.750f, 3.750f; 0.5f in from each side
		    	
  		    	platformDynamicCollision.setCollisionVolume(collisionVolume);
  		    
  		    	platformDynamicCollision.setHitReactionComponent(platformHitReact);
		    	
	    		if (sound != null) {
	    			platform.setPlatformSound(sound.load(R.raw.sound_platform_move));
//	    			platform.setElevatorSound(sound.load(R.raw.sound_platform_move));
	    		}
  		    	
  		    	objectPlatform.hitPoints = 3;
  		      
  		    	RenderComponent platformRender = (RenderComponent)allocateComponent(RenderComponent.class);
  		    	platformRender.priority = SortConstants.FOREGROUND_OBJECT;
  		
  		    	objectPlatform.add(platformHitReact);
  		    	objectPlatform.add(platformDynamicCollision);
  		    	objectPlatform.add(platform);
  		    	objectPlatform.add(platformRender);
  		    	
  		    	manager.add(objectPlatform);
  		      
  		    	mGameObjectIdCount++;
  	        } else {
  	    		Log.e("GameObjectFactory", "GameObject = NULL");
  	    	}
      	} else {
      		Log.e("GameObjectFactory", "GameObjectManager = NULL");
      	}
    }
    
    public void spawnPlatformLevelEnd(GL11 gl11, Type type, float x, float y, float z, float r) {
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
      		// spawn PlatformLevelEnd
  	    	GameObject objectPlatform = mGameObjectPool.allocate();
  	    	
  	    	if (objectPlatform != null) {
  	    		objectPlatform.group = Group.PLATFORM_LEVEL_END;
  	    		objectPlatform.type = type;
  		    	
  	    		objectPlatform.currentPosition.set(x, y, z, r);
  		    	
  	    		objectPlatform.activationRadius = ACTIVATION_RADIUS_NORMAL;
//  	    		objectPlatform.activationRadius = mWideActivationRadius;
  	    		
	    		objectPlatform.currentState = CurrentState.MOVE;
	    		objectPlatform.previousState = CurrentState.MOVE;
//  	    		objectPlatform.currentState = manager.droidBottomGameObject.currentState;
//  	    		objectPlatform.previousState = manager.droidBottomGameObject.previousState;
  		      
  		    	objectPlatform.gameObjectId = mGameObjectIdCount;
  		    	
//		        BackgroundCollisionComponent backgroundCollision = 
//			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  		    	
  		    	DynamicCollisionComponent platformDynamicCollision = 
  	  		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
  		    	
  		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume();
  		    	
  		    	HitReactionComponent platformHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  		    	
  		    	PlatformComponent platform = (PlatformComponent)allocateComponent(PlatformComponent.class);
	    		SoundSystem sound = sSystemRegistry.soundSystem;
	    		
		    	if (mPlatformLevelEndDrawableDroidObject == null) {
		    		mPlatformLevelEndDrawableDroidObject = new DrawableDroid();
//		    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		    		objectPlatform.objectInstantiation();
		          
		    		if (GameParameters.supportsVBOs && gl11 != null) {
		    			mPlatformLevelEndDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_level_end_vbo, context);
//		    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_level_end_vbo, context);
		    		} else {
		    			Log.e("VBO", "GameObjectFactory spawnEnemy() type gl11 is null! " + "[" + type + "]");
		    		}
		    		
//		    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mLight0PositionBuffer);
//		    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mMatShininessLowBuffer, mZeroBuffer);
//		          
//		    		mPlatformLevelEndDrawableDroidObject = new DrawableDroid();
//		    		mPlatformLevelEndDrawableDroidObject = objectPlatform.drawableDroid;
//		          
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformLevelEnd NEW object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//		    	} else {
//		    		objectPlatform.drawableDroid = mPlatformLevelEndDrawableDroidObject;
//		        
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformLevelEnd COPY object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
		    	}
		    	
		    	objectPlatform.drawableDroid = mPlatformLevelEndDrawableDroidObject;
		    	
	    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
	    		
				if (GameParameters.debug) {
		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformLevelEnd object ID and type = " + 
		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
  			    	
		    	collisionVolume.setCollisionVolume(2.750f, 2.750f);	// full size 3.750f, 3.750f; 0.5f in from each side
//		    	collisionVolume.setCollisionVolume(2.750f, 2.750f, HitType.BOUNCE);	// full size 3.750f, 3.750f; 0.5f in from each side
		    	
  		    	platformDynamicCollision.setCollisionVolume(collisionVolume);
  		    
  		    	platformDynamicCollision.setHitReactionComponent(platformHitReact);
		    	
	    		if (sound != null) {
	    			platform.setPlatformSound(sound.load(R.raw.sound_platform_teleport));
//	    			platform.setElevatorSound(sound.load(R.raw.sound_platform_teleport));
	    		}
	    		
  		    	objectPlatform.hitPoints = 3;
    		      
  		    	RenderComponent platformRender = (RenderComponent)allocateComponent(RenderComponent.class);
  		    	platformRender.priority = SortConstants.FOREGROUND_OBJECT;
	    		
  		    	objectPlatform.add(platformHitReact);
  		    	objectPlatform.add(platformDynamicCollision);
  		    	objectPlatform.add(platform);
  		    	objectPlatform.add(platformRender);
  		    	
  		    	manager.add(objectPlatform);
  		      
  		    	mGameObjectIdCount++;
  	        } else {
  	    		Log.e("GameObjectFactory", "GameObject = NULL");
  	    	}
      	} else {
      		Log.e("GameObjectFactory", "GameObjectManager = NULL");
      	}
    }
    
    public void spawnPlatformSectionStart(GL11 gl11, Type type, float x, float y, float z, float r) {
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
      		// spawn PlatformSectionStart
  	    	GameObject objectPlatform = mGameObjectPool.allocate();
  	    	
  	    	if (objectPlatform != null) {
  	    		objectPlatform.group = Group.PLATFORM_SECTION_START;
  	    		objectPlatform.type = type;
  		    	
  	    		objectPlatform.currentPosition.set(x, y, z, r);
  	    		
		    	/* levelXX_build must read PlatformSectionEnd to PlatformSectionStart in reverse order
		    	 * to provide PlatformSectionStart reference Position to next PlatformSectionEnd */
  	    		objectPlatform.nextSectionPosition.set(mNextSectionPositionTemp);
//		    	mNextSectionPositionTemp.set(x, y, z);
  	    		
  	    		// Provide PlatformSectionEnd reference Position to previous PlatformSectionStart
  	    		mPreviousSectionGameObjectTemp.previousSectionPosition.set(x, y, z);
//  	    		mPreviousSectionGameObjectTemp.previousSectionPosition.set(objectPlatform.currentPosition);
  		    	
  	    		objectPlatform.activationRadius = ACTIVATION_RADIUS_NORMAL;
//  	    		objectPlatform.activationRadius = mWideActivationRadius;
  	    		
	    		objectPlatform.currentState = CurrentState.MOVE;
	    		objectPlatform.previousState = CurrentState.MOVE;
//  	    		objectPlatform.currentState = manager.droidBottomGameObject.currentState;
//  	    		objectPlatform.previousState = manager.droidBottomGameObject.previousState;
  		      
  		    	objectPlatform.gameObjectId = mGameObjectIdCount;
  		    	
  		    	objectPlatform.backgroundGroup = true;
  		    	objectPlatform.backgroundRadius.set(x - 2.5f, z - 2.5f, x + 2.5f, z + 2.5f);
  		    	
		        // FIXME Not necessary for PlatformSection to include objectPlatform pointer (note: needed for Elevator)
		        BackgroundCollisionComponent backgroundCollisionTrigger = 
			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
	        	FixedSizeArray<LineSegmentCollisionVolume> collisionVolumesTrigger = 
	        			loadPlatformSectionCollisionVolumes(R.raw.platform_0deg_collision00, objectPlatform, true);
		        backgroundCollisionTrigger.setCollisionVolumes(collisionVolumesTrigger);
		        
	  		    HitReactionComponent hitReactTrigger = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
	  		    backgroundCollisionTrigger.setHitReactionComponent(hitReactTrigger);
	        	
//		        BackgroundCollisionComponent backgroundCollisionExit = 
//			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
//	        	FixedSizeArray<LineSegmentCollisionVolume> collisionVolumesExit = 
//	        			loadPlatformSectionCollisionVolumes(R.raw.platform_0deg_collision01, objectPlatform, true);
//		        backgroundCollisionExit.setCollisionVolumes(collisionVolumesExit);
//		        
//	  		    HitReactionComponent hitReactExit = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//	  		    backgroundCollisionExit.setHitReactionComponent(hitReactExit);
  		    	
//  		    	DynamicCollisionComponent platformDynamicCollision = 
//  	  		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
//  		    	
//  		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume();
//  		    	
//  		    	HitReactionComponent platformHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  		    	
  		    	PlatformComponent platform = (PlatformComponent)allocateComponent(PlatformComponent.class);
	    		SoundSystem sound = sSystemRegistry.soundSystem;
	    		
		    	if (mPlatformSectionStartDrawableDroidObject == null) {
		    		mPlatformSectionStartDrawableDroidObject = new DrawableDroid();
//		    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		    		objectPlatform.objectInstantiation();
		          
		    		if (GameParameters.supportsVBOs && gl11 != null) {
		    			mPlatformSectionStartDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_section_start_vbo, context);
//		    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_section_start_vbo, context);
		    		} else {
		    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformSectionStart gl11 is null!");
		    		}
		    		
//		    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mLight0PositionBuffer);
//		    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mMatShininessLowBuffer, mZeroBuffer);
//		          
//		    		mPlatformSectionStartDrawableDroidObject = new DrawableDroid();
//		    		mPlatformSectionStartDrawableDroidObject = objectPlatform.drawableDroid;
//		          
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformSectionStart NEW object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//		    	} else {
//		    		objectPlatform.drawableDroid = mPlatformSectionStartDrawableDroidObject;
//		        
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformSectionStart COPY object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
		    	}
		    	
		    	objectPlatform.drawableDroid = mPlatformSectionStartDrawableDroidObject;
		    	
	    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
	    		
				if (GameParameters.debug) {
		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformSectionStart object ID and type = " + 
		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		    	
//		    	collisionVolume.setCollisionVolume(2.750f, 2.750f);	// full size 3.750f, 3.750f; 0.5f in from each side
//		    	
//  		    	platformDynamicCollision.setCollisionVolume(collisionVolume);
//  		    
//  		    	platformDynamicCollision.setHitReactionComponent(platformHitReact);
		    	
	    		if (sound != null) {
	    			platform.setPlatformSound(sound.load(R.raw.sound_platform_teleport));
	    		}
  		    	
  		    	objectPlatform.hitPoints = 3;
  		      
  		    	RenderComponent platformRender = (RenderComponent)allocateComponent(RenderComponent.class);
  		    	platformRender.priority = SortConstants.FOREGROUND_OBJECT;
  		
  		    	objectPlatform.add(backgroundCollisionTrigger);
  		    	objectPlatform.add(hitReactTrigger);
//  		    	objectPlatform.add(backgroundCollisionExit);
//  		    	objectPlatform.add(hitReactExit);
//  		    	objectPlatform.add(platformHitReact);
//  		    	objectPlatform.add(platformDynamicCollision);
  		    	objectPlatform.add(platform);
  		    	objectPlatform.add(platformRender);
  		    	
  		    	manager.add(objectPlatform);
  		      
  		    	mGameObjectIdCount++;
  		    	
  	        } else {
  	    		Log.e("GameObjectFactory", "GameObject = NULL");
  	    	}
      	} else {
      		Log.e("GameObjectFactory", "GameObjectManager = NULL");
      	}
    }
    
    public void spawnPlatformSectionEnd(GL11 gl11, Type type, float x, float y, float z, float r) {
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
      		// spawn PlatformSectionEnd
  	    	GameObject objectPlatform = mGameObjectPool.allocate();
  	    	
  	    	if (objectPlatform != null) {
  	    		objectPlatform.group = Group.PLATFORM_SECTION_END;
  	    		objectPlatform.type = type;
  		    	
  	    		objectPlatform.currentPosition.set(x, y, z, r);
  	    		
		    	/* levelXX_build must read PlatformSectionEnd to PlatformSectionStart in reverse order
		    	 * to provide PlatformSectionStart reference Position to next PlatformSectionEnd */
		    	mNextSectionPositionTemp.set(x, y, z);
//  	    		objectPlatform.nextSectionPosition.set(mNextSectionPositionTemp);
		    	
		    	mPreviousSectionGameObjectTemp = objectPlatform;
  		    	
  	    		objectPlatform.activationRadius = ACTIVATION_RADIUS_NORMAL;
//  	    		objectPlatform.activationRadius = mWideActivationRadius;
  	    		
	    		objectPlatform.currentState = CurrentState.MOVE;
	    		objectPlatform.previousState = CurrentState.MOVE;
//  	    		objectPlatform.currentState = manager.droidBottomGameObject.currentState;
//  	    		objectPlatform.previousState = manager.droidBottomGameObject.previousState;
  		      
  		    	objectPlatform.gameObjectId = mGameObjectIdCount;

		        // FIXME Not necessary for PlatformSection to include objectPlatform pointer (note: needed for Elevator)
		        BackgroundCollisionComponent backgroundCollisionTrigger = 
			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
	        	FixedSizeArray<LineSegmentCollisionVolume> collisionVolumesTrigger = 
	        			loadPlatformSectionCollisionVolumes(R.raw.platform_0deg_collision00, objectPlatform, true);
		        backgroundCollisionTrigger.setCollisionVolumes(collisionVolumesTrigger);
		        
	  		    HitReactionComponent hitReactTrigger = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
	  		    backgroundCollisionTrigger.setHitReactionComponent(hitReactTrigger);
	        	
//		        BackgroundCollisionComponent backgroundCollisionExit = 
//			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
//	        	FixedSizeArray<LineSegmentCollisionVolume> collisionVolumesExit = 
//	        			loadPlatformSectionCollisionVolumes(R.raw.platform_0deg_collision01, objectPlatform, true);
//		        backgroundCollisionExit.setCollisionVolumes(collisionVolumesExit);
//		        
//	  		    HitReactionComponent hitReactExit = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//	  		    backgroundCollisionExit.setHitReactionComponent(hitReactExit);
  		    	
//  		    	DynamicCollisionComponent platformDynamicCollision = 
//  	  		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
//  		    	
//  		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume();
//  		    	
//  		    	HitReactionComponent platformHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  		    	
  		    	PlatformComponent platform = (PlatformComponent)allocateComponent(PlatformComponent.class);
	    		SoundSystem sound = sSystemRegistry.soundSystem;
	    		
  		      	if (mPlatformSectionEndDrawableDroidObject == null) {
  		      		mPlatformSectionEndDrawableDroidObject = new DrawableDroid();
//		    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//		    		objectPlatform.objectInstantiation();
		          
		    		if (GameParameters.supportsVBOs && gl11 != null) {
		    			mPlatformSectionEndDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_section_end_vbo, context);
//		    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_section_end_vbo, context);
		    		} else {
		    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformSectionEnd mGL11 is null!");
		    		}
		    		
//		    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mLight0PositionBuffer);
//		    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//			    			mMatShininessLowBuffer, mZeroBuffer);
//		          
//		    		mPlatformSectionEndDrawableDroidObject = new DrawableDroid();
//		    		mPlatformSectionEndDrawableDroidObject = objectPlatform.drawableDroid;
//		          
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformSectionEnd NEW object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//		    	} else {
//		    		objectPlatform.drawableDroid = mPlatformSectionEndDrawableDroidObject;
//		        
//		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformSectionEnd COPY object ID and type = " + 
//		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
		    	}
  		      	
  		      	objectPlatform.drawableDroid = mPlatformSectionEndDrawableDroidObject;
  		      	
	    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
	    		
				if (GameParameters.debug) {
		    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformSectionEnd object ID and type = " + 
		    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
				}
		    	
//		    	collisionVolume.setCollisionVolume(2.750f, 2.750f);	// full size 3.750f, 3.750f; 0.5f in from each side
//		    	
//  		    	platformDynamicCollision.setCollisionVolume(collisionVolume);
//  		    
//  		    	platformDynamicCollision.setHitReactionComponent(platformHitReact);
		    	
	    		if (sound != null) {
	    			platform.setPlatformSound(sound.load(R.raw.sound_platform_teleport));
//	    			platform.setElevatorSound(sound.load(R.raw.sound_platform_teleport));
	    		}
  		    	
  		    	objectPlatform.hitPoints = 3;
  		      
  		    	RenderComponent platformRender = (RenderComponent)allocateComponent(RenderComponent.class);
  		    	platformRender.priority = SortConstants.FOREGROUND_OBJECT;
  		
  		    	objectPlatform.add(backgroundCollisionTrigger);
  		    	objectPlatform.add(hitReactTrigger);
//  		    	objectPlatform.add(backgroundCollisionExit);
//  		    	objectPlatform.add(hitReactExit);
//  		    	objectPlatform.add(platformHitReact);
//  		    	objectPlatform.add(platformDynamicCollision);
  		    	objectPlatform.add(platform);
  		    	objectPlatform.add(platformRender);
  		    	
  		    	manager.add(objectPlatform);
  		      
  		    	mGameObjectIdCount++;
  		    	
  	        } else {
  	    		Log.e("GameObjectFactory", "GameObject = NULL");
  	    	}
      	} else {
      		Log.e("GameObjectFactory", "GameObjectManager = NULL");
      	}
    }
    
    public void spawnPlatformElevator(GL11 gl11, Type type, float x, float y, float z, float r) {
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
      		// spawn Platform
  	    	GameObject objectPlatform = mGameObjectPool.allocate();
  	    	
  	    	if (objectPlatform != null) {
  	    		objectPlatform.group = Group.PLATFORM_ELEVATOR;
  	    		objectPlatform.type = type;
  		    	
  	    		objectPlatform.currentPosition.set(x, y, z, r);
  		    	
  	    		objectPlatform.activationRadius = ACTIVATION_RADIUS_NORMAL;
//  	    		objectPlatform.activationRadius = mWideActivationRadius;
  	    		
	    		objectPlatform.currentState = CurrentState.MOVE;
	    		objectPlatform.previousState = CurrentState.MOVE;
  		      
  		    	objectPlatform.gameObjectId = mGameObjectIdCount;
  		    	
//  		    	DynamicCollisionComponent platformDynamicCollision = 
//  	  		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
  		    	
//  		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume();
  		    	
  		    	HitReactionComponent platformHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  		    	
  		    	PlatformComponent platform = (PlatformComponent)allocateComponent(PlatformComponent.class);
	    		SoundSystem sound = sSystemRegistry.soundSystem;
		        
		        switch(GameParameters.levelRow) {
		        case 1:
		        	switch(type) {
		        	case SECTION_04:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        case 3:
		        	switch(type) {
		        	case SECTION_01:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_04:
				    	if (mPlatformElevator45DegDrawableDroidObject == null) {
				    		mPlatformElevator45DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator45DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_45deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_45deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator45DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator45DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator45DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator45DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.000f, 3.000f);	// 45deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_05:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_06:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_07:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        case 5:
		        	switch(type) {
		        	case SECTION_04:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_06:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_08:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        case 6:
		        	switch(type) {
		        	case SECTION_02:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_04:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_06:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        case 7:
		        	switch(type) {
		        	case SECTION_02:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_07:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        case 8:
		        	switch(type) {
		        	case SECTION_03:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_05:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        		
		        	case SECTION_06:
				    	if (mPlatformElevator45DegDrawableDroidObject == null) {
				    		mPlatformElevator45DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator45DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_45deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_45deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator45DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator45DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator45DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator45DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        case 9:
		        	switch(type) {
		        	case SECTION_03:
				    	if (mPlatformElevator0DegDrawableDroidObject == null) {
				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//				    		objectPlatform.objectInstantiation();
				          
				    		if (GameParameters.supportsVBOs && gl11 != null) {
				    			mPlatformElevator0DegDrawableDroidObject.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
//				    			objectPlatform.drawableDroid.loadObjectVBO(gl11, R.raw.platform_elevator_0deg_vbo, context);
				    		} else {
				    			Log.e("VBO", "GameObjectFactory spawnPlatform() PlatformElevator mGL11 is null!");
				    		}
				          
//				    		mPlatformElevator0DegDrawableDroidObject = new DrawableDroid();
//				    		mPlatformElevator0DegDrawableDroidObject = objectPlatform.drawableDroid;
//				          
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator NEW object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//				    	} else {
//				    		objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
//				        
//				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator COPY object ID and type = " + 
//				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
				    	}
				    	
				    	objectPlatform.drawableDroid = mPlatformElevator0DegDrawableDroidObject;
				    	
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatform() PlatformElevator object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
				    	
//				    	collisionVolume.setCollisionVolume(3.500f, 3.500f);	// 0deg version; full size 5.000f, 5.000f
		        		break;
		        	}
		        	
		        	break;
		        	
		        default:
		        	break;
		        }
		        
	    		objectPlatform.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectPlatform.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);
		    	
	    		if (sound != null) {
	    			platform.setPlatformSound(sound.load(R.raw.sound_platform_move));
//	    			platform.setElevatorSound(sound.load(R.raw.sound_platform_move));
	    		}
  		    	
  		    	objectPlatform.hitPoints = 3;
  		      
  		    	RenderComponent platformRender = (RenderComponent)allocateComponent(RenderComponent.class);
  		    	platformRender.priority = SortConstants.FOREGROUND_OBJECT;
  		
  		    	objectPlatform.add(platformHitReact);
//  		    	objectPlatform.add(platformDynamicCollision);
  		    	objectPlatform.add(platform);
  		    	objectPlatform.add(platformRender);
  		    	
  		    	manager.add(objectPlatform);
  		      
  		    	mGameObjectIdCount++;
  		    	
  		    	// Initialize ElevatorCollisionVolumes. Enabled in enableElevatorCollision01(), 02(), or 03() via GameObjectCollisionSystem.
  		    	switch(GameParameters.levelRow) {  		    	
  		    	case 1:
  		    		switch(type) {
  		    		case SECTION_04:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(-22.0f, -19.0f, -16.0f, -19.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
  	  	  		        
  	  	  		        mGameObjectIdCount++;
  		    			
  		    			// Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(-20.0f, -19.0f, -20.0f, -13.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(-20.0f, -19.0f, -20.0f, -13.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(-20.0f, -19.0f, -20.0f, -13.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	case 3:
  		    		switch(type) {
  		    		case SECTION_01:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(-60.0f, -3.0f, -60.0f, 3.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  		    			
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(-60.0f, 0.0f, -55.0f, 0.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(-60.0f, -0.0f, -55.0f, -0.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(-60.0f, -0.0f, -55.0f, -0.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_90deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_04:
  		    			// Use Elevator GameObject B. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00B.type = type;
  	  	  		        mElevatorCollisionGameObject00B.backgroundRadius.set(55.0f, 10.0f, 60.0f, 35.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00B = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00B.setCollisionVolumes(mElevatorCollisionVolumes00B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00B.setPauseOnAttack(false);
  	  	  		        elevatorCollision00B.setHitReactionComponent(hitReact00B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00B.add(elevatorCollision00B);
  	  	  		        mElevatorCollisionGameObject00B.add(hitReact00B);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  		    			
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01B.type = type;
  	  	  		        mElevatorCollisionGameObject01B.backgroundRadius.set(55.0f, 10.0f, 60.0f, 35.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01B = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01B.setCollisionVolumes(mElevatorCollisionVolumes01B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01B.setPauseOnAttack(false);
  	  	  		        elevatorCollision01B.setHitReactionComponent(hitReact01B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01B.add(elevatorCollision01B);
  	  	  		        mElevatorCollisionGameObject01B.add(hitReact01B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02B.type = type;
  	  			        mElevatorCollisionGameObject02B.backgroundRadius.set(55.0f, 10.0f, 60.0f, 35.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02B = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision02or03_135deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02B.setCollisionVolumes(mElevatorCollisionVolumes02B);
  	  			        
  	  			        HitReactionComponent hitReact02B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02B.setPauseOnAttack(false);
  	  			        elevatorCollision02B.setHitReactionComponent(hitReact02B);
  	  			        
  	  			        mElevatorCollisionGameObject02B.add(elevatorCollision02B);
  	  			        mElevatorCollisionGameObject02B.add(hitReact02B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03B.type = type;
  	  			        mElevatorCollisionGameObject03B.backgroundRadius.set(55.0f, 10.0f, 60.0f, 35.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03B = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision02or03_45deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03B.setCollisionVolumes(mElevatorCollisionVolumes03B);
  	  			        
  	  			        HitReactionComponent hitReact03B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03B.setPauseOnAttack(false);
  	  			        elevatorCollision03B.setHitReactionComponent(hitReact03B);
  	  			        
  	  			        mElevatorCollisionGameObject03B.add(elevatorCollision03B);
  	  			        mElevatorCollisionGameObject03B.add(hitReact03B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_05:
  		    			// Use Elevator GameObject C. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00C.type = type;
  	  	  		        mElevatorCollisionGameObject00C.backgroundRadius.set(15.0f, 45.0f, 25.0f, 55.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00C.setCollisionVolumes(mElevatorCollisionVolumes00C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00C.setPauseOnAttack(false);
  	  	  		        elevatorCollision00C.setHitReactionComponent(hitReact00C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00C.add(elevatorCollision00C);
  	  	  		        mElevatorCollisionGameObject00C.add(hitReact00C);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  		    			
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01C.type = type;
  	  	  		        mElevatorCollisionGameObject01C.backgroundRadius.set(15.0f, 45.0f, 25.0f, 55.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01C.setCollisionVolumes(mElevatorCollisionVolumes01C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01C.setPauseOnAttack(false);
  	  	  		        elevatorCollision01C.setHitReactionComponent(hitReact01C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01C.add(elevatorCollision01C);
  	  	  		        mElevatorCollisionGameObject01C.add(hitReact01C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  			        mElevatorCollisionGameObject02C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02C.type = type;
  	  			        mElevatorCollisionGameObject02C.backgroundRadius.set(15.0f, 45.0f, 25.0f, 55.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_90deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02C.setCollisionVolumes(mElevatorCollisionVolumes02C);
  	  			        
  	  			        HitReactionComponent hitReact02C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02C.setPauseOnAttack(false);
  	  			        elevatorCollision02C.setHitReactionComponent(hitReact02C);
  	  			        
  	  			        mElevatorCollisionGameObject02C.add(elevatorCollision02C);
  	  			        mElevatorCollisionGameObject02C.add(hitReact02C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03C.type = type;
  	  			        mElevatorCollisionGameObject03C.backgroundRadius.set(15.0f, 45.0f, 25.0f, 55.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03C.setCollisionVolumes(mElevatorCollisionVolumes03C);
  	  			        
  	  			        HitReactionComponent hitReact03C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03C.setPauseOnAttack(false);
  	  			        elevatorCollision03C.setHitReactionComponent(hitReact03C);
  	  			        
  	  			        mElevatorCollisionGameObject03C.add(elevatorCollision03C);
  	  			        mElevatorCollisionGameObject03C.add(hitReact03C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_06:
  		    			// Use Elevator GameObject D. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00D = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00D.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00D.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00D.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00D.type = type;
  	  	  		        mElevatorCollisionGameObject00D.backgroundRadius.set(0.0f, 70.0f, 10.0f, 70.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00D = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00D = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00D.setCollisionVolumes(mElevatorCollisionVolumes00D);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00D = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00D.setPauseOnAttack(false);
  	  	  		        elevatorCollision00D.setHitReactionComponent(hitReact00D);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00D.add(elevatorCollision00D);
  	  	  		        mElevatorCollisionGameObject00D.add(hitReact00D);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00D);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  		    			
  	  	  		        // Use Elevator GameObject D. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01D = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01D.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01D.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01D.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01D.type = type;
  	  	  		        mElevatorCollisionGameObject01D.backgroundRadius.set(5.0f, 70.0f, 5.0f, 75.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01D = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01D = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01D.setCollisionVolumes(mElevatorCollisionVolumes01D);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01D = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01D.setPauseOnAttack(false);
  	  	  		        elevatorCollision01D.setHitReactionComponent(hitReact01D);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01D.add(elevatorCollision01D);
  	  	  		        mElevatorCollisionGameObject01D.add(hitReact01D);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject D. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02D = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02D.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02D.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02D.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02D.type = type;
  	  			        mElevatorCollisionGameObject02D.backgroundRadius.set(5.0f, 70.0f, 5.0f, 75.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02D = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02D = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02D.setCollisionVolumes(mElevatorCollisionVolumes02D);
  	  			        
  	  			        HitReactionComponent hitReact02D = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02D.setPauseOnAttack(false);
  	  			        elevatorCollision02D.setHitReactionComponent(hitReact02D);
  	  			        
  	  			        mElevatorCollisionGameObject02D.add(elevatorCollision02D);
  	  			        mElevatorCollisionGameObject02D.add(hitReact02D);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject D. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03D = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03D.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03D.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03D.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03D.type = type;
  	  			        mElevatorCollisionGameObject03D.backgroundRadius.set(5.0f, 70.0f, 5.0f, 75.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03D = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03D = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03D.setCollisionVolumes(mElevatorCollisionVolumes03D);
  	  			        
  	  			        HitReactionComponent hitReact03D = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03D.setPauseOnAttack(false);
  	  			        elevatorCollision03D.setHitReactionComponent(hitReact03D);
  	  			        
  	  			        mElevatorCollisionGameObject03D.add(elevatorCollision03D);
  	  			        mElevatorCollisionGameObject03D.add(hitReact03D);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_07:
  		    			// Use Elevator GameObject E. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00E = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00E.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00E.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00E.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00E.type = type;
  	  	  		        mElevatorCollisionGameObject00E.backgroundRadius.set(15.0f, 80.0f, 15.0f, 90.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00E = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00E = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00E.setCollisionVolumes(mElevatorCollisionVolumes00E);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00E = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00E.setPauseOnAttack(false);
  	  	  		        elevatorCollision00E.setHitReactionComponent(hitReact00E);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00E.add(elevatorCollision00E);
  	  	  		        mElevatorCollisionGameObject00E.add(hitReact00E);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00E);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  		    			
  	  	  		        // Use Elevator GameObject E. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01E = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01E.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01E.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01E.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01E.type = type;
  	  	  		        mElevatorCollisionGameObject01E.backgroundRadius.set(15.0f, 85.0f, 20.0f, 85.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01E = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01E = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01E.setCollisionVolumes(mElevatorCollisionVolumes01E);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01E = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01E.setPauseOnAttack(false);
  	  	  		        elevatorCollision01E.setHitReactionComponent(hitReact01E);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01E.add(elevatorCollision01E);
  	  	  		        mElevatorCollisionGameObject01E.add(hitReact01E);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject E. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02E = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02E.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02E.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02E.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02E.type = type;
  	  			        mElevatorCollisionGameObject02E.backgroundRadius.set(15.0f, 85.0f, 20.0f, 85.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02E = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02E = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02E.setCollisionVolumes(mElevatorCollisionVolumes02E);
  	  			        
  	  			        HitReactionComponent hitReact02E = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02E.setPauseOnAttack(false);
  	  			        elevatorCollision02E.setHitReactionComponent(hitReact02E);
  	  			        
  	  			        mElevatorCollisionGameObject02E.add(elevatorCollision02E);
  	  			        mElevatorCollisionGameObject02E.add(hitReact02E);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject E. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03E = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03E.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03E.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03E.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03E.type = type;
  	  			        mElevatorCollisionGameObject03E.backgroundRadius.set(15.0f, 85.0f, 20.0f, 85.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03E = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_90deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03E = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03E.setCollisionVolumes(mElevatorCollisionVolumes03E);
  	  			        
  	  			        HitReactionComponent hitReact03E = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03E.setPauseOnAttack(false);
  	  			        elevatorCollision03E.setHitReactionComponent(hitReact03E);
  	  			        
  	  			        mElevatorCollisionGameObject03E.add(elevatorCollision03E);
  	  			        mElevatorCollisionGameObject03E.add(hitReact03E);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;

  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	case 5:
  		    		switch(type) {
  		    		case SECTION_04:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(-3.0f, -15.0f, 3.0f, -15.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(-2.5f, -15.0f, 2.5f, -20.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(-2.5f, -15.0f, 2.5f, -20.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(-2.5f, -15.0f, 2.5f, -20.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_06:
  		    			// Use Elevator GameObject B. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00B.type = type;
  	  	  		        mElevatorCollisionGameObject00B.backgroundRadius.set(20.0f, -10.0f, 25.0f, 0.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00B.setCollisionVolumes(mElevatorCollisionVolumes00B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00B.setPauseOnAttack(false);
  	  	  		        elevatorCollision00B.setHitReactionComponent(hitReact00B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00B.add(elevatorCollision00B);
  	  	  		        mElevatorCollisionGameObject00B.add(hitReact00B);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01B.type = type;
  	  	  		        mElevatorCollisionGameObject01B.backgroundRadius.set(20.0f, -2.5f, 25.0f, -7.5f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01B.setCollisionVolumes(mElevatorCollisionVolumes01B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01B.setPauseOnAttack(false);
  	  	  		        elevatorCollision01B.setHitReactionComponent(hitReact01B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01B.add(elevatorCollision01B);
  	  	  		        mElevatorCollisionGameObject01B.add(hitReact01B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02B.type = type;
  	  			        mElevatorCollisionGameObject02B.backgroundRadius.set(20.0f, -2.5f, 25.0f, -7.5f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02B.setCollisionVolumes(mElevatorCollisionVolumes02B);
  	  			        
  	  			        HitReactionComponent hitReact02B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02B.setPauseOnAttack(false);
  	  			        elevatorCollision02B.setHitReactionComponent(hitReact02B);
  	  			        
  	  			        mElevatorCollisionGameObject02B.add(elevatorCollision02B);
  	  			        mElevatorCollisionGameObject02B.add(hitReact02B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03B.type = type;
  	  			        mElevatorCollisionGameObject03B.backgroundRadius.set(20.0f, -2.5f, 25.0f, -7.5f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03B.setCollisionVolumes(mElevatorCollisionVolumes03B);
  	  			        
  	  			        HitReactionComponent hitReact03B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03B.setPauseOnAttack(false);
  	  			        elevatorCollision03B.setHitReactionComponent(hitReact03B);
  	  			        
  	  			        mElevatorCollisionGameObject03B.add(elevatorCollision03B);
  	  			        mElevatorCollisionGameObject03B.add(hitReact03B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_08:
  		    			// Use Elevator GameObject C. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00C.type = type;
  	  	  		        mElevatorCollisionGameObject00C.backgroundRadius.set(100.0f, 20.0f, 120.0f, 25.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00C.setCollisionVolumes(mElevatorCollisionVolumes00C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00C.setPauseOnAttack(false);
  	  	  		        elevatorCollision00C.setHitReactionComponent(hitReact00C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00C.add(elevatorCollision00C);
  	  	  		        mElevatorCollisionGameObject00C.add(hitReact00C);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01C.type = type;
  	  	  		        mElevatorCollisionGameObject01C.backgroundRadius.set(100.0f, 20.0f, 120.0f, 25.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01C.setCollisionVolumes(mElevatorCollisionVolumes01C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01C.setPauseOnAttack(false);
  	  	  		        elevatorCollision01C.setHitReactionComponent(hitReact01C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01C.add(elevatorCollision01C);
  	  	  		        mElevatorCollisionGameObject01C.add(hitReact01C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  			        mElevatorCollisionGameObject02C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02C.type = type;
  	  			        mElevatorCollisionGameObject02C.backgroundRadius.set(100.0f, 20.0f, 120.0f, 25.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02C.setCollisionVolumes(mElevatorCollisionVolumes02C);
  	  			        
  	  			        HitReactionComponent hitReact02C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02C.setPauseOnAttack(false);
  	  			        elevatorCollision02C.setHitReactionComponent(hitReact02C);
  	  			        
  	  			        mElevatorCollisionGameObject02C.add(elevatorCollision02C);
  	  			        mElevatorCollisionGameObject02C.add(hitReact02C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03C.type = type;
  	  			        mElevatorCollisionGameObject03C.backgroundRadius.set(100.0f, 20.0f, 120.0f, 25.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_90deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03C.setCollisionVolumes(mElevatorCollisionVolumes03C);
  	  			        
  	  			        HitReactionComponent hitReact03C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03C.setPauseOnAttack(false);
  	  			        elevatorCollision03C.setHitReactionComponent(hitReact03C);
  	  			        
  	  			        mElevatorCollisionGameObject03C.add(elevatorCollision03C);
  	  			        mElevatorCollisionGameObject03C.add(hitReact03C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	case 6:
  		    		switch(type) {
  		    		case SECTION_02:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(70.0f, 45.0f, 80.0f, 40.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(70.0f, 45.0f, 80.0f, 40.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(70.0f, 45.0f, 80.0f, 40.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(70.0f, 45.0f, 80.0f, 40.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_90_180_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_04:
  		    			// Use Elevator GameObject B. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00B.type = type;
  	  	  		        mElevatorCollisionGameObject00B.backgroundRadius.set(10.0f, -35.0f, 0.0f, -40.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00B.setCollisionVolumes(mElevatorCollisionVolumes00B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00B.setPauseOnAttack(false);
  	  	  		        elevatorCollision00B.setHitReactionComponent(hitReact00B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00B.add(elevatorCollision00B);
  	  	  		        mElevatorCollisionGameObject00B.add(hitReact00B);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01B.type = type;
  	  	  		        mElevatorCollisionGameObject01B.backgroundRadius.set(-5.0f, -35.0f, 0.0f, -40.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01B.setCollisionVolumes(mElevatorCollisionVolumes01B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01B.setPauseOnAttack(false);
  	  	  		        elevatorCollision01B.setHitReactionComponent(hitReact01B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01B.add(elevatorCollision01B);
  	  	  		        mElevatorCollisionGameObject01B.add(hitReact01B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02B.type = type;
  	  			        mElevatorCollisionGameObject02B.backgroundRadius.set(-5.0f, -35.0f, 0.0f, -40.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02B.setCollisionVolumes(mElevatorCollisionVolumes02B);
  	  			        
  	  			        HitReactionComponent hitReact02B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02B.setPauseOnAttack(false);
  	  			        elevatorCollision02B.setHitReactionComponent(hitReact02B);
  	  			        
  	  			        mElevatorCollisionGameObject02B.add(elevatorCollision02B);
  	  			        mElevatorCollisionGameObject02B.add(hitReact02B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03B.type = type;
  	  			        mElevatorCollisionGameObject03B.backgroundRadius.set(-5.0f, -35.0f, 0.0f, -40.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0_90_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03B.setCollisionVolumes(mElevatorCollisionVolumes03B);
  	  			        
  	  			        HitReactionComponent hitReact03B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03B.setPauseOnAttack(false);
  	  			        elevatorCollision03B.setHitReactionComponent(hitReact03B);
  	  			        
  	  			        mElevatorCollisionGameObject03B.add(elevatorCollision03B);
  	  			        mElevatorCollisionGameObject03B.add(hitReact03B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_06:
  		    			// Use Elevator GameObject C. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00C.type = type;
  	  	  		        mElevatorCollisionGameObject00C.backgroundRadius.set(30.0f, -70.0f, 40.0f, -80.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00C.setCollisionVolumes(mElevatorCollisionVolumes00C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00C.setPauseOnAttack(false);
  	  	  		        elevatorCollision00C.setHitReactionComponent(hitReact00C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00C.add(elevatorCollision00C);
  	  	  		        mElevatorCollisionGameObject00C.add(hitReact00C);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01C.type = type;
  	  	  		        mElevatorCollisionGameObject01C.backgroundRadius.set(35.0f, -70.0f, 40.0f, -80.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01C.setCollisionVolumes(mElevatorCollisionVolumes01C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01C.setPauseOnAttack(false);
  	  	  		        elevatorCollision01C.setHitReactionComponent(hitReact01C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01C.add(elevatorCollision01C);
  	  	  		        mElevatorCollisionGameObject01C.add(hitReact01C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  			        mElevatorCollisionGameObject02C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02C.type = type;
  	  			        mElevatorCollisionGameObject02C.backgroundRadius.set(35.0f, -70.0f, 40.0f, -80.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02C.setCollisionVolumes(mElevatorCollisionVolumes02C);
  	  			        
  	  			        HitReactionComponent hitReact02C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02C.setPauseOnAttack(false);
  	  			        elevatorCollision02C.setHitReactionComponent(hitReact02C);
  	  			        
  	  			        mElevatorCollisionGameObject02C.add(elevatorCollision02C);
  	  			        mElevatorCollisionGameObject02C.add(hitReact02C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03C.type = type;
  	  			        mElevatorCollisionGameObject03C.backgroundRadius.set(35.0f, -70.0f, 40.0f, -80.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03C = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0_90_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03C.setCollisionVolumes(mElevatorCollisionVolumes03C);
  	  			        
  	  			        HitReactionComponent hitReact03C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03C.setPauseOnAttack(false);
  	  			        elevatorCollision03C.setHitReactionComponent(hitReact03C);
  	  			        
  	  			        mElevatorCollisionGameObject03C.add(elevatorCollision03C);
  	  			        mElevatorCollisionGameObject03C.add(hitReact03C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	case 7:
  		    		switch(type) {
  		    		case SECTION_02:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(-120.0f, 10.0f, -130.0f, 20.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(-120.0f, 10.0f, -130.0f, 20.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(-120.0f, 10.0f, -130.0f, 20.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(-120.0f, 10.0f, -130.0f, 20.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_07:
  		    			// Use Elevator GameObject B. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00B.type = type;
  	  	  		        mElevatorCollisionGameObject00B.backgroundRadius.set(-20.0f, -40.0f, -30.0f, -70.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00B.setCollisionVolumes(mElevatorCollisionVolumes00B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00B.setPauseOnAttack(false);
  	  	  		        elevatorCollision00B.setHitReactionComponent(hitReact00B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00B.add(elevatorCollision00B);
  	  	  		        mElevatorCollisionGameObject00B.add(hitReact00B);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01B.type = type;
  	  	  		        mElevatorCollisionGameObject01B.backgroundRadius.set(-20.0f, -40.0f, -30.0f, -70.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01B.setCollisionVolumes(mElevatorCollisionVolumes01B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01B.setPauseOnAttack(false);
  	  	  		        elevatorCollision01B.setHitReactionComponent(hitReact01B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01B.add(elevatorCollision01B);
  	  	  		        mElevatorCollisionGameObject01B.add(hitReact01B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02B.type = type;
  	  			        mElevatorCollisionGameObject02B.backgroundRadius.set(-20.0f, -40.0f, -30.0f, -70.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02B.setCollisionVolumes(mElevatorCollisionVolumes02B);
  	  			        
  	  			        HitReactionComponent hitReact02B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02B.setPauseOnAttack(false);
  	  			        elevatorCollision02B.setHitReactionComponent(hitReact02B);
  	  			        
  	  			        mElevatorCollisionGameObject02B.add(elevatorCollision02B);
  	  			        mElevatorCollisionGameObject02B.add(hitReact02B);
  	  			        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03B.type = type;
  	  			        mElevatorCollisionGameObject03B.backgroundRadius.set(-20.0f, -40.0f, -30.0f, -70.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03B.setCollisionVolumes(mElevatorCollisionVolumes03B);
  	  			        
  	  			        HitReactionComponent hitReact03B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03B.setPauseOnAttack(false);
  	  			        elevatorCollision03B.setHitReactionComponent(hitReact03B);
  	  			        
  	  			        mElevatorCollisionGameObject03B.add(elevatorCollision03B);
  	  			        mElevatorCollisionGameObject03B.add(hitReact03B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	case 8:
  		    		switch(type) {
  		    		case SECTION_03:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(-55.0f, 20.0f, -50.0f, 30.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(-55.0f, 20.0f, -50.0f, 30.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(-55.0f, 20.0f, -50.0f, 30.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(-55.0f, 20.0f, -50.0f, 30.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_05:
  		    			// Use Elevator GameObject B. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00B.type = type;
  	  	  		        mElevatorCollisionGameObject00B.backgroundRadius.set(-5.0f, 25.0f, 5.0f, 20.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00B.setCollisionVolumes(mElevatorCollisionVolumes00B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00B.setPauseOnAttack(false);
  	  	  		        elevatorCollision00B.setHitReactionComponent(hitReact00B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00B.add(elevatorCollision00B);
  	  	  		        mElevatorCollisionGameObject00B.add(hitReact00B);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01B = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01B.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01B.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01B.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01B.type = type;
  	  	  		        mElevatorCollisionGameObject01B.backgroundRadius.set(-5.0f, 25.0f, 5.0f, 20.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01B = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01B.setCollisionVolumes(mElevatorCollisionVolumes01B);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01B.setPauseOnAttack(false);
  	  	  		        elevatorCollision01B.setHitReactionComponent(hitReact01B);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01B.add(elevatorCollision01B);
  	  	  		        mElevatorCollisionGameObject01B.add(hitReact01B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02B.type = type;
  	  			        mElevatorCollisionGameObject02B.backgroundRadius.set(-5.0f, 25.0f, 5.0f, 20.0f);  // Wider backgroundRadius for zMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02B.setCollisionVolumes(mElevatorCollisionVolumes02B);
  	  			        
  	  			        HitReactionComponent hitReact02B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02B.setPauseOnAttack(false);
  	  			        elevatorCollision02B.setHitReactionComponent(hitReact02B);
  	  			        
  	  			        mElevatorCollisionGameObject02B.add(elevatorCollision02B);
  	  			        mElevatorCollisionGameObject02B.add(hitReact02B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject B. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03B = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03B.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03B.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03B.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03B.type = type;
  	  			        mElevatorCollisionGameObject03B.backgroundRadius.set(-5.0f, 25.0f, 5.0f, 20.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03B = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_180deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03B = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03B.setCollisionVolumes(mElevatorCollisionVolumes03B);
  	  			        
  	  			        HitReactionComponent hitReact03B = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03B.setPauseOnAttack(false);
  	  			        elevatorCollision03B.setHitReactionComponent(hitReact03B);
  	  			        
  	  			        mElevatorCollisionGameObject03B.add(elevatorCollision03B);
  	  			        mElevatorCollisionGameObject03B.add(hitReact03B);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		case SECTION_06:
  		    			// Use Elevator GameObject C. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00C.type = type;
  	  	  		        mElevatorCollisionGameObject00C.backgroundRadius.set(5.0f, -10.0f, 20.0f, -20.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes00C = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision00, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00C.setCollisionVolumes(mElevatorCollisionVolumes00C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00C.setPauseOnAttack(false);
  	  	  		        elevatorCollision00C.setHitReactionComponent(hitReact00C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00C.add(elevatorCollision00C);
  	  	  		        mElevatorCollisionGameObject00C.add(hitReact00C);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01C = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01C.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01C.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01C.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01C.type = type;
  	  	  		        mElevatorCollisionGameObject01C.backgroundRadius.set(5.0f, -10.0f, 20.0f, -20.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  			    	mElevatorCollisionVolumes01C = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision01, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01C = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01C.setCollisionVolumes(mElevatorCollisionVolumes01C);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01C.setPauseOnAttack(false);
  	  	  		        elevatorCollision01C.setHitReactionComponent(hitReact01C);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01C.add(elevatorCollision01C);
  	  	  		        mElevatorCollisionGameObject01C.add(hitReact01C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 01.
  	  			        mElevatorCollisionGameObject02C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02C.type = type;
  	  			        mElevatorCollisionGameObject02C.backgroundRadius.set(5.0f, -10.0f, 20.0f, -20.0f);  // Wider backgroundRadius for xMove
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02C = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision02or03_315deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02C.setCollisionVolumes(mElevatorCollisionVolumes02C);
  	  			        
  	  			        HitReactionComponent hitReact02C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02C.setPauseOnAttack(false);
  	  			        elevatorCollision02C.setHitReactionComponent(hitReact02C);
  	  			        
  	  			        mElevatorCollisionGameObject02C.add(elevatorCollision02C);
  	  			        mElevatorCollisionGameObject02C.add(hitReact02C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject C. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03C = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03C.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03C.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03C.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03C.type = type;
  	  			        mElevatorCollisionGameObject03C.backgroundRadius.set(5.0f, -10.0f, 20.0f, -20.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03C = loadElevatorCollisionVolumes(R.raw.elevator_45deg_collision02or03_135deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03C = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03C.setCollisionVolumes(mElevatorCollisionVolumes03C);
  	  			        
  	  			        HitReactionComponent hitReact03C = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03C.setPauseOnAttack(false);
  	  			        elevatorCollision03C.setHitReactionComponent(hitReact03C);
  	  			        
  	  			        mElevatorCollisionGameObject03C.add(elevatorCollision03C);
  	  			        mElevatorCollisionGameObject03C.add(hitReact03C);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	case 9:
  		    		switch(type) {
  		    		case SECTION_03:
  		    			// Use Elevator GameObject A. Load Elevator Collision 00.  Initial trigger point, doesn't move.
  	  	  		        mElevatorCollisionGameObject00A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject00A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject00A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject00A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject00A.type = type;
  	  	  		        mElevatorCollisionGameObject00A.backgroundRadius.set(115.0f, 5.0f, 140.0f, 10.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision00, objectPlatform, true);
//  	  			    mElevatorCollisionVolumes00A = loadElevatorCollisionVolumes(R.raw.level09_elevator03_collision00, objectPlatform, false);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision00A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision00A.setCollisionVolumes(mElevatorCollisionVolumes00A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact00A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
//  	  	  		        hitReact00A.setPauseOnAttack(false);
  	  	  		        elevatorCollision00A.setHitReactionComponent(hitReact00A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject00A.add(elevatorCollision00A);
  	  	  		        mElevatorCollisionGameObject00A.add(hitReact00A);
  	  	  		        
  	  	  		        manager.add(mElevatorCollisionGameObject00A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 01.
  	  	  		        mElevatorCollisionGameObject01A = mGameObjectPool.allocate();
  	  	  		        mElevatorCollisionGameObject01A.gameObjectId = mGameObjectIdCount;
  	  	  		        mElevatorCollisionGameObject01A.backgroundGroup = true; // Boolean used by GameObjectManager
  	  	  		        mElevatorCollisionGameObject01A.group = Group.PLATFORM_ELEVATOR;
  	  	  		        mElevatorCollisionGameObject01A.type = type;
  	  	  		        mElevatorCollisionGameObject01A.backgroundRadius.set(115.0f, 5.0f, 140.0f, 10.0f);
  	  	  		        
  	  			    	mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision01, objectPlatform, true);
//  	  			    mElevatorCollisionVolumes01A = loadElevatorCollisionVolumes(R.raw.elevator_collision01_270deg, objectPlatform, true);
  	  	  		        
  	  	  		        BackgroundCollisionComponent elevatorCollision01A = 
  	  	  			        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  	  		        elevatorCollision01A.setCollisionVolumes(mElevatorCollisionVolumes01A);
  	  	  		        
  	  	  		        HitReactionComponent hitReact01A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  	  		        hitReact01A.setPauseOnAttack(false);
  	  	  		        elevatorCollision01A.setHitReactionComponent(hitReact01A);
  	  	  		    	
  	  	  		        mElevatorCollisionGameObject01A.add(elevatorCollision01A);
  	  	  		        mElevatorCollisionGameObject01A.add(hitReact01A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 02.
  	  			        mElevatorCollisionGameObject02A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject02A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject02A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject02A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject02A.type = type;
  	  			        mElevatorCollisionGameObject02A.backgroundRadius.set(115.0f, 5.0f, 140.0f, 10.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_270deg, objectPlatform, true);
//  	  	  		    mElevatorCollisionVolumes02A = loadElevatorCollisionVolumes(R.raw.elevator_collision02_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision02A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision02A.setCollisionVolumes(mElevatorCollisionVolumes02A);
  	  			        
  	  			        HitReactionComponent hitReact02A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact02A.setPauseOnAttack(false);
  	  			        elevatorCollision02A.setHitReactionComponent(hitReact02A);
  	  			        
  	  			        mElevatorCollisionGameObject02A.add(elevatorCollision02A);
  	  			        mElevatorCollisionGameObject02A.add(hitReact02A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  	  	  		        // Use Elevator GameObject A. Load Elevator Collision 03.
  	  			        mElevatorCollisionGameObject03A = mGameObjectPool.allocate();
  	  			        mElevatorCollisionGameObject03A.gameObjectId = mGameObjectIdCount;
  	  			        mElevatorCollisionGameObject03A.backgroundGroup = true;	// Boolean used by GameObjectManager
  	  			        mElevatorCollisionGameObject03A.group = Group.PLATFORM_ELEVATOR;
  	  			        mElevatorCollisionGameObject03A.type = type;
  	  			        mElevatorCollisionGameObject03A.backgroundRadius.set(115.0f, 5.0f, 140.0f, 10.0f);
  	  	  		        
  	  	  		        mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_0deg_collision02or03_0_90_180deg, objectPlatform, true);
//  	  	  		    mElevatorCollisionVolumes03A = loadElevatorCollisionVolumes(R.raw.elevator_collision02_270deg, objectPlatform, true);
  	  			        
  	  			        BackgroundCollisionComponent elevatorCollision03A = 
  	  				        	(BackgroundCollisionComponent)allocateComponent(BackgroundCollisionComponent.class);
  	  			        elevatorCollision03A.setCollisionVolumes(mElevatorCollisionVolumes03A);
  	  			        
  	  			        HitReactionComponent hitReact03A = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
  	  			        hitReact03A.setPauseOnAttack(false);
  	  			        elevatorCollision03A.setHitReactionComponent(hitReact03A);
  	  			        
  	  			        mElevatorCollisionGameObject03A.add(elevatorCollision03A);
  	  			        mElevatorCollisionGameObject03A.add(hitReact03A);
  	  	  		        
						if (GameParameters.debug) {
				    		Log.i("Object", "GameObjectFactory spawnPlatformElevator() mElevatorCollisionGameObjectXXx object ID and type = " + 
				    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
						}
			    		
  	  	  		        mGameObjectIdCount++;
  	  	  		        
  		    			break;
  		    			
  		    		default:
  		    			break;	
  		    		}
  		    		
  		    		break;
  		    		
  		    	default:
  		    		break;
  		    	}

  		        
  	        } else {
  	    		Log.e("GameObjectFactory", "GameObject = NULL");
  	    	}
      	} else {
      		Log.e("GameObjectFactory", "GameObjectManager = NULL");
      	}
    }
    
    public void spawnItem(GL11 gl11, Type type, float x, float y, float z, float r) {	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
    	if (manager != null) {
    		// spawn Item
	    	GameObject objectItem = mGameObjectPool.allocate();
	    	
	    	if (objectItem != null) {
		    	objectItem.group = Group.ITEM;
		    	objectItem.type = type;
		    	
		    	objectItem.currentPosition.set(x, y, z, r);
		    	
		    	objectItem.activationRadius = ACTIVATION_RADIUS_NORMAL;
//		    	objectItem.activationRadius = mWideActivationRadius;
		    	
		    	objectItem.currentState = manager.droidBottomGameObject.currentState;
		    	objectItem.previousState = manager.droidBottomGameObject.previousState;
		      
		    	objectItem.gameObjectId = mGameObjectIdCount;
		    	
		    	OBBCollisionVolume collisionVolume;
		    	
		    	switch(type) {
		    	case ITEM_CRATE_WOOD:
			    	if (mItemCrateWoodDrawableDroidObject == null) {
			    		mItemCrateWoodDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectItem.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mItemCrateWoodDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_crate_wood_vbo, context);
//			    			objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_crate_wood_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnItem() Crate Wood gl11 is null!");
			    		}
			    		
////			    		objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mItemCrateWoodDrawableDroidObject = new DrawableDroid();
//			    		mItemCrateWoodDrawableDroidObject = objectItem.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnItem() Crate Wood NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectItem.drawableDroid = mItemCrateWoodDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnItem() Crate Wood COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectItem.drawableDroid = mItemCrateWoodDrawableDroidObject;
			    	
			    	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);	// Needs to be square to account for item rotation
//			    	collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnItem() Crate Wood object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			      	objectItem.hitPoints = 5;
			      	
			      	// Level00 Crate Wood used for Camera Focus Position
			    	if (GameParameters.levelRow == 0) {
			    		CameraSystem cameraSystem = sSystemRegistry.cameraSystem;
				    	cameraSystem.setTarget(objectItem);
				    	cameraSystem.setFocusPosition(objectItem.currentPosition);
			    	}
			    	
		    		break;
		    	
		    	case ITEM_CRATE_METAL:
			    	if (mItemCrateMetalDrawableDroidObject == null) {
			    		mItemCrateMetalDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectItem.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mItemCrateMetalDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_crate_metal_vbo, context);
//			    			objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_crate_metal_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnItem() Crate Metal gl11 is null!");
			    		}
			    		
////			    		objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mItemCrateMetalDrawableDroidObject = new DrawableDroid();
//			    		mItemCrateMetalDrawableDroidObject = objectItem.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnItem() Crate Metal NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectItem.drawableDroid = mItemCrateMetalDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnItem() Crate Metal COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectItem.drawableDroid = mItemCrateMetalDrawableDroidObject;
			    	
			    	collisionVolume = new OBBCollisionVolume(0.700f, 0.700f);
//			    	collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnItem() Crate Metal object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			      	objectItem.hitPoints = 50;
			    	
		    		break;
	    	
		    	case ITEM_LIGHT_BEACON:
			    	if (mItemLightBeaconDrawableDroidObject == null) {
			    		mItemLightBeaconDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectItem.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mItemLightBeaconDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_light_beacon_vbo, context);
//			    			objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_light_beacon_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnItem() Light Beacon gl11 is null!");
			    		}
			    		
////			    		objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mItemLightBeaconDrawableDroidObject = new DrawableDroid();
//			    		mItemLightBeaconDrawableDroidObject = objectItem.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnItem() Light Beacon NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectItem.drawableDroid = mItemLightBeaconDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnItem() Light Beacon COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectItem.drawableDroid = mItemLightBeaconDrawableDroidObject;
			    	
			    	collisionVolume = new OBBCollisionVolume(0.300f, 0.300f);
//			    	collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnItem() Light Beacon object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			    	// Special Level09 Beacon used for Final View
			    	if (GameParameters.levelRow == 9) {
			    		if (r > 700.0f) {
			    			mFinalLevel09BeaconGameObject = objectItem;
			    		}
			    	}
			    	
			      	objectItem.hitPoints = 50;
			    	
		    		break;
    	
				case ITEM_PISTON_ENGINE:
			    	if (mItemPistonEngineDrawableDroidObject == null) {
			    		mItemPistonEngineDrawableDroidObject = new DrawableDroid();
//			    		// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//			    		objectItem.objectInstantiation();
			          
			    		if (GameParameters.supportsVBOs && gl11 != null) {
			    			mItemPistonEngineDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_piston_engine_vbo, context);
//			    			objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_piston_engine_vbo, context);
			    		} else {
			    			Log.e("VBO", "GameObjectFactory spawnItem() Piston Engine gl11 is null!");
			    		}
			    		
////			    		objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////			    		objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//			          
//			    		mItemPistonEngineDrawableDroidObject = new DrawableDroid();
//			    		mItemPistonEngineDrawableDroidObject = objectItem.drawableDroid;
//			          
//			    		Log.i("Object", "GameObjectFactory spawnItem() Piston Engine NEW object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//			    	} else {
//			    		objectItem.drawableDroid = mItemPistonEngineDrawableDroidObject;
//			        
//			    		Log.i("Object", "GameObjectFactory spawnItem() Piston Engine COPY object ID and type = " + 
//			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");
			    	}
			    	
			    	objectItem.drawableDroid = mItemPistonEngineDrawableDroidObject;
			    	
			    	collisionVolume = new OBBCollisionVolume(0.300f, 0.300f);
//			    	collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
			    	
					if (GameParameters.debug) {
			    		Log.i("Object", "GameObjectFactory spawnItem() Piston Engine object ID and type = " + 
			    				" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
			    	
			      	objectItem.hitPoints = 50;
			    	
					break;
    
				case ITEM_TABLE:
					if (mItemTableDrawableDroidObject == null) {
						mItemTableDrawableDroidObject = new DrawableDroid();
//						// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						objectItem.objectInstantiation();
				      
						if (GameParameters.supportsVBOs && gl11 != null) {
							mItemTableDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_table_vbo, context);
//							objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_table_vbo, context);
						} else {
							Log.e("VBO", "GameObjectFactory spawnItem() Table gl11 is null!");
						}
						
////						objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////						objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//				      
//						mItemTableDrawableDroidObject = new DrawableDroid();
//						mItemTableDrawableDroidObject = objectItem.drawableDroid;
//				      
//						Log.i("Object", "GameObjectFactory spawnItem() Table NEW object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//					} else {
//						objectItem.drawableDroid = mItemTableDrawableDroidObject;
//				    
//						Log.i("Object", "GameObjectFactory spawnItem() Table COPY object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
					}
					
					objectItem.drawableDroid = mItemTableDrawableDroidObject;
					
					collisionVolume = new OBBCollisionVolume(1.300f, 1.300f);
//					collisionVolume = new OBBCollisionVolume(1.600f, 0.900f);
////					collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
					
					if (GameParameters.debug) {
						Log.i("Object", "GameObjectFactory spawnItem() Table object ID and type = " + 
								" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
					
				  	objectItem.hitPoints = 50;
					
					break;

				case ITEM_TABLE_COMPUTER:
					if (mItemTableComputerDrawableDroidObject == null) {
						mItemTableComputerDrawableDroidObject = new DrawableDroid();
//						// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						objectItem.objectInstantiation();
				      
						if (GameParameters.supportsVBOs && gl11 != null) {
							mItemTableComputerDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_table_computer_vbo, context);
//							objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_table_computer_vbo, context);
						} else {
							Log.e("VBO", "GameObjectFactory spawnItem() Table Computer gl11 is null!");
						}
						
////						objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////						objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//				      
//						mItemTableComputerDrawableDroidObject = new DrawableDroid();
//						mItemTableComputerDrawableDroidObject = objectItem.drawableDroid;
//				      
//						Log.i("Object", "GameObjectFactory spawnItem() Table Computer NEW object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//					} else {
//						objectItem.drawableDroid = mItemTableComputerDrawableDroidObject;
//				    
//						Log.i("Object", "GameObjectFactory spawnItem() Table Computer COPY object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
					}
					
					objectItem.drawableDroid = mItemTableComputerDrawableDroidObject;
					
					collisionVolume = new OBBCollisionVolume(1.300f, 1.300f);
//					collisionVolume = new OBBCollisionVolume(1.600f, 0.900f);
////					collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
					
					if (GameParameters.debug) {
						Log.i("Object", "GameObjectFactory spawnItem() Table Computer object ID and type = " + 
								" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
					
				  	objectItem.hitPoints = 50;
					
					break;

				case ITEM_SPACESHIP:
					if (mItemSpaceshipDrawableDroidObject == null) {
						mItemSpaceshipDrawableDroidObject = new DrawableDroid();
//						// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						objectItem.objectInstantiation();
				      
						if (GameParameters.supportsVBOs && gl11 != null) {
							mItemSpaceshipDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_spaceship_vbo, context);
//							objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_spaceship_vbo, context);
						} else {
							Log.e("VBO", "GameObjectFactory spawnItem() Spaceship gl11 is null!");
						}
						
////						objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////						objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//				      
//						mItemSpaceshipDrawableDroidObject = new DrawableDroid();
//						mItemSpaceshipDrawableDroidObject = objectItem.drawableDroid;
//				      
//						Log.i("Object", "GameObjectFactory spawnItem() Spaceship NEW object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//					} else {
//						objectItem.drawableDroid = mItemSpaceshipDrawableDroidObject;
//				    
//						Log.i("Object", "GameObjectFactory spawnItem() Spaceship COPY object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
					}
					
					objectItem.drawableDroid = mItemSpaceshipDrawableDroidObject;
					
					collisionVolume = new OBBCollisionVolume(0.01f, 0.01f);		// Collision Not Required
//					collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
					
					if (GameParameters.debug) {
						Log.i("Object", "GameObjectFactory spawnItem() Spaceship object ID and type = " + 
								" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
					
				  	objectItem.hitPoints = 50;
					
					break;

				case ITEM_SPACESHIP_DOOR:
					if (mItemShapeshipDoorDrawableDroidObject == null) {
						mItemShapeshipDoorDrawableDroidObject = new DrawableDroid();
//						// Multiple GameObjects using the same DrawableDroid only require one DrawableDroid instantiation
//						objectItem.objectInstantiation();
				      
						if (GameParameters.supportsVBOs && gl11 != null) {
							mItemShapeshipDoorDrawableDroidObject.loadObjectVBO(gl11, R.raw.item_spaceship_door_vbo, context);
//							objectItem.drawableDroid.loadObjectVBO(gl11, R.raw.item_spaceship_door_vbo, context);
						} else {
							Log.e("VBO", "GameObjectFactory spawnItem() Spaceship Door gl11 is null!");
						}
						
////						objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mLight0PositionBuffer);
////						objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
////				    			mMatShininessLowBuffer, mZeroBuffer);
//				      
//						mItemShapeshipDoorDrawableDroidObject = new DrawableDroid();
//						mItemShapeshipDoorDrawableDroidObject = objectItem.drawableDroid;
//				      
//						Log.i("Object", "GameObjectFactory spawnItem() Spaceship Door NEW object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
//					} else {
//						objectItem.drawableDroid = mItemShapeshipDoorDrawableDroidObject;
//				    
//						Log.i("Object", "GameObjectFactory spawnItem() Spaceship Door COPY object ID and type = " + 
//								" [" + mGameObjectIdCount + "] " + " (" + type + ")");
					}
					
					objectItem.drawableDroid = mItemShapeshipDoorDrawableDroidObject;
					
					collisionVolume = new OBBCollisionVolume(0.01f, 0.01f);		// Collision Not Required
//					collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
					
					if (GameParameters.debug) {
						Log.i("Object", "GameObjectFactory spawnItem() Spaceship Door object ID and type = " + 
								" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
					}
					
				  	objectItem.hitPoints = 50;
					
					break;
					
				default:
					collisionVolume = null;
					
					break;
				}
		    	
	    		objectItem.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
	    		objectItem.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mZeroBuffer);

		    	RenderComponent itemRender = (RenderComponent)allocateComponent(RenderComponent.class);
		    	itemRender.priority = SortConstants.FOREGROUND_OBJECT;
		      
		    	ItemComponent item = (ItemComponent)allocateComponent(ItemComponent.class);
		      
		    	DynamicCollisionComponent itemDynamicCollision = 
		    		(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
		      
//		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.800f, 0.550f);
////		    	OBBCollisionVolume collisionVolume = new OBBCollisionVolume(0.800f, 0.550f, HitType.BOUNCE);
		      
		    	itemDynamicCollision.setCollisionVolume(collisionVolume);
		    
		    	HitReactionComponent itemHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
		    	// FIXME Change Type.DROID_LASER_STD to variable for different Enemy types
//		    	itemHitReact.setSpawnOnDealHit(HitType.HIT, Type.DROID_LASER_STD, false, true);
		    
		    	itemDynamicCollision.setHitReactionComponent(itemHitReact);
		
		    	objectItem.add(item);
		    	objectItem.add(itemRender);
		    	objectItem.add(itemHitReact);
		    	if (type != Type.ITEM_SPACESHIP || type != Type.ITEM_SPACESHIP_DOOR) {
		    		objectItem.add(itemDynamicCollision);
		    	}
//		    	objectItem.add(itemDynamicCollision);
		    	
		    	manager.add(objectItem);
		      
		    	mGameObjectIdCount++;
	        } else {
	    		Log.e("GameObjectFactory", "GameObject = NULL");
	    	}
    	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
    
    public void spawnSpecialEffect(GL11 gl11, Type type, float x, float y, float z, float r) {    	
      	GameObjectManager manager = sSystemRegistry.gameObjectManager;
      	
      	if (manager != null) {
      		SpecialEffectSystem specialEffect = sSystemRegistry.specialEffectSystem;
      		
      		// Max 16 Special Effect Objects Per Set (4 GameObject Frames x 4 Sets Per Level)
    		FixedSizeArray<AnimationSet> specialEffectAnimationArraySets = new FixedSizeArray<AnimationSet>(MAX_SPECIAL_EFFECT_SETS);
    		
	    	switch(type) {
	    	case EXPLOSION:	    		
	    		for (int i = 0; i < MAX_SPECIAL_EFFECT_SETS; i++) {
	    			// spawn Explosion Special Effect. 4 Total Animation Frames and 4 Increments Per Frame.
	    			AnimationSet animationSet = new AnimationSet(4, 4);
	    			animationSet.type = type;
	    			
	    			for (int j = 0; j < 4; j++) {
	    				GameObject gameObject = mGameObjectPool.allocate();
	    				
	    				if (gameObject != null) {
	    					gameObject.group = Group.SPECIAL_EFFECT;
	    					gameObject.type = type;
	    					gameObject.activationRadius = ACTIVATION_RADIUS_ALWAYS;
	    					gameObject.gameObjectId = mGameObjectIdCount;
	    					gameObject.hitPoints = 1;
	    					
//	    					gameObject.objectInstantiation();
	    					
	    					switch(j) {
	    					case 0:
	    						if(mExplosionFrame1DrawableDroidObject == null) {
	    							mExplosionFrame1DrawableDroidObject = new DrawableDroid();
	    							
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionFrame1DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    			    		
	    						break;
	    						
	    					case 1:
	    						if(mExplosionFrame2DrawableDroidObject == null) {
	    							mExplosionFrame2DrawableDroidObject = new DrawableDroid();
	    							
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionFrame2DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 2:
	    						if(mExplosionFrame3DrawableDroidObject == null) {
	    							mExplosionFrame3DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionFrame3DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 3:
	    						if(mExplosionFrame4DrawableDroidObject == null) {
	    							mExplosionFrame4DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionFrame4DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    					}
	    					
	    					animationSet.addAnimationFrame(gameObject);
	    					
	    					if (GameParameters.debug) {
		    		    		Log.i("Object", "GameObjectFactory spawnSpecialEffect() EXPLOSION object ID and type = " + 
		    	    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
	    					}
	    					
	    					mGameObjectIdCount++;
	    				} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
	    			specialEffectAnimationArraySets.add(animationSet);
	    		}
	    		specialEffect.setInactiveAnimationSet(type, MAX_SPECIAL_EFFECT_SETS, specialEffectAnimationArraySets);
	    		
	    		break;
	    		
	    	case ELECTRIC_RING:	    		
	    		for (int i = 0; i < MAX_SPECIAL_EFFECT_SETS; i++) {
	    			// spawn Electric Ring Special Effect. 4 Total Animation Frames and 4 Increments Per Frame.
	    			AnimationSet animationSet = new AnimationSet(4, 4);
	    			animationSet.type = type;
	    			
	    			for (int j = 0; j < 4; j++) {
	    				GameObject gameObject = mGameObjectPool.allocate();
	    				
	    				if (gameObject != null) {
	    					gameObject.group = Group.SPECIAL_EFFECT;
	    					gameObject.type = type;
	    					gameObject.activationRadius = ACTIVATION_RADIUS_ALWAYS;
	    					gameObject.gameObjectId = mGameObjectIdCount;
	    					gameObject.hitPoints = 1;
	    					
//	    					gameObject.objectInstantiation();
	    					
	    					switch(j) {
	    					case 0:
	    						if(mElectricRingFrame1DrawableDroidObject == null) {
	    							mElectricRingFrame1DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricRingFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.electric_ring_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electric_ring_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() ElectricRing Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricRingFrame1DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 1:
	    						if(mElectricRingFrame2DrawableDroidObject == null) {
	    							mElectricRingFrame2DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricRingFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.electric_ring_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electric_ring_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() ElectricRing Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricRingFrame2DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 2:
	    						if(mElectricRingFrame3DrawableDroidObject == null) {
	    							mElectricRingFrame3DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricRingFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.electric_ring_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electric_ring_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() ElectricRing Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricRingFrame3DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 3:
	    						if(mElectricRingFrame4DrawableDroidObject == null) {
	    							mElectricRingFrame4DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricRingFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.electric_ring_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electric_ring_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() ElectricRing Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricRingFrame4DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    					}
	    					
	    					animationSet.addAnimationFrame(gameObject);
	    					
	    					if (GameParameters.debug) {
		    		    		Log.i("Object", "GameObjectFactory spawnSpecialEffect() ELECTRIC_RING object ID and type = " + 
		    	    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
	    					}
	    					
	    					mGameObjectIdCount++;
	    				} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
	    			specialEffectAnimationArraySets.add(animationSet);
	    		}
	    		specialEffect.setInactiveAnimationSet(type, MAX_SPECIAL_EFFECT_SETS, specialEffectAnimationArraySets);
	    		
	    		break;
	    		
	    	case ELECTRICITY:	    		
	    		for (int i = 0; i < MAX_SPECIAL_EFFECT_SETS; i++) {
	    			// spawn Electricity Special Effect. 4 Total Animation Frames and 4 Increments Per Frame.
	    			AnimationSet animationSet = new AnimationSet(4, 4);
	    			animationSet.type = type;
	    			
	    			for (int j = 0; j < 4; j++) {
	    				GameObject gameObject = mGameObjectPool.allocate();
	    				
	    				if (gameObject != null) {
	    					gameObject.group = Group.SPECIAL_EFFECT;
	    					gameObject.type = type;
	    					gameObject.activationRadius = ACTIVATION_RADIUS_ALWAYS;
	    					gameObject.gameObjectId = mGameObjectIdCount;
	    					gameObject.hitPoints = 1;
	    					
//	    					gameObject.objectInstantiation();
	    					
	    					switch(j) {
	    					case 0:
	    						if(mElectricityFrame1DrawableDroidObject == null) {
	    							mElectricityFrame1DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricityFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.electricity_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electricity_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Electricity Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricityFrame1DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    			    		
	    						break;
	    						
	    					case 1:
	    						if(mElectricityFrame2DrawableDroidObject == null) {
	    							mElectricityFrame2DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricityFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.electricity_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electricity_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Electricity Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricityFrame2DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 2:
	    						if(mElectricityFrame3DrawableDroidObject == null) {
	    							mElectricityFrame3DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricityFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.electricity_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electricity_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Electricity Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricityFrame3DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 3:
	    						if(mElectricityFrame4DrawableDroidObject == null) {
	    							mElectricityFrame4DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mElectricityFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.electricity_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.electricity_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Electricity Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mElectricityFrame4DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    					}
	    					
	    					animationSet.addAnimationFrame(gameObject);
	    					
	    					if (GameParameters.debug) {
		    		    		Log.i("Object", "GameObjectFactory spawnSpecialEffect() ELECTRICITY object ID and type = " + 
		    	    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
	    					}
	    					
	    					mGameObjectIdCount++;
	    				} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
	    			specialEffectAnimationArraySets.add(animationSet);
	    		}
	    		specialEffect.setInactiveAnimationSet(type, MAX_SPECIAL_EFFECT_SETS, specialEffectAnimationArraySets);
	    		
	    		break;
	    		
	    	case EXPLOSION_LARGE:	    		
	    		for (int i = 0; i < MAX_SPECIAL_EFFECT_SETS; i++) {
	    			// spawn Explosion Large Special Effect. 4 Total Animation Frames and 4 Increments Per Frame.
	    			AnimationSet animationSet = new AnimationSet(4, 4);
	    			animationSet.type = type;
	    			
	    			for (int j = 0; j < 4; j++) {
	    				GameObject gameObject = mGameObjectPool.allocate();
	    				
	    				if (gameObject != null) {
	    					gameObject.group = Group.SPECIAL_EFFECT;
	    					gameObject.type = type;
	    					gameObject.activationRadius = ACTIVATION_RADIUS_ALWAYS;
	    					gameObject.gameObjectId = mGameObjectIdCount;
	    					gameObject.hitPoints = 1;
	    					
//	    					gameObject.objectInstantiation();
	    					
	    					switch(j) {
	    					case 0:
	    						if(mExplosionLargeFrame1DrawableDroidObject == null) {
	    							mExplosionLargeFrame1DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionLargeFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_large_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_large_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Large Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionLargeFrame1DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    			    		
	    						break;
	    						
	    					case 1:
	    						if(mExplosionLargeFrame2DrawableDroidObject == null) {
	    							mExplosionLargeFrame2DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionLargeFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_large_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_large_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Large Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionLargeFrame2DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 2:
	    						if(mExplosionLargeFrame3DrawableDroidObject == null) {
	    							mExplosionLargeFrame3DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionLargeFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_large_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_large_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion LargeFrame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionLargeFrame3DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 3:
	    						if(mExplosionLargeFrame4DrawableDroidObject == null) {
	    							mExplosionLargeFrame4DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionLargeFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_large_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_large_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Large Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionLargeFrame4DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    					}

				    		// Area Collision Radius. Enabled in SpecialEffectSystem.
				    		DynamicCollisionComponent areaLaserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				    		
				    		OBBCollisionVolume areaLaserCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);
				          
				    		areaLaserDynamicCollision.setCollisionVolume(areaLaserCollisionVolume);
				          
				    		// Hit Reaction
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				        
				    		areaLaserDynamicCollision.setHitReactionComponent(laserHitReact);
				    		
				    		gameObject.add(laserHitReact);
				    		gameObject.add(areaLaserDynamicCollision);
	    					
	    					animationSet.addAnimationFrame(gameObject);
	    					
	    					if (GameParameters.debug) {
		    		    		Log.i("Object", "GameObjectFactory spawnSpecialEffect() EXPLOSION LARGE object ID and type = " + 
		    	    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
	    					}
	    					
	    					mGameObjectIdCount++;
	    				} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
	    			specialEffectAnimationArraySets.add(animationSet);
	    		}
	    		specialEffect.setInactiveAnimationSet(type, MAX_SPECIAL_EFFECT_SETS, specialEffectAnimationArraySets);
	    		
	    		break;
	    		
	    	case EXPLOSION_RING:	    		
	    		for (int i = 0; i < MAX_SPECIAL_EFFECT_SETS; i++) {
	    			// spawn Explosion Ring Special Effect. 4 Total Animation Frames and 4 Increments Per Frame.
	    			AnimationSet animationSet = new AnimationSet(4, 4);
	    			animationSet.type = type;
	    			
	    			for (int j = 0; j < 4; j++) {
	    				GameObject gameObject = mGameObjectPool.allocate();
	    				
	    				if (gameObject != null) {
	    					gameObject.group = Group.SPECIAL_EFFECT;
	    					gameObject.type = type;
	    					gameObject.activationRadius = ACTIVATION_RADIUS_ALWAYS;
	    					gameObject.gameObjectId = mGameObjectIdCount;
	    					gameObject.hitPoints = 1;
	    					
//	    					gameObject.objectInstantiation();
	    					
	    					switch(j) {
	    					case 0:
	    						if(mExplosionRingFrame1DrawableDroidObject == null) {
	    							mExplosionRingFrame1DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionRingFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_ring_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_ring_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionRingFrame1DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    			    		
	    						break;
	    						
	    					case 1:
	    						if(mExplosionRingFrame2DrawableDroidObject == null) {
	    							mExplosionRingFrame2DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionRingFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_ring_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_ring_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionRingFrame2DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 2:
	    						if(mExplosionRingFrame3DrawableDroidObject == null) {
	    							mExplosionRingFrame3DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionRingFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_ring_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_ring_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionRingFrame3DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 3:
	    						if(mExplosionRingFrame4DrawableDroidObject == null) {
	    							mExplosionRingFrame4DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mExplosionRingFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.explosion_ring_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.explosion_ring_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mExplosionRingFrame4DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    					}
	    					
				    		// Area Collision Radius. Enabled in SpecialEffectSystem.
				    		DynamicCollisionComponent areaLaserDynamicCollision = 
					    			(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
				    		
				    		OBBCollisionVolume areaLaserCollisionVolume = new OBBCollisionVolume(3.0f, 3.0f);
				          
				    		areaLaserDynamicCollision.setCollisionVolume(areaLaserCollisionVolume);
				          
				    		// Hit Reaction
				    		HitReactionComponent laserHitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class); 
				        
				    		areaLaserDynamicCollision.setHitReactionComponent(laserHitReact);
				    		
				    		gameObject.add(laserHitReact);
				    		gameObject.add(areaLaserDynamicCollision);
				    		
	    					animationSet.addAnimationFrame(gameObject);
	    					
	    					if (GameParameters.debug) {
		    		    		Log.i("Object", "GameObjectFactory spawnSpecialEffect() EXPLOSION RING object ID and type = " + 
		    	    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
	    					}
	    					
	    					mGameObjectIdCount++;
	    				} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
	    			specialEffectAnimationArraySets.add(animationSet);
	    		}
	    		specialEffect.setInactiveAnimationSet(type, MAX_SPECIAL_EFFECT_SETS, specialEffectAnimationArraySets);
	    		
	    		break;
	    		
	    	case TELEPORT_RING:	    		
	    		for (int i = 0; i < 1; i++) {
	    			// spawn Teleport Ring Special Effect. 4 Total Animation Frames and 4 Increments Per Frame.
	    			AnimationSet animationSet = new AnimationSet(4, 4);
	    			animationSet.type = type;
	    			
	    			for (int j = 0; j < 4; j++) {
	    				GameObject gameObject = mGameObjectPool.allocate();
	    				
	    				if (gameObject != null) {
	    					gameObject.group = Group.SPECIAL_EFFECT;
	    					gameObject.type = type;
	    					gameObject.activationRadius = ACTIVATION_RADIUS_ALWAYS;
	    					gameObject.gameObjectId = mGameObjectIdCount;
	    					gameObject.hitPoints = 1;
	    					
//	    					gameObject.objectInstantiation();
	    					
	    					switch(j) {
	    					case 0:
	    						if(mTeleportRingFrame1DrawableDroidObject == null) {
	    							mTeleportRingFrame1DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mTeleportRingFrame1DrawableDroidObject.loadObjectVBO(gl11, R.raw.teleport_ring_frame1_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.teleport_ring_frame1_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mTeleportRingFrame1DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    			    		
	    						break;
	    						
	    					case 1:
	    						if(mTeleportRingFrame2DrawableDroidObject == null) {
	    							mTeleportRingFrame2DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mTeleportRingFrame2DrawableDroidObject.loadObjectVBO(gl11, R.raw.teleport_ring_frame2_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.teleport_ring_frame2_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mTeleportRingFrame2DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 2:
	    						if(mTeleportRingFrame3DrawableDroidObject == null) {
	    							mTeleportRingFrame3DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mTeleportRingFrame3DrawableDroidObject.loadObjectVBO(gl11, R.raw.teleport_ring_frame3_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.teleport_ring_frame3_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mTeleportRingFrame3DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    						
	    					case 3:
	    						if(mTeleportRingFrame4DrawableDroidObject == null) {
	    							mTeleportRingFrame4DrawableDroidObject = new DrawableDroid();
		    						
		    						if (GameParameters.supportsVBOs && gl11 != null) {
		    							mTeleportRingFrame4DrawableDroidObject.loadObjectVBO(gl11, R.raw.teleport_ring_frame4_vbo, context);
//						    			gameObject.drawableDroid.loadObjectVBO(gl11, R.raw.teleport_ring_frame4_vbo, context);
							    	} else {
							    		Log.e("GameObjectFactory", "GameObjectFactory spawnSpecialEffect() Explosion Ring Frame gl11 is null!");
						    		}
	    						}
	    						
	    						gameObject.drawableDroid = mTeleportRingFrame4DrawableDroidObject;
	    						
	    						gameObject.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mLight0PositionBuffer);
	    						gameObject.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
	    				    			mMatShininessLowBuffer, mZeroBuffer);
	    						
	    						break;
	    					}
	    					
	    					animationSet.addAnimationFrame(gameObject);
	    					
	    					if (GameParameters.debug) {
		    		    		Log.i("Object", "GameObjectFactory spawnSpecialEffect() TELEPORT RING object ID and type = " + 
		    	    					" [" + mGameObjectIdCount + "] " + " (" + type + ")");	
	    					}
	    					
	    					mGameObjectIdCount++;
	    				} else {
				    		Log.e("GameObjectFactory", "GameObjectPool = NULL!");
				    	}
			    	}
	    			specialEffectAnimationArraySets.add(animationSet);
	    		}
	    		specialEffect.setInactiveAnimationSet(type, 1, specialEffectAnimationArraySets);
	    		
	    		break;
	    		
	    	}
	   	} else {
    		Log.e("GameObjectFactory", "GameObjectManager = NULL");
    	}
    }
    
    // FIXME 12/5/12 ADDED
    public void reloadDrawables(GL11 gl11) {   
    	Context newContext = ConstructActivity.getAppContext();
    	
		if (GameParameters.supportsVBOs && gl11 != null) {
	        // Drawable Objects	        
	        // Drawable Objects - FarBackground
	        if(mFarBackgroundDrawableDroidObject != null) {
		    	mFarBackgroundDrawableDroidObject.reloadGLTexture(gl11, newContext);
		    	mFarBackgroundDrawableDroidObject.reloadObjectVBO(gl11, newContext);	
	        }
	        
	        // Drawable Objects - Background
	        if(mBackgroundSection00DrawableDroidObject != null) {
	        	mBackgroundSection00DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection01DrawableDroidObject != null) {
	        	mBackgroundSection01DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection02DrawableDroidObject != null) {
	        	mBackgroundSection02DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection03DrawableDroidObject != null) {
	        	mBackgroundSection03DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection04DrawableDroidObject != null) {
	        	mBackgroundSection04DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection05DrawableDroidObject != null) {
	        	mBackgroundSection05DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection06DrawableDroidObject != null) {
	        	mBackgroundSection06DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection07DrawableDroidObject != null) {
	        	mBackgroundSection07DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection08DrawableDroidObject != null) {
	        	mBackgroundSection08DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundSection09DrawableDroidObject != null) {
	        	mBackgroundSection09DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - BackgroundWall
	        if(mBackgroundWallLaserSection01DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection01DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection02DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection02DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection03DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection03DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection04DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection04DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection05DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection05DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection06DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection06DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection07DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection07DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection08DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection08DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallLaserSection09DrawableDroidObject != null) {
	        	mBackgroundWallLaserSection09DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection01DrawableDroidObject != null) {
	        	mBackgroundWallPostSection01DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection02DrawableDroidObject != null) {
	        	mBackgroundWallPostSection02DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection03DrawableDroidObject != null) {
	        	mBackgroundWallPostSection03DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection04DrawableDroidObject != null) {
	        	mBackgroundWallPostSection04DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection05DrawableDroidObject != null) {
	        	mBackgroundWallPostSection05DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection06DrawableDroidObject != null) {
	        	mBackgroundWallPostSection06DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection07DrawableDroidObject != null) {
	        	mBackgroundWallPostSection07DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection08DrawableDroidObject != null) {
	        	mBackgroundWallPostSection08DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mBackgroundWallPostSection09DrawableDroidObject != null) {
	        	mBackgroundWallPostSection09DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Droid
	        if(mDroidBottomDrawableDroidObject != null) {
	        	mDroidBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidTopDrawableDroidObject != null) {
	        	mDroidTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Droid Weapons
	        if(mDroidWeaponLaserStdDrawableDroidObject != null) {
	        	mDroidWeaponLaserStdDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidWeaponLaserPulseDrawableDroidObject != null) {
	        	mDroidWeaponLaserPulseDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidWeaponLaserEmpDrawableDroidObject != null) {
	        	mDroidWeaponLaserEmpDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidWeaponLaserGrenadeDrawableDroidObject != null) {
	        	mDroidWeaponLaserGrenadeDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidWeaponLaserRocketDrawableDroidObject != null) {
	        	mDroidWeaponLaserRocketDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Droid Lasers
	        if(mDroidLaserStdDrawableDroidObject != null) {
	        	mDroidLaserStdDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidLaserPulseDrawableDroidObject != null) {
	        	mDroidLaserPulseDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidLaserEmpDrawableDroidObject != null) {
	        	mDroidLaserEmpDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidLaserGrenadeDrawableDroidObject != null) {
	        	mDroidLaserGrenadeDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mDroidLaserRocketDrawableDroidObject != null) {
	        	mDroidLaserRocketDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Enemies
	        if(mEnemyEMOTBottomDrawableDroidObject != null) {
	        	mEnemyEMOTBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMOTTopDrawableDroidObject != null) {
	        	mEnemyEMOTTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMOWBottomDrawableDroidObject != null) {
	        	mEnemyEMOWBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMOWTopDrawableDroidObject != null) {
	        	mEnemyEMOWTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMOWBossBottomDrawableDroidObject != null) {
	        	mEnemyEMOWBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMOWBossTopDrawableDroidObject != null) {
	        	mEnemyEMOWBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMSLBottomDrawableDroidObject != null) {
	        	mEnemyEMSLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMSLTopDrawableDroidObject != null) {
	        	mEnemyEMSLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMSLBossBottomDrawableDroidObject != null) {
	        	mEnemyEMSLBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyEMSLBossTopDrawableDroidObject != null) {
	        	mEnemyEMSLBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDFLBottomDrawableDroidObject != null) {
	        	mEnemyHDFLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDFLTopDrawableDroidObject != null) {
	        	mEnemyHDFLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDOLBottomDrawableDroidObject != null) {
	        	mEnemyHDOLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDOLTopDrawableDroidObject != null) {
	        	mEnemyHDOLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDOTBottomDrawableDroidObject != null) {
	        	mEnemyHDOTBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDOTTopDrawableDroidObject != null) {
	        	mEnemyHDOTTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDOWBottomDrawableDroidObject != null) {
	        	mEnemyHDOWBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDOWTopDrawableDroidObject != null) {
	        	mEnemyHDOWTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDSLBottomDrawableDroidObject != null) {
	        	mEnemyHDSLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDSLTopDrawableDroidObject != null) {
	        	mEnemyHDSLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTLBottomDrawableDroidObject != null) {
	        	mEnemyHDTLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTLTopDrawableDroidObject != null) {
	        	mEnemyHDTLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTLBossBottomDrawableDroidObject != null) {
	        	mEnemyHDTLBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTLBossTopDrawableDroidObject != null) {
	        	mEnemyHDTLBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTTBottomDrawableDroidObject != null) {
	        	mEnemyHDTTBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTTTopDrawableDroidObject != null) {
	        	mEnemyHDTTTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTTBossBottomDrawableDroidObject != null) {
	        	mEnemyHDTTBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTTBossTopDrawableDroidObject != null) {
	        	mEnemyHDTTBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTWBottomDrawableDroidObject != null) {
	        	mEnemyHDTWBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyHDTWTopDrawableDroidObject != null) {
	        	mEnemyHDTWTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCFMBottomDrawableDroidObject != null) {
	        	mEnemyLCFMBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCFMTopDrawableDroidObject != null) {
	        	mEnemyLCFMTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCOTBottomDrawableDroidObject != null) {
	        	mEnemyLCOTBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCOTTopDrawableDroidObject != null) {
	        	mEnemyLCOTTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCOTBossBottomDrawableDroidObject != null) {
	        	mEnemyLCOTBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCOTBossTopDrawableDroidObject != null) {
	        	mEnemyLCOTBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCSLBottomDrawableDroidObject != null) {
	        	mEnemyLCSLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCSLTopDrawableDroidObject != null) {
	        	mEnemyLCSLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCSLBossBottomDrawableDroidObject != null) {
	        	mEnemyLCSLBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCSLBossTopDrawableDroidObject != null) {
	        	mEnemyLCSLBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCTTBottomDrawableDroidObject != null) {
	        	mEnemyLCTTBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLCTTTopDrawableDroidObject != null) {
	        	mEnemyLCTTTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLSFMBottomDrawableDroidObject != null) {
	        	mEnemyLSFMBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLSFMTopDrawableDroidObject != null) {
	        	mEnemyLSFMTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLSTTBottomDrawableDroidObject != null) {
	        	mEnemyLSTTBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLSTTTopDrawableDroidObject != null) {
	        	mEnemyLSTTTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLSTTBossBottomDrawableDroidObject != null) {
	        	mEnemyLSTTBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLSTTBossTopDrawableDroidObject != null) {
	        	mEnemyLSTTBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyTAFLBottomDrawableDroidObject != null) {
	        	mEnemyTAFLBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyTAFLTopDrawableDroidObject != null) {
	        	mEnemyTAFLTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyTAFLBossBottomDrawableDroidObject != null) {
	        	mEnemyTAFLBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyTAFLBossTopDrawableDroidObject != null) {
	        	mEnemyTAFLBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyDRTTBossBottomDrawableDroidObject != null) {
	        	mEnemyDRTTBossBottomDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyDRTTBossTopDrawableDroidObject != null) {
	        	mEnemyDRTTBossTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Enemy Lasers    
	        if(mEnemyLaserStdDrawableDroidObject != null) {
	        	mEnemyLaserStdDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyLaserEmpDrawableDroidObject != null) {
	        	mEnemyLaserEmpDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mEnemyBossLaserDrawableDroidObject != null) {
	        	mEnemyBossLaserDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Astronauts
	        if(mAstronautPrivateTopDrawableDroidObject != null) {
	        	mAstronautPrivateTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautSergeantTopDrawableDroidObject != null) {
	        	mAstronautSergeantTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautCaptainTopDrawableDroidObject != null) {
	        	mAstronautCaptainTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautGeneralTopDrawableDroidObject != null) {
	        	mAstronautGeneralTopDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautBottomFrame1DrawableDroidObject != null) {
	        	mAstronautBottomFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautBottomFrame2DrawableDroidObject != null) {
	        	mAstronautBottomFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautBottomFrame3DrawableDroidObject != null) {
	        	mAstronautBottomFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mAstronautBottomFrame4DrawableDroidObject != null) {
	        	mAstronautBottomFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Platforms
	        if(mPlatformLevelStartDrawableDroidObject != null) {
	        	mPlatformLevelStartDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mPlatformLevelEndDrawableDroidObject != null) {
	        	mPlatformLevelEndDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mPlatformSectionStartDrawableDroidObject != null) {
	        	mPlatformSectionStartDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mPlatformSectionEndDrawableDroidObject != null) {
	        	mPlatformSectionEndDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mPlatformElevator0DegDrawableDroidObject != null) {
	        	mPlatformElevator0DegDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mPlatformElevator45DegDrawableDroidObject != null) {
	        	mPlatformElevator45DegDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Items
	        if(mItemCrateWoodDrawableDroidObject != null) {
	        	mItemCrateWoodDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemCrateMetalDrawableDroidObject != null) {
	        	mItemCrateMetalDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemLightBeaconDrawableDroidObject != null) {
	        	mItemLightBeaconDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemPistonEngineDrawableDroidObject != null) {
	        	mItemPistonEngineDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemTableDrawableDroidObject != null) {
	        	mItemTableDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemTableComputerDrawableDroidObject != null) {
	        	mItemTableComputerDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemSpaceshipDrawableDroidObject != null) {
	        	mItemSpaceshipDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mItemShapeshipDoorDrawableDroidObject != null) {
	        	mItemShapeshipDoorDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        // Drawable Objects - Special Effects
	        if(mExplosionFrame1DrawableDroidObject != null) {
	        	mExplosionFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionFrame2DrawableDroidObject != null) {
	        	mExplosionFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionFrame3DrawableDroidObject != null) {
	        	mExplosionFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionFrame4DrawableDroidObject != null) {
	        	mExplosionFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricRingFrame1DrawableDroidObject != null) {
	        	mElectricRingFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricRingFrame2DrawableDroidObject != null) {
	        	mElectricRingFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricRingFrame3DrawableDroidObject != null) {
	        	mElectricRingFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricRingFrame4DrawableDroidObject != null) {
	        	mElectricRingFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricityFrame1DrawableDroidObject != null) {
	        	mElectricityFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricityFrame2DrawableDroidObject != null) {
	        	mElectricityFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricityFrame3DrawableDroidObject != null) {
	        	mElectricityFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mElectricityFrame4DrawableDroidObject != null) {
	        	mElectricityFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionLargeFrame1DrawableDroidObject != null) {
	        	mExplosionLargeFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionLargeFrame2DrawableDroidObject != null) {
	        	mExplosionLargeFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionLargeFrame3DrawableDroidObject != null) {
	        	mExplosionLargeFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionLargeFrame4DrawableDroidObject != null) {
	        	mExplosionLargeFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionRingFrame1DrawableDroidObject != null) {
	        	mExplosionRingFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionRingFrame2DrawableDroidObject != null) {
	        	mExplosionRingFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionRingFrame3DrawableDroidObject != null) {
	        	mExplosionRingFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mExplosionRingFrame4DrawableDroidObject != null) {
	        	mExplosionRingFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mTeleportRingFrame1DrawableDroidObject != null) {
	        	mTeleportRingFrame1DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mTeleportRingFrame2DrawableDroidObject != null) {
	        	mTeleportRingFrame2DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mTeleportRingFrame3DrawableDroidObject != null) {
	        	mTeleportRingFrame3DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        if(mTeleportRingFrame4DrawableDroidObject != null) {
	        	mTeleportRingFrame4DrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
			
//			if (mDroidBottomDrawableDroidObject != null) {
//				mDroidBottomDrawableDroidObject.reloadObjectVBO(gl11);
////				mDroidBottomDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_bottom_vbo, newContext);
//			}
//			
//			if (mDroidTopDrawableDroidObject != null) {
//				mDroidTopDrawableDroidObject.reloadObjectVBO(gl11);
////				mDroidTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.droid_top_vbo, newContext);
//			}
//			
//			if (mEnemyHDOLTopDrawableDroidObject != null) {
//				mEnemyHDOLTopDrawableDroidObject.reloadObjectVBO(gl11);
////				mEnemyHDOLTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_head_oneleg_top_vbo, newContext);	
//			}
//			
//			if (mEnemyEMOTTopDrawableDroidObject !=null) {
//				mEnemyEMOTTopDrawableDroidObject.reloadObjectVBO(gl11);
////				mEnemyEMOTTopDrawableDroidObject.loadObjectVBO(gl11, R.raw.enemy_emp_onetread_top_vbo, newContext);
//			}
		} else {
			Log.e("VBO", "GameObjectFactory reloadDrawables() gl11 is null!");
		}
	
    }
    // FIXME END 12/5/12 ADDED
    
    // FIXME 12/5/12 ADDED
    public void reloadSplashScreenDrawables(GL11 gl11) {
    	Context newContext = ConstructActivity.getAppContext();
    	
		if (GameParameters.supportsVBOs && gl11 != null) {
	        // Drawable Objects - Splashscreen
	        if(mSplashLogoDrawableDroidObject != null) {
	        	mSplashLogoDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
	        
	        if(mSplashBackgroundDrawableDroidObject != null) {
	        	mSplashBackgroundDrawableDroidObject.reloadObjectVBO(gl11, newContext);
	        }
		} else {
			Log.e("VBO", "GameObjectFactory reloadSplashScreenDrawables() gl11 is null!");
		}
    }
    // FIXME END 12/5/12 ADDED
    
    public void finalLevel09View() {
    	CameraSystem cameraSystem = sSystemRegistry.cameraSystem;
    	cameraSystem.setTarget(mFinalLevel09BeaconGameObject);
    	cameraSystem.setFocusPosition(mFinalLevel09BeaconGameObject.currentPosition);
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	if (mFinalLevel09Astronaut1 != null) {
    		manager.add(mFinalLevel09Astronaut1);
    	}
    	if (mFinalLevel09Astronaut2 != null) {
    		manager.add(mFinalLevel09Astronaut2);
    	}
    	if (mFinalLevel09Astronaut3 != null) {
    		manager.add(mFinalLevel09Astronaut3);
    	}
    	if (mFinalLevel09Astronaut4 != null) {
    		manager.add(mFinalLevel09Astronaut4);
    	}
    	if (mFinalLevel09Astronaut5 != null) {
    		manager.add(mFinalLevel09Astronaut5);
    	}
    	
    	HudSystem hudSystem = sSystemRegistry.hudSystem;
		final float viewY = 8.0f;
		final float x = -7.8f;
		final float y = 6.75f;
		final float z = 7.8f;
		
		hudSystem.setViewangleBarButtonLocationY(viewY);
		cameraSystem.setViewangle(x, y, z);
    }
    
    public void enableElevatorCollision00(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory enableElevatorCollision00)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
	        	manager.add(mElevatorCollisionGameObject00A);
	        	
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:		    	
		    	manager.add(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject00C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject00D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject00E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.add(mElevatorCollisionGameObject00C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject00C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject00B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.add(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject00C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
//                Log.i("BackgroundCollision", "GameObjectFactory enableElevatorCollision00");
	        	
	        	manager.add(mElevatorCollisionGameObject00A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void disableElevatorCollision00(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory disableElevatorCollision00)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
	        	manager.remove(mElevatorCollisionGameObject00A);
	        	
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:		    	
		    	manager.remove(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject00C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject00D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject00E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.remove(mElevatorCollisionGameObject00C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject00C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject00B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.remove(mElevatorCollisionGameObject00A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject00B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject00C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
//                Log.i("BackgroundCollision", "GameObjectFactory disableElevatorCollision00");
	        	
	        	manager.remove(mElevatorCollisionGameObject00A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void enableElevatorCollision01(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory enableElevatorCollision01)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
	        
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:		    	
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject01C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject01D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject01E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.add(mElevatorCollisionGameObject01C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject01C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject01B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject01C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:
//                Log.i("BackgroundCollision", "GameObjectFactory enableElevatorCollision01");
                
		    	manager.add(mElevatorCollisionGameObject01A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void disableElevatorCollision01(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory disableElevatorCollision01)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
	        
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:		    	
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject01C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject01D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject01E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.remove(mElevatorCollisionGameObject01C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject01C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject01B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject01B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject01C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:
//	        	Log.i("BackgroundCollision", "GameObjectFactory disableElevatorCollision01");
	        	
		    	manager.remove(mElevatorCollisionGameObject01A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void enableElevatorCollision02(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory enableElevatorCollision02)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
	        
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:   	
		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject02C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject02D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject02E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.add(mElevatorCollisionGameObject02C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject02C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject02B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject02C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
//                Log.i("BackgroundCollision", "GameObjectFactory enableElevatorCollision02");
	        	
	        	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void disableElevatorCollision02(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory disableElevatorCollision02)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
	        	manager.remove(mElevatorCollisionGameObject02A);
	        	
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:		    	
		    	manager.remove(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject02C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject02D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject02E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.remove(mElevatorCollisionGameObject02C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject02C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject02B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.remove(mElevatorCollisionGameObject02A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject02B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject02C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
//                Log.i("BackgroundCollision", "GameObjectFactory disableElevatorCollision02");
	        	
	        	manager.remove(mElevatorCollisionGameObject02A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void enableElevatorCollision03(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory enableElevatorCollision03)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
	        
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
		    	manager.add(mElevatorCollisionGameObject03A);
//		    	manager.add(mElevatorCollisionGameObject02A);
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:   	
		    	manager.add(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject03C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject03D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject03E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.add(mElevatorCollisionGameObject03C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.add(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject03C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.add(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.add(mElevatorCollisionGameObject03B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.add(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.add(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.add(mElevatorCollisionGameObject03C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
//                Log.i("BackgroundCollision", "GameObjectFactory enableElevatorCollision03");
	        	
	        	manager.add(mElevatorCollisionGameObject03A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    public void disableElevatorCollision03(GameObject elevatorParentObject) {
//    	Log.i("Elevator", "GameObjectFactory disableElevatorCollision03)");
    	
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	
        switch(GameParameters.levelRow) {
        case 1:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:
	        	manager.remove(mElevatorCollisionGameObject03A);
	        	
	        	break;
	        }
        	break;
        	
        case 3:
	        switch(elevatorParentObject.type) {
	        case SECTION_01:		    	
		    	manager.remove(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject03C);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject03D);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject03E);
	        	break;
	        }
        	break;
        	
        case 5:
	        switch(elevatorParentObject.type) {
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_08:		    	
		    	manager.remove(mElevatorCollisionGameObject03C);
	        	break;
	        }
        	break;
        	
        case 6:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_04:		    	
		    	manager.remove(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject03C);
	        	break;
	        }
        	break;
        	
        case 7:
	        switch(elevatorParentObject.type) {
	        case SECTION_02:		    	
		    	manager.remove(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_07:		    	
		    	manager.remove(mElevatorCollisionGameObject03B);
	        	break;
	        }
        	break;
        	
        case 8:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
		    	manager.remove(mElevatorCollisionGameObject03A);
	        	break;
	        	
	        case SECTION_05:		    	
		    	manager.remove(mElevatorCollisionGameObject03B);
	        	break;
	        	
	        case SECTION_06:		    	
		    	manager.remove(mElevatorCollisionGameObject03C);
	        	break;
	        }
        	break;
        	
        case 9:
	        switch(elevatorParentObject.type) {
	        case SECTION_03:		    	
//                Log.i("BackgroundCollision", "GameObjectFactory disableElevatorCollision03");
	        	
	        	manager.remove(mElevatorCollisionGameObject03A);
	        	break;
	        }
        	break;
        	
        default:
        	break;
        }
    }
    
    /** FixedSizeArray<LineSegmentCollisionVolume> for Background */
    private FixedSizeArray<LineSegmentCollisionVolume> loadBackgroundCollisionVolumes(int levelSectionCollision) {
    	int numSegments = 0;
    	
		List<String> vLines = null;
		List<String> fLines = null;
		StringTokenizer vToken;
		StringTokenizer fToken;
		
    	try {
			// Reader for the Location File
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(context.getResources().openRawResource(levelSectionCollision)));
			
			// Read vertices and line segments
			String line = reader.readLine();
			while(line != null) {
				// Skip comments and empty lines
				if(line.startsWith("#") || line.trim().equals("")) {
					line = reader.readLine();
					continue;
				}
				
				// Read how many line segments this file contains
				if(line.startsWith("NUMPOLLIES")) {
					numSegments = Integer.valueOf(line.split(" ")[1]);					
					line = reader.readLine();
					continue;
				}
				
				// Read vertices
				while(line.startsWith("v")){
					if(vLines == null) {
						vLines = new ArrayList<String>();
					}
					vLines.add(line.substring(2));
			    	
					line = reader.readLine();
				}
				
				// Read line segment index
				while(line.startsWith("f")) {
					if(fLines == null) {
						fLines = new ArrayList<String>();
					}
					fLines.add(line.substring(2));
					
					line = reader.readLine();
					
					if(line == null) {
						break;
					}
					
					if(line.startsWith("v") || line.startsWith("#") || line.trim().equals("")) {
						continue;
					}
				}
				
				if(line == null) {
					break;
				} else {
					continue;
				}
			}
			
			reader.close();
			
		} catch(Exception e) {
			Log.e("Object", "GameObjectFactory loadBackgroundCollisionVolumes() Line Segment Collision could not load successfully", e);
		}	
		
        FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes = new FixedSizeArray<LineSegmentCollisionVolume>(numSegments);
        
        try {
			// Load Line Segments for Collision
			for(int i = 0; i < numSegments; i++) {
				float yTemp;
				
				fToken = new StringTokenizer(fLines.get(i));
				
				// Fill vertices for each line segment x1,z1 x2,z2 and calculate angle r
				LineSegment lineSegment = new LineSegment();
				int startPoint = Integer.valueOf(fToken.nextToken()) - 1;
				int endPoint = Integer.valueOf(fToken.nextToken()) - 1;
//				int quadrant = Integer.valueOf(fToken.nextToken());
				int hitType = Integer.valueOf(fToken.nextToken());
				
				// FIXME Add mMinLeftBottomDistance and mMinRightTopDistance check for all read segments
				vToken = new StringTokenizer(vLines.get(startPoint));
				lineSegment.x1 = Float.valueOf(vToken.nextToken());
				yTemp = Float.valueOf(vToken.nextToken());  // ignore
				lineSegment.z1 = Float.valueOf(vToken.nextToken());
				
				vToken = new StringTokenizer(vLines.get(endPoint));
				lineSegment.x2 = Float.valueOf(vToken.nextToken());
				yTemp = Float.valueOf(vToken.nextToken());  // ignore
				lineSegment.z2 = Float.valueOf(vToken.nextToken());
				
				// FIXME TEMP ONLY
				lineSegment.lineSegmentId = mLineSegmentIdCount;
				
				if (GameParameters.debug) {
					Log.i("Object", "GameObjectFactory loadBackgroundCollisionVolumes() [LS] [hitType] x1,z1;x2,z2 =" +
							" [ " + mLineSegmentIdCount + "] " + "[" + hitType + "] " +
							lineSegment.x1 + ", " + lineSegment.z1 + "; " + lineSegment.x2 + ", " + lineSegment.z2);	
				}
				
				mLineSegmentIdCount++;
				
				// hitType 0 = Bounce, hitType 1 = Fall, hitType 2 = Elevator
				switch (hitType) {
				case 0:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.BOUNCE));
					break;
					
				case 1:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.FALL));
					break;
					
				case 2:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.ELEVATOR));
					break;
					
				case 3:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.ELEVATOR_EXIT));
					break;
					
				case 4:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.ELEVATOR_ENEMY_BARRIER));
					break;
					
				case 5:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.GAME_END));
					break;
					
				default:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.BOUNCE));
					break;
				}
			}
		} catch(Exception e) {
			Log.e("Object", "GameObjectFactory loadBackgroundCollisionVolumes() Line Segment Collision could not load successfully", e);
		}
        
        return collisionVolumes;
    }
    
    /** FixedSizeArray<LineSegmentCollisionVolume> for Platform Elevator */
    private FixedSizeArray<LineSegmentCollisionVolume> loadElevatorCollisionVolumes(int levelSectionCollision, GameObject elevator, boolean moveType) {
    	int numSegments = 0;
    	
		List<String> vLines = null;
		List<String> fLines = null;
		StringTokenizer vToken;
		StringTokenizer fToken;
		
    	try {
			// Reader for the Location File
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(context.getResources().openRawResource(levelSectionCollision)));
			
			// Read vertices and line segments
			String line = reader.readLine();
			while(line != null) {
				// Skip comments and empty lines
				if(line.startsWith("#") || line.trim().equals("")) {
					line = reader.readLine();
					continue;
				}
				
				// Read how many line segments this file contains
				if(line.startsWith("NUMPOLLIES")) {
					numSegments = Integer.valueOf(line.split(" ")[1]);					
					line = reader.readLine();
					continue;
				}
				
				// Read vertices
				while(line.startsWith("v")){
					if(vLines == null) {
						vLines = new ArrayList<String>();
					}
					vLines.add(line.substring(2));
			    	
					line = reader.readLine();
				}
				
				// Read line segment index
				while(line.startsWith("f")) {
					if(fLines == null) {
						fLines = new ArrayList<String>();
					}
					fLines.add(line.substring(2));
					
					line = reader.readLine();
					
					if(line == null) {
						break;
					}
					
					if(line.startsWith("v") || line.startsWith("#") || line.trim().equals("")) {
						continue;
					}
				}
				
				if(line == null) {
					break;
				} else {
					continue;
				}
			}
			
			reader.close();
			
		} catch(Exception e) {
			Log.e("Object", "GameObjectFactory loadElevatorCollisionVolumes() Line Segment Collision could not load successfully", e);
		}	
		
        FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes = new FixedSizeArray<LineSegmentCollisionVolume>(numSegments);
        
        try {
			// Load Line Segments for Collision
			for(int i = 0; i < numSegments; i++) {
				float yTemp;
				
				fToken = new StringTokenizer(fLines.get(i));
				
				// Fill vertices for each line segment x1,z1 x2,z2 and calculate angle r
				LineSegment lineSegment = new LineSegment();
				int startPoint = Integer.valueOf(fToken.nextToken()) - 1;
				int endPoint = Integer.valueOf(fToken.nextToken()) - 1;
//				int quadrant = Integer.valueOf(fToken.nextToken());
				int hitType = Integer.valueOf(fToken.nextToken());
				
				// FIXME Add mMinLeftBottomDistance and mMinRightTopDistance check for all read segments
				vToken = new StringTokenizer(vLines.get(startPoint));
				lineSegment.x1 = Float.valueOf(vToken.nextToken());
				yTemp = Float.valueOf(vToken.nextToken());  // ignore
				lineSegment.z1 = Float.valueOf(vToken.nextToken());
				
				vToken = new StringTokenizer(vLines.get(endPoint));
				lineSegment.x2 = Float.valueOf(vToken.nextToken());
				yTemp = Float.valueOf(vToken.nextToken());  // ignore
				lineSegment.z2 = Float.valueOf(vToken.nextToken());
				
				// FIXME TEMP ONLY
				lineSegment.lineSegmentId = mLineSegmentIdCount;
				
				if (GameParameters.debug) {
					Log.i("Object", "GameObjectFactory loadElevatorCollisionVolumes() [LS] [hitType] x1,z1;x2,z2 =" +
							" [ " + mLineSegmentIdCount + "] " + "[" + hitType + "] " +
							lineSegment.x1 + ", " + lineSegment.z1 + "; " + lineSegment.x2 + ", " + lineSegment.z2);	
				}
				
				mLineSegmentIdCount++;
				
				// hitType 0 = Bounce, hitType 1 = Fall, hitType 2 = Elevator, hitType 3 = Elevator Exit
				switch (hitType) {
				case 0:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.BOUNCE, elevator, moveType));
					break;
					
				case 1:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.FALL, elevator, moveType));
					break;
					
				case 2:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.ELEVATOR, elevator, moveType));
					break;
					
				case 3:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.ELEVATOR_EXIT, elevator, moveType));
					break;
					
				case 4:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.ELEVATOR_ENEMY_BARRIER, elevator, moveType));
					break;
					
				case 5:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.GAME_END, elevator, moveType));
					break;
					
				default:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.BOUNCE, elevator, moveType));
					break;
				}
			}
		} catch(Exception e) {
			Log.e("Object", "GameObjectFactory loadElevatorCollisionVolumes() Line Segment Collision could not load successfully", e);
		}
        
        return collisionVolumes;
    }
    
    /** FixedSizeArray<LineSegmentCollisionVolume> for Platform Section */
    private FixedSizeArray<LineSegmentCollisionVolume> loadPlatformSectionCollisionVolumes(int levelSectionCollision, GameObject platform, boolean moveType) {
    	int numSegments = 0;
    	
		List<String> vLines = null;
		List<String> fLines = null;
		StringTokenizer vToken;
		StringTokenizer fToken;
		
    	try {
			// Reader for the Location File
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(context.getResources().openRawResource(levelSectionCollision)));
			
			// Read vertices and line segments
			String line = reader.readLine();
			while(line != null) {
				// Skip comments and empty lines
				if(line.startsWith("#") || line.trim().equals("")) {
					line = reader.readLine();
					continue;
				}
				
				// Read how many line segments this file contains
				if(line.startsWith("NUMPOLLIES")) {
					numSegments = Integer.valueOf(line.split(" ")[1]);					
					line = reader.readLine();
					continue;
				}
				
				// Read vertices
				while(line.startsWith("v")){
					if(vLines == null) {
						vLines = new ArrayList<String>();
					}
					vLines.add(line.substring(2));
			    	
					line = reader.readLine();
				}
				
				// Read line segment index
				while(line.startsWith("f")) {
					if(fLines == null) {
						fLines = new ArrayList<String>();
					}
					fLines.add(line.substring(2));
					
					line = reader.readLine();
					
					if(line == null) {
						break;
					}
					
					if(line.startsWith("v") || line.startsWith("#") || line.trim().equals("")) {
						continue;
					}
				}
				
				if(line == null) {
					break;
				} else {
					continue;
				}
			}
			
			reader.close();
			
		} catch(Exception e) {
			Log.e("Object", "GameObjectFactory loadPlatformSectionCollisionVolumes() Line Segment Collision could not load successfully", e);
		}	
		
        FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes = new FixedSizeArray<LineSegmentCollisionVolume>(numSegments);
        
        try {
			// Load Line Segments for Collision
			for(int i = 0; i < numSegments; i++) {
				float yTemp;
				
				fToken = new StringTokenizer(fLines.get(i));
				
				// Fill vertices for each line segment x1,z1 x2,z2 and calculate angle r
				LineSegment lineSegment = new LineSegment();
				int startPoint = Integer.valueOf(fToken.nextToken()) - 1;
				int endPoint = Integer.valueOf(fToken.nextToken()) - 1;
//				int quadrant = Integer.valueOf(fToken.nextToken());
				int hitType = Integer.valueOf(fToken.nextToken());
				
				// FIXME Add mMinLeftBottomDistance and mMinRightTopDistance check for all read segments
				vToken = new StringTokenizer(vLines.get(startPoint));
				lineSegment.x1 = Float.valueOf(vToken.nextToken());
				yTemp = Float.valueOf(vToken.nextToken());  // ignore
				lineSegment.z1 = Float.valueOf(vToken.nextToken());
				
				vToken = new StringTokenizer(vLines.get(endPoint));
				lineSegment.x2 = Float.valueOf(vToken.nextToken());
				yTemp = Float.valueOf(vToken.nextToken());  // ignore
				lineSegment.z2 = Float.valueOf(vToken.nextToken());
				
				// FIXME TEMP ONLY
				lineSegment.lineSegmentId = mLineSegmentIdCount;
				
//				Log.i("Object", "GameObjectFactory loadPlatformSectionCollisionVolumes() lineSegmentId =" +
//						" [" + lineSegment.lineSegmentId + "] ");
				
				mLineSegmentIdCount++;
				
				// hitType 0 = Bounce, hitType 1 = Fall, hitType 2 = Platform Start or Platform End, hitType 3 = Platform Exit
				switch (hitType) {
				case 0:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.BOUNCE, platform, moveType));
					break;
					
				case 1:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.FALL, platform, moveType));
					break;
					
				case 2:
					if (platform.group == Group.PLATFORM_SECTION_START) {
						collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.PLATFORM_SECTION_START, platform, moveType));
					} else {
						collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.PLATFORM_SECTION_END, platform, moveType));
					}
					
					break;
					
//				case 3:
//					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.PLATFORM_SECTION_EXIT, platform, moveType));
//					break;
					
				default:
					collisionVolumes.add(new LineSegmentCollisionVolume(lineSegment, CurrentState.BOUNCE, platform, moveType));
					break;
				}
			}
		} catch(Exception e) {
			Log.e("Object", "GameObjectFactory loadElevatorCollisionVolumes() Line Segment Collision could not load successfully", e);
		}
        
        return collisionVolumes;
    }
    
//    private void switchLineSegmentPoints(LineSegment lineSegment) {
//    	float xTemp;
//    	float zTemp;
//    	
//		xTemp = lineSegment.x1;
//		lineSegment.x1 = lineSegment.x2;
//		lineSegment.x2 = xTemp;
//		
//		zTemp = lineSegment.z1;
//		lineSegment.z1 = lineSegment.z2;
//		lineSegment.z2 = zTemp;
//    }
    
    private int getFarBackgroundTexture(GL11 gl11, Type type) {
    	int texture = -1;
    	
		switch(type) {
		case SECTION_00:
			texture = R.drawable.far_background01;
	    	break;
	    		
		case SECTION_01:
//		case FAR_BACKGROUND_01:
			texture = R.drawable.far_background01;
    		break;
    		
		case SECTION_02:
//		case FAR_BACKGROUND_02:
			texture = R.drawable.far_background02;
    		break;
    		
		case SECTION_03:
//		case FAR_BACKGROUND_03:
			texture = R.drawable.far_background03;
    		break;
    		
		case SECTION_04:
//		case FAR_BACKGROUND_04:
			texture = R.drawable.far_background04;
    		break;
    		
		case SECTION_05:
//		case FAR_BACKGROUND_05:
			texture = R.drawable.far_background05;
    		break;
    		
		case SECTION_06:
//		case FAR_BACKGROUND_06:
			texture = R.drawable.far_background06;
    		break;
    		
		case SECTION_07:
//		case FAR_BACKGROUND_07:
			texture = R.drawable.far_background07;
    		break;
    		
		case SECTION_08:
//		case FAR_BACKGROUND_08:
			texture = R.drawable.far_background08;
    		break;
    		
		case SECTION_09:
//		case FAR_BACKGROUND_09:
			texture = R.drawable.far_background09;
    		break;
    		
    	default:
			texture = R.drawable.far_background01;
    		break;
		}
    	
    	return texture;
    }
    
    private int getLevelSectionVBO(GL11 gl11, GameObject object, Group group, Type type) {
    	int section = -1;
    	
    	switch(group) {
    	case BACKGROUND_01:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level01_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level01_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level01_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level01_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level01_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level01_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level01_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level01_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level01_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_02:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level02_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level02_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level02_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level02_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level02_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level02_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level02_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level02_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level02_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_03:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level03_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level03_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level03_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level03_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level03_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level03_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level03_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level03_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_04:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level04_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level04_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level04_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level04_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level04_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level04_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level04_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level04_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level04_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_05:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level05_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level05_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level05_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level05_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level05_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level05_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level05_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level05_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level05_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_06:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level06_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level06_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level06_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level06_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level06_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level06_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level06_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		break;
    		
    	case BACKGROUND_07:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level07_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level07_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level07_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level07_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level07_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level07_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level07_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level07_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level07_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_08:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level08_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level08_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level08_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level08_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level08_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level08_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level08_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level08_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level08_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_09:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level09_section01_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level09_section02_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level09_section03_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level09_section04_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level09_section05_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level09_section06_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level09_section07_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level09_section08_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level09_section09_vbo;
        		
//		    	object.drawableDroid.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mLight0PositionBuffer);
//		    	object.drawableDroid.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
//		    			mMatShininessLowBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    	
        default:
        	break;
    	}
        
        return section;
    }
    
    private int getLevelSectionCollision(Group group, Type type) {
    	int section = -1;
    	
    	switch(group) {
    	case BACKGROUND_01:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level01_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level01_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level01_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level01_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level01_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level01_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level01_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level01_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level01_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_02:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level02_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level02_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level02_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level02_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level02_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level02_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level02_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level02_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level02_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_03:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level03_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level03_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level03_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level03_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level03_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level03_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level03_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level03_collision08;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_04:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level04_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level04_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level04_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level04_collision04;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level04_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level04_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level04_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level04_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_05:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level05_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level05_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level05_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level05_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level05_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level05_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level05_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level05_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level05_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_06:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level06_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level06_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level06_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level06_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level06_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level06_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level06_collision07;
        		break;
        		
            default:
            	break;
    		}
    		break;
    		
    	case BACKGROUND_07:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level07_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level07_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level07_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level07_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level07_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level07_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level07_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level07_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level07_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_08:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level08_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level08_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level08_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level08_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level08_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level08_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level08_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level08_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level08_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_09:
    		switch(type) {
    		case SECTION_01:
        		section = R.raw.level09_collision01;
        		break;
        	
        	case SECTION_02:
        		section = R.raw.level09_collision02;
        		break;
        		
        	case SECTION_03:
        		section = R.raw.level09_collision03;
        		break;
        		
        	case SECTION_04:
        		section = R.raw.level09_collision04;
        		break;
        		
        	case SECTION_05:
        		section = R.raw.level09_collision05;
        		break;
        		
        	case SECTION_06:
        		section = R.raw.level09_collision06;
        		break;
        		
        	case SECTION_07:
        		section = R.raw.level09_collision07;
        		break;
        		
        	case SECTION_08:
        		section = R.raw.level09_collision08;
        		break;
        		
        	case SECTION_09:
        		section = R.raw.level09_collision09;
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    	
        default:
        	break;
    	}
        
        return section;
    }
    
    private int getBackgroundWallVBO(GL11 gl11, DrawableDroid drawable, Group group, Type type) {
    	int wall = -1;
    	
    	switch(group) {
    	case BACKGROUND_WALL_01:
    		switch(type) {
    		case WALL_LASER_01:
    			wall = R.raw.level01_wall_laser_blue01_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        	
        	case WALL_LASER_02:
        		wall = R.raw.level01_wall_laser_blue02_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        		
        	case WALL_LASER_03:
        		wall = R.raw.level01_wall_laser_blue03_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        		
        	case WALL_LASER_04:
        		wall = R.raw.level01_wall_laser_blue04_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        		
        	case WALL_LASER_05:
        		wall = R.raw.level01_wall_laser_blue05_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        		
        	case WALL_LASER_07:
        		wall = R.raw.level01_wall_laser_blue07_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        		
        	case WALL_LASER_08:
        		wall = R.raw.level01_wall_laser_blue08_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
        		
        	case WALL_LASER_09:
        		wall = R.raw.level01_wall_laser_blue09_vbo;
        		
		    	drawable.loadLight0Buffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mBlueLaserBuffer, mBlueLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mBlueLaserBuffer);
        		break;
            	
        	case WALL_POST_01:
        		wall = R.raw.level01_wall_post_gold01_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
            	
        	case WALL_POST_02:
        		wall = R.raw.level01_wall_post_gold02_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_03:
        		wall = R.raw.level01_wall_post_gold03_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_04:
        		wall = R.raw.level01_wall_post_gold04_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_05:
        		wall = R.raw.level01_wall_post_gold05_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_07:
        		wall = R.raw.level01_wall_post_gold07_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_08:
        		wall = R.raw.level01_wall_post_gold08_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_09:
        		wall = R.raw.level01_wall_post_gold09_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_WALL_02:
    		switch(type) {
    		case WALL_LASER_01:
    			wall = R.raw.level02_wall_laser_red01_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        	
        	case WALL_LASER_02:
        		wall = R.raw.level02_wall_laser_red02_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_03:
        		wall = R.raw.level02_wall_laser_red03_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_04:
        		wall = R.raw.level02_wall_laser_red04_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_05:
        		wall = R.raw.level02_wall_laser_red05_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_06:
        		wall = R.raw.level02_wall_laser_red06_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_07:
        		wall = R.raw.level02_wall_laser_red07_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_08:
        		wall = R.raw.level02_wall_laser_red08_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
        		
        	case WALL_LASER_09:
        		wall = R.raw.level02_wall_laser_red09_vbo;
        		
		    	drawable.loadLight0Buffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mRedLaserBuffer, mRedLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mRedLaserBuffer);
        		break;
            	
        	case WALL_POST_01:
        		wall = R.raw.level02_wall_post_gold01_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
            	
        	case WALL_POST_02:
        		wall = R.raw.level02_wall_post_gold02_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_03:
        		wall = R.raw.level02_wall_post_gold03_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_04:
        		wall = R.raw.level02_wall_post_gold04_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_05:
        		wall = R.raw.level02_wall_post_gold05_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_06:
        		wall = R.raw.level02_wall_post_gold06_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_07:
        		wall = R.raw.level02_wall_post_gold07_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_08:
        		wall = R.raw.level02_wall_post_gold08_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
        	case WALL_POST_09:
        		wall = R.raw.level02_wall_post_gold09_vbo;
        		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
        		break;
        		
            default:
            	break;
    		}
    		
    		break;
    		
    	case BACKGROUND_WALL_03:
    		switch(type) {
    		case WALL_LASER_01:
    			wall = R.raw.level03_wall_laser_green01_vbo;
        		
		    	drawable.loadLight0Buffers(mGreenLaserBuffer, mGreenLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mGreenLaserBuffer, mGreenLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mGreenLaserBuffer);
        		break;
        	
        	case WALL_LASER_04:
        		wall = R.raw.level03_wall_laser_green04_vbo;
        		
		    	drawable.loadLight0Buffers(mGreenLaserBuffer, mGreenLaserBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mGreenLaserBuffer, mGreenLaserBuffer, mHighBuffer, 
		    			mMatShininessLowBuffer, mGreenLaserBuffer);
        		break;
    		
	    	case WALL_POST_01:
	    		wall = R.raw.level03_wall_post_gold01_vbo;
	    		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
	    		break;
	        	
	    	case WALL_POST_04:
	    		wall = R.raw.level03_wall_post_gold04_vbo;
	    		
		    	drawable.loadLight0Buffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mLight0PositionBuffer);
		    	drawable.loadMaterialBuffers(mMidBuffer, mMidBuffer, mHighBuffer, 
		    			mMatShininessHighBuffer, mZeroBuffer);
	    		break;
    		
        	default:
        		break;
    		}
        		
    		break;
    		
    	case BACKGROUND_WALL_04:
    		break;
    		
    	case BACKGROUND_WALL_05:
    		break;
    		
    	case BACKGROUND_WALL_06:
    		break;
    		
    	case BACKGROUND_WALL_07:
    		break;
    		
    	case BACKGROUND_WALL_08:
    		break;
    		
    	case BACKGROUND_WALL_09:
    		break;
    	
        default:
        	break;
    	}
        
        return wall;
    }
    
    public void setWeapons(Type weaponActiveType, Type weaponInventoryType) {
    	mWeaponActiveType = weaponActiveType;
    	mWeaponInventoryType = weaponInventoryType;
    	
//        Log.i("GameFlow", "GameObjectFactory setWeapons() mWeaponActiveType, mWeaponInventoryType = " +
//        		mWeaponActiveType + ", " + mWeaponInventoryType);
    }
    
    public void startBackgroundMusic(int sound) {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameObjectFactory startBackgroundMusic(int sound)");	
    	}
    	
    	mBackgroundMusic = MediaPlayer.create(context, sound);
//    	mBackgroundMusic = MediaPlayer.create(context, R.raw.sound_voice_intruder_alert_background);
      	mBackgroundMusic.setVolume(1.0f, 1.0f);
      	mBackgroundMusic.setLooping(true);
      	if (mMusicEnabled) {
          	mBackgroundMusic.start();
        	musicPlaying = true;
      	}
    }
    
    public void startBackgroundMusic() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameObjectFactory startBackgroundMusic()");	
    	}
    	
    	if (mMusicEnabled) {
          	mBackgroundMusic.start();
        	musicPlaying = true;	
    	}
    }
    
    public void stopBackgroundMusic() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameObjectFactory stopBackgroundMusic()");	
    	}
    	
    	if (mMusicEnabled) {
          	mBackgroundMusic.stop();
        	musicPlaying = false;	
    	}
    }
    
    public void pauseBackgroundMusic() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameObjectFactory pauseBackgroundMusic()");	
    	}
    	
    	if (mMusicEnabled) {
          	mBackgroundMusic.pause();
        	musicPlaying = false;	
    	}
    }
    
    public void setBackgroundMusicVolume(float left, float right) {
    	mBackgroundMusic.setVolume(left, right);
    }
    
    public void setMusicEnabled(boolean enable) {
    	mMusicEnabled = enable;
    }
    
    /** Comparator for game objects objects. */
    private static final class ComponentPoolComparator implements Comparator<GameComponentPool> {
        public int compare(final GameComponentPool object1, final GameComponentPool object2) {
            int result = 0;
            if (object1 == null && object2 != null) {
                result = 1;
            } else if (object1 != null && object2 == null) {
                result = -1;
            } else if (object1 != null && object2 != null) {
                result = object1.objectClass.hashCode() - object2.objectClass.hashCode();
            }
            return result;
        }
    }
    
    public class GameObjectPool extends TObjectPool<GameObject> {

        public GameObjectPool() {
            super();
            
			if (GameParameters.debug) {
	            Log.i("Object", "GameObjectFactory GameObjectPool GameObjectPool() initialize DEFAULT_SIZE");	
			}
        }
        
        public GameObjectPool(int size) {
            super(size);
            
			if (GameParameters.debug) {
	            Log.i("Object", "GameObjectFactory GameObjectPool ObjectPool() initialize setSize = " + size);	
			}
        }
        
        @Override
        protected void fill() {
			if (GameParameters.debug) {
	            Log.i("Object", "GameObjectFactory GameObjectPool fill() getSize = " + getSize());	
			}
        	
            for (int x = 0; x < getSize(); x++) {
//            	Log.i("Object", "GameObjectFactory GameObjectPool fill() x = " + x);
            	
                getAvailable().add(new GameObject());
            }
        }

        @Override
        public void release(Object entry) {
			if (GameParameters.debug) {
	        	Log.i("Object", "GameObjectFactory GameObjectPool release()" + " [" + ((GameObject)entry).gameObjectId + "] ");	
			}
        	
            ((GameObject)entry).reset();
            super.release(entry);
        }
    }
}
