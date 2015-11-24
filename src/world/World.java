/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import config.FilePathConfig;
import world.model.Threat;
import world.model.Obstacle;
import config.NonStaticInitConfig;
import config.StaticInitConfig;
import java.awt.Rectangle;
import java.util.ArrayList;
import ui.RightControlPanel;
import util.BoundUtil;
import util.ConflictCheckUtil;
import world.uav.Attacker;
import world.uav.UAVBase;
import util.DistanceUtil;
import world.model.Conflict;
import world.model.OntologyBasedKnowledge;
import world.model.Target;
import world.model.shape.Point;
import world.uav.UAVPath;
import world.uav.Scout;

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
    public static int bound_width = 400;
    public static int bound_height = 300;

    private int scout_num; //The number of our scouts
    private int threat_num; //The number of enemy threats_in_world
    private int attacker_num; //The number of our attackers

    private float threat_radius = 100;
    private float attacker_patrol_range; //The patrol range of scout 

    private int inforshare_algorithm = 0; //distinction between information-sharing algrithm

    //robot coordinates, robot_coordinates[1][0], robot_coordinates[1][1] represents the x, y coordinate of robot 1
    public static UAVBase uav_base;
    /**
     * * internal variables
     *
     */
    public static ArrayList<Attacker> attackers;
    private ArrayList<Scout> scouts;
    public static ControlCenter control_center;

    private int total_path_len = 0;
    private int total_msg_num = 0;
    private int num_of_attacker_remained = 0;
    private int num_of_threat_remained;
    private int no_threat_time_step = Integer.MAX_VALUE;
    private boolean scout_scaned_over = false;

    private ArrayList<Conflict> conflicts;
    private ArrayList<Threat> threats;
    private ArrayList<Obstacle> obstacles;

    private int time_step = 0; //times of simulation

    private MessageDispatcher msg_dispatcher;

    private int conflict_times = 0;

    public static String based_log_dir = "0";

    static {
        System.setProperty("LOGDIR", based_log_dir);
        System.setProperty("EXP_INDEX", FilePathConfig.exp_index);
    }

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(World.class);

    /**intiate the world according to configuration object.
     *
     * @param init_config records the simulation parameter
     */
    public World(NonStaticInitConfig init_config) {
//        World.kb = new WorldKnowledge();//OntologyBasedKnowledge();WorldKnowledge
        this.conflicts = new ArrayList<Conflict>();
        this.control_center = new ControlCenter(new OntologyBasedKnowledge());
        initParameterFromInitConfig(init_config);
        this.num_of_threat_remained = this.threat_num;
        this.num_of_attacker_remained = this.attacker_num;
        initUAVs();
        this.control_center.setAttackers(attackers);
        this.control_center.setScouts(scouts);
        this.control_center.setScout_speed(StaticInitConfig.SPEED_OF_SCOUT);
        this.control_center.setConflicts(conflicts);
        this.control_center.roleAssignForScouts();
        control_center.roleAssignForAttackerWithSubTeam(-1, -1); //initialize role assignment
    }

    /**initiate the parameter from init_config, which is called by World constructor.
     * @param init_config
     */
    private void initParameterFromInitConfig(NonStaticInitConfig init_config) {
        World.bound_width = init_config.getBound_width();
        World.bound_height = init_config.getBound_height();
        this.inforshare_algorithm = init_config.getInforshare_algorithm();

        this.attacker_num = init_config.getAttacker_num();
        this.scout_num = init_config.getScout_num();
        this.threat_num = init_config.getThreat_num();

        this.threat_radius = init_config.getThreat_radius();
        this.attacker_patrol_range = init_config.getAttacker_patrol_range();

        ArrayList<Threat> threats = new ArrayList<Threat>();
        for (Threat threat : init_config.getThreats()) {
            Threat threat_copy = (Threat) threat.deepClone();
            threats.add(threat_copy);
        }
        this.setThreats(threats);
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

        if (StaticInitConfig.debug_rrt) {
            StaticInitConfig.SHOW_FOG_OF_WAR = false;
            this.scout_num = 0;
            this.attacker_num = 3;
        }
    }

    /**initialize the scouts and attackers, which is called by World init functions.
     * 
     */
    private void initScoutsAndAttackers() {
        float[] uav_base_coordinate = uav_base.getCoordinate();
        float[] uav_base_center = new float[2];
        uav_base_center[0] = uav_base_coordinate[0];// + uav_base_width / 2;
        uav_base_center[1] = uav_base_coordinate[1]; //+ uav_base_height / 2;
        for (int i = 0; i < attacker_num; i++) {
            float[] uav_init_coord = uav_base.assignUAVLocation(i);
            Attacker attacker = new Attacker(i, null, StaticInitConfig.ATTACKER, uav_init_coord, null, Float.MAX_VALUE);
            attackers.add(attacker);
        }

        for (int i = 0; i < scout_num; i++) {
            Scout scout = new Scout(i, StaticInitConfig.SCOUT, uav_base_center, uav_base_center, control_center, Float.MAX_VALUE);
            scouts.add(scout);
        }
    }

    /**initiate all uavs, including scouts and attackers.
     *
     */
    private void initUAVs() {
        this.scouts = new ArrayList<Scout>();
        this.attackers = new ArrayList<Attacker>();
        initScoutsAndAttackers();
    }

    /**path planning for attackers, which are not destroyed.
     *
     */
    private void planPathForAllAttacker() {
        for (Attacker attacker : this.attackers) {
            if (attacker.isVisible()) {
                attacker.pathPlan();
            }
        }
    }

    /**check whether the uav is too close to others and cause conflict. If too close, then the uav should replan.
     * 
     */
    private void checkConflict() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker1 = World.attackers.get(i);
            if (attacker1.getTarget_indicated_by_role() == null)//check whether the attacker is in the uav base. If it is in the uav base, then it will not conflict with others.
            {
                continue;
            }
            float[] attacker1_coord = attacker1.getCenter_coordinates();
            for (int j = i + 1; j < this.attacker_num; j++) {
                Attacker attacker2 = World.attackers.get(j);
                if (attacker2.getTarget_indicated_by_role() == null || attacker2.isVisible() == false) {
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

    /**check whether the target of the attacker is reached and update attacker's status: lock the target, destroy the target or normal fly status.
     * 
     */
    private void checkThreatReached() {
        ArrayList<Obstacle> obstacles_in_the_world = this.obstacles;
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = World.attackers.get(i);
            Target attacker_target = attacker.getTarget_indicated_by_role();
            if (!attacker.isVisible()) {
                continue;
            }
            //Only when the attacker is in the base, its target indicated by the role is null
            if (attacker_target == null) {
                continue;
            }
            int threat_index = attacker_target.getIndex();
            //threat_index=-1 means the attacker is returning to the base
            if (threat_index == Threat.UAV_BASE_INDEX) {
                float[] target_coord = attacker_target.getCoordinates();
                float[] attacker_coord = attacker.getCenter_coordinates();
                //if the attacker reached the base
                if (DistanceUtil.distanceBetween(attacker_coord, target_coord) < (attacker.getUav_radar().getRadius() / 3)) {
                    attacker.setTarget_indicated_by_role(null);
                    attacker.setNeed_to_replan(false);
                    this.control_center.setNeed_to_assign_role(true);
                    break;
                } else {
                    attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_IDLE);
                    attacker.setFly_mode(Attacker.FLYING_MODE);
                }

                continue;
            }
            //Otherwise, the attacker is flying toward its target
            ArrayList<Threat> threats_in_world = this.threats;
            for (int j = 0; j < threats_in_world.size(); j++) {
                Threat threat = threats_in_world.get(j);
                //threat is destroyed
                if (!threat.isEnabled()) {
                    continue;
                }
                //Otherwise, destroy its threat when the attacker is close to the threat
                if (threat_index == threat.getIndex()) {
                    float distance_to_target = DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), threat.getCoordinates());
                    if (distance_to_target < attacker.getUav_radar().getRadius() / 2) {
                        if (attacker.getFly_mode() == Attacker.FLYING_MODE) {
                            attacker.setFly_mode(Attacker.TARGET_LOCKED_MODE);
                            attacker.setHovered_time_step(0);
                            threat.setMode(Threat.LOCKED_MODE);
                            attacker.setNeed_to_replan(true);
                            this.control_center.lockAttackerToThreat(attacker.getIndex(), threat.getIndex());
                        } else if (attacker.getFly_mode() == Attacker.TARGET_LOCKED_MODE) {
                            if (attacker.getHovered_time_step() < StaticInitConfig.LOCKED_TIME_STEP_UNTIL_DESTROYED) {
                                attacker.increaseHovered_time_step();
                            } else {
                                threat.setEnabled(false);
                                this.control_center.updateThreat(threat);
                                this.control_center.threatDestroyedAndUnlocked(threat.getIndex());
                                this.num_of_threat_remained--;
                                this.control_center.setNeed_to_assign_role(true);
                                float[] dummy_threat_coord = World.assignUAVPortInBase(attacker.getIndex());
                                Threat dummy_threat = new Threat(Threat.UAV_BASE_INDEX, dummy_threat_coord, 0, 0);
                                attacker.setTarget_indicated_by_role(dummy_threat);
                                attacker.setNeed_to_replan(true);
                                attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_IDLE);
                                attacker.setFly_mode(Attacker.FLYING_MODE);
                            }
                        }
                    } 
                    break;
                }
            }

        }
    }

    /**update coordinate of attackers.
     * 
     */
    private void updateAttackerCoordinate() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = World.attackers.get(i);
            if (!attacker.isVisible()) {
                continue;
            }
            boolean moved = attacker.moveToNextWaypoint();
            if (moved) {
//                scout.setNeed_to_replan(true);
                logger.debug("attacker:" + attacker.getIndex() + " moved");
            }
        }
    }

    /** The experiment is over in 2 cases: 1. there are no attackers; 2. the scouts are scaned over, no threats are remained and all attackers returned back to uav base.
     * 
     * @return true when experiment is over
     */
    public boolean isExperiment_over() {
        if (this.num_of_attacker_remained == 0) {
            return true;
        }
        if (!this.scout_scaned_over || this.num_of_threat_remained != 0) {
            return false;
        }
        for (Attacker attacker : this.attackers) {
            if (attacker.getTarget_indicated_by_role() != null && attacker.isVisible()) {
                return false;
            }
        }
        return true;
    }

    /**During patrol,the attacker detect event by radar.
     * 
     */
    private void detectAttackerEvent() {
        for (Attacker attacker : World.attackers) {
            if (!attacker.isVisible()) {
                continue;
            }
            ArrayList<Obstacle> obstacles = this.getObstaclesForUIRendering();
            int obs_list_size = obstacles.size();
            for (int i = 0; i < obs_list_size; i++) {
                Obstacle obs = obstacles.get(i);
                if (obs.getMbr().intersects(attacker.getUav_radar().getBounds())) {
                    if (!attacker.containsObstacle(obs)) {
                        attacker.addObstacle(obs);
                        attacker.setNeed_to_replan(true);
                    }
                    if (!control_center.containsObstacle(obs)) {
                        control_center.addObstacle(obs);
                    }
                }
            }

            ArrayList<Threat> threats = this.getThreatsForUIRendering();
            int threat_list_size = threats.size();
            for (int i = 0; i < threat_list_size; i++) {
                Threat threat = threats.get(i);
                float dist_from_attacker_to_threat = DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), threat.getCoordinates());
                if (dist_from_attacker_to_threat < attacker.getUav_radar().getRadius() && threat.isEnabled()) {
                    if (!attacker.containsThreat(threat)) {
                        attacker.addThreat(threat);
                        attacker.setNeed_to_replan(true);
                    }

                    if (!control_center.containsThreat(threat)) {
                        control_center.addThreat(threat);
                        control_center.setNeed_to_assign_role(true);
                    }
                }
            }
        }
    }

    /**During patrol,the scout detect event by radar.
     * 
     */
    private void detectScoutEvent() {
        for (Scout scout : this.scouts) {
            int obs_list_size = this.getObstaclesForUIRendering().size();
            for (int i = 0; i < obs_list_size; i++) {
                Obstacle obs = this.getObstaclesForUIRendering().get(i);
                if (!control_center.containsObstacle(obs) && obs.getMbr().intersects(scout.getUav_radar().getBounds())) {
                    control_center.addObstacle(obs);
                    scout.getKb().addObstacle(obs);
                }
            }

            int threat_list_size = this.getThreatsForUIRendering().size();
            for (int i = 0; i < threat_list_size; i++) {
                Threat threat = this.getThreatsForUIRendering().get(i);
                float dist_from_attacker_to_threat = DistanceUtil.distanceBetween(scout.getCenter_coordinates(), threat.getCoordinates());
                if (threat.isEnabled() && !control_center.containsThreat(threat) && dist_from_attacker_to_threat < scout.getUav_radar().getRadius() * 0.9) {
                    control_center.addThreat(threat);
                    scout.getKb().addThreat(threat);
                }
            }
        }
    }

    /**register information requirement for attackers, according to its target and location.
     * 
     */
    private void registerInfoRequirement() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = World.attackers.get(i);
            if (!attacker.isVisible()) {
                continue;
            }
            Target target = attacker.getTarget_indicated_by_role();
            if (target != null) {
                float[] attacker_coord = attacker.getCenter_coordinates();
                this.msg_dispatcher.register(attacker.getIndex(), attacker_coord, target);
            }
        }
    }

    /**share information every 3 time step.
     * 
     */
    private void shareInfoAfterRegistration() {
        if (this.time_step % 3 == 0) {
            this.msg_dispatcher.decideAndSumitMsgToSend();
            this.msg_dispatcher.dispatch();
        }
    }

    private void resetDecisionParameter() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = World.attackers.get(i);
            attacker.setNeed_to_replan(false);
        }
    }

    /**update conflict.
     * 
     */
    private void updateConflict() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = World.attackers.get(i);
            if (!attacker.isVisible()) {
                continue;
            }
            if (attacker.isReplanned_at_current_time_step()) {
                Conflict conflict = new Conflict(attacker.getIndex(), attacker.getFuturePath().getWaypointsAsLinkedList(), this.time_step, StaticInitConfig.SAFE_DISTANCE_FOR_CONFLICT);
                this.addConflict(conflict);
            }
        }
    }

    /** update the coordinate of all threats, which are not destroyed (visible).
     * 
     */
    private void updateThreatCoordinate() {
        ArrayList<Threat> threats_in_world = this.getThreatsForUIRendering();
        for (int i = 0; i < threats_in_world.size(); i++) {
            Threat threat = threats_in_world.get(i);
            float[] threat_coord = threat.getCoordinates();
            if (!threat.isEnabled()) {
                continue;
            }
            boolean moved = threat.moveToNextWaypoint();
            if (!moved) {
                planPathForThreat(threat);
            }
        }
    }

    /** plan path for given threat, the threat moves slowly and straightly(North, South, West, East) until it reaches an obstacle with distance NonStaticInitConfig.threat_range_from_obstacles.
     * 
     * @param threat 
     */
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
            Rectangle threat_mbr = new Rectangle((int) coord_x - (Threat.threat_width + NonStaticInitConfig.threat_range_from_obstacles) / 2, (int) coord_y - (Threat.threat_height + NonStaticInitConfig.threat_range_from_obstacles) / 2, Threat.threat_width + NonStaticInitConfig.threat_range_from_obstacles, Threat.threat_height + NonStaticInitConfig.threat_range_from_obstacles);
            point_conflicted_with_obstacles = ConflictCheckUtil.checkThreatInObstacles(obstacles, threat_mbr)||World.uav_base.getBase_shape().intersects(threat_mbr);
            if (point_conflicted_with_obstacles || !BoundUtil.withinRelaxedBound(coord_x, coord_y, bound_width, bound_height) || DistanceUtil.distanceBetween(threat.getCoordinates(), new float[]{coord_x, coord_y}) > StaticInitConfig.maximum_threat_movement_length) {
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

    /**This method is extremely important and it arranges all the procedures in the simulations. 
     * It performs as the god and have the following important steps: 1. calls the control center to send out scouts 2. call control center to share the info it detects to the attackers; 
     * 3.calls control center to assign role for each attacker. 4. call each attacker to plan path for itself. 5. check whether each threat is reached, locked or destroyed. 5. summarize and record all data we cared.
     */
    public void updateAll() {
        if (this.time_step == 0) {
            logger.info("-------------------------------------");
            logger.info("attacker_num=" + this.attacker_num + " scout_num=" + this.scout_num + " threat_num=" + this.threat_num + " obstalce_num=" + NonStaticInitConfig.obstacle_num + " infoshare=" + this.inforshare_algorithm + "(0:broadcast 1:noinfor 2:intelligent)");
            if (StaticInitConfig.debug_rrt) {
                this.control_center.setObstacles(obstacles);
                this.control_center.setThreats(threats);
                this.control_center.setConflicts(conflicts);
            }
            RightControlPanel.setWorldKnowledge(World.attackers.get(0).getKb());
        }

        updateScoutInControlCenter();
        logger.debug("scout updated in control center over");
        detectScoutEvent();
        logger.debug("scout event detect over");
        detectAttackerEvent();
        logger.debug("attacker detect over");
        registerInfoRequirement();
        logger.debug("information register over");
        shareInfoAfterRegistration();
        logger.debug("information share over");
        roleAssignmentInControlCenter();
        logger.debug("role assign in control center over");
        planPathForAllAttacker();
        logger.debug("path planning for attackers over");
        resetDecisionParameter();
        logger.debug("parameter rest over");
        updateAttackerCoordinate();
        logger.debug("attacker coordinate update over");
        checkReplanningAccordingToAttackerMovement();
        logger.debug("replanning check over");
        checkThreatReached();
        logger.debug("threat terminate check over");
        checkNumOfAttackerDestroyed();
        logger.debug("uav destroyed check over");
        checkConflict();
        updateConflict();
        logger.debug("conflict update over");
        recordResultInLog();
        logger.debug("record result in log");
        if (this.time_step % 10 == 0) {
            updateThreatCoordinate();
            logger.debug("update threat over");
            updateThreatCoordinateInControlCenter();
        }

        if (this.num_of_threat_remained == 0 && this.no_threat_time_step == Integer.MAX_VALUE) {
            this.no_threat_time_step = this.time_step;
        }
        this.time_step++;
        this.scout_scaned_over = control_center.isScout_scanned_over();

        //when all the scout scanned over, clear all the fog and show all threats
        if (this.scout_scaned_over) {
            StaticInitConfig.SHOW_FOG_OF_WAR = false;
            ArrayList<Threat> threats_in_control = this.control_center.getThreats();
            for (Threat threat : this.threats) {
                if (!threats_in_control.contains(threat)) {
                    threats_in_control.add(threat);
                    this.control_center.setNeed_to_assign_role(true);
                }
            }
        }
    }

    /**sumarize the number of attackers that is destroyed at current time step.
     * 
     */
    private void checkNumOfAttackerDestroyed() {
        this.num_of_attacker_remained = this.attacker_num;
        for (Attacker attacker : World.attackers) {
            if (!attacker.isVisible()) {
                this.num_of_attacker_remained--;
                continue;
            }
            float[] coordinate = attacker.getCenter_coordinates();
            for (Obstacle obstacle : this.getObstaclesForUIRendering()) {
                if (obstacle.getMbr().contains(coordinate[0], coordinate[1])) {
                    attacker.setVisible(false);
                    this.num_of_attacker_remained--;
                    Target target = attacker.getTarget_indicated_by_role();
                    if (target != null && target.getIndex() != Target.UAV_BASE_INDEX) {
                        this.control_center.unlockAttacerFromThreat(attacker.getIndex(), target.getIndex());
                    }
                    break;
                }
            }
        }
    }

    /** inform the control center about the coordinate of the threat.
     * 
     */
    private void updateThreatCoordinateInControlCenter() {
        ArrayList<Threat> threats_in_control_center = this.control_center.getThreats();
        for (Threat threat_in_control_center : threats_in_control_center) {
            for (Threat threat : this.threats) {
                if (threat_in_control_center.getIndex() == threat.getIndex()) {
                    threat_in_control_center.setCoordinates(threat.getCoordinates());
                    this.control_center.updateThreat(threat);
                }
            }
        }
        threats_in_control_center = this.control_center.getThreats();
    }

    /** record all the date we cared in log file(log4j).
     * 
     */
    private void recordResultInLog() {
        this.total_path_len = (int) this.getTotalHistoryPathLen();
        this.total_msg_num = msg_dispatcher.getTotalNumOfMsgSent();
        logger.info(this.time_step + " " + this.total_path_len + " " + this.total_msg_num + " " + this.num_of_threat_remained + " " + this.num_of_attacker_remained);
    }

    /** calls the control center to assign role for each attacker.
     * 
     */
    private void roleAssignmentInControlCenter() {
        if (this.control_center.isNeed_to_assign_role()) {
            this.control_center.roleAssignForAttackerWithSubTeam(-1, -1);
        }
        this.control_center.setNeed_to_assign_role(false);
    }

    /** calls the control center to update the coordinates of the scouts it owned.
     * 
     */
    private void updateScoutInControlCenter() {
        this.control_center.updateScoutCoordinate();
    }

    /** check whether the attacker should replan in  next time step. 
     * 
     */
    private void checkReplanningAccordingToAttackerMovement() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = this.attackers.get(i);
            if (!attacker.isVisible() || attacker.getTarget_indicated_by_role() == null) {
                continue;
            }
            boolean moved = attacker.isMoved_at_last_time();
            if (!moved) {//not moved
                attacker.setNeed_to_replan(true);
                //returning to the base
                if (attacker.getTarget_indicated_by_role().getIndex() == Threat.UAV_BASE_INDEX) {
                    float[] dummy_threat_coord = World.assignUAVPortInBase(attacker.getIndex());
                    Threat dummy_threat = new Threat(Threat.UAV_BASE_INDEX, dummy_threat_coord, 0, 0);
                    attacker.setTarget_indicated_by_role(dummy_threat);
                    attacker.setNeed_to_replan(true);
                    attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_IDLE);
                    attacker.setFly_mode(Attacker.FLYING_MODE);
                } else {//have target to destroy
                    float dist_to_target = DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), attacker.getTarget_indicated_by_role().getCoordinates());
