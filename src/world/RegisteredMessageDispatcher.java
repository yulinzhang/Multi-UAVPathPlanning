/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
import world.model.KnowledgeInterface;
import world.model.Obstacle;
import world.model.Target;
import world.model.Threat;
import world.model.WorldKnowledge;
import world.uav.UAV;

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
        target_registered=new HashMap<Integer, Integer>();
    }

    private void roleBasedRegister(Integer uav_index, Target target) {
        target_registered.put(uav_index, target.getIndex());
    }

    private void gisBasedRegister(Integer uav_index, float[] uav_loc, float[] target_loc) {
        float[] rect_center = new float[2];
        rect_center[0] = (target_loc[0] + uav_loc[0]) / 2.0f;
        rect_center[1] = (target_loc[1] + uav_loc[1]) / 2.0f;
        int width = (int) Math.abs(target_loc[0] - uav_loc[0]);
        int height = (int) Math.abs(target_loc[1] - uav_loc[1]);
        Rectangle rect = new Rectangle((int) rect_center[0] - width / 2, (int) rect_center[0] - height / 2, width, height);
        this.gis_rect_registered.put(uav_index, rect);
    }

    @Override
    public void decideAndSumitMsgToSend() {
        List<Obstacle> obstacles = intelligent_unit.getObstacles();
        int obstacle_num = obstacles.size();
        List<Threat> threats = intelligent_unit.getThreats();
        int threat_num = threats.size();
        List<Conflict> conflicts = intelligent_unit.getConflicts();
        int conflict_num = conflicts.size();

        List<UAV> attackers = World.getAttackers();
        int attacker_num = attackers.size();
        for (int i = 0; i < attacker_num; i++) {
            UAV attacker = attackers.get(i);
            KnowledgeInterface kb = attacker.getKb();
            int attacker_index = attacker.getIndex();
            int threat_index = this.target_registered.get(attacker_index);
            Rectangle gis_rect = this.gis_rect_registered.get(attacker_index);

            for (int j = 0; j < threat_num; j++) {
                Threat threat = threats.get(j);
                if (threat.getIndex() == threat_index && !kb.containsThreat(threat)) {
                    this.addRecvMessage(i, threat);
                }
            }

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
