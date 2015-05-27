/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.uav;

import algorithm.RRT.RRTAlg;
import algorithm.RRT.RRTTree;
import config.NonStaticInitConfig;
import config.StaticInitConfig;
import java.util.LinkedList;
import java.util.ArrayList;
import util.BoundUtil;
import util.ConflictCheckUtil;
import util.DistanceUtil;
import util.VectorUtil;
import world.Message;

import world.model.shape.Circle;
import world.model.Obstacle;
import world.World;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
import world.model.KnowledgeInterface;
import world.model.OntologyBasedKnowledge;
import world.model.Target;
import world.model.Threat;
import world.model.WorldKnowledge;
import world.model.shape.DubinsCurve;
import world.model.shape.Point;
import world.model.shape.Trajectory;

/**
 *
 * @author Yulin_Zhang
 */
public class Attacker extends UAV implements KnowledgeAwareInterface {

    private volatile UAVPath path_planned_at_current_time_step;
    private int current_index_of_planned_path = 0; //index of waypoint
    private UAVPath path_planned_at_last_time_step;
    private UAVPath history_path;
    private boolean need_to_replan = true;
    private boolean replanned_at_current_time_step = false;
    private boolean moved_at_last_time = false;

    //variables for conflict planning
    private KnowledgeInterface kb;

    private int fly_mode = 0;
    private int hovered_time_step = 0;
    private float[] goal_for_each_iteration;
    private int stucked_times=0;//when the flying angle and radar radius are not large enough, the uav could be stucked in front of the obstacle and not able to be moved.
    private int max_stucked_times=4;
    
