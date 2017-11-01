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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
//import com.frostbladegames.droidconstruct.GameObjectGroups.HitType;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;

import com.frostbladegames.basestation9.GameObjectGroups.BottomMoveType;
import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;

/**
 * GameObject defines any object that resides in the game world (character, background, special
 * effect, enemy, etc).  It is a collection of GameComponents which implement its behavior;
 * GameObjects themselves have no intrinsic behavior.  GameObjects are also "bags of data" that
 * components can use to share state (direct component-to-component communication is discouraged).
 * Fields are managed by GameComponents.
 */
public class GameObject extends ObjectManager {
// public class GameObject extends BaseObject {   // Needs to extend ObjectManager for GameComponents that are added
// public class GameObject extends PhasedObjectManager {
    private static final int DEFAULT_LIFE = 1;
//    private static final float DEFAULT_LIFE = 1.0f;
    private static final float COLLISION_SURFACE_DECAY_TIME = 0.3f;
    
    public boolean renderGameObject;
    
    // FIXME Do public Objects need to be changed to private for certain copy vs pointer use cases?
    /* Vector (Start Point) currentPosition, (Endpoint) bouncePosition, (Endpoint) fallPosition.
     * (Endpoint) movePosition and (Endpoint) firePosition maintained in InputSystem. */
    public Vector3 currentPosition;
    public Vector3 previousPosition;
    public Vector3 bouncePosition;
    public Vector3 previousSectionPosition;
    public Vector3 nextSectionPosition;
    
    public Vector3 laserReceiveGameObjectPosition;
    
    public LineSegment backgroundRadius;
    
    public float magnitude;
    public float xMoveMagnitude;
    public float yMoveMagnitude;
    public float zMoveMagnitude;
    public float bounceMagnitude;
    
    public float empSlowMagnitude;
    
//    // Replica original Vector Points
//    private Vector3 mVelocity;
//    private Vector3 mTargetVelocity;
//    private Vector3 mAcceleration;
//    private Vector3 mImpulse;
    
    public int gameObjectId;
    
    public boolean backgroundGroup;
  
    public boolean positionLocked;
    
    public boolean soundPlayed;
    
    // x,y,z Move for Droid, Platform
    public float xValueBeforeMove;
    public float xMoveDistance;
    public float yValueBeforeMove;
    public float yMoveDistance;
    public float zValueBeforeMove;
    public float zMoveDistance;
    
    // Test whether Elevator at Startpoint or Endpoint
    public boolean elevatorStartpoint;
    
    // Platform Type reference for Droid during state<Platform>()
    public Type platformType;
    
//    // Boolean check for Platform Group
//    public boolean platformCompleted;
////    public boolean elevatorCompleted;
    
    // Activation and Enemy Attack Radius
    public float activationRadius;
    public float attackRadius;
    
    public boolean destroyOnDeactivation;
    
    // XXX Previous Test
//    public boolean markedForDeath;
    
    // Attack and Receive Hit System
    public float attackHitDamage;
    public int hitPoints;
    public float lastReceivedHitTime;
    
    /* FIXME Not currently used. Could this be used in HitReactionComponent where cannot 
     * receive same hit type while invincible, but can receive a different hit type? */
    public CurrentState lastReceivedHitType;
    
    // FIXME Add attacker GameObject pointer, so only invincible vs that GameObject (usually after Bounce) for 1-3 sec, but not others
    public boolean invincible;
    public float invincibleTime;
    
    // Value setting for each GameObject
    public int killPoints;
    public int collectPoints;
    
    // Used for HitType Bounce only
    public boolean noBounceIfInvincible;
    
    // GameObject State
    public boolean gameObjectInactive;
    public boolean collisionAttackRadius;
    
    // Weapon States
    public boolean activeWeapon;
    public boolean inventoryWeapon;
    
//    private Vector3 mBackgroundCollisionNormal;
    
