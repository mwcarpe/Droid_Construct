/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import com.frostbladegames.basestation9.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
//import android.content.Intent;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool.OnLoadCompleteListener;


public class SplashScreenActivityOld extends Activity {
//public class SplashScreenActivity extends Activity implements OnCompletionListener, OnPreparedListener, SurfaceHolder.Callback {
    protected boolean mActive = true;
    // FIXME TEMP ONLY. Change back to mSplashTime = 5000
//    protected int mSplashTime = 30000;
    protected int mSplashTime = 5000;
	private SoundPool mSoundPool;
	private int mSoundId;
	private boolean mLoaded = false;
	private boolean mSoundPlayed = false;
	
//    // FIXME TEST ONLY. DISABLE IF UNSUCCESSFUL TEST.
//    private SurfaceView mPreview;
//    private SurfaceHolder holder;
//	private MediaPlayer mSplashscreenVideo;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
//        // FIXME TEMP ONLY. RE-ENABLE.
//        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId,
//                    int status) {
//                mLoaded = true;
//            }
//        });
//        mSoundId = mSoundPool.load(this, R.raw.sound_splashscreen, 1);
//        
//        // FIXME TEST ONLY. DISABLE IF UNSUCCESSFUL TEST.
////        DisplayMetrics dm = new DisplayMetrics();
////        getWindowManager().getDefaultDisplay().getMetrics(dm);
////        int viewWidth = dm.widthPixels;
////        int viewHeight = dm.heightPixels;
////        mPreview = (SurfaceView) findViewById(R.id.surface);
////        holder = mPreview.getHolder();
////        holder.setFixedSize(viewWidth, viewHeight);
////        holder.addCallback(this);
////        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
////        
////        mSplashscreenVideo = MediaPlayer.create(this, R.raw.splashscreen_video);
//////      mSplashscreenVideo = MediaPlayer.create(this, R.raw.familyguytrailer);
////        
////        mSplashscreenVideo.setDisplay(holder);
//////        mSplashscreenVideo.prepare();
//////      mMediaPlayer.setOnBufferingUpdateListener(this);
////        mSplashscreenVideo.setOnCompletionListener(this);
////        mSplashscreenVideo.setOnPreparedListener(this);
//////      mMediaPlayer.setOnVideoSizeChangedListener(this);
////        mSplashscreenVideo.setAudioStreamType(AudioManager.STREAM_MUSIC);
////
////        mSplashscreenVideo.setVolume(1.0f, 1.0f);
//        
//        // Thread for displaying the Splash Screen
//        Thread splashThread = new Thread() {
//            @Override
//            public void run() {
//                try {                    
//                    int waited = 0;
//                    while(mActive && (waited < mSplashTime)) {                    	
//                        sleep(100);
//                        if(mActive) {
//                            waited += 100;
//                        }
//                        
//                     // FIXME TEMP ONLY. RE-ENABLE.
//                    	if (mLoaded && waited > 1500) {
//                        	if (!mSoundPlayed) {
//                                mSoundPool.play(mSoundId, 1.0f, 1.0f, 2, 0, 1.0f);
//                                mSoundPlayed = true;
//                        	}
//                    	}
//                    	
//                        // FIXME TEST ONLY. DISABLE IF UNSUCCESSFUL TEST.
////                        mSplashscreenVideo.start();
////                    	if (waited > 3000) {
////                            mSplashscreenVideo.start();
////                    	}
//                    }
//                    
////                  Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
////                  startActivity(i);
//                    
//                } catch(InterruptedException e) {
//                    // do nothing
//                } finally {
//                    finish();
////                    Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
////                    startActivity(i);
//                    // FIXME Is this Thread stable? stop() has been deprecated. But don't want this Splash to re-appear.
////                    stop();
//                }
//            }
//        };
//        
//        splashThread.start();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        	mActive = false;
        }
        return true;
    }
    
    // FIXME TEMP surfacexxx() ONLY
//    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
//        Log.d("Video Test", "surfaceChanged called");
//
//    }
//
//    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
//        Log.d("Video Test", "surfaceDestroyed called");
//    }
//
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.d("Video Test", "surfaceCreated called");
////        playVideo(extras.getInt(MEDIA));
//    }
//    
//    public void onCompletion(MediaPlayer arg0) {
//        Log.d("Video Test", "onCompletion called");
//    }
//    
//    public void onPrepared(MediaPlayer mediaplayer) {
//        Log.d("Video Test", "onPrepared called");
//        
//        startVideoPlayback();
//        
////        mIsVideoReadyToBePlayed = true;
////        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
////            startVideoPlayback();
////        }
//    }
//    
//    private void startVideoPlayback() {
//    	mSplashscreenVideo.start();
//    }
    
    @Override
    protected void onPause() {
        super.onPause();
//        releaseMediaPlayer();
////        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        releaseMediaPlayer();
////        doCleanUp();
    }

//    private void releaseMediaPlayer() {
//        if (mSplashscreenVideo != null) {
//        	mSplashscreenVideo.release();
//        	mSplashscreenVideo = null;
//        }
//    }
}
