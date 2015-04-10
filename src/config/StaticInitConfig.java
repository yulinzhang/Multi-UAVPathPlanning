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

    public static Integer ATTACKER_NUM =3; //The number of our attackers
    public static Integer SCOUT_NUM = 1; //The number of our attackers
    public static Integer ENEMY_UAV_NUM = 0; //The number of enemy uavs
    public static Integer THREAT_NUM = 3; //The number of enemy threats

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
    public static String ROBOT_ONTOLOGY_TEMPLATE_FILE_PATH="D:\\KingSoft\\Dissertation\\Simulator\\ontology-owl\\robot_ontology_template.owl";

    public static int SIDE_A = 0;
    public static int SIDE_B = 1;

    public static Threat NON_ROLE = null;

    //Extend towards the goal with probability
    public static float rrt_goal_toward_probability = 0.8f;
    public static int rrt_iteration_times = 1000;
    //Set a safe distance from the target point
    public static int SAFE_DISTANCE_FOR_TARGET=5;
    public static int SAFE_DISTANCE_FOR_CONFLICT=5;
    public static int rrt_planning_times_for_each_uav=4;

    //how much time it takes to drive the simulaiton to next time step
    public static int INIT_SIMULATION_DELAY = 100; //milliseconds,this value will be set according to the user's simulation speed
    public static Timer SIMULATION_WITH_UI_TIMER; //simulation Timer

    public static boolean SHOW_PLANNED_PATH = true;
    public static boolean SHOW_PLANNED_TREE = true;
    public static boolean SHOW_HISTORY_PATH = false;
    public static boolean SHOW_FOG_OF_WAR = true;
    
    public static int STATIC_THREAT_TYPE = 0; //Distinguish between types of threats
    public static int DYNAMIC_THREAT_TYPE = 1;
    
    public static String UAV_KNOWLEDGE="UAV知识";
    public static String THREAT_INFO="威胁信息";
    public static String CONFLICT_INFO="冲突信息";
    public static String OBSTACLE_INFO="障碍信息";
    
    public static String THREAT_NAME="威胁";
    public static String OBSTACLE_NAME="障碍";
    public static String CONFLICT_NAME="冲突UAV";
    
    //Distinguish between types of information-sharing
    public static int BROADCAST_INFOSHARE=0; 
    public static int NONE_INFORSHARE=1;
    public static int REGISTER_BASED_INFORSHARE=2;
}