    public DrawableDroid drawableDroid;
    
    // GameObject initialState at beginning of loop and currentState at any point in loop
    public CurrentState initialState;
    public CurrentState currentState;
    public CurrentState previousState;

    public Group group;

    public Type type;
    
    public Type hitReactType;
    
    public BottomMoveType bottomMoveType;
    
    public GameObject() {
        super();

        currentPosition = new Vector3();
        previousPosition = new Vector3();
        bouncePosition = new Vector3();
        previousSectionPosition = new Vector3();
        nextSectionPosition = new Vector3();
        
        laserReceiveGameObjectPosition = new Vector3();
        
        backgroundRadius = new LineSegment();
        
        elevatorStartpoint = true;
        
//        mVelocity = new Vector3();
//        mTargetVelocity = new Vector3();
//        mAcceleration = new Vector3();
//        mImpulse = new Vector3();
        
//        mBackgroundCollisionNormal = new Vector3();
        
        reset();
    }
    
    public GameObject(GameObject gameObject) {
        super();
        
        renderGameObject = gameObject.renderGameObject;
        
        currentPosition = new Vector3(gameObject.currentPosition);
        previousPosition = new Vector3(gameObject.previousPosition);
        bouncePosition = new Vector3(gameObject.bouncePosition);
        previousSectionPosition = new Vector3(gameObject.previousSectionPosition);
        nextSectionPosition = new Vector3(gameObject.nextSectionPosition);
        
        laserReceiveGameObjectPosition = new Vector3(gameObject.laserReceiveGameObjectPosition);
        
        backgroundRadius = new LineSegment(gameObject.backgroundRadius);
        
        magnitude = gameObject.magnitude;
        xMoveMagnitude = gameObject.xMoveMagnitude;
        yMoveMagnitude = gameObject.yMoveMagnitude;
        zMoveMagnitude = gameObject.zMoveMagnitude;
        bounceMagnitude = gameObject.bounceMagnitude;
        
        empSlowMagnitude = gameObject.empSlowMagnitude;
        
        gameObjectId = gameObject.gameObjectId;
        
        backgroundGroup = gameObject.backgroundGroup;
      
        positionLocked = gameObject.positionLocked;
        
        soundPlayed = gameObject.soundPlayed;
        
        xValueBeforeMove = gameObject.xValueBeforeMove;
        xMoveDistance = gameObject.xMoveDistance;
        yValueBeforeMove = gameObject.yValueBeforeMove;
        yMoveDistance = gameObject.yMoveDistance;
        zValueBeforeMove = gameObject.zValueBeforeMove;
        zMoveDistance = gameObject.zMoveDistance;
        
        elevatorStartpoint = true;
        platformType = gameObject.platformType;
        
//        platformCompleted = gameObject.platformCompleted;
        
        activationRadius = gameObject.activationRadius;
        attackRadius = gameObject.attackRadius;
        destroyOnDeactivation = gameObject.destroyOnDeactivation;
        
//        markedForDeath = gameObject.markedForDeath;
        
        attackHitDamage = gameObject.attackHitDamage;
        hitPoints = gameObject.hitPoints;
        lastReceivedHitTime = gameObject.lastReceivedHitTime;
        
        lastReceivedHitType = gameObject.lastReceivedHitType;
        
        invincible = gameObject.invincible;
        invincibleTime = gameObject.invincibleTime;
        
        killPoints = gameObject.killPoints;
        collectPoints = gameObject.collectPoints;
        
        noBounceIfInvincible = gameObject.noBounceIfInvincible;
        
        gameObjectInactive = gameObject.gameObjectInactive;
        collisionAttackRadius = gameObject.collisionAttackRadius;
        
        activeWeapon = gameObject.activeWeapon;
        inventoryWeapon = gameObject.inventoryWeapon;
        
        drawableDroid = gameObject.drawableDroid;
        
        initialState = gameObject.initialState;
        currentState = gameObject.currentState;
        previousState = gameObject.previousState;

        group = gameObject.group;

        type = gameObject.type;
        
        hitReactType = gameObject.hitReactType;
        
        bottomMoveType = gameObject.bottomMoveType;
        
        reset();
    }
    
