/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import java.util.ArrayList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author boluo
 */
public class WorldKnowledge implements TreeModel {

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

        Conflict conflict1 = new Conflict(0, null, 1);
        Conflict conflict2 = new Conflict(1, null, 1);
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

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        if (obstacles == null) {
            return;
        }
        this.obstacles = obstacles;
    }

    public ArrayList<Threat> getThreats() {
        return threats;
    }

    public void setThreats(ArrayList<Threat> threats) {
        if (threats == null) {
            return;
        }
        this.threats = threats;
    }

    public ArrayList<Object> getRoot_child() {
        return root_child;
    }

    public void setRoot_child(ArrayList<Object> root_child) {
        this.root_child = root_child;
    }

    public ArrayList<Conflict> getConflicts() {
        return conflicts;
    }

    public void setConflicts(ArrayList<Conflict> conflicts) {
        if (conflicts == null) {
            return;
        }
        this.conflicts = conflicts;
    }

    public void addConflict(Conflict conflict) {
        this.conflicts.add(conflict);
    }

    public void addThreat(Threat threat) {
        this.threats.add(threat);
    }

    public boolean containsObstacle(Obstacle obstacle)
    {
        return this.obstacles.contains(obstacle);
    }
    public boolean containsThreat(Threat threat)
    {
        return this.threats.contains(threat);
    }
    
    public void addObstacle(Obstacle obstacle) {
        if (this.obstacles == null) {
            this.obstacles = new ArrayList<Obstacle>();
        }
        this.obstacles.add(obstacle);
    }

}
