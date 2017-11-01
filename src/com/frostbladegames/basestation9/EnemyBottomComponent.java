/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;

import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.SoundSystem.Sound;
import com.frostbladegames.basestation9.R;

public class EnemyBottomComponent extends GameComponent {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;
    
	private static final float LEVEL_START_SPEED_ADJUST = 0.25f;
	
	private static final float RANDOM_CHECK_MOUNT = 4.0f;
	private static final float RANDOM_TIME_MOUNT = 0.5f;
	private static final int RANDOM_DEGREES_MOUNT = 15;
	private static final float RANDOM_CHECK_SPIDER_LEGS = 3.0f;
	private static final float RANDOM_TIME_SPIDER_LEGS = 1.5f;
	private static final int RANDOM_DEGREES_SPIDER_LEGS = 30;
	private static final float RANDOM_CHECK_WHEELS_TREAD = 4.0f;
	private static final float RANDOM_TIME_WHEELS_TREAD = 1.5f;
	private static final int RANDOM_DEGREES_WHEELS_TREAD = 25;
	private static final float RANDOM_CHECK_SPRING_LEGS = 3.0f;
	private static final float RANDOM_TIME_SPRING_LEGS = 1.5f;
	private static final int RANDOM_DEGREES_SPRING_LEGS = 40;
	private static final float RANDOM_CHECK_FLY = 3.0f;
//	private static final float RANDOM_TIME_FLY = 1.0f;
	private static final int RANDOM_DEGREES_FLY = 25;
	
	private static final float SPRING_HEIGHT_MAX = 0.75f;
	
	private static final float ENEMY_WHEELS_SPRINT_MAGNITUDE = 2.0f;
    
	private static final float HIT_REACT_TIME = 0.5f;
	
	private static final float COLLISION_RESPONSE_VELOCITY = 1.0f;
	
    public static final int MAX_ENEMY_LIFE = 3;
//    public static final float MAX_ENEMY_LIFE = 3.0f;
	
	public static final float GLOW_DURATION = 15.0f;
	
//	private CurrentState mState;
	
    private Vector3 mTempPosition;
	
//	private float mX;
//	private float mY;
//	private float mZ;
//	private float mR;
	
//	private float mOX;
//	private float mOY;
//	private float mOZ;
	
    private float mTimer;
    private float mLastRandomMoveTime;
    private float mNextRandomCheckTime;
    private int mRAdjust;
    private boolean mRandomInitialize;
    
    private float mTimeUntilDeath;
    private Type mSpawnOnDeathType;
	
//    private HitReactionComponent mHitReaction;
	
	private float mCollisionResponse;
	private boolean mHitResult;
	private boolean mFreezeStart;
	
	private float mEmpSlowFactor;
	
	private float mYSpringStartTime;
	private float mYSpringHeight;
	private boolean mYSpringPositive;
	
//	private boolean mHitPlayed;
//	private boolean mBouncePlayed;
//	private boolean mFallPlayed;
////	private boolean mDeathPlayed;
	
//    private Sound mEnemySound;
    private Sound mBounceSound;
    private Sound mHitSound;
    private Sound mFallSound;
    private Sound mDeathSound;
	
	public EnemyBottomComponent() {
	    super();
	    
	    setPhase(ComponentPhases.MOVEMENT.ordinal());
//	    setPhase(ComponentPhases.THINK.ordinal());
	    reset();
	}
	
	@Override
	public void reset() {
	//    DebugLog.d("EnemyBottomComponent", "reset()");

//	    mState = CurrentState.MOVE;
	    
        mTimer = 0.0f;
        mLastRandomMoveTime = 0.0f;
        mNextRandomCheckTime = 0.0f;
        mRAdjust = 0;
        
        mRandomInitialize = false;
        
        mTempPosition = new Vector3();

//	    mHitReaction = null;
	    
	    mCollisionResponse = COLLISION_RESPONSE_VELOCITY;
	    mHitResult = false;
	    
//		mHitPlayed = false;
//		mBouncePlayed = false;
//		mFallPlayed = false;
	    
	    mEmpSlowFactor = 1.0f;
	    
        // Random Start Time for mYSpringHeight for different Enemies so doesn't look like clockwork
	    float gameTime = sSystemRegistry.timeSystem.getGameTime();
        Random rStart = new Random();
        mYSpringStartTime = gameTime + rStart.nextFloat();	// Random Start Time between 0.0f to 1.0f
        
        mYSpringPositive = true;
        
	    mBounceSound = null;
	    mHitSound = null;
	    mFallSound = null;
	    mDeathSound = null;
	}
	
