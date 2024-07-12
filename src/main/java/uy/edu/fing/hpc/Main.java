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
        var cost = solution.computeCost();

        System.out.println("Costo inicial: " + cost);

        for (var i = 0; i < 1000; i++) {
            var neighbor = solution.getNeighbor();
            var neighborCost = neighbor.computeCost();

            if (neighborCost < cost) {
                solution = neighbor;
                cost = neighborCost;

                System.out.println("Nuevo costo: " + neighborCost + " en iteración " + i);
            }
        }

        System.out.println("Costo final: " + cost);

        System.out.println("Listo");
    }
}