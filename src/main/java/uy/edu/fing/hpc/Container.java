package uy.edu.fing.hpc;

public class Container {
    private int id;
    private Frequency frequency;
    private double latitude;
    private double longitude;

    public Container(int id, Frequency frequency, double latitude, double longitude) {
        this.id = id;
        this.frequency = frequency;
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
