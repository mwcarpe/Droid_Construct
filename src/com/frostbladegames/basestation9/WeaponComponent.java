/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import javax.microedition.khronos.opengles.GL11;
import android.util.Log;

import com.frostbladegames.basestation9.GameObjectFactory.GameObjectPool;
import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Type;

public class WeaponComponent extends GameComponent {	
    private static final float PI_OVER_180 = 0.0174532925f;
	
	private float mWeaponOX;
	private float mWeaponOY;
	private float mWeaponOZ;
	
	private float mLaserOX;
	private float mLaserOY;
	private float mLaserOZ;
	
	private float mLastTime;
	
	private GameObject mTopGameObject;
	
	private Type mObjectTypeToSpawn;
	
    // FIXME TEMP DELETE
    private int mLaserObjectCounter;
    private float mCounterTimer;
	
	public WeaponComponent() {
	    super();

	    setPhase(ComponentPhases.MOVEMENT.ordinal());
//	    setPhase(ComponentPhases.THINK.ordinal());
	    reset();
	}
	
	@Override
	public void reset() {
		mLastTime = 0.0f;
//		mLastTime = sSystemRegistry.timeSystem.getGameTime();
		
	    mObjectTypeToSpawn = Type.INVALID;
	    
        mLaserObjectCounter = 0;
        mCounterTimer = 0.0f;
	}
	
