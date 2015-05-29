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
package world.model;

import config.StaticInitConfig;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import world.Message;
import world.model.shape.Point;
import world.uav.UAVPath;

/**
 *
 * @author Yulin_Zhang
 */
public class Threat extends Target implements Serializable {

    protected int target_type = 0;
    protected float threat_range = 0;
    protected String threat_cap = "";
    protected UAVPath path_planned_at_current_time_step;
    private float current_angle = 0;
    private float[] goal;
    private int current_index_of_planned_path = 0;
    private Rectangle threat_mbr;
    
    public static int threat_width = 20;
    public static int threat_height = 20;
    
    public Threat(int index, float[] coordinates, int target_type, float speed) {
        super(index, coordinates);
        this.target_type = target_type;
        this.msg_type = Message.THREAT_MSG;
        this.speed = speed;
        this.path_planned_at_current_time_step = new UAVPath();
        threat_mbr=new Rectangle((int) coordinates[0] - Threat.threat_width / 2, (int) coordinates[1] - Threat.threat_height / 2, Threat.threat_width, Threat.threat_height);
//        rrt_alg = new RRTAlg(coordinates, null, StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, null, null, -1);
    }

    public void resetCurrentIndexOfPath() {
        this.current_index_of_planned_path = -1;
    }

    public boolean moveToNextWaypoint() {
        if (this.speed == 0) {
            return true;
        }
        current_index_of_planned_path++;
        if (path_planned_at_current_time_step.getWaypointNum() == 0 || current_index_of_planned_path >= path_planned_at_current_time_step.getWaypointNum()) {
            return false;
        }
        Point current_waypoint = this.path_planned_at_current_time_step.getWaypoint(current_index_of_planned_path);
        float[] coordinate = current_waypoint.toFloatArray();
        moveTo(coordinate[0], coordinate[1]);
        this.current_angle = (float) current_waypoint.getYaw();
        return true;
    }

    /**
     *
     * @param center_coordinate_x
     * @param center_coordinate_y
     */
    public void moveTo(float center_coordinate_x, float center_coordinate_y) {
        float[] coordinate = new float[]{center_coordinate_x, center_coordinate_y};
        this.setCoordinates(coordinate);
        this.threat_mbr=new Rectangle((int) coordinates[0] - Threat.threat_width / 2, (int) coordinates[1] - Threat.threat_height / 2, Threat.threat_width, Threat.threat_height);
    }

    @Override
    public String toString() {
        return StaticInitConfig.THREAT_NAME + this.index;//this.coordinates[0]+","+this.coordinates[1]+this.threat_range+this.threat_cap;
    }

    public int getTarget_type() {
        return target_type;
    }

    public void setTarget_type(int target_type) {
        this.target_type = target_type;
    }

    public float getThreat_range() {
        return threat_range;
    }

    public void setThreat_range(float threat_range) {
        this.threat_range = threat_range;
    }

    public String getThreat_cap() {
        return threat_cap;
    }

    public float[] getGoal() {
        return goal;
    }

    public float getCurrent_angle() {
        return current_angle;
    }

    public void setCurrent_angle(float current_angle) {
        this.current_angle = current_angle;
    }

    public void setGoal(float[] goal) {
        this.goal = goal;
    }

    public void setThreat_cap(String threat_cap) {
        this.threat_cap = threat_cap;
    }

    public UAVPath getPath_planned_at_current_time_step() {
        return path_planned_at_current_time_step;
    }

    public Rectangle getThreat_mbr() {
        return threat_mbr;
    }

    public void setPath_planned_at_current_time_step(UAVPath path_planned_at_current_time_step) {
        this.path_planned_at_current_time_step = path_planned_at_current_time_step;
        this.resetCurrentIndexOfPath();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Threat) {
            Threat threat = (Threat) obj;
            if (this.index==threat.getIndex()) {//&&this.coordinates[0] == threat.coordinates[0] && this.coordinates[1] == threat.coordinates[1]
                return true;
            }
        }
        return false;
    }

    public Object deepClone() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (IOException ex) {
            Logger.getLogger(Target.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Target.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
