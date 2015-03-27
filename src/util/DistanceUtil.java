/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author boluo
 */
public class DistanceUtil {

    public static float distanceBetween(float[] coordinate_node_1, float[] coordinate_node_2) {
        float dist = (float) Math.sqrt(Math.pow(coordinate_node_1[0] - coordinate_node_2[0], 2) + Math.pow(coordinate_node_1[1] - coordinate_node_2[1], 2));
        return dist;
    }
}
