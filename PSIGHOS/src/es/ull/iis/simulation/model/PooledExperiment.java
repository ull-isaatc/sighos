/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.util.concurrent.StandardThreadPool;

/**
 * A class to execute several simulations in parallel. It uses a pool of threads to execute the 
 * simulations
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class PooledExperiment extends Experiment {
	/** Internal structure to execute simulations in parallel */
	final private StandardThreadPool<Simulation> pool;

	/**
	 * @param description
	 * @param nExperiments
	 * @param parallel
	 */
	public PooledExperiment(String description) {
		this(description, Runtime.getRuntime().availableProcessors());
	}

	/**
	 * @param description
	 * @param nExperiments
	 * @param parallel
	 * @param nThreads
	 */
	public PooledExperiment(String description, int nThreads) {
		super(description, 1, true, nThreads);
		pool = StandardThreadPool.getPool(nThreads);
	}

	/** 
	 * Launches a simulation to be executed in the thread pool
	 * @param sim New simulation to execute
	 */
	public void execSimulation(Simulation sim) {
		pool.execute(sim);
	}
	
	/**
	 * Preparation for the parallel execution
	 */
	public abstract void preExecution();
	/**
	 * Parallel execution of simulations
	 */
	public abstract void parallelExecution();
	/**
	 * Steps to be performed after the parallel simulation is finished
	 */
	public abstract void postExecution();
	
	@Override
	public void start() {
		preExecution();
		parallelExecution();
		postExecution();
	}
}
