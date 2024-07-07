package uy.edu.fing.hpc.im;

public class IMCircuit {
    private int id;
    private String name;
    private String[] points;

    public IMCircuit(int id, String name, String[] points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getPoints() {
        return points;
    }
}
