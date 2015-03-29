/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import config.GraphicConfig;
import config.NonStaticInitConfig;
import config.StaticInitConfig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import uav.UAV;
import uav.UAVBase;
import util.ImageUtil;
import world.model.Obstacle;
import world.model.StaticThreat;
import world.World;

/**
 *
 * @author boluo
 */
public class AnimationPanel extends JPanel {

    /**
     * -------------outside variable---------------
     */
    private int bound_width = 800;
    private int bound_height = 600;

    /**
     * -------------internal variable---------------
     */
    private World world;

    private BufferedImage background_image_level_1;
    private BufferedImage obstacle_image_level_2;
    private BufferedImage enemy_uav_image_level_3;
    private BufferedImage fog_of_war_image_level_4;
    private BufferedImage uav_history_path_image_level_5;
    private BufferedImage uav_planned_tree_image_level_6;
    private BufferedImage uav_planned_path_image_level_7;
    private BufferedImage uav_image_level_8;

    private Color transparent_color;
    private Graphics2D fog_of_war_graphics;
    private Graphics2D uav_image_graphics;
    private Graphics2D obstacle_image_graphics;
    private Graphics2D enemy_uav_image_graphics;
    private Graphics2D uav_history_path_image_graphics;
    private Graphics2D uav_planned_tree_image_graphics;
    private Graphics2D uav_planned_path_image_graphics;

    private MyGraphic virtualizer;

    private Vector<UAV> attackers;
    private Vector<UAV> scouts;
    private Vector<UAV> enemy_uavs;
    private UAVBase uav_base;
    private Vector<Obstacle> obstacles;
    private Vector<StaticThreat> targets;

    private static int uav_base_line_width = 3;

