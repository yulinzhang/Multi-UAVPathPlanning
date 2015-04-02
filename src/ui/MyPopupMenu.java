/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import config.StaticInitConfig;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import world.World;
import world.model.Threat;

/**
 *
 * @author boluo
 */
public class MyPopupMenu extends JPopupMenu implements ActionListener {

    ArrayList<JMenuItem> items;
    World world;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MyPopupMenu.class);

    public MyPopupMenu(World world) {
        super();
        this.world = world;
        items = new ArrayList<JMenuItem>();
        ArrayList<Threat> threats = world.getThreats();
        for (Threat threat : threats) {
            JMenuItem threat_item = new JMenuItem(StaticInitConfig.THREAT_NAME+threat.getIndex());
            items.add(threat_item);
            this.add(threat_item);
            threat_item.addActionListener(this);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = items.indexOf(e.getSource());
        logger.debug("choose:" + index);
        StaticInitConfig.SIMULATION_ON = true;
    }

}