//                    logger.debug(attacker.getIndex() + " not moved and has target-------------start--------------");
//                    logger.debug("target index:" + attacker.getTarget_indicated_by_role().getIndex());
//                    logger.debug("target " + attacker.getTarget_indicated_by_role().getIndex() + " visible=" + this.getThreatsForUIRendering().get(attacker.getTarget_indicated_by_role().getIndex()).isEnabled());
//                    logger.debug("dist to target:" + dist_to_target);
//                    logger.debug("attacker mode:" + attacker.getFly_mode());
//                    logger.debug("not moved and has target-------------end--------------");

                    if (attacker.getFly_mode() == Attacker.TARGET_LOCKED_MODE) {
                        attacker.setNeed_to_replan(true);
                    } else {
                        if (dist_to_target < attacker.getUav_radar().getRadius() / 2) {
                            attacker.setNeed_to_replan(true);
                            attacker.setFly_mode(Attacker.TARGET_LOCKED_MODE);
                            attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_ON_DESTROYING_THREAT);
                        } else {//not reaching target
                            attacker.setNeed_to_replan(true);
                            attacker.setFly_mode(Attacker.FLYING_MODE);
                            attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_ON_TASK);
                        }
                    }
                }
            } else {
//                attacker.setNeed_to_replan(false);
            }
        }
    }

    /** sumarize the total path length of all attackers.
     *
     * @return the total path length.
     */
    private float getTotalHistoryPathLen() {
        float total_path_len = 0;
        for (Attacker attacker : attackers) {
            total_path_len += attacker.getHistory_path().getPath_length();
        }
        return total_path_len;
    }

    public static ArrayList<Attacker> getAttackers() {
        return attackers;
    }

    public void setAttackers(ArrayList<Attacker> attackers) {
        this.attackers = attackers;
    }

    public ArrayList<Scout> getScouts() {
        return scouts;
    }

    public void setScouts(ArrayList<Scout> scouts) {
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

    public float getTotal_path_len() {
        return total_path_len;
    }

    public int getTotal_msg_num() {
        return total_msg_num;
    }

    public float getAttacker_patrol_range() {
        return attacker_patrol_range;
    }

    public UAVBase getUav_base() {
        return uav_base;
    }

    public ArrayList<Obstacle> getObstaclesForUIRendering() {
        return this.obstacles;
    }

    public ArrayList<Threat> getThreatsForUIRendering() {
        return this.threats;
    }

    public ArrayList<Conflict> getConflictsForUIRendering() {
        return this.conflicts;
    }

    private void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    private void setConflicts(ArrayList<Conflict> conflicts) {
        this.conflicts = conflicts;
    }

    private void setThreats(ArrayList<Threat> threats) {
        this.threats = threats;
    }

    public ArrayList<Threat> getThreats() {
        return threats;
    }

    private void addObstacle(Obstacle obs) {
        this.obstacles.add(obs);
    }

    private void addConflict(Conflict conflict) {
        this.conflicts.add(conflict);
    }

    public static float[] assignUAVPortInBase(int attacker_index) {
        return World.uav_base.assignUAVLocation(attacker_index);
    }

}
