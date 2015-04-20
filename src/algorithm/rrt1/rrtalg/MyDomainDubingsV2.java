package algorithm.rrt1.rrtalg;

import algorithm.rrt1.domain.Domain;
import algorithm.rrt1.util.Extension;
import algorithm.rrt1.util.ExtensionEstimate;
import algorithm.rrt1.util.Vertex;
import java.util.ArrayList;
import java.util.Random;
import util.ConflictCheckUtil;
import util.DistanceUtil;
import util.VectorUtil;
import world.World;
import world.model.Obstacle;
import world.model.shape.DubinsCurve;
import world.model.shape.Point;
import world.model.shape.Trajectory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author boluo
 */
public class MyDomainDubingsV2 implements Domain<Point, DubinsCurve> {

    private ArrayList<Obstacle> obstacles;
    protected Point goal;
    protected double tryGoalRatio;
    protected Random random;
    protected float speed = 10;
    protected double max_angle = Math.PI / 12;
    private float adaptive_target_reached_radius_check = 5;
    private Point initialState;

    /**
     *
     * @param obstacles
     * @param goal
     * @param tryGoalRatio
     */
    public MyDomainDubingsV2(ArrayList<Obstacle> obstacles, Point initialState, Point goal, double tryGoalRatio) {
        this.obstacles = obstacles;
        this.goal = goal;
        this.tryGoalRatio = tryGoalRatio;
        this.random = new Random(System.currentTimeMillis());
        this.initialState = initialState;
        adaptive_target_reached_radius_check = Math.min(adaptive_target_reached_radius_check, DistanceUtil.distanceBetween(initialState.toFloatArray(), goal.toFloatArray()) / 2);
    }

    @Override
    public Point sampleState() {
        if (random.nextDouble() <= tryGoalRatio) {
            return goal;
        }
        float[] random_goal_coordinate = new float[2];
        random_goal_coordinate[0] = (float) (Math.random() * World.bound_width);
        random_goal_coordinate[1] = (float) (Math.random() * World.bound_height);
        boolean collisioned = true;
        while (collisioned) {
            random_goal_coordinate[0] = (float) (Math.random() * World.bound_width);
            random_goal_coordinate[1] = (float) (Math.random() * World.bound_height);
            if (!ConflictCheckUtil.checkPointInObstacles(obstacles, random_goal_coordinate[0], random_goal_coordinate[1])) {
                collisioned = false;
            }
        }
        Point point = new Point(random_goal_coordinate[0], random_goal_coordinate[1], 0);
        return point;
    }

    @Override
    public Extension<Point, DubinsCurve> extendTo(Vertex<Point, DubinsCurve> from_vertex, Point to) {
        Point from = from_vertex.getState();
        DubinsCurve min_dc = null;
        double min_len = Float.MAX_VALUE;
        Vertex<Point, DubinsCurve> parent_of_from = from_vertex.getParent();
        double from_angle = 0;
        if (parent_of_from != null) {
            Point parent_of_from_point = parent_of_from.getState();
            from_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(from.getX() - parent_of_from_point.getX(), from.getY() - parent_of_from_point.getY());
        }

        to.setYaw(from_angle + Math.PI);
        DubinsCurve dc = new DubinsCurve(from, to, 20, false, speed, 1);
        if (!ConflictCheckUtil.checkTrajectoryInObstacles(obstacles, dc.getTraj()) && dc.getLength() < min_len) {
            min_dc = dc;
            min_len = dc.getLength();
        }
        if (min_dc == null) {
            return null;
        }
        Extension<Point, DubinsCurve> extension = new Extension<Point, DubinsCurve>(from, to, min_dc, min_dc.getLength(), min_dc.isExact());//S source, S target, E edge, double cost, boolean exact
        return extension;
    }

    @Override
    public ExtensionEstimate estimateExtension(Point from, Point to) {
        return new ExtensionEstimate(DistanceUtil.distanceBetween(from.toFloatArray(), to.toFloatArray()), true);
    }

    @Override
    /**
     * estimate cost to go to target
     *
     */
    public double estimateCostToGo(Point s) {
        return DistanceUtil.distanceBetween(s.toFloatArray(), goal.toFloatArray());
    }

    @Override
    public double distance(Point s1, Point s2) {
        return DistanceUtil.distanceBetween(s1.toFloatArray(), s2.toFloatArray());
    }

    @Override
    public double nDimensions() {
        return 2;
    }

    @Override
    public boolean isInTargetRegion(Point s) {

        if (DistanceUtil.distanceBetween(s.toFloatArray(), goal.toFloatArray()) < 1) {
            if (adaptive_target_reached_radius_check > 2) {
                adaptive_target_reached_radius_check = adaptive_target_reached_radius_check / 2;
            }
            return true;
        } else {
            return false;
        }
    }

}
