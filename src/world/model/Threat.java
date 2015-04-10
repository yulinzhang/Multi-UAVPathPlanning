/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import algorithm.RRT.RRTAlg;
import algorithm.RRT.RRTTree;
import config.StaticInitConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ConflictCheckUtil;
import world.Message;
import world.World;
import world.model.shape.Point;
import world.uav.UAVPath;

/**
 *
 * @author boluo
 */
public class Threat extends Target implements Serializable {

    protected int target_type = 0;
    protected float threat_range = 0;
    protected String threat_cap = "";
    protected UAVPath path_planned_at_current_time_step;

    private RRTAlg rrt_alg;
    private RRTTree rrt_tree;
    private float current_angle = 0;
    private float[] goal;
    private int current_index_of_planned_path = 0;
    private World world;
    

    public Threat(int index, float[] coordinates, int target_type, float speed) {
        super(index, coordinates);
        this.target_type = target_type;
        this.msg_type = Message.THREAT_MSG;
        this.speed = speed;
        this.path_planned_at_current_time_step = new UAVPath();
        rrt_alg = new RRTAlg(coordinates, null, StaticInitConfig.rrt_goal_toward_probability, World.bound_width, World.bound_height, StaticInitConfig.rrt_iteration_times, speed, null, null, -1);
    }

    public void pathPlan() {
        dummyPathPlan();
    }

    private void dummyPathPlan() {
    }

    private void runRRT() {
        rrt_alg.setGoal_coordinate(goal);
        rrt_alg.setInit_coordinate(coordinates);
        rrt_tree = rrt_alg.buildRRT(coordinates, current_angle);
        this.setPath_planned_at_current_time_step(rrt_tree.getPath_found());
        this.resetCurrentIndexOfPath();
    }

    public void resetCurrentIndexOfPath() {
        this.current_index_of_planned_path = -1;
    }

    public boolean moveToNextWaypoint() {
        if (this.speed == 0) {
            return true;
        }
//        }else{
//            for(float delta=0;delta<this.speed;delta++)
//            {
//                if(ConflictCheckUtil.checkNodeInObstacles(, null))
//            }
//        }
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

    public void setGoal(float[] goal) {
        this.goal = goal;
    }

    public void setThreat_cap(String threat_cap) {
        this.threat_cap = threat_cap;
    }

    public UAVPath getPath_planned_at_current_time_step() {
        return path_planned_at_current_time_step;
    }

    public void setPath_planned_at_current_time_step(UAVPath path_planned_at_current_time_step) {
        this.path_planned_at_current_time_step = path_planned_at_current_time_step;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Threat) {
            Threat threat = (Threat) obj;
            if (this.index==threat.getIndex()&&this.coordinates[0] == threat.coordinates[0] && this.coordinates[1] == threat.coordinates[1]) {
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
