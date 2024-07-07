package uy.edu.fing.hpc;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DataSource {
    private static final CoordinateTransform transform;

    static {
        CRSFactory crsFactory = new CRSFactory();
        var sourceCrs = crsFactory.createFromName("EPSG:31981");
        var targetCrs = crsFactory.createFromName("EPSG:4326");

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        transform = ctFactory.createTransform(sourceCrs, targetCrs);
    }

    public static List<Container> readContainers(String filename) {
        try (var stream = Files.lines(Paths.get(filename))) {
            return stream.skip(1).map(DataSource::readContainer).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Container readContainer(String line) {
        var parts = line.split(";");

        var id = Integer.parseInt(parts[0]);
        var frequency = getFrequency(parts[2]);

        var x = Double.parseDouble(parts[3]);
        var y = Double.parseDouble(parts[4]);
        var coordinates = transformCoordinates(x, y);

        return new Container(id, frequency, coordinates.y, coordinates.x);
    }

    private static ProjCoordinate transformCoordinates(double x, double y) {
        var result = new ProjCoordinate();
        transform.transform(new ProjCoordinate(x, y), result);
        return result;
    }

    private static int getFrequency(String shiftName) {
        shiftName = shiftName.substring(1).split(":", 2)[0];
        return switch (shiftName) {
            case "DOMINGOS A VIERNES Y TODOS LOS FERIADOS", "LUNES A SABADOS CON FERIADO LABORABLE",
                 "LUNES A SABADOS CON FERIADOS LABORABLES" -> 6;
            case "DOMINGOS Y DOMINGOS FERIADOS" -> 1;
            default -> 3;
        };
    }
}
