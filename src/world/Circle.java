/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author boluo
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
