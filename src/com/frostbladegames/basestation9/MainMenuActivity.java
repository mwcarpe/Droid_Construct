/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import com.frostbladegames.basestation9.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
//import android.widget.Toast;


public class MainMenuActivity extends Activity {
//    public static final String PREFERENCE_LEVEL_ROW = "levelRow";
//    public static final String PREFERENCE_LEVEL_INDEX = "levelIndex";
//    public static final String PREFERENCE_LEVEL_COMPLETED = "levelsCompleted";
//    public static final String PREFERENCE_NAME = "BaseStationPrefs";
    
//	private boolean mSplashScreenPlayed;
	
    private boolean mPaused;
    
    private View mPlayGameButton;
//    private View mContinueButton;
    private View mOptionsButton;
    private View mBackground;
//    private View mTicker;
    private Animation mButtonFlickerAnimation;
    private Animation mFadeOutAnimation;
    private Animation mAlternateFadeOutAnimation;
    private Animation mFadeInAnimation;
    
//    private MediaPlayer mBackgroundMusic;
//	private boolean mMusicPlaying;
////	private SoundPool mSoundPool;
////	private int mSoundId;
////	private boolean mSoundPlayed = false;
    
    public static final int QUIT_GAME_DIALOG = 0;
//    private static final int WHATS_NEW_DIALOG = 0;
    
    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener sStartButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
//                if (mMusicPlaying) {
//                	mBackgroundMusic.stop();
//                	mMusicPlaying = false;
//                }
            	
//                SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
//                
//                int levelRow = prefs.getInt(PREFERENCE_LEVEL_ROW, 0);
//                int levelIndex = prefs.getInt(PREFERENCE_LEVEL_INDEX, 0);
//                int completed = prefs.getInt(PREFERENCE_LEVEL_COMPLETED, 0);
//                
//                Log.i("GameFlow", "MainMenuActivity onCreate() levelRow, levelIndex, completed = " + 
//                		levelRow + ", " + levelIndex + ", " + completed);
                
                Intent i = new Intent(getBaseContext(), LevelSelectActivity.class);
//                Intent i;
//                
//                if (levelRow == 0) {
//                	i = new Intent(getBaseContext(), ConstructActivity.class);
////                	Intent i = new Intent(getBaseContext(), ConstructActivity.class);
//                	i.putExtra("LevelSelected", 0);
//                	startActivity(i);
//                } else {
//                	i = new Intent(getBaseContext(), LevelSelectActivity.class);
////                	Intent i = new Intent(getBaseContext(), LevelSelectActivity.class);
//                }
////            	Intent i = new Intent(getBaseContext(), LevelSelectActivity.class);
//////            	startActivity(i);
//////                Intent i = new Intent(getBaseContext(), ConstructActivity.class);

                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartLevelSelectActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
//                mContinueButton.startAnimation(mAlternateFadeOutAnimation);
                mOptionsButton.startAnimation(mAlternateFadeOutAnimation);
//                mTicker.startAnimation(mAlternateFadeOutAnimation);
                mPaused = true;
            }
        }
    };
    
//    private View.OnClickListener sContinueButtonListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            if (!mPaused) {
////                if (mMusicPlaying) {
////                	mBackgroundMusic.stop();
////                	mMusicPlaying = false;
////                }
//                
//                Intent i = new Intent(getBaseContext(), ConstructActivity.class);
//
//                v.startAnimation(mButtonFlickerAnimation);
//                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
//                mBackground.startAnimation(mFadeOutAnimation);
//                mStartButton.startAnimation(mAlternateFadeOutAnimation);
//                mOptionsButton.startAnimation(mAlternateFadeOutAnimation);
////                mTicker.startAnimation(mAlternateFadeOutAnimation);
//                mPaused = true;
//            }
//        }
//    };
    
    private View.OnClickListener sOptionButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (!mPaused) {
                if (LevelTree.isLoaded()) {
                	LevelTree.clearLevelTree();
                }
            	
                Intent i = new Intent(getBaseContext(), SetPreferencesActivity.class);

                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartLevelSelectActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mPlayGameButton.startAnimation(mAlternateFadeOutAnimation);
//                mContinueButton.startAnimation(mAlternateFadeOutAnimation);
//                mTicker.startAnimation(mAlternateFadeOutAnimation);
                mPaused = true;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.mainmenu);
        mPaused = true;
        
        mPlayGameButton = findViewById(R.id.playgameButton);
//        mContinueButton = findViewById(R.id.continueButton);
        mOptionsButton = findViewById(R.id.optionButton);
        mBackground = findViewById(R.id.mainMenuBackground);
        
        if (mPlayGameButton != null) {
            mPlayGameButton.setOnClickListener(sStartButtonListener);
        }
        
