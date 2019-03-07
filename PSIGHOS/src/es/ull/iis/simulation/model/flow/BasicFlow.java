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
	 * @param model The simulation this flow belongs to.
	 */
	public BasicFlow(final Simulation model) {
		super(model, model.getFlowList().size(), "F");
		model.add(this);
	}
	
	@Override
	public StructuredFlow getParent() {
		return parent;
	}
	
	@Override
	public void setParent(final StructuredFlow parent) {
		this.parent = (StructuredFlow)parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * By default, returns true.
	 * @return True by default.
	 */
	public boolean beforeRequest(final ElementInstance ei) {
		return true;
	}

	/**
	 * Assigns this flow as the last flow visited by the element instance.
	 * @param ei Element instance which requested this flow.
	 */
	public void next(final ElementInstance ei) {
		ei.setLastFlow(this);
	}

	@Override
	public void assignSimulation(final SimulationEngine simul) {
	}
}
