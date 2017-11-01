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

import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

public final class LevelTree {
    public static final ArrayList<LevelGroup> levelGroups = new ArrayList<LevelGroup>();
    
    private static boolean mLoaded = false;
    
    public static final void loadLevelTree(int resource, Context context) {
//    public static final void loadLevelTree(int resource, int difficulty, Context context) {
//    public static final void loadLevelTree(int resource, Context context) {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "LevelTree loadLevelTree()");	
    	}
    	
        if (levelGroups.size() > 0) {
        	// already loaded
        	return;
        }
        
    	XmlResourceParser parser = context.getResources().getXml(resource);
        
    	// FIXME Delete LevelGroup. Not required. Only work with Level. Change Level to straight Array (e.g. levels[])?
    	levelGroups.clear();
        
        LevelGroup currentGroup = null;
        Level currentLevel = null;
        
        try { 
            int eventType = parser.getEventType(); 
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                	if (parser.getName().equals("group")) {
                		currentGroup = new LevelGroup();
                		levelGroups.add(currentGroup);
                		currentLevel = null;
                		
//        				Log.i("GameFlow", "LevelTree loadLevelTree() AFTER levelGroups.size = " + levelGroups.size());
                	}
                	
                    if (parser.getName().equals("level") && currentGroup != null) {
                    	int levelResource = 0;
                    	String titleString = null;
                    	boolean restartable = true;
                    	boolean showWaitMessage = false;
                        for(int i=0; i < parser.getAttributeCount(); i++) {
                    		if (parser.getAttributeName(i).equals("restartable")) {
                    			if (parser.getAttributeValue(i).equals("false")) {
                    				restartable = false;
                    			}
                    		} else if (parser.getAttributeName(i).equals("waitmessage")) {
                    			if (parser.getAttributeValue(i).equals("true")) {
                    				showWaitMessage = true;
                    			}
                    		} else {
                                final int value = parser.getAttributeResourceValue(i, -1);
                                if (value != -1) {
                                	switch(GameParameters.difficulty) {
                                	case 0:
                                        if (parser.getAttributeName(i).equals("resourceEasy")) {                        				
                                            levelResource = value;
                                            
                                        	if (GameParameters.debug) {
                                				Log.i("GameFlow", "LevelTree loadLevelTree() resourceEasy = " + levelResource);	
                                        	}
                                        }
                                		break;
                                		
                                	case 1:
                                        if (parser.getAttributeName(i).equals("resourceMedium")) {                        				
                                            levelResource = value;
                                            
                                        	if (GameParameters.debug) {
                                				Log.i("GameFlow", "LevelTree loadLevelTree() resourceMedium = " + levelResource);	
                                        	}
                                        }
                                		break;
                                		
                                	case 2:
                                        if (parser.getAttributeName(i).equals("resourceHard")) {                        				
                                            levelResource = value;
                                            
                                        	if (GameParameters.debug) {
                                				Log.i("GameFlow", "LevelTree loadLevelTree() resourceHard = " + levelResource);	
                                        	}
                                        }
                                		break;
                                		
                                	default:
                                        if (parser.getAttributeName(i).equals("resourceMedium")) {                        				
                                            levelResource = value;
                                            
                                        	if (GameParameters.debug) {
                                				Log.i("GameFlow", "LevelTree loadLevelTree() resourceMedium (default) = " + levelResource);	
                                        	}
                                        }
                                		break;
                                	}
//                                    if (parser.getAttributeName(i).equals("resource")) {                        				
//                                        levelResource = value;
//                                        
//                        				Log.i("GameFlow", "LevelTree loadLevelTree() resource = " + levelResource);
//                                    }
                                    
                                    if (parser.getAttributeName(i).equals("title")) {
                                        titleString = context.getString(value);
                                        
//                        				Log.i("GameFlow", "LevelTree loadLevelTree() titleString = " + titleString);
                                    }
                                }
                    		}
                        	
                        } 

                        currentLevel = new Level(levelResource, titleString, restartable, showWaitMessage);
                        currentGroup.levels.add(currentLevel);
                    } 
                } 
                eventType = parser.next(); 
            } 
        } catch(Exception e) { 
                Log.e("LevelTree", e.getStackTrace().toString()); 
        } finally { 
            parser.close(); 
        } 
        mLoaded = true;
    }

	public static final void updateCompletedState(int levelRow, int completedLevels) {
    	if (GameParameters.debug) {
    		Log.i("GameFlow", "LevelTree updateCompletedState()");	
    	}
		
		final int rowCount = levelGroups.size();
		for (int x = 0; x < rowCount; x++) {
			final LevelGroup group = levelGroups.get(x);
			final int levelCount = group.levels.size();
			for (int y = 0; y < levelCount; y++) {
				final Level level = group.levels.get(y);
				if (x < levelRow) {
					level.completed = true;
				} else if (x == levelRow) {
					if ((completedLevels & (1 << y)) != 0) {
						level.completed = true;
					}
				} else {
					level.completed = false;
				}
			}
		}
	}

	public static final int packCompletedLevels(int levelRow) {
    	if (GameParameters.debug) {
    		Log.i("GameFlow", "LevelTree packCompletedLevels() levelRow = " + levelRow);	
    	}
		
		// FIXME 12/17/12 Hack. Fix.
		if(levelRow > 9) {
			levelRow = 9;
		}
		
		int completed = 0;
		
		if (mLoaded) {
//        if (levelGroups.size() > 0) {
    		final LevelGroup group = levelGroups.get(levelRow);
    		final int levelCount = group.levels.size();
    		
        	if (GameParameters.debug) {
        		Log.i("GameFlow", "LevelTree packCompletedLevels() levelCount = " + levelCount);	
        	}
    		
    		for (int y = 0; y < levelCount; y++) {
    			final Level level = group.levels.get(y);
    			if (level.completed) {
    				completed |= 1 << y;
    			}
    		}
        }

		return completed;
	}
	
    public static final Level get(int row, int index) {
    	return levelGroups.get(row).levels.get(index);
    }
    
    public static final void clearLevelTree() {
    	if (!levelGroups.isEmpty()) {
        	levelGroups.clear();
    	}
    	mLoaded = false;
    }

    public static final boolean isLoaded() {
    	return mLoaded;
    }
    
