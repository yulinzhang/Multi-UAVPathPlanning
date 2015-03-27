/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import algorithm.RRT.RRTNode;
import algorithm.RRT.RRTTree;
import config.GraphicConfig;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import uav.UAV;
import world.Circle;
import world.Obstacle;
import world.Target;
import world.Threat;

/**
 *
 * @author Yulin_Zhang
 */
public class MyGraphic {

//    public void clearUAVShadowInUAVImage(Graphics2D graphics,UAV scout) {
//        graphics.setComposite(AlphaComposite.Clear);
//        graphics.fill(scout.getUav_radar());
//    }
    public void drawScoutInFogOfWarInLevel3(Graphics2D graphics, UAV scout) {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fill(scout.getUav_radar());
    }

    public void drawUAVInUAVImage(Graphics2D graphics, UAV uav, Color uav_radar_color, Color uav_center_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(uav_radar_color);
        Circle uav_radar = uav.getUav_radar();
        graphics.fill(uav_radar);
        graphics.setColor(uav_center_color);
        graphics.fillPolygon(uav.getUav_center());
    }

    public void drawUAVHistoryPath(Graphics2D graphics, UAV uav, Color uav_history_path_color) {
        float[] previous_waypoint = uav.getPrevious_waypoint();
        float[] current_waypoint = uav.getCenter_coordinates();
        Stroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0,
                new float[]{0.5f, 2}, 0);
        graphics.setStroke(bs);
        graphics.setColor(uav_history_path_color);
        graphics.drawLine((int) previous_waypoint[0], (int) previous_waypoint[1], (int) current_waypoint[0], (int) current_waypoint[1]);
    }

    public void drawUAVPlannedPath(Graphics2D graphics, UAV uav, Color uav_planned_path_color) {
        graphics.setColor(uav_planned_path_color);
        graphics.setStroke(new BasicStroke(2.0f));
        LinkedList<RRTNode> planned_path = uav.getFuturePath();
        int planned_path_size = planned_path.size();
        float[] current_waypoint;
        float[] next_waypoint;
        for (int i = 0; i < planned_path_size; i++) {
            current_waypoint = planned_path.get(i).getCoordinate();
            if (i + 1 < planned_path_size) {
                next_waypoint = planned_path.get(i + 1).getCoordinate();
                graphics.drawLine((int) current_waypoint[0], (int) current_waypoint[1], (int) next_waypoint[0], (int) next_waypoint[1]);
            }
        }
    }

    public void drawUAVPlannedTree(Graphics2D graphics, UAV uav, Color uav_planned_tree_color) {
        graphics.setColor(uav_planned_tree_color);
        graphics.setStroke(new BasicStroke(0.5f));
        float[] current_waypoint;
        float[] next_waypoint;
        RRTTree rrt_tree = uav.getRrt_tree();
        RRTNode root_node = rrt_tree.getNode(0);
        Queue<RRTNode> queue = new LinkedList<RRTNode>();
        queue.add(root_node);
//            ArrayList<RRTNode> queue=rrt_tree.getChildren(root_node);
        while (!queue.isEmpty()) {
            RRTNode node_out_of_queue = queue.poll();
            current_waypoint = node_out_of_queue.getCoordinate();
            ArrayList<RRTNode> children = rrt_tree.getChildren(node_out_of_queue);
            if (children != null) {
                for (RRTNode child : children) {
                    next_waypoint = child.getCoordinate();
                    graphics.drawLine((int) current_waypoint[0], (int) current_waypoint[1], (int) next_waypoint[0], (int) next_waypoint[1]);
                    queue.add(child);
                }
            }
        }
    }

    public void drawThreat(Graphics2D graphics, Threat threat, Color threat_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(threat_color);
        graphics.fill(threat.getShape());
    }

    public void drawObstacle(Graphics2D graphics, Obstacle obstacle, Color obstacle_center_color, Color obstacle_edge_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setStroke(new BasicStroke(1f));
        graphics.setColor(obstacle_center_color);
        graphics.fill(obstacle.getShape());
        graphics.setColor(obstacle_edge_color);
        graphics.draw(obstacle.getShape());
    }

    public void drawTarget(Graphics2D graphics, Target target, Color target_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(target_color);
        graphics.setStroke(new BasicStroke(3.0f));
        graphics.drawRect((int) target.getCoordinates()[0] - GraphicConfig.target_width / 2, (int) target.getCoordinates()[1] - GraphicConfig.target_height / 2, GraphicConfig.target_width, GraphicConfig.target_height);
    }

}
