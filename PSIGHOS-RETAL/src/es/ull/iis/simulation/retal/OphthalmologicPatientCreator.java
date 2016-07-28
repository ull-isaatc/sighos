/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.function.TimeFunction;

/**
 * @author Iván Castilla
 *
 */
public class OphthalmologicPatientCreator extends PatientCreator {

	/**
	 * @param simul
	 * @param nPatients
	 * @param pMen
	 */
	public OphthalmologicPatientCreator(RETALSimulation simul, int nPatients, double pMen, TimeFunction initialAges) {
		super(simul, nPatients, pMen, initialAges);
	}

	
	@Override
	protected Patient createPatient() {
		final double age = initialAges.getValue(0);
		final int sex = (RNG_SEX.nextDouble() < pMen) ? 0 : 1;
		return new OphthalmologicPatient(simul, age, sex, simul.getTimeToDeath(age, sex));
	}
}
