/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.ElementInstance;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementInstanceEngine extends EngineObject {
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
	public ElementInstanceEngine(SimulationEngine simul, ElementInstance modelInstance) {
		super(counter++, simul, "EI");
		this.modelInstance = modelInstance;
	}

	/**
	 * @return the modelInstance
	 */
	public ElementInstance getModelInstance() {
		return modelInstance;
	}

	public void notifyResourcesAcquired() {
		// Nothing to do
	}

}