    public AnimationPanel() {
        try {
            transparent_color = GraphicConfig.transparent_color;
            Color fog_of_war_color = GraphicConfig.fog_of_war_color;//Color.black;

            //initiate background image
            background_image_level_1 = ImageUtil.retrieveImage("/resources/background2.jpg");

            //initiate obstacle image
            obstacle_image_level_2 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            obstacle_image_graphics = obstacle_image_level_2.createGraphics();

            //initiate enemy_uav_image
            enemy_uav_image_level_3 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            enemy_uav_image_graphics = enemy_uav_image_level_3.createGraphics();

            //initiate fog_of_war image
            fog_of_war_image_level_4 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            fog_of_war_graphics = fog_of_war_image_level_4.createGraphics();
            fog_of_war_graphics.setBackground(fog_of_war_color);
            fog_of_war_graphics.setColor(fog_of_war_color);
            fog_of_war_graphics.fillRect(0, 0, bound_width, bound_height);

            //initiate uav_image
            uav_image_level_8 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            uav_image_graphics = uav_image_level_8.createGraphics();
            uav_image_graphics.setBackground(transparent_color);

            //initiate history path image to store history path of uavs
            uav_history_path_image_level_5 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            uav_history_path_image_graphics = uav_history_path_image_level_5.createGraphics();
            uav_history_path_image_graphics.setBackground(transparent_color);

            //initiate planned tree image to store planned tree of uavs
            uav_planned_tree_image_level_6 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            uav_planned_tree_image_graphics = uav_planned_tree_image_level_6.createGraphics();
            uav_planned_tree_image_graphics.setBackground(transparent_color);

            //initiate planned path image to store planned paths of uavs
            uav_planned_path_image_level_7 = new BufferedImage(bound_width, bound_height,
                    BufferedImage.TYPE_INT_ARGB);
            uav_planned_path_image_graphics = uav_planned_path_image_level_7.createGraphics();
            uav_planned_path_image_graphics.setBackground(transparent_color);

            //initate mygraphic
            virtualizer = new MyGraphic();

            //initiate world
            NonStaticInitConfig init_config = new NonStaticInitConfig();
            world = new World(init_config);
            this.scouts = world.getScouts();
            this.attackers = world.getAttackers();

            //initiate obstacles in level 2
            this.initObstaclesInObstacleImageInLevel2(world.getObstacles());
            this.initTargetInObstacleImageLevel2(world.getTargets());

            //initiate parameters according to world
            this.initParameterFromInitConfig(world);

            //drive the world and ui
            StaticInitConfig.SIMULATION_WITH_UI_TIMER = new javax.swing.Timer(StaticInitConfig.INIT_SIMULATION_DELAY, new animatorListener(this));
            StaticInitConfig.SIMULATION_WITH_UI_TIMER.start();
        } catch (IOException ex) {
            Logger.getLogger(AnimationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initParameterFromInitConfig(World world) {
        this.bound_width = world.getBound_width();
        this.bound_height = world.getBound_height();

        this.scouts = world.getScouts();
        this.attackers = world.getAttackers();
        this.enemy_uavs = world.getEnemy_uavs();
        this.targets = world.getTargets();
        this.obstacles = world.getObstacles();
        this.uav_base = world.getUav_base();

    }

    private void initObstaclesInObstacleImageInLevel2(Vector<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            virtualizer.drawObstacle(obstacle_image_graphics, obs, GraphicConfig.obstacle_center_color, GraphicConfig.obstacle_edge_color);
        }
    }

    private void initTargetInObstacleImageLevel2(Vector<StaticThreat> static_threats) {
        for (StaticThreat static_threat : static_threats) {
            virtualizer.drawTarget(obstacle_image_graphics, static_threat, GraphicConfig.static_threat_color);
        }
    }

    private void initUAVBase(Graphics2D uav_image_graphics) {
        uav_image_graphics.setColor(Color.white);
        uav_image_graphics.setStroke(new BasicStroke(uav_base_line_width));
        uav_image_graphics.drawRect((int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width(), uav_base.getBase_height());
        uav_image_graphics.setColor(GraphicConfig.uav_base_color);
        uav_image_graphics.fillRect((int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width(), uav_base.getBase_height());
        uav_image_graphics.drawImage(uav_base.getImage(), (int)uav_base.getCoordinate()[0],(int)uav_base.getCoordinate()[1],uav_base.getBase_width()*2/3,uav_base.getBase_height()*2/3, null);
    }

    private void clearUAVImageBeforeUpdate() {
        uav_image_graphics.setColor(transparent_color);
        uav_image_graphics.setBackground(transparent_color);
        uav_image_graphics.clearRect(0, 0, bound_width, bound_height);
        enemy_uav_image_graphics.setColor(transparent_color);
        enemy_uav_image_graphics.setBackground(transparent_color);
        enemy_uav_image_graphics.clearRect(0, 0, bound_width, bound_height);
    }

    private void updateImageCausedByUAVMovement() {
        initUAVBase(uav_image_graphics);
        updateUAVImageInLevel4();
        updateFogOfWarImageInLevel3();
        updateUAVHistoryPath();
        showUAVPlannedPath();
        showUAVPlannedTree();
    }

    private void updateUAVHistoryPath() {
        for (UAV scout : this.scouts) {
            virtualizer.drawUAVHistoryPath(uav_history_path_image_graphics, scout, GraphicConfig.side_a_center_color);
        }
    }

    private void showUAVPlannedTree() {
        uav_planned_tree_image_graphics.setBackground(transparent_color);
        uav_planned_tree_image_graphics.setColor(transparent_color);
        uav_planned_tree_image_graphics.clearRect(0, 0, bound_width, bound_height);
        for (UAV scout : this.scouts) {
            virtualizer.drawUAVPlannedTree(uav_planned_tree_image_graphics, scout, GraphicConfig.uav_planned_path_color);
        }
    }

    private void showUAVPlannedPath() {
        uav_planned_path_image_graphics.setBackground(transparent_color);
        uav_planned_path_image_graphics.setColor(transparent_color);
        uav_planned_path_image_graphics.clearRect(0, 0, bound_width, bound_height);
        for (UAV scout : this.scouts) {
            virtualizer.drawUAVPlannedPath(uav_planned_path_image_graphics, scout, GraphicConfig.uav_planned_path_color);
        }
    }

    private void updateUAVImageInLevel4() {
        for (UAV scout : this.scouts) {
            virtualizer.drawUAVInUAVImage(uav_image_graphics, scout, GraphicConfig.side_a_radar_color, GraphicConfig.side_a_center_color);
        }
        for (UAV attacker : this.attackers) {
            virtualizer.drawUAVInUAVImage(uav_image_graphics, attacker, GraphicConfig.side_a_radar_color, GraphicConfig.side_a_center_color);
        }
        for (UAV enemy_uav : this.enemy_uavs) {
            virtualizer.drawUAVInUAVImage(enemy_uav_image_graphics, enemy_uav, GraphicConfig.side_b_radar_color, GraphicConfig.side_b_center_color);
        }
    }

    private void updateFogOfWarImageInLevel3() {
        for (UAV scout : this.scouts) {
            virtualizer.drawScoutInFogOfWarInLevel3(fog_of_war_graphics, scout);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background_image_level_1, 0, 0, null);
        g.drawImage(obstacle_image_level_2, 0, 0, null);
        g.drawImage(enemy_uav_image_level_3, 0, 0, null);
        if (StaticInitConfig.SHOW_FOG_OF_WAR) {
            g.drawImage(fog_of_war_image_level_4, 0, 0, null);
        }
        if (StaticInitConfig.SHOW_HISTORY_PATH) {
            g.drawImage(uav_history_path_image_level_5, 0, 0, null);
        }
        if (StaticInitConfig.SHOW_PLANNED_TREE) {
            g.drawImage(uav_planned_tree_image_level_6, 0, 0, null);
        }
        if (StaticInitConfig.SHOW_PLANNED_PATH) {
            g.drawImage(uav_planned_path_image_level_7, 0, 0, null);
        }
        g.drawImage(uav_image_level_8, 0, 0, null);
    }

    //animation
    private class animatorListener implements ActionListener {

        JPanel panel;

        public animatorListener(JPanel panel) {
            this.panel = panel;
        }

        public void actionPerformed(ActionEvent e) {
            clearUAVImageBeforeUpdate();
            if (StaticInitConfig.SIMULATION_ON) {
                world.updateAll();
            }
            updateImageCausedByUAVMovement();
            repaint();
            System.out.println(panel.getSize().width + "-" + panel.getSize().height);
        }
    }
}
