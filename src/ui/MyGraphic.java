/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import algorithm.RRT.RRTNode;
import algorithm.RRT.RRTTree;
import config.StaticInitConfig;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import world.uav.Attacker;
import world.model.shape.Circle;
import world.model.Obstacle;
import world.model.Threat;
import world.uav.UAV;
import world.uav.UAVBase;
import world.uav.UAVPath;

/**
 *
 * @author Yulin_Zhang
 */
public class MyGraphic {

    private static int uav_base_line_width = 3;

    public void drawUAVBaseInFogOfWar(Graphics2D graphics, UAVBase uav_base) {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fill(uav_base.getBase_shape());
    }

    public void drawUAVInFogOfWarInLevel3(Graphics2D graphics, UAV uav) {
        if (!uav.isVisible()) {
            return;
        }
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fill(uav.getUav_radar());
    }

    public void showObstacleInFogOfWar(Graphics2D graphics, Obstacle obs) {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fill(obs.getMbr());
    }

    public void showThreatInFogOfWar(Graphics2D graphics, Threat threat) {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect((int) threat.getCoordinates()[0] - Threat.threat_width / 2, (int) threat.getCoordinates()[1] - Threat.threat_height / 2, Threat.threat_width, Threat.threat_height);
    }

    public void drawUAVInUAVImage(Graphics2D graphics, UAVBase uav_base, UAV uav, Color uav_highlight_color) {
        if (!uav.isVisible()) {
            return;
        }

        boolean draw_radar_or_not = true;
        
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(uav.getRadar_color());

        Color uav_radar_color_inner = uav.getRadar_color();
        Color uav_radar_color_outter = new Color(uav_radar_color_inner.getRed(), uav_radar_color_inner.getGreen(), uav_radar_color_inner.getBlue(), uav_radar_color_inner.getAlpha() / 2);
        Circle uav_radar_outter = uav.getUav_radar();
        Circle uav_radar_inner = new Circle(uav_radar_outter.getCenter_coordinates()[0], uav_radar_outter.getCenter_coordinates()[1], uav_radar_outter.getRadius() / 2);

        if (draw_radar_or_not) {

            graphics.setColor(uav_radar_color_outter);
            graphics.fill(uav_radar_outter);

            graphics.setColor(uav_radar_color_inner);
            graphics.fill(uav_radar_inner);
        }
        graphics.setColor(uav.getCenter_color());
        graphics.fillPolygon(uav.getUav_center());

        if (uav_highlight_color != null) {
            graphics.setColor(uav_highlight_color);
            graphics.draw(uav_radar_outter);
        }
    }