    @Override
    public void reset() {
//        DebugLog.d("GameObject", "reset()");
    	
        removeAll();
        commitUpdates();
        
        renderGameObject = true;
        
        currentPosition.zero();
//        position.zero();
////        mPosition.zero();
//        movePosition.zero();
        bouncePosition.zero();
//        fallPosition.zero();
//        mVelocity.zero();
//        mTargetVelocity.zero();
//        mAcceleration.zero();
//        mImpulse.zero();
        previousSectionPosition.zero();
        nextSectionPosition.zero();
        
        laserReceiveGameObjectPosition.zero();
        
//        animationStepNum = 0;
        
//        offset.zero();
        
//        mBackgroundCollisionNormal.zero();
//        facingDirection.set(1.0f, 1.0f);
        
        backgroundGroup = false;
//        levelIntro = false;
//        playerType = false;
//        mPlayerType = false;
//        backgroundType = false;
//        topObjectType = false;
        
//        mHeading.zero();
        
//        offset.zero();
        
        // FIXME Changed to default currentState = CurrentState.MOVE. Confirm no other Components expecting initial INVALID.
        initialState = CurrentState.INVALID;
        currentState = CurrentState.INVALID;
        previousState = CurrentState.INVALID;
//        initialState = CurrentState.MOVE;
//        currentState = CurrentState.MOVE;
//        currentState = CurrentState.INVALID;
        
        bottomMoveType = BottomMoveType.INVALID;
//        bottomMoveType = -1;
        
        positionLocked = false;
        
        soundPlayed = false;
//        laserFired = false;
        
        platformType = Type.INVALID;
        
        elevatorStartpoint = true;
        
//        platformCompleted = false;
//        elevatorCompleted = false;
        
        activationRadius = 25.0f;
        attackRadius = 10.0f;
        destroyOnDeactivation = false;
//        markedForDeath = false;
        hitPoints = DEFAULT_LIFE;
        lastReceivedHitTime = 0.0f;
        lastReceivedHitType = CurrentState.NO_HIT;
//        lastReceivedHitType = HitType.NO_HIT;
        invincible = false;
        invincibleTime = 1.0f;
        
//        invincibleBackgroundCollision = false;
        
        xValueBeforeMove = 0.0f;
        xMoveDistance = 0.0f;
        yValueBeforeMove = 0.0f;
        yMoveDistance = 0.0f;
        zValueBeforeMove = 0.0f;
        zMoveDistance = 0.0f;
        
        magnitude = 0.1f;
        xMoveMagnitude = 0.0f;
        yMoveMagnitude = 0.0f;
        zMoveMagnitude = 0.0f;
//        verticalMagnitude = 0.0f;
//        moveSpeed = 0.1f;
        
//        bounceHitReact = false;
//      bounceHitReact = false;
        bounceMagnitude = 1.0f;
//        bounceSpeedFactor = 1.0f;
        
        empSlowMagnitude = 0.25f;
        
        noBounceIfInvincible = false;
        
//        fallMagnitude = 0.1f;
//        fallSpeedRate = 0.1f;
//        fallHitReact = false;
        
        gameObjectInactive = false;
        collisionAttackRadius = false;
        
        activeWeapon = false;
        inventoryWeapon = false;
        
        group = Group.INVALID;     
        type = Type.INVALID;
        hitReactType = Type.INVALID;
        
//        width = 0.0f;
//        height = 0.0f;
//        depth = 0.0f;
    }
    
