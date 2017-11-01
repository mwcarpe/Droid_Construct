/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;

//import java.util.Random;
import android.util.Log;
//import com.frostbladegames.droidconstruct.GameObjectGroups.HitType;
//import com.frostbladegames.droidconstruct.CollisionParameters.HitType;

import com.frostbladegames.basestation9.GameObjectGroups.CurrentState;
import com.frostbladegames.basestation9.GameObjectGroups.Group;
import com.frostbladegames.basestation9.GameObjectGroups.Type;

/** 
 * A general-purpose component that responds to collision notifications.  This component
 * may be configured to produce common responses to hit (taking damage, being knocked back, etc), or
 * it can be derived for entirely different responses.  This component must exist on an object for
 * that object to respond to collisions.
 */
public class HitReactionComponent extends GameComponent {
    private static final float PI_OVER_180 = 0.0174532925f;
    private static final float ONE_EIGHTY_OVER_PI = 57.295779513f;
    
    private static final float ATTACK_PAUSE_DELAY = 0.0667f;
//    private static final float ATTACK_PAUSE_DELAY = (1.0f / 60) * 4;
    private static final float DEFAULT_BOUNCE_MAGNITUDE = 200.0f;
    private static final float EVENT_SEND_DELAY = 5.0f;
//    private static final float INVINCIBLE_TIME = 3.0f;
////    private static final float INVINCIBLE_TIME = 2.0f;
    
//    public boolean invincible;
//  private boolean mInvincible;
    
//    private float mLastHitTime;
    
//    // Test whether Elevator at Startpoint or Endpoint
//    private boolean mElevatorStartpoint;
    
    private boolean mPauseOnAttack;
    private float mPauseOnAttackTime;
//    private boolean mBounceOnHit;
    private float mBounceMagnitude;
//    private float mInvincibleAfterHitTime;
    private boolean mDieOnCollect;
    private boolean mDieOnAttack;
//    private ChangeComponentsComponent mPossessionComponent;
//    private InventoryComponent.UpdateRecord mInventoryUpdate;
//    private LauncherComponent mLauncherComponent;
    private int mLauncherHitType;
    private float mInvincibleTime;
    private CurrentState mGameEventHitType;
//    private HitType mGameEventHitType;
//    private int mGameEventHitType;
    private int mGameEventOnHit;
    private int mGameEventIndexData;
    private float mLastGameEventTime;
    private boolean mForceInvincibility;
    private SoundSystem.Sound mReceivedHitSound;
    private SoundSystem.Sound mDealHitSound;
    private CurrentState mDealHitSoundHitType;
//    private HitType mDealHitSoundHitType;
//    private int mDealHitSoundHitType;
    private int mReceivedHitSoundHitType;

    private Type mSpawnOnDealHitObjectType;
//    private GameObjectType mSpawnOnDealHitObjectType;
    private CurrentState mSpawnOnDealHitHitType;
//    private HitType mSpawnOnDealHitHitType;
//    private int mSpawnOnDealHitHitType;
    private boolean mAlignDealHitObjectToVictimX;
//    private boolean mAlignDealHitObjectToVictimY;
    private boolean mAlignDealHitObjectToVictimZ;
    
//    private Vector3 mSectionPositionTemp;
    
    public HitReactionComponent() {
        super();
        
        setPhase(ComponentPhases.FRAME_END.ordinal());        
        reset();        
//        setPhase(ComponentPhases.PRE_DRAW.ordinal());
    }
    
    @Override
    public void reset() {
//    	mElevatorStartpoint = true;
    	
        mPauseOnAttack = false;
        mPauseOnAttackTime = ATTACK_PAUSE_DELAY;
//        mBounceOnHit = false;
        mBounceMagnitude = DEFAULT_BOUNCE_MAGNITUDE;
//        mInvincibleAfterHitTime = 0.0f;
//        invincible = false;
//        mInvincible = false;
        mDieOnCollect = false;
        mDieOnAttack = false;
//        mPossessionComponent = null;
//        mInventoryUpdate = null;
//        mLauncherComponent = null;
//        mLauncherHitType = HitType.LAUNCH;
        
//        mLastHitTime = 0.0f;
        
        mInvincibleTime = 0.0f;
        mGameEventOnHit = -1;
        mGameEventIndexData = 0;
        mLastGameEventTime = -1.0f;
        mGameEventHitType = CurrentState.NO_HIT;
//        mGameEventHitType = HitType.NO_HIT;
        mForceInvincibility = false;
        mReceivedHitSound = null;
        mDealHitSound = null;
        mSpawnOnDealHitObjectType = Type.INVALID;
        mSpawnOnDealHitHitType = CurrentState.NO_HIT;
//        mSpawnOnDealHitHitType = HitType.NO_HIT;
        mDealHitSoundHitType = CurrentState.NO_HIT;
//        mDealHitSoundHitType = HitType.NO_HIT;
        mAlignDealHitObjectToVictimX = false;
//        mAlignDealHitObjectToVictimY = false;
        mAlignDealHitObjectToVictimZ = false;
        
//        mSectionPositionTemp = new Vector3();
    }
    
    @Override
//    public void update(float timeDelta, BaseObject parent, boolean gamePause) { 
    public void update(float timeDelta, BaseObject parent) {    	
        GameObject parentObject = (GameObject)parent;
        
        final float gameTime = sSystemRegistry.timeSystem.getGameTime();
//        TimeSystem time = sSystemRegistry.timeSystem;  
//        final float gameTime = time.getGameTime();
        
//        Log.i("Loop", "HitReactionComponent update()" + " [" + parentObject.gameObjectId + "] ");
        
//        // Check if last hit time is > INVINCIBLE_TIME
//        if (parentObject.invincible && (gameTime > (parentObject.lastReceivedHitTime + parentObject.invincibleTime))) {
////        if (parentObject.invincible && (gameTime > (parentObject.lastReceivedHitTime + INVINCIBLE_TIME))) {
//        	parentObject.invincible = false;
////        	parentObject.currentState = CurrentState.MOVE;
//        }
    }
    
