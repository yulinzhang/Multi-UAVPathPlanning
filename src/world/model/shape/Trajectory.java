/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model.shape;

/**
 *
 * @author boluo
 */
public class Trajectory {

    Point[] points;
    int samplingInterval;
    double cost;

    /**
     * 
     * @param points
     * @param samplingInterval is the time spent for each segment in this trajectory
     * @param cost 
     */
    public Trajectory(Point[] points, int samplingInterval,
            double cost) {
        this.points = points;
        this.samplingInterval = samplingInterval;
        this.cost = cost;
    }
    public double getCost() {
        return cost;
    }

    public int getMinTime() {
        return 0;
    }

    public int getMaxTime() {
        return (points.length - 1) * samplingInterval;
    }

    public Point get(int t) {
        int i = t / samplingInterval;
        return new Point(points[i].x, points[i].y,points[i].z);
    }

    public Point getEndPoint()
    {
        int i=points.length-1;
        return new Point(points[i].x,points[i].y,points[i].z);
    }
    
    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point[] points) {
        this.points = points;
    }
    
    
}
