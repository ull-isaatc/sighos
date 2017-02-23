/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public abstract class ModelObject implements Comparable<ModelObject>, Identifiable, Debuggable {
	protected final Model model;
	private final String objectTypeId;
	private final int id;
    /** String which represents the object */
    private final String idString;
	
	public ModelObject(Model model, int id, String objectTypeId) {
		this.model = model;
		this.objectTypeId = objectTypeId;
		this.id = id;
		idString = new String("[" + objectTypeId + id + "]");
	}
	
	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
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
	public int compareTo(ModelObject o) {
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
    	if (Model.isDebugEnabled())
    		Model.debug(this.toString() + "\t" + model.getSimulationEngine().getTs() + "\t" + message);
	}
	
    /*
     * (non-Javadoc)
     * @see es.ull.iis.simulation.Debuggable#error(java.lang.String)
     */
	public void error(String description) {
		Model.error(this.toString() + "\t" + model.getSimulationEngine().getTs() + "\t" + description);
	}
    
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Debuggable#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return Model.isDebugEnabled();
	}

	protected abstract void assignSimulation(SimulationEngine simul);
}
