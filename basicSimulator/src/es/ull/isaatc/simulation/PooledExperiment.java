/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.simulation.state.processor.StateProcessor;

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
	 * @param description
	 * @param nExperiments
	 * @param processor
	 */
	public PooledExperiment(String description, int nExperiments, StateProcessor processor) {
		this(description, nExperiments, Executors.newSingleThreadExecutor(), processor);
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param tp
	 * @param processor
	 */
	public PooledExperiment(String description, int nExperiments, ExecutorService tp, StateProcessor processor) {
		super(description, nExperiments, processor);
        this.tp = tp;
	}
	
	public void start() {
		ArrayList<Simulation> sims = new ArrayList<Simulation>(nExperiments);
		for (int i = 0; i < nExperiments; i++)
			sims.add(getSimulation(i));
		try {
			List<Future<SimulationState>> results = tp.invokeAll(sims);
			for (Future<SimulationState> future : results)
				processor.process(future.get());				
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		tp.shutdown();
		end();
	}


}