	//@Override
	public void update(float timeDelta, BaseObject parent) {		
		if(!GameParameters.gamePause) {
		    TimeSystem time = sSystemRegistry.timeSystem;
		    GameObject parentObject = (GameObject)parent;
		    
		    final float gameTime = time.getGameTime();
		    
		    if (!mRandomInitialize) {
		    	randomInitialize(parentObject);
		    	mRandomInitialize = true;
		    }
		    
		    CurrentState droidCurrentState = sSystemRegistry.gameObjectManager.droidBottomGameObject.currentState;
//	    	float droidY = sSystemRegistry.gameObjectManager.droidBottomGameObject.currentPosition.y;
		    
		    // If Level09 End of Game, all remaining Enemies die so that Droid and Astronauts escape
		    if (droidCurrentState == CurrentState.GAME_END) {
		    	parentObject.currentState = CurrentState.DEAD;
		    }
		    
//		    Log.i("EnemyState", "EnemyBottomComponent Enemy currentState = " + parentObject.currentState);
	  		
		    switch(parentObject.currentState) {
	      	case INTRO: 
	      		stateIntro(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case LEVEL_START: 
	      		stateLevelStart(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case MOVE: 
	      		if (droidCurrentState == CurrentState.LEVEL_START || droidCurrentState == CurrentState.ELEVATOR || 
	      				droidCurrentState == CurrentState.LEVEL_END) {
	      			stateLevelStart(gameTime, timeDelta, parentObject);
	      		} else {
	          		stateMove(gameTime, timeDelta, parentObject);
	      		}
	      		break;
	      		
	      	case BOUNCE:
	      		stateBounce(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case HIT:
	      		stateHit(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case FROZEN:
	      		if (droidCurrentState == CurrentState.ELEVATOR || droidCurrentState == CurrentState.LEVEL_END) {
	      			stateLevelStart(gameTime, timeDelta, parentObject);
	      		} else {
	          		stateFrozen(gameTime, timeDelta, parentObject);
	      		}
	      		break;
	      		
	      	case FALL:
	      		stateFall(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case DEAD:
	      		stateDead(gameTime, timeDelta, parentObject);
	      		break;
		            
		    default:
		    	stateLevelStart(gameTime, timeDelta, parentObject);
		        break;
		    }	
		}    
	}
	
//	protected void move(float time, float timeDelta, GameObject parentObject) {		
//
//	}
	
//	protected void gotoMove(GameObject parentObject) {
//		parentObject.currentState = CurrentState.MOVE;
////	    mState = CurrentState.MOVE;
//	}
	
	protected void stateIntro (float gameTime, float timeDelta, GameObject parentObject) {
//	    Type type = parentObject.type;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	    switch(parentObject.bottomMoveType) {
    	case INVALID:
    		// Ignore
    		break;
    		
    	case MOUNT:
    		// r is constant for stateIntro()
    		break;
    		
    	case SPIDER_LEGS:
    		// r is constant for stateIntro()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		break;
    		
    	case WHEELS_TREAD:
    		// r is constant for stateIntro()
    		
			if (parentObject.type == Type.ENEMY_EM_OW || parentObject.type == Type.ENEMY_HD_OW || 
				parentObject.type == Type.ENEMY_HD_TW) {
				// Enemy Wheel Type Sprint
				x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * ENEMY_WHEELS_SPRINT_MAGNITUDE;
				z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * ENEMY_WHEELS_SPRINT_MAGNITUDE;
			} else {
				x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
				z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			}
    		break;
    		
    	case SPRING_LEGS:
    		// r is constant for stateIntro()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		
    		if (gameTime > mYSpringStartTime) {
        		if (mYSpringHeight >= SPRING_HEIGHT_MAX) {
        			y -= 0.01f;
        			mYSpringHeight -= 0.01f;
        			mYSpringPositive = false;
        		} else if (mYSpringHeight <= 0.01f) {	// Allow for float variance
        			y += 0.01f;
        			mYSpringHeight += 0.01f;
        			mYSpringPositive = true;
        		} else {
        			if (mYSpringPositive) {
        				y += 0.01f;
        				mYSpringHeight += 0.01f;
        			} else {
        				y -= 0.01f;
        				mYSpringHeight -= 0.01f;
        			}
        		}
        		
//        		y += mYSpringHeight;	
    		}
    		
    		break;
    		
    	case FLY:
    		// r is constant for stateIntro()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		break;
    		
    	default:
    		break;
	    }
    	
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
	}
	
    protected void stateLevelStart(float gameTime, float timeDelta, GameObject parentObject) {
//	    Type type = parentObject.type;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	    switch(parentObject.bottomMoveType) {
    	case INVALID:
    		// Ignore
    		break;
    		
    	case MOUNT:
    		// r is constant for stateLevelStart()
    		break;
    		
    	case SPIDER_LEGS:
    		// r is constant for stateLevelStart()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		break;
    		
    	case WHEELS_TREAD:
    		// r is constant for stateLevelStart()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		break;
    		
    	case SPRING_LEGS:
    		// r is constant for stateLevelStart()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		
    		if (gameTime > mYSpringStartTime) {
        		if (mYSpringHeight >= SPRING_HEIGHT_MAX) {
        			y -= 0.01f;
        			mYSpringHeight -= 0.01f;
        			mYSpringPositive = false;
        		} else if (mYSpringHeight <= 0.01f) {	// Allow for float variance
        			y += 0.01f;
        			mYSpringHeight += 0.01f;
        			mYSpringPositive = true;
        		} else {
        			if (mYSpringPositive) {
        				y += 0.01f;
        				mYSpringHeight += 0.01f;
        			} else {
        				y -= 0.01f;
        				mYSpringHeight -= 0.01f;
        			}
        		}
        		
//        		y += mYSpringHeight;	
    		}
    		
    		break;
    		
    	case FLY:
    		// r is constant for stateLevelStart()
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		break;
    		
    	default:
    		break;
	    }
    	
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
	
	protected void stateMove(float gameTime, float timeDelta, GameObject parentObject) {	
	    CameraSystem center = sSystemRegistry.cameraSystem;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
		    
	    float droidX = center.getFocusPositionX();
	    float droidZ = center.getFocusPositionZ();
	    
	    float droidY = center.getFocusPositionY();
    		
	    switch(parentObject.bottomMoveType) {
    	case INVALID:
    		// Ignore
    		break;
    		
    	case MOUNT:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
        		mLastRandomMoveTime = gameTime;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_MOUNT)) {
    			// r is constant. Enemy faces in r direction during random time.
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);	
    		}
    		
    		break;
    		
    	case SPIDER_LEGS:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    	        
        		mLastRandomMoveTime = gameTime;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_SPIDER_LEGS)) {
    			// r is constant. Enemy moves in r direction during random time.
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
    			
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		}

    		break;
    		
    	case WHEELS_TREAD:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    	        
        		mLastRandomMoveTime = gameTime;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_WHEELS_TREAD)) {
    			// r is constant. Enemy moves in r direction during random time.
    			
    			if (parentObject.type == Type.ENEMY_EM_OW || parentObject.type == Type.ENEMY_HD_OW || 
    					parentObject.type == Type.ENEMY_HD_TW) {
    				// Enemy Wheel Type Sprint
            		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * ENEMY_WHEELS_SPRINT_MAGNITUDE;
            		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * ENEMY_WHEELS_SPRINT_MAGNITUDE;
    			} else {
            		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
            		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    			}
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
    			
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		}
    		
    		break;
    		
    	case SPRING_LEGS:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {    			
    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    	        
        		mLastRandomMoveTime = gameTime;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_SPRING_LEGS)) {    			
    			// r is constant. Enemy moves in r direction during random time.
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
        		
    		} else {    
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
    			
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		}
    		
    		if (mYSpringHeight >= SPRING_HEIGHT_MAX) {
    			y -= 0.01f;
    			mYSpringHeight -= 0.01f;
    			mYSpringPositive = false;
    		} else if (mYSpringHeight <= 0.01f) {	// Allow for float variance
    			y += 0.01f;
    			mYSpringHeight += 0.01f;
    			mYSpringPositive = true;
    		} else {
    			if (mYSpringPositive) {
    				y += 0.01f;
    				mYSpringHeight += 0.01f;
    			} else {
    				y -= 0.01f;
    				mYSpringHeight -= 0.01f;
    			}
    		}
    		
    		break;
    		
    	case FLY:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {    			    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    	        
        		mLastRandomMoveTime = gameTime;
        		
    		} else {    			
    			// r is constant for FLY type
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		}
    		
    		break;
    		
    	default:
    		break;
	    }
    	
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
	}
	
  	protected void stateBounce(float gameTime, float timeDelta, GameObject parentObject) {  		
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	  	if (parentObject.bounceMagnitude > 1.0f) {  		
		  	x += parentObject.bouncePosition.x * parentObject.bounceMagnitude;
		  	z += parentObject.bouncePosition.z * parentObject.bounceMagnitude;
	  		
		  	parentObject.bounceMagnitude *= 0.5f;
//		  	parentObject.bounceMagnitude *= 0.7f;
//	  		parentObject.bounceMagnitude *= 0.2f;
		  	
      		SoundSystem sound = sSystemRegistry.soundSystem;
      		
		  	if (!parentObject.soundPlayed) {
//		  	if (!mBouncePlayed) {
		    	if (mBounceSound == null) {
		    		Log.e("Sound", "EnemyBottomComponent update() FAIL mBounceSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
		        		mBounceSound = sound.load(R.raw.sound_gameobject_bounce);
			          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "EnemyBottomComponent stateBounce() factory.context = NULL");
		        	}
		    	} else {
		          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		    	}
//		  		SoundSystem sound = sSystemRegistry.soundSystem;
//		      	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		      	parentObject.soundPlayed = true;
//		      	mBouncePlayed = true;
		  	}
			
	  	} else {
		  	x += parentObject.bouncePosition.x * parentObject.magnitude;
		  	z += parentObject.bouncePosition.z * parentObject.magnitude;
	
	  		parentObject.bounceMagnitude = 1.0f;
//	    	parentObject.invincible = false;
	    	parentObject.currentState = CurrentState.MOVE;
	    	parentObject.soundPlayed = false;
//	    	mBouncePlayed = false;
	  	}
    	
	  	parentObject.setCurrentPosition(x, y, z, r);
	  	
		parentObject.setPreviousPosition(mTempPosition);
  }
  	
  	protected void stateHit(float gameTime, float timeDelta, GameObject parentObject) {
		HudSystem hud = sSystemRegistry.hudSystem;
		hud.totalKillCollectPoints += 10;
		hud.totalKillCollectPointsDigitsChanged = true;
		
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	  	if (parentObject.bounceMagnitude > 1.0f) {  		
		  	x += parentObject.bouncePosition.x * parentObject.bounceMagnitude;
		  	z += parentObject.bouncePosition.z * parentObject.bounceMagnitude;
	  		
		  	parentObject.bounceMagnitude *= 0.5f;
//		  	parentObject.bounceMagnitude *= 0.7f;
		  	
      		SoundSystem sound = sSystemRegistry.soundSystem;
		  	
		  	if (!parentObject.soundPlayed) {
		    	if (mHitSound == null) {
		    		Log.e("Sound", "EnemyBottomComponent update() FAIL mHitSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
				    	switch(parentObject.bottomMoveType) {
				    	case SPIDER_LEGS:
				    		mHitSound = sound.load(R.raw.sound_enemy_spider_hit);
				    		break;
				    		
				    	case FLY:
				    		mHitSound = sound.load(R.raw.sound_enemy_fly_hit);
				    		break;
				    		
				    	case MOUNT:
				    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
				    		break;
				    		
				    	case WHEELS_TREAD:
				    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
				    		break;
				    		
				    	case SPRING_LEGS:
				    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
				    		break;
				    		
				    	default:
				    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
				    		break;
				    	}
//				    	switch(parentObject.type) {
//				    	case ENEMY_LC_SL:
//				    		mHitSound = sound.load(R.raw.sound_enemy_walk_hit);
//				    		break;
//				    	case ENEMY_TA_FL:
//				    		mHitSound = sound.load(R.raw.sound_enemy_fly_hit);
//				    		break;
//				    	}
			          	sound.play(mHitSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "EnemyBottomComponent stateHit() factory.context = NULL");
		        	}
		    	} else {
		          	sound.play(mHitSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		    	}
		    	
		      	parentObject.soundPlayed = true;
		  	}		
	  	} else {
		  	x += parentObject.bouncePosition.x * parentObject.magnitude;
		  	z += parentObject.bouncePosition.z * parentObject.magnitude;
	
	  		parentObject.bounceMagnitude = 1.0f;
//	    	parentObject.invincible = false;
	    	parentObject.currentState = CurrentState.MOVE;
	    	parentObject.soundPlayed = false;
//	    	mHitPlayed = false;
	  	}
    	
	  	parentObject.setCurrentPosition(x, y, z, r);
	  	
		parentObject.setPreviousPosition(mTempPosition);
  }

    protected void stateFrozen(float gameTime, float timeDelta, GameObject parentObject) {
	    Type type = parentObject.type;
	    
	    CameraSystem center = sSystemRegistry.cameraSystem;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
		    
	    float droidX = center.getFocusPositionX();
	    float droidZ = center.getFocusPositionZ();
	    
	    float droidY = center.getFocusPositionY();
		
	    // Enemy will slowly regain speed
		if (gameTime < (parentObject.lastReceivedHitTime + 2.0f)) {
			mEmpSlowFactor = 0.25f;
//			mEmpSlowFactor = 1.0f;
		} else if (gameTime < (parentObject.lastReceivedHitTime + 3.0f)) {
			mEmpSlowFactor = 0.50f;
//			mEmpSlowFactor = 2.0f;
		} else if (gameTime < (parentObject.lastReceivedHitTime + 4.0f)) {
			mEmpSlowFactor = 0.75f;
//			mEmpSlowFactor = 3.0f;
		} else {
			mEmpSlowFactor = 0.90f;
//			mEmpSlowFactor = 4.0f;
	    	parentObject.currentState = CurrentState.MOVE;
	    	parentObject.soundPlayed = false;
		}
		
	    switch(parentObject.bottomMoveType) {
    	case INVALID:
    		// Ignore
    		break;
    		
    	case MOUNT:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {
//    	        Random rStart = new Random();
    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
//    	        float rAdjust = rStart.nextInt(RANDOM_DEGREES_MOUNT);
//    	        boolean rSign = rStart.nextBoolean();
//    	        
//    	        // Random r adjust of +/- 0 to RANDOM_DEGREES_<enemy_leg_type>
//    	        if (rSign) {
//            		r += rAdjust;
//    	        } else {
//    	        	r -= rAdjust;
//    	        }
    	        
        		mLastRandomMoveTime = gameTime;
//        		mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_MOUNT;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_MOUNT)) {
    			// r is constant. Enemy faces in r direction during random time.
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
//    			if (Math.abs(droidY - y) < 0.1f) {
//        			// Enemy targets Droid position
//            		r = moveAngleCalc(x, z, droidX, droidZ);	
//    			} else {
//    				// Enemy and Droid not at same y height, therefore r is constant
//    			}
    		}
    		
    		break;
    		
    	case SPIDER_LEGS:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {
//    	        Random rStart = new Random();
    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
//    	        float rAdjust = rStart.nextInt(RANDOM_DEGREES_SPIDER_LEGS);
//    	        boolean rSign = rStart.nextBoolean();
//    	        
//    	        // Random r adjust of +/- 0 to RANDOM_DEGREES_<enemy_leg_type>
//    	        if (rSign) {
//            		r += rAdjust;
//    	        } else {
//    	        	r -= rAdjust;
//    	        }
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    	        
        		mLastRandomMoveTime = gameTime;
//        		mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_SPIDER_LEGS;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_SPIDER_LEGS)) {
    			// r is constant. Enemy moves in r direction during random time.
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
//    			if (Math.abs(droidY - y) < 0.1f) {
//        			// Enemy targets Droid position
//            		r = moveAngleCalc(x, z, droidX, droidZ);	
//    			} else {
//    				// Enemy and Droid not at same y height, therefore r is constant
//    			}
    			
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    		}

    		break;
    		
    	case WHEELS_TREAD:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {
//    	        Random rStart = new Random();
    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
//    	        float rAdjust = rStart.nextInt(RANDOM_DEGREES_WHEELS_TREAD);
//    	        boolean rSign = rStart.nextBoolean();
//    	        
//    	        // Random r adjust of +/- 0 to RANDOM_DEGREES_<enemy_leg_type>
//    	        if (rSign) {
//            		r += rAdjust;
//    	        } else {
//    	        	r -= rAdjust;
//    	        }
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    	        
        		mLastRandomMoveTime = gameTime;
//        		mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_WHEELS_TREAD;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_WHEELS_TREAD)) {
    			// r is constant. Enemy moves in r direction during random time.
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
//    			if (Math.abs(droidY - y) < 0.1f) {
//        			// Enemy targets Droid position
//            		r = moveAngleCalc(x, z, droidX, droidZ);	
//    			} else {
//    				// Enemy and Droid not at same y height, therefore r is constant
//    			}
    			
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    		}
    		
    		break;
    		
    	case SPRING_LEGS:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {
//    	        Random rStart = new Random();
    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
//    	        float rAdjust = rStart.nextInt(RANDOM_DEGREES_SPRING_LEGS);
//    	        boolean rSign = rStart.nextBoolean();
//    	        
//    	        // Random r adjust of +/- 0 to RANDOM_DEGREES_<enemy_leg_type>
//    	        if (rSign) {
//            		r += rAdjust;
//    	        } else {
//    	        	r -= rAdjust;
//    	        }
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    	        
        		mLastRandomMoveTime = gameTime;
//        		mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_SPRING_LEGS;
    	        
    		} else if (gameTime < (mLastRandomMoveTime + RANDOM_TIME_SPRING_LEGS)) {
    			// r is constant. Enemy moves in r direction during random time.
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		
    		} else {
    			// Enemy targets Droid position
        		r = moveAngleCalc(x, z, droidX, droidZ);
//    			if (Math.abs(droidY - y) < SPRING_HEIGHT_MAX) {
////    			if (Math.abs(droidY - y) < 0.1f) {
//        			// Enemy targets Droid position
//            		r = moveAngleCalc(x, z, droidX, droidZ);	
//    			} else {
//    				// Enemy and Droid not at same y height, therefore r is constant
//    			}
    			
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    		}
    		
    		if (mYSpringHeight >= SPRING_HEIGHT_MAX) {
    			y -= 0.01f;
    			mYSpringHeight -= 0.01f;
    			mYSpringPositive = false;
    		} else if (mYSpringHeight <= 0.01f) {	// Allow for float variance
    			y += 0.01f;
    			mYSpringHeight += 0.01f;
    			mYSpringPositive = true;
    		} else {
    			if (mYSpringPositive) {
    				y += 0.01f;
    				mYSpringHeight += 0.01f;
    			} else {
    				y -= 0.01f;
    				mYSpringHeight -= 0.01f;
    			}
    		}
    		
//    		y += mYSpringHeight;
    		
    		break;
    		
    	case FLY:
    		if (gameTime > (mLastRandomMoveTime + mNextRandomCheckTime)) {
//    	        Random rStart = new Random();
    			
    	        mRAdjust *= -1;
    	        r += mRAdjust;
    	        
//    	        float rAdjust = rStart.nextInt(RANDOM_DEGREES_FLY);
//    	        boolean rSign = rStart.nextBoolean();
//    	        
//    	        // Random r adjust of +/- 0 to RANDOM_DEGREES_<enemy_leg_type>
//    	        if (rSign) {
//            		r += rAdjust;
//    	        } else {
//    	        	r -= rAdjust;
//    	        }
    	        
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    	        
        		mLastRandomMoveTime = gameTime;
//        		mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_FLY;
        		
    		} else {
    			// r is constant for FLY type
        		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
        		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * mEmpSlowFactor;
    		}
    		
    		break;
    		
    	default:
    		break;
	    }
	    
	    SoundSystem sound = sSystemRegistry.soundSystem;
	  	
	  	if (!parentObject.soundPlayed) {
	    	if (mHitSound == null) {
	    		Log.e("Sound", "EnemyBottomComponent update() FAIL mHitSound = NULL [gameObjectId]" +
	    				" [" + parentObject.gameObjectId + "] ");
	    		
	        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
	        	Context context = factory.context;
	        	if (context != null) {
			    	switch(parentObject.bottomMoveType) {
			    	case SPIDER_LEGS:
			    		mHitSound = sound.load(R.raw.sound_enemy_spider_hit);
			    		break;
			    		
			    	case FLY:
			    		mHitSound = sound.load(R.raw.sound_enemy_fly_hit);
			    		break;
			    		
			    	case MOUNT:
			    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
			    		break;
			    		
			    	case WHEELS_TREAD:
			    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
			    		break;
			    		
			    	case SPRING_LEGS:
			    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
			    		break;
			    		
			    	default:
			    		mHitSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_hit);
			    		break;
			    	}
//			    	switch(parentObject.type) {
//			    	case ENEMY_LC_SL:
//			    		mHitSound = sound.load(R.raw.sound_enemy_walk_hit);
//			    		break;
//			    	case ENEMY_TA_FL:
//			    		mHitSound = sound.load(R.raw.sound_enemy_fly_hit);
//			    		break;
//			    	}
		          	sound.play(mHitSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	        	} else {
	            	Log.e("Sound", "EnemyBottomComponent stateHit() factory.context = NULL");
	        	}
	    	} else {
	          	sound.play(mHitSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	    	}
	    	
	      	parentObject.soundPlayed = true;
	  	}		
    	
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);

    }
    
    protected void stateFall(float gameTime, float timeDelta, GameObject parentObject) {
    	/* FIXME Change to Fall system where Droid, Enemy or Astronaut Falls Off Edge
    	 * then slowly Rotates and Floats Away. Shouldn't be Gravity once Fall off the Platform. */
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
		if (y >= -8.0f) {
			y -= parentObject.yMoveMagnitude;
			
			parentObject.yMoveMagnitude *= 2.0f;
			
      		SoundSystem sound = sSystemRegistry.soundSystem;
			
		  	if (!parentObject.soundPlayed) {
		    	if (mFallSound == null) {
		    		Log.e("Sound", "EnemyBottomComponent update() FAIL mFallSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
				    	switch(parentObject.bottomMoveType) {
				    	case SPIDER_LEGS:
				    		mFallSound = sound.load(R.raw.sound_enemy_fly_death);
				    		break;
				    		
				    	case FLY:
				    		mFallSound = sound.load(R.raw.sound_enemy_fly_death);
				    		break;
				    		
				    	case MOUNT:
				    		mFallSound = sound.load(R.raw.sound_enemy_fly_death);
				    		break;
				    		
				    	case WHEELS_TREAD:
				    		mFallSound = sound.load(R.raw.sound_enemy_fly_death);
				    		break;
				    		
				    	case SPRING_LEGS:
				    		mFallSound = sound.load(R.raw.sound_enemy_fly_death);
				    		break;
				    		
				    	default:
				    		mFallSound = sound.load(R.raw.sound_enemy_fly_death);
				    		break;
				    	}
//			    		mFallSound = sound.load(R.raw.sound_enemy_fall);
			          	sound.play(mFallSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "EnemyBottomComponent stateFall() factory.context = NULL");
		        	}
		    	} else {
		          	sound.play(mFallSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		    	}
		    	
		      	parentObject.soundPlayed = true;
		  	}			
		} else {
			parentObject.yMoveMagnitude = 0.0f;
//			parentObject.fallHitReact = false;
			parentObject.hitPoints = 0;
//			parentObject.invincible = false;
			parentObject.currentState = CurrentState.DEAD;
			parentObject.soundPlayed = false;
//			mFallPlayed = false;
		}
    
//			move.set(mX, mY, mZ, mR);
		
		parentObject.setCurrentPosition(x, y, z, r);
//		parentObject.setPosition(mX, mY, mZ, mR);
//			parentObject.setPosition(move);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
	
//	protected void gotoDead(float gameTime, GameObject parentObject) {
////		  DebugLog.d("Lifetime", "EnemyBottomComponent gotoDead()");
//			
//		parentObject.currentState = CurrentState.DEAD;
////	  	mState = CurrentState.DEAD;
//		mTimer = time;
//	}
		
	protected void stateDead(float gameTime, float timeDelta, GameObject parentObject) {	
		HudSystem hud = sSystemRegistry.hudSystem;
		hud.totalKillCollectPoints += 25;
		hud.totalKillCollectPointsDigitsChanged = true;
  		
//		parentObject.currentState = CurrentState.DEAD;
////		mState = CurrentState.DEAD;
		
  		SoundSystem sound = sSystemRegistry.soundSystem;
    	if (mDeathSound == null) {
    		Log.e("Sound", "EnemyBottomComponent update() FAIL mDeathSound = NULL [gameObjectId]" +
    				" [" + parentObject.gameObjectId + "] ");
    		
        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	Context context = factory.context;
        	if (context != null) {
		    	switch(parentObject.bottomMoveType) {
		    	case SPIDER_LEGS:
		    		mDeathSound = sound.load(R.raw.sound_enemy_spider_death);
		    		break;
		    		
		    	case FLY:
		    		mDeathSound = sound.load(R.raw.sound_enemy_fly_death);
		    		break;
		    		
		    	case MOUNT:
		    		mDeathSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_death);
		    		break;
		    		
		    	case WHEELS_TREAD:
		    		mDeathSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_death);
		    		break;
		    		
		    	case SPRING_LEGS:
		    		mDeathSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_death);
		    		break;
		    		
		    	default:
		    		mDeathSound = sound.load(R.raw.sound_enemy_wheels_tread_legs_mount_death);
		    		break;
		    	}
//		    	switch(parentObject.type) {
//		    	case ENEMY_LC_SL:
//	        		mDeathSound = sound.load(R.raw.sound_enemy_walk_death);
//		    		break;
//		    	case ENEMY_TA_FL:
//	        		mDeathSound = sound.load(R.raw.sound_enemy_fly_death);
//		    		break;
//		    	}
              	sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	} else {
        		Log.e("Sound", "EnemyBottomComponent stateDead() factory.context = NULL");
        	}
    	} else {
          	sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
    	}
    	
//    	parentObject.initialCurrentPosition(0.0f, 0.0f, 0.0f, 0.0f);
//	    parentObject.hitPoints = 3.0f;
//	    parentObject.invincible = false;
//		parentObject.currentState = CurrentState.MOVE;
		
		GameObjectManager manager = sSystemRegistry.gameObjectManager;
	      
		if (manager != null) {
			manager.destroy(parentObject);
		}
    	
//    	// XXX Previous Test
//    	parentObject.markedForDeath = true;
			
	  	// FIXME Is this code req, or only code below? Test timing of death sequence.
//	    if (mTimeUntilDeath > 0) {
////		    DebugLog.d("Lifetime", "LifetimeComponent update(): mTimeUntilDeath = " + mTimeUntilDeath);
//	  	    
//	          mTimeUntilDeath -= timeDelta;
//	          
////	          DebugLog.d("Lifetime", "LifetimeComponent update(): mTimeUntilDeath -= timeDelta = " + mTimeUntilDeath);
//	          
//	          if (mTimeUntilDeath <= 0) {
//	              die(parentObject);
//	              return;
//	          }
//	      }
			
	  		// FIXME RE-ENABLE
//	      if (mSpawnOnDeathType != GameObjectFactory.GameObjectType.INVALID) {
//	      	GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 0.0f, 
//	              parentObject.getPosition().z, 0.0f, parentObject.facingDirection.x < 0.0f);
////	      	GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 
////	              parentObject.getPosition().y, parentObject.facingDirection.x < 0.0f);
	//
//	      	if (object != null && manager != null) {
//	          	manager.add(object);
//	      	} 
//	  	}
			
			// FIXME RE-ENABLE
//	      if (mDeathSound != null) {
//	    		SoundSystem sound = sSystemRegistry.soundSystem;
//	    		if (sound != null) {
//	    			sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL);
//	    		}
//	    	}
			
			// FIXME (see DroidBottomComponent and original PlayerComponent code)
	}
	
	private void randomInitialize(GameObject parentObject) {
		Random rStart = new Random();
		
	    switch(parentObject.bottomMoveType) {
    	case INVALID:
    		// Ignore
    		break;
    		
    	case MOUNT:
    		mNextRandomCheckTime = RANDOM_CHECK_MOUNT + rStart.nextFloat();
//    		mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_MOUNT;
    		mRAdjust = RANDOM_DEGREES_MOUNT;
    		break;
    		
    	case SPIDER_LEGS:
        	mNextRandomCheckTime = RANDOM_CHECK_SPIDER_LEGS + rStart.nextFloat();
//        	mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_SPIDER_LEGS;
    		mRAdjust = RANDOM_DEGREES_SPIDER_LEGS;
    		break;
    		
    	case WHEELS_TREAD:
        	mNextRandomCheckTime = RANDOM_CHECK_WHEELS_TREAD + rStart.nextFloat();
//        	mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_WHEELS_TREAD;
    		mRAdjust = RANDOM_DEGREES_WHEELS_TREAD;
    		break;
    		
    	case SPRING_LEGS:
        	mNextRandomCheckTime = RANDOM_CHECK_SPRING_LEGS + rStart.nextFloat();
//        	mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_SPRING_LEGS;
    		mRAdjust = RANDOM_DEGREES_SPRING_LEGS;
    		break;
    		
    	case FLY:
        	mNextRandomCheckTime = RANDOM_CHECK_FLY + rStart.nextFloat();
//        	mNextRandomCheckTime = rStart.nextFloat() * RANDOM_CHECK_FLY;
    		mRAdjust = RANDOM_DEGREES_FLY;
    		break;
    		
    	default:
    		break;
	    }
	}
	
	/**
	 * Calculates the angle at which Enemy should be facing Droid
	 * @param enemyX
	 * @param enemyZ
	 * @param droidX
	 * @param droidZ
	 * @return float moveAngle
	 */
	private float moveAngleCalc(float enemyX, float enemyZ, float droidX, float droidZ) {
		float moveAngle = 0.0f;
		
		// Angle in Degrees
		if (droidX <= enemyX) {
			if (droidZ <= enemyZ) {
				if ((enemyZ - droidZ) == 0) {
					// Set to minimal denominator
					moveAngle = (float)Math.atan(
							(enemyX - droidX) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					moveAngle = (float)Math.atan(
							(enemyX - droidX) /
							(enemyZ - droidZ)) * ONE_EIGHTY_OVER_PI;
				}
			} else {
				if ((enemyX - droidX) == 0) {
					// Set to minimal denominator
					moveAngle = 90.0f + (float)Math.atan(
							(droidZ - enemyZ) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					moveAngle = 90.0f + (float)Math.atan(
							(droidZ - enemyZ) /
							(enemyX - droidX)) * ONE_EIGHTY_OVER_PI;
				}
			}
		} else {
			if (droidZ > enemyZ) {
				moveAngle = 180.0f + (float)Math.atan(
						(droidX - enemyX) /
						(droidZ - enemyZ)) * ONE_EIGHTY_OVER_PI;
			} else {
				moveAngle = 270.0f + (float)Math.atan(
						(enemyZ - droidZ) /
						(droidX - enemyX)) * ONE_EIGHTY_OVER_PI;
			}
		}
		return moveAngle;
//		if (droidX <= enemyX) {
//			if (droidZ <= enemyZ) {
//				if ((enemyZ - droidZ) == 0) {
//					// Set to minimal denominator
//					moveAngle = (float)Math.toDegrees(Math.atan(
//							(enemyX - droidX) /
//							0.001f));
//				} else {
//					moveAngle = (float)Math.toDegrees(Math.atan(
//							(enemyX - droidX) /
//							(enemyZ - droidZ)));
//				}
//			} else {
//				if ((enemyX - droidX) == 0) {
//					// Set to minimal denominator
//					moveAngle = 90.0f + (float)Math.toDegrees(Math.atan(
//							(droidZ - enemyZ) /
//							0.001f));
//				} else {
//					moveAngle = 90.0f + (float)Math.toDegrees(Math.atan(
//							(droidZ - enemyZ) /
//							(enemyX - droidX)));
//				}
//			}
//		} else {
//			if (droidZ > enemyZ) {
//				moveAngle = 180.0f + (float)Math.toDegrees(Math.atan(
//						(droidX - enemyX) /
//						(droidZ - enemyZ)));
//			} else {
//				moveAngle = 270.0f + (float)Math.toDegrees(Math.atan(
//						(enemyZ - droidZ) /
//						(droidX - enemyX)));
//			}
//		}
//		return moveAngle;
	}
	
//	public final void setHitReactionComponent(HitReactionComponent hitReact) {
//	    mHitReaction = hitReact;
//	}
	
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
    
//    public void setObjectToSpawnOnDeath(Type type) {
//        mSpawnOnDeathType = type;
//    }
	
//	public final void setEnemySound(Sound enemySound) {
//		mEnemySound = enemySound;
//	}
	
    public final void setBounceSound(Sound bounceSound) {    	
    	mBounceSound = bounceSound;
    }
    
    public final void setHitSound(Sound hitSound) {
    	mHitSound = hitSound;
    }
    
    public final void setFallSound(Sound fallSound) {
    	mFallSound = fallSound;
    }
    
    public final void setDeathSound(Sound deathSound) {
    	mDeathSound = deathSound;
    }
}