package algorithm.rrt1.util;


public class ExtensionEstimate {
    final public double cost;
    final public boolean exact;

    public ExtensionEstimate(double cost, boolean exact) {
        super();
        this.cost = cost;
        this.exact = exact;
    }
}