    public void drawUAVHistoryPath(Graphics2D graphics, Attacker uav, Color uav_history_path_color) {
        if (!uav.isVisible()) {
            return;
        }
        float[] previous_waypoint = uav.getPrevious_waypoint();
        float[] current_waypoint = uav.getCenter_coordinates();
        Stroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0,
                new float[]{0.5f, 2}, 0);
        graphics.setStroke(bs);
        graphics.setColor(uav_history_path_color);
        graphics.drawLine((int) previous_waypoint[0], (int) previous_waypoint[1], (int) current_waypoint[0], (int) current_waypoint[1]);
    }

    public void drawUAVPlannedPath(Graphics2D graphics, Attacker uav, Color uav_planned_path_color) {
        if (!uav.isVisible()) {
            return;
        }
        graphics.setColor(uav.getCenter_color());
        graphics.setStroke(new BasicStroke(2.0f)); //Set the width of the stroke
        UAVPath planned_path = uav.getFuturePath();
        int planned_path_size = planned_path.getWaypointNum();
        float[] current_waypoint;
        float[] next_waypoint;
        for (int i = 0; i < planned_path_size; i++) {
            current_waypoint = planned_path.getWaypoint(i).toFloatArray();
            if (i + 1 < planned_path_size) {
                next_waypoint = planned_path.getWaypoint(i + 1).toFloatArray();
                graphics.drawLine((int) current_waypoint[0], (int) current_waypoint[1], (int) next_waypoint[0], (int) next_waypoint[1]);

            }
        }

        Stroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0,
                new float[]{0.5f, 2}, 0);
        graphics.setStroke(bs);
        planned_path = uav.getPath_planned_at_last_time_step();
        if (planned_path == null) {
            return;
        }
        planned_path_size = planned_path.getWaypointNum();
        for (int i = 0; i < planned_path_size; i++) {
            current_waypoint = planned_path.getWaypoint(i).toFloatArray();
            if (i + 1 < planned_path_size) {
                next_waypoint = planned_path.getWaypoint(i + 1).toFloatArray();
                graphics.drawLine((int) current_waypoint[0], (int) current_waypoint[1], (int) next_waypoint[0], (int) next_waypoint[1]);
            }
        }
    }

    public void drawUAVPlannedTree(Graphics2D graphics, Attacker uav, Color uav_planned_tree_color) {
        if (!uav.isVisible()) {
            return;
        }
        graphics.setColor(uav_planned_tree_color);
        graphics.setStroke(new BasicStroke(0.5f)); //Set the width of the stroke
        float[] current_waypoint;
        float[] next_waypoint;
        RRTTree rrt_tree = uav.getRrt_tree();
        RRTNode root_node = rrt_tree.getNode(0);
        Queue<RRTNode> queue = new LinkedList<RRTNode>();
        queue.add(root_node);
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

    public void drawTankTarget(Graphics2D graphics, int[] upper_left_point, int width, int height) {
        graphics.drawRect(upper_left_point[0], upper_left_point[1], width, height);
        int oval_width = width * 3 / 4;
        int oval_height = height / 2;
        graphics.drawOval(upper_left_point[0] + width / 2 - oval_width / 2, upper_left_point[1] + height / 2 - oval_height / 2, oval_width, oval_height);
    }

    public void drawCombatSymbol(Graphics2D graphics, float[] combat_center, int combat_cross_len, Color combat_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(combat_color);
        double angle = Math.PI / 4;
        double[] upper_left_coord = new double[2];
        double[] upper_right_coord = new double[2];
        double[] lower_left_coord = new double[2];
        double[] lower_right_coord = new double[2];

        upper_left_coord[0] = combat_center[0] - Math.cos(angle) * combat_cross_len / 2;
        upper_left_coord[1] = combat_center[1] - Math.sin(angle) * combat_cross_len / 2;

        upper_right_coord[0] = combat_center[0] + Math.cos(angle) * combat_cross_len / 2;
        upper_right_coord[1] = combat_center[1] - Math.sin(angle) * combat_cross_len / 2;

        lower_left_coord[0] = combat_center[0] - Math.cos(angle) * combat_cross_len / 2;
        lower_left_coord[1] = combat_center[1] + Math.sin(angle) * combat_cross_len / 2;

        lower_right_coord[0] = combat_center[0] + Math.cos(angle) * combat_cross_len / 2;
        lower_right_coord[1] = combat_center[1] + Math.sin(angle) * combat_cross_len / 2;

        graphics.drawLine((int) upper_left_coord[0], (int) upper_left_coord[1], (int) lower_right_coord[0], (int) lower_right_coord[1]);
        graphics.drawLine((int) upper_right_coord[0], (int) upper_right_coord[1], (int) lower_left_coord[0], (int) lower_left_coord[1]);
    }

    public void drawThreat(Graphics2D graphics, Threat threat, Color target_color, Color target_highlight_color) {
        graphics.setComposite(AlphaComposite.SrcOver);
        if (target_highlight_color != null) {
            graphics.setColor(target_highlight_color);
        } else {
            graphics.setColor(target_color);
        }
        graphics.setStroke(new BasicStroke(3.0f));//Set the width of the stroke
        graphics.drawString(StaticInitConfig.THREAT_NAME + threat.getIndex(), threat.getCoordinates()[0] - 10, threat.getCoordinates()[1] - 15);
        int[] upper_left_point = new int[2];
        upper_left_point[0] = (int) threat.getCoordinates()[0] - Threat.threat_width / 2;
        upper_left_point[1] = (int) threat.getCoordinates()[1] - Threat.threat_height / 2;
        this.drawTankTarget(graphics, upper_left_point, Threat.threat_width, Threat.threat_height);
    }

    public void drawUAVBase(Graphics2D graphics, UAVBase uav_base) {
        graphics.setColor(Color.white);
        graphics.setStroke(new BasicStroke(uav_base_line_width));
        graphics.draw(uav_base.getBase_shape());
//        graphics.drawRect((int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width(), uav_base.getBase_height());
//        graphics.setColor(GraphicConfig.uav_base_color);
//        graphics.fillRect((int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width(), uav_base.getBase_height());
        graphics.drawImage(uav_base.getImage(), (int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], (int) uav_base.getBase_shape().getRadius() * 2 / 3, (int) uav_base.getBase_shape().getRadius() * 2 / 3, null);
    }
}
