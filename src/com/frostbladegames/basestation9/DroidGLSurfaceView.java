/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


public class DroidGLSurfaceView extends GLSurfaceView {	
//	private Game mGame;
//	private GameRenderer mRenderer;
	
    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     */
    public DroidGLSurfaceView(Context context) {
        super(context);
    }

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     */
    public DroidGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
//    @Override
//    public void onPause() {
//        super.onPause();
//        
//        Log.i("GameFlow", "DroidGLSurfaceView onPause()");
//    }
//    
//    @Override
//    public void onResume() {
//        super.onResume();
//        
//        Log.i("GameFlow", "DroidGLSurfaceView onResume()");
//    }
    
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//    	// FIXME TEMP. DELETE.
//    	GameParameters.droidSurfaceViewCounter++;
//    	
//    	if (mGame.isRunning() && !mGame.isPaused()) {    		
//    		
//    		final MotionEvent touchEvent = event;
//    		
//    		mGame.onTouchEvent(touchEvent);
//    		
////    		queueEvent(new Runnable() {
////    			public void run() {
////    				if (mGame.isRunning()) {
////    					mRenderer.onTouchEvent(touchEvent);
//////        				mRenderer.onTouchEvent(touchEvent, mGame.isRunning());
////////        				mGame.onTouchEvent(touchEvent);
////    				}
////    			}
////    		});
//    		
//    		// Sleep so that the main thread doesn't get flooded with UI events.
//    		try {
//    			Thread.sleep(32);
////    			Thread.sleep(64);
//          	
//    		} catch (InterruptedException e) {
//    			// No big deal if this sleep is interrupted.
//    		}
//    		
//    		mGame.getRenderer().waitDrawingComplete();
//    	}
//    	
//    	return true;
//    }
    
//    public void setGame(Game game) {
//    	mGame = game;
//    }
//    
//    public void setRenderer(GameRenderer renderer) {
//    	mRenderer = renderer;
//    	super.setRenderer(renderer);
//    }
}
