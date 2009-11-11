/**
 * 
 */
package es.ull.isaatc.simulation.threaded;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class LogicalProcessCreator {
	protected final Simulation simul;

	/**
	 * @param simul
	 */
	public LogicalProcessCreator(Simulation simul) {
		this.simul = simul;
	}

	/**
	 * Specifies the way the structure of the logical processes is built. 
	 */
	protected abstract void createLogicalProcesses();
}
