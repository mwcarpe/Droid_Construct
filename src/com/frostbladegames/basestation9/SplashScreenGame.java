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

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.frostbladegames.basestation9.R;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;


/**
 * High-level setup object for SplashScreenActivity
 */
public class SplashScreenGame extends AllocationGuard {
    private SplashScreenThread mGameThread;
//    private GameThread mGameThread;
    private Thread mGame;
    private ObjectManager mGameRoot;
    
    private SplashScreenRenderer mRenderer;
//    private GameRenderer mRenderer;
    private DroidGLSurfaceView mSurfaceView;
    private boolean mRunning;
    private boolean mBootstrapComplete;
//    private LevelTree.Level mPendingLevel;
//    private LevelTree.Level mCurrentLevel;
//    private LevelTree.Level mLastLevel;
    private boolean mGLDataLoaded;
//    private ContextParameters mContextParameters;
    
    private boolean mObjectsSpawned;
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    private int mViewWidth;
    private int mViewHeight;
    private float mInverseViewScaleY;
    
//    private MediaPlayer mBackgroundMusic;
//    private int mMusicLevel;
//	private boolean mMusicPlaying;
	private boolean mSoundSystemActive;
	
	private boolean mLevelLoaded;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public SplashScreenGame() {
        super();
//    	mLastTime = SystemClock.uptimeMillis();
    	
        mRunning = false;
        mBootstrapComplete = false;
        mGLDataLoaded = false;
//        mContextParameters = new ContextParameters();
        
        mObjectsSpawned = false;
        
//        mMusicPlaying = false;
        mSoundSystemActive = false;
        mLevelLoaded = false;
        /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    }

    /** 
     * Creates core game objects and constructs the game engine object graph for SplashScreenActivity.
     * The game does not actually begin running after this function is called (see start() below).
     * @param context
     */
    public void bootstrap(Context context, int viewWidth, int viewHeight, int gameWidth, int gameHeight) {    	
        if (!mBootstrapComplete) {
        	if (GameParameters.debug) {
            	Log.i("GameFlow", "SplashScreenGame bootstrap() START");	
        	}
        	
        	// TODO Is this part of main Game Graph loop system and initiates GL state each loop?
            // Create core systems
            BaseObject.sSystemRegistry.openGLSystem = new OpenGLSystem(null);
            
            mViewWidth = viewWidth;
            mViewHeight = viewHeight;
            
            // viewWidth, viewHeight for Touch Events; gameWidth, gameHeight for Render
            GameParameters.viewWidth = viewWidth;
            GameParameters.viewHeight = viewHeight;
            GameParameters.gameWidth = gameWidth;
            GameParameters.gameHeight = gameHeight;
            
            // viewScaleX, viewScaleY used for scaling Touch region and scaling png for Render based on mdpi, hdpi, other
            float viewScaleX = (float)viewWidth / gameWidth;
            float viewScaleY = (float)viewHeight / gameHeight;
            mInverseViewScaleY = 1 / viewScaleY;
            
            mRenderer = new SplashScreenRenderer(context, this, gameWidth, gameHeight, viewScaleX, viewScaleY);
//            mRenderer = new GameRenderer(context, this, gameWidth, gameHeight, viewScaleX, viewScaleY);
            
//            TextureLibrary shortTermTextureLibrary = new TextureLibrary();
////            BaseObject.sSystemRegistry.shortTermTextureLibrary = shortTermTextureLibrary;
            
            // FIXME DELETED 11/11/12
//            // Long-term textures persist between levels.
//            TextureLibrary longTermTextureLibrary = new TextureLibrary();
//            BaseObject.sSystemRegistry.longTermTextureLibrary = longTermTextureLibrary;
            
            SoundSystem sound = new SoundSystem();
            BaseObject.sSystemRegistry.soundSystem = sound;
//            BaseObject.sSystemRegistry.soundSystem = new SoundSystem();
            mSoundSystemActive = true;
            BaseObject.sSystemRegistry.registerForReset(sound);
            
            // The root of the game graph.
            MainLoop gameRoot = new MainLoop();
    
            InputSystem input = new InputSystem();
            BaseObject.sSystemRegistry.inputSystem = input;
            BaseObject.sSystemRegistry.registerForReset(input);
            
            LevelSystem level = new LevelSystem();
            BaseObject.sSystemRegistry.levelSystem = level;

            GameObjectManager gameManager = new GameObjectManager(GameParameters.viewWidth * 2);
            BaseObject.sSystemRegistry.gameObjectManager = gameManager;
            
            GameObjectFactory objectFactory = new GameObjectFactory();
            BaseObject.sSystemRegistry.gameObjectFactory = objectFactory;
            objectFactory.context = context;
            
            CameraSystem camera = new CameraSystem();
            BaseObject.sSystemRegistry.cameraSystem = camera;
            BaseObject.sSystemRegistry.registerForReset(camera);
    
            gameRoot.add(gameManager);
    
            /* Camera must come after the game manager so that the camera target moves
               before the camera centers. */
            gameRoot.add(camera);
            
            SpecialEffectSystem specialEffect = new SpecialEffectSystem();
            gameRoot.add(specialEffect);
            BaseObject.sSystemRegistry.specialEffectSystem = specialEffect;

            GameObjectCollisionSystem dynamicCollision = new GameObjectCollisionSystem();
            gameRoot.add(dynamicCollision);
            BaseObject.sSystemRegistry.gameObjectCollisionSystem = dynamicCollision;
            
            SplashScreenRenderSystem renderer = new SplashScreenRenderSystem();
            BaseObject.sSystemRegistry.splashScreenRenderSystem = renderer;
//            RenderSystem renderer = new RenderSystem();
//            BaseObject.sSystemRegistry.renderSystem = renderer;
            
            BaseObject.sSystemRegistry.vectorPool = new VectorPool();
          
            HudSystem hud = new HudSystem((int)mViewWidth, (int)mViewHeight); 
//            HudSystem hud = new HudSystem();            
            BaseObject.sSystemRegistry.hudSystem = hud;
            gameRoot.add(hud);
            
            // registerForReset(objectFactory) is last system called 
            BaseObject.sSystemRegistry.registerForReset(objectFactory);
    
            mGameRoot = gameRoot;
            
            mGameThread = new SplashScreenThread(mRenderer, context);
//            mGameThread = new GameThread(mRenderer);
            mGameThread.setSplashRoot(mGameRoot);
//            mGameThread.setGameRoot(mGameRoot);
            
            mBootstrapComplete = true;
            
            if (GameParameters.debug) {
                Log.i("GameFlow", "SplashScreenGame bootstrap() END");	
            }
        }
    }

