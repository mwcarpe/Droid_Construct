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


import com.frostbladegames.basestation9.Texture;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.RenderSystem.RenderElement;
import com.frostbladegames.basestation9.RenderSystem.RenderElementPool;

/**
 * Manages a double-buffered queue of renderable objects for SplashScreenActivity
 */
public class SplashScreenRenderSystem extends BaseObject {
    private static final int TEXTURE_SORT_BUCKET_SIZE = 1000;
    private SplashScreenRenderElementPool mElementPool;
//    private RenderElementPool mElementPool;
    private ObjectManager[] mRenderQueues;
    private int mQueueIndex;
    
    private static final int DRAW_QUEUE_COUNT = 2;
    private static final int MAX_RENDER_OBJECTS_PER_FRAME = 8;
//    private static final int MAX_RENDER_OBJECTS_PER_FRAME = 256;
    private static final int MAX_RENDER_OBJECTS = MAX_RENDER_OBJECTS_PER_FRAME * DRAW_QUEUE_COUNT;
    
    private float mTargetX;
    private float mTargetY;
    private float mTargetZ;
    
    public SplashScreenRenderSystem() {
        super();
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "SplashScreenRenderSystem <constructor>");	
        }
        
        mElementPool = new SplashScreenRenderElementPool(MAX_RENDER_OBJECTS);
//        mElementPool = new RenderElementPool(MAX_RENDER_OBJECTS);
        mRenderQueues = new ObjectManager[DRAW_QUEUE_COUNT];
        for (int x = 0; x < DRAW_QUEUE_COUNT; x++) {
            mRenderQueues[x] = new ObjectManager(MAX_RENDER_OBJECTS_PER_FRAME);
//            mRenderQueues[x] = new PhasedObjectManager(MAX_RENDER_OBJECTS_PER_FRAME);
        }
        mQueueIndex = 0;
    }
    
    @Override
    public void reset() {
        
    }

    public void scheduleForDraw(DrawableObject object, Vector3 position, Vector3 drawOffset, 
    		float rZ, int priority, boolean cameraRelative, Type type, int objectId) {
//    public void scheduleForDraw(DrawableObject object, Vector3 position, int priority, boolean cameraRelative, int objectId) {
//        DebugLog.d("RenderSystem", "scheduleForDraw()");
        
        // FIXME Multiple objects use priority == 20. Either change non droid_bottom objects to 21 or change this if() method.
        if (priority == 20) {
        	mTargetX = position.x;
        	mTargetY = position.y;
        	mTargetZ = position.z;
        }
    	
        SplashScreenRenderElement element = mElementPool.allocate();
//        RenderElement element = mElementPool.allocate();
        if (element != null) {
//        	DebugLog.d("RenderSystem", "scheduleForDraw(): mElementPool allocated = " + 
//        			mElementPool.getAllocatedCount());
        	
            element.set(object, position, drawOffset, rZ, priority, cameraRelative, type, objectId);
//            element.set(object, position, rZ, priority, cameraRelative, objectId);
            mRenderQueues[mQueueIndex].add(element);
        }
    }

    private void clearQueue(FixedSizeArray<BaseObject> objects) {
//        DebugLog.d("RenderSystem", "clearQueue()");
    	
        final int count = objects.getCount();
        final Object[] objectArray = objects.getArray();
        final SplashScreenRenderElementPool elementPool = mElementPool;
//        final RenderElementPool elementPool = mElementPool;
        for (int i = count - 1; i >= 0; i--) {
            SplashScreenRenderElement element = (SplashScreenRenderElement)objectArray[i];
//            RenderElement element = (RenderElement)objectArray[i];
            elementPool.release(element);
            objects.removeLast();
        }
        
    }
    
    public void swap(SplashScreenRenderer renderer, float cameraX, float cameraY, float cameraZ, 
    		float viewangleX, float viewangleY, float viewangleZ, int splashTime) {
        mRenderQueues[mQueueIndex].commitUpdates();

        // This code will block if the previous queue is still being executed.
        renderer.setDrawQueue(mRenderQueues[mQueueIndex], cameraX, cameraY, cameraZ,
        		viewangleX, viewangleY, viewangleZ, splashTime); 

        final int lastQueue = (mQueueIndex == 0) ? DRAW_QUEUE_COUNT - 1 : mQueueIndex - 1;
    
        // Clear the old queue.
        FixedSizeArray<BaseObject> objects = mRenderQueues[lastQueue].getObjects();
        clearQueue(objects);
      
        mQueueIndex = (mQueueIndex + 1) % DRAW_QUEUE_COUNT;
    }
    
    /* Empties all draw queues and disconnects the game thread from the renderer. */
    public void emptyQueues(SplashScreenRenderer renderer) {
        renderer.setDrawQueue(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0);

        for (int x = 0; x < DRAW_QUEUE_COUNT; x++) {
            mRenderQueues[x].commitUpdates();
            FixedSizeArray<BaseObject> objects = mRenderQueues[x].getObjects();
            clearQueue(objects);
        }
    }

    public class SplashScreenRenderElement extends PhasedObject {
        public DrawableObject mDrawable;
        
        public float x;
        public float y;
        public float z;
        public float r;
        public float oX;
        public float oY;
        public float oZ;
        public float rZ;
        
        public Type objectType;
        public int objectId;
        public boolean cameraRelative;
        
        public SplashScreenRenderElement() {
            super();
        }

        public void set(DrawableObject drawable, Vector3 position, Vector3 drawOffset, 
        		float rotateZ, int priority, boolean isCameraRelative, Type type, int id) {
            mDrawable = drawable;
            if (position != null) {
                x = position.x;
                y = position.y;
                z = position.z;
                r = position.r;
            }
            if (drawOffset != null) {
                oX = drawOffset.x;
                oY = drawOffset.y;
                oZ = drawOffset.z;
            }

            rZ = rotateZ;
            
            objectType = type;
            
            objectId = id;

            cameraRelative = isCameraRelative;
            final int sortBucket = priority * TEXTURE_SORT_BUCKET_SIZE;
            int sortOffset = 0;
            if (drawable != null) {
                Texture tex = drawable.getTexture();
                if (tex != null) {
                    sortOffset = (tex.resource % TEXTURE_SORT_BUCKET_SIZE) * Utils.sign(priority);
                }
            }
            setPhase(sortBucket + sortOffset);
        }

        public void reset() {
            mDrawable = null;
            x = 0.0f;
            y = 0.0f;
            z = 0.0f;
            r = 0.0f;
            oX = 0.0f;
            oY = 0.0f;
            oZ = 0.0f;
            rZ = 0.0f;
            
            objectType = Type.INVALID;
            
            objectId = 0;
            cameraRelative = false;
        }
    }

    protected class SplashScreenRenderElementPool extends TObjectPool<SplashScreenRenderElement> {
//    protected class SplashScreenRenderElementPool extends TObjectPool<RenderElement> {
    	SplashScreenRenderElementPool(int max) {
            super(max);
        }

        @Override
        public void release(Object element) {
            SplashScreenRenderElement renderable = (SplashScreenRenderElement)element;
//            RenderElement renderable = (RenderElement)element;
            // if this drawable came out of a pool, make sure it is returned to that pool.
            final ObjectPool pool = renderable.mDrawable.getParentPool();
            if (pool != null) {
            	pool.release(renderable.mDrawable);
            }
            // reset on release
            renderable.reset();
            super.release(element);
        }

        @Override
        protected void fill() {
            for (int i = 0; i < getSize(); i++) {
                getAvailable().add(new SplashScreenRenderElement());
            }
        }
    }
}
