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
import util.BoundUtil;
import util.ConflictCheckUtil;
import world.uav.UAV;
import world.uav.UAVBase;
import util.DistanceUtil;
import world.model.Conflict;
import world.model.OntologyBasedKnowledge;
import world.model.Target;
import world.model.shape.Point;
import world.uav.UAVPath;

/**
 *
 * @author Yulin_Zhang
 */
public class World {

    /**
     * *
     * ----------environment settings----------------------------
     */
    //bound of the canvas x_left_up,y_left_up,x_right_down,y_right_down
    public static int bound_width = 800;
    public static int bound_height = 600;

    private int scout_num; //The number of our scouts
    private int enemy_num;  //The number of enemy uavs
    private int threat_num; //The number of enemy threats
    private int attacker_num; //The number of our attackers

    private float threat_radius = 100;
    private float attacker_patrol_range; //The patrol range of attacker 

    private int inforshare_algorithm = 0; //distinction between information-sharing algrithm

    //robot coordinates, robot_coordinates[1][0], robot_coordinates[1][1] represents the x, y coordinate of robot 1
    private UAVBase uav_base;
    /**
     * * internal variables
     *
     */
    public static ArrayList<UAV> attackers;
    private ArrayList<UAV> enemy_uavs;
    private ArrayList<UAV> scouts;
    private ControlCenter control_center;

    private ArrayList<Conflict> conflicts;
    private ArrayList<Threat> threats;
    private ArrayList<Obstacle> obstacles;

    private int time_step = 0; //times of simulation

    private MessageDispatcher msg_dispatcher;

    private int conflict_times = 0;

    private float theta_increase_for_enemy_uav = (float) Math.PI / 40;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(World.class);

    /**
     * initiate world,contain paint,number of
     * attacker_num,scout_num,enemy_num...
     *
     * @param init_config
     */
    public World(NonStaticInitConfig init_config) {
//        World.kb = new WorldKnowledge();//OntologyBasedKnowledge();WorldKnowledge
        this.conflicts = new ArrayList<Conflict>();
        this.control_center = new ControlCenter(new OntologyBasedKnowledge(), attackers);
        initParameterFromInitConfig(init_config);
        initUAVs();
        this.control_center.setAttackers(attackers);
        this.control_center.setObstacles(obstacles);
        this.control_center.setThreats(threats);
        this.control_center.setConflicts(conflicts);
        control_center.roleAssign(-1, -1); //initialize role assignment
    }

    /**
     * init the parameter of battleground objects
     *
     * @param init_config
     */
    public void initParameterFromInitConfig(NonStaticInitConfig init_config) {
        World.bound_width = init_config.getBound_width();
        World.bound_height = init_config.getBound_height();
        this.inforshare_algorithm = init_config.getInforshare_algorithm();

        this.attacker_num = init_config.getAttacker_num();
        this.scout_num = init_config.getScout_num();
        this.enemy_num = init_config.getEnemy_num();
        this.threat_num = init_config.getThreat_num();

        this.threat_radius = init_config.getThreat_radius();
        this.attacker_patrol_range = init_config.getAttacker_patrol_range();

        this.setThreats(init_config.getThreats());
        this.setObstacles(init_config.getObstacles());
        this.uav_base = init_config.getUav_base();

        //share information in different ways
        if (this.inforshare_algorithm == StaticInitConfig.BROADCAST_INFOSHARE) {
            this.msg_dispatcher = new BroadcastMessageDispatcher(control_center);
        } else if (this.inforshare_algorithm == StaticInitConfig.REGISTER_BASED_INFORSHARE) {
            this.msg_dispatcher = new RegisteredMessageDispatcher(control_center);
        } else if (this.inforshare_algorithm == StaticInitConfig.NONE_INFORSHARE) {
            this.msg_dispatcher = new DummyMessageDispatcher(control_center);
        }
    }

