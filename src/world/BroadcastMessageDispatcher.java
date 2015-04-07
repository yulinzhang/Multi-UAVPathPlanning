/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.util.List;
import world.model.Conflict;
import world.model.Obstacle;
import world.model.Target;
import world.model.Threat;
import world.model.WorldKnowledge;
import world.uav.UAV;

/**
 *
 * @author Yulin_Zhang
 */
public class BroadcastMessageDispatcher extends MessageDispatcher {

    public BroadcastMessageDispatcher(World world) {
        super(world);
    }

    @Override
    public void decideAndSumitMsgToSend() {
        List<Obstacle> obstacles = world.getObstacles();
        int obstacle_num = obstacles.size();
        List<Threat> threats=world.getThreats();
        int threat_num=threats.size();
        List<Conflict> conflicts=world.getConflicts();
        int conflict_num=conflicts.size();

        List<UAV> attackers = world.getAttackers();
        int attacker_num = attackers.size();
        for (int i = 0; i < attacker_num; i++) {
            UAV attacker = attackers.get(i);
            WorldKnowledge kb = attacker.getKb();
            
            for (int j = 0; j < obstacle_num; j++) {
                Obstacle obstacle = obstacles.get(j);
                if (!kb.containsObstacle(obstacle)) {
                    this.addRecvMessage(i, obstacle);
                }
            }
            
            for(int j=0;j<threat_num;j++)
            {
                Threat threat=threats.get(j);
                if(!kb.containsThreat(threat))
                {
                    this.addRecvMessage(i, threat);
                }
            }
            
            for(int j=0;j<conflict_num;j++)
            {
                Conflict conflict=conflicts.get(j);
                if(!kb.containsConflict(conflict))
                {
                    this.addRecvMessage(i, conflict);
                }
            }
        }
    }

    @Override
    public void register(Integer uav_index, float[] current_loc, Target target) {
    }

}
