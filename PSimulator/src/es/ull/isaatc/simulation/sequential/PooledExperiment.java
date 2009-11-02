/**
 * 
 */
package es.ull.isaatc.simulation.sequential;

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
	 * @param description
	 * @param nExperiments
	 */
	public PooledExperiment(String description, int nExperiments) {
		this(description, nExperiments, Executors.newSingleThreadExecutor());
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param tp
	 */
	public PooledExperiment(String description, int nExperiments, ExecutorService tp) {
		super(description, nExperiments);
        this.tp = tp;
	}
	
	/**
	 * Starts the experiment.
	 */
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
