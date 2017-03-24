/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;


/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iván Castilla Rodríguez
 */
public abstract class BasicFlow extends SimulationObject implements Flow {
	/** The structured flow containing this flow. */
	protected StructuredFlow parent = null;
	
	/**
	 * Create a new basic flow.
	 * @param simul The simulation this flow belongs to.
	 */
	public BasicFlow(Simulation model) {
		super(model, model.getFlowList().size(), "F");
		model.add(this);
	}
	
	@Override
	public StructuredFlow getParent() {
		return parent;
	}
	
	@Override
	public void setParent(StructuredFlow parent) {
		this.parent = (StructuredFlow)parent;
	}

	/**
	 * By default, returns true.
	 * @return True by default.
	 */
	@Override
	public boolean beforeRequest(ElementInstance fe) {
		return true;
	}

	/**
	 * Assigns this flow as the last flow visited by the work thread.
	 * @param wThread Work thread which requested this flow.
	 */
	public void next(final ElementInstance wThread) {
		wThread.setLastFlow(this);
	}

	@Override
	public void assignSimulation(SimulationEngine simul) {
	}
}
