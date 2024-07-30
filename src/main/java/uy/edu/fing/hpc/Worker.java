package uy.edu.fing.hpc;

import java.util.concurrent.*;

public class Worker implements Callable<Solution> {
	private final Solution initialSolution;
	private final double temperature;

	public Worker(Solution initialSolution, double temperature) {
		this.initialSolution = initialSolution;
		this.temperature = temperature;
	}

	@Override
	public Solution call() {
		var bestSolution = initialSolution;

		int i = 0;
		while (i < Constants.ITERATIONS_PER_WORKER) { //Se puede agregar condicion de que si llega a una solucion suficientemente chica deje de iterar
			var neighbor = bestSolution.getNeighbor();
			if (acceptNeighbor(bestSolution, neighbor)) {
				bestSolution = neighbor;
			}

			i++;
		}

		return bestSolution;
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
