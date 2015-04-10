/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.uav;

import config.StaticInitConfig;
import java.io.Serializable;
import java.util.LinkedList;
import util.DistanceUtil;
import world.model.shape.Point;

/**
 *
 * @author boluo
 */
public class UAVPath implements Serializable{

    private LinkedList<Point> waypoints;
    private float path_length = 0;

    public UAVPath() {
        this.waypoints = new LinkedList<Point>();
    }

    public Point getWaypoint(int index) {
        return waypoints.get(index);
    }

    public int getWaypointNum() {
        return this.waypoints.size();
    }

    public void addWaypointToEnd(Point point) {
        if (this.waypoints.size() > 0) {
            float length = DistanceUtil.distanceBetween(point.toFloatArray(), this.waypoints.getLast().toFloatArray());
            this.waypoints.addLast(point);
            this.path_length += length;
        } else {
            this.waypoints.add(point);
        }
    }

    public void addWaypointToBeginning(Point point) {
        if (this.waypoints.size() > 0) {
            float length = DistanceUtil.distanceBetween(point.toFloatArray(), this.waypoints.getFirst().toFloatArray());
            this.waypoints.addFirst(point);
            this.path_length += length;
        } else {
            this.waypoints.add(point);
        }
    }

    public Point getLastWaypoint() {
        return this.waypoints.getLast();
    }

    public LinkedList<Point> getWaypointsAsLinkedList() {
        return waypoints;
    }

    public void setWaypoints(LinkedList<Point> planned_path) {
        this.waypoints = planned_path;
    }

    public float getPath_length() {
        return path_length;
    }

    public void setPath_length(float path_length) {
        this.path_length = path_length;
    }
    
    public boolean pathReachEndPoint(float[] target_coord)
    {
        float dist_to_target=DistanceUtil.distanceBetween(this.waypoints.getLast().toFloatArray(), target_coord);
        if(dist_to_target<StaticInitConfig.SAFE_DISTANCE_FOR_TARGET)
        {
            return true;
        }else{
            return false;
        }
    }
}
