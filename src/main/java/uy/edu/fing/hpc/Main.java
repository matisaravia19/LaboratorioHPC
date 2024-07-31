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

		var initialSolution = new Solution(circuits);

		long initialCost = initialSolution.computeCost();

		System.out.println("Costo inicial: " + initialCost);

		var solutionList = initialSolution.splitByShifts(12);

		try (var executorService = Executors.newFixedThreadPool(solutionList.size())) {
			var solutions = new ArrayList<>(solutionList);
			var futures = new ArrayList<Future<Solution>>(solutionList.size());

			double temperature = Constants.INITIAL_TEMPERATURE;
			while (temperature > Constants.MIN_TEMPERATURE) {
				futures.clear();
				for (var solution : solutions) {
					futures.add(executorService.submit(new Worker(solution, temperature)));
				}

				long cost = 0;
				solutions.clear();
				for (var future : futures) {
					var solution = future.get();
					cost += solution.computeCost();
					solutions.add(solution);
				}

				if(cost < Constants.EXPECTED_COST) {
					break;
				}

				//System.out.println("Costo actual: " + cost + " - Temperatura: " + temperature);

				temperature *= Constants.TEMPERATURE_DECREASE_FACTOR;
			}

			var finalSolution = Solution.merge(solutions);

			System.out.println("Costo final: " + finalSolution.computeCost());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
