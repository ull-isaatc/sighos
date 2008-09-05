/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class TimeStampedSimulationObject extends SimulationObject {

	public TimeStampedSimulationObject(int id, Simulation simul) {
		super(id, simul);
	}

	/**
	 * Returns the simulation timestamp of this object.
	 * @return Simulation timestamp of the object.
	 */
	public abstract double getTs();

    public void debug(String message) {
    	if (simul.isDebugEnabled())
    		simul.debug(this.toString() + "\t" + getTs() + "\t" + message);
	}
	
	public void error(String description) {
		simul.error(this.toString() + "\t" + getTs() + "\t" + description);
	}
    
	/**
	 * @return True if the debug mode is activated
	 */
	public boolean isDebugEnabled() {
		return simul.isDebugEnabled();
	}

}
