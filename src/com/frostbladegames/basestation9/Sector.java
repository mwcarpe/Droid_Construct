/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;


/**
 * The Sector class containing Triangles, Vertices, and Textures
 */
public class Sector {
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public int mNumTriangles;
	
	public Bitmap mBitmap;
	
	// Initiate Vertex and Texture Buffers
	public FloatBuffer mVertexBuffer;
	public FloatBuffer mTextureBuffer;
	
	public Triangle[] mTriangle;
	
	// Initial Vertex definition	
	public float[] mVertices;
	
	// Initial Texture Coordinates (u, v)
	public float[] mTextureUV;
	
	// Texture Pointer
	private int[] mTexturePointer = new int[1];
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	private Context mContext;
	
	public void loadSector(String sectorString, Context context) {		
		mContext = context;
		
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		try {
			int numTriangles = 0;
			int counter = 0;
			
			List<String> lines = null;
			StringTokenizer tokenizer;
			
			// Quick Reader for the Input File
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(mContext.getAssets().open(sectorString)));
			
			// Iterate over all lines
			String line = null;
			while((line = reader.readLine()) != null) {
				// Skip comments and empty lines
				if(line.startsWith("//") || line.trim().equals("")) {
					continue;
				}
				
				// Read how many polygons this file contains
				if(line.startsWith("NUMPOLLIES")) {
					numTriangles = Integer.valueOf(line.split(" ")[1]);					
					mNumTriangles = numTriangles;
					mTriangle = new Triangle[mNumTriangles];
				
				// Read every other line
				} else {
					if(lines == null) {
						lines = new ArrayList<String>();
					}
					lines.add(line);
				}
			}
			
			// Clean up!
			reader.close();
			
			// Now iterate over all read lines...
			for(int loop = 0; loop < mNumTriangles; loop++) {
				// ...define triangles...
				Triangle triangle = new Triangle();
				
				// ...and fill these triangles with the five read data 
				for(int vert = 0; vert < 3; vert++) {
					line = lines.get(loop * 3 + vert);
					tokenizer = new StringTokenizer(line);
					
					triangle.vertex[vert] = new Vertex();
					triangle.vertex[vert].x = Float.valueOf(tokenizer.nextToken());
					triangle.vertex[vert].y = Float.valueOf(tokenizer.nextToken());
					triangle.vertex[vert].z = Float.valueOf(tokenizer.nextToken());
					// TODO Change to Color vertex
//					triangle.vertex[vert].u = Float.valueOf(tokenizer.nextToken());
//					triangle.vertex[vert].v = Float.valueOf(tokenizer.nextToken());
				}
				
				// Finally, add the triangle to the sector
				mTriangle[counter++] = triangle;
			}
			
		// If anything should happen, write a log and return
		} catch(Exception e) {
			Log.e("World", "Could not load the World file!", e);
			return;
		}
		
		/*  
		 * Could/Should be done in one step above. Kept
		 * separated to stick near to the original Lesson10.
		 * This is a quick and not recommended solution.
		 * Just made to quickly present the tutorial.
		 */
		/*
		 * Convert to classic buffer structure.
		 */
		mVertices = new float[mNumTriangles * 3 * 3];
		mTextureUV = new float[mNumTriangles * 3 * 2];
		
		int vertCounter = 0;
		int texCounter = 0;
				
		for(Triangle triangle : mTriangle) {
			for(Vertex vertex : triangle.vertex) {
				mVertices[vertCounter++] = vertex.x;
				mVertices[vertCounter++] = vertex.y;
				mVertices[vertCounter++] = vertex.z;

				// TODO Change to Color vertex
//				mTextureUV[texCounter++] = vertex.u;
//				mTextureUV[texCounter++] = vertex.v;
			}
		}		
		
		// Build the buffers
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(mVertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuf.asFloatBuffer();
		mVertexBuffer.put(mVertices);
		mVertexBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(mTextureUV.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(mTextureUV);
		mTextureBuffer.position(0);
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public void loadBitmap(int bitmapPointer, Context context) {		
		mContext = context;
				
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		// Get the texture from the Android Resource directory
		InputStream is = mContext.getResources().openRawResource(bitmapPointer);

		try {
			// BitmapFactory is an Android Graphics Utility for Images
			mBitmap = BitmapFactory.decodeStream(is);
		} finally {
			// Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public void loadSectorGLTexture(GL10 gl, Context context) {
//		Log.d("Sector", "loadSectorGLTexture()");
		
		mContext = context;
		
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		Bitmap bitmap = mBitmap;
		
		// Generate a texture pointer
		gl.glGenTextures(1, mTexturePointer, 0);
		
		// XXX Test all 3 filter types. Change back to Mipmap?
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, 
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, 
				GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// XXX Change back to Mipmap after gluLookAt() debug
//		// Create MipMapped textures and bind it to texture
//		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, 
//				GL10.GL_LINEAR);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, 
//				GL10.GL_LINEAR_MIPMAP_NEAREST);
//		
//		// XXX Since GL11 supported, buildMipmap() not called
//		/*
//		 * buildMipMap does not exist anymore in the Android SDK.
//		 * Check if GL context is version 1.1 and generate MipMaps by flag.
//		 * Otherwise call custom buildMipMap method
//		 */
//		if(gl instanceof GL11) {
//			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
//			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//		} else {
//			buildMipmap(gl, bitmap);
//		}		
		
		// Clean up
		bitmap.recycle();
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	public void drawSector(GL10 gl) {
//		Log.d("Sector", "drawSector()");
		
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		// Bind the texture based on the given filter
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
		
		// Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				
		// Point to our buffers
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
		
		// Draw the vertices as triangles
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertices.length / 3);
		
		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
	
	/**
	 * MipMap generation implementation.
	 * Scale the original bitmap down, always by factor two,
	 * and set it as new mipmap level.
	 * Thanks to Mike Miller and InsanityDesign.
	 * 
	 * @param gl - The GL Context
	 * @param bitmap - The bitmap to mipmap
	 */
	private void buildMipmap(GL10 gl, Bitmap bitmap) {
//		Log.d("Sector", "buildMipmap()");
		
		int level = 0;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		while(height >= 1 || width >= 1) {
			// Generate the texture from bitmap and set to according level
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
			
			if(height == 1 || width == 1) {
				break;
			}

			// Increase the MipMap level
			level++;

			height /= 2;
			width /= 2;
			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			
			// Clean up
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}
	
//	public Bitmap getBitmap() {
//		return mBitmap;
//	}
//
//	public void setBitmap(Bitmap mBitmap) {
//		this.mBitmap = mBitmap;
//	}

//	public FloatBuffer getVertexBuffer() {
//		return mVertexBuffer;
//	}
//
//	public void setVertexBuffer(FloatBuffer vertexBuffer) {
//		this.mVertexBuffer = vertexBuffer;
//	}
//
//	public FloatBuffer getTextureBuffer() {
//		return mTextureBuffer;
//	}
//
//	public void setTextureBuffer(FloatBuffer textureBuffer) {
//		this.mTextureBuffer = textureBuffer;
//	}
}