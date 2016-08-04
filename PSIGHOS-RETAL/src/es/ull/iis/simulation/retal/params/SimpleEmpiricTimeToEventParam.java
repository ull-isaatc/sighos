/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.LinkedList;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SimpleEmpiricTimeToEventParam extends EmpiricTimeToEventParam {
	/** Random number generator for this param */
	final protected Random rng;
	/** An internal list of generated times to event to be used when creating validated times to event */
	final protected LinkedList<Long> queue = new LinkedList<Long>();
	/** First-eye incidence of EARM */
	final protected double [][] probabilities;	

	/**
	 * 
	 */
	public SimpleEmpiricTimeToEventParam(boolean baseCase, TimeUnit unit, int nAgeGroups) {
		super(baseCase, unit);
		this.rng = new Random();
		this.probabilities = new double[nAgeGroups][3];
	}

	/**
	 * Returns the "brute" simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	public long getTimeToEvent(OphthalmologicPatient pat) {
		final double []rnd = new double[probabilities.length];
		for (int j = 0; j < probabilities.length; j++)
			rnd[j] = rng.nextDouble();
		final double time = getTimeToEvent(probabilities, pat.getAge(), rnd);
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, unit);
		
	}

}
