/*
 * Copyright © 2012 FrostBlade LLC
 */

package com.frostbladegames.basestation9;



/**
 * Enum Group, Type, and CurrentState Values for GameObject and Component Reference
 */
public final class GameObjectGroups {
	
    /** Current Action and HitType State for Component Reference. Component default state = MOVE. */
    public enum CurrentState {
        INVALID,
        INTRO,
        LEVEL_START,
//        IDLE,
        MOVE,
        NO_HIT,
        HIT,
        BOUNCE,
        FROZEN,
        FALL,
        PLATFORM_SECTION_START,
        PLATFORM_SECTION_END,
//        PLATFORM_SECTION_EXIT,
        ELEVATOR,
        ELEVATOR_EXIT,
        ELEVATOR_ENEMY_BARRIER,
        COLLECT,
//        DEPRESS,
        WAIT_FOR_DEAD,
        DEAD,
        LEVEL_END,
        GAME_END
    }
    
//    /** GameObject1 Received HitType upon Collision with GameObject2 */
//    public enum HitType {
//    	NO_HIT,		// No Type or No Hit
//    	BOUNCE,		// Collision will bounce the Dynamic GameObject back at calculated angle
//        HIT,		// Standard Hit. Life is reduced by HitDamage.
//        FREEZE,		// Standard Hit damage plus Freeze duration.
//        FALL,		// Collision with Edge LineSegment. Instant Death followed by Fall Animation.
//        DEATH,		// Instant Death Hit
//        COLLECT,	// Collision with Collectable GameObject is collected
//        TELEPORT,	// Collision teleports Droid from current Section Platform End to next Section Platform Start
//        ELEVATOR,	// Collision moves the Elevator vertically and/or horizontally to next Section or Level
//        DEPRESS,	// Collision with interactive game item (e.g. floor button)
//        LEVEL_END	// Collision with PLATFORM_END
//    }
    
    /** Generic GameObject Group */
    public enum Group {
        INVALID (-1),
        BACKGROUND_01 (1),
        BACKGROUND_02 (2),
        BACKGROUND_03 (3),
        BACKGROUND_04 (4),
        BACKGROUND_05 (5),
        BACKGROUND_06 (6),
        BACKGROUND_07 (7),
        BACKGROUND_08 (8),
        BACKGROUND_09 (9),
        BACKGROUND_WALL_01 (11),
        BACKGROUND_WALL_02 (12),
        BACKGROUND_WALL_03 (13),
        BACKGROUND_WALL_04 (14),
        BACKGROUND_WALL_05 (15),
        BACKGROUND_WALL_06 (16),
        BACKGROUND_WALL_07 (17),
        BACKGROUND_WALL_08 (18),
        BACKGROUND_WALL_09 (19),
        FAR_BACKGROUND (90),
        DROID (100),
        DROID_WEAPON (101),
        DROID_LASER (110),
        ENEMY (120),
        ENEMY_LASER (170),
        ASTRONAUT (190),
        PLATFORM_LEVEL_START (200),
        PLATFORM_LEVEL_END (201),
        PLATFORM_SECTION_START (202),
        PLATFORM_SECTION_END (203),
        PLATFORM_ELEVATOR (204),
//      PLATFORM (150),
        ITEM (220),
        SPECIAL_EFFECT (300),
        SPLASHSCREEN (400);
        
        private final int mIndex;
        
        Group(int index) {
            this.mIndex = index;
        }
        
        public int index() {
            return mIndex;
        }
        
        public static Group indexToType(int index) {
            final Group[] valuesArray = values();
            Group foundGroup = INVALID;
            for (int x = 0; x < valuesArray.length; x++) {
                Group group = valuesArray[x];
                if (group.mIndex == index) {
                    foundGroup = group;
                    break;
                }
            }
            return foundGroup;
        }
    }
    
    /** Specific GameObject Type for each Group */
    public enum Type {
        INVALID (-1),
        
        // Section Type for Background, FarBackground, and Platform Groups
        SECTION_00 (0),
        SECTION_01 (1),
        SECTION_02 (2),
        SECTION_03 (3),
        SECTION_04 (4),
        SECTION_05 (5),
        SECTION_06 (6),
        SECTION_07 (7),
        SECTION_08 (8),
        SECTION_09 (9),
        
        // Background Item Group
        WALL_LASER_01 (11),
        WALL_LASER_02 (12),
        WALL_LASER_03 (13),
        WALL_LASER_04 (14),
        WALL_LASER_05 (15),
        WALL_LASER_06 (16),
        WALL_LASER_07 (17),
        WALL_LASER_08 (18),
        WALL_LASER_09 (19),
        
        WALL_POST_01 (21),
        WALL_POST_02 (22),
        WALL_POST_03 (23),
        WALL_POST_04 (24),
        WALL_POST_05 (25),
        WALL_POST_06 (26),
        WALL_POST_07 (27),
        WALL_POST_08 (28),
        WALL_POST_09 (29),
        
//        // Far Background Group
//        FAR_BACKGROUND_00 (90),
//        FAR_BACKGROUND_01 (91),
//        FAR_BACKGROUND_02 (92),
//        FAR_BACKGROUND_03 (93),
//        FAR_BACKGROUND_04 (94),
//        FAR_BACKGROUND_05 (95),
//        FAR_BACKGROUND_06 (96),
//        FAR_BACKGROUND_07 (97),
//        FAR_BACKGROUND_08 (98),
//        FAR_BACKGROUND_09 (99),
        