    protected synchronized void goToLevel() {  
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "SplashScreenGame goToLevel()");	
    	}
    	
    	Context context = BaseObject.sSystemRegistry.gameObjectFactory.context;

//        mRenderer.setContext(context);
        
        int levelId = R.raw.level00_splashscreen_medium;
        
        BaseObject.sSystemRegistry.levelSystem.loadLevelSplashScreen(context, levelId, mGameRoot);
//        BaseObject.sSystemRegistry.levelSystem.loadLevel(level,
//          		context.getResources().openRawResource(level.resource), mGameRoot);

        // FIXME DELETED 11/11/12
//        mRenderer.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
////        mRenderer.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
        
        mRenderer.spawnObjects(BaseObject.sSystemRegistry.levelSystem);
        
        mObjectsSpawned = true;
        
        mGLDataLoaded = true;
        
        TimeSystem time = BaseObject.sSystemRegistry.timeSystem;
        time.reset();
        
        HudSystem hud = BaseObject.sSystemRegistry.hudSystem;
        
        start();
    }

//    protected synchronized void stopSplash() {
//    	stop();
//      	GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
//      	manager.destroyAll();
//      	manager.commitUpdates();
//      
//      	// XXX: it's not strictly necessary to clear the static data here, but if I don't do it
//      	// then two things happen: first, the static data will refer to junk Texture objects, and
//      	// second, memory that may not be needed for the next level will hang around.  One solution
//      	// would be to break up the texture library into static and non-static things, and
//      	// then selectively clear static game components based on their usefulness next level,
//      	// but this is way simpler.
//      	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//      	// TODO Re-enable - staticData
////      factory.clearStaticData();
//      
//      	factory.sanityCheckPools();
//      
////    	Reset the level
//      	BaseObject.sSystemRegistry.levelSystem.reset();
//      
//      	// Reset systems that need it.
//      	BaseObject.sSystemRegistry.reset();
//    }
    
    /** Starts the game running. */
    public void start() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "SplashScreenGame start()");	
    	}
    	
        if (!mRunning) {
        	if (GameParameters.debug) {
            	Log.i("GameFlow", "SplashScreenGame start() !mRunning");	
        	}
        	
            assert mGame == null;
            // Now's a good time to run the GC.
            Runtime r = Runtime.getRuntime();
            r.gc();
            mGame = new Thread(mGameThread);
            mGame.setName("Splash");
            mGame.start();
            mRunning = true;
            AllocationGuard.sGuardActive = false;
            
//        	Context context = BaseObject.sSystemRegistry.gameObjectFactory.context;
//        	
//        	mBackgroundMusic = MediaPlayer.create(context, R.raw.sound_splashscreen);
//          	mBackgroundMusic.setVolume(1.0f, 1.0f);
//          	mBackgroundMusic.setLooping(false);
//          	mBackgroundMusic.start();
//        	mMusicPlaying = true;
        } else {
        	if (GameParameters.debug) {
        		Log.i("GameFlow", "SplashScreenGame start() mRunning resumeGame()");	
        	}
        	
        	mGameThread.resumeGame();
        }
    }
    
    public void stop() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "SplashScreenGame stop()");	
    	}
    	
        if (mRunning) {     
        	if (GameParameters.debug) {
            	Log.i("GameFlow", "SplashScreenGame stop() mRunning");	
        	}
        	
//            if (mMusicPlaying) {
//            	mBackgroundMusic.stop();
//            	mMusicPlaying = false;
//            }
            
//            if (mSoundSystemActive) {
//            	SoundSystem soundSystem = BaseObject.sSystemRegistry.soundSystem;
//            	soundSystem.reset();
//            	mSoundSystemActive = false;
//            }
            
            if (mGameThread.getPaused()) {
                mGameThread.resumeGame();
            }
            mGameThread.stopSplash();
            
//            mGameThread.stopActive();
////            mGameThread.stopSplash();
////            mGameThread.stopGame();
            
            try {
                mGame.join();
            } catch (InterruptedException e) {
                mGame.interrupt();
            }
            
            mGame = null;
            mRunning = false;
            
            GameParameters.splashScreen = false;
            
//            mCurrentLevel = null;
            AllocationGuard.sGuardActive = false;
            
          	GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
          	manager.destroyAll();
          	manager.commitUpdates();
          	
          	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
          	if (GameParameters.debug) {
              	factory.sanityCheckPools();	
          	}
          	
          	// Reset systems that need it.
          	BaseObject.sSystemRegistry.reset();
        }
        
