/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.RRT;

import java.util.ArrayList;
import util.ConflictCheckUtil;
import util.DistanceUtil;
import static util.DistanceUtil.distanceBetween;
import world.model.Conflict;
import world.model.Obstacle;
import world.model.shape.DubinsCurve;
import world.model.shape.Point;
import world.model.shape.Trajectory;

/**
 *
 * @author boluo
 */
public class RRTAlgWithDubinCurve extends RRTAlg {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RRTAlgWithDubinCurve.class);

    public RRTAlgWithDubinCurve(float[] init_coordinate, float[] goal_coordinate, float goal_probability, int bound_width, int bound_height, int k_step, float max_delta_distance, ArrayList<Obstacle> obstacles, ArrayList<Conflict> conflicts, int uav_index) {
        super(init_coordinate, goal_coordinate, goal_probability, bound_width, bound_height, k_step, max_delta_distance, obstacles, conflicts, uav_index);
    }

    @Deprecated
    public RRTTree buildRRTStar3(float[] init_coordinate, float current_angle) {
        this.setInit_coordinate(init_coordinate);
        RRTTree G = new RRTTree();
        RRTNode start_node = new RRTNode(init_coordinate[0], init_coordinate[1]);
        start_node.setCurrent_angle(current_angle);
        G.addNode(start_node, null);

        float[] random_goal;
        RRTNode nearest_node;

        for (int time_step = 1; time_step <= k_step;) {
            //random choose a direction or goal
            random_goal = randGoal(this.goal_coordinate, goal_probability, bound_width, bound_height, obstacles);
            //choose the nearest node to extend
            nearest_node = nearestVertex(random_goal, G);
            //extend the child node and validate its confliction

            Point start = new Point(nearest_node.getCoordinate()[0], nearest_node.getCoordinate()[1], nearest_node.getCurrent_angle());
            Point end = new Point(random_goal[0], random_goal[1], Math.PI / 4);
            DubinsCurve dc = new DubinsCurve(start, end, 150, false, 1, 10);
            Trajectory traj = dc.getTraj();
            Point z_new = traj.getEndPoint();

            if (!ConflictCheckUtil.checkTrajectoryInObstacles(obstacles, traj)) {
                ArrayList<RRTNode> nearestNodesToNewNodeSet = neareSortedNodesToNode(G, z_new.toFloatArray(), Math.E * 2 * Math.log(G.getNodeCount() + 1));
                float c_min = nearest_node.getPath_lenght_from_root() + (float) traj.getCost();
                RRTNode z_min = nearest_node;
                Trajectory traj_min = traj;
                for (RRTNode z_near : nearestNodesToNewNodeSet) {
                    Point near_as_start = new Point(z_near.getCoordinate()[0], z_near.getCoordinate()[1], z_near.getCurrent_angle());
                    DubinsCurve inner_dc = new DubinsCurve(near_as_start, z_new, 150, false, 1, 10);
                    Trajectory inner_traj = inner_dc.getTraj();
                    Point inner_end_point = inner_traj.getEndPoint();
                    if (!ConflictCheckUtil.checkTrajectoryInObstacles(obstacles, inner_traj) && DistanceUtil.distanceBetween(new float[]{(float) inner_end_point.getX(), (float) inner_end_point.getY()}, z_near.getCoordinate()) < 1) {
                        if (z_near.getPath_lenght_from_root() + inner_traj.getCost() < c_min) {
                            c_min = z_near.getPath_lenght_from_root() + (float) inner_traj.getCost();
                            z_min = z_near;
                            traj_min = inner_traj;
                        }
                    }
                }
                RRTNode parent = z_min;
                for (Point point : traj_min.getPoints()) {
                    RRTNode node_to_add = new RRTNode((float) point.getX(), (float) point.getY());
                    G.addNode(node_to_add, parent);
                    parent = node_to_add;
                }

                if (distanceBetween(parent.getCoordinate(), goal_coordinate) < goal_range_for_delta) {
//                    G.generatePath(parent);
                    G.generatePath();
                    return G;
                }

                RRTNode z_new_node = parent;
                for (RRTNode z_near : nearestNodesToNewNodeSet) {
                    if (z_near == z_min) {
                        continue;
                    }
                    Point near_as_start = new Point(z_near.getCoordinate()[0], z_near.getCoordinate()[1], z_near.getCurrent_angle());
                    DubinsCurve inner_dc = new DubinsCurve(z_new, near_as_start, 150, false,1, 10);
                    Trajectory inner_traj = inner_dc.getTraj();
                    Point inner_end_point = inner_traj.getEndPoint();
                    if (!ConflictCheckUtil.checkTrajectoryInObstacles(obstacles, inner_traj) && DistanceUtil.distanceBetween(new float[]{(float) inner_end_point.getX(), (float) inner_end_point.getY()}, z_near.getCoordinate()) < 1) {
                        if (z_near.getPath_lenght_from_root() > z_new_node.getPath_lenght_from_root() + c_min) {
                            G.changeParent(z_new_node, z_near);
                        }
                    }
                }
            }
        }
//        G.generatePath(new_node);
        G.generatePath();
        return G;
    }

    @Deprecated
    public RRTTree buildRRTStar2FromIRRT(float[] init_coordinate, float current_angle) {
        this.setInit_coordinate(init_coordinate);
        RRTTree G = new RRTTree();
        RRTNode start_node = new RRTNode(init_coordinate[0], init_coordinate[1]);
        start_node.setCurrent_angle(current_angle);
        G.addNode(start_node, null);

        float[] random_goal;
        ArrayList<RRTNode> near_node_set;
        RRTNode nearest_node = null;
        double radius = 9999;

        for (int time_step = 1; time_step <= k_step;) {
            //random choose a direction or goal
            random_goal = randGoal(this.goal_coordinate, goal_probability, bound_width, bound_height, obstacles);
            //choose the nearest node to extend
            near_node_set = neareSortedNodesToNode(G, random_goal, radius);

            boolean node_added = false;
            for (int i = 0; i < near_node_set.size(); i++) {
                nearest_node = near_node_set.get(i);

                Point start = new Point(nearest_node.getCoordinate()[0], nearest_node.getCoordinate()[1], nearest_node.getCurrent_angle());
                Point end = new Point(random_goal[0], random_goal[1], Math.PI / 4);
                DubinsCurve dc = new DubinsCurve(start, end, 150, false,1, 10);
                Trajectory traj = dc.getTraj();
                if (!ConflictCheckUtil.checkTrajectoryInObstacles(obstacles, traj)) {

                    int total_num = traj.getMaxTime();
                    for (int j = 0; j <= total_num; j++) {
                        Point point = traj.get(j);
                        float[] point_coord = point.toFloatArray();
                        RRTNode node = new RRTNode(point_coord[0], point_coord[1]);
                        nearest_node = node;
                        G.addNode(node, nearest_node);
                    }

                    if (DistanceUtil.distanceBetween(nearest_node.getCoordinate(), goal_coordinate) < goal_range_for_delta) {
//                        G.generatePath(nearest_node);
                        G.generatePath();
                        return G;
                    }

                    node_added = true;
                }
                if (node_added) {
                    break;
                }
            }

            //rewire
            if (node_added) {
                for (int i = 0; i < near_node_set.size(); i++) {
                    RRTNode this_node = near_node_set.get(i);
                    float newNodePathLenght = nearest_node.getPath_lenght_from_root();
                    double thisNodepathLength = this_node.getPath_lenght_from_root();
                    float distance = DistanceUtil.distanceBetween(nearest_node.getCoordinate(), this_node.getCoordinate());

                    if (newNodePathLenght + distance < thisNodepathLength) {
                        Point start = new Point(nearest_node.getCoordinate()[0], nearest_node.getCoordinate()[1], nearest_node.getCurrent_angle());
                        Point end = new Point(this_node.getCoordinate()[0], this_node.getCoordinate()[1], Math.PI / 4);
                        DubinsCurve dc = new DubinsCurve(start, end, 150, false,1, 10);
                        Trajectory traj = dc.getTraj();
                        if (!ConflictCheckUtil.checkTrajectoryInObstacles(obstacles, traj)) {
                            G.changeParent(this_node, nearest_node);
                        }
                    }
                }
            }

        }
//        G.generatePath(nearest_node);
        G.generatePath();
        return G;
    }
}
