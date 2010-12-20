/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes a set of simulations in a thread pool. This type of experiment should be
 * used when measuring execution times in a single processor computer.<p>
 * The constructors which don't include an <code>ExecutorService</code> involve a stand-alone
 * thread execution (<code>Executors.newFixedThreadPool(1)</code>), that is, a sequential execution.   
 * @author Iván Castilla Rodríguez
 */
public abstract class PooledExperiment extends Experiment {
    /** Thread pool to execute events */
    protected ExecutorService tp;

	/**
	 * Default constructor
	 */
	public PooledExperiment() {		
	}
	
	/**
	 * Creates a stand-alone thread execution (<code>Executors.newFixedThreadPool(1)</code>). A single
	 * thread executes sequentially all the simulations.
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 */
	public PooledExperiment(String description, int nExperiments) {
		this(description, nExperiments, Executors.newSingleThreadExecutor());
	}
	
	/**
	 * Creates a customized simulation executor.
	 * @param description A short text describing this experiment
	 * @param nExperiments Number of experiments to be carried out
	 * @param tp The thread pool used to execute the simulations
	 */
	public PooledExperiment(String description, int nExperiments, ExecutorService tp) {
		super(description, nExperiments);
        this.tp = tp;
	}
	
	/**
	 * Starts the experiment. Simply creates all the simulations and send them to the thread pool.
	 */
	@Override
	public void start() {
		ArrayList<Callable<Integer>> sims = new ArrayList<Callable<Integer>>(nExperiments);
		for (int i = 0; i < nExperiments; i++)
			sims.add(getSimulation(i));
		try {
			tp.invokeAll(sims);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		tp.shutdown();
		end();
	}


}
