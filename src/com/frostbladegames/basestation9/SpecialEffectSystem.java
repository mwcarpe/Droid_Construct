/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;

import com.frostbladegames.basestation9.GameObjectGroups.Type;

public class SpecialEffectSystem extends BaseObject {
	private static final int MAX_SPECIAL_EFFECT_OBJECTS = 16;	// Max 16 Special Effect Objects
//	private static final float INCREMENT_VALUE = 0.25f;
//    private static final int SPECIAL_EFFECT_STEP_INTERVAL = 8;
	
//	private int [] mAnimationFrameNum = new int[MAX_SPECIAL_EFFECT_OBJECTS];
	
	private FixedSizeArray<AnimationSet> mActiveAnimationSets;
	private FixedSizeArray<AnimationSet> mInactiveExplosionAnimationSets;
	private FixedSizeArray<AnimationSet> mInactiveElectricRingAnimationSets;
	private FixedSizeArray<AnimationSet> mInactiveElectricityAnimationSets;
	private FixedSizeArray<AnimationSet> mInactiveExplosionLargeAnimationSets;
	private FixedSizeArray<AnimationSet> mInactiveExplosionRingAnimationSets;
	private FixedSizeArray<AnimationSet> mInactiveTeleportRingAnimationSets;
	
	public SpecialEffectSystem() {
	    super();
	    
    	if (GameParameters.debug) {
            Log.i("GameFlow", "SpecialEffectSystem <constructor>");	
    	}
	    
	    mActiveAnimationSets = new FixedSizeArray<AnimationSet>(MAX_SPECIAL_EFFECT_OBJECTS);
	    
//	    reset();	// note: reset() here causes crash
	}
	
	@Override
	public void reset() {
		mActiveAnimationSets = null;
		mInactiveExplosionAnimationSets = null;
		mInactiveElectricRingAnimationSets = null;
		mInactiveElectricityAnimationSets = null;
		mInactiveExplosionLargeAnimationSets = null;
		mInactiveExplosionRingAnimationSets = null;
		mInactiveTeleportRingAnimationSets = null;
////		mActiveAnimationSets.clear();
////		mInactiveExplosionAnimationSets.clear();
////		mInactiveElectricRingAnimationSets.clear();
////		mInactiveElectricityAnimationSets.clear();
////		mInactiveExplosionLargeAnimationSets.clear();
////		mInactiveExplosionRingAnimationSets.clear();
	}
	
//	public void clear() {
//		mActiveAnimationSets.clear();
//		mInactiveExplosionAnimationSets.clear();
//		mInactiveElectricRingAnimationSets.clear();
//		mInactiveElectricityAnimationSets.clear();
//		mInactiveExplosionLargeAnimationSets.clear();
//		mInactiveExplosionRingAnimationSets.clear();
//	}
	
