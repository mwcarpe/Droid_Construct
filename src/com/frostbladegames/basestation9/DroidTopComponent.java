/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;

public class DroidTopComponent extends GameComponent {
//	private float mX;
//	private float mY;
//	private float mZ;
//	private float mR;
	
	private float mLastTime;
//	private float mLastTimeDelta;
  
	private GameObject mBottomGameObject;

	public DroidTopComponent() {
		super();
      
		setPhase(ComponentPhases.MOVEMENT.ordinal());
//      setPhase(ComponentPhases.THINK.ordinal());
		reset();
	}
  
	@Override
	public void reset() {
//      DebugLog.d("DroidTopComponent", "reset()");
		
		mLastTime = 0.0f;
//		mLastTimeDelta = 0.0f;
	}
  
	//@Override
	public void update(float timeDelta, BaseObject parent) {  
    	if(!GameParameters.gamePause){
    		TimeSystem time = sSystemRegistry.timeSystem;
    		GameObject parentObject = (GameObject)parent;
          
    		final float gameTime = time.getGameTime();
    		
    		if (mBottomGameObject != null) {
    			parentObject.currentState = mBottomGameObject.initialState;
//    			parentObject.currentState = mBottomGameObject.currentState;	
    		} else {
    			parentObject.currentState = CurrentState.DEAD;
    				
    			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
    		}
    		
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
          		
//          	case PLATFORM_SECTION_END:
//          		stateSectionEnd(gameTime, timeDelta, parentObject);
//          		break;
          		
          	case DEAD:
          		stateDead(gameTime, timeDelta, parentObject);
          		break;
          		
          	case LEVEL_END:
          		stateLevelEnd(gameTime, timeDelta, parentObject);
          		break;
          		
          	default:
          		stateMove(gameTime, timeDelta, parentObject);
          		break;
    		}
    	}
	}
	
  	protected void stateIntro(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
	          	
			parentObject.setCurrentPosition(x, y, z, r);
			
		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
  	}
	
    protected void stateLevelStart(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {
//			VectorPool pool = sSystemRegistry.vectorPool;
//	      
//			if (pool != null) {
//					Vector3 fire = pool.allocate();
	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
//					mX = mBottomGameObject.position.x;
//					mY = mBottomGameObject.position.y;
//					mZ = mBottomGameObject.position.z;
//					mR = mBottomGameObject.position.r;
	              
//					fire.set(mX, mY, mZ, mR);
	          	
			parentObject.setCurrentPosition(x, y, z, r);

//			Log.i("Loop", "DroidTopComponent stateLevelStart()" + " [" + parentObject.gameObjectId + "] " +
//			  		"x,y,z,r = " + x + ", " + y + ", " + z + ", " + r);
			
		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
	
    protected void stateMove(float gameTime, float timeDelta, GameObject parentObject) {		
    	if (mBottomGameObject != null) {
//			VectorPool pool = sSystemRegistry.vectorPool;
//			InputGameInterface input = sSystemRegistry.inputGameInterface;
	      
			InputSystem inputSystem = sSystemRegistry.inputSystem;
//			final InputXY touchMove = inputSystem.getTouchMove();
//			final InputXY touchFire = inputSystem.getTouchFire();
	      
			if (inputSystem != null) {
//			if (pool != null && inputSystem != null) {
//			if (pool != null && input != null) {
//				Vector3 fire = pool.allocate();
				
				float x = mBottomGameObject.currentPosition.x;
				float y = mBottomGameObject.currentPosition.y;
				float z = mBottomGameObject.currentPosition.z;
				float r = parentObject.currentPosition.r;
//				mX = mBottomGameObject.position.x;
//				mY = mBottomGameObject.position.y;
//				mZ = mBottomGameObject.position.z;
				
				if (inputSystem.touchFirePress) {
//				if (touchFire.getPressed()) {
//				if (touchFire.getPressed() || touchMove.getPressed()) {
					
					r = inputSystem.firePosition.r;
//					mR = inputSystem.fireDirection.r;
//					final InputXY fireButton = input.getFireButton();
//					mR = fireButton.getR();
					
					mLastTime = gameTime;
//					mLastTimeDelta = timeDelta;
				} else {					
					// Wait 2 continuous seconds for no Fire, then face DroidTop towards DroidBottom direction
					if ((gameTime - mLastTime) > 2.0f) {
//					if ((timeDelta - mLastTimeDelta) > 2.0f) {
						r = mBottomGameObject.currentPosition.r;
					}
				}
	              
//				fire.set(mX, mY, mZ, mR);
          	
				parentObject.setCurrentPosition(x, y, z, r);

//				Log.i("Loop", "DroidTopComponent stateMove()" + " [" + parentObject.gameObjectId + "] " +
//				  		"x,y,z,r = " + x + ", " + y + ", " + z + ", " + r);
			}
			
		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
    
    protected void stateBounce(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {
//			VectorPool pool = sSystemRegistry.vectorPool;
//	      
//			if (pool != null) {
//					Vector3 fire = pool.allocate();
	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
//					mX = mBottomGameObject.position.x;
//					mY = mBottomGameObject.position.y;
//					mZ = mBottomGameObject.position.z;
//					mR = mBottomGameObject.position.r;
	              
//					fire.set(mX, mY, mZ, mR);
	          	
			parentObject.setCurrentPosition(x, y, z, r);

//			Log.i("Loop", "DroidTopComponent stateBounce()" + " [" + parentObject.gameObjectId + "] " +
//			  		"x,y,z,r = " + x + ", " + y + ", " + z + ", " + r);
			
		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
    
    protected void stateFrozen(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {
//			VectorPool pool = sSystemRegistry.vectorPool;
//	      
//			if (pool != null) {
//					Vector3 fire = pool.allocate();
	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
//					mX = mBottomGameObject.position.x;
//					mY = mBottomGameObject.position.y;
//					mZ = mBottomGameObject.position.z;
//					mR = mBottomGameObject.position.r;
	              
//					fire.set(mX, mY, mZ, mR);
	          	
			parentObject.setCurrentPosition(x, y, z, r);
//					parentObject.setPosition(mX, mY, mZ, mR);
//					parentObject.setPosition(fire);
	          	
//					pool.release(fire);
//			}
			
		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
    
    protected void stateFall(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {     
//			VectorPool pool = sSystemRegistry.vectorPool;
//
//			if (pool != null) {
//					Vector3 fire = pool.allocate();
					
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
//					mX = mBottomGameObject.position.x;
//					mY = mBottomGameObject.position.y;
//					mZ = mBottomGameObject.position.z;
//					mR = mBottomGameObject.position.r;
	              
//					fire.set(mX, mY, mZ, mR);
	          	
			parentObject.setCurrentPosition(x, y, z, r);
//					parentObject.setPosition(mX, mY, mZ, mR);
//					parentObject.setPosition(fire);
	          	
//					pool.release(fire);
//			}
			
    	} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
    
    protected void stateElevator(float gameTime, float timeDelta, GameObject parentObject) {		
    	if (mBottomGameObject != null) {
			InputSystem inputSystem = sSystemRegistry.inputSystem;
	      
			if (inputSystem != null) {				
				float x = mBottomGameObject.currentPosition.x;
				float y = mBottomGameObject.currentPosition.y;
				float z = mBottomGameObject.currentPosition.z;
				float r = parentObject.currentPosition.r;
				
				if (inputSystem.touchFirePress) {					
					r = inputSystem.firePosition.r;
					
					mLastTime = gameTime;
				} else {					
					// Wait 2 continuous seconds for no Fire, then face DroidTop towards DroidBottom direction
					if ((gameTime - mLastTime) > 2.0f) {
						r = mBottomGameObject.currentPosition.r;
					}
				}
          	
				parentObject.setCurrentPosition(x, y, z, r);
			}
			
		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
    
    protected void stateSectionStart(float gameTime, float timeDelta, GameObject parentObject) { 
//    protected void stateSectionEnd(float gameTime, float timeDelta, GameObject parentObject) { 
    	if (mBottomGameObject != null) {	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
	          	
			parentObject.setCurrentPosition(x, y, z, r);

		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
  
    protected void stateDead(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
	          	
			parentObject.setCurrentPosition(x, y, z, r);

		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
	}
    
    protected void stateLevelEnd(float gameTime, float timeDelta, GameObject parentObject) {
    	if (mBottomGameObject != null) {	      	
			float x = mBottomGameObject.currentPosition.x;
			float y = mBottomGameObject.currentPosition.y;
			float z = mBottomGameObject.currentPosition.z;
			float r = mBottomGameObject.currentPosition.r;
	          	
			parentObject.setCurrentPosition(x, y, z, r);

		} else {
			Log.e("DroidTopComponent", "mBottomGameObject = NULL");
		}
    }
  
	public void setBottomGameObject(GameObject bottomGameObject) {
		mBottomGameObject = bottomGameObject;
	}
}
