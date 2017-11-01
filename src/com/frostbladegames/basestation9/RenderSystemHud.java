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
import android.os.SystemClock;
import android.util.Log;


import com.frostbladegames.basestation9.Texture;

/**
 * Manages a double-buffered queue of renderable objects.  The game thread submits drawable objects
 * to the the active render queue while the render thread consumes drawables from the alternate
 * queue.  When both threads complete a frame the queues are swapped.  Note that this class can
 * manage any number (>=2) of render queues, but increasing the number over two means that the game
 * logic will be running significantly ahead of the rendering thread, which may make the user feel
 * that the controls are "loose."
 */
public class RenderSystemHud extends BaseObject {
    private static final int TEXTURE_SORT_BUCKET_SIZE = 100;
//    private static final int TEXTURE_SORT_BUCKET_SIZE = 1000;
    private RenderElementPoolHud mElementPool;
//    private RenderElementPool mElementPool;
    private ObjectManager[] mRenderQueues;
    private int mQueueIndex;
    
    private static final int DRAW_QUEUE_COUNT = 2;
    private static final int MAX_RENDER_OBJECTS_PER_FRAME = 512;
//    private static final int MAX_RENDER_OBJECTS_PER_FRAME = 256;
    private static final int MAX_RENDER_OBJECTS = MAX_RENDER_OBJECTS_PER_FRAME * DRAW_QUEUE_COUNT;
    
//    private float mTargetX;
//    private float mTargetY;
//    private float mTargetZ;
    
//    // FIXME TEMP TEST. DELETE.
//    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private long mRenderTime;
//    private int mRenderDroidCount;
//    private int mRenderEnemyCount;
    
