/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import java.util.LinkedList;
import world.Message;
import world.model.shape.Point;

/**
 *
 * @author boluo
 */
public class Conflict extends Message{

    private int uav_index;
    private LinkedList<Point> path_prefound;
    private int decision_time_step;
    
    public Conflict(int uav_index,LinkedList<Point> path_prefound,int decision_time_step)
    {
        this.uav_index=uav_index;
        this.path_prefound=path_prefound;
        this.decision_time_step=decision_time_step;
        this.msg_type=Message.CONFLICT_MSG;
    }
    public int getUav_index() {
        return uav_index;
    }

    public void setUav_index(int uav_index) {
        this.uav_index = uav_index;
    }

    public LinkedList<Point> getPath_prefound() {
        return path_prefound;
    }

    public void setPath_prefound(LinkedList<Point> path_prefound) {
        this.path_prefound = path_prefound;
    }

    public int getDecision_time_step() {
        return decision_time_step;
    }

    public void setDecision_time_step(int decision_time_step) {
        this.decision_time_step = decision_time_step;
    }
    
    
    @Override
    public String toString()
    {
        return StaticInitConfig.CONFLICT_NAME+this.uav_index;
    }

    @Override
    public int getMsgSize() {
        return this.path_prefound.size();
    }
    
}
