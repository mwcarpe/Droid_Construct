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

import java.util.Comparator;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;

//import com.frostbladegames.droidconstruct.DebugLog;

/** 
 * A node in the game graph that manages the activation status of its children.  The
 * GameObjectManager moves the objects it manages in and out of the active list (that is,
 * in and out of the game tree, causing them to be updated or ignored, respectively) each frame
 * based on the distance of that object to the camera.  Objects may specify an "activation radius"
 * to define an area around themselves so that the position of the camera can be used to determine
 * which objects should receive processing time and which should be ignored.  Objects that do not
 * move should have an activation radius that defines a sphere similar to the size of the screen;
 * they only need processing when they are visible.  Objects that move around will probably need
 * larger regions so that they can leave the visible area of the game world and not be immediately
 * deactivated.
 */
public class GameObjectManager extends ObjectManager {
    private static final int MAX_GAME_OBJECTS = 2030;
//    private static final int MAX_GAME_OBJECTS = 1970;
//    private static final int MAX_GAME_OBJECTS = 1860;
//    private static final int MAX_GAME_OBJECTS = 1790;
//    private static final int MAX_GAME_OBJECTS = 2149;
//    private static final int MAX_GAME_OBJECTS = 2048;
//    private static final int MAX_GAME_OBJECTS = 1280;
//    private static final int MAX_GAME_OBJECTS = 384;
    
//    private static final int MAX_LASER_GAMEOBJECTS = 40;
////    private static final int MAX_LASER_GAMEOBJECTS = 30;
    
//    private static final float LIGHTING_CHANGE_RATE = 10.0f;

    // FIXME Optimize BACKGROUND_ACTIVATION_RADIUS2 length based on actual distances to distanceStart and distanceEnd
    private static final float BACKGROUND_ACTIVATION_RADIUS2 = 3000.0f;
//    private static final float BACKGROUND_ACTIVATION_RADIUS2 = 2000.0f;
    
    private static final int SPECIAL_EFFECT_STEP_INTERVAL = 8;
  
    private int mSpecialEffectStepCount;
    private int mSpecialEffectStepNum;
    
    private boolean mWeaponChange;
    private float mLastWeaponChangeTime;
    private GameObject mWeapon1GameObject;
    private GameObject mWeapon2GameObject;
    
    private float mMaxActivationRadius;
    
    private static final GameObjectComparator sGameObjectComparator 
    = new GameObjectComparator();
    
    // GameObject FixedSizeArrays
    // Inactive and MarkedForDeath GameObject FixedSizeArrays
    private FixedSizeArray<BaseObject> mInactiveObjects;
    private FixedSizeArray<GameObject> mMarkedForDeathObjects;
    
    // Droid Laser GameObject FixedSizeArrays
	private FixedSizeArray<GameObject> mDroidLaserStdObjects;
	private FixedSizeArray<GameObject> mDroidLaserPulseObjects;
	private FixedSizeArray<GameObject> mDroidLaserEmpObjects;
	private FixedSizeArray<GameObject> mDroidLaserGrenadeObjects;
	private FixedSizeArray<GameObject> mDroidLaserRocketObjects;
	
	public boolean enemyLaserStdLoaded;
	public boolean enemyLaserEmpLoaded;
	
    // Enemy Laser GameObject FixedSizeArrays
	private FixedSizeArray<GameObject> mEnemyLaserStdObjects;
	private FixedSizeArray<GameObject> mEnemyLaserEmpObjects;
	private FixedSizeArray<GameObject> mEnemyLaserBossObjects;
//    // Special Effect GameObject FixedSizeArrays
//	private FixedSizeArray<GameObject> mExplosionStep1Objects;
//	private FixedSizeArray<GameObject> mExplosionStep2Objects;
//	private FixedSizeArray<GameObject> mExplosionStep3Objects;
//	private FixedSizeArray<GameObject> mExplosionStep4Objects;
	
	// FIXME Is mPlayer or mVisitingGraph still required?
//    private GameObject mPlayer;
    private boolean mVisitingGraph;
    
    private Vector3 mCameraFocus;
    
    public GameObject droidBottomGameObject;
    
    private float mLightingChangeRate;
    private float mPreviousTime;
    
    // FIXME TEMP DELETE
    private int mMaxLaserCount;
    private int mMaxObjectCount;
	private float mCounterTimer;
        
    public GameObjectManager(float maxActivationRadius) {
        super(MAX_GAME_OBJECTS);
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "GameObjectManager <constructor>");	
    	}
        
        mSpecialEffectStepCount = 0;
        mSpecialEffectStepNum = 1;
        
        mWeaponChange = false;
        mLastWeaponChangeTime = 0.0f;
        
        mMaxActivationRadius = maxActivationRadius;
        
        enemyLaserStdLoaded = false;
        enemyLaserEmpLoaded = false;
        
        mInactiveObjects = new FixedSizeArray<BaseObject>(MAX_GAME_OBJECTS);
        mInactiveObjects.setComparator(sGameObjectComparator);
        
        mMarkedForDeathObjects = new FixedSizeArray<GameObject>(MAX_GAME_OBJECTS);
        mVisitingGraph = false;
        
        mCameraFocus = new Vector3();
        
        mLightingChangeRate = 4.0f;
