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
import world.uav.UAV;
import world.model.shape.Circle;
import world.model.Obstacle;
import world.model.Threat;
import world.model.shape.Point;

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

    public void drawUAVInUAVImage(Graphics2D graphics, UAV uav, Color uav_radar_color, Color uav_center_color, Color uav_highlight_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(uav_radar_color);
        Circle uav_radar = uav.getUav_radar();
        graphics.fill(uav_radar);
        graphics.setColor(uav_center_color);
        graphics.fillPolygon(uav.getUav_center());
        if (uav_highlight_color != null) {
            graphics.setColor(uav_highlight_color);
            graphics.draw(uav_radar);
        }
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
        graphics.setStroke(new BasicStroke(2.0f)); //Set the width of the stroke
        LinkedList<Point> planned_path = uav.getFuturePath();
        int planned_path_size = planned_path.size();
        float[] current_waypoint;
        float[] next_waypoint;
        for (int i = 0; i < planned_path_size; i++) {
            current_waypoint = planned_path.get(i).toFloatArray();
            if (i + 1 < planned_path_size) {
                next_waypoint = planned_path.get(i + 1).toFloatArray();
                graphics.drawLine((int) current_waypoint[0], (int) current_waypoint[1], (int) next_waypoint[0], (int) next_waypoint[1]);
            }
        }
    }

    public void drawUAVPlannedTree(Graphics2D graphics, UAV uav, Color uav_planned_tree_color) {
        graphics.setColor(uav_planned_tree_color);
        graphics.setStroke(new BasicStroke(0.5f)); //Set the width of the stroke
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

    public void drawObstacle(Graphics2D graphics, Obstacle obstacle, Color obstacle_center_color, Color obstacle_edge_color, Color obstacle_hightlight_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setStroke(new BasicStroke(1f)); //Set the width of the stroke
        graphics.setColor(obstacle_center_color);
        graphics.fill(obstacle.getShape());
        if (obstacle_hightlight_color != null) {
            graphics.setColor(obstacle_hightlight_color);
        } else {
            graphics.setColor(obstacle_edge_color);
        }
        graphics.draw(obstacle.getShape());

    }
    public void highlightObstacle(Graphics2D graphics, Obstacle obstacle, Color obstacle_center_color, Color obstacle_edge_color, Color obstacle_hightlight_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setStroke(new BasicStroke(2f));
        if (obstacle_hightlight_color != null) {
            graphics.setColor(obstacle_hightlight_color);
        } else {
            graphics.setColor(obstacle_edge_color);
        }
        graphics.draw(obstacle.getShape());

    }

    public void drawTarget(Graphics2D graphics, Threat target, Color target_color, Color target_highlight_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        if (target_highlight_color != null) {
            graphics.setColor(target_highlight_color);
        } else {
            graphics.setColor(target_color);
        }
        graphics.setStroke(new BasicStroke(3.0f)); //Set the width of the stroke
        graphics.drawRect((int) target.getCoordinates()[0] - GraphicConfig.threat_width / 2, (int) target.getCoordinates()[1] - GraphicConfig.threat_height / 2, GraphicConfig.threat_width, GraphicConfig.threat_height);
    }

}
