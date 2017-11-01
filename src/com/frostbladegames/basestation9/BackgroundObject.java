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
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
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


/* TODO Should BackgroundObject extend BaseObject (ref TextureLibrary)
   or stand alone (ref Grid)? */
/**
 * Background Object Class for Background
 */
public class BackgroundObject {
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	public int mNumTriangles;
	
	// XXX Moved Texture Bitmap code to TextureLibrary
//	public Bitmap mBitmap;
	
	// FIXME Temp code. Delete static modifiers.
	// Initiate Vertex, Texture, and Index Buffers
	private static FloatBuffer mVertexBuffer;
	private static FloatBuffer mNormalBuffer;
//	private static FloatBuffer mTextureBuffer;
	private static FloatBuffer mColorBuffer;
	private static ShortBuffer mIndexBuffer;
//	private static ByteBuffer mIndexBuffer;
	
	public Triangle[] mTriangle;
	
	// FIXME Optimize code. Change to private with get/set.
	public DrawableFarBackground mBackground;

	// Texture Pointer
//	private int[] mTexturePointer = new int[3];

	// FIXME Temp code. Delete static modifier.
	// Initial Vertex definition	
	private static float mVertices[];
	
	// FIXME Temp code. Delete static modifier.
	// Initial Vertex definition	
	private static float mNormals[];
	
	// FIXME Temp code. Delete static modifier.
	// Initial Vertex definition	
	private static float mColors[];

	// Initial Texture Coordinates (u, v)	
//	private float mTextureUV[];

	// FIXME Temp code. Delete static modifier.
	// Initial Indices definition
	private static short mIndices[];
//	private static byte mIndices[];
	
//	private Context mContext;
	
	public BackgroundObject() {
		
	}
	
	// TODO Review Replica code reference and delete unnecessary code
//	// XXX Replica code reference - Start
//	public BackgroundObject(int quadsAcross, int quadsDown, boolean useFixedPoint) {
//    	final int vertsAcross = quadsAcross * 2;
//    	final int vertsDown = quadsDown * 2;
//        if (vertsAcross < 0 || vertsAcross >= 65536) {
//            throw new IllegalArgumentException("quadsAcross");
//        }
//        if (vertsDown < 0 || vertsDown >= 65536) {
//            throw new IllegalArgumentException("quadsDown");
//        }
//        if (vertsAcross * vertsDown >= 65536) {
//            throw new IllegalArgumentException("quadsAcross * quadsDown >= 32768");
//        }
//
//        mUseHardwareBuffers = false;
//        
//        mVertsAcross = vertsAcross;
//        mVertsDown = vertsDown;
//        int size = vertsAcross * vertsDown;
//        
//        
//        if (useFixedPoint) {
//        	mFixedVertexBuffer = ByteBuffer.allocateDirect(FIXED_SIZE * size * 3)
//            	.order(ByteOrder.nativeOrder()).asIntBuffer();
//        	mFixedTexCoordBuffer = ByteBuffer.allocateDirect(FIXED_SIZE * size * 2)
//            	.order(ByteOrder.nativeOrder()).asIntBuffer();
//        	
//        	mVertexBuffer = mFixedVertexBuffer;
//        	mTexCoordBuffer = mFixedTexCoordBuffer;
//        	mCoordinateSize = FIXED_SIZE;
//        	mCoordinateType = GL10.GL_FIXED;
//        	
//        } else {
//        	mFloatVertexBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 3)
//            	.order(ByteOrder.nativeOrder()).asFloatBuffer();
//        	mFloatTexCoordBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 2)
//            	.order(ByteOrder.nativeOrder()).asFloatBuffer();
//        	
//        	mVertexBuffer = mFloatVertexBuffer;
//        	mTexCoordBuffer = mFloatTexCoordBuffer;
//        	mCoordinateSize = FLOAT_SIZE;
//        	mCoordinateType = GL10.GL_FLOAT;
//        }
//        
//        int quadCount = quadsAcross * quadsDown;
//        int indexCount = quadCount * 6;
//        mIndexCount = indexCount;
//        mIndexBuffer = ByteBuffer.allocateDirect(CHAR_SIZE * indexCount)
//            .order(ByteOrder.nativeOrder()).asCharBuffer();
//
//        /*
//         * Initialize triangle list mesh.
//         *
//         *     [0]------[1]   [2]------[3] ...
//         *      |    /   |     |    /   |
//         *      |   /    |     |   /    |
//         *      |  /     |     |  /     |
//         *     [w]-----[w+1] [w+2]----[w+3]...
//         *      |       |
//         *
//         */
//
//        {
//            int i = 0;
//            for (int y = 0; y < quadsDown; y++) {
//            	final int indexY = y * 2;
//                for (int x = 0; x < quadsAcross; x++) {
//                	final int indexX = x * 2;
//                    char a = (char) (indexY * mVertsAcross + indexX);
//                    char b = (char) (indexY * mVertsAcross + indexX + 1);
//                    char c = (char) ((indexY + 1) * mVertsAcross + indexX);
//                    char d = (char) ((indexY + 1) * mVertsAcross + indexX + 1);
//
//                    mIndexBuffer.put(i++, a);
//                    mIndexBuffer.put(i++, b);
//                    mIndexBuffer.put(i++, c);
//
//                    mIndexBuffer.put(i++, b);
//                    mIndexBuffer.put(i++, c);
//                    mIndexBuffer.put(i++, d);
//                }
//            }
//        }
//        mVertBufferIndex = 0;
//	}
//	// XXX Replica code reference - Finish
	
