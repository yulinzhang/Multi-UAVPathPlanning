/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    protected Target target_indicated_by_role=null;
    //which side the unit belongs to 
    protected int flag_of_war;
    protected float[] center_coordinates = new float[2];
    /**
     * internal variables
     *
     */
    protected Triangle uav_center;

    public static int scout_radar_radius = 30;
    public static int center_height = 10;
    public static int center_width = 8;

    public Unit(int index, Target target_indicated_by_role, int flag_of_war, float[] center_coordinates) {
        this.index = index;
        this.target_indicated_by_role = target_indicated_by_role;
        this.flag_of_war=flag_of_war;
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

    public Target getRole_target() {
        return target_indicated_by_role;
    }

    public void setRole_target(Threat role_target) {
        this.target_indicated_by_role = role_target;
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



}
