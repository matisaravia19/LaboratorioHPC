package uy.edu.fing.hpc;

import java.util.concurrent.*;

public class Worker implements Callable<Solution> {
	private Solution solution;
	private double temperature;

	public Worker(Solution initialSolution, double temperature) {
		this.solution = initialSolution;
		this.temperature = temperature;
	}

	@Override
	public Solution call() {
		int i = 0;
		while (i < Constants.ITERATIONS_PER_WORKER) {
			var neighbor = solution.getNeighbor();
			if (acceptNeighbor(solution, neighbor)) {
				solution = neighbor;
			}

			i++;
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
