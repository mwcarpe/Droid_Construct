/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
//import java.util.Random;
import android.util.Log;

import com.frostbladegames.basestation9.GameComponent.ComponentPhases;
import com.frostbladegames.basestation9.GameObjectGroups.BottomMoveType;
import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;

public class SplashScreenComponent extends GameComponent {

    public int priority;
    public boolean cameraRelative;

    private Vector3 mScreenLocation;
    private Vector3 mDrawOffset;
    
    private int mRZ;
    
    public SplashScreenComponent() {
        super();
        setPhase(ComponentPhases.DRAW.ordinal());
        
        mScreenLocation = new Vector3();
        mDrawOffset = new Vector3();
        reset();
    }
    
    @Override
    public void reset() {
        priority = 0;
        cameraRelative = true;
        mDrawOffset.zero();
        
        mRZ = 0;
    }

    public void update(float timeDelta, BaseObject parent) {
        SplashScreenRenderSystem render = sSystemRegistry.splashScreenRenderSystem;
//        RenderSystem render = sSystemRegistry.renderSystem;
        
        GameObject object = (GameObject)parent;
        
        int objectId = object.gameObjectId;
        
        cameraRelative = true;
        
        render.scheduleForDraw(object.drawableDroid, object.currentPosition, mDrawOffset, 
        		(float)mRZ, priority, cameraRelative, object.type, objectId);
    }
    
    public void setDrawOffset(float x, float y, float z) {
        mDrawOffset.set(x, y, z);
    }
    
    public void setDrawOffset(Vector3 offset) {
        mDrawOffset.set(offset);
    }
}
