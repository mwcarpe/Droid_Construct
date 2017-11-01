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

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;
import com.frostbladegames.basestation9.BaseObject;
//import com.frostbladegames.basestation9.ContextParameters;
import com.frostbladegames.basestation9.GameObjectGroups.Type;
import com.frostbladegames.basestation9.RenderSystem.RenderElement;
import com.frostbladegames.basestation9.RenderSystemHud.RenderElementHud;

/**
 * GameRenderer the top-level rendering interface for the game engine.  It is called by
 * GLSurfaceView and is responsible for submitting commands to OpenGL.  GameRenderer receives a
 * queue of renderable objects from the thread and uses that to draw the scene every frame.  If
 * no queue is available then no drawing is performed.  If the queue is not changed from frame to
 * frame, the same scene will be redrawn every frame.
 * The GameRenderer also invokes texture loads when it is activated.
 */
public class GameRenderer implements DroidGLSurfaceView.Renderer {
	//public class GameRenderer implements GLSurfaceView.Renderer {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;
    
	private static FloatBuffer mLightZeroBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
	private static FloatBuffer mLightVeryLowBuffer = FloatBuffer.wrap(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
	private static FloatBuffer mLightLowBuffer = FloatBuffer.wrap(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
	private static FloatBuffer mLightMidBuffer = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
	private static FloatBuffer mLightHighBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
	private static FloatBuffer mLightMaxBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
	
	private static FloatBuffer mLight2PositionBufferStatic = FloatBuffer.wrap(new float[]{54.625f, -7.5f, -54.625f, 1.0f});
	private static FloatBuffer mLight2SpotDirectionStatic = FloatBuffer.wrap(new float[]{1.0f, 0.5f, -1.0f});
	private static FloatBuffer mLight2SpotCutoffStatic = FloatBuffer.wrap(new float[]{20.0f});
    
    // ISOMETRIC for Move and Fire angle adjustment to align with gluLookAt() at 45 degrees
    private static final float ISOMETRIC = -45.0f;
    
    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private static final int PROFILE_REPORT_DELAY = 1 * 1000;
//    private static final int PROFILE_REPORT_DELAY = 3 * 1000;
    
//    private int mLaserGameObjectCount;

    private int mGameWidth;
    private int mGameHeight;
    // TODO mHalfGameWidth, mHalfGameHeight originally used for Camera by Replica. Required?
    private int mHalfGameWidth;
    private int mHalfGameHeight;
//    private int mWidth;
//    private int mHeight;
//    private int mHalfWidth;
//    private int mHalfHeight;
    
    private float mViewScaleX;
    private float mViewScaleY;
//    private float mScaleX;
//    private float mScaleY;
//    private Context mContext;
    private long mLastTime;
    private int mProfileFrames;
    private long mProfileWaitTime;
    private long mProfileFrameTime;
    private long mProfileSubmitTime;
    private int mProfileObjectCount;
    
    private int mCallbackLoopCount;
//    private float mCallbackTimer;
////    private long mCallbackTimer;
    
    private Game mGame;
    
    private boolean mDrawPaused;
    
    private ObjectManager mDrawQueue;
    private boolean mDrawQueueChanged;
    private Object mDrawLock;
    
    private ObjectManager mDrawQueueHud;
    private boolean mDrawQueueChangedHud;
    private Object mDrawLockHud;
    
    // TODO Is it optimal to keep mGL instance? Works for loadTextures() and loadBuffers().
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    GL10 mGL;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    private float mCameraX;
    private float mCameraY;
    private float mCameraZ;
    private float mViewangleX;
    private float mViewangleY;
    private float mViewangleZ;
//    private float mViewangleYPrevious;
    
//    private float mLastTouchTime;
//    
//    private int mViewWidth;
//    private int mViewHeight;
//    private float mInverseViewScaleY;
//    
//    private float mMoveFireButtonTouchWidth;
//    private float mMoveFireButtonTouchHeight;
//    private float mMoveFireButtonTouchRadius;
//    private float mMoveButtonTouchLeftX;
//    private float mMoveButtonTouchBottomY;
//    private float mMoveButtonCenterX;
//    private float mMoveButtonCenterY;
//    private float mMoveButtonTopRenderLeftX;
//    private float mMoveButtonTopRenderBottomY;
//    
//    private float mFireButtonTouchLeftX;
//    private float mFireButtonTouchBottomY;
//    private float mFireButtonCenterX;
//    private float mFireButtonCenterY;
//    private float mFireButtonTopRenderLeftX;
//    private float mFireButtonTopRenderBottomY;
//    
//    private float mWeaponButtonTouchWidth;
//    private float mWeaponButtonTouchHeight;
//    private float mWeapon1ButtonTouchLeftX;
//    private float mWeapon1ButtonTouchBottomY;
//    private float mWeapon2ButtonTouchLeftX;
//    private float mWeapon2ButtonTouchBottomY;
//    
//    private float mViewangleBarBaseTouchLeftX;
//    private float mViewangleBarBaseTouchBottomY; 
//    private float mViewangleBarBaseTouchWidth;
//    private float mViewangleBarBaseTouchHeight; 
//    private float mViewangleBarButtonTouchLeftX;
//    private float mViewangleBarButtonTouchBottomY; 
//    private float mViewangleBarButtonTouchWidth;
//    private float mViewangleBarButtonTouchHeight; 
//    
//    private boolean mTempMovePress;
//    private boolean mTempFirePress;
//    private boolean mTempWeapon1Press;
//    private boolean mTempWeapon2Press;
//    private boolean mTempViewangleBarPress;
//    
//    private int mMovePointerId;
//    private int mFirePointerId;
//    private int mWeapon1PointerId;
//    private int mWeapon2PointerId;
//    private int mViewangleBarPointerId;
    
    private int mLevelRow;
    
    boolean mCallbackRequested;
    
//    // FIXME TEMP TEST. DELETE.
////    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private long mRenderTime;
//    private int mRenderDroidCount;
//    private int mRenderEnemyCount;
        
    public GameRenderer(Context context, Game game, int gameWidth, int gameHeight, float viewScaleX, float viewScaleY) {
//    public GameRenderer(Context context, Game game, int gameWidth, int gameHeight) {
//    	mLaserGameObjectCount = 0;
    	
    	if (GameParameters.debug) {
            Log.i("GameFlow", "GameRenderer <constructor>");	
    	}
    	
//        mContext = context;
        mGame = game;
        mGameWidth = gameWidth;
        mGameHeight = gameHeight;
        mHalfGameWidth = gameWidth / 2;
        mHalfGameHeight = gameHeight / 2;
        mViewScaleX = viewScaleX;
        mViewScaleY = viewScaleY;
//        mScaleX = 1.0f;
//        mScaleY = 1.0f;
        mDrawQueueChanged = false;
        mDrawLock = new Object();
        mDrawQueueChangedHud = false;
        mDrawLockHud = new Object();
        
        mDrawPaused = false;
        
        mCameraX = 0.0f;
        mCameraY = 0.0f;
        mCameraZ = 0.0f;
        mViewangleX = 0.0f;
        mViewangleY = 0.0f;
        mViewangleZ = 0.0f;
//        mViewangleYPrevious = 0.0f;
        
//        mTempMovePress = false;
//        mTempFirePress = false;
//        mTempWeapon1Press = false;
//        mTempWeapon2Press = false;
//        mTempViewangleBarPress = false;
//        
//        mMovePointerId = -1;
//        mFirePointerId = -1;
//        mWeapon1PointerId = -1;
//        mWeapon2PointerId = -1;
//        mViewangleBarPointerId = -1;
        
        mLevelRow = 0;
        
        mCallbackRequested = false;
    }
    
//    private void hackBrokenDevices() {
//    	// Some devices are broken.  Fix them here.  This is pretty much the only
//    	// device-specific code in the whole project.  Ugh.
//        ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
//
//        // TODO Check to see if Motorola Morrison model fixed this issue, then delete code
//    	if (Build.PRODUCT.contains("morrison")) {
//        	DebugLog.d("GameRenderer", "hackBrokenDevices() Product contains Motorola Morrison");
//        	
//    		// This is the Motorola Cliq.  This device LIES and says it supports
//    		// VBOs, which it actually does not (or, more likely, the extensions string
//    		// is correct and the GL JNI glue is broken).
//    		params.supportsVBOs = false;
//    		// If Motorola fixes this, I should switch to using the fingerprint
//    		// (blur/morrison/morrison/morrison:1.5/CUPCAKE/091007:user/ota-rel-keys,release-keys)
//    		// instead of the product name so that newer versions use VBOs.
//    	}
//    }
    
    public void spawnObjects(LevelSystem level) {
    	// XXX Re-enable for GL Debug as required
//    	checkGLError(mGL);
    	
    	if (mGL != null) {
    		level.spawnObjects(mGL);
    	} else {
    		Log.e("GameRenderer", "spawnObjects() mGL = null!");
    	}
    	
  	  	// XXX Re-enable for GL Debug as required
//    	checkGLError(mGL);
    	
    	mGL = null;
    }
    
    // FIXME DELETED 11/11/12
//    public void loadTextures(TextureLibrary library) {
////        public void loadTextures(GL10 gl, TextureLibrary library) {
////        DebugLog.d("GameRenderer", "loadTextures()");
//        
////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        GL10 gl = OpenGLSystem.getGL();
////        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////    	
////        if (gl != null) {
////            library.loadAll(mContext, gl);
////        }
//        
//        if (mGL != null) {
//            library.loadAll(mContext, mGL);
//        }
//    }
//    
//    public void flushTextures(TextureLibrary library) {
////        public void flushTextures(GL10 gl, TextureLibrary library) {
////        DebugLog.d("GameRenderer", "flushTextures()");
//    	
////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        GL10 gl = OpenGLSystem.getGL();
////        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////        
////        if (gl != null) {
////            library.deleteAll(gl);
////        }
//        
//        if (mGL != null) {
//            library.deleteAll(mGL);
//        }
//    }
    
    public void loadBuffers(BufferLibrary library) {
//      public void loadBuffers(GL10 gl, BufferLibrary library) {
//        DebugLog.d("GameRenderer", "loadBuffers()");
        
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        GL10 gl = OpenGLSystem.getGL();
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//      	
//      	// TODO Re-enable
//          if (gl != null) {
////              library.generateHardwareBuffers(gl);
//          }
    	
        if (mGL != null) {
//          library.generateHardwareBuffers(mGL);
        }
    }
    
    public void flushBuffers(BufferLibrary library) {
//      public void flushBuffers(GL10 gl, BufferLibrary library) {
//        DebugLog.d("GameRenderer", "flushBuffers()");
        
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        GL10 gl = OpenGLSystem.getGL();
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//      	
//      	// TODO Re-enable
//        if (gl != null) {
////              library.releaseHardwareBuffers(gl);
//          }
          
        if (mGL != null) {
//            library.releaseHardwareBuffers(mGL);
        }
    }
    
    public synchronized void setDrawQueue(ObjectManager queue, float cameraX, float cameraY, float cameraZ, 
    		float viewangleX, float viewangleY, float viewangleZ) {
//    public synchronized void setDrawQueue(ObjectManager queue, float cameraX, float cameraY, float cameraZ) {
//        Log.i("Loop", "GameRenderer setDrawQueue()");
      	
  		mDrawQueue = queue;
  		
//          DebugLog.d("GameRenderer", "setDrawQueue queue.getCount() = " + 
//          		queue.getCount() + ", mDrawQueue.getCount() = " + mDrawQueue.getCount());
  		
  		mCameraX = cameraX;
  		mCameraY = cameraY;
  		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
  		mCameraZ = cameraZ;
  		mViewangleX = viewangleX;
  		mViewangleY = viewangleY;
  		mViewangleZ = viewangleZ;
  		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
  		
//          DebugLog.d("GameRenderer", "setDrawQueue() mCameraX,Y,Z = " + mCameraX + ", " +
//          		mCameraY + ", " + mCameraZ);
  		
      	synchronized(mDrawLock) {
      		mDrawQueueChanged = true;
      		mDrawLock.notify();
      	}
    }
    
    public void setDrawQueueHud(ObjectManager queue) {
//    public synchronized void setDrawQueueHud(ObjectManager queue, float cameraX, float cameraY, float cameraZ, 
//    		float viewangleX, float viewangleY, float viewangleZ) {
//    public synchronized void setDrawQueue(ObjectManager queue, float cameraX, float cameraY, float cameraZ) {
//        Log.i("Loop", "GameRenderer setDrawQueue()");
      	
  		mDrawQueueHud = queue;
//  		mDrawQueue = queue;
  		
//          DebugLog.d("GameRenderer", "setDrawQueue queue.getCount() = " + 
//          		queue.getCount() + ", mDrawQueue.getCount() = " + mDrawQueue.getCount());
  		
//  		mCameraX = cameraX;
//  		mCameraY = cameraY;
//  		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//  		mCameraZ = cameraZ;
//  		mViewangleX = viewangleX;
//  		mViewangleY = viewangleY;
//  		mViewangleZ = viewangleZ;
//  		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
  		
//          DebugLog.d("GameRenderer", "setDrawQueue() mCameraX,Y,Z = " + mCameraX + ", " +
//          		mCameraY + ", " + mCameraZ);
  		
      	synchronized(mDrawLockHud) {
      		mDrawQueueChangedHud = true;
      		mDrawLockHud.notify();
      	}
    }
    
//    public void setLevelRow(int levelRow) {
//    	Log.i("GameFlow", "GameRenderer setLevelRow()");
//    	
//    	mLevelRow = levelRow;
//    }
    
    // FIXME RE-ENABLE
//    private static void beginDrawingIntro(GL10 gl) {   
////    public static void beginDrawingIntro(GL10 gl) {    	
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, mLight2PositionBufferStatic);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_DIRECTION, mLight2SpotDirectionStatic);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_CUTOFF, mLight2SpotCutoffStatic);
//
//        gl.glEnable(GL10.GL_LIGHT2);
//    }
    
    private void beginDrawing(GL10 gl) {
//    public static void beginDrawing(GL10 gl, float cameraX, float cameraY, float cameraZ, 
//    		float viewangleX, float viewangleY, float viewangleZ) {

    	// Clear screen
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// FIXME 10/22/12 TEMP RE-ADD
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// FIXME 10/22/12 Moved back to onSurfaceCreated()
//		gl.glDepthFunc(GL10.GL_LEQUAL);
//    	gl.glShadeModel(GL10.GL_SMOOTH);
//        gl.glEnable(GL10.GL_DITHER);
//        gl.glEnable(GL10.GL_LIGHTING);
//        gl.glEnable(GL10.GL_LIGHT0);
//        gl.glEnable(GL10.GL_COLOR_MATERIAL);
//        gl.glEnable(GL10.GL_BLEND);
//        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
////        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);        
        
		// FIXME 10/22/12 Moved back to onSurfaceChanged()
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//        GLU.gluPerspective(gl, 40.0f, (float)mGameWidth / (float)mGameHeight, 1.0f, 100.0f);
        // FIXME END TEMP
		
//        // 3D Object Blend
//		gl.glEnable(GL10.GL_BLEND);
//		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// FIXME 10/5/12 TEST. Moved to onSurfaceChanged() with Push/PopMatrix in beginDrawingHud
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//
//        GLU.gluPerspective(gl, 40.0f, (float)mGameWidth / (float)mGameHeight, 1.0f, 100.0f);
    	
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // Adjust cameraX,Y,Z center when using GamePlay isometric angle
        if (mViewangleX < -0.1f) {
        	GLU.gluLookAt(gl, mCameraX + mViewangleX, mCameraY + mViewangleY, mCameraZ + mViewangleZ, 
        			mCameraX - 0.5f, mCameraY + 0.5f, mCameraZ + 0.5f, 0.0f, 1.0f, 0.0f);
        } else {
        	GLU.gluLookAt(gl, mCameraX + mViewangleX, mCameraY + mViewangleY, mCameraZ + mViewangleZ, 
        			mCameraX, mCameraY + 1.0f, mCameraZ, 0.0f, 1.0f, 0.0f);	
        }
        
//        // FIXME 10/22/12 TEMP ADD
//        gl.glFrontFace(GL10.GL_CCW);
//        // FIXME END TEMP
        
        // FIXME 10/22/12 TEMP DISABLE. SEE ABOVE.
//		gl.glEnable(GL10.GL_DEPTH_TEST);
		// FIME END TEMP
        
		// Enable the 3D vertex, normal, and color states
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
    
    private void beginDrawingHud(GL10 gl) {
//      public static void beginDrawing(GL10 gl, int gameWidth, int gameHeight) {
//      public static void beginDrawing(GL10 gl, float viewWidth, float viewHeight) {
      	
    	// FIXME TEST 10/3/12. Duplicate or Re-enable?
        // 2D Hud Blend makes texture background transparent (otherwise black)
//        gl.glEnable(GL10.GL_BLEND);
//        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
          
        // TODO ColorBuffer Test code. Why using Hex instead of Float? Required?
//    	gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
////          gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
    	
//    	// FIXME 10/20/12. TEMP ONLY. DELETE.
//    	// Clear screen
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
////        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        // FIXME 10/20/12. END TEMP.
    	
    	// FIXME 10/22/12 DISABLED
//    	// FIXME 10/22/12 TEMP RE-ADD
//        // Blend makes texture background transparent (otherwise black)
//        gl.glEnable(GL10.GL_BLEND);
//        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
////        // TODO ColorBuffer Test code. Why using Hex instead of Float? Required?
////        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
//////      gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
//        // FIXME END TEMP
        // FIXME END DISABLE

//    	// Enable Texture for Hud
//    	gl.glEnable(GL10.GL_TEXTURE_2D);
          
//    	// FIXME TEMP RE-ADD
//    	// FIXME TEST 10/4/12. Re-enable?
//    	gl.glShadeModel(GL10.GL_FLAT);
//    	// FIXME END TEMP
          
          /* FIXME GL_DITHER - Test glEnable vs glDisable. 
           * Value same for both DrawableDroid and DrawableBitmap, so move to GameRenderer.onSurfaceCreated()? */
//          gl.glEnable(GL10.GL_DITHER);

          // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
//          gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
          
    	// FIXME 10/21/12 TEMP DISABLE
        /* TODO Are 2x PushMatrix() and 2x PopMatrix() required?
         * If use OpenGL Circles for DrawableBitmap, glMatrixMode Projection and ModelView duplication required? */
    	gl.glMatrixMode(GL10.GL_PROJECTION);
    	gl.glPushMatrix();
    	// FIXME 12/18/12 DELETED
//    	gl.glLoadIdentity();
    	// FIXME END 12/18/12 DELETED
          
        // gluPerspective() for DrawableDroid, glOrthof() for DrawableBitmap
    	// FIXME 10/21/12 TEMP TEST ONLY FOR -1.0f. Change back to 0.0f.
//    	gl.glOrthof(0.0f, (float)mGameWidth, 0.0f, (float)mGameHeight, -1.0f, 1.0f);
    	gl.glOrthof(0.0f, (float)mGameWidth, 0.0f, (float)mGameHeight, 0.0f, 1.0f);
//          gl.glOrthof(0.0f, gameWidth, 0.0f, gameHeight, 0.0f, 1.0f);
    	gl.glMatrixMode(GL10.GL_MODELVIEW);
    	gl.glPushMatrix();
    	gl.glLoadIdentity();
    	// FIXME END TEMP
    	
    	// Enable Texture for Hud
    	gl.glEnable(GL10.GL_TEXTURE_2D);
    	
    	// OLD glDrawArrays() Test Code
//    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//    	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
    
    public void beginDrawingIntro(GL10 gl) {    	
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, mLightHighBuffer);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, mLightHighBuffer);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, mLight2PositionBufferStatic);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_DIRECTION, mLight2SpotDirectionStatic);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_CUTOFF, mLight2SpotCutoffStatic);

        gl.glEnable(GL10.GL_LIGHT2);
    }

    /** Draws the scene.  Note that the draw queue is locked for the duration of this function. */
    public void onDrawFrame(GL10 gl) {
//    	Log.i("GameFlow", "GameRenderer onDrawFrame()");
      
        // XXX For Profile FPS. Re-enable for debug as required.
//    	long time;
//    	long time_delta;
//    	if (GameParameters.debug) {
//            time = SystemClock.uptimeMillis();
//            time_delta = (time - mLastTime);	
//    	}
        
        synchronized(mDrawLock) {
//        	Log.i("GameFlow", "GameRenderer onDrawFrame() synchronized (mDrawLock)");
        	
            if (!mDrawQueueChanged) {
                while (!mDrawQueueChanged) {
//                	Log.i("GameFlow", "GameRenderer onDrawFrame() while(!mDrawLockChanged); mDrawLock.wait()");
                	
                    try {
                    	GameParameters.drawQueueWaitCounter++;
                    	
                    	mDrawLock.wait();
                    } catch (InterruptedException e) {
                        // No big deal if this wait is interrupted.
                    }
                }
            }
            
//            Log.i("GameFlow", "GameRenderer onDrawFrame() mDrawLockChanged = TRUE; proceed in loop");
            
            mDrawQueueChanged = false;
        }
        
        synchronized(mDrawLockHud) {
//        	Log.i("GameFlow", "GameRenderer onDrawFrame() synchronized (mDrawLockHud)");
        	
            if (!mDrawQueueChangedHud) {
                while (!mDrawQueueChangedHud) {
//                	Log.i("GameFlow", "GameRenderer onDrawFrame() while(!mDrawLockChangedHud); ; mDrawLockHud.wait()");
                	
                    try {
                    	GameParameters.drawQueueHudWaitCounter++;
                    	
                    	mDrawLockHud.wait();
                    } catch (InterruptedException e) {
                        // No big deal if this wait is interrupted.
                    }
                }
            }
            
//            Log.i("GameFlow", "GameRenderer onDrawFrame() mDrawLockChangedHud = TRUE; proceed in loop");
            
            mDrawQueueChangedHud = false;
        }
        
        // XXX For Profile FPS. Re-enable for debug as required.
//        final long wait = SystemClock.uptimeMillis();
        
//        final float gameTime = BaseObject.sSystemRegistry.timeSystem.getGameTime();
        
        // FIXME 12/16/12 900pm MODIFIED
        // FIXME 12/16/12 900am MODIFIED
        if (mCallbackRequested) {
//        	mGame.onRendererRestart();
//        	
//        	mCallbackRequested = false;
//        	
        	// Allow enough time for Renderer to Restart
    		if (mCallbackLoopCount >= 10) {
//    		if (mCallbackLoopCount >= 5) {
//    		if (gameTime > (mCallbackTimer + 10)) {
//    		if (wait > (mCallbackTimer + 10000)) {
//    		if (SystemClock.uptimeMillis() > (mCallbackTimer + 10000)) {
    	    	if (GameParameters.debug) {
        			Log.i("GameFlow", "GameRenderer onDrawFrame() mCallbackRequested = TRUE and mCallbackLoopCount = " +
        					mCallbackLoopCount + "; therefore set gamePause = FALSE and call mGame.onSurfaceReady()");	
    	    	}
    			
            	mCallbackRequested = false;
            	
            	mCallbackLoopCount = 0;
    			
    			mGame.onRendererRestart();
    			
//        		BaseObject.sSystemRegistry.setGamePause(false);
//        		
//        		mGame.setGameRestart(true);
//        		
//            	mGame.onSurfaceReady();
            	
    		} else {
    			mCallbackLoopCount++;
    		}
        }
        // FIXME END 12/16/12 900am MODIFIED
        // FIXME END 12/16/12 900pm MODIFIED
        
  	  	// XXX Re-enable for GL Debug as required
//        checkGLError(gl);
        
        // FIXME TEMP. DELETE.
        GameParameters.renderCounter++;
        
//        // FIXME START local beginDrawing() Test 9/16/12.
//        beginDrawing(gl);
//        // FIXME START Drawable Droid Test 9/8/12. RE-ENABLE full DrawableDroid.beginDrawing().
////        DrawableDroid.beginDrawing(gl, mCameraX, mCameraY, mCameraZ, mViewangleX, mViewangleY, mViewangleZ);
////        // FIXME Re-enable DrawableBitmap.beginDrawing() and DrawableDroid.beginDrawing() for one time state change
////        DrawableDroid.beginDrawing(gl, mCameraX, mCameraY, mCameraZ, mViewangleX, mViewangleY, mViewangleZ,
////        		mGameWidth, mGameHeight);
//        // FIXME END Drawable Droid Test 9/8/12
//        
//        if (GameParameters.levelRow == 0 && GameParameters.light2Enabled) {
//            DrawableDroid.beginDrawingIntro(gl);
//        }
////        if (mLevelRow == 0 && GameParameters.light2Enabled) {
////            DrawableDroid.beginDrawingIntro(gl);
////        }
        
//        DrawableDroid.beginDrawing(gl, mCameraX, mCameraY, mCameraZ, mGameWidth, mGameHeight);
//        DrawableBitmap.beginDrawing(gl, mWidth, mHeight);

//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        RenderSystem render = sSystemRegistry.;
//        
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//		gl.glClearDepthf(1.0f);
//		
//		gl.glEnable(GL10.GL_DEPTH_TEST);
//		gl.glDepthFunc(GL10.GL_LEQUAL);
//    	gl.glShadeModel(GL10.GL_SMOOTH);
//        gl.glEnable(GL10.GL_DITHER);
//        gl.glEnable(GL10.GL_LIGHTING);
//        gl.glEnable(GL10.GL_LIGHT0);
//        gl.glEnable(GL10.GL_COLOR_MATERIAL);
//        
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//        GLU.gluPerspective(gl, 45.0f, 480.0f / 320.0f, 1.0f, 100.0f);
////        GLU.gluPerspective(gl, 45.0f, (float)gameWidth / (float)gameHeight, 1.0f, 100.0f);
//
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
//		GLU.gluLookAt(gl, 0.0f, 5.0f, 2.5f, targetX, targetY, targetZ, 0.0f, 1.0f, 0.0f);
////		GLU.gluLookAt(gl, 0.0f, 5.0f, 2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
//		
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glFrontFace(GL10.GL_CCW);
//		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        
        synchronized (this) {
//        	Log.i("GameFlow", "GameRenderer onDrawFrame() synchronized (this)");
        	
        	// FIXME 10/20/12 TEST ONLY. RE-ENABLE.
        	// Draw GameObject DrawableDroid and DrawableFarBackground 
            // FIXME START local beginDrawing() Test 9/16/12.
            beginDrawing(gl);
            // FIXME START Drawable Droid Test 9/8/12. RE-ENABLE full DrawableDroid.beginDrawing().
//            DrawableDroid.beginDrawing(gl, mCameraX, mCameraY, mCameraZ, mViewangleX, mViewangleY, mViewangleZ);
//            // FIXME Re-enable DrawableBitmap.beginDrawing() and DrawableDroid.beginDrawing() for one time state change
//            DrawableDroid.beginDrawing(gl, mCameraX, mCameraY, mCameraZ, mViewangleX, mViewangleY, mViewangleZ,
//            		mGameWidth, mGameHeight);
            // FIXME END Drawable Droid Test 9/8/12
            
            if (GameParameters.levelRow == 0 && GameParameters.light2Enabled) {
            	beginDrawingIntro(gl);
//                DrawableDroid.beginDrawingIntro(gl);
            }
            
            if (mDrawQueue != null && mDrawQueue.getObjects().getCount() > 0) {
//            	Log.i("GameFlow", "GameRenderer onDrawFrame() mDrawQueue != null && mDrawQueue.getObjects().getCount() > 0");
            	
                OpenGLSystem.setGL(gl);
                
                FixedSizeArray<BaseObject> objects = mDrawQueue.getObjects();
                
                Object[] objectArray = objects.getArray();
                final int count = objects.getCount();

                mProfileObjectCount += count;
                
                for (int i = 0; i < count; i++) {
                    RenderElement element = (RenderElement)objectArray[i];

                    if(element != null) {
                        float x = element.x;
                        float y = element.y;
                        float z = element.z;
                        float r = element.r;
                        float oX = element.oX;
                        float oY = element.oY;
                        float oZ = element.oZ;
                        float rZ = element.rZ;
                        
                        int objectId = element.objectId;
                        
                        gl.glMatrixMode(GL10.GL_MODELVIEW);
                        gl.glPushMatrix();
//                        gl.glLoadIdentity();
                        
                        /* FIXME Delete mViewScaleX, mViewScaleY, mGameWidth, mGameHeight pass-thru
                         * If keep Type pass-thru, then change Type.Droid to RenderElement Type
                         * Add rX,rY to rZ pass-thru?
                         * Change x,y,z,r to Vector3() pass-thru? Answer: Replica probably used x,y,z,r for Render speed 
                         * Pass through static Object Type for each object and draw accordingly? 
                         * Move glTranslatef(), glRotatef(), glScalef() from DrawableDroid to here?
                         * e.g. Background will not Translate or Rotate. Player needs to Rotate locally, but Translate globally.
                         * Check OpenGL Programming Guide Ch3 on order of Translate and Rotate for local vs global. */
                        element.mDrawable.draw(gl, x, y, z, r, oX, oY, oZ,
                        		rZ, mViewScaleX, mViewScaleY, mGameWidth, mGameHeight, objectId);
//                        element.mDrawable.draw(gl, x, y, z, r, oX, oY, oZ, Type.DROID,
//                        		rZ, mViewScaleX, mViewScaleY, mGameWidth, mGameHeight);
//                        element.mDrawable.draw(gl, x, y, z, r, scaleX, scaleY, mGameWidth, mGameHeight);
//                        element.mDrawable.draw(gl, vboSupport, x, y, z, r, scaleX, scaleY, mWidth, mHeight);
//                        element.mDrawable.draw(x, y, scaleX, scaleY, mWidth, mHeight);
//                        element.mDrawable.draw(x, y, scaleX, scaleY);
                        
                        gl.glMatrixMode(GL10.GL_MODELVIEW);
                        gl.glPopMatrix();
                    }
                }
            } else if (mDrawQueue == null) {
                // If we have no draw queue, clear the screen.  If we have a draw queue that
                // is empty, we'll leave the frame buffer alone.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }
            
            endDrawing(gl);
//                DrawableDroid.endDrawing(gl);
////                DrawableBitmap.endDrawing(gl);
            
            // Draw Hud DrawableBitmap
            beginDrawingHud(gl);
            
            if (mDrawQueueHud != null && mDrawQueueHud.getObjects().getCount() > 0) {             	
                OpenGLSystem.setGL(gl);
                
                FixedSizeArray<BaseObject> objectsHud = mDrawQueueHud.getObjects();
                
                Object[] objectArrayHud = objectsHud.getArray();
                final int countHud = objectsHud.getCount();
                
                for (int i = 0; i < countHud; i++) {
                    RenderElementHud element = (RenderElementHud)objectArrayHud[i];

                    if (element != null) {
                        // FIXME Optimize code
                        float x = element.x;
                        float y = element.y;
//                            float z = element.z;
//                            float r = element.r;
//                            float oX = element.oX;
//                            float oY = element.oY;
//                            float oZ = element.oZ;
//                            float rZ = element.rZ;
//                            
//                            int objectId = element.objectId;
                        
                        // FIXME 10/5/12 TEST. Delete Hud ModelView and Push/PopMatrix calls. Necessary? 
//                        gl.glMatrixMode(GL10.GL_MODELVIEW);
//                        gl.glPushMatrix();
////                            gl.glLoadIdentity();
                        
                        element.mDrawable.draw(gl, x, y, mViewScaleX, mViewScaleY);
//                            element.mDrawable.draw(gl, x, y, z, r, oX, oY, oZ,
//                            		rZ, mViewScaleX, mViewScaleY, mGameWidth, mGameHeight, objectId);
                        
                        // FIXME 10/5/12 TEST. Delete Hud ModelView and Push/PopMatrix calls. Necessary?
//                        gl.glMatrixMode(GL10.GL_MODELVIEW);
//                        gl.glPopMatrix();	
                    }
                }
            } else if (mDrawQueueHud == null) {
                // If we have no draw queue, clear the screen.  If we have a draw queue that
                // is empty, we'll leave the frame buffer alone.
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            }
                
            endDrawingHud(gl);
            
            OpenGLSystem.setGL(null);
            
            // FIXME 12/13/12 TEMP DISABLE. RE-ENABLE.
//            // FIXME 12/8/12 ADDED
//            if(BaseObject.sSystemRegistry.gamePause) {
//            	Log.i("GameFlow", "GameRenderer onDrawFrame() gamePause = TRUE");
//            	
////            	mGame.onResume(mContext, true);
//            	
//            	mGame.onPause();
//            	
//            	BaseObject.sSystemRegistry.gamePause = false;
//            }
//            // FIXME END 12/8/12 ADDED
            // FIXME END 12/13/12 TEMP DISABLE. RE-ENABLE.
        }
        
//        // FIXME START Test 9/16/12. Local endDrawing()
//        endDrawing(gl);
////        DrawableDroid.endDrawing(gl);
//////        DrawableBitmap.endDrawing(gl);
        
  	  	// XXX Re-enable for GL Debug as required
//        checkGLError(gl);
        
        // XXX For Profile FPS. Re-enable for debug as required.
//    	if (GameParameters.debug) {
//            long time2 = SystemClock.uptimeMillis();
//            mLastTime = time2;
//
//            mProfileFrameTime += time_delta;
//            mProfileSubmitTime += time2 - time;
//            mProfileWaitTime += wait - time;
//            
//            mProfileFrames++;
//            if (mProfileFrameTime > PROFILE_REPORT_DELAY) {
//            	final int validFrames = mProfileFrames;
//                final long averageFrameTime = mProfileFrameTime / validFrames;
//                final long averageSubmitTime = mProfileSubmitTime / validFrames;
//                final float averageObjectsPerFrame = (float)mProfileObjectCount / validFrames;
//                final long averageWaitTime = mProfileWaitTime / validFrames;
//
//                Log.i("Loop", 
//                		"GameRenderer onDrawFrame() Avg Render Frame Time: " + averageSubmitTime +
//                		"  Avg Time Between Render Frames: " + averageFrameTime + 
//                		"  Objects/Frame: " + averageObjectsPerFrame +
//                		"  Wait Time: " + averageWaitTime);
//               
//                mProfileFrameTime = 0;
//                mProfileSubmitTime = 0;
//                mProfileFrames = 0;
//                mProfileObjectCount = 0;
//            }
//    	}
    }
    
    /**
     * Ends the drawing and restores the OpenGL state.
     * 
     * @param gl  A pointer to the OpenGL context.
     */

    private void endDrawing(GL10 gl) {
//    public static void endDrawing(GL10 gl) {
//      public static void endDrawing() {
    	
//    	Log.i("Renderer", "GameRenderer endDrawing()");
    	
//    	// FIXME 10/22/12 TEMP ADD
//    	gl.glDisable(GL10.GL_BLEND);
//    	gl.glDisable(GL10.GL_COLOR_MATERIAL);
//    	gl.glDisable(GL10.GL_LIGHT0);
//    	gl.glDisable(GL10.GL_LIGHT1);
//    	gl.glDisable(GL10.GL_LIGHT2);
//    	gl.glDisable(GL10.GL_LIGHTING);
//      	// FIXME END TEMP
    	
//		gl.glDisable(GL10.GL_DEPTH_TEST);
    
    	// FIXME START Drawable Droid Test 9/8/12 
    	// FIXME START TEMP ADDED CODE
		// Disable the 3D client states before leaving
    	//	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		// FIXME 12/18/12 550pm Moved below glDisableClientState
		gl.glDisable(GL10.GL_DEPTH_TEST);
		// FIXME END 12/18/12 550pm Moved below glDisableClientState
		
//		// FIXME TEMP TEST ONLY
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// FIXME END TEMP ADDED CODE
		
////        GL10 gl = OpenGLSystem.getGL();
//        
////        gl.glMatrixMode(GL10.GL_MODELVIEW);
////        gl.glPopMatrix();
//        
//    	gl.glDisable(GL10.GL_BLEND);
//        
////        // FIXME Same setting for both DrawableDroid and DrawableBitmap? Move glEnable to onSurfaceCreated()?
////        gl.glDisable(GL10.GL_DITHER);
//        
//        gl.glDisable(GL10.GL_COLOR_MATERIAL);
//        gl.glDisable(GL10.GL_LIGHT0);
//        gl.glDisable(GL10.GL_LIGHT1);
//        gl.glDisable(GL10.GL_LIGHT2);
//        gl.glDisable(GL10.GL_LIGHTING);
//        
//        gl.glDisable(GL10.GL_DEPTH_TEST);
//        
////        // FIXME Test Normalize setting
////		// XXX RE-ENABLED. TEST ONLY.
////        gl.glDisable(GL10.GL_RESCALE_NORMAL);
//////        gl.glDisable(GL10.GL_NORMALIZE);
//
//		// Disable the client state before leaving
////		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		
////        gl.glMatrixMode(GL10.GL_PROJECTION);
////        gl.glPopMatrix();
////        gl.glMatrixMode(GL10.GL_MODELVIEW);
////        gl.glPopMatrix();
		// FIXME END Drawable Droid Test 9/8/12
    }
    
    private void endDrawingHud(GL10 gl) {
//    public static void endDrawing(GL10 gl) {
//      public static void endDrawing(GL10 gl) {
          
          /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//          GL10 gl = OpenGLSystem.getGL();
          /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    	
    	// OLD glDrawArrays() Test Code
//    	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
          
    	// Disable Texture for Hud
    	gl.glDisable(GL10.GL_TEXTURE_2D);
      	
    	// FIXME 10/22/12 DISABLED
//        // Blend makes texture background transparent (otherwise black)
//    	gl.glDisable(GL10.GL_BLEND);
    	// FIXME END DISABLE
    	
    	// FIXME 10/20/12 TEMP DISABLE.
    	gl.glMatrixMode(GL10.GL_MODELVIEW);
    	gl.glPopMatrix();
    	gl.glMatrixMode(GL10.GL_PROJECTION);
    	gl.glPopMatrix();

    }
    
//  	public void onTouchEvent(MotionEvent event) {
////  	public void onTouchEvent(MotionEvent event, boolean running) {
//  		
//  		// ORIGINAL ONTOUCHEVENT() CODE
//    	// FIXME TEMP. DELETE.
//    	GameParameters.gameCounter++;
//    	
//    	HudSystem hudSystem = BaseObject.sSystemRegistry.hudSystem;
//    	
//        if (!hudSystem.levelIntro) { 
////        if (mRunning && !hudSystem.levelIntro) {  
//        	
////        	int pointerCount = event.getPointerCount();
////        	Log.i("TouchEvent", "Game onTouchEvent() pointerCount = " + pointerCount);
//    		
//    		final int action = event.getAction();
//    		
//    		InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
//    		
//    		switch (action & MotionEvent.ACTION_MASK) {
//    		
//    		case MotionEvent.ACTION_UP: {
//    			GameParameters.gameCounterTouchUp++;
//    			
//    			inputSystem.reset();
//    			
//    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//    			
//    			mMovePointerId = -1;
//    			mFirePointerId = -1;
//    			mWeapon1PointerId = -1;
//    			mWeapon2PointerId = -1;
//    			mViewangleBarPointerId = -1;
//    			
//    			break;
//    		}
//    		
//    		case MotionEvent.ACTION_DOWN: {
//    			GameParameters.gameCounterTouchDownMove++;
//    			
//        		float x = event.getX();
//        		float y = mViewHeight - event.getY();
//        		
//    	        if (getTouchedWithinRegionMove(x, y)) {        					        		
//		        	touchMoveDown(inputSystem, x, y);
//		        	
//	    			hudSystem.setMoveButtonTopLocationTouch(x, y);
//		        		
//		        	mMovePointerId = event.getPointerId(0);
//    	        } else if (getTouchedWithinRegionFire(x, y)) {        					        		
//        			touchFireDown(inputSystem, x, y);
//        			
//	    			hudSystem.setFireButtonTopLocationTouch(x, y);
//		        		
//		        	mFirePointerId = event.getPointerId(0);
//		        } else if (getTouchedWithinRegionWeapon1(x, y)) {        					        		
//		        	inputSystem.touchWeapon1Press = true;
//		        		
//		        	mWeapon1PointerId = event.getPointerId(0);
//		        } else if (getTouchedWithinRegionWeapon2(x, y)) {		        		
//		        	inputSystem.touchWeapon2Press = true;
//		        		
//		        	mWeapon2PointerId = event.getPointerId(0);
//		        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
//		        	touchViewangleBarDown(hudSystem, y);
//		        		
//		        	mViewangleBarPointerId = event.getPointerId(0);
//		        }
//        		
//        		break;
//        	}
//    		
//    		case MotionEvent.ACTION_MOVE: {
//    			GameParameters.gameCounterTouchDownMove++;
//    			
//    			if (mMovePointerId > -1 && mFirePointerId > -1) {
//        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
//        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//        			
//            		float touchMoveX = event.getX(pointerIndexMove);
//            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
//            		float touchFireX = event.getX(pointerIndexFire);
//            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//            		
////            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
////            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
////            		
////        	        if (touchMove && touchFire) {    		        		
//            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//            			
//    	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//    	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
////            			
////            		} else if (touchMove) {
////            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
////            			inputSystem.touchFirePress = false;
////            			
////    	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
////    	    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
////            			
////            		} else if (touchFire) {
////            			touchFireDown(inputSystem, touchFireX, touchFireY);
////            			inputSystem.touchMovePress = false;
////            			
////            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
////    	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
////            		}
//            	} else if (mMovePointerId > -1) {
//        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
//        			
//            		float touchMoveX = event.getX(pointerIndexMove);
//            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
//            		
////        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		
//            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//            			
//    	    			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
////            		} else {
////            			inputSystem.touchMovePress = false;
////            			
////            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
////            		}
//            	} else if (mFirePointerId > -1) {
//        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//        			
//              		float touchFireX = event.getX(pointerIndexFire);
//            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//            		
////        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {    		        		
//            			touchFireDown(inputSystem, touchFireX, touchFireY);
//            			
//    	    			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
////    		        } else {
////    		        	inputSystem.touchFirePress = false;
////    		        	
////    		        	hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
////    		        }
//            	}
//    			
////    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
////    			if (mWeapon1PointerId > -1) {
////        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
////        			
////              		float x = event.getX(pointerIndexWeapon1);
////            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
////            		
////        	        if (getTouchedWithinRegionWeapon1(x, y)) {    		        		
////            			inputSystem.touchWeapon1Press = true;
////    		        }
////            	} else if (mWeapon2PointerId > -1) {
////        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
////        			
////              		float x = event.getX(pointerIndexWeapon2);
////            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
////            		
////        	        if (getTouchedWithinRegionWeapon2(x, y)) {    		        		
////            			inputSystem.touchWeapon2Press = true;
////    		        }
////            	}
//    			
//    			if (mViewangleBarPointerId > -1) {
//        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//        			
//              		float x = event.getX(pointerIndexViewangleBar);
//            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//            		
//    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//    		        	touchViewangleBarDown(hudSystem, y);
//    				}
//    			}
//    			
//            	break;
//    		}
//    		
//    		case MotionEvent.ACTION_POINTER_UP: {
//    			GameParameters.gameCounterTouchUp++;
//    			
//    			final int pointerIndexUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
//					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//
//            	final int pointerId = event.getPointerId(pointerIndexUp);
//            	
//            	if (pointerId == mMovePointerId) {            		
//            		// TODO Since will set to false in <GameObject>Component after each update(), is this setting necessary?
//            		inputSystem.touchMovePress = false;
//            		
//	    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//            		
//        			mMovePointerId = -1;
//            	} else if (pointerId == mFirePointerId) {            		
//            		inputSystem.touchFirePress = false;
//            		
//	    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	    			
//        			mFirePointerId = -1;
//            	} else if (pointerId == mWeapon1PointerId) {            		
//            		inputSystem.touchWeapon1Press = false;
//
//        			mWeapon1PointerId = -1;
//            	} else if (pointerId == mWeapon2PointerId) {            		
//            		inputSystem.touchWeapon2Press = false;
//
//        			mWeapon2PointerId = -1;
//            	} else if (pointerId == mViewangleBarPointerId) {
//            		mViewangleBarPointerId = -1;
//    			}
//            	
//            	if (mMovePointerId > -1 && mFirePointerId > -1) {
//        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
//        			
//               		float touchMoveX = event.getX(pointerIndexDownMove);
//            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//               		float touchFireX = event.getX(pointerIndexDownFire);
//            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
//            		
//            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
//            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
//            		
//        	        if (touchMove && touchFire) {    		        		
//            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//            			
//            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//            			
//            		} else if (touchMove) {
//            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//            			
//            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//            			
//            		} else if (touchFire) {
//            			touchFireDown(inputSystem, touchFireX, touchFireY);
//            			
//            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//            			
//            		} 
//            	} else if (mMovePointerId > -1) {
//        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//        			
//               		float touchMoveX = event.getX(pointerIndexDownMove);
//            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//
//        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		                  			
//            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//            			
//            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//        	        } else {
//        	        	hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//        	        }
//    			} else if (mFirePointerId > -1) {
//        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//        			
//            		float touchFireX = event.getX(pointerIndexFire);
//            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//            		
//        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {    		        		
//            			touchFireDown(inputSystem, touchFireX, touchFireY);
//            			
//            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//            		} else {
//            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//            		}
//    			}
//    			
//    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//    			if (mWeapon1PointerId > -1) {
//        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
//        			
//              		float x = event.getX(pointerIndexWeapon1);
//            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
//            		
//        	        if (getTouchedWithinRegionWeapon1(x, y)) {    		        		
//            			inputSystem.touchWeapon1Press = true;
//    		        }
//            	} else if (mWeapon2PointerId > -1) {
//        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
//        			
//              		float x = event.getX(pointerIndexWeapon2);
//            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
//            		
//        	        if (getTouchedWithinRegionWeapon2(x, y)) {    		        		
//            			inputSystem.touchWeapon2Press = true;
//    		        }
//            	}
//    			
//    			if (mViewangleBarPointerId > -1) {
//        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//        			
//              		float x = event.getX(pointerIndexViewangleBar);
//            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//            		
//    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//    		        	touchViewangleBarDown(hudSystem, y);
//    				}
//    			}
//    			
//            	break;
//    		}
//    		
//    		case MotionEvent.ACTION_POINTER_DOWN: {
//    			GameParameters.gameCounterTouchDownMove++;
//    			
//    			final int pointerIndexDown = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
//					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//            	final int pointerId = event.getPointerId(pointerIndexDown);
//    			
//          		float downX = event.getX(pointerIndexDown);
//        		float downY = mViewHeight - (event.getY(pointerIndexDown));
//            	
//    	        if (mMovePointerId == -1 && getTouchedWithinRegionMove(downX, downY)) {
//    	        	
//    	        	mMovePointerId = pointerId;
//    	        	
//	    			if (mFirePointerId > -1) {
//	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//	        			
//	            		float touchFireX = event.getX(pointerIndexFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//	            		
//	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
//	            			touchMoveFireDown(inputSystem, downX, downY, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(downX, downY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} else {	            			
//	            			touchMoveDown(inputSystem, downX, downY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(downX, downY);
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            		}
//	    			} else {
//	        			touchMoveDown(inputSystem, downX, downY);
//	        			
//	        			hudSystem.setMoveButtonTopLocationTouch(downX, downY);
//	    			}
//	    			
//	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//	    			if (mWeapon1PointerId > -1) {
//	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon1);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
//	            		
//	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
//	            			inputSystem.touchWeapon1Press = true;
//	    		        }
//	            	} else if (mWeapon2PointerId > -1) {
//	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon2);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
//	            		
//	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
//	            			inputSystem.touchWeapon2Press = true;
//	    		        }
//	            	}
//	    			
//	    			if (mViewangleBarPointerId > -1) {
//	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//	        			
//	              		float x = event.getX(pointerIndexViewangleBar);
//	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//	            		
//	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//	    		        	touchViewangleBarDown(hudSystem, y);
//	    				}
//	    			}
//    	        } else if (mFirePointerId == -1 && getTouchedWithinRegionFire(downX, downY)) {
//    	        	
//		        	mFirePointerId = pointerId;
//		        	
//	    			if (mMovePointerId > -1) {
//	        			final int pointerIndexMove = event.findPointerIndex(mMovePointerId);
//	        			
//	            		float touchMoveX = event.getX(pointerIndexMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexMove));
//	            		
//	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {
//	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, downX, downY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationTouch(downX, downY);
//	            			
//	            		} else {	            			
//	            			touchFireDown(inputSystem, downX, downY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            			hudSystem.setFireButtonTopLocationTouch(downX, downY);
//	            		}
//	    			} else {
//	        			touchFireDown(inputSystem, downX, downY);
//	        			
//	        			hudSystem.setFireButtonTopLocationTouch(downX, downY);
//	    			}
//	    			
//	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//	    			if (mWeapon1PointerId > -1) {
//	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon1);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
//	            		
//	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
//	            			inputSystem.touchWeapon1Press = true;
//	    		        }
//	            	} else if (mWeapon2PointerId > -1) {
//	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon2);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
//	            		
//	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
//	            			inputSystem.touchWeapon2Press = true;
//	    		        }
//	            	}
//	    			
//	    			if (mViewangleBarPointerId > -1) {
//	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//	        			
//	              		float x = event.getX(pointerIndexViewangleBar);
//	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//	            		
//	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//	    		        	touchViewangleBarDown(hudSystem, y);
//	    				}
//	    			}
//		        } else if (mWeapon1PointerId == -1 && getTouchedWithinRegionWeapon1(downX, downY)) {		        	
//		        	mWeapon1PointerId = pointerId;
//		        	mWeapon2PointerId = -1;		// Default to mWeapon1PointerId
//		        		
//		        	inputSystem.touchWeapon1Press = true;
//		        	
//	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//	               		float touchFireX = event.getX(pointerIndexDownFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
//	            		
//	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
//	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
//	            		
//	        	        if (touchMove && touchFire) {	    		        		
//	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} else if (touchMove) {
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            			
//	            		} else if (touchFire) {
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} 
//	            	} else if (mMovePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//
//	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            		} else {
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            		}
//	    			} else if (mFirePointerId > -1) {
//	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//	        			
//	            		float touchFireX = event.getX(pointerIndexFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//	            		
//	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            		} else {
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            		}
//	    			}
//	            	
//	    			if (mViewangleBarPointerId > -1) {
//	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//	        			
//	              		float x = event.getX(pointerIndexViewangleBar);
//	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//	            		
//	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//	    		        	touchViewangleBarDown(hudSystem, y);
//	    				}
//	    			}
//		        } else if (mWeapon1PointerId == -1 && mWeapon2PointerId == -1 && getTouchedWithinRegionWeapon2(downX, downY)) {       				        		
//		        	mWeapon2PointerId = pointerId;
//		        	
//		        	inputSystem.touchWeapon2Press = true;
//		        	
//	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//	               		float touchFireX = event.getX(pointerIndexDownFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
//	            		
//	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
//	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
//	            		
//	        	        if (touchMove && touchFire) {	    		        		
//	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} else if (touchMove) {
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            			
//	            		} else if (touchFire) {
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} 
//	            	} else if (mMovePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//
//	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            		} else {
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            		}
//	    			} else if (mFirePointerId > -1) {
//	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//	        			
//	            		float touchFireX = event.getX(pointerIndexFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//	            		
//	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	        	        } else {
//	        	        	hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	        	        }
//	    			}
//	            	
//	    			if (mViewangleBarPointerId > -1) {
//	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//	        			
//	              		float x = event.getX(pointerIndexViewangleBar);
//	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//	            		
//	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//	    		        	touchViewangleBarDown(hudSystem, y);
//	    				}
//	    			}
//		        } else if (mViewangleBarPointerId == -1 && getTouchedWithinRegionViewangleBar(downX, downY)) {
//	        		
//		        	mViewangleBarPointerId = pointerId;
//		        	
//		        	touchViewangleBarDown(hudSystem, downY);
//		        	
//	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//	               		float touchFireX = event.getX(pointerIndexDownFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
//	            		
//	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
//	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
//	            		
//	        	        if (touchMove && touchFire) {	    		        		
//	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} else if (touchMove) {
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            			
//	            		} else if (touchFire) {
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} 
//	            	} else if (mMovePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//
//	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            		} else {
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            		}
//	    			} else if (mFirePointerId > -1) {
//	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//	        			
//	            		float touchFireX = event.getX(pointerIndexFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//	            		
//	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	        	        } else {
//	        	        	hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	        	        }
//	    			}
//	            	
//	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//	    			if (mWeapon1PointerId > -1) {
//	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon1);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
//	            		
//	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
//	            			inputSystem.touchWeapon1Press = true;
//	    		        }
//	            	} else if (mWeapon2PointerId > -1) {
//	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon2);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
//	            		
//	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
//	            			inputSystem.touchWeapon2Press = true;
//	    		        }
//	            	}
//		        } else {
//	            	if (mMovePointerId > -1 && mFirePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			final int pointerIndexDownFire = event.findPointerIndex(mFirePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//	               		float touchFireX = event.getX(pointerIndexDownFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexDownFire));
//	            		
//	            		final boolean touchMove = getTouchedWithinRegionMove(touchMoveX, touchMoveY);
//	            		final boolean touchFire = getTouchedWithinRegionFire(touchFireX, touchFireY);
//	            		
//	        	        if (touchMove && touchFire) {	    		        		
//	            			touchMoveFireDown(inputSystem, touchMoveX, touchMoveY, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} else if (touchMove) {
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            			
//	            		} else if (touchFire) {
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            			
//	            		} 
//	            	} else if (mMovePointerId > -1) {
//	        			final int pointerIndexDownMove = event.findPointerIndex(mMovePointerId);
//	        			
//	               		float touchMoveX = event.getX(pointerIndexDownMove);
//	            		float touchMoveY = mViewHeight - (event.getY(pointerIndexDownMove));
//
//	        	        if (getTouchedWithinRegionMove(touchMoveX, touchMoveY)) {    		        		            			
//	            			touchMoveDown(inputSystem, touchMoveX, touchMoveY);
//	            			
//	            			hudSystem.setMoveButtonTopLocationTouch(touchMoveX, touchMoveY);
//	            		} else {
//	            			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
//	            		}
//	    			} else if (mFirePointerId > -1) {
//	        			final int pointerIndexFire = event.findPointerIndex(mFirePointerId);
//	        			
//	            		float touchFireX = event.getX(pointerIndexFire);
//	            		float touchFireY = mViewHeight - (event.getY(pointerIndexFire));
//	            		
//	        	        if (getTouchedWithinRegionFire(touchFireX, touchFireY)) {	    		        		
//	            			touchFireDown(inputSystem, touchFireX, touchFireY);
//	            			
//	            			hudSystem.setFireButtonTopLocationTouch(touchFireX, touchFireY);
//	            		} else {
//	            			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
//	            		}
//	    			}
//	    			
//	    			// Only allow one Weapon Button press at a time. Default to Weapon Button 1.
//	    			if (mWeapon1PointerId > -1) {
//	        			final int pointerIndexWeapon1 = event.findPointerIndex(mWeapon1PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon1);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon1));
//	            		
//	        	        if (getTouchedWithinRegionWeapon1(x, y)) {	    		        		
//	            			inputSystem.touchWeapon1Press = true;
//	    		        }
//	            	} else if (mWeapon2PointerId > -1) {
//	        			final int pointerIndexWeapon2 = event.findPointerIndex(mWeapon2PointerId);
//	        			
//	              		float x = event.getX(pointerIndexWeapon2);
//	            		float y = mViewHeight - (event.getY(pointerIndexWeapon2));
//	            		
//	        	        if (getTouchedWithinRegionWeapon2(x, y)) {	    		        		
//	            			inputSystem.touchWeapon2Press = true;
//	    		        }
//	            	}
//	    			
//	    			if (mViewangleBarPointerId > -1) {
//	        			final int pointerIndexViewangleBar = event.findPointerIndex(mViewangleBarPointerId);
//	        			
//	              		float x = event.getX(pointerIndexViewangleBar);
//	            		float y = mViewHeight - (event.getY(pointerIndexViewangleBar));
//	            		
//	    				if (getTouchedWithinRegionViewangleBar(x, y)) {
//	    		        	touchViewangleBarDown(hudSystem, y);
//	    				}
//	    			}
//		        }
//
//            	break;
//    		}
//            	
//            default:
//            	break;
//    		} // end of switch statement
////            }
//    	}
////        return true;
//  		
//  		// NEW ONTOUCHEVENT() CODE
////      	// FIXME TEMP. DELETE.
////      	GameParameters.gameCounter++;
////      	
////      	HudSystem hudSystem = BaseObject.sSystemRegistry.hudSystem;
////  	    
//////  	    final float gameTime = ObjectRegistry.sSystemRegistry.timeSystem.getGameTime();
//////      	
//////  		if (gameTime > (mLastTouchTime + 0.032f)) {
//////  			GameParameters.gameTimerCounter++;
//////  			
////        // FIXME Change back to original code method, which was more accurate, since new code + timer method did not improve UI Flooding performance
////      	if (!hudSystem.levelIntro) { 
//////	        if (running && !hudSystem.levelIntro) {  
////        	int pointerCount = event.getPointerCount();
////    		
////    		final int action = event.getAction();
////    		
////    		InputSystem inputSystem = BaseObject.sSystemRegistry.inputSystem;
////    		
////    		float x = 0.0f;
////    		float y = 0.0f;
////    		
////    		switch (action & MotionEvent.ACTION_MASK) {
////    		
////    		case MotionEvent.ACTION_UP:
////    			GameParameters.gameCounterTouchUp++;
////    			
////    			// Reset all touchPress = false
////    			inputSystem.reset();
////    			mTempMovePress = false;
////    			mTempFirePress = false;
////    			mTempWeapon1Press = false;
////    			mTempWeapon2Press = false;
////    			mTempViewangleBarPress = false;
////    			
////    			hudSystem.setMoveButtonTopLocationDefault(mMoveButtonTopRenderLeftX, mMoveButtonTopRenderBottomY);
////    			hudSystem.setFireButtonTopLocationDefault(mFireButtonTopRenderLeftX, mFireButtonTopRenderBottomY);
////    			
////    			break;
////    		
////    		case MotionEvent.ACTION_DOWN:
////    			GameParameters.gameCounterTouchDownMove++;
////    			
////        		x = event.getX();
////        		y = mViewHeight - event.getY();
////        		
////    	        if (getTouchedWithinRegionMove(x, y)) {
////    	        	mTempMovePress = true;
////    	        	inputSystem.touchMovePress = true;
////    	        	
////		        	touchMoveDown(inputSystem, x, y);
////		        	
////	    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////    	        } else if (getTouchedWithinRegionFire(x, y)) {
////    	        	mTempFirePress = true;
////    	        	inputSystem.touchFirePress = true;
////    	        	
////        			touchFireDown(inputSystem, x, y);
////        			
////	    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////		        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////		        	mTempWeapon1Press = true;
////		        	inputSystem.touchWeapon1Press = true;
////
////		        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////		        	mTempWeapon2Press = true;
////		        	inputSystem.touchWeapon2Press = true;
////
////		        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////		        	mTempViewangleBarPress = true;
////		        	inputSystem.touchViewangleBarPress = true;
////		        	
////		        	touchViewangleBarDown(hudSystem, y);
////
////		        }
////    	        
////    	        if (!mTempMovePress) {
////    	        	inputSystem.touchMovePress = false;
////    	        }
////    	        if (!mTempFirePress) {
////    	        	inputSystem.touchFirePress = false;
////    	        }
////    	        if (!mTempWeapon1Press) {
////    	        	inputSystem.touchWeapon1Press = false;
////    	        }
////    	        if (!mTempWeapon2Press) {
////    	        	inputSystem.touchWeapon2Press = false;
////    	        }
////    	        if (!mTempViewangleBarPress) {
////    	        	inputSystem.touchViewangleBarPress = false;
////    	        }
////    	        	
////    			mTempMovePress = false;
////    			mTempFirePress = false;
////    			mTempWeapon1Press = false;
////    			mTempWeapon2Press = false;
////    			mTempViewangleBarPress = false;
////        		
////        		break;
////    		
////    		case MotionEvent.ACTION_MOVE:
////    			GameParameters.gameCounterTouchDownMove++;
////    			
////    			for (int i = 0; i < pointerCount; i++) {
////    				
////	        		x = event.getX(i);
////	        		y = mViewHeight - event.getY(i);
////	        		
////	    	        if (getTouchedWithinRegionMove(x, y)) {
////	    	        	mTempMovePress = true;
////	    	        	inputSystem.touchMovePress = true;
////	    	        	
////			        	touchMoveDown(inputSystem, x, y);
////			        	
////		    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////	    	        } else if (getTouchedWithinRegionFire(x, y)) {
////	    	        	mTempFirePress = true;
////	    	        	inputSystem.touchFirePress = true;
////	    	        	
////	        			touchFireDown(inputSystem, x, y);
////	        			
////		    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////			        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////			        	mTempWeapon1Press = true;
////			        	inputSystem.touchWeapon1Press = true;
////
////			        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////			        	mTempWeapon2Press = true;
////			        	inputSystem.touchWeapon2Press = true;
////
////			        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////			        	mTempViewangleBarPress = true;
////			        	inputSystem.touchViewangleBarPress = true;
////			        	
////			        	touchViewangleBarDown(hudSystem, y);
////
////			        }
////    			}
////    			
////    	        if (!mTempMovePress) {
////    	        	inputSystem.touchMovePress = false;
////    	        }
////    	        if (!mTempFirePress) {
////    	        	inputSystem.touchFirePress = false;
////    	        }
////    	        if (!mTempWeapon1Press) {
////    	        	inputSystem.touchWeapon1Press = false;
////    	        }
////    	        if (!mTempWeapon2Press) {
////    	        	inputSystem.touchWeapon2Press = false;
////    	        }
////    	        if (!mTempViewangleBarPress) {
////    	        	inputSystem.touchViewangleBarPress = false;
////    	        }
////    			
////    			mTempMovePress = false;
////    			mTempFirePress = false;
////    			mTempWeapon1Press = false;
////    			mTempWeapon2Press = false;
////    			mTempViewangleBarPress = false;
////    			
////            	break;
////    		
////    		case MotionEvent.ACTION_POINTER_UP:
////    			GameParameters.gameCounterTouchUp++;
////    			
////    			for (int i = 0; i < pointerCount; i++) {
////    				
////	        		x = event.getX(i);
////	        		y = mViewHeight - event.getY(i);
////	        		
////	    	        if (getTouchedWithinRegionMove(x, y)) {
////	    	        	mTempMovePress = true;
////	    	        	inputSystem.touchMovePress = true;
////	    	        	
////			        	touchMoveDown(inputSystem, x, y);
////			        	
////		    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////	    	        } else if (getTouchedWithinRegionFire(x, y)) {
////	    	        	mTempFirePress = true;
////	    	        	inputSystem.touchFirePress = true;
////	    	        	
////	        			touchFireDown(inputSystem, x, y);
////	        			
////		    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////			        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////			        	mTempWeapon1Press = true;
////			        	inputSystem.touchWeapon1Press = true;
////
////			        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////			        	mTempWeapon2Press = true;
////			        	inputSystem.touchWeapon2Press = true;
////
////			        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////			        	mTempViewangleBarPress = true;
////			        	inputSystem.touchViewangleBarPress = true;
////			        	
////			        	touchViewangleBarDown(hudSystem, y);
////
////			        }
////    			}
////    			
////    	        if (!mTempMovePress) {
////    	        	inputSystem.touchMovePress = false;
////    	        }
////    	        if (!mTempFirePress) {
////    	        	inputSystem.touchFirePress = false;
////    	        }
////    	        if (!mTempWeapon1Press) {
////    	        	inputSystem.touchWeapon1Press = false;
////    	        }
////    	        if (!mTempWeapon2Press) {
////    	        	inputSystem.touchWeapon2Press = false;
////    	        }
////    	        if (!mTempViewangleBarPress) {
////    	        	inputSystem.touchViewangleBarPress = false;
////    	        }
////    			
////    			mTempMovePress = false;
////    			mTempFirePress = false;
////    			mTempWeapon1Press = false;
////    			mTempWeapon2Press = false;
////    			mTempViewangleBarPress = false;
////    			
////            	break;
////    		
////    		case MotionEvent.ACTION_POINTER_DOWN:
////    			GameParameters.gameCounterTouchDownMove++;
////    			
////    			for (int i = 0; i < pointerCount; i++) {
////    				
////	        		x = event.getX(i);
////	        		y = mViewHeight - event.getY(i);
////	        		
////	    	        if (getTouchedWithinRegionMove(x, y)) {
////	    	        	mTempMovePress = true;
////	    	        	inputSystem.touchMovePress = true;
////	    	        	
////			        	touchMoveDown(inputSystem, x, y);
////			        	
////		    			hudSystem.setMoveButtonTopLocationTouch(x, y);
////
////	    	        } else if (getTouchedWithinRegionFire(x, y)) {
////	    	        	mTempFirePress = true;
////	    	        	inputSystem.touchFirePress = true;
////	    	        	
////	        			touchFireDown(inputSystem, x, y);
////	        			
////		    			hudSystem.setFireButtonTopLocationTouch(x, y);
////
////			        } else if (getTouchedWithinRegionWeapon1(x, y)) {  
////			        	mTempWeapon1Press = true;
////			        	inputSystem.touchWeapon1Press = true;
////
////			        } else if (getTouchedWithinRegionWeapon2(x, y)) {	
////			        	mTempWeapon2Press = true;
////			        	inputSystem.touchWeapon2Press = true;
////
////			        } else if (getTouchedWithinRegionViewangleBar(x, y)) {
////			        	mTempViewangleBarPress = true;
////			        	inputSystem.touchViewangleBarPress = true;
////			        	
////			        	touchViewangleBarDown(hudSystem, y);
////
////			        }
////    			}
////    			
////    	        if (!mTempMovePress) {
////    	        	inputSystem.touchMovePress = false;
////    	        }
////    	        if (!mTempFirePress) {
////    	        	inputSystem.touchFirePress = false;
////    	        }
////    	        if (!mTempWeapon1Press) {
////    	        	inputSystem.touchWeapon1Press = false;
////    	        }
////    	        if (!mTempWeapon2Press) {
////    	        	inputSystem.touchWeapon2Press = false;
////    	        }
////    	        if (!mTempViewangleBarPress) {
////    	        	inputSystem.touchViewangleBarPress = false;
////    	        }
////    			
////    			mTempMovePress = false;
////    			mTempFirePress = false;
////    			mTempWeapon1Press = false;
////    			mTempWeapon2Press = false;
////    			mTempViewangleBarPress = false;
////
////            	break;
////            	
////            default:
////            	break;
////    		} // end of switch statement
////    	}
//////  			
//////  	        mLastTouchTime = gameTime;
//////  		}
//    }
//  	
//	private final boolean getTouchedWithinRegionMove(float x, float y) {
//		 return (x >= mMoveButtonTouchLeftX &&
//				 y >= mMoveButtonTouchBottomY &&
//				 x <= mMoveButtonTouchLeftX + mMoveFireButtonTouchWidth &&
//				 y <= mMoveButtonTouchBottomY + mMoveFireButtonTouchHeight);
//	}
//	
//	private final boolean getTouchedWithinRegionFire(float x, float y) {
//		 return (x >= mFireButtonTouchLeftX &&
//				 y >= mFireButtonTouchBottomY &&
//				 x <= mFireButtonTouchLeftX + mMoveFireButtonTouchWidth &&
//				 y <= mFireButtonTouchBottomY + mMoveFireButtonTouchHeight);
//	}
//	
//	private final boolean getTouchedWithinRegionWeapon1(float x, float y) {
//		 return (x >= mWeapon1ButtonTouchLeftX &&
//				 y >= mWeapon1ButtonTouchBottomY &&
//				 x <= mWeapon1ButtonTouchLeftX + mWeaponButtonTouchWidth &&
//				 y <= mWeapon1ButtonTouchBottomY + mWeaponButtonTouchHeight);
//	}
//	
//	private final boolean getTouchedWithinRegionWeapon2(float x, float y) {
//		 return (x >= mWeapon2ButtonTouchLeftX &&
//				 y >= mWeapon2ButtonTouchBottomY &&
//				 x <= mWeapon2ButtonTouchLeftX + mWeaponButtonTouchWidth &&
//				 y <= mWeapon2ButtonTouchBottomY + mWeaponButtonTouchHeight);
//	}
//	
//	private final boolean getTouchedWithinRegionViewangleBar(float x, float y) {
//		 return (x >= mViewangleBarBaseTouchLeftX &&
//				 y >= mViewangleBarBaseTouchBottomY &&
//				 x <= mViewangleBarBaseTouchLeftX + mViewangleBarBaseTouchWidth &&
//				 y <= mViewangleBarBaseTouchBottomY + mViewangleBarBaseTouchHeight);
//	}
//		
//	private void touchMoveDown(InputSystem inputSystem, float touchMoveX, float touchMoveY) {	
//		float moveR = buttonAngleCalc(touchMoveX, touchMoveY, mMoveButtonCenterX, mMoveButtonCenterY);
//		moveR = moveR + ISOMETRIC;  // Isometric angle adjustment included
//		
//		float moveMultiplier = 1.0f;
//		float vx = mMoveButtonCenterX - touchMoveX;
//		float vy = mMoveButtonCenterY - touchMoveY;
//		float m = (float)Math.sqrt((vx * vx) + (vy * vy));
//		if (m > (mMoveFireButtonTouchRadius * 0.66f)) {
//			moveMultiplier = 3.0f;
//		} else if (m > (mMoveFireButtonTouchRadius * 0.33f)) {
//			moveMultiplier = 2.0f;
//		}
//		
//		// Calculate Motion Vector vx (moveX) and vz (moveZ) components
//		float moveX = (float)Math.sin(moveR * PI_OVER_180) * moveMultiplier;
//		float moveZ = (float)Math.cos(moveR * PI_OVER_180) * moveMultiplier;
//		
//		inputSystem.movePosition.set(moveX, 0.0f, moveZ, moveR);
//		inputSystem.touchMovePress = true;
//	}
//	
//	private void touchFireDown(InputSystem inputSystem, float touchFireX, float touchFireY) {		
//		float fireR = buttonAngleCalc(touchFireX, touchFireY, mFireButtonCenterX, mFireButtonCenterY);
//		fireR = fireR + ISOMETRIC;  // Isometric angle adjustment included
//		
//		inputSystem.firePosition.set(0.0f, 0.0f, 0.0f, fireR);
//		inputSystem.touchFirePress = true;
//	}
//	
//	private void touchMoveFireDown(InputSystem inputSystem, float touchMoveX, float touchMoveY, float touchFireX, float touchFireY) {		
//		float moveR = buttonAngleCalc(touchMoveX, touchMoveY, mMoveButtonCenterX, mMoveButtonCenterY);	
//		moveR = moveR + ISOMETRIC;  // Isometric angle adjustment included	
//		
//		float moveMultiplier = 1.0f;
//		float vx = mMoveButtonCenterX - touchMoveX;
//		float vy = mMoveButtonCenterY - touchMoveY;
//		float m = (float)Math.sqrt((vx * vx) + (vy * vy));
//		if (m > (mMoveFireButtonTouchRadius * 0.66f)) {
//			moveMultiplier = 3.0f;
//		} else if (m > (mMoveFireButtonTouchRadius * 0.33f)) {
//			moveMultiplier = 2.0f;
//		}
//
//		float moveX = (float)Math.sin(moveR * PI_OVER_180) * moveMultiplier;
//		float moveZ = (float)Math.cos(moveR * PI_OVER_180) * moveMultiplier;
//		
//		float fireR = buttonAngleCalc(touchFireX, touchFireY, mFireButtonCenterX, mFireButtonCenterY);
//		fireR = fireR + ISOMETRIC;  // Isometric angle adjustment included
//		
//		inputSystem.movePosition.set(moveX, 0.0f, moveZ, moveR);
//		inputSystem.touchMovePress = true;
//		
//		inputSystem.firePosition.set(moveX, 0.0f, moveZ, fireR);
//		inputSystem.touchFirePress = true;
//	}
//	
//	private void touchViewangleBarDown(HudSystem hudSystem, float touchY) {
//		CameraSystem camera = BaseObject.sSystemRegistry.cameraSystem;
//		
//		float viewY = (touchY - mViewangleBarBaseTouchBottomY) * mInverseViewScaleY;
//		float x = 0.0f;
//		float y = 0.0f;
//		float z = 0.0f;
//		
//		/* viewY range 0.0f to 80.0f (take inverse of viewScaleY)
//		 * 5 possible HudSystem ViewangleBarButton settings: 8.0f, 24.0f, 40.0f (default), 56.0f, 72.0f
//		 * Set gluLookAt() angle setting */ 
//		if (viewY < 16.0f) {
//			viewY = 8.0f;
//			x = -7.8f;
//			y = 6.75f;
//			z = 7.8f;
//		} else if (viewY < 32.0f) {
//			viewY = 24.0f;
//			x = -6.5f;
//			y = 9.25f;
//			z = 6.5f;
//		} else if (viewY < 48.0f) {
//			viewY = 40.0f;	// default
//			x = -5.0f;
//			y = 11.0f;
//			z = 5.0f;
//		} else if (viewY < 64.0f) {
//			viewY = 56.0f;
//			x = -4.0f;
//			y = 11.75f;
//			z = 4.0f;
//		} else {
//			viewY = 72.0f;
//			x = -2.75f;
//			y = 12.3f;
//			z = 2.75f;
//		}
//		
//		hudSystem.setViewangleBarButtonLocationY(viewY);
//		camera.setViewangle(x, y, z);
//	}
//	
//	/**
//	 * Calculates the angle at which an Object should be Facing
//	 * @param x
//	 * @param y
//	 * @param centerX
//	 * @param centerY
//	 * @return
//	 */
//	private float buttonAngleCalc(float x, float y, float centerX, float centerY) {
//		float buttonAngle = 0.0f;
//
//		// 2D Angle Calculation in Degrees
//		if (x <= centerX) {	        
//			if (y >= centerY) {
//				if ((y - centerY) == 0) {
//					// Set to minimal denominator
//					buttonAngle = (float)Math.atan(
//							(centerX - x) /
//							0.001f) * ONE_EIGHTY_OVER_PI;
//				} else {
//					buttonAngle = (float)Math.atan(
//							(centerX - x) /
//							(y - centerY)) * ONE_EIGHTY_OVER_PI;
//				}
//			} else {
//				if ((centerX - x) == 0) {
//					// Set to minimal denominator
//					buttonAngle = 90.0f + (float)Math.atan(
//							(centerY - y) /
//							0.001f) * ONE_EIGHTY_OVER_PI;
//				} else {
//					buttonAngle = 90.0f + (float)Math.atan(
//							(centerY - y) /
//							(centerX - x)) * ONE_EIGHTY_OVER_PI;
//				}
//			}
//		} else {
//			if (y < centerY) {
//				buttonAngle = 180.0f + (float)Math.atan(
//						(x - centerX) /
//						(centerY - y)) * ONE_EIGHTY_OVER_PI;
//			} else {
//				buttonAngle = 270.0f + (float)Math.atan(
//						(y - centerY) /
//						(x - centerX)) * ONE_EIGHTY_OVER_PI;
//			}
//		}
//		return buttonAngle;
//	}
    
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameRenderer onSurfaceCreated()");	
    	}
    	
