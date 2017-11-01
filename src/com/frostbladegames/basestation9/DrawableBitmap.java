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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.frostbladegames.basestation9.GameObjectGroups.Type;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

/** 
 * Draws a screen-aligned bitmap to the screen.
 */
public class DrawableBitmap {
//public class DrawableBitmap extends DrawableObject {
	// START: From original DrawableObject code
    private float mPriority;
    private ObjectPool mParentPool;
    // END: From original DrawableObject code
	
    // FIXME DELETED 11/28/12
//    // FIXME DELETED 11/11/12
////    private Texture mTexture;
////    private int mWidth;
////    private int mHeight;
    private int mCrop[];
    
    private int mViewWidth;
    private int mViewHeight;
    private float mOpacity;
    
    private int mImageWidth;
    private int mImageHeight;
    
//    private float mViewScaleX;
//    private float mViewScaleY;
    
    // FIXME ADDED 11/11/12
    private int[] mTexturePointer;
    // FIXME END 11/11/12
    
    // OLD glDrawArrays() Test Code
//    // FIXME Added 10/15/12
//    private FloatBuffer mVertexBuffer;
//    private FloatBuffer mTextureBuffer;
//    
//    private float mVertices[] = {
//        	-1.0f, -1.0f, 0.0f,	// bottom left
//        	-1.0f,  1.0f, 0.0f,	// top left
//        	 1.0f, -1.0f, 0.0f,	// bottom right
//        	 1.0f,  1.0f, 0.0f	// top right
//        };
////    private float mVertices[] = {
////    	-1.0f, -1.0f, 0.0f,	// bottom left
////    	 1.0f, -1.0f, 0.0f,	// bottom right
////    	-1.0f,  1.0f, 0.0f,	// top left
////    	 1.0f,  1.0f, 0.0f	// top right
////    };
//    
//    private float mTextureCoord[] = {
//    		// Mapping coordinates for the vertices
//    		0.0f, 1.0f,		// top left (V2)
//    		0.0f, 0.0f,		// bottom left (V1)
//    		1.0f, 1.0f, 	// top right (V4)
//    		1.0f, 0.0f		// bottom right (V3)
//    };
//    
//    private int[] mTexturePointer = new int[1];
    
//    // FIXME TEMP TEST. DELETE.
//    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private long mDrawTime;
//    private int mDrawCount;
    
    // FIXME RE-ADDED 11/11/12
    DrawableBitmap() {
        super();
        
        // FIXME RE-ENABLE 11/28/12
//        Log.i("HudTest", "DrawableBitmap() <constructor>");

        // FIXME width, height always 0,0 upon load, so change constructor to DrawableBitmap().
//        mTexture = texture;
//        mWidth = width;
//        mHeight = height;
        
//        mCrop = new int[4];
//        mViewWidth = 0;
//        mViewHeight = 0;
        mOpacity = 1.0f;
//        setCrop(0, height, width, height);
        
        // OLD glDrawArrays() Test Code
//		ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
//		vbb.order(ByteOrder.nativeOrder());
//		mVertexBuffer = vbb.asFloatBuffer();
//		mVertexBuffer.put(mVertices);
//		mVertexBuffer.position(0);
//		
//		ByteBuffer tbb = ByteBuffer.allocateDirect(mTextureCoord.length * 4);
//		tbb.order(ByteOrder.nativeOrder());
//		mTextureBuffer = tbb.asFloatBuffer();
//		mTextureBuffer.put(mTextureCoord);
//		mTextureBuffer.position(0);
        // FIXME END RE-ENABLE 11/28/12
    }
    
//    DrawableBitmap(int width, int height) {
//        super();
//        
////        Log.i("HudTest", "DrawableBitmap() <constructor>");
//
//        // FIXME width, height always 0,0 upon load, so change constructor to DrawableBitmap().
////        mTexture = texture;
//        mWidth = width;
//        mHeight = height;
//        
////        mCrop = new int[4];
////        mViewWidth = 0;
////        mViewHeight = 0;
//        mOpacity = 1.0f;
////        setCrop(0, height, width, height);
//        
////		ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
////		vbb.order(ByteOrder.nativeOrder());
////		mVertexBuffer = vbb.asFloatBuffer();
////		mVertexBuffer.put(mVertices);
////		mVertexBuffer.position(0);
////		
////		ByteBuffer tbb = ByteBuffer.allocateDirect(mTextureCoord.length * 4);
////		tbb.order(ByteOrder.nativeOrder());
////		mTextureBuffer = tbb.asFloatBuffer();
////		mTextureBuffer.put(mTextureCoord);
////		mTextureBuffer.position(0);
//    }
    // FIXME END 11/11/12
    