    /**
     * initialize the nuber of sounts and attackers and assign role for uavs
     */
    private void initScoutsAndAttackers() {
        float[] uav_base_coordinate = uav_base.getCoordinate();
        int uav_base_height = uav_base.getBase_height();
        int uav_base_width = uav_base.getBase_width();
        float[] uav_base_center = new float[2];
        uav_base_center[0] = uav_base_coordinate[0] + uav_base_width / 2;
        uav_base_center[1] = uav_base_coordinate[1] + uav_base_height / 2;
        for (int i = 0; i < attacker_num; i++) {
            Threat threat = this.getThreatsForUIRendering().get(i);
            UAV attacker = new UAV(i, threat, StaticInitConfig.SIDE_A, uav_base_center, null);
            attackers.add(attacker);
        }
    }

    /**
     * initiate all uavs
     */
    private void initUAVs() {
        this.scouts = new ArrayList<UAV>();
        this.attackers = new ArrayList<UAV>();
        this.enemy_uavs = new ArrayList<UAV>();
        initScoutsAndAttackers();
//        initEnemyUAV();
    }

    /**
     * path planning for attackers
     */
    private void planPathForAllAttacker() {
        for (UAV attacker : this.attackers) {
            attacker.pathPlan();
        }
    }

    /**
     *
     */
    private void checkConflict() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker1 = this.attackers.get(i);
            float[] attacker1_coord = attacker1.getCenter_coordinates();
            for (int j = i + 1; j < this.attacker_num; j++) {
                UAV attacker2 = this.attackers.get(j);
                if (attacker2.isTarget_reached()) {
                    continue;
                }
                float[] attacker2_coord = attacker2.getCenter_coordinates();
                if (DistanceUtil.distanceBetween(attacker1_coord, attacker2_coord) < StaticInitConfig.SAFE_DISTANCE_FOR_CONFLICT) {
                    conflict_times++;
                    attacker1.setNeed_to_replan(true);
                    logger.debug("conflict:" + conflict_times);
                }
            }
        }
    }

    private void checkThreatTerminate() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker = this.attackers.get(i);
            int threat_index = attacker.getTarget_indicated_by_role().getIndex();
            if (threat_index == -1) {
                if (attacker.isTarget_reached() && !attacker.isMoved_at_last_time()) {
                    float[] dummy_threat_coord = this.randomGoalForThreat(attacker.getCenter_coordinates());
                    Threat dummy_threat = new Threat(-1, dummy_threat_coord, 0, 0);
                    attacker.setTarget_indicated_by_role(dummy_threat);
                    attacker.setNeed_to_replan(true);
                    attacker.setSpeed(1);
                }
                continue;
            }

            ArrayList<Threat> threats = this.getThreatsForUIRendering();
            for (int j = 0; j < threats.size(); j++) {
                Threat threat = threats.get(j);
                if (threat_index == threat.getIndex()) {
                    if (DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), threat.getCoordinates()) < StaticInitConfig.SAFE_DISTANCE_FOR_TARGET) {
                        threat.setEnabled(false);
                        attacker.setTarget_reached(true);
                        float[] dummy_threat_coord = this.randomGoalForThreat(attacker.getCenter_coordinates());
                        Threat dummy_threat = new Threat(-1, dummy_threat_coord, 0, 0);
                        attacker.setTarget_indicated_by_role(dummy_threat);
                        attacker.setNeed_to_replan(true);
                        attacker.setSpeed(1);
                    }
                }
            }
        }
    }

    /**
     * undate coordinate of attackers
     */
    private void updateAttackerCoordinate() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker = this.attackers.get(i);
            boolean moved = attacker.moveToNextWaypoint();
