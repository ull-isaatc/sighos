/**
 * 
 */
package es.ull.isaatc.simulation.intervalThreaded.flow;

import es.ull.isaatc.simulation.intervalThreaded.Simulation;
import es.ull.isaatc.simulation.intervalThreaded.WorkThread;

/**
 * A merge flow which allows only one of the incoming branches to pass. Which one
 * passes depends on the <code>acceptValue</code>.
 * @author Iván Castilla Rodríguez
 */
public abstract class ANDJoinFlow extends MergeFlow implements es.ull.isaatc.simulation.common.flow.ANDJoinFlow {
	/** The number of branches which have to arrive to pass the control thread */
	protected int acceptValue;

	/**
	 * Creates a new AND flow.
	 * @param simul Simulation this flow belongs to.
	 */
	public ANDJoinFlow(Simulation simul) {
		super(simul);
	}
	
	/**
	 * Creates a new AND flow
	 * @param simul Simulation this flow belongs to
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(Simulation simul, int acceptValue) {
		super(simul);
		this.acceptValue = acceptValue;
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public ANDJoinFlow(Simulation simul, boolean safe) {
		super(simul, safe);
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param simul Simulation this flow belongs to
	 * @param safe True for safe context; false in other case
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(Simulation simul, boolean safe, int acceptValue) {
		super(simul, safe);
		this.acceptValue = acceptValue;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.MergeFlow#canPass(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	protected boolean canPass(WorkThread wThread) {
		return (!control.get(wThread.getElement()).isActivated() 
				&& (control.get(wThread.getElement()).getTrueChecked() == acceptValue));
	}

	@Override
	public int getAcceptValue() {
		return acceptValue;
	}	

}
