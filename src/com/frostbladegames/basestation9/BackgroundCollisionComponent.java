/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


/**
 * A component for background collision volumes checked every frame.
 * This component creates a bounding volume that encompasses the set and submits
 * it to the collision system.  Including this component in a game object will allow it to
 * send and receive hits to other game objects.
 */
public class BackgroundCollisionComponent extends GameComponent {
	private FixedSizeArray<LineSegmentCollisionVolume> mCollisionVolumes;
    private HitReactionComponent mHitReactionComponent;
    
    public BackgroundCollisionComponent() {
        super();
        
        setPhase(ComponentPhases.FRAME_END.ordinal());
        reset();
    }
    
    @Override
    public void reset() {
    	mCollisionVolumes = null;
    	mHitReactionComponent = null;     
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {

//    	GameObject parentObject = (GameObject)parent;
//    	Log.i("Loop", "BackgroundCollisionComponent update()" + " [" + parentObject.gameObjectId + "] ");
    	
        GameObjectCollisionSystem collision = sSystemRegistry.gameObjectCollisionSystem;
        if (collision != null) {            
            collision.registerForBackgroundCollisions((GameObject)parent, mHitReactionComponent, mCollisionVolumes);
            
        } else {
        	Log.e("BackgroundCollisionComponent", "update() collision = NULL");
        }
    }
    
    public void setHitReactionComponent(HitReactionComponent component) {
        mHitReactionComponent = component;
    }
    
    public void setCollisionVolumes(FixedSizeArray<LineSegmentCollisionVolume> collisionVolumes) {
    	mCollisionVolumes = collisionVolumes;
    }
}
