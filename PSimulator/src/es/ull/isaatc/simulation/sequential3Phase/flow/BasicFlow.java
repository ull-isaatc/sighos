/**
 * 
 */
package es.ull.isaatc.simulation.sequential3Phase.flow;

import es.ull.isaatc.simulation.sequential3Phase.Simulation;
import es.ull.isaatc.simulation.sequential3Phase.SimulationObject;
import es.ull.isaatc.simulation.sequential3Phase.WorkThread;


/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class BasicFlow extends SimulationObject implements Flow {
	/** Generator of unique identifiers */
	private static int counter = 0;
	/** The structured flow containing this flow. */
	protected StructuredFlow parent = null;
	
	/**
	 * Create a new basic flow.
	 * @param simul The simulation this flow belongs to.
	 */
	public BasicFlow(Simulation simul) {
		super(counter++, simul);
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
	 * @see es.ull.isaatc.simulation.Flow#getParent()
	 */
	public StructuredFlow getParent() {
		return parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#setParent(es.ull.isaatc.simulation.StructuredFlow)
	 */
	public void setParent(es.ull.isaatc.simulation.common.flow.StructuredFlow parent) {
		this.parent = (StructuredFlow)parent;
	}

	/**
	 * By default, returns true.
	 * @return True by default.
	 */
	public boolean beforeRequest(es.ull.isaatc.simulation.common.Element e) {
		return true;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "F";
	}
	
	
}