//    	Log.i("HudTest", "GameRenderer onSurfaceCreated()");
    	
    	mGL = gl;
    	
    	// FIXME 10/4/12 REQUIRED FOR HUD? RE-ENABLE?
//    	// Set Hud Color
//    	gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
    
    	// FIXME 10/21/12 TEMP DISABLE
//    	gl.glShadeModel(GL10.GL_SMOOTH);
    	gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//    	gl.glClearColor(0.0f, 0.f, 0.0f, 0.5f);
//    	gl.glClearDepthf(1.0f);
//    	gl.glEnable(GL10.GL_DEPTH_TEST);
//    	gl.glDepthFunc(GL10.GL_LEQUAL);
    	
    	// TODO Re-enable and test GL_FASTEST vs GL_NICEST
        /*
         * Some one-time OpenGL initialization can be made here probably based
         * on features of this particular context
         */
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        
        // TODO Required here or in DrawableDroid?
//		gl.glClearDepthf(1.0f);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // FIXME END TEMP
       
        // FIXME Study
        String extensions = gl.glGetString(GL10.GL_EXTENSIONS); 
        String version = gl.glGetString(GL10.GL_VERSION);
        String renderer = gl.glGetString(GL10.GL_RENDERER);
        boolean isSoftwareRenderer = renderer.contains("PixelFlinger");
        boolean isOpenGL10 = version.contains("1.0");
        boolean supportsDrawTexture = extensions.contains("draw_texture");
        // VBOs are standard in GLES1.1
        // No use using VBOs when software renderering, esp. since older versions of the software renderer
        // had a crash bug related to freeing VBOs.
        boolean supportsVBOs = !isSoftwareRenderer && (!isOpenGL10 || extensions.contains("vertex_buffer_object"));
