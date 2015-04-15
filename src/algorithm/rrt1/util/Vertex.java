package algorithm.rrt1.util;

import java.util.Collection;
import java.util.HashSet;

public class Vertex<S, E> {
    public S state;
    public Vertex<S, E> parent;
    public Collection<Vertex<S, E>> children;

    public E edgeFromParent;

    public double costFromRoot;
    public double costFromParent;

    public Vertex(S state) {
        super();
        this.state = state;
        this.parent = null;
        this.costFromRoot = 0.0;
        this.costFromParent = 0.0;
        this.children = new HashSet<Vertex<S, E>>();
    }

    public S getState() {
        return state;
    }

    public double getCostFromRoot() {
        return costFromRoot;
    }

    public void addChild(Vertex<S, E> child) {
        children.add(child);
    }

    public void removeChild(Vertex<S, E> child) {
        children.remove(child);
    }

    public Vertex<S,E> getParent() {
        return parent;
    }

    public void setParent(Vertex<S,E> parent) {
        this.parent = parent;
    }

    public Collection<Vertex<S,E>> getChildren() {
        return children;
    }

    public void setCostFromParent(double costToParent) {
        this.costFromParent = costToParent;
    }

    public void setCostFromRoot(double costToRoot) {
        this.costFromRoot = costToRoot;
    }

    public double getCostFromParent() {
        return costFromParent;
    }

    @Override
    public String toString() {
        return "(" + state + ", toRoot=" + costFromRoot
                + ", toParent=" + costFromParent + ")";
    }

    public E getEdgeFromParent() {
        return edgeFromParent;
    }

    public void setEdgeFromParent(E edgeFromParent) {
        this.edgeFromParent = edgeFromParent;
    }

}