	// TODO Is context required?
	// TODO update code from Sector baseline to custom obj reader
	public void loadObject(String sectorString, Context context) {
//	public void loadObject(String sectorString) {
//	public int loadObject(String sectorString, Context context) {
//		mContext = context;
//		mContext = BaseObject.sSystemRegistry.contextParameters.context;

			
			/* FIXME Change to Binary File direct Streamer.
			 * Create flag to Read Txt and Write Binary (Development), or Read Binary (Production) */
//			final String extension = ".";
//			int bitmapPointer = 0;
			
			try {
				int numTriangles = 0;
				int triangleCounter = 0;
				short indexCounter = 0;
//				int vCounter = 0;
				
				String cValue = null;
				List<String> vLines = null;
				List<String> nLines = null;
				List<String> cLines = null;
//				List<String> uLines = null;
				List<String> fLines = null;
//				String material = null;
				StringTokenizer vToken;
				StringTokenizer nToken;
				StringTokenizer cToken;
//				StringTokenizer uToken;
				StringTokenizer fToken;
				
				// TODO Replace mContext with ObjectRegistry item or other?
				// Reader for the Droid File
				BufferedReader reader = new BufferedReader(new 
						InputStreamReader(context.getAssets().open(sectorString)));
//						InputStreamReader(mContext.getAssets().open(sectorString)));
				
//				int vTestCounter = 0;
//				int cTestCounter = 0;
//				int fTestCounter = 0;
				
				/* TODO Optimize this read loop. Change to switch statement
				   with known order of line types? */
				// Read vertices, UV, material, and faces
				String line = reader.readLine();
				while(line != null) {
//				while((line = reader.readLine()) != null) {
					// Skip comments and empty lines
					if(line.startsWith("#") || line.trim().equals("")) {
						line = reader.readLine();
						continue;
					}
					
					// Read how many polygons this file contains
					if(line.startsWith("NUMPOLLIES")) {
						numTriangles = Integer.valueOf(line.split(" ")[1]);					
						mNumTriangles = numTriangles;
						mTriangle = new Triangle[mNumTriangles];
						line = reader.readLine();
						continue;
					}
					
					// Read vertices
					while(line.startsWith("v")){
						if(vLines == null) {
							vLines = new ArrayList<String>();
						}
						vLines.add(line.substring(2));
				    	
						line = reader.readLine();
					}
					
					// Read normals
					while(line.startsWith("n")){
						if(nLines == null) {
							nLines = new ArrayList<String>();
						}
						nLines.add(line.substring(2));
						line = reader.readLine();
					}
					
					// Read color
					while(line.startsWith("c")){				    	
				    	cValue = line.substring(2);
				    	
				    	line = reader.readLine();
					}
					
					// XXX Re-enable - UV InputStreamReader
//					// Read uv
//					while(line.startsWith("u")) {
//						if(uLines == null) {
//							uLines = new ArrayList<String>();
//						}
//						uLines.add(line.substring(2));
//						line = reader.readLine();
//					}
					
					// XXX Re-enable - Material InputStreamReader
					// Read material
//					if(line.startsWith("m")) {
//						material = line.substring(2);
////						material = line.substring(2, line.indexOf(extension));
//						material = "com.frostbladegames.droidconstruct:drawable/"
//							+ material;
//						bitmapPointer = mContext.getResources().
//							getIdentifier(material, null, null);
//						line = reader.readLine();
////						continue;
//					}
					
					if(line.startsWith("s")) {
						line = reader.readLine();
					}
					
					// Read face index
					while(line.startsWith("f")) {
						if(fLines == null) {
							fLines = new ArrayList<String>();
						}
						fLines.add(line.substring(2));
						
						if(cLines == null) {
							cLines = new ArrayList<String>();
						}
				    	
						cLines.add(cValue);
						
						line = reader.readLine();
						
						if(line == null) {
							break;
						}
						
						if(line.startsWith("v") || line.startsWith("n") || line.startsWith("c") || 
								line.startsWith("s") || line.startsWith("#") || line.trim().equals("")) {
							continue;
						}
					}
				}
				
				reader.close();
				
//				mIndices = new short[mNumTriangles * 3];
//				mIndices = new byte[mNumTriangles * 3];
				
				// TODO create separate class for parsers for Droid, Enemies
				// Add the triangles from the read data
//				for(int i = 0; i < fLines.size(); i++) {
				for(int i = 0; i < mNumTriangles; i++) {
					Triangle triangle = new Triangle();
						
					fToken = new StringTokenizer(fLines.get(i));
					
					cToken = new StringTokenizer(cLines.get(i));
					float tempC1 = Float.valueOf(cToken.nextToken());
					float tempC2 = Float.valueOf(cToken.nextToken());
					float tempC3 = Float.valueOf(cToken.nextToken());
					
					// Fill triangles with the five read data 
					for(int j = 0; j < 3; j++) {
						String slash = "/";
////						final String slash = "/";
						
//						int fv = Integer.valueOf(fToken.nextToken()) - 1;
						String fTemp = fToken.nextToken();
						int slashIndex = fTemp.indexOf(slash);
						int fv = Integer.valueOf(fTemp.substring(0, slashIndex)) - 1;
						int fn = Integer.valueOf(fTemp.substring(slashIndex + 1)) - 1;
						
						triangle.vertex[j] = new Vertex();
						
						triangle.vertex[j].i = indexCounter;
						indexCounter++;
						
						vToken = new StringTokenizer(vLines.get(fv));
						triangle.vertex[j].x = Float.valueOf(vToken.nextToken());
						triangle.vertex[j].y = Float.valueOf(vToken.nextToken());
						triangle.vertex[j].z = Float.valueOf(vToken.nextToken());
						
						nToken = new StringTokenizer(nLines.get(fn));
						triangle.vertex[j].n1 = Float.valueOf(nToken.nextToken());
						triangle.vertex[j].n2 = Float.valueOf(nToken.nextToken());
						triangle.vertex[j].n3 = Float.valueOf(nToken.nextToken());
						
//						cToken = new StringTokenizer(cLines.get(fv));
//						triangle.vertex[j].c1 = Float.valueOf(cToken.nextToken());
//						triangle.vertex[j].c2 = Float.valueOf(cToken.nextToken());
//						triangle.vertex[j].c3 = Float.valueOf(cToken.nextToken());
						triangle.vertex[j].c1 = tempC1;
						triangle.vertex[j].c2 = tempC2;
						triangle.vertex[j].c3 = tempC3;
						// TODO Alpha = 1.0
						triangle.vertex[j].c4 = 1.0f;
						
//						uToken = new StringTokenizer(uLines.get(fu));
//						triangle.vertex[j].u = Float.valueOf(uToken.nextToken());
//						triangle.vertex[j].v = Float.valueOf(uToken.nextToken());
					}				
					// Add triangle to the sector
					mTriangle[triangleCounter++] = triangle;
				}
			} catch(Exception e) {
				Log.e("BackgroundObject", "Could not load successfully", e);
				return;
			}
			
			/*  
			 * Could/Should be done in one step above. Kept
			 * separated to stick near to the original Lesson10.
			 * This is quick and not a recommended solution.
			 * Just made to quickly present the tutorial.
			 */
			/*
			 * Convert to classic buffer structure.
			 */
	    	// TODO Study converting float[] to short[] to speed up game
			mIndices = new short[mNumTriangles * 3];
			mVertices = new float[mNumTriangles * 3 * 3];
			mNormals = new float[mNumTriangles * 3 * 3];
			mColors = new float[mNumTriangles * 3 * 4];
//			mTextureUV = new float[mNumTriangles * 3 * 2];
			
			int idxCounter = 0;
			int vertCounter = 0;
			int normCounter = 0;
			int colCounter = 0;
//			int texCounter = 0;
					
			for(Triangle triangle : mTriangle) {
				for(Vertex vertex : triangle.vertex) {
					mIndices[idxCounter++] = vertex.i;
					
					mVertices[vertCounter++] = vertex.x;
					mVertices[vertCounter++] = vertex.y;
					mVertices[vertCounter++] = vertex.z;
					
					mNormals[normCounter++] = vertex.n1;
					mNormals[normCounter++] = vertex.n2;
					mNormals[normCounter++] = vertex.n3;
					
					mColors[colCounter++] = vertex.c1;
					mColors[colCounter++] = vertex.c2;
					mColors[colCounter++] = vertex.c3;
					mColors[colCounter++] = vertex.c4;

//					mTextureUV[texCounter++] = vertex.u;
//					mTextureUV[texCounter++] = vertex.v;
				}
			}	
			
			// Build the buffers
			ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			mVertexBuffer.put(mVertices);
			mVertexBuffer.position(0);
	    	
			ByteBuffer nbb = ByteBuffer.allocateDirect(mNormals.length * 4);
			nbb.order(ByteOrder.nativeOrder());
			mNormalBuffer = nbb.asFloatBuffer();
			mNormalBuffer.put(mNormals);
			mNormalBuffer.position(0);
			
			ByteBuffer cbb = ByteBuffer.allocateDirect(mColors.length * 4);
			cbb.order(ByteOrder.nativeOrder());
			mColorBuffer = cbb.asFloatBuffer();
			mColorBuffer.put(mColors);
			mColorBuffer.position(0);
			
			ByteBuffer ibb = ByteBuffer.allocateDirect(mIndices.length * 2);
//	    	mIndexBuffer = ByteBuffer.allocateDirect(mIndices.length);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();
			mIndexBuffer.put(mIndices);
			mIndexBuffer.position(0);

//			ByteBuffer ubb = ByteBuffer.allocateDirect(mTextureUV.length * 4);
////			byteBuf = ByteBuffer.allocateDirect(mTextureUV.length * 4);
//			ubb.order(ByteOrder.nativeOrder());
////			byteBuf.order(ByteOrder.nativeOrder());
//			mTextureBuffer = ubb.asFloatBuffer();
////			mTextureBuffer = byteBuf.asFloatBuffer();
//			mTextureBuffer.put(mTextureUV);
//			mTextureBuffer.position(0);
			
			// XXX Moved Texture Bitmap code to TextureLibrary
			// Load bitmap. Get texture from Android Resource directory
//			InputStream is = mContext.getResources().openRawResource(bitmapPointer);
//
//			try {
//				// BitmapFactory is an Android Graphics Utility for Images
//				mBitmap = BitmapFactory.decodeStream(is);
//			} finally {
//				// Always clear and close
//				try {
//					is.close();
//					is = null;
//				} catch (IOException e) {
//				}
//			}
//		return bitmapPointer;
	}
	
