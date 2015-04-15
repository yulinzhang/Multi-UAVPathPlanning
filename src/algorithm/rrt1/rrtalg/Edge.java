package algorithm.rrt1.rrtalg;

import java.util.ArrayList;
import java.util.List;
import util.DistanceUtil;
import util.VectorUtil;
import world.model.shape.Point;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author boluo
 */
public class Edge {

    private Point source;
    private Point end;
    private float cost;

    public Edge(Point source, Point target) {
        this.source = source;
        this.end = target;
        this.cost = DistanceUtil.distanceBetween(source.toFloatArray(), end.toFloatArray());
    }

    public List<Point> getWaypoints(float speed) {
        List<Point> waypoints = new ArrayList<Point>();
        if (this.cost < speed) {
            waypoints.add(end);
            return waypoints;
        }
        double from_source_to_end_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(end.getX() - source.getX(), end.getY() - source.getY());
        float total_len = speed;
        double source_coord_x = source.getX();
        double source_coord_y = source.getY();
        while (total_len <= this.cost) {
            double cood_x = source_coord_x + total_len * Math.cos(from_source_to_end_angle);
            double cood_y = source_coord_y + total_len * Math.sin(from_source_to_end_angle);
            Point point=new Point();
            point.setX(cood_x);
            point.setY(cood_y);
            waypoints.add(point);
            total_len+=speed;
        }
        if(total_len-speed<this.cost)
        {
            waypoints.add(end);
        }
        return waypoints;
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

}
