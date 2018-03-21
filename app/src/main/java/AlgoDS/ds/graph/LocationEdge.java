package AlgoDS.ds.graph;

import android.support.annotation.NonNull;

import temp.navigationapplication.LocationDataPoint;

/**
 * Created by sherxon on 1/7/17.
 */
public class LocationEdge implements Comparable<LocationEdge> {
    private Double weight;
    private LocationDataPoint from;
    private LocationDataPoint to;
    private Boolean accessible;
    public LocationEdge(Double weight) {
        this.weight = weight;
    }

    public LocationEdge(LocationDataPoint from, LocationDataPoint to, Double weight) {
        this.weight = weight;
        this.from = from;
        this.to = to;
        this.accessible = true;
    }

    public LocationEdge(LocationDataPoint from, LocationDataPoint to, Double weight, Boolean accessible) {
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
        if (!(o instanceof LocationEdge)) return false;

        LocationEdge edge = (LocationEdge) o;

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

    public LocationDataPoint getFrom() {
        return from;
    }

    public void setFrom(LocationDataPoint from) {
        this.from = from;
    }

    public LocationDataPoint getTo() {
        return to;
    }

    public Boolean getAccessible() {
        return accessible;
    }

    public void setAccessible(Boolean accessible) {
        this.accessible = accessible;
    }

    public void setTo(LocationDataPoint to) {
        this.to = to;
    }

    public LocationEdge getOpposite(){
        return new LocationEdge(from, to, weight, accessible);
    }

    @Override
    public int compareTo(@NonNull LocationEdge o) {
        return (int) (this.weight - o.weight);
    }
}
