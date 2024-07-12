package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;

public class Circuit {
    private Shift shift;
    private int truckId;
    private long cost;
    private final List<Container> containers;

    public Circuit(Shift shift) {
        this.shift = shift;
        containers = new ArrayList<>();
        cost = -1;
    }

    private Circuit(Shift shift, List<Container> containers) {
        this.shift = shift;
        this.containers = containers;
        cost = -1;
    }

    public long getCost() {
        if (cost >= 0) {
            return cost;
        }

        if (containers.size() > Constants.TRUCK_CAPACITY) {
            return Long.MAX_VALUE;
        }

        cost = 0;
        for (int i = 1; i < containers.size(); i++) {
            Container container = containers.get(i);
            Container previousContainer = containers.get(i - 1);
            cost += Router.getInstance().getRouteTime(previousContainer, container);
        }

        cost += Router.getInstance().getRouteTimeFromLandfill(containers.getFirst());
        cost += Router.getInstance().getRouteTimeToLandfill(containers.getLast());

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

    public void switchTwoRandomContainers() {
        int i = (int) (Math.random() * containers.size());
        int j = (int) (Math.random() * containers.size());
        Container temp = containers.get(i);
        containers.set(i, containers.get(j));
        containers.set(j, temp);
    }

    public Circuit copy() {
        return new Circuit(shift, new ArrayList<>(containers));
    }
}
