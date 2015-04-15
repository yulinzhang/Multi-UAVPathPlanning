/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.Rectangle;
import java.util.List;
import util.RectangleUtil;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
import world.model.KnowledgeInterface;
import world.model.Obstacle;
import world.model.Target;
import world.model.Threat;
import world.uav.Attacker;

/**
 *
 * @author Yulin_Zhang
 */
public class BroadcastMessageDispatcher extends MessageDispatcher {

    public BroadcastMessageDispatcher(KnowledgeAwareInterface intelligent_unit) {
        super(intelligent_unit);
    }

    @Override
    public void decideAndSumitMsgToSend() {
        List<Obstacle> obstacles = intelligent_unit.getObstacles();
        int obstacle_num = obstacles.size();
        List<Threat> threats = intelligent_unit.getThreats();
        int threat_num = threats.size();
        List<Conflict> conflicts = intelligent_unit.getConflicts();
        int conflict_num = conflicts.size();

        List<Attacker> attackers = World.getAttackers();
        int attacker_num = attackers.size();
        for (int i = 0; i < attacker_num; i++) {
            Attacker attacker = attackers.get(i);
            KnowledgeInterface kb = attacker.getKb();
            Rectangle attacker_rect = null;
            Target attacker_target=attacker.getTarget_indicated_by_role();
            if ( attacker_target!= null) {
                attacker_rect = RectangleUtil.findMBRRect(attacker.getCenter_coordinates(), attacker_target.getCoordinates());
            }

            for (int j = 0; j < obstacle_num; j++) {
                Obstacle obstacle = obstacles.get(j);
                if (!kb.containsObstacle(obstacle)) {
                    super.addRecvMessage(i, obstacle);
                }
            }

            for (int j = 0; j < threat_num; j++) {
                Threat threat = threats.get(j);
//                if (!kb.containsThreat(threat)) {
                    this.addRecvMessage(i, threat);
//                }
            }
            
            if(attacker_rect==null)
            {
                continue;
            }
            for (int j = 0; j < conflict_num; j++) {
                Conflict conflict = conflicts.get(j);
                int uav_index = conflict.getUav_index();
                Attacker conflict_uav = attackers.get(uav_index);
                Target conflict_uav_target = conflict_uav.getTarget_indicated_by_role();
                if (conflict_uav_target != null && uav_index!=attacker.getIndex()) {
                    Rectangle conflict_uav_rect = RectangleUtil.findMBRRect(conflict_uav.getCenter_coordinates(), conflict_uav_target.getCoordinates());
                    if (attacker_rect.intersects(conflict_uav_rect)) {
                        super.addRecvMessage(i, conflict);
                    }
                }

            }
        }
    }

    @Override
    public void register(Integer uav_index, float[] current_loc, Target target) {
    }

}
