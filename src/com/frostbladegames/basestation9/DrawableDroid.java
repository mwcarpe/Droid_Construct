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

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
//import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.frostbladegames.basestation9.GameObjectGroups.Type;

import android.content.Context;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

/** 
 * Draws an OpenGL 3D object to the screen.
 */
public class DrawableDroid extends DrawableObject {
	private static final int VERTEX_SIZE = 40;  // 10 values (= 3 verts + 3 norm + 4 col) * 4 float size
	private static final int NORMAL_OFFSET = 12;  // 3 vertex points * 4 float size
	private static final int COLOR_OFFSET = 24;  // NORMAL_OFFSET + (3 normal points * 4 float size)
	
	private boolean mVBOSupport = false;
	
	private int mVBOFileId;
	
//    private Texture mTexture;
	// FIXME Confirm mWidth, mHeight are for Object, not screen, then change to Float incl mDepth
    private int mWidth;
    private int mHeight;
//    private int mCrop[];
    private int mViewWidth;
    private int mViewHeight;
    private float mOpacity;
    
//    private float mCubeRot = 0.0f;
//    private float mXTrans = 0.0f;
    
//    // Lighting Value 0 = Low Lighting, 1 = Very Low Lighting, 2 = Zero Lighting
//    private int mLightingValue;
//    private float mPreviousTime;
    
    private boolean mLaserGroup;
    
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private FloatBuffer mColorBuffer;
	private ShortBuffer mIndexBuffer;
	
	// Main Game Lighting
	private FloatBuffer mLight0AmbientBuffer;
	private FloatBuffer mLight0DiffuseBuffer;
	private FloatBuffer mLight0SpecularBuffer;
	private FloatBuffer mLight0PositionBuffer;
	// FrostBlade Splashscreen Logo Spotlight
	private FloatBuffer mLight1AmbientBuffer;
	private FloatBuffer mLight1DiffuseBuffer;
	private FloatBuffer mLight1SpecularBuffer;
	private FloatBuffer mLight1PositionBuffer;
	private FloatBuffer mLight1SpotDirection;
	private FloatBuffer mLight1SpotCutoff;
	private boolean mLight1Enabled;
	// Game Intro Droid Power-up Spotlight
	private FloatBuffer mLight2AmbientBuffer;
	private FloatBuffer mLight2DiffuseBuffer;
	private FloatBuffer mLight2SpecularBuffer;
	private FloatBuffer mLight2PositionBuffer;
	private FloatBuffer mLight2SpotDirection;
	private FloatBuffer mLight2SpotCutoff;
	private boolean mLight2Enabled;
	
	private FloatBuffer mMaterialAmbientBuffer;
	private FloatBuffer mMaterialDiffuseBuffer;
	private FloatBuffer mMaterialSpecularBuffer;
	private FloatBuffer mMaterialShininessBuffer;
	private FloatBuffer mMaterialEmissionBuffer;
	
