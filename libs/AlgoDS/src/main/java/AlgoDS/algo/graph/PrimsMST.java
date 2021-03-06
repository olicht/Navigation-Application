package AlgoDS.algo.graph;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.WeightedGraph;

/**
 * This is Prim's greedy Minimum Spanning Tree algorithm which can be used in connected weighted graphs.
 * Algorithms starts building MST by randomly choosing one vertex. Then, we add least weighted edge from already
 * selected vertices and add its adjacent vertex to tree if it is not already in the tree.
 * Running time is O(V^2). It can be done O(E+ V logV) by using Fibonacci heap.
 */
@SuppressWarnings("unused")
public class PrimsMST<VT> {


    private WeightedGraph<VT> graph;

    public PrimsMST(WeightedGraph<VT> graph) {
        this.graph = graph;
    }

    public List<Edge<VT>> getMST() {
        List<Edge<VT>> list = new ArrayList<>();

        VT start = getRandomVertex();
        if (start == null) return list;

        Set<VT> mst = new HashSet<>();
        mst.add(start);
        PriorityQueue<Edge<VT>> q = new PriorityQueue<>(graph.getEdges(start));

        while (!q.isEmpty()) {
            Edge<VT> minEdge = q.remove(); // get min weighted edge
            VT vertex = minEdge.getTo();
            if (mst.contains(vertex)) continue; // if it is already in the MST tree, ignore it

            q.addAll(graph.getEdges(vertex));

            list.add(minEdge);
            mst.add(vertex); // add this vertex to mst

        }
        return list;
    }

    /**
     * we can start any from any random vertex
     */
    @Nullable
    private VT getRandomVertex() {

        if (graph.getVertices().size() > 0)
            return graph.getVertices().iterator().next();

        return null;
    }


}
