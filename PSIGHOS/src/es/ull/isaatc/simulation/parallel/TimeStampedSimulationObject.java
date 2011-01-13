/**
 * 
 */
package es.ull.isaatc.simulation.parallel;

import es.ull.isaatc.simulation.core.Debuggable;

/**
 * A simulation object which has knowledge of the simulation time.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class TimeStampedSimulationObject extends VariableStoreSimulationObject implements Debuggable {

	/**
	 * Creates a new simulation object with an identifier and having knowledge of the 
	 * simulation time 
	 * @param id Object's identifier
	 * @param simul Simulation this object belongs to
	 */
	public TimeStampedSimulationObject(int id, Simulation simul) {
		super(id, simul);
	}

	/**
	 * Returns the simulation timestamp of this object.
	 * @return Simulation timestamp of the object.
	 */
	public abstract long getTs();

	@Override
    public void debug(String message) {
    	if (simul.isDebugEnabled())
    		simul.debug(this.toString() + "\t" + getTs() + "\t" + message);
	}
	
	@Override
	public void error(String description) {
		simul.error(this.toString() + "\t" + getTs() + "\t" + description);
	}
    
	@Override
	public boolean isDebugEnabled() {
		return simul.isDebugEnabled();
	}

}
