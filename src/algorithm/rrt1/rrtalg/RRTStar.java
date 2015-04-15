package algorithm.rrt1.rrtalg;

import algorithm.rrt1.domain.Domain;
import algorithm.rrt1.util.Extension;
import algorithm.rrt1.util.ExtensionEstimate;
import algorithm.rrt1.util.Vertex;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;

import java.util.*;
import util.DistanceUtil;

public class RRTStar<S, E> implements Graph<S, E> {

    protected Domain<S, E> domain;

    protected List<RRTStarListener<S, E>> listeners;
    protected int nSamples;

    protected Vertex<S, E> root;
    protected double initialRadius;
    protected double minRadius;
    protected double maxRadius;

    protected Vertex<S, E> bestVertex;

    protected Map<E, S> edgeSources = new HashMap<E, S>();
    protected Map<E, S> edgeTargets = new HashMap<E, S>();
    protected Map<S, E> incomingEdges = new HashMap<S, E>();

    // only for debugging/visualisation purposes
    public int ext_counter;
    public S lastSampleDrawn = null;
    public S lastNewSample = null;

    public RRTStar(Domain<S, E> domain, S initialState, double initialRadius) {
        this(domain, initialState, initialRadius, 0, Double.POSITIVE_INFINITY);
    }

    public RRTStar(Domain<S, E> domain, S initialState, double initialRadius, double minRadius, double maxRadius) {
        super();

        this.domain = domain;
        this.initialRadius = initialRadius;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.root = new Vertex<S, E>(initialState);
        this.nSamples = 1;
        this.ext_counter = 0;

        this.bestVertex = null;
        this.listeners = new LinkedList<RRTStarListener<S, E>>();
    }


    public GraphPath<S, E> plan(double maxIterations) {
        for (int i = 0; i < maxIterations; i++) {
            iterate();
        }

        return getBestPath();
    }

    public void iterate() {
        lastSampleDrawn = null;
        lastNewSample = null;

        // 1. Sample a new state
        S randomSample = sampleState();
        lastSampleDrawn = randomSample;

        if (randomSample == null)
            return;

        Vertex<S, E> nearestVertex = getNearestParentVertex(randomSample);
        Extension<S, E> nearestToNew = extendTo(nearestVertex, randomSample);

        if (nearestToNew != null) {
            S newSample = nearestToNew.target;
            lastNewSample = newSample;

            // 2. Compute the set of all near vertices in the ball
            Collection<Vertex<S, E>> nearVertices = getNearParentCandidates(newSample);
            nearVertices.add(nearestVertex);

            // 3. Find the best parent and extend from that parent
            BestParentSearchResult result = null;
            result = findBestParent(newSample, nearVertices);

            if (result != null) {
                // 3.c add the trajectory from the best parent to the tree
                Vertex<S, E> newVertex = insertExtension(result.parent, result.extension);
                if (newVertex != null) {
                    // 4. rewire the tree
                    nearVertices = getNearChildrenCandidates(newVertex.getState());
                    rewire(newVertex, nearVertices);
                }
            }

        }
    }

    private Extension<S, E> extendTo(Vertex<S, E> vertex, S sample) {
        ext_counter++;
//        if (ext_counter++ % 200 == 0)
//            if (bestVertex == null) {
//                System.out.println(String.format("best,%d,Inf", ext_counter));
//            } else {
//                System.out.println(String.format("best,%d,%f", ext_counter, bestVertex.costFromRoot));
//            }
        return domain.extendTo(vertex, sample);
    }

    /**
     * Alternative implementation, which corresponds more closely to the C++
     * implementation of RRT* by Karaman. It works faster in most domains,
     * however, it differs from the formulation as used in RRT* papers and thus it
     * probably does not provide its theoretical guarantees.
     */

    public void iterateAlt() {
        lastSampleDrawn = null;
        lastNewSample = null;

        // 1. Sample a new state， lastSampleDrawn is not used
        S randomSample = sampleState();
        lastSampleDrawn = randomSample;

        if (randomSample == null)
            return;

        // 2. Compute the set of all near vertices in the ball
        Collection<Vertex<S, E>> nearVertices = getNearParentCandidates(randomSample);

        // 3. Find the best parent and extend from that parent
        BestParentSearchResult result = null;
        if (!nearVertices.isEmpty()) {
            // a) find best parent
            result = findBestParent(randomSample, nearVertices);
        } else {
            // b) extend from nearest
            Vertex<S, E> nearestVertex = getNearestParentVertex(randomSample);
            Extension<S, E> nearestToNew = extendTo(nearestVertex, randomSample);
            if (nearestToNew != null) {
                result = new BestParentSearchResult(nearestVertex, nearestToNew);
            }
        }

        if (result != null) {
            // 3.c add the trajectory from the best parent to the tree
            Vertex<S, E> newVertex = insertExtension(result.parent, result.extension);
            if (newVertex != null) {
                // 4. rewire the tree
                nearVertices = getNearChildrenCandidates(newVertex.getState());
                rewire(newVertex, nearVertices);
            }
        }
    }