//        mLightingChangeRate = 8.0f;
        mPreviousTime = 0.0f;
    }
    
    @Override
    public void reset() {
    	// GameObjectManager reset() not required. Handled in destroyAll().
    }
    
    @Override
    public void commitUpdates() {
        super.commitUpdates();
        
        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        final int objectsToKillCount = mMarkedForDeathObjects.getCount();
        
//        if (objectsToKillCount > 0){
//    		Log.i("Object", "GameObjectManager commitUpdates() mMarkedForDeathObjects.getCount() = " + objectsToKillCount);	
//        }
		
//		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        for (int i = objectsToKillCount - 1; i >= 0; i--) {
////          for (int i = 0; i < objectsToKillCount; i++) {
//        	GameObject gameObject = (GameObject)mMarkedForDeathObjects.get(i);
//        	Log.i("GameFlow", "GameObjectManager destroyAll() mMarkedForDeathObjects gameObjectId = " + gameObject.gameObjectId);
//        }
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
        // XXX Previous Test. Moved to destroyAll()
        if (factory != null && objectsToKillCount > 0) {
            final Object[] deathArray = mMarkedForDeathObjects.getArray();
            for (int x = 0; x < objectsToKillCount; x++) {        	
                factory.destroy((GameObject)deathArray[x]);
            }
            mMarkedForDeathObjects.clear();
        }
    }

    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
        commitUpdates();
        
        mMaxLaserCount = 0;
        mMaxObjectCount = 0;
        
        CameraSystem camera = sSystemRegistry.cameraSystem;
        
        // FIXME Add CameraSystem method and change to mCameraFocus.set(camera.getFocalPosition). Eliminate other duplicate camera position values.
        mCameraFocus.set(camera.getFocusPositionX(), camera.getFocusPositionY(),
        		camera.getFocusPositionZ());
        
        // TODO Is mVisitingGraph required?
        mVisitingGraph = true;
        
    	if (GameParameters.levelRow == 4) {
        	float gameTime = sSystemRegistry.timeSystem.getGameTime();
        	
        	if(gameTime > (mPreviousTime + mLightingChangeRate)) {
//        	if(gameTime > (mPreviousTime + LIGHTING_CHANGE_RATE)) {
//        	if(gameTime > (mPreviousTime + 1.0f)) {
//        		Log.i("Lighting", "GameObjectManager gameTime, mPreviousTime = " + gameTime + ", " + mPreviousTime);
        		
                Random rStart = new Random();
                int randomLighting = rStart.nextInt(10);
                
                if (randomLighting > 8) {
                	GameParameters.lightingValue = 2;
                	mLightingChangeRate = 2.0f;
                } else if (randomLighting > 6) {
                	GameParameters.lightingValue = 1;
                	mLightingChangeRate = 4.0f;
//                	mLightingChangeRate = 5.0f;
                } else {
                	GameParameters.lightingValue = 0;
                	mLightingChangeRate = 6.0f;
//                	mLightingChangeRate = 8.0f;
                }
                
                mPreviousTime = gameTime;
        	}
    	}
        
        FixedSizeArray<BaseObject> objects = getObjects();
        final int totalActiveObjects = objects.getCount();
        
    	InputSystem inputSystem = sSystemRegistry.inputSystem;
        
        GameObjectCollisionSystem gameObjectCollisionSystem = sSystemRegistry.gameObjectCollisionSystem;
        
//		float gameTime = sSystemRegistry.timeSystem.getGameTime();  
//        if (gameTime > (mLastWeaponChangeTime + 0.05f)) {
////        if (gameTime > (mLastWeaponChangeTime + 0.2f)) {
//        	mWeaponChange = true;
//        }
        
