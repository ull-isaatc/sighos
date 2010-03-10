/**
 * 
 */
package es.ull.isaatc.simulation.bonn3Phase;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class ActivityManagerCreator {
	protected final Simulation simul;

	/**
	 * @param simul
	 */
	public ActivityManagerCreator(Simulation simul) {
		this.simul = simul;
	}

	/**
	 * Specifies the way the structure of the activity managers is built.
	 */	
	protected abstract void createActivityManagers();
}