//        ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
        GameParameters.supportsDrawTexture = supportsDrawTexture;
        GameParameters.supportsVBOs = supportsVBOs;
//        params.supportsDrawTexture = supportsDrawTexture;
//        params.supportsVBOs = supportsVBOs;
        
        mGame.onSurfaceCreated(gl);
//        mGame.onSurfaceCreated(mGL);
//        mGame.onSurfaceCreated();
        
        // FIXME 10/21/12 TEMP DISABLE
//		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
    	gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DITHER);
        
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);

        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        
        /* FIXME Can 3D Object Blend (e.g. laser wall alpha blend) also use the GL_ONE setting or does it
         * require the GL_SRC_ALPHA setting? If GL_SRC_ALPHA, then will need to move both settings back
         * to their respective beginDrawing() and beginDrawingHud() methods. */
        // Blend makes HUD texture background transparent (otherwise black)
        gl.glEnable(GL10.GL_BLEND);
        // 2D Hud Blend
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // 3D Object Blend
//        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // FIXME END TEMP
        
        // FIXME 10/20/12 TEMP DISABLE
        gl.glFrontFace(GL10.GL_CCW);
        
//        // Enable GL_TEXTURE_2D for DrawableFarBackground and Hud
//        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameRenderer onSurfaceChanged()");	
    	}
             
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
        