	@Override
//	public void update(float timeDelta, BaseObject parent, boolean gamePause) {
	public void update(float timeDelta, BaseObject parent) {
//		Log.i("Loop", "SpecialEffectSystem update()");
		
		final int totalActiveAnimationSets = mActiveAnimationSets.getCount();
		
        if (totalActiveAnimationSets > 0) {
    		Object[] activeAnimationSets = mActiveAnimationSets.getArray();
//    		AnimationSet[] activeAnimationSets = (AnimationSet[])mActiveAnimationSets.getArray();
            int count = 0;
            int index = 0;
        
            while (count < totalActiveAnimationSets) {
//	        for (int i = 0; i < totalActiveAnimationSets; i++) {
	        	AnimationSet animationSet = (AnimationSet)activeAnimationSets[index];
	        	final int totalFrames = animationSet.totalFrames;
	        	final int currentFrame = animationSet.currentFrame;
	        	final int totalIncrements = animationSet.totalIncrements;
	        	final int currentIncrement = animationSet.currentIncrement;
	        	final int incrementMultiplier = animationSet.incrementMultiplier;
//	        	final int total = animationSet.totalFrames;
//	        	final int frame = animationSet.currentFrame;
//	        	final float increment = animationSet.currentIncrement;
////	        	final int increment = animationSet.currentIncrement;
////	        	GameObject gameObject = animationSet.getAnimationFrame(frame);
////	//        	final int total = activeAnimationSets[i].totalFrames;
////	//        	final int frame = activeAnimationSets[i].currentFrame;
////	//        	final int increment = activeAnimationSets[i].currentIncrement;
	        	
        		GameObject gameObject = animationSet.getAnimationFrame(animationSet.currentFrame - 1);
        		
        		// Enable SpecialEffect DynamicCollisionComponent
        		gameObject.currentPosition.set(animationSet.position);
//        		gameObject.update(timeDelta, this, gamePause);
        		gameObject.update(timeDelta, this);
        		
        	    final RenderSystem render = sSystemRegistry.renderSystem;
        		render.scheduleForDraw(gameObject.drawableDroid, animationSet.position, null, 0.0f, 0, true, gameObject.gameObjectId);
	        	
	        	if (currentIncrement >= totalIncrements) {
//	        	if (frame > total) {
	    			mActiveAnimationSets.moveToLast(index);
	            	AnimationSet inactiveAnimationSet = mActiveAnimationSets.removeLast();
	            	
        			if (inactiveAnimationSet != null) {
        				inactiveAnimationSet.currentFrame = 1;
        				inactiveAnimationSet.currentIncrement = 1;
//        				inactiveAnimationSet.currentIncrement = 0.0f; 
        				
		              	switch(inactiveAnimationSet.type) {
	//	              	switch(activeAnimationSets[index].type) {
		        		case EXPLOSION:
		            		mInactiveExplosionAnimationSets.add(inactiveAnimationSet);
		        			break;
		        			
		        		case ELECTRIC_RING:
	            			mInactiveElectricRingAnimationSets.add(inactiveAnimationSet);
	            			break;
	            			
		        		case ELECTRICITY:
		        			mInactiveElectricityAnimationSets.add(inactiveAnimationSet);
	            			break;
	            			
		        		case EXPLOSION_LARGE:
		        			mInactiveExplosionLargeAnimationSets.add(inactiveAnimationSet);
	            			break;
	            			
		        		case EXPLOSION_RING:
		        			mInactiveExplosionRingAnimationSets.add(inactiveAnimationSet);
	            			break;
	            			
		        		case TELEPORT_RING:
		        			mInactiveTeleportRingAnimationSets.add(inactiveAnimationSet);
	            			break;
		            		
		            	default:
		            		break;
		              	}
        			} else {
        				Log.e("SpecialEffectSystem", "update() animationSet = NULL!");
        			}
	        	} else if (currentIncrement >= (currentFrame * incrementMultiplier)) {
//	        	} else if ((increment * 0.99f) < frame) {	// Allow 0.01f tolerance for float
//	        	} else if (increment <= (frame * SPECIAL_EFFECT_STEP_INTERVAL)) {
	        		// Keep current Frame value. Increment only.
	        		animationSet.currentFrame++;
	        		animationSet.currentIncrement++;
//	        		animationSet.currentIncrement += INCREMENT_VALUE;
	        		
//	        		GameObject gameObject = animationSet.getAnimationFrame(animationSet.currentFrame - 1);
//	        		
//	        	    final RenderSystem render = sSystemRegistry.renderSystem;
//	        		render.scheduleForDraw(gameObject.drawableDroid, animationSet.position, null, 0.0f, 0, true, gameObject.gameObjectId);
	        		
                	index++;
	        	} else {
	        		animationSet.currentIncrement++;
//	        		animationSet.currentIncrement += INCREMENT_VALUE;
//	        		animationSet.currentFrame++;
	        		
//	        		GameObject gameObject = animationSet.getAnimationFrame(animationSet.currentFrame - 1);
//	        		
//	        	    final RenderSystem render = sSystemRegistry.renderSystem;
//	        		render.scheduleForDraw(gameObject.drawableDroid, animationSet.position, null, 0.0f, 0, true, gameObject.gameObjectId);
	        		
//	        		if (animationSet.currentFrame <= total) {
//		        		GameObject gameObject = animationSet.getAnimationFrame(animationSet.currentFrame - 1);
//		        		
//		        	    final RenderSystem render = sSystemRegistry.renderSystem;
//		        		render.scheduleForDraw(gameObject.drawableDroid, animationSet.position, null, 0.0f, 0, true, gameObject.gameObjectId);
//	        		}
	        		
                	index++;
	        	}
	        	count++;
	        }
        }
	}
	