    // Utility functions
//    public final boolean touchingGround() {
////        DebugLog.d("GameObject", "touchingGround()");
//    	
//        final TimeSystem time = sSystemRegistry.timeSystem;
//        final float gameTime = time.getGameTime();
//        final boolean touching = gameTime > 0.1f &&
//            Utils.close(mLastTouchedFloorTime, time.getGameTime(), COLLISION_SURFACE_DECAY_TIME);
//        return touching;
//    }
    
//    public final boolean touchingCeiling() {
////        DebugLog.d("GameObject", "touchingCeiling()");
//    	
//        final TimeSystem time = sSystemRegistry.timeSystem;
//        final float gameTime = time.getGameTime();
//        final boolean touching = gameTime > 0.1f && 
//            Utils.close(mLastTouchedCeilingTime, time.getGameTime(), COLLISION_SURFACE_DECAY_TIME);
//        return touching;
//    }
    
//    public final boolean touchingLeftWall() {
////        DebugLog.d("GameObject", "touchingLeftWall()");
//    	
//        final TimeSystem time = sSystemRegistry.timeSystem;
//        final float gameTime = time.getGameTime();
//        final boolean touching = gameTime > 0.1f &&
//            Utils.close(mLastTouchedLeftWallTime, time.getGameTime(), COLLISION_SURFACE_DECAY_TIME);
//        return touching;
//    }
    
//    public final boolean touchingRightWall() {
////        DebugLog.d("GameObject", "touchingRightWall()");
//    	
//        final TimeSystem time = sSystemRegistry.timeSystem;
//        final float gameTime = time.getGameTime();
//        final boolean touching = gameTime > 0.1f &&
//            Utils.close(mLastTouchedRightWallTime, time.getGameTime(), COLLISION_SURFACE_DECAY_TIME);
//        return touching;
//    }

//    public final Vector3 getPosition() {
////        DebugLog.d("Position", "GameObject getPosition() " + getDrawableDroidObject().toString() + " [" + mGameObjectId + "] " +
////        		" x,y,z,r = " + mPosition.x + ", " + mPosition.y + ", " + mPosition.z + ", " + mPosition.r);
//    	
//        return position;
////        return mPosition;
//    }

//    public final void setPosition(Vector3 objectPosition) {    	
//        position.set(objectPosition);
////        mPosition.set(position);
//    }
    
    public final void initialCurrentPositionX(float x) {
    	previousPosition.x = x - getVelocityX();
    	currentPosition.x = x;
    }
    
    public final void initialCurrentPositionY(float y) {
    	previousPosition.y = y - getVelocityY();
    	currentPosition.y = y;
    }
    
    public final void initialCurrentPositionZ(float z) {
    	previousPosition.z = z - getVelocityZ();
    	currentPosition.z = z;
    }
    
    public final void initialCurrentPosition(float x, float y, float z) {    	
    	previousPosition.x = x - getVelocityX();
    	previousPosition.y = y - getVelocityY();
    	previousPosition.z = z - getVelocityZ();

    	currentPosition.x = x;
    	currentPosition.y = y;
    	currentPosition.z = z;
    }
    
    public final void initialCurrentPosition(float x, float y, float z, float r) {    	
    	previousPosition.x = x - getVelocityX();
    	previousPosition.y = y - getVelocityY();
    	previousPosition.z = z - getVelocityZ();

    	currentPosition.x = x;
    	currentPosition.y = y;
    	currentPosition.z = z;
    	
    	currentPosition.r = r;
    }
    
    public final void setCurrentPosition(Vector3 other) {    	
        currentPosition.set(other);
    }

    public final void setCurrentPosition(float x, float z) {
        currentPosition.set(x, z);
    }
    
    public final void setCurrentPosition(float x, float y, float z, float r) {    	
        currentPosition.set(x, y, z, r);
    }
    
    public final void setPreviousPosition(Vector3 other) {    	
        previousPosition.set(other);
    }
    
