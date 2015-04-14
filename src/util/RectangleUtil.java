/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Rectangle;

/**
 *
 * @author boluo
 */
public class RectangleUtil {
    public static Rectangle findMBRRect(float[] point1,float[] point2)
    {
        float[] rect_center=new float[2]; 
        rect_center[0] = (point1[0] + point2[0]) / 2.0f;
        rect_center[1] = (point1[1] + point2[1]) / 2.0f;
        int width = (int) Math.abs(point2[0] - point1[0]);
        int height = (int) Math.abs(point2[1] - point1[1]);
        Rectangle rect = new Rectangle((int) rect_center[0] - width / 2, (int) rect_center[0] - height / 2, width, height);
        return rect;
    }
}
