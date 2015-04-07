/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import world.model.Threat;
import world.model.Obstacle;
import config.NonStaticInitConfig;
import config.StaticInitConfig;
import java.util.ArrayList;
import java.util.TreeSet;
import world.uav.UAV;
import world.uav.UAVBase;
import util.ConflictCheckUtil;
import util.DistanceUtil;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
import world.model.Target;
import world.model.WorldKnowledge;

/**
 *
 * @author Yulin_Zhang
 */
public class World implements KnowledgeAwareInterface {

    /**
     * *
     * ----------environment settings----------------------------
     */
    //bound of the canvas x_left_up,y_left_up,x_right_down,y_right_down
    public static int bound_width = 800;
    public static int bound_height = 600;

    private int scout_num;
    private int enemy_num;
    private int threat_num;
    private int attacker_num;

    private float threat_radius = 100;
    private float attacker_patrol_range;

    //robot coordinates, robot_coordinates[1][0], robot_coordinates[1][1] represents the x, y coordinate of robot 1
    private UAVBase uav_base;
    public static WorldKnowledge kb;
    /**
     * * internal variables
     *
     */
    private ArrayList<UAV> attackers;
    private ArrayList<UAV> enemy_uavs;
    private ArrayList<UAV> scouts;
    private int time_step = 0; //times of simulation

    private float theta_increase_for_enemy_uav = (float) Math.PI / 40;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(World.class);

    /**
     * initiate world,contain paint,number of
     * attacker_num,scout_num,enemy_num...
     *
     * @param init_config
     */
    public World(NonStaticInitConfig init_config) {
        this.kb = new WorldKnowledge();
        initParameterFromInitConfig(init_config);
        initUAVs();
    }

    /**
     * init the parameter of battleground objects
     *
     * @param init_config
     */
    public void initParameterFromInitConfig(NonStaticInitConfig init_config) {
        this.bound_width = init_config.getBound_width();
        this.bound_height = init_config.getBound_height();

        this.attacker_num = init_config.getAttacker_num();
        this.scout_num = init_config.getScout_num();
        this.enemy_num = init_config.getEnemy_num();
        this.threat_num = init_config.getThreat_num();

        this.threat_radius = init_config.getThreat_radius();
        this.attacker_patrol_range = init_config.getAttacker_patrol_range();

        this.setThreats(init_config.getThreats());
        this.setObstacles(init_config.getObstacles());
        this.uav_base = init_config.getUav_base();
    }

    private void initScoutsAndAttackers() {
        float[] uav_base_coordinate = uav_base.getCoordinate();
        int uav_base_height = uav_base.getBase_height();
        int uav_base_width = uav_base.getBase_width();
        float[] uav_base_center = new float[2];
        uav_base_center[0] = uav_base_coordinate[0] + uav_base_width / 2;
        uav_base_center[1] = uav_base_coordinate[1] + uav_base_height / 2;
        for (int i = 0; i < attacker_num; i++) {
            UAV attacker = new UAV(i, this.getThreats().get(i), StaticInitConfig.SIDE_A, uav_base_center, null);
            attackers.add(attacker);
        }
        roleAssign(-1, -1);
    }

//    private void initEnemyUAV() {
//        for (int i = 0; i < enemy_num; i++) {
//            int target_to_protect = i % threat_num;
//            float[] target_coordinates = this.getThreats().get(target_to_protect).getCoordinates();
//            float[] attacker_coordinates = new float[2];
//            float theta_from_target = (float) (Math.random() * Math.PI * 2);
//            for (float dist = attacker_patrol_range; dist > 0; dist = dist / 2f) {
//                attacker_coordinates[1] = target_coordinates[1] + dist * (float) Math.sin(theta_from_target);
//                attacker_coordinates[0] = target_coordinates[0] + dist * (float) Math.cos(theta_from_target);
//                if (attacker_coordinates[0] < bound_width) {
//                    boolean available = !ConflictCheckUtil.checkPointInObstacles(this.getObstacles(), attacker_coordinates[0], attacker_coordinates[1]);
//                    if (available) {
//                        break;
//                    }
//                }
//            }
//            UAV enemy_uav = new UAV(i, this.getThreats().get(target_to_protect), StaticInitConfig.SIDE_B, attacker_coordinates, this.getObstacles());
//            enemy_uavs.add(enemy_uav);
//        }
//    }
    private void initUAVs() {
        this.scouts = new ArrayList<UAV>();
        this.attackers = new ArrayList<UAV>();
        this.enemy_uavs = new ArrayList<UAV>();
        initScoutsAndAttackers();
//        initEnemyUAV();
    }

