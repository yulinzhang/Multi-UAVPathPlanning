/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.util.Random;
import java.util.Vector;
import uav.UAVBase;
import util.ConflictCheckUtil;
import util.ObtacleUtil;
import world.Circle;
import world.model.Obstacle;
import world.model.Target;
import world.model.Threat;

/**
 *
 * @author boluo
 */
public class InitConfig {


    private int enemy_num;
    private int target_num;
    private int attacker_num;
    private int scout_num;
    private int threat_num;

    private Vector<Obstacle> obstacles;
    private Vector<Target> targets;
    private Vector<Threat> threats;

    private UAVBase uav_base;

    //robot coordinates, robot_coordinates[1][0], robot_coordinates[1][1] represents the x, y coordinate of robot 1
    private float attacker_patrol_range = 100;
    private float threat_radius = 100;

    private int bound_width = 800;
    private int bound_height = 600;
    
    public static boolean obstacles_customized=false;

    

    public InitConfig(int enemy_num, int target_num, int attacker_num, int scout_num, int threat_num, UAVBase uav_base) {
        this.enemy_num = enemy_num;
        this.target_num = target_num;
        this.attacker_num = attacker_num;
        this.scout_num = scout_num;
        this.threat_num = threat_num;
        this.uav_base = uav_base;
        
        initObstacles();
        initThreats();
        initTargets();
    }
    
    public InitConfig()
    {
        this.enemy_num=0;
        this.target_num=1;
        this.attacker_num=0;
        this.scout_num=1;
        this.threat_num=0;
        float[] coordinate=new float[]{0,0};
        UAVBase uav_base=new UAVBase(coordinate,100,100);
        this.uav_base=uav_base;
        initObstacles();
        initThreats();
        initTargets();
//        initTargetAndEnemyUAV();
    }

    
    private void initTargets() {
        targets = new Vector<Target>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < target_num; i++) {
            float coordinate_x = 0;
            float coordinate_y = 0;
            boolean found = false;
            while (!found) {
                coordinate_x = random.nextFloat()*(bound_width-2*attacker_patrol_range)+attacker_patrol_range;
                coordinate_y = random.nextFloat()*(bound_height-2*attacker_patrol_range)+attacker_patrol_range;
//                coordinate_x=400;
//                coordinate_y=400;
                found = !ConflictCheckUtil.checkPointInObstaclesAndThreats(obstacles,threats,coordinate_x, coordinate_y);
            }
            Target target = new Target(i, new float[]{coordinate_x, coordinate_y});
            targets.add(target);
        }
    }

    private void initConfigurationFromParameterConfiguration() {
        this.attacker_num = UserParameterConfig.ATTACKER_NUM;
        this.scout_num = UserParameterConfig.SCOUT_NUM;
        this.enemy_num = UserParameterConfig.ENEMY_UAV_NUM;
        this.threat_num = UserParameterConfig.THREAT_NUM;
        this.target_num = UserParameterConfig.TARGET_NUM;
    }

    private void initObstacles() {
        if (UserParameterConfig.EXTERNAL_KML_FILE_PATH == null) {
            obstacles = ObtacleUtil.readObstacleFromResourceKML("/resources/Obstacle.kml");
        } else {
            obstacles = ObtacleUtil.readObstacleFromExternalKML(UserParameterConfig.EXTERNAL_KML_FILE_PATH);
        }
    }

    private void initThreats() {
        threats=new Vector<Threat>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < threat_num; i++) {
            float coordinate_x = 0;
            float coordinate_y = 0;
            boolean found = false;
            while (!found) {
                coordinate_x = random.nextFloat()*(bound_width-2*threat_radius)+threat_radius;
                coordinate_y = random.nextFloat()*(bound_height-2*threat_radius)+threat_radius;
                found = !ConflictCheckUtil.checkPointInObstaclesAndThreats(obstacles,threats, coordinate_x, coordinate_y);
            }

            Circle circle = new Circle(coordinate_x, coordinate_y, threat_radius);
            Threat threat = new Threat(circle, i);
            threats.add(threat);
        }
        
    }

    public int getEnemy_num() {
        return enemy_num;
    }

    public void setEnemy_num(int enemy_num) {
        this.enemy_num = enemy_num;
    }

    public int getTarget_num() {
        return target_num;
    }

    public void setTarget_num(int target_num) {
        this.target_num = target_num;
    }

    public int getAttacker_num() {
        return attacker_num;
    }

    public void setAttacker_num(int attacker_num) {
        this.attacker_num = attacker_num;
    }

    public int getScout_num() {
        return scout_num;
    }

    public void setScout_num(int scout_num) {
        this.scout_num = scout_num;
    }

    public int getThreat_num() {
        return threat_num;
    }

    public void setThreat_num(int threat_num) {
        this.threat_num = threat_num;
    }

    public Vector<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(Vector<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public Vector<Target> getTargets() {
        return targets;
    }

    public void setTargets(Vector<Target> targets) {
        this.targets = targets;
    }

    public Vector<Threat> getThreats() {
        return threats;
    }

    public void setThreats(Vector<Threat> threats) {
        this.threats = threats;
    }

    public UAVBase getUav_base() {
        return uav_base;
    }

    public void setUav_base(UAVBase uav_base) {
        this.uav_base = uav_base;
    }

    public float getAttacker_patrol_range() {
        return attacker_patrol_range;
    }

    public void setAttacker_patrol_range(float attacker_patrol_range) {
        this.attacker_patrol_range = attacker_patrol_range;
    }

    public float getThreat_radius() {
        return threat_radius;
    }

    public void setThreat_radius(float threat_radius) {
        this.threat_radius = threat_radius;
    }

    public int getBound_width() {
        return bound_width;
    }

    public void setBound_width(int bound_width) {
        this.bound_width = bound_width;
    }

    public int getBound_height() {
        return bound_height;
    }

    public void setBound_height(int bound_height) {
        this.bound_height = bound_height;
    }
    

}
