package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * An identifiable object belonging to a simulation which can be compared. The identifier is
 * unique per type of simulation object, thus different types of simulation objects can use 
 * the same identifiers.
 * @author Iván Castilla Rodríguez
 */
public abstract class EngineObject implements es.ull.iis.simulation.model.engine.EngineObject {
    /** Unique object identifier  */
	protected final int id;
    /** Simulation this object belongs to */
    protected final SimulationEngine simul;
    /** String which represents the object */
    private final String idString;
    private final String objTypeId;
    protected final Simulation model;
    
	/**
     * Creates a new simulation object.
     * @param id Unique identifier of the object
     * @param simul Simulation this object belongs to
     */
	public EngineObject(int id, SimulationEngine simul, String objTypeId) {
		this.simul = simul;
		this.model = simul.getSimulation();
		this.id = id;
		this.objTypeId = objTypeId;
		idString = new String("[" + objTypeId + id + "]");
	}

	/**
	 * Returns a String that identifies the type of simulation object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the simulation object.
	 */
	public String getObjectTypeIdentifier() {
		return objTypeId;
	}
	
    /**
     * Returns the simulation which this object is attached to.
     * @return Simulation this object belongs to
     */
    public SimulationEngine getSimulationEngine() {
        return simul;
    }
    
    /**
     * Returns the object's identifier
     * @return The identifier of the object
     */
	public int getIdentifier() {
		return(id);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(es.ull.iis.simulation.model.engine.EngineObject o) {
		if (id < o.getIdentifier())
			return -1;
		if (id > o.getIdentifier())
			return 1;
		return 0;
	}

	@Override
	public String toString() {
    	return idString;
    }

}