    public void roleAssign(int attacker_index, int uav_assigned_role_index) {
        TreeSet<Integer> assigned_attacker = new TreeSet<Integer>();
        assigned_attacker.add(attacker_index);
        for (int i = 0; i < this.threat_num; i++) {
            Threat threat = this.kb.getThreats().get(i);
            if (i == uav_assigned_role_index) {
                for (int j = 0; j < this.attacker_num; j++) {
                    if (attacker_index == j) {
                        this.attackers.get(j).setTarget_indicated_by_role(threat);
                        break;
                    }
                }
                continue;
            }
            float min_dist = Float.MAX_VALUE;
            int attacker_index_to_assign = -1;
            UAV attacker_to_assign = null;
            for (int j = 0; j < this.attacker_num; j++) {
                UAV current_attacker = this.attackers.get(j);
                if (assigned_attacker.contains(j)) {
                    continue;
                }

                float dist_between_uav_and_role = DistanceUtil.distanceBetween(current_attacker.getCenter_coordinates(), threat.getCoordinates());
                if (dist_between_uav_and_role < min_dist) {
                    min_dist = dist_between_uav_and_role;
                    attacker_index_to_assign = j;
                    attacker_to_assign = current_attacker;
                }
            }
            if (attacker_index_to_assign != -1) {
                assigned_attacker.add(attacker_index_to_assign);
                attacker_to_assign.setTarget_indicated_by_role(threat);
            }
        }
    }

    private void planPathForAllAttacker() {
        for (UAV attacker : this.attackers) {
            attacker.pathPlan();
        }
    }

    private void updateAttackerCoordinate() {
        for (UAV attacker : this.attackers) {
            boolean moved = attacker.moveToNextWaypoint();
            Target uav_target = attacker.getRole_target();
            if (moved || uav_target == null) {
                continue;
            }
        }
    }

    private void detectEvent() {
        for (UAV attacker : this.attackers) {
            int obs_list_size = this.getObstacles().size();
            for (int i = 0; i < obs_list_size; i++) {
                Obstacle obs = this.getObstacles().get(i);
                if (!attacker.containsObstacle(obs) && obs.getMbr().intersects(attacker.getUav_radar().getBounds())) {
                    attacker.addObstacle(obs);
                    attacker.setNeed_to_replan(true);
                }
            }
            int threat_list_size = this.getThreats().size();
            for (int i = 0; i < threat_list_size; i++) {
                Threat threat = this.getThreats().get(i);
                if (!attacker.containsThreat(threat) && DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), threat.getCoordinates()) < attacker.getUav_radar().getRadius()) {
                    attacker.addThreat(threat);
                    attacker.setNeed_to_replan(true);
                }
            }
        }
    }

    private void registerInfoRequirement() {
    }

    private void shareInfoAfterRegistration() {
    }

    private void resetDecisionParameter() {
        for (UAV attacker : this.attackers) {
            attacker.setNeed_to_replan(false);
        }
    }

    public void updateAll() {
        detectEvent();
        registerInfoRequirement();
        shareInfoAfterRegistration();
        planPathForAllAttacker();
        updateAttackerCoordinate();
        resetDecisionParameter();
        this.time_step++;
    }

    public float getTotalHistoryPathLen() {
        float total_path_len = 0;
        for (UAV attacker : attackers) {
            total_path_len += attacker.getHistory_path().getPath_length();
        }
        return total_path_len;
    }

    @Override
    public ArrayList<Obstacle> getObstacles() {
        return this.kb.getObstacles();
    }

    public ArrayList<UAV> getAttackers() {
        return attackers;
    }

    public void setAttackers(ArrayList<UAV> attackers) {
        this.attackers = attackers;
    }

    public ArrayList<UAV> getScouts() {
        return scouts;
    }

    public void setScouts(ArrayList<UAV> scouts) {
        this.scouts = scouts;
    }

    public int getBound_width() {
        return bound_width;
    }

    public int getBound_height() {
        return bound_height;
    }

    public float getThreat_radius() {
        return threat_radius;
    }

    public float getAttacker_patrol_range() {
        return attacker_patrol_range;
    }

    public UAVBase getUav_base() {
        return uav_base;
    }

    @Override
    public ArrayList<Threat> getThreats() {
        return this.kb.getThreats();
    }

    public ArrayList<UAV> getEnemy_uavs() {
        return enemy_uavs;
    }

    @Override
    public ArrayList<Conflict> getConflicts() {
        return this.kb.getConflicts();
    }

    @Override
    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.kb.setObstacles(obstacles);
    }

    @Override
    public void setConflicts(ArrayList<Conflict> conflicts) {
        this.kb.setConflicts(conflicts);
    }

    @Override
    public void setThreats(ArrayList<Threat> threats) {
        this.kb.setThreats(threats);
    }

    @Override
    public void addObstacle(Obstacle obs) {
        this.kb.addObstacle(obs);
    }

    @Override
    public void addConflict(Conflict conflict) {
        this.kb.addConflict(conflict);
    }

    @Override
    public void addThreat(Threat threat) {
        this.kb.addThreat(threat);
    }

    @Override
    public boolean containsThreat(Threat threat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsObstacle(Obstacle obstacle) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
