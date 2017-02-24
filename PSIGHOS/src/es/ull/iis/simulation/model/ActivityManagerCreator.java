/**
 * 
 */
package es.ull.iis.simulation.model;


/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class ActivityManagerCreator {
	protected final Model model;

	/**
	 * @param simul
	 */
	public ActivityManagerCreator(Model model) {
		this.model = model;
	}

	/**
	 * Specifies the way the structure of the activity managers is built.
	 */	
	protected abstract void createActivityManagers();
}
