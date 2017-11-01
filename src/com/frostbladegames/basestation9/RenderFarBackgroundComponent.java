/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;


/** 
 * Implements rendering of a drawable far background object for a game object.  If a drawable is set on this
 * component it will be passed to the renderer and drawn on the screen every frame.  Drawable
 * objects may be set to be "camera-relative" (meaning their screen position is relative to the
 * location of the camera focus in the scene) or not (meaning their screen position is relative to
 * the origin at the lower-left corner of the display).
 */
public class RenderFarBackgroundComponent extends GameComponent {
	// PARALLAX moves in opposite direction as a % of Droid x,z Movement
	private static final float PARALLAX_XZ = -0.015f;
	private static final float PARALLAX_Y = -0.005f;
//	private static final float PARALLAX = -0.02f;
//	private static final float PARALLAX = -0.01f;
//	private static final float PARALLAX_NEGATIVE = -0.01f;
//	private static final float PARALLAX_POSITIVE = 0.01f;
	private static final float DROID_MAX_MOVE = 11.111f;	// equals 1 divided by Droid Max Move 0.09
//	private static final float DROID_MAX_MOVE = 0.09f;
//	private static final float DISTANCE_FROM_CAMERA = 10.0f;
	
    public int priority;
    public boolean cameraRelative;
    
    private float mDistanceFromCameraX;
    private float mDistanceFromCameraY;
    private float mDistanceFromCameraZ;
    
    private float mPreviousPositionX;
    private float mPreviousPositionY;
    private float mPreviousPositionZ;
    
    private float mParallaxX;
    private float mParallaxY;
    private float mParallaxZ;
    
//    private float mX;
//    private float mY;
//    private float mZ;
//    private float mR;
    
//    private Vector3 mDrawOffset;
    
    public DrawableFarBackground drawableFarBackground;
    
    public RenderFarBackgroundComponent() {
        super();
        setPhase(ComponentPhases.DRAW.ordinal());
        
//        mDrawOffset = new Vector3();
        
        // FIXME 12/5/12 DELETED
//        drawableFarBackground = new DrawableFarBackground();
        // FIXME END 12/5/12 DELETED
        
        reset();
    }
    
    @Override
    public void reset() {    	
        priority = 0;
        cameraRelative = true;
//        mDrawOffset.zero();
        
        mDistanceFromCameraX = 0.0f;
        mDistanceFromCameraY = 0.0f;
        mDistanceFromCameraZ = 0.0f;
        
        mPreviousPositionX = 0.0f;
        mPreviousPositionY = 0.0f;
        mPreviousPositionZ = 0.0f;
        
        mParallaxX = 0.0f;
        mParallaxY = 0.0f;
        mParallaxZ = 0.0f;
    }

    public void update(float timeDelta, BaseObject parent) {
        RenderSystem render = sSystemRegistry.renderSystem;
        
        GameObject gameObject = (GameObject)parent;
        
//        int gameObjectId = gameObject.gameObjectId;
        
//        Log.i("Loop", "RenderFarBackgroundComponent update()" + " [" + gameObject.gameObjectId + "] ");
    	
	    CameraSystem camera = sSystemRegistry.cameraSystem;
	    
	    float x = camera.getFocusPositionX();
        float y = camera.getFocusPositionY();
//        float y = camera.getFocusPositionY() + mDistanceFromCameraY;
	    float z = camera.getFocusPositionZ();
        float r = 0.0f;
        
        float xMove = x - mPreviousPositionX;
        float yMove = y - mPreviousPositionY;
        float zMove = z - mPreviousPositionZ;
        
        // Increment Parallax in opposite direction as a % of Droid x,z Movement
        mParallaxX += (xMove * DROID_MAX_MOVE) * PARALLAX_XZ;
        mParallaxY += (yMove * DROID_MAX_MOVE) * PARALLAX_Y;
        mParallaxZ += (zMove * DROID_MAX_MOVE) * PARALLAX_XZ;
//        mParallaxX += (xMove / DROID_MAX_MOVE) * PARALLAX_XZ;
//        mParallaxY += (yMove / DROID_MAX_MOVE) * PARALLAX_Y;
//        mParallaxZ += (zMove / DROID_MAX_MOVE) * PARALLAX_XZ;
	    
//	    if ((x - mPreviousPositionX) < -0.001f) {	// Allow float tolerance
//	    	mParallaxX += (x / DROID_MAX_MOVE) * PARALLAX_POSITIVE;
//	    } else if ((x - mPreviousPositionX) > 0.001f) {
//	    	mParallaxX += (x / DROID_MAX_MOVE) * PARALLAX_NEGATIVE;
//	    }
//	    
//	    if ((z - mPreviousPositionZ) < -0.001f) {	// Allow float tolerance
//	    	mParallaxZ += (z / DROID_MAX_MOVE) * PARALLAX_POSITIVE;
//	    } else if ((z - mPreviousPositionZ) > 0.001f) {
//	    	mParallaxZ += (z / DROID_MAX_MOVE) * PARALLAX_NEGATIVE;
//	    }
        
        // Set previousPosition for next FarBackground move
        mPreviousPositionX = x;
        mPreviousPositionY = y;
        mPreviousPositionZ = z;
	    
        x += mDistanceFromCameraX + mParallaxX;
        y += mDistanceFromCameraY + mParallaxY;
        z += mDistanceFromCameraZ + mParallaxZ;
//        float x = camera.getFocusPositionX() + mDistanceFromCameraX;
//        float z = camera.getFocusPositionX() + mDistanceFromCameraZ;
        
        gameObject.currentPosition.set(x, y, z, r);
//        object.position.set(mX, mY, mZ, mR);
        
        // FIXME Is mCameraRelative required?
        cameraRelative = true;
        
		// Set FarBackground x angle based on gluLookAt() angle setting:  2.0f, 6.0f, 10.0f (default), 14.0f, 18.0f
        float farBackgroundAngle = camera.getFarBackgroundAngle();
        
//        Log.i("FarBackground", "RenderFarBackground update() levelRow; FarBackground.currentPosition.x,y,z,r; farBackgroundAngle = " +
//        		GameParameters.levelRow + "; " + x + ", " + y + ", " + z + ", " + r + "; " + farBackgroundAngle);
        
        // FIXME Delete gameObject.gameObjectId pass-thru, used for Debug only
        render.scheduleForDraw(drawableFarBackground, gameObject.currentPosition, null, farBackgroundAngle, priority, cameraRelative, gameObject.gameObjectId);
//        render.scheduleForDraw(drawableFarBackground, gameObject.currentPosition, null, 0.0f, priority, cameraRelative, gameObject.gameObjectId);
//        render.scheduleForDraw(object.drawableDroid, object.position, 0.0f, priority, cameraRelative, objectId);
    }
    
    public void setDistanceFromCamera(float x, float y, float z) {
    	mDistanceFromCameraX = x;
    	mDistanceFromCameraY = y;
    	mDistanceFromCameraZ = z;
    	
    	mPreviousPositionX = x;
    	mPreviousPositionY = y;
    	mPreviousPositionZ = z;
    }
    
//    public void setDrawableFarBackground(DrawableFarBackground drawableFarBackground) {
//    	mDrawableFarBackground = drawableFarBackground;
//    }

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
//    
//    public void setDrawOffset(float x, float y) {
////        DebugLog.d("RenderComponent", "setDrawOffset()");
//    	
//        mDrawOffset.set(x, y);
//    }
}
