package uy.edu.fing.hpc;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {
    private long cost;
    private List<Circuit> circuits;

    public Solution(List<Circuit> circuits) {
        this.circuits = circuits;
        this.cost = -1;
    }

    public List<Circuit> getCircuits() {
        return circuits;
    }

    public long computeCost() {
        if (cost >= 0) {
            return cost;
        }

        long cost = 0;
        for (var circuit : circuits) {
            cost += circuit.getCost();
        }
        return cost;
    }

    public List<Solution> splitByShifts(int n) {
        var completeSplit = splitByShifts();
        return Lists.partition(completeSplit, completeSplit.size() / n).stream()
                .map(Solution::merge)
                .toList();
    }

    public List<Solution> splitByShifts() {
        return circuits.stream()
                .collect(Collectors.groupingBy(Circuit::getShift))
                .values().stream()
                .map(circuits -> new Solution(new ArrayList<>(circuits)))
                .toList();
    }

    public static Solution merge(List<Solution> solutions) {
        var circuits = solutions.stream()
                .map(solution -> solution.circuits)
                .flatMap(List::stream)
                .toList();
        return new Solution(circuits);
    }

    public Solution getNeighbor() {
        var randomNeighborType = NeighborType.getRandom();
        return getNeighbor(randomNeighborType);
    }

    public Solution getNeighbor(NeighborType type){
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

        if (circuit.isEmpty()) {
            return neighbor;
        }

        circuit.switchTwoRandomContainers();

        return neighbor;
    }

    private Solution getNeighborSwitchTwoRandomContainersBetweenCircuits() {
        var neighbor = copy();

        var circuit1 = neighbor.copyRandomCircuit();
        var circuit2 = neighbor.copyRandomCircuitWithSameShiftAs(circuit1);

        if (circuit1.isEmpty() || circuit2.isEmpty()) {
            return neighbor;
        }

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

        if (circuit1.isEmptyOrJustOne() || circuit2.isFull()) {
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

        var circuitsInShift = neighbor.circuits.stream()
                .filter(c -> c.getShift() == circuit.getShift())
                .count();
        if (circuitsInShift >= Constants.TRUCK_COUNT) {
            return neighbor;
        }

        if (circuit.isEmptyOrJustOne()){
            return neighbor;
        }

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

    private Solution copy() {
        var circuitsCopy = new ArrayList<>(circuits);
        return new Solution(circuitsCopy);
    }
}
