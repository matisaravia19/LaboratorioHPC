package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.HashMap;

public class Solution {
    private double cost;
    private ArrayList<Circuit> circuits = new ArrayList<>();
    private HashMap<Container, ArrayList<Circuit>> containerToCircuits = new HashMap<>();

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
