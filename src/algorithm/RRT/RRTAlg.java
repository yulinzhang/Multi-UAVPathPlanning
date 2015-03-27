/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.RRT;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import util.BoundUtil;
import util.ConflictCheckUtil;
import util.DistanceUtil;
import static util.DistanceUtil.distanceBetween;
import util.MapSortUtil;
import util.VectorUtil;
import world.model.Obstacle;
import world.model.Threat;

/**
 *
 * @author boluo
 */
public class RRTAlg {

    /**
     * external variables
     *
     */
    private Vector<Obstacle> obstacles;
    private Vector<Threat> threats;
    private int bound_width = 800;
    private int bound_height = 600;
    private float[] init_coordinate;
    private float[] goal_coordinate;
    private float current_angle;
    private float goal_probability = 0.6f;

    /**
     * internal variables
     *
     */
    private int k_step = 20;
    private float max_delta_distance = 5;
    private float max_angle = (float) Math.PI / 12;
    /**
     * less than goal_range_for_delta means goal reached
     *
     */
    private float goal_range_for_delta = max_delta_distance;

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
    public RRTAlg(float[] init_coordinate, float[] goal_coordinate, float goal_probability, int bound_width, int bound_height, int k_step, float max_delta_distance, Vector<Obstacle> obstacles, Vector<Threat> threats) {
        this.init_coordinate = init_coordinate;
        this.k_step = k_step;
        this.max_delta_distance = max_delta_distance;
        this.obstacles = obstacles;
        this.threats = threats;
        this.bound_height = bound_height;
        this.bound_width = bound_width;
        this.goal_probability = goal_probability;
        this.goal_coordinate = goal_coordinate;
    }

    public RRTTree buildRRT(float[] init_coordinate, float current_angle) {
        this.setInit_coordinate(init_coordinate);
        RRTTree G = new RRTTree();
        RRTNode start_node = new RRTNode(init_coordinate[0], init_coordinate[1]);
        start_node.setCurrent_angle(current_angle);
        G.addNode(start_node, null);

        float[] random_goal;
        RRTNode nearest_node;
        RRTNode new_node = null;

        for (int time_step = 1; time_step <= k_step;) {
            //random choose a direction or goal
            random_goal = randGoal(this.goal_coordinate, goal_probability, bound_width, bound_height, obstacles, threats);
            //choose the nearest node to extend
            nearest_node = nearestVertex(random_goal, G);
            //extend the child node and validate its confliction 
            new_node = extendTowardGoalV2(nearest_node, random_goal, this.max_delta_distance, max_angle);

            boolean conflicted = ConflictCheckUtil.checkNodeInObstaclesAndThreats(obstacles, threats, new_node);
            boolean within_bound = BoundUtil.withinBound(new_node, bound_width, bound_height);
            //if not conflicted,add the child to the tree
            if (!conflicted && true) {
                G.addNode(new_node, nearest_node);
                if (DistanceUtil.distanceBetween(new_node.getCoordinate(), goal_coordinate) < goal_range_for_delta) {
                    G.generatePath(new_node);
                    return G;
                }
                time_step++;
            }
        }
        G.generatePath(new_node);
        return G;
    }

    public RRTTree buildRRTStar2(float[] init_coordinate, float current_angle) {
        this.setInit_coordinate(init_coordinate);
        RRTTree G = new RRTTree();
        RRTNode start_node = new RRTNode(init_coordinate[0], init_coordinate[1]);
        start_node.setCurrent_angle(current_angle);
        G.addNode(start_node, null);

        float[] random_goal;
        Vector<RRTNode> near_node_set;
        RRTNode nearest_node;
        RRTNode new_node = null;
        double radius =9999;

        for (int time_step = 1; time_step <= k_step;) {
            //random choose a direction or goal
            random_goal = randGoal(this.goal_coordinate, goal_probability, bound_width, bound_height, obstacles, threats);
            //choose the nearest node to extend
            near_node_set = neareSortedNodesToNode(G, random_goal, radius);

            for (int i = 0; i < near_node_set.size(); i++) {
                nearest_node = near_node_set.get(i);
                new_node = extendTowardGoalV2(nearest_node, random_goal, this.max_delta_distance, max_angle);
                if (new_node == null) {
                    continue;
                }
                G.addNode(new_node, nearest_node);
                
                nearest_node=new_node;
                
                while(DistanceUtil.distanceBetween(new_node.getCoordinate(), random_goal)>this.max_delta_distance)
                {
                    new_node = extendTowardGoalV2(nearest_node, random_goal, this.max_delta_distance, max_angle);
                    
                }
            }
        }
        return null;
    }

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
            random_goal = randGoal(this.goal_coordinate, goal_probability, bound_width, bound_height, obstacles, threats);
            //choose the nearest node to extend
            nearest_node = nearestVertex(random_goal, G);
            //extend the child node and validate its confliction
            new_node = extendTowardGoalV2(nearest_node, random_goal, this.max_delta_distance, max_angle);

