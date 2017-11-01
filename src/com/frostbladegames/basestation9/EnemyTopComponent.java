/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Type;

public class EnemyTopComponent extends GameComponent {
    private static final float PI_OVER_180 = 0.0174532925f;
    
//	private float mX;
//	private float mY;
//	private float mZ;
//	private float mR;
	
	private GameObject mBottomGameObject;
	
	private float mLaserOX;
	private float mLaserOY;
	private float mLaserOZ;
	
	private float mLastTime;
	
	private float mTimeCheck;
	
	private Type mObjectTypeToSpawn;
	
	public EnemyTopComponent() {
	    super();

	    setPhase(ComponentPhases.MOVEMENT.ordinal());
//	    setPhase(ComponentPhases.THINK.ordinal());
	    reset();
	}
	
	@Override
	public void reset() {
	//    DebugLog.d("EnemyTopComponent", "reset()");
		
		mLastTime = 0.0f;
		
		mTimeCheck = 5.0f;
		
	    mObjectTypeToSpawn = Type.INVALID;
	}
	
	//@Override
	public void update(float timeDelta, BaseObject parent) {
		if(!GameParameters.gamePause) {
		    TimeSystem time = sSystemRegistry.timeSystem;
		    GameObject parentObject = (GameObject)parent;
		    
		    final float gameTime = time.getGameTime();
	    	
			if (mBottomGameObject == null || mBottomGameObject.hitPoints <= 0) {
				parentObject.hitPoints = 0;
				parentObject.currentState = CurrentState.DEAD;
				stateDead(gameTime, timeDelta, parentObject);
//				return;
			} else {
				parentObject.currentState = mBottomGameObject.currentState;
			    
//	    		final float x = mBottomGameObject.currentPosition.x;
//	    		final float y = mBottomGameObject.currentPosition.y;
//	    		final float z = mBottomGameObject.currentPosition.z;
//	    		final float r = mBottomGameObject.currentPosition.r;
//				
//	    		parentObject.setCurrentPosition(x, y, z, r);
			}
			
		    CurrentState droidCurrentState = sSystemRegistry.gameObjectManager.droidBottomGameObject.currentState;
			
		    switch(parentObject.currentState) {
	      	case INTRO: 
	      		stateIntro(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case LEVEL_START: 
	      		stateLevelStart(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case MOVE: 
	      		if (droidCurrentState == CurrentState.ELEVATOR || droidCurrentState == CurrentState.LEVEL_END) {
	      			stateLevelStart(gameTime, timeDelta, parentObject);
	      		} else {
	          		stateMove(gameTime, timeDelta, parentObject);
	      		}
//	      		stateMove(gameTime, timeDelta, parentObject);
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
//	      		stateFrozen(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case FALL:
	      		stateFall(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case DEAD:
	      		stateDead(gameTime, timeDelta, parentObject);
	      		break;
		            
		    default:
	      		stateMove(gameTime, timeDelta, parentObject);
		        break;
		    }	
		}
	}
	
	protected void stateIntro (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		float enemyPositionX = x;
		float enemyPositionZ = z;
		
//		float timeCheck;
//			
//		switch(parentObject.type) {
//		case ENEMY_EM_OT:
//			timeCheck = 2.0f;
//			break;
//			
//		case ENEMY_EM_OW:
//			timeCheck = 2.0f;
//			break;
//			
//		case ENEMY_EM_SL:
//			timeCheck = 2.0f;
//			break;
//			
//		case ENEMY_LC_SL:
//			timeCheck = 3.0f;
//			break;
//		
//		default:
//			timeCheck = 2.0f;
//			break;
//		}
		
//		if (gameTime > (mLastTime + mTimeCheck)) {
////		if (gameTime > (mLastTime + timeCheck)) {
////		if (gameTime > (mLastTime + 0.25f)) {
//	    	GameObjectManager manager = sSystemRegistry.gameObjectManager;
//
//    		if (manager != null) {  		        			
//		    	float initialX = ((mLaserOX * (float)Math.cos(r * PI_OVER_180)) +
//		    			(mLaserOZ * (float)Math.sin(r * PI_OVER_180))) + enemyPositionX;
//		    	float initialZ = ((mLaserOX * (-(float)Math.sin(r * PI_OVER_180))) +
//		    			(mLaserOZ * (float)Math.cos(r * PI_OVER_180))) + enemyPositionZ;
//    			
//    			manager.activateLaserGameObject(mObjectTypeToSpawn, initialX, y, initialZ, r);
//    			
//    		} else {
//				Log.e("Object", "GameObjectManager = NULL");
//    		}
//    		
//			mLastTime = gameTime;
//		}
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
	protected void stateLevelStart (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
	protected void stateMove (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		float enemyPositionX = x;
		float enemyPositionZ = z;
		
//		float timeCheck;
//			
//		switch(parentObject.type) {
//		case ENEMY_EM_OT:
//			timeCheck = 3.0f;
//			break;
//			
//		case ENEMY_EM_OW:
//			timeCheck = 3.0f;
//			break;
//			
//		case ENEMY_EM_SL:
//			timeCheck = 3.0f;
//			break;
//			
//		case ENEMY_LC_FM:
//			timeCheck = 2.0f;
//			break;
//			
//		case ENEMY_LC_OT:
//			timeCheck = 2.0f;
//			break;
//			
//		case ENEMY_LC_SL:
//			timeCheck = 2.0f;
//			break;
//			
//		case ENEMY_LC_TT:
//			timeCheck = 2.0f;
//			break;
//		
//		default:
//			timeCheck = 2.0f;
//			break;
//		}
		
		if (gameTime > (mLastTime + mTimeCheck)) {
//		if (gameTime > (mLastTime + timeCheck)) {
//		if (gameTime > (mLastTime + 0.25f)) {
	    	GameObjectManager manager = sSystemRegistry.gameObjectManager;

    		if (manager != null) {  		        			
		    	float initialX = ((mLaserOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mLaserOZ * (float)Math.sin(r * PI_OVER_180))) + enemyPositionX;
		    	float initialZ = ((mLaserOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mLaserOZ * (float)Math.cos(r * PI_OVER_180))) + enemyPositionZ;
    			
    			manager.activateLaserGameObject(mObjectTypeToSpawn, initialX, y, initialZ, r);
    			
    		} else {
				Log.e("Object", "GameObjectManager = NULL");
    		}
    		
			mLastTime = gameTime;
		}
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
	protected void stateBounce (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
	protected void stateHit (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
	protected void stateFrozen (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
	protected void stateFall (float gameTime, float timeDelta, GameObject parentObject) {
		final float x = mBottomGameObject.currentPosition.x;
		final float y = mBottomGameObject.currentPosition.y;
		final float z = mBottomGameObject.currentPosition.z;
		final float r = mBottomGameObject.currentPosition.r;
		
		parentObject.setCurrentPosition(x, y, z, r);
	}
	
    protected void stateDead(float time, float timeDelta, GameObject parentObject) {	  	
		GameObjectManager manager = sSystemRegistry.gameObjectManager;
	      
		if (manager != null) {
			manager.destroy(parentObject);
		}
	}
  
	public void setBottomGameObject(GameObject bottomGameObject) {
		mBottomGameObject = bottomGameObject;
	}
	
    public void setEnemyLaserOffset(Vector3 laserOffset) {
    	mLaserOX = laserOffset.x;
    	mLaserOY = laserOffset.y;
    	mLaserOZ = laserOffset.z;
    }
  
	public void setObjectTypeToSpawn(Type objectTypeToSpawn) {
		mObjectTypeToSpawn = objectTypeToSpawn;
		
		switch(objectTypeToSpawn) {
		case ENEMY_LASER_STD:
			mTimeCheck = 5.0f;
			break;
			
		case ENEMY_LASER_EMP:
			mTimeCheck = 7.0f;
			break;
			
		case ENEMY_BOSS_LASER_STD:
			mTimeCheck = 3.0f;
			break;
			
		case ENEMY_BOSS_LASER_EMP:
			mTimeCheck = 4.0f;
			break;
			
		case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
			mTimeCheck = 3.0f;
			break;
		
		default:
			mTimeCheck = 5.0f;
			break;
		}
	}
}