    protected S sampleState() {
        return domain.sampleState();
    }

    protected Vertex<S, E> insertExtension(Vertex<S, E> parent, Extension<S, E> extension) {

        if (bestVertex != null) {
            if (bestVertex.getCostFromRoot() < parent.getCostFromRoot() + domain.estimateCostToGo(parent.getState())) {
                return null;
            }
        }

        Vertex<S, E> newVertex = new Vertex<S, E>(extension.target);
        nSamples++;

        insertExtension(parent, extension, newVertex);

        return newVertex;
    }

    protected void insertExtension(Vertex<S, E> parent, Extension<S, E> extension, Vertex<S, E> target) {
        if (target.parent != null) {
            target.parent.removeChild(target);
        }

        assert (!extension.exact || extension.target.equals(target.getState()));

        parent.addChild(target);
        target.setParent(parent);
        
        target.setEdgeFromParent(extension.edge);

        target.setCostFromParent(extension.cost);
        target.setCostFromRoot(parent.getCostFromRoot() + extension.cost);

        checkBestVertex(target);

        edgeSources.put(extension.edge, parent.getState());
        edgeTargets.put(extension.edge, target.getState());
        incomingEdges.put(target.getState(), extension.edge);

        for (RRTStarListener listener1 : listeners) {
            listener1.notifyNewVertexInTree(target);
        }
    }

    protected class BestParentSearchResult {
        final Vertex<S, E> parent;
        final Extension<S, E> extension;

        public BestParentSearchResult(Vertex<S, E> parent, Extension<S, E> extension) {
            this.parent = parent;
            this.extension = extension;
        }
    }

    protected BestParentSearchResult findBestParent(S randomSample, Collection<Vertex<S, E>> nearVertices) {

        class VertexCost implements Comparable<VertexCost> {
            final Vertex<S, E> vertex;
            final double cost;

            public VertexCost(Vertex<S, E> vertex, double cost) {
                super();
                this.vertex = vertex;
                this.cost = cost;
            }

            @Override
            public int compareTo(VertexCost other) {
                // Smallest cost will be first
                return Double.compare(this.cost, other.cost);
            }

            @Override
            public String toString() {
                return "VertexCost [vertex=" + vertex + ", cost=" + cost + "]";
            }
        }

        // Sort according to the cost of nearby vertices
        List<VertexCost> vertexCosts = new LinkedList<VertexCost>();

        for (Vertex<S, E> vertex : nearVertices) {
            ExtensionEstimate extensionEst = domain.estimateExtension(vertex.getState(), randomSample);
            if (extensionEst != null) {
                vertexCosts.add(new VertexCost(vertex, vertex.getCostFromRoot() + extensionEst.cost));
            }
        }

        // Sort according to the vertex costs
        Collections.sort(vertexCosts);

        // Try to establish an edge to vertices in increasing order of costs
        for (VertexCost vertexCost : vertexCosts) {
            Vertex<S, E> vertex = vertexCost.vertex;
            Extension<S, E> extension = extendTo(vertex, randomSample);
            if (extension != null && extension.exact) {
                return new BestParentSearchResult(vertex, extension);
            }
        }

        return null;
    }


    protected void rewire(Vertex<S, E> candidateParent, Collection<Vertex<S, E>> vertices) {
        for (Vertex<S, E> nearVertex : vertices) {
            if (nearVertex != candidateParent) {
                ExtensionEstimate extensionEst = domain.estimateExtension(candidateParent.getState(), nearVertex.getState());
                if (extensionEst != null) {
                    double costToRootOverNew = candidateParent.getCostFromRoot() + extensionEst.cost;
                    if (extensionEst.exact && costToRootOverNew < nearVertex.getCostFromRoot()) {
                        Extension<S, E> extension = extendTo(candidateParent, nearVertex.getState());
                        if (extension != null && extension.exact) {
                            insertExtension(candidateParent, extension, nearVertex);
                            updateBranchCost(nearVertex);
                        }
                    }
                }

            }
        }
    }

    protected void updateBranchCost(Vertex<S, E> vertex) {
        checkBestVertex(vertex);
        for (Vertex<S, E> child : vertex.getChildren()) {
            child.setCostFromRoot(vertex.getCostFromRoot() + child.getCostFromParent());
            updateBranchCost(child);
        }
    }

