/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Polygon;

/**
 *
 * @author boluo
 */
public class PolygonUtil {

    public static Polygon genPolygonFromRawOntologyString(String raw_points_str) {
        Polygon polygon = new Polygon();
        String[] points_str = raw_points_str.split(" ");
        for (String point_str : points_str) {
            String[] coord_str = point_str.split(",");
            int[] coord = new int[2];
            coord[0] = Integer.parseInt(coord_str[0]);
            coord[1] = Integer.parseInt(coord_str[1]);
            polygon.addPoint(coord[0], coord[1]);
        }
        return polygon;
    }
}
