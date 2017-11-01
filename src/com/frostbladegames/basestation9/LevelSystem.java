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

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * Manages information about the current level, including setup, deserialization, and tear-down.
 */
public class LevelSystem extends BaseObject {
//    public int mWidthInTiles;
//    public int mHeightInTiles;
//    public int mTileWidth;
//    public int mTileHeight;
    
    // FIXME Legacy Replica code. Objects should be private, not public. Review and delete.
//    public GameObject mBackgroundObject;
    public ObjectManager mRoot;
//    private byte[] mWorkspaceBytes;
//    private TiledWorld mSpawnLocations;
    private GameFlowEvent mGameFlowEvent;
//    private int mAttempts;
    private LevelTree.Level mCurrentLevel;
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
    private LevelObjects mLevelObjects;
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
    
    public LevelSystem() {
        super();
        
    	  if (GameParameters.debug) {
    	        Log.i("GameFlow", "LevelSystem <constructor>");  
    	  }
        
//        mWorkspaceBytes = new byte[4];
        mGameFlowEvent = new GameFlowEvent();
        reset();
    }
    
    @Override
    public void reset() {
    	if (GameParameters.debug) {
        	Log.i("Object", "LevelSystem reset()");	
    	}
    	
    	if (mRoot != null) {
    		mRoot = null;
    	}
//        if (mBackgroundObject != null && mRoot != null) {
//            mBackgroundObject.removeAll();
//            mBackgroundObject.commitUpdates();
//            mRoot.remove(mBackgroundObject);
//            mBackgroundObject = null;
//            mRoot = null;
//        }

//        mSpawnLocations = null;
//        mAttempts = 0;

        mCurrentLevel = null;
    }
    
//    public float getLevelWidth() {
//        return mWidthInTiles * mTileWidth;
//    }
//    
//    public float getLevelHeight() {
//        return mHeightInTiles * mTileHeight;
//    }
    
    // XXX Not used by Replica, so disabled in Droid Construct. Use sendGameEvent() only.
//    public void sendRestartEvent() {
//        mGameFlowEvent.post(GameFlowEvent.EVENT_RESTART_LEVEL, 0,
//        		sSystemRegistry.gameObjectFactory.context);
////        mGameFlowEvent.post(GameFlowEvent.EVENT_RESTART_LEVEL, 0,
////                sSystemRegistry.contextParameters.context);
//    }
//    
//    public void sendNextLevelEvent() {
//    	mGameFlowEvent.post(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0,
//    			sSystemRegistry.gameObjectFactory.context);
////        mGameFlowEvent.post(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0,
////                sSystemRegistry.contextParameters.context);
//    }
    
    public void sendGameEvent(int type, int index, boolean immediate) {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "LevelSystem sendGameEvent()");	
    	}
    	
        if (immediate) {
//        	mGameFlowEvent.postImmediate(type, index, ConstructActivity.getAppContext());
        	mGameFlowEvent.postImmediate(type, index, sSystemRegistry.gameObjectFactory.context);
        	
        } else {
//        	mGameFlowEvent.post(type, index, ConstructActivity.getAppContext());
        	mGameFlowEvent.post(type, index, sSystemRegistry.gameObjectFactory.context);
        }
