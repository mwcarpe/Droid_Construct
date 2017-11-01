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



//import com.frostbladegames.droidconstruct.DebugLog;

// FIXME DELETE THIS CLASS. Ref sInterpolate info as required. 
/**
 * A game component that implements velocity-based movement.
 */
public class MovementComponent extends GameComponent {
    // If multiple game components were ever running in different threads, this would need
    // to be non-static.
    private static Interpolator sInterpolator = new Interpolator();

    public MovementComponent() {
        super();
        setPhase(ComponentPhases.MOVEMENT.ordinal());
    }
    
    @Override
    public void reset() {
        
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
//        DebugLog.d("MovementComponent", "update()");
    	
        GameObject object = (GameObject) parent;

        /* FIXME Temporary code only. Note: this code is completely unnecessary, 
           just a placeholder to confirm read/write of object x,y */
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        // XXX Tried to set heading and position here. Instead set in InputGameInterface.
//        InputSystem input = sSystemRegistry.inputSystem;
//        final InputXY directionalPad = input.getTouchScreen();
        
        // TODO Re-enable. Temp code. Optimize.
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        float newX = object.getPosition().x;
//        float newY = object.getPosition().y;
//        float newZ = object.getPosition().z;
//        float newR = object.getPosition().r;
//        
//        newX += 1.0f;
//        newZ += 1.0f;
//
//        object.getPosition().set(newX, 0.0f, newZ, 0.0f);
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
        // TODO Check how positionLocked is set to false
//        if (object.positionLocked == false) {
//            object.getPosition().set(newX, newY);
//        }
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
        // TODO Re-enable - Velocity based movement
//        sInterpolator.set(object.getVelocity().x, object.getTargetVelocity().x,
//                object.getAcceleration().x);
//        float offsetX = sInterpolator.interpolate(timeDelta);
//        float newX = object.getPosition().x + offsetX;
//        float newVelocityX = sInterpolator.getCurrent();
//
//        sInterpolator.set(object.getVelocity().y, object.getTargetVelocity().y,
//                object.getAcceleration().y);
//        float offsetY = sInterpolator.interpolate(timeDelta);
//        float newY = object.getPosition().y + offsetY;
//        float newVelocityY = sInterpolator.getCurrent();
//
//        if (object.positionLocked == false) {
//            object.getPosition().set(newX, newY);
//        }
//        
//        object.getVelocity().set(newVelocityX, newVelocityY);
    }
}
