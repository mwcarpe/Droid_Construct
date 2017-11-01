/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.SoundSystem.Sound;

/** 
 * This component allows objects to die and be deleted when their life is reduced to zero or they
 * meet other configurable criteria.
 */
public class LifetimeComponent extends GameComponent {
    private boolean mDieWhenInvisible;
    private float mTimeUntilDeath;
    private Type mSpawnOnDeathType;
//    private GameObjectType mSpawnOnDeathType;
//    private LaunchProjectileComponent mTrackingSpawner;
    private Vector3 mHotSpotTestPoint;
//    private Vector2 mHotSpotTestPoint;
    private boolean mReleaseGhostOnDeath;
    private boolean mVulnerableToDeathTiles;
    private boolean mDieOnHitBackground;
    private Sound mDeathSound;
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
	private GameObject mBottomGameObject;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public LifetimeComponent() {
        super();
        mHotSpotTestPoint = new Vector3();
//        mHotSpotTestPoint = new Vector2();
        
        setPhase(ComponentPhases.MOVEMENT.ordinal());
//      setPhase(ComponentPhases.THINK.ordinal());
        
        reset();

    }
    
    @Override
    public void reset() {
        mDieWhenInvisible = false;
        mTimeUntilDeath = -1;
        mSpawnOnDeathType = Type.INVALID;
//        mTrackingSpawner = null;
        mHotSpotTestPoint.zero();
        mReleaseGhostOnDeath = true;
        mVulnerableToDeathTiles = false;
        mDieOnHitBackground = false;
        mDeathSound = null;
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;
        
//        Log.i("Loop", "LifetimeComponent update()" + " [" + parentObject.gameObjectId + "] ");
        
//        // FIXME Move this to individual <GameObject>Component. For e.g. DroidTopComponent, will no longer need .topObjectType flag
//        if (parentObject.topObjectType) {
//        	if (mBottomGameObject == null || mBottomGameObject.hitPoints <= 0.0f) {
//        		parentObject.hitPoints = 0.0f;
//        		die(parentObject);
//                return;
////        	} else if (mBottomGameObject.hitPoints <= 0.0f) {
////        		parentObject.hitPoints = 0.0f;
////                die(parentObject);
////                return;
//        	}
//        } else if (parentObject.hitPoints <= 0) {
//            die(parentObject);
//            return;
//        }
//        
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
//        
////        if (mDieWhenInvisible) {
////            CameraSystem camera = sSystemRegistry.cameraSystem;
////            ContextParameters context = sSystemRegistry.contextParameters;
////            final float dx = 
////                Math.abs(parentObject.getPosition().x - camera.getFocusPositionX());
////            final float dy = 
////                Math.abs(parentObject.getPosition().y - camera.getFocusPositionY());
////            if (dx > context.gameWidth || dy > context.gameHeight) {
////                // the position of this object is off the screen, destroy!
////                // TODO: this is a pretty dumb test.  We should have a bounding volume instead.
////                die(parentObject);
////                return;
////            }
////        }
//        
////        if (parentObject.life > 0 && mVulnerableToDeathTiles) {
////            HotSpotSystem hotSpot = sSystemRegistry.hotSpotSystem;
////            if (hotSpot != null) {
////                // TODO: HACK!  Unify all this code.
////                if (hotSpot.getHotSpot(parentObject.getCenteredPositionX(), 
////                        parentObject.getPosition().y + 10.0f) == HotSpotSystem.HotSpotType.DIE) {
////                    parentObject.life = 0;
////                }
////            }
////        }
//        
////        if (parentObject.life > 0 && mDieOnHitBackground) {
////        	if (parentObject.getBackgroundCollisionNormal().length2() > 0.0f) {
////        		parentObject.life = 0;
////        	}
////        }
//        
////	    DebugLog.d("Lifetime", "LifetimeComponent update():" + " [" + parentObject.getGameObjectId() + "] " + 
////	    		"parentObject.life = " + parentObject.life);
    }
    
    private void die(GameObject parentObject) {
    	
//    	DebugLog.d("Lifetime", "LifetimeComponent die()");
    	
//        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
//        GameObjectManager manager = sSystemRegistry.gameObjectManager;
//        
//        if (manager != null) {
//        	if (parentObject.group == Group.DROID || parentObject.group == Group.ITEM) {
//        		// FIXME TEMP. Ignore and let DroidBottomComponent re-start Droid at (55.625, 0, -55.625, 0) Red Pad Start
//        		if (parentObject.playerType) {
//        			parentObject.currentState = CurrentState.DEAD;
//        		}
//        	} else {
//                manager.destroy(parentObject);
//        	}
//        }
        
//        if (mReleaseGhostOnDeath) {
//            // TODO: This is sort of a hack.  Find a better way to do this without introducing a
//            // dependency between these two.  Generic on-death event or something.
//            GhostComponent ghost = parentObject.findByClass(GhostComponent.class);
//            if (ghost != null) {
//                ghost.releaseControl(parentObject);
//            }
//        }

//        if (mSpawnOnDeathType != GameObjectFactory.GameObjectType.INVALID) {
//            GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 0.0f, 
//                    parentObject.getPosition().z, 0.0f, parentObject.facingDirection.x < 0.0f);
////            GameObject object = factory.spawn(mSpawnOnDeathType, parentObject.getPosition().x, 
////                    parentObject.getPosition().y, parentObject.facingDirection.x < 0.0f);
//
//            if (object != null && manager != null) {
//                manager.add(object);
//            } 
//        }
        
//        if (mTrackingSpawner != null) {
//            mTrackingSpawner.trackedProjectileDestroyed();
//        }
        
//        if (mDeathSound != null) {
//        	SoundSystem sound = sSystemRegistry.soundSystem;
//        	if (sound != null) {
//        		sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL);
//        	}
//        }

    }
    
    public void setDieWhenInvisible(boolean die) {
        mDieWhenInvisible = die;
    }
    
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
    
//    public void setObjectToSpawnOnDeath(Type type) {
//        mSpawnOnDeathType = type;
//    }
    
//    public final void setTrackingSpawner(LaunchProjectileComponent spawner) {
//        mTrackingSpawner = spawner;
//    }
    
//    public final void setReleaseGhostOnDeath(boolean release) {
//        mReleaseGhostOnDeath = release;
//    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public void setBottomGameObject(GameObject bottomGameObject) {
    	mBottomGameObject = bottomGameObject;
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public final void setVulnerableToDeathTiles(boolean vulnerable) {
        mVulnerableToDeathTiles = vulnerable;
    }
    
    public final void setDieOnHitBackground(boolean die) {
    	mDieOnHitBackground = die;
    }
    
    public final void setDeathSound(Sound deathSound) {
    	mDeathSound = deathSound;
    }
}
