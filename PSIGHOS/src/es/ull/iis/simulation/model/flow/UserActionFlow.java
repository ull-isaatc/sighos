/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;

/**
 * A simple flow to execute user actions at some point of the workflow. The user specifies the actions in the {@link #userAction(ElementInstance)} method.
 * @author Iván Castilla
 *
 */
public class UserActionFlow extends SingleSuccessorFlow implements ActionFlow {
	/** A brief description of the flow */
	final private String description;
	/**
	 * Creates a user action flow
	 * @param model The simulation model this flow belongs to
	 * @param description A brief description of the flow
	 */
	public UserActionFlow(final Simulation model, String description) {
		super(model);
		this.description = description;
	}

	@Override
	public void addPredecessor(Flow predecessor) {
	}

	/**
	 * The code that this flow executes
	 * @param ei An element instance invoking this flow
	 */
	public void userAction(ElementInstance ei) {
		
	}
	@Override
	public void request(ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					userAction(ei);
					next(ei);
				}
				else {
					ei.cancel(this);
					next(ei);
				}
			}
			else {
				ei.updatePath(this);
				next(ei);
			}
		} else
			ei.notifyEnd();
	}

	@Override
	public String getDescription() {
		return description;
	}

}
