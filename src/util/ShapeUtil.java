/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Rectangle;
import world.model.shape.Circle;

/**
 *
 * @author boluo
 */
public class ShapeUtil {
    
    public static boolean isIntersected(Rectangle rect, float[] line_starting_point, float[] line_ending_point)
    {
        return rect.intersectsLine(line_starting_point[0], line_starting_point[1], line_ending_point[0], line_ending_point[1]);
    }
    
    
    public static boolean isIntersected(Circle circle,float[] line_starting_point, float[] line_ending_point)
    {
        return circle.getCollision_rect().intersects(line_starting_point[0], line_starting_point[1], line_ending_point[0], line_ending_point[1]);
    }
}
