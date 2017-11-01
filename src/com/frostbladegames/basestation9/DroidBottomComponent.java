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

import android.content.Context;
import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.SoundSystem.Sound;
import com.frostbladegames.basestation9.R;
//import com.frostbladegames.droidconstruct.GameObjectGroups.Group;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;
//import javax.microedition.khronos.opengles.GL10;
//import android.content.Context;
//import android.media.MediaPlayer;
//import com.frostbladegames.droidconstruct.RenderComponent;

public class DroidBottomComponent extends GameComponent {
    // FIXME DROID_MOVE_SPEED TEMP ONLY. Make sure value is same as InputGameInterface value. Replace with variable speed system.
//    private static final float DROID_MOVE_SPEED = 0.1f;
    private static final float PI_OVER_180 = 0.0174532925f;
    
    private static final float SECTION_TIME_WAIT = 0.5f;
    private static final float SECTION_TIME_END = 0.5f;
//    private static final float LEVEL_END_TIME_WAIT = 4.0f;
    private static final float LEVEL_END_TIME_WAIT = 3.0f;
//    private static final float DEAD_TIME_WAIT = 4.0f;
    private static final float DEAD_TIME_WAIT = 3.0f;
    private static final float GAME_END_TIME_WAIT = 10.0f;
    
    private static final float GROUND_IMPULSE_SPEED = 5000.0f;
    private static final float AIR_HORIZONTAL_IMPULSE_SPEED = 4000.0f;
    private static final float AIR_VERTICAL_IMPULSE_SPEED = 1200.0f;
    private static final float AIR_VERTICAL_IMPULSE_SPEED_FROM_GROUND = 250.0f;
    private static final float AIR_DRAG_SPEED = 4000.0f;
    private static final float MAX_GROUND_HORIZONTAL_SPEED = 500.0f;
    private static final float MAX_AIR_HORIZONTAL_SPEED = 150.0f;
    private static final float MAX_UPWARD_SPEED = 250.0f;
    private static final float VERTICAL_IMPULSE_TOLERANCE = 50.0f;
    private static final float FUEL_AMOUNT = 1.0f;
    private static final float FUEL_AIR_REFILL_SPEED = 0.15f;
    private static final float FUEL_GROUND_REFILL_SPEED = 2.0f;
    private static final float JUMP_TO_JETS_DELAY = 0.5f;
    
    private static final float STOMP_VELOCITY = -1000.0f;
    private static final float STOMP_DELAY_TIME = 0.15f;
    private static final float STOMP_AIR_HANG_TIME = 0.0f; //0.25f;
    private static final float STOMP_SHAKE_MAGNITUDE = 15.0f;
    private static final float STOMP_VIBRATE_TIME = 0.05f;
    private static final float HIT_REACT_TIME = 0.5f;

    private static final float GHOST_REACTIVATION_DELAY = 0.3f;
    private static final float GHOST_CHARGE_TIME = 0.75f;
    
//    public static final int MAX_DROID_LIFE = 3;
////    public static final float MAX_DROID_LIFE = 3.0f;
    private static final int MAX_GEMS_PER_LEVEL = 3;
    private static final int COINS_PER_POWERUP = 20;
    
    private static final float NO_GEMS_GHOST_TIME = 3.0f;
    private static final float ONE_GEM_GHOST_TIME = 8.0f;
    private static final float TWO_GEMS_GHOST_TIME = 0.0f; // no limit.
    
    public static final float GLOW_DURATION = 15.0f;
    
    // DDA boosts
    private static final int DDA_STAGE_1_ATTEMPTS = 3;
    private static final int DDA_STAGE_2_ATTEMPTS = 8;
    private static final int DDA_STAGE_1_LIFE_BOOST = 1;
    private static final int DDA_STAGE_2_LIFE_BOOST = 2;
    private static final float FUEL_AIR_REFILL_SPEED_DDA1 = 0.22f;
    private static final float FUEL_AIR_REFILL_SPEED_DDA2 = 0.30f;
    
//    public enum State {
//        MOVE,
//        STOMP,
//        HIT_REACT,
//        DEAD,
//        WIN,
//        FROZEN,
//        POST_GHOST_DELAY
//    }
    
//    private CurrentState mState;
////    private State mState;
    
    private Vector3 mTempPosition;
    
//    private float mXMoveOnElevator;
//    private float mZMoveOnElevator;
    
//    private Vector3 mNextSectionPosition;
    
//    private float mX;
//    private float mY;
//    private float mZ;
//    private float mR;
    
    private boolean mPowerupPlayed;
    private boolean mSectionState;
    private boolean mLevelEndState;
    private boolean mDeadState;
    
    private float mTimeUntilDeath;
    private Type mSpawnOnDeathType;
    
	private float mEmpSlowFactor;
    
//    private Sound mSoundLaser;
//    private int mSoundLaserStream;
//	private Sound mSoundEmp;
//	private int mSoundEmpStream;
	
//	private boolean mHitPlayed;
//	private boolean mBouncePlayed;
//	private boolean mFallPlayed;
////	private boolean mDeathPlayed;
	
    private Sound mPowerupSound;
    private Sound mHitSound;
    private Sound mBounceSound;
    private Sound mFallSound;
    private Sound mDeathSound;
    private Sound mLevelEndSound;
    private Sound mGameEndSound;
    
//    private boolean mTouchingGround;
    
//    private float mTimer;
//    private float mTimerIntro;
//    private float mLastTime;
//    private boolean mChangeViewIntro;
//    private boolean mSpotlightIntro;
////    private float mTimer2;

    // TODO Re-enable - Inventory
//    private InventoryComponent mInventory;
    private Vector3 mHotSpotTestPoint;
    // TODO Re-enable - ChangeComponentsComponent
//    private ChangeComponentsComponent mInvincibleSwap;
    private float mInvincibleEndTime;
    private float mSectionTime;
    private float mLevelEndTime;
    private float mDeathTime;
//    private HitReactionComponent mHitReaction;
//    private float mFuelAirRefillSpeed;
    
//    private boolean mLevelEnd;
    
    // FIXME TEMP. DELETE.
    private float mTouchCounterTimer;
    
