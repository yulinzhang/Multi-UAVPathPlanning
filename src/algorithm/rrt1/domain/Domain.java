package algorithm.rrt1.domain;

import algorithm.rrt1.util.Extension;
import algorithm.rrt1.util.ExtensionEstimate;
import algorithm.rrt1.util.Vertex;

/**
 * The definition of a planning problem for RRT*
 * @author Michal Cap
 *
 * @param <S> class representing a state in the search space
 * @param <E> class representing a transition between states in the search space
 */
public interface Domain<S,E> {
    /**
     * @return a state from the free space, typically random sample
     */
    S sampleState();

    /**
     * @return constructs an extension from one state towards another state,
     * returns null if the extension cannot be constructed
     */
    Extension<S,E> extendTo(Vertex<S, E> from, S to);

    /**
     * @return an estimate of the extension cost without collision checking
     * (used to sort neighbors before trying to construct extensions)
     */
    ExtensionEstimate estimateExtension(S from, S to);

    /**
     * @return the lower bound heuristic estimate of the cost from state s1 to the target region
     */
    double estimateCostToGo(S s);

    /**
     * @return the distance between states s1 and s2
     * used to find the set of neighbors within a ball of given radius
     * the neighbors are candidates for parents of a new sample and for rewiring
     */
    double distance(S s1, S s2);

    /**
     * @return the number of dimensions of the state space
     */
    double nDimensions();

    /**
     * @return true if the point p is in the target region, false otherwise
     */
    boolean isInTargetRegion(S s);
    
}
