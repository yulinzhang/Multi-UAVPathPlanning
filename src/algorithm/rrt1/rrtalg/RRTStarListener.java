package algorithm.rrt1.rrtalg;

import algorithm.rrt1.util.Vertex;






public abstract class RRTStarListener<S,E> {
    /**
     * A callback from the planner informing about a new vertex added to the tree.
     */
    public void notifyNewVertexInTree(Vertex<S,E> v) {};



    /**
     * A callback from the planner to the domain,
     * informing about a new vertex added to the tree.
     */
    public void notifyIterationFinished() {};
}