    /** Called when this Dynamic GameObject is hit by the Background GameObject */
    public void receivedHitBackground(GameObject receiveGameObject, CurrentState receiveHitType, LineSegment backgroundLineSegment) {
//    public void receivedHitBackground(GameObject receiveGameObject, HitType receiveHitType, LineSegment backgroundLineSegment) {
    	
        final TimeSystem time = sSystemRegistry.timeSystem;
        final float gameTime = time.getGameTime();
         
        // TODO RE-ENABLE LevelSystem sendGameEvent(). What is purpose of EVENT_SEND_DELAY?
//        if (mGameEventHitType == receiveHitType && 
//                mGameEventHitType != CollisionParameters.HitType.INVALID ) {
//        	if (mLastGameEventTime < 0.0f || gameTime > mLastGameEventTime + EVENT_SEND_DELAY) {
//	            LevelSystem level = sSystemRegistry.levelSystem;
//	            level.sendGameEvent(mGameEventOnHit, mGameEventIndexData, true);
//	        } else {
//	        	// special case.  If we're waiting for a hit type to spawn an event and
//	        	// another event has just happened, eat this hit so we don't miss
//	        	// the chance to send the event.
//	        	receiveHitType = CollisionParameters.HitType.INVALID;
//	        }
//        	mLastGameEventTime = gameTime;
//        }
        
        switch(receiveHitType) {
        case NO_HIT:
        	// Ignore
        	
            break;
        
        case HIT:            	
            if (receiveGameObject.hitPoints > 1) {
            	// Lose 1 HitPoint
                receiveGameObject.hitPoints -= 1;
                
            	/* Use simple Particle-Line Collision Projection for Bounce Angle Calculation.
            	 * Hit does not use .invincible setting. */
            	backgroundBounce(receiveGameObject, backgroundLineSegment);
            	
           	    receiveGameObject.bounceMagnitude = 8.0f;
//           	    receiveGameObject.bounceMagnitude = 5.0f;
//           	    receiveGameObject.bounceMagnitude = 10.0f;
//            	    receiveGameObject.bounceMagnitude = 20.0f;
        	    
                receiveGameObject.lastReceivedHitTime = gameTime;
                
                if (receiveGameObject.currentState == CurrentState.BOUNCE || 
                		receiveGameObject.currentState == CurrentState.HIT ||
                		receiveGameObject.currentState == CurrentState.FROZEN) {
                	// Don't set previousState. Keep it as Move, Elevator, or other.
                	receiveGameObject.currentState = CurrentState.HIT;
                } else {
                    receiveGameObject.previousState = receiveGameObject.currentState;
                	receiveGameObject.currentState = CurrentState.HIT;
                }
            	
            } else {
//            } else if (receiveGameObject.hitPoints <= 1) {
                receiveGameObject.lastReceivedHitTime = gameTime;
                
                if (receiveGameObject.currentState == CurrentState.BOUNCE || 
                		receiveGameObject.currentState == CurrentState.HIT ||
                		receiveGameObject.currentState == CurrentState.FROZEN) {
                	// Don't set previousState. Keep it as Move, Elevator, or other.
                	receiveGameObject.currentState = CurrentState.DEAD;
                } else {
                    receiveGameObject.previousState = receiveGameObject.currentState;
                	receiveGameObject.currentState = CurrentState.DEAD;
                }
            }
            
            receiveGameObject.laserReceiveGameObjectPosition.set(receiveGameObject.currentPosition);
            
            break;
            
        case BOUNCE:
        	/* Use simple Particle-Line Collision Projection for Bounce Angle Calculation.
        	 * Bounce does not use .invincible setting. */
        	backgroundBounce(receiveGameObject, backgroundLineSegment);
        	
        	receiveGameObject.bounceMagnitude = 8.0f;
//        	receiveGameObject.bounceMagnitude = 5.0f;
//        	receiveGameObject.bounceMagnitude = 10.0f;
//          receiveGameObject.bounceMagnitude = 2.0f;
//          receiveGameObject.bounceMagnitude = 20.0f;

            receiveGameObject.lastReceivedHitTime = gameTime;
            
            if (receiveGameObject.currentState == CurrentState.ELEVATOR) {
            	// ignore
            } else if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.BOUNCE;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.BOUNCE;
            }
//            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
//            		receiveGameObject.currentState == CurrentState.HIT) {
//            	// Don't set previousState. Keep it as Move, Elevator, or other.
//            	receiveGameObject.currentState = CurrentState.BOUNCE;
//            } else {
//                receiveGameObject.previousState = receiveGameObject.currentState;
//            	receiveGameObject.currentState = CurrentState.BOUNCE;
//            }

        	
        	break;
            
        case FALL:
        	gameObjectFall(receiveGameObject);
        	
        	if (receiveGameObject.currentPosition.x > receiveGameObject.previousPosition.x) {
            	receiveGameObject.xMoveMagnitude = 0.025f;
        	} else {
            	receiveGameObject.xMoveMagnitude = -0.025f;
        	}

        	if (receiveGameObject.currentPosition.z > receiveGameObject.previousPosition.z) {
            	receiveGameObject.zMoveMagnitude = 0.025f;
        	} else {
            	receiveGameObject.zMoveMagnitude = -0.025f;
        	}
        	
        	receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
        	receiveGameObject.yMoveDistance = -8.0f;
        	receiveGameObject.yMoveMagnitude = -0.01f;
//        	receiveGameObject.yMoveMagnitude = -0.1f;
        	
        	receiveGameObject.lastReceivedHitTime = gameTime;
        	
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.FALL;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.FALL;
            }
        	
//            	float fallX = receiveGameObject.currentPosition.x;
//            	float fallY = receiveGameObject.currentPosition.y;
//            	float fallZ = receiveGameObject.currentPosition.z;
//            	float fallR = receiveGameObject.currentPosition.r;
//            	
//            	fallX -= (float)Math.sin(fallR * PI_OVER_180) * receiveGameObject.magnitude * 20.0f;
//            	fallZ -= (float)Math.cos(fallR * PI_OVER_180) * receiveGameObject.magnitude * 20.0f;
////            	fallX -= (float)Math.sin(fallR * PI_OVER_180) * receiveGameObject.magnitude * 10.0f;
////            	fallZ -= (float)Math.cos(fallR * PI_OVER_180) * receiveGameObject.magnitude * 10.0f;
//            	
//            	receiveGameObject.verticalMagnitude = 0.1f;
//            	
//            	receiveGameObject.currentPosition.set(fallX, fallY, fallZ, fallR);
////            	receiveGameObject.setFallPosition(fallX, fallY, fallZ, fallR);
//            	
//                receiveGameObject.lastReceivedHitTime = gameTime;
//                
//            	receiveGameObject.currentState = CurrentState.FALL;
        	
