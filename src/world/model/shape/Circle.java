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

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/** 
 *
 * @author Yulin_Zhang
 */
public class Circle extends Ellipse2D.Double {

    private float[] center_coordinates = new float[2];
    private float radius;

    /**
     * used for collision detection
     *
     */
    private Rectangle collision_rect;

    public Circle(float center_coordinate_x, float center_coordinate_y, float radius) {
        this.radius = radius;
        setCoordinate(center_coordinate_x, center_coordinate_y);
        collision_rect = new Rectangle((int) (center_coordinate_x - radius), (int) (center_coordinate_y - radius), (int) (2 * radius), (int) (2 * radius));
    }

    public float[] getCenter_coordinates() {
        return center_coordinates;
    }

    public void setCenter_coordinates(float[] center_coordinates) {
        this.center_coordinates = center_coordinates;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCoordinate(float center_coordinate_x, float center_coordinate_y) {
        this.setFrameFromCenter(center_coordinate_x, center_coordinate_y, center_coordinate_x + radius, center_coordinate_y + radius);
        this.center_coordinates[0] = center_coordinate_x;
        this.center_coordinates[1] = center_coordinate_y;
        if (collision_rect != null) {
            collision_rect.setRect((int) (center_coordinate_x - radius), (int) (center_coordinate_y - radius), (int) (2 * radius), (int) (2 * radius));
        }
    }

    public Rectangle getCollision_rect() {
        return collision_rect;
    }

}
