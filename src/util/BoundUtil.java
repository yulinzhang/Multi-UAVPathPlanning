/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import algorithm.RRT.RRTNode;

/**
 *
 * @author boluo
 */
public class BoundUtil {
    /**
     * check whether coordinate of node in world
     * @param node
     * @param bound_width
     * @param bound_height
     * @return true if within given bound
     */
    public static boolean withinBound(RRTNode node, int bound_width, int bound_height)
    {
        float[] coordinate=node.getCoordinate();
        return withinBound(coordinate[0],coordinate[1],bound_width,bound_height);
    }
    
    public static boolean withinBound(float coord_x,float coord_y,int bound_width, int bound_height)
    {
        if(coord_x>bound_width||coord_x<0||coord_y>bound_height||coord_y<0)
        {
            return false;
        }
        return true;
    }
}
