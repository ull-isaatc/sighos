/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.ull.isaatc.simulation.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.model.Model;

/**
 * Executes a set of simulations in a thread pool. This type of experiment should be
 * used when measuring execution times in a single processor computer.<p>
 * The constructors which don't include an <code>ExecutorService</code> involve a stand-alone
 * thread execution (<code>Executors.newFixedThreadPool(1)</code>), that is, a sequential execution.   
 * @author Iván Castilla Rodríguez
 */
public class PooledExperiment extends Experiment {
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
	public PooledExperiment(SimulationType type, String description, Model model,int nExperiments) {
		this(type, description, model, nExperiments, Executors.newSingleThreadExecutor());
	}
	
	/**
	 * @param description
	 * @param nExperiments
	 * @param tp
	 */
	public PooledExperiment(SimulationType type, String description, Model model, int nExperiments, ExecutorService tp) {
		super(type, description, model, nExperiments);
        this.tp = tp;
	}
	
	@Override
	public void start() {
		ArrayList<Callable<Integer>> sims = new ArrayList<Callable<Integer>>(nExperiments);
		for (int i = 0; i < nExperiments; i++) {
			Simulation sim = SimulationFactory.getInstance(type, i, model);
			sims.add(sim);
		}
		try {
			tp.invokeAll(sims);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		tp.shutdown();
		end();
	}


}
