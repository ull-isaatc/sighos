/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public abstract class ModelObject implements Comparable<ModelObject> {
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
	public String toString() {
    	return idString;
    }
}