//        // FIXME Temp static code
//        int viewWidth = 480;
//        int viewHeight = 320;
        
        //mWidth = w;0
        //mHeight = h;
    	// ensure the same aspect ratio as the game
//    	float scaleX = (float)w / mGameWidth;
//    	float scaleY =  (float)h / mGameHeight;
    	final int viewportWidth = (int)(mGameWidth * mViewScaleX);
    	final int viewportHeight = (int)(mGameHeight * mViewScaleY);
//    	final int viewportWidth = (int)(mGameWidth * scaleX);
//    	final int viewportHeight = (int)(mGameHeight * scaleY);
    	
//        DebugLog.d("GameRenderer", "onSurfaceChanged() scaleX, scaleY = " + scaleX + ", " + scaleY);
//        DebugLog.d("GameRenderer", "onSurfaceChanged() viewportWidth, viewportHeight = " + viewportWidth + ", " + viewportHeight);
    	
//    	// FIXME Currently set to 50% scale?  Test different settings. Note: duplicate setting in DrawableDroid.
//        gl.glViewport(0, 0, viewWidth, viewHeight);
        gl.glViewport(0, 0, viewportWidth, viewportHeight);
//        mScaleX = scaleX;
//        mScaleY = scaleY;
        
        // FIXME 10/5/12 TEST. Moved here fom beginDrawing() with Push/PopMatrix in beginDrawingHud()
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        GLU.gluPerspective(gl, 40.0f, (float)mGameWidth / (float)mGameHeight, 1.0f, 100.0f);
        
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
//        Log.i("HudTest", "GameRenderer onSurfaceChanged() mGameWidth, mGameHeight; viewportWidth, viewportHeight = " +
//        		mGameWidth + ", " + mGameHeight + "; " + viewportWidth + ", " + viewportHeight);

