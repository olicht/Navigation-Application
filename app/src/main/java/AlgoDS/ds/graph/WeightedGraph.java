package AlgoDS.ds.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* *
 * Created by sherxon on 1/7/17.
 */
public class WeightedGraph<VT> {
    private Set<Edge<VT>> edges;
    private boolean Undirected;
    private Map<VT, Set<Edge<VT>>> map;

    public WeightedGraph(boolean Undirected) {
        this.edges = new HashSet<>();
        this.Undirected = Undirected;
        this.map = new HashMap<>();
    }

    public void addVertex(VT v) {
        map.put(v, new HashSet<>());
    }

    public void addEdge(VT v1, VT v2, Double weight) {
        this.addEdge(new Edge<>(v1, v2, weight));
    }

    public void addEdge(VT v1, VT v2, Double weight, boolean accessible) {
        this.addEdge(new Edge<>(v1, v2, weight, accessible));
    }
    public void addEdge(Edge<VT> edge) {
        if (!map.containsKey(edge.getFrom())) return;
        if (!map.containsKey(edge.getTo())) return;

        map.get(edge.getFrom()).add(edge);
        edges.add(edge);

        if (Undirected) {
            Edge<VT> edge2 = edge.getOpposite();
            map.get(edge2.getFrom()).add(edge2);
            edges.add(edge2);
        }
    }

    public void removeVertex(VT v) {

    }

    public Set<VT> getVertices() {
        return new HashSet<>(map.keySet());
    }

    public Set<Edge<VT>> getVertexNeighbors(VT v) {
        return map.get(v);
    }

    public Set<Edge<VT>> getEdges() {
        return edges;
    }

    public Set<Edge<VT>> getEdges(VT ver) {
        return map.get(ver);
    }

    public int size() {
        return map.size();
    }
}