    // FIXME DELETE 11/11/12
//    DrawableBitmap(Texture texture, int width, int height) {
//        super();
//
//        mTexture = texture;
//        mWidth = width;
//        mHeight = height;
//        mCrop = new int[4];
//        mViewWidth = 0;
//        mViewHeight = 0;
//        mOpacity = 1.0f;
//        setCrop(0, height, width, height);
//    }
    // FIXME END 11/11/12

    public void reset() {
//        mTexture = null;
        mViewWidth = 0;
        mViewHeight = 0;
        mOpacity = 1.0f;
        
        mImageWidth = 0;
        mImageHeight = 0;
        
//        mViewScaleX = 0.0f;
//        mViewScaleY = 0.0f;
        
    }
      
    public void loadGLTexture(GL10 gl, Context context, int resourceId) {
//    	Log.i("HudTest", "DrawableBitmap loadGLTexture()");
    	
    	mTexturePointer = new int[1];
    	
    	// generate one texture pointer
    	gl.glGenTextures(1, mTexturePointer, 0);
    	
    	// bind it to our array
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
    	
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(resourceId);
		
		// FIXME MODIFIED 11/17/12
		Bitmap bitmapBase = null;
//		Bitmap bitmap = null;
		// FIXME END MODIFIED 11/17/12
		
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmapBase = BitmapFactory.decodeStream(is);
//			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		
//		// FIXME ADDED 11/17/12
//		mViewScaleX = GameParameters.viewScaleX;
//		mViewScaleY = GameParameters.viewScaleY;
		
		Matrix flip = new Matrix();
//		flip.postScale(mViewScaleX, -mViewScaleY);
//		flip.postScale(1.5f, -1.5f);
		flip.postScale(1.0f, -1.0f);
//		Bitmap bitmapFlip = Bitmap.createBitmap(bitmapBase, 0, 0, bitmapBase.getWidth(), bitmapBase.getHeight(), flip, true);
//		Bitmap bitmap = Bitmap.createBitmap(bitmapBase, 0, 0, (int)(bitmapBase.getWidth() * mViewScaleX), (int)(bitmapBase.getHeight() * mViewScaleY), flip, true);
		Bitmap bitmap = Bitmap.createBitmap(bitmapBase, 0, 0, bitmapBase.getWidth(), bitmapBase.getHeight(), flip, true);
		
//		// FIXME TEMP ONLY. DELETE.
//		final int baseWidth = bitmapBase.getWidth();
//		final int baseHeight = bitmapBase.getHeight();
//		
//		//Clean up
//		bitmapBase.recycle();
//		
//		Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFlip, 128, 128, true);
//		Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFlip, 48, 48, true);
//		int flipWidth = (int)(bitmapFlip.getWidth() * 1.5);
//		int flipHeight = (int)(bitmapFlip.getHeight() * 1.5);
//		
//		if (flipWidth > 128) {
//			flipWidth = 128;
//		}
//		
//		if (flipHeight > 128) {
//			flipHeight = 128;
//		}
//		
//		Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFlip, flipWidth, flipHeight, true);
//		Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFlip, (int)(bitmapFlip.getWidth() * 1.5), (int)(bitmapFlip.getHeight() * 1.5), true);
//		Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFlip, 64, 64, true);
//		Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFlip, (int)(bitmapFlip.getWidth() * mViewScaleX), (int)(bitmapFlip.getHeight() * mViewScaleY), true);
//		bitmapBase = bitmap;
//		bitmap = Bitmap.createScaledBitmap(bitmapBase, 150, 150, true);
//		bitmap = Bitmap.createScaledBitmap(bitmapBase, (int)(bitmapBase.getWidth() * mViewScaleX), (int)(bitmapBase.getHeight() * mViewScaleY), true);
		// FIXME END ADDED 11/17/12
		
//		Log.i("TouchCounter", "DrawableBitmap loadGLTexture() baseWidth,Height; flipWidth,Height = " +
//				baseWidth + ", " + baseHeight + "; " + flipWidth + ", " + flipHeight);
		
		mCrop = new int[4];
		mCrop[0] = 0;
//		mCrop[1] = bitmap.getHeight();
		mCrop[1] = 0;
//		mCrop[2] = mImageWidth;
//		mCrop[3] = mImageHeight;
		mCrop[2] = bitmap.getWidth();
		mCrop[3] = bitmap.getHeight();
//		mCrop[0] = 0;
//		mCrop[1] = bitmap.getHeight();
//		mCrop[2] = bitmap.getWidth();
//		mCrop[3] = -bitmap.getHeight();
		
//		mImageWidth = (int)(bitmap.getWidth() * mViewScaleX);
//		mImageHeight = (int)(bitmap.getHeight() * mViewScaleY);
		mImageWidth = bitmap.getWidth();
		mImageHeight = bitmap.getHeight();
//		mWidth = bitmap.getWidth();
//		mHeight = bitmap.getHeight();
		
//    	// loading texture
//    	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId);
    	
		// FIXME MODIFIED 11/17/12
    	// create nearest filtered texture
//		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
////    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, (float)GL10.GL_NEAREST);
//////    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
//////    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//    	
//    	gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
		// FIXME END MODIFIED 11/17/12
    	
		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		//Clean up
		bitmapBase.recycle();
//		bitmapFlip.recycle();
		bitmap.recycle();
		
    	// FIXME ADDED 11/17/12
		// Set glBindTexture to null
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    	// FIXME END ADDED 11/17/12
		
//		ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
//		vbb.order(ByteOrder.nativeOrder());
//		mVertexBuffer = vbb.asFloatBuffer();
//		mVertexBuffer.put(mVertices);
//		mVertexBuffer.position(0);
//		
//		ByteBuffer tbb = ByteBuffer.allocateDirect(mTextureCoord.length * 4);
//		tbb.order(ByteOrder.nativeOrder());
//		mTextureBuffer = tbb.asFloatBuffer();
//		mTextureBuffer.put(mTextureCoord);
//		mTextureBuffer.position(0);
    }
    // FIXME END 11/11/12
    // FIXME END MODIFIED 11/28/12
    // OLD glDrawArrays Test Code
//    public void loadGLTexture(GL10 gl, Context context, int resourceId) {    	
//		//Get the texture from the Android resource directory
//		InputStream is = context.getResources().openRawResource(resourceId);
//		
//		Bitmap bitmap = null;
//		
//		try {
//			//BitmapFactory is an Android graphics utility for images
//			bitmap = BitmapFactory.decodeStream(is);
//
//		} finally {
//			//Always clear and close
//			try {
//				is.close();
//				is = null;
//			} catch (IOException e) {
//			}
//		}
//		
//    	// generate one texture pointer
//    	gl.glGenTextures(1, mTexturePointer, 0);
//    	
//    	// bind it to our array
//    	gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
//
//		mImageWidth = bitmap.getWidth();
//		mImageHeight = bitmap.getHeight();
//    	
//    	// create nearest filtered texture
//    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
//    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//    	
//		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
//		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//		
//		//Clean up
//		bitmap.recycle();
//    }
    
//    /**
//     * Begins drawing bitmaps. Sets the OpenGL state for rapid drawing.
//     * 
//     * @param gl  A pointer to the OpenGL context.
//     * @param viewWidth  The width of the screen.
//     * @param viewHeight  The height of the screen.
//     */
//    public static void beginDrawing(GL10 gl, int gameWidth, int gameHeight) {
////    public static void beginDrawing(GL10 gl, float viewWidth, float viewHeight) {
//        
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        GL10 gl = OpenGLSystem.getGL();
////        float viewWidth = 480.0f;
////        float viewHeight = 320.0f;
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//    	
//        // Blend makes texture background transparent (otherwise black)
//        gl.glEnable(GL10.GL_BLEND);
//        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
//        
//        // TODO ColorBuffer Test code. Why using Hex instead of Float? Required?
////        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
//        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
//        
//        gl.glEnable(GL10.GL_TEXTURE_2D);
//        
//        gl.glShadeModel(GL10.GL_FLAT);
//        
//        /* FIXME GL_DITHER - Test glEnable vs glDisable. 
//         * Value same for both DrawableDroid and DrawableBitmap, so move to GameRenderer.onSurfaceCreated()? */
////        gl.glEnable(GL10.GL_DITHER);
//
//        // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
////        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
//        
//        /* TODO Are 2x PushMatrix() and 2x PopMatrix() required?
//         * If use OpenGL Circles for DrawableBitmap, glMatrixMode Projection and ModelView duplication required? */
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glPushMatrix();
//        gl.glLoadIdentity();
//        
//        // gluPerspective() for DrawableDroid, glOrthof() for DrawableBitmap
//        gl.glOrthof(0.0f, (float)gameWidth, 0.0f, (float)gameHeight, 0.0f, 1.0f);
////        gl.glOrthof(0.0f, gameWidth, 0.0f, gameHeight, 0.0f, 1.0f);
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glPushMatrix();
//        gl.glLoadIdentity();
//    }

