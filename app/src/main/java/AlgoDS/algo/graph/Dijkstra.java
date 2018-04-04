package AlgoDS.algo.graph;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.WeightedGraph;

/* *
 * Created by sherxon on 1/7/17.
 * <p>
 * This is the algorithm to find shortest Path in weighted and non-negative edged graph. Graph can be directed
 * or undirected. This is not optimized version as shortestPath() method searches vertex with minimal weight
 * every time. To optimize Fibonacci heap can be used. This algorithm finds shortest path from source vertex
 * to all other reachable vertexes. Time complexity is O(VE)
 */

/**
 * Choosing vertex with min distance is the main factor that affects running time of this algorithms
 * 1) Using naive approach i.e.  iterating vertex and choosing vertex with nim distance O(EV)
 * 2) Binary Heaps, if decreaseKey is implemented O(E logV)
 * 3) Fibonacci Heap, O(V logV + E), impractical
 */
public class Dijkstra<VT> {

    private Map<VT, Three> distance;
    private WeightedGraph<VT> graph;

    public Dijkstra(@NonNull WeightedGraph<VT> graph) {
        this.graph = graph;
        this.distance = new HashMap<>();
    }

    public Map<VT, Three> getDistance() {
        return distance;
    }

    public void setDistance(Map<VT, Three> distance) {
        this.distance = distance;
    }



    /**
     * This is naive implementation of Dijkstra shortest path algorithm. Running time is O(VE);
     * this implementation works best with dense graphs
     */
//    public void shortestPath(VT source, Boolean accessible) {
//
//        Set<VT> openSet = new TreeSet<>();
//        for (VT vertex : graph.getVertices()) {
//            if (source.equals(vertex)) distance.put(source, new Pair(null, 0.0));
//            else distance.put(vertex, new Pair(null, Double.POSITIVE_INFINITY));
//            openSet.add(vertex);
//        }
//        while (!openSet.isEmpty()) {
//
//            VT minVertex = null;
//            Pair minDis = new Pair(null, Double.POSITIVE_INFINITY);
//            for (VT vertex : openSet) {
//                if (minDis.compareTo(distance.get(vertex)) >= 0) {
//                    minDis = distance.get(vertex);
//                    minVertex = vertex;
//                }
//            }
//
//            openSet.remove(minVertex);
//
//            for (Edge<VT> edge : graph.getEdges(minVertex)) {
//                if (accessible && !edge.getAccessible()) continue;
//                Pair newPath = new Pair(minVertex, distance.get(minVertex).weight + edge.getWeight());
//                if (distance.get(edge.getTo()).compareTo(newPath) > 0) {
//                    distance.put(edge.getTo(), newPath);
//                }
//            }
//        }
//    }

    /**
     * This is optimized version of shortest path algorithm, whose running time is O(E logE)
     * this works better than other implementations in practise
     */
    public void shortestPathOptimized(VT source, Boolean accessible) {
        PriorityQueue<Three> queue = new PriorityQueue<>();
        HashMap<VT, Three> map = new HashMap<>();
        for (VT vertex : graph.getVertices()) {
            Three three;
            if (source.equals(vertex)) three = new Three(vertex, 0d, null);
            else three = new Three(vertex, Double.POSITIVE_INFINITY, null);
            queue.add(three);
            map.put(vertex, three);
        }

        while (!queue.isEmpty()) {

            Three three = queue.remove();

            if (map.get(three.label) != three) continue; // if pair is already updated

            for (Edge<VT> edge : graph.getEdges(three.label)) {
                if (accessible && !edge.getAccessible()) continue;
                double newPath = three.weight + edge.getWeight();
                if (newPath < map.get(edge.getTo()).weight) {
                    Three newThree = new Three(edge.getTo(), newPath, edge.getFrom());
                    map.put(edge.getTo(), newThree);
                    queue.add(newThree);
                }
            }
        }

        this.distance = map;

    }

    private class Pair implements Comparable<Pair> {
        VT label;
        Double weight;

        Pair(VT name, Double weight) {
            this.label = name;
            this.weight = weight;
        }

        @Override
        public int compareTo(@NonNull Pair p) {
            return (int) (this.weight - p.weight);
        }
    }

    public class Three implements Comparable<Three> {
        VT label;
        Double weight;
        VT prev;

        Three(VT name, Double weight, VT prev) {
            this.label = name;
            this.weight = weight;
            this.prev = prev;
        }

        public VT getPrev() {return prev;}

        @Override
        public int compareTo(@NonNull Three t) {
            return (int) (this.weight - t.weight);
        }
    }
}
