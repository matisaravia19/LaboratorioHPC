package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;

public class Circuit {
    private Day day;
    private Shift shift;
    private int truckId;
    private long cost;
    private List<Container> containers;

    public Circuit(Shift shift) {
        this.shift = shift;
        containers = new ArrayList<>();
    }

    public Circuit(List<Container> containers) {
        this.containers = containers;
    }

    public double calculateCost() {
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

    public List<Container> getContainers() {
        return containers;
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public boolean isFull() {
        return containers.size() == Constants.TRUCK_CAPACITY;
    }
}
