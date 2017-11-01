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
import java.lang.reflect.Type;
import java.nio.FloatBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
//import com.frostbladegames.droidconstruct.RenderSystem.RenderElement;

import com.frostbladegames.basestation9.BaseObject;
//import com.frostbladegames.basestation9.ContextParameters;
import com.frostbladegames.basestation9.SplashScreenRenderSystem.SplashScreenRenderElement;

/**
 * Renderer for SplashScreenActivity
 */
public class SplashScreenRenderer implements DroidGLSurfaceView.Renderer {
  private int mGameWidth;
  private int mGameHeight;
  private int mHalfGameWidth;
  private int mHalfGameHeight;
  
  private float mViewScaleX;
  private float mViewScaleY;
//  private Context mContext;
  private long mLastTime;
  
  private ObjectManager mDrawQueue;
  private boolean mDrawQueueChanged;
  private SplashScreenGame mGame;
//  private Game mGame;
  private Object mDrawLock;
  
  GL10 mGL;
  
  private float mCameraX;
  private float mCameraY;
  private float mCameraZ;
  private float mViewangleX;
  private float mViewangleY;
  private float mViewangleZ;
  
	private float mLightPositionX;
	
	private int mSplashTime;
  
  boolean mCallbackRequested;
      
  public SplashScreenRenderer(Context context, SplashScreenGame game, int gameWidth, int gameHeight, float viewScaleX, float viewScaleY) {
	  if (GameParameters.debug) {
		  Log.i("GameFlow", "SplashScreenRenderer <constructor>");  
	  }
	  
//      mContext = context;
      mGame = game;
      mGameWidth = gameWidth;
      mGameHeight = gameHeight;
      mHalfGameWidth = gameWidth / 2;
      mHalfGameHeight = gameHeight / 2;
      mViewScaleX = viewScaleX;
      mViewScaleY = viewScaleY;
      mDrawQueueChanged = false;
      mDrawLock = new Object();
      mCameraX = 0.0f;
      mCameraY = 0.0f;
      mCameraZ = 0.0f;
      mViewangleX = 0.0f;
      mViewangleY = 0.0f;
      mViewangleZ = 0.0f;
      
      mLightPositionX = -4.0f;
      
      mSplashTime = 0;
      
      mCallbackRequested = false;
  }
  
  public void spawnObjects(LevelSystem level) {
	  // XXX Re-enable for GL Debug as required
//	  checkGLError(mGL);
	  
	  if (mGL != null) {
		  level.spawnObjects(mGL);
	  } else {
		  Log.e("SplashScreenRenderer", "spawnObjects() mGL = null!");
	  }
	  
	  // XXX Re-enable for GL Debug as required
//	  checkGLError(mGL);
  }
  
  // FIXME DELETED 11/11/12
//  public void loadTextures(TextureLibrary library) {      
//      if (mGL != null) {
//          library.loadAll(mContext, mGL);
//      }
//  }
//  
//  public void flushTextures(TextureLibrary library) {      
//      if (mGL != null) {
//          library.deleteAll(mGL);
//      }
//  }
  
  public void loadBuffers(BufferLibrary library) {  	
      if (mGL != null) {
//        library.generateHardwareBuffers(mGL);
      }
  }
  
  public void flushBuffers(BufferLibrary library) {        
      if (mGL != null) {
//          library.releaseHardwareBuffers(mGL);
      }
  }
  
  public synchronized void setDrawQueue(ObjectManager queue, float cameraX, float cameraY, float cameraZ, 
  		float viewangleX, float viewangleY, float viewangleZ, int splashTime) {
	  
		mDrawQueue = queue;
		
		mCameraX = cameraX;
		mCameraY = cameraY;
		mCameraZ = cameraZ;
		mViewangleX = viewangleX;
		mViewangleY = viewangleY;
		mViewangleZ = viewangleZ;
		
		mSplashTime = splashTime;
		
    	synchronized(mDrawLock) {
    		mDrawQueueChanged = true;
    		mDrawLock.notify();
    	}
  }

