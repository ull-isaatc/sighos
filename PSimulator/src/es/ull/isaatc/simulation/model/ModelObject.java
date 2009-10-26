package es.ull.isaatc.simulation.model;

import es.ull.isaatc.simulation.Identifiable;

/**
 * An identifiable object belonging to a simulation which can be compared. The identifier is
 * unique per type of simulation object, thus different types of simulation objects can use 
 * the same identifiers.
 * @author Iván Castilla Rodríguez
 */
public abstract class ModelObject implements Identifiable, Comparable<ModelObject> {
    /** Unique object identifier  */
	protected final int id;
    /** Simulation this object belongs to */
    protected final Model model;
    /** String which represents the object */
    private final String idString;
    
	/**
     * Creates a new simulation object.
     * @param id Unique identifier of the object
     * @param model Simulation this object belongs to
     */
	public ModelObject(int id, Model model) {
		super();
		this.model = model;
		this.id = id;
		idString = new String("[" + getObjectTypeIdentifier() + id + "]");
	}

	/**
	 * Returns a String that identifies the type of simulation object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the simulation object.
	 */
	public abstract String getObjectTypeIdentifier();
	
    /**
     * Returns the simulation which this object is attached to.
     * @return Simulation this object belongs to
     */
    public Model getModel() {
        return model;
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
	public int compareTo(ModelObject o) {
		if (id < o.id)
			return -1;
		if (id > o.id)
			return 1;
		return 0;
	}

	@Override
	public String toString() {
    	return idString;
    }

}
