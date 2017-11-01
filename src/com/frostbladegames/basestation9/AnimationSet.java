/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

import android.util.Log;
import java.util.Random;

import com.frostbladegames.basestation9.GameObjectGroups.Type;

/** 
 * Animation class. 
 * Set Total Animation Frames and Increment Multiplier for Increments per Animation Frame. 
 */
public class AnimationSet {
	// 4 Increments per Frame
//    private static final int INCREMENTS_PER_FRAME = 4;
	
	public int totalFrames;
	public int currentFrame;
	public int totalIncrements;
	public int currentIncrement;
//	public float currentIncrement;
	public int incrementMultiplier;
	
	public Type type;
	
	public Vector3 position;
	
    private FixedSizeArray<GameObject> mAnimationArray;
//    private FixedSizeArray<GameObject> mAnimationGameObjects;
    
	public AnimationSet() {
		
	}
	
	public AnimationSet(int totalAnimationFrames, int incrementAnimationMultiplier) {
//	public AnimationSet(int total) {
		totalFrames = totalAnimationFrames;
//		totalFrames = total;
		
		Random randomFrame = new Random();
		currentFrame = randomFrame.nextInt(totalAnimationFrames);
		// currentFrame range is 1 to 4, so discard random value = 0
		if (currentFrame == 0) {
			currentFrame = 1;
		}
//		currentFrame = 1;
		
		incrementMultiplier = incrementAnimationMultiplier;
		totalIncrements = totalFrames * incrementMultiplier;
		currentIncrement = 1;
//		currentIncrement = 0.0f;
		
		type = Type.INVALID;
		
		position = new Vector3();
		
		mAnimationArray = new FixedSizeArray<GameObject>(totalFrames);
//		mAnimationGameObjects = new FixedSizeArray<GameObject>(total);
	}
	
	public AnimationSet(AnimationSet animationSet) {
		this.totalFrames = animationSet.totalFrames;
		
		Random randomFrame = new Random();
		currentFrame = randomFrame.nextInt(this.totalFrames);
		// currentFrame range is 1 to 4, so discard random value = 0
		if (currentFrame == 0) {
			currentFrame = 1;
		}
//		currentFrame = 1;
			
		this.incrementMultiplier = animationSet.incrementMultiplier;
		this.totalIncrements = animationSet.totalIncrements;
		currentIncrement = 1;
			
		this.type = animationSet.type;
			
		position = new Vector3();
			
		mAnimationArray = animationSet.getAnimationArray();
//		mAnimationArray = animationSet.getAnimationGameObjectArray();
	}
	
//	public void incrementFrame() {
//		currentFrame++;
//	}
	
	public FixedSizeArray<GameObject> getAnimationArray() {
//	public FixedSizeArray<GameObject> getAnimationGameObjectArray() {
		return mAnimationArray;
	}
	
	/** frame index = currentFrame - 1 */
	public GameObject getAnimationFrame(int frame) {
//		final GameObject[] gameObjectArray = (GameObject[])mAnimationGameObjects.getArray();
		
		return mAnimationArray.get(frame);
//		return gameObjectArray[frame];
	}
	
	public void addAnimationFrame(GameObject gameObject) {
		mAnimationArray.add(gameObject);
	}
	
	public void addAnimationArray(FixedSizeArray<GameObject> animationArray) {
		mAnimationArray = animationArray;
	}
}
