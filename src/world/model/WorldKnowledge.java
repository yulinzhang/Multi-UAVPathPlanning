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

    private String rootNode = StaticInitConfig.UAV_KNOWLEDGE;
    private String firstChild = StaticInitConfig.OBSTACLE_INFO;
    private String secondChild = StaticInitConfig.THREAT_INFO;
    private String thirdChild = StaticInitConfig.CONFLICT_INFO;

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

        Conflict conflict1 = new Conflict(0, null, 1,2);
        Conflict conflict2 = new Conflict(1, null, 1,2);
        conflicts.add(conflict1);
        conflicts.add(conflict2);

    }

    @Override
    public Object getRoot() {
        return rootNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == rootNode) {
            return root_child.get(index);
        } else if (root_child.contains(parent)) {//parent is in second level
            if (parent == firstChild) {
                return obstacles.get(index);
            } else if (parent == secondChild) {
                return threats.get(index);
            } else if (parent == thirdChild) {
                return conflicts.get(index);
            }
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == rootNode) {
            return 3;
        } else if (root_child.contains(parent)) {//parent is in second level
            if (parent == firstChild) {
                return obstacles.size();
            } else if (parent == secondChild) {
                return threats.size();
            } else if (parent == thirdChild) {
                return conflicts.size();
            }
        }
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node == rootNode || root_child.contains(node)) {
            return false;
        }
        return true;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == rootNode) {
            return root_child.indexOf(child);
        } else if (root_child.contains(parent)) {//parent is in second level
            if (parent == firstChild) {
                return obstacles.indexOf(child);
            } else if (parent == secondChild) {
                return threats.indexOf(child);
            } else if (parent == thirdChild) {
                return conflicts.indexOf(child);
            }
        }
        return -1;
    }
// Misc methods

    @Override
    public boolean deleteComponent(TreePath path, Object leaf_node) {
        boolean result = false;
        if (obstacles.contains(leaf_node)) {
            result = obstacles.remove(leaf_node);
            obstacle_num--;
        } else if (threats.contains(leaf_node)) {
            result = threats.remove(leaf_node);
            threat_num--;
        } else if (conflicts.contains(leaf_node)) {
            result = conflicts.remove(leaf_node);
            conflict_num--;
        }
        this.fireStructureChanged(path);
        return result;
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
        obstacle_num=obstacles.size();
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
        threat_num=threats.size();
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
        conflict_num=conflicts.size();
    }

    @Override
    public void addConflict(Conflict conflict) {
        if(conflict_num==0)
        {
            this.conflicts.add(conflict);
            return;
        }
        for(int i=0;i<conflict_num;i++)
        {
            Conflict temp_conflict=this.conflicts.get(i);
            if(temp_conflict.getUav_index()==conflict.getUav_index())
            {
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
    public boolean containsConflict(Conflict conflict)
    {
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

}
