package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Circuit {
    private Day day;
    private Shift shift;
    private int truckId;
    private double cost;
    private ArrayList<Container> containers = new ArrayList<>();

    private double calculateCost() {
        if (containers.size() > Constants.TRUCK_CAPACITY) {
            return Constants.INFINITE_COST;
        }

        cost = 0;
        for (Container container : containers) {
            cost += 100; // Placeholder for the cost calculation
        }

        return cost;
    }

    public Iterable<Container> getContainers() {
        return containers;
    }
}
