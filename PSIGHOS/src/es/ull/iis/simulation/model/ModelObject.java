/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public interface ModelObject {
	/**
	 * Returns a String that identifies the type of model object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the model object.
	 */
	String getObjectTypeIdentifier();

}