    public final void setPreviousPosition(float x, float y, float z, float r) {    	
        previousPosition.set(x, y, z, r);
    }
    
//    public final void setMovePosition(float x, float y, float z, float r) {    	
//        movePosition.set(x, y, z, r);
//    }
    
    public final void setBouncePosition(float x, float y, float z, float r) {    	
        bouncePosition.set(x, y, z, r);
    }
    
    public final void setNextSectionPosition(float x, float y, float z, float r) {    	
    	nextSectionPosition.set(x, y, z, r);
    }
    
//    public final void setFallPosition(float x, float y, float z, float r) {    	
//        fallPosition.set(x, y, z, r);
//    }
    
    public final float getVelocityX() {
    	float velocityX = currentPosition.x - previousPosition.x;
    	
    	// Normalize velocityX in event of Bounce, other
    	if (velocityX > 0.09f) {
    		velocityX = 0.09f;
    	} else if (velocityX < -0.09f) {
    		velocityX = -0.09f;
    	}
    	
    	return velocityX;
//    	return (currentPosition.x - previousPosition.x);
    }
    
//    public final void setVelocityX() {
//    	previousPosition.x = currentPosition.x - magnitude;
//    }
    
    public final void setVelocityX(float vx) {
    	previousPosition.x = currentPosition.x - vx;
    }
    
    public final float getVelocityY() {
    	return (currentPosition.y - previousPosition.y);
    }
    
//    public final void setVelocityY() {
//    	previousPosition.y = currentPosition.y - verticalMagnitude;
//    }
    
    public final void setVelocityY(float vy) {
    	previousPosition.y = currentPosition.y - vy;
    }
    
    public final float getVelocityZ() {
    	float velocityZ = currentPosition.z - previousPosition.z;
    	
    	// Normalize velocityZ in event of Bounce, other
    	if (velocityZ > 0.09f) {
    		velocityZ = 0.09f;
    	} else if (velocityZ < -0.09f) {
    		velocityZ = -0.09f;
    	}
    	
    	return velocityZ;
//    	return (currentPosition.z - previousPosition.z);
    }
    
//    public final void setVelocityZ() {
//    	previousPosition.z = currentPosition.z - magnitude;
//    }
    
    public final void setVelocityZ(float vz) {
    	previousPosition.z = currentPosition.z - vz;
    }
    
//    public final void setVelocity() {
//    	previousPosition.x = currentPosition.x - magnitude;
//    	previousPosition.y = currentPosition.y - verticalMagnitude;
//    	previousPosition.z = currentPosition.z - magnitude;
//    }
//    
//    public final void setVelocity(float magnitudeValue) {
//    	previousPosition.x = currentPosition.x - magnitudeValue;
//    	previousPosition.y = currentPosition.y - verticalMagnitude;
//    	previousPosition.z = currentPosition.z - magnitudeValue;
//    }
//    
//    public final void setVelocity(float magnitudeValue, float verticalMagnitudeValue) {
//    	previousPosition.x = currentPosition.x - magnitudeValue;
//    	previousPosition.y = currentPosition.y - verticalMagnitudeValue;
//    	previousPosition.z = currentPosition.z - magnitudeValue;
//    }
    