    public void activateAnimationSet(Type specialEffectType, Vector3 gameObjectPosition) {
//    	AnimationSet animationSet;
      	
      	switch(specialEffectType) {
    		case EXPLOSION:
    			if (mInactiveExplosionAnimationSets != null) {
    				AnimationSet animationSet = mInactiveExplosionAnimationSets.removeLast();
        			
        			if (animationSet != null) {
        				animationSet.currentFrame = 1;
        				animationSet.currentIncrement = 1;
//        				animationSet.currentIncrement = 0.0f;
//        				animationSet.type = specialEffectType;
            			animationSet.position.set(gameObjectPosition);
            			
            			mActiveAnimationSets.add(animationSet);
            			
        			} else {
        				Log.e("SpecialEffectSystem", "activateAnimationSet() animationSet = NULL!");
        			}
    			} else {
    				Log.e("SpecialEffectSystem", "activateAnimationSet() mInactiveExplosionAnimationSets = NULL!");
    			}
    			
    			break;
    			
    		case ELECTRIC_RING:
    			if (mInactiveElectricRingAnimationSets != null) {
    				AnimationSet animationSet = mInactiveElectricRingAnimationSets.removeLast();
        			
        			if (animationSet != null) {
        				animationSet.currentFrame = 1;
        				animationSet.currentIncrement = 1;
//        				animationSet.currentIncrement = 0.0f;
//        				animationSet.type = specialEffectType;
            			animationSet.position.set(gameObjectPosition);
            			
            			mActiveAnimationSets.add(animationSet);
            			
        			} else {
        				Log.e("SpecialEffectSystem", "activateAnimationSet() animationSet = NULL!");
        			}
    			} else {
    				Log.e("SpecialEffectSystem", "activateAnimationSet() mInactiveElectricRingAnimationSets = NULL!");
    			}
    			
    			break;
    			
    		case ELECTRICITY:
    			if (mInactiveElectricityAnimationSets != null) {
    				AnimationSet animationSet = mInactiveElectricityAnimationSets.removeLast();
        			
        			if (animationSet != null) {
        				animationSet.currentFrame = 1;
        				animationSet.currentIncrement = 1;
//        				animationSet.currentIncrement = 0.0f;
//        				animationSet.type = specialEffectType;
            			animationSet.position.set(gameObjectPosition);
            			
            			mActiveAnimationSets.add(animationSet);
            			
        			} else {
        				Log.e("SpecialEffectSystem", "activateAnimationSet() animationSet = NULL!");
        			}
    			} else {
    				Log.e("SpecialEffectSystem", "activateAnimationSet() mInactiveElectricityAnimationSets = NULL!");
    			}
    			
    			break;
    			
    		case EXPLOSION_LARGE:
    			if (mInactiveExplosionLargeAnimationSets != null) {
    				AnimationSet animationSet = mInactiveExplosionLargeAnimationSets.removeLast();
        			
        			if (animationSet != null) {
        				animationSet.currentFrame = 1;
        				animationSet.currentIncrement = 1;
//        				animationSet.currentIncrement = 0.0f;
//        				animationSet.type = specialEffectType;
            			animationSet.position.set(gameObjectPosition);
            			
            			mActiveAnimationSets.add(animationSet);
            			
        			} else {
        				Log.e("SpecialEffectSystem", "activateAnimationSet() animationSet = NULL!");
        			}
    			} else {
    				Log.e("SpecialEffectSystem", "activateAnimationSet() mInactiveExplosionLargeAnimationSets = NULL!");
    			}
    			
    			break;
    			
    		case EXPLOSION_RING:
    			if (mInactiveExplosionRingAnimationSets != null) {
    				AnimationSet animationSet = mInactiveExplosionRingAnimationSets.removeLast();
        			
        			if (animationSet != null) {
        				animationSet.currentFrame = 1;
        				animationSet.currentIncrement = 1;
//        				animationSet.currentIncrement = 0.0f;
//        				animationSet.type = specialEffectType;
            			animationSet.position.set(gameObjectPosition);
            			
            			mActiveAnimationSets.add(animationSet);
            			
        			} else {
        				Log.e("SpecialEffectSystem", "activateAnimationSet() animationSet = NULL!");
        			}
    			} else {
    				Log.e("SpecialEffectSystem", "activateAnimationSet() mInactiveExplosionRingAnimationSets = NULL!");
    			}
    			
    			break;
    			
    		case TELEPORT_RING:
    			if (mInactiveTeleportRingAnimationSets != null) {
    				AnimationSet animationSet = mInactiveTeleportRingAnimationSets.removeLast();
        			
        			if (animationSet != null) {
        				animationSet.currentFrame = 1;
        				animationSet.currentIncrement = 1;
//        				animationSet.currentIncrement = 0.0f;
//        				animationSet.type = specialEffectType;
            			animationSet.position.set(gameObjectPosition);
            			
            			mActiveAnimationSets.add(animationSet);
            			
        			} else {
        				Log.e("SpecialEffectSystem", "activateAnimationSet() animationSet = NULL!");
        			}
    			} else {
    				Log.e("SpecialEffectSystem", "activateAnimationSet() mInactiveTeleportRingAnimationSets = NULL!");
    			}
    			
    			break;
        		
        	default:
        		break;
  		}
    }
	
