package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solution {
    private long cost;
    private List<Circuit> circuits;

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
        var randomNeighborType = NeighborType.getRandom();
        return getNegihbor(randomNeighborType);
    }

    public Solution getNegihbor(NeighborType type){
        return switch (type) {
            case SWITCH_TWO_RANDOM_CONTAINERS_IN_CIRCUIT -> getNeighborSwitchTwoRandomContainersInCircuit();
            case SWITCH_TWO_RANDOM_CONTAINERS_BETWEEN_CIRCUITS -> getNeighborSwitchTwoRandomContainersBetweenCircuits();
            case MOVE_CONTAINER_BETWEEN_CIRCUITS -> getNeighborMoveContainerBetweenCircuits();
            case SPLIT_CIRCUITS -> getNeighborSplitCircuits();
            case MERGE_CIRCUITS -> getNeighborMergeCircuits();
        };
    }

    private Solution getNeighborSwitchTwoRandomContainersInCircuit() {
        var neighbor = copy();

        var circuit = neighbor.copyRandomCircuit();
        circuit.switchTwoRandomContainers();

        return neighbor;
    }

    private Solution getNeighborSwitchTwoRandomContainersBetweenCircuits() {
        var neighbor = copy();

        var circuit1 = neighbor.copyRandomCircuit();
        var circuit2 = neighbor.copyRandomCircuitWithSameShiftAs(circuit1);

        var container1 = circuit1.getRandomContainer();
        var container2 = circuit2.getRandomContainer();

        circuit1.replaceContainer(container1, container2);
        circuit2.replaceContainer(container2, container1);

        return neighbor;
    }

    private Solution getNeighborMoveContainerBetweenCircuits() {
        var neighbor = copy();

        var circuit1 = neighbor.copyRandomCircuit();
        var circuit2 = neighbor.copyRandomCircuitWithSameShiftAs(circuit1);

        if (circuit1.isEmpty() || circuit2.isFull()) {
            return neighbor;
        }

        var container = circuit1.getRandomContainer();
        circuit1.removeContainer(container);
        circuit2.addContainer(container);

        return neighbor;
    }

    private Solution getNeighborSplitCircuits() {
        var neighbor = copy();

        var circuit = neighbor.copyRandomCircuit();

        var newCircuit = circuit.split();
        neighbor.circuits.add(newCircuit);

        return neighbor;
    }

    private Solution getNeighborMergeCircuits() {
        var neighbor = copy();

        var circuit1 = neighbor.copyRandomCircuit();
        var circuit2 = neighbor.copyRandomCircuitWithSameShiftAs(circuit1);

        var couldMerge = circuit1.merge(circuit2);

        if (couldMerge) {
            neighbor.circuits.remove(circuit2);
        }

        return neighbor;
    }

    private Circuit copyRandomCircuit() {
        var i = Random.getRandomIndex(circuits.size());
        var circuit = circuits.get(i).copy();
        circuits.set(i, circuit);
        return circuit;
    }

    private Circuit copyRandomCircuitWithSameShiftAs(Circuit circuit) {
        var circuitsWithSameShift = circuits.stream()
                .filter(c -> c.getShift() == circuit.getShift() && c != circuit)
                .toList();
        var i = Random.getRandomIndex(circuitsWithSameShift.size());
        var circuitCopy = circuitsWithSameShift.get(i).copy();
        circuits.set(circuits.indexOf(circuitsWithSameShift.get(i)), circuitCopy);
        return circuitCopy;
    }

    private Solution fullCopy() {
        var circuitsCopy = circuits.stream().map(Circuit::copy).toList();
        return new Solution(circuitsCopy);
    }

    private Solution copy() {
        var circuitsCopy = new ArrayList<>(circuits);
        return new Solution(circuitsCopy);
    }
}