//        Log.i("Object", "GameObjectManager update() totalActiveObjects = " + totalActiveObjects);
       
        if (totalActiveObjects > 0) {
            final Object[] objectArray = objects.getArray();
            int count = 0;
            int index = 0;
            
            while (count < totalActiveObjects) {
                GameObject gameObject = (GameObject)objectArray[index];
                
                if (gameObject != null) {
                	
                	if (!gameObject.backgroundGroup && gameObject.group != Group.DROID_LASER) {
                		mMaxObjectCount++;
                	}
                	
            		// FIXME Change this to a switch() statement?
                	if (gameObject.backgroundGroup) {
//                	if (gameObject.group == Group.BACKGROUND) {
//                    	if (gameObject.backgroundType == true) {
                		
                    	/* FIXME Check LeftBottom and RightTop enough, or check all four vertices of rectangle shape?
                    	 * A: Generally there is an entry and exit point for each Section, so just place vertices there */
                    	// FIXME Re-study abs(distance) < activationRadius vs distance2 < activationRadius2 vs sin(distance) calc
                    	final float distanceStart = gameObject.backgroundRadius.backgroundDistanceStart(mCameraFocus);
                    	final float distanceEnd = gameObject.backgroundRadius.backgroundDistanceEnd(mCameraFocus);
                    		
                    	// Check Background Radius using StartPoint (Quadrant3) and Endpoint (Quadrant1)
                    	if ((distanceStart < BACKGROUND_ACTIVATION_RADIUS2) || (distanceEnd < BACKGROUND_ACTIVATION_RADIUS2)) {
                        	gameObject.update(timeDelta, this);
                        	index++;
                    	} else {
                        	// Move object to end of active object list, remove object, then add to inactive list
                    		objects.moveToLast(index);
                        	objects.removeLast();
                        	
                      	  	mInactiveObjects.add((BaseObject)gameObject);
                    	}
                    } else if (gameObject.group == Group.DROID_WEAPON) {                    	
//                    	InputSystem inputSystem = sSystemRegistry.inputSystem;
                		HudSystem hud = sSystemRegistry.hudSystem;
                		GameObject buttonWeaponPressedGameObject = null;
                		GameObject buttonWeaponUnpressedGameObject = null;
                		boolean weapon1Press = false;
                		
                    	if (inputSystem.touchWeapon1Press || inputSystem.touchWeapon2Press) {
//                    	if (mWeaponChange && (inputSystem.touchWeapon1Press || inputSystem.touchWeapon2Press)) {
//                    	if (inputSystem.touchWeapon1Press || inputSystem.touchWeapon2Press) {                    		
                			if (inputSystem.touchWeapon1Press) {
                				weapon1Press = true;
                        		buttonWeaponPressedGameObject = mWeapon1GameObject;
                        		buttonWeaponUnpressedGameObject = mWeapon2GameObject;
//                        		buttonWeaponPressedGameObject = hud.getButtonWeapon1GameObject();
//                        		buttonWeaponUnpressedGameObject = hud.getButtonWeapon2GameObject();

                			} else {
                        		buttonWeaponPressedGameObject = mWeapon2GameObject;
                        		buttonWeaponUnpressedGameObject = mWeapon1GameObject;
//                        		buttonWeaponPressedGameObject = hud.getButtonWeapon2GameObject();
//                        		buttonWeaponUnpressedGameObject = hud.getButtonWeapon1GameObject();
                                
                			}
                			
                			GameObject collisionWeaponGameObject = gameObjectCollisionSystem.getCollisionWeaponGameObject();
                			
                			if (collisionWeaponGameObject != null) {
                				if (gameObject.gameObjectId == collisionWeaponGameObject.gameObjectId) {                            		
    								gameObject.activeWeapon = true;
    								gameObject.inventoryWeapon = false;
    								
        							if (weapon1Press) {
        								mWeapon1GameObject = gameObject;
//        								hud.setButtonWeapon1GameObjectType(gameObject.type);
//        								hud.setButtonWeapon1GameObject(gameObject);
        							} else {
        								mWeapon2GameObject = gameObject;
//        								hud.setButtonWeapon2GameObjectType(gameObject.type);
//        								hud.setButtonWeapon2GameObject(gameObject);
        							}
    								
    								gameObject.update(timeDelta, this);
    								index++;
    								
                				} else if ((buttonWeaponPressedGameObject != null && gameObject.gameObjectId == buttonWeaponPressedGameObject.gameObjectId)) {                					
    								gameObject.activeWeapon = false;
    								gameObject.inventoryWeapon = false;
                					
                					gameObject.update(timeDelta, this);
                					index++;
                					
                				} else if ((buttonWeaponUnpressedGameObject != null && gameObject.gameObjectId == buttonWeaponUnpressedGameObject.gameObjectId)) {
    								gameObject.activeWeapon = false;
    								gameObject.inventoryWeapon = true;
                					
                					gameObject.update(timeDelta, this);
                					index++;
                					
                    			} else if (checkActivationDistance(gameObject)) {
    								gameObject.activeWeapon = false;
    								gameObject.inventoryWeapon = false;
    								
                					gameObject.update(timeDelta, this);
                					index++;
                					
                				} else {
    								gameObject.activeWeapon = false;
    								gameObject.inventoryWeapon = false;
    								
                                    objects.moveToLast(index);
                                    objects.removeLast();
                                    mInactiveObjects.add((BaseObject)gameObject);
                                    
                				}
                			} else if (buttonWeaponPressedGameObject != null && gameObject.gameObjectId == buttonWeaponPressedGameObject.gameObjectId) {
            					gameObject.activeWeapon = true;
            					gameObject.inventoryWeapon = false;
            					
        						gameObject.update(timeDelta, this);
        						index++;
        						
                			} else if (buttonWeaponUnpressedGameObject != null && gameObject.gameObjectId == buttonWeaponUnpressedGameObject.gameObjectId) {
            					gameObject.activeWeapon = false;
            					gameObject.inventoryWeapon = true;
            					
        						gameObject.update(timeDelta, this);
        						index++;
        						
                			} else if (checkActivationDistance(gameObject)) {
            					gameObject.activeWeapon = false;
            					gameObject.inventoryWeapon = false;
            					
            					gameObject.update(timeDelta, this);
            					index++;
            					
                			} else {
            					gameObject.activeWeapon = false;
            					gameObject.inventoryWeapon = false;
            					
                				objects.moveToLast(index);
                				objects.removeLast();
                				mInactiveObjects.add((BaseObject)gameObject);
                			}
                		} else {
                			if (gameObject.activeWeapon || gameObject.inventoryWeapon) {
        						gameObject.update(timeDelta, this);
        						index++;
                			} else if (checkActivationDistance(gameObject)) {
        						gameObject.update(timeDelta, this);
        						index++;
                			} else {
                            	objects.moveToLast(index);
                            	objects.removeLast();
                            	mInactiveObjects.add((BaseObject)gameObject);
                			}
                		}
                    } else if (gameObject.group == Group.DROID_LASER) {
                    	if (!gameObject.gameObjectInactive && checkActivationDistance(gameObject)) {
                    		mMaxLaserCount++;
                    		
                        	gameObject.update(timeDelta, this);
                        	index++;
                        } else {
                        	gameObject.gameObjectInactive = false;
                        	gameObject.soundPlayed = false;
                        	gameObject.currentState = CurrentState.MOVE;
                        	gameObject.hitReactType = Type.INVALID;
                        	
                        	objects.moveToLast(index);
                        	GameObject inactiveGameObject = (GameObject)objects.removeLast();
                        	
                        	switch(gameObject.type) {
                      		case DROID_LASER_STD:
                      			mDroidLaserStdObjects.add(inactiveGameObject);
                      			break;
                      		
                          	case DROID_LASER_PULSE:
                      			mDroidLaserPulseObjects.add(inactiveGameObject);
                          		break;
                          		
                          	case DROID_LASER_EMP:
                      			mDroidLaserEmpObjects.add(inactiveGameObject);
                          		break;
                          		
                          	case DROID_LASER_GRENADE:
                      			mDroidLaserGrenadeObjects.add(inactiveGameObject);
                          		break;
                          		
                          	case DROID_LASER_ROCKET:
                      			mDroidLaserRocketObjects.add(inactiveGameObject);
                          		break;
                          		
                          	default:
                          		break;
                    		}
                        }
                    } else if (gameObject.group == Group.ENEMY_LASER) {
                    	if (!gameObject.gameObjectInactive && checkActivationDistance(gameObject)) {      
                        	gameObject.update(timeDelta, this);
                        	index++;
                        } else {
                        	gameObject.gameObjectInactive = false;
                        	gameObject.soundPlayed = false;
                        	gameObject.currentState = CurrentState.MOVE;
                        	gameObject.hitReactType = Type.INVALID;
                        	
                        	objects.moveToLast(index);
                        	GameObject inactiveGameObject = (GameObject)objects.removeLast();
                        	
                        	switch(gameObject.type) {
                      		case ENEMY_LASER_STD:
                      			mEnemyLaserStdObjects.add(inactiveGameObject);
                      			break;
                          		
                          	case ENEMY_LASER_EMP:
                      			mEnemyLaserEmpObjects.add(inactiveGameObject);
                          		break;
                          		
                      		case ENEMY_BOSS_LASER_STD:
                      			mEnemyLaserBossObjects.add(inactiveGameObject);
                      			break;
                          		
                          	case ENEMY_BOSS_LASER_EMP:
                          		mEnemyLaserBossObjects.add(inactiveGameObject);
                          		break;
                          		
                          	case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
                      			mEnemyLaserBossObjects.add(inactiveGameObject);
                          		break;
                          		
                          	default:
                          		break;
                    		}
                        }
                	} else if (gameObject.group == Group.ENEMY || gameObject.group == Group.ASTRONAUT) {
                        if (checkActivationDistance(gameObject)) {
//                      if (checkActivationDistance(gameObject) && !gameObject.markedForDeath) {
                        	if (checkAttackDistance(gameObject) && gameObject.currentState == CurrentState.LEVEL_START) {
                        		gameObject.collisionAttackRadius = true;
                        		gameObject.currentState = CurrentState.MOVE;
                        	} else if (gameObject.currentState == CurrentState.MOVE) {
                        		gameObject.collisionAttackRadius = false;
                        		gameObject.currentState = CurrentState.LEVEL_START;
                        	}
                        	
                        	gameObject.update(timeDelta, this);
                        	index++;
                        } else {
                        	gameObject.collisionAttackRadius = false;
                        	
                        	objects.moveToLast(index);
                        	objects.removeLast();
                        	
                        	if (gameObject.destroyOnDeactivation) {
                        		mMarkedForDeathObjects.add(gameObject);	
                        	} else {
                        		mInactiveObjects.add((BaseObject)gameObject);
                        	}
                        }
                    } else {
                        if (checkActivationDistance(gameObject)) {                        	
                        	gameObject.update(timeDelta, this);
                        	index++;
                        } else {                      	
                        	objects.moveToLast(index);
                        	objects.removeLast();
                        	
                        	if (gameObject.destroyOnDeactivation) {
                        		mMarkedForDeathObjects.add(gameObject);	
                        	} else {
                        		mInactiveObjects.add((BaseObject)gameObject);
                        	}
                        }
                    }
                } else {
                	Log.e("Object", "GameObjectManager gameObject = NULL");
                }
                count++;
            }
        }
        
