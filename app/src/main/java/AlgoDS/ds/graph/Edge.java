package AlgoDS.ds.graph;

import android.support.annotation.NonNull;

/**
 * Created by sherxon on 1/7/17.
 */
public class Edge<VT> implements Comparable<Edge<VT>> {
    private Double weight;
    private VT from;
    private VT to;
    private Boolean accessible;
    public Edge(Double weight) {
        this.weight = weight;
    }

    public Edge(VT from, VT to, Double weight) {
        this.weight = weight;
        this.from = from;
        this.to = to;
        this.accessible = true;
    }

    public Edge(VT from, VT to, Double weight, Boolean accessible) {
        this.weight = weight;
        this.from = from;
        this.to = to;
        this.accessible = accessible;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "weight=" + weight +
                ", from=" + from +
                ", to=" + to +
                ", accessible=" + accessible +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (weight != null ? !weight.equals(edge.weight) : edge.weight != null) return false;
        if (from != null ? !from.equals(edge.from) : edge.from != null) return false;
        if (to != null ? !to.equals(edge.to) : edge.to != null) return false;
        return accessible != null ? accessible.equals(edge.accessible) : edge.accessible == null;
    }

    @Override
    public int hashCode() {
        int result = weight != null ? weight.hashCode() : 0;
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (accessible != null ? accessible.hashCode() : 0);
        return result;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public VT getFrom() {
        return from;
    }

    public void setFrom(VT from) {
        this.from = from;
    }

    public VT getTo() {
        return to;
    }

    public Boolean getAccessible() {
        return accessible;
    }

    public void setAccessible(Boolean accessible) {
        this.accessible = accessible;
    }

    public void setTo(VT to) {
        this.to = to;
    }

    public Edge<VT> getOpposite(){
        return new Edge<>(from, to, weight, accessible);
    }

    @Override
    public int compareTo(@NonNull Edge o) {
        return (int) (this.weight - o.weight);
    }
}