    public RenderSystemHud() {
        super();
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "RenderSystem <constructor>");	
    	}
        
        mElementPool = new RenderElementPoolHud(MAX_RENDER_OBJECTS);
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

    public void scheduleForDraw(DrawableBitmap object, Vector3 position, Vector3 drawOffset, float rZ, int priority, boolean cameraRelative, int objectId) {
//    public void scheduleForDraw(DrawableObject object, Vector3 position, Vector3 drawOffset, float rZ, int priority, boolean cameraRelative, int objectId) {
//    public void scheduleForDraw(DrawableObject object, Vector3 position, int priority, boolean cameraRelative, int objectId) {
//        DebugLog.d("RenderSystem", "scheduleForDraw()");
        
//        // XXX Multiple objects use priority == 20. Either change non droid_bottom objects to 21 or change this if() method.
//        if (priority == 20) {
//        	mTargetX = position.x;
//        	mTargetY = position.y;
//        	mTargetZ = position.z;
//        }
    	
//    	Log.i("HudTest", "RenderSystemHud scheduleForDraw() DrawableBitmap.width,height = " + object.getWidth() + ", " + object.getHeight());
//    	Log.i("HudTest", "RenderSystemHud scheduleForDraw() position.x,y = " + position.x + ", " + position.y);
//    	Log.i("HudTest", "RenderSystemHud scheduleForDraw() objectId = " + objectId);
    	
        RenderElementHud element = mElementPool.allocate();
//        RenderElement element = mElementPool.allocate();
        if (element != null) {
//        	DebugLog.d("RenderSystem", "scheduleForDraw(): mElementPool allocated = " + 
//        			mElementPool.getAllocatedCount());
        	
            element.set(object, position, drawOffset, rZ, priority, cameraRelative, objectId);
//            element.set(object, position, rZ, priority, cameraRelative, objectId);
            mRenderQueues[mQueueIndex].add(element);
        } else {
        	Log.e("Renderer", "RenderSystem scheduleForDraw() RenderElement = NULL");
        }
        
//        // FIXME TEMP. DELETE.
//        if (objectId == 4) {
//        	mRenderDroidCount++;
//        }
//        if (objectId == 159) {
//        	mRenderEnemyCount++;
//        }
//        if (objectId == 4 || objectId == 159) {
//        	final long currentTime = SystemClock.uptimeMillis();
//        	if (currentTime > (mRenderTime + PROFILE_REPORT_DELAY)) {
//        		Log.i("Renderer", "RenderSystem update() mRenderSystemDroidCount [4], mRenderSystemEnemyCount [159] = " + 
//        				mRenderDroidCount + ", " + mRenderEnemyCount);
//        		mRenderTime = currentTime;
//        	}
//        }
    }
    
    public void swap(GameRenderer renderer) {
//    public void swap(GameRenderer renderer, float cameraX, float cameraY, float cameraZ, 
//    		float viewangleX, float viewangleY, float viewangleZ) {
//    public void swap(GameRenderer renderer, float cameraX, float cameraY, float cameraZ) {
//    public void swap(GameRenderer renderer, float cameraX, float cameraY) {

        mRenderQueues[mQueueIndex].commitUpdates();
        
        // This code will block if the previous queue is still being executed.
        renderer.setDrawQueueHud(mRenderQueues[mQueueIndex]); 
//        renderer.setDrawQueue(mRenderQueues[mQueueIndex], cameraX, cameraY, cameraZ,
//        		viewangleX, viewangleY, viewangleZ); 
////        renderer.setDrawQueue(mRenderQueues[mQueueIndex], cameraX, cameraY); 

        final int lastQueue = (mQueueIndex == 0) ? DRAW_QUEUE_COUNT - 1 : mQueueIndex - 1;
    
        // Clear the old queue.
        FixedSizeArray<BaseObject> objects = mRenderQueues[lastQueue].getObjects();
        clearQueue(objects);
      
        mQueueIndex = (mQueueIndex + 1) % DRAW_QUEUE_COUNT;
    }
    
    private void clearQueue(FixedSizeArray<BaseObject> objects) {
//      DebugLog.d("RenderSystem", "clearQueue()");
	
    	final int count = objects.getCount();
    	final Object[] objectArray = objects.getArray();
    	final RenderElementPoolHud elementPool = mElementPool;
//    	final RenderElementPool elementPool = mElementPool;
    	for (int i = count - 1; i >= 0; i--) {
    		RenderElementHud element = (RenderElementHud)objectArray[i];
//    		RenderElement element = (RenderElement)objectArray[i];
    		elementPool.release(element);
    		objects.removeLast();
    	}
    }
    
    /* Empties all draw queues and disconnects the game thread from the renderer. */
    public void emptyQueues(GameRenderer renderer) {
//        DebugLog.d("RenderSystem", "emptyQueues()");
    	
        renderer.setDrawQueueHud(null);
//        renderer.setDrawQueue(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
//        renderer.setDrawQueue(null, 0.0f, 0.0f, 0.0f);
//        renderer.setDrawQueue(null, 0.0f, 0.0f); 
        for (int x = 0; x < DRAW_QUEUE_COUNT; x++) {
            mRenderQueues[x].commitUpdates();
            FixedSizeArray<BaseObject> objects = mRenderQueues[x].getObjects();
            clearQueue(objects);
        }
    }

    public class RenderElementHud extends PhasedObject {
        public DrawableBitmap mDrawable;
//        public DrawableObject mDrawable;
        
        // FIXME Why do RenderSystem RenderElement and GameRenderer use individual float values instead of Vector3()?
        public float x;
        public float y;
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        public float z;
        public float r;
        public float oX;
        public float oY;
        public float oZ;
        public float rZ;
        
        public int objectId;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        public boolean cameraRelative;
        
        public RenderElementHud() {
            super();
        }

        public void set(DrawableBitmap drawable, Vector3 position, Vector3 drawOffset, float rotateZ, int priority, boolean isCameraRelative, int id) {
//        public void set(DrawableObject drawable, Vector3 position, Vector3 drawOffset, float rotateZ, int priority, boolean isCameraRelative, int id) {
//            DebugLog.d("RenderElement", "set()");
        	
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
//            oX = position.oX;
//            oY = position.oY;
//            oZ = position.oZ;
            rZ = rotateZ;
            
            objectId = id;

            cameraRelative = isCameraRelative;
            
            // FIXME DELETED 11/11/12
//            // FIXME Required for HudSystem (and FarBackground?) Textures only, not GameObjects. Add boolean check or create separate HudRenderSystem.
//            final int sortBucket = priority * TEXTURE_SORT_BUCKET_SIZE;
//            int sortOffset = 0;
//            if (drawable != null) {
//                Texture tex = drawable.getTexture();
//                if (tex != null) {
//                    sortOffset = (tex.resource % TEXTURE_SORT_BUCKET_SIZE) * Utils.sign(priority);
//                }
//            }
//            setPhase(sortBucket + sortOffset);
        }

        public void reset() {
//            DebugLog.d("RenderElement", "reset()");
        	
            mDrawable = null;
            x = 0.0f;
            y = 0.0f;
            /* XXX Droid Code © 2012 FrostBlade LLC - Start */
            z = 0.0f;
            r = 0.0f;
            oX = 0.0f;
            oY = 0.0f;
            oZ = 0.0f;
            rZ = 0.0f;
            
            objectId = 0;
            /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
            cameraRelative = false;
        }
    }

    protected class RenderElementPoolHud extends TObjectPool<RenderElementHud> {
        RenderElementPoolHud(int max) {
            super(max);
        }

        @Override
        public void release(Object element) {
//            DebugLog.d("RenderElementPool", "release()");
        	
            RenderElementHud renderable = (RenderElementHud)element;
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
//            DebugLog.d("RenderElementPool", "fill()");
        	
            for (int i = 0; i < getSize(); i++) {
                getAvailable().add(new RenderElementHud());
            }
        }
    }
}
