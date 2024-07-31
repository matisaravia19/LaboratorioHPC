package uy.edu.fing.hpc;

public class Container {
    private int id;
    private double latitude;
    private double longitude;

    public Container(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getId() {
        return id;
    }
}
