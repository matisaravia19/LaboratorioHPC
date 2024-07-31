package uy.edu.fing.hpc;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataSource {
    private static final CoordinateTransform transform;

    private final String containerFile;

    private final List<Container> containers = new ArrayList<>();
    private final Map<String, List<Circuit>> circuits = new HashMap<>();

    static {
        CRSFactory crsFactory = new CRSFactory();
        var sourceCrs = crsFactory.createFromName("EPSG:31981");
        var targetCrs = crsFactory.createFromName("EPSG:4326");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        transform = ctFactory.createTransform(sourceCrs, targetCrs);
    }

    public DataSource(String containerFile) {
        this.containerFile = containerFile;
    }

    public void load() {
        try (var stream = Files.lines(Paths.get(containerFile))) {
            stream.skip(1).forEach(this::loadContainerAndAddToCircuit);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void saveSolution(String path, Solution solution) {
        try (var writer = new BufferedWriter(new FileWriter(path, false))) {
            writer.write("Shift, Cost, Container count, Container IDs\n");
            for (var circuit : solution.getCircuits()) {
                var containers = circuit.getContainers();

                writer.write(circuit.getShift().toString() + ", " + circuit.getCost() + ", " + containers.size() + ",");
                for (var container : circuit.getContainers()) {
                    writer.write(" " + container.getId());
                }
                writer.write("\n");
            }

         } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void loadContainerAndAddToCircuit(String line) {
        var parts = line.split(";");

        var id = Integer.parseInt(parts[0]);
        var circuitName = parts[1];
        var shift = readShift(parts[2]);
        var x = Double.parseDouble(parts[3]);
        var y = Double.parseDouble(parts[4]);

        var coordinates = transformCoordinates(x, y);
        var container = new Container(id, coordinates.y, coordinates.x);
        containers.add(container);

        var circuit = getCircuit(circuitName, shift);
        circuit.addContainer(container);
    }

    private Circuit getCircuit(String circuitName, Shift shift) {
        var circuitGroup = circuits.computeIfAbsent(circuitName, k -> new ArrayList<>());
        if (!circuitGroup.isEmpty() && !circuitGroup.getLast().isFull()) {
            return circuitGroup.getLast();
        }

        var circuit = new Circuit(shift);
        circuitGroup.add(circuit);
        return circuit;
    }

    private static Shift readShift(String shiftName) {
        shiftName = shiftName.substring(1, shiftName.length() - 1).trim();
        return switch (shiftName) {
            case "DOMINGOS Y DOMINGOS FERIADOS: Matutino (06 a 14 hrs.)" -> Shift.SUN_MORNING;
            case "LUNES A SABADOS CON FERIADO LABORABLE: Vespertino (14 a 22 hrs.)",
                 "LUNES A SABADOS CON FERIADOS LABORABLES: Vespertino (14 a 22 hrs.)" -> Shift.MON_TO_SAT_AFTERNOON;
            case "DOMINGOS A VIERNES Y TODOS LOS FERIADOS: Nocturno (22 a 06 hrs.)" -> Shift.SUN_TO_FRI_NIGHT;
            case "LUNES, MIERCOLES Y VIERNES CON FERIADOS LABORABLES: Nocturno (22 a 06 hrs.)" -> Shift.MON_WED_FRI_NIGHT;
            case "MARTES JUEVES Y SABADOS CON FERIADOS LABORABLES: Matutino (06 a 14 hrs.)" -> Shift.TUE_THU_SAT_MORNING;
            case "MARTES JUEVES Y SABADOS CON FERIADOS LABORABLES: Vespertino (14 a 22 hrs.)" -> Shift.TUE_THU_SAT_AFTERNOON;
            case "MARTES JUEVES Y SABADOS CON FERIADOS LABORABLES: Nocturno (22 a 06 hrs.)" -> Shift.TUE_THU_SAT_NIGHT;
            case "DOMINGOS, MIERCOLES Y VIERNES: Vespertino (14 a 22 hrs.)" -> Shift.SUN_WED_FRI_AFTERNOON;
            case "LUNES, MIERCOLES Y VIERNES CON FERIADOS LABORABLES: Matutino (06 a 14 hrs.)" -> Shift.MON_WED_FRI_MORNING;
            case "LUNES, MIERCOLES Y VIERNES CON FERIADOS LABORABLES: Vespertino (14 a 22 hrs.)" -> Shift.MON_WED_FRI_AFTERNOON;
            case "DOMINGOS, MARTES Y JUEVES CON FERIADOS LABORABLES: Nocturno (22 a 06 hrs.)" -> Shift.SUN_TUE_THU_NIGHT;
            case "DOMINGOS, MARTES Y JUEVES CON FERIADOS LABORABLES: Matutino (06 a 14 hrs.)", "" -> Shift.SUN_TUE_THU_MORNING;
            default -> throw new IllegalArgumentException("Invalid shift: " + shiftName);
        };
    }

    public List<Container> getContainers() {
        return containers;
    }

    public List<Circuit> getCircuits() {
        return circuits.values().stream().flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
    }

    private static ProjCoordinate transformCoordinates(double x, double y) {
        var result = new ProjCoordinate();
        transform.transform(new ProjCoordinate(x, y), result);
        return result;
    }
}
