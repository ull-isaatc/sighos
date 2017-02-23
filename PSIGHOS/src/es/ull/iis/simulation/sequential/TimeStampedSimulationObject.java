/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * A simulation object which has knowledge of the simulation time.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class TimeStampedSimulationObject extends VariableStoreSimulationObject {

	/**
	 * Creates a new simulation object with an identifier and having knowledge of the 
	 * simulation time 
	 * @param id Object's identifier
	 * @param simul Simulation this object belongs to
	 */
	public TimeStampedSimulationObject(int id, SequentialSimulationEngine simul, String objTypeId) {
		super(id, simul, objTypeId);
	}

	/**
	 * Returns the simulation timestamp of this object.
	 * @return Simulation timestamp of the object.
	 */
	public abstract long getTs();

}
