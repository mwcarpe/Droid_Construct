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
/* XXX Droid Code © 2012 FrostBlade LLC - Start */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/* XXX Droid Code © 2012 FrostBlade LLC - Finish */

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;


/**
 * The Texture Library manages all textures in the game.  Textures are pooled and handed out to
 * requesting parties via allocateTexture().  However, the texture data itself is not immediately
 * loaded at that time; it may have already been loaded or it may be loaded in the future via
 * a call to loadTexture() or loadAllTextures().  This allows Texture objects to be dispersed to
 * various game systems and while the texture data itself is streamed in or loaded as necessary.
 */
public class TextureLibrary extends BaseObject {
    // Textures are stored in a simple hash.  This class implements its own array-based hash rather
    // than using HashMap for performance.
    Texture[] mTextureHash;
    int[] mTextureNameWorkspace;
    int[] mCropWorkspace;
    static final int DEFAULT_SIZE = 256;
//    static final int DEFAULT_SIZE = 512;
    static BitmapFactory.Options sBitmapOptions  = new BitmapFactory.Options();
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
	// XXX Moved to Droid Object
//	public int mNumTriangles;

	// TODO Change from class level to local level bitmap variable?
//	public Texture mDroidTexture;
	
	// XXX Moved to Droid Object
//	// Initiate Vertex, Texture, Index, and Normal Buffers
//	private FloatBuffer mVertexBuffer;
//	private FloatBuffer mTextureBuffer;
//	private ByteBuffer mIndexBuffer;
//	private FloatBuffer mNormalBuffer;
	
	// XXX Moved to Droid Object
//	public Triangle[] mTriangle;
//
//	// Texture Pointer
//	private int[] mTexturePointer = new int[3];
//
//	// Initial Vertex definition	
//	private float mVertices[];
//	
//	// Initial Texture Coordinates (u, v)	
//	private float mTextureUV[];
//
//	// Initial Indices definition
//	private byte mIndices[];
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public TextureLibrary() {
        super();
        
        if (GameParameters.debug) {
            Log.i("GameFlow", "TextureLibrary <constructor>");	
        }
        
        mTextureHash = new Texture[DEFAULT_SIZE];
        for (int x = 0; x < mTextureHash.length; x++) {
            mTextureHash[x] = new Texture();
        }

        mTextureNameWorkspace = new int[1];
        mCropWorkspace = new int[4];
        
        /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        mDroidTexture = new Texture();
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
                
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }
    
    @Override
    public void reset() {
//        DebugLog.d("TextureLibrary", "reset()");
    	
        removeAll();
    }

    /** 
     * Creates a Texture object that is mapped to the passed resource id.  If a texture has already
     * been allocated for this id, the previously allocated Texture object is returned.
     * @param resourceID
     * @return
     */
    public Texture allocateTexture(int resourceID) {
//        DebugLog.d("TextureLibrary", "allocateTexture()- resourceID = " + resourceID);
    	
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	Texture texture = null;
//    	if(mDroidTexture != null) {
//    		texture = mDroidTexture;
//    	} else {
//    		texture = new Texture();
//    		texture.resource = resourceID;
//    		mDroidTexture = texture;
//    	}
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    	
    	// XXX Re-enabled Replica original code
        Texture texture = getTextureByResource(resourceID);
        if (texture == null) {
            texture = addTexture(resourceID, -1, 0, 0);
        }
        
        return texture;
    }

    /** Loads a single texture into memory.  Does nothing if the texture is already loaded. */
    public Texture loadTexture(Context context, GL10 gl, int resourceID) {
//        DebugLog.d("TextureLibrary", "loadTexture()");
    	
        Texture texture = allocateTexture(resourceID);
        texture = loadBitmap(context, gl, texture);
        return texture;
    }