    private RRTAlg rrt_alg;
    private RRTTree rrt_tree;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Attacker.class);

    public static int FLYING_MODE = 0;
    public static int TARGET_LOCKED_MODE = 1;



    /**
     *
     * @param index
     * @param target
     * @param center_coordinates
     */
    public Attacker(int index, Target target, int uav_type, float[] center_coordinates, ArrayList<Obstacle> obstacles, float remained_energy) {
        super(index, target, uav_type, center_coordinates, remained_energy);
        this.uav_radar = new Circle(center_coordinates[0], center_coordinates[1], StaticInitConfig.attacker_radar_radius);
        this.path_planned_at_current_time_step = new UAVPath();
        this.history_path = new UAVPath();
        setPreviousWaypoint();
        this.kb = new OntologyBasedKnowledge();//OntologyBasedKnowledge();WorldKnowledge
        this.kb.setObstacles(obstacles);
        this.speed = StaticInitConfig.SPEED_OF_ATTACKER_ON_TASK;
        if (target == null) {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), null, StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, null, this.getConflicts(), this.index);
        } else {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), target.getCoordinates(), StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, null, this.getConflicts(), this.index);
        }
        initColor(index);
    }

    public void ignoreEverythingAndTestDubinPath() {
        Point start = new Point(this.getCenter_coordinates()[0], this.getCenter_coordinates()[1], this.current_angle);
        Point end = new Point(target_indicated_by_role.getCoordinates()[0], target_indicated_by_role.getCoordinates()[1], 0);
        DubinsCurve min_dc = null;
        double min_len = Float.MAX_VALUE;
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 12) {
            end.setYaw(angle);
            DubinsCurve dc = new DubinsCurve(start, end, 10, false, 10, 1);
            if (dc.getLength() < min_len) {
                min_dc = dc;
                min_len = dc.getLength();
            }
        }
        Trajectory traj = min_dc.getTraj();
        logger.debug("total waypoint num:" + traj.getPoints().length);
        logger.debug("total traj lenght:" + traj.getCost());
        logger.debug("end node:" + traj.getEndPoint());
        logger.debug("goal point:" + end);
        UAVPath path = new UAVPath();
        for (Point point : traj.getPoints()) {
            path.addWaypointToEnd(point);
        }

        this.setPath_prefound(path);
        this.resetCurrentIndexOfPath();
    }

    /**
     * path planning
     */
    public void pathPlan() {
        //if the attacker need to replan and it has target
        if (this.need_to_replan && this.target_indicated_by_role != null) {
            this.path_planned_at_last_time_step = this.path_planned_at_current_time_step;
            int planning_times = 0;
            this.goal_for_each_iteration = target_indicated_by_role.getCoordinates();
            if (this.fly_mode == Attacker.TARGET_LOCKED_MODE && this.target_indicated_by_role.getIndex() != Threat.UAV_BASE_INDEX) {
                this.goal_for_each_iteration = this.genRandomHoveringGoal(goal_for_each_iteration, NonStaticInitConfig.threat_range_from_obstacles/2, this.getObstacles());
                this.speed=StaticInitConfig.SPEED_OF_ATTACKER_ON_DESTROYING_THREAT;
                this.rrt_alg.setMax_angle((float) Math.PI / 3);
            }else if(this.fly_mode== Attacker.FLYING_MODE && this.target_indicated_by_role.getIndex() == Threat.UAV_BASE_INDEX)
            {
                this.speed=StaticInitConfig.SPEED_OF_ATTACKER_IDLE;
                this.rrt_alg.setMax_angle((float) Math.PI / 6);
            }else if(this.fly_mode== Attacker.FLYING_MODE && this.target_indicated_by_role.getIndex() != Threat.UAV_BASE_INDEX)
            {
                this.speed=StaticInitConfig.SPEED_OF_ATTACKER_ON_TASK;
                this.rrt_alg.setMax_angle((float) Math.PI / 6);
            }
            
            
            if (this.target_indicated_by_role.getIndex() == Threat.UAV_BASE_INDEX) {
                logger.debug("find path for retunning uav");
            } else {
                logger.debug("find path for busy uav");
            }
            UAVPath shortest_path = null;
            float shotest_path_length = Float.MAX_VALUE;
            planning_times = StaticInitConfig.rrt_planning_times_for_attacker;
            boolean available_path_found = false;
            int nums_of_trap = 0;
            for (int i = 0; i <= planning_times; i++) {
                this.runRRT();
                available_path_found = available_path_found || this.path_planned_at_current_time_step.pathReachEndPoint(this.target_indicated_by_role.getCoordinates());
                if (!available_path_found && nums_of_trap < 10) {
                    i--;
                    nums_of_trap++;
                    continue;
                }
                if (this.path_planned_at_current_time_step.getPath_length() < shotest_path_length) {
                    shotest_path_length = this.path_planned_at_current_time_step.getPath_length();
                    shortest_path = this.path_planned_at_current_time_step;
                }
            }
            if (shortest_path != null) {
                Point path_dest = shortest_path.getLastWaypoint();
                if (path_dest.getX() == this.center_coordinates[0] && path_dest.getY() == this.center_coordinates[1]) {
                    stucked_times++;
                    if(this.stucked_times>this.max_stucked_times)
                    {
                        this.setVisible(false);
                    }
                    this.setNeed_to_replan(true);
                    logger.debug("not able to plan path for this uav " + this.getIndex());
                }else{
                    stucked_times=0;
                }
                this.path_planned_at_current_time_step = shortest_path;
            } else {
                logger.error("null path");
            }
            this.setReplanned_at_current_time_step(true);
        } else {
            if (!this.need_to_replan) {
                logger.debug("no need to plan");
            }
            if (target_indicated_by_role == null) {
                logger.debug("no target");
            }
            this.setReplanned_at_current_time_step(false);
        }
    }

    private void runRRT() {
        rrt_alg.setMax_delta_distance(this.speed);
        rrt_alg.setObstacles(this.getObstacles());
        rrt_alg.setGoal_coordinate(goal_for_each_iteration);
        rrt_alg.setInit_coordinate(center_coordinates);
        rrt_tree = rrt_alg.buildRRT(center_coordinates, current_angle);
        this.setPath_prefound(rrt_tree.getPath_found());
        this.resetCurrentIndexOfPath();
    }

    public void resetCurrentIndexOfPath() {
        this.current_index_of_planned_path = 0;
    }

    /**
     * Drive Attacker to next waypoint
     *
     * @return
     */
    public boolean moveToNextWaypoint() {
        if (this.target_indicated_by_role != null) {
            current_index_of_planned_path++;
            if (path_planned_at_current_time_step.getWaypointNum() == 0 || current_index_of_planned_path >= path_planned_at_current_time_step.getWaypointNum()) {
                this.moved_at_last_time = false;
                this.setNeed_to_replan(true);
                return false;
            }
            Point current_waypoint = this.path_planned_at_current_time_step.getWaypoint(current_index_of_planned_path);
            float[] coordinate = current_waypoint.toFloatArray();
            setPreviousWaypoint();
            moveTo(coordinate[0], coordinate[1]);
            this.current_angle = (float) current_waypoint.getYaw();
            this.moved_at_last_time = true;
            this.setNeed_to_replan(false);
            if(path_planned_at_current_time_step.getWaypointNum() == 0 || current_index_of_planned_path == path_planned_at_current_time_step.getWaypointNum())
            {
                this.setNeed_to_replan(true);
            }
            return this.moved_at_last_time;
        } else {
            this.moved_at_last_time = false;
            this.setNeed_to_replan(true);
            return moved_at_last_time;
        }
    }

    public boolean isEnduranceCapReachable(Target potential_target) {
        float dist_to_potential_target = DistanceUtil.distanceBetween(this.center_coordinates, potential_target.getCoordinates());
        float dist_from_potential_target_to_uav_base = DistanceUtil.distanceBetween(potential_target.getCoordinates(), World.uav_base.getCoordinate());
        float path_parameter = 1.5f;
        if (path_parameter * (dist_to_potential_target + dist_from_potential_target_to_uav_base) > this.remained_energy) {
            return false;
        }
        return true;
    }

    public UAVPath getPath_planned_at_last_time_step() {
        return path_planned_at_last_time_step;
    }

    public UAVPath getFuturePath() {
        if (!this.isVisible()) {
            return null;
        }
        UAVPath future_path = new UAVPath();
        synchronized (path_planned_at_current_time_step) {
            for (int i = current_index_of_planned_path; i < path_planned_at_current_time_step.getWaypointNum(); i++) {
                future_path.addWaypointToEnd(path_planned_at_current_time_step.getWaypoint(i));
            }
        }
        return future_path;
    }

    public void setTarget_indicated_by_role(Target target_indicated_by_role) {
        if (this.target_indicated_by_role == target_indicated_by_role) {
            return;
        }
        this.target_indicated_by_role = target_indicated_by_role;
    }

    private void setPreviousWaypoint() {
        Point previous_waypoint = new Point(this.getCenter_coordinates()[0], this.getCenter_coordinates()[1], this.current_angle);
        this.history_path.addWaypointToEnd(previous_waypoint);
    }

    /**
     * parsing the received information, and the information is converted into
     * structures uav can understand.
     *
     * @param msg
     */
    private void parseMessage(Message msg) {
        int msg_type = msg.getMsg_type();
        if (msg_type == Message.CONFLICT_MSG) {
            Conflict conflict = (Conflict) msg;
            this.addConflict(conflict);
            this.setNeed_to_replan(true);
        } else if (msg_type == Message.OBSTACLE_MSG) {
            Obstacle obstacle = (Obstacle) msg;
            this.addObstacle(obstacle);
            if (this.getTarget_indicated_by_role() == null || !this.isObstacleInTargetMBR(obstacle.getShape().getBounds())) {
                this.setNeed_to_replan(true);
            }
        } else if (msg_type == Message.THREAT_MSG) {
            Threat threat = (Threat) msg;
            this.addThreat(threat);
            this.setNeed_to_replan(true);
        }
    }

    /**
     * receive message and parse message
     *
     * @param msg
     */
    public void receiveMesage(Message msg) {
        if (msg != null) {
            parseMessage(msg);
        }
    }

    /**
     * To determine whether the need for re-planning
     *
     * @param need_to_replan
     */
    public void setNeed_to_replan(boolean need_to_replan) {
        this.need_to_replan = need_to_replan;
    }

    public void increaseHovered_time_step() {
        this.hovered_time_step++;
    }

    public boolean isReplanned_at_current_time_step() {
        return replanned_at_current_time_step;
    }

    public void setReplanned_at_current_time_step(boolean replanned_at_current_time_step) {
        this.replanned_at_current_time_step = replanned_at_current_time_step;
    }

    public float[] getPrevious_waypoint() {
        return this.history_path.getLastWaypoint().toFloatArray();
    }

    public boolean isMoved_at_last_time() {
        return moved_at_last_time;
    }

    public void setMoved_at_last_time(boolean moved_at_last_time) {
        this.moved_at_last_time = moved_at_last_time;
    }

    public LinkedList<Point> getPath_prefound() {
        return path_planned_at_current_time_step.getWaypointsAsLinkedList();
    }

    public void setPath_prefound(UAVPath path_prefound) {
        this.path_planned_at_current_time_step = path_prefound;
    }

    public RRTTree getRrt_tree() {
        return rrt_tree;
    }

    @Override
    public ArrayList<Obstacle> getObstacles() {
        return this.kb.getObstacles();
    }

    @Override
    public ArrayList<Conflict> getConflicts() {
        return this.kb.getConflicts();
    }

    @Override
    public ArrayList<Threat> getThreats() {
        return this.kb.getThreats();
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
        if (!this.kb.containsObstacle(obs)) {
            this.kb.addObstacle(obs);
        }
    }

    @Override
    public void addConflict(Conflict conflict) {
        this.kb.addConflict(conflict);
    }

    @Override
    public void addThreat(Threat threat) {
        ArrayList<Threat> threats = this.getThreats();
        for (int i = 0; i < threats.size(); i++) {
            Threat current_threat = threats.get(i);
            if (threat.getIndex() == current_threat.getIndex()) {
                this.kb.removeThreat(current_threat);
                this.addThreat(threat);
                if (this.target_indicated_by_role != null && threat.getIndex() == this.target_indicated_by_role.getIndex()) {
                    this.target_indicated_by_role = threat;
                }
                return;
            }
        }
        if (this.target_indicated_by_role != null && threat.getIndex() == this.target_indicated_by_role.getIndex()) {
            this.target_indicated_by_role = threat;
        }
        this.kb.addThreat(threat);
    }

    private float[] genRandomHoveringGoalV1(float[] threat_location, float hover_radius, ArrayList<Obstacle> obstacles) {
        float[] random_goal_coordinate = new float[2];
        double random_theta = Math.random() * Math.PI * 2;
        random_goal_coordinate[0] = threat_location[0] + (float) Math.cos(random_theta) * hover_radius;
        random_goal_coordinate[1] = threat_location[1] + (float) Math.sin(random_theta) * hover_radius;
        boolean collisioned = true;
        boolean withinBound = BoundUtil.withinBound(random_goal_coordinate[0], random_goal_coordinate[1], World.bound_width, World.bound_height);
        while (collisioned || !withinBound) {
            random_goal_coordinate[0] = threat_location[0] + (float) Math.cos(random_theta) * hover_radius;
            random_goal_coordinate[1] = threat_location[1] + (float) Math.sin(random_theta) * hover_radius;
            if (!ConflictCheckUtil.checkPointInObstacles(obstacles, random_goal_coordinate[0], random_goal_coordinate[1])) {
                collisioned = false;
            } else {
                random_theta = Math.random() * Math.PI * 2;
            }
            withinBound = BoundUtil.withinBound(random_goal_coordinate[0], random_goal_coordinate[1], World.bound_width, World.bound_height);
            logger.debug("find hovering goal for attackers");
        }
        return random_goal_coordinate;
    }

    private float[] genRandomHoveringGoal(float[] threat_location, float hover_radius, ArrayList<Obstacle> obstacles) {
        double random_center_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(threat_location[0]-this.center_coordinates[0], threat_location[1]-this.center_coordinates[1]);
        float[] random_goal_coordinate = new float[2];

        boolean collisioned = true;
        boolean withinBound = false;
        while (collisioned || !withinBound) {
            random_goal_coordinate[0] = threat_location[0] + (float) Math.cos(random_center_angle) * hover_radius;
            random_goal_coordinate[1] = threat_location[1] + (float) Math.sin(random_center_angle) * hover_radius;
            withinBound = BoundUtil.withinBound(random_goal_coordinate[0], random_goal_coordinate[1], World.bound_width, World.bound_height);
            collisioned=ConflictCheckUtil.checkPointInObstacles(obstacles, random_goal_coordinate[0], random_goal_coordinate[1]);
            int total_segment_num=4;
            for(int i=1;i<=total_segment_num;i++)
            {
                float[] temp_goal_coord=new float[2];
                temp_goal_coord[0]=threat_location[0] + (float) Math.cos(random_center_angle) * hover_radius*i/(total_segment_num+1);
                temp_goal_coord[1]=threat_location[1] + (float) Math.sin(random_center_angle) * hover_radius*i/(total_segment_num+1);
                collisioned=collisioned && ConflictCheckUtil.checkPointInObstacles(obstacles, random_goal_coordinate[0], random_goal_coordinate[1]);
            }
            random_center_angle+=Math.PI/36;
            logger.debug("find hovering goal for attackers");
        }
        return random_goal_coordinate;
    }

    public KnowledgeInterface getKb() {
        return kb;
    }

    public void setKb(WorldKnowledge kb) {
        this.kb = kb;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getFly_mode() {
        return fly_mode;
    }

    public void setFly_mode(int fly_mode) {
        this.fly_mode = fly_mode;
    }

    public int getHovered_time_step() {
        return hovered_time_step;
    }

    public void setHovered_time_step(int hovered_time_step) {
        this.hovered_time_step = hovered_time_step;
    }

    public UAVPath getHistory_path() {
        return history_path;
    }

    @Override
    public boolean containsThreat(Threat threat) {
        return this.kb.containsThreat(threat);
    }

    @Override
    public boolean containsObstacle(Obstacle obstacle) {
        return this.kb.containsObstacle(obstacle);
    }

}
