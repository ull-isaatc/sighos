/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ActivityManagerCreator {
	protected final SequentialSimulationEngine simul;

	/**
	 * @param simul
	 */
	public ActivityManagerCreator(SequentialSimulationEngine simul) {
		this.simul = simul;
	}

	/**
	 * Specifies the way the structure of the activity managers is built.
	 */	
	protected abstract void createActivityManagers();
}
