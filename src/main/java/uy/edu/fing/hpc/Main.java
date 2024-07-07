package uy.edu.fing.hpc;

public class Main {
    public static void main(String[] args) {
        var containers = DataSource.readContainers("C:/Users/matis/OneDrive/Documentos/Fing/HPC/Contenedores_domiciliarios.csv");

        System.out.println("Listo");
    }
}