//        // TODO Check ratio properly calculated and set
//        /*
//         * Set our projection matrix. This doesn't have to be done each time we
//         * draw, but usually a new projection needs to be set when the viewport
//         * is resized.
//         */
//        float ratio = (float) mWidth / mHeight;
//        
//        DebugLog.d("GameRenderer", "onSurfaceChanged() ratio = " + ratio);
        
        // FIXME START DISABLED 9/16/12.
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//        // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Re-add to DrawableDroid? Study.
////        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
//        
//        // Moved to DrawableDroid
////        // gluPerspective() for DrawableDroid, glOrthof() for DrawableBitmap
////        GLU.gluPerspective(gl, 45.0f, mWidth / mHeight, 0.1f, 100.0f);
//////        GLU.gluPerspective(gl, 45.0f, viewWidth / viewHeight, 0.1f, 100.0f);
////        gl.glMatrixMode(GL10.GL_MODELVIEW);
////        gl.glLoadIdentity();
//        
//        // FIXME START Drawable Droid Test 9/8/12
//        GLU.gluPerspective(gl, 40.0f, (float)mGameWidth / (float)mGameHeight, 1.0f, 100.0f);
//        
//        // TODO Is this required?  Already set in onSurfaceChanged()
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        
////        // Adjust cameraX,Y,Z center when using GamePlay isometric angle
////        if (viewangleX < -0.1f) {
////        	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
////        			cameraX - 0.5f, cameraY + 0.5f, cameraZ + 0.5f, 0.0f, 1.0f, 0.0f);
////        } else {
////        	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
////        			cameraX, cameraY + 1.0f, cameraZ, 0.0f, 1.0f, 0.0f);	
////        }
//		
        // FIXME END DISABLED 9/16/12.
        
