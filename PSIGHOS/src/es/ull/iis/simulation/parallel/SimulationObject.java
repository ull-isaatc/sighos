package es.ull.iis.simulation.parallel;


/**
 * An identifiable object belonging to a simulation which can be compared. The identifier is
 * unique per type of simulation object, thus different types of simulation objects can use 
 * the same identifiers.
 * @author Iván Castilla Rodríguez
 */
public abstract class SimulationObject implements es.ull.iis.simulation.model.engine.EngineObject {
    /** Unique object identifier  */
	protected final int id;
    /** ParallelSimulationEngine this object belongs to */
    protected final ParallelSimulationEngine simul;
    /** String which represents the object */
    private final String idString;
    
	/**
     * Creates a new simulation object.
     * @param id Unique identifier of the object
     * @param simul ParallelSimulationEngine this object belongs to
     */
	public SimulationObject(int id, ParallelSimulationEngine simul) {
		this.simul = simul;
		this.id = id;
		idString = new String("[" + getObjectTypeIdentifier() + id + "]"); 
	}

	/**
	 * Returns a String that identifies the type of simulation object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the simulation object.
	 */
	public abstract String getObjectTypeIdentifier();

	@Override
    public ParallelSimulationEngine getSimulationEngine() {
        return simul;
    }

    @Override
	public int getIdentifier() {
		return(id);
	}

    @Override
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
