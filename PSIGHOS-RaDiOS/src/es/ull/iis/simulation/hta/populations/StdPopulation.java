/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.PatientProfile;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;

/**
 * A basic class to generate non-correlated information about patients.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class StdPopulation implements Population {
	/** Random number generator */
	private final RandomNumber rng;

	private final Disease disease;
	protected final SecondOrderParamsRepository secParams;
	/**
	 * Creates a standard population
	 */
	public StdPopulation(SecondOrderParamsRepository secParams, Disease disease) {
		this.disease = disease;
		this.secParams = secParams;
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	}

	@Override
	public PatientProfile getPatientProfile(DiseaseProgressionSimulation simul) {
		final int sex = (rng.draw() < getPMan(simul)) ? BasicConfigParams.MAN : BasicConfigParams.WOMAN;
		final double initAge = Math.min(Math.max(getBaselineAge(simul).generate(), getMinAge()), getMaxAge());
		final Disease dis = (rng.draw() < getPDisease(simul)) ? disease : secParams.HEALTHY;
		return new PatientProfile(initAge, sex, dis, rng.draw() < getPDiagnosed(simul));
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}
	
	@Override
	public int getMaxAge() {
		return BasicConfigParams.DEF_MAX_AGE;
	}
	
	@Override
	public Disease getDisease() {
		return disease;
	}
	/**
	 * Creates and returns the probability of being a man according to the population characteristics.
	 * @return the probability of being a man according to the population characteristics
	 */
	protected abstract double getPMan(DiseaseProgressionSimulation simul);
	/**
	 * Creates and returns the probability of starting with a diagnosis according to the population characteristics.
	 * @return the probability of starting with a diagnosis according to the population characteristics
	 */
	protected abstract double getPDiagnosed(DiseaseProgressionSimulation simul);	
	/**
	 * Creates and returns a function to assign the baseline age
	 * @return a function to assign the baseline age
	 */
	protected abstract RandomVariate getBaselineAge(DiseaseProgressionSimulation simul);
	
}
