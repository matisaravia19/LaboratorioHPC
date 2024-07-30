package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

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

		long cost = initialSolution.computeCost();

		System.out.println("Costo inicial: " + cost);

		var solutionList = initialSolution.splitByShifts();
//			var solutionList = new ArrayList<Solution>();
//			solutionList.add(initialSolution);

		try (var executorService = Executors.newFixedThreadPool(solutionList.size())) {
			List<Future<Solution>> futures = new ArrayList<>();
			for (var solution : solutionList) {
				futures.add(executorService.submit(new Worker(solution)));
			}

			List<Solution> solutions = new ArrayList<>();
			for (Future<Solution> future : futures) {
				solutions.add(future.get());
			}

			var finalSolution = Solution.merge(solutions);

			System.out.println("Costo final: " + finalSolution.computeCost());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
