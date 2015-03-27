/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import javax.swing.Timer;
import world.model.Target;

/**
 *
 * @author Yulin_Zhang
 */
public class UserParameterConfig {

    public static boolean SIMULATION_ON = true;

    public static Integer ATTACKER_NUM = 0;
    public static Integer SCOUT_NUM = 1;
    public static Integer THREAT_NUM = 1;
    public static Integer ENEMY_UAV_NUM = 0;
    public static Integer TARGET_NUM = 1;

    public static boolean VIEW_OBSTACLE = false;
    public static boolean VIEW_THREAT = false;
    public static boolean VIEW_ENEMY_UAV = false;
    public static boolean VIEW_TARGET = false;

    public static String VIEW_OBSTACLE_ACTION_COMMAND = "显示障碍";
    public static String VIEW_THREAT_ACTION_COMMAND = "显示威胁";
    public static String VIEW_ENEMY_UAV_ACTION_COMMAND = "显示敌方UAV";
    public static String VIEW_TARGET_ACTION_COMMAND = "显示目标";

    public static Integer MIN_SPINNER_VALUE = 0;
    public static Integer MAX_SPINNER_VALUE = 100;
    public static Integer STEP_SIZE_OF_SPINNER = 1;

    public static String EXTERNAL_KML_FILE_PATH = null;

    public static int SIDE_A = 0;
    public static int SIDE_B = 1;

    public static Target NON_ROLE =null;
    
    public static float rrt_goal_toward_probability=0.3f;
    public static int rrt_iteration_times=10000;
    
    //how much time it takes to drive the simulaiton to next time step
    public static int INIT_SIMULATION_DELAY=300;
    public static Timer SIMULATION_WITH_UI_TIMER;
    
    public static boolean SHOW_PLANNED_PATH=true;
    public static boolean SHOW_PLANNED_TREE=true;
    public static boolean SHOW_HISTORY_PATH=true;
    public static boolean SHOW_FOG_OF_WAR=true;
}
