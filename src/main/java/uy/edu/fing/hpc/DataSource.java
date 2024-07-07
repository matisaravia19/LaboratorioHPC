package uy.edu.fing.hpc;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import uy.edu.fing.hpc.im.IMCircuit;
import uy.edu.fing.hpc.im.IMContainer;
import uy.edu.fing.hpc.im.IMShift;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataSource {
    private static final CoordinateTransform transform;
    private static final Pattern polygonPattern = Pattern.compile("POLYGON \\(\\((.*)\\)\\)");

    private static List<IMContainer> containers;
    private static List<IMCircuit> circuits;

    private static Map<String, List<IMContainer>> circuitMap;

    static {
        CRSFactory crsFactory = new CRSFactory();
        var sourceCrs = crsFactory.createFromName("EPSG:31981");
        var targetCrs = crsFactory.createFromName("EPSG:4326");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        transform = ctFactory.createTransform(sourceCrs, targetCrs);
    }

    public static void load(String containerFile, String circuitFile) {
        containers = readContainers(containerFile);
        circuits = readCircuits(circuitFile);

        circuitMap = containers.stream().collect(Collectors.groupingBy(IMContainer::getCircuit));
    }

    private static List<IMContainer> readContainers(String filename) {
        try (var stream = Files.lines(Paths.get(filename))) {
            return stream.skip(1).map(DataSource::readContainer).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static IMContainer readContainer(String line) {
        var parts = line.split(";");

        var id = Integer.parseInt(parts[0]);
        var circuit = parts[1];
        var shift = getShift(parts[2]);
        var x = Double.parseDouble(parts[3]);
        var y = Double.parseDouble(parts[4]);

        return new IMContainer(id, circuit, shift, x, y);
    }

    private static IMShift getShift(String shiftName) {
        shiftName = shiftName.substring(1, shiftName.length() - 1).trim();
        return switch (shiftName) {
            case "DOMINGOS Y DOMINGOS FERIADOS: Matutino (06 a 14 hrs.)" -> IMShift.SUN_MORNING;
            case "LUNES A SABADOS CON FERIADO LABORABLE: Vespertino (14 a 22 hrs.)",
                 "LUNES A SABADOS CON FERIADOS LABORABLES: Vespertino (14 a 22 hrs.)" -> IMShift.MON_TO_SAT_AFTERNOON;
            case "DOMINGOS A VIERNES Y TODOS LOS FERIADOS: Nocturno (22 a 06 hrs.)" -> IMShift.SUN_TO_FRI_NIGHT;
            case "LUNES, MIERCOLES Y VIERNES CON FERIADOS LABORABLES: Nocturno (22 a 06 hrs.)" -> IMShift.MON_WED_FRI_NIGHT;
            case "MARTES JUEVES Y SABADOS CON FERIADOS LABORABLES: Matutino (06 a 14 hrs.)" -> IMShift.TUE_THU_SAT_MORNING;
            case "MARTES JUEVES Y SABADOS CON FERIADOS LABORABLES: Vespertino (14 a 22 hrs.)" -> IMShift.TUE_THU_SAT_AFTERNOON;
            case "MARTES JUEVES Y SABADOS CON FERIADOS LABORABLES: Nocturno (22 a 06 hrs.)" -> IMShift.TUE_THU_SAT_NIGHT;
            case "DOMINGOS, MIERCOLES Y VIERNES: Vespertino (14 a 22 hrs.)" -> IMShift.SUN_WED_FRI_AFTERNOON;
            case "LUNES, MIERCOLES Y VIERNES CON FERIADOS LABORABLES: Matutino (06 a 14 hrs.)" -> IMShift.MON_WED_FRI_MORNING;
            case "LUNES, MIERCOLES Y VIERNES CON FERIADOS LABORABLES: Vespertino (14 a 22 hrs.)" -> IMShift.MON_WED_FRI_AFTERNOON;
            case "DOMINGOS, MARTES Y JUEVES CON FERIADOS LABORABLES: Nocturno (22 a 06 hrs.)" -> IMShift.SUN_TUE_THU_NIGHT;
            case "DOMINGOS, MARTES Y JUEVES CON FERIADOS LABORABLES: Matutino (06 a 14 hrs.)" -> IMShift.SUN_TUE_THU_MORNING;
            case "" -> IMShift.MISSING;
            default -> throw new IllegalArgumentException("Invalid shift: " + shiftName);
        };
    }

    public static List<IMCircuit> readCircuits(String filename) {
        try (var stream = Files.lines(Paths.get(filename))) {
            return stream.skip(1).map(DataSource::readCircuit).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static IMCircuit readCircuit(String line) {
        var parts = line.split(";");

        var id = Integer.parseInt(parts[0]);
        var name = parts[1];

        var matcher = polygonPattern.matcher(parts[3]);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid polygon format: " + parts[3]);
        }

        var points = matcher.group(1).split(",");

        return new IMCircuit(id, name, points);
    }

//    private static Circuit readCircuitOld(String line, Map<String, List<Container>> circuitMap) {
//        var parts = line.split(";");
//
//        var circuitName = parts[1];
//        var containers = circuitMap.get(circuitName);
//
//        var matcher = polygonPattern.matcher(parts[3]);
//        if (!matcher.find()) {
//            throw new IllegalArgumentException("Invalid polygon format: " + parts[3]);
//        }
//
//        var polygon = matcher.group(1);
//        var points = polygon.split(",");
//
//        var day = Day.valueOf(parts[0]);
//        var shift = Shift.valueOf(parts[1]);
//        var truckId = Integer.parseInt(parts[2]);
//
//        var containers = circuitMap.get(parts[3]).stream()
//                .map(Container::getId)
//                .map(Router.getInstance()::getContainer)
//                .collect(Collectors.toList());
//
//        return new Circuit(day, shift, truckId, containers);
//    }

//    private static Container readContainerOld(String line) {
//        var parts = line.split(";");
//
//        var id = Integer.parseInt(parts[0]);
//        var originalCircuit = parts[1];
//        var frequency = getFrequency(parts[2]);
//
//        var x = Double.parseDouble(parts[3]);
//        var y = Double.parseDouble(parts[4]);
//        var coordinates = transformCoordinates(x, y);
//
//        return new Container(id, originalCircuit, frequency, coordinates.y, coordinates.x);
//    }

    private static ProjCoordinate transformCoordinates(double x, double y) {
        var result = new ProjCoordinate();
        transform.transform(new ProjCoordinate(x, y), result);
        return result;
    }

//    private static int getFrequency(String shiftName) {
//        shiftName = shiftName.substring(1).split(":", 2)[0];
//        return switch (shiftName) {
//            case "DOMINGOS A VIERNES Y TODOS LOS FERIADOS", "LUNES A SABADOS CON FERIADO LABORABLE",
//                 "LUNES A SABADOS CON FERIADOS LABORABLES" -> 6;
//            case "DOMINGOS Y DOMINGOS FERIADOS" -> 1;
//            default -> 3;
//        };
//    }
}