//    public static final void setNotLoaded() {
//    	mLoaded = false;
//    }
	
	public static boolean levelIsValid(int row, int index) {
		boolean valid = false;
		if (row >= 0 && row < levelGroups.size()) {
			final LevelGroup group = levelGroups.get(row);
			if (index >=0 && index < group.levels.size()) {
				valid = true;
			}
		}
		
		return valid;
	}
	
	public static boolean rowIsValid(int row) {
		boolean valid = false;
		if (row >= 0 && row < levelGroups.size()) {
			valid = true;
		}
		
		return valid;
	}
	
    public static class Level {
    	public int resource;
//    	// Added by FrostBlade Start
//    	public int difficulty;
//    	// Added by FrostBlade End
        public String name;
        public boolean completed;
        public boolean restartable;
        public boolean showWaitMessage;
        
        public Level(int levelId, String title, boolean restartOnDeath, boolean waitMessage) {
//        public Level(int levelId, int levelDifficulty, String title, boolean restartOnDeath, boolean waitMessage) {
//        public Level(int levelId, String title, boolean restartOnDeath, boolean waitMessage) {
            resource = levelId;
//        	// Added by FrostBlade Start
//            difficulty = levelDifficulty;
//            // Added by FrostBlade End
            name = title;
            completed = false;
            restartable = restartOnDeath;
            showWaitMessage = waitMessage;
        }
        
    }
    
	public static class LevelGroup {
		public ArrayList<Level> levels = new ArrayList<Level>();
	}
}
