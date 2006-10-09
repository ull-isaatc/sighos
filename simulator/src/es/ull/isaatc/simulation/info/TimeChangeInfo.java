/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.LogicalProcess;

/**
 * Information related to a cahnge in the simulation clock.
 * @author Iván Castilla Rodríguez
 */
public class TimeChangeInfo extends SimulationInfo {
	private static final long serialVersionUID = -3997751536094041841L;
	/** Timestamp which the simulation time has advance to */
	private double ts;

	/**
	 * 
	 * @param lp Logical process which advances the simulation clock.
	 */
	public TimeChangeInfo(LogicalProcess lp) {
		super(lp);
		this.ts = lp.getTs();
	}

	/**
	 * 
	 * @return The logical process which produced the information
	 */
	public LogicalProcess getLogicalProcess() {
		return(LogicalProcess)source;
	}
	
	/**
	 * 
	 * @return The logical process timestamp.
	 */
	public double getTs() {
		return ts;
	}
}
