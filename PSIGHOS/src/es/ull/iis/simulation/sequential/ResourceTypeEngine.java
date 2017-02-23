package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.ActivityManager;


/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * @author Carlos Martin Galan
 */
public class ResourceTypeEngine extends SimulationObject implements es.ull.iis.simulation.model.ResourceTypeEngine {
    /** Activity manager this resource type belongs to. */
    protected ActivityManager manager;
    private final es.ull.iis.simulation.model.ResourceType modelRT;

    /**
     * Creates a new resource type.
     * @param simul Associated simulation
     * @param description A short text describing this resource type.
     */
	public ResourceTypeEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.ResourceType modelRT) {
		super(simul.getNextResourceTypeId(), simul, "RT");
        this.modelRT = modelRT;
	}

	/**
	 * @return the modelRT
	 */
	public es.ull.iis.simulation.model.ResourceType getModelRT() {
		return modelRT;
	}

    /**
     * Returns the activity manager this resource type belongs to.
     * @return Value of property manager.
     */
    public ActivityManager getManager() {
        return manager;
    }
    
    /**
     * Sets the activity manager this resource type belongs to. It also
     * adds this resource type to the manager.
     * @param manager New value of property manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }

} 
