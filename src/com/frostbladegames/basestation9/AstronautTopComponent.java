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

public class AstronautTopComponent extends GameComponent {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;
    
	private static final float HIT_REACT_TIME = 0.5f;
	
	private static final float LEVEL_START_SPEED_ADJUST = 0.25f;
	
	private static final float COLLISION_RESPONSE_VELOCITY = 1.0f;
	
//    private static final float MAX_ASTRONAUT_LIFE = 1.0f;
    
    private static final int ANIMATION_STEP_INTERVAL = 8;
//    public static final int ANIMATION_STEP_INTERVAL = 4;
	
	private static final float GLOW_DURATION = 15.0f;
	
//	private static final float INCREMENT_VALUE = 0.25f;
	
    private Vector3 mTempPosition;
    
    private int mAnimationStepCount;
    
	private int mTotalFrames;
	private int mTotalIncrements;
	private int mIncrementMultiplier;
    
    private AnimationSet mAstronautAppendages;
	
    private float mTimer;
    
    private float mTimeUntilDeath;
    private Type mSpawnOnDeathType;
	
	private float mCollisionResponse;
	private boolean mHitResult;
	
//    private Sound mEnemySound;
    private Sound mBounceSound;
    private Sound mCollectSound;
    private Sound mDeathSound;
	
	public AstronautTopComponent() {
	    super();
	    
	    setPhase(ComponentPhases.MOVEMENT.ordinal());
	    reset();
	}
	
	@Override
	public void reset() {	    
        mTimer = 0.0f;
        
        mTempPosition = new Vector3();
        
        mAnimationStepCount = 0;
        
        mBounceSound = null;
        mCollectSound = null;
        mDeathSound = null;
	    
	    mCollisionResponse = COLLISION_RESPONSE_VELOCITY;
	    mHitResult = false;
	}
	
