/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.util.ThreadPool;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SimulationExecutorSetter {
	protected final Simulation simul;
	protected final ArrayList<ThreadPool<BasicElement.DiscreteEvent>> executorList;
	
	public SimulationExecutorSetter(Simulation simul) {
		this.simul = simul;
		executorList = new ArrayList<ThreadPool<BasicElement.DiscreteEvent>>();
	}
	
	protected abstract void setExecutors();
	
	protected void shutdownAll() {
		for (ThreadPool<BasicElement.DiscreteEvent> tp : executorList)
			tp.shutdown();
	}
}