    /** Loads all unloaded textures into OpenGL memory.  Already-loaded textures are ignored. */
    public void loadAll(Context context, GL10 gl) {    	
//        DebugLog.d("TextureLibrary", "loadAll()");
    	
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		
		/* TODO Is it necessary to get bitmapPointer from DroidObject to here?
    	   Currently called in DroidBottomComponent via GameObjectFactory */
//		mDroidTexture.resource = bitmapPointer;
		
//		if (mDroidTexture.resource != -1 && mDroidTexture.loaded == false) {
//			loadBitmap(context, gl, mDroidTexture);
//		}
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    	
    	// XXX Re-enabled - loadAll() mTextureHash
        for (int x = 0; x < mTextureHash.length; x++) {
        	// TODO Temp code only
        	if(mTextureHash[x].resource > 0){
//                DebugLog.d("TextureLibrary", "loadAll()- mTextureHash[x].resource = " + mTextureHash[x].resource);
        	}
        	
            if (mTextureHash[x].resource != -1 && mTextureHash[x].loaded == false) {
                loadBitmap(context, gl, mTextureHash[x]);
            }
        }
    }

    /** Flushes all textures from OpenGL memory */
    public void deleteAll(GL10 gl) {
//        DebugLog.d("TextureLibrary", "deleteAll()");
    	
        for (int x = 0; x < mTextureHash.length; x++) {
            if (mTextureHash[x].resource != -1 && mTextureHash[x].loaded) {
            	assert mTextureHash[x].name != -1;
                mTextureNameWorkspace[0] = mTextureHash[x].name;
                mTextureHash[x].name = -1;
                mTextureHash[x].loaded = false;
                
//            	DebugLog.d("TextureLibrary", "deleteAll() GL CODE");
                
                gl.glDeleteTextures(1, mTextureNameWorkspace, 0);
                int error = gl.glGetError();
                if (error != GL10.GL_NO_ERROR) {
                    Log.e("Texture Delete", "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + mTextureHash[x].resource);
                }
                
                assert error == GL10.GL_NO_ERROR;
            }
        }
    }
    
    /** Marks all textures as unloaded */
    public void invalidateAll() {
//        DebugLog.d("TextureLibrary", "invalidateAll()");
    	
        for (int x = 0; x < mTextureHash.length; x++) {
            if (mTextureHash[x].resource != -1 && mTextureHash[x].loaded) {
                mTextureHash[x].name = -1;
                mTextureHash[x].loaded = false;
            }
        }
    }

    /** Loads a bitmap into OpenGL and sets up the common parameters for 2D texture maps. */
//    protected Texture loadBitmap(Context context, GL10 gl, Texture droidTexture) {
    // XXX Replica original code
    protected Texture loadBitmap(Context context, GL10 gl, Texture texture) {
//        DebugLog.d("TextureLibrary", "loadBitmap()");
        
//    	DebugLog.d("TextureLibrary", "loadBitmap() GL CODE");

	    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
	   	assert gl != null;
        assert context != null;
        assert texture != null;
        if (texture.loaded == false && texture.resource != -1) {
            gl.glGenTextures(1, mTextureNameWorkspace, 0);
            
            int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("Texture Load 1", "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + texture.resource);
            }
            
            assert error == GL10.GL_NO_ERROR;
            
            int textureName = mTextureNameWorkspace[0];
            
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
            
            error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("Texture Load 2", "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + texture.resource);
            }
            
            assert error == GL10.GL_NO_ERROR;

            // XXX Droid original code
    		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
    		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
//            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE); //GL10.GL_REPLACE);

            InputStream is = context.getResources().openRawResource(texture.resource);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                	e.printStackTrace();
                    // Ignore.
                }
            }
            
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            
            error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("Texture Load 3", "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + texture.resource);
            }
            
            assert error == GL10.GL_NO_ERROR;

            // TODO Re-enable Replica original code. No apparent impact when disabled. Non-Hud setting only? Study.
//            mCropWorkspace[0] = 0;
//            mCropWorkspace[1] = bitmap.getHeight();
//            mCropWorkspace[2] = bitmap.getWidth();
//            mCropWorkspace[3] = -bitmap.getHeight();
//
//            ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES,
//                            mCropWorkspace, 0);

            texture.name = textureName;
            texture.width = bitmap.getWidth();
            texture.height = bitmap.getHeight();

            bitmap.recycle();
            
            error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("Texture Load 4", "GLError: " + error + " (" + GLU.gluErrorString(error) + "): " + texture.resource);
            }
            