//  	  ((SplashScreenActivity)mContext).startMainMenu();

//        mSurfaceView.post(Runnable finish());
        
//        Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
//        startActivity(i);
    }
    
	public boolean isRunning() {
		return (mRunning && mGameThread != null);
	}
    
    public void onPause() {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "SplashScreenGame onPause()");	
    	}
  	
    	if (mRunning) {
    		if (GameParameters.debug) {
        		Log.i("GameFlow", "SplashScreenGame onPause() mRunning");	
    		}
    		
//            if (mMusicPlaying) {
//            	mBackgroundMusic.pause();
//            	mMusicPlaying = false;
//            }
    		mGameThread.pauseGame();
    	}
    }
  
	public boolean isPaused() {
		return (mRunning && mGameThread != null && mGameThread.getPaused());
	}
  
	public void onResume(Context context, boolean force) {
		if (GameParameters.debug) {
			Log.i("GameFlow", "SplashScreenGame onResume()");	
		}
  	
		if (force && mRunning) {
			if (GameParameters.debug) {
				Log.i("GameFlow", "SplashScreenGame onResume() force && mRunning");	
			}
			
			mGameThread.resumeGame();
			
//            if (!mMusicPlaying) {
//            	mBackgroundMusic.start();
//            	mMusicPlaying = true;
//            }
			
			BaseObject.sSystemRegistry.gameObjectFactory.context = context;
			
//  		} else {
//  			Log.i("GameFlow", "SplashScreenGame onResume() NOT force or mRunning");
//  			
////	        mRenderer.setContext(context);
//	        // Don't explicitly resume the game here.  We'll do that in
//	        // the SurfaceReady() callback, which will prevent the game
//	        // starting before the render thread is ready to go.
//	        BaseObject.sSystemRegistry.gameObjectFactory.context = context;
////	        BaseObject.sSystemRegistry.contextParameters.context = context;
//	        
////            if (!mMusicPlaying) {
////            	mBackgroundMusic.start();
////            	mMusicPlaying = true;
////            }
  		}
	}
  
  	public void onSurfaceCreated(GL10 gl) {
  		if (GameParameters.debug) {
  	  		Log.i("GameFlow", "SplashScreenGame onSurfaceCreated()");	
  		}
  		
  	// XXX: this is dumb.  SurfaceView doesn't need to control everything here.
  	// GL should just be passed to this function and then set up directly.
    
  	// XXX Added by FrostBlade
  		
  	    if (!mGLDataLoaded) {
  	        if (mObjectsSpawned) {
  	  	        GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
  	  	        GL11 gl11 = (GL11)gl;
  	  	        factory.reloadSplashScreenDrawables(gl11);
  	        }
  	        
//  	    if (!mGLDataLoaded && mGameThread.getPaused() && mRunning && mPendingLevel == null) {
//  		if (!mGLDataLoaded && mGameThread.getPaused() && mRunning) {
    	
  	    	// FIXME DELETED 11/11/12 Currently only longTermTextureLibrary used for HUD buttons. Optimize code.
//        	mRenderer.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
////        	mRenderer.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
////        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
////        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
    	
//        	mRenderer.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
////        mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
        	
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Start */
//        	GL11 gl11 = (GL11)gl;
//        	GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
//        	factory.setGL(gl11);
//        	/* XXX Droid Code © 2012 FrostBlade LLC - Finish */
        	
        	mGLDataLoaded = true;
  		}  
  	}
  
  	public void onSurfaceReady() {
  		if (GameParameters.debug) {
  	  		Log.i("GameFlow", "SplashScreenGame onSurfaceReady()");	
  		}
  		
  	    goToLevel();
  	    
//  	  	if (mGameThread.getPaused() && mRunning) {
//			mGameThread.resumeGame();
//  	  	}
    
//  		// Temporary code only
//  		if (!mLevelLoaded) {
//  	  		goToLevel();
//  	  		mLevelLoaded = true;
//  		}

//    	if (mPendingLevel != null && mPendingLevel != mCurrentLevel) {
//	        if (mRunning) {
//	            stopLevel();
//	        }
//	        goToLevel(mPendingLevel);
//    	} else if (mGameThread.getPaused() && mRunning) {
//  			mGameThread.resumeGame();
//    	} 
    }
  
  	public void onSurfaceLost() {
  		if (GameParameters.debug) {
  	  		Log.i("GameFlow", "SplashScreenGame onSurfaceLost()");	
  		}
  		
  		// FIXME DELETED 11/11/12 Currently only longTermTextureLibrary used for HUD buttons. Optimize code.
////  		BaseObject.sSystemRegistry.shortTermTextureLibrary.invalidateAll();
//      	BaseObject.sSystemRegistry.longTermTextureLibrary.invalidateAll();
      
//      BaseObject.sSystemRegistry.bufferLibrary.invalidateHardwareBuffers();

      	mGLDataLoaded = false;
  	}
    
    public boolean onTouchEvent(MotionEvent event) {
    	
    	// FIXME Re-enable and debug crash upon SplashScreenActivity and SplashScreenGame onTouchEvent()
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//        	mGameThread.stopActive();
////        	mGameThread.stopSplash();
////        	mActive = false;
//        }
        return true;
    }
    
