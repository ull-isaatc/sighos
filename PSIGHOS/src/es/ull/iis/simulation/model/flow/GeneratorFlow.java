/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Generator;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class GeneratorFlow extends SingleSuccessorFlow implements TaskFlow, ActionFlow {
    /** A brief description of the kind of generation performed */
    private final String description;
    /** Generador de elementos */
    private final Generator<? extends Generator.GenerationInfo> generator;

	/**
	 * @param model
	 */
	public GeneratorFlow(Simulation model, String description, Generator<? extends Generator.GenerationInfo> generator) {
		super(model);
		this.description = description;
		this.generator = generator;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void addPredecessor(Flow predecessor) {
	}

	@Override
	public void request(ElementInstance ei) {
		if (!ei.wasVisited(this)) {
			if (ei.isExecutable()) {
				if (beforeRequest(ei)) {
					generator.create();
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
