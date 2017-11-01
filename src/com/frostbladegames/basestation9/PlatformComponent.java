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
import com.frostbladegames.basestation9.R;

public class PlatformComponent extends GameComponent {
	
    private Vector3 mTempPosition;
    
//    private Vector3 mNextSectionPosition;
    
//    // Test whether Elevator at Startpoint or Endpoint
//    private boolean mElevatorStartpoint;
    
    private float mTimer;
    
    private float mTimeUntilDeath;
    private Type mSpawnOnDeathType;
	
	private float mCollisionResponse;
	private boolean mHitResult;
	
//	private boolean mPlatformCollision01Enabled;
//	private boolean mPlatformCollision02Enabled;
////	private boolean mElevatorCollision01Enabled;
////	private boolean mElevatorCollision02Enabled;
	
	private Sound mPlatformSound;
//	private Sound mElevatorSound;
//	private Sound mTeleportSound;
    
	public PlatformComponent() {
	    super();
	    
	    setPhase(ComponentPhases.MOVEMENT.ordinal());
	    reset();
	}
	
	@Override
	public void reset() {	            
        mTempPosition = new Vector3();
        
//        mNextSectionPosition = new Vector3();
        
        mTimer = 0.0f;
	    
//	    mCollisionResponse = COLLISION_RESPONSE_VELOCITY;
	    mHitResult = false;
	    
//	    mPlatformCollision01Enabled = false;
//	    mPlatformCollision02Enabled = false;
//	    mElevatorCollision01Enabled = false;
//	    mElevatorCollision02Enabled = false;
	    
	    mPlatformSound = null;
	    
//	    mElevatorSound = null;
//	    mTeleportSound = null;
	}

