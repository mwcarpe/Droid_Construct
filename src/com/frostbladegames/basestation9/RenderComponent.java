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

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.util.Random;

import android.os.SystemClock;
import android.util.Log;


import com.frostbladegames.basestation9.GameObjectGroups.BottomMoveType;
import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;

/** 
 * Implements rendering of a drawable object for a game object.  If a drawable is set on this
 * component it will be passed to the renderer and drawn on the screen every frame.  Drawable
 * objects may be set to be "camera-relative" (meaning their screen position is relative to the
 * location of the camera focus in the scene) or not (meaning their screen position is relative to
 * the origin at the lower-left corner of the display).
 */
public class RenderComponent extends GameComponent {
//    private DrawableObject mDrawable;
	
    public int priority;
    public boolean cameraRelative;
//    private int mPriority;
//    private boolean mCameraRelative;
    
    private Vector3 mPositionWorkspace;
    private Vector3 mScreenLocation;
    private Vector3 mDrawOffset;
    
//    private float mRX;
//    private boolean mAstronautAppendageForward;
    
    private int mRY;
//    private float mRY;
    private boolean mEnemyClockwiseWFL;
//    private float mLocalRotate = 10.0f;
//    private DrawableDroid mDrawableDroid;
    
    private int mRZ;
//    private float mRZ;
    
//    // FIXME TEMP TEST. DELETE.
//    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private long mRenderTime;
//    private int mRenderCount;
    
    public RenderComponent() {
        super();
        setPhase(ComponentPhases.DRAW.ordinal());
        
        mPositionWorkspace = new Vector3();
        mScreenLocation = new Vector3();
        mDrawOffset = new Vector3();
        reset();
    }
    
    @Override
    public void reset() {
//        DebugLog.d("RenderComponent", "reset()");
    	
        priority = 0;
        cameraRelative = true;
//        mPriority = 0;
//        mCameraRelative = true;
//        mDrawable = null;
        mDrawOffset.zero();
        
        // Make mRY variable for different Enemies, even same types, so doesn't look like clockwork
        Random rStart = new Random();
        mRY = rStart.nextInt(5) * 2;	// Increment mRY in steps of 2 to match movement
//        mRY = (float)rStart.nextInt(10);
//        mRY = 10.0f;
        
        mEnemyClockwiseWFL = false;
//        mAstronautAppendageForward = false;
        
        mRZ = 0;
//        mRZ = 0.0f;
    }

