/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.isaatc.function.TimeFunction;

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
	public OphthalmologicPatientCreator(RETALSimulation simul, TimeFunction nPatients, double pMen, TimeFunction initialAges) {
		super(simul, nPatients, pMen, initialAges);
	}

	@Override
	protected Patient createPatient() {
		return new OphthalmologicPatient(simul, initialAges.getValue(0), (RNG_SEX.nextDouble() < pMen) ? 0 : 1);
	}
}