            boolean conflicted = ConflictCheckUtil.checkNodeInObstaclesAndThreats(obstacles, threats, new_node);
            boolean within_bound = BoundUtil.withinBound(new_node, bound_width, bound_height);
            //if not conflicted,add the child to the tree
            if (!conflicted && true) {
//                G.addNode(new_node, nearest_node);
                if (distanceBetween(new_node.getCoordinate(), goal_coordinate) < goal_range_for_delta) {
                    G.addNode(new_node, nearest_node);
                    G.generatePath(new_node);
                    return G;
                }
                int max_num = 200;//(int) (2 * Math.E * Math.log(G.getNodeCount()));
                Vector<RRTNode> nearestNodesToNewNodeSet = neareSortedNodesToNode(G, new_node.getCoordinate(), radius);
                if (nearestNodesToNewNodeSet.size() > max_num) {
                    radius = DistanceUtil.distanceBetween(new_node.getCoordinate(), nearestNodesToNewNodeSet.get(max_num).getCoordinate());
                }
                rewireRRTStar(G, nearestNodesToNewNodeSet, new_node, nearest_node);
                time_step++;
            }
        }
        G.generatePath(new_node);
        return G;
    }

    private void rewireRRTStar(RRTTree G, Vector<RRTNode> nearestNodesToNewNodeSet, RRTNode new_node, RRTNode nearest_node) {
        float[] new_node_coordinate = new_node.getCoordinate();
        float min_path_length = nearest_node.getPath_lenght_from_root() + DistanceUtil.distanceBetween(new_node_coordinate, nearest_node.getCoordinate());
        RRTNode min_parent_node = nearest_node;
        float temp_path_length;
        for (RRTNode nearNode : nearestNodesToNewNodeSet) {
            temp_path_length = nearNode.getPath_lenght_from_root() + DistanceUtil.distanceBetween(new_node_coordinate, nearNode.getCoordinate());
            if (temp_path_length < min_path_length) {
                min_parent_node = nearNode;
                min_path_length = temp_path_length;
            }
        }
        G.addNode(new_node, min_parent_node);
        for (RRTNode nearNode : nearestNodesToNewNodeSet) {
            temp_path_length = new_node.getPath_lenght_from_root() + DistanceUtil.distanceBetween(new_node_coordinate, nearNode.getCoordinate());
            if (temp_path_length < nearNode.getPath_lenght_from_root()) {
                G.changeParent(nearNode, new_node);
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
    private Vector<RRTNode> neareSortedNodesToNode(RRTTree G, float[] node_coordinate, double radius) {
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
    private RRTNode nearestVertex(float[] goal_coordinate, RRTTree G) {
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
    private float[] randGoal(float[] goal_coordinate, float goal_probability, float width, float height, Vector<Obstacle> obstacles, Vector<Threat> threats) {
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
            if (!ConflictCheckUtil.checkPointInObstaclesAndThreats(obstacles, threats, random_goal_coordinate[0], random_goal_coordinate[1])) {
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
    private RRTNode extendTowardGoal(RRTNode nearest_node, float[] random_goal_coordinate, float max_length, float max_angle) {
        float current_angle = nearest_node.getCurrent_angle();
        float[] nearest_coordinate = nearest_node.getCoordinate();
        float toward_goal_angle = VectorUtil.getAngleOfTwoVector(random_goal_coordinate, nearest_coordinate);
        float delta_angle = toward_goal_angle - current_angle;

        float total_dist_from_nearest_to_goal = DistanceUtil.distanceBetween(nearest_coordinate, random_goal_coordinate);

        float[] new_node_coord = new float[2];

        new_node_coord[0] = nearest_coordinate[0] + (float) Math.cos(toward_goal_angle) * max_length;
        new_node_coord[1] = nearest_coordinate[1] + (float) Math.sin(toward_goal_angle) * max_length;
        /**
         * max_length * (random_goal_coordinate[0] - nearest_coordinate[0]) /
         * total_dist_from_nearest_to_goal + nearest_coordinate[0];
         * new_node_coord[1] = max_length * (random_goal_coordinate[1] -
         * nearest_coordinate[1]) / total_dist_from_nearest_to_goal +
         * nearest_coordinate[1];*
         */
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
    private RRTNode extendTowardGoalV2(RRTNode nearest_node, float[] random_goal_coordinate, float max_length, float max_angle) {
        float current_angle = nearest_node.getCurrent_angle();
        float[] nearest_coordinate = nearest_node.getCoordinate();
        float toward_goal_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(random_goal_coordinate[0] - nearest_coordinate[0], random_goal_coordinate[1] - nearest_coordinate[1]);
        float delta_angle = VectorUtil.getBetweenAngle(toward_goal_angle, current_angle);
        float[] new_node_coord = new float[2];
        if (delta_angle > max_angle) {
            float temp_goal_angle1 = VectorUtil.getNormalAngle(current_angle - max_angle);
            float delta_angle_1 = VectorUtil.getBetweenAngle(toward_goal_angle, temp_goal_angle1);

            float temp_goal_angle2 = VectorUtil.getNormalAngle(current_angle + max_angle);
            float delta_angle_2 = VectorUtil.getBetweenAngle(toward_goal_angle, temp_goal_angle2);

//            if (VectorUtil.getNormalAngle(Math.abs(toward_goal_angle - temp_goal_angle1)) < VectorUtil.getNormalAngle(Math.abs(toward_goal_angle - temp_goal_angle2))) {
            if (delta_angle_1 < delta_angle_2) {
                toward_goal_angle = temp_goal_angle1;
            } else {
                toward_goal_angle = temp_goal_angle2;
            }
        }
        new_node_coord[0] = nearest_coordinate[0] + (float) Math.cos(toward_goal_angle) * max_length;
        new_node_coord[1] = nearest_coordinate[1] + (float) Math.sin(toward_goal_angle) * max_length;

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

}
