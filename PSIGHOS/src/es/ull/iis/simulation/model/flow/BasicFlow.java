/**
 * 
 */
package es.ull.iis.simulation.model.flow;

/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iván Castilla Rodríguez
 */
public abstract class BasicFlow implements Flow {
	/** The structured flow containing this flow. */
	protected StructuredFlow parent = null;
	
	/**
	 * Create a new basic flow.
	 * @param simul The simulation this flow belongs to.
	 */
	public BasicFlow() {
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

	@Override
	public String getObjectTypeIdentifier() {
		return "F";
	}
	
}
