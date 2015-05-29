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
package world;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.RectangleUtil;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
import world.model.KnowledgeInterface;
import world.model.Obstacle;
import world.model.Target;
import world.model.Threat;
import world.model.WorldKnowledge;
import world.uav.Attacker;

/**
 *
 * @author Yulin_Zhang
 */
public class RegisteredMessageDispatcher extends MessageDispatcher {

    private Map<Integer, Rectangle> gis_rect_registered;
    private Map<Integer, Integer> target_registered;

    public RegisteredMessageDispatcher(KnowledgeAwareInterface intelligent_unit) {
        super(intelligent_unit);
        gis_rect_registered = new HashMap<Integer, Rectangle>();
        target_registered = new HashMap<Integer, Integer>();
    }

    private void roleBasedRegister(Integer uav_index, Target target) {
        target_registered.put(uav_index, target.getIndex());
    }

    private void gisBasedRegister(Integer uav_index, float[] uav_loc, float[] target_loc) {
        Rectangle rect = RectangleUtil.findMBRRect(uav_loc, target_loc);
        this.gis_rect_registered.put(uav_index, rect);
    }

    @Override
    public void decideAndSumitMsgToSend() {
        List<Obstacle> obstacles = intelligent_unit.getObstacles();
        int obstacle_num = obstacles.size();
        List<Threat> threats = intelligent_unit.getThreats();
        int threat_num = threats.size();
//        List<Conflict> conflicts = intelligent_unit.getConflicts();

        List<Attacker> attackers = World.getAttackers();
        int attacker_num = attackers.size();
        for (int i = 0; i < attacker_num; i++) {
            Attacker attacker = attackers.get(i);
            if(!attacker.isVisible())
            {
                continue;
            }
            KnowledgeInterface kb = attacker.getKb();
            int attacker_index = attacker.getIndex();
            Integer threat_index = this.target_registered.get(attacker_index);
            if(threat_index==null)
            {
                continue;
            }
            for (int j = 0; j < threat_num; j++) {
                Threat threat = threats.get(j);
                if (threat.getIndex() == threat_index) {
                    this.addRecvMessage(i, threat);
                }
            }
            
            Rectangle gis_rect = this.gis_rect_registered.get(attacker_index);
            for (int j = 0; j < obstacle_num; j++) {
                Obstacle obstacle = obstacles.get(j);
                boolean gis_rect_covered = obstacle.getMbr().intersects(gis_rect);
                if (gis_rect_covered && !kb.containsObstacle(obstacle)) {
                    this.addRecvMessage(i, obstacle);
                }
            }

        }
    }

    @Override
    public void register(Integer uav_index, float[] current_loc, Target target) {
        this.roleBasedRegister(uav_index, target);
        this.gisBasedRegister(uav_index, current_loc, target.getCoordinates());
    }
}
