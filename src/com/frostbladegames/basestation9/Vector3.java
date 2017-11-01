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



/**
 * 3D vector class.  Handles basic vector math for 3D vectors, including rotation.
 */
public final class Vector3 extends AllocationGuard {
    public float x;
    public float y;
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public float z;
    public float r;
    
//    public float oX;
//    public float oY;
//    public float oZ;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */

    // FIXME Where is this used?
    public static final Vector3 ZERO = new Vector3(0, 0, 0, 0);
//    public static final Vector3 ZERO = new Vector3(0, 0);
    
    public Vector3() {
        super();
    }

    public Vector3(float xValue, float yValue, boolean hudDummy) {
    	super();
        set(xValue, yValue, hudDummy);
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public Vector3(float xValue, float yValue, float zValue, float rValue) {
    	super();
        set(xValue, yValue, zValue, rValue);
    }
    
//    public Vector3(float xValue, float yValue, float zValue, float rValue, float oXValue, float oYValue, float oZValue) {
//    	super();
//        set(xValue, yValue, zValue, rValue, oXValue, oYValue, oZValue);
//    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public Vector3(Vector3 other) {
    	super();
        set(other);
    }

    public final void add(Vector3 other) {
        x += other.x;
        y += other.y;
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        z += other.z;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }
    
    public final void add(float otherX, float otherY) {
        x += otherX;
        y += otherY;
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public final void add(float otherX, float otherY, float otherZ) {
        x += otherX;
        y += otherY;
        z += otherZ;
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */

    public final void subtract(Vector3 other) {
        x -= other.x;
        y -= other.y;
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        z -= other.z;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }

    public final void multiply(float magnitude) {
        x *= magnitude;
        y *= magnitude;
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        z *= magnitude;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }
    
    public final void multiply(Vector3 other) {
        x *= other.x;
        y *= other.y;
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        z *= other.z;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }

    public final void divide(float magnitude) {
        if (magnitude != 0.0f) {
            x /= magnitude;
            y /= magnitude;
            /* XXX Droid Code © 2012 FrostBlade LLC - Start */
            z /= magnitude;
            /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        }
    }
 
    public final void set(Vector3 other) {        
        x = other.x;
        y = other.y;
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        z = other.z;
        r = other.r;
//        oX = other.oX;
//        oY = other.oY;
//        oZ = other.oZ;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }  
    
    public final void set(float xValue, float zValue) {
        x = xValue;
        z = zValue;
    }

    /** Boolean hudDummy is not used. Only for polymorphism since .set(x, y) also exists. */
    public final void set(float xValue, float yValue, boolean hudDummy) {
        x = xValue;
        y = yValue;
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public final void set(float xValue, float yValue, float zValue) {  
        x = xValue;
        y = yValue;
        z = zValue;
    }
    
    public final void set(float xValue, float yValue, float zValue, float rValue) {      
        x = xValue;
        y = yValue;
        z = zValue;
        r = rValue;
    }
    
//    public final void set(float xValue, float yValue, float zValue, float rValue, float oXValue, float oYValue, float oZValue) {      
//        x = xValue;
//        y = yValue;
//        z = zValue;
//        r = rValue;
//        oX = oXValue;
//        oY = oYValue;
//        oZ = oZValue;
//    }
    
    public final void setR(float rValue) {
      r = rValue;
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    /** Calculate Dot Product of Object Position and Other Object x, z */
    public final float dot(float xValue, float zValue) {    	
        return (x * xValue) + (z * zValue);
    }

    /** Calculate Dot Product of Object Position and Other Object Position */
    public final float dot(Vector3 other) {
//    	float temp = (x * other.x) + (z * other.z);
    	
        return (x * other.x) + (z * other.z);
//        return (x * other.x) + (y * other.y);
    }
    
//    /** Calculate Dot Product of Object Position and Other Object Left Normal x,z */
//    public final float dotNormalLeft(float xValue, float zValue) {
//    	final float leftNormalX = zValue;
//    	final float leftNormalZ = -xValue;
//    	
//        return (x * leftNormalX) + (z * leftNormalZ);
//    }
//    
//    /** Calculate Dot Product of Object Position and Other Object Left Normal Position */
//    public final float dotNormalLeft(Vector3 other) {
//    	final float leftNormalX = other.z;
//    	final float leftNormalZ = -other.x;
//    	
//        return (x * leftNormalX) + (z * leftNormalZ);
//    }
//    
//    /** Calculate Dot Product of Object Position and Other Object Right Normal x,z */
//    public final float dotNormalRight(float xValue, float zValue) {
//    	final float rightNormalX = -zValue;
//    	final float rightNormalZ = xValue;
//    	
//        return (x * rightNormalX) + (z * rightNormalZ);
//    }
//    
//    /** Calculate Dot Product of Object Position and Other Object Right Normal Position */
//    public final float dotNormalRight(Vector3 other) {
//    	final float rightNormalX = -other.z;
//    	final float rightNormalZ = other.x;
//    	
//        return (x * rightNormalX) + (z * rightNormalZ);
//    }

    public final float length() {
        return (float) Math.sqrt(length2());
    }

    public final float length2() {
        return (x * x) + (z * z);
//        return (x * x) + (y * y);
    }
    
    public final float distance2(Vector3 other) {
        float dx = x - other.x;
        float dz = z - other.z;
//        float dy = y - other.y;
        return (dx * dx) + (dz * dz);
//        return (dx * dx) + (dy * dy);
    }

    public final float normalize() {
        final float magnitude = length();

        // XXX: I'm choosing safety over speed here.
        if (magnitude != 0.0f) {
            x /= magnitude;
//            y /= magnitude;
            z /= magnitude;
        }
        return magnitude;
    }

    public final void zero() {
        set(0.0f, 0.0f, 0.0f, 0.0f);
//        set(0.0f, 0.0f);
    }
    
//    public final void flipHorizontal(float aboutWidth) {
//        x = (aboutWidth - x);
//    }
//    
//    public final void flipVertical(float aboutHeight) {
//        y = (aboutHeight - y);
//    }
//    
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public final void flipDepth(float aboutDepth) {
//        z = (aboutDepth - z);
//    }
//    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
}
