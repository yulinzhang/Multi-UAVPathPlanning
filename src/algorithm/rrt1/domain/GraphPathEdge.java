package algorithm.rrt1.domain;


import org.jgrapht.GraphPath;

public class GraphPathEdge<S, E> {
    private GraphPath<S, E> path;

    public GraphPathEdge(GraphPath<S, E> path) {
        this.path = path;
    }

    public GraphPath<S, E> getPath() {
        return path;
    }
}
