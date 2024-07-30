package uy.edu.fing.hpc;

import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.GHUtility;
import com.graphhopper.util.shapes.GHPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Router {
    private static final Router instance = new Router();
    private static final GHPoint landfillPoint = new GHPoint(-34.84856638301756, -56.0942988400724);

    private Map<Integer, GHPoint> points;
    private GraphHopper hopper;

    private ConcurrentHashMap<Route, Long> routeCache;

    private Router() {
        routeCache = new ConcurrentHashMap<>();
    }

    public static Router getInstance() {
        return instance;
    }

    public void init(List<Container> containers, String osmPath) {
        this.points = new HashMap<>(containers.size());
        for (var container : containers) {
            points.put(container.getId(), new GHPoint(container.getLatitude(), container.getLongitude()));
        }

        hopper = new GraphHopper();
        hopper.setOSMFile(osmPath);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation("data/routing-graph-cache");

        // add all encoded values that are used in the custom model, these are also available as path details or for client-side custom models
        hopper.setEncodedValuesString("car_access, car_average_speed");
        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile("car").setCustomModel(GHUtility.loadCustomModelFromJar("car.json")));

        // this enables speed mode for the profile we called car
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));

        // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
        hopper.importOrLoad();
    }

    public long getRouteTime(Container from, Container to) {
        var fromPoint = points.get(from.getId());
        var toPoint = points.get(to.getId());

        return getRoute(fromPoint, toPoint);
    }

    public long getRouteTimeToLandfill(Container from) {
        var fromPoint = points.get(from.getId());
        return getRoute(fromPoint, landfillPoint);
    }

    public long getRouteTimeFromLandfill(Container to) {
        var toPoint = points.get(to.getId());
        return getRoute(landfillPoint, toPoint);
    }

    private long getRoute(GHPoint from, GHPoint to) {
        var route = new Route(from, to);

        var cost = routeCache.get(route);
        if (cost != null) {
            return cost;
        }

        cost = route.calculate(hopper);
        routeCache.put(route, cost);

        return cost;
    }
}
