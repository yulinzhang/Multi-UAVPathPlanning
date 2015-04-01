/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uav;

import algorithm.RRT.RRTAlg;
import algorithm.RRT.RRTTree;
import config.StaticInitConfig;
import java.util.LinkedList;
import java.util.Vector;

import world.model.shape.Circle;
import world.model.Obstacle;
import world.World;
import world.model.Target;
import world.model.shape.DubinsCurve;
import world.model.shape.Point;
import world.model.shape.Trajectory;

/**
 *
 * @author Yulin_Zhang
 */
public class UAV extends Unit {

    private Circle uav_radar;

    private LinkedList<Point> path_prefound;
    private int current_index = 0;

    private float[] previous_waypoint = new float[2];

    //variables for path planning
    private Vector<Obstacle> obstacles;

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
    public UAV(int index, Target target, int flag_of_war, float[] center_coordinates, Vector<Obstacle> obstacles) {
        super(index, target, flag_of_war, center_coordinates);
        this.uav_radar = new Circle(center_coordinates[0], center_coordinates[1], scout_radar_radius);
        this.path_prefound = new LinkedList<Point>();
        setPreviousWaypoint();
        this.obstacles = new Vector<Obstacle>();
        for (Obstacle obs : obstacles) {
            this.obstacles.add(obs);
        }
        if (target == null) {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), null, StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, this.obstacles);
        } else {
            rrt_alg = new RRTAlg(super.getCenter_coordinates(), target.getCoordinates(), StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, this.obstacles);
        }
    }

    public void ignoreEverythingAndTestDubinPath() {
        Point start = new Point(this.getCenter_coordinates()[0], this.getCenter_coordinates()[1], Math.PI*2);
        Point end = new Point(target_indicated_by_role.getCoordinates()[0],target_indicated_by_role.getCoordinates()[1],  Math.PI*2-Math.PI*7/4);
        DubinsCurve dc = new DubinsCurve(start, end, 150, false);
        Trajectory traj = dc.getTrajectory(1, 10);
        logger.debug("total waypoint num:" +traj.getPoints().length);
        logger.debug("total traj lenght:"+traj.getCost());
        logger.debug("end node:"+traj.getEndPoint());
        logger.debug("goal point:"+end);
        LinkedList<Point> path = new LinkedList<Point>();
        for (Point point : traj.getPoints()) {
            path.add(point);
        }
        this.setPath_prefound(path);
    }

    public void runRRT() {
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
        this.current_index = -1;
    }

    public boolean moveToNextWaypoint() {
        current_index++;
        if (path_prefound.size() == 0 || current_index >= path_prefound.size()) {
            return false;
        }
        Point current_waypoint = this.path_prefound.get(current_index);
        float[] coordinate = current_waypoint.toFloatArray();
        setPreviousWaypoint();
        moveTo(coordinate[0], coordinate[1]);
        this.current_angle = (float) current_waypoint.getYaw();
        return true;
    }

    public LinkedList<Point> getFuturePath() {
        LinkedList<Point> future_path = new LinkedList<Point>();
        for (int i = current_index; i < path_prefound.size(); i++) {
            future_path.add(path_prefound.get(i));
        }
        return future_path;
    }

    private void setPreviousWaypoint() {
        this.previous_waypoint[0] = this.getCenter_coordinates()[0];
        this.previous_waypoint[1] = this.getCenter_coordinates()[1];
    }

    public float[] getPrevious_waypoint() {
        return previous_waypoint;
    }

    public Circle getUav_radar() {
        return uav_radar;
    }

    public void setUav_radar(Circle uav_radar) {
        this.uav_radar = uav_radar;
    }

    public LinkedList<Point> getPath_prefound() {
        return path_prefound;
    }

    public void setPath_prefound(LinkedList<Point> path_prefound) {
        this.path_prefound = path_prefound;
    }

    public RRTTree getRrt_tree() {
        return rrt_tree;
    }

}