//        final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//    	if (gameTime > (mCounterTimer + 10.0f)) {
//    		Log.i("GameObjectCounter", "Max Laser Object (One Loop) = " + mMaxLaserCount);
//    		Log.i("GameObjectCounter", "Max Game Object Except Laser (One Loop) = " + mMaxObjectCount);
//    		
//    		mCounterTimer = gameTime;
//    	}
        
        inputSystem.touchWeapon1Press = false;
        inputSystem.touchWeapon2Press = false;
        
        gameObjectCollisionSystem.setCollisionWeaponGameObjectNull();
        
//        if (mWeaponChange) {
//    		mLastWeaponChangeTime = gameTime;
//    		mWeaponChange = false;
//        }

    	// TODO With correct Activate/Inactivate system always in ascending order, is sort() still required?
        mInactiveObjects.sort(false);
////      mInactiveObjects.sort(true);
        final int totalInactiveObjects = mInactiveObjects.getCount();
        
//        Log.i("Object", "GameObjectManager update() totalInactiveObjects = " + totalInactiveObjects);
        
        if (totalInactiveObjects > 0) {
//            mInactiveObjects.sort(true);
            
            final Object[] inactiveArray = mInactiveObjects.getArray();
            int count = 0;
            int index = 0;
            
            // FIXME Change back to int i = totalInactiveObjects - 1; i >=0; i--
            while (count < totalInactiveObjects) {
//            for (int i = 0; i < inactiveCount; i++) {
//            for (int i = inactiveCount - 1; i >= 0; i--) {
                GameObject gameObject = (GameObject)inactiveArray[index];
                
                if (gameObject != null) {            		
                    if (gameObject.backgroundGroup) {                    	
                    	final float distanceStart = gameObject.backgroundRadius.backgroundDistanceStart(mCameraFocus);
                    	final float distanceEnd = gameObject.backgroundRadius.backgroundDistanceEnd(mCameraFocus);
                    	
                    	// distance1 left/bottom behind, distance2 right/top in front of 45 degree view angle
                    	if ((distanceStart < BACKGROUND_ACTIVATION_RADIUS2) || (distanceEnd < BACKGROUND_ACTIVATION_RADIUS2)) {
                        	gameObject.update(timeDelta, this);
                        	
                        	mInactiveObjects.moveToLast(index);
                    		mInactiveObjects.removeLast();
                      	  	objects.add(gameObject);
                    	} else {
                    		index++;
                    	}
                    } else {               
                    	if (checkActivationDistance(gameObject)) {         
//                    	if (checkActivationDistance(gameObject) && !gameObject.markedForDeath) {
                            gameObject.update(timeDelta, this);
                            
                            mInactiveObjects.moveToLast(index);
                            mInactiveObjects.removeLast();
                            objects.add(gameObject);
                        } else {
                        	index++;
                        }
                    }	
                } else {
                	Log.e("GameObjectManager", "gameObject = NULL");
                }
                count++;
            }
        }
        mVisitingGraph = false;
    }
    
    @Override
    public void add(BaseObject object) {    	
        if (object instanceof GameObject) {        	
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//            GameObject gameObject = (GameObject)object;
//            Log.i("Object", "GameObjectManager add()" + " [" + gameObject.gameObjectId + "] " +
//            		"gameObject allocated, count, concrete count = " + gameObject.getAllocated() + ", " +
//            		gameObject.getCount() + ", " + gameObject.getConcreteCount());
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        	
            super.add(object);
        } else {
            Log.e("Object", "GameObjectManager add() NOT instanceof GameObject");
        }
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public void moveToInactive(BaseObject object) {  	
    	if (object instanceof GameObject) {
//        	Log.i("Object", "GameObjectManager moveToInactive()" + " [" + ((GameObject)object).gameObjectId + "] ");
        	
  	  		mInactiveObjects.add(object);  	  	
      	} else {
      		Log.e("Object", "GameObjectManager moveToInactive() NOT instanceof GameObject");
      	}
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
     
    @Override
    public void remove(BaseObject object) {
    	if (object instanceof GameObject) {
//        	Log.i("Object", "GameObjectManager remove()" + " [" + ((GameObject)object).gameObjectId + "] "); 	  	
      	} else {
      		Log.e("Object", "GameObjectManager remove() NOT instanceof GameObject");
      	}
    	
        super.remove(object);
    }
    
    public void destroy(GameObject object) {
    	if (GameParameters.debug) {
        	Log.i("Object", "GameObjectManager destroy()" + " [" + object.gameObjectId + "] ");	
    	}
    	
        mMarkedForDeathObjects.add(object);
        remove(object);
    }
    
    public void destroyAll() {
        assert mVisitingGraph == false;
        commitUpdates();
        
        FixedSizeArray<BaseObject> objects = getObjects();
        final int objectCount = objects.getCount();
        
//        for (int i = 0; i < count; i++) {
        for (int i = objectCount - 1; i >= 0; i--) {        	
            mMarkedForDeathObjects.add((GameObject)objects.get(i));
            objects.remove(i);
        }
        
        final int inactiveObjectCount = mInactiveObjects.getCount();
        
    	if (GameParameters.debug) {
            Log.i("Object", "GameObjectManager destroyAll() objectCount, inactiveObjectCount = " +
            		objectCount + ", " + inactiveObjectCount);	
    	}
        
//        for (int j = 0; j < inactiveObjectCount; j++) {
        for (int j = inactiveObjectCount - 1; j >= 0; j--) {        	
            mMarkedForDeathObjects.add((GameObject)mInactiveObjects.get(j));
            mInactiveObjects.remove(j);
        }
        
        if (mDroidLaserStdObjects != null) {
            final int laserStdObjectCount = mDroidLaserStdObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserStdObjects.getCount() = " + laserStdObjectCount);	
        	}
            
//            for (int k = 0; k < laserStdObjectCount; k++) {
            for (int k = laserStdObjectCount - 1; k >= 0; k--) {            	
                mMarkedForDeathObjects.add(mDroidLaserStdObjects.get(k));
                mDroidLaserStdObjects.remove(k);
            }	
        }
        
        if (mDroidLaserPulseObjects != null) {
            final int laserPulseObjectCount = mDroidLaserPulseObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserPulseObjects.getCount() = " + laserPulseObjectCount);	
        	}
            
//            for (int l = 0; l < laserPulseObjectCount; l++) {
            for (int l = laserPulseObjectCount - 1; l >= 0; l--) {            	
                mMarkedForDeathObjects.add(mDroidLaserPulseObjects.get(l));
                mDroidLaserPulseObjects.remove(l);
            }	
        }
        
        if (mDroidLaserEmpObjects != null) {
            final int laserEmpObjectCount = mDroidLaserEmpObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserEmpObjects.getCount() = " + laserEmpObjectCount);	
        	}
            
//            for (int m = 0; m < laserEmpObjectCount; m++) {
            for (int m = laserEmpObjectCount - 1; m >= 0; m--) {            	
                mMarkedForDeathObjects.add(mDroidLaserEmpObjects.get(m));
                mDroidLaserEmpObjects.remove(m);
            }	
        }
        
        if (mDroidLaserGrenadeObjects != null) {
            final int laserGrenadeObjectCount = mDroidLaserGrenadeObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserGrenadeObjects.getCount() = " + laserGrenadeObjectCount);	
        	}
            
            for (int k = laserGrenadeObjectCount - 1; k >= 0; k--) {            	
                mMarkedForDeathObjects.add(mDroidLaserGrenadeObjects.get(k));
                mDroidLaserGrenadeObjects.remove(k);
            }	
        }
        
        if (mDroidLaserRocketObjects != null) {
            final int laserRocketObjectCount = mDroidLaserRocketObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserRocketObjects.getCount() = " + laserRocketObjectCount);	
        	}
            
            for (int k = laserRocketObjectCount - 1; k >= 0; k--) {            	
                mMarkedForDeathObjects.add(mDroidLaserRocketObjects.get(k));
                mDroidLaserRocketObjects.remove(k);
            }	
        }
        
        if (mEnemyLaserStdObjects != null) {
            final int laserStdObjectCount = mEnemyLaserStdObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserStdObjects.getCount() = " + laserStdObjectCount);	
        	}
            
