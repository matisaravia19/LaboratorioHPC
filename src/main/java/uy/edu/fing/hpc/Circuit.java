package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;

public class Circuit {
    private final Shift shift;
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

    public Shift getShift() {
        return shift;
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public void removeContainer(Container container) {
        containers.remove(container);
    }

    public boolean isFull() {
        return containers.size() == Constants.TRUCK_CAPACITY;
    }

    public boolean isEmpty() {
        return containers.isEmpty();
    }

    public Container getRandomContainer() {
        return containers.get(Random.getRandomIndex(containers.size()));
    }

    public void switchTwoRandomContainers() {
        int i = (int) (Math.random() * containers.size());
        int j = (int) (Math.random() * containers.size());
        Container temp = containers.get(i);
        containers.set(i, containers.get(j));
        containers.set(j, temp);
    }

    public void replaceContainer(Container oldContainer, Container newContainer) {
        int index = containers.indexOf(oldContainer);
        containers.set(index, newContainer);
    }

    public Circuit split() {
        if (isEmpty()) {
            return new Circuit(shift);
        }

        var i = Random.getRandomIndex(containers.size());

        var firstHalf = containers.subList(0, i);
        var secondHalf = containers.subList(i, containers.size());

        var newCircuit = new Circuit(shift, new ArrayList<>(secondHalf));

        containers.clear();
        containers.addAll(firstHalf);

        return newCircuit;
    }

    public boolean merge(Circuit other) {
        if (containers.size() + other.containers.size() > Constants.TRUCK_CAPACITY) {
            return false;
        }

        containers.addAll(other.containers);
        other.containers.clear();

        return true;
    }

    public Circuit copy() {
        return new Circuit(shift, new ArrayList<>(containers));
    }
}