            break;
        	
//        case ELEVATOR:
//    	    switch(GameParameters.levelRow) {
//          	case 1:
//          		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
//          		receiveGameObject.yMoveDistance = -20.0f;
//            	receiveGameObject.verticalMagnitude = -0.1f;
//            	
//          		break;
//          		
//    	    default:
//    	        break;
//    	    }
//        	
//            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
//            		receiveGameObject.currentState == CurrentState.HIT) {
//            	// Don't set previousState. Keep it as Move, Elevator, or other.
//            	receiveGameObject.currentState = CurrentState.ELEVATOR;
//            } else {
//                receiveGameObject.previousState = receiveGameObject.currentState;
//            	receiveGameObject.currentState = CurrentState.ELEVATOR;
//            }
//            
//            receiveGameObject.platformCompleted = true;
////                receiveGameObject.elevatorCompleted = true;
//        	
//            break;
            
//        case COLLECT:            	
////          if (mInventoryUpdate != null && receive.life > 0) {
////          InventoryComponent attackInventory = attack.findByClass(InventoryComponent.class);
////          if (attackInventory != null) {
////              attackInventory.applyUpdate(mInventoryUpdate);
////          }
////      }
////      if (mDieOnCollect && receive.life > 0) {
////          receive.life = 0;
////      } 
//    	break;
            
//            case DEPRESS:
//            	// Add code
//            	
//                break;
            
        case COLLECT:
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
    				receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.COLLECT;
            } else {
            	receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.COLLECT;
            }

        	break;
            
        case DEAD:
            receiveGameObject.lastReceivedHitTime = gameTime;
            
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.DEAD;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.DEAD;
            }
            
            receiveGameObject.laserReceiveGameObjectPosition.set(receiveGameObject.currentPosition);

            break;
            
        case GAME_END:
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
    				receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.GAME_END;
            } else {
            	receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.GAME_END;
            }
        	
        	break;
            
        default:
        	// Ignore
        	
            break;
        }
