/* 
 * Copyright (c) Yulin Zhang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package world.uav;

import config.StaticInitConfig;
import java.io.Serializable;
import java.util.LinkedList;
import util.DistanceUtil;
import world.model.shape.Point;

/** This is the data structure to maintain the path for the uav.
 *
 * @author Yulin_Zhang
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

    /**
     * add waypoint to end and undate path length
     * 
     * @param point 
     */
    public void addWaypointToEnd(Point point) {
        if (this.waypoints.size() > 0) {
            float length = DistanceUtil.distanceBetween(point.toFloatArray(), this.waypoints.getLast().toFloatArray());
            this.waypoints.addLast(point);
            this.path_length += length;
        } else {
            this.waypoints.add(point); //this point is origin
        }
    }

    /**
     * add waypoint to beginning and undate path length
     * 
     * @param point 
     */
    public void addWaypointToBeginning(Point point) {
        if (this.waypoints.size() > 0) {
            float length = DistanceUtil.distanceBetween(point.toFloatArray(), this.waypoints.getFirst().toFloatArray());
            this.waypoints.addFirst(point);
            this.path_length += length;
        } else {
            this.waypoints.add(point); //this point is origin
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
    
    /**
     * Determining whether UAC has reached the target point
     * 
     * @param target_coord
     * @return 
     */
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
