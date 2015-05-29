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

import world.model.Threat;
import world.model.shape.Triangle;
import world.model.Target;

/** 
 *
 * @author Yulin_Zhang
 */
public class Unit {

    /**
     * external variables
     *
     */
    protected int index;
    protected Target target_indicated_by_role = null;
    //which side the unit belongs to 
    protected int uav_type;
    protected float[] center_coordinates = new float[2];
    /**
     * internal variables
     *
     */
    protected Triangle uav_center;


    public static int center_height = 10;
    public static int center_width = 8;

    public Unit(int index, Target target_indicated_by_role, int uav_type, float[] center_coordinates) {
        this.index = index;
        if (target_indicated_by_role != null) {
            this.target_indicated_by_role = (Target)target_indicated_by_role.deepClone();
        }
        this.uav_type = uav_type;
        this.center_coordinates[0] = center_coordinates[0];
        this.center_coordinates[1] = center_coordinates[1];
        this.uav_center = new Triangle(center_coordinates[0], center_coordinates[1], 0, center_width, center_height);
    }

    public Triangle getUav_center() {
        return uav_center;
    }

    public void setUav_center(Triangle uav_center) {
        this.uav_center = uav_center;
    }

    public float[] getCenter_coordinates() {
        return center_coordinates;
    }

    public void setCenter_coordinates(float[] center_coordinates) {
        this.center_coordinates = center_coordinates;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Target getTarget_indicated_by_role() {
        return target_indicated_by_role;
    }

    public int getUav_type() {
        return uav_type;
    }

    public void setUav_type(int uav_type) {
        this.uav_type = uav_type;
    }

}
