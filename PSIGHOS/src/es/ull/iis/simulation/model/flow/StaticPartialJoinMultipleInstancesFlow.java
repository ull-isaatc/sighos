/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Simulation;

/**
 * Meets the Static Partial Join for Multiple Instances pattern (WFP34) if 
 * <code>acceptValue > 1</code> and <code>acceptValue < nInstances</code>.
 * If <code>nInstances = acceptValue</code> is equivalent to the <code>
 * SynchronizedMultipleInstanceFlow</code>.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StaticPartialJoinMultipleInstancesFlow extends PredefinedStructuredFlow {
	/**	The number of thread instances this flow creates */
	protected int nInstances;
	/** The number of threads which must finish to pass the control */
	protected int acceptValue;

	/**
	 * Creates a new Static Partial Join for Multiple Instances flow
	 * @param nInstances The number of thread instances this flow creates
	 * @param acceptValue The number of threads which must finish to pass the control
	 */
	public StaticPartialJoinMultipleInstancesFlow(Simulation model, int nInstances, int acceptValue) {
		super(model);
		initialFlow = new ThreadSplitFlow(model, nInstances);
		initialFlow.setParent(this);
		finalFlow = new ThreadMergeFlow(model, nInstances, acceptValue);
		finalFlow.setParent(this);
		this.nInstances = nInstances;
		this.acceptValue = acceptValue;
	}

	/**
	 * Returns the number of threads which must finish to pass the control.
	 * @return The number of threads which must finish to pass the control
	 */
	public int getAcceptValue() {
		return acceptValue;
	}

	/**
	 * Returns the number of thread instances created in this flow.
	 * @return The number of thread instances created in this flow
	 */
	public int getNInstances() {
		return nInstances;
	}


}