	private static FloatBuffer mLightZeroBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
	private static FloatBuffer mLightVeryLowBuffer = FloatBuffer.wrap(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
	private static FloatBuffer mLightLowBuffer = FloatBuffer.wrap(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
	private static FloatBuffer mLightMidBuffer = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
	private static FloatBuffer mLightHighBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
	private static FloatBuffer mLightMaxBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//	private static FloatBuffer mLightAmbientBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
//	private static FloatBuffer mLightDiffuseBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//	private static FloatBuffer mLightSpecularBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
	private static FloatBuffer mLight0PositionBufferStatic = FloatBuffer.wrap(new float[]{0.0f, 5.0f, 5.0f, 0.0f});
//	private static FloatBuffer mLight1PositionBuffer = FloatBuffer.wrap(new float[]{-3.0f, 1.5f, 1.0f, 1.0f});
	private static FloatBuffer mLight1SpotDirectionStatic = FloatBuffer.wrap(new float[]{0.0f, 0.0f, -1.0f});
//	private static FloatBuffer mLight1SpotDirectionStatic = FloatBuffer.wrap(new float[]{54.625f, -2.5f, -54.625f});
	private static FloatBuffer mLight1SpotCutoffStatic = FloatBuffer.wrap(new float[]{4.0f});
//	private static FloatBuffer mLightSpotCutoff = FloatBuffer.wrap(new float[]{5.0f});
//	private static FloatBuffer mLightSpotCutoff = FloatBuffer.wrap(new float[]{10.0f});
//	private static FloatBuffer mLightPositionBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.5f, 5.0f, 0.0f});
//	private static FloatBuffer mLightPositionBuffer = FloatBuffer.wrap(new float[]{-5.0f, 10.0f, 5.0f, 315.0f});
//	private static FloatBuffer mLightPositionBuffer = FloatBuffer.wrap(new float[]{50.0f, 5.0f, -50.0f, 0.0f});
////	private static FloatBuffer mLightModelAmbientBuffer = FloatBuffer.wrap(new float[]{0.4f, 0.4f, 0.4f, 1.0f});
//	private float mLightPositionX;
	private static FloatBuffer mLight2PositionBufferStatic = FloatBuffer.wrap(new float[]{54.625f, -7.5f, -54.625f, 1.0f});
	private static FloatBuffer mLight2SpotDirectionStatic = FloatBuffer.wrap(new float[]{1.0f, 0.5f, -1.0f});
//	private static FloatBuffer mLight2SpotDirectionStatic = FloatBuffer.wrap(new float[]{55.625f, -2.5f, -55.625f});
	private static FloatBuffer mLight2SpotCutoffStatic = FloatBuffer.wrap(new float[]{20.0f});
	
	private static FloatBuffer mMaterialZeroBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
	private static FloatBuffer mMaterialVeryLowBuffer = FloatBuffer.wrap(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
	private static FloatBuffer mMaterialLowBuffer = FloatBuffer.wrap(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
	private static FloatBuffer mMaterialMidBuffer = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
	private static FloatBuffer mMaterialHighBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
	private static FloatBuffer mMaterialMaxBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//	private static FloatBuffer mMaterialAmbientBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
//	private static FloatBuffer mMaterialDiffuseBuffer = FloatBuffer.wrap(new float[]{0.1f, 0.5f, 0.8f, 1.0f});
//	private static FloatBuffer mMaterialSpecularBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
	private static FloatBuffer mMaterialShininessZeroBuffer = FloatBuffer.wrap(new float[]{0.0f});
	private static FloatBuffer mMaterialShininessLowBuffer = FloatBuffer.wrap(new float[]{5.0f});
	private static FloatBuffer mMaterialShininessMidBuffer = FloatBuffer.wrap(new float[]{50.0f});
	private static FloatBuffer mMaterialShininessHighBuffer = FloatBuffer.wrap(new float[]{100.0f});
	private static FloatBuffer mMaterialShininessMaxBuffer = FloatBuffer.wrap(new float[]{128.0f});
	private static FloatBuffer mMaterialEmissionBufferStatic = FloatBuffer.wrap(new float[]{0.3f, 0.2f, 0.2f, 1.0f});
//	private float[] mMaterialEmission = new float[] {0.3f, 0.2f, 0.2f, 0.0f};
	
	private int mVertexId;
	private int mIndexId;
//	private int mNormalId;
//	private int mColorId;
	
	private int mIndicesLength;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
//    // FIXME TEMP TEST. DELETE.
//    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private long mDrawTime;
//    private int mDrawCount;
	
	DrawableDroid() {
		super();
//      mTexture = texture;
		// FIXME Temp mWidth, mHeight
        mWidth = 64;
        mHeight = 64;
//        mCrop = new int[4];
        mViewWidth = 0;
        mViewHeight = 0;
        mOpacity = 1.0f;
//        setCrop(0, height, width, height);
        
//        mLightingValue = 0;
//        mPreviousTime = 0.0f;
        
        mLaserGroup = false;
        
    	mVertexBuffer = null;
    	mNormalBuffer = null;
    	mColorBuffer = null;
    	mIndexBuffer = null;
    	
    	mLight0AmbientBuffer = null;
    	mLight0DiffuseBuffer = null;
    	mLight0SpecularBuffer = null;
    	mLight0PositionBuffer = null;
    	mLight1AmbientBuffer = null;
    	mLight1DiffuseBuffer = null;
    	mLight1SpecularBuffer = null;
    	mLight1PositionBuffer = null;
    	mLight1SpotDirection = null;
    	mLight1SpotCutoff = null;
    	mLight1Enabled = false;
    	mLight2AmbientBuffer = null;
    	mLight2DiffuseBuffer = null;
    	mLight2SpecularBuffer = null;
    	mLight2PositionBuffer = null;
    	mLight2SpotDirection = null;
    	mLight2SpotCutoff = null;
    	mLight2Enabled = false;
    	
    	mMaterialAmbientBuffer = null;
    	mMaterialDiffuseBuffer = null;
    	mMaterialSpecularBuffer = null;
    	mMaterialShininessBuffer = null;
    	mMaterialEmissionBuffer = null;
        
//        mLightPositionX = 0.0f;
        
//		ByteBuffer byteBuf = ByteBuffer.allocateDirect(mMaterialEmission.length * 4);
//		byteBuf.order(ByteOrder.nativeOrder());
//		mMatEmissionBuffer = byteBuf.asFloatBuffer();
//		mMatEmissionBuffer.put(mMaterialEmission);
//		mMatEmissionBuffer.position(0);
	}
    
    DrawableDroid(int width, int height) {
        super();
//        mTexture = texture;
        mWidth = width;
        mHeight = height;
//        mCrop = new int[4];
        mViewWidth = 0;
        mViewHeight = 0;
        mOpacity = 1.0f;
//        setCrop(0, height, width, height);
        
//        mLightingValue = 0;
//        mPreviousTime = 0.0f;
        
        mLaserGroup = false;
        
    	mVertexBuffer = null;
    	mNormalBuffer = null;
    	mColorBuffer = null;
    	mIndexBuffer = null;
    	
    	mLight0AmbientBuffer = null;
    	mLight0DiffuseBuffer = null;
    	mLight0SpecularBuffer = null;
    	mLight0PositionBuffer = null;
    	mLight1AmbientBuffer = null;
    	mLight1DiffuseBuffer = null;
    	mLight1SpecularBuffer = null;
    	mLight1PositionBuffer = null;
    	mLight1SpotDirection = null;
    	mLight1SpotCutoff = null;
    	mLight1Enabled = false;
    	mLight2AmbientBuffer = null;
    	mLight2DiffuseBuffer = null;
    	mLight2SpecularBuffer = null;
    	mLight2PositionBuffer = null;
    	mLight2SpotDirection = null;
    	mLight2SpotCutoff = null;
    	mLight2Enabled = false;
    	
    	mMaterialAmbientBuffer = null;
    	mMaterialDiffuseBuffer = null;
    	mMaterialSpecularBuffer = null;
    	mMaterialShininessBuffer = null;
    	mMaterialEmissionBuffer = null;
        
//        mLightPositionX = 0.0f;
        
//		ByteBuffer byteBuf = ByteBuffer.allocateDirect(mMaterialEmission.length * 4);
//		byteBuf.order(ByteOrder.nativeOrder());
//		mMatEmissionBuffer = byteBuf.asFloatBuffer();
//		mMatEmissionBuffer.put(mMaterialEmission);
//		mMatEmissionBuffer.position(0);
    }

    public void reset() {
//        mTexture = null;
        mViewWidth = 0;
        mViewHeight = 0;
        mOpacity = 1.0f;
        
//        mLightingValue = 0;
//        mPreviousTime = 0.0f;
        
        mLaserGroup = false;
        
    	mVertexBuffer = null;
    	mNormalBuffer = null;
    	mColorBuffer = null;
    	mIndexBuffer = null;
    	
    	mLight0AmbientBuffer = null;
    	mLight0DiffuseBuffer = null;
    	mLight0SpecularBuffer = null;
    	mLight0PositionBuffer = null;
    	mLight1AmbientBuffer = null;
    	mLight1DiffuseBuffer = null;
    	mLight1SpecularBuffer = null;
    	mLight1PositionBuffer = null;
    	mLight1SpotDirection = null;
    	mLight1SpotCutoff = null;
    	mLight1Enabled = false;
    	mLight2AmbientBuffer = null;
    	mLight2DiffuseBuffer = null;
    	mLight2SpecularBuffer = null;
    	mLight2PositionBuffer = null;
    	mLight2SpotDirection = null;
    	mLight2SpotCutoff = null;
    	mLight2Enabled = false;
    	
    	mMaterialAmbientBuffer = null;
    	mMaterialDiffuseBuffer = null;
    	mMaterialSpecularBuffer = null;
    	mMaterialShininessBuffer = null;
    	mMaterialEmissionBuffer = null;
    }
    
	public void loadObject(int fileId, Context context) {
		long timeStart = 0;
		
		mVBOSupport = false;
		
		InputStream is = null;
		DataInputStream dis = null;
		
		try {			
			is = context.getResources().openRawResource(fileId);
			
			dis = new DataInputStream(is);
			
			int numTriangles = dis.readInt();
			
			mIndicesLength = numTriangles * 3;
			
			int indCapacity = numTriangles * 3 * 2;
			int vertCapacity = numTriangles * 3 * 3 * 4;
			int normCapacity = numTriangles * 3 * 3 * 4;
			int colCapacity = numTriangles * 3 * 4 * 4;
			
			byte byteIndices[] = new byte[indCapacity];
			byte byteVertices[] = new byte[vertCapacity];
			byte byteNormals[] = new byte[normCapacity];
			byte byteColors[] = new byte[colCapacity];			
			
			dis.read(byteIndices);
			short[] indArray = new short[indCapacity/2];
			ShortBuffer indShortBuf = ByteBuffer.wrap(byteIndices).asShortBuffer();
			indShortBuf.get(indArray);
			ByteBuffer ibb = ByteBuffer.allocateDirect(indCapacity);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();
			mIndexBuffer.put(indArray);
			mIndexBuffer.position(0);
			
			dis.read(byteVertices);
			float[] vertArray = new float[vertCapacity/4];
			FloatBuffer vertFloatBuf = ByteBuffer.wrap(byteVertices).asFloatBuffer();
			vertFloatBuf.get(vertArray);
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertCapacity);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			mVertexBuffer.put(vertArray);
			mVertexBuffer.position(0);
			
			dis.read(byteNormals);
			float[] normArray = new float[normCapacity/4];
			FloatBuffer normFloatBuf = ByteBuffer.wrap(byteNormals).asFloatBuffer();
			normFloatBuf.get(normArray);
			ByteBuffer nbb = ByteBuffer.allocateDirect(normCapacity);
			nbb.order(ByteOrder.nativeOrder());
			mNormalBuffer = nbb.asFloatBuffer();
			mNormalBuffer.put(normArray);
			mNormalBuffer.position(0);
			
			dis.read(byteColors);
			float[] colArray = new float[colCapacity/4];
			FloatBuffer colFloatBuf = ByteBuffer.wrap(byteColors).asFloatBuffer();
			colFloatBuf.get(colArray);
			ByteBuffer cbb = ByteBuffer.allocateDirect(colCapacity);
			cbb.order(ByteOrder.nativeOrder());
			mColorBuffer = cbb.asFloatBuffer();
			mColorBuffer.put(colArray);
			mColorBuffer.position(0);
			
		} catch (FileNotFoundException fnfe) {
			Log.e("DroidObject", "loadObject FileNotFoundException error = " + fnfe.getMessage());
		} catch (IOException ioe) {
			Log.e("DroidObject", "loadObject IOException = " + ioe.getMessage());
		} catch (Exception e) {
			Log.e("DroidObject", "loadObject error = " + e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if (dis != null) {
					dis.close();
					dis = null;
				}
			} catch (Exception e) {
				Log.e("DroidObject", "loadObject error" + e.getMessage());
			}
		}
	}
	
	public void loadObjectVBO(GL11 gl11, int fileId, Context context) {		
		long timeStart = 0;
		
		mVBOFileId = fileId;
		
		mVBOSupport = true;
				
		InputStream is = null;
		DataInputStream dis = null;
		
		try {
			is = context.getResources().openRawResource(fileId);
			
			dis = new DataInputStream(is);
			
			int numTriangles = dis.readInt();
			
			mIndicesLength = numTriangles * 3;
			
			int indCapacity = numTriangles * 3 * 2;
			int vertCapacity = numTriangles * ((3 * 3 * 4) + (3 * 3 * 4) + (3 * 4 * 4));
			
			byte byteIndices[] = new byte[indCapacity];
			byte byteVertices[] = new byte[vertCapacity];	
			
			// FIXME Change mIndexBuffer and mVertexBuffer to local indexBuffer and vertexBuffer, since use VBO IDs 
			dis.read(byteIndices);
			short[] indArray = new short[indCapacity/2];
			ShortBuffer indShortBuf = ByteBuffer.wrap(byteIndices).asShortBuffer();
			indShortBuf.get(indArray);
			ByteBuffer ibb = ByteBuffer.allocateDirect(indCapacity);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();
			mIndexBuffer.put(indArray);
			mIndexBuffer.position(0);		
			
			dis.read(byteVertices);
			float[] vertArray = new float[vertCapacity/4];
			FloatBuffer vertFloatBuf = ByteBuffer.wrap(byteVertices).asFloatBuffer();
			vertFloatBuf.get(vertArray);
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertCapacity);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			mVertexBuffer.put(vertArray);
			mVertexBuffer.position(0);
			
			// Load Vertex VBO
	      	int [] vboId = new int[1];
	      	gl11.glGenBuffers(1, vboId, 0);
			mVertexId = vboId[0];
			
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GL11.GL_STATIC_DRAW);
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			
			// Null the original Vertex Buffer
			mVertexBuffer = null;
			
			// Load Index VBO
	      	int [] indId = new int[1];
	      	gl11.glGenBuffers(1, indId, 0);
			mIndexId = indId[0];
			
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
			gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndicesLength * 2, mIndexBuffer, GL11.GL_STATIC_DRAW);
			
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			// Null the original Index Buffer
			mIndexBuffer = null;
			
		} catch (FileNotFoundException fnfe) {
			Log.e("DroidObject", "loadObject FileNotFoundException error = " + fnfe.getMessage());
		} catch (IOException ioe) {
			Log.e("DroidObject", "loadObject IOException = " + ioe.getMessage());
		} catch (Exception e) {
			Log.e("DroidObject", "loadObject error = " + e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if (dis != null) {
					dis.close();
					dis = null;
				}
			} catch (Exception e) {
				Log.e("DroidObject", "loadObject error" + e.getMessage());
			}
		}
	}
	
