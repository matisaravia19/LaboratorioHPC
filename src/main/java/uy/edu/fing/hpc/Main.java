package uy.edu.fing.hpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class Main {
	private static final String CONTAINERS_PATH = "data/containers.csv";
	private static final String OSM_PATH = "data/uruguay-latest.osm.pbf";
	private static final int NUM_THREADS = 4; // Número de hilos
	private static final double TEMPERATURE = 100.0; // Initial temperature
	private static final double TEMPERATURE_DECAY = 0.95; // Temperature decay factor

	public static void main(String[] args) {
		try {
			var dataSource = new DataSource(CONTAINERS_PATH);
			dataSource.load();
			double temperature = TEMPERATURE;
			var containers = dataSource.getContainers();
			var circuits = dataSource.getCircuits();

			Router.getInstance().init(containers, OSM_PATH);

			var solution = new Solution(circuits);

			Long cost = solution.computeCost();

			System.out.println("Costo inicial: " + cost);

			// Estructura concurrente para almacenar los vecinos recorridos
			ConcurrentHashMap<Solution, Long> visitedNeighbors = new ConcurrentHashMap<>();
			Long bestFinalCost = cost;
			

			// Semáforo para controlar el acceso a la variable cost y al hash
			Semaphore semaphore = new Semaphore(1);
			Semaphore semaphoreBest = new Semaphore(1);

			// Crear un pool de hilos
			ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

			// Crear tareas para paralelizar las iteraciones
			int totalIterations = 2000;
			int iterationsPerThread = totalIterations / NUM_THREADS;

			while (temperature > 90) {
				List<Future<Solution>> futures = new ArrayList<>();
				for (int i = 0; i < NUM_THREADS; i++) {
					int startIteration = i * iterationsPerThread;
					int endIteration = (i + 1) * iterationsPerThread;
					futures.add(executorService.submit(
							new Worker(solution, startIteration, endIteration, visitedNeighbors, semaphore, cost)));
				}
				
				// Esperar a que todas las tareas terminen
				for (Future<Solution> future : futures) {
					
					solution = future.get();
					cost = visitedNeighbors.get(solution);
					
					semaphoreBest.acquire();
					if(bestFinalCost > cost) {
						bestFinalCost = cost;
					}
					semaphoreBest.release();
				}

				temperature *= TEMPERATURE_DECAY;
			}

			executorService.shutdown();

			System.out.println("Costo final: " + bestFinalCost);
			System.out.println("Listo");
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}

class Worker implements Callable<Solution> {
	private Solution solution;
	private int startIteration;
	private int endIteration;
	private ConcurrentHashMap<Solution, Long> visitedNeighbors;
	private Semaphore semaphore;
	private Long cost;

	public Worker(Solution solution, int startIteration, int endIteration,
			ConcurrentHashMap<Solution, Long> visitedNeighbors, Semaphore semaphore, long cost) {
		this.solution = solution;
		this.startIteration = startIteration;
		this.endIteration = endIteration;
		this.visitedNeighbors = visitedNeighbors;
		this.semaphore = semaphore;
		this.cost = cost;
	}

	@Override
	public Solution call() {
		Solution bestSolution = solution;
		long bestCost = cost;

		int i = startIteration;

		while (i < endIteration) { //Se puede agregar condicion de que si llega a una solucion suficientemente chica deje de iterar
			Solution neighbor = bestSolution.getNeighbor();
			if (!visitedNeighbors.containsKey(neighbor)) {
				long neighborCost = neighbor.computeCost();
				try {
					semaphore.acquire();
					visitedNeighbors.put(neighbor, neighborCost);
					if (neighborCost < bestCost) {
						bestSolution = neighbor;
						bestCost = neighborCost;
						cost = bestCost;
						System.out.println("Nuevo costo: " + neighborCost + " en iteración " + i);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					semaphore.release();
					i++;
				}
			}
		}

		return bestSolution;
	}
}
