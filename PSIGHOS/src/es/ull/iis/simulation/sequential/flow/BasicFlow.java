/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.SimulationObject;
import es.ull.iis.simulation.sequential.WorkThread;


/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iván Castilla Rodríguez
 */
public abstract class BasicFlow extends SimulationObject implements Flow<WorkThread> {
	/** The structured flow containing this flow. */
	protected StructuredFlow parent = null;
	
	/**
	 * Create a new basic flow.
	 * @param simul The simulation this flow belongs to.
	 */
	public BasicFlow(Simulation simul) {
		super(simul.getNextFlowId(), simul);
		simul.add(this);
	}
	
	/**
	 * Requests this flow successor(s) to continue the execution. This method is invoked 
	 * after all the tasks associated to this flow has been successfully carried out.
	 * Assigns this flow as the last flow visited by the work thread.
	 * @param wThread Work thread which requested this flow.
	 */
	public void next(WorkThread wThread) {
		wThread.setLastFlow(this);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#getParent()
	 */
	public StructuredFlow getParent() {
		return parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#setParent(es.ull.iis.simulation.StructuredFlow)
	 */
	public void setParent(es.ull.iis.simulation.core.flow.StructuredFlow<WorkThread> parent) {
		this.parent = (StructuredFlow)parent;
	}

	/**
	 * By default, returns true.
	 * @return True by default.
	 */
	public boolean beforeRequest(es.ull.iis.simulation.core.Element<WorkThread> e) {
		return true;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "F";
	}
	
	
}
