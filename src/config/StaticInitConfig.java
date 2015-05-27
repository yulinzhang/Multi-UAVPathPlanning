/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import javax.swing.Timer;
import world.model.Threat;

/**
 *
 * @author Yulin_Zhang
 */
public class StaticInitConfig {

    public static boolean SIMULATION_ON = true; //enable simulation

    public static Integer ATTACKER_NUM = 10; //The number of our attackers
    public static Integer SCOUT_NUM = 1; //The number of our attackers
    public static Integer ENEMY_UAV_NUM = 0; //The number of enemy uavs
    public static Integer THREAT_NUM = 20; //The number of enemy threats

    public static boolean VIEW_OBSTACLE = false; //Determine whether an obstacle is detected
    public static boolean VIEW_THREAT = false; //Determine whether a threat is detected
    public static boolean VIEW_ENEMY_UAV = false; //Determine whether an enemy uav is detected

    public static String VIEW_OBSTACLE_ACTION_COMMAND = "显示障碍";
    public static String VIEW_THREAT_ACTION_COMMAND = "显示威胁";
    public static String VIEW_ENEMY_UAV_ACTION_COMMAND = "显示敌方UAV";

    public static Integer MIN_SPINNER_VALUE = 0; //range of spinner,0-100
    public static Integer MAX_SPINNER_VALUE = 100;
    public static Integer STEP_SIZE_OF_SPINNER = 1;

    public static String EXTERNAL_KML_FILE_PATH = null; //KML file path stored static obstacle

    public static int ATTACKER = 0;
    public static int SCOUT = 1;

    public static Threat NON_ROLE = null;

    //Extend towards the goal with probability
    public static float rrt_goal_toward_probability = 0.7f;
    public static int rrt_iteration_times = 3500;
    //Set a safe distance from the target point
    public static int SAFE_DISTANCE_FOR_TARGET = 3;
    public static int SAFE_DISTANCE_FOR_CONFLICT = 2;
    public static int rrt_planning_times_for_attacker = 5;
    public static int rrt_planning_times_for_scout = 1;

    //how much time it takes to drive the simulaiton to next time step
    public static int INIT_SIMULATION_DELAY = 400; //milliseconds,this value will be set according to the user's simulation speed
    public static Timer SIMULATION_WITH_UI_TIMER; //simulation Timer
    public static float SIMULATION_SPEED = 8;

    public static int scout_radar_radius = 50; //The scout range of radar
    public static int attacker_radar_radius = 20;

    public static boolean SHOW_PLANNED_PATH = true;
    public static boolean SHOW_PLANNED_TREE = true;
    public static boolean SHOW_HISTORY_PATH = false;
    public static boolean SHOW_FOG_OF_WAR = true;

    public static boolean debug_rrt = false;

    public static int STATIC_THREAT_TYPE = 0; //Distinguish between types of threats
    public static int DYNAMIC_THREAT_TYPE = 1;

    public static float maximum_threat_movement_length = 20;

    public static String UAV_KNOWLEDGE = "UAV Knowledge";
    public static String THREAT_INFO = "Threat Info";
    public static String CONFLICT_INFO = "Conflict Info";
    public static String OBSTACLE_INFO = "Obstacle Info";

    public static String THREAT_NAME = "Threat";
    public static String OBSTACLE_NAME = "Obstacle";
    public static String CONFLICT_NAME = "Conflicted UAV";

    //Distinguish between types of information-sharing
    public static int BROADCAST_INFOSHARE = 0;
    public static int NONE_INFORSHARE = 1;
    public static int REGISTER_BASED_INFORSHARE = 2;

    public static int SPEED_OF_ATTACKER_ON_TASK = 8;
    public static int SPEED_OF_ATTACKER_ON_DESTROYING_THREAT = 4;
    public static int SPEED_OF_SCOUT = 4;
    public static int SPEED_OF_ATTACKER_IDLE = 4;

    public static boolean UI_PARAMETER_CONFIG = false;

    public static int LOCKED_TIME_STEP_UNTIL_DESTROYED = 20;
}