            assert error == GL10.GL_NO_ERROR;
            
            texture.loaded = true;
        }
        return texture;
	    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }

    public boolean isTextureLoaded(int resourceID) {
    	// XXX Re-enabled Replica original code
        return getTextureByResource(resourceID) != null;
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	return true;
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }

    /**
     * Returns the texture associated with the passed Android resource ID.
     * @param resourceID The resource ID of a bitmap defined in R.java.
     * @return An associated Texture object, or null if there is no associated
     *  texture in the library.
     */
    public Texture getTextureByResource(int resourceID) {
//        DebugLog.d("TextureLibrary", "getTextureByResource()");
    	
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	Texture texture = null;
//    	if(mTextureHash[0].resource != -1) {
//            for (int x = 0; x < mTextureHash.length; x++) {
//                if(mTextureHash[x].resource == resourceID) {
//                    texture = mTextureHash[x];
//                }
//            }
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        int index = getHashIndex(resourceID);
        int realIndex = findFirstKey(index, resourceID);
        Texture texture = null;
        if (realIndex != -1) {
            texture = mTextureHash[realIndex];
        }        

    	if(texture != null) {
//    		DebugLog.d("TextureLibrary", "getTextureByResource()- texture.resource = " + texture.resource);
    	} else {
//    		DebugLog.d("TextureLibrary", "getTextureByResource()- texture = null");
    	}
        
        return texture;
    }

    private int getHashIndex(int id) {
        return id % mTextureHash.length;
    }

    /**
     * Locates the texture in the hash.  This hash uses a simple linear probe chaining mechanism:
     * if the hash slot is occupied by some other entry, the next empty array index is used.  
     * This is O(n) for the worst case (every slot is a cache miss) but the average case is
     * constant time. 
     * @param startIndex
     * @param key
     * @return
     */
    private int findFirstKey(int startIndex, int key) {
//    	Log.i("Sort", "TextureLibrary findFirstKey()");
    	
        int index = -1;
        for (int x = 0; x < mTextureHash.length; x++) {
            final int actualIndex = (startIndex + x) % mTextureHash.length;
            if (mTextureHash[actualIndex].resource == key) {
                index = actualIndex;
                break;
            } else if (mTextureHash[actualIndex].resource == -1) {
                break;
            }
        }
//		DebugLog.d("TextureLibrary", "findFirstKey()- actualIndex = " + index);
        
        return index;
    }

    /** Inserts a texture into the hash */
    protected Texture addTexture(int id, int name, int width, int height) {
//        DebugLog.d("TextureLibrary", "addTexture()");
        
    	// XXX Replica original code
        int index = findFirstKey(getHashIndex(id), -1);
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    	int index = id;
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        Texture texture = null;
        assert index != -1;
        
        if (index != -1) {
            mTextureHash[index].resource = id;
            mTextureHash[index].name = name;
            mTextureHash[index].width = width;
            mTextureHash[index].height = height;
            texture = mTextureHash[index];
        }
        return texture;
    }
    
    public void removeAll() {
//        DebugLog.d("TextureLibrary", "removeAll()");
    	
        for (int x = 0; x < mTextureHash.length; x++) {
            mTextureHash[x].reset();
        }
    }
    
//    // Temp code
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public void confirmTexture(int i) {
//    	int temp = mTextureHash[i].resource;
////		DebugLog.d("TextureLibrary", "confirmTexture()- mTextureHash[" + i + "].resource = " + temp);
//    }
//    // end Temp code
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
}