	// FIXME Find correct solution to reload, then re-enable
	public void reloadObjectVBO(GL11 gl11, Context context) {	
		long timeStart = 0;
		
		mVBOSupport = true;
		
//		Context context = ConstructActivity.getAppContext();
				
		InputStream is = null;
		DataInputStream dis = null;
		
		try {
			is = context.getResources().openRawResource(mVBOFileId);
//			is = context.getResources().openRawResource(R.raw.droid_top_vbo);
////			is = context.getResources().openRawResource(fileId);
			// FIXME END 12/5/12 TEST ONLY.
			
			dis = new DataInputStream(is);
			
			int numTriangles = dis.readInt();
			
			mIndicesLength = numTriangles * 3;
			
			int indCapacity = numTriangles * 3 * 2;
			int vertCapacity = numTriangles * ((3 * 3 * 4) + (3 * 3 * 4) + (3 * 4 * 4));
			
			byte byteIndices[] = new byte[indCapacity];
			byte byteVertices[] = new byte[vertCapacity];	
			
			// FIXME Change mIndexBuffer and mVertexBuffer to local indexBuffer and vertexBuffer, since use VBO IDs 
			dis.read(byteIndices);
			short[] indArray = new short[indCapacity/2];
			ShortBuffer indShortBuf = ByteBuffer.wrap(byteIndices).asShortBuffer();
			indShortBuf.get(indArray);
			ByteBuffer ibb = ByteBuffer.allocateDirect(indCapacity);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();
			mIndexBuffer.put(indArray);
			mIndexBuffer.position(0);		
			
			dis.read(byteVertices);
			float[] vertArray = new float[vertCapacity/4];
			FloatBuffer vertFloatBuf = ByteBuffer.wrap(byteVertices).asFloatBuffer();
			vertFloatBuf.get(vertArray);
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertCapacity);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			mVertexBuffer.put(vertArray);
			mVertexBuffer.position(0);
			
			// Load Vertex VBO
	      	int [] vboId = new int[1];
	      	gl11.glGenBuffers(1, vboId, 0);
			mVertexId = vboId[0];
			
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GL11.GL_STATIC_DRAW);
			
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
			
