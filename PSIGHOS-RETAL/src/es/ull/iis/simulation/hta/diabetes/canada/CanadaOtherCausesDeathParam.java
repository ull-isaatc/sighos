/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Param;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.UniqueEventParam;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaOtherCausesDeathParam extends UniqueEventParam<Long> implements Param<Long> {
	private final static double INV_RISK = -1 / 0.002;
	private final static double MAX_AGE = 54.0;
	

	public CanadaOtherCausesDeathParam(CanadaSecondOrderParams secParams) {
		super(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), secParams.getnPatients(), true);
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	public Long getValue(DiabetesPatient pat) {
		final double age = pat.getAge();
		double time = INV_RISK * draw(pat);
		// If the expected time to death is higher than the max age to apply the initial risk, we assume that the patient will
		// survive up to that age and compute the time to death according to the general population equations. 
		if (age + time > MAX_AGE)
//			time = Math.min(MAX_AGE - age + ModelParams.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], MAX_AGE, draw(pat)), BasicConfigParams.MAX_AGE - pat.getAge());
			time = BasicConfigParams.DEF_MAX_AGE - pat.getAge();
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}

}
