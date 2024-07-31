package uy.edu.fing.hpc;

import java.time.Duration;

public class Main {
	private static final String CONTAINERS_PATH = "data/containers.csv";
	private static final String OSM_PATH = "data/uruguay-latest.osm.pbf";

	private static final String INITIAL_SOLUTION_PATH = "data/initial-solution.csv";
	private static final String FINAL_SOLUTION_PATH = "data/final-solution.csv";

	public static void main(String[] args) {
		var dataSource = new DataSource(CONTAINERS_PATH);
		dataSource.load();

		var containers = dataSource.getContainers();
		var circuits = dataSource.getCircuits();

		Router.getInstance().init(containers, OSM_PATH);

		var initialSolution = new Solution(circuits);

		System.out.println("Costo inicial: " + initialSolution.computeCost());

		long totalTime = 0;
		Solution solution = null;
		for (int i = 0; i < Constants.PROFILING_ITERATIONS; i++) {
			long start = System.currentTimeMillis();

			solution = run(initialSolution);

			long end = System.currentTimeMillis();
			totalTime += end - start;

			System.out.println("Costo final iteración " + i + ": " + solution.computeCost());

			Router.getInstance().resetCache();
		}

		var averageTime = Duration.ofMillis(totalTime / Constants.PROFILING_ITERATIONS);
		System.out.println("Tiempo promedio: " + averageTime.toString());

		DataSource.saveSolution(FINAL_SOLUTION_PATH, solution);

		System.out.println("Costo final: " + solution.computeCost());
	}

	private static Solution run(Solution solution) {
		double temperature = Constants.INITIAL_TEMPERATURE;
		while (temperature > Constants.MIN_TEMPERATURE) {
			int i = 0;
			while (i < Constants.ITERATIONS_PER_WORKER) {
				var neighbor = solution.getNeighbor();
				if (acceptNeighbor(solution, neighbor, temperature)) {
					solution = neighbor;
				}

				i++;
			}

			if(solution.computeCost() < Constants.EXPECTED_COST) {
				break;
			}

			temperature *= Constants.TEMPERATURE_DECREASE_FACTOR;
		}

		return solution;
	}

	private static boolean acceptNeighbor(Solution currentSolution, Solution neighbor, double temperature) {
		long currentCost = currentSolution.computeCost();
		long neighborCost = neighbor.computeCost();
		if (neighborCost < currentCost) {
			return true;
		}

		double probability = Math.exp((currentCost - neighborCost) / temperature);
		return Random.nextDouble() < probability;
	}
}
