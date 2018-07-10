package temp.navigationapplication;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class LocationDataPoint implements Serializable, Comparable<LocationDataPoint> {

    private double longitude;
    private double latitude;
    private double altitude;
    private boolean accessible;
    private float bearing;
    private float accuracy;
    private long time;


    public LocationDataPoint(double longitude, double latitude, boolean accessible) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.accessible = accessible;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    @SuppressWarnings("All")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationDataPoint)) return false;

        LocationDataPoint that = (LocationDataPoint) o;

        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.altitude, altitude) != 0) return false;
        if (accessible != that.accessible) return false;
        if (Float.compare(that.bearing, bearing) != 0) return false;
        if (Float.compare(that.accuracy, accuracy) != 0) return false;
        return time == that.time;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(longitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (accessible ? 1 : 0);
        result = 31 * result + (bearing != +0.0f ? Float.floatToIntBits(bearing) : 0);
        result = 31 * result + (accuracy != +0.0f ? Float.floatToIntBits(accuracy) : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "LocationDataPoint{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", accessible=" + accessible +
                ", bearing=" + bearing +
                ", accuracy=" + accuracy +
                ", time=" + time +
                '}';
    }

    @Override
    public int compareTo(@NonNull LocationDataPoint o) {
        return (int) (this.time - o.time);
    }
}