	//@Override
	public void update(float timeDelta, BaseObject parent) {
		if(!GameParameters.gamePause) {
			TimeSystem time = sSystemRegistry.timeSystem;
			GameObject parentObject = (GameObject)parent;
			
			final float gameTime = time.getGameTime();
			
			if (mTopGameObject != null) {
				parentObject.currentState = mTopGameObject.currentState;
			} else {
				parentObject.currentState = CurrentState.DEAD;
					
				Log.e("WeaponComponent", "mTopGameObject = NULL");
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
	      		
//	      	case PLATFORM_SECTION_END:
//	      		stateSectionEnd(gameTime, timeDelta, parentObject);
//	      		break;
	      		
	      	case DEAD:
	      		stateDead(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	case LEVEL_END:
	      		stateLevelEnd(gameTime, timeDelta, parentObject);
	      		break;
	      		
	      	default:
	      		break;
			}
		}
	}
	
  	protected void stateIntro(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {	    		
	    		float x = mTopGameObject.currentPosition.x;
	    		float y = mTopGameObject.currentPosition.y;
	    		float z = mTopGameObject.currentPosition.z;
	    		float r = mTopGameObject.currentPosition.r;
	    		
		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
	          	
		    	parentObject.setCurrentPosition(x, y, z, r);
				
			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  	  			r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
  	}
	
    protected void stateLevelStart(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {	    		
	    		float x = mTopGameObject.currentPosition.x;
	    		float y = mTopGameObject.currentPosition.y;
	    		float z = mTopGameObject.currentPosition.z;
	    		float r = mTopGameObject.currentPosition.r;

		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
	          	
		    	parentObject.setCurrentPosition(x, y, z, r);
				
			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {  			
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  	  			r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
	
    protected void stateMove(float gameTime, float timeDelta, GameObject parentObject) {		
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
  	    	if (mTopGameObject != null) {
  	    		// FIXME Replicate GameTime code in each Method
//  	    		final float gameTime = sSystemRegistry.timeSystem.getGameTime();
  		      
  				InputSystem inputSystem = sSystemRegistry.inputSystem;
  		      
  				if (inputSystem != null) {  					
  		    		float x = mTopGameObject.currentPosition.x;
  		    		float y = mTopGameObject.currentPosition.y;
  		    		float z = mTopGameObject.currentPosition.z;
  		    		float r = mTopGameObject.currentPosition.r;
  					
					float droidPositionX = x;
  					float droidPositionZ = z;
  					
  					// Delay Fire Rate by Weapon Type
  					if (inputSystem.touchFirePress && parentObject.activeWeapon) {  						
//				    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
//				    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;						
//				    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
//				    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
  						
  						float timeCheck;
  						
  						switch(parentObject.type) {
  						case DROID_WEAPON_LASER_STD:
  							timeCheck = 0.25f;
//  							timeCheck = 0.3f;
  							break;
  							
  						case DROID_WEAPON_LASER_PULSE:
  							timeCheck = 0.15f;
//  							timeCheck = 0.2f;
  							break;
  							
  						case DROID_WEAPON_LASER_EMP:
  							timeCheck = 0.4f;
//  							timeCheck = 0.35f;
//  							timeCheck = 0.5f;
  							break;
  							
  						case DROID_WEAPON_LASER_GRENADE:
  							timeCheck = 0.6f;
//  							timeCheck = 0.75f;
  							
//  							parentObject.yValueBeforeMove = parentObject.currentPosition.y;
//  							parentObject.yMoveDistance = -0.583f;
//  							parentObject.yMoveMagnitude = -0.058f;
  							
  							break;
  							
  						case DROID_WEAPON_LASER_ROCKET:
  							timeCheck = 0.8f;
//  							timeCheck = 1.0f;
  							break;
  						
  						default:
  							timeCheck = 0.25f;
  							break;
  						}
  						
  						if (gameTime > (mLastTime + timeCheck)) {
//  	  				if (gameTime > (mLastTime + 0.25f)) {
  					    	GameObjectManager manager = sSystemRegistry.gameObjectManager;

  	  		        		if (manager != null) {  		        			
  						    	float initialX = ((mLaserOX * (float)Math.cos(r * PI_OVER_180)) +
  						    			(mLaserOZ * (float)Math.sin(r * PI_OVER_180))) + droidPositionX;
  						    	float initialZ = ((mLaserOX * (-(float)Math.sin(r * PI_OVER_180))) +
  						    			(mLaserOZ * (float)Math.cos(r * PI_OVER_180))) + droidPositionZ;
  	  		        			
  	  		        			manager.activateLaserGameObject(mObjectTypeToSpawn, initialX, y, initialZ, r);
  	  		        			
//	  	  		          	  	mLaserObjectCounter++;
//	  	  		            	if (gameTime > (mCounterTimer + 10.0f)) {
//	  	  		            		Log.i("GameObjectCounter", "Total Laser Object count = " + mLaserObjectCounter);
//	  	  		            		
//	  	  		            		mCounterTimer = gameTime;
//	  	  		            	}
  	  		        			
  	  		        		} else {
  	  		    				Log.e("Object", "GameObjectManager = NULL");
  	  		        		}
  	  		        		
  	  						mLastTime = gameTime;
  						}
  					}
  					
			    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
			    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;						
			    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
			    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;  
  	          	
  					parentObject.setCurrentPosition(x, y, z, r);
  				}
  				
  			} else {
  				Log.e("WeaponComponent", "mTopGameObject = NULL");
  			}
  		} else {
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  				r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
    
    protected void stateBounce(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {				
		    	float x = mTopGameObject.currentPosition.x;
  		    	float y = mTopGameObject.currentPosition.y;
  		    	float z = mTopGameObject.currentPosition.z;
  		    	float r = mTopGameObject.currentPosition.r;
				
		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
          	
		    	parentObject.setCurrentPosition(x, y, z, r);

			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  				r += 2.0f;
  			} else {
  				r = 0.0f;
  			}

  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
    
    protected void stateFrozen(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {
  				InputSystem inputSystem = sSystemRegistry.inputSystem;
    		      
  				if (inputSystem != null) {  					
  		    		float x = mTopGameObject.currentPosition.x;
  		    		float y = mTopGameObject.currentPosition.y;
  		    		float z = mTopGameObject.currentPosition.z;
  		    		float r = mTopGameObject.currentPosition.r;
  					
					float droidPositionX = x;
  					float droidPositionZ = z;
  					
  					// Delay Fire Rate by Weapon Type
  					if (inputSystem.touchFirePress && parentObject.activeWeapon) {  						
//				    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
//				    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;						
//				    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
//				    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
  						
  						float timeCheck;
  						
  						switch(parentObject.type) {
  						case DROID_WEAPON_LASER_STD:
  							timeCheck = 0.5f;
//  							timeCheck = 0.3f;
  							break;
  							
  						case DROID_WEAPON_LASER_PULSE:
  							timeCheck = 0.4f;
  							break;
  							
  						case DROID_WEAPON_LASER_EMP:
  							timeCheck = 0.75f;
  							break;
  							
  						case DROID_WEAPON_LASER_GRENADE:
  							timeCheck = 1.0f;
  							
//  							parentObject.yValueBeforeMove = parentObject.currentPosition.y;
//  							parentObject.yMoveDistance = -0.583f;
//  							parentObject.yMoveMagnitude = -0.058f;
  							
  							break;
  							
  						case DROID_WEAPON_LASER_ROCKET:
  							timeCheck = 1.5f;
  							break;
  						
  						default:
  							timeCheck = 0.5f;
  							break;
  						}
  						
  						if (gameTime > (mLastTime + timeCheck)) {
//  	  				if (gameTime > (mLastTime + 0.25f)) {
  					    	GameObjectManager manager = sSystemRegistry.gameObjectManager;

  	  		        		if (manager != null) {  		        			
  						    	float initialX = ((mLaserOX * (float)Math.cos(r * PI_OVER_180)) +
  						    			(mLaserOZ * (float)Math.sin(r * PI_OVER_180))) + droidPositionX;
  						    	float initialZ = ((mLaserOX * (-(float)Math.sin(r * PI_OVER_180))) +
  						    			(mLaserOZ * (float)Math.cos(r * PI_OVER_180))) + droidPositionZ;
  	  		        			
  	  		        			manager.activateLaserGameObject(mObjectTypeToSpawn, initialX, y, initialZ, r);
  	  		        			
//	  	  		          	  	mLaserObjectCounter++;
//	  	  		            	if (gameTime > (mCounterTimer + 10.0f)) {
//	  	  		            		Log.i("GameObjectCounter", "Total Laser Object count = " + mLaserObjectCounter);
//	  	  		            		
//	  	  		            		mCounterTimer = gameTime;
//	  	  		            	}
  	  		        			
  	  		        		} else {
  	  		    				Log.e("Object", "GameObjectManager = NULL");
  	  		        		}
  	  		        		
  	  						mLastTime = gameTime;
  						}
  					}
  					
			    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
			    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;						
			    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
			    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;  
  	          	
  					parentObject.setCurrentPosition(x, y, z, r);
  				}
//		    	float x = mTopGameObject.currentPosition.x;
//  		    	float y = mTopGameObject.currentPosition.y;
//  		    	float z = mTopGameObject.currentPosition.z;
//  		    	float r = mTopGameObject.currentPosition.r;
//
//		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
//		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
//		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
//		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
//          	
//		    	parentObject.setCurrentPosition(x, y, z, r);
				
			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  				r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
    
    protected void stateFall(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {     
		    	float x = mTopGameObject.currentPosition.x;
  		    	float y = mTopGameObject.currentPosition.y;
  		    	float z = mTopGameObject.currentPosition.z;
  		    	float r = mTopGameObject.currentPosition.r;
						
		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
          	
		    	parentObject.setCurrentPosition(x, y, z, r);
				
	    	} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  				r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
    
    protected void stateElevator(float gameTime, float timeDelta, GameObject parentObject) {		
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
  	    	if (mTopGameObject != null) {  		      
  				InputSystem inputSystem = sSystemRegistry.inputSystem;
  		      
  				if (inputSystem != null) {  					
  		    		float x = mTopGameObject.currentPosition.x;
  		    		float y = mTopGameObject.currentPosition.y;
  		    		float z = mTopGameObject.currentPosition.z;
  		    		float r = mTopGameObject.currentPosition.r;
  					
					float droidPositionX = x;
  					float droidPositionZ = z;
  					
  					if (inputSystem.touchFirePress && parentObject.activeWeapon) {  						
  						float timeCheck;
  						
  						switch(parentObject.type) {
  						case DROID_WEAPON_LASER_STD:
  							timeCheck = 0.25f;
  							break;
  							
  						case DROID_WEAPON_LASER_PULSE:
  							timeCheck = 0.15f;
//  							timeCheck = 0.2f;
  							break;
  							
  						case DROID_WEAPON_LASER_EMP:
  							timeCheck = 0.4f;
//  							timeCheck = 0.35f;
  							break;
  							
  						case DROID_WEAPON_LASER_GRENADE:
  							timeCheck = 0.6f;
  							
//  							parentObject.yValueBeforeMove = parentObject.currentPosition.y;
//  							parentObject.yMoveDistance = -0.583f;
//  							parentObject.yMoveMagnitude = -0.058f;
  							
  							break;
  							
  						case DROID_WEAPON_LASER_ROCKET:
  							timeCheck = 0.8f;
  							break;
  						
  						default:
  							timeCheck = 0.25f;
  							break;
  						}
  						
  						if (gameTime > (mLastTime + timeCheck)) {
  					    	GameObjectManager manager = sSystemRegistry.gameObjectManager;

  	  		        		if (manager != null) {  		        			
  						    	float initialX = ((mLaserOX * (float)Math.cos(r * PI_OVER_180)) +
  						    			(mLaserOZ * (float)Math.sin(r * PI_OVER_180))) + droidPositionX;
  						    	float initialZ = ((mLaserOX * (-(float)Math.sin(r * PI_OVER_180))) +
  						    			(mLaserOZ * (float)Math.cos(r * PI_OVER_180))) + droidPositionZ;
  	  		        			
  	  		        			manager.activateLaserGameObject(mObjectTypeToSpawn, initialX, y, initialZ, r);
  	  		        			
  	  		        		} else {
  	  		    				Log.e("Object", "GameObjectManager = NULL");
  	  		        		}
  	  		        		
  	  						mLastTime = gameTime;
  						}
  					}
  					
			    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
			    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;						
			    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
			    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;  
  	          	
  					parentObject.setCurrentPosition(x, y, z, r);
  				}
  				
  			} else {
  				Log.e("WeaponComponent", "mTopGameObject = NULL");
  			}
  		} else {
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  				r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
    
    protected void stateSectionStart(float gameTime, float timeDelta, GameObject parentObject) { 
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {	    		
	    		float x = mTopGameObject.currentPosition.x;
	    		float y = mTopGameObject.currentPosition.y;
	    		float z = mTopGameObject.currentPosition.z;
	    		float r = mTopGameObject.currentPosition.r;

		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
	          	
		    	parentObject.setCurrentPosition(x, y, z, r);
				
			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {  			
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  	  			r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}	
    }
  
    protected void stateDead(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {	    		
	    		float x = mTopGameObject.currentPosition.x;
	    		float y = mTopGameObject.currentPosition.y;
	    		float z = mTopGameObject.currentPosition.z;
	    		float r = mTopGameObject.currentPosition.r;

		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
	          	
		    	parentObject.setCurrentPosition(x, y, z, r);
				
			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {  			
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  	  			r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
	}
    
    protected void stateLevelEnd(float gameTime, float timeDelta, GameObject parentObject) {
  		if (parentObject.activeWeapon || parentObject.inventoryWeapon) {
	    	if (mTopGameObject != null) {	    		
	    		float x = mTopGameObject.currentPosition.x;
	    		float y = mTopGameObject.currentPosition.y;
	    		float z = mTopGameObject.currentPosition.z;
	    		float r = mTopGameObject.currentPosition.r;

		    	x = ((mWeaponOX * (float)Math.cos(r * PI_OVER_180)) +
		    			(mWeaponOZ * (float)Math.sin(r * PI_OVER_180))) + x;
		    	z = ((mWeaponOX * (-(float)Math.sin(r * PI_OVER_180))) +
		    			(mWeaponOZ * (float)Math.cos(r * PI_OVER_180))) + z;
	          	
		    	parentObject.setCurrentPosition(x, y, z, r);
				
			} else {
				Log.e("WeaponComponent", "mTopGameObject = NULL");
			}
  		} else {  			
  			float x = parentObject.currentPosition.x;
  			float y = parentObject.currentPosition.y;
  			float z = parentObject.currentPosition.z;
  			float r = parentObject.currentPosition.r;
  			
  			if (r < 359.0f) {		// Allow 1.0f tolerance for float type
  	  			r += 2.0f;
  			} else {
  				r = 0.0f;
  			}
			
  			parentObject.setCurrentPosition(x, y, z, r);
  		}
    }
    
    public void setWeaponOffset(Vector3 weaponOffset) {
    	mWeaponOX = weaponOffset.x;
    	mWeaponOY = weaponOffset.y;
    	mWeaponOZ = weaponOffset.z;
    }
    
    public void setLaserOffset(Vector3 laserOffset) {
    	mLaserOX = laserOffset.x;
    	mLaserOY = laserOffset.y;
    	mLaserOZ = laserOffset.z;
    }
    
	public void setTopGameObject(GameObject topGameObject) {
		mTopGameObject = topGameObject;
	}
  
	public void setObjectTypeToSpawn(Type objectTypeToSpawn) {
		mObjectTypeToSpawn = objectTypeToSpawn;
	}
}