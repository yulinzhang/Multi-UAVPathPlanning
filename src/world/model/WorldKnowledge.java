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
package world.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.event.TreeModelListener;

/**
 *
 * @author Yulin_Zhang
 */
public class WorldKnowledge extends KnowledgeInterface {

    private ArrayList<Obstacle> obstacles;
    private ArrayList<Threat> threats;
    private ArrayList<Conflict> conflicts;

    private ArrayList<TreeModelListener> treeModelListeners
            = new ArrayList<TreeModelListener>();

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorldKnowledge.class);

    public WorldKnowledge() {
        super();
        obstacles = new ArrayList<Obstacle>();
        threats = new ArrayList<Threat>();
        conflicts = new ArrayList<Conflict>();
//        init();
    }

    /** Just used for unit test.
     * 
     */
    private void init() {
        Obstacle obstacle1 = new Obstacle(null, 0);
        Obstacle obstacle2 = new Obstacle(null, 1);
        obstacles.add(obstacle1);
        obstacles.add(obstacle2);

        Threat threat1 = new Threat(0, null, 1,2);
        Threat threat2 = new Threat(1, null, 1,4);
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
