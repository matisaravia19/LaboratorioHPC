package uy.edu.fing.hpc.im;

public class IMCircuit {
    public int id;
    public String name;
    public String[] points;

    public IMCircuit(int id, String name, String[] points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }
}
