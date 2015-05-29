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
package world.model.shape;

import java.awt.Polygon;
import util.VectorUtil;

/**
 *
 * @author Yulin_Zhang
 */
public class Triangle extends Polygon {

    private float[] center_coordinate = new float[2];
    private float radians_angle;
    private int height = 10;
    private int width = 10;

    /**
     *
     * @param center_coordinate
     * @param radians_angle radians, the result of Math.toRadians(x);
     */
    public Triangle(float center_coordinate_x, float center_coorindate_y, float radians_angle, int width, int height) {
        this.center_coordinate[0] = center_coordinate_x;
        this.center_coordinate[1] = center_coorindate_y;
        this.radians_angle = radians_angle;
        updateTriangle();
        this.width = width;
        this.height = height;
        //translate backward to be more beautiful
//        this.translate(-height / 4, 0);
    }

    public void updateTriangle() {
        float[] xpoints = new float[3];
        float[] ypoints = new float[3];
        xpoints[0] = (float) (height * Math.cos(this.radians_angle) + this.center_coordinate[0]);
        ypoints[0] = (float) (height * Math.sin(this.radians_angle) + this.center_coordinate[1]);
        double angle_of_point_left_center = this.radians_angle + Math.PI / 2;
        double angle_of_point_right_center = this.radians_angle - Math.PI / 2;
        xpoints[1] = (float) (width / 2 * Math.cos(angle_of_point_left_center) + this.center_coordinate[0]);
        ypoints[1] = (float) (width / 2 * Math.sin(angle_of_point_left_center) + this.center_coordinate[1]);
        xpoints[2] = (float) (width / 2 * Math.cos(angle_of_point_right_center) + this.center_coordinate[0]);
        ypoints[2] = (float) (width / 2 * Math.sin(angle_of_point_right_center) + this.center_coordinate[1]);
        this.reset();
        this.addPoint((int) xpoints[0], (int) ypoints[0]);
        this.addPoint((int) xpoints[1], (int) ypoints[1]);
        this.addPoint((int) xpoints[2], (int) ypoints[2]);
    }

    public void setCoordinate(float center_coordinate_x, float center_coorindate_y) {
        this.radians_angle =(float) VectorUtil.getAngleOfVectorRelativeToXCoordinate(center_coordinate_x-this.center_coordinate[0], center_coorindate_y-this.center_coordinate[1]);
        this.center_coordinate[0] = center_coordinate_x;
        this.center_coordinate[1] = center_coorindate_y;
        updateTriangle();
    }
}
