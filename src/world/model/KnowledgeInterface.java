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
public abstract class KnowledgeInterface implements TreeModel {

    protected final String rootNode = StaticInitConfig.UAV_KNOWLEDGE;
    protected final String firstChild = StaticInitConfig.OBSTACLE_INFO;
    protected final String secondChild = StaticInitConfig.THREAT_INFO;
    protected final String thirdChild = StaticInitConfig.CONFLICT_INFO;

    protected int obstacle_num;
    protected int threat_num;
    protected int conflict_num;

    protected ArrayList<Object> root_child;
    protected final ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

    public KnowledgeInterface() {
        root_child = new ArrayList<Object>();
        root_child.add(firstChild);
        root_child.add(secondChild);
        root_child.add(thirdChild);
    }

    /**
     * Notifies the listener that the structure below a given node has been
     * completely changed.
     *
     * @param path the sequence of nodes that lead up the tree to the root node.
     */
    public void fireStructureChanged(TreePath path) {
        TreeModelEvent event = new TreeModelEvent(this, path);
        for (int i = 0; i < treeModelListeners.size(); i++) {
            TreeModelListener lis = treeModelListeners.get(i);
            lis.treeStructureChanged(event);
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
    }

    @Override
    public Object getRoot() {
        return rootNode;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == rootNode) {
            return root_child.indexOf(child);
        } else if (root_child.contains(parent)) {//parent is in second level
            if (parent == firstChild) {
                return this.getObstacles().indexOf(child);
            } else if (parent == secondChild) {
                return this.getThreats().indexOf(child);
            } else if (parent == thirdChild) {
                return this.getConflicts().indexOf(child);
            }
        }
        return -1;
    }

    public boolean deleteComponent(TreePath path, Object leaf_node) {
        boolean result = false;
        if (this.containsObstacle((Obstacle) leaf_node)) {
            result = this.removeObstacle((Obstacle) leaf_node);
        } else if (this.containsThreat((Threat) leaf_node)) {
            result = this.removeThreat((Threat) leaf_node);
        } else if (this.containsConflict((Conflict) leaf_node)) {
            result = this.removeConflict((Conflict) leaf_node);
        }
        this.fireStructureChanged(path);
        return result;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (node == rootNode || root_child.contains(node)) {
            return false;
        }
        return true;
    }
    
        @Override
    public int getChildCount(Object parent) {
        if (parent == rootNode) {
            return 3;
        } else if (root_child.contains(parent)) {//parent is in second level
            if (parent == firstChild) {
                return this.obstacle_num;
            } else if (parent == secondChild) {
                return this.threat_num;
            } else if (parent == thirdChild) {
                return this.conflict_num;
            }
        }
        return 0;
    }
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
    
       @Override
    public Object getChild(Object parent, int index) {
        if (parent == rootNode) {
            return root_child.get(index);
        } else if (root_child.contains(parent)) {//parent is in second level
            if (parent == firstChild) {
                return this.getObstacles().get(index);
            } else if (parent == secondChild) {
                return this.getThreats().get(index);
            } else if (parent == thirdChild) {
                return this.getConflicts().get(index);
            }
        }
        return null;
    }
    
    public abstract boolean removeObstacle(Obstacle obstacle);

    public abstract boolean removeThreat(Threat threat);

    public abstract boolean removeConflict(Conflict conflict);

    public abstract ArrayList<Obstacle> getObstacles();

    public abstract void setObstacles(ArrayList<Obstacle> obstacles);

    public abstract ArrayList<Threat> getThreats();

    public abstract void setThreats(ArrayList<Threat> threats);

    public abstract ArrayList<Conflict> getConflicts();

    public abstract void setConflicts(ArrayList<Conflict> conflicts);

    public abstract void addConflict(Conflict conflict);

    public abstract void addThreat(Threat threat);

    public abstract boolean containsObstacle(Obstacle obstacle);

    public abstract boolean containsThreat(Threat threat);

    public abstract boolean containsConflict(Conflict conflict);

    public abstract void addObstacle(Obstacle obstacle);
}
