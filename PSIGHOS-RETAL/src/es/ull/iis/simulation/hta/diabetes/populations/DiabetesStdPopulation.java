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
 * @author icasrod
 *
 */
public abstract class DiabetesStdPopulation implements DiabetesPopulation {
	/** The random number generator for first order values */
	private final RandomNumber rng;
	/** Probability of a patient of being sex=male */
	private final double pMan;
	/** Distribution to set the age of the patients when created */
	private final RandomVariate baselineAge;
	/** Distribution to set the HbA1c level of the patients when created */
	private final RandomVariate baselineHBA1c;
	/** Distribution to set the duration of diabetes of the patients when created */
	private final RandomVariate baselineDurationOfDiabetes;
	private final DiabetesType type;

	/**
	 * 
	 * @param secondOrder The second order repository that defines the second-order uncertainty on the parameters
	 */
	public DiabetesStdPopulation(final SecondOrderParamsRepository secParams, final DiabetesType type) {
		this.type = type;
		this.rng = secParams.getRngFirstOrder();
		pMan = getPMan();
		baselineAge = getBaselineAge();
		baselineHBA1c = getBaselineHBA1c();
		baselineDurationOfDiabetes = getBaselineDurationOfDiabetes();
	}

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
	
	public abstract double getPMan();
	public abstract RandomVariate getBaselineHBA1c();
	public abstract RandomVariate getBaselineAge();
	public abstract RandomVariate getBaselineDurationOfDiabetes();
}
