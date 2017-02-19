/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public abstract class ModelObject implements Comparable<ModelObject> {
	private static int idGenerator = 0;
	protected final Model model;
	private final String objectTypeId;
	private final int id;
	
	public ModelObject(Model model, String objectTypeId) {
		this.model = model;
		this.objectTypeId = objectTypeId;
		id = idGenerator++;
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
}
