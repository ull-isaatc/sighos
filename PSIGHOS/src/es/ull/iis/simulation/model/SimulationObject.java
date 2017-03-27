/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public abstract class SimulationObject implements Comparable<SimulationObject>, Identifiable, Debuggable {
	protected final Simulation simul;
	private final String objectTypeId;
	protected final int id;
    /** String which represents the object */
    private final String idString;
	
	public SimulationObject(Simulation simul, int id, String objectTypeId) {
		this.simul = simul;
		this.objectTypeId = objectTypeId;
		this.id = id;
		idString = new String("[" + objectTypeId + id + "]");
		// In case the object is created after the simulation has started
		if (simul.getSimulationEngine() != null)
			assignSimulation(simul.getSimulationEngine());
	}
	
	/**
	 * @return the model
	 */
	public Simulation getSimulation() {
		return simul;
	}

	/**
	 * Returns a String that identifies the type of model object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the model object.
	 */
	public String getObjectTypeIdentifier() {
		return objectTypeId;
	}

	@Override
	public int compareTo(SimulationObject o) {
		if (id < o.id)
			return -1;
		if (id > o.id)
			return 1;
		return 0;
	}

	@Override
	public int getIdentifier() {
		return id;
	}
	
	@Override
	public String toString() {
    	return idString;
    }
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Debuggable#debug(java.lang.String)
	 */
    public void debug(String message) {
    	if (Simulation.isDebugEnabled())
    		Simulation.debug(this.toString() + "\t" + simul.getSimulationEngine().getTs() + "\t" + message);
	}
	
    /*
     * (non-Javadoc)
     * @see es.ull.iis.simulation.Debuggable#error(java.lang.String)
     */
	public void error(String description) {
		Simulation.error(this.toString() + "\t" + simul.getSimulationEngine().getTs() + "\t" + description);
	}
    
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Debuggable#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return Simulation.isDebugEnabled();
	}

	protected abstract void assignSimulation(SimulationEngine simul);
}
