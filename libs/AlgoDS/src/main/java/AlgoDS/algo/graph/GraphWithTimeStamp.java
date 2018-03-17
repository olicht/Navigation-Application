package AlgoDS.algo.graph;

import AlgoDS.ds.graph.Graph;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a graph with timestamps (in and out) in each vertex.
 */
@SuppressWarnings("unused")
public class GraphWithTimeStamp {

    protected Graph graph;
    protected Map<Integer, Integer> in;
    protected Map<Integer, Integer> out;
    @SuppressWarnings("WeakerAccess")
    int time;

    public GraphWithTimeStamp(Graph graph) {
        this.graph = graph;
        in = new HashMap<>();
        out = new HashMap<>();
    }

    @SuppressWarnings("WeakerAccess")
    protected void addTimeStamp(Integer current) {
        time++;
        in.put(current, time);
        for (Integer neighbor : graph.getNeighbors(current)) {

            if (!in.containsKey(neighbor))
                addTimeStamp(neighbor);
        }
        time++;
        out.put(current, time);
    }


}
