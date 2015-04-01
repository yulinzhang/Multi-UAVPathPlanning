/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import ui.AnimationPanel;

/**
 *
 * @author boluo
 */
public class WorldKnowledge implements TreeModel {

    private String rootNode = "机器人知识";
    private String firstChild = "障碍信息";
    private String secondChild = "威胁信息";
    private String thirdChild = "冲突信息";

    private Vector<Obstacle> obstacles;
    private Vector<Threat> threats;
    private Vector<Conflict> conflicts;

    private Vector<Object> root_child;
    private Vector<TreeModelListener> treeModelListeners
            = new Vector<TreeModelListener>();

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorldKnowledge.class);

    public WorldKnowledge() {
        root_child = new Vector<Object>();
        root_child.add(firstChild);
        root_child.add(secondChild);
        root_child.add(thirdChild);
        obstacles = new Vector<Obstacle>();
        threats = new Vector<Threat>();
        conflicts = new Vector<Conflict>();
        init();
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
        } else if (root_child.contains(parent)){//parent is in second level
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

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

}