			// Null the original Vertex Buffer
			mVertexBuffer = null;
			
			// Load Index VBO
	      	int [] indId = new int[1];
	      	gl11.glGenBuffers(1, indId, 0);
			mIndexId = indId[0];
			
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
			gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndicesLength * 2, mIndexBuffer, GL11.GL_STATIC_DRAW);
			
			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			// Null the original Index Buffer
			mIndexBuffer = null;
			
		} catch (FileNotFoundException fnfe) {
			Log.e("DroidObject", "loadObject FileNotFoundException error = " + fnfe.getMessage());
		} catch (IOException ioe) {
			Log.e("DroidObject", "loadObject IOException = " + ioe.getMessage());
		} catch (Exception e) {
			Log.e("DroidObject", "loadObject error = " + e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if (dis != null) {
					dis.close();
					dis = null;
				}
			} catch (Exception e) {
				Log.e("DroidObject", "loadObject error" + e.getMessage());
			}
		}
	}
	
	public void loadLight0Buffers(FloatBuffer ambient, FloatBuffer diffuse, FloatBuffer specular,
			FloatBuffer light0Position) {
		mLight0AmbientBuffer = ambient;
		mLight0DiffuseBuffer = diffuse;
		mLight0SpecularBuffer = specular;
		
		mLight0PositionBuffer = light0Position;
	}
	
	public void loadLight1Buffers(FloatBuffer ambient, FloatBuffer diffuse, FloatBuffer specular,
			FloatBuffer light1Position, FloatBuffer spotDirection, FloatBuffer spotCutoff) {
		mLight1AmbientBuffer = ambient;
		mLight1DiffuseBuffer = diffuse;
		mLight1SpecularBuffer = specular;
		
		mLight1PositionBuffer = light1Position;
		mLight1SpotDirection = spotDirection;
		mLight1SpotCutoff = spotCutoff;
		
    	mLight1Enabled = true;
	}
	
	public void loadLight2Buffers(FloatBuffer ambient, FloatBuffer diffuse, FloatBuffer specular,
			FloatBuffer light2Position, FloatBuffer spot2Direction, FloatBuffer spot2Cutoff) {
		mLight2AmbientBuffer = ambient;
		mLight2DiffuseBuffer = diffuse;
		mLight2SpecularBuffer = specular;
		
		mLight2PositionBuffer = light2Position;
		mLight2SpotDirection = spot2Direction;
		mLight2SpotCutoff = spot2Cutoff;
		
    	mLight2Enabled = true;
	}
	
	public void loadMaterialBuffers(FloatBuffer ambient, FloatBuffer diffuse, FloatBuffer specular,
			FloatBuffer shininess, FloatBuffer emission) {
		mMaterialAmbientBuffer = ambient;
		mMaterialDiffuseBuffer = diffuse;
		mMaterialSpecularBuffer = specular;
		mMaterialShininessBuffer = shininess;
		mMaterialEmissionBuffer = emission;
	}
	
    /**
     * Begins GL draw() settings for both SplashScreen DrawableDroid
     * 
     * @param gl  A pointer to the OpenGL context.
     * @param cameraX,Y,Z  Camera viewpoint
     * @param gameWidth,Height  The screen dimensions
     */
    public static void beginDrawingSplashScreen(GL10 gl, float cameraX, float cameraY, float cameraZ, 
    		float viewangleX, float viewangleY, float viewangleZ, int gameWidth, int gameHeight, FloatBuffer light1PositionBuffer) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);

    	gl.glShadeModel(GL10.GL_SMOOTH);
        
        gl.glEnable(GL10.GL_DITHER);

