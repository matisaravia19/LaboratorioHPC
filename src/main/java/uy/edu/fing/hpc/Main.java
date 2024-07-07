package uy.edu.fing.hpc;

import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.GHUtility;

public class Main {
    private static final String CONTAINERS_PATH = "C:/Users/matis/OneDrive/Documentos/Fing/HPC/Contenedores_domiciliarios.csv";
    private static final String CIRCUITS_PATH = "C:/Users/matis/OneDrive/Documentos/Fing/HPC/Circuitos_recoleccion.csv";
    private static final String OSM_PATH = "C:/Users/matis/OneDrive/Documentos/Fing/HPC/uruguay-latest.osm.pbf";

    public static void main(String[] args) {
        DataSource.load(CONTAINERS_PATH, CIRCUITS_PATH);

        //Router.getInstance().init(containers);

        System.out.println("Listo");
    }
}