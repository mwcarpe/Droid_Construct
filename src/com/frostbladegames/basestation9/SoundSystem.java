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

import java.util.Comparator;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


public class SoundSystem extends BaseObject {
    private static final int MAX_STREAMS = 16;
//    private static final int MAX_STREAMS = 8;
//    private static final int MAX_STREAMS = 20;
    private static final int MAX_SOUNDS = 32;
//    private static final int MAX_SOUNDS = 64;
    private static final SoundComparator sSoundComparator = new SoundComparator();
    
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_MUSIC = 3;

    private SoundPool mSoundPool;
    private FixedSizeArray<Sound> mSounds;
    private Sound mSearchDummy;
    private boolean mSoundEnabled;
    private int[] mLoopingStreams;
    
    public SoundSystem() {
        super();
        
    	if (GameParameters.debug) {
            Log.i("GameFlow", "SoundSystem <constructor>");	
    	}
        
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        // FIXME Why is sSoundComparator and mSearchDummy required?
        mSounds = new FixedSizeArray<Sound>(MAX_SOUNDS, sSoundComparator);
        mSearchDummy = new Sound();
        mLoopingStreams = new int[MAX_STREAMS];
        for (int x = 0; x < mLoopingStreams.length; x++) {
        	mLoopingStreams[x] = -1;
        }
        mSoundEnabled = true;
    }
    
    @Override
    public void reset() {
    	if (GameParameters.debug) {
        	Log.i("Object", "SoundSystem reset()");	
    	}
    	
        mSoundPool.release();
        mSounds.clear();
        mSoundEnabled = true;
        for (int x = 0; x < mLoopingStreams.length; x++) {
        	mLoopingStreams[x] = -1;
        }
    }

    public Sound load(int resource) {        
        final int index = findSound(resource);
        
        Sound result = null;
        if (index < 0) {
            // new sound
        	Context context = sSystemRegistry.gameObjectFactory.context;
            if (context != null) {
//           if (sSystemRegistry.contextParameters != null) {
//               Context context = sSystemRegistry.contextParameters.context;
               result = new Sound();
               result.resource = resource;
               result.soundId = mSoundPool.load(context, resource, 1);
               mSounds.add(result);
               mSounds.sort(false);
           }
        } else {
            result = mSounds.get(index);
        }
        
        return result;
    }
    
    synchronized public final int play(Sound sound, boolean loop, int priority) {
//    	Log.i("Sound", "SoundSystem play(sound, loop, priority)");
    	
    	int stream = -1;
    	if (mSoundEnabled) {
    		stream = mSoundPool.play(sound.soundId, 1.0f, 1.0f, priority, loop ? -1 : 0, 1.0f);
    		if (loop) {
    			addLoopingStream(stream);
    		}
    	} 
    	
    	return stream;
    }
    
    synchronized public final int play(Sound sound, boolean loop, int priority, float volume, float rate) {
//    	Log.i("Sound", "SoundSystem play(sound, loop, priority, volume, rate)");
    	
    	int stream = -1;
    	if (mSoundEnabled) {
    		stream = mSoundPool.play(sound.soundId, volume, volume, priority, loop ? -1 : 0, rate);
    		if (loop) {
    			addLoopingStream(stream);
    		}
    	} 
    	
    	return stream;
    }
    
    public final void stop(int stream) {
        mSoundPool.stop(stream);
        removeLoopingStream(stream);
    }
    
    public final void pause(int stream) {
        mSoundPool.pause(stream);
    }
    
    public final void resume(int stream) {
       mSoundPool.resume(stream);
    }
    
    // HACK: There's no way to pause an entire sound pool, but if we
    // don't do something when our parent activity is paused, looping
    // sounds will continue to play.  Rather than reproduce all the bookkeeping
    // that SoundPool does internally here, I've opted to just pause looping
    // sounds when the Activity is paused.
    public void pauseAll() {
    	final int count = mLoopingStreams.length;
    	for (int x = 0; x < count; x++) {
    		if (mLoopingStreams[x] >= 0) {
    			pause(mLoopingStreams[x]);
    		}
    	}
    }
    
    private void addLoopingStream(int stream) {
    	final int count = mLoopingStreams.length;
    	for (int x = 0; x < count; x++) {
    		if (mLoopingStreams[x] < 0) {
    			mLoopingStreams[x] = stream;
    			break;
    		}
    	}
    }
    
    private void removeLoopingStream(int stream) {
    	final int count = mLoopingStreams.length;
    	for (int x = 0; x < count; x++) {
    		if (mLoopingStreams[x] == stream) {
    			mLoopingStreams[x] = -1;
    			break;
    		}
    	}
    }
    
    private final int findSound(int resource) {
        mSearchDummy.resource = resource;
        return mSounds.find(mSearchDummy, false);
    }

	synchronized public final void setSoundEnabled(boolean soundEnabled) {
		mSoundEnabled = soundEnabled;
	}
	
	public final boolean getSoundEnabled() {
		return mSoundEnabled;
	}
	
    public class Sound extends AllocationGuard {
        public int resource;
        public int soundId;
    }
    
    /** Comparator for sounds. */
    private static final class SoundComparator implements Comparator<Sound> {
        public int compare(final Sound object1, final Sound object2) {
            int result = 0;
            if (object1 == null && object2 != null) {
                result = 1;
            } else if (object1 != null && object2 == null) {
                result = -1;
            } else if (object1 != null && object2 != null) {
                result = object1.resource - object2.resource;
            }
            return result;
        }
    }

    
}