//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLightMidBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLightMidBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBufferStatic);
        
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, mLightHighBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, mLightHighBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, light1PositionBuffer);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION, mLight1SpotDirectionStatic);
        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF, mLight1SpotCutoffStatic);
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION, mLightSpotDirection);
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF, mLightSpotCutoff);
        
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnable(GL10.GL_LIGHT1);
        
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialMidBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialMidBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialHighBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessHighBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialZeroBuffer);
        
        gl.glEnable(GL10.GL_COLOR_MATERIAL);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glMatrixMode(GL10.GL_PROJECTION);

        gl.glLoadIdentity();

        GLU.gluPerspective(gl, 40.0f, (float)gameWidth / (float)gameHeight, 1.0f, 100.0f);
        
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        
        gl.glLoadIdentity();
        
    	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
    			cameraX, cameraY + 1.0f, cameraZ, 0.0f, 1.0f, 0.0f);
		
		gl.glFrontFace(GL10.GL_CCW);
        
		// Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }
    
    public static void beginDrawingIntro(GL10 gl) {    	
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, mLightHighBuffer);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, mLightHighBuffer);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, mLight2PositionBufferStatic);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_DIRECTION, mLight2SpotDirectionStatic);
        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_CUTOFF, mLight2SpotCutoffStatic);

        gl.glEnable(GL10.GL_LIGHT2);
    }
    
    /**
     * Begins GL draw() settings for both DrawableDroid and DrawableBitmap. Settings
     * not required for DrawableBitmap are disabled in DrawableDroid.endDrawing().
     * 
     * @param gl  A pointer to the OpenGL context.
     * @param cameraX,Y,Z  Camera viewpoint
     * @param gameWidth,Height  The screen dimensions
     */
    public static void beginDrawing(GL10 gl, float cameraX, float cameraY, float cameraZ, 
    		float viewangleX, float viewangleY, float viewangleZ, int gameWidth, int gameHeight) {
//    public static void beginDrawing(GL10 gl, float cameraX, float cameraY, float cameraZ, int gameWidth, int gameHeight) {
//    public static void beginDrawing(GL10 gl, float x, float y, float z, float r, int gameWidth, int gameHeight) {
//    public static void beginDrawing(GL10 gl, int gameWidth, int gameHeight, float cubeRot) {
//    public static void beginDrawing(GL10 gl, float viewWidth, float viewHeight) {
    	
//    	Log.i("Renderer", "DrawableDroid beginDrawing() w/ gameWidth, gameHeight");
        
//      GL10 gl = OpenGLSystem.getGL();
    	
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        // TODO Temp static code
//        float viewWidth = 480.0f;
//        float viewHeight = 320.0f;
        
        // TODO Upgrade GL10 calls to GL11 or GL20 calls (confirm supported GL20 Device Hardware)
        
    	// TODO Unnecessary to re-draw background every frame? Optimize.
        
        // XXX glClear() in GameRenderer.onSurfaceCreated() and here. This is first gl called in onDrawFrame().
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
//		gl.glLoadIdentity();
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
        // TODO Is this required?  Already set in onSurfaceCreated()
    	// TODO Study correct Depth settings. Re-enable glClearDepthf() or move to onSurfaceCreated() (ref NeHe).
//		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// TODO Test different glDepthFunc settings. Default is GL_LESS.
//		gl.glDepthFunc(GL10.GL_NEVER);
		gl.glDepthFunc(GL10.GL_LEQUAL);
//		gl.glDepthFunc(GL10.GL_ALWAYS);
		
        // FIXME Is this required?  Already set in onSurfaceCreated()
		// TODO Continue to test both GL_SMOOTH and GL_FLAT for optimal setting.
    	gl.glShadeModel(GL10.GL_SMOOTH);
//    	gl.glShadeModel(GL10.GL_FLAT);
        
//        // FIXME GL_DITHER - Test glEnable vs glDisable. No apparent effect.
        gl.glEnable(GL10.GL_DITHER);
    	
    	// FIXME Test Culling (ref droidnova.com)
//        // enable the differentiation of which side may be visible 
//        gl.glEnable(GL10.GL_CULL_FACE);
//        // which is the front? the one which is drawn counter clockwise
//        gl.glFrontFace(GL10.GL_CCW);
//        // which one should NOT be drawn
//        gl.glCullFace(GL10.GL_BACK);
        
//        // XXX DISABLED. TEST ONLY.
////        gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
        
        // FIXME TEMP REFERENCE ONLY
//    	private static FloatBuffer mLightZeroBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
//    	private static FloatBuffer mLightLowBuffer = FloatBuffer.wrap(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
//    	private static FloatBuffer mLightMidBuffer = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
//    	private static FloatBuffer mLightHighBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
//    	private static FloatBuffer mLightMaxBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//    	private static FloatBuffer mLightAmbientBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
//    	private static FloatBuffer mLightDiffuseBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//    	private static FloatBuffer mLightSpecularBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//    	private static FloatBuffer mLightPositionBuffer = FloatBuffer.wrap(new float[]{0.0f, 10.0f, 0.0f, 0.0f});
//    	
//    	private static FloatBuffer mMaterialZeroBuffer = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
//    	private static FloatBuffer mMaterialLowBuffer = FloatBuffer.wrap(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
//    	private static FloatBuffer mMaterialMidBuffer = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
//    	private static FloatBuffer mMaterialHighBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
//    	private static FloatBuffer mMaterialMaxBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//    	private static FloatBuffer mMaterialAmbientBuffer = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
//    	private static FloatBuffer mMaterialDiffuseBuffer = FloatBuffer.wrap(new float[]{0.1f, 0.5f, 0.8f, 1.0f});
//    	private static FloatBuffer mMaterialSpecularBuffer = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
//    	private static FloatBuffer mMaterialShininessZeroBuffer = FloatBuffer.wrap(new float[]{0.0f});
//    	private static FloatBuffer mMaterialShininessLowBuffer = FloatBuffer.wrap(new float[]{5.0f});
//    	private static FloatBuffer mMaterialShininessMidBuffer = FloatBuffer.wrap(new float[]{50.0f});
//    	private static FloatBuffer mMaterialShininessHighBuffer = FloatBuffer.wrap(new float[]{100.0f});
//    	private static FloatBuffer mMaterialShininessMaxBuffer = FloatBuffer.wrap(new float[]{128.0f});
//    	private static FloatBuffer mMatEmissionBuffer = FloatBuffer.wrap(new float[]{0.3f, 0.2f, 0.2f, 1.0f});
        
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLightMidBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLightMidBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBufferStatic);
        
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, light1PositionBuffer);
////        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, mLight1PositionBuffer);
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION, mLight1SpotDirection);
//        gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF, mLight1SpotCutoff);
//      gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION, mLightSpotDirection);
//      gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF, mLightSpotCutoff);
        
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, mLightHighBuffer);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, mLight2PositionBufferStatic);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_DIRECTION, mLight2SpotDirectionStatic);
//        gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPOT_CUTOFF, mLight2SpotCutoffStatic);
        