  /** Draws the scene.  Note that the draw queue is locked for the duration of this function. */
  public void onDrawFrame(GL10 gl) {
      long time = SystemClock.uptimeMillis();
      long time_delta = (time - mLastTime);
      
      synchronized(mDrawLock) {
          if (!mDrawQueueChanged) {
              while (!mDrawQueueChanged) {              	
                  try {
                  	mDrawLock.wait();
                  } catch (InterruptedException e) {
                      // No big deal if this wait is interrupted.
                  }
              }
          }
          mDrawQueueChanged = false;
      }
      
      final long wait = SystemClock.uptimeMillis();
      
      if (mCallbackRequested) {
    	  if (GameParameters.debug) {
        	  Log.i("GameFlow", "SplashScreenRenderer onDrawFrame() mCallbackRequested = TRUE, therefore mGame.onSurfaceReady");  
    	  }
    	  
      		mGame.onSurfaceReady();
      		mCallbackRequested = false;
      }
      
	  // XXX Re-enable for GL Debug as required
//      checkGLError(gl);
      
      FloatBuffer light1PositionBuffer = FloatBuffer.wrap(new float[]{mLightPositionX, 1.25f, 1.0f, 1.0f});
      
      DrawableDroid.beginDrawingSplashScreen(gl, mCameraX, mCameraY, mCameraZ, mViewangleX, mViewangleY, mViewangleZ,
      		mGameWidth, mGameHeight, light1PositionBuffer);
      
      if (mSplashTime > 1000) {
          mLightPositionX += 0.2f; 
//          mLightPositionX += 0.1f;  
      }
      
      synchronized (this) {
          if (mDrawQueue != null && mDrawQueue.getObjects().getCount() > 0) {
              OpenGLSystem.setGL(gl);
              
              FixedSizeArray<BaseObject> objects = mDrawQueue.getObjects();
              
              Object[] objectArray = objects.getArray();
              final int count = objects.getCount();
              
              for (int i = 0; i < count; i++) {
                  SplashScreenRenderElement element = (SplashScreenRenderElement)objectArray[i];
//                  RenderElement element = (RenderElement)objectArray[i];

                  float x = element.x;
                  float y = element.y;
                  float z = element.z;
                  float r = element.r;
                  float oX = element.oX;
                  float oY = element.oY;
                  float oZ = element.oZ;
                  float rZ = element.rZ;
                  
                  int objectId = element.objectId;
                  
//                  GameObjectGroups.Type renderType = element.objectType;
                  
                  /* TODO Move these calls back to draw()? Is it necessary to call glMatrixMode() before and after each draw()?
                   * Either move only glPushMatrix(), glPopMatrix(), or also include glMatrixMode() in move to draw() */
                  gl.glMatrixMode(GL10.GL_MODELVIEW);
                  gl.glPushMatrix();
//                  gl.glLoadIdentity();
                  
                  /* FIXME Pass through static Object Type for each object and draw accordingly? 
                   * Move glTranslatef(), glRotatef(), glScalef() from DrawableDroid to here?
                   * e.g. Background will not Translate or Rotate. Player needs to Rotate locally, but Translate globally.
                   * Check OpenGL Programming Guide Ch3 on order of Translate and Rotate for local vs global. */
                  element.mDrawable.draw(gl, x, y, z, r, oX, oY, oZ,
                		  rZ, mViewScaleX, mViewScaleY, mGameWidth, mGameHeight, objectId);
                  
                  gl.glMatrixMode(GL10.GL_MODELVIEW);
                  gl.glPopMatrix();                    
              }
              OpenGLSystem.setGL(null);
          } else if (mDrawQueue == null) {
              // If we have no draw queue, clear the screen.  If we have a draw queue that
              // is empty, we'll leave the frame buffer alone.
              gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
          }
      }
      
      DrawableDroid.endDrawing(gl);
      
	  // XXX Re-enable for GL Debug as required
//      checkGLError(gl);
      
      long time2 = SystemClock.uptimeMillis();
      mLastTime = time2;
  }
  
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
  	mGL = gl;
  	
//  	gl.glShadeModel(GL10.GL_SMOOTH);
  	gl.glClearColor(0.0f, 0.f, 0.0f, 1.0f);
//  	gl.glClearColor(0.0f, 0.f, 0.0f, 0.5f);
//  	gl.glClearDepthf(1.0f);
//  	gl.glEnable(GL10.GL_DEPTH_TEST);
//  	gl.glDepthFunc(GL10.GL_LEQUAL);
  	
  	// TODO Re-enable and test GL_FASTEST vs GL_NICEST
      /*
       * Some one-time OpenGL initialization can be made here probably based
       * on features of this particular context
       */
      gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
//      gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
      
      // TODO Required here or in DrawableDroid?
//		gl.glClearDepthf(1.0f);

      gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
     
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
//      ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
      GameParameters.supportsDrawTexture = supportsDrawTexture;
      GameParameters.supportsVBOs = supportsVBOs;
//      params.supportsDrawTexture = supportsDrawTexture;
//      params.supportsVBOs = supportsVBOs;

      mGame.onSurfaceCreated(mGL);
  }

  public void onSurfaceChanged(GL10 gl, int w, int h) {
  	final int viewportWidth = (int)(mGameWidth * mViewScaleX);
  	final int viewportHeight = (int)(mGameHeight * mViewScaleY);

      gl.glViewport(0, 0, viewportWidth, viewportHeight);
      
      gl.glMatrixMode(GL10.GL_PROJECTION);
      gl.glLoadIdentity();
      
      mGame.onSurfaceReady();
  }
  
  public void onSurfaceLost() {
  	mGame.onSurfaceLost();
  }
  
  public synchronized void onPause() {  	
  	/* Stop waiting to avoid deadlock.
  	   XXX: this is a hack.  Probably this renderer
  	   should just use GLSurfaceView's non-continuous render
  	   mode. */
  	synchronized(mDrawLock) {
  		mDrawQueueChanged = true;
  		mDrawLock.notify();
  	}
  }
  
//  public void stopSplash() {
//	  mGame.stopSplash();
//	  
////	  ((SplashScreenActivity)mContext).finish();
//	  
////	  ((SplashScreenActivity)mContext).goToMainMenu();
//	  
////	  ((Activity)mContext).finish();
////	  
////	  Intent i = new Intent(mContext, MainMenuActivity.class);
//////    Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
////	  mContext.startActivity(i);
//  }
  
  public void requestCallback() {
	  if (GameParameters.debug) {
		  Log.i("GameFlow", "SplashScreenRenderer requestCallback()");  
	  }
	  
  		mCallbackRequested = true;
  }

  /**
   * This function blocks while drawFrame() is in progress, and may be used by other threads to
   * determine when drawing is occurring.
   */
  public synchronized void waitDrawingComplete() {
	  // wait
  }
  
  public synchronized void checkGLError(GL10 gl) {
  	int error = ((GL10)gl).glGetError();
  	if (error != GL10.GL_NO_ERROR) {
  		Log.e("SplashScreenRenderer", "GLError!");
  		throw new RuntimeException("GLError 0x" + Integer.toHexString(error));
  	}
  }
  
//  public void setContext(Context newContext) {
//      mContext = newContext;
//  }
}
