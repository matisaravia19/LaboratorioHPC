package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Worker implements Callable<Solution> {
	private Solution solution;
	private double temperature;

	public Worker(Solution initialSolution) {
		this.solution = initialSolution;
		this.temperature = Constants.INITIAL_TEMPERATURE;
	}

	@Override
	public Solution call() {
		while (temperature > Constants.MIN_TEMPERATURE) {
			int i = 0;
			while (i < Constants.ITERATIONS_PER_WORKER) { //Se puede agregar condicion de que si llega a una solucion suficientemente chica deje de iterar
				var neighbor = solution.getNeighbor();
				if (acceptNeighbor(solution, neighbor)) {
					solution = neighbor;
				}

				i++;
			}

			temperature *= Constants.TEMPERATURE_DECREASE_FACTOR;
		}

		return solution;
	}

	private boolean acceptNeighbor(Solution currentSolution, Solution neighbor) {
		long currentCost = currentSolution.computeCost();
		long neighborCost = neighbor.computeCost();
		if (neighborCost < currentCost) {
			return true;
		}

		double probability = Math.exp((currentCost - neighborCost) / temperature);
		return Random.nextDouble() < probability;
	}
}