//            for (int k = 0; k < laserStdObjectCount; k++) {
            for (int k = laserStdObjectCount - 1; k >= 0; k--) {            	
                mMarkedForDeathObjects.add(mEnemyLaserStdObjects.get(k));
                mEnemyLaserStdObjects.remove(k);
            }	
        }
        
        if (mEnemyLaserEmpObjects != null) {
            final int laserEmpObjectCount = mEnemyLaserEmpObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserEmpObjects.getCount() = " + laserEmpObjectCount);	
        	}
            
//            for (int m = 0; m < laserEmpObjectCount; m++) {
            for (int m = laserEmpObjectCount - 1; m >= 0; m--) {            	
                mMarkedForDeathObjects.add(mEnemyLaserEmpObjects.get(m));
                mEnemyLaserEmpObjects.remove(m);
            }	
        }
        
        if (mEnemyLaserBossObjects != null) {
            final int laserPulseObjectCount = mEnemyLaserBossObjects.getCount();
            
        	if (GameParameters.debug) {
                Log.i("Object", "GameObjectManager destroyAll() mLaserPulseObjects.getCount() = " + laserPulseObjectCount);	
        	}
            
//            for (int l = 0; l < laserPulseObjectCount; l++) {
            for (int l = laserPulseObjectCount - 1; l >= 0; l--) {            	
                mMarkedForDeathObjects.add(mEnemyLaserBossObjects.get(l));
                mEnemyLaserBossObjects.remove(l);
            }	
        }
        