//        glTranslated(0.0f, 0.0f, tempLightPositionX);
        
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
//        gl.glEnable(GL10.GL_LIGHT1);
//        gl.glEnable(GL10.GL_LIGHT2);
        
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialMidBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialMidBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialHighBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessLowBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialZeroBuffer);
        
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
////    gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
        
//        // FIXME Test Rescale Normal setting. If doesn't work, try Normalize though supposed to be less efficient.
//        // XXX RE-ENABLED. TEST ONLY.
//        gl.glEnable(GL10.GL_RESCALE_NORMAL);
//////        gl.glEnable(GL10.GL_NORMALIZE);
        
//        // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
////        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

//        // FIXME Disabled Replica original code. Texture appeared upon disable (GL_BLEND washed out Texture?). Study.
        gl.glEnable(GL10.GL_BLEND);
//    	
//        // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? 
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
////        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
////        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);  // Replica original code

//        /* FIXME Is this required?  Already set in onSurfaceChanged()
//         * Same for both DrawableDroid and DrawableBitmap? */
//		gl.glViewport(0, 0, gameWidth, gameHeight);
////		gl.glViewport(0, 0, (int)viewWidth, (int)viewHeight);
    	
        // FIXME Is this required?  Already set in onSurfaceChanged()
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // FIXME add glPushMatrix() and glPopMatrix() for multiple Droid and Enemy Objects
//        gl.glPushMatrix();
        gl.glLoadIdentity();

        // FIXME Is this required?  Already set in onSurfaceChanged()
        // gluPerspective() for DrawableDroid, glOrthof() for DrawableBitmap
        
        /* TODO How much far clipping plane [depth] is required (currently 100.0f)? Do calc for 3D view x,z depth.
         * Also optimize near clipping plan 1.0f vs HUD? */
        GLU.gluPerspective(gl, 40.0f, (float)gameWidth / (float)gameHeight, 1.0f, 100.0f);
        
