package uy.edu.fing.hpc.im;

public class IMContainer {
    private int id;
    private String circuit;
    private IMShift shift;
    private double x;
    private double y;

    public IMContainer(int id, String circuit, IMShift shift, double x, double y) {
        this.id = id;
        this.circuit = circuit;
        this.shift = shift;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getCircuit() {
        return circuit;
    }

    public IMShift getShift() {
        return shift;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