//    public boolean onKeyDownEvent(int keyCode) {
////        DebugLog.d("Game", "onKeyDownEvent()");
//    	
//        boolean result = false;
//        if (mRunning) {
//            BaseObject.sSystemRegistry.inputSystem.keyDown(keyCode);
//        }
//        return result;
//    }
    
//    public boolean onKeyUpEvent(int keyCode) {
////        DebugLog.d("Game", "onKeyUpEvent()");
//    	
//        boolean result = false;
//        if (mRunning) {
//        	BaseObject.sSystemRegistry.inputSystem.keyUp(keyCode);
//        }
//        return result;
//    }
    
    public SplashScreenRenderer getRenderer() {
        return mRenderer;
    }  
//    public GameRenderer getRenderer() {
//        return mRenderer;
//    }  
    
    public void setSurfaceView(DroidGLSurfaceView surfaceView) {      	
    	mSurfaceView = surfaceView;
    	
    	mGameThread.setSurfaceView(surfaceView);
    }

	public void setSoundEnabled(boolean soundEnabled) {
		SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
//		BaseObject.sSystemRegistry.soundSystem.setSoundEnabled(soundEnabled);
		sound.setSoundEnabled(soundEnabled);
	}
	
	public float getGameTime() {
		return BaseObject.sSystemRegistry.timeSystem.getGameTime();
	}
}
