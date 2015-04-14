/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.uav;

import config.GraphicConfig;
import java.awt.Color;
import java.awt.Rectangle;
import util.RectangleUtil;
import util.VectorUtil;
import world.model.Target;
import world.model.shape.Circle;

/**
 *
 * @author boluo
 */
public class UAV extends Unit {

    protected Color center_color;
    protected Color radar_color; //the radar color in world
    protected Circle uav_radar;
    protected float current_angle = 0;
    protected float max_angle = (float) Math.PI / 4;
    protected int speed = 5;
    protected boolean visible=true;

    public UAV(int index, Target target_indicated_by_role, int uav_type, float[] center_coordinates) {
        super(index, target_indicated_by_role, uav_type, center_coordinates);
    }

    /**
     *
     * @param center_coordinate_x
     * @param center_coordinate_y
     */
    public void moveTo(float center_coordinate_x, float center_coordinate_y) {
        uav_center.setCoordinate(center_coordinate_x, center_coordinate_y);
        uav_radar.setCoordinate(center_coordinate_x, center_coordinate_y);
        this.setCenter_coordinates(uav_radar.getCenter_coordinates());
    }

    protected float[] extendTowardGoalWithDynamics(float[] current_coordinate, float current_angle, float[] random_goal_coordinate, float max_length, float max_angle) {
        float toward_goal_angle = VectorUtil.getAngleOfVectorRelativeToXCoordinate(random_goal_coordinate[0] - current_coordinate[0], random_goal_coordinate[1] - current_coordinate[1]);
        float delta_angle = VectorUtil.getBetweenAngle(toward_goal_angle, current_angle);
        float[] new_node_coord = new float[2];
        if (delta_angle > max_angle) {
            float temp_goal_angle1 = VectorUtil.getNormalAngle(current_angle - max_angle);
            float delta_angle_1 = VectorUtil.getBetweenAngle(toward_goal_angle, temp_goal_angle1);

            float temp_goal_angle2 = VectorUtil.getNormalAngle(current_angle + max_angle);
            float delta_angle_2 = VectorUtil.getBetweenAngle(toward_goal_angle, temp_goal_angle2);

            if (delta_angle_1 < delta_angle_2) {
                toward_goal_angle = temp_goal_angle1;
            } else {
                toward_goal_angle = temp_goal_angle2;
            }
        }
        new_node_coord[0] = current_coordinate[0] + (float) (Math.cos(toward_goal_angle) * max_length);
        new_node_coord[1] = current_coordinate[1] + (float) (Math.sin(toward_goal_angle) * max_length);
        return new_node_coord;
    }

    public boolean isObstacleInTargetMBR(Rectangle obs_mbr)
    {
        float[] target_coord=this.getTarget_indicated_by_role().getCoordinates();
        Rectangle rect = RectangleUtil.findMBRRect(this.center_coordinates, target_coord);
        if(rect.intersects(obs_mbr))
        {
            return true;
        }else{
            return false;
        }
    }
    
    public Circle getUav_radar() {
        return uav_radar;
    }

    public void setUav_radar(Circle uav_radar) {
        this.uav_radar = uav_radar;
    }

    public void initColor(int uav_index) {
        center_color = GraphicConfig.uav_colors.get(uav_index);
        radar_color = new Color(center_color.getRed(), center_color.getGreen(), center_color.getBlue(), 128);
    }

    public Color getCenter_color() {
        return center_color;
    }

    public Color getRadar_color() {
        return radar_color;
    }

    public float getCurrent_angle() {
        return current_angle;
    }

    public void setCurrent_angle(float current_angle) {
        this.current_angle = current_angle;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    
}
