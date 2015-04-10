/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.util.ArrayList;
import java.util.TreeSet;
import util.DistanceUtil;
import world.model.Conflict;
import world.model.KnowledgeAwareInterface;
import world.model.KnowledgeInterface;
import world.model.Obstacle;
import world.model.Threat;
import world.uav.UAV;
import world.uav.Unit;

/**
 *
 * @author boluo
 */
public class ControlCenter implements KnowledgeAwareInterface {

    private KnowledgeInterface kb;
    private ArrayList<UAV> attackers;
    int attacker_num = -1;

    public ControlCenter(KnowledgeInterface kb, ArrayList<UAV> attackers) {
        this.kb = kb;
        this.attackers = attackers;
        this.attacker_num = attacker_num;
    }

    /**
     * assign role for uavs. Special case: attacker i should be assigned with
     * role j. Other role should be assigned to the nearest uav
     *
     * @param attacker_index
     * @param uav_assigned_role_index
     */
    public void roleAssign(int attacker_index, int uav_assigned_role_index) {
        TreeSet<Integer> assigned_attacker = new TreeSet<Integer>();
        assigned_attacker.add(attacker_index);
        ArrayList<Threat> threats = kb.getThreats();
        int threat_num = threats.size();
        for (int i = 0; i < threat_num; i++) {
            Threat threat = threats.get(i);
            if (!threat.isEnabled()) {
                continue;
            }
            if (threat.getIndex() == uav_assigned_role_index) {
                for (int j = 0; j < this.attacker_num; j++) {
                    if (attacker_index == j) {
                        this.attackers.get(j).setTarget_indicated_by_role(threat);
                        break;
                    }
                }
                continue;
            }
            float min_dist = Float.MAX_VALUE;
            int attacker_index_to_assign = -1;
            UAV attacker_to_assign = null;
            for (int j = 0; j < this.attacker_num; j++) {
                UAV current_attacker = this.attackers.get(j);
                if (assigned_attacker.contains(j)) {
                    continue;
                }

                float dist_between_uav_and_role = DistanceUtil.distanceBetween(current_attacker.getCenter_coordinates(), threat.getCoordinates());
                if (dist_between_uav_and_role < min_dist) {
                    min_dist = dist_between_uav_and_role;
                    attacker_index_to_assign = j;
                    attacker_to_assign = current_attacker;
                }
            }
            if (attacker_index_to_assign != -1) {
                assigned_attacker.add(attacker_index_to_assign);
                attacker_to_assign.setTarget_indicated_by_role(threat);
            }
        }
    }

    @Override
    public ArrayList<Obstacle> getObstacles() {
        return kb.getObstacles();
    }

    @Override
    public ArrayList<Conflict> getConflicts() {
        return kb.getConflicts();
    }

    public ArrayList<UAV> getAttackers() {
        return attackers;
    }

    public void setAttackers(ArrayList<UAV> attackers) {
        this.attackers = attackers;
    }

    @Override
    public ArrayList<Threat> getThreats() {
        return kb.getThreats();
    }

    @Override
    public void setObstacles(ArrayList<Obstacle> obstacles) {
        kb.setObstacles(obstacles);
    }

    @Override
    public void setConflicts(ArrayList<Conflict> conflicts) {
        kb.setConflicts(conflicts);
    }

    @Override
    public void setThreats(ArrayList<Threat> threats) {
        kb.setThreats(threats);
    }

    @Override
    public void addObstacle(Obstacle obs) {
        kb.addObstacle(obs);
    }

    @Override
    public void addConflict(Conflict conflict) {
        kb.addConflict(conflict);
    }

    @Override
    public void addThreat(Threat threat) {
        kb.addThreat(threat);
    }

    @Override
    public boolean containsThreat(Threat threat) {
        return kb.containsThreat(threat);
    }

    @Override
    public boolean containsObstacle(Obstacle obstacle) {
        return kb.containsObstacle(obstacle);
    }

}
