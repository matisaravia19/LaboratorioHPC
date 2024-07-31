package uy.edu.fing.hpc;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

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
		var solutionList = initialSolution.splitByShifts(12);

		System.out.println("Costo inicial: " + initialSolution.computeCost());
		DataSource.saveSolution(INITIAL_SOLUTION_PATH, initialSolution);

		long totalTime = 0;
		Solution solution = null;
		for (int i = 0; i < Constants.PROFILING_ITERATIONS; i++) {
			long start = System.currentTimeMillis();

			solution = run(solutionList);

			long end = System.currentTimeMillis();
			totalTime += end - start;

			System.out.println("Costo final iteración " + i + ": " + solution.computeCost());

			Router.getInstance().resetCache();
		}

		var averageTime = Duration.ofMillis(totalTime / Constants.PROFILING_ITERATIONS);
		System.out.println("Tiempo promedio: " + averageTime.toString());

		DataSource.saveSolution(FINAL_SOLUTION_PATH, solution);
	}

	private static Solution run(List<Solution> solutionList) {
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

				temperature *= Constants.TEMPERATURE_DECREASE_FACTOR;
			}

			return Solution.merge(solutions);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
