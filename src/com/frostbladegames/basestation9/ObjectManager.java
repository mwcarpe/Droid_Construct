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

import java.util.Random;

import android.util.Log;


/** 
 * ObjectManagers are "group nodes" in the game graph.  They contain child objects, and updating
 * an object manager invokes update on its children.  ObjectManagers themselves are derived from
 * BaseObject, so they may be strung together into a hierarchy of objects.  ObjectManager may 
 * be specialized to implement special types of traversals (e.g. PhasedObjectManager sorts its
 * children).
 */
public class ObjectManager extends BaseObject {
	// FIXME TEST DEFAULT_ARRAY_SIZE = 8.  Optimize.
    protected static final int DEFAULT_ARRAY_SIZE = 8;
//    protected static final int DEFAULT_ARRAY_SIZE = 64;
    
//    private static final float LIGHTING_CHANGE_RATE = 10.0f;
    
    private FixedSizeArray<BaseObject> mObjects;
    private FixedSizeArray<BaseObject> mPendingAdditions;
    private FixedSizeArray<BaseObject> mPendingRemovals;
    
//    private boolean mGamePause;
    
//    private float mPreviousTime;

    public ObjectManager() {
        super();
        mObjects = new FixedSizeArray<BaseObject>(DEFAULT_ARRAY_SIZE);
        mPendingAdditions = new FixedSizeArray<BaseObject>(DEFAULT_ARRAY_SIZE);
        mPendingRemovals = new FixedSizeArray<BaseObject>(DEFAULT_ARRAY_SIZE);
        
//        mGamePause = false;
        
//        mPreviousTime = 0.0f;
    }
    
    public ObjectManager(int arraySize) {
        super();
        mObjects = new FixedSizeArray<BaseObject>(arraySize);
        mPendingAdditions = new FixedSizeArray<BaseObject>(arraySize);
        mPendingRemovals = new FixedSizeArray<BaseObject>(arraySize);
        
//        mGamePause = false;
        
//        mPreviousTime = 0.0f;
    }
    
    @Override
    public void reset() {
    	if (GameParameters.debug) {
            Log.i("Object", "ObjectManager reset()");	
    	}
    	
        commitUpdates();
        final int count = mObjects.getCount();
        for (int i = 0; i < count; i++) {
            BaseObject object = mObjects.get(i);
            object.reset();
        }
        
//        mGamePause = false;
        
//        mPreviousTime = 0.0f;
    }