	// TODO Moved Texture Bitmap code to TextureLibrary. Confirm code.
//	/**
//	 * Load the textures
//	 * 
//	 * @param gl - The GL Context
//	 * @param context - The Activity context
//	 */
////	public void loadGLTexture() {
//	public void loadGLTexture(GL10 gl, Context context) {
////		Log.d("DroidPlayer", "loadGLTexture()");
//		
//		// XXX Moved Texture Bitmap code to TextureLibrary
//		mContext = context;
//		Bitmap bitmap = mBitmap;
//
//		// TODO Add import GL10. Study how to sync with program GL.
//		// Generate there texture pointer
//		gl.glGenTextures(1, mTexturePointer, 0);
//		
//		// XXX Test all 3 filter types. Change back to Mipmap?
//		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, 
//				GL10.GL_NEAREST);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, 
//				GL10.GL_NEAREST);
//		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//		//Clean up
//		bitmap.recycle();
//	}

//	/* TODO Study Grid.draw(). Move enableClientState to begin/endDraw, other location.
//	   Add BindBuffer, other Grid code. */
//	/**
//	 * DroidPlayer draw method. Called from World.
//	 * 
//	 * @param gl - The GL Context
//	 */
//	public void draw(GL10 gl) {
//		
////		Log.d("DroidPlayer", "draw()");
//		
//		// Bind the texture according to the set texture filter
//		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexturePointer[0]);
//
//		// Enable the vertex, texture and normal state
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
////		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
//
//		// XXX Is it necessary to set glFrontFace?
//		// Set the face rotation
////		gl.glFrontFace(GL10.GL_CCW);
//		
//		// Point to our buffers
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
//		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
////		gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
//		
////		// Draw the vertices as triangles, based on Index Buffer info
////		gl.glDrawElements(GL10.GL_TRIANGLES, mIndices.length,
////				GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
//		
//		// Draw the vertices as triangles
//		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, mVertices.length / 3);
//		
//		// Disable the client state before leaving
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
////		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
//		
//    	// TODO Re-enable - Grid original baseline code
////		// XXX Replica Grid class reference - Start
////        if (!mUseHardwareBuffers) {
////            gl.glVertexPointer(3, mCoordinateType, 0, mVertexBuffer);
////    
////            if (useTexture) {
////                gl.glTexCoordPointer(2, mCoordinateType, 0, mTexCoordBuffer);
////            } 
////    
////            gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount,
////                    GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
////        } else {
////            GL11 gl11 = (GL11)gl;
////            // draw using hardware buffers
////            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
////            gl11.glVertexPointer(3, mCoordinateType, 0, 0);
////            
////            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex);
////            gl11.glTexCoordPointer(2, mCoordinateType, 0, 0);
////            
////            gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
////            gl11.glDrawElements(GL11.GL_TRIANGLES, mIndexCount,
////                    GL11.GL_UNSIGNED_SHORT, 0);
////            
////            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
////            gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
////        }
//        // XXX Replica code reference - Finish
//	}
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
//	// XXX Replica code reference - Start
//    public void set(int quadX, int quadY, float[][] positions, float[][] uvs) {
//        if (quadX < 0 || quadX * 2 >= mVertsAcross) {
//            throw new IllegalArgumentException("quadX");
//        }
//        if (quadY < 0 || quadY * 2 >= mVertsDown) {
//            throw new IllegalArgumentException("quadY");
//        }
//        if (positions.length < 4) {
//            throw new IllegalArgumentException("positions");
//        }
//        if (uvs.length < 4) {
//            throw new IllegalArgumentException("quadY");
//        }
//
//        int i = quadX * 2;
//        int j = quadY * 2;
//        
//        setVertex(i, j, 		positions[0][0], positions[0][1], positions[0][2], uvs[0][0], uvs[0][1]);
//        setVertex(i + 1, j, 	positions[1][0], positions[1][1], positions[1][2], uvs[1][0], uvs[1][1]);
//        setVertex(i, j + 1, 	positions[2][0], positions[2][1], positions[2][2], uvs[2][0], uvs[2][1]);
//        setVertex(i + 1, j + 1, positions[3][0], positions[3][1], positions[3][2], uvs[3][0], uvs[3][1]);
//    }
//	
//    private void setVertex(int i, int j, float x, float y, float z, float u, float v) {
//  	  if (i < 0 || i >= mVertsAcross) {
//  	       throw new IllegalArgumentException("i");
//  	   }
//  	   if (j < 0 || j >= mVertsDown) {
//  	       throw new IllegalArgumentException("j");
//  	   }
//
//  	   final int index = mVertsAcross * j + i;
//
//  	   final int posIndex = index * 3;
//  	   final int texIndex = index * 2;
//
//  	   
//  	   if (mCoordinateType == GL10.GL_FLOAT) {
//  	    mFloatVertexBuffer.put(posIndex, x);
//  	    mFloatVertexBuffer.put(posIndex + 1, y);
//  	    mFloatVertexBuffer.put(posIndex + 2, z);
//
//  	    mFloatTexCoordBuffer.put(texIndex, u);
//  	    mFloatTexCoordBuffer.put(texIndex + 1, v);
//  	   } else {
//  	    mFixedVertexBuffer.put(posIndex, (int)(x * (1 << 16)));
//  	    mFixedVertexBuffer.put(posIndex + 1, (int)(y * (1 << 16)));
//  	    mFixedVertexBuffer.put(posIndex + 2, (int)(z * (1 << 16)));
//
//  	    mFixedTexCoordBuffer.put(texIndex, (int)(u * (1 << 16)));
//  	    mFixedTexCoordBuffer.put(texIndex + 1, (int)(v * (1 << 16)));
//  	   }
//  	}
      
//      public static void beginDrawing(GL10 gl, boolean useTexture) {
//          gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//          
//          if (useTexture) {
//              gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//              gl.glEnable(GL10.GL_TEXTURE_2D);
//          } else {
//              gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//              gl.glDisable(GL10.GL_TEXTURE_2D);
//          }
//      }
      
//      public void beginDrawingStrips(GL10 gl, boolean useTexture) {
//      	// TODO Re-enable - Grid original baseline code
////          beginDrawing(gl, useTexture);
////          if (!mUseHardwareBuffers) {
////              gl.glVertexPointer(3, mCoordinateType, 0, mVertexBuffer);
////      
////              if (useTexture) {
////                  gl.glTexCoordPointer(2, mCoordinateType, 0, mTexCoordBuffer);
////              } 
////              
////          } else {
////              GL11 gl11 = (GL11)gl;
////              // draw using hardware buffers
////              gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
////              gl11.glVertexPointer(3, mCoordinateType, 0, 0);
////              
////              gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mTextureCoordBufferIndex);
////              gl11.glTexCoordPointer(2, mCoordinateType, 0, 0);
////              
////              gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
////          }
//      }
      
//      // Assumes beginDrawingStrips() has been called before this.
//      public void drawStrip(GL10 gl, boolean useTexture, int startIndex, int indexCount) {
//      	// TODO Re-enable - Grid original baseline code
////      	int count = indexCount;
////      	if (startIndex + indexCount >= mIndexCount) {
////      		count = mIndexCount - startIndex;
////      	}
////      	if (!mUseHardwareBuffers) {
////              gl.glDrawElements(GL10.GL_TRIANGLES, count,
////                      GL10.GL_UNSIGNED_SHORT, mIndexBuffer.position(startIndex));
////          } else {
////          	GL11 gl11 = (GL11)gl;
////              gl11.glDrawElements(GL11.GL_TRIANGLES, count,
////                      GL11.GL_UNSIGNED_SHORT, startIndex * CHAR_SIZE);
////   
////          }
//      }
	
//    public static void endDrawing(GL10 gl) {
//        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//    }
    
