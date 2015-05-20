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
    private int enemy_num;  //The number of enemy uavs
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
    private ArrayList<Attacker> enemy_uavs;
    private ArrayList<Scout> scouts;
    private ControlCenter control_center;

    private int total_path_len = 0;
    private int total_msg_num = 0;
    private int num_of_attacker_remained = 0;
    private int num_of_threat_remained;
    private int no_threat_time_step = Integer.MAX_VALUE;
    private int not_threat_time_found = 0;
    private boolean scout_scaned_over = false;

    private ArrayList<Conflict> conflicts;
    private ArrayList<Threat> threats;
    private ArrayList<Obstacle> obstacles;

    private int time_step = 0; //times of simulation

    private MessageDispatcher msg_dispatcher;

    private int conflict_times = 0;

    private float theta_increase_for_enemy_uav = (float) Math.PI / 40;
    private boolean experiment_over = false;

    public static String exp_index = "D:\\kingsoft\\dissertation\\simulator\\result";
    public static String based_log_dir = "0";

    static {
        System.setProperty("LOGDIR", based_log_dir);
        System.setProperty("EXP_INDEX", exp_index);
    }

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(World.class);

    /**
     * intiate the world according to configuration object.
     *
     * @param init_config
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
        this.control_center.setScout_speed(5);
//        this.control_center.setObstacles(obstacles_in_the_world);
//        this.control_center.setThreats(threats_in_world);
        this.control_center.setConflicts(conflicts);
        this.control_center.roleAssignForScouts();
        control_center.roleAssignForAttacker(-1, -1); //initialize role assignment
    }

    /**
     * initiate the parameter of battleground objects.
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
        
        ArrayList<Threat> threats=new ArrayList<Threat>();
        for(Threat threat:init_config.getThreats())
        {
            Threat threat_copy=(Threat)threat.deepClone();
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

    /**
     * initialize the scouts and attackers.
     *
     */
    private void initScoutsAndAttackers() {
        float[] uav_base_coordinate = uav_base.getCoordinate();
        int uav_base_height = uav_base.getBase_height();
        int uav_base_width = uav_base.getBase_width();
        float[] uav_base_center = new float[2];
        uav_base_center[0] = uav_base_coordinate[0];// + uav_base_width / 2;
        uav_base_center[1] = uav_base_coordinate[1]; //+ uav_base_height / 2;
        for (int i = 0; i < attacker_num; i++) {
            Threat threat = this.getThreatsForUIRendering().get(i);
            Attacker attacker = new Attacker(i, null, StaticInitConfig.ATTACKER, uav_base_center, null, Float.MAX_VALUE);
            attackers.add(attacker);
        }

        for (int i = 0; i < scout_num; i++) {
            Scout scout = new Scout(i, StaticInitConfig.SCOUT, uav_base_center, uav_base_center, control_center, Float.MAX_VALUE);
            scouts.add(scout);
        }
    }

    /**
     * initiate all uavs
     *
     */
    private void initUAVs() {
        this.scouts = new ArrayList<Scout>();
        this.attackers = new ArrayList<Attacker>();
        this.enemy_uavs = new ArrayList<Attacker>();
        initScoutsAndAttackers();
//        initEnemyUAV();
    }

    /**
     * path planning for attackers, which are not destroyed.
     *
     */
    private void planPathForAllAttacker() {
        for (Attacker attacker : this.attackers) {
            if (attacker.isVisible()) {
                attacker.pathPlan();
            }
        }
    }

    /**
     * check whether the uav is too close to others. If too close, then the uav
     * should replan.
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
                if (attacker2.getTarget_indicated_by_role() == null || attacker2.isVisible()==false) {
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

    private void checkThreatReached() {
        ArrayList<Obstacle> obstacles_in_the_world = this.obstacles;
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = World.attackers.get(i);
            if (!attacker.isVisible()) {
                continue;
            }
            if (attacker.getTarget_indicated_by_role() == null) {
                continue;
            }
            int threat_index = attacker.getTarget_indicated_by_role().getIndex();
            if (threat_index == -1) {
                float[] target_coord = attacker.getTarget_indicated_by_role().getCoordinates();
                float[] attacker_coord = attacker.getCenter_coordinates();
                if (DistanceUtil.distanceBetween(attacker_coord, target_coord) < (attacker.getUav_radar().getRadius())) {
                    attacker.setTarget_indicated_by_role(null);
                    attacker.setNeed_to_replan(false);
                    this.control_center.setNeed_to_assign_role(true);
                    break;
                }

                if (!attacker.isMoved_at_last_time()) {
                    float[] dummy_threat_coord = World.randomGoalForAvailableUAV(attacker.getCenter_coordinates(), obstacles_in_the_world);
                    Threat dummy_threat = new Threat(-1, dummy_threat_coord, 0, 0);
                    attacker.setTarget_indicated_by_role(dummy_threat);
                    attacker.setNeed_to_replan(true);
                    this.control_center.setNeed_to_assign_role(true);
                    attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_IDLE);
                }
                continue;
            }

            ArrayList<Threat> threats_in_world = this.threats;
            for (int j = 0; j < threats_in_world.size(); j++) {
                Threat threat = threats_in_world.get(j);
                if (!threat.isEnabled()) {
                    continue;
                }
                if (threat_index == threat.getIndex()) {
                    if (DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), threat.getCoordinates()) < (attacker.getUav_radar().getRadius())) {
                        threat.setEnabled(false);
                        this.control_center.updateThreat(threat);
                        this.num_of_threat_remained--;
                        this.control_center.setNeed_to_assign_role(true);
                        float[] dummy_threat_coord = World.randomGoalForAvailableUAV(attacker.getCenter_coordinates(), obstacles_in_the_world);
                        Threat dummy_threat = new Threat(-1, dummy_threat_coord, 0, 0);
                        attacker.setTarget_indicated_by_role(dummy_threat);
                        attacker.setNeed_to_replan(true);
                        attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_IDLE);
                    }
                    break;
                }
            }
        }
    }

    /**
     * undate coordinate of attackers
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

    public boolean isExperiment_over() {
        if( this.num_of_attacker_remained ==0)
        {
            return true;
        }
        if (!this.scout_scaned_over||this.num_of_threat_remained!=0) {
            return false;
        }
        for (Attacker attacker : this.attackers) {
            if (attacker.getTarget_indicated_by_role() != null && attacker.isVisible()) {
                return false;
            }
        }
        return true;
    }

    /**
     * During patrol,the uav detect event by radar or share information
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

    /**
     * During patrol,the uav detect event by radar or share information
     */
    private void detectScoutEvent() {
        for (Scout scout : this.scouts) {
            int obs_list_size = this.getObstaclesForUIRendering().size();
            for (int i = 0; i < obs_list_size; i++) {
                Obstacle obs = this.getObstaclesForUIRendering().get(i);
                if (!control_center.containsObstacle(obs) && obs.getMbr().intersects(scout.getUav_radar().getBounds())) {
                    control_center.addObstacle(obs);
                }
            }

            int threat_list_size = this.getThreatsForUIRendering().size();
            for (int i = 0; i < threat_list_size; i++) {
                Threat threat = this.getThreatsForUIRendering().get(i);
                float dist_from_attacker_to_threat = DistanceUtil.distanceBetween(scout.getCenter_coordinates(), threat.getCoordinates());
                if (threat.isEnabled() && !control_center.containsThreat(threat) && dist_from_attacker_to_threat < scout.getUav_radar().getRadius() * 0.9) {
                    control_center.addThreat(threat);
                }
            }
        }
    }

    /**
     * register information requirement for attackers,this mothod is used to
     * information sharing rapidly
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

    /**
     * information sharing
     */
    private void shareInfoAfterRegistration() {
        if (this.time_step % 10 == 0) {
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

    /**
     * unpdate conflict
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
        checkUAVDestroyedAndNumberOfAttackerRemained();
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

        if (scout_scaned_over) {
            this.not_threat_time_found++;
        }
    }

    private void checkUAVDestroyedAndNumberOfAttackerRemained() {
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
                    break;
                }
            }
        }
    }

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

    private void recordResultInLog() {
        this.total_path_len = (int) this.getTotalHistoryPathLen();
        this.total_msg_num = msg_dispatcher.getTotalNumOfMsgSent();
        logger.info(this.time_step + " " + this.total_path_len + " " + this.total_msg_num + " " + this.num_of_threat_remained + " " + this.num_of_attacker_remained);
    }

    private void roleAssignmentInControlCenter() {
        if (this.control_center.isNeed_to_assign_role()) {
            this.control_center.roleAssignForAttackerV3(-1, -1);
        }
        this.control_center.setNeed_to_assign_role(false);
    }

    private void updateScoutInControlCenter() {
        this.control_center.updateScoutCoordinate();
    }

    private void updateControlCenterKnowledge() {
        this.control_center.setThreats(threats);
    }

    private void checkReplanningAccordingToAttackerMovement() {
        for (int i = 0; i < this.attacker_num; i++) {
            Attacker attacker = this.attackers.get(i);
            if (!attacker.isVisible()) {
                continue;
            }
            boolean moved = attacker.isMoved_at_last_time();
            if (!moved) {
                attacker.setNeed_to_replan(true);
                if (attacker.getTarget_indicated_by_role() != null && attacker.getTarget_indicated_by_role().getIndex() == -1) {
                    float[] dummy_threat_coord = World.randomGoalForAvailableUAV(attacker.getCenter_coordinates(), obstacles);
                    Threat dummy_threat = new Threat(-1, dummy_threat_coord, 0, 0);
                    attacker.setTarget_indicated_by_role(dummy_threat);
                    attacker.setNeed_to_replan(true);
                    attacker.setSpeed(StaticInitConfig.SPEED_OF_ATTACKER_IDLE);
                }
            } else {
                attacker.setNeed_to_replan(false);
            }
        }
    }

    /**
     * get total history path length
     *
     * @return
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

    public ArrayList<Attacker> getEnemy_uavs() {
        return enemy_uavs;
    }

    public void setExperiment_over(boolean experiment_over) {
        this.experiment_over = experiment_over;
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
//        this.kb.setObstacles(obstacles_in_the_world);
        this.obstacles = obstacles;
    }

    private void setConflicts(ArrayList<Conflict> conflicts) {
//        this.kb.setConflicts(conflicts);
        this.conflicts = conflicts;
    }

    private void setThreats(ArrayList<Threat> threats) {
//        this.kb.setThreats(threats_in_world);
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

    public static float[] randomGoalForAvailableUAV(float[] current_coord, ArrayList<Obstacle> obstacles) {
//        float[] random_goal_coordinate = new float[2];
//        double random_theta = Math.random() * Math.PI * 2;
//        random_goal_coordinate[0] = current_coord[0] + (float) Math.cos(random_theta) * 400;
//        random_goal_coordinate[1] = current_coord[1] + (float) Math.sin(random_theta) * 400;
//        boolean collisioned = true;
//        boolean withinBound = BoundUtil.withinBound(random_goal_coordinate[0], random_goal_coordinate[0], World.bound_width, World.bound_height);
//        while (collisioned && !withinBound) {
//            random_goal_coordinate[0] = current_coord[0] + (float) Math.cos(random_theta) * 400;
//            random_goal_coordinate[1] = current_coord[1] + (float) Math.sin(random_theta) * 400;
//            if (!ConflictCheckUtil.checkPointInObstacles(obstacles_in_the_world, random_goal_coordinate[0], random_goal_coordinate[1])) {
//                collisioned = false;
//            } else {
//                random_theta = Math.PI / 4 + Math.random() * Math.PI / 2;
//            }
//            withinBound = BoundUtil.withinBound(random_goal_coordinate[0], random_goal_coordinate[0], World.bound_width, World.bound_height);
//            logger.debug("find random goal for uav");
//        }
//        return random_goal_coordinate;
        return World.uav_base.getCoordinate();

    }

}
