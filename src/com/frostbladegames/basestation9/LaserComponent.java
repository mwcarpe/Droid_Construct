/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

//import java.util.Random;
import android.content.Context;
import android.util.Log;

import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.SoundSystem.Sound;
import com.frostbladegames.basestation9.R;

public class LaserComponent extends GameComponent {
    private static final float PI_OVER_180 = 0.0174532925f;   
    
    private Vector3 mTempPosition;
    
//    private DynamicCollisionComponent mAreaLaserDynamicCollisionComponent;
	
    private Sound mFireSound;
    
//    private boolean mWaitForDeadState;
    private float mPreviousTime;
	
	public LaserComponent() {
	  super();
	  
	  setPhase(ComponentPhases.MOVEMENT.ordinal());
	  
	  reset();
	}
	
	@Override
	public void reset() {	
        mTempPosition = new Vector3();
        
		mFireSound = null;
		
//		mWaitForDeadState = true;
		
        mPreviousTime = 0.0f;
	}
	
	//@Override
	public void update(float timeDelta, BaseObject parent) {
		if(!GameParameters.gamePause) {
			TimeSystem time = sSystemRegistry.timeSystem;
			GameObject parentObject = (GameObject)parent;
		  
	  	  	final float gameTime = time.getGameTime();
	  	  	
			if (parentObject.currentState == CurrentState.DEAD) {
				parentObject.hitPoints = 0;
				stateDead(gameTime, timeDelta, parentObject);
				
			} else if (parentObject.currentState == CurrentState.WAIT_FOR_DEAD) {
				stateWaitForDead(gameTime, timeDelta, parentObject);
				
			} else {
				mTempPosition.set(parentObject.currentPosition);
				
			    float x = parentObject.currentPosition.x;
			    float y = parentObject.currentPosition.y;
			    float z = parentObject.currentPosition.z;
			    float r = parentObject.currentPosition.r;
			    
			    // FIXME Verify how LaserComponent and GameObjectManager work for DEAD -> UPDATE (where does MOVE fit [see stateDead()], since not used here?)
//			    if (parentObject.type == Type.DROID_LASER_GRENADE) {
//					if (Math.abs(y - parentObject.yValueBeforeMove) < Math.abs(parentObject.yMoveDistance)) {						
//							x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
//							y += parentObject.yMoveMagnitude;
//							z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
//							
//							parentObject.yMoveMagnitude *= 1.1f;
//							
//						} else {
//							y = (float)Math.round(parentObject.yValueBeforeMove + parentObject.yMoveDistance);	// set to exact to avoid float calc errors
//							
//							parentObject.yValueBeforeMove = 0.0f;
//							parentObject.yMoveDistance = 0.0f;
//							parentObject.yMoveMagnitude = 0.0f;
//							
//							parentObject.hitPoints = 0;
//							parentObject.currentState = CurrentState.DEAD;
//						}
//			    } else {
//					x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
//					z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
//			    }
			  
			    if (!parentObject.soundPlayed) {
		      		SoundSystem sound = sSystemRegistry.soundSystem;

			    	if (mFireSound == null) {
			    		Log.e("Sound", "LaserComponent update() FAIL mFireSound = NULL [gameObjectId]" +
			    				" [" + parentObject.gameObjectId + "] ");
			    		
			        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
			        	Context context = factory.context;
			        	if (context != null) {
					    	switch(parentObject.type) {
					    	case DROID_LASER_STD:
					    		mFireSound = sound.load(R.raw.sound_laser_std);
					    		break;
					    		
					    	case DROID_LASER_PULSE:
					    		mFireSound = sound.load(R.raw.sound_laser_pulse);
					    		break;
					    		
					    	case DROID_LASER_EMP:
					    		mFireSound = sound.load(R.raw.sound_laser_emp);
					    		break;
					    		
					    	case DROID_LASER_GRENADE:
					    		mFireSound = sound.load(R.raw.sound_laser_rocket);
					    		break;
					    		
					    	case DROID_LASER_ROCKET:
					    		mFireSound = sound.load(R.raw.sound_laser_rocket);
					    		break;
					    		
					    	default:
					    		mFireSound = sound.load(R.raw.sound_laser_std);
					    		break;
					    	}
					    	
				          	sound.play(mFireSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
			        	} else {
			            	Log.e("Sound", "LaserComponent update() factory.context = NULL");
			        	}
			    	} else {
//			    		Log.i("Sound", "LaserComponent update() SUCCESS mFireSound != null [gameObjectId] [soundId]" +
//			    				" [" + parentObject.gameObjectId + "] " + "[" + mFireSound.soundId + "] ");
			    		
			          	sound.play(mFireSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
			    	}
			    	
			      	parentObject.soundPlayed = true;				
				} else {
			    	switch(parentObject.type) {
			    	case DROID_LASER_STD:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case DROID_LASER_PULSE:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case DROID_LASER_EMP:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case DROID_LASER_GRENADE:					
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
						
			    		if (Math.round(Math.abs(y - parentObject.yValueBeforeMove)) < Math.abs(parentObject.yMoveDistance)) {		    			
			    			y += parentObject.yMoveMagnitude;
			    			
			    			parentObject.yMoveMagnitude *= 1.1f;
			    			
			    		} else {
			    			y = parentObject.yValueBeforeMove + parentObject.yMoveDistance;	// set to avoid float calc errors
			    			
			    			parentObject.yValueBeforeMove = 0.0f;
			    			parentObject.yMoveDistance = 0.0f;
			    			parentObject.yMoveMagnitude = 0.0f;
			    			
			    			mPreviousTime = gameTime;
			    			
			    			parentObject.laserReceiveGameObjectPosition.set(parentObject.currentPosition);
			    			
			    			parentObject.previousState = parentObject.currentState;
			    			parentObject.currentState = CurrentState.WAIT_FOR_DEAD;
			    			
//			    			if(mWaitForDeadState && gameTime > (mPreviousTime + 1.0f)) {
//			    				mPreviousTime = gameTime;
//			    				mWaitForDeadState = false;
//			    			} else if (gameTime > (mPreviousTime + 1.0f)) {
//				    			parentObject.laserReceiveGameObjectPosition.set(parentObject.currentPosition);
//				    			
//				    			parentObject.hitReactType = Type.EXPLOSION;
//				    			
//				    			parentObject.add(mAreaLaserDynamicCollisionComponent);
//				    			
//				    			parentObject.currentState = CurrentState.DEAD;
//				    			
//			    			} else {
//			    				// Wait
//			    			}
			    		}
			    		
			    		break;
			    		
			    	case DROID_LASER_ROCKET:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
						
						parentObject.laserReceiveGameObjectPosition.set(parentObject.currentPosition);
						
			    		break;
			    		
			    	default:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    	}
				}			
				
				parentObject.setCurrentPosition(x, y, z, r);
				
				parentObject.setPreviousPosition(mTempPosition);
			}
		}
	}
	
    protected void stateWaitForDead(float time, float timeDelta, GameObject parentObject) {
		mTempPosition.set(parentObject.currentPosition);
		
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
    	switch(parentObject.type) {
    	case DROID_LASER_GRENADE:
    		// If WaitForDead due to y = 0.0f then will Wait, otherwise if direct Collision will immediately go to Dead
    		if(time > (mPreviousTime + 0.2f)) {
    			parentObject.yValueBeforeMove = 0.0f;
    			parentObject.yMoveDistance = 0.0f;
    			parentObject.yMoveMagnitude = 0.0f;
    			
    			float laserX = parentObject.laserReceiveGameObjectPosition.x;
    			float laserY = parentObject.laserReceiveGameObjectPosition.y + 0.05f; // Create Bounce Effect
    			float laserZ = parentObject.laserReceiveGameObjectPosition.z;
    			float laserR = parentObject.laserReceiveGameObjectPosition.r;
    			
//    		    y += 0.05f;	// Put y back approx at Enemy Midsection
    		    
    			parentObject.laserReceiveGameObjectPosition.set(laserX, laserY, laserZ, laserR);
//    			parentObject.laserReceiveGameObjectPosition.set(x, y, z, r);
//    			parentObject.laserReceiveGameObjectPosition.set(parentObject.currentPosition);
    			
    			parentObject.hitReactType = Type.EXPLOSION_LARGE;
    			
//    			parentObject.add(mAreaLaserDynamicCollisionComponent);
    			
    			parentObject.previousState = parentObject.currentState;
    			parentObject.currentState = CurrentState.DEAD;
    			
    		} else {
    			// Wait
    			x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    			z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
    		}
    		break;
    		
    	case DROID_LASER_ROCKET:
//			parentObject.laserReceiveGameObjectPosition.set(parentObject.currentPosition);
			
			parentObject.hitReactType = Type.EXPLOSION_RING;
			
//			parentObject.add(mAreaLaserDynamicCollisionComponent);
			
			parentObject.previousState = parentObject.currentState;
			parentObject.currentState = CurrentState.DEAD;
    		break;
    		
    	default:
			parentObject.previousState = parentObject.currentState;
			parentObject.currentState = CurrentState.DEAD;
			
    		break;
    	}
		
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
    }
	
    protected void stateDead(float time, float timeDelta, GameObject parentObject) {	
		SpecialEffectSystem specialEffect = sSystemRegistry.specialEffectSystem;
  	  	
//  	  mWaitForDeadState = true;
  	  	
//  	  	 // TODO Opt1: Enable mAreaLaserDynamicCollisionComponent, even after direct single Enemy Hit
//  	  	 // TODO Opt2: Find way to check if mAreaLaserDynamicCollisionComponent is Active or Inactive in GameObjectManager
//  	  	 // TODO Add GameObject Active / Inactive Boolean Flag
//  	  	if (parentObject.type == Type.DROID_LASER_GRENADE || parentObject.type == Type.DROID_LASER_ROCKET) {
////  	  		Log.i("Grenade", "LaserComponent stateDead() parentObject.remove(mAreaLaserDynamicCollisionComponent)");
//  	  		
////  	  		parentObject.remove(mAreaLaserDynamicCollisionComponent);
//  	  	}
		
      	switch(parentObject.hitReactType) {
		case EXPLOSION:
			specialEffect.activateAnimationSet(Type.EXPLOSION, parentObject.laserReceiveGameObjectPosition);
//			specialEffect.activateAnimationSet(Type.EXPLOSION, parentObject.currentPosition);
			parentObject.hitReactType = Type.INVALID;
			break;
			
		case ELECTRIC_RING:
			specialEffect.activateAnimationSet(Type.ELECTRIC_RING, parentObject.laserReceiveGameObjectPosition);
//			specialEffect.activateAnimationSet(Type.ELECTRIC_RING, parentObject.currentPosition);
			parentObject.hitReactType = Type.INVALID;
			break;
			
		case ELECTRICITY:
			specialEffect.activateAnimationSet(Type.ELECTRICITY, parentObject.laserReceiveGameObjectPosition);
//			specialEffect.activateAnimationSet(Type.ELECTRIC_RING, parentObject.currentPosition);
			parentObject.hitReactType = Type.INVALID;
			break;
			
		case EXPLOSION_LARGE:
			specialEffect.activateAnimationSet(Type.EXPLOSION_LARGE, parentObject.laserReceiveGameObjectPosition);
//			specialEffect.activateAnimationSet(Type.ELECTRIC_RING, parentObject.currentPosition);
			parentObject.hitReactType = Type.INVALID;
			break;
			
		case EXPLOSION_RING:
			specialEffect.activateAnimationSet(Type.EXPLOSION_RING, parentObject.laserReceiveGameObjectPosition);
//			specialEffect.activateAnimationSet(Type.ELECTRIC_RING, parentObject.currentPosition);
			parentObject.hitReactType = Type.INVALID;
			break;
			
		case INVALID:
			// Ignore
			break;
			
		default:
			parentObject.hitReactType = Type.INVALID;
			break;
      	}
		
		parentObject.gameObjectInactive = true;
		parentObject.currentState = CurrentState.MOVE;
		parentObject.soundPlayed = false;
	}
    
//    public final void setAreaLaserDynamicCollisionComponent(DynamicCollisionComponent laserDynamicCollisionComponent) {
//    	mAreaLaserDynamicCollisionComponent = laserDynamicCollisionComponent;
//    }
	
	public final void setFireSound(Sound fireSound) {
//		Log.i("Sound", "LaserComponent setFireSound() [soundId]" + " [" + fireSound.soundId + "] ");
		
		mFireSound = fireSound;
	}
}