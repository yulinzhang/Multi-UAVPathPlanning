/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.uav;

import algorithm.RRT.RRTAlg;
import algorithm.RRT.RRTTree;
import config.GraphicConfig;
import config.StaticInitConfig;
import java.awt.Color;
import java.util.LinkedList;
import java.util.ArrayList;
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
public class UAV extends Unit implements KnowledgeAwareInterface {

    private Circle uav_radar; //the uav color in world
    private Color center_color;
    public Color radar_color; //the radar color in world

    private UAVPath path_planned_at_current_time_step;
    private int current_index_of_planned_path = 0; //index of waypoint
    private UAVPath path_planned_at_last_time_step;
    private UAVPath history_path;
    private boolean need_to_replan = true;
    private boolean replanned_at_current_time_step = false;
    private boolean target_reached = false;
    private boolean moved_at_last_time=false;

    //variables for conflict planning
    private KnowledgeInterface kb;

    private RRTAlg rrt_alg;
    private RRTTree rrt_tree;
    private int speed = 5;
    private float current_angle = -1;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UAV.class);

    /**
     *
     * @param index
     * @param target
     * @param center_coordinates
     */
    public UAV(int index, Target target, int flag_of_war, float[] center_coordinates, ArrayList<Obstacle> obstacles) {
        super(index, (Target) target.deepClone(), flag_of_war, center_coordinates);
        this.uav_radar = new Circle(center_coordinates[0], center_coordinates[1], scout_radar_radius);
        this.path_planned_at_current_time_step = new UAVPath();
        this.history_path = new UAVPath();
        setPreviousWaypoint();
        this.setTarget_reached(false);
        this.kb = new OntologyBasedKnowledge();//OntologyBasedKnowledge();WorldKnowledge
        this.kb.setObstacles(obstacles);
        if (target == null) {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), null, StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, null, this.getConflicts(), this.index);
        } else {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), target.getCoordinates(), StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, null, this.getConflicts(), this.index);
        }
        initColor(index);
    }

    private void initColor(int uav_index) {
        center_color = GraphicConfig.uav_colors.get(uav_index);
        radar_color = new Color(center_color.getRed(), center_color.getGreen(), center_color.getBlue(), 128);
    }

    public void ignoreEverythingAndTestDubinPath() {
        Point start = new Point(this.getCenter_coordinates()[0], this.getCenter_coordinates()[1], Math.PI * 2);
        Point end = new Point(target_indicated_by_role.getCoordinates()[0], target_indicated_by_role.getCoordinates()[1], Math.PI * 2 - Math.PI * 7 / 4);
        DubinsCurve dc = new DubinsCurve(start, end, 150, false);
        Trajectory traj = dc.getTrajectory(1, 10);
        logger.debug("total waypoint num:" + traj.getPoints().length);
        logger.debug("total traj lenght:" + traj.getCost());
        logger.debug("end node:" + traj.getEndPoint());
        logger.debug("goal point:" + end);
        UAVPath path = new UAVPath();
        for (Point point : traj.getPoints()) {
            path.addWaypointToEnd(point);
        }
        this.setPath_prefound(path);
    }

    /**
     * path planning
     */
    public void pathPlan() {
        if (this.need_to_replan) {
            this.path_planned_at_last_time_step = this.path_planned_at_current_time_step;
            UAVPath shortest_path = null;
            float shotest_path_length = Float.MAX_VALUE;
            for (int i = 0; i <= StaticInitConfig.rrt_planning_times_for_each_uav; i++) {
                this.runRRT();
                if (!this.path_planned_at_current_time_step.pathReachEndPoint(this.target_indicated_by_role.getCoordinates())) {
                    i--;
                    continue;
                }
                if (this.path_planned_at_current_time_step.getPath_length() < shotest_path_length) {
                    shotest_path_length = this.path_planned_at_current_time_step.getPath_length();
                    shortest_path = this.path_planned_at_current_time_step;
                }
            }
            if (shortest_path != null) {
                this.path_planned_at_current_time_step = shortest_path;
            } else {
                logger.error("null path");
            }
            this.setReplanned_at_current_time_step(true);
        } else {
            this.setReplanned_at_current_time_step(false);
        }
    }

    private void runRRT() {
        rrt_alg.setMax_delta_distance(this.speed);
        rrt_alg.setObstacles(this.getObstacles());
        rrt_alg.setGoal_coordinate(target_indicated_by_role.getCoordinates());
        rrt_alg.setInit_coordinate(center_coordinates);
        rrt_tree = rrt_alg.buildRRT(center_coordinates, current_angle);
        this.setPath_prefound(rrt_tree.getPath_found());
        this.resetCurrentIndexOfPath();
    }

    public void runRRTStar() {
        rrt_alg.setGoal_coordinate(target_indicated_by_role.getCoordinates());
        rrt_alg.setInit_coordinate(center_coordinates);
        rrt_tree = rrt_alg.buildRRTStar2FromIRRT(center_coordinates, current_angle);
        this.setPath_prefound(rrt_tree.getPath_found());
        this.resetCurrentIndexOfPath();
    }

    /**
     *
     * @param center_coordinate_x
     * @param center_coordinate_y
     */
    public void moveTo(float center_coordinate_x, float center_coordinate_y) {
        uav_center.setCoordinate(center_coordinate_x, center_coordinate_y);
        uav_radar.setCoordinate(center_coordinate_x, center_coordinate_y);
        this.setCenter_coordinates(uav_radar.getCenter_coordinates());
    }

    public void resetCurrentIndexOfPath() {
        this.current_index_of_planned_path = -1;
    }

    /**
     * Drive UAV to next waypoint
     *
     * @return
     */
    public boolean moveToNextWaypoint() {
        current_index_of_planned_path++;
        if (path_planned_at_current_time_step.getWaypointNum() == 0 || current_index_of_planned_path >= path_planned_at_current_time_step.getWaypointNum()) {
            this.moved_at_last_time=false;
            return false;
        }
        Point current_waypoint = this.path_planned_at_current_time_step.getWaypoint(current_index_of_planned_path);
        float[] coordinate = current_waypoint.toFloatArray();
        setPreviousWaypoint();
        moveTo(coordinate[0], coordinate[1]);
        this.current_angle = (float) current_waypoint.getYaw();
        this.moved_at_last_time=true;
        return true;
    }

    public UAVPath getPath_planned_at_last_time_step() {
        return path_planned_at_last_time_step;
    }

    public UAVPath getFuturePath() {
        UAVPath future_path = new UAVPath();
        for (int i = current_index_of_planned_path; i < path_planned_at_current_time_step.getWaypointNum(); i++) {
            future_path.addWaypointToEnd(path_planned_at_current_time_step.getWaypoint(i));
        }
        return future_path;
    }

    public void setTarget_indicated_by_role(Target target_indicated_by_role) {
        if (this.target_indicated_by_role == target_indicated_by_role) {
            return;
        }
        if (target_indicated_by_role.getIndex() != -1) {
            this.setTarget_reached(false);
        }
        this.target_indicated_by_role = target_indicated_by_role;
        this.setNeed_to_replan(true);
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
            //TODO:
            this.addConflict(conflict);
            this.setNeed_to_replan(true);
        } else if (msg_type == Message.OBSTACLE_MSG) {
            Obstacle obstacle = (Obstacle) msg;
            this.addObstacle(obstacle);
            this.setNeed_to_replan(true);
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

    public boolean isReplanned_at_current_time_step() {
        return replanned_at_current_time_step;
    }

    public void setReplanned_at_current_time_step(boolean replanned_at_current_time_step) {
        this.replanned_at_current_time_step = replanned_at_current_time_step;
    }

    public float[] getPrevious_waypoint() {
        return this.history_path.getLastWaypoint().toFloatArray();
    }

    public boolean isTarget_reached() {
        return target_reached;
    }

    public boolean isMoved_at_last_time() {
        return moved_at_last_time;
    }

    public void setMoved_at_last_time(boolean moved_at_last_time) {
        this.moved_at_last_time = moved_at_last_time;
    }

    public void setTarget_reached(boolean target_reached) {
        this.target_reached = target_reached;
    }

    public Circle getUav_radar() {
        return uav_radar;
    }

    public void setUav_radar(Circle uav_radar) {
        this.uav_radar = uav_radar;
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
                return;
            }
        }
        this.kb.addThreat(threat);
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

    public Color getCenter_color() {
        return center_color;
    }

    public Color getRadar_color() {
        return radar_color;
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
