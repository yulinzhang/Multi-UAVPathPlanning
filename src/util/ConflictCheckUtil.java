/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import algorithm.RRT.RRTNode;
import java.awt.Rectangle;
import java.util.Vector;
import world.model.Obstacle;
import world.model.Threat;

/**
 *
 * @author Yulin_Zhang
 */
public class ConflictCheckUtil {

    /**
     * if conflicted then return true, otherwise return false;
     *
     * @param obstacles
     * @param threats
     * @param coordinate_x
     * @param coordinate_y
     * @return
     */
    public static boolean checkPointInObstaclesAndThreats(Vector<Obstacle> obstacles, Vector<Threat> threats, float coordinate_x, float coordinate_y) {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                /** increase a little bit bound to keep a visible safe distance from obstacle and make it looks less dangerous.
                 * 
                 */
                Rectangle bound = obstacle.getShape().getBounds();
                bound.setBounds(bound.x - 2, bound.y - 2, bound.width + 4, bound.height + 4);
//                if (obstacle.getShape().getBounds().contains(coordinate_x, coordinate_y)) {
//                    return true;
//                }
                
                if (bound.contains(coordinate_x, coordinate_y)) {
                    return true;
                }
            }
        }
        if (threats != null) {
            for (Threat threat : threats) {
                /** increase a little bit bound to keep a visible safe distance from obstacle and make it looks less dangerous.
                 * 
                 */
                Rectangle bound = threat.getShape().getBounds();
                bound.setBounds(bound.x - 2, bound.y - 2, bound.width + 4, bound.height + 4);
//                if (threat.getShape().getBounds().contains(coordinate_x, coordinate_y)) {
//                    return true;
//                }
                if (bound.contains(coordinate_x, coordinate_y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkNodeInObstaclesAndThreats(Vector<Obstacle> obstacles, Vector<Threat> threats, RRTNode node) {
        float[] coordinate = node.getCoordinate();
        return checkPointInObstaclesAndThreats(obstacles, threats, coordinate[0], coordinate[1]);
    }

    public static boolean checkLineInObstaclesAndThreats(Vector<Obstacle> obstacles, Vector<Threat> threats, float[] start_coord, float[] end_coord) {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                if (ShapeUtil.isIntersected(obstacle.getShape().getBounds(), start_coord, end_coord)) {
                    return true;
                }
            }
        }
        if (threats != null) {
            for (Threat threat : threats) {
                if (ShapeUtil.isIntersected(threat.getShape(), start_coord, end_coord)) {
                    return true;
                }
            }
        }
        return false;
    }
}
