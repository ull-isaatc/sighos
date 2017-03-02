/**
 * 
 */
package es.ull.iis.simulation.model;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ActivityManagerCreator {
	protected final Simulation model;

	/**
	 * @param simul
	 */
	public ActivityManagerCreator(Simulation model) {
		this.model = model;
	}

	/**
	 * Specifies the way the structure of the activity managers is built.
	 */	
	protected abstract void createActivityManagers();
}
