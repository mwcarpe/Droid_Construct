/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;



/**
 * Objects that populate the level
 *
 */
public class LevelObjects extends AllocationGuard {
	final private static int MAX_LOCATIONS = 1000;
	
	public int [] objectGroup;
	
	public int [] objectType;
	
	public Vector3 [] objectLocation;
	
//	public Vector3 [] objectOffset;
	
    public LevelObjects() {
        super();
        
        /* TODO Is it necessary to initialize objectGroup and objectType with 
         * For Statement? Seems heavy and unnecessary. Test. */
        objectGroup = new int[MAX_LOCATIONS];
        for(int i = 0; i < MAX_LOCATIONS; i++) {
        	objectGroup[i] = -1;
        }
        
        objectType = new int[MAX_LOCATIONS];
        for(int i = 0; i < MAX_LOCATIONS; i++) {
        	objectType[i] = -1;
        }
        
        objectLocation = new Vector3[MAX_LOCATIONS];
        
//        objectOffset = new Vector3[MAX_LOCATIONS];
    }
    
    public LevelObjects(int maxLocations) {
        super();
        
        objectGroup = new int[maxLocations];
        for(int i = 0; i < maxLocations; i++) {
        	objectGroup[i] = -1;
        }
        
        objectType = new int[maxLocations];
        for(int i = 0; i < maxLocations; i++) {
        	objectType[i] = -1;
        }
        
        objectLocation = new Vector3[maxLocations];
        
//        objectOffset = new Vector3[maxLocations];
    }
}