	// FIXME Enable or Delete usingHardwareBuffers()
    public boolean usingHardwareBuffers() {    
    	// TODO Re-enable - Grid original baseline code
//        return mUseHardwareBuffers;
    	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
    	return true;
    	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }
    
    // FIXME Enable or Move invalidateHardwareBuffers()
    /** 
     * When the OpenGL ES device is lost, GL handles become invalidated.
     * In that case, we just want to "forget" the old handles (without
     * explicitly deleting them) and make new ones.
     */
    public void invalidateHardwareBuffers() {
    	// TODO Re-enable - Grid original baseline code
//        mVertBufferIndex = 0;
//        mIndexBufferIndex = 0;
//        mTextureCoordBufferIndex = 0;
//        mUseHardwareBuffers = false;
    }
    
    // FIXME Enable or Move releaseHardwareBuffers()
    /**
     * Deletes the hardware buffers allocated by this object (if any).
     */
    public void releaseHardwareBuffers(GL10 gl) {
    	// TODO Re-enable - Grid original baseline code
//        if (mUseHardwareBuffers) {
//            if (gl instanceof GL11) {
//                GL11 gl11 = (GL11)gl;
//                int[] buffer = new int[1];
//                buffer[0] = mVertBufferIndex;
//                gl11.glDeleteBuffers(1, buffer, 0);
//                
//                buffer[0] = mTextureCoordBufferIndex;
//                gl11.glDeleteBuffers(1, buffer, 0);
//                
//                buffer[0] = mIndexBufferIndex;
//                gl11.glDeleteBuffers(1, buffer, 0);
//            }
//            
//            invalidateHardwareBuffers();
//        }
    }
    
