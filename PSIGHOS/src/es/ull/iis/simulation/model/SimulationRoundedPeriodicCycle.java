/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.util.Cycle;
import es.ull.iis.util.RoundedPeriodicCycle;
import es.ull.iis.util.RoundedPeriodicCycle.Type;

/**
 * @author Iván Castilla Rodríguez
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
	public SimulationRoundedPeriodicCycle(TimeUnit unit, long startTs, TimeFunction period, long endTs, Type type, long scale, long shift) {
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
	public SimulationRoundedPeriodicCycle(TimeUnit unit, TimeStamp startTs, TimeFunction period, TimeStamp endTs, Type type, TimeStamp scale, TimeStamp shift) {
		cycle = new RoundedPeriodicCycle(unit.convert(startTs), period, unit.convert(endTs), type, unit.convert(scale), unit.convert(shift));
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
	public SimulationRoundedPeriodicCycle(TimeUnit unit, long startTs, TimeFunction period, int iterations, Type type, long scale, long shift) {
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
	public SimulationRoundedPeriodicCycle(TimeUnit unit, TimeStamp startTs, TimeFunction period, int iterations, Type type, TimeStamp scale, TimeStamp shift) {
		cycle = new RoundedPeriodicCycle(unit.convert(startTs), period, iterations, type, unit.convert(scale), unit.convert(shift));		
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.SimulationCycle#getCycle()
	 */
	@Override
	public Cycle getCycle() {
		return cycle;
	}

}
