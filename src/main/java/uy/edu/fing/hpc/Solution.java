package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solution {
    private long cost;
    private List<Circuit> circuits;
    private HashMap<Container, ArrayList<Circuit>> containerToCircuits = new HashMap<>();

    public Solution(List<Circuit> circuits) {
        this.circuits = circuits;
    }

    public long computeCost() {
        long cost = 0;
        for (var circuit : circuits) {
            cost += circuit.getCost();
        }
        return cost;
    }

    public Solution getNeighbor() {
        var neighbor = copy();

        var i = (int) (Math.random() * circuits.size());
        var circuit = neighbor.circuits.get(i).copy();
        neighbor.circuits.set(i, circuit);

        circuit.switchTwoRandomContainers();

        return neighbor;
    }

    public Solution fullCopy() {
        var circuitsCopy = circuits.stream().map(Circuit::copy).toList();
        return new Solution(circuitsCopy);
    }

    private Solution copy() {
        var circuitsCopy = new ArrayList<>(circuits);
        return new Solution(circuitsCopy);
    }

    private void initContainerToCircuits() {
        containerToCircuits.clear();
        for (Circuit circuit : circuits) {
            for (Container container : circuit.getContainers()) {
                if (!containerToCircuits.containsKey(container)) {
                    containerToCircuits.put(container, new ArrayList<>());
                }
                containerToCircuits.get(container).add(circuit);
            }
        }
    }
}