//		gl.glViewport(0, 0, gameWidth, gameHeight);
        
        // TODO Is this required?  Already set in onSurfaceChanged()
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // TODO Replica original setting - Test Disable/Re-enable OpenGL
//        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        // Adjust cameraX,Y,Z center when using GamePlay isometric angle
        if (viewangleX < -0.1f) {
        	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
        			cameraX - 0.5f, cameraY + 0.5f, cameraZ + 0.5f, 0.0f, 1.0f, 0.0f);
        } else {
        	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
        			cameraX, cameraY + 1.0f, cameraZ, 0.0f, 1.0f, 0.0f);	
        }
		
		gl.glFrontFace(GL10.GL_CCW);
        
		// Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }
    
    public static void beginDrawing(GL10 gl, float cameraX, float cameraY, float cameraZ, 
    		float viewangleX, float viewangleY, float viewangleZ) {
    	
//    	Log.i("Renderer", "DrawableDroid beginDrawing() w/o gameWidth, gameHeight");
    	
    	// Clear screen
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // Adjust cameraX,Y,Z center when using GamePlay isometric angle
        if (viewangleX < -0.1f) {
        	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
        			cameraX - 0.5f, cameraY + 0.5f, cameraZ + 0.5f, 0.0f, 1.0f, 0.0f);
        } else {
        	GLU.gluLookAt(gl, cameraX + viewangleX, cameraY + viewangleY, cameraZ + viewangleZ, 
        			cameraX, cameraY + 1.0f, cameraZ, 0.0f, 1.0f, 0.0f);	
        }
        
		// Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }

    /**
     * Draw the object at a given x,y position, expressed in pixels, with the
     * lower-left-hand-corner of the view being (0,0).
     * 
     * @param gl  A pointer to the OpenGL context
     * @param x  The number of pixels to offset this drawable's origin in the x-axis.
     * @param y  The number of pixels to offset this drawable's origin in the y-axis
     * @param z  The number of pixels to offset this drawable's origin in the z-axis
     * @param r  The rotation angle for this drawable
     * @param scaleX The horizontal scale factor between the bitmap resolution and the display resolution.
     * @param scaleY The vertical scale factor between the bitmap resolution and the display resolution.
     */
    @Override
    public void draw(GL10 gl, float x, float y, float z, float r, float oX, float oY, float oZ,
    		float rZ, float scaleX, float scaleY, int gameWidth, int gameHeight, int objectId) {
//    public void draw(GL10 gl, float x, float y, float z, float r, float oX, float oY, float oZ, Type type,
//    		float rZ, float scaleX, float scaleY, int gameWidth, int gameHeight) {
//        public void draw(float x, float y, float scaleX, float scaleY, int gameWidth, int gameHeight) {
//    public void draw(float x, float y, float scaleX, float scaleY) {   	
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
    	
//        GL10 gl = OpenGLSystem.getGL();
        
//        final Texture texture = mTexture;
        
//		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.name);
    	
    	if (GameParameters.levelRow == 4) {    		
    		int lightingValue = GameParameters.lightingValue;
            
    		if (mLaserGroup) {
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLight0AmbientBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLight0DiffuseBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLight0SpecularBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBuffer);

                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialAmbientBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialDiffuseBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialSpecularBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialEmissionBuffer);
    		} else if (lightingValue == 2) {
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLightZeroBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLightZeroBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLightZeroBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBuffer);

                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialZeroBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialZeroBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialZeroBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialEmissionBuffer);
            } else if (lightingValue == 1) {
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLightVeryLowBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLightVeryLowBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLightVeryLowBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBuffer);

                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialVeryLowBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialVeryLowBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialVeryLowBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialEmissionBuffer);
            } else {
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLightLowBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLightLowBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLightLowBuffer);
            	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBuffer);

                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialLowBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialLowBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialLowBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessBuffer);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialEmissionBuffer);
            }
    	} else {
        	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLight0AmbientBuffer);
        	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLight0DiffuseBuffer);
        	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLight0SpecularBuffer);
        	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBuffer);

            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialAmbientBuffer);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialDiffuseBuffer);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialSpecularBuffer);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessBuffer);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialEmissionBuffer);
    	}
    	
		// Vertex Buffer Objects (VBOs)
		GL11 gl11 = (GL11)gl;
		
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
		gl11.glVertexPointer(3, GL10.GL_FLOAT, VERTEX_SIZE, 0);
//		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mNormalId);
		gl11.glNormalPointer(GL10.GL_FLOAT, VERTEX_SIZE, NORMAL_OFFSET);
//		gl11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
//		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mColorId);
		gl11.glColorPointer(4, GL10.GL_FLOAT, VERTEX_SIZE, COLOR_OFFSET);
//		gl11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
		
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
		
//		gl.glTranslatef(oX, oY, oZ);
		