    public final void setVelocity(float vx, float vy, float vz) {
    	previousPosition.x = currentPosition.x - vx;
    	previousPosition.y = currentPosition.y - vy;
    	previousPosition.z = currentPosition.z - vz;
    }

//    public float getCenteredPositionX() {
////    public final float getCenteredPositionX() {
//        return position.x + (width / 2.0f);
////        return mPosition.x + (width / 2.0f);
//    }
//    
//    public float getCenteredPositionY() {
////    public final float getCenteredPositionY() {
//        return position.y + (height / 2.0f);
////        return mPosition.y + (height / 2.0f);
//    }
//    
//    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public float getCenteredPositionZ() {
////    public final float getCenteredPositionZ() {
//        return position.z + (depth / 2.0f);
////        return mPosition.z + (depth / 2.0f);
//    }

//    public final Vector3 getVelocity() {
////        DebugLog.d("GameObject", "getVelocity()");
//    	
//        return mVelocity;
//    }
//
//    public final void setVelocity(Vector3 velocity) {
////        DebugLog.d("GameObject", "setVelocity()");
//    	
//        mVelocity.set(velocity);
//    }
//
//    public final Vector3 getTargetVelocity() {
//        return mTargetVelocity;
//    }
//
//    public final void setTargetVelocity(Vector3 targetVelocity) {
//        mTargetVelocity.set(targetVelocity);
//    }
//
//    public final Vector3 getAcceleration() {
////        DebugLog.d("GameObject", "getAcceleration()");
//    	
//        return mAcceleration;
//    }
//
//    public final void setAcceleration(Vector3 acceleration) {
////        DebugLog.d("GameObject", "setAcceleration()");
//    	
//        mAcceleration.set(acceleration);
//    }
//
//    public final Vector3 getImpulse() {
////        DebugLog.d("GameObject", "getImpulse()");
//    	
//        return mImpulse;
//    }
//
//    public final void setImpulse(Vector3 impulse) {
////        DebugLog.d("GameObject", "setImpulse()");
//    	
//        mImpulse.set(impulse);
//    }

//    public final Vector3 getBackgroundCollisionNormal() {
////        DebugLog.d("GameObject", "getBackgroundCollisionNormal()");
//    	
//        return mBackgroundCollisionNormal;
//    }
//
//    public final void setBackgroundCollisionNormal(Vector3 normal) {
////        DebugLog.d("GameObject", "setBackgroundCollisionNormal()");
//    	
//        mBackgroundCollisionNormal.set(normal);
//    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public final int getGameObjectId() {
//    	return mGameObjectId;
//    }
//    
//    public final void setGameObjectId(int id) {
//    	mGameObjectId = id;
//    }
    
//    public final Vector3 getHeading() {
//    	return mHeading;
//    }
//    
//    public final void setHeading(Vector3 heading) {
//    	mHeading.set(heading);
//    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */

//    public final float getLastTouchedFloorTime() {
//        return mLastTouchedFloorTime;
//    }
//
//    public final void setLastTouchedFloorTime(float lastTouchedFloorTime) {
//        mLastTouchedFloorTime = lastTouchedFloorTime;
//    }
//
//    public final float getLastTouchedCeilingTime() {
//        return mLastTouchedCeilingTime;
//    }
//
//    public final void setLastTouchedCeilingTime(float lastTouchedCeilingTime) {
//        mLastTouchedCeilingTime = lastTouchedCeilingTime;
//    }
//
//    public final float getLastTouchedLeftWallTime() {
//        return mLastTouchedLeftWallTime;
//    }
//
//    public final void setLastTouchedLeftWallTime(float lastTouchedLeftWallTime) {
//        mLastTouchedLeftWallTime = lastTouchedLeftWallTime;
//    }
//
//    public final float getLastTouchedRightWallTime() {
//        return mLastTouchedRightWallTime;
//    }
//
//    public final void setLastTouchedRightWallTime(float lastTouchedRightWallTime) {
//        mLastTouchedRightWallTime = lastTouchedRightWallTime;
//    }
    
//    public final ActionType getCurrentAction() {
//        return currentAction;
//    }
//    
//    public final void setCurrentAction(ActionType type) {
//        currentAction = type;
//    }
    