//        
//        if (receiveHitType != HitType.NO_HIT) {
//        	// FIXME Add mReceivedHitSound either here or above in specific HitType (see above)
//            
//        }
    }
    
    /** Called when this Dynamic GameObject is hit by another Dynamic GameObject */
    public void receivedHitDynamic(GameObject receiveGameObject, CurrentState receiveHitType, GameObject attackGameObject) {
//    public void receivedHitDynamic(GameObject receiveGameObject, HitType receiveHitType, GameObject attackGameObject) {
//    public void receivedHitDynamic(GameObject receiveGameObject, HitType receiveHitType, GameObject attackGameObject, HitType attackHitType) {
    	
        final TimeSystem time = sSystemRegistry.timeSystem;
        final float gameTime = time.getGameTime();
         
        // TODO RE-ENABLE LevelSystem sendGameEvent()
//      if (mGameEventHitType == receiveHitType && 
//              mGameEventHitType != CollisionParameters.HitType.INVALID ) {
//      	if (mLastGameEventTime < 0.0f || gameTime > mLastGameEventTime + EVENT_SEND_DELAY) {
//	            LevelSystem level = sSystemRegistry.levelSystem;
//	            level.sendGameEvent(mGameEventOnHit, mGameEventIndexData, true);
//	        } else {
//	        	// special case.  If we're waiting for a hit type to spawn an event and
//	        	// another event has just happened, eat this hit so we don't miss
//	        	// the chance to send the event.
//	        	receiveHitType = CollisionParameters.HitType.INVALID;
//	        }
//      	mLastGameEventTime = gameTime;
//      }
        
        switch(receiveHitType) {
        case NO_HIT:
        	// Ignore
        	
            break;
            
//        case MOVE:
//        	receiveGameObject.currentState = CurrentState.MOVE;
//        	
//        	attackGameObject.currentState = CurrentState.MOVE;
//        	
//            break;
        
        case HIT:
            if (receiveGameObject.hitPoints > 1) {
            	// Lose 1 HitPoint
                receiveGameObject.hitPoints -= 1;
                
            	/* Use simple Particle-Particle and DistanceVector Collision Projection for Bounce Angle Calculation.
            	 * Bounce does not use .invincible setting. */
                gameObjectBounce(receiveGameObject, attackGameObject);
            	
        	    receiveGameObject.bounceMagnitude = 8.0f;
//        	    receiveGameObject.bounceMagnitude = 5.0f;
//        	    receiveGameObject.bounceMagnitude = 10.0f;
//            	    receiveGameObject.bounceMagnitude = 20.0f;
        	    
                receiveGameObject.lastReceivedHitTime = gameTime;
                
                if (receiveGameObject.currentState == CurrentState.BOUNCE || 
                		receiveGameObject.currentState == CurrentState.HIT ||
                		receiveGameObject.currentState == CurrentState.FROZEN) {
                	// Don't set previousState. Keep it as Move, Elevator, or other.
                	receiveGameObject.currentState = CurrentState.HIT;
                } else {
                    receiveGameObject.previousState = receiveGameObject.currentState;
                	receiveGameObject.currentState = CurrentState.HIT;
                }
            	
            } else {
//            } else if (receiveGameObject.hitPoints <= 1) {
                receiveGameObject.lastReceivedHitTime = gameTime;
                
                if (receiveGameObject.currentState == CurrentState.BOUNCE || 
                		receiveGameObject.currentState == CurrentState.HIT ||
                		receiveGameObject.currentState == CurrentState.FROZEN) {
                	// Don't set previousState. Keep it as Move, Elevator, or other.
                	receiveGameObject.currentState = CurrentState.DEAD;
                } else {
                    receiveGameObject.previousState = receiveGameObject.currentState;
                	receiveGameObject.currentState = CurrentState.DEAD;
                }
            }
            
            attackGameObject.laserReceiveGameObjectPosition.set(receiveGameObject.currentPosition);
            
            break;
            
        case BOUNCE:            	
        	/* Use simple Particle-Particle and DistanceVector Collision Projection for Bounce Angle Calculation.
        	 * Bounce does not use .invincible setting. */
        	gameObjectBounce(receiveGameObject, attackGameObject);
        	
        	receiveGameObject.bounceMagnitude = 8.0f;
//        	receiveGameObject.bounceMagnitude = 5.0f;
//        	receiveGameObject.bounceMagnitude = 10.0f;
//        	    receiveGameObject.bounceMagnitude = 20.0f;

            receiveGameObject.lastReceivedHitTime = gameTime;
            
            if (receiveGameObject.currentState == CurrentState.ELEVATOR) {
            	// ignore
            } else if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move or other.
            	receiveGameObject.currentState = CurrentState.BOUNCE;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.BOUNCE;
            }
//            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
//            		receiveGameObject.currentState == CurrentState.HIT) {
//            	// Don't set previousState. Keep it as Move, Elevator, or other.
//            	receiveGameObject.currentState = CurrentState.BOUNCE;
//            } else {
//                receiveGameObject.previousState = receiveGameObject.currentState;
//            	receiveGameObject.currentState = CurrentState.BOUNCE;
//            }

        	break;
            
        case FROZEN:        	
            if (receiveGameObject.hitPoints > 1) {
            	// Lose 1 HitPoint
                receiveGameObject.hitPoints -= 1;
                
//            	/* Use simple Particle-Particle and DistanceVector Collision Projection for Bounce Angle Calculation.
//            	 * Bounce does not use .invincible setting. */
//                gameObjectBounce(receiveGameObject, attackGameObject);
//            	
//        	    receiveGameObject.bounceMagnitude = 5.0f;
////        	    receiveGameObject.bounceMagnitude = 10.0f;
////            	    receiveGameObject.bounceMagnitude = 20.0f;
        	    
                receiveGameObject.lastReceivedHitTime = gameTime;
                
                if (receiveGameObject.currentState == CurrentState.BOUNCE || 
                		receiveGameObject.currentState == CurrentState.HIT ||
                		receiveGameObject.currentState == CurrentState.FROZEN) {
                	// Don't set previousState. Keep it as Move, Elevator, or other.
                	receiveGameObject.currentState = CurrentState.FROZEN;
                } else {
                    receiveGameObject.previousState = receiveGameObject.currentState;
                	receiveGameObject.currentState = CurrentState.FROZEN;
                }
            	
            } else {
//            } else if (receiveGameObject.hitPoints <= 1) {
                receiveGameObject.lastReceivedHitTime = gameTime;
                
                if (receiveGameObject.currentState == CurrentState.BOUNCE || 
                		receiveGameObject.currentState == CurrentState.HIT ||
                		receiveGameObject.currentState == CurrentState.FROZEN) {
                	// Don't set previousState. Keep it as Move, Elevator, or other.
                	receiveGameObject.currentState = CurrentState.DEAD;
                } else {
                    receiveGameObject.previousState = receiveGameObject.currentState;
                	receiveGameObject.currentState = CurrentState.DEAD;
                }
            }
            
            attackGameObject.laserReceiveGameObjectPosition.set(receiveGameObject.currentPosition);
            break;
            
        case FALL:
        	gameObjectFall(receiveGameObject);
        	
        	receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
        	receiveGameObject.yMoveDistance = -8.0f;
        	receiveGameObject.yMoveMagnitude = -0.1f;
        	
        	receiveGameObject.lastReceivedHitTime = gameTime;
        	
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.FALL;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.FALL;
            }
            
            break;
            
        case PLATFORM_SECTION_START:
        	if (receiveGameObject.group == Group.DROID) {
//              	mSectionPositionTemp.set(receiveGameObject.currentPosition);
        		
              	float nextX = attackGameObject.nextSectionPosition.x;
              	float nextY = attackGameObject.nextSectionPosition.y;
              	float nextZ = attackGameObject.nextSectionPosition.z;
              	float nextR = receiveGameObject.currentPosition.r;
              	
              	receiveGameObject.setNextSectionPosition(nextX, nextY, nextZ, nextR);
//              	receiveGameObject.setCurrentPosition(nextX, nextY, nextZ, nextR);
//      			
//              	receiveGameObject.setPreviousPosition(mSectionPositionTemp);	
        	}
          	
            receiveGameObject.lastReceivedHitTime = gameTime;
              
          	receiveGameObject.previousState = receiveGameObject.currentState;
          	receiveGameObject.currentState = CurrentState.PLATFORM_SECTION_START;
          	
          	attackGameObject.previousState = attackGameObject.currentState;
          	attackGameObject.currentState = CurrentState.PLATFORM_SECTION_START;
