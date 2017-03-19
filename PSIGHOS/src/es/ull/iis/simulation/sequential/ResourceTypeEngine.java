package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.engine.EngineObject;

/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * FIXME: Candidate to be removed
 * @author Carlos Martin Galan
 */
public class ResourceTypeEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ResourceTypeEngine {
    private final es.ull.iis.simulation.model.ResourceType modelRT;

    /**
     * Creates a new resource type.
     * @param simul Associated simulation
     * @param description A short text describing this resource type.
     */
	public ResourceTypeEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.ResourceType modelRT) {
		super(modelRT.getIdentifier(), simul, "RT");
        this.modelRT = modelRT;
	}

	/**
	 * @return the modelRT
	 */
	public es.ull.iis.simulation.model.ResourceType getModelRT() {
		return modelRT;
	}

} 