//        if (immediate) {
//        	mGameFlowEvent.postImmediate(type, index,
//                sSystemRegistry.contextParameters.context);
//        } else {
//        	mGameFlowEvent.post(type, index,
//                    sSystemRegistry.contextParameters.context);
//        }
    }
    
    /**
     * Loads a level from a binary file.  The file consists of several layers, including background
     * tile layers and at most one collision layer.  Each layer is used to bootstrap related systems
     * and provide them with layer data.
     * @param level  The level information.
     * @param stream  The input stream for the level file resource.
     * @param root  The ObjectManager root.
     * @return
     */
    public boolean loadLevel(LevelTree.Level level, InputStream stream, ObjectManager root) {
//    public boolean loadLevel(ObjectManager root, int levelId, Context context) {
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "LevelSystem loadLevel()");	
    	}
    	
        mCurrentLevel = level;
    	
    	// TODO Replace code with .dat file DataInputStream method
    	mRoot = root;
    	
    	int numLocations = 0;
    	
		try {
			List<String> oLines = null;
			StringTokenizer oToken;

			// TODO In LevelSystem() instantiate, create int Array of levels in raw directory
			// Reader for the Location File
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			// Read Locations
			String line = reader.readLine();
			while(line != null) {
				if(line.startsWith("#") || line.trim().equals("")) {
					line = reader.readLine();
					continue;
				}
				
				if(oLines == null) {
					oLines = new ArrayList<String>();
				}
				
				if(line != null) {
					oLines.add(line);
					numLocations++;
					line = reader.readLine();
					continue;
				}
				
				if(line == null) {
					break;
				} else {
					continue;
				}
			}
			
			reader.close();
			
			if (numLocations > 0) {
				mLevelObjects = new LevelObjects(numLocations);
		    	if (GameParameters.debug) {
		    		Log.i("GameFlow", "LevelSystem numLocations = " + numLocations);	
		    	}
				
				/* TODO Much simpler to add another int object type identifier in .dat file 
				 * (e.g. background = 1, droid = 2, enemy = 3). Then won't have to assume only 1 background, 1 droid. */
				float x = 0.0f;
				float y = 0.0f;
				float z = 0.0f;
				float r = 0.0f;
				
				// Load Object Locations to LevelObjects
				for(int i = 0; i < numLocations; i++) {
					oToken = new StringTokenizer(oLines.get(i));
					mLevelObjects.objectGroup[i] = Integer.valueOf(oToken.nextToken());
					mLevelObjects.objectType[i] = Integer.valueOf(oToken.nextToken());
					x = Float.valueOf(oToken.nextToken());
					y = Float.valueOf(oToken.nextToken());
					z = Float.valueOf(oToken.nextToken());
					r = Float.valueOf(oToken.nextToken());
					
//					Log.i("GameFlow", "LevelSystem objectGroup, objectType = " + mLevelObjects.objectGroup[i] + ", " + mLevelObjects.objectType[i]);
//					Log.i("GameFlow", "LevelSystem x,y,z,r = " + x + ", " + y + ", " + z + ", " + r);
					
					Vector3 location = new Vector3();					
					location.set(x, y, z, r);

					mLevelObjects.objectLocation[i] = location;
				}
			} else {
				mLevelObjects = null;
			}
		} catch(Exception e) {
			Log.e("LevelSystem", "Level could not load successfully", e);
			return false;
		}
    	
    	/* TODO Called via Game, but no check on return value. 
    	   Re-confirm that success true or false here doesn't matter. */
        boolean success = true;

        return success;
    }
    
    /**
     * Loads level00_splashscreen from a binary file
     * @param level  The level information.
     * @param stream  The input stream for the level file resource.
     * @param root  The ObjectManager root.
     * @return
     */
    public boolean loadLevelSplashScreen(Context context, int levelId, ObjectManager root) {
    	
    	// TODO Replace code with .dat file DataInputStream method
    	mRoot = root;
    	
    	int numLocations = 0;
    	
		try {
			List<String> oLines = null;
			StringTokenizer oToken;

			// TODO In LevelSystem() instantiate, create int Array of levels in raw directory
			// Reader for the Location File
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(context.getResources().openRawResource(levelId)));
//			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			// Read Locations
			String line = reader.readLine();
			while(line != null) {
				if(line.startsWith("#") || line.trim().equals("")) {
					line = reader.readLine();
					continue;
				}
				
				if(oLines == null) {
					oLines = new ArrayList<String>();
				}
				
				if(line != null) {
					oLines.add(line);
					numLocations++;
					line = reader.readLine();
					continue;
				}
				
				if(line == null) {
					break;
				} else {
					continue;
				}
			}
			
			reader.close();
			
			if (numLocations > 0) {
				mLevelObjects = new LevelObjects(numLocations);
				
				/* TODO Much simpler to add another int object type identifier in .dat file 
				 * (e.g. background = 1, droid = 2, enemy = 3). Then won't have to assume only 1 background, 1 droid. */
				float x = 0.0f;
				float y = 0.0f;
				float z = 0.0f;
				float r = 0.0f;
				
				// Load Object Locations to LevelObjects
				for(int i = 0; i < numLocations; i++) {
					oToken = new StringTokenizer(oLines.get(i));
					mLevelObjects.objectGroup[i] = Integer.valueOf(oToken.nextToken());
					mLevelObjects.objectType[i] = Integer.valueOf(oToken.nextToken());
					x = Float.valueOf(oToken.nextToken());
					y = Float.valueOf(oToken.nextToken());
					z = Float.valueOf(oToken.nextToken());
					r = Float.valueOf(oToken.nextToken());
					
					Vector3 location = new Vector3();					
					location.set(x, y, z, r);

					mLevelObjects.objectLocation[i] = location;
				}
			} else {
				mLevelObjects = null;
			}
		} catch(Exception e) {
			Log.e("LevelSystem", "Level could not load successfully", e);
			return false;
		}
    	
    	/* TODO Called via Game, but no check on return value. 
    	   Re-confirm that success true or false here doesn't matter. */
        boolean success = true;

        return success;
    }
    
    public void spawnObjects(GL10 gl) {
//    public void spawnObjects(Context context) {    
    	if (GameParameters.debug) {
        	Log.i("GameFlow", "LevelSystem spawnObjects()");	
    	}
    	
        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        
        if (factory != null) {        	
        	GL11 gl11 = (GL11)gl;
            
        	if (mLevelObjects != null) {
                factory.spawnWorld(gl11, mLevelObjects);	
        	} else {
        		// FIXME Alert system to display alert to user, re-start level, and/or re-start game
        	}
            
        	// FIXME RE-ENABLE? I added this vs Replica. Should mLevelObjects be retained in memory for Level Re-Start?
//            mLevelObjects = null;
        }
    }

//    public void incrementAttemptsCount() {
//        mAttempts++;
//    }
//    
//    public int getAttemptsCount() {
//        return mAttempts;
//    }
    
    public LevelTree.Level getCurrentLevel() {
    	return mCurrentLevel;
    }
}
