/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.RoundedPeriodicCycle;
import es.ull.isaatc.util.RoundedPeriodicCycle.Type;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SimulationRoundedPeriodicCycle implements SimulationCycle {
	private RoundedPeriodicCycle cycle;
	
	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public SimulationRoundedPeriodicCycle(TimeUnit unit, long startTs, SimulationTimeFunction period, long endTs, Type type, long scale, long shift) {
		this(unit, new TimeStamp(unit, startTs), period, new TimeStamp(unit, endTs), type, new TimeStamp(unit, scale), new TimeStamp(unit, shift));
	}

	/**
	 * Creates a new cycle which "rounds" the values that it returns and 
	 * finishes at the specified timestamp.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param endTs Relative time when this cycle is expected to finish.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public SimulationRoundedPeriodicCycle(TimeUnit unit, TimeStamp startTs, SimulationTimeFunction period, TimeStamp endTs, Type type, TimeStamp scale, TimeStamp shift) {
		cycle = new RoundedPeriodicCycle(unit.convert(startTs), period.getFunction(), unit.convert(endTs), type, unit.convert(scale), unit.convert(shift));
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public SimulationRoundedPeriodicCycle(TimeUnit unit, long startTs, SimulationTimeFunction period, int iterations, Type type, long scale, long shift) {
		this(unit, new TimeStamp(unit, startTs), period, iterations, type, new TimeStamp(unit, scale), new TimeStamp(unit, shift));
	}

	/**
	 * Creates a cycle which "rounds" the values that it returns and is 
	 * executed the specified iterations.
	 * @param startTs Relative time when this cycle is expected to start.
	 * @param period Time interval between two successive events.
	 * @param iterations How many times this cycle is executed. A value of 0 indicates 
     * infinite iterations.
	 * @param type The way the events are going to be treated.
	 * @param scale The factor to which the results are fitted. 
	 */
	public SimulationRoundedPeriodicCycle(TimeUnit unit, TimeStamp startTs, SimulationTimeFunction period, int iterations, Type type, TimeStamp scale, TimeStamp shift) {
		cycle = new RoundedPeriodicCycle(unit.convert(startTs), period.getFunction(), iterations, type, unit.convert(scale), unit.convert(shift));		
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.SimulationCycle#getCycle()
	 */
	@Override
	public Cycle getCycle() {
		return cycle;
	}

}
