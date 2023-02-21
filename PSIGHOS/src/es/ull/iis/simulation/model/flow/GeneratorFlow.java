/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class GeneratorFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the kind of generation performed */
    private final String description;

	/**
	 * @param model
	 */
	public GeneratorFlow(Simulation model, String description) {
		super(model);
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void addPredecessor(Flow predecessor) {
	}

	public void create(ElementInstance ei) {		
	}
	
	@Override
	public void request(ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					create(ei);
					finish(ei);
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
	public void finish(ElementInstance ei) {
		afterFinalize(ei);
		next(ei);
	}

	@Override
	public void afterFinalize(ElementInstance ei) {
	}
}