    public void setInactiveAnimationSet(Type specialEffectType, int maxSpecialEffectSets, FixedSizeArray<AnimationSet> animationSets) {
      	switch(specialEffectType) {
		case EXPLOSION:
			if (mInactiveExplosionAnimationSets == null) {
				mInactiveExplosionAnimationSets = new FixedSizeArray<AnimationSet>(maxSpecialEffectSets);
				mInactiveExplosionAnimationSets = animationSets;
			} else {
				// Already set, so ignore additional spawn()
			}
			break;
			
		case ELECTRIC_RING:
			if (mInactiveElectricRingAnimationSets == null) {
				mInactiveElectricRingAnimationSets = new FixedSizeArray<AnimationSet>(maxSpecialEffectSets);
				mInactiveElectricRingAnimationSets = animationSets;
			} else {
				// Already set, so ignore additional spawn()
			}
			break;
			
		case ELECTRICITY:
			if (mInactiveElectricityAnimationSets == null) {
				mInactiveElectricityAnimationSets = new FixedSizeArray<AnimationSet>(maxSpecialEffectSets);
				mInactiveElectricityAnimationSets = animationSets;
			} else {
				// Already set, so ignore additional spawn()
			}
			break;
			
		case EXPLOSION_LARGE:
			if (mInactiveExplosionLargeAnimationSets == null) {
				mInactiveExplosionLargeAnimationSets = new FixedSizeArray<AnimationSet>(maxSpecialEffectSets);
				mInactiveExplosionLargeAnimationSets = animationSets;
			} else {
				// Already set, so ignore additional spawn()
			}
			break;
			
		case EXPLOSION_RING:
			if (mInactiveExplosionRingAnimationSets == null) {
				mInactiveExplosionRingAnimationSets = new FixedSizeArray<AnimationSet>(maxSpecialEffectSets);
				mInactiveExplosionRingAnimationSets = animationSets;
			} else {
				// Already set, so ignore additional spawn()
			}
			break;
			
		case TELEPORT_RING:
			if (mInactiveTeleportRingAnimationSets == null) {
				mInactiveTeleportRingAnimationSets = new FixedSizeArray<AnimationSet>(maxSpecialEffectSets);
				mInactiveTeleportRingAnimationSets = animationSets;
			} else {
				// Already set, so ignore additional spawn()
			}
			break;
    		
    	default:
    		break;
		}
    }
}
