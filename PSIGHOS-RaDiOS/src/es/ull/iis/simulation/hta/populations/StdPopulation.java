/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.PatientProfile;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;

/**
 * A basic class to generate non-correlated information about patients.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class StdPopulation implements Population {
	/** Probability of a patient of being sex=male */
	private final double pMan;
	/** Distribution to set the age of the patients when created */
	private final RandomVariate baselineAge;
	/** Random number generator */
	private final RandomNumber rng;

	private final Disease disease;
	
	/**
	 * Creates a standard population
	 */
	public StdPopulation(Disease disease) {
		pMan = getPMan();
		baselineAge = getBaselineAge();
		this.disease = disease;
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	}

	@Override
	public PatientProfile getPatientProfile() {
		final int sex = (rng.draw() < pMan) ? BasicConfigParams.MAN : BasicConfigParams.WOMAN;
		final double initAge = Math.min(Math.max(baselineAge.generate(), getMinAge()), getMaxAge());
		final Disease dis = (rng.draw() < getPDisease()) ? disease : Disease.HEALTHY;
		return new PatientProfile(initAge, sex, dis);
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}
	
	@Override
	public int getMaxAge() {
		return BasicConfigParams.DEF_MAX_AGE;
	}
	
	/**
	 * Creates and returns the probability of being a man according to the population characteristics.
	 * @return the probability of being a man according to the population characteristics
	 */
	protected abstract double getPMan();
	/**
	 * Creates and returns a function to assign the baseline age
	 * @return a function to assign the baseline age
	 */
	protected abstract RandomVariate getBaselineAge();
	
	/**
	 * Creates and returns the probability of having a disease according to the population characteristics.
	 * @return the probability of having a disease according to the population characteristics
	 */
	protected abstract double getPDisease();
}