//            if (!moved) {
////                attacker.setNeed_to_replan(true);
//            }
        }
    }

    /**
     * During patrol,the uav detect event by radar or share information
     */
    private void detectEvent() {
        for (UAV attacker : this.attackers) {
            int obs_list_size = this.getObstaclesForUIRendering().size();
            for (int i = 0; i < obs_list_size; i++) {
                Obstacle obs = this.getObstaclesForUIRendering().get(i);
                if (!attacker.containsObstacle(obs) && obs.getMbr().intersects(attacker.getUav_radar().getBounds())) {
                    attacker.addObstacle(obs);
                    attacker.setNeed_to_replan(true);
                }
            }
            int threat_list_size = this.getThreatsForUIRendering().size();
            for (int i = 0; i < threat_list_size; i++) {
                Threat threat = this.getThreatsForUIRendering().get(i);
                float dist_from_attacker_to_threat = DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), threat.getCoordinates());
//                if (dist_from_attacker_to_threat < StaticInitConfig.SAFE_DISTANCE_FOR_TARGET) {
//                    if (threat.getIndex() == attacker.getTarget_indicated_by_role().getIndex()) {
//                        attacker.setTarget_reached(true);
//                        threat.setEnabled(false);
//                    }
//                } else 
                if (!attacker.containsThreat(threat) && dist_from_attacker_to_threat < attacker.getUav_radar().getRadius()) {
                    attacker.addThreat(threat);
                    attacker.setNeed_to_replan(true);
                }
            }
        }
    }

    private void mandatoryReplan() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker = this.attackers.get(i);
            attacker.setNeed_to_replan(true);
            attacker.setReplanned_at_current_time_step(true);
        }
    }

    /**
     * register information requirement for attackers,this mothod is used to
     * information sharing rapidly
     */
    private void registerInfoRequirement() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker = this.attackers.get(i);
            Target target = attacker.getTarget_indicated_by_role();
            float[] attacker_coord = attacker.getCenter_coordinates();
            this.msg_dispatcher.register(attacker.getIndex(), attacker_coord, target);
        }
    }

    /**
     * information sharing
     */
    private void shareInfoAfterRegistration() {
        this.msg_dispatcher.decideAndSumitMsgToSend();
        this.msg_dispatcher.dispatch();
    }

    private void resetDecisionParameter() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker = this.attackers.get(i);
            attacker.setNeed_to_replan(false);
        }
    }

    /**
     * unpdate conflict
     */
    private void updateConflict() {
        for (int i = 0; i < this.attacker_num; i++) {
            UAV attacker = this.attackers.get(i);
            if (attacker.isReplanned_at_current_time_step()) {
                Conflict conflict = new Conflict(attacker.getIndex(), attacker.getFuturePath().getWaypointsAsLinkedList(), this.time_step, StaticInitConfig.SAFE_DISTANCE_FOR_CONFLICT);
                this.addConflict(conflict);
            }
        }
    }

    private void updateThreatCoordinate() {
        ArrayList<Threat> threats = this.getThreatsForUIRendering();
        for (int i = 0; i < threats.size(); i++) {
            Threat threat = threats.get(i);
            boolean moved = threat.moveToNextWaypoint();
            if (!moved) {
                planPathForThreat(threat);
            }
        }
        this.control_center.setThreats(threats);
    }

    private void planPathForThreat(Threat threat) {
        if (threat.getSpeed() == 0) {
            return;
        }
        UAVPath path = new UAVPath();
        float coord_x, coord_y;
        coord_x = threat.getCoordinates()[0];
        coord_y = threat.getCoordinates()[1];
        float initial_angle = threat.getCurrent_angle();
        float threat_angle = initial_angle;
        float speed = threat.getSpeed();
        boolean point_conflicted_with_obstacles;
        Point point = new Point(coord_x, coord_y, threat_angle);
        path.addWaypointToEnd(point);
        while (true) {
            coord_x += speed * (float) Math.cos(threat_angle);
            coord_y += speed * (float) Math.sin(threat_angle);
            point_conflicted_with_obstacles = ConflictCheckUtil.checkPointInObstacles(obstacles, coord_x, coord_y);
            if (point_conflicted_with_obstacles || !BoundUtil.withinBound(coord_x, coord_y, bound_width, bound_height) || DistanceUtil.distanceBetween(threat.getCoordinates(), new float[]{coord_x, coord_y}) > StaticInitConfig.maximum_threat_movement_length) {
                coord_x -= speed * (float) Math.cos(threat_angle);
                coord_y -= speed * (float) Math.sin(threat_angle);
                threat_angle += Math.PI / 2;
                if (threat_angle > Math.PI * 2) {
                    threat_angle -= Math.PI * 2;
                    threat.setPath_planned_at_current_time_step(path);
                    break;
                }
            } else {
                point = new Point(coord_x, coord_y, threat_angle);
                path.addWaypointToEnd(point);
            }
        }
    }

    /**
     * undate all objects in world
     */
    public void updateAll() {
        if (this.time_step > 2) {
            int i = 0;
        }
        updateConflict();
        updateThreatCoordinate();
        detectEvent();
        registerInfoRequirement();
        shareInfoAfterRegistration();
        planPathForAllAttacker();
        resetDecisionParameter();
        updateAttackerCoordinate();
        checkThreatTerminate();
        checkConflict();
        this.time_step++;
    }

    /**
     * get total history path length
     *
     * @return
     */
    public float getTotalHistoryPathLen() {
        float total_path_len = 0;
        for (UAV attacker : attackers) {
            total_path_len += attacker.getHistory_path().getPath_length();
        }
        return total_path_len;
    }

    public static ArrayList<UAV> getAttackers() {
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

    public ControlCenter getControl_center() {
        return control_center;
    }

    public float getAttacker_patrol_range() {
        return attacker_patrol_range;
    }

    public UAVBase getUav_base() {
        return uav_base;
    }

    public ArrayList<UAV> getEnemy_uavs() {
        return enemy_uavs;
    }

    public ArrayList<Obstacle> getObstaclesForUIRendering() {
//        return this.kb.getObstaclesForUIRendering();
        return this.obstacles;
    }

    public ArrayList<Threat> getThreatsForUIRendering() {
//        return this.kb.getThreatsForUIRendering();
        return this.threats;
    }

    public ArrayList<Conflict> getConflictsForUIRendering() {
//        return this.kb.getConflictsForUIRendering();
        return this.conflicts;
    }

    private void setObstacles(ArrayList<Obstacle> obstacles) {
//        this.kb.setObstacles(obstacles);
        this.obstacles = obstacles;
    }

    private void setConflicts(ArrayList<Conflict> conflicts) {
//        this.kb.setConflicts(conflicts);
        this.conflicts = conflicts;
    }

    private void setThreats(ArrayList<Threat> threats) {
//        this.kb.setThreats(threats);
        this.threats = threats;
    }

    private void addObstacle(Obstacle obs) {
//        this.kb.addObstacle(obs);
        this.obstacles.add(obs);
    }

    private void addConflict(Conflict conflict) {
//        this.kb.addConflict(conflict);
        this.conflicts.add(conflict);
    }

//    private void addThreat(Threat threat) {
////        this.kb.addThreat(threat);
//        this.threats.add(threat);
//    }
//    private boolean containsThreat(Threat threat) {
//        return this.threats.contains(threat);
//    }
//    private boolean containsObstacle(Obstacle obstacle) {
//        return this.obstacles.contains(obstacle);
//    }
    private float[] randomGoalForThreat(float[] current_coord) {
        float[] random_goal_coordinate = new float[2];
        random_goal_coordinate[0] = current_coord[0]+(float) (100-Math.random() * 200);
        random_goal_coordinate[1] = current_coord[1]+(float)  (100-Math.random() * 200);

        boolean collisioned = true;
        while (collisioned) {
        random_goal_coordinate[0] = current_coord[0]+(float) (100-Math.random() * 200);
        random_goal_coordinate[1] = current_coord[1]+(float)  (100-Math.random() * 200);
            if (!ConflictCheckUtil.checkPointInObstacles(this.getObstaclesForUIRendering(), random_goal_coordinate[0], random_goal_coordinate[1])) {
                collisioned = false;
            }
        }
        return random_goal_coordinate;
    }

}
