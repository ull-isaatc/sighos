/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.SimulationObject;
import es.ull.iis.simulation.parallel.WorkThread;


/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iván Castilla Rodríguez
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
	 * Assigns this flow as the last flow visited by the work thread.
	 * @param wThread Work thread which requested this flow.
	 */
	public void next(final WorkThread wThread) {
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
	public void setParent(es.ull.iis.simulation.core.flow.StructuredFlow parent) {
		this.parent = (StructuredFlow)parent;
	}

	/**
	 * By default, returns true.
	 * @return True by default.
	 */
	public boolean beforeRequest(es.ull.iis.simulation.core.Element e) {
		return true;
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "F";
	}
}
