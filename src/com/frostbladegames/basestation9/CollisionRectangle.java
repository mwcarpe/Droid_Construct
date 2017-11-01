/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;



/** 
 * A rectangle class. 
 * point[0] = backLeft;
 * point[1] = frontLeft;
 * point[2] = frontRight;
 * point[3] = backRight;
 *  */
public class CollisionRectangle {
	public float [] pointX;
	public float [] pointZ;
	
	public CollisionRectangle() {
		pointX = new float[4];
		pointZ = new float[4];
	}
}
