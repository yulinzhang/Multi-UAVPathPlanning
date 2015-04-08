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

    public abstract boolean deleteComponent(TreePath path, Object leaf_node);

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
