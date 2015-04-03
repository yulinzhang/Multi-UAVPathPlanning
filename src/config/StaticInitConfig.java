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

    public static boolean SIMULATION_ON = true;

    public static Integer ATTACKER_NUM =3;
    public static Integer SCOUT_NUM = 1;
    public static Integer ENEMY_UAV_NUM = 0;
    public static Integer THREAT_NUM = 3;

    public static boolean VIEW_OBSTACLE = false;
    public static boolean VIEW_THREAT = false;
    public static boolean VIEW_ENEMY_UAV = false;

    public static String VIEW_OBSTACLE_ACTION_COMMAND = "显示障碍";
    public static String VIEW_THREAT_ACTION_COMMAND = "显示威胁";
    public static String VIEW_ENEMY_UAV_ACTION_COMMAND = "显示敌方UAV";

    public static Integer MIN_SPINNER_VALUE = 0; //range of spinner,0-100
    public static Integer MAX_SPINNER_VALUE = 100;
    public static Integer STEP_SIZE_OF_SPINNER = 1;

    public static String EXTERNAL_KML_FILE_PATH = null; //KML file path stored static obstacle

    public static int SIDE_A = 0;
    public static int SIDE_B = 1;

    public static Threat NON_ROLE = null;

    public static float rrt_goal_toward_probability = 0.8f;
    public static int rrt_iteration_times = 1000;

    //how much time it takes to drive the simulaiton to next time step
    public static int INIT_SIMULATION_DELAY = 300; //milliseconds,this value will be set according to the user's simulation speed
    public static Timer SIMULATION_WITH_UI_TIMER; //simulation Timer

    public static boolean SHOW_PLANNED_PATH = true;
    public static boolean SHOW_PLANNED_TREE = true;
    public static boolean SHOW_HISTORY_PATH = true;
    public static boolean SHOW_FOG_OF_WAR = true;
    
    public static int STATIC_THREAT_TYPE = 0;
    public static int DYNAMIC_THREAT_TYPE = 1;
    
    public static String UAV_KNOWLEDGE="UAV知识";
    public static String THREAT_INFO="威胁信息";
    public static String CONFLICT_INFO="冲突信息";
    public static String OBSTACLE_INFO="障碍信息";
    
    public static String THREAT_NAME="威胁";
    public static String OBSTACLE_NAME="障碍";
    public static String CONFLICT_NAME="冲突UAV";
}
