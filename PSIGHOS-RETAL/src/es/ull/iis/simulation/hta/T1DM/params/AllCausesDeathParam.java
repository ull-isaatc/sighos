/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class AllCausesDeathParam extends UniqueEventParam<Long> implements Param<Long> {
	// Parameters for death. Source: Spanish 2014 Mortality risk. INE
	// Adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 40 to 100.
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.72495061), Math.exp(-12.03468863)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.097793214, 0.108276765};

	public AllCausesDeathParam(int nPatients) {
		super(nPatients);
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public Long getValue(T1DMPatient pat) {
		return getValue(pat, 1.0);
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables.
	 * The risk of death can be increased by the specified factor.
	 * @param pat A patient
	 * @param additionalRisk Additional risk of death, expressed as a factor 
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	public Long getValue(T1DMPatient pat, double additionalRisk) {
		final double time = Math.min(ModelParams.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), draw(pat) / additionalRisk), CommonParams.MAX_AGE - pat.getAge());
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}

}