    /* XXX Droid Code © 2012 FrostBlade LLC - Start */
//    public Boolean getPlayerType() {
//    	return mPlayerType;
//    }
//    
//    public void setPlayerType(Boolean type) {
//    	mPlayerType = type;
//    }
    
//    public void objectInstantiation() {
////        public void playerInstantiation() {
////        DebugLog.d("GameObject", "objectInstantiation()");
//        
//    	drawableDroid = new DrawableDroid();
////    	droidObject = new DroidObject();
////    	mDroidObject = new DroidObject();
////    	mPlayerBottom = new DroidObject();
//    }
    
//    public void offsetInstantiation() {
//        offset = new Vector3();
//    }
    
//    public void loadDroidObject(int fileId, Context context) {    	
////        public void loadDroidObject(String objectString, Context context) {   
////        DebugLog.d("GameObject", "loadDroidObject()");
//    	
//    	droidObject.loadObject(fileId, context);
////    	mDroidObject.loadObject(fileId, context);
////    	mDroidObject.loadObject(objectString, context);
////    	mPlayerBottom.loadObject(objectString, context);
//    }
//    
//    public void loadDroidObjectVBO(int fileId, Context context) {
//    	droidObject.loadObjectVBO(fileId, context);
////    	mDroidObject.loadObjectVBO(fileId, context);
//  }
    
//    public DroidObject getDroidObject() {
//    	return mDroidObject;
//    }
//    
//    public void setDroidObject(DroidObject object) {
////    	DebugLog.d("GameObject", "setDroidObject");
//    	
//    	mDroidObject = object;
//    }
//    
//    public DrawableDroid getDrawableDroidObject() {
////    public DrawableDroid getDroid() {
////        DebugLog.d("GameObject", "getDrawableDroidObject()");
//    	
//    	return mDroidObject.drawableDroid;
////    	return mDroidObject.mDroid;
////    	return mPlayerBottom.mDroid;
//    }
//    
//    public void setDrawableDroidObject(int width, int height) {
////    public void setDroid(int width, int height) {
////    public void setDroid(Texture texture, int width, int height) {
////        DebugLog.d("GameObject", "setDrawableDroidObject()");
//    	
//    	// FIXME Confirm width, height are for Object, not screen, then change to Float incl depth
//    	mDroidObject.drawableDroid = new DrawableDroid(width, height);
////    	mDroidObject.mDroid = new DrawableDroid(width, height);
////    	mPlayerBottom.mDroid = new DrawableDroid(width, height);
////    	mPlayerBottom.mDroid = new DrawableDroid(texture, width, height);
//    }
    
//    public void backgroundInstantiation() {
//        DebugLog.d("GameObject", "backgroundInstantiation()");
//    	
//    	mBackground = new BackgroundObject();
//    }
//    
//    public void loadBackgroundObject(String objectString, Context context) {
//        DebugLog.d("GameObject", "loadBackgroundObject()");
//    	
//    	mBackground.loadObject(objectString, context);
//    }
//    
//    // FIXME Temp code
//    public DrawableBackground getBackground() {
//        DebugLog.d("GameObject", "getBackground()");
//    	
//    	return mBackground.mBackground;
//    }
//    
//    // FIXME Temp code
//    public void setBackground(int width, int height) {	
//        DebugLog.d("GameObject", "setBackground()");
//    	
//    	mBackground.mBackground = new DrawableBackground(width, height);
//    }
    
//    public FloatBuffer getVertexBuffer() {
//    	return mDroidObject.getVertexBuffer();
////    	return mPlayerBottom.getVertexBuffer();
//    }
//    
//    public FloatBuffer getNormalBuffer() {
//    	return mDroidObject.getNormalBuffer();
////    	return mPlayerBottom.getNormalBuffer();
//    }
//    
//    public FloatBuffer getColorBuffer() {
//    	return mDroidObject.getColorBuffer();
////    	return mPlayerBottom.getColorBuffer();
//    }
//    
//    public ShortBuffer getIndexBuffer() {
//    	return mDroidObject.getIndexBuffer();
////    	return mPlayerBottom.getIndexBuffer();
//    }
//    
//    public int getIndicesLength() {
//    	return mDroidObject.getIndicesLength();
////    	return mPlayerBottom.getIndicesLength();
//    }
    /* XXX Droid Code © 2012 FrostBlade LLC - Finish */
}