    public void commitUpdates() {
//    	if(mObjects.getCount() == 0 && mPendingAdditions.getCount() == 0 && mPendingRemovals.getCount() == 0) {
//    		// no Log.i
//    	} else {
//	        Log.i("Object", "ObjectManager commitUpdates() mObjects, mPendingAdditions, mPendingRemovals: " + 
//	        		mObjects.getCount() + ", " + mPendingAdditions.getCount() + ", " + mPendingRemovals.getCount());
//    	}
    	
        final int additionCount = mPendingAdditions.getCount();
        if (additionCount > 0) {
            final Object[] additionsArray = mPendingAdditions.getArray();
            
            for (int i = 0; i < additionCount; i++) {
                BaseObject object = (BaseObject)additionsArray[i];
                mObjects.add(object);
            } 
            mPendingAdditions.clear();
        }
        
        final int removalCount = mPendingRemovals.getCount();
        if (removalCount > 0) {
            final Object[] removalsArray = mPendingRemovals.getArray();
    
            for (int i = 0; i < removalCount; i++) {
                BaseObject object = (BaseObject)removalsArray[i];
                mObjects.remove(object, true);
            } 
            mPendingRemovals.clear();
        }
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) {
    public void update(float timeDelta, BaseObject parent) {
//    	Log.i("Loop", "ObjectManager update()");
    	
        commitUpdates();
        
//    	if (GameParameters.levelRow == 4) {
//        	float gameTime = sSystemRegistry.timeSystem.getGameTime();
//        	
//        	if(gameTime > (mPreviousTime + LIGHTING_CHANGE_RATE)) {
////        	if(gameTime > (mPreviousTime + 1.0f)) {
//        		Log.i("Lighting", "ObjectManager gameTime, mPreviousTime = " + gameTime + ", " + mPreviousTime);
//        		
//                Random rStart = new Random();
//                int randomLighting = rStart.nextInt(10);
//                
//                if (randomLighting > 8) {
//                	GameParameters.lightingValue = 2;
//                } else if (randomLighting > 6) {
//                	GameParameters.lightingValue = 1;
//                } else {
//                	GameParameters.lightingValue = 0;
//                }
//                
//                mPreviousTime = gameTime;
//        	}
//    	}
        
        final int count = mObjects.getCount();
        if (count > 0) {
            final Object[] objectArray = mObjects.getArray();
            for (int i = 0; i < count; i++) {
                BaseObject object = (BaseObject)objectArray[i];
//                object.update(timeDelta, this, mGamePause);
                object.update(timeDelta, this);
            }
        }
    }

    public final FixedSizeArray<BaseObject> getObjects() {
        return mObjects;
    }
    
    public final int getCount() {    	
        return mObjects.getCount();
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public final int getAllocated() {
    	final int allocated = mObjects.getCapacity() - getCount();
    	return allocated;
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    /** Returns the count after the next commitUpdates() is called. */
    public final int getConcreteCount() {
        return mObjects.getCount() + mPendingAdditions.getCount() - mPendingRemovals.getCount();
    }
    
    public final BaseObject get(int index) {
        return mObjects.get(index);
    }

    public void add(BaseObject object) {
//    	DebugLog.d("ObjectManager", "add() object.toString() = " + object.toString());
    	
        mPendingAdditions.add(object);
        
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        final int count = mObjects.getCount();
//        final Object[] objectArray = mObjects.getArray();
//        for (int i = 0; i < count; i++) {
//        	Texture temp = findByClass(Texture.class);
//        	if (temp != null) {
//            	DebugLog.d("ObjectManager", "add() Texture name = " + 
//            			temp.name);
//        	}
//        }
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
//        DebugLog.d("ObjectManager", "add() mObjects, mPendingAdditions, mPendingRemovals: " + 
//        		mObjects.getCount() + ", " + mPendingAdditions.getCount() + ", " + mPendingRemovals.getCount());
    }

    public void remove(BaseObject object) {
        mPendingRemovals.add(object);
        
//        DebugLog.d("ObjectManager", "remove() mObjects, mPendingAdditions, mPendingRemovals: " + 
//        		mObjects.getCount() + ", " + mPendingAdditions.getCount() + ", " + mPendingRemovals.getCount());
    }
    
    public void removeAll() {
//        DebugLog.d("ObjectManager", "removeAll()");
    	
        final int count = mObjects.getCount();
        final Object[] objectArray = mObjects.getArray();
        for (int i = 0; i < count; i++) {
            mPendingRemovals.add((BaseObject)objectArray[i]);
        }
        mPendingAdditions.clear();
    }
    
//    public boolean getGamePause() {
//    	return mGamePause;
//    }
//    
//    public void setGamePause(boolean pauseState) {
//    	mGamePause = pauseState;
//    }

    /** 
     * Finds a child object by its type.  Note that this may invoke the class loader and therefore
     * may be slow.
     * @param classObject The class type to search for (e.g. BaseObject.class).
     * @return
     */
    public <T> T findByClass(Class<T> classObject) {
        T object = null;
        final int count = mObjects.getCount();
        for (int i = 0; i < count; i++) {
            BaseObject currentObject = mObjects.get(i);
            if (currentObject.getClass() == classObject) {
                object = classObject.cast(currentObject);
                break;
            }
        }
        return object;
    }
    
    protected FixedSizeArray<BaseObject> getPendingObjects() {
        return mPendingAdditions;
    }
}
