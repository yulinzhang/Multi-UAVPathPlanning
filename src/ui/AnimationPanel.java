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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import world.uav.UAV;
import world.uav.UAVBase;
import util.DistanceUtil;
import util.ImageUtil;
import world.model.Obstacle;
import world.model.Threat;
import world.World;

/**
 *
 * @author boluo
 */
public class AnimationPanel extends JPanel implements MouseListener {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AnimationPanel.class);

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
    private BufferedImage enemy_uav_image_level_4;
    private BufferedImage fog_of_war_image_level_5;
    private BufferedImage uav_history_path_image_level_6;
    private BufferedImage uav_planned_tree_image_level_7;
    private BufferedImage uav_planned_path_image_level_8;
    private BufferedImage uav_image_level_9;
    private BufferedImage highlight_obstacle_image_level_3;

    private Color transparent_color;
    private Graphics2D fog_of_war_graphics;
    private Graphics2D uav_image_graphics;
    private Graphics2D obstacle_image_graphics;
    private Graphics2D enemy_uav_image_graphics;
    private Graphics2D uav_history_path_image_graphics;
    private Graphics2D uav_planned_tree_image_graphics;
    private Graphics2D uav_planned_path_image_graphics;
    private Graphics2D highlight_obstacle_image_graphics;

    private MyGraphic virtualizer;

    private static ArrayList<UAV> attackers;
    private ArrayList<UAV> scouts;
    private ArrayList<UAV> enemy_uavs;
    private UAVBase uav_base;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Threat> threats;

    public static int highlight_uav_index = -1;
    public static int highlight_obstacle_index = -1;
    public static int highlight_threat_index = -1;

    private MyPopupMenu my_popup_menu;

    private static int uav_base_line_width = 3;

    public AnimationPanel() {
        try {
            transparent_color = GraphicConfig.transparent_color;
            Color fog_of_war_color = GraphicConfig.fog_of_war_color;//Color.black;

            //initiate background image
            background_image_level_1 = ImageUtil.retrieveImage("/resources/background2.jpg");

            //initiate obstacle image
            obstacle_image_level_2 = createBufferedImage();
            obstacle_image_graphics = obstacle_image_level_2.createGraphics();

            highlight_obstacle_image_level_3 = createBufferedImage();
            highlight_obstacle_image_graphics = highlight_obstacle_image_level_3.createGraphics();

            //initiate enemy_uav_image
            enemy_uav_image_level_4 = createBufferedImage();
            enemy_uav_image_graphics = enemy_uav_image_level_4.createGraphics();

            //initiate fog_of_war image
            fog_of_war_image_level_5 = createBufferedImage();
            fog_of_war_graphics = fog_of_war_image_level_5.createGraphics();
            fog_of_war_graphics.setBackground(fog_of_war_color);
            fog_of_war_graphics.setColor(fog_of_war_color);
            fog_of_war_graphics.fillRect(0, 0, bound_width, bound_height);

            //initiate uav_image
            uav_image_level_9 = createBufferedImage();
            uav_image_graphics = uav_image_level_9.createGraphics();
            uav_image_graphics.setBackground(transparent_color);

            //initiate history path image to store history path of uavs
            uav_history_path_image_level_6 = createBufferedImage();
            uav_history_path_image_graphics = uav_history_path_image_level_6.createGraphics();
            uav_history_path_image_graphics.setBackground(transparent_color);

            //initiate planned tree image to store planned tree of uavs
            uav_planned_tree_image_level_7 = createBufferedImage();
            uav_planned_tree_image_graphics = uav_planned_tree_image_level_7.createGraphics();
            uav_planned_tree_image_graphics.setBackground(transparent_color);

            //initiate planned path image to store planned paths of uavs
            uav_planned_path_image_level_8 = createBufferedImage();
            uav_planned_path_image_graphics = uav_planned_path_image_level_8.createGraphics();
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
            this.initTargetInObstacleImageLevel2(world.getThreats());

            //initiate parameters according to world
            this.initParameterFromInitConfig(world);

            //drive the world and ui
            StaticInitConfig.SIMULATION_WITH_UI_TIMER = new javax.swing.Timer(StaticInitConfig.INIT_SIMULATION_DELAY, new animatorListener(this));
            StaticInitConfig.SIMULATION_WITH_UI_TIMER.start();

            my_popup_menu = new MyPopupMenu(world);
            this.addMouseListener(this);

        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    public static void setHighlightUAV(int uav_index) {
        AnimationPanel.highlight_uav_index = uav_index;
        UAV highlight_uav = null;
        for (UAV attacker : attackers) {
            if (attacker.getIndex() == uav_index) {
                highlight_uav = attacker;
            }
        }
        if (highlight_uav != null) {
            RightControlPanel.setWorldKnowledge(highlight_uav.getKb());
        }
    }

    private BufferedImage createBufferedImage() {
        return new BufferedImage(bound_width, bound_height,
                BufferedImage.TYPE_INT_ARGB);
    }

    private void initParameterFromInitConfig(World world) {
        this.bound_width = world.getBound_width();
        this.bound_height = world.getBound_height();

        this.scouts = world.getScouts();
        this.attackers = world.getAttackers();
        this.enemy_uavs = world.getEnemy_uavs();
        this.threats = world.getThreats();
        this.obstacles = world.getObstacles();
        this.uav_base = world.getUav_base();

    }

    private void initObstaclesInObstacleImageInLevel2(ArrayList<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            virtualizer.drawObstacle(obstacle_image_graphics, obs, GraphicConfig.obstacle_center_color, GraphicConfig.obstacle_edge_color, null);
        }
    }

    private void updateHighlightObstacleImage(ArrayList<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            if (obs.getIndex() == AnimationPanel.highlight_obstacle_index) {
                virtualizer.highlightObstacle(highlight_obstacle_image_graphics, obs, GraphicConfig.obstacle_center_color, GraphicConfig.obstacle_edge_color, GraphicConfig.highlight_obstacle_color);
            }
        }
    }

    private void initTargetInObstacleImageLevel2(ArrayList<Threat> threats) {
        for (Threat threat : threats) {
            if (threat.getIndex() == AnimationPanel.highlight_threat_index) {
                virtualizer.drawTarget(obstacle_image_graphics, threat, GraphicConfig.threat_color, GraphicConfig.highlight_threat_color);
            }
            virtualizer.drawTarget(obstacle_image_graphics, threat, GraphicConfig.threat_color, null);
        }
    }

    private void initUAVBase(Graphics2D uav_image_graphics) {
        uav_image_graphics.setColor(Color.white);
        uav_image_graphics.setStroke(new BasicStroke(uav_base_line_width));
        uav_image_graphics.drawRect((int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width(), uav_base.getBase_height());
        uav_image_graphics.setColor(GraphicConfig.uav_base_color);
        uav_image_graphics.fillRect((int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width(), uav_base.getBase_height());
        uav_image_graphics.drawImage(uav_base.getImage(), (int) uav_base.getCoordinate()[0], (int) uav_base.getCoordinate()[1], uav_base.getBase_width() * 2 / 3, uav_base.getBase_height() * 2 / 3, null);
    }

    private void clearImageBeforeUpdate(Graphics2D graphics) {
        graphics.setColor(transparent_color);
        graphics.setBackground(transparent_color);
        graphics.clearRect(0, 0, bound_width, bound_height);
    }

    private void clearUAVImageBeforeUpdate() {
        clearImageBeforeUpdate(uav_image_graphics);
        clearImageBeforeUpdate(enemy_uav_image_graphics);
        clearImageBeforeUpdate(highlight_obstacle_image_graphics);
    }

    private void updateImageCausedByUAVMovement() {
        initUAVBase(uav_image_graphics);
        updateHighlightObstacleImage(obstacles);
        updateUAVImageInLevel4();
        updateFogOfWarImageInLevel3();
        updateUAVHistoryPath();
        showUAVPlannedPath();
//        showUAVPlannedTree();
    }

    private void updateUAVHistoryPath() {
        if (!StaticInitConfig.SHOW_HISTORY_PATH) {
            return;
        }
        for (UAV attacker : this.attackers) {
            virtualizer.drawUAVHistoryPath(uav_history_path_image_graphics, attacker, GraphicConfig.side_a_center_color);
        }
    }

    private void showUAVPlannedTree() {
        uav_planned_tree_image_graphics.setBackground(transparent_color);
        uav_planned_tree_image_graphics.setColor(transparent_color);
        uav_planned_tree_image_graphics.clearRect(0, 0, bound_width, bound_height);
        if (!StaticInitConfig.SHOW_PLANNED_TREE) {
            return;
        }
        for (UAV attacker : this.attackers) {
            virtualizer.drawUAVPlannedTree(uav_planned_tree_image_graphics, attacker, GraphicConfig.uav_planned_path_color);
        }
    }

    private void showUAVPlannedPath() {
        uav_planned_path_image_graphics.setBackground(transparent_color);
        uav_planned_path_image_graphics.setColor(transparent_color);
        uav_planned_path_image_graphics.clearRect(0, 0, bound_width, bound_height);
        if (!StaticInitConfig.SHOW_PLANNED_PATH && AnimationPanel.highlight_uav_index == -1) {
            return;
        } else if (!StaticInitConfig.SHOW_PLANNED_PATH && AnimationPanel.highlight_uav_index > 0) {
            for (UAV attacker : this.attackers) {
                if (attacker.getIndex() == AnimationPanel.highlight_uav_index) {
                    virtualizer.drawUAVPlannedPath(uav_planned_path_image_graphics, attacker, GraphicConfig.uav_planned_path_color);
                }
            }
            return;
        }
        for (UAV attacker : this.attackers) {
            virtualizer.drawUAVPlannedPath(uav_planned_path_image_graphics, attacker, GraphicConfig.uav_planned_path_color);
        }
    }

    private void updateUAVImageInLevel4() {
        for (UAV scout : this.scouts) {
            virtualizer.drawUAVInUAVImage(uav_image_graphics, scout, GraphicConfig.side_a_radar_color, GraphicConfig.side_a_center_color, null);
        }
        for (UAV attacker : this.attackers) {
            if (attacker.getIndex() == this.highlight_uav_index) {
                virtualizer.drawUAVInUAVImage(uav_image_graphics, attacker, GraphicConfig.side_a_radar_color, GraphicConfig.side_a_center_color, GraphicConfig.highlight_uav_color);
            }
            virtualizer.drawUAVInUAVImage(uav_image_graphics, attacker, GraphicConfig.side_a_radar_color, GraphicConfig.side_a_center_color, null);

        }
        for (UAV enemy_uav : this.enemy_uavs) {
            virtualizer.drawUAVInUAVImage(enemy_uav_image_graphics, enemy_uav, GraphicConfig.side_b_radar_color, GraphicConfig.side_b_center_color, null);
        }
    }

    private void updateFogOfWarImageInLevel3() {
        if (!StaticInitConfig.SHOW_FOG_OF_WAR) {
            return;
        }
        for (UAV scout : this.scouts) {
            virtualizer.drawScoutInFogOfWarInLevel3(fog_of_war_graphics, scout);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background_image_level_1, 0, 0, null);
        g.drawImage(obstacle_image_level_2, 0, 0, null);
        g.drawImage(this.highlight_obstacle_image_level_3, 0, 0, null);
        g.drawImage(enemy_uav_image_level_4, 0, 0, null);
        if (StaticInitConfig.SHOW_FOG_OF_WAR) {
            g.drawImage(fog_of_war_image_level_5, 0, 0, null);
        }
        if (StaticInitConfig.SHOW_HISTORY_PATH) {
            g.drawImage(uav_history_path_image_level_6, 0, 0, null);
        }
        if (StaticInitConfig.SHOW_PLANNED_TREE) {
            g.drawImage(uav_planned_tree_image_level_7, 0, 0, null);
        }
        g.drawImage(uav_planned_path_image_level_8, 0, 0, null);
        g.drawImage(uav_image_level_9, 0, 0, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        StaticInitConfig.SIMULATION_ON = true;
        int chosen_attacker_index = findChosenAttacker(e.getPoint());
        AnimationPanel.setHighlightUAV(chosen_attacker_index);
    }

    private int findChosenAttacker(Point mouse_point) {
        float[] mouse_point_coord = new float[]{(float) mouse_point.getX(), (float) mouse_point.getY()};
        for (UAV attacker : attackers) {
            float[] center_coord = attacker.getCenter_coordinates();
            float dist = DistanceUtil.distanceBetween(center_coord, mouse_point_coord);
            if (dist < attacker.getUav_radar().getRadius()) {
                return attacker.getIndex();
            }
        }
        return -1;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            int chosen_attacker_index = findChosenAttacker(e.getPoint());
            if(chosen_attacker_index==-1)
                return;
            AnimationPanel.setHighlightUAV(chosen_attacker_index);
            my_popup_menu.setChoosedAttackerIndex(chosen_attacker_index);
            my_popup_menu.show(this, e.getX(), e.getY());
            StaticInitConfig.SIMULATION_ON = false;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
//            logger.debug(panel.getSize().width + "-" + panel.getSize().height);
        }
    }
}