    /**
     * Draw the bitmap at a given x,y position, expressed in pixels, with the
     * lower-left-hand-corner of the view being (0,0).
     * 
     * @param gl  A pointer to the OpenGL context
     * @param x  The number of pixels to offset this drawable's origin in the x-axis.
     * @param y  The number of pixels to offset this drawable's origin in the y-axis
     * @param scaleX The horizontal scale factor between the bitmap resolution and the display resolution.
     * @param scaleY The vertical scale factor between the bitmap resolution and the display resolution.
     */
//    @Override
    public void draw(GL10 gl, float x, float y, float scaleX, float scaleY) {
    	// FIXME MODIFIED 11/28/12
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
    	((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCrop, 0);
    	
    	// FIXME TEMP CODE. Re-add Scale calculation. Move code to GameRenderer to optimize.
    	final int intX = (int)(x);
    	final int intY = (int)(y);
    	final int intWidth = mImageWidth;
    	final int intHeight = mImageHeight;
    	
    	((GL11Ext)gl).glDrawTexiOES(intX, intY, 0, intWidth, intHeight);
    	
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    	
    	// OLD glDrawArrays() Test Code
//    	// Bind the previously generated texture
//    	gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
//    	
////    	// Enable arrays
////    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
////    	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//    	
//    	// FIXME Can glFrontFace() be moved to GameRenderer beginDrawingHud()?
//    	// Set face rotation
//    	gl.glFrontFace(GL10.GL_CW);
//    	
//    	// Point to buffers
//    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
//    	gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
//    	
////      if (opacity < 1.0f) {
////      gl.glColor4f(opacity, opacity, opacity, opacity);
////  	}
//    	
//    	gl.glTranslatef(x, y, 0.0f);
//    	
//    	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVertices.length / 3);
//    	
////      if (opacity < 1.0f) {
////      gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
////  	}
//    	
////    	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
//    	
////    	// Disable arrays
////    	gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
////    	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    	// FIXME END MODIFIED 11/28/12
    	
    	// FIXME DELETE 11/11/12
//        final Texture texture = mTexture;
//        
//        if (gl != null && texture != null) {
//            assert texture.loaded;
//            
//            final float snappedX = (int) x;
//            final float snappedY = (int) y;
//                 
//            final float opacity = mOpacity;
//            final float width = mWidth;
//            final float height = mHeight;
//            final float viewWidth = mViewWidth;
//            final float viewHeight = mViewHeight;
//            
//            boolean cull = false;
//            if (viewWidth > 0) {
//                if (snappedX + width < 0.0f 
//                		|| snappedX > viewWidth 
//                        || snappedY + height < 0.0f
//                        || snappedY > viewHeight 
//                        || opacity == 0.0f
//                        || !texture.loaded) {
//                    cull = true;
//                }
//            }
//            
//            if (!cull) {
//                OpenGLSystem.bindTexture(GL10.GL_TEXTURE_2D, texture.name);
//
//                // FIXME 10/29/12 Temp Delete. Originally deleted successfully 10/6/12 (though no HUD Render). Necessary?
//                // This is necessary because we could be drawing the same texture with different
//                // crop (say, flipped horizontally) on the same frame.
//                OpenGLSystem.setTextureCrop(mCrop);
//               
//                if (opacity < 1.0f) {
//                    gl.glColor4f(opacity, opacity, opacity, opacity);
//                }
//                
//                ((GL11Ext) gl).glDrawTexfOES(snappedX * scaleX, snappedY * scaleY, 
//                		getPriority(), width * scaleX, height * scaleY);
//                
//                if (opacity < 1.0f) {
//                    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
//                }
//            }
//        }
        // FIXME END 11/11/12

        // FIXME TEST. RE-ENABLE.
//    	// TODO Temp code - not efficient
//    	endDrawing(gl);
    	
//    	mDrawCount++;
//    	final long currentTime = SystemClock.uptimeMillis();
//    	if (currentTime > (mDrawTime + PROFILE_REPORT_DELAY)) {
//    		Log.i("Renderer", "DrawableBitmap draw() mDrawCount = " + mDrawCount);
//    		mDrawTime = currentTime;
//    	}
    }

//    /**
//     * Ends the drawing and restores the OpenGL state.
//     * 
//     * @param gl  A pointer to the OpenGL context.
//     */
//    public static void endDrawing(GL10 gl) {
////    public static void endDrawing(GL10 gl) {
//        
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////        GL10 gl = OpenGLSystem.getGL();
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//        
//        gl.glDisable(GL10.GL_TEXTURE_2D);
//    	
//        // Blend makes texture background transparent (otherwise black)
//        gl.glDisable(GL10.GL_BLEND);
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glPopMatrix();
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glPopMatrix();
//    }

    // FIXME DELETED 11/11/12
//    public void resize(int width, int height) {
//        mImageWidth = width;
//        mImageHeight = height;
////        mWidth = width;
////        mHeight = height;
//        setCrop(0, height, width, height);
//    }

    public int getWidth() {
    	return mImageWidth;
//        return mWidth;
    }

    public void setWidth(int width) {
    	mImageWidth = width;
//        mWidth = width;
    }

    public int getHeight() {
    	return mImageHeight;
//        return mHeight;
    }

    public void setHeight(int height) {
    	mImageHeight = height;
//        mHeight = height;
    }
    
    public void setViewSize(int width, int height) {
        mViewHeight = height;
        mViewWidth = width;
    }
    
    public void setOpacity(float opacity) {
        mOpacity = opacity;
    }

    // FIXME DELETED 11/11/12
//    /**
//     * Changes the crop parameters of this bitmap.  Note that the underlying OpenGL texture's
//     * parameters are not changed immediately The crop is updated on the
//     * next call to draw().  Note that the image may be flipped by providing a negative width or
//     * height.
//     * 
//     * @param left
//     * @param bottom
//     * @param width
//     * @param height
//     */
//    public void setCrop(int left, int bottom, int width, int height) {
//        // Negative width and height values will flip the image.
//        mCrop[0] = left;
//        mCrop[1] = bottom;
//        mCrop[2] = width;
//        mCrop[3] = -height;
//    }

    // FIXME DELETED 11/28/12
//    public int[] getCrop() {
//        return mCrop;
//    }
    // FIXME END DELETED 11/28/12

    // FIXME DELETED 11/11/12
//    public void setTexture(Texture texture) {
//        mTexture = texture;
//    }
//
////    @Override
//    public Texture getTexture() {
//        return mTexture;
//    }

    // FIXME DELETED 11/11/12
////   @Override
//   public boolean visibleAtPosition(Vector3 position) {
//       boolean cull = false;
//       if (mViewWidth > 0) {
//           if (position.x + mImageWidth < 0 || position.x > mViewWidth 
//                   || position.y + mImageHeight < 0 || position.y > mViewHeight) {
//               cull = true;
//           }
////           if (position.x + mWidth < 0 || position.x > mViewWidth 
////                   || position.y + mHeight < 0 || position.y > mViewHeight) {
////               cull = true;
////           }
//       }
//       return !cull;
//   }
   
    // FIXME DELETED 11/11/12
//   protected final void setFlip(boolean horzFlip, boolean vertFlip) {
//       setCrop(horzFlip ? mImageWidth : 0, 
//               vertFlip ? 0 : mImageHeight, 
//               horzFlip ? -mImageWidth : mImageWidth,
//               vertFlip ? -mImageHeight : mImageHeight);
////       setCrop(horzFlip ? mWidth : 0, 
////               vertFlip ? 0 : mHeight, 
////               horzFlip ? -mWidth : mWidth,
////               vertFlip ? -mHeight : mHeight);
//   }
   
   // START: From original DrawableObject code
   public void setPriority(float f) {
       mPriority = f;
   }

   public float getPriority() {
       return mPriority;
   }

   public void setParentPool(ObjectPool pool) {
       mParentPool = pool;
   }

   public ObjectPool getParentPool() {
       return mParentPool;
   }
   
//   // Override to allow drawables to be sorted by texture.
//   public Texture getTexture() {
//       return null;
//   }
//   
//   // Function to allow drawables to specify culling rules.
//   public boolean visibleAtPosition(Vector3 position) {
//       return true;
//   }
   // END: From original DrawableObject code
}