//        if (mExplosionStep1Objects != null) {
//            final int mExplosionObjectCount = mExplosionStep1Objects.getCount();
//            
//            Log.i("Object", "GameObjectManager destroyAll() mExplosionStep1Objects.getCount() = " + mExplosionObjectCount);
//            
////            for (int n = 0; n < mExplosionObjectCount; n++) {
//            for (int n = mExplosionObjectCount - 1; n >= 0; n--) {
//                mMarkedForDeathObjects.add(mExplosionStep1Objects.get(n));
//                mExplosionStep1Objects.remove(n);
//                
//                mMarkedForDeathObjects.add(mExplosionStep2Objects.get(n));
//                mExplosionStep2Objects.remove(n);
//                
//                mMarkedForDeathObjects.add(mExplosionStep3Objects.get(n));
//                mExplosionStep3Objects.remove(n);
//                
//                mMarkedForDeathObjects.add(mExplosionStep4Objects.get(n));
//                mExplosionStep4Objects.remove(n);
//            }
//        }
        
        // XXX Previous Test. Moved here from commitUpdates().
//        commitUpdates();
//        
//        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//        final int objectsToKillCount = mMarkedForDeathObjects.getCount();
//        
//        if (factory != null && objectsToKillCount > 0) {
//            final Object[] deathArray = mMarkedForDeathObjects.getArray();
//            for (int x = 0; x < objectsToKillCount; x++) {        	
//                factory.destroy((GameObject)deathArray[x]);
//            }
//            mMarkedForDeathObjects.clear();
//        }
    }
    