//          	receiveGameObject.currentState = CurrentState.PLATFORM_SECTION_END;
          	
//          	receiveGameObject.platformCompleted = true;
      	
          	break;
            
        case PLATFORM_SECTION_END:
        	if (receiveGameObject.group == Group.DROID) {
//              	mSectionPositionTemp.set(receiveGameObject.currentPosition);
              	
              	float previousX = attackGameObject.previousSectionPosition.x;
              	float previousY = attackGameObject.previousSectionPosition.y;
              	float previousZ = attackGameObject.previousSectionPosition.z;
              	float previousR = receiveGameObject.currentPosition.r;
              	
              	receiveGameObject.setNextSectionPosition(previousX, previousY, previousZ, previousR);
//              	receiveGameObject.setCurrentPosition(previousX, previousY, previousZ, previousR);
//      			
//              	receiveGameObject.setPreviousPosition(mSectionPositionTemp);	
        	}
          	
            receiveGameObject.lastReceivedHitTime = gameTime;
              
          	receiveGameObject.previousState = receiveGameObject.currentState;
          	receiveGameObject.currentState = CurrentState.PLATFORM_SECTION_END;
          	
          	attackGameObject.previousState = attackGameObject.currentState;
          	attackGameObject.currentState = CurrentState.PLATFORM_SECTION_END;
//          	receiveGameObject.currentState = CurrentState.PLATFORM_SECTION_END;
          	
//          	receiveGameObject.platformCompleted = true;
      	
          	break;
            
        case ELEVATOR:        	
        	float direction = 0.0f;
        	
        	// Check whether Elevator at Startpoint or Endpoint
        	if (receiveGameObject.elevatorStartpoint) {
        		direction = 1.0f;
        	} else {
        		direction = -1.0f;
        	}
        			
        	// Set values for both Elevator (receiveGameObject) and Droid (attackGameObject) using Elevator Type