    public void update(float timeDelta, BaseObject parent) {
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	// TODO Necessary to add check for getPlayerType()?
//        GameObject test = (GameObject)parent;
//        if(test.getPlayerType()) {
//
//        }
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
        // FIXME Optimize code.
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        RenderSystem render = sSystemRegistry.renderSystem;
        
//        final GameObjectManager manager = sSystemRegistry.gameObjectManager;
        
//      GameObject object = manager.getPlayer();
        
        GameObject object = (GameObject)parent;
        
        int objectId = object.gameObjectId;
//        int objectId = object.getGameObjectId();
        
//        Log.i("Loop", "RenderComponent update()" + " [" + object.gameObjectId + "] ");
        
        /* FIXME Delete all static calls including object.getDroid()
         * Why is this setting necessary? RenderComponent mDrawableDroid already ref to DroidObject via GameObjectFactory. 
         * Is there another way to set from RenderComponent() instantiation or GameObjectFactory? */
//        mDrawableDroid = object.getDrawableDroidObject();
////        mDrawableDroid = object.getDroid();
//        
//        mDrawableDroid.setVertexBuffer(object.getVertexBuffer());
//        mDrawableDroid.setNormalBuffer(object.getNormalBuffer());
//        mDrawableDroid.setColorBuffer(object.getColorBuffer());
//        mDrawableDroid.setIndexBuffer(object.getIndexBuffer());
//        mDrawableDroid.setIndicesLength(object.getIndicesLength());
        
        // FIXME Is mPositionWorkspace required?
        mPositionWorkspace = object.currentPosition;
//        mPositionWorkspace = object.position;
//        mPositionWorkspace = object.getPosition();
        
        // FIXME Is mCameraRelative required?
        cameraRelative = true;
//        mCameraRelative = true;
        
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        VectorPool pool = sSystemRegistry.vectorPool;
        Vector3 positionWorkspace = pool.allocate(mPositionWorkspace);
//        Vector3 positionWorkspace = new Vector3();
//        positionWorkspace.set(mPositionWorkspace);
        
//        if ((object.group == Group.DROID || object.group == Group.DROID_WEAPON) && object.currentState == CurrentState.FALL) {
//        	
//        }
        		
        
        /* FIXME Add if ((currentTick - lastTick) >= 1000) {
         * Add random factor to increase / decrease speed of Enemy */
        if (object.group == Group.ENEMY && !GameParameters.gamePause) {
//        if (object.group == Group.ENEMY) {
        	switch (object.bottomMoveType) {
        	case INVALID:
        		// Ignore
        		break;
        		
        	case MOUNT:
        		// Ignore
        		break;
        		
        	case SPIDER_LEGS:
//        	case SPIDER_WALK_FOUR_LEGS:
            	positionWorkspace.setR(object.currentPosition.r + mRY);
            	
        		if (mRY >= 10) {
        			mRY -= 2;
        			mEnemyClockwiseWFL = false;
        		} else if (mRY <= -10) {
        			mRY += 2;
        			mEnemyClockwiseWFL = true;
        		} else {
        			if (mEnemyClockwiseWFL) {
        				mRY += 2;
        			} else {
        				mRY -= 2;
        			}
        		}
            	
        		break;
        		
        	case WHEELS_TREAD:
        		// No special y or z rotation
        		break;
        		
        	case SPRING_LEGS:
        		// No special y or z rotation
        		break;
        		
        	case FLY:
        		// FIXME Add new Vector3 mRX and/or mRZ rotation variables for DrawableDroid glRotate() setting; change current mR to mRY
      			if (mRZ < 360) {
      				mRZ += 10;
      			} else {
      				mRZ = 0;
      			}
      			
        		break;
        		
        	default:
        		break;
        	}
        }
        
//        DebugLog.d("Rotation", "RenderComponent update() " + " [" + object.getGameObjectId() + "] " + 
//        		"position x,y,z,r = " + object.getPosition().x + ", " + object.getPosition().y + ", " + 
//        		object.getPosition().z + ", " + object.getPosition().r);
        
        // FIXME Add object.getGameObjectId parameter to pass thru to RenderElement for object ID tracking
        // TODO Change to DrawableObject mDrawable?
    	if (!object.inventoryWeapon && object.renderGameObject) {
            render.scheduleForDraw(object.drawableDroid, positionWorkspace, mDrawOffset, (float)mRZ, priority, cameraRelative, objectId);
//            render.scheduleForDraw(object.drawableDroid, positionWorkspace, mRZ, priority, cameraRelative, objectId);	
    	}
//    	render.scheduleForDraw(object.drawableDroid, positionWorkspace, mRZ, priority, cameraRelative, objectId);
////        render.scheduleForDraw(mDrawableDroid, positionWorkspace, mPriority, mCameraRelative, objectId);
////        render.scheduleForDraw(mDrawableDroid, mPositionWorkspace, mPriority, mCameraRelative);
        
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        pool.release(positionWorkspace);
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
//        // FIXME TEMP. DELETE.
//        if (objectId == 4 || objectId == 159) {
//        	mRenderCount++;
//        	final long currentTime = SystemClock.uptimeMillis();
//        	if (currentTime > (mRenderTime + PROFILE_REPORT_DELAY)) {
//        		Log.i("Renderer", "RenderComponent update() mRenderComponentCount [gameObjectID] = " + mRenderCount + " [" + objectId + "]");
//        		mRenderTime = currentTime;
//        	}
//        }
        
//        // TODO Make dynamic for Player, Enemies, Background, Objects.  Change back to mDrawable method?
//        if (object.getPlayerType() == true) {
//            // ENABLE TEMP CODE, DISABLE TEXTURE BASED CODE
////          DrawableDroid playerBitmap = null;
//          DrawableDroid droid = object.getDroid();
//          
////          int tempInt = playerBitmap.getTexture().resource;
////          DebugLog.d("RenderComponent", "update() player.getBitmap().getTexture().resource = " + tempInt);
//          
////          if (playerBitmap.getWidth() == 0) {
////              // first time init
////              Texture tex = playerBitmap.getTexture();
////              
////              DebugLog.d("RenderComponent", "update() playerBitmap.getTexture tex.width, tex.height = " + 
////              		tex.width + ", " + tex.height);
////              
////              playerBitmap.resize(tex.width, tex.height);
////          }
//          
//          DebugLog.d("RenderComponent", "update() player x,y,z,r = " + object.getPosition().x +
//          		", " + object.getPosition().y + ", " + object.getPosition().z + ", " + object.getPosition().r);
//          
//          render.scheduleForDraw(droid, object.getPosition(), SortConstants.PLAYER, true);
//        } else {
//        	DrawableBackground background = object.getBackground();
//          
//          DebugLog.d("RenderComponent", "update() background x,y,z,r = " + object.getPosition().x +
//          		", " + object.getPosition().y + ", " + object.getPosition().z + ", " + object.getPosition().r);
//          
//          render.scheduleForDraw(background, object.getPosition(), SortConstants.FOREGROUND, false);
//        }

        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    	
//        if (mDrawable != null) {
//            RenderSystem system = sSystemRegistry.renderSystem;
//            if (system != null) {
//                mPositionWorkspace.set(((GameObject)parent).getPosition());
//                mPositionWorkspace.add(mDrawOffset);
//                if (mCameraRelative) {
//                    CameraSystem camera = sSystemRegistry.cameraSystem;
//                    ContextParameters params = sSystemRegistry.contextParameters;
//                    mScreenLocation.x = (mPositionWorkspace.x - camera.getFocusPositionX()
//                                    + (params.gameWidth / 2));
//                    mScreenLocation.y = (mPositionWorkspace.y - camera.getFocusPositionY()
//                                    + (params.gameHeight / 2));
//                }
//                // XXX Since mDrawable is DrawableObject, works for both DrawableBitmap and DrawableDroid objects
//                // It might be better not to do culling here, as doing it in the render thread
//                // would allow us to have multiple views into the same scene and things like that.
//                // But at the moment significant CPU is being spent on sorting the list of objects
//                // to draw per frame, so I'm going to go ahead and cull early.
//                if (mDrawable.visibleAtPosition(mScreenLocation)) {
//                    system.scheduleForDraw(mDrawable, mPositionWorkspace, mPriority, mCameraRelative);
//                } else if (mDrawable.getParentPool() != null) {
//                    // Normally the render system releases drawable objects back to the factory
//                    // pool, but in this case we're short-circuiting the render system, so we
//                    // need to release the object manually.
//                	
//                	// TODO Re-enable - DrawableFactory
////                    sSystemRegistry.drawableFactory.release(mDrawable);
////                    mDrawable = null;
//                }
//            }
//        }
    }

//    public DrawableObject getDrawable() {
////        DebugLog.d("RenderComponent", "getDrawable()");
//    	
//        return mDrawable;
//    }
//    
//    public void setDrawable(DrawableObject drawable) {
////        DebugLog.d("RenderComponent", "setDrawable()");
//    	
//        mDrawable = drawable;
//    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */    
//    public DrawableDroid getDrawableDroid() {
////        DebugLog.d("RenderComponent", "getDrawableDroid()");
//    	
//        return mDrawableDroid;
//    }
    
//    public void setDrawableDroid(GameObject object, DrawableDroid drawableDroid) {
////        DebugLog.d("RenderComponent", "setDrawableDroid()");
//    	
//        mDrawableDroid = drawableDroid;
//        
//        mDrawableDroid.setVertexBuffer(object.getVertexBuffer());
//        mDrawableDroid.setNormalBuffer(object.getNormalBuffer());
//        mDrawableDroid.setColorBuffer(object.getColorBuffer());
//        mDrawableDroid.setIndexBuffer(object.getIndexBuffer());
//        mDrawableDroid.setIndicesLength(object.getIndicesLength());
//    }
    
//    public void setDrawableDroidVBO(GameObject object, DrawableDroid drawableDroid, GL11 gl11) {
////      DebugLog.d("RenderComponent", "setDrawableDroid()");
//  	
//      mDrawableDroid = drawableDroid;
//      
//      // FIXME Temporary copy. Find more efficient way for VBOs
//      mDrawableDroid.setVertexBuffer(object.getVertexBuffer());
////      mDrawableDroid.setNormalBuffer(object.getNormalBuffer());
////      mDrawableDroid.setColorBuffer(object.getColorBuffer());
//      mDrawableDroid.setIndexBuffer(object.getIndexBuffer());
////      mDrawableDroid.setIndicesLength(object.getIndicesLength());
//      
//      // FIXME Change .dat file format, then change to int [] vboId = new int[1];
//      int [] vboId = new int[1];
//      gl11.glGenBuffers(1, vboId, 0);
////      int [] vboId = new int[3];
////      gl11.glGenBuffers(3, vboId, 0);
//////      int vertexId = vboId[0];
//////      int normalId = vboId[1];
//////      int colorId = vboId[2];
//      
//      mDrawableDroid.setVertexBufferVBO(object.getVertexBuffer(), gl11, vboId[0]);
////      mDrawableDroid.setNormalBufferVBO(object.getNormalBuffer(), gl11, vboId[1]);
////      mDrawableDroid.setColorBufferVBO(object.getColorBuffer(), gl11, vboId[2]);
//      
//      int [] indexId = new int[1];
//      gl11.glGenBuffers(1, indexId, 0);
//      mDrawableDroid.setIndicesLength(object.getIndicesLength());
//      mDrawableDroid.setIndexBufferVBO(object.getIndexBuffer(), gl11, indexId[0]);
//  }
//    
//    public void copyDrawableDroidVBO(DrawableDroid drawableDroid) {
//    	mDrawableDroid = drawableDroid;
//    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */

//    public void setPriority(int priority) {
////        DebugLog.d("RenderComponent", "setPriority()");
//    	
//        mPriority = priority;
//    }
//    
//    public int getPriority() {
////        DebugLog.d("RenderComponent", "getPriority()");
//    	
//        return mPriority;
//    }
//
//    public void setCameraRelative(boolean relative) {
////        DebugLog.d("RenderComponent", "setCameraRelative()");
//    	
//        mCameraRelative = relative;
//    }
    
    public void setDrawOffset(float x, float y, float z) {
        mDrawOffset.set(x, y, z);
    }
    
    public void setDrawOffset(Vector3 offset) {
        mDrawOffset.set(offset);
    }
}
