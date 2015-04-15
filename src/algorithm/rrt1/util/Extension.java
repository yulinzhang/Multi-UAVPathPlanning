package algorithm.rrt1.util;


public class Extension<S,E> {
    final public S source;
    final public S target;
    final public E edge;
    final public double cost;
    final public boolean exact;//exact target

    /**
     * 
     * @param source
     * @param target
     * @param edge
     * @param cost
     * @param exact 
     */
    public Extension(S source, S target, E edge, double cost, boolean exact) {
        super();
        this.source = source;
        this.target = target;
        this.edge = edge;
        this.cost = cost;
        this.exact = exact;
    }
}