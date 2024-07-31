package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.concurrent.*;

public class Main {
	private static final String CONTAINERS_PATH = "data/containers.csv";
	private static final String OSM_PATH = "data/uruguay-latest.osm.pbf";

	public static void main(String[] args) {
		var dataSource = new DataSource(CONTAINERS_PATH);
		dataSource.load();

		var containers = dataSource.getContainers();
		var circuits = dataSource.getCircuits();

		Router.getInstance().init(containers, OSM_PATH);

		var solution = new Solution(circuits);

		System.out.println("Costo inicial: " + solution.computeCost());

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


		System.out.println("Costo final: " + solution.computeCost());
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
