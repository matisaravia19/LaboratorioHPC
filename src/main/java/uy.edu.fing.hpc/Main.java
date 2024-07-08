package uy.edu.fing.hpc;

public class Main {
    private static final String CONTAINERS_PATH = "C:/Users/matis/OneDrive/Documentos/Fing/HPC/Contenedores_domiciliarios.csv";
    private static final String CIRCUITS_PATH = "C:/Users/matis/OneDrive/Documentos/Fing/HPC/Circuitos_recoleccion.csv";
    private static final String OSM_PATH = "C:/Users/matis/OneDrive/Documentos/Fing/HPC/uruguay-latest.osm.pbf";

    public static void main(String[] args) {
        var dataSource = new DataSource(CONTAINERS_PATH);
        dataSource.load();

        var containers = dataSource.getContainers();
        var circuits = dataSource.getCircuits();

        Router.getInstance().init(containers);

        var solution = new Solution(circuits);

        System.out.println("Costo inicial: " + solution.computeCost());

        var neighbor = solution.getNeighbor();

        System.out.println("Costo vecino: " + neighbor.computeCost());

        System.out.println("Listo");
    }
}