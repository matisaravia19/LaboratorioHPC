package uy.edu.fing.hpc;

public class Main {
    private static final String CONTAINERS_PATH = "data/containers.csv";
    private static final String OSM_PATH = "data/uruguay-latest.osm.pbf";

    public static void main(String[] args) {
        var dataSource = new DataSource(CONTAINERS_PATH);
        dataSource.load();

        var containers = dataSource.getContainers();
        var circuits = dataSource.getCircuits();

        Router.getInstance().init(containers, OSM_PATH);

        var solution = new Solution(circuits);

        System.out.println("Costo inicial: " + solution.computeCost());

        var neighbor = solution.getNeighbor();

        System.out.println("Costo vecino: " + neighbor.computeCost());

        System.out.println("Listo");
    }
}