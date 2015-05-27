/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.uav;

import config.StaticInitConfig;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ImageUtil;
import world.model.shape.Circle;

/**
 *
 * @author boluo
 */
public class UAVBase {

    private float[] coordinate;
    private int base_radius;
    private BufferedImage image;
    private Circle base_shape;
    private Map<Integer, float[]> uav_port_map;
    public UAVBase(float[] coordinate, int base_radius) {
        try {
            this.coordinate = coordinate;
            this.base_radius = base_radius;
            this.base_shape = new Circle(coordinate[0], coordinate[1], this.base_radius);
            image = ImageUtil.retrieveImage("/resources/radar2.jpg");
        } catch (IOException ex) {
            Logger.getLogger(UAVBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        initUAVPort();
    }

    public void initUAVPort() {
        uav_port_map = new HashMap<Integer, float[]>();

//        int num_of_uav_in_loop_1 = (int) (2 * Math.PI * 2 / 2);
//        int num_of_uav_in_loop_2 = (int) (2 * Math.PI * 4 / 2);
//        int num_of_uav_in_loop_3 = (int) (2 * Math.PI * 6 / 2);
        int total_uav_port_num = 0;
        for (int loop_index = 1; loop_index <= 3; loop_index++) {
            int current_loop_num = (int) Math.floor(Math.PI * 2 * loop_index);
            double delta_theta = Math.PI * 2 / current_loop_num;
            int port_radius = StaticInitConfig.attacker_radar_radius * loop_index;
            double theta = 0;
            for (int uav_index = total_uav_port_num; uav_index < total_uav_port_num + current_loop_num; uav_index++) {
                float[] coord = new float[2];
                coord[0] = this.coordinate[0] + (float) (port_radius * Math.cos(theta));
                coord[1] = this.coordinate[1] + (float) (port_radius * Math.sin(theta));
                uav_port_map.put(uav_index, coord);
                theta += delta_theta;
            }
            total_uav_port_num += current_loop_num;
        }
    }

    public float[] assignUAVLocation(int attacker_index) {
        return uav_port_map.get(attacker_index);
    }

    public float[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(float[] coordinate) {
        this.coordinate = coordinate;
    }

//    public int getBase_width() {
//        return base_width;
//    }
//
//    public void setBase_width(int base_width) {
//        this.base_width = base_width;
//    }
//
//    public int getBase_height() {
//        return base_radius;
//    }
//
//    public void setBase_height(int base_height) {
//        this.base_radius = base_height;
//    }
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Circle getBase_shape() {
        return base_shape;
    }

}
