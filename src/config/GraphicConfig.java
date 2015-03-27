/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.awt.Color;

/**
 *
 * @author Yulin_Zhang
 */
public class GraphicConfig {

    public static Color obstacle_center_color = new Color(109, 209, 110);//Color.GREEN;
    public static Color obstacle_edge_color = Color.black;
    public static Color threat_color = new Color(109, 110, 209,150);
    public static Color side_a_center_color = new Color(250, 0, 0);
    public static Color side_a_radar_color = new Color(236, 128, 128, 150);
    public static Color side_b_center_color = new Color(0, 0, 250);
    public static Color side_b_radar_color = new Color(128, 128, 236, 150);
    public static Color transparent_color = new Color(0, 0, 0, 0);
    public static Color fog_of_war_color = transparent_color;//Color.black;;;//Color.black;
    public static Color uav_base_color = new Color(236, 128, 128,150);
    public static Color target_color = Color.blue;
    public static Color uav_planned_path_color=Color.black;

    public static int target_width = 20;
    public static int target_height = 20;
}
