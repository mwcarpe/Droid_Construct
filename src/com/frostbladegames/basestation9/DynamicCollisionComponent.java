/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;

/**
 * A component to include dynamic collision volumes (such as those produced every frame from
 * animating sprites) in the dynamic collision world.  Given a set of "attack" volumes and 
 * "vulnerability" volumes (organized such that only attack vs vulnerability intersections result
 * in valid "hits"), this component creates a bounding volume that encompasses the set and submits
 * it to the dynamic collision system.  Including this component in a game object will allow it to
 * send and receive hits to other game objects.
 */
public class DynamicCollisionComponent extends GameComponent {
	private OBBCollisionVolume mCollisionVolume;
    private HitReactionComponent mHitReactionComponent;
    
    public DynamicCollisionComponent() {
        super();
        
//		DebugLog.d("AttackReceive", "DynamicCollisionComponent <contructor>()");
		
        mCollisionVolume = new OBBCollisionVolume();
//        mCollisionVolume = new OBBCollisionVolume(0.0f, 0.0f);
        
        setPhase(ComponentPhases.FRAME_END.ordinal());
        reset();
    }
    
    public DynamicCollisionComponent(float width, float depth) {
        super();
        
//		DebugLog.d("AttackReceive", "DynamicCollisionComponent <contructor>(width, depth)");
		
        mCollisionVolume = new OBBCollisionVolume(width, depth);
        
        setPhase(ComponentPhases.FRAME_END.ordinal());
        reset();
    }
    
    @Override
    public void reset() {
    	mCollisionVolume = null;

    	mHitReactionComponent = null;     
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
    	GameObject parentObject = (GameObject)parent;
    	
//    	Log.i("Loop", "DynamicCollisionComponent update()" + " [" + parentObject.gameObjectId + "] ");
    	
        GameObjectCollisionSystem collision = sSystemRegistry.gameObjectCollisionSystem;
        if (collision != null) {
        	if (parentObject.currentState != CurrentState.DEAD) {
        		if (!parentObject.activeWeapon && !parentObject.inventoryWeapon) { 
                    collision.registerForDynamicCollisions(parentObject, mHitReactionComponent, mCollisionVolume);
        		} else {
                	// Ignore collision for activeWeapon or inventoryWeapon
        		}
        	}
        } else {
        	Log.e("DynamicCollisionComponent", "update() GameObjectCollisionSystem = NULL");
        }
    }
    
    public void setHitReactionComponent(HitReactionComponent component) {
//		DebugLog.d("AttackReceive", "DynamicCollisionComponent setHitReactionComponent()");
		
        mHitReactionComponent = component;
    }
    
    public void setCollisionVolume(OBBCollisionVolume collisionVolume) {
//		DebugLog.d("AttackReceive", "DynamicCollisionComponent setCollisionVolume()");
		
        mCollisionVolume = collisionVolume;
    }
}