    // Variables recorded for animation decisions.
//    private boolean mRocketsOn;
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    // TODO Create NPCComponent class and add DroidObject mEnemyBottom, etc arrays
//    private DroidObject mPlayerBottom;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */

    public DroidBottomComponent() {
        super();
        mHotSpotTestPoint = new Vector3();
        
        setPhase(ComponentPhases.MOVEMENT.ordinal());
//      setPhase(ComponentPhases.THINK.ordinal());
        
        reset();
        
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        mPlayerBottom = new DroidObject();
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }
    
    @Override
    public void reset() {
//        mTimer = 0.0f;
//        mTimerIntro = 0.0f;
//        mLastTime = 0.0f;
//        mChangeViewIntro = false;
//        mSpotlightIntro = false;

//        // TODO Re-enable - Inventory
////        mInventory = null;
        
        mTempPosition = new Vector3();
        
//        mXMoveOnElevator = 0.0f;
//        mZMoveOnElevator = 0.0f;
        
//        mNextSectionPosition = new Vector3();
        
        mEmpSlowFactor = 1.0f;
        
        mPowerupPlayed = false;
        mSectionState = false;
        mLevelEndState = false;
        mDeadState = false;
        
        mHotSpotTestPoint.zero();

        mInvincibleEndTime = 0.0f;
        
        mSectionTime = 0.0f;
        mLevelEndTime = 0.0f;
        mDeathTime = 0.0f;

//        mSoundLaser = null;
//        mSoundLaserStream = -1;
//        mSoundEmp = null;
//        mSoundEmpStream = -1;
        
//		mHitPlayed = false;
//		mBouncePlayed = false;
//		mFallPlayed = false;
        
        mPowerupSound = null;
	    mHitSound = null;
	    mBounceSound = null;
	    mFallSound = null;
	    mDeathSound = null;
	    mLevelEndSound = null;
	    mGameEndSound = null;
	    
//	    mLevelEnd = false;
    }
    
    //@Override
    public void update(float timeDelta, BaseObject parent) {
    	if(!GameParameters.gamePause){
          	TimeSystem time = sSystemRegistry.timeSystem;
          	GameObject parentObject = (GameObject)parent;
          	
      		parentObject.initialState = parentObject.currentState;
          
      		// FIXME Change this and all GameComponent calls to one final float gameTime = sSystemRegistry.timeSystem.getGameTime()
      		final float gameTime = time.getGameTime();
      		
        	GameParameters.droidBottomComponentCounter++;
//        	if (GameParameters.debug) {
//            	GameParameters.droidBottomComponentCounter++;
//            	if (gameTime > (mTouchCounterTimer + 10.0f)) {
//            		Log.i("TouchCounter", "constructTouch, gameTouch, touchDownMove, touchUp, objectManager, render = " +
//            				GameParameters.constructActivityCounter + ", " + GameParameters.gameCounter + ", " +
//            				GameParameters.gameCounterTouchDownMove + ", " + GameParameters.gameCounterTouchUp + ", " +
//            				GameParameters.droidBottomComponentCounter + ", " + GameParameters.renderCounter);
//            		
//            		mTouchCounterTimer = gameTime;
//            	}	
//        	}
        	
//        	Log.i("EnemyState", "DroidBottomComponent Droid currentState = " + parentObject.currentState);
      		
      		HudSystem hud = sSystemRegistry.hudSystem;
      		hud.droidHitPoints = parentObject.hitPoints;
          
      		switch(parentObject.currentState) {
      		case INTRO: 
      			stateIntro(gameTime, timeDelta, parentObject);
      			break;
      			
      		case LEVEL_START: 
      			stateLevelStart(gameTime, timeDelta, parentObject);
      			break;
      		
          	case MOVE: 
          		stateMove(gameTime, timeDelta, parentObject);
          		break;
          		
          	case BOUNCE:
          		stateBounce(gameTime, timeDelta, parentObject);
          		break;
          		
          	case HIT:
          		stateHit(gameTime, timeDelta, parentObject);
          		break;
          		
          	case FROZEN:
          		stateFrozen(gameTime, timeDelta, parentObject);
          		break;
          		
          	case FALL:
          		stateFall(gameTime, timeDelta, parentObject);
          		break;
          		
          	case ELEVATOR:
          		stateElevator(gameTime, timeDelta, parentObject);
          		break;
          		
          	case PLATFORM_SECTION_START:
          		stateSectionStart(gameTime, timeDelta, parentObject);
          		break;
          		
          	case PLATFORM_SECTION_END:
          		stateSectionEnd(gameTime, timeDelta, parentObject);
          		break;
          		
          	case DEAD:
          		stateDead(gameTime, timeDelta, parentObject);
          		break;
          		
          	case LEVEL_END:
          		stateLevelEnd(gameTime, timeDelta, parentObject);
          		break;
          		
          	case GAME_END:
          		stateGameEnd(gameTime, timeDelta, parentObject);
          		break;
          		
          	case WAIT_FOR_DEAD:
          		stateWaitEnd(gameTime, timeDelta, parentObject);
          		break;
          		
          	default:
          		stateMove(gameTime, timeDelta, parentObject);
          		break;
      		}
          
          // TODO Re-enable - hud.setFuelPercent(), hud.setButtonState()
//          final HudSystem hud = sSystemRegistry.hudSystem;
//          final InputGameInterface input = sSystemRegistry.inputGameInterface;
//          if (hud != null) {
//              hud.setFuelPercent(mFuel / FUEL_AMOUNT);
//              hud.setButtonState(input.getJumpButton().getPressed(), input.getAttackButton().getPressed());
//          }
    	}
  	}
    
