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
 * @author Yulin_Zhang
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