//    public void reloadDrawableObjects(GL11 gl11) {
////    	// Reload Active Drawable Objects
////        FixedSizeArray<BaseObject> activeObjects = getObjects();
////        if (activeObjects != null) {
////            final int totalActiveObjects = activeObjects.getCount();
////            final Object[] activeObjectArray = activeObjects.getArray();
////
////            for(int i = 0; i < totalActiveObjects; i++) {
////            	GameObject gameObject = (GameObject)activeObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////        // Reload Inactive Drawable Objects
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
//        
//        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//        factory.reloadDrawables(gl11);
//        
//        // FIXME 12/5/12 ADD THIS CODE. OTHER OBJECTS TO RELOAD? FARBACKGROUND, SPECIAL EFFECTS, OTHERS
////        // Reload Laser Drawable Objects
////    	mDroidLaserStdObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mDroidLaserPulseObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mDroidLaserEmpObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mDroidLaserGrenadeObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mDroidLaserRocketObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mEnemyLaserStdObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mEnemyLaserEmpObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
////        
////    	mEnemyLaserBossObjects;
////        if (mInactiveObjects != null) {
////            final int totalInactiveObjects = mInactiveObjects.getCount();
////            final Object[] inactiveObjectArray = mInactiveObjects.getArray();
////            
////            for(int i = 0; i < totalInactiveObjects; i++) {
////            	GameObject gameObject = (GameObject)inactiveObjectArray[i];
////            	gameObject.drawableDroid.reloadObjectVBO(gl11);
////            }
////        }
//    }
    
    private boolean checkActivationDistance(GameObject gameObject) {
        boolean withinDistance = false;
        final float activationRadius = gameObject.activationRadius;
         
    	float distance2 = mCameraFocus.distance2(gameObject.currentPosition);
    	
        if ((distance2 < (activationRadius * activationRadius)) || activationRadius == -1) {
        	withinDistance = true;
        }
        
    	return withinDistance;
    }
    
    private boolean checkAttackDistance(GameObject gameObject) {
        boolean withinDistance = false;
        final float attackRadius = gameObject.attackRadius;
         
    	float distance2 = mCameraFocus.distance2(gameObject.currentPosition);
    	
        if ((distance2 < (attackRadius * attackRadius)) || attackRadius == -1) {
        	withinDistance = true;
        }
        
    	return withinDistance;
    }
    
    public void activateLaserGameObject(Type laserType, float x, float y, float z, float r) {
    	GameObject gameObject;
    	
    	switch(laserType) {
  		case DROID_LASER_STD:  			
  			gameObject = (GameObject)mDroidLaserStdObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			} else {
  				Log.e("Loop", "GameObjectManager activateLaserGameObject() Laser gameObject = NULL!");
  			}
  			
  			break;
  		
      	case DROID_LASER_PULSE:
  			gameObject = (GameObject)mDroidLaserPulseObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			}
  			
      		break;
      		
      	case DROID_LASER_EMP:
  			gameObject = (GameObject)mDroidLaserEmpObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			}
  			
      		break;
      		
  		case DROID_LASER_GRENADE:  			
  			gameObject = (GameObject)mDroidLaserGrenadeObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			
  	  			gameObject.yValueBeforeMove = gameObject.currentPosition.y;
  	  			gameObject.yMoveDistance = -0.583f;
  	  			gameObject.yMoveMagnitude = -0.01f;
  	  			
  	  			add(gameObject);	
  			} else {
  				Log.e("Loop", "GameObjectManager activateLaserGameObject() Laser gameObject = NULL!");
  			}
  			
  			break;
  			
  		case DROID_LASER_ROCKET:  			
  			gameObject = (GameObject)mDroidLaserRocketObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			} else {
  				Log.e("Loop", "GameObjectManager activateLaserGameObject() Laser gameObject = NULL!");
  			}
  			
  			break;
  			
  		case ENEMY_LASER_STD:  			
  			gameObject = (GameObject)mEnemyLaserStdObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			} else {
  				Log.e("Loop", "GameObjectManager activateLaserGameObject() Laser gameObject = NULL!");
  			}
  			
  			break;
      		
      	case ENEMY_LASER_EMP:
  			gameObject = (GameObject)mEnemyLaserEmpObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			}
  			
      		break;
      		
      	case ENEMY_BOSS_LASER_STD:
  			gameObject = (GameObject)mEnemyLaserBossObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			}
  			
      	case ENEMY_BOSS_LASER_EMP:
  			gameObject = (GameObject)mEnemyLaserBossObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			}
      		
      	case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
  			gameObject = (GameObject)mEnemyLaserBossObjects.removeLast();
  			
  			if (gameObject != null) {
  	  			gameObject.currentPosition.set(x, y, z, r);
  	  			add(gameObject);	
  			}
  			
      		break;
      		
      	default:
      		break;
		}
    }
    
