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

import android.util.Log;


/**
 * A pool of 2D vectors.
 */
public class VectorPool extends TObjectPool<Vector3> {
    public VectorPool() {
        super();
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "VectorPool <constructor>");	
        }
    }
    
    @Override
    protected void fill() {
//        DebugLog.d("VectorPool", "fill()");
    	
        for (int x = 0; x < getSize(); x++) {
            getAvailable().add(new Vector3());
        }
    }

    @Override
    public void release(Object entry) {
//        DebugLog.d("VectorPool", "release()");
    	
        ((Vector3)entry).zero();
        super.release(entry);
    }

    /** Allocates a vector and assigns the value of the passed source vector to it. */
    public Vector3 allocate(Vector3 source) {
//        DebugLog.d("VectorPool", "allocate()");
    	
        Vector3 entry = super.allocate();
        entry.set(source);
        return entry;
    }
    
    public Vector3 allocate(float x, float y, float z, float r) {
//      Log.i("VectorPool", "allocate()");
    	
//		Log.i("Level", "VectorPool allocate() params x,y,z,r = " +
//				x + ", " + y + ", " + z + ", " + r);
  	
		Vector3 entry = super.allocate();
      	entry.set(x, y, z, r);

//		Log.d("Level", "VectorPool allocate() Vector3 entry x,y,z,r = " +
//				entry.x + ", " + entry.y + ", " + entry.z + ", " + entry.r);
      	
      	return entry;
    }
}
