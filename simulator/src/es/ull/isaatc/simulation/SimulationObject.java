package es.ull.isaatc.simulation;

/**
 * An object belonging to a simulation.
 * @author Iván Castilla Rodríguez
 */
public abstract class SimulationObject implements Comparable<SimulationObject> {
    /** Unique object identifier  */
	protected int id;
    /** Simulation where this object is used in */
    protected Simulation simul = null;
    
	/**
     * Creates a new simulation object.
     * @param id Unique identifier of the object
     * @param simul Simulation which includes this object
     */
	public SimulationObject(int id, Simulation simul) {
		this.id = id;
        this.simul = simul;
	}

    /**
     * Returns the object's identifier
     * @return The identifier of the object
     */
	public int getIdentifier() {
		return(id);
	}
	
	/**
	 * Returns a String that identifies the type of simulation object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the simulation object.
	 */
	public abstract String getObjectTypeIdentifier();
	
	/**
	 * Returns the simulation timestamp of this object.
	 * @return Simulation timestamp of the object.
	 */
	public abstract double getTs();
	
    /**
     * Returns the simulation which this object is attached to.
     * @return Value of property simul.
     */
    public es.ull.isaatc.simulation.Simulation getSimul() {
        return simul;
    }
    
    public void debug(String message) {
    	if (simul.isDebugEnabled())
    		simul.debug(this.toString() + "\t" + getTs() + "\t" + message);
	}
	
	public void error(String description) {
		simul.error(this.toString() + "\t" + getTs() + "\t" + description);
	}
    
	/**
	 * @return True if the debug mode is activated
	 */
	public boolean isDebugEnabled() {
		return simul.isDebugEnabled();
	}

	public String toString() {
    	return new String("[" + getObjectTypeIdentifier() + id + "]");
    }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SimulationObject o) {
		if (id < o.id)
			return -1;
		if (id > o.id)
			return 1;
		return 0;
	}

}
