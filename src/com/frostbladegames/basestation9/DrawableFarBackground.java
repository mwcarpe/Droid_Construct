/*
 * Copyright © 2012 FrostBlade LLC
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
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import com.frostbladegames.basestation9.GameObjectGroups.Type;

/** 
 * Draws an OpenGL 3D far background object to the screen.
 */
public class DrawableFarBackground extends DrawableObject {
	private static final int VERTEX_SIZE = 32;  // 8 values (= 3 verts + 3 norms + 2 uv texture) * 4 float size
	private static final int NORMAL_OFFSET = 12;  // 3 vertex points * 4 float size
	private static final int TEXTURE_OFFSET = 24;  // NORMAL_OFFSET + (3 normal points * 4 float size)
	
	private boolean mVBOSupport = false;
	
	private int mTextureFileId;
	private int mVBOFileId;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mNormalBuffer;
	private FloatBuffer mTextureBuffer;
	// FIXME TEMP TEST. RE-ENABLE.
	private ShortBuffer mIndexBuffer;
	// FIXME TEMP TEST. RE-ENABLE.
	// FIXME OLD TEST CODE. DELETE.
//	private ByteBuffer mIndexBuffer;
	// FIXME OLD TEST CODE. DELETE.
	
	private FloatBuffer mLight0AmbientBuffer;
	private FloatBuffer mLight0DiffuseBuffer;
	private FloatBuffer mLight0SpecularBuffer;
	private FloatBuffer mLight0PositionBuffer;
	private FloatBuffer mLight1AmbientBuffer;
	private FloatBuffer mLight1DiffuseBuffer;
	private FloatBuffer mLight1SpecularBuffer;
	private FloatBuffer mLight1PositionBuffer;
	private FloatBuffer mLight1SpotDirection;
	private FloatBuffer mLight1SpotCutoff;
	
	private FloatBuffer mMaterialAmbientBuffer;
	private FloatBuffer mMaterialDiffuseBuffer;
	private FloatBuffer mMaterialSpecularBuffer;
	private FloatBuffer mMaterialShininessBuffer;
	private FloatBuffer mMaterialEmissionBuffer;
	
	private int mVertexId;
	private int mNormalId;
	private int mIndexId;
	
	private int mIndicesLength;
	
	/** Our texture pointer */
	private int[] mTextures = new int[1];
	
//    private Texture mTexture;
//    private int mWidth;
//    private int mHeight;
//    private int mViewWidth;
//    private int mViewHeight;
//    private float mOpacity;
	
//    // FIXME TEMP TEST. DELETE.
//    private static final int PROFILE_REPORT_DELAY = 10 * 1000;
//    private long mDrawTime;
//    private int mDrawCount;
    
