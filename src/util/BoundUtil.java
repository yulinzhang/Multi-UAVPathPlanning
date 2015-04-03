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
        if(coordinate[0]>bound_width||coordinate[0]<0||coordinate[1]>bound_height||coordinate[1]<0)
        {
            return false;
        }
        return true;
    }
}
