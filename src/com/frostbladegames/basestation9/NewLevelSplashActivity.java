/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import com.frostbladegames.basestation9.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;


/**
 * Splash Graphic before starting New Level
 *
 */
public class NewLevelSplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.newlevelsplashscreen);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	finish();
    	
    	return true;
    }
}
