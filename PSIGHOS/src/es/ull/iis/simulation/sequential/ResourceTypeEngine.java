package es.ull.iis.simulation.sequential;

/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * @author Carlos Martin Galan
 */
public class ResourceTypeEngine extends SimulationObject implements es.ull.iis.simulation.model.ResourceTypeEngine {
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
