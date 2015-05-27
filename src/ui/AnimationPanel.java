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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import world.uav.Attacker;
import world.uav.UAVBase;
import util.DistanceUtil;
import util.ImageUtil;
import world.ControlCenter;
import world.model.Obstacle;
import world.model.Threat;
import world.World;
import world.uav.Scout;
import world.uav.UAV;

/**
 *
 * @author boluo
 */
public class AnimationPanel extends JPanel implements MouseListener {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AnimationPanel.class);

    /**
     * -------------outside variable---------------
     */
    private int bound_width = 800; //The size of paint
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
    private BufferedImage uav_image_level_10;
    private BufferedImage highlight_obstacle_image_level_3;
    private BufferedImage threat_image_level_9;

    /**
     * Draw various shapes in the simulation
     */
    private Color transparent_color;
    private Graphics2D fog_of_war_graphics;
    private Graphics2D uav_image_graphics;
    private Graphics2D obstacle_image_graphics;
    private Graphics2D enemy_uav_image_graphics;
    private Graphics2D uav_history_path_image_graphics;
    private Graphics2D uav_planned_tree_image_graphics;
    private Graphics2D uav_planned_path_image_graphics;
    private Graphics2D highlight_obstacle_image_graphics;
    private Graphics2D threat_image_graphics;

    private MyGraphic virtualizer;

    private static ArrayList<Attacker> attackers;
    private ArrayList<Threat> threats_from_god_view;
    private ArrayList<Scout> scouts;
    private UAVBase uav_base;
    private ControlCenter control_center;

    public static int highlight_uav_index = -1;
    public static int highlight_obstacle_index = -1;
    public static int highlight_threat_index = -1;

    private long simulation_time_in_milli_seconds = 0;

    private MyPopupMenu my_popup_menu;

    public AnimationPanel() {
        initComponents();
    }

    public void initComponents() {
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

            threat_image_level_9 = createBufferedImage();
            threat_image_graphics = threat_image_level_9.createGraphics();

            //initiate fog_of_war image
            fog_of_war_image_level_5 = createBufferedImage();
            fog_of_war_graphics = fog_of_war_image_level_5.createGraphics();
            fog_of_war_graphics.setBackground(fog_of_war_color);
            fog_of_war_graphics.setColor(fog_of_war_color);
            fog_of_war_graphics.fillRect(0, 0, bound_width, bound_height);

            //initiate uav_image
            uav_image_level_10 = createBufferedImage();
            uav_image_graphics = uav_image_level_10.createGraphics();
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
            this.control_center = world.getControl_center();
            this.uav_base = world.getUav_base();
            this.threats_from_god_view = world.getThreatsForUIRendering();

            //initiate obstacles in level 2
            this.initObstaclesInObstacleImageInLevel2(world.getObstaclesForUIRendering());
//            this.updateTargetInUAVImageLevel(world.getThreatsForUIRendering());

            this.initFogOfWarImage();

            //initiate parameters according to world
            this.initParameterFromInitConfig(world);

            my_popup_menu = new MyPopupMenu(world.getControl_center());
            this.addMouseListener(this);

        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    public void start() {
        //drive the world and ui
        StaticInitConfig.SIMULATION_WITH_UI_TIMER = new javax.swing.Timer((int) (StaticInitConfig.INIT_SIMULATION_DELAY / StaticInitConfig.SIMULATION_SPEED), new animatorListener(this));
        StaticInitConfig.SIMULATION_WITH_UI_TIMER.start();
//        this.runTask();
    }

    public void runTask() {

        Thread t = new Thread() {
            public void run() {
                while (true) {
                    world.updateAll();
                    try {
                        Thread.sleep((int) (StaticInitConfig.INIT_SIMULATION_DELAY / StaticInitConfig.SIMULATION_SPEED));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AnimationPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        t.start();
    }

    public static void setHighlightUAV(int uav_index) {
        AnimationPanel.highlight_uav_index = uav_index;
        Attacker highlight_uav = null;
        for (Attacker attacker : AnimationPanel.attackers) {
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

    /**
     * Initiate parameter of world
     *
     * @param world
     */
    private void initParameterFromInitConfig(World world) {
        this.bound_width = world.getBound_width();
        this.bound_height = world.getBound_height();

        this.scouts = world.getScouts();
        this.attackers = world.getAttackers();
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

    /**
     * Initiate the parameters of Attacker
     *
     * @param uav_image_graphics
     */
    private void initUAVBase(Graphics2D uav_image_graphics) {
        virtualizer.drawUAVBase(uav_image_graphics, uav_base);
    }

    /**
     * each undate will clear history image
     */
    private void clearImageBeforeUpdate(Graphics2D graphics) {
        graphics.setColor(transparent_color);
        graphics.setBackground(transparent_color);
        graphics.clearRect(0, 0, bound_width, bound_height);
    }

    private void clearUAVImageBeforeUpdate() {
        clearImageBeforeUpdate(uav_image_graphics);
        clearImageBeforeUpdate(enemy_uav_image_graphics);
        clearImageBeforeUpdate(highlight_obstacle_image_graphics);
        clearImageBeforeUpdate(threat_image_graphics);
    }

    private void updateImageCausedByUAVMovement() {
        initUAVBase(uav_image_graphics);
//        updateTargetInUAVImageLevel(world.getThreatsForUIRendering());
        updateThreatImage();
        updateHighlightObstacleImage(world.getObstaclesForUIRendering());
        updateUAVImageInLevel4();
        updateFogOfWarImageInLevel3();
        updateUAVHistoryPath();
        showUAVPlannedPath();
//        showUAVPlannedTree();
    }

    private void updateThreatImage() {
        ArrayList<Threat> threats = control_center.getThreats();
        for (Threat threat : threats) {
            if (!threat.isEnabled()) {
                continue;
            }
            if (threat.getIndex() == AnimationPanel.highlight_threat_index) {
                virtualizer.drawThreat(threat_image_graphics, threat, GraphicConfig.threat_color, GraphicConfig.highlight_threat_color);
            } else {
                virtualizer.drawThreat(threat_image_graphics, threat, GraphicConfig.threat_color, null);
            }
            int threat_index=threat.getIndex();
            if (this.threats_from_god_view.get(threat_index).getMode() == Threat.LOCKED_MODE) {
                virtualizer.drawCombatSymbol(threat_image_graphics, threat.getCoordinates(), Threat.threat_width * 3 / 2, Color.red);
            }
        }
    }

    private void updateUAVHistoryPath() {
        if (!StaticInitConfig.SHOW_HISTORY_PATH) {
            return;
        }
        for (Attacker attacker : this.attackers) {
            if (attacker.isVisible() && attacker.getTarget_indicated_by_role() != null) {
                virtualizer.drawUAVHistoryPath(uav_history_path_image_graphics, attacker, GraphicConfig.side_a_center_color);
            }
        }
    }

    private void showUAVPlannedTree() {
        uav_planned_tree_image_graphics.setBackground(transparent_color);
        uav_planned_tree_image_graphics.setColor(transparent_color);
        uav_planned_tree_image_graphics.clearRect(0, 0, bound_width, bound_height);
        if (!StaticInitConfig.SHOW_PLANNED_TREE) {
            return;
        }
        for (Attacker attacker : this.attackers) {
            if (attacker.getTarget_indicated_by_role() != null) {
                virtualizer.drawUAVPlannedTree(uav_planned_tree_image_graphics, attacker, GraphicConfig.uav_planned_path_color);
            }
        }
    }

    private void showUAVPlannedPath() {
        uav_planned_path_image_graphics.setBackground(transparent_color);
        uav_planned_path_image_graphics.setColor(transparent_color);
        uav_planned_path_image_graphics.clearRect(0, 0, bound_width, bound_height);
        if (!StaticInitConfig.SHOW_PLANNED_PATH && AnimationPanel.highlight_uav_index == -1) {
            return;
        } else if (!StaticInitConfig.SHOW_PLANNED_PATH && AnimationPanel.highlight_uav_index > 0) {
            for (Attacker attacker : this.attackers) {
                if (attacker.getIndex() == AnimationPanel.highlight_uav_index) {
                    virtualizer.drawUAVPlannedPath(uav_planned_path_image_graphics, attacker, GraphicConfig.uav_planned_path_color);
                }
            }
            return;
        }
        for (Attacker attacker : this.attackers) {
            if (attacker.getTarget_indicated_by_role() != null) {
                virtualizer.drawUAVPlannedPath(uav_planned_path_image_graphics, attacker, GraphicConfig.uav_planned_path_color);
            }
        }
    }

    private void updateUAVImageInLevel4() {
        for (Scout scout : this.scouts) {
            virtualizer.drawUAVInUAVImage(uav_image_graphics, this.uav_base, scout, null);
        }
        for (Attacker attacker : this.attackers) {
            if (attacker.getIndex() == this.highlight_uav_index) {
                virtualizer.drawUAVInUAVImage(uav_image_graphics, this.uav_base, attacker, GraphicConfig.highlight_uav_color);
            }
            virtualizer.drawUAVInUAVImage(uav_image_graphics, this.uav_base, attacker, null);

        }
    }

    private void initFogOfWarImage() {
        virtualizer.drawUAVBaseInFogOfWar(fog_of_war_graphics, this.uav_base);
    }

    private void updateFogOfWarImageInLevel3() {
        if (!StaticInitConfig.SHOW_FOG_OF_WAR) {
            return;
        }
        for (Attacker attcker : AnimationPanel.attackers) {
            virtualizer.drawUAVInFogOfWarInLevel3(fog_of_war_graphics, (UAV) attcker);
        }
        for (Scout scout : this.scouts) {
            virtualizer.drawUAVInFogOfWarInLevel3(fog_of_war_graphics, (UAV) scout);
        }
        ArrayList<Obstacle> obstacles = this.control_center.getObstacles();
        for (Obstacle obstacle : obstacles) {
            virtualizer.showObstacleInFogOfWar(fog_of_war_graphics, obstacle);
        }
        ArrayList<Threat> threats = this.control_center.getThreats();
        for (Threat threat : threats) {
            virtualizer.showThreatInFogOfWar(fog_of_war_graphics, threat);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background_image_level_1, 0, 0, null);
        g.drawImage(obstacle_image_level_2, 0, 0, null);
        g.drawImage(this.highlight_obstacle_image_level_3, 0, 0, null);
        g.drawImage(this.threat_image_level_9, 0, 0, null);
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
        g.drawImage(uav_image_level_10, 0, 0, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        StaticInitConfig.SIMULATION_ON = true;
        int chosen_attacker_index = findChosenAttacker(e.getPoint());
        AnimationPanel.setHighlightUAV(chosen_attacker_index);
    }

    private int findChosenAttacker(Point mouse_point) {
        float[] mouse_point_coord = new float[]{(float) mouse_point.getX(), (float) mouse_point.getY()};
        for (Attacker attacker : attackers) {
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
        StaticInitConfig.SIMULATION_ON = false;
        if (e.getButton() == MouseEvent.BUTTON3) {
            int chosen_attacker_index = findChosenAttacker(e.getPoint());
            if (chosen_attacker_index == -1) {
                return;
            }
            AnimationPanel.setHighlightUAV(chosen_attacker_index);
            my_popup_menu.setChoosedAttackerIndex(chosen_attacker_index);
            my_popup_menu.show(this, e.getX(), e.getY());
            StaticInitConfig.SIMULATION_ON = false;
        }
        StaticInitConfig.SIMULATION_ON = true;
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

    /**
     * implements ActionListener interface for receiving action events
     */
    private class animatorListener implements ActionListener {

        JPanel panel;

        public animatorListener(JPanel panel) {
            this.panel = panel;
        }

        /**
         * real-time undates of all simulation objects
         *
         * @param e
         */
        public void actionPerformed(ActionEvent e) {
            clearUAVImageBeforeUpdate();
            if (StaticInitConfig.SIMULATION_ON) {
                world.updateAll();
                simulation_time_in_milli_seconds += StaticInitConfig.INIT_SIMULATION_DELAY;
                long seconds = simulation_time_in_milli_seconds / 1000;
                long hours = seconds / 3600;
                long minimutes = (seconds - hours * 3600) / 60;
                seconds = seconds - hours * 3600 - minimutes * 60;
                String simulated_time_str = String.format("%1$02d:%2$02d:%3$02d", hours, minimutes, seconds);
                ControlPanel.jFormattedTextField1.setText(simulated_time_str);
                ControlPanel.setTotalHistoryPathLen(world.getTotal_path_len());
            }
            updateImageCausedByUAVMovement();
            repaint();
            if (world.isExperiment_over()) {
                System.exit(0);
            }

//            logger.debug(panel.getSize().width + "-" + panel.getSize().height);
        }
    }
}
