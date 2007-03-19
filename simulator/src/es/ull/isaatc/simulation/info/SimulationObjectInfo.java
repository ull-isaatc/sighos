/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.SimulationObject;

/**
 * Information related to a simulation object: Resource, Element...
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SimulationObjectInfo extends SimulationInfo {
	/** Timestamp when the information is produced */
	private double ts; 

	/**
	 * @param source Object which produces the event
	 * @param ts Timestamp of the event
	 */
	public SimulationObjectInfo(SimulationObject source, double ts) {
		super(source);
		this.ts = ts;
	}

	/**
	 * Returns the timestamp when this event was thrown
	 * @return Returns the timestamp of this event.
	 */
	public double getTs() {
		return ts;
	}
	
	/**
	 * Returns the identifier of the simulation object which throws this event.
	 * @return The identifier of the source object 
	 */
	public int getIdentifier() {
		return ((SimulationObject)source).getIdentifier();
	}

}
