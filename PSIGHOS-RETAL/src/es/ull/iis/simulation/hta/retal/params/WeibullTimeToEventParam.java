/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class WeibullTimeToEventParam extends Param {
	final private RandomVariate rnd;
	final TimeUnit unit;
	/**
	 * 
	 */
	public WeibullTimeToEventParam(TimeUnit unit, RandomNumber rng, double alpha, double beta) {
		super();
		rnd = RandomVariateFactory.getInstance("WeibullVariate", rng, alpha, beta);
		this.unit = unit;
	}

	/**
	 * Returns the simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	public long getTimeToEvent(RetalPatient pat) {
		final long time = pat.getTs() + pat.getSimulation().getTimeUnit().convert(rnd.generate(), unit);
		return (time > pat.getSimulation().getEndTs()) ? Long.MAX_VALUE : time;
	}
}
