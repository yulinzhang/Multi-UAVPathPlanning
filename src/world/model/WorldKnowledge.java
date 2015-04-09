/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import java.util.ArrayList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author boluo
 */
public class WorldKnowledge extends KnowledgeInterface {

    private ArrayList<Obstacle> obstacles;
    private ArrayList<Threat> threats;
    private ArrayList<Conflict> conflicts;

    private ArrayList<Object> root_child;
    private ArrayList<TreeModelListener> treeModelListeners
            = new ArrayList<TreeModelListener>();

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorldKnowledge.class);

    public WorldKnowledge() {
        root_child = new ArrayList<Object>();
        root_child.add(firstChild);
        root_child.add(secondChild);
        root_child.add(thirdChild);
        obstacles = new ArrayList<Obstacle>();
        threats = new ArrayList<Threat>();
        conflicts = new ArrayList<Conflict>();
//        init();
    }

    private void init() {
        Obstacle obstacle1 = new Obstacle(null, 0);
        Obstacle obstacle2 = new Obstacle(null, 1);
        obstacles.add(obstacle1);
        obstacles.add(obstacle2);

        Threat threat1 = new Threat(0, null, 1);
        Threat threat2 = new Threat(1, null, 1);
        threats.add(threat1);
        threats.add(threat2);

        Conflict conflict1 = new Conflict(0, null, 1, 2);
        Conflict conflict2 = new Conflict(1, null, 1, 2);
        conflicts.add(conflict1);
        conflicts.add(conflict2);

    }

    @Override
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    @Override
    public void setObstacles(ArrayList<Obstacle> obstacles) {
        if (obstacles == null) {
            return;
        }
        this.obstacles = obstacles;
        obstacle_num = obstacles.size();
    }

    @Override
    public ArrayList<Threat> getThreats() {
        return threats;
    }

    @Override
    public void setThreats(ArrayList<Threat> threats) {
        if (threats == null) {
            return;
        }
        this.threats = threats;
        threat_num = threats.size();
    }

    @Override
    public ArrayList<Conflict> getConflicts() {
        return conflicts;
    }

    @Override
    public void setConflicts(ArrayList<Conflict> conflicts) {
        if (conflicts == null) {
            return;
        }
        this.conflicts = conflicts;
        conflict_num = conflicts.size();
    }

    @Override
    public void addConflict(Conflict conflict) {
        if (conflict_num == 0) {
            this.conflicts.add(conflict);
            return;
        }
        for (int i = 0; i < conflict_num; i++) {
            Conflict temp_conflict = this.conflicts.get(i);
            if (temp_conflict.getUav_index() == conflict.getUav_index()) {
                this.conflicts.remove(i);
                this.conflicts.add(i, conflict);
                return;
            }
        }
        this.conflicts.add(conflict);
        conflict_num++;
    }

    @Override
    public void addThreat(Threat threat) {
        this.threats.add(threat);
        threat_num++;
    }

    @Override
    public boolean containsObstacle(Obstacle obstacle) {
        return this.obstacles.contains(obstacle);
    }

    @Override
    public boolean containsThreat(Threat threat) {
        return this.threats.contains(threat);
    }

    @Override
    public boolean containsConflict(Conflict conflict) {
        return this.conflicts.contains(conflict);
    }

    @Override
    public void addObstacle(Obstacle obstacle) {
        if (this.obstacles == null) {
            this.obstacles = new ArrayList<Obstacle>();
        }
        this.obstacles.add(obstacle);
        obstacle_num++;
    }

    @Override
    public boolean removeObstacle(Obstacle obstacle) {
        boolean result = this.obstacles.remove(obstacle);
        if (result) {
            obstacle_num--;
        }
        return result;
    }

    @Override
    public boolean removeThreat(Threat threat) {
        boolean result = this.threats.remove(threat);
        if (result) {
            threat_num--;
        }
        return result;
    }

    @Override
    public boolean removeConflict(Conflict conflict) {
        boolean result = this.conflicts.remove(conflict);
        if (result) {
            conflict_num--;
        }
        return result;
    }

}
