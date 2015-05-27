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
import world.ControlCenter;
import world.World;
import world.model.Threat;

/**
 *
 * @author boluo
 */
public class MyPopupMenu extends JPopupMenu implements ActionListener {

    ArrayList<JMenuItem> items;
    ControlCenter control_center;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MyPopupMenu.class);

    /** internal variable
     * 
     */
    private int choosen_attacker_index=-1;
    
    public MyPopupMenu(ControlCenter control_center) {
        super();
        this.control_center = control_center;
        items = new ArrayList<JMenuItem>();
        ArrayList<Threat> threats = this.control_center.getThreats();
        for (Threat threat : threats) {
            JMenuItem threat_item = new JMenuItem(StaticInitConfig.THREAT_NAME+threat.getIndex());
            items.add(threat_item);
            this.add(threat_item);
            threat_item.addActionListener(this);
        }

    }
    
    public void setChoosedAttackerIndex(int attacker_index)
    {
        this.choosen_attacker_index=attacker_index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int index = items.indexOf(e.getSource());
        control_center.roleAssignForAttackerWithSubTeam(choosen_attacker_index, index);
        StaticInitConfig.SIMULATION_ON = true;
    }

}
