package uy.edu.fing.hpc;

import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;

import java.util.Objects;

public class Route {
    private final GHPoint from;
    private final GHPoint to;

    public Route(GHPoint from, GHPoint to) {
        this.from = from;
        this.to = to;
    }

    public long calculate(GraphHopper hopper) {
        var request = new GHRequest()
                .addPoint(from)
                .addPoint(to)
                .setProfile("car");
        var response = hopper.route(request);

        return response.getBest().getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(from, route.from) && Objects.equals(to, route.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