    protected void checkBestVertex(Vertex<S, E> vertex) {
        if (domain.isInTargetRegion(vertex.getState())) {
            if (bestVertex == null || vertex.costFromRoot < bestVertex.costFromRoot) {
                bestVertex = vertex;
            }
        }
    }

    protected Collection<Vertex<S, E>> getNearParentCandidates(final S x) {
        final double radius = getNearBallRadius();
        this.maxRadius=radius;
        return dfsConditionSearch(new Condition<S, E>() {
            @Override
            public boolean satisfiesCondition(Vertex<S, E> examined) {
                return (domain.distance(examined.getState(), x) <= radius);
            }
        });
    }


    protected Collection<Vertex<S, E>> getNearChildrenCandidates(final S x) {
        final double radius = getNearBallRadius();
        return dfsConditionSearch(new Condition<S, E>() {
            @Override
            public boolean satisfiesCondition(Vertex<S, E> examined) {
                return (!x.equals(examined.getState()) && domain.distance(x, examined.getState()) <= radius);
            }
        });
    }

    public double getNearBallRadius() {
        int n = nSamples;

        double radius = initialRadius * Math.pow(Math.log(n + 1) / (n + 1), 1 / domain.nDimensions());

        radius = Math.min(radius, maxRadius);
        radius = Math.max(radius, minRadius);

        return radius;
    }

    protected Vertex<S, E> getNearestParentVertex(S x) {
        return dfsNearestParentSearch(x);
    }

    public interface Condition<S, E> {
        boolean satisfiesCondition(Vertex<S, E> examinedVertex);

    }

    Collection<Vertex<S, E>> dfsConditionSearch(Condition<S, E> condition) {
        Queue<Vertex<S, E>> queue = new LinkedList<Vertex<S, E>>();
        LinkedList<Vertex<S, E>> result = new LinkedList<Vertex<S, E>>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Vertex<S, E> current = queue.poll();
            if (condition.satisfiesCondition(current)) {
                result.add(current);
            }

            for (Vertex<S, E> child : current.getChildren()) {
                queue.offer(child);
            }
        }

        return result;
    }

    Vertex<S, E> dfsNearestParentSearch(S center) {
        Queue<Vertex<S, E>> queue1 = new LinkedList<Vertex<S, E>>();
        Vertex<S, E> minDistVertex = null;
        double minDist = Double.POSITIVE_INFINITY;

        queue1.add(root);

        while (!queue1.isEmpty()) {
            Vertex<S, E> current = queue1.poll();
            double distance = domain.distance(current.getState(), center);
            if (distance <= minDist) {
                minDistVertex = current;
                minDist = distance;
            }

            for (Vertex<S, E> child : current.getChildren()) {
                queue1.offer(child);
            }
        }

        return minDistVertex;
    }

    public Vertex<S, E> getRoot() {
        return root;
    }

    public GraphPath<S, E> getBestPath() {
        LinkedList<E> edges = new LinkedList<E>();

        S end;
        S start;

        if (bestVertex == null) {
            return null;
        } else {
            end = bestVertex.getState();
            Vertex<S, E> current = bestVertex;
            while (current.getParent() != null) {
                edges.addFirst(current.getEdgeFromParent());
                current = current.getParent();
            }
            start = current.getState();
        }

        return new GraphPathImpl<S, E>(this, start, end, edges, bestVertex.getCostFromRoot());
    }

    public Vertex<S, E> getBestVertex() {
        return bestVertex;
    }

    public boolean foundSolution() {
        return bestVertex != null;
    }

    public void registerListener(RRTStarListener<S, E> listener) {
        listeners.add(listener);
    }

    @Override
    public E addEdge(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean addEdge(S arg0, S arg1, E arg2) {
        throw new NotImplementedException();
    }


    @Override
    public boolean addVertex(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsEdge(E arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsEdge(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsVertex(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> edgeSet() {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> edgesOf(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> getAllEdges(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public E getEdge(S start, S end) {
        return incomingEdges.get(end);
    }


    @Override
    public EdgeFactory<S, E> getEdgeFactory() {
        throw new NotImplementedException();
    }


    @Override
    public S getEdgeSource(E edge) {
        return edgeSources.get(edge);
    }


    @Override
    public S getEdgeTarget(E edge) {
        return edgeTargets.get(edge);
    }


    @Override
    public double getEdgeWeight(E arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean removeAllEdges(Collection<? extends E> arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> removeAllEdges(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean removeAllVertices(Collection<? extends S> arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean removeEdge(E arg0) {
        throw new NotImplementedException();
    }


    @Override
    public E removeEdge(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean removeVertex(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<S> vertexSet() {
        throw new NotImplementedException();
    }


    public S getLastSample() {
        return lastSampleDrawn;
    }

    public S getNewSample() {
        return lastNewSample;
    }

}
