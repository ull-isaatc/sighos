/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * A simulation object which has knowledge of the simulation time.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class TimeStampedSimulationObject extends SimulationObject implements Debuggable, TimeStampedObject {

	/**
	 * Creates a new simulation object with an identifier and having knowledge of the 
	 * simulation time 
	 * @param id Object's identifier
	 * @param simul Simulation this object belongs to
	 */
	public TimeStampedSimulationObject(int id, Simulation simul) {
		super(id, simul);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Debuggable#debug(java.lang.String)
	 */
    public void debug(String message) {
    	if (simul.isDebugEnabled())
    		simul.debug(this.toString() + "\t" + getTs() + "\t" + message);
	}
	
    /*
     * (non-Javadoc)
     * @see es.ull.isaatc.simulation.Debuggable#error(java.lang.String)
     */
	public void error(String description) {
		simul.error(this.toString() + "\t" + getTs() + "\t" + description);
	}
    
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Debuggable#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return simul.isDebugEnabled();
	}

}