//		// FIXME TEMP ONLY. DELETE.
////      	int [] vboId = new int[1];
////      	vboId[0] = mVertexId;
////		gl11.glGetBufferParameteriv(GL11.GL_ARRAY_BUFFER, GL11.GL_BUFFER_SIZE, vboId, 0);		
//		final boolean isBuffer = gl11.glIsBuffer(mVertexId);
//		Log.i("GameFlow", "DrawableDroid draw() VBO isBuffer; x,y,z = " + isBuffer + "; " +
//				x + ", " + y + ", " + z);
//		// FIXME END TEMP
		
		gl.glTranslatef(x, y, z);
		gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
		
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//		gl.glPushMatrix();
		
		gl.glTranslatef(oX, oY, oZ);
		
//		gl.glPopMatrix();
		
//		if ((int)rZ > 0) {
//			gl.glPushMatrix();
		
		// EnemyBottom Fly Rotation
		gl.glRotatef(rZ, 0.0f, 0.0f, 1.0f);
//			gl.glRotatef(rZ, 1.0f, 0.0f, 0.0f);
    		
//    		gl.glPopMatrix();
//		}
//		gl.glScalef(0.2f, 0.2f, 0.2f);
		
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		// Draw the vertices as triangles, based on Index Buffer info
		gl11.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength,
				GL10.GL_UNSIGNED_SHORT, 0);
		
		// Set glBindBuffers to null
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		
//		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
//    	if (mVBOSupport) {
//    		// Vertex Buffer Objects (VBOs)
//    		GL11 gl11 = (GL11)gl;
//    		
//    		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
//    		gl11.glVertexPointer(3, GL10.GL_FLOAT, VERTEX_SIZE, 0);
////    		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mNormalId);
//    		gl11.glNormalPointer(GL10.GL_FLOAT, VERTEX_SIZE, NORMAL_OFFSET);
////    		gl11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
////    		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mColorId);
//    		gl11.glColorPointer(4, GL10.GL_FLOAT, VERTEX_SIZE, COLOR_OFFSET);
////    		gl11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
//    		
//    		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
//    		
////    		gl.glTranslatef(oX, oY, oZ);
//    		
//    		gl.glTranslatef(x, y, z);
//    		gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
//    		
//    		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
////    		gl.glPushMatrix();
//    		
//    		gl.glTranslatef(oX, oY, oZ);
//    		
////    		gl.glPopMatrix();
//    		
////    		if ((int)rZ > 0) {
////    			gl.glPushMatrix();
//    		
//    		// EnemyBottom Fly Rotation
//    		gl.glRotatef(rZ, 0.0f, 0.0f, 1.0f);
////    			gl.glRotatef(rZ, 1.0f, 0.0f, 0.0f);
//        		
////        		gl.glPopMatrix();
////    		}
////    		gl.glScalef(0.2f, 0.2f, 0.2f);
//    		
//    		// Draw the vertices as triangles, based on Index Buffer info
//    		gl11.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength,
//    				GL10.GL_UNSIGNED_SHORT, 0);
//    		
//    		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
//    		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
//    	} else {
//    		// Vertex Array Pointers
//    		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
//    		gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
//    		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
////    		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
//    		
////    		gl.glTranslatef(oX, oY, oZ);
//    		
//    		gl.glTranslatef(x, y, z);
////    		gl.glTranslatef(x, 0.0f, z);
//
//    		gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
//    		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
////    		gl.glPushMatrix();
//    		
//    		gl.glTranslatef(oX, oY, oZ);
//    		
////    		if ((int)rZ > 0) {
////			gl.glPushMatrix();
//    		gl.glRotatef(rZ, 0.0f, 0.0f, 1.0f);
////    		gl.glPopMatrix();
////		}
//    		
////    		gl.glPopMatrix();
//    		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
////    		gl.glScalef(0.2f, 0.2f, 0.2f);
//    		
//    		// Draw the vertices as triangles, based on Index Buffer info
//    		gl.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength,
//    				GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
//    	}
		
		// Draw the vertices as triangles
//		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, DroidObject.getVertices().length / 3);
////		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertices.length / 3);
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    	
//    	mDrawCount++;
//    	final long currentTime = SystemClock.uptimeMillis();
//    	if (currentTime > (mDrawTime + PROFILE_REPORT_DELAY)) {
//    		Log.i("Renderer", "DrawableDroid draw() mDrawCount [gameObjectID] = " + mDrawCount + " [" + objectId + "]");
//    		mDrawTime = currentTime;
//    	}
    }

    /**
     * Ends the drawing and restores the OpenGL state.
     * 
     * @param gl  A pointer to the OpenGL context.
     */
//    public static void endDrawing() {
    public static void endDrawing(GL10 gl) {
    	
//    	Log.i("Renderer", "DrawableDroid endDrawing()");
    
    	// FIXME START Drawable Droid Test 9/8/12 
    	// FIXME START TEMP ADDED CODE
		// Disable the client state before leaving
    	//	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
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
    
    public void setViewSize(int width, int height) {
    	mViewHeight = height;
    	mViewWidth = width;
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
//        setCrop(0, height, width, height);
    }
    
    public void setOpacity(float opacity) {
      mOpacity = opacity;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }
    
    public void setLaserGroup(boolean laserGroup) {
    	mLaserGroup = true;
    }

    @Override
    public boolean visibleAtPosition(Vector3 position) {
    	boolean cull = false;
    	if (mViewWidth > 0) {
    		if (position.x + mWidth < 0 || position.x > mViewWidth 
                   || position.y + mHeight < 0 || position.y > mViewHeight) {
    			cull = true;
    		}
    	}
    	return !cull;
    }
}
