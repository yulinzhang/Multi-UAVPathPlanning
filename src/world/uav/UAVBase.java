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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ImageUtil;
import world.model.shape.Circle;

/** This is the base of all uavs.
 *
 * @author Yulin_Zhang
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

    /** It initiate the places for all attackers.
     * 
     */
    public void initUAVPort() {
        uav_port_map = new HashMap<Integer, float[]>();

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