	//@Override
	public void update(float timeDelta, BaseObject parent) {
		if(!GameParameters.gamePause) {
		    TimeSystem time = sSystemRegistry.timeSystem;
		    GameObject parentObject = (GameObject)parent;
		    
		    final float gameTime = time.getGameTime();
		    
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
	      		
	      	case PLATFORM_SECTION_START:
	      		statePlatformSectionStart(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case PLATFORM_SECTION_END:
	      		statePlatformSectionEnd(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case ELEVATOR:
	      		stateElevator(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case LEVEL_END:
	      		stateLevelEnd(gameTime, timeDelta, parentObject);
	      		break;
		            
		    default:
		        break;
		    }	 
		}   
	}
	
  	protected void stateIntro(float time, float timeDelta, GameObject parentObject) {
  		// Go to stateMove() idle
  		parentObject.currentState = CurrentState.MOVE;
  	}
	
    protected void stateLevelStart(float time, float timeDelta, GameObject parentObject) {
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
			parentObject.soundPlayed = true;
			
	    	parentObject.currentState = CurrentState.MOVE;
	    	parentObject.previousState = CurrentState.MOVE;
			break;
	    }
	    
	  	if (!parentObject.soundPlayed) {
      		SoundSystem sound = sSystemRegistry.soundSystem;
      		
	    	if (mPlatformSound == null) {
	        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
	        	Context context = factory.context;
	        	if (context != null) {
		    		mPlatformSound = sound.load(R.raw.sound_platform_move);
		          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	        	} else {
	            	Log.e("Sound", "PlatformComponent stateLevelStart() factory.context = NULL");
	        	}
	    	} else {
	          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	    	}
	    	
	      	parentObject.soundPlayed = true;
	  	}
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
	
	protected void stateMove(float time, float timeDelta, GameObject parentObject) {
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
	}
    
    protected void statePlatformSectionStart(float gameTime, float timeDelta, GameObject parentObject) {		
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
//	    switch(GameParameters.levelRow) {
//	    case 2:
//	    	switch(parentObject.type) {
//	    	case SECTION_01:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//    		
//	    	case SECTION_02:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_03:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_04:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_05:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_06:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_07:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_08:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//		    default:
//		    	parentObject.soundPlayed = true;
//		    	
//				parentObject.setCurrentPosition(x, y, z, r);
//		    	
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//		    	break;
//	    	}
//	    	
//	    	break;
//	    	
//	    case 7:
//	    	switch(parentObject.type) {
//	    	case SECTION_04:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//    		
//	    	case SECTION_06:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//	    	case SECTION_08:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//		    default:
//		    	parentObject.soundPlayed = true;
//		    	
//				parentObject.setCurrentPosition(x, y, z, r);
//		    	
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//		    	break;
//	    	}
//	    	
//	    	break;
//	    	
//	    case 8:
//	    	switch(parentObject.type) {
//	    	case SECTION_07:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//		    default:
//		    	parentObject.soundPlayed = true;
//		    	
//				parentObject.setCurrentPosition(x, y, z, r);
//		    	
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//		    	break;
//	    	}
//	    	
//	    	break;
//	    	
//	    case 9:
//	    	switch(parentObject.type) {
//	    	case SECTION_05:
////	    		parentObject.setCurrentPosition(mNextSectionPosition);
//	    		
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//	    		break;
//	    		
//		    default:
//		    	parentObject.soundPlayed = true;
//		    	
//				parentObject.setCurrentPosition(x, y, z, r);
//		    	
//		    	parentObject.currentState = CurrentState.MOVE;
//		    	parentObject.previousState = CurrentState.MOVE;
//		    	break;
//	    	}
//	    	
//	    	break;
//	    	
//	    default:
//	    	parentObject.soundPlayed = true;
//	    	
//			parentObject.setCurrentPosition(x, y, z, r);
//	    	
//	    	parentObject.currentState = CurrentState.MOVE;
//	    	parentObject.previousState = CurrentState.MOVE;
//	    	
//	    	break;
//	    }
	    
//	  	if (!parentObject.soundPlayed) {
//      		SoundSystem sound = sSystemRegistry.soundSystem;
//      		
//	    	if (mPlatformSound == null) {
//	        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//	        	Context context = factory.context;
//	        	if (context != null) {
//		    		mPlatformSound = sound.load(R.raw.sound_platform_teleport);
//		          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
//	        	} else {
//	            	Log.e("Sound", "PlatformComponent statePlatformSectionEnd() factory.context = NULL");
//	        	}
//	    	} else {
//	          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
//	    	}
//	    	
//	      	parentObject.soundPlayed = true;
//	  	}
	  	
  		SoundSystem sound = sSystemRegistry.soundSystem;
  		
    	if (mPlatformSound == null) {
        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	Context context = factory.context;
        	if (context != null) {
	    		mPlatformSound = sound.load(R.raw.sound_platform_teleport);
	          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	} else {
            	Log.e("Sound", "PlatformComponent statePlatformSectionEnd() factory.context = NULL");
        	}
    	} else {
          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
    	}
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
		
    	parentObject.currentState = CurrentState.MOVE;
    	parentObject.previousState = CurrentState.MOVE;
    }
    
    protected void statePlatformSectionEnd(float gameTime, float timeDelta, GameObject parentObject) {		
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
  		SoundSystem sound = sSystemRegistry.soundSystem;
  		
    	if (mPlatformSound == null) {
        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	Context context = factory.context;
        	if (context != null) {
	    		mPlatformSound = sound.load(R.raw.sound_platform_teleport);
	          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	} else {
            	Log.e("Sound", "PlatformComponent statePlatformSectionEnd() factory.context = NULL");
        	}
    	} else {
          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
    	}
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
		
    	parentObject.currentState = CurrentState.MOVE;
    	parentObject.previousState = CurrentState.MOVE;
    }
    
    protected void stateElevator(float gameTime, float timeDelta, GameObject parentObject) {
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	  	if (!parentObject.soundPlayed) {
      		SoundSystem sound = sSystemRegistry.soundSystem;
      		
	    	if (mPlatformSound == null) {
	        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
	        	Context context = factory.context;
	        	if (context != null) {
		    		mPlatformSound = sound.load(R.raw.sound_platform_move);
		          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	        	} else {
	            	Log.e("Sound", "PlatformComponent stateElevator() factory.context = NULL");
	        	}
	    	} else {
	          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	    	}
	    	
	      	parentObject.soundPlayed = true;
	  	}
	    
	    switch(GameParameters.levelRow) {		
	    case 1:
	    	switch(parentObject.type) {
	    	case SECTION_04:	        	
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
//	    			Log.i("Elevator", "PlatformComponent stateElevator() y += yMoveMagnitude)");
	    			
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
//	    			Log.i("Elevator", "PlatformComponent stateElevator() y = yValueBeforeMove + yMoveDistance ENABLE02/03, DISABLE01)");
	    			
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	    			
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	
	    	break;
	    	
	    case 3:
	    	switch(parentObject.type) {
	    	case SECTION_01:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_04:
	    		// Both y and z Move.
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else if (Math.round(Math.abs(z - parentObject.zValueBeforeMove)) < Math.abs(parentObject.zMoveDistance)) {	    			
	    			z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_05:
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			
	    		} else {
	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_06:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_07:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	
	    	break;
	    	
	    case 5:
	    	switch(parentObject.type) {
	    	case SECTION_04:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_06:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
	    	case SECTION_08:
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			
	    		} else {
	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	
	    	break;
	    	
	    case 6:
	    	switch(parentObject.type) {
	    	case SECTION_02:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_04:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_06:
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    case 7:
	    	switch(parentObject.type) {
	    	case SECTION_02:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_07:
	    		// Both y and z Move. y down, then z down.
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else if (Math.round(Math.abs(z - parentObject.zValueBeforeMove)) < Math.abs(parentObject.zMoveDistance)) {
	    			z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    case 8:
	    	switch(parentObject.type) {
	    	case SECTION_03:	    		
	    		if (Math.round(Math.abs(z - parentObject.zValueBeforeMove)) < Math.abs(parentObject.zMoveDistance)) {
	    			z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_05:	    		
	    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {
	    			y += parentObject.yMoveMagnitude;
	    			
	    		} else {
	    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.yValueBeforeMove = 0.0f;
	    			parentObject.yMoveDistance = 0.0f;
	    			parentObject.yMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
	    	case SECTION_06:
	    		// Both x and z Move at same time.  Control loop via x move.
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			z += parentObject.zMoveMagnitude;
	    			
	    		} else {
	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
	    			z = parentObject.zValueBeforeMove + parentObject.zMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);	
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
	    			parentObject.zValueBeforeMove = 0.0f;
	    			parentObject.zMoveDistance = 0.0f;
	    			parentObject.zMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
	    			parentObject.currentState = CurrentState.MOVE;
	    		}
	    		break;
	    		
		    default:
		    	parentObject.currentState = CurrentState.MOVE;
		    	break;
	    	}
	    	break;
	    	
	    case 9:
	    	switch(parentObject.type) {
	    	case SECTION_03:	    		
	    		if (Math.round(Math.abs(x - parentObject.xValueBeforeMove)) < Math.abs(parentObject.xMoveDistance)) {
	    			x += parentObject.xMoveMagnitude;
	    			
	    		} else {
	    			x = parentObject.xValueBeforeMove + parentObject.xMoveDistance;	// set to avoid float calc errors
	    			
          			GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
          			
          			if (parentObject.elevatorStartpoint) {
          				factory.enableElevatorCollision02(parentObject);
          			} else {
          				factory.enableElevatorCollision03(parentObject);
          			}
          			
      				factory.disableElevatorCollision01(parentObject);
//          			factory.disableElevatorCollision02(parentObject);
	    			
	    			parentObject.xValueBeforeMove = 0.0f;
	    			parentObject.xMoveDistance = 0.0f;
	    			parentObject.xMoveMagnitude = 0.0f;
	    			
	    			parentObject.soundPlayed = false;
	    			
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
    
    protected void stateLevelEnd(float gameTime, float timeDelta, GameObject parentObject) {
    	if (GameParameters.debug) {
        	Log.i("LevelEnd", "PlatformComponent stateLevelEnd()");	
    	}
    	
		mTempPosition.set(parentObject.currentPosition);
    	
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
	    switch(GameParameters.levelRow) {
	    case 1:
	    	// Level End. Controlled by DroidBottomComponent.
	    	break;
	    	
	    default:
	    	// Level End. Controlled by DroidBottomComponent.
	    	break;
	    }
	    
	  	if (!parentObject.soundPlayed) {
      		SoundSystem sound = sSystemRegistry.soundSystem;
      		
	    	if (mPlatformSound == null) {
	        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
	        	Context context = factory.context;
	        	if (context != null) {
		    		mPlatformSound = sound.load(R.raw.sound_platform_teleport);
		          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	        	} else {
	            	Log.e("Sound", "PlatformComponent statePlatformLevelEnd() factory.context = NULL");
	        	}
	    	} else {
	          	sound.play(mPlatformSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
	    	}
	    	
	      	parentObject.soundPlayed = true;
	  	}
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
	
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
    
//    public void setObjectToSpawnOnDeath(Type type) {
//        mSpawnOnDeathType = type;
//    }
    
//    public Vector3 getNextSectionPosition() {
//    	return mNextSectionPosition;
//    }
//    
//    public void setNextSectionPosition(Vector3 nextSectionPosition) {
//    	float x = nextSectionPosition.x;
//    	float y = nextSectionPosition.y;
//    	float z = nextSectionPosition.z;
//    	
//    	mNextSectionPosition.set(x, y, z);
//    }
    
	public final void setPlatformSound(Sound platformSound) {
		mPlatformSound = platformSound;
	}
    
//	public final void setElevatorSound(Sound elevatorSound) {
//		mPlatformSound = elevatorSound;
//	}
//	
//	public final void setTeleportSound(Sound teleportSound) {
//		mTeleportSound = teleportSound;
//	}
}
