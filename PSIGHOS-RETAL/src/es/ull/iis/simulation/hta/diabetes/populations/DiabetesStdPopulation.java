/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.populations;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatientProfile;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariate;

/**
 * A basic class to generate non-correlated information about patients.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class DiabetesStdPopulation implements DiabetesPopulation {
	/** Probability of a patient of being sex=male */
	private final double pMan;
	/** Distribution to set the age of the patients when created */
	private final RandomVariate baselineAge;
	/** Distribution to set the HbA1c level of the patients when created */
	private final RandomVariate baselineHBA1c;
	/** Distribution to set the duration of diabetes of the patients when created */
	private final RandomVariate baselineDurationOfDiabetes;
	/** Diabetes type */
	private final DiabetesType type;
	/** Random number generator */
	private final RandomNumber rng;

	/**
	 * Creates a standard population
	 * @param secondOrder The second order repository that defines the second-order uncertainty on the parameters
	 * @param type Diabetes type
	 */
	public DiabetesStdPopulation(final DiabetesType type) {
		this.type = type;
		pMan = getPMan();
		baselineAge = getBaselineAge();
		baselineHBA1c = getBaselineHBA1c();
		baselineDurationOfDiabetes = getBaselineDurationOfDiabetes();
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	}

	@Override
	public DiabetesPatientProfile getPatientProfile() {
		final int sex = (rng.draw() < pMan) ? BasicConfigParams.MAN : BasicConfigParams.WOMAN;
		final double initAge = Math.max(baselineAge.generate(), getMinAge());		
		return new DiabetesPatientProfile(initAge, sex, baselineDurationOfDiabetes.generate(), baselineHBA1c.generate());
	}

	@Override
	public DiabetesType getType() {
		return type;
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}
	
	/**
	 * Creates and returns the probability of being a man according to the population characteristics.
	 * @return the probability of being a man according to the population characteristics
	 */
	protected abstract double getPMan();
	/**
	 * Creates and returns a function to assign the baseline HbA1c level 
	 * @return a function to assign the baseline HbA1c level
	 */
	protected abstract RandomVariate getBaselineHBA1c();
	/**
	 * Creates and returns a function to assign the baseline age
	 * @return a function to assign the baseline age
	 */
	protected abstract RandomVariate getBaselineAge();
	/**
	 * Creates and returns a function to assign the duration of diabetes at baseline
	 * @return a function to assign the duration of diabetes at baseline
	 */
	protected abstract RandomVariate getBaselineDurationOfDiabetes();
}
