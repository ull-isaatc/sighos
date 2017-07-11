/**
 * 
 */
package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.engine.EngineObject;

/**
 * @author Iván Castilla
 *
 */
public class ElementInstanceEngine extends EngineObject
		implements es.ull.iis.simulation.model.engine.ElementInstanceEngine {
	/** Element instance's counter. Useful for identifying each instance */
	// Must start in 1 to avoid problems with internal control of request flows
	private static int counter = 1;
	/** Associated {@link ElementInstance} */
	private final ElementInstance modelInstance;

	/**
	 * @param id
	 * @param simul
	 * @param objTypeId
	 */
	public ElementInstanceEngine(SequentialSimulationEngine simul, ElementInstance modelInstance) {
		super(counter++, simul, "EI");
		this.modelInstance = modelInstance;
	}

	/**
	 * @return the modelInstance
	 */
	public ElementInstance getModelInstance() {
		return modelInstance;
	}

	@Override
	public void notifyResourcesAcquired() {
		// Nothing to do
	}

}