	//@Override
	public void update(float timeDelta, BaseObject parent) {
		if(!GameParameters.gamePause) {
		    TimeSystem time = sSystemRegistry.timeSystem;
		    GameObject parentObject = (GameObject)parent;
		    
//	      	Log.i("Loop", "AstronautTopComponent update()" + " [" + parentObject.gameObjectId + "] ");
		    
		    final float gameTime = time.getGameTime();
		    
//		    if (parentObject.currentState == CurrentState.INVALID) {
//		        gotoMove(parentObject);
//		    }
		    
//		    // Watch for hit reactions or death interrupting the state machine.
//		    if (parentObject.currentState != CurrentState.DEAD) {
////		    if (mState != CurrentState.DEAD) {
//		//    if (mState != State.DEAD && mState != State.WIN ) {
//		        if (parentObject.hitPoints <= 0.0f) {
//		            gotoDead(gameTime, parentObject);
//		        }
//		    }
		    
	    	CurrentState droidCurrentState = sSystemRegistry.gameObjectManager.droidBottomGameObject.currentState;
//	    	float droidY = sSystemRegistry.gameObjectManager.droidBottomGameObject.currentPosition.y;
	    	
	    	switch(parentObject.currentState) {
	      	case INTRO: 
	      		stateIntro(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case LEVEL_START: 
	      		stateLevelStart(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case MOVE: 
	      		if (droidCurrentState == CurrentState.ELEVATOR  || droidCurrentState == CurrentState.LEVEL_END) {
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
	      		stateFrozen(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case FALL:
	      		stateFall(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case COLLECT:
	      		stateCollect(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case DEAD:
	      		stateDead(gameTime, timeDelta, parentObject);
	      		break;
		            
		    default:
		    	stateLevelStart(gameTime, timeDelta, parentObject);
		        break;
		    }
		    
			// FIXME (see DroidBottomComponent and original PlayerComponent code)
		    
		}
	}
	
	protected void stateIntro(float time, float timeDelta, GameObject parentObject) {
	    Type type = parentObject.type;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
	    
		switch(type) {
		case ASTRONAUT_PRIVATE:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		case ASTRONAUT_SERGEANT:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		case ASTRONAUT_CAPTAIN:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		case ASTRONAUT_GENERAL:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		default:
			break;
		}
	    
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
		
		final int currentFrame = mAstronautAppendages.currentFrame;
		final int currentIncrement = mAstronautAppendages.currentIncrement;
    	
		GameObject gameObject = mAstronautAppendages.getAnimationFrame(mAstronautAppendages.currentFrame - 1);
		
	    final RenderSystem render = sSystemRegistry.renderSystem;
		render.scheduleForDraw(gameObject.drawableDroid, parentObject.currentPosition, null, 0.0f, 0, true, gameObject.gameObjectId);
    	
    	if (currentIncrement >= mTotalIncrements) {
    		// Re-start Increment and Frame at 1    		
    		mAstronautAppendages.currentFrame = 1;
    		mAstronautAppendages.currentIncrement = 1;

    	} else if (currentIncrement >= (currentFrame * mIncrementMultiplier)) {
    		// Next Increment, Next Frame    		
    		mAstronautAppendages.currentFrame++;
    		mAstronautAppendages.currentIncrement++;

    	} else {
    		// Next Increment, Same Frame
    		mAstronautAppendages.currentIncrement++;
    	}
	}
	
    protected void stateLevelStart(float time, float timeDelta, GameObject parentObject) {
	    Type type = parentObject.type;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
    	
		switch(type) {
		case ASTRONAUT_PRIVATE:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
			break;
			
		case ASTRONAUT_SERGEANT:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
			break;
			
		case ASTRONAUT_CAPTAIN:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
			break;
			
		case ASTRONAUT_GENERAL:    	    
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude * LEVEL_START_SPEED_ADJUST;
			break;
			
		default:
			break;
		}
	    
		parentObject.setCurrentPosition(x, y, z, r);
		
		parentObject.setPreviousPosition(mTempPosition);
		
		final int currentFrame = mAstronautAppendages.currentFrame;
		final int currentIncrement = mAstronautAppendages.currentIncrement;
    	
		GameObject gameObject = mAstronautAppendages.getAnimationFrame(mAstronautAppendages.currentFrame - 1);
		
	    final RenderSystem render = sSystemRegistry.renderSystem;
		render.scheduleForDraw(gameObject.drawableDroid, parentObject.currentPosition, null, 0.0f, 0, true, gameObject.gameObjectId);
    	
    	if (currentIncrement >= mTotalIncrements) {
    		// Re-start Increment and Frame at 1    		
    		mAstronautAppendages.currentFrame = 1;
    		mAstronautAppendages.currentIncrement = 1;

    	} else if (currentIncrement >= (currentFrame * mIncrementMultiplier)) {
    		// Next Increment, Next Frame    		
    		mAstronautAppendages.currentFrame++;
    		mAstronautAppendages.currentIncrement++;

    	} else {
    		// Next Increment, Same Frame
    		mAstronautAppendages.currentIncrement++;
    	}
    }
	
	protected void stateMove(float time, float timeDelta, GameObject parentObject) {	
	    Type type = parentObject.type;
	    
		mTempPosition.set(parentObject.currentPosition);
	    
	    float x = parentObject.currentPosition.x;
	    float y = parentObject.currentPosition.y;
	    float z = parentObject.currentPosition.z;
	    float r = parentObject.currentPosition.r;
    		
		switch(type) {
		case ASTRONAUT_PRIVATE:
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;    		
			break;
			
		case ASTRONAUT_SERGEANT:
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		case ASTRONAUT_CAPTAIN:
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		case ASTRONAUT_GENERAL:
    		x -= (float)Math.sin(r * PI_OVER_180) * parentObject.magnitude;
    		z -= (float)Math.cos(r * PI_OVER_180) * parentObject.magnitude;
			break;
			
		default:
			break;
		}
		
		parentObject.setCurrentPosition(x, y, z, r);

		parentObject.setPreviousPosition(mTempPosition);
		
		final int currentFrame = mAstronautAppendages.currentFrame;

		final int currentIncrement = mAstronautAppendages.currentIncrement;
    	
		GameObject gameObject = mAstronautAppendages.getAnimationFrame(mAstronautAppendages.currentFrame - 1);
		
	    final RenderSystem render = sSystemRegistry.renderSystem;
		render.scheduleForDraw(gameObject.drawableDroid, parentObject.currentPosition, null, 0.0f, 0, true, gameObject.gameObjectId);
    	
    	if (currentIncrement >= mTotalIncrements) {
    		// Re-start Increment and Frame at 1    		
    		mAstronautAppendages.currentFrame = 1;
    		mAstronautAppendages.currentIncrement = 1;

    	} else if (currentIncrement >= (currentFrame * mIncrementMultiplier)) {
    		// Next Increment, Next Frame    		
    		mAstronautAppendages.currentFrame++;
    		mAstronautAppendages.currentIncrement++;
    		
    	} else {
    		// Next Increment, Same Frame
    		mAstronautAppendages.currentIncrement++;
    	}
	}
	
  	protected void stateBounce(float time, float timeDelta, GameObject parentObject) {  		  		
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
		    	if (mBounceSound == null) {
		    		Log.e("Sound", "AstronautTopComponent update() FAIL mBounceSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
		        		mBounceSound = sound.load(R.raw.sound_gameobject_bounce);
			          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "AstronautTopComponent stateBounce() factory.context = NULL");
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
	  		parentObject.soundPlayed = false;
	    	parentObject.currentState = CurrentState.MOVE;
	  	}
    	
	  	parentObject.setCurrentPosition(x, y, z, r);
	  	
		parentObject.setPreviousPosition(mTempPosition);
		
		final int currentFrame = mAstronautAppendages.currentFrame;

		final int currentIncrement = mAstronautAppendages.currentIncrement;
    	
		GameObject gameObject = mAstronautAppendages.getAnimationFrame(mAstronautAppendages.currentFrame - 1);
		
	    final RenderSystem render = sSystemRegistry.renderSystem;
		render.scheduleForDraw(gameObject.drawableDroid, parentObject.currentPosition, null, 0.0f, 0, true, gameObject.gameObjectId);
    	
    	if (currentIncrement >= mTotalIncrements) {
    		// Re-start Increment and Frame at 1    		
    		mAstronautAppendages.currentFrame = 1;
    		mAstronautAppendages.currentIncrement = 1;

    	} else if (currentIncrement >= (currentFrame * mIncrementMultiplier)) {
    		// Next Increment, Next Frame    		
    		mAstronautAppendages.currentFrame++;
    		mAstronautAppendages.currentIncrement++;
    		
    	} else {
    		// Next Increment, Same Frame
    		mAstronautAppendages.currentIncrement++;
    	}
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
		    	if (mBounceSound == null) {
		    		Log.e("Sound", "AstronautTopComponent update() FAIL mBounceSound = NULL [gameObjectId]" +
		    				" [" + parentObject.gameObjectId + "] ");
		    		
		        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
		        	Context context = factory.context;
		        	if (context != null) {
		        		mBounceSound = sound.load(R.raw.sound_gameobject_bounce);
			          	sound.play(mBounceSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
		        	} else {
		            	Log.e("Sound", "AstronautTopComponent stateBounce() factory.context = NULL");
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
	    	parentObject.currentState = CurrentState.MOVE;
//	    	mHitPlayed = false;
	  	}
		
	  	parentObject.setCurrentPosition(x, y, z, r);
	  	
		parentObject.setPreviousPosition(mTempPosition);
		
		final int currentFrame = mAstronautAppendages.currentFrame;

		final int currentIncrement = mAstronautAppendages.currentIncrement;
    	
		GameObject gameObject = mAstronautAppendages.getAnimationFrame(mAstronautAppendages.currentFrame - 1);
		
	    final RenderSystem render = sSystemRegistry.renderSystem;
		render.scheduleForDraw(gameObject.drawableDroid, parentObject.currentPosition, null, 0.0f, 0, true, gameObject.gameObjectId);
    	
    	if (currentIncrement >= mTotalIncrements) {
    		// Re-start Increment and Frame at 1    		
    		mAstronautAppendages.currentFrame = 1;
    		mAstronautAppendages.currentIncrement = 1;

    	} else if (currentIncrement >= (currentFrame * mIncrementMultiplier)) {
    		// Next Increment, Next Frame    		
    		mAstronautAppendages.currentFrame++;
    		mAstronautAppendages.currentIncrement++;
    		
    	} else {
    		// Next Increment, Same Frame
    		mAstronautAppendages.currentIncrement++;
    	}
  	}
  	
    protected void stateFrozen(float time, float timeDelta, GameObject parentObject) {
    	parentObject.currentState = CurrentState.MOVE;
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
    
	protected void stateCollect(float time, float timeDelta, GameObject parentObject) {	
		HudSystem hud = sSystemRegistry.hudSystem;
		hud.totalKillCollectPoints += 50;
		hud.totalCollectNum++;
		hud.totalKillCollectPointsDigitsChanged = true;
		hud.totalCollectNumDigitsChanged = true;
		
  		SoundSystem sound = sSystemRegistry.soundSystem;
    	if (mCollectSound == null) {
    		Log.e("Sound", "AstronautTopComponent update() FAIL mCollectSound = NULL [gameObjectId]" +
    				" [" + parentObject.gameObjectId + "] ");
    		
        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	Context context = factory.context;
        	if (context != null) {
		    	switch(parentObject.type) {
		    	case ASTRONAUT_PRIVATE:
	        		mCollectSound = sound.load(R.raw.sound_astronaut_collect);
		    		break;
		    		
		    	case ASTRONAUT_SERGEANT:
		    		mCollectSound = sound.load(R.raw.sound_astronaut_collect);
		    		break;
		    		
		    	case ASTRONAUT_CAPTAIN:
		    		mCollectSound = sound.load(R.raw.sound_astronaut_collect);
		    		break;
		    		
		    	case ASTRONAUT_GENERAL:
		    		mCollectSound = sound.load(R.raw.sound_astronaut_collect);
		    		break;
		    		
		    	default:
		    		mCollectSound = sound.load(R.raw.sound_astronaut_collect);
		    		break;
		    	}
              	sound.play(mCollectSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	} else {
        		Log.e("Sound", "AstronautTopComponent stateCollect() factory.context = NULL");
        	}
    	} else {
          	sound.play(mCollectSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
    	}
		
		// FIXME Change to Astronaut Pool System
		GameObjectManager manager = sSystemRegistry.gameObjectManager;
	      
		if (manager != null) {
			manager.destroy(parentObject);
		}
	}
		
	protected void stateDead(float time, float timeDelta, GameObject parentObject) {	
		parentObject.currentState = CurrentState.DEAD;
//		mState = CurrentState.DEAD;
		
  		SoundSystem sound = sSystemRegistry.soundSystem;
    	if (mDeathSound == null) {
    		Log.e("Sound", "AstronautTopComponent update() FAIL mDeathSound = NULL [gameObjectId]" +
    				" [" + parentObject.gameObjectId + "] ");
    		
        	GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        	Context context = factory.context;
        	if (context != null) {
		    	switch(parentObject.type) {
		    	case ASTRONAUT_PRIVATE:
		    		mDeathSound = sound.load(R.raw.sound_astronaut_death);
		    		break;
		    		
		    	case ASTRONAUT_SERGEANT:
		    		mDeathSound = sound.load(R.raw.sound_astronaut_death);
		    		break;
		    		
		    	case ASTRONAUT_CAPTAIN:
		    		mDeathSound = sound.load(R.raw.sound_astronaut_death);
		    		break;
		    		
		    	case ASTRONAUT_GENERAL:
		    		mDeathSound = sound.load(R.raw.sound_astronaut_death);
		    		break;
		    		
		    	default:
		    		mDeathSound = sound.load(R.raw.sound_astronaut_death);
		    		break;
		    	}
              	sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
        	} else {
        		Log.e("Sound", "AstronautTopComponent stateDead() factory.context = NULL");
        	}
    	} else {
          	sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL, 1.0f, 1.0f);
    	}
			
	  	// FIXME Is this code req, or only code below? Test timing of death sequence.
//	    if (mTimeUntilDeath > 0) {
////		    Log.d("Lifetime", "LifetimeComponent update(): mTimeUntilDeath = " + mTimeUntilDeath);
//	  	    
//	          mTimeUntilDeath -= timeDelta;
//	          
////	          Log.d("Lifetime", "LifetimeComponent update(): mTimeUntilDeath -= timeDelta = " + mTimeUntilDeath);
//	          
//	          if (mTimeUntilDeath <= 0) {
//	              die(parentObject);
//	              return;
//	          }
//	      }
			
		GameObjectManager manager = sSystemRegistry.gameObjectManager;
	      
		if (manager != null) {
			manager.destroy(parentObject);
		}
    	
//    	// XXX Previous Test
//    	parentObject.markedForDeath = true;
			
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
	
	public void setAnimationSet(AnimationSet animationSet) {
//	public void setAnimationSet(int totalAnimationFrames, int incrementAnimationMultiplier, AnimationSet animationSet) {
//	public void setAnimationSet(int total, AnimationSet animationSet) {
		mAstronautAppendages = new AnimationSet(animationSet);
		
//		mAstronautAppendages = new AnimationSet(totalAnimationFrames, incrementAnimationMultiplier);
////		mAstronautAppendages = new AnimationSet(total);
//		mAstronautAppendages = animationSet;
		
		mTotalFrames = mAstronautAppendages.totalFrames;
		mTotalIncrements = mAstronautAppendages.totalIncrements;
		mIncrementMultiplier = mAstronautAppendages.incrementMultiplier;
	}
	
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
	
    public final void setCollectSound(Sound collectSound) {
    	mCollectSound = collectSound;
    }
	
    public final void setDeathSound(Sound deathSound) {
    	mDeathSound = deathSound;
    }
}
