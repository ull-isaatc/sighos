/**
 * 
 */
package es.ull.iis.simulation.parallel;

import es.ull.iis.simulation.model.Debuggable;

/**
 * A simulation object which has knowledge of the simulation time.
 * @author Iván Castilla Rodríguez
 */
public abstract class TimeStampedSimulationObject extends VariableStoreSimulationObject implements Debuggable {

	/**
	 * Creates a new simulation object with an identifier and having knowledge of the 
	 * simulation time 
	 * @param id Object's identifier
	 * @param simul ParallelSimulationEngine this object belongs to
	 */
	public TimeStampedSimulationObject(int id, ParallelSimulationEngine simul) {
		super(id, simul);
	}

	/**
	 * Returns the simulation timestamp of this object.
	 * @return ParallelSimulationEngine timestamp of the object.
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
