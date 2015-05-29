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
package config;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Yulin_Zhang
 */
public class GraphicConfig {

    public static Color obstacle_center_color = new Color(109, 209, 110);//Color.GREEN;
    public static Color obstacle_edge_color = Color.black;
    public static Color side_a_center_color = new Color(250, 0, 0);
    public static Color side_a_radar_color = new Color(236, 128, 128, 150);
    public static Color side_b_center_color = new Color(0, 0, 250);
    public static Color side_b_radar_color = new Color(128, 128, 236, 150);
    public static Color transparent_color = new Color(0, 0, 0, 0);
    public static Color threat_color = Color.blue;
    public static Color fog_of_war_color = new Color(0,0,0,220);//Color.black;;//Color.black;;;//Color.black;
    public static Color uav_base_color = new Color(236, 128, 128, 150);
    public static Color uav_planned_path_color = Color.black;

    public static Color highlight_uav_color = Color.white;
    public static Color highlight_obstacle_color = Color.white;
    public static Color highlight_threat_color = Color.white;



    public static ArrayList<Color> uav_colors = new ArrayList<Color>();

    static {
        uav_colors.add(Color.CYAN);
//        uav_colors.add(Color.GRAY);
        uav_colors.add(Color.MAGENTA);
        uav_colors.add(Color.ORANGE);
        uav_colors.add(Color.PINK);
        uav_colors.add(Color.RED);
        uav_colors.add(Color.decode("#FF00FF"));
        uav_colors.add(Color.decode("#00FFFF"));
        uav_colors.add(Color.decode("#FFCCCC"));
        uav_colors.add(Color.decode("#FFFFCC"));
        uav_colors.add(Color.decode("#CCFFCC"));
        uav_colors.add(Color.decode("#CCFFFF"));
        uav_colors.add(Color.decode("#CCE5FF"));
        uav_colors.add(Color.decode("#E5CCFF"));
        uav_colors.add(Color.decode("#FFCCE5"));
        uav_colors.add(Color.decode("#FFB266"));
        uav_colors.add(Color.decode("#6666FF"));
        uav_colors.add(Color.decode("#99004C"));
        uav_colors.add(Color.decode("#4C0099"));
        uav_colors.add(Color.decode("#009999"));
        uav_colors.add(Color.decode("#009900"));
        uav_colors.add(Color.decode("#994C00"));
        uav_colors.add(Color.decode("#FF0000"));
        uav_colors.add(Color.decode("#FF8000"));
        uav_colors.add(Color.decode("#FFFF00"));
        uav_colors.add(Color.decode("#80FF00"));
        uav_colors.add(Color.CYAN);
//        uav_colors.add(Color.GRAY);
        uav_colors.add(Color.MAGENTA);
        uav_colors.add(Color.ORANGE);
        uav_colors.add(Color.PINK);
        uav_colors.add(Color.RED);
        uav_colors.add(Color.decode("#FF00FF"));
        uav_colors.add(Color.decode("#00FFFF"));
        uav_colors.add(Color.decode("#FFCCCC"));
        uav_colors.add(Color.decode("#FFFFCC"));
        uav_colors.add(Color.decode("#CCFFCC"));
        uav_colors.add(Color.decode("#CCFFFF"));
        uav_colors.add(Color.decode("#CCE5FF"));
        uav_colors.add(Color.decode("#E5CCFF"));
        uav_colors.add(Color.decode("#FFCCE5"));
        uav_colors.add(Color.decode("#FFB266"));
        uav_colors.add(Color.decode("#6666FF"));
        uav_colors.add(Color.decode("#99004C"));
        uav_colors.add(Color.decode("#4C0099"));
        uav_colors.add(Color.decode("#009999"));
        uav_colors.add(Color.decode("#009900"));
        uav_colors.add(Color.decode("#994C00"));
        uav_colors.add(Color.decode("#FF0000"));
        uav_colors.add(Color.decode("#FF8000"));
        uav_colors.add(Color.decode("#FFFF00"));
        uav_colors.add(Color.decode("#80FF00"));
                uav_colors.add(Color.CYAN);
//        uav_colors.add(Color.GRAY);
        uav_colors.add(Color.MAGENTA);
        uav_colors.add(Color.ORANGE);
        uav_colors.add(Color.PINK);
        uav_colors.add(Color.RED);
        uav_colors.add(Color.decode("#FF00FF"));
        uav_colors.add(Color.decode("#00FFFF"));
        uav_colors.add(Color.decode("#FFCCCC"));
        uav_colors.add(Color.decode("#FFFFCC"));
        uav_colors.add(Color.decode("#CCFFCC"));
        uav_colors.add(Color.decode("#CCFFFF"));
        uav_colors.add(Color.decode("#CCE5FF"));
        uav_colors.add(Color.decode("#E5CCFF"));
        uav_colors.add(Color.decode("#FFCCE5"));
        uav_colors.add(Color.decode("#FFB266"));
        uav_colors.add(Color.decode("#6666FF"));
        uav_colors.add(Color.decode("#99004C"));
        uav_colors.add(Color.decode("#4C0099"));
        uav_colors.add(Color.decode("#009999"));
        uav_colors.add(Color.decode("#009900"));
        uav_colors.add(Color.decode("#994C00"));
        uav_colors.add(Color.decode("#FF0000"));
        uav_colors.add(Color.decode("#FF8000"));
        uav_colors.add(Color.decode("#FFFF00"));
        uav_colors.add(Color.decode("#80FF00"));
                uav_colors.add(Color.CYAN);
//        uav_colors.add(Color.GRAY);
        uav_colors.add(Color.MAGENTA);
        uav_colors.add(Color.ORANGE);
        uav_colors.add(Color.PINK);
        uav_colors.add(Color.RED);
        uav_colors.add(Color.decode("#FF00FF"));
        uav_colors.add(Color.decode("#00FFFF"));
        uav_colors.add(Color.decode("#FFCCCC"));
        uav_colors.add(Color.decode("#FFFFCC"));
        uav_colors.add(Color.decode("#CCFFCC"));
        uav_colors.add(Color.decode("#CCFFFF"));
        uav_colors.add(Color.decode("#CCE5FF"));
        uav_colors.add(Color.decode("#E5CCFF"));
        uav_colors.add(Color.decode("#FFCCE5"));
        uav_colors.add(Color.decode("#FFB266"));
        uav_colors.add(Color.decode("#6666FF"));
        uav_colors.add(Color.decode("#99004C"));
        uav_colors.add(Color.decode("#4C0099"));
        uav_colors.add(Color.decode("#009999"));
        uav_colors.add(Color.decode("#009900"));
        uav_colors.add(Color.decode("#994C00"));
        uav_colors.add(Color.decode("#FF0000"));
        uav_colors.add(Color.decode("#FF8000"));
        uav_colors.add(Color.decode("#FFFF00"));
        uav_colors.add(Color.decode("#80FF00"));
    }
}