    // FIXME Enable or Move generateHardwareBuffers()
    /** 
     * Allocates hardware buffers on the graphics card and fills them with
     * data if a buffer has not already been previously allocated.  Note that
     * this function uses the GL_OES_vertex_buffer_object extension, which is
     * not guaranteed to be supported on every device.
     * @param gl  A pointer to the OpenGL ES context.
     */
    public void generateHardwareBuffers(GL10 gl) {
    	// TODO Re-enable - Grid original baseline code
//        if (!mUseHardwareBuffers) {
//            if (gl instanceof GL11) {
//                GL11 gl11 = (GL11)gl;
//                int[] buffer = new int[1];
//                
//                // Allocate and fill the vertex buffer.
//                gl11.glGenBuffers(1, buffer, 0);
//                mVertBufferIndex = buffer[0];
//                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
//                final int vertexSize = mVertexBuffer.capacity() * mCoordinateSize; 
//                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, 
//                        mVertexBuffer, GL11.GL_STATIC_DRAW);
//                
//                // Allocate and fill the texture coordinate buffer.
//                gl11.glGenBuffers(1, buffer, 0);
//                mTextureCoordBufferIndex = buffer[0];
//                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 
//                        mTextureCoordBufferIndex);
//                final int texCoordSize = 
//                    mTexCoordBuffer.capacity() * mCoordinateSize;
//                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, 
//                        mTexCoordBuffer, GL11.GL_STATIC_DRAW);    
//                
//                // Unbind the array buffer.
//                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
//                
//                // Allocate and fill the index buffer.
//                gl11.glGenBuffers(1, buffer, 0);
//                mIndexBufferIndex = buffer[0];
//                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 
//                        mIndexBufferIndex);
//                // A char is 2 bytes.
//                final int indexSize = mIndexBuffer.capacity() * 2;
//                gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, mIndexBuffer, 
//                        GL11.GL_STATIC_DRAW);
//                
//                // Unbind the element array buffer.
//                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
//                
//                mUseHardwareBuffers = true;
//                
//                assert mVertBufferIndex != 0;
//                assert mTextureCoordBufferIndex != 0;
//                assert mIndexBufferIndex != 0;
//                assert gl11.glGetError() == 0;
//            }
//        }
    }
    // XXX Replica Grid class reference - Finish
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
	// FIXME Delete static modifiers
    public static FloatBuffer getVertexBuffer() {
    	return mVertexBuffer;
    }
    
    public static float[] getVertices() {
    	return mVertices;
    }
    
    public static int getVerticesLength() {
    	return mVertices.length;
    }
    
    public static FloatBuffer getNormalBuffer() {
    	return mNormalBuffer;
    }
    
    public static FloatBuffer getColorBuffer() {
    	return mColorBuffer;
    }
    
    public static ShortBuffer getIndexBuffer() {
    	return mIndexBuffer;
    }
    
//    public static ByteBuffer getIndexBuffer() {
//    	return mIndexBuffer;
//    }
    
    public static int getIndicesLength() {
    	return mIndices.length;
    }
    
//    public static FloatBuffer getTextureBuffer() {
//    	return mTextureBuffer;
//    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
}
