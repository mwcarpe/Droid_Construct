/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import com.frostbladegames.basestation9.R;

import android.app.Activity;
import android.os.Bundle;


/**
 * Activity to Select Player for New Game
 */
public class NewGameActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame);
    }
}
