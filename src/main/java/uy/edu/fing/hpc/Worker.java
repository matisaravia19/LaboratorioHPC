package uy.edu.fing.hpc;

import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

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
