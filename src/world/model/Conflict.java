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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import world.Message;
import world.model.shape.Point;

/**
 *
 * @author Yulin_Zhang
 */
public class Conflict extends Message implements Serializable {

    private int uav_index;
    private float conflict_range;
    private LinkedList<Point> path_prefound; //waypoints have been generated
    private int decision_time_step;

    public Conflict(int uav_index, LinkedList<Point> path_prefound, int decision_time_step, float conflict_range) {
        this.uav_index = uav_index;
        this.path_prefound = path_prefound;
        this.decision_time_step = decision_time_step;
        this.msg_type = Message.CONFLICT_MSG;
        this.conflict_range = conflict_range;
    }

    public void sort() {
        int path_len = path_prefound.size();
        for (int i = 0; i < path_len; i++) {
            Point point1 = path_prefound.get(i);
            for (int j = i + 1; j < path_len; j++) {
                Point point2 = path_prefound.get(j);
                if (point1.getExptected_time_step() >= point2.getExptected_time_step()) {
                    path_prefound.set(i, point2);
                    path_prefound.set(j, point1);
                }
            }
        }
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

    public float getConflict_range() {
        return conflict_range;
    }

    public void setConflict_range(float conflict_range) {
        this.conflict_range = conflict_range;
    }

    @Override
    public String toString() {
        return StaticInitConfig.CONFLICT_NAME + this.uav_index;
    }

    @Override
    public int getMsgSize() {
        return this.path_prefound.size();
    }
    
        @Override
    public boolean equals(Object obj)
    {
        
        return false;
    }
    
    public Object deepClone(){
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
