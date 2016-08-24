/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
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
	public WeibullTimeToEventParam(RETALSimulation simul, boolean baseCase, TimeUnit unit, double alpha, double beta) {
		super(simul, baseCase);
		// TODO Prepare the param for 2nd order analysis
		rnd = RandomVariateFactory.getInstance("WeibullVariate", alpha, beta);
		this.unit = unit;
	}

	/**
	 * Returns the simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	public long getTimeToEvent(Patient pat) {
		final long time = pat.getTs() + simul.getTimeUnit().convert(rnd.generate(), unit);
		return (time > simul.getInternalEndTs()) ? Long.MAX_VALUE : time;
	}
}
