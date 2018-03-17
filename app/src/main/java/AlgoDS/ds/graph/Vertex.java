package AlgoDS.ds.graph;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sherxon on 1/1/17.
 */
public class Vertex<T> implements Comparable<Vertex<T>> {
    private T value;
    private Set<Vertex<T>> neighbors; // used with Unweighted graphs
    private boolean visited; //used for bfs and dfs

    public Vertex(T value) {
        this.value = value;
        this.neighbors = new HashSet<>();
    }

    public Vertex() {
        this.value = null;
        this.neighbors = null;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "value=" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex<?> vertex = (Vertex<?>) o;

        return value.equals(vertex.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public void addNeighbor(Vertex<T> vertex) {
        this.neighbors.add(vertex);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Set<Vertex<T>> getNeighbors() {
        return neighbors;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void removeNeighrbor(Vertex<T> vertex) {
        this.neighbors.remove(vertex);
    }

    @Override
    public int compareTo(@NonNull Vertex<T> o) {
        return (int) (this.hashCode() - o.hashCode());
    }
}
