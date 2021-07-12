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
import simkit.random.RandomVariateFactory;

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
	/** Probability of a patient of being smoker */
	private final double pSmoker;
	/** Probability of a patient of having atrial fibrillation */
	private final double pAtrialFib;
	/** Distribution to set the SBP of the patients when created */
	private final RandomVariate baselineSBP;
	/** Distribution to set the lipid ratio T:H of the patients when created */
	private final RandomVariate baselineLipidRatio;
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
		baselineSBP = getBaselineSBP();
		baselineLipidRatio = getBaselineLipidRatio();
		pSmoker = getPSmoker();
		pAtrialFib = getPAtrialFibrillation();
		rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	}

	@Override
	public DiabetesPatientProfile getPatientProfile() {
		final int sex = (rng.draw() < pMan) ? BasicConfigParams.MAN : BasicConfigParams.WOMAN;
		final double initAge = Math.min(Math.max(baselineAge.generate(), getMinAge()), getMaxAge());	
		final double duration = Math.min(initAge, baselineDurationOfDiabetes.generate());
		return new DiabetesPatientProfile(initAge, sex, duration, baselineHBA1c.generate(), 
				rng.draw() < pSmoker, rng.draw() < pAtrialFib, baselineSBP.generate(), baselineLipidRatio.generate());
	}

	@Override
	public DiabetesType getType() {
		return type;
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
	/**
	 * Creates and returns the probability of being smoker according to the population characteristics.
	 * @return the probability of being smoker according to the population characteristics
	 */
	protected double getPSmoker() {
		return BasicConfigParams.DEFAULT_P_SMOKER;
	}
	/**
	 * Creates and returns the probability of having atrial fibrillation according to the population characteristics.
	 * @return the probability of having atrial fibrillation according to the population characteristics
	 */
	protected double getPAtrialFibrillation() {
		return BasicConfigParams.DEFAULT_P_ATRIAL_FIB;
	}
	/**
	 * Creates and returns a function to assign the SBP at baseline
	 * @return a function to assign the SBP at baseline
	 */
	protected RandomVariate getBaselineSBP() {
		return RandomVariateFactory.getInstance("Constant", BasicConfigParams.DEFAULT_SBP);
	}
	/**
	 * Creates and returns a function to assign the lipid ratio at baseline
	 * @return a function to assign the lipid ratio at baseline
	 */
	protected RandomVariate getBaselineLipidRatio() {
		return RandomVariateFactory.getInstance("Constant", BasicConfigParams.DEFAULT_LIPID_RATIO);		
	}
}