//        if (mContinueButton != null) {
//            mContinueButton.setOnClickListener(sContinueButtonListener);
//        }
        
        if (mOptionsButton != null) {
            mOptionsButton.setOnClickListener(sOptionButtonListener);
        }
        
        mButtonFlickerAnimation = AnimationUtils.loadAnimation(this, R.anim.button_flicker);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        
        // TODO Re-enable LevelTree
//        if (!LevelTree.isLoaded()) {
//        	LevelTree.loadLevelTree(R.xml.level_tree, this);
//        	LevelTree.loadAllDialog(this);
//        }
        
//        mTicker = findViewById(R.id.ticker);
//        if (mTicker != null) {
//        	mTicker.setFocusable(true);
//        	mTicker.requestFocus();
//        	mTicker.setSelected(true);
//        }
        
        // FIXME Is this used?
        // Keep the volume control type consistent across all activities.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
//    	if (!mMusicPlaying){
//        	mBackgroundMusic = MediaPlayer.create(this, R.raw.sound_start_page);
//          	mBackgroundMusic.setVolume(1.0f, 1.0f);
//          	mBackgroundMusic.setLooping(true);
//          	mBackgroundMusic.start();
//        	mMusicPlaying = true;
//    	}
        
//        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//        mSoundId = mSoundPool.load(this, R.raw.sound_splashscreen, 1);       
//        mSoundPool.play(mSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
//        mSoundPlayed = true;
        
        //MediaPlayer mp = MediaPlayer.create(this, R.raw.bwv_115);
        //mp.start();
        
        if(!GameParameters.splashScreenPlayed) {
            Intent i = new Intent(getBaseContext(), SplashScreenActivity.class);
            startActivity(i);
            
            GameParameters.splashScreenPlayed = true;
        }
        
//        new Handler().postDelayed(new Runnable() {
//        	@Override
//        	public void run() {
//        		final Intent i = new Intent(getBaseContext(), SplashScreenActivity.class);
//                startActivity(i);
//                finish();
//        	}
//        }, 5000);
////        startActivityforResult(i, id);
////        finishActivity(id);
      
//        showDialog(WHATS_NEW_DIALOG);
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
        
        mPaused = true;
        
//        if (mMusicPlaying) {
//        	mBackgroundMusic.stop();
//        	mMusicPlaying = false;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        mPaused = false;
        
//        if (!mMusicPlaying) {
//        	mBackgroundMusic.start();
//        	mMusicPlaying = true;
//        }
        
        if (mPlayGameButton != null) {
        	mPlayGameButton.setVisibility(View.VISIBLE);
        	mPlayGameButton.clearAnimation();
        	mPlayGameButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
//            mStartButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_slide));
            
            // Change "start" to "continue" if there's a saved game.
            SharedPreferences prefs = getSharedPreferences(ConstructActivity.PREFERENCE_NAME, MODE_PRIVATE);
            
            // TODO Re-enable Level Saved Game
//            final int row = prefs.getInt(AndouKun.PREFERENCE_LEVEL_ROW, 0);
//            final int index = prefs.getInt(AndouKun.PREFERENCE_LEVEL_INDEX, 0);
//            if (row != 0 || index != 0) {
//            	((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.ui_button_continue));
//            } else {
//            	((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.ui_button_start));
//            }
            /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//            ((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_newgame));
//            ((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.ui_button_start));
            /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
            
            final int lastVersion = prefs.getInt(ConstructActivity.PREFERENCE_LAST_VERSION, 0);
            
//            if (lastVersion == 0) {
//            	// This is the first time the game has been run.  
//            	// Pre-configure the control options to match the device.
//            	// The resource system can tell us what this device has.
//            	// TODO: is there a better way to do this?  Seems like a kind of neat
//            	// way to do custom device profiles.
//            	final String navType = getString(R.string.nav_type);
//            	if (navType != null) {
//            		if (navType.equalsIgnoreCase("DPad")) {
//            			// Turn off the click-to-attack pref on devices that have a dpad.
//            			SharedPreferences.Editor editor = prefs.edit();
//                    	editor.putBoolean(ConstructActivity.PREFERENCE_CLICK_ATTACK, false);
//                    	editor.commit();
//            		} else if (navType.equalsIgnoreCase("None")) {
//            			// Turn on tilt controls if there's nothing else.
//            			SharedPreferences.Editor editor = prefs.edit();
//                    	editor.putBoolean(ConstructActivity.PREFERENCE_TILT_CONTROLS, true);
//                    	editor.commit();
//            		}
//            	}
//            }
            
//            if (Math.abs(lastVersion) < Math.abs(ConstructActivity.VERSION)) {
//            	// This is a new install or an upgrade.
//            	
//            	// Check the safe mode option.
//            	// Useful reference: http://en.wikipedia.org/wiki/List_of_Android_devices
//            	if (Build.PRODUCT.contains("morrison") ||	// Motorola Cliq/Dext
//            			Build.MODEL.contains("Pulse") ||	// Huawei Pulse
//            			Build.MODEL.contains("U8220") ||	// Huawei Pulse
//            			Build.MODEL.contains("U8230") ||	// Huawei U8230
//            			Build.MODEL.contains("MB300") ||	// Motorola Backflip
//            			Build.MODEL.contains("Behold+II")) {	// Samsung Behold II
//            		// These are all models that users have complained about.  They likely use
//            		// the same buggy QTC graphics driver.  Turn on Safe Mode by default
//            		// for these devices.
//            		SharedPreferences.Editor editor = prefs.edit();
//                	editor.putBoolean(ConstructActivity.PREFERENCE_SAFE_MODE, true);
//                	editor.commit();
//            	}
//            	
//            	// show what's new message
//            	SharedPreferences.Editor editor = prefs.edit();
//            	editor.putInt(ConstructActivity.PREFERENCE_LAST_VERSION, ConstructActivity.VERSION);
//            	editor.commit();
//            	
//            	showDialog(WHATS_NEW_DIALOG);
//            }
        }
        
