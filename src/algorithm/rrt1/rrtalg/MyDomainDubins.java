package algorithm.rrt1.rrtalg;

import algorithm.rrt1.domain.Domain;
import algorithm.rrt1.util.Extension;
import algorithm.rrt1.util.ExtensionEstimate;
import algorithm.rrt1.util.Vertex;
import config.StaticInitConfig;
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
public class MyDomainDubins implements Domain<Point, DubinsCurve> {

    private ArrayList<Obstacle> obstacles;
    protected Point goal;
    protected double tryGoalRatio;
    protected Random random;
    protected float speed=1;
    protected double max_angle=Math.PI/12;

    /**
     * 
     * @param obstacles
     * @param goal
     * @param tryGoalRatio 
     */
    public MyDomainDubins(ArrayList<Obstacle> obstacles, Point goal, double tryGoalRatio) {
        this.obstacles = obstacles;
        this.goal = goal;
        this.tryGoalRatio = tryGoalRatio;
        this.random = new Random();
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
    public Extension<Point,DubinsCurve> extendTo(Vertex<Point, DubinsCurve> from_vertext, Point to) {
        Point from=from_vertext.getState();
        DubinsCurve min_dc=null;
        double min_len = Float.MAX_VALUE;
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 12) {
            to.setYaw(angle);
            DubinsCurve dc = new DubinsCurve(from, to, 20, false, speed, 1);
            if (dc.getLength() < min_len) {
                min_dc=dc;
                min_len=dc.getLength();
            }
        }
//        double angle=VectorUtil.getAngleOfVectorRelativeToXCoordinate((float)(to.getX()-from.getX()), (float)(to.getY()-from.getY()));
//        to.setYaw(angle);
        final Trajectory traj = min_dc.getTraj();
        if(traj==null)
        {
            return null;
        }
        Point[] points=traj.getPoints();
        for(Point point:points)
        {
            if( ConflictCheckUtil.checkPointInObstacles(obstacles, (float)point.getX(),(float) point.getY()))
            {
                return null;
            }
        }
        Extension<Point, DubinsCurve> extension=new Extension<Point, DubinsCurve>(from,to,min_dc,min_dc.getLength(),min_dc.isExact());//S source, S target, E edge, double cost, boolean exact
        return extension;
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
    protected Point extendTowardGoalWithDynamics(float[] nearest_coordinate, double current_angle, float[] random_goal_coordinate, float max_length, double max_angle) {
        double toward_goal_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(random_goal_coordinate[0] - nearest_coordinate[0], random_goal_coordinate[1] - nearest_coordinate[1]);
        double delta_angle = VectorUtil.getBetweenAngle(toward_goal_angle, current_angle);
        float[] new_node_coord = new float[2];
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
        }
        new_node_coord[0] = nearest_coordinate[0] + (float) (Math.cos(toward_goal_angle) * max_length);
        new_node_coord[1] = nearest_coordinate[1] + (float) (Math.sin(toward_goal_angle) * max_length);

        boolean conflicted = ConflictCheckUtil.checkPointInObstacles(obstacles, new_node_coord[0], new_node_coord[1]);
        if (conflicted) {
            return null;
        }
        Point new_node = new Point(new_node_coord[0], new_node_coord[1],toward_goal_angle);
        return new_node;
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
        if (DistanceUtil.distanceBetween(s.toFloatArray(), goal.toFloatArray())<100) {
            return true;
        } else {
            return false;
        }
    }

}
