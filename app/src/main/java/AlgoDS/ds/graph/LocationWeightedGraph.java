package AlgoDS.ds.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import temp.navigationapplication.LocationDataPoint;

/* *
 * Created by sherxon on 1/7/17.
 */
public class LocationWeightedGraph {
    private Set<LocationEdge> edges;
    private boolean Undirected;
    private Map<LocationDataPoint, Set<LocationEdge>> map;

    public LocationWeightedGraph(boolean Undirected) {
        this.edges = new HashSet<>();
        this.Undirected = Undirected;
        this.map = new HashMap<>();
    }

    public void addVertex(LocationDataPoint v) {
        map.put(v, new HashSet<>());
    }

    public void addEdge(LocationDataPoint v1, LocationDataPoint v2, Double weight) {
        this.addEdge(new LocationEdge(v1, v2, weight));
    }

    public void addEdge(LocationDataPoint v1, LocationDataPoint v2, Double weight, boolean accessible) {
        this.addEdge(new LocationEdge(v1, v2, weight, accessible));
    }
    public void addEdge(LocationEdge edge) {
        if (!map.containsKey(edge.getFrom())) return;
        if (!map.containsKey(edge.getTo())) return;

        map.get(edge.getFrom()).add(edge);
        edges.add(edge);

        if (Undirected) {
            LocationEdge edge2 = edge.getOpposite();
            map.get(edge2.getFrom()).add(edge2);
            edges.add(edge2);
        }
    }

    public void removeVertex(LocationDataPoint v) {

    }

    public Set<LocationDataPoint> getVertices() {
        return new HashSet<>(map.keySet());
    }

    public void setVertices(Map<LocationDataPoint, Set<LocationEdge>> map2) {
        map = map2;
    }

    public Set<LocationEdge> getVertexNeighbors(LocationDataPoint v) {
        return map.get(v);
    }

    public Set<LocationEdge> getEdges() {
        return edges;
    }

    public void setEdges(Set<LocationEdge> edges2) {
        edges = edges2;
    }

    public Set<LocationEdge> getEdges(LocationDataPoint ver) {
        return map.get(ver);
    }

    public int size() {
        return map.size();
    }
}
