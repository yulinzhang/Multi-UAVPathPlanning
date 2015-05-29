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
package algorithm.RRT;

import java.util.ArrayList;
import util.DistanceUtil;
import util.VectorUtil;
import world.model.shape.Point;
import world.uav.UAVPath;

/**
 *
 * @author Yulin_Zhang
 */
public class RRTTree {

    private ArrayList<RRTNode> vertices;
    private ArrayList<RRTNode> parents_of_vertices;
    private ArrayList<ArrayList<RRTNode>> children_of_vertices;
    
    private RRTNode last_time_added=null;

    /**
     * variable to store the path
     *
     */
    private UAVPath path_found;

    //static double dist = 0;
    //static Node closestNode = null;        
    public RRTTree() {
        vertices = new ArrayList<RRTNode>();
        children_of_vertices = new ArrayList<ArrayList<RRTNode>>();
        parents_of_vertices = new ArrayList<RRTNode>();
        path_found = new UAVPath();
    }

    /**
     *
     * @param child
     * @param parent
     */
    public void addNode(RRTNode child, RRTNode parent) {
        if (!vertices.contains(child)) {
            vertices.add(child);
            addParent(child, parent);
            this.last_time_added=child;
        }
    }

    private void addParent(RRTNode child, RRTNode parent) {
        int index = vertices.indexOf(child);
        parents_of_vertices.add(index, parent);
        children_of_vertices.add(index, new ArrayList<RRTNode>());
        if (parent != null) {//not root node
            addChild(parent, child);
            child.setCurrent_angle(VectorUtil.getAngleOfVectorRelativeToXCoordinate(child.getCoordinate()[0] - parent.getCoordinate()[0], child.getCoordinate()[1] - parent.getCoordinate()[1]));
            child.setPath_lenght_from_root(parent.getPath_lenght_from_root() + DistanceUtil.distanceBetween(parent.getCoordinate(), child.getCoordinate()));
            child.setExpected_time_step(parent.getExpected_time_step() + 1);
        } else {//root node
            child.setPath_lenght_from_root(0);
            child.setExpected_time_step(0);
        }
    }

    /**
     *
     * @param parent
     * @param child
     */
    private void addChild(RRTNode parent, RRTNode child) {
        int index = vertices.indexOf(parent);
        if (!children_of_vertices.get(index).contains(child)) {
            children_of_vertices.get(index).add(child);
        }
    }

    public ArrayList<RRTNode> getChildren(RRTNode n) {
        int index = vertices.indexOf(n);
        if (index == -1) {
            return null;
        } else if (children_of_vertices.get(index).isEmpty()) {
            return null;
        } else {
            return children_of_vertices.get(index);
        }
    }

    public RRTNode getParent(RRTNode n) {
        int index = vertices.indexOf(n);
        return parents_of_vertices.get(index);
    }

    /**
     *
     * @param child_node
     * @param newParent
     */
    public void changeParent(RRTNode child_node, RRTNode new_parent) {
        int child_index = vertices.indexOf(child_node);
        RRTNode oldParent = parents_of_vertices.get(child_index);
        child_index = vertices.indexOf(oldParent);
        children_of_vertices.get(child_index).remove(child_node);
//        RRTNode oldParent = new RRTNode(temp.getCoordinate()[0], temp.getCoordinate()[1]);
//        parents_of_vertices.set(index, newParent);

        parents_of_vertices.set(child_index, new_parent);
        addChild(new_parent, child_node);
        child_node.setCurrent_angle(VectorUtil.getAngleOfVectorRelativeToXCoordinate(child_node.getCoordinate()[0] - new_parent.getCoordinate()[0], child_node.getCoordinate()[1] - new_parent.getCoordinate()[1]));
        child_node.setPath_lenght_from_root(new_parent.getPath_lenght_from_root() + DistanceUtil.distanceBetween(new_parent.getCoordinate(), child_node.getCoordinate()));

    }

    public RRTNode getNode(int index) {

        if (index == -1) {
            return null;
        }

        if (index < vertices.size()) {
            return vertices.get(index);
        } else {
            return null;
        }
    }

    public int getNodeCount() {
        return vertices.size();
    }

    public void generatePath() {
        RRTNode n=this.last_time_added;
        if(n==null)
        {
            return;
        }
        Point point = new Point(n.getCoordinate()[0], n.getCoordinate()[1], n.getCurrent_angle());
        path_found.addWaypointToBeginning(point);
        RRTNode parent = this.getParent(n);
        while (parent != null) {
            Point parent_point = new Point(parent.getCoordinate()[0], parent.getCoordinate()[1], parent.getCurrent_angle());
            path_found.addWaypointToBeginning(parent_point);
            parent = this.getParent(parent);
        }
    }

    public UAVPath getPath_found() {
        return path_found;
    }

    public void setPath_found(UAVPath path) {
        this.path_found = path;
    }

    public RRTNode getLast_time_added() {
        return last_time_added;
    }

    public void setLast_time_added(RRTNode last_time_added) {
        this.last_time_added = last_time_added;
    }

}