//        if (mContinueButton != null) {
//        	mContinueButton.setVisibility(View.VISIBLE);
//        	mContinueButton.clearAnimation();
//            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//            anim.setStartOffset(200L);
//            mContinueButton.startAnimation(anim);
////        	mContinueButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
////        	mContinueButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_slide));
//            
//            // Change "start" to "continue" if there's a saved game.
//            SharedPreferences prefs = getSharedPreferences(ConstructActivity.PREFERENCE_NAME, MODE_PRIVATE);
//            
//            // TODO Re-enable Level Saved Game
////            final int row = prefs.getInt(AndouKun.PREFERENCE_LEVEL_ROW, 0);
////            final int index = prefs.getInt(AndouKun.PREFERENCE_LEVEL_INDEX, 0);
////            if (row != 0 || index != 0) {
////            	((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.ui_button_continue));
////            } else {
////            	((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.ui_button_start));
////            }
//            /* XXX Droid Code © 2012 FrostBlade LLC - Start */
////            ((ImageView)mContinueButton).setImageDrawable(getResources().getDrawable(R.drawable.mainmenu_continue_grayout));
////            ((ImageView)mStartButton).setImageDrawable(getResources().getDrawable(R.drawable.ui_button_start));
//            /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
//            
//            final int lastVersion = prefs.getInt(ConstructActivity.PREFERENCE_LAST_VERSION, 0);
//        }
        
        if (mOptionsButton != null) {
        	mOptionsButton.setVisibility(View.VISIBLE);
        	mOptionsButton.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//            Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_slide);
            anim.setStartOffset(400L);
            mOptionsButton.startAnimation(anim);
        }
        
        if (mBackground != null) {
//        	mBackground.setVisibility(View.VISIBLE);
        	mBackground.clearAnimation();
//            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//            mBackground.startAnimation(anim);
        }
        
//        if (mTicker != null) {
//        	mTicker.clearAnimation();
//        	mTicker.setAnimation(mFadeInAnimation);
//        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {    	
    	boolean result = false;
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		result = true;
    		showDialog(QUIT_GAME_DIALOG);
    	} else if (keyCode == KeyEvent.KEYCODE_MENU) {
    		result = true;
    	}
        return result;
    }
    
//    @Override
//	protected Dialog onCreateDialog(int id) {
//    	Dialog dialog;
//		if (id == WHATS_NEW_DIALOG) {
//			dialog = new AlertDialog.Builder(this)
//            .setTitle(R.string.whats_new_dialog_title)
//            .setPositiveButton(R.string.whats_new_dialog_ok, null)
//            .setMessage(R.string.whats_new_dialog_message)
//            .create();
//		} else {
//			dialog = super.onCreateDialog(id);
//		}
//		return dialog;
//	}
    
    // FIXME Create custom Quit Game Dialog
    @Override
    protected Dialog onCreateDialog(int id) {    	
        Dialog dialog = null;
        if (id == QUIT_GAME_DIALOG) {
        	
            dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.quit_game_dialog_title)
                .setPositiveButton(R.string.quit_game_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	finish();
                    }
                })
                .setNegativeButton(R.string.quit_game_dialog_cancel, null)
                .setMessage(R.string.quit_game_dialog_message)
                .create();
        }
        return dialog;
    }

	protected class StartLevelSelectActivityAfterAnimation implements Animation.AnimationListener {
        private Intent mIntent;
        
        StartLevelSelectActivityAfterAnimation(Intent intent) {
            mIntent = intent;
        }  

        public void onAnimationEnd(Animation animation) {        	
        	mPlayGameButton.setVisibility(View.INVISIBLE);
        	mPlayGameButton.clearAnimation();
//        	mContinueButton.setVisibility(View.INVISIBLE);
//        	mContinueButton.clearAnimation();
        	mOptionsButton.setVisibility(View.INVISIBLE);
        	mOptionsButton.clearAnimation();
//        	mBackground.setVisibility(View.INVISIBLE);
//        	mBackground.clearAnimation();
            startActivity(mIntent);            
        }

        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
        }
    }
}