//		gl.glFrontFace(GL10.GL_CCW);
        
//		// Enable the vertex, texture and normal state
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        // FIXME END Drawable Droid Test 9/8/12
        
        mGame.onSurfaceReady();
    }
    
    public void onSurfaceLost() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameRenderer onSurfaceLost()");	
    	}
  	
    	mGame.onSurfaceLost();
    }
    
    public synchronized void onPause() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameRenderer onPause()");	
    	}
    	
    	/* Stop waiting to avoid deadlock.
    	   XXX: this is a hack.  Probably this renderer
    	   should just use GLSurfaceView's non-continuous render
    	   mode. */
    	synchronized(mDrawLock) {
    		mDrawQueueChanged = true;
    		mDrawLock.notify();
    	}
    	
    	synchronized(mDrawLockHud) {
    		mDrawQueueChangedHud = true;
    		mDrawLockHud.notify();
    	}
    	
    	mDrawPaused = true;
    }
    
    public synchronized void onResume() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameRenderer onResume()");	
    	}
  	
    	synchronized(mDrawLock) {
    		mDrawQueueChanged = false;
    		mDrawLock.notifyAll();
    	}
    	
    	synchronized(mDrawLockHud) {
    		mDrawQueueChangedHud = false;
    		mDrawLockHud.notifyAll();
    	}
    	
    	mDrawPaused = false;
  }
    
    public void requestCallback() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "GameRenderer requestCallback()");	
    	}
    	
