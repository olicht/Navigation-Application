package temp.navigationapplication;

import android.support.annotation.NonNull;

import AlgoDS.ds.graph.Edge;
import AlgoDS.ds.graph.WeightedGraph;

import java.util.Arrays;
import java.util.HashMap;

public class HashMapToWeightedGraph {

    public static WeightedGraph<LocationDataPoint> cast(@NonNull HashMap<Long, LocationDataPoint> map) {
        WeightedGraph<LocationDataPoint> graph = new WeightedGraph<>(true);
        double dist;
        Edge<LocationDataPoint> edge;
        LocationDataPoint prev = null;

        LocationDataPoint[] arr = map.values().toArray(new LocationDataPoint[0]);
        Arrays.sort(arr);

        for (int i = 0, arrLength = arr.length; i < arrLength; i++) {
            LocationDataPoint datum = arr[i];
            graph.addVertex(datum);

            if (i > 0) {
                dist = CalculateDistance.distanceBetween(datum.getLatitude(), datum.getLongitude(), prev.getLatitude(), prev.getLongitude())[0];

                edge = new Edge<>(prev, datum, dist);
                graph.getVertexNeighbors(datum).add(edge);
                graph.addEdge(edge);
            }
            prev = datum;
        }
        LocationDataPoint first = arr[0];
        LocationDataPoint last = arr[arr.length - 1];
        assert prev != null;
        dist = CalculateDistance.distanceBetween(first.getLatitude(), first.getLongitude(), last.getLatitude(), last.getLongitude())[0];
        edge = new Edge<>(last, first, dist);
        graph.addEdge(edge);

        return graph;
    }
}