    DrawableFarBackground() {
//    DrawableFarBackground(int width, int height) {
        super(); 
        
        reset();
        
//        mTexture = texture;
//        mWidth = width;
//        mHeight = height;
//        mCrop = new int[4];
//        mViewWidth = 0;
//        mViewHeight = 0;
//        mOpacity = 1.0f;
//        setCrop(0, height, width, height);
        
//        // FIXME TEMP ONLY. MOVE TO LevelSystem or other.
//        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        mBackgroundObject = new BackgroundObject();
//        
//        mBackgroundObject.loadObject("floor_test1.txt", context);
//        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }
    
//    DrawableBackground(Texture texture, int width, int height) {
//        super();
//        mTexture = texture;
//        mWidth = width;
//        mHeight = height;
//        mCrop = new int[4];
//        mViewWidth = 0;
//        mViewHeight = 0;
//        mOpacity = 1.0f;
//        setCrop(0, height, width, height);
//    }

    public void reset() {
    	mVertexBuffer = null;
    	mNormalBuffer = null;
    	mTextureBuffer = null;
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
    	
    	mMaterialAmbientBuffer = null;
    	mMaterialDiffuseBuffer = null;
    	mMaterialSpecularBuffer = null;
    	mMaterialShininessBuffer = null;
    	mMaterialEmissionBuffer = null;
    	
//        mTexture = null;
//        mViewWidth = 0;
//        mViewHeight = 0;
//        mOpacity = 1.0f;
        
    }
    
	/**
	 * Load the textures
	 * 
	 * @param gl - The GL Context
	 * @param context - The Activity context
	 */
	public void loadGLTexture(GL10 gl, int fileId, Context context) {
		mTextureFileId = fileId;
		
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(fileId);
//		InputStream is = context.getResources().openRawResource(R.drawable.far_background01);
		Bitmap bitmap = null;
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		//Generate one texture pointer...
		gl.glGenTextures(1, mTextures, 0);
		//...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
		
		//Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		
		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		//Clean up
		bitmap.recycle();
	}
	
	public void reloadGLTexture(GL10 gl, Context context) {
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(mTextureFileId);
//		InputStream is = context.getResources().openRawResource(R.drawable.far_background01);
		Bitmap bitmap = null;
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		//Generate one texture pointer...
		gl.glGenTextures(1, mTextures, 0);
		//...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);
		
		//Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		
		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		//Clean up
		bitmap.recycle();
	}
	
	 // FIXME OLD TEST CODE. DELETE
//	public void loadObjectVBO(GL11 gl11, int currentLevel) {		
//		mVBOSupport = true;
//		
//		// FIXME TEMP ONLY
//		if (currentLevel == 0) {
//		    final float vertices[] = {
//					//Vertices according to faces
//		    		25.000000f, 40.000000f, 0.000000f, 0.000000f, 0.000000f, 1.000000f, 0.000000f, 0.000000f,
//		    		-25.000000f, 40.000000f, 0.000000f, 0.000000f, 0.000000f, 1.000000f, 1.000000f, 0.000000f,
//					-25.000000f, -10.000000f, 0.000000f, 0.000000f, 0.000000f, 1.000000f, 1.000000f, 1.000000f,
//		
//					25.000000f, 40.000000f, 0.000000f, 0.000000f, 0.000000f, 1.000000f, 0.000000f, 0.000000f,
//					-25.000000f, -10.000000f, 0.000000f, 0.000000f, 0.000000f, 1.000000f, 1.000000f, 1.000000f,
//					25.000000f, -10.000000f, 0.000000f, 0.000000f, 0.000000f, 1.000000f, 0.000000f, 1.000000f,
//										};
//
//			final byte indices[] = {
//								//Faces definition
//					    		0,1,2, 3,4,5,	
//													};
//			
//			mIndicesLength = indices.length;
//			
//			// FIXME TEMP ONLY
//			//
//			ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
//			byteBuf.order(ByteOrder.nativeOrder());
//			mVertexBuffer = byteBuf.asFloatBuffer();
//			mVertexBuffer.put(vertices);
//			mVertexBuffer.position(0);
//			
//			//
//			mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
//			mIndexBuffer.put(indices);
//			mIndexBuffer.position(0);  	
//			
//		} else {
//		    final float vertices[] = {
//					//Vertices according to faces
//					32.591831f, 13.421445f, 2.763508f, -0.379616f, 0.843673f, 0.379616f, 0.000000f, 0.000000f,
//					-2.763494f, 13.421449f, -32.591831f, -0.379616f, 0.843673f, 0.379616f, 1.000000f, 0.000000f,
//					-32.591831f, -13.421443f, -2.763512f, -0.379616f, 0.843673f, 0.379616f, 1.000000f, 1.000000f,
//		
//					32.591831f, 13.421445f, 2.763508f, -0.379616f, 0.843673f, 0.379616f, 0.000000f, 0.000000f,
//					-32.591831f, -13.421443f, -2.763512f, -0.379616f, 0.843673f, 0.379616f, 1.000000f, 1.000000f,
//					2.763506f, -13.421444f, 32.591831f, -0.379616f, 0.843673f, 0.379616f, 0.000000f, 1.000000f,
//										};
//
//			final byte indices[] = {
//								//Faces definition
//					    		0,1,2, 3,4,5,	
//													};
//			
//			mIndicesLength = indices.length;
//			
//			// FIXME TEMP ONLY
//			//
//			ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
//			byteBuf.order(ByteOrder.nativeOrder());
//			mVertexBuffer = byteBuf.asFloatBuffer();
//			mVertexBuffer.put(vertices);
//			mVertexBuffer.position(0);
//			
//			//
//			mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
//			mIndexBuffer.put(indices);
//			mIndexBuffer.position(0);  	
//		}
//		
//		try {			
//			// Load Vertex VBO
//	      	int [] vboId = new int[1];
//	      	gl11.glGenBuffers(1, vboId, 0);
//			mVertexId = vboId[0];
//			
//			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
//			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GL11.GL_STATIC_DRAW);
//			
//			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
//			
//			// Null the original Vertex Buffer
//			mVertexBuffer = null;
//			
//			// Load Index VBO
//	      	int [] indId = new int[1];
//	      	gl11.glGenBuffers(1, indId, 0);
//			mIndexId = indId[0];
//			
//			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
//			gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndicesLength, mIndexBuffer, GL11.GL_STATIC_DRAW);
//			
//			gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
//			
//			// Null the original Index Buffer
//			mIndexBuffer = null;
//			
//		} catch (Exception e) {
//			Log.e("DroidObject", "loadObject error = " + e.getMessage());
//		}
//	}
	// FIXME OLD TEST CODE. DELETE
    
	public void loadObjectVBO(GL11 gl11, int fileId, Context context) {			
		mVBOSupport = true;
		
		mVBOFileId = fileId;
		
		// FIXME RE-ENABLED for TEST.
		InputStream is = null;
		DataInputStream dis = null;
		
		try {
			is = context.getResources().openRawResource(fileId);
			
			dis = new DataInputStream(is);
			
			int numTriangles = dis.readInt();
			
			mIndicesLength = numTriangles * 3;
			
			int indCapacity = numTriangles * 3 * 2;
			
			// Texture object capacity
			int vertCapacity = numTriangles * ((3 * 3 * 4) + (3 * 3 * 4) + (3 * 2 * 4));
			
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
	
	public void reloadObjectVBO(GL11 gl11, Context context) {			
		mVBOSupport = true;
		
		// FIXME RE-ENABLED for TEST.
		InputStream is = null;
		DataInputStream dis = null;
		
		try {
			is = context.getResources().openRawResource(mVBOFileId);
			
			dis = new DataInputStream(is);
			
			int numTriangles = dis.readInt();
			
			mIndicesLength = numTriangles * 3;
			
			int indCapacity = numTriangles * 3 * 2;
			
			// Texture object capacity
			int vertCapacity = numTriangles * ((3 * 3 * 4) + (3 * 3 * 4) + (3 * 2 * 4));
			
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
     * Begins GL draw() settings for both DrawableBackground and DrawableBitmap. Settings
     * not required for DrawableBitmap are disabled in DrawableBackground.endDrawing().
     * 
     * @param gl  A pointer to the OpenGL context.
     * @param viewWidth  The width of the screen. FIXME Required?
     * @param viewHeight  The height of the screen. FIXME Required?
     */
    public static void beginDrawing(GL10 gl) {
//    public static void beginDrawing(GL10 gl, int gameWidth, int gameHeight) {
    	
//    	Log.i("Renderer", "DrawableFarBackground beginDrawing()");
    	
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//
//		gl.glClearDepthf(1.0f);
//		gl.glEnable(GL10.GL_DEPTH_TEST);
//		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// Enable Texture for FarBackground
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
//    	gl.glShadeModel(GL10.GL_SMOOTH);
//        
//        gl.glEnable(GL10.GL_DITHER);
//    	        
//        gl.glEnable(GL10.GL_LIGHTING);
//        gl.glEnable(GL10.GL_LIGHT0);
//        gl.glEnable(GL10.GL_COLOR_MATERIAL);
//
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//        
//        GLU.gluPerspective(gl, 45.0f, (float)gameWidth / (float)gameHeight, 1.0f, 100.0f);
		
//		// FIXME TEMP ONLY
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        
//        CameraSystem camera = BaseObject.sSystemRegistry.cameraSystem;
//        float x = camera.getFocusPositionX();
//        float y = camera.getFocusPositionY();
//        float z = camera.getFocusPositionZ();
//        
//        // FIXME RE-ENABLE original gluLookAt() Game Play Angles.  Use variables for Intro Animation, View Slider Bar, etc.
//        GLU.gluLookAt(gl, x - 0.0f, y + 1.0f, z + 10.0f, x, y + 1.0f, z, 0.0f, 1.0f, 0.0f);
        
		// Enable the vertex, texture and normal state
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    /**
     * Draw the object at a given x,y position, expressed in pixels, with the
     * lower-left-hand-corner of the view being (0,0).
     * 
     * @param gl  A pointer to the OpenGL context
     * @param x  The number of pixels to offset this drawable's origin in the x-axis.
     * @param y  The number of pixels to offset this drawable's origin in the y-axis
     * @param scaleX The horizontal scale factor between the bitmap resolution and the display resolution.
     * @param scaleY The vertical scale factor between the bitmap resolution and the display resolution.
     */
//    @Override
    public void draw(GL10 gl, float x, float y, float z, float r, float oX, float oY, float oZ,
    		float rX, float scaleX, float scaleY, int gameWidth, int gameHeight, int objectId) {
//    public void draw(GL10 gl, float x, float y, float z, float r, float oX, float oY, float oZ, Type type,
//    		float rX, float scaleX, float scaleY, int gameWidth, int gameHeight) {
//    public void draw(GL10 gl, float x, float y, float z, float r, float scaleX, float scaleY, int gameWidth, int gameHeight) {
    	
    	beginDrawing(gl);
//    	beginDrawing(gl, gameWidth, gameHeight);
    	
    	// FIXME START TEST 9/16/12. RE-ENABLE.
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, mLight0AmbientBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, mLight0DiffuseBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mLight0SpecularBuffer);
//        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, mLight0PositionBuffer);
//        
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mMaterialAmbientBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mMaterialDiffuseBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mMaterialSpecularBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mMaterialShininessBuffer);
//        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, mMaterialEmissionBuffer);
    	// FIXME END TEST 9/16/12
    	
		//Bind our only previously generated texture in this case
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[0]);	
		
		// Vertex Buffer Objects (VBOs)
		GL11 gl11 = (GL11)gl;
		
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
		gl11.glVertexPointer(3, GL10.GL_FLOAT, VERTEX_SIZE, 0);
		gl11.glNormalPointer(GL10.GL_FLOAT, VERTEX_SIZE, NORMAL_OFFSET);
//		gl11.glColorPointer(4, GL10.GL_FLOAT, VERTEX_SIZE, COLOR_OFFSET);
		gl11.glTexCoordPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, TEXTURE_OFFSET);
		
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
		
		gl.glTranslatef(x, y, z);
		
		if (GameParameters.levelRow == 0) {
    		// Intro 0 degree view rotation
    		gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
		} else {
    		// Set 45 degree view rotation
    		gl.glRotatef(315.0f, 0.0f, 1.0f, 0.0f);
		}

		// Set view angle to align with Droid gluLookAt
		gl.glRotatef(rX, 1.0f, 0.0f, 0.0f);
//		gl.glRotatef(315.0f, 1.0f, 0.0f, 0.0f);
		
////		gl.glTranslatef(x, 0.0f, z);
//		gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
////		gl.glRotatef(-45.0f, 0.0f, 1.0f, 0.0f);
////		gl.glScalef(0.2f, 0.2f, 0.2f);
		
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// Draw the vertices as triangles, based on Index Buffer info        	
    	// FIXME TEMP TEST. RE-ENABLE.
		gl11.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength, GL10.GL_UNSIGNED_SHORT, 0);
    	// FIXME TEMP TEST. RE-ENABLE.
    	// FIXME OLD TEST CODE. DELETE.
//		gl11.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength, GL10.GL_UNSIGNED_BYTE, 0);
    	// FIXME OLD TEST CODE. DELETE.
		
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    	
//    	if (mVBOSupport) {
//    		// Vertex Buffer Objects (VBOs)
//    		GL11 gl11 = (GL11)gl;
//    		
//    		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertexId);
//    		gl11.glVertexPointer(3, GL10.GL_FLOAT, VERTEX_SIZE, 0);
//    		gl11.glNormalPointer(GL10.GL_FLOAT, VERTEX_SIZE, NORMAL_OFFSET);
////    		gl11.glColorPointer(4, GL10.GL_FLOAT, VERTEX_SIZE, COLOR_OFFSET);
//    		gl11.glTexCoordPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, TEXTURE_OFFSET);
//    		
//    		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexId);
//    		
//    		gl.glTranslatef(x, y, z);
//    		
//    		if (GameParameters.levelRow == 0) {
//        		// Intro 0 degree view rotation
//        		gl.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
//    		} else {
//        		// Set 45 degree view rotation
//        		gl.glRotatef(315.0f, 0.0f, 1.0f, 0.0f);
//    		}
//
//    		// Set view angle to align with Droid gluLookAt
//    		gl.glRotatef(rX, 1.0f, 0.0f, 0.0f);
////    		gl.glRotatef(315.0f, 1.0f, 0.0f, 0.0f);
//    		
//////    		gl.glTranslatef(x, 0.0f, z);
////    		gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
//////    		gl.glRotatef(-45.0f, 0.0f, 1.0f, 0.0f);
//////    		gl.glScalef(0.2f, 0.2f, 0.2f);
//    		
//    		// Draw the vertices as triangles, based on Index Buffer info        	
//        	// FIXME TEMP TEST. RE-ENABLE.
//    		gl11.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength, GL10.GL_UNSIGNED_SHORT, 0);
//        	// FIXME TEMP TEST. RE-ENABLE.
//        	// FIXME OLD TEST CODE. DELETE.
////    		gl11.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength, GL10.GL_UNSIGNED_BYTE, 0);
//        	// FIXME OLD TEST CODE. DELETE.
//    		
//    		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
//    		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
//    		
//    	} else {
//    		// Vertex Array Pointers
//    		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
//    		gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
////    		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
//    		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
//    		
//    		gl.glTranslatef(x, y, z);
//////    		gl.glTranslatef(x, 0.0f, z);
////    		gl.glRotatef(r, 0.0f, 1.0f, 0.0f);
////    		gl.glScalef(0.2f, 0.2f, 0.2f);
//    		
//    		// Draw the vertices as triangles, based on Index Buffer info
//    		gl.glDrawElements(GL10.GL_TRIANGLES, mIndicesLength, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
//    	}
		
    	endDrawing(gl);
    	
//    	mDrawCount++;
//    	final long currentTime = SystemClock.uptimeMillis();
//    	if (currentTime > (mDrawTime + PROFILE_REPORT_DELAY)) {
//    		Log.i("Renderer", "DrawableFarBackground draw() mDrawCount = " + mDrawCount);
//    		mDrawTime = currentTime;
//    	}
    }

    /**
     * Ends the drawing and restores the OpenGL state.
     * 
     * @param gl  A pointer to the OpenGL context.
     */
    public static void endDrawing(GL10 gl) {
    	
//    	Log.i("Renderer", "DrawableFarBackground endDrawing()");
    	
//		// FIXME TEMP ONLY
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glLoadIdentity();
//        
//        CameraSystem camera = BaseObject.sSystemRegistry.cameraSystem;
//        float x = camera.getFocusPositionX();
//        float y = camera.getFocusPositionY();
//        float z = camera.getFocusPositionZ();
//        
//        // FIXME RE-ENABLE original gluLookAt() Game Play Angles.  Use variables for Intro Animation, View Slider Bar, etc.
//        GLU.gluLookAt(gl, x - 4.5f, y + 10.0f, z + 4.5f, x - 0.5f, y, z + 0.5f, 0.0f, 1.0f, 0.0f);
    	
//        gl.glDisable(GL10.GL_COLOR_MATERIAL);
//        gl.glDisable(GL10.GL_LIGHT0);
//        gl.glDisable(GL10.GL_LIGHTING);
//        
//        gl.glDisable(GL10.GL_DEPTH_TEST);
		
		// Disable the client state
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
    	// Disable Texture for FarBackground
		gl.glDisable(GL10.GL_TEXTURE_2D);
    }

//    public void resize(int width, int height) {    
//        mWidth = width;
//        mHeight = height;
////        setCrop(0, height, width, height);
//    }
//    
//    public void setViewSize(int width, int height) {
//      mViewHeight = height;
//      mViewWidth = width;
//    }
//  
//    public void setOpacity(float opacity) {
//      mOpacity = opacity;
//    }
//
//    public int getWidth() {
//        return mWidth;
//    }
//
//    public void setWidth(int width) {
//        mWidth = width;
//    }
//
//    public int getHeight() {
//        return mHeight;
//    }
//
//    public void setHeight(int height) {
//        mHeight = height;
//    }

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
//
//    public int[] getCrop() {
//        return mCrop;
//    }
//
//    public void setTexture(Texture texture) {
//        mTexture = texture;
//    }
//
//    @Override
//    public Texture getTexture() {
//        return mTexture;
//    }

//   @Override
//   public boolean visibleAtPosition(Vector3 position) {
//	   
//       boolean cull = false;
//       if (mViewWidth > 0) {
//           if (position.x + mWidth < 0 || position.x > mViewWidth 
//                   || position.y + mHeight < 0 || position.y > mViewHeight) {
//               cull = true;
//           }
//       }
//       return !cull;
//   }
}