        // Droid Group
        DROID (100),
        
        // Droid Weapon Group
        DROID_WEAPON_LASER_STD (101),
        DROID_WEAPON_LASER_PULSE (102),
        DROID_WEAPON_LASER_EMP (103),
        DROID_WEAPON_LASER_GRENADE (104),
        DROID_WEAPON_LASER_ROCKET (105),
//        DROID_WEAPON_LASER_ELECTRIC_WALL (106),
        
        // Droid Laser Group
        DROID_LASER_STD (111),
        DROID_LASER_PULSE (112),
        DROID_LASER_EMP (113),
        DROID_LASER_GRENADE (114),
        DROID_LASER_ROCKET (115),
//        DROID_LASER_ELECTRIC_WALL (116),
        
		// Network Players

        // Enemy Group
        ENEMY_EM_OT (120),
        ENEMY_EM_OW (121),
        ENEMY_EM_SL (122),
        ENEMY_HD_FL (123),
        ENEMY_HD_OL (124),
        ENEMY_HD_OT (125),
        ENEMY_HD_OW (126),
        ENEMY_HD_SL (127),
        ENEMY_HD_TL (128),
        ENEMY_HD_TT (129),
        ENEMY_HD_TW (130),
        ENEMY_LC_FM (131),
        ENEMY_LC_OT (132),
        ENEMY_LC_SL (133),
        ENEMY_LC_TT (134),
        ENEMY_LS_FM (135),
        ENEMY_LS_TT (136),
        ENEMY_TA_FL (137),
        ENEMY_DR_TT (138),
        ENEMY_EM_OW_BOSS (139),
        ENEMY_EM_SL_BOSS (140),
        ENEMY_HD_TL_BOSS (141),
        ENEMY_HD_TT_BOSS (142),
        ENEMY_LC_OT_BOSS (143),
        ENEMY_LC_SL_BOSS (144),
        ENEMY_LS_TT_BOSS (145),
        ENEMY_TA_FL_BOSS (146),
        ENEMY_DR_TT_BOSS (147),
        
        // Enemy Laser Group
        ENEMY_LASER_STD (170),
        ENEMY_LASER_EMP (171),
        ENEMY_BOSS_LASER_STD (172),
        ENEMY_BOSS_LASER_EMP (173),
        ENEMY_BOSS_LASER_SMALL_FLYING_ENEMY (174),
        
        // Astronaut Group
        ASTRONAUT_PRIVATE (190),
        ASTRONAUT_SERGEANT (191),
        ASTRONAUT_CAPTAIN (192),
        ASTRONAUT_GENERAL (193),
        
//        // Platform Group
//        PLATFORM_LEVEL_START (150),
//        PLATFORM_LEVEL_END (151),
//        PLATFORM_SECTION_START (152),
//        PLATFORM_SECTION_END (153),
//        PLATFORM_ELEVATOR (154),
        
        // Item Group
        ITEM_CRATE_WOOD (220),
        ITEM_CRATE_METAL (221),
        ITEM_LIGHT_BEACON (222),
        ITEM_PISTON_ENGINE (223),
        ITEM_TABLE (224),
        ITEM_TABLE_COMPUTER (225),
        ITEM_SPACESHIP (226),
        ITEM_SPACESHIP_DOOR (227),
        
        // Special Effect Group
        EXPLOSION (300),
        ELECTRIC_RING (301),
        ELECTRICITY (302),
        EXPLOSION_LARGE (303),
        EXPLOSION_RING (304),
        TELEPORT_RING (305),
        
        // Splashscreen
        SPLASHSCREEN_LOGO (400),
        SPLASHSCREEN_BACKGROUND (401),
        
        // Special Spawnable
//        CAMERA_BIAS(56),
//        FRAMERATE_WATCHER(57),
        
        // End
        OBJECT_COUNT(-1);
        
        private final int mIndex;
        
        Type(int index) {
            this.mIndex = index;
        }
        
        public int index() {
            return mIndex;
        }
        
        public static Type indexToType(int index) {
            final Type[] valuesArray = values();
            Type foundType = INVALID;
            for (int x = 0; x < valuesArray.length; x++) {
                Type type = valuesArray[x];
                if (type.mIndex == index) {
                    foundType = type;
                    break;
                }
            }
            return foundType;
        }
    }
    
    /** Type of bottom movement for Enemies for Component Reference. Component default state = INVALID. */
    public enum BottomMoveType {
        INVALID,
        MOUNT,
//        WALK_ONE_LEG,
//        WALK_TWO_LEGS,
//        SPIDER_WALK_FOUR_LEGS,
//        WALK_SIX_LEGS,
        SPIDER_LEGS,
        WHEELS_TREAD,
//        WHEELS_TREAD_ONE,
//        WHEELS_TREAD_TWO,
//        WHEELS_TREAD_FOUR,
        SPRING_LEGS,
//        SPRING_ONE_LEG,
//        SPRING_TWO_LEGS,
        FLY
    }
    
    public GameObjectGroups() {
    	
    }
}
