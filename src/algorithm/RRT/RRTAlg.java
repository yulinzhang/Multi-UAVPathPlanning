/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.RRT;

import config.StaticInitConfig;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import util.BoundUtil;
import util.ConflictCheckUtil;
import util.DistanceUtil;
import static util.DistanceUtil.distanceBetween;
import util.MapSortUtil;
import util.VectorUtil;
import world.model.Conflict;
import world.model.Obstacle;
import world.model.shape.Point;

/**
 *
 * @author boluo
 */
public class RRTAlg implements Serializable {

    /**
     * external variables
     *
     */
    protected ArrayList<Obstacle> obstacles;
    protected int bound_width = 800;
    protected int bound_height = 600;
    protected float[] init_coordinate;
    protected float[] goal_coordinate;
    protected float current_angle;
    protected float goal_probability = 0.6f;
    protected ArrayList<Conflict> conflicts;
    protected int uav_index;

    protected boolean idle_uav = false;

    /**
     * internal variables
     *
     */
    protected int k_step = 20;
    protected float max_delta_distance = 5;
    protected float max_angle = (float) Math.PI / 6;
    /**
     * less than goal_range_for_delta means goal reached
     *
     */
    protected float goal_range_for_delta = StaticInitConfig.SAFE_DISTANCE_FOR_TARGET;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RRTAlg.class);

    /**
     *
     * @param init_coordinate
     * @param goal_coordinate
     * @param goal_probability
     * @param bound_width
     * @param bound_height
     * @param k_step
     * @param max_delta_distance
     * @param obstacles
     * @param threats
     * @return
     */
    public RRTAlg(float[] init_coordinate, float[] goal_coordinate, float goal_probability, int bound_width, int bound_height, int k_step, float max_delta_distance, ArrayList<Obstacle> obstacles, ArrayList<Conflict> conflicts, int uav_index) {
        this.init_coordinate = init_coordinate;
        this.k_step = k_step;
        this.max_delta_distance = max_delta_distance;
        this.obstacles = obstacles;
        this.bound_height = bound_height;
        this.bound_width = bound_width;
        this.goal_probability = goal_probability;
        this.goal_coordinate = goal_coordinate;
        if (conflicts != null) {
            this.conflicts = conflicts;
        } else {
            this.conflicts = new ArrayList<Conflict>();
        }
        this.uav_index = uav_index;
    }

    public RRTTree buildRRT(float[] init_coordinate, double current_angle) {

        this.setInit_coordinate(init_coordinate);
        RRTTree G = new RRTTree();
        RRTNode start_node = new RRTNode(init_coordinate[0], init_coordinate[1]);
        start_node.setCurrent_angle(current_angle);
        G.addNode(start_node, null);
        float probability = goal_probability;
//        if(idle_uav)
//        {
//            probability=0.1f;
//        }else{
//            probability=goal_probability;
//        }

        float[] random_goal;
        RRTNode nearest_node = null;
        RRTNode new_node = null;

        int time_step = 0;
        for (time_step = 0; time_step <= k_step;time_step++) {
            //random choose a direction or goal
            random_goal = randGoal(this.goal_coordinate, probability, bound_width, bound_height, obstacles);
            //choose the nearest node to extend
            nearest_node = nearestVertex(random_goal, G);
            //extend the child node and validate its confliction 
            new_node = extendTowardGoalWithDynamics(nearest_node, random_goal, this.max_delta_distance, max_angle);
            new_node.setExpected_time_step(nearest_node.getExpected_time_step() + 1);
            boolean conflict_with_other_uavs = false;
            int conflict_num = this.conflicts.size();
            for (int i = 0; i < conflict_num; i++) {
                Conflict conflict = this.conflicts.get(i);
                if (conflict.getUav_index() > this.uav_index) {
                    conflict_with_other_uavs = ConflictCheckUtil.checkUAVConflict(new_node, conflict);
                }
                if (conflict_with_other_uavs) {
                    break;
                }
            }
//            conflict_with_other_uavs=false;
            boolean conflicted = ConflictCheckUtil.checkNodeInObstacles(obstacles, new_node) || conflict_with_other_uavs;
//            boolean within_bound = BoundUtil.withinBound(new_node, bound_width, bound_height);
            //if not conflicted,add the child to the tree
            if (!conflicted && true) {
                G.addNode(new_node, nearest_node);
                if (DistanceUtil.distanceBetween(new_node.getCoordinate(), goal_coordinate) < this.max_delta_distance) {
                    Point goal_point = new Point(goal_coordinate[0], goal_coordinate[1], 0);
                    G.generatePath();
                    G.getPath_found().addWaypointToEnd(goal_point);
                    logger.debug(time_step);
                    return G;
                }
//                time_step++;
            }
            
//            num_of_trap++;
//            if (idle_uav && num_of_trap > 2 * k_step) {
//                new_node = nearest_node;
//                break;
//            }
//           new_node=nearest_node; 
        }
        G.generatePath();
//        G.setPath_found(null);
        return G;
    }

   

    @Deprecated
    public RRTTree buildRRTStar1(float[] init_coordinate, float current_angle) {
        this.setInit_coordinate(init_coordinate);
        RRTTree G = new RRTTree();
        RRTNode start_node = new RRTNode(init_coordinate[0], init_coordinate[1]);
        start_node.setCurrent_angle(current_angle);
        G.addNode(start_node, null);

        float[] random_goal;
        RRTNode nearest_node;
        RRTNode new_node = null;
        double radius = 9999;

        for (int time_step = 1; time_step <= k_step;) {
            //random choose a direction or goal
            random_goal = randGoal(this.goal_coordinate, goal_probability, bound_width, bound_height, obstacles);
            //choose the nearest node to extend
            nearest_node = nearestVertex(random_goal, G);
            //extend the child node and validate its confliction
            new_node = extendTowardGoalWithDynamics(nearest_node, random_goal, this.max_delta_distance, max_angle);

            boolean conflicted = ConflictCheckUtil.checkNodeInObstacles(obstacles, new_node);
//            boolean within_bound = BoundUtil.withinBound(new_node, bound_width, bound_height);
            //if not conflicted,add the child to the tree
            if (!conflicted && true) {
                //
                if (distanceBetween(new_node.getCoordinate(), goal_coordinate) < goal_range_for_delta) {
                    G.addNode(new_node, nearest_node);
//                    G.generatePath(new_node);
                    G.generatePath();
                    return G;
                }
                int max_num = 200;//(int) (2 * Math.E * Math.log(G.getNodeCount()));
                ArrayList<RRTNode> nearestNodesToNewNodeSet = neareSortedNodesToNode(G, new_node.getCoordinate(), radius);
                if (nearestNodesToNewNodeSet.size() > max_num) {
                    radius = DistanceUtil.distanceBetween(new_node.getCoordinate(), nearestNodesToNewNodeSet.get(max_num).getCoordinate());
                }
                G.addNode(new_node, nearest_node);
                for (RRTNode near_node : nearestNodesToNewNodeSet) {
                    rewireRRTStar(G, near_node, new_node);
                }
                for (RRTNode near_node : nearestNodesToNewNodeSet) {
                    rewireRRTStar(G, new_node, near_node);
                }
                time_step++;
            }
        }
//        G.generatePath(new_node);
        G.generatePath();
        return G;
    }

    protected void rewireRRTStar(RRTTree G, RRTNode potential_parent, RRTNode new_node) {
        float[] new_node_coordinate = new_node.getCoordinate();
        float[] potential_parent_coordinate = potential_parent.getCoordinate();
        if (!ConflictCheckUtil.checkLineInObstacles(obstacles, potential_parent_coordinate, new_node_coordinate)) {
            float min_path_length = new_node.getPath_lenght_from_root();
            float distance_between_two_nodes = DistanceUtil.distanceBetween(potential_parent_coordinate, new_node_coordinate);
            if (potential_parent.getPath_lenght_from_root() + distance_between_two_nodes < min_path_length) {
                G.changeParent(new_node, potential_parent);
            }
        }
    }

    /**
     * sort the near nodes with distance, the shorter, the index is smaller
     *
     * @param G
     * @param node
     * @param radius
     * @return
     */
    protected ArrayList<RRTNode> neareSortedNodesToNode(RRTTree G, float[] node_coordinate, double radius) {
        RRTNode temp_node;
        Map<RRTNode, Double> NeareNodeSet = new HashMap<RRTNode, Double>();
        double temp_dist;
        int total_node_num = G.getNodeCount();
        for (int i = 0; i < total_node_num; i++) {
            temp_node = G.getNode(i);
            temp_dist = DistanceUtil.distanceBetween(node_coordinate, temp_node.getCoordinate());
            if (temp_dist < radius) {
                NeareNodeSet.put(temp_node, temp_dist);
            }
        }
        MapSortUtil<RRTNode> map_sort_util = new MapSortUtil<RRTNode>();
        return map_sort_util.sortMap(NeareNodeSet);
    }

    /**
     * find the nearest vertex in RRTTree
     *
     * @param goal_coordinate
     * @param G
     * @return
     */
    protected RRTNode nearestVertex(float[] goal_coordinate, RRTTree G) {
        RRTNode temp_node;
        RRTNode nearest_node = null;

        float temp_dist;
        float min_dist = Float.MAX_VALUE;

        int total_node_num = G.getNodeCount();
        for (int i = 0; i < total_node_num; i++) {
            temp_node = G.getNode(i);
            temp_dist = distanceBetween(goal_coordinate, temp_node.getCoordinate());
            if (temp_dist < min_dist) {
                nearest_node = temp_node;
                min_dist = temp_dist;
            }
        }
        return nearest_node;
    }

    /**
     * randomly generate goal location
     *
     * @param goal_coordinate
     * @param goal_probability
     * @param width
     * @param height
     * @param obstacles
     * @param threats
     * @return
     */
    protected float[] randGoal(float[] goal_coordinate, float goal_probability, float width, float height, ArrayList<Obstacle> obstacles) {
        float probability = (float) Math.random();
        if (probability <= goal_probability) {
            return goal_coordinate;
        }
        float[] random_goal_coordinate = new float[2];
        random_goal_coordinate[0] = (float) (Math.random() * width);
        random_goal_coordinate[1] = (float) (Math.random() * height);
        boolean collisioned = true;
        while (collisioned) {
            random_goal_coordinate[0] = (float) (Math.random() * width);
            random_goal_coordinate[1] = (float) (Math.random() * height);
            if (!ConflictCheckUtil.checkPointInObstacles(obstacles, random_goal_coordinate[0], random_goal_coordinate[1])) {
                collisioned = false;
            }
        }
        return random_goal_coordinate;
    }

    /**
     * extend toward goal location
     *
     * @param nearest_node
     * @param random_goal_coordinate
     * @param max_length
     * @param max_angle
     * @return
     */
    protected RRTNode extendTowardGoalWithoutDynamics(RRTNode nearest_node, float[] random_goal_coordinate, float max_length, double max_angle) {
        float[] nearest_coordinate = nearest_node.getCoordinate();
        double toward_goal_angle = VectorUtil.getAngleOfTwoVector(random_goal_coordinate, nearest_coordinate);


        float[] new_node_coord = new float[2];

        new_node_coord[0] = nearest_coordinate[0] + (float) Math.cos(toward_goal_angle) * max_length;
        new_node_coord[1] = nearest_coordinate[1] + (float) Math.sin(toward_goal_angle) * max_length;

        RRTNode new_node = new RRTNode(new_node_coord[0], new_node_coord[1]);

//        boolean conflicted = ConflictCheckUtil.checkPointInObstaclesAndThreats(obstacles, threats, new_node_coord[0], new_node_coord[1]);
//        if (conflicted) {
//            return null;
//        }
        return new_node;
    }

    /**
     * extend toward goal location
     *
     * @param nearest_node
     * @param random_goal_coordinate
     * @param max_length
     * @param max_angle
     * @return
     */
    protected RRTNode extendTowardGoalWithDynamics(RRTNode nearest_node, float[] random_goal_coordinate, float max_length, double max_angle) {
        double current_angle = nearest_node.getCurrent_angle();
        float[] nearest_coordinate = nearest_node.getCoordinate();
        double toward_goal_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(random_goal_coordinate[0] - nearest_coordinate[0], random_goal_coordinate[1] - nearest_coordinate[1]);
        double delta_angle = VectorUtil.getBetweenAngle(toward_goal_angle, current_angle);
        float[] new_node_coord = new float[2];
        float len=max_length;
        if (delta_angle > max_angle) {
            double temp_goal_angle1 = VectorUtil.getNormalAngle(current_angle - max_angle);
            double delta_angle_1 = VectorUtil.getBetweenAngle(toward_goal_angle, temp_goal_angle1);

            double temp_goal_angle2 = VectorUtil.getNormalAngle(current_angle + max_angle);
            double delta_angle_2 = VectorUtil.getBetweenAngle(toward_goal_angle, temp_goal_angle2);

            if (delta_angle_1 < delta_angle_2) {
                toward_goal_angle = temp_goal_angle1;
            } else {
                toward_goal_angle = temp_goal_angle2;
            }
            len=Math.min(len, DistanceUtil.distanceBetween(nearest_coordinate, random_goal_coordinate));
        }
        new_node_coord[0] = nearest_coordinate[0] + (float) (Math.cos(toward_goal_angle) * max_length);
        new_node_coord[1] = nearest_coordinate[1] + (float) (Math.sin(toward_goal_angle) * max_length);

//        boolean conflicted = ConflictCheckUtil.checkPointInObstaclesAndThreats(obstacles, threats, new_node_coord[0], new_node_coord[1]);
//        if (conflicted) {
//            return null;
//        }
        RRTNode new_node = new RRTNode(new_node_coord[0], new_node_coord[1]);
        return new_node;
    }

    public void setGoal_coordinate(float[] goal_coordinate) {
        this.goal_coordinate = goal_coordinate;
    }

    public void setInit_coordinate(float[] init_coordinate) {
        this.init_coordinate = init_coordinate;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public float getMax_delta_distance() {
        return max_delta_distance;
    }

    public void setMax_delta_distance(float max_delta_distance) {
        this.max_delta_distance = max_delta_distance;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

}