//        	if (receiveGameObject.elevatorStartpoint) {
    	    switch(GameParameters.levelRow) {
          	case 1:
          		switch(receiveGameObject.type) {
          		case SECTION_04:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -20.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.083f * direction;
                	
              		attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
              		attackGameObject.yMoveDistance = -20.0f * direction;
              		attackGameObject.yMoveMagnitude = -0.083f * direction;
              		
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		
          		break;
          		
          	case 3:
          		switch(receiveGameObject.type) {
          		case SECTION_01:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -30.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.124f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = -30.0f * direction;
                	attackGameObject.yMoveMagnitude = -0.124f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_04:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -20.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.1765f * direction;
                	
              		receiveGameObject.zValueBeforeMove = receiveGameObject.currentPosition.z;
              		receiveGameObject.zMoveDistance = 22.93f * direction;
                	receiveGameObject.zMoveMagnitude = 0.1765f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = -20.0f * direction;
                	attackGameObject.yMoveMagnitude = -0.1765f * direction;
                	
                	attackGameObject.zValueBeforeMove = attackGameObject.currentPosition.z;
                	attackGameObject.zMoveDistance = 22.93f * direction;
                	attackGameObject.zMoveMagnitude = 0.1765f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_05:
              		receiveGameObject.xValueBeforeMove = receiveGameObject.currentPosition.x;
              		receiveGameObject.xMoveDistance = -10.0f * direction;
                	receiveGameObject.xMoveMagnitude = -0.04f * direction;
                	
                	attackGameObject.xValueBeforeMove = attackGameObject.currentPosition.x;
                	attackGameObject.xMoveDistance = -10.0f * direction;
                	attackGameObject.xMoveMagnitude = -0.04f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_06:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -20.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.083f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = -20.0f * direction;
                	attackGameObject.yMoveMagnitude = -0.083f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_07:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -20.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.083f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = -20.0f * direction;
                	attackGameObject.yMoveMagnitude = -0.083f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		break;
          		
          	case 5:
          		switch(receiveGameObject.type) {
          		case SECTION_04:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = 20.0f * direction;
                	receiveGameObject.yMoveMagnitude = 0.083f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = 20.0f * direction;
                	attackGameObject.yMoveMagnitude = 0.083f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_06:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -20.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.083f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = -20.0f * direction;
                	attackGameObject.yMoveMagnitude = -0.083f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_08:
              		receiveGameObject.xValueBeforeMove = receiveGameObject.currentPosition.x;
              		receiveGameObject.xMoveDistance = 11.0f * direction;
                	receiveGameObject.xMoveMagnitude = 0.044f * direction;
                	
                	attackGameObject.xValueBeforeMove = attackGameObject.currentPosition.x;
                	attackGameObject.xMoveDistance = 11.0f * direction;
                	attackGameObject.xMoveMagnitude = 0.044f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		break;
          		
          	case 6:
          		switch(receiveGameObject.type) {
          		case SECTION_02:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = 10.0f * direction;
                	receiveGameObject.yMoveMagnitude = 0.04f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = 10.0f * direction;
                	attackGameObject.yMoveMagnitude = 0.04f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_04:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = 10.0f * direction;
                	receiveGameObject.yMoveMagnitude = 0.04f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = 10.0f * direction;
                	attackGameObject.yMoveMagnitude = 0.04f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_06:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = 10.0f * direction;
                	receiveGameObject.yMoveMagnitude = 0.04f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = 10.0f * direction;
                	attackGameObject.yMoveMagnitude = 0.04f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		break;
          		
          	case 7:
          		switch(receiveGameObject.type) {
          		case SECTION_02:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = 10.0f * direction;
                	receiveGameObject.yMoveMagnitude = 0.04f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = 10.0f * direction;
                	attackGameObject.yMoveMagnitude = 0.04f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_07:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = -10.0f * direction;
                	receiveGameObject.yMoveMagnitude = -0.16f * direction;
                	
              		receiveGameObject.zValueBeforeMove = receiveGameObject.currentPosition.z;
              		receiveGameObject.zMoveDistance = -28.75f * direction;
                	receiveGameObject.zMoveMagnitude = -0.1598f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = -10.0f * direction;
                	attackGameObject.yMoveMagnitude = -0.16f * direction;
                	
                	attackGameObject.zValueBeforeMove = attackGameObject.currentPosition.z;
                	attackGameObject.zMoveDistance = -28.75f * direction;
                	attackGameObject.zMoveMagnitude = -0.1598f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		break;
          		
          	case 8:
          		switch(receiveGameObject.type) {
          		case SECTION_03:
              		receiveGameObject.zValueBeforeMove = receiveGameObject.currentPosition.z;
              		receiveGameObject.zMoveDistance = 5.0f * direction;
                	receiveGameObject.zMoveMagnitude = 0.0211f * direction;
                	
                	attackGameObject.zValueBeforeMove = attackGameObject.currentPosition.z;
                	attackGameObject.zMoveDistance = 5.0f * direction;
                	attackGameObject.zMoveMagnitude = 0.0211f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_05:
              		receiveGameObject.yValueBeforeMove = receiveGameObject.currentPosition.y;
              		receiveGameObject.yMoveDistance = 10.0f * direction;
                	receiveGameObject.yMoveMagnitude = 0.04f * direction;
                	
                	attackGameObject.yValueBeforeMove = attackGameObject.currentPosition.y;
                	attackGameObject.yMoveDistance = 10.0f * direction;
                	attackGameObject.yMoveMagnitude = 0.04f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          			
          		case SECTION_06:
              		receiveGameObject.xValueBeforeMove = receiveGameObject.currentPosition.x;
              		receiveGameObject.xMoveDistance = 6.764f * direction;
                	receiveGameObject.xMoveMagnitude = 0.0569f * direction;
                	
              		receiveGameObject.zValueBeforeMove = receiveGameObject.currentPosition.z;
              		receiveGameObject.zMoveDistance = -6.95f * direction;
                	receiveGameObject.zMoveMagnitude = -0.057f * direction;
                	
                	attackGameObject.xValueBeforeMove = attackGameObject.currentPosition.x;
                	attackGameObject.xMoveDistance = 6.764f * direction;
                	attackGameObject.xMoveMagnitude = 0.0569f * direction;
                	
                	attackGameObject.zValueBeforeMove = attackGameObject.currentPosition.z;
                	attackGameObject.zMoveDistance = -6.95f * direction;
                	attackGameObject.zMoveMagnitude = -0.057f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		break;
          		
          	case 9:
          		switch(receiveGameObject.type) {
          		case SECTION_03:
              		receiveGameObject.xValueBeforeMove = receiveGameObject.currentPosition.x;
              		receiveGameObject.xMoveDistance = 20.0f * direction;
                	receiveGameObject.xMoveMagnitude = 0.083f * direction;
                	
                	attackGameObject.xValueBeforeMove = attackGameObject.currentPosition.x;
                	attackGameObject.xMoveDistance = 20.0f * direction;
                	attackGameObject.xMoveMagnitude = 0.083f * direction;
                	
              		attackGameObject.platformType = receiveGameObject.type;
          			break;
          		}
          		break;
          		
    	    default:
    	        break;
    	    }
        	
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.ELEVATOR;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.ELEVATOR;
            }
            
            if (attackGameObject.currentState == CurrentState.BOUNCE || 
            		attackGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	attackGameObject.currentState = CurrentState.ELEVATOR;
            } else {
            	attackGameObject.previousState = attackGameObject.currentState;
            	attackGameObject.currentState = CurrentState.ELEVATOR;
            }
            
//            if(receiveGameObject.group == Group.PLATFORM_LEVEL_START || receiveGameObject.group == Group.PLATFORM_LEVEL_END ||
//            		receiveGameObject.group == Group.PLATFORM_SECTION_START || receiveGameObject.group == Group.PLATFORM_SECTION_END ||
//            		receiveGameObject.group == Group.PLATFORM_ELEVATOR) {
//            	
//                receiveGameObject.platformCompleted = true;
//            }
        	
            break;
            
        case COLLECT:
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
    				receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.COLLECT;
            } else {
            	receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.COLLECT;
            }

        	break;
            
//        case DEPRESS:
//        	// Add code
//        	
//            break;   
        	
        case WAIT_FOR_DEAD:
            receiveGameObject.lastReceivedHitTime = gameTime;
            
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
    				receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.WAIT_FOR_DEAD;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.WAIT_FOR_DEAD;
            }
            
            attackGameObject.laserReceiveGameObjectPosition.set(receiveGameObject.currentPosition);
            
        	break;
        
        case DEAD:
            receiveGameObject.lastReceivedHitTime = gameTime;
            
            if (receiveGameObject.currentState == CurrentState.BOUNCE || 
            		receiveGameObject.currentState == CurrentState.HIT ||
            		receiveGameObject.currentState == CurrentState.FROZEN) {
            	// Don't set previousState. Keep it as Move, Elevator, or other.
            	receiveGameObject.currentState = CurrentState.DEAD;
            } else {
                receiveGameObject.previousState = receiveGameObject.currentState;
            	receiveGameObject.currentState = CurrentState.DEAD;
            }
            
            attackGameObject.laserReceiveGameObjectPosition.set(receiveGameObject.currentPosition);
            
            break;
            
        case LEVEL_END:
            receiveGameObject.lastReceivedHitTime = gameTime;
            
        	receiveGameObject.previousState = receiveGameObject.currentState;
        	receiveGameObject.currentState = CurrentState.LEVEL_END;
        	
//            receiveGameObject.platformCompleted = true;
        	
            break;
            
        default:
        	// Ignore
        	
            break;
        }
    }
    
	private void backgroundBounce(GameObject receiveGameObject, LineSegment backgroundLineSegment) {
    	float receiveVx = receiveGameObject.getVelocityX();
    	float receiveVz = receiveGameObject.getVelocityZ();
    	
    	// Normalize for multiple Collisions and Bounce Calculations (e.g. Wall Corner)
		float adjust = 1.0f;
		
    	if (Math.abs(receiveVx) > 0.1f) {
    		adjust = Math.abs(0.1f / receiveVx);
    		receiveVx *= adjust;
    	}
    	
    	if (Math.abs(receiveVz) > 0.1f) {
    		adjust = Math.abs(0.1f / receiveVz);
    		receiveVz *= adjust;
    	}
    		
//    	if (Math.abs(receiveVx) > 0.1f || Math.abs(receiveVz) > 0.1f) {
//    		float adjust;
//    		
//    		if (Math.abs(receiveVx) > Math.abs(receiveVz)) {
//    			adjust = Math.abs(0.1f / receiveVx);
//    		} else {
//    			adjust = Math.abs(0.1f / receiveVz);
//    		}
//    		
//    		receiveVx *= adjust;
//    		receiveVz *= adjust;
//    	}
    	
    	float lineDx = backgroundLineSegment.getUnitVectorX();
    	float lineDz = backgroundLineSegment.getUnitVectorZ();
    	
    	float dp1 = (receiveVx * lineDx) + (receiveVz * lineDz);
    	
    	float projection1X = dp1 * lineDx;
    	float projection1Z = dp1 * lineDz;
    	
    	// Background LineSegments use Right-Side Collision Boundaries and LeftUnitNormals for Bounce
    	float lineLnX = backgroundLineSegment.getLeftUnitNormalX();
    	float lineLnZ = backgroundLineSegment.getLeftUnitNormalZ();
    	
    	float dp2 = (receiveVx * lineLnX) + (receiveVz * lineLnZ);
    	
    	float projection2X = dp2 * lineLnX;
    	float projection2Z = dp2 * lineLnZ;
    	
    	projection2X *= -1.0f;
    	projection2Z *= -1.0f;

    	float bounceVx = projection1X + projection2X;
    	float bounceVz = projection1Z + projection2Z;
    	
//    	// Adjust to set minimum bounce angle
//    	if (Math.abs(bounceVx) < 0.03f) {
//    		if (bounceVx < 0.0f) {
//    			bounceVx = -0.03f;
//    		} else {
//    			bounceVx = 0.03f;
//    		}
//    	}
//    	
//    	if (Math.abs(bounceVz) < 0.03f) {
//    		if (bounceVz < 0.0f) {
//    			bounceVz = -0.03f;
//    		} else {
//    			bounceVz = 0.03f;
//    		}
//    	}
    	
    	receiveGameObject.currentPosition.r = backgroundCollisionAngleCalc(bounceVx, bounceVz);

    	receiveGameObject.setBouncePosition(bounceVx, 0.0f, bounceVz, 0.0f);
	}
	
	private float backgroundCollisionAngleCalc(float bounceVx, float bounceVz) {
		float collisionAngle = 0.0f;

		// Angle in Degrees
		if (bounceVx <= 0.0f) {
			if (bounceVz <= 0.0f) {
				if (bounceVz == 0) {
					// Set to minimal denominator
					collisionAngle = (float)Math.atan(
							(-bounceVx) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					collisionAngle = (float)Math.atan(
							(-bounceVx) /
							(-bounceVz)) * ONE_EIGHTY_OVER_PI;
				}
			} else {
				if ((0.0f - bounceVx) == 0) {
					// Set to minimal denominator
					collisionAngle = 90.0f + (float)Math.atan(
							(bounceVz) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					collisionAngle = 90.0f + (float)Math.atan(
							(bounceVz) /
							(-bounceVx)) * ONE_EIGHTY_OVER_PI;
				}
			}
		} else {
			if (bounceVz > 0.0f) {
				collisionAngle = 180.0f + (float)Math.atan(
						(bounceVx) /
						(bounceVz)) * ONE_EIGHTY_OVER_PI;
			} else {
				collisionAngle = 270.0f + (float)Math.atan(
						(-bounceVz) /
						(bounceVx)) * ONE_EIGHTY_OVER_PI;
			}
		}
		return collisionAngle;
	}
	
	private void gameObjectBounce(GameObject receiveGameObject, GameObject attackGameObject) {
		// Calculate Bounce Angle between two GameObjects using relative Positions (Centerpoints) for realistic Bounce
    	float r = gameObjectCollisionAngleCalc(receiveGameObject.currentPosition.x, receiveGameObject.currentPosition.z,
    			attackGameObject.currentPosition.x, attackGameObject.currentPosition.z);
//		// Calculate Angle between two GameObject Particle Centerpoints, then Bounce in opposite 180 degree direction
//    	float r = gameObjectCollisionAngleCalc(receiveGameObject.currentPosition.x, receiveGameObject.currentPosition.z,
//    			attackGameObject.currentPosition.x, attackGameObject.currentPosition.z) - 180.0f;
    	
//    	// Randomly adjust angle slightly so Receive and Attack GameObjects don't continue to collide head-to-head
//        Random angleAdjust = new Random();
//        r += angleAdjust.nextInt(5);  
		
    	float bounceVx = (float)Math.sin(r * PI_OVER_180) * receiveGameObject.magnitude;
    	float bounceVz = (float)Math.cos(r * PI_OVER_180) * receiveGameObject.magnitude;
    	
    	receiveGameObject.currentPosition.r = r + 180.0f;
//    	receiveGameObject.currentPosition.r = r;
    	
    	receiveGameObject.setBouncePosition(bounceVx, 0.0f, bounceVz, 0.0f);
	}
	
	private float gameObjectCollisionAngleCalc(float receiveX, float receiveZ, float attackX, float attackZ) {
		float collisionAngle = 0.0f;
		
		// Angle in Degrees
		if (attackX <= receiveX) {
			if (attackZ <= receiveZ) {
				if ((receiveZ - attackZ) == 0) {
					// Set to minimal denominator
					collisionAngle = (float)Math.atan(
							(receiveX - attackX) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					collisionAngle = (float)Math.atan(
							(receiveX - attackX) /
							(receiveZ - attackZ)) * ONE_EIGHTY_OVER_PI;
				}
			} else {
				if ((receiveX - attackX) == 0) {
					// Set to minimal denominator
					collisionAngle = 90.0f + (float)Math.atan(
							(attackZ - receiveZ) /
							0.001f) * ONE_EIGHTY_OVER_PI;
				} else {
					collisionAngle = 90.0f + (float)Math.atan(
							(attackZ - receiveZ) /
							(receiveX - attackX)) * ONE_EIGHTY_OVER_PI;
				}
			}
		} else {
			if (attackZ > receiveZ) {
				collisionAngle = 180.0f + (float)Math.atan(
						(attackX - receiveX) /
						(attackZ - receiveZ)) * ONE_EIGHTY_OVER_PI;
			} else {
				collisionAngle = 270.0f + (float)Math.atan(
						(receiveZ - attackZ) /
						(attackX - receiveX)) * ONE_EIGHTY_OVER_PI;
			}
		}
		return collisionAngle;
	}
	
	private void gameObjectFall(GameObject gameObject) {
    	float fallX = gameObject.currentPosition.x;
    	float fallY = gameObject.currentPosition.y;
    	float fallZ = gameObject.currentPosition.z;
    	float fallR = gameObject.currentPosition.r;
    	
    	fallX -= (float)Math.sin(fallR * PI_OVER_180) * gameObject.magnitude * 20.0f;
    	fallZ -= (float)Math.cos(fallR * PI_OVER_180) * gameObject.magnitude * 20.0f;
//    	fallX -= (float)Math.sin(fallR * PI_OVER_180) * receiveGameObject.magnitude * 10.0f;
//    	fallZ -= (float)Math.cos(fallR * PI_OVER_180) * receiveGameObject.magnitude * 10.0f;
    	
    	gameObject.yMoveMagnitude = 0.05f;
//    	gameObject.yMoveMagnitude = 0.1f;
    	
    	gameObject.currentPosition.set(fallX, fallY, fallZ, fallR);
//    	receiveGameObject.setFallPosition(fallX, fallY, fallZ, fallR);
    	
//    	gameObject.lastReceivedHitTime = gameTime;
//        
//    	gameObject.currentState = CurrentState.FALL;
	}
	
    public void setPauseOnAttack(boolean pause) {
        mPauseOnAttack = pause;
    }
    
    public void setPauseOnAttackTime(float seconds) {
        mPauseOnAttackTime = seconds;
    }
    
//    public void setBounceOnHit(boolean bounce) {
//        mBounceOnHit = bounce;
//    }
    
    public void setBounceMagnitude(float magnitude) {
        mBounceMagnitude = magnitude;
    }
    
//    public void setInvincibleTime(float time) {
//        mInvincibleAfterHitTime = time;
//    }
    
    public void setDieWhenCollected(boolean die) {
        mDieOnCollect = true;
    }
    
    public void setDieOnAttack(boolean die) {
        mDieOnAttack = die;
    }
    
//    public void setInvincible(boolean invincibleValue) {
//    	invincible = invincibleValue;
////        mInvincible = invincible;
//    }
      
//    public void setInventoryUpdate(InventoryComponent.UpdateRecord update) {
//        mInventoryUpdate = update;
//    }
    
//    public void setSpawnGameEventOnHit(HitType hitType, int gameFlowEventType, int indexData) {
//        mGameEventHitType = hitType;
//        mGameEventOnHit = gameFlowEventType;
//        mGameEventIndexData = indexData;
//        if (hitType == HitType.NO_HIT) {
//        	// The game event has been cleared, so reset the timer blocking a
//        	// subsequent event.
//        	mLastGameEventTime = -1.0f;
//        }
//    }
//
//    public final void setForceInvincible(boolean force) {
//        mForceInvincibility = force;
//    }
//    
//    public final void setTakeHitSound(int hitType, SoundSystem.Sound sound) {
//    	mReceivedHitSoundHitType = hitType;
//        mReceivedHitSound = sound;
//    }
//    
//    public final void setDealHitSound(HitType hitType, SoundSystem.Sound sound) {
//        mDealHitSound = sound;
//        mDealHitSoundHitType = hitType;
//    }
//    
//    public final void setSpawnOnDealHit(HitType hitType, Type objectType, boolean alignToVictimX,
//            boolean alignToVictimZ) {
//        mSpawnOnDealHitObjectType = objectType;
//        mSpawnOnDealHitHitType = hitType;
//        mAlignDealHitObjectToVictimX = alignToVictimX;
////        mAlignDealHitObjectToVictimY = alignToVicitmY;
//        mAlignDealHitObjectToVictimZ = alignToVictimZ;
//    }   
}
