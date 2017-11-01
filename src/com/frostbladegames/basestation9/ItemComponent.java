/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;

import com.frostbladegames.basestation9.GameComponent.ComponentPhases;
import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.SoundSystem.Sound;

public class ItemComponent extends GameComponent {
	
    private Vector3 mTempPosition;
    
    private float mTimer;
    
    private float mTimeUntilDeath;
    private Type mSpawnOnDeathType;
	
	private float mCollisionResponse;
	private boolean mHitResult;
	
    private Sound mItemSound;
    
	public ItemComponent() {
	    super();
	    
	    setPhase(ComponentPhases.MOVEMENT.ordinal());
	    reset();
	}
	
	@Override
	public void reset() {	            
        mTempPosition = new Vector3();
        
        mTimer = 0.0f;
	    
//	    mCollisionResponse = COLLISION_RESPONSE_VELOCITY;
	    mHitResult = false;
	}

	//@Override
	public void update(float timeDelta, BaseObject parent) {
		if(!GameParameters.gamePause) {
		    TimeSystem time = sSystemRegistry.timeSystem;
		    GameObject parentObject = (GameObject)parent;
		    
		    final float gameTime = time.getGameTime();  		
		    
		    switch(parentObject.currentState) {
	      	case INTRO: 
	      		stateLevelStart(gameTime, timeDelta, parentObject);
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
	      		
	      	case FROZEN:
	      		stateFrozen(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case FALL:
	      		stateFall(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case DEAD:
	      		stateDead(gameTime, timeDelta, parentObject);
	      		break;
		            
		    default:
		        break;
		    }
		    
			// FIXME (see DroidBottomComponent and original PlayerComponent code)
		    	
		}
	}
	
    protected void stateIntro(float time, float timeDelta, GameObject parentObject) {
		mTempPosition.set(parentObject.currentPosition);
		
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
	    
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	parentObject.currentState = manager.droidBottomGameObject.currentState;
//    	parentObject.currentState = CurrentState.MOVE;
    }
	
    protected void stateLevelStart(float time, float timeDelta, GameObject parentObject) {
		mTempPosition.set(parentObject.currentPosition);
		
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
	    
    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
    	parentObject.currentState = manager.droidBottomGameObject.currentState;
//    	parentObject.currentState = CurrentState.MOVE;
    }
	
	protected void stateMove(float time, float timeDelta, GameObject parentObject) {
	    Type type = parentObject.type;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
		
//		switch(type) {
//		case CRATE_STD:
//    		
//			break;
//			
//		default:
//			break;
//		}
    	
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
		
		// FIXME (see DroidBottomComponent and original PlayerComponent code)
	}
	
  	protected void stateBounce(float time, float timeDelta, GameObject parentObject) {
  		/* FIXME Add initial check for first time stateHitReact called and set mTimer = time
  		 * The subsequent loops will check time - mTimer > HIT_REACT_TIME then reset to CurrentState.MOVE */	
  		
//	    if (sDebugLog) {
//	        DebugLog.d("AttackReceive", "ItemComponent stateBounce()" + " [" + parentObject.gameObjectId + "] " + 
//	        		" (" + parentObject.type + ") " + "parentObject.invincible, .currentState, .bounceSpeedFactor BEFORE = " + 
//	        		parentObject.invincible + ", " + parentObject.currentState + ", " + parentObject.bounceSpeedFactor);
//	    }
		
//	    VectorPool pool = sSystemRegistry.vectorPool;
//	    
//		if (pool != null) {
//			Vector3 enemyMove = pool.allocate();
  		
//  		// FIXME IS THIS CORRECT??  Back out to previousPosition, then apply bouncePosition?
//		mTempPosition.set(parentObject.previousPosition);
//	    
//	    float x = parentObject.previousPosition.x;
//	    float y = parentObject.previousPosition.y;
//	    float z = parentObject.previousPosition.z;
  		
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
//	    float r = parentObject.bouncePosition.r;
	    
//	    float bounceX = parentObject.bouncePosition.x;
//	    float bounceZ = parentObject.bouncePosition.z;
//	    float bounceR = parentObject.bouncePosition.r;
	    
	  	if (parentObject.bounceMagnitude > 1.0f) {  		
		  	x += parentObject.bouncePosition.x * parentObject.bounceMagnitude;
		  	z += parentObject.bouncePosition.z * parentObject.bounceMagnitude;
	  		
		  	parentObject.bounceMagnitude *= 0.5f;
//	  		parentObject.bounceMagnitude *= 0.2f;
			
	  	} else {
		  	x += parentObject.bouncePosition.x * parentObject.magnitude;
		  	z += parentObject.bouncePosition.z * parentObject.magnitude;
	
	  		parentObject.bounceMagnitude = 1.0f;
//	    	parentObject.invincible = false;
	    	parentObject.currentState = CurrentState.MOVE;
	  	}
		    
//	  	if (parentObject.bounceMagnitude >= 3.0f) {
////	  	if (parentObject.bounceSpeedFactor >= 3.0f) {
////	  	if (parentObject.bounceHitReact && parentObject.bounceSpeedFactor == 2.0f) {
//	  		r = bounceR;
////	  		r = r - 180.0f;
//	  		
//	  		/* FIXME Or just set x = bounceX and z = bounceZ. However, duplicate work in HitReactionComponent and here.
//	  		 * bounceX and bounceZ calculations (or bounceR calculation) may not be required. */
//	  		
//	  		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * parentObject.bounceMagnitude;
//	  		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * parentObject.bounceMagnitude;
////	  		mR = mR - 180.0f;
////	  		
////	  		mX -= (float)Math.sin(mR * PI_OVER_180) * parentObject.moveSpeed * parentObject.bounceSpeedFactor;
////	  		mZ -= (float)Math.cos(mR * PI_OVER_180) * parentObject.moveSpeed * parentObject.bounceSpeedFactor;
//	  		
//	  		parentObject.bounceMagnitude = 2.5f;
//			
//	  	} else if (parentObject.bounceMagnitude < 3.0f && parentObject.bounceMagnitude > 1.0f) {
////	  	} else if (parentObject.bounceHitReact && parentObject.bounceSpeedFactor == 1.5f) {
//	  		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * parentObject.bounceMagnitude;
//	  		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * parentObject.bounceMagnitude;
////	  		mX -= (float)Math.sin(mR * PI_OVER_180) * parentObject.moveSpeed * parentObject.bounceSpeedFactor;
////	  		mZ -= (float)Math.cos(mR * PI_OVER_180) * parentObject.moveSpeed * parentObject.bounceSpeedFactor;
//	  		
////	  		parentObject.bounceHitReact = false;
//	  		parentObject.bounceMagnitude = 1.0f;
//	    	parentObject.invincible = false;
//	    	parentObject.currentState = CurrentState.MOVE;
//	  	} else {
//	  		parentObject.bounceMagnitude = 1.0f;
//	    	parentObject.invincible = false;
//	    	parentObject.currentState = CurrentState.MOVE;
//	  	}
  	
//	    	enemyMove.set(mX, mY, mZ, mR);
////	    	enemyMove.set(mX, mY, mZ, mR, mOX, mOY, mOZ);
    	
	  	parentObject.setCurrentPosition(x, y, z, r);
//		parentObject.setPosition(mX, mY, mZ, mR);
//	    	parentObject.setPosition(enemyMove);
		
//		parentObject.setMove(mX, mY, mZ, mR);
	  	
		parentObject.setPreviousPosition(mTempPosition);
    	
//	    	pool.release(enemyMove);
	  	
//		} else {
//			DebugLog.e("ItemComponent", "VectorPool = NULL");
//		}
	  	
//	    if (sDebugLog) {
//		    if (sDebugLog) {
//		        DebugLog.d("AttackReceive", "DroidBottomComponent stateBounce()" + " [" + parentObject.gameObjectId + "] " + 
//		        		" (" + parentObject.type + ") " + "parentObject.invincible, .currentState, .bounceSpeedFactor AFTER = " + 
//		        		parentObject.invincible + ", " + parentObject.currentState + ", " + parentObject.bounceSpeedFactor);
//		    }
//	        DebugLog.d("AttackReceive", "ItemComponent stateBounce()" + " [" + parentObject.gameObjectId + "] " + 
//	        		" (" + parentObject.type + ") " + "mX,mY,mZ,mR = " + mX + ", " + mY + ", " + mZ + ", " + mR);	
//	    }
	  	
	    		// FIXME Re-study if timer based HitReact is better
	//	        // This state just waits until the timer is expired.
	//	        if (time - mTimer > HIT_REACT_TIME) {
	//	      		parentObject.currentState = CurrentState.MOVE;
	//	//            gotoMove(parentObject);
	//	        }
  }

    protected void stateFrozen(float time, float timeDelta, GameObject parentObject) {
//		DebugLog.d("DroidBottomComponent", "stateFrozen()");

// 		FIXME TEMP. Add check for timer, then change state to CurrentState.MOVE
//	    parentObject.invincible = false;
    	parentObject.currentState = CurrentState.MOVE;

//		if (parentObject.currentState == CurrentState.MOVE) {
//    		gotoMove(parentObject);
//		}
    	
//	    if (sDebugLog) {
//	        DebugLog.d("AttackReceive", "ItemComponent stateFrozen()" + " [" + parentObject.gameObjectId + "] " + 
//	        		" (" + parentObject.type + ") " + "mX,mY,mZ,mR = " + mX + ", " + mY + ", " + mZ + ", " + mR);	
//	    }
    }
    
    protected void stateFall(float time, float timeDelta, GameObject parentObject) {  		
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
			
		} else {
			parentObject.yMoveMagnitude = 0.0f;
//			parentObject.fallHitReact = false;
			parentObject.hitPoints = 0;
//			parentObject.invincible = false;
			parentObject.currentState = CurrentState.DEAD;
		}
    
//			move.set(mX, mY, mZ, mR);
		
		parentObject.setCurrentPosition(x, y, z, r);
//		parentObject.setPosition(mX, mY, mZ, mR);
//			parentObject.setPosition(move);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
		
	protected void stateDead(float time, float timeDelta, GameObject parentObject) {	
		parentObject.currentState = CurrentState.DEAD;
//		mState = CurrentState.DEAD;
			
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
		
//	    if (sDebugLog) {
//	        DebugLog.d("AttackReceive", "ItemComponent stateDead()" + " [" + parentObject.gameObjectId + "] " + 
//	        		" (" + parentObject.type + ") " + "mX,mY,mZ,mR = " + mX + ", " + mY + ", " + mZ + ", " + mR);	
//	    }
			
		// FIXME Change to Enemy Pool System
		GameObjectManager manager = sSystemRegistry.gameObjectManager;
	      
		if (manager != null) {
			manager.destroy(parentObject);
		}
			
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
	
//	/**
//	 * Calculates the angle at which Enemy should be facing Droid
//	 * @param enemyX
//	 * @param enemyZ
//	 * @param droidX
//	 * @param droidZ
//	 * @return float moveAngle
//	 */
//	private float moveAngleCalc(float enemyX, float enemyZ, float droidX, float droidZ) {
//		float moveAngle = 0.0f;
//		
//		// Angle in Degrees
//		if (droidX <= enemyX) {
//			if (droidZ <= enemyZ) {
//				if ((enemyZ - droidZ) == 0) {
//					// Set to minimal denominator
//					moveAngle = (float)Math.atan(
//							(enemyX - droidX) /
//							0.001f) * ONE_EIGHTY_OVER_PI;
//				} else {
//					moveAngle = (float)Math.atan(
//							(enemyX - droidX) /
//							(enemyZ - droidZ)) * ONE_EIGHTY_OVER_PI;
//				}
//			} else {
//				if ((enemyX - droidX) == 0) {
//					// Set to minimal denominator
//					moveAngle = 90.0f + (float)Math.atan(
//							(droidZ - enemyZ) /
//							0.001f) * ONE_EIGHTY_OVER_PI;
//				} else {
//					moveAngle = 90.0f + (float)Math.atan(
//							(droidZ - enemyZ) /
//							(enemyX - droidX)) * ONE_EIGHTY_OVER_PI;
//				}
//			}
//		} else {
//			if (droidZ > enemyZ) {
//				moveAngle = 180.0f + (float)Math.atan(
//						(droidX - enemyX) /
//						(droidZ - enemyZ)) * ONE_EIGHTY_OVER_PI;
//			} else {
//				moveAngle = 270.0f + (float)Math.atan(
//						(enemyZ - droidZ) /
//						(droidX - enemyX)) * ONE_EIGHTY_OVER_PI;
//			}
//		}
//		return moveAngle;
//	}
	
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
    
//    public void setObjectToSpawnOnDeath(Type type) {
//        mSpawnOnDeathType = type;
//    }
	
    public final void setItemSound(Sound itemSound) {
    	mItemSound = itemSound;
    }
}
