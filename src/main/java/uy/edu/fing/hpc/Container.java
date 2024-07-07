package uy.edu.fing.hpc;

public class Container {
    private int id;
    private String originalCircuit;
    private int frequency;
    private double latitude;
    private double longitude;

    public Container(int id, String originalCircuit, int frequency, double latitude, double longitude) {
        this.id = id;
        this.originalCircuit = originalCircuit;
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

    public String getOriginalCircuit() {
        return originalCircuit;
    }
}
