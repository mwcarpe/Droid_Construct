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

public class EnemyLaserComponent extends GameComponent {
    private static final float PI_OVER_180 = 0.0174532925f;   
    
    private Vector3 mTempPosition;
    
    private DynamicCollisionComponent mAreaLaserDynamicCollisionComponent;
	
    private Sound mFireSound;
    
//    private boolean mWaitForDeadState;
    private float mPreviousTime;
	
	public EnemyLaserComponent() {
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
	  	  	
			// TODO Study setTimeUntilDeath() as alternate method to destroy laser based on time vs activeRadius distance
	  	  	
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
			  
			    if (!parentObject.soundPlayed) {
		      		SoundSystem sound = sSystemRegistry.soundSystem;

			    	if (mFireSound == null) {
			    		Log.e("Sound", "EnemyLaserComponent update() FAIL mFireSound = NULL [gameObjectId]" +
			    				" [" + parentObject.gameObjectId + "] ");
			    		
			        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
			        	Context context = factory.context;
			        	if (context != null) {
					    	switch(parentObject.type) {
					    	case ENEMY_LASER_STD:
					    		mFireSound = sound.load(R.raw.sound_laser_std);
					    		break;
					    		
					    	case ENEMY_LASER_EMP:
					    		mFireSound = sound.load(R.raw.sound_laser_emp);
					    		break;
					    		
					    	case ENEMY_BOSS_LASER_STD:
					    		mFireSound = sound.load(R.raw.sound_laser_emp);
					    		break;
					    		
					    	case ENEMY_BOSS_LASER_EMP:
					    		mFireSound = sound.load(R.raw.sound_laser_emp);
					    		break;
					    		
					    	case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
					    		mFireSound = sound.load(R.raw.sound_laser_rocket);
					    		break;
					    		
					    	default:
					    		mFireSound = sound.load(R.raw.sound_laser_std);
					    		break;
					    	}
				          	sound.play(mFireSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
			        	} else {
			            	Log.e("Sound", "EnemyLaserComponent update() factory.context = NULL");
			        	}
			    	} else {
			          	sound.play(mFireSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
			    	}
			    	
			      	parentObject.soundPlayed = true;				
				} else {
			    	switch(parentObject.type) {
			    	case ENEMY_LASER_STD:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case ENEMY_LASER_EMP:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case ENEMY_BOSS_LASER_STD:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case ENEMY_BOSS_LASER_EMP:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			    		break;
			    		
			    	case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
						x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
						z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
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
    	case DROID_LASER_ROCKET:
//			parentObject.laserReceiveGameObjectPosition.set(parentObject.currentPosition);
			
			parentObject.hitReactType = Type.EXPLOSION_RING;
			
			// FIXME Re-add
			parentObject.add(mAreaLaserDynamicCollisionComponent);
			
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
		
 	  	 // TODO Opt1: Enable mAreaLaserDynamicCollisionComponent, even after direct single Enemy Hit
 	  	 // TODO Opt2: Find way to check if mAreaLaserDynamicCollisionComponent is Active or Inactive in GameObjectManager
 	  	 // TODO Add GameObject Active / Inactive Boolean Flag
 	  	if (parentObject.type == Type.ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY) {
// 	  	if (parentObject.type == Type.DROID_LASER_GRENADE && parentObject.previousState == CurrentState.WAIT_FOR_DEAD) {
 	  		parentObject.remove(mAreaLaserDynamicCollisionComponent);
 	  	}
		
      	switch(parentObject.hitReactType) {
		case EXPLOSION:
			specialEffect.activateAnimationSet(Type.EXPLOSION, parentObject.laserReceiveGameObjectPosition);
//			specialEffect.activateAnimationSet(Type.EXPLOSION, parentObject.currentPosition);
			parentObject.hitReactType = Type.INVALID;
			break;
			
		case ELECTRICITY:
			specialEffect.activateAnimationSet(Type.ELECTRICITY, parentObject.laserReceiveGameObjectPosition);
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
    
    public final void setAreaLaserDynamicCollisionComponent(DynamicCollisionComponent laserDynamicCollisionComponent) {
    	mAreaLaserDynamicCollisionComponent = laserDynamicCollisionComponent;
    }
	
	public final void setFireSound(Sound fireSound) {
		mFireSound = fireSound;
	}
}