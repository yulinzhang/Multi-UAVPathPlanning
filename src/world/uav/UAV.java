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

import world.model.shape.Circle;
import world.model.Obstacle;
import world.World;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
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

    private Circle uav_radar;
    private Color center_color;
    public Color radar_color;

    private UAVPath path_planned_at_current_time_step;
    private int current_index_of_planned_path = 0;
    private UAVPath path_planned_at_last_time_step;
    private UAVPath history_path;
    private boolean need_to_replan = true;

    //variables for path planning
    private WorldKnowledge kb;

    private RRTAlg rrt_alg;
    private RRTTree rrt_tree;
    private int speed = 10;
    private float current_angle = -1;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UAV.class);

    /**
     *
     * @param index
     * @param target
     * @param center_coordinates
     */
    public UAV(int index, Target target, int flag_of_war, float[] center_coordinates, ArrayList<Obstacle> obstacles) {
        super(index, target, flag_of_war, center_coordinates);
        this.uav_radar = new Circle(center_coordinates[0], center_coordinates[1], scout_radar_radius);
        this.path_planned_at_current_time_step = new UAVPath();
        this.history_path = new UAVPath();
        setPreviousWaypoint();
        this.kb = new WorldKnowledge();
        this.kb.setObstacles(obstacles);
        if (target == null) {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), null, StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, this.getObstacles());
        } else {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), target.getCoordinates(), StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, this.getObstacles());
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

    public void pathPlan() {
        if (this.need_to_replan) {
            this.path_planned_at_last_time_step = this.path_planned_at_current_time_step;
            this.runRRT();
        }
    }

    private void runRRT() {
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

    public boolean moveToNextWaypoint() {
        current_index_of_planned_path++;
        if (path_planned_at_current_time_step.getWaypointNum() == 0 || current_index_of_planned_path >= path_planned_at_current_time_step.getWaypointNum()) {
            return false;
        }
        Point current_waypoint = this.path_planned_at_current_time_step.getWaypoint(current_index_of_planned_path);
        float[] coordinate = current_waypoint.toFloatArray();
        setPreviousWaypoint();
        moveTo(coordinate[0], coordinate[1]);
        this.current_angle = (float) current_waypoint.getYaw();

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
        this.target_indicated_by_role = target_indicated_by_role;
        this.setNeed_to_replan(true);
    }

    private void setPreviousWaypoint() {
        Point previous_waypoint = new Point(this.getCenter_coordinates()[0], this.getCenter_coordinates()[1], this.current_angle);
        this.history_path.addWaypointToEnd(previous_waypoint);
    }

    public void setNeed_to_replan(boolean need_to_replan) {
        this.need_to_replan = need_to_replan;
    }

    public float[] getPrevious_waypoint() {
        return this.history_path.getLastWaypoint().toFloatArray();
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

//    public void setPath_prefound(LinkedList<Point> path_prefound) {
//        this.path_planned_at_current_time_step.setWaypoints(path_prefound);
//    }
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

    public WorldKnowledge getKb() {
        return kb;
    }

    public void setKb(WorldKnowledge kb) {
        this.kb = kb;
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

}
