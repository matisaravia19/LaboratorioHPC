package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;

public class Circuit {
    private Day day;
    private Shift shift;
    private int truckId;
    private long cost;
    private List<Container> containers;

    public Circuit(List<Container> containers) {
        this.containers = containers;
    }

    private double calculateCost() {
        if (containers.size() > Constants.TRUCK_CAPACITY) {
            return Constants.INFINITE_COST;
        }

        cost = 0;
        for (int i = 1; i < containers.size(); i++) {
            Container container = containers.get(i);
            Container previousContainer = containers.get(i - 1);
            cost += Router.getInstance().getRouteTime(previousContainer, container);
        }

        return cost;
    }

    public Iterable<Container> getContainers() {
        return containers;
    }
}
