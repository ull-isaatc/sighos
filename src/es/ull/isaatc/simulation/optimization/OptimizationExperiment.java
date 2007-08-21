/**
 * 
 */
package es.ull.isaatc.simulation.optimization;

import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Simulation;

/**
 * @author Roberto
 *
 */
public abstract class OptimizationExperiment extends PooledExperiment {


	protected abstract void run();
	
	protected abstract void init();
	

}
