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
package util;

import algorithm.RRT.RRTNode;
import java.awt.Rectangle;
import java.util.ArrayList;
import ui.AnimationPanel;
import world.model.Conflict;
import world.model.Obstacle;
import world.model.shape.Point;

/** This class is a tool class and providing tool functions to check whether a given unit is conflicted with others.
 *
 * @author Yulin_Zhang
 */
public class ConflictCheckUtil {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AnimationPanel.class);

    /**if current point is within given obstacles, then it means conflicted and return true, otherwise return false;
     * 
     * @param obstacles
     * @param threats
     * @param coordinate_x
     * @param coordinate_y
     * @return
     */
    public static boolean checkPointInObstacles(ArrayList<Obstacle> obstacles, float coordinate_x, float coordinate_y) {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                
                Rectangle bound=null;
                try{
                 bound= obstacle.getShape().getBounds();
                }catch(Exception e)
                {
                    logger.debug("error index"+obstacle.getIndex());
                    logger.error(e);
                }
                //increase a little bit obs_mbr to keep a visible safe distance from obstacle and make it looks less dangerous.
                bound.setBounds(bound.x - 2, bound.y - 2, bound.width + 4, bound.height + 4);
               
                if (bound.contains(coordinate_x, coordinate_y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**if threat intersects with any other obstacle, then it means conflicted and return true, otherwise return false;
     * 
     * @param obstacles
     * @param threat
     * @return
     */
    public static boolean checkThreatInObstacles(ArrayList<Obstacle> obstacles, Rectangle threat_mbr) {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                
                Rectangle obs_mbr=null;
                try{
                 obs_mbr= obstacle.getShape().getBounds();
                }catch(Exception e)
                {
                    logger.debug("error index"+obstacle.getIndex());
                    logger.error(e);
                }
                //increase a little bit obs_mbr to keep a visible safe distance from obstacle and make it looks less dangerous.
                obs_mbr.setBounds(obs_mbr.x - 2, obs_mbr.y - 2, obs_mbr.width + 4, obs_mbr.height + 4);
               
                if (obs_mbr.intersects(threat_mbr)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /** if the node planned by rrt is too close to a given uav waypoint at the same time step, then it means conflicted and returns true, otherwise returns false.
     * 
     * @param uav_future_path
     * @param uav_conflict
     * @param uav_safe_conflict_dist
     * @return 
     */
    public static boolean checkUAVConflict(RRTNode new_node,Conflict uav_conflict)
    {
         float uav_safe_conflict_dist=uav_conflict.getConflict_range();
        if(uav_conflict==null)
        {
            return false;
        }
        int uav_conflict_size=uav_conflict.getPath_prefound().size();
        int new_node_exptected_time_step=new_node.getExpected_time_step();
        if(new_node_exptected_time_step<uav_conflict_size)
        {
            Point conflict_point=uav_conflict.getPath_prefound().get(new_node_exptected_time_step);
            int conflict_time=conflict_point.getExptected_time_step();
            if(conflict_time==new_node_exptected_time_step&& DistanceUtil.distanceBetween(conflict_point.toFloatArray(), new_node.getCoordinate())<uav_safe_conflict_dist)
            {
                return true;
            }else if(conflict_time>new_node_exptected_time_step)
            {
                return false;
            }
        }
        return false;
    }
    
    /** check whether given point planned by rrt is within any obstacle. If it is, then it means conflicted and returns true. Otherwise returns false.
     * 
     * @param obstacles
     * @param node
     * @return 
     */
    public static boolean checkNodeInObstacles(ArrayList<Obstacle> obstacles,  RRTNode node) {
        float[] coordinate = node.getCoordinate();
        return checkPointInObstacles(obstacles, coordinate[0], coordinate[1]);
    }

    /** if line intersects with any obstacle, then return true; otherwise return false;
     * 
     * @param obstacles
     * @param start_coord
     * @param end_coord
     * @return 
     */
    public static boolean checkLineInObstacles(ArrayList<Obstacle> obstacles, float[] start_coord, float[] end_coord) {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                if (ShapeIntersectionUtil.isIntersected(obstacle.getShape().getBounds(), start_coord, end_coord)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /** return true if two rectangle intersects.
     * 
     * @param rect1
     * @param rect2
     * @return 
     */
    public static boolean checkMBRIntersected(Rectangle rect1,Rectangle rect2)
    {
        return rect1.intersects(rect2);
    }
}