//    	mCallbackTimer = BaseObject.sSystemRegistry.timeSystem.getGameTime();
////    	mCallbackTimer = SystemClock.uptimeMillis();
//    	
//    	Log.i("GameFlow", "GameRenderer requestCallback() mCallbackTimer = " + mCallbackTimer);
  	
    	mCallbackRequested = true;
    }

    /**
     * This function blocks while drawFrame() is in progress, and may be used by other threads to
     * determine when drawing is occurring.
     */
    public synchronized void waitDrawingComplete() {
//    	Log.i("GameFlow", "GameRenderer waitDrawingComplete()");
    	
    	GameParameters.rendererWaitDrawCompleteCounter++;
    	
    	// wait
    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    public synchronized void checkGLError(GL10 gl) {
    	int error = ((GL10)gl).glGetError();
    	if (error != GL10.GL_NO_ERROR) {
    		Log.e("GameRenderer", "GLError!");
    		throw new RuntimeException("GLError 0x" + Integer.toHexString(error));
//    	} else {
//    		if (debugLog) {
//        		DebugLog.d("GameRenderer", "No GLError");	
//    		}
    	}
    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public boolean getDrawPaused() {
    	return mDrawPaused;
    }
    
    public void setGL(GL10 gl) {
    	mGL = gl;
    }
    
//    public void setContext(Context newContext) {
////        DebugLog.d("GameRenderer", "setContext()");
//    	
//        mContext = newContext;
//    }
    
//    public void setButtons(int viewWidth, int viewHeight, float inverseViewScaleY,
//    		float moveFireButtonTouchWidth, float moveFireButtonTouchHeight, float moveFireButtonTouchRadius,
//    		float moveButtonTouchLeftX, float moveButtonTouchBottomY, float moveButtonCenterX, float moveButtonCenterY,
//    		float moveButtonTopRenderLeftX, float moveButtonTopRenderBottomY, 
//    		float fireButtonTouchLeftX, float fireButtonTouchBottomY, float fireButtonCenterX, float fireButtonCenterY,
//    		float fireButtonTopRenderLeftX, float fireButtonTopRenderBottomY,
//    		float weaponButtonTouchWidth, float weaponButtonTouchHeight,
//    		float weapon1ButtonTouchLeftX, float weapon1ButtonTouchBottomY,
//    		float weapon2ButtonTouchLeftX, float weapon2ButtonTouchBottomY,
//    		float viewangleBarBaseTouchLeftX, float viewangleBarBaseTouchBottomY,
//    		float viewangleBarBaseTouchWidth, float viewangleBarBaseTouchHeight,
//    		float viewangleBarButtonTouchLeftX, float viewangleBarButtonTouchBottomY,
//    		float viewangleBarButtonTouchWidth, float viewangleBarButtonTouchHeight) {
//    	
//    	mViewWidth = viewWidth;
//    	mViewHeight = viewHeight;
//    	mInverseViewScaleY = inverseViewScaleY;
//    	
//    	mMoveFireButtonTouchWidth = moveFireButtonTouchWidth;
//    	mMoveFireButtonTouchHeight = moveFireButtonTouchHeight;
//    	mMoveFireButtonTouchRadius = moveFireButtonTouchRadius;
//    	
//    	mMoveButtonTouchLeftX = moveButtonTouchLeftX;
//    	mMoveButtonTouchBottomY = moveButtonTouchBottomY;
//    	mMoveButtonCenterX = moveButtonCenterX;
//    	mMoveButtonCenterY = moveButtonCenterY;
//    	mMoveButtonTopRenderLeftX = moveButtonTopRenderLeftX;
//    	mMoveButtonTopRenderBottomY = moveButtonTopRenderBottomY;
//    	
//    	mFireButtonTouchLeftX = fireButtonTouchLeftX;
//    	mFireButtonTouchBottomY = fireButtonTouchBottomY;
//    	mFireButtonCenterX = fireButtonCenterX;
//    	mFireButtonCenterY = fireButtonCenterY;
//    	mFireButtonTopRenderLeftX = fireButtonTopRenderLeftX;
//    	mFireButtonTopRenderBottomY = fireButtonTopRenderBottomY;
//    	
//		mWeaponButtonTouchWidth = weaponButtonTouchWidth;
//		mWeaponButtonTouchHeight = weaponButtonTouchHeight;
//		mWeapon1ButtonTouchLeftX = weapon1ButtonTouchLeftX;
//		mWeapon1ButtonTouchBottomY = weapon1ButtonTouchBottomY;
//		mWeapon2ButtonTouchLeftX = weapon2ButtonTouchLeftX;
//		mWeapon2ButtonTouchBottomY = weapon2ButtonTouchBottomY;
//		mViewangleBarBaseTouchLeftX = viewangleBarBaseTouchLeftX;
//		mViewangleBarBaseTouchBottomY = viewangleBarBaseTouchBottomY;
//		mViewangleBarBaseTouchWidth = viewangleBarBaseTouchWidth;
//		mViewangleBarBaseTouchHeight = viewangleBarBaseTouchHeight;
//		mViewangleBarButtonTouchLeftX = viewangleBarButtonTouchLeftX;
//		mViewangleBarButtonTouchBottomY = viewangleBarButtonTouchBottomY;
//		mViewangleBarButtonTouchWidth = viewangleBarButtonTouchWidth;
//		mViewangleBarButtonTouchHeight = viewangleBarButtonTouchHeight;
//    }
//    
////public class GameRenderer implements GLSurfaceView.Renderer {
//    private static final int PROFILE_REPORT_DELAY = 3 * 1000;
//    
//    private boolean debugLog = false;
//    
//    private int mWidth;
//    private int mHeight;
//    private int mHalfWidth;
//    private int mHalfHeight;
//    
//    private float mScaleX;
//    private float mScaleY;
//    private Context mContext;
//    private long mLastTime;
//    private int mProfileFrames;
//    private long mProfileWaitTime;
//    private long mProfileFrameTime;
//    private long mProfileSubmitTime;
//    private int mProfileObjectCount;
//    
//    private ObjectManager mDrawQueue;
//    private boolean mDrawQueueChanged;
//    private Game mGame;
//    private Object mDrawLock;
//    
////    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////    GL10 mGL;
////    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    
//    float mCameraX;
//    float mCameraY;
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    float mCameraZ;
//    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    
//    boolean mCallbackRequested;
//        
//    public GameRenderer(Context context, Game game, int gameWidth, int gameHeight) {
//    	debugLog = DebugLog.getDebugLog();
//    	
//        mContext = context;
//        mGame = game;
//        mWidth = gameWidth;
//        mHeight = gameHeight;
//        mHalfWidth = gameWidth / 2;
//        mHalfHeight = gameHeight / 2;
//        mScaleX = 1.0f;
//        mScaleY = 1.0f;
//        mDrawQueueChanged = false;
//        mDrawLock = new Object();
//        mCameraX = 0.0f;
//        mCameraY = 0.0f;
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        mCameraZ = 0.0f;
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//        mCallbackRequested = false;
//    }
//
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
////    	DebugLog.d("GameRenderer", "onSurfaceCreated()");
//    	
////    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
////    	OpenGLSystem.setGL(gl);
//////    	mGL = gl;
////    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    	
////    	gl.glShadeModel(GL10.GL_SMOOTH);
//    	gl.glClearColor(0.0f, 0.f, 0.0f, 1.0f);
////    	gl.glClearColor(0.0f, 0.f, 0.0f, 0.5f);
////    	gl.glClearDepthf(1.0f);
////    	gl.glEnable(GL10.GL_DEPTH_TEST);
////    	gl.glDepthFunc(GL10.GL_LEQUAL);
//    	
//    	// TODO Re-enable and test GL_FASTEST vs GL_NICEST
//        /*
//         * Some one-time OpenGL initialization can be made here probably based
//         * on features of this particular context
//         */
//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
////        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
//        
//        // TODO Required here or in DrawableDroid?
////		gl.glClearDepthf(1.0f);
//
//        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//       
//        // FIXME Study
//        String extensions = gl.glGetString(GL10.GL_EXTENSIONS); 
//        String version = gl.glGetString(GL10.GL_VERSION);
//        String renderer = gl.glGetString(GL10.GL_RENDERER);
//        boolean isSoftwareRenderer = renderer.contains("PixelFlinger");
//        boolean isOpenGL10 = version.contains("1.0");
//        boolean supportsDrawTexture = extensions.contains("draw_texture");
//        // VBOs are standard in GLES1.1
//        // No use using VBOs when software renderering, esp. since older versions of the software renderer
//        // had a crash bug related to freeing VBOs.
//        boolean supportsVBOs = !isSoftwareRenderer && (!isOpenGL10 || extensions.contains("vertex_buffer_object"));
//        ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
//        params.supportsDrawTexture = supportsDrawTexture;
//        params.supportsVBOs = supportsVBOs;
//          
////        hackBrokenDevices();
//        
//        if (debugLog) {
//            DebugLog.i("Graphics Support", version + " (" + renderer + "): " +(supportsDrawTexture ?  "draw texture" : "") + (supportsVBOs ? ", vbos" : ""));	
//        }
//        
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        mGame.onSurfaceCreated(gl);
////        mGame.onSurfaceCreated(mGL);
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////        mGame.onSurfaceCreated();
//    }
//    
////    private void hackBrokenDevices() {
////    	// Some devices are broken.  Fix them here.  This is pretty much the only
////    	// device-specific code in the whole project.  Ugh.
////        ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
////
////        // TODO Check to see if Motorola Morrison model fixed this issue, then delete code
////    	if (Build.PRODUCT.contains("morrison")) {
////        	DebugLog.d("GameRenderer", "hackBrokenDevices() Product contains Motorola Morrison");
////        	
////    		// This is the Motorola Cliq.  This device LIES and says it supports
////    		// VBOs, which it actually does not (or, more likely, the extensions string
////    		// is correct and the GL JNI glue is broken).
////    		params.supportsVBOs = false;
////    		// If Motorola fixes this, I should switch to using the fingerprint
////    		// (blur/morrison/morrison/morrison:1.5/CUPCAKE/091007:user/ota-rel-keys,release-keys)
////    		// instead of the product name so that newer versions use VBOs.
////    	}
////    }
//    
//    public void spawnObjects(LevelSystem level) {
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	GL10 gl = OpenGLSystem.getGL();
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    	checkGLError(gl);
//    	if (gl != null) {
//    		level.spawnObjects(gl);
//    	} else {
//    		DebugLog.d("VBO", "GameRenderer spawnObjects() gl = null!");
//    	}
//    	checkGLError(gl);
////    	checkGLError(mGL);
////    	if (mGL != null) {
////    		level.spawnObjects(mGL);
////    	} else {
////    		DebugLog.d("VBO", "GameRenderer spawnObjects() mGL = null!");
////    	}
////    	checkGLError(mGL);
//    }
//    
//    public void loadTextures(TextureLibrary library) {
////        public void loadTextures(GL10 gl, TextureLibrary library) {
////        DebugLog.d("GameRenderer", "loadTextures()");
//        
////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        GL10 gl = OpenGLSystem.getGL();
////        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////    	
////        if (gl != null) {
////            library.loadAll(mContext, gl);
////        }
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	GL10 gl = OpenGLSystem.getGL();
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//        
//        if (gl != null) {
//            library.loadAll(mContext, gl);
//        }
////        if (mGL != null) {
////            library.loadAll(mContext, mGL);
////        }
//    }
//    
//    public void flushTextures(TextureLibrary library) {
////        public void flushTextures(GL10 gl, TextureLibrary library) {
////        DebugLog.d("GameRenderer", "flushTextures()");
//    	
////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        GL10 gl = OpenGLSystem.getGL();
////        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////        
////        if (gl != null) {
////            library.deleteAll(gl);
////        }
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	GL10 gl = OpenGLSystem.getGL();
//    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//        
//        if (gl != null) {
//            library.deleteAll(gl);
//        }
////        if (mGL != null) {
////            library.deleteAll(mGL);
////        }
//    }
//    
////    public void loadBuffers(BufferLibrary library) {
//////      public void loadBuffers(GL10 gl, BufferLibrary library) {
//////        DebugLog.d("GameRenderer", "loadBuffers()");
////        
//////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//////        GL10 gl = OpenGLSystem.getGL();
//////        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//////      	
//////      	// TODO Re-enable
//////          if (gl != null) {
////////              library.generateHardwareBuffers(gl);
//////          }
////    	
////        if (mGL != null) {
//////          library.generateHardwareBuffers(mGL);
////        }
////    }
//    
////    public void flushBuffers(BufferLibrary library) {
//////      public void flushBuffers(GL10 gl, BufferLibrary library) {
//////        DebugLog.d("GameRenderer", "flushBuffers()");
////        
//////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//////        GL10 gl = OpenGLSystem.getGL();
//////        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//////      	
//////      	// TODO Re-enable
//////        if (gl != null) {
////////              library.releaseHardwareBuffers(gl);
//////          }
////          
////        if (mGL != null) {
//////            library.releaseHardwareBuffers(mGL);
////        }
////    }
//    
//    public void onSurfaceLost() {
////        DebugLog.d("GameRenderer", "onSurfaceLost()");
//    	
//        mGame.onSurfaceLost();
//    }
//  
//    public void requestCallback() {
////        DebugLog.d("GameRenderer", "requestCallback()");
//    	
//    	mCallbackRequested = true;
//    }
//
//    /** Draws the scene.  Note that the draw queue is locked for the duration of this function. */
//    public void onDrawFrame(GL10 gl) {
////        DebugLog.d("GameRenderer", "onDrawFrame()");
////    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
////    	OpenGLSystem.setGL(gl);
////    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//      
//        long time = SystemClock.uptimeMillis();
//        long time_delta = (time - mLastTime);
//        
//        synchronized(mDrawLock) {
//            if (!mDrawQueueChanged) {
//                while (!mDrawQueueChanged) {
//                    try {
//                    	mDrawLock.wait();
//                    } catch (InterruptedException e) {
//                        // No big deal if this wait is interrupted.
//                    }
//                }
//            }
//            mDrawQueueChanged = false;
//        }
//        
//        final long wait = SystemClock.uptimeMillis();
//        
//        if (mCallbackRequested) {
//        	mGame.onSurfaceReady();
//        	mCallbackRequested = false;
//        }
//        
//        checkGLError(gl);
//        
//        // TODO Re-enable DrawableBitmap.beginDrawing() and DrawableDroid.beginDrawing() for one time state change
//        DrawableDroid.beginDrawing(gl, mCameraX, mCameraY, mCameraZ, mWidth, mHeight);
////        DrawableBitmap.beginDrawing(gl, mWidth, mHeight);
//
////        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        RenderSystem render = sSystemRegistry.;
////        
////		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
////		gl.glClearDepthf(1.0f);
////		
////		gl.glEnable(GL10.GL_DEPTH_TEST);
////		gl.glDepthFunc(GL10.GL_LEQUAL);
////    	gl.glShadeModel(GL10.GL_SMOOTH);
////        gl.glEnable(GL10.GL_DITHER);
////        gl.glEnable(GL10.GL_LIGHTING);
////        gl.glEnable(GL10.GL_LIGHT0);
////        gl.glEnable(GL10.GL_COLOR_MATERIAL);
////        
////        gl.glMatrixMode(GL10.GL_PROJECTION);
////        gl.glLoadIdentity();
////        GLU.gluPerspective(gl, 45.0f, 480.0f / 320.0f, 1.0f, 100.0f);
//////        GLU.gluPerspective(gl, 45.0f, (float)gameWidth / (float)gameHeight, 1.0f, 100.0f);
////
////        gl.glMatrixMode(GL10.GL_MODELVIEW);
////        gl.glLoadIdentity();
////		GLU.gluLookAt(gl, 0.0f, 5.0f, 2.5f, targetX, targetY, targetZ, 0.0f, 1.0f, 0.0f);
//////		GLU.gluLookAt(gl, 0.0f, 5.0f, 2.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
////		
////		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
////		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
////		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
////		gl.glFrontFace(GL10.GL_CCW);
////		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//        
//        synchronized (this) {
//            if (mDrawQueue != null && mDrawQueue.getObjects().getCount() > 0) {  	
//                OpenGLSystem.setGL(gl);
//                boolean vboSupport = BaseObject.sSystemRegistry.contextParameters.supportsVBOs;
//                
//                if (vboSupport) {
//                	DebugLog.d("VBO", "GameRenderer onDrawFrame() VBO supported");
//                } else {
//                	DebugLog.d("VBO", "GameRenderer onDrawFrame() Software Only, no VBOs");
//                }
//                
//                FixedSizeArray<BaseObject> objects = mDrawQueue.getObjects();
//                
//                if (debugLog) {
//                    DebugLog.d("GameRenderer", "onDrawFrame() mDrawQueue.getObjects().getCount() = " + 
//                    		mDrawQueue.getObjects().getCount());	
//                }
//                
//                Object[] objectArray = objects.getArray();
//                final int count = objects.getCount();
//                
//                final float scaleX = mScaleX;
//                final float scaleY = mScaleY;
//                final float halfWidth = mHalfWidth;
//                final float halfHeight = mHalfHeight;
//                mProfileObjectCount += count;
//                for (int i = 0; i < count; i++) {
//                    RenderElement element = (RenderElement)objectArray[i];
//
//                    // FIXME Optimize code
//                    float x = element.x;
//                    float y = element.y;
//                    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//                    float z = element.z;
//                    float r = element.r;
//                    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//                    
////                    /* TODO Update Camera code for x,y,z,r. Change camera view from halfWidth to smaller scale? 
////                     * How does Replica buffer background outside of viewable area? */
////                    if (element.cameraRelative) {
////                        DebugLog.d("GameRenderer", "onDrawFrame() cameraRelative x, mCameraX, " + 
////                        		"halfWidth; y, mCameraY, halfHeight = " + x + ", " + mCameraX + ", " +
////                        		halfWidth + "; " + y + ", " + mCameraY + ", " + halfHeight);
////                    	
////                        
////                        /* XXX For Droid, mCameraX = halfWidth and mCameraY = halfHeight, so no effect
////                         * Confirm effect on Enemies.  Note: Background is not cameraRelative. */
////                    	x = (x - mCameraX) + halfWidth;
////                    	y = (y - mCameraY) + halfHeight;
////                    	
////                    	DebugLog.d("GameRenderer", "onDrawFrame() cameraRelative post-calc x, y = " +
////                    			x + ", " + y);
////                    }
//                    
//                	/* XXX Droid Code © 2012 FrostBlade LLC - Start */    
//                    if (debugLog) {
//                        DebugLog.d("GameRenderer", "onDrawFrame()" + "[ " + element.objectId + "] " +
//                        		"position x,y,z,r = " + x + ", " + y + ", " + z + ", " + r);	
//                    }
////                    DebugLog.d("GameRenderer", "onDrawFrame() scaleX, scaleY = " + scaleX + ", " + scaleY);
////                    DebugLog.d("GameRenderer", "onDrawFrame() mWidth, mHeight = " + mWidth + ", " + mHeight);
//                    
//                    /* TODO Move these calls back to draw()? Is it necessary to call glMatrixMode() before and after each draw()?
//                     * Either move only glPushMatrix(), glPopMatrix(), or also include glMatrixMode() in move to draw() */
//                    gl.glMatrixMode(GL10.GL_MODELVIEW);
//                    gl.glPushMatrix();
////                    gl.glLoadIdentity();
//                    
//                    /* FIXME Pass through static Object Type for each object and draw accordingly? 
//                     * Move glTranslatef(), glRotatef(), glScalef() from DrawableDroid to here?
//                     * e.g. Background will not Translate or Rotate. Player needs to Rotate locally, but Translate globally.
//                     * Check OpenGL Programming Guide Ch3 on order of Translate and Rotate for local vs global. */
//                    element.mDrawable.draw(gl, x, y, z, r, scaleX, scaleY, mWidth, mHeight);
////                    element.mDrawable.draw(gl, vboSupport, x, y, z, r, scaleX, scaleY, mWidth, mHeight);
////                    element.mDrawable.draw(x, y, scaleX, scaleY, mWidth, mHeight);
////                    element.mDrawable.draw(x, y, scaleX, scaleY);
//                    
//                    gl.glMatrixMode(GL10.GL_MODELVIEW);
//                    gl.glPopMatrix();
//                    
//                    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//                }
//                OpenGLSystem.setGL(null);
//            } else if (mDrawQueue == null) {
////            	DebugLog.d("GameRenderer", "onDrawFrame() mDrawQueue == null");
//            	
//                // If we have no draw queue, clear the screen.  If we have a draw queue that
//                // is empty, we'll leave the frame buffer alone.
//                gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//            }
//        }
//        
//        // FIXME RE-ENABLE
//        DrawableDroid.endDrawing(gl);
////        DrawableBitmap.endDrawing(gl);
//        
//        checkGLError(gl);
//        
//        long time2 = SystemClock.uptimeMillis();
//        mLastTime = time2;
//
//        mProfileFrameTime += time_delta;
//        mProfileSubmitTime += time2 - time;
//        mProfileWaitTime += wait - time;
//        
//        mProfileFrames++;
//        if (mProfileFrameTime > PROFILE_REPORT_DELAY) {
//        	final int validFrames = mProfileFrames;
//            final long averageFrameTime = mProfileFrameTime / validFrames;
//            final long averageSubmitTime = mProfileSubmitTime / validFrames;
//            final float averageObjectsPerFrame = (float)mProfileObjectCount / validFrames;
//            final long averageWaitTime = mProfileWaitTime / validFrames;
//
//            if (debugLog) {
//                DebugLog.d("Render Profile", 
//                		"Average Submit: " + averageSubmitTime 
//                		+ "  Average Draw: " + averageFrameTime 
//                		+ " Objects/Frame: " + averageObjectsPerFrame
//                		+ " Wait Time: " + averageWaitTime);	
//            }
//           
//            mProfileFrameTime = 0;
//            mProfileSubmitTime = 0;
//            mProfileFrames = 0;
//            mProfileObjectCount = 0;
//        }
//    }
//
//    public void onSurfaceChanged(GL10 gl, int w, int h) {
////        DebugLog.d("GameRenderer", "onSurfaceChanged() Surface Size Change: " + w + ", " + h);
//             
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////    	OpenGLSystem.setGL(gl);
//        
////        // FIXME Temp static code
////        int viewWidth = 480;
////        int viewHeight = 320;
//        
//        //mWidth = w;0
//        //mHeight = h;
//    	// ensure the same aspect ratio as the game
//    	float scaleX = (float)w / mWidth;
//    	float scaleY =  (float)h / mHeight;
//    	final int viewportWidth = (int)(mWidth * scaleX);
//    	final int viewportHeight = (int)(mHeight * scaleY);
//    	
////        DebugLog.d("GameRenderer", "onSurfaceChanged() scaleX, scaleY = " + scaleX + ", " + scaleY);
////        DebugLog.d("GameRenderer", "onSurfaceChanged() viewportWidth, viewportHeight = " + viewportWidth + ", " + viewportHeight);
//    	
////    	// FIXME Currently set to 50% scale?  Test different settings. Note: duplicate setting in DrawableDroid.
////        gl.glViewport(0, 0, viewWidth, viewHeight);
//        gl.glViewport(0, 0, viewportWidth, viewportHeight);
//        mScaleX = scaleX;
//        mScaleY = scaleY;
//
////        // TODO Check ratio properly calculated and set
////        /*
////         * Set our projection matrix. This doesn't have to be done each time we
////         * draw, but usually a new projection needs to be set when the viewport
////         * is resized.
////         */
////        float ratio = (float) mWidth / mHeight;
////        
////        DebugLog.d("GameRenderer", "onSurfaceChanged() ratio = " + ratio);
//        
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//        // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Re-add to DrawableDroid? Study.
////        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
//        
//        // Moved to DrawableDroid
////        // gluPerspective() for DrawableDroid, glOrthof() for DrawableBitmap
////        GLU.gluPerspective(gl, 45.0f, mWidth / mHeight, 0.1f, 100.0f);
//////        GLU.gluPerspective(gl, 45.0f, viewWidth / viewHeight, 0.1f, 100.0f);
////        gl.glMatrixMode(GL10.GL_MODELVIEW);
////        gl.glLoadIdentity();
//        
//        mGame.onSurfaceReady();
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    }
//
//    public synchronized void setDrawQueue(ObjectManager queue, float cameraX, float cameraY, float cameraZ) {
////    public synchronized void setDrawQueue(ObjectManager queue, float cameraX, float cameraY) {
////        DebugLog.d("GameRenderer", "setDrawQueue()");
//    	
//		mDrawQueue = queue;
//		
////        DebugLog.d("GameRenderer", "setDrawQueue queue.getCount() = " + 
////        		queue.getCount() + ", mDrawQueue.getCount() = " + mDrawQueue.getCount());
//		
//		mCameraX = cameraX;
//		mCameraY = cameraY;
//		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//		mCameraZ = cameraZ;
//		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//		
////        DebugLog.d("GameRenderer", "setDrawQueue() mCameraX,Y,Z = " + mCameraX + ", " +
////        		mCameraY + ", " + mCameraZ);
//		
//    	synchronized(mDrawLock) {
//    		mDrawQueueChanged = true;
//    		mDrawLock.notify();
//    	}
//    }
//    
//    public synchronized void onPause() {
////        DebugLog.d("GameRenderer", "onPause()");
//    	
//    	/* Stop waiting to avoid deadlock.
//    	   XXX: this is a hack.  Probably this renderer
//    	   should just use GLSurfaceView's non-continuous render
//    	   mode. */
//    	synchronized(mDrawLock) {
//    		mDrawQueueChanged = true;
//    		mDrawLock.notify();
//    	}
//    }
//
//    /**
//     * This function blocks while drawFrame() is in progress, and may be used by other threads to
//     * determine when drawing is occurring.
//     */
//    public synchronized void waitDrawingComplete() {
//    	if (debugLog) {
//            DebugLog.d("GameRenderer", "waitDrawingComplete()");	
//    	}
//    }
//    
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public synchronized void checkGLError(GL10 gl) {
//    	int error = ((GL10)gl).glGetError();
//    	if (error != GL10.GL_NO_ERROR) {
//    		if (debugLog) {
//        		DebugLog.d("VBO", "GLError!");	
//    		}
//    		throw new RuntimeException("GLError 0x" + Integer.toHexString(error));
//    	} else {
//    		if (debugLog) {
//        		DebugLog.d("VBO", "No GLError");	
//    		}
//    	}
//    }
//    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    
//    public void setContext(Context newContext) {
////        DebugLog.d("GameRenderer", "setContext()");
//    	
//        mContext = newContext;
//    }
}
