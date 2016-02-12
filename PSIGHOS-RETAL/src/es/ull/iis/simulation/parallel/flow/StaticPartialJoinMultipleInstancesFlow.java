/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Simulation;

/**
 * Meets the Static Partial Join for Multiple Instances pattern (WFP34) if 
 * <code>acceptValue > 1</code> and <code>acceptValue < nInstances</code>.
 * If <code>nInstances = acceptValue</code> is equivalent to the <code>
 * SynchronizedMultipleInstanceFlow</code>.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StaticPartialJoinMultipleInstancesFlow extends PredefinedStructuredFlow implements es.ull.iis.simulation.core.flow.StaticPartialJoinMultipleInstancesFlow {
	/**	The number of thread instances this flow creates */
	protected int nInstances;
	/** The number of threads which must finish to pass the control */
	protected int acceptValue;

	/**
	 * Creates a new Static Partial Join for Multiple Instances flow
	 * @param simul Simulation this flow belongs to
	 * @param nInstances The number of thread instances this flow creates
	 * @param acceptValue The number of threads which must finish to pass the control
	 */
	public StaticPartialJoinMultipleInstancesFlow(Simulation simul, int nInstances, int acceptValue) {
		super(simul);
		initialFlow = new ThreadSplitFlow(simul, nInstances);
		initialFlow.setParent(this);
		finalFlow = new ThreadMergeFlow(simul, nInstances, acceptValue);
		finalFlow.setParent(this);
		this.nInstances = nInstances;
		this.acceptValue = acceptValue;
	}

	@Override
	public int getAcceptValue() {
		return acceptValue;
	}

	@Override
	public int getNInstances() {
		return nInstances;
	}


}