//    public void activateSpecialEffectGameObject(Type specialEffectType, Vector3 specialEffectPosition) {
//    	GameObject gameObjectStep1;
//    	GameObject gameObjectStep2;
//    	GameObject gameObjectStep3;
//    	GameObject gameObjectStep4;
//    	
//    	switch(specialEffectType) {
//  		case EXPLOSION:  			
//  			gameObjectStep1 = (GameObject)mExplosionStep1Objects.removeLast();
//  			gameObjectStep2 = (GameObject)mExplosionStep2Objects.removeLast();
//  			gameObjectStep3 = (GameObject)mExplosionStep3Objects.removeLast();
//  			gameObjectStep4 = (GameObject)mExplosionStep4Objects.removeLast();
//  			
//  			if (gameObjectStep1 != null && gameObjectStep2 != null && gameObjectStep3 != null && gameObjectStep4 != null) {
//  	  			gameObjectStep1.currentPosition.set(specialEffectPosition);
//  	  			gameObjectStep2.currentPosition.set(specialEffectPosition);
//  	  			gameObjectStep3.currentPosition.set(specialEffectPosition);
//  	  			gameObjectStep4.currentPosition.set(specialEffectPosition);
//  	  			
//  	  			add(gameObjectStep1);
//  	  			add(gameObjectStep2);
//  	  			add(gameObjectStep3);
//  	  			add(gameObjectStep4);
//  			} else {
//  				Log.e("Loop", "GameObjectManager activateSpecialEffectGameObject() Special Effect gameObject = NULL!");
//  			}
//  			
//  			break;
//      		
//      	default:
//      		break;
//		}
//    }
    
	public GameObject getWeapon1GameObject() {
		return mWeapon1GameObject;
	}
	
	public void setWeapon1GameObject(GameObject weapon1) {
		mWeapon1GameObject = weapon1;
	}
	
	public GameObject getWeapon2GameObject() {
		return mWeapon2GameObject;
	}
	
	public void setWeapon2GameObject(GameObject weapon2) {
		mWeapon2GameObject = weapon2;
	}
	
	public Type getWeapon1GameObjectType() {
		if(mWeapon1GameObject != null) {
			return mWeapon1GameObject.type;	
		} else {
			return null;
		}
	}
	
	public Type getWeapon2GameObjectType() {
		if(mWeapon2GameObject != null) {
			return mWeapon2GameObject.type;	
		} else {
			return null;
		}
	}
    
    public void setLaserGameObjectArray(Type laserType, int max, FixedSizeArray<GameObject> laserObjectArray) {
    	switch(laserType) {
  		case DROID_LASER_STD:
  			mDroidLaserStdObjects = new FixedSizeArray<GameObject>(max); 		
  			mDroidLaserStdObjects = laserObjectArray;
  			
  			break;
  		
      	case DROID_LASER_PULSE:
  			mDroidLaserPulseObjects = new FixedSizeArray<GameObject>(max);			
  			mDroidLaserPulseObjects = laserObjectArray;
      		break;
      		
      	case DROID_LASER_EMP:
  			mDroidLaserEmpObjects = new FixedSizeArray<GameObject>(max);  		
  			mDroidLaserEmpObjects = laserObjectArray;
      		break;
      		
      	case DROID_LASER_GRENADE:
  			mDroidLaserGrenadeObjects = new FixedSizeArray<GameObject>(max);  		
  			mDroidLaserGrenadeObjects = laserObjectArray;
      		break;
      		
      	case DROID_LASER_ROCKET:
  			mDroidLaserRocketObjects = new FixedSizeArray<GameObject>(max);  		
  			mDroidLaserRocketObjects = laserObjectArray;
      		break;
      		
  		case ENEMY_LASER_STD:
  			mEnemyLaserStdObjects = new FixedSizeArray<GameObject>(max); 		
  			mEnemyLaserStdObjects = laserObjectArray;
  			
  			enemyLaserStdLoaded = true;
  			
  			break;
  			
      	case ENEMY_LASER_EMP:
  			mEnemyLaserEmpObjects = new FixedSizeArray<GameObject>(max);  		
  			mEnemyLaserEmpObjects = laserObjectArray;
  			
  			enemyLaserEmpLoaded = true;
  			
      		break;
      		
      	case ENEMY_BOSS_LASER_STD:
  			mEnemyLaserBossObjects = new FixedSizeArray<GameObject>(max);			
  			mEnemyLaserBossObjects = laserObjectArray;
      		break;
      		
      	case ENEMY_BOSS_LASER_EMP:
  			mEnemyLaserBossObjects = new FixedSizeArray<GameObject>(max);			
  			mEnemyLaserBossObjects = laserObjectArray;
      		break;
  		
      	case ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY:
  			mEnemyLaserBossObjects = new FixedSizeArray<GameObject>(max);			
  			mEnemyLaserBossObjects = laserObjectArray;
      		break;
      		
      	default:
      		break;
		}
    }
    
//    public void setSpecialEffectGameObjectArray(Type specialEffectType, int max, FixedSizeArray<GameObject> specialEffectStep1ObjectArray,
//    		FixedSizeArray<GameObject> specialEffectStep2ObjectArray, FixedSizeArray<GameObject> specialEffectStep3ObjectArray,
//    		FixedSizeArray<GameObject> specialEffectStep4ObjectArray) {
//    	
//      	switch(specialEffectType) {
//    		case EXPLOSION:
//    			mExplosionStep1Objects = new FixedSizeArray<GameObject>(max);
//    			mExplosionStep2Objects = new FixedSizeArray<GameObject>(max);
//    			mExplosionStep3Objects = new FixedSizeArray<GameObject>(max);
//    			mExplosionStep4Objects = new FixedSizeArray<GameObject>(max);
//    			
//    			mExplosionStep1Objects = specialEffectStep1ObjectArray;
//    			mExplosionStep2Objects = specialEffectStep2ObjectArray;
//    			mExplosionStep3Objects = specialEffectStep3ObjectArray;
//    			mExplosionStep4Objects = specialEffectStep4ObjectArray;
//    			
//    			break;
//        		
//        	default:
//        		break;
//  		}
//      }
    
//    public void setPlayer(GameObject player) {    	
//        mPlayer = player;
//    }
//    
//    public GameObject getPlayer() {
//        return mPlayer;
//    }
    
    public void checkObjects() {
    	commitUpdates();
    	
    	if (GameParameters.debug) {
	        FixedSizeArray<BaseObject> objects = getObjects();
	        final int objectCount = objects.getCount();
        
            Log.i("Object", "GameObjectManager checkObjects() objects.getCount() = " + objectCount);	
        
	//        for (int i = 0; i < count; i++) {
	        for (int i = objectCount - 1; i >= 0; i--) {
	        	GameObject gameObject = (GameObject)objects.get(i);
	        	if(gameObject != null) {
	            	Log.i("Object", "GameObjectManager checkObjects() objects gameObjectId = " + gameObject.gameObjectId);
	        	} else {
	            	Log.e("Object", "GameObjectManager checkObjects() objects gameObject = NULL");
	        	}
	        }
    	}
    }

    /** Comparator for game objects objects. */
    private static final class GameObjectComparator implements Comparator<BaseObject> {
        public int compare(BaseObject object1, BaseObject object2) {
            int result = 0;
            if (object1 == null && object2 != null) {
                result = 1;
            } else if (object1 != null && object2 == null) {
                result = -1;
            } else if (object1 != null && object2 != null) {
                result = ((GameObject) object1).gameObjectId - ((GameObject) object2).gameObjectId;
            }
        	
            return result;
        }
    }
}
