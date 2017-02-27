/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ModelObject;
import es.ull.iis.simulation.model.engine.SimulationEngine;


/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iván Castilla Rodríguez
 */
public abstract class BasicFlow extends ModelObject implements Flow {
	/** The structured flow containing this flow. */
	protected StructuredFlow parent = null;
	
	/**
	 * Create a new basic flow.
	 * @param simul The simulation this flow belongs to.
	 */
	public BasicFlow(Model model) {
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
	public boolean beforeRequest(FlowExecutor fe) {
		return true;
	}

	/**
	 * Assigns this flow as the last flow visited by the work thread.
	 * @param wThread Work thread which requested this flow.
	 */
	public void next(final FlowExecutor wThread) {
		wThread.setLastFlow(this);
	}

	@Override
	public void assignSimulation(SimulationEngine simul) {
	}
}