  	protected void stateIntro(float gameTime, float timeDelta, GameObject parentObject) {
	    Type type = parentObject.type;
	    
	    float timerIntro = sSystemRegistry.timeSystem.getTimerIntro();
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
    	
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
		
  		SoundSystem sound = sSystemRegistry.soundSystem;
		
		if (!mPowerupPlayed && timerIntro > 4.0f) {
	    	if (mPowerupSound == null) {
	        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
	        	Context context = factory.context;
	        	if (context != null) {
	        		mPowerupSound = sound.load(R.raw.sound_gameobject_bounce);
		          	sound.play(mPowerupSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	        	} else {
	            	Log.e("Sound", "DroidBottomComponent stateIntro() factory.context = NULL");
	        	}
	    	} else {
	          	sound.play(mPowerupSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	    	}
			
			mPowerupPlayed = true;
		}
		
//		if (mTimerIntro > 6.0f) {
//			LevelSystem level = sSystemRegistry.levelSystem;
//			level.sendGameEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0, false);
////			level.sendNextLevelEvent();
//			
////	    	   HudSystem hud = sSystemRegistry.hudSystem;
////	    	   if (hud != null && !hud.isFading()) {
////	    		   if (elapsed > 2.0f) {
////	    			   hud.startFade(false, 1.5f);
////	    			   hud.sendGameEventOnFadeComplete(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0);
////	    		   }
////	    	   }
//		} else if (!mChangeViewIntro && mTimerIntro > 3.0f) {
//	    	CameraSystem cameraSystem = sSystemRegistry.cameraSystem;
//	    	cameraSystem.setTarget(parentObject);
//	    	cameraSystem.setFocusPosition(parentObject.currentPosition);
//	    	cameraSystem.setViewangle(-1.2f, 0.5f, 1.2f);	// y has 0.5f buffer to offset gluLookAt() default parameters
//	    	
//	    	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//	    	factory.setBackgroundMusicVolume(0.25f, 0.25f);
//	    	
//	    	mChangeViewIntro = true;
//		} else if (!mSpotlightIntro && mTimerIntro > 4.0f) {
//			GameParameters.light2Enabled = true;
//			
//			mSpotlightIntro = true;
//		}
//		
//		mTimerIntro += time - mLastTime;
//		mLastTime = time;
	}
    
    protected void stateLevelStart(float gameTime, float timeDelta, GameObject parentObject) {    	
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	    switch(GameParameters.levelRow) {
	    case 1:
		    if (y < -0.033f) {
		    	y += 0.033f;
//		    if (y < -0.05f) {
//		    	y += 0.05f;
		    } else {
		    	y = 0.0f;
		    	parentObject.currentState = CurrentState.MOVE;
		    	parentObject.previousState = CurrentState.MOVE;
		    }
		    
	    	break;
	    	
	    default:
	    	parentObject.currentState = CurrentState.MOVE;
	    	parentObject.previousState = CurrentState.MOVE;
	    	
	    	break;
	    }
	    
//	    mLevelEnd = false;
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
    	
//	    parentObject.invincible = false;
    }

  	protected void stateMove(float gameTime, float timeDelta, GameObject parentObject) {      				  
		InputSystem inputSystem = sSystemRegistry.inputSystem;

		if (inputSystem != null) {				
			if (inputSystem.touchMovePress) {
				mTempPosition.set(parentObject.currentPosition);
				
			  	final float x = parentObject.currentPosition.x - (inputSystem.movePosition.x * parentObject.magnitude);
			  	final float y = parentObject.currentPosition.y;
			  	final float z = parentObject.currentPosition.z - (inputSystem.movePosition.z * parentObject.magnitude);
			  	final float r = inputSystem.movePosition.r;
				
				parentObject.setCurrentPosition(x, y, z, r);
				
				parentObject.setPreviousPosition(mTempPosition);

			}		  	
		} else {
			Log.e("DroidBottomComponent", "InputSystem = NULL");
		}
	}
  	
  	protected void stateBounce(float gameTime, float timeDelta, GameObject parentObject) {  		
		mTempPosition.set(parentObject.currentPosition);
		    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	  	if (parentObject.bounceMagnitude > 1.0f) {	// Float variance is OK for this type of check  		
		  	x += parentObject.bouncePosition.x * parentObject.bounceMagnitude;
		  	z += parentObject.bouncePosition.z * parentObject.bounceMagnitude;
		  	
		  	parentObject.bounceMagnitude *= 0.5f;
//		  	parentObject.bounceMagnitude *= 0.7f;
		  	
      		SoundSystem sound = sSystemRegistry.soundSystem;
      		
		  	if (!parentObject.soundPlayed) {
		    	if (mBounceSound == null) {
		    		Log.e("Sound", "DroidBottomComponent update() FAIL mBounceSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
		        		mBounceSound = sound.load(R.raw.sound_gameobject_bounce);
			          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "DroidBottomComponent stateBounce() factory.context = NULL");
		        	}
		    	} else {
		          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		    	}
		      	parentObject.soundPlayed = true;
		  	}
	  	} else {
		  	x += parentObject.bouncePosition.x * parentObject.magnitude;
		  	z += parentObject.bouncePosition.z * parentObject.magnitude;

	  		parentObject.bounceMagnitude = 1.0f;
//	    	parentObject.invincible = false;
	    	parentObject.soundPlayed = false;
	    	parentObject.currentState = parentObject.previousState;
//	    	parentObject.currentState = CurrentState.MOVE;
	  	}
	  	
//	  	// Check if Bounce while on Elevator
//	  	if ((int)Math.round(parentObject.yMoveDistance) != 0) {
//			if (Math.abs(y - parentObject.yValueBeforeMove) < Math.abs(parentObject.yMoveDistance)) {
//				y += parentObject.yMoveMagnitude;
//			} else {
//				y = (float)Math.round(parentObject.yValueBeforeMove + parentObject.yMoveDistance);	// set to exact to avoid float calc errors
//				
//				parentObject.yValueBeforeMove = 0.0f;
//				parentObject.yMoveDistance = 0.0f;
//				parentObject.yMoveMagnitude = 0.0f;
//				
//				// previousState before Elevator will always be Move, Bounce or Hit, so set to Move
//				parentObject.currentState = CurrentState.MOVE;
//			}
//	  	}
		
	  	parentObject.setCurrentPosition(x, y, z, r);
	  	
		parentObject.setPreviousPosition(mTempPosition);
  }
  	
  	protected void stateHit(float gameTime, float timeDelta, GameObject parentObject) {  		
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
		    		Log.e("Sound", "DroidBottomComponent update() FAIL mHitSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
		        		mHitSound = sound.load(R.raw.sound_droid_hit);
			          	sound.play(mHitSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "DroidBottomComponent stateHit() factory.context = NULL");
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
	    	parentObject.currentState = parentObject.previousState;
//	    	parentObject.currentState = CurrentState.MOVE;
	    	parentObject.soundPlayed = false;
//	    	mHitPlayed = false;
	  	}
	  	
	  	// Check if Hit while on Elevator
	  	if ((int)Math.round(parentObject.yMoveDistance) != 0) {
			if (Math.abs(y - parentObject.yValueBeforeMove) < Math.abs(parentObject.yMoveDistance)) {
				y += parentObject.yMoveMagnitude;
			} else {
				y = (float)Math.round(parentObject.yValueBeforeMove + parentObject.yMoveDistance);	// set to exact to avoid float calc errors
				
				parentObject.yValueBeforeMove = 0.0f;
				parentObject.yMoveDistance = 0.0f;
				parentObject.yMoveMagnitude = 0.0f;
				
				// previousState before Elevator will always be Move, Bounce or Hit, so set to Move
				parentObject.currentState = CurrentState.MOVE;
			}
	  	}
		
	  	parentObject.setCurrentPosition(x, y, z, r);
	  	
		parentObject.setPreviousPosition(mTempPosition);
  }

    protected void stateFrozen(float gameTime, float timeDelta, GameObject parentObject) {
		InputSystem inputSystem = sSystemRegistry.inputSystem;

		if (inputSystem != null) {				
			if (inputSystem.touchMovePress) {
				mTempPosition.set(parentObject.currentPosition);
				
			    // Droid will slowly regain speed
				if (gameTime < (parentObject.lastReceivedHitTime + 0.5f)) {
					mEmpSlowFactor = 0.25f;
//					mEmpSlowFactor = 1.0f;
				} else if (gameTime < (parentObject.lastReceivedHitTime + 1.0f)) {
					mEmpSlowFactor = 0.50f;
//					mEmpSlowFactor = 2.0f;
				} else if (gameTime < (parentObject.lastReceivedHitTime + 1.5f)) {
					mEmpSlowFactor = 0.75f;
//					mEmpSlowFactor = 3.0f;
				} else {
					mEmpSlowFactor = 0.90f;
//					mEmpSlowFactor = 4.0f;
			    	parentObject.currentState = CurrentState.MOVE;
				}
				
			  	final float x = parentObject.currentPosition.x - (inputSystem.movePosition.x * parentObject.magnitude * mEmpSlowFactor);
			  	final float y = parentObject.currentPosition.y;
			  	final float z = parentObject.currentPosition.z - (inputSystem.movePosition.z * parentObject.magnitude * mEmpSlowFactor);
			  	final float r = inputSystem.movePosition.r;
				
				parentObject.setCurrentPosition(x, y, z, r);
				
				parentObject.setPreviousPosition(mTempPosition);

			}		  	
		} else {
			Log.e("DroidBottomComponent", "InputSystem = NULL");
		}
    }
    
    protected void stateFall(float gameTime, float timeDelta, GameObject parentObject) {
    	/* FIXME Option to add initial check for first time stateHitReact called and set mTimer = time
    	 * The subsequent loops will check time - mTimer > HIT_REACT_TIME then reset to CurrentState.MOVE */
//        // This state just waits until the timer is expired.
//        if (time - mTimer > HIT_REACT_TIME) {
//      		parentObject.currentState = CurrentState.MOVE;
////            gotoMove(parentObject);
//        }
//	}
    	
    	/* FIXME Change to Fall system where Droid, Enemy or Astronaut Falls Off Edge
    	 * then slowly Rotates and Floats Away. Shouldn't be Gravity once Fall off the Platform. */
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
		if (Math.abs(y - parentObject.yValueBeforeMove) < Math.abs(parentObject.yMoveDistance)) {
//		if ((float)Math.round(y) > ((float)Math.round(parentObject.yValueBeforeMove) + 
//				(float)Math.round(parentObject.yMoveDistance))) {
			x += parentObject.xMoveMagnitude;
			z += parentObject.zMoveMagnitude;
			
			y += parentObject.yMoveMagnitude;
			
			parentObject.xMoveMagnitude *= 1.02f;
			parentObject.zMoveMagnitude *= 1.02f;
			
			parentObject.yMoveMagnitude *= 1.05f;
//			parentObject.yMoveMagnitude *= 1.1f;
//			parentObject.yMoveMagnitude *= 1.2f;
//			parentObject.yMoveMagnitude *= 2.0f;
			
      		SoundSystem sound = sSystemRegistry.soundSystem;
			
		  	if (!parentObject.soundPlayed) {
		    	if (mFallSound == null) {
		    		Log.e("Sound", "DroidBottomComponent update() FAIL mFallSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
			    		mFallSound = sound.load(R.raw.sound_droid_fall);
			          	sound.play(mFallSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "DroidBottomComponent stateFall() factory.context = NULL");
		        	}
		    	} else {
		          	sound.play(mFallSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		    	}
		    	
		      	parentObject.soundPlayed = true;
		  	}			
		} else {
			y = (float)Math.round(parentObject.yValueBeforeMove + parentObject.yMoveDistance);	// set to exact to avoid float calc errors
			
			parentObject.xMoveMagnitude = 0.0f;
			parentObject.zMoveMagnitude = 0.0f;
			
			parentObject.yValueBeforeMove = 0.0f;
			parentObject.yMoveDistance = 0.0f;
			parentObject.yMoveMagnitude = 0.0f;
			
			parentObject.hitPoints = 0;
//			parentObject.invincible = false;
			parentObject.currentState = CurrentState.DEAD;
			parentObject.soundPlayed = false;
		}
    
//			move.set(mX, mY, mZ, mR);
		
		parentObject.setCurrentPosition(x, y, z, r);
//		parentObject.setPosition(mX, mY, mZ, mR);
//			parentObject.setPosition(move);
		
		parentObject.setPreviousPosition(mTempPosition);
		
//		parentObject.setMove(mX, mY, mZ, mR);
    }
    
    protected void stateElevator(float gameTime, float timeDelta, GameObject parentObject) {
		InputSystem inputSystem = sSystemRegistry.inputSystem;
		
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	    // Check and adjust for Bounce, otherwise Move
	  	if (parentObject.bounceMagnitude > 1.01f) {	// Check for Bounce. Allow for Float variance.  		
		  	x += parentObject.bouncePosition.x * parentObject.bounceMagnitude;
		  	z += parentObject.bouncePosition.z * parentObject.bounceMagnitude;
		  	
		  	parentObject.bounceMagnitude *= 0.7f;
		  	
      		SoundSystem sound = sSystemRegistry.soundSystem;
      		
		  	if (!parentObject.soundPlayed) {
		    	if (mBounceSound == null) {
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
		        		mBounceSound = sound.load(R.raw.sound_gameobject_bounce);
			          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "DroidBottomComponent stateBounce() factory.context = NULL");
		        	}
		    	} else {
		          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		    	}
		      	parentObject.soundPlayed = true;
		  	}
		  	
	  	} else if (inputSystem != null) {				
			if (inputSystem.touchMovePress) {
			  	x = parentObject.currentPosition.x - (inputSystem.movePosition.x * parentObject.magnitude);
			  	z = parentObject.currentPosition.z - (inputSystem.movePosition.z * parentObject.magnitude);
			  	r = inputSystem.movePosition.r;
			}
			
			// Clear Bounce in case just ended
	  		parentObject.bounceMagnitude = 1.0f;
	    	parentObject.soundPlayed = false;
		} else {
			Log.e("DroidBottomComponent", "InputSystem = NULL");
		}
	  	
//	  	// Track Droid x,z Movement while on Elevator
//	  	mXMoveOnElevator += (x - parentObject.currentPosition.x);
//	  	mZMoveOnElevator += (z - parentObject.currentPosition.z);
		
	  	// Adjust Position for Elevator Move
	    switch(GameParameters.levelRow) {
	    case 1:
	    	switch(parentObject.platformType) {
	    	case SECTION_04:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	
	    	break;
	    	
	    case 3:
	    	switch(parentObject.platformType) {
	    	case SECTION_01:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_04:
	    		// Both y and z Move. y down, then z up.
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else if (Math.round(Math.abs(z - parentObject.zValueBeforeMove)) < Math.abs(parentObject.zMoveDistance)) {
	    			z += parentObject.zMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			// No final adjust necessary for z
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float zBeforeFinalAdjust = z;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			z = parentObject.zValueBeforeMove + (parentObject.zMoveDistance + mZMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.z += (z - zBeforeFinalAdjust);
//	    			
////	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_05:	    		
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.x += parentObject.xMoveMagnitude;
	    			
	    		} else {
	    			// No final adjust necessary for x
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float xBeforeFinalAdjust = x;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			x = parentObject.xValueBeforeMove + (parentObject.xMoveDistance + mXMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.x += (x - xBeforeFinalAdjust);
//	    			
////	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_06:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_07:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	
	    	break;
	    	
	    case 5:
	    	switch(parentObject.platformType) {
	    	case SECTION_04:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_06:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_08:
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.x += parentObject.xMoveMagnitude;
	    			
	    		} else {
	    			// No final adjust necessary for x
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float xBeforeFinalAdjust = x;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			x = parentObject.xValueBeforeMove + (parentObject.xMoveDistance + mXMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.x += (x - xBeforeFinalAdjust);
//	    			
////	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	
	    	break;
	    	
	    case 6:
	    	switch(parentObject.platformType) {
	    	case SECTION_02:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_04:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_06:
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    case 7:
	    	switch(parentObject.platformType) {
	    	case SECTION_02:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_07:
	    		// Both y and z Move. y down, then z down.
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else if (Math.round(Math.abs(z - parentObject.zValueBeforeMove)) < Math.abs(parentObject.zMoveDistance)) {
	    			z += parentObject.zMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			// No final adjust necessary for z
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float zBeforeFinalAdjust = z;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			z = parentObject.zValueBeforeMove + (parentObject.zMoveDistance + mZMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.z += (z - zBeforeFinalAdjust);
//	    			
////	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    case 8:
	    	switch(parentObject.platformType) {
	    	case SECTION_03:	    		
	    		if (Math.round(Math.abs(z - parentObject.zValueBeforeMove)) < Math.abs(parentObject.zMoveDistance)) {
	    			z += parentObject.zMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			// No final adjust necessary for z
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float zBeforeFinalAdjust = z;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			z = parentObject.zValueBeforeMove + (parentObject.zMoveDistance + mZMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.z += (z - zBeforeFinalAdjust);
//	    			
////	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_05:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_06:
	    		// Both x and z Move at same time.  Control loop via x move.
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			z += parentObject.zMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.x += parentObject.xMoveMagnitude;
	    			mTempPosition.z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			// No final adjust necessary for x or z
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float xBeforeFinalAdjust = x;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			x = parentObject.xValueBeforeMove + (parentObject.xMoveDistance + mXMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.x += (x - xBeforeFinalAdjust);
//	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float zBeforeFinalAdjust = z;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			z = parentObject.zValueBeforeMove + (parentObject.zMoveDistance + mZMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.z += (z - zBeforeFinalAdjust);
//	    			
////	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
////	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    case 9:
	    	switch(parentObject.platformType) {
	    	case SECTION_03:
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
		    	// Adjust for Droid x,z Movement while on Elevator
//	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < (Math.abs(parentObject.xMoveDistance + mXMoveOnElevator))) {
//	    			x += parentObject.xMoveMagnitude;
	    			
	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
	    			mTempPosition.x += parentObject.xMoveMagnitude;
	    			
	    		} else {
	    			// No final adjust necessary for x
	    			
//	    			// Manually calculate final Elevator Move Distance
//	    			final float xBeforeFinalAdjust = x;
//	    			
//	    			// Adjust for Droid x,z Movement while on Elevator
//	    			x = parentObject.xValueBeforeMove + (parentObject.xMoveDistance + mXMoveOnElevator);	// set to avoid float calc errors
//	    			
//	    			// Also move previousPosition.x,z for relative Collision and Bounce calculations
//	    			mTempPosition.x += (x - xBeforeFinalAdjust);
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
//	    			mXMoveOnElevator = 0.0f;
//	    			mZMoveOnElevator = 0.0f;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    default:
	    	parentObject.currentState = CurrentState.MOVE;
	    	break;
	    }
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
    
    protected void stateSectionStart(float gameTime, float timeDelta, GameObject parentObject) { 
//    protected void stateSectionEnd(float gameTime, float timeDelta, GameObject parentObject) { 
//		mTempPosition.set(parentObject.currentPosition);
//	    
//	    float x = parentObject.currentPosition.x;
//	    float y = parentObject.currentPosition.y;
//	    float z = parentObject.currentPosition.z;
//	    float r = parentObject.currentPosition.r;
	    
    	if (!mSectionState) {
    		// Sound played by PlatformComponent
    		
    		SpecialEffectSystem specialEffect = sSystemRegistry.specialEffectSystem;
    		specialEffect.activateAnimationSet(Type.TELEPORT_RING, parentObject.currentPosition);
        	
        	mSectionState = true;
        	mSectionTime = gameTime;

    	} else if (gameTime < (mSectionTime + SECTION_TIME_WAIT)) {
    		// Wait
    		
    	} else if (gameTime < (mSectionTime + SECTION_TIME_WAIT + SECTION_TIME_END)) {
    		// Move Droid to Next Section Platform
    		float x = parentObject.nextSectionPosition.x;
    		float y = parentObject.nextSectionPosition.y;
    		float z = parentObject.nextSectionPosition.z;
    		float r = parentObject.nextSectionPosition.r;
    	    
    	    parentObject.setCurrentPosition(x, y, z, r);
    	    
    	    // Set Previous Position also in order not to trigger Background Wall Collision teleporting across Platforms
    	    parentObject.setPreviousPosition(x, y, z, r);
    		
    	} else {
    		mSectionState = false;
    		
        	parentObject.currentState = CurrentState.MOVE;
        	parentObject.previousState = CurrentState.MOVE;
    	}
    	
//	  	parentObject.setCurrentPosition(x, y, z, r);
//	  	
//		parentObject.setPreviousPosition(mTempPosition);
    }
    
    protected void stateSectionEnd(float gameTime, float timeDelta, GameObject parentObject) { 
//		mTempPosition.set(parentObject.currentPosition);
//	    
//	    float x = parentObject.currentPosition.x;
//	    float y = parentObject.currentPosition.y;
//	    float z = parentObject.currentPosition.z;
//	    float r = parentObject.currentPosition.r;
	    
    	if (!mSectionState) {
    		// Sound played by PlatformComponent
    		
    		SpecialEffectSystem specialEffect = sSystemRegistry.specialEffectSystem;
    		specialEffect.activateAnimationSet(Type.TELEPORT_RING, parentObject.currentPosition);
        	
        	mSectionState = true;
        	mSectionTime = gameTime;

    	} else if (gameTime < (mSectionTime + SECTION_TIME_WAIT)) {
    		// Wait
    		
    	} else if (gameTime < (mSectionTime + SECTION_TIME_WAIT + SECTION_TIME_END)) {
    		// Move Droid to Next Section Platform
    	    float x = parentObject.nextSectionPosition.x;
    	    float y = parentObject.nextSectionPosition.y;
    	    float z = parentObject.nextSectionPosition.z;
    	    float r = parentObject.nextSectionPosition.r;
    	    
    	    parentObject.setCurrentPosition(x, y, z, r);
    	    
    	    // Set Previous Position also in order not to trigger Background Wall Collision teleporting across Platforms
    	    parentObject.setPreviousPosition(x, y, z, r);
    		
    	} else {
    		mSectionState = false;
    		
        	parentObject.currentState = CurrentState.MOVE;
        	parentObject.previousState = CurrentState.MOVE;
    	}
		
//	  	parentObject.setCurrentPosition(x, y, z, r);
//	  	
//		parentObject.setPreviousPosition(mTempPosition);
    }
    
    protected void stateDead(float gameTime, float timeDelta, GameObject parentObject) {    	
    	/* FIXME Option to add initial check for first time stateDead called and set mTimer = time.
    	 * Part of legacy Replica code, so find out why mTimer set is necessary in stateDead() */
        if (GameParameters.debug) {
        	Log.i("GameFlow", "DroidBottomComponent stateDead()");	
        }
        
    	if (!mDeadState) {
            if (GameParameters.debug) {
        		Log.i("LevelEnd", "DroidBottomComponent stateDead() !mDeadState");	
            }
            
      		SoundSystem sound = sSystemRegistry.soundSystem;
        	if (mDeathSound == null) {
	    		Log.e("Sound", "DroidBottomComponent update() FAIL mDeathSound = NULL [gameObjectId]" +
	    				" [" + parentObject.gameObjectId + "] ");
	    		
            	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
            	Context context = factory.context;
            	if (context != null) {
            		mDeathSound = sound.load(R.raw.sound_droid_death);
                  	sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
            	} else {
            		Log.e("Sound", "DroidBottomComponent stateDead() factory.context = NULL");
            	}
        	} else {
              	sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	}
        	
        	mDeadState = true;
        	mDeathTime = gameTime;
        	
//        	mLevelEnd = false;
        	
//        	LevelSystem level = sSystemRegistry.levelSystem;
//        	if (level != null) {
//        		level.sendGameEvent(GameFlowEvent.EVENT_PAUSE_GAME, 0, false);
//        	}
    	} else if (gameTime < (mDeathTime + DEAD_TIME_WAIT)) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateDead() gameTime < (mDeathTime + DEAD_TIME_WAIT) therefore wait");	
            }
    		// wait
    	} else {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateDead() GameFlowEvent.EVENT_RESTART_LEVEL");	
            }
            
//    	    float x = 55.625f;
//    	    float y = 0.0f;
//    	    float z = -55.625f;
//    	    float r = 0.0f;
//    	    
//        	parentObject.initialCurrentPosition(x, y, z, r);
//        	
//    	    CameraSystem camera = sSystemRegistry.cameraSystem;
//    	    camera.setCameraPosition(x, y, z, r);
//    	    
//    	    parentObject.hitPoints = 3;
////    	    parentObject.invincible = false;
//    		parentObject.currentState = CurrentState.MOVE;
//    		
//    		mDeadState = false;
    		
        	LevelSystem level = sSystemRegistry.levelSystem;
        	if (level != null) {
        		level.sendGameEvent(GameFlowEvent.EVENT_RESTART_LEVEL, 0, true);
//        		level.sendGameEvent(GameFlowEvent.EVENT_RESTART_LEVEL, 0, false);
        	}
        	
    	    parentObject.currentState = CurrentState.WAIT_FOR_DEAD;
//        	LevelSystem level = sSystemRegistry.levelSystem;
//        	if (level != null && !mLevelEnd) {
//        		mLevelEnd = true;
//        		
////        		level.sendGameEvent(GameFlowEvent.EVENT_END_GAME, 0, false);
//        		level.sendGameEvent(GameFlowEvent.EVENT_RESTART_LEVEL, 0, false);
//        	}
    	}
    	
//	    float x = parentObject.currentPosition.x;
//	    float y = parentObject.currentPosition.y;
//	    float z = parentObject.currentPosition.z;
//	    float r = parentObject.currentPosition.r;
	    
		// FIXME TEMP. Re-start Droid at Red Pad Start (55.625, 0.0, -55.625, 0.0)

//    	parentObject.initialCurrentPosition(55.625f, 0.0f, -55.625f, 0.0f);
////	    mX = 55.625f;
//	    y = 0.0f;
//	    z -= 5.0f;
////	    mZ = -55.625f;
////	    mR = 0.0f;

	// FIXME Vector3 cameraPosition required, or just direct camera.setCameraPosition(mX, mY, mZ, mR)?
//        move.set(mX, mY, mZ, mR);
    

//	    camera.setCameraPosition(mX, mY, mZ, mR);
//	    camera.setCameraPosition(move);
    
//	    parentObject.setCurrentPosition(x, y, z, r);
////	    parentObject.setPosition(mX, mY, mZ, mR);
////		parentObject.setPosition(move);
	
//	    parentObject.setMove(mX, mY, mZ, mR);
    
//	    pool.release(move);

	    
  		/* FIXME Assume just inactivate Droid, then re-activate to re-start same level.
  		 * Only destroy when going to next/new level. Study if possible to just inactivate
  		 * between levels also and not re-read in GameObjectFactory. */ 
//      GameObjectManager manager = sSystemRegistry.gameObjectManager;
//      
//      if (manager != null) {
			// TODO Or make Inactive only to re-start level
//      	manager.destroy(parentObject);
//      }
	    
        // TODO Re-enable - Dead, HitReact, HotSpot
//	      // Watch for hit reactions or death interrupting the state machine.
//	      if (mState != State.DEAD && mState != State.WIN ) {
//	          if (parentObject.life <= 0) {
//	              gotoDead(gameTime);
//	          } else if (parentObject.getPosition().y < -parentObject.height) {
//	              // we fell off the bottom of the screen, die.
//	              parentObject.life = 0;
//	              gotoDead(gameTime);
//	          } else if (mState != State.HIT_REACT
//	          		&& parentObject.lastReceivedHitType != HitType.INVALID
//	                  && parentObject.getCurrentAction() == ActionType.HIT_REACT) {
//	              gotoHitReact(parentObject, gameTime);
//	          } else {
//	              HotSpotSystem hotSpot = sSystemRegistry.hotSpotSystem;
//	              if (hotSpot != null) {
//	                  // XXX: HACK!  Unify all this code.
//	                  if (hotSpot.getHotSpot(parentObject.getCenteredPositionX(), 
//	                          parentObject.getPosition().y + 10.0f) == HotSpotSystem.HotSpotType.DIE) {
//	                      parentObject.life = 0;
//	                      gotoDead(gameTime);
//	                  }
//	              }
//	          }
//	      }
  		
  		// FIXME Is this code req, or only code below? Test timing of death sequence.
//        if (mTimeUntilDeath > 0) {
////    	    DebugLog.d("Lifetime", "LifetimeComponent update(): mTimeUntilDeath = " + mTimeUntilDeath);
//    	    
//            mTimeUntilDeath -= timeDelta;
//            
////            DebugLog.d("Lifetime", "LifetimeComponent update(): mTimeUntilDeath -= timeDelta = " + mTimeUntilDeath);
//            
//            if (mTimeUntilDeath <= 0) {
//                die(parentObject);
//                return;
//            }
//        }
  		
  		// FIXME RE-ENABLE
//      if (mSpawnOnDeathType != GameObjectFactory.GameObjectType.INVALID) {
//      	GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 0.0f, 
//              parentObject.getPosition().z, 0.0f, parentObject.facingDirection.x < 0.0f);
////      	GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 
////              parentObject.getPosition().y, parentObject.facingDirection.x < 0.0f);
//
//      	if (object != null && manager != null) {
//          	manager.add(object);
//      	} 
//  	}
  		
  		// FIXME RE-ENABLE
//      if (mDeathSound != null) {
//    		SoundSystem sound = sSystemRegistry.soundSystem;
//    		if (sound != null) {
//    			sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL);
//    		}
//    	}
        
//        if (parentObject.currentState == CurrentState.DEAD && mTimer > 0.0f) {
//            final float elapsed = time - mTimer;
//            HudSystem hud = sSystemRegistry.hudSystem;
//            if (hud != null && !hud.isFading()) {
//                if (elapsed > 2.0f) {
//                    hud.startFade(false, 1.5f);
//                    hud.sendGameEventOnFadeComplete(GameFlowEvent.EVENT_RESTART_LEVEL, 0);
//                    // XXX Delete - Replica EventRecorder
////                    EventRecorder recorder = sSystemRegistry.eventRecorder;
////                    if (recorder != null) {
////                    	recorder.setLastDeathPosition(parentObject.getPosition());
////                    }
//                }
//            }
//        }
	    
    }
    
    protected void stateLevelEnd(float gameTime, float timeDelta, GameObject parentObject) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "DroidBottomComponent stateLevelEnd()");	
        }
    	
    	if (!mLevelEndState) {
            if (GameParameters.debug) {
        		Log.i("LevelEnd", "DroidBottomComponent stateLevelEnd() !mLevelEndState");	
            }
    		
    		// Sound played by PlatformComponent
    		
    		SpecialEffectSystem specialEffect = sSystemRegistry.specialEffectSystem;
    		specialEffect.activateAnimationSet(Type.TELEPORT_RING, parentObject.currentPosition);
        	
        	mLevelEndState = true;
        	mLevelEndTime = gameTime;
        	
//        	mLevelEnd = false;

        // FIXME Hack. Fix this timing issue.
    	} else if (gameTime < (mLevelEndTime + LEVEL_END_TIME_WAIT)) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateLevelEnd() gameTime < (mLevelEndTime + LEVEL_END_TIME_WAIT) therefore wait");	
            }
    		
    	} else {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateLevelEnd() GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL");	
            }
    		
    	    parentObject.hitPoints = 3;
    		
        	LevelSystem level = sSystemRegistry.levelSystem;
        	if (level != null) {
        		level.sendGameEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0, true);
//        		level.sendGameEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0, false);
        	}
        	
    	    parentObject.currentState = CurrentState.WAIT_FOR_DEAD;
//        	LevelSystem level = sSystemRegistry.levelSystem;
//        	if (level != null && !mLevelEnd) {
//        		mLevelEnd = true;
//        		
//        		level.sendGameEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0, false);
////        		level.sendGameEvent(GameFlowEvent.EVENT_END_GAME, 0, false);
////        		level.sendGameEvent(GameFlowEvent.EVENT_RESTART_LEVEL, 0, false);
//        	}
    	}
    }
    
    protected void stateGameEnd(float gameTime, float timeDelta, GameObject parentObject) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "DroidBottomComponent stateGameEnd()");	
        }
    	
    	if (!mLevelEndState) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateGameEnd() !mLevelEndState");	
            }
    		
      		SoundSystem sound = sSystemRegistry.soundSystem;
        	if (mGameEndSound == null) {
	    		Log.e("Sound", "DroidBottomComponent update() FAIL mGameEndSound = NULL [gameObjectId]" +
	    				" [" + parentObject.gameObjectId + "] ");
	    		
            	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
            	Context context = factory.context;
            	if (context != null) {
            		mGameEndSound = sound.load(R.raw.sound_astronaut_collect);
                  	sound.play(mGameEndSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
            	} else {
            		Log.e("Sound", "DroidBottomComponent stateGameEnd() factory.context = NULL");
            	}
        	} else {
              	sound.play(mGameEndSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	}
        	
        	parentObject.setCurrentPosition(-48.75f, 10.0f, -155.0f, 0.0f);
        	parentObject.setPreviousPosition(-48.75f, 10.0f, -155.0f, 0.0f);
        	
        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	factory.finalLevel09View();
        	
        	mLevelEndState = true;
        	mLevelEndTime = gameTime;
        	
//        	mLevelEnd = false;

    	} else if (gameTime < (mLevelEndTime + GAME_END_TIME_WAIT)) {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateGameEnd() gameTime < (mGameEndTime + 10.0f) therefore wait");	
            }
    		
    	} else {
            if (GameParameters.debug) {
        		Log.i("GameFlow", "DroidBottomComponent stateGameEnd() GameFlowEvent.EVENT_END_GAME");	
            }
    		
    	    parentObject.hitPoints = 3;
    	    
        	LevelSystem level = sSystemRegistry.levelSystem;
        	if (level != null) {
        		level.sendGameEvent(GameFlowEvent.EVENT_END_GAME, 0, true);
//        		level.sendGameEvent(GameFlowEvent.EVENT_END_GAME, 0, false);
        	}
        	
    	    parentObject.currentState = CurrentState.WAIT_FOR_DEAD;
//        	LevelSystem level = sSystemRegistry.levelSystem;
//        	if (level != null && !mLevelEnd) {
//        		mLevelEnd = true;
//        		
//        		level.sendGameEvent(GameFlowEvent.EVENT_END_GAME, 0, false);
//        	}
    	}
    }
    
    protected void stateWaitEnd(float gameTime, float timeDelta, GameObject parentObject) {
        if (GameParameters.debug) {
        	Log.i("GameFlow", "DroidBottomComponent stateWaitEnd()");	
        }
    	
        // Wait until Game Play end
    }
    
    // TODO Re-enable - Inventory
//    public final void setInventory(InventoryComponent inventory) {
//        mInventory = inventory;
//    }

//    public final void setHitReactionComponent(HitReactionComponent hitReact) {
//        mHitReaction = hitReact;
//    }
    
    // TODO Re-enable - adjustDifficulty
//    public final void adjustDifficulty(GameObject parent, int levelAttemps ) {
//    	// Super basic DDA.
//    	// If we've tried this levels several times secretly increase our
//        // hit points so the level gets easier.
//    	// Also make fuel refill faster in the air after we've died too many times.
//    	if (levelAttemps >= DDA_STAGE_1_ATTEMPTS) {
//            if (levelAttemps >= DDA_STAGE_2_ATTEMPTS) {
//            	parent.life += DDA_STAGE_2_LIFE_BOOST;
//            	mFuelAirRefillSpeed = FUEL_AIR_REFILL_SPEED_DDA2;
//            } else {
//            	parent.life += DDA_STAGE_1_LIFE_BOOST;
//            	mFuelAirRefillSpeed = FUEL_AIR_REFILL_SPEED_DDA1;
//            }
//        }	
//    }
    
//    public void setNextSectionPosition(Vector3 nextSectionPosition) {
//    	float x = nextSectionPosition.x;
//    	float y = nextSectionPosition.y;
//    	float z = nextSectionPosition.z;
//    	
//    	mNextSectionPosition.set(x, y, z);
//    }
//    
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
//    
//    public void setObjectToSpawnOnDeath(Type type) {
//        mSpawnOnDeathType = type;
//    }

//    public void setFireSounds(Sound laser, Sound emp) {
//    	mSoundLaser = laser;
//    	mSoundEmp = emp;
//    }
    
    public final void setPowerupSound(Sound powerupSound) {
    	mPowerupSound = powerupSound;
    }
    
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
    
    public final void setGameEndSound(Sound gameEndSound) {
    	mGameEndSound = gameEndSound;
    }
    
//    public final void setLevelEndSound(Sound levelEndSound) {
//    	mLevelEndSound = levelEndSound;
//    }
}
