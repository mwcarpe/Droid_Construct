/**
 * 
 */
package com.frostbladegames.basestation9;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.DisplayMetrics;


/**
 * Fire Button graphic
 */
public class FireButton {
	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
	/** The buffer holding the vertices */
	private FloatBuffer mVertexBuffer;
	
	/** The initial vertex definition */
//	private float[] mVertices = new float[12];
	private float[] mVertices;
//	private float mVertices[] = { 
//								1.1f, -0.2f, 0.0f, //Bottom Left
//								1.5f, -0.2f, 0.0f, 	//Bottom Right
//								1.1f, 0.2f, 0.0f, 	//Top Left
//								1.5f, 0.2f, 0.0f 	//Top Right
//								-0.2f, -0.2f, 0.0f, //Bottom Left
//								0.2f, -0.2f, 0.0f, 	//Bottom Right
//								-0.2f, 0.2f, 0.0f, 	//Top Left
//								0.2f, 0.2f, 0.0f 	//Top Right
//								-1.0f, -1.0f, 0.0f, //Bottom Left
//								1.0f, -1.0f, 0.0f, 	//Bottom Right
//								-1.0f, 1.0f, 0.0f, 	//Top Left
//								1.0f, 1.0f, 0.0f 	//Top Right
//												};
	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	
	/**
	 * The Square constructor.
	 * 
	 * Initiate the buffers.
	 */
	public FireButton() {
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		mVertices = new float[12];
		
//		// XXX Change from static to dynamic scaling
//		DisplayMetrics dm = new DisplayMetrics();
////		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		if(dm.densityDpi == 160) {
////		if(dm.densityDpi == dm.DENSITY_MEDIUM) {
////			final float vert[] = { 
////					1.1f, -0.2f, 0.0f, //Bottom Left
////					1.5f, -0.2f, 0.0f, 	//Bottom Right
////					1.1f, 0.2f, 0.0f, 	//Top Left
////					1.5f, 0.2f, 0.0f 	//Top Right
////			};
////			mVertices = vert;
//			mVertices[0] = 1.1f;
//			mVertices[1] = -0.2f;
//			mVertices[2] = 0.0f;
//			mVertices[3] = 1.5f;
//			mVertices[4] = -0.2f;
//			mVertices[5] = 0.0f;
//			mVertices[6] = 1.1f;
//			mVertices[7] = 0.2f;
//			mVertices[8] = 0.0f;
//			mVertices[9] = 1.5f;
//			mVertices[10] = 0.2f;
//			mVertices[11] = 0.0f;
//		} else if(dm.densityDpi == 240) {
//		} else if(dm.densityDpi == dm.DENSITY_HIGH) {
//			final float vert[] = { 
//					1.1f, -0.2f, 0.0f, //Bottom Left
//					1.5f, -0.2f, 0.0f, 	//Bottom Right
//					1.1f, 0.2f, 0.0f, 	//Top Left
//					1.5f, 0.2f, 0.0f 	//Top Right
//			};
//			mVertices = vert;
			mVertices[0] = 2.6f;
			mVertices[1] = -0.2f;
			mVertices[2] = 0.0f;
			mVertices[3] = 3.0f;
			mVertices[4] = -0.2f;
			mVertices[5] = 0.0f;
			mVertices[6] = 2.6f;
			mVertices[7] = 0.2f;
			mVertices[8] = 0.0f;
			mVertices[9] = 3.0f;
			mVertices[10] = 0.2f;
			mVertices[11] = 0.0f;
//		}
		
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(mVertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuf.asFloatBuffer();
		mVertexBuffer.put(mVertices);
		mVertexBuffer.position(0);
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}

	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 * 
	 * @param gl - The GL context
	 */
	public void draw(GL10 gl) {
		/* XXX Droid Code © 2012 FrostBlade LLC - Start */
		//Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		//Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		
		//Enable vertex buffer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		// Set 50% Alpha and Blending for Translucency
//		gl.glColor4f(0, 0, 0, 0.8f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		//Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, mVertices.length / 3);
		
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
	}
}
