/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.GompertzVariate;
import simkit.random.RandomNumber;

/**
 * A death submodel based on the Spanish 2016 Mortality risk from the Instituto Nacional de Estadística (INE). The
 * parameters are adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 18 to 100.
 * @author Iván Castilla Rodríguez
 *
 */
public class StandardSpainDeathSubmodel extends DeathSubmodel {
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43996654), Math.exp(-11.43877681)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093286762, 0.099683525};
	private final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
	/** A random value [0, 1] for each patient (useful for common numbers techniques) and simulation */
	private final double[][] rnd;

	/**
	 * Creates a death submodel based on the Spanish 2016 Mortality risk
	 * @param rng A random number generator
	 * @param nPatients Number of simulated patients
	 */
	public StandardSpainDeathSubmodel(SecondOrderParamsRepository secParams) {
		super(secParams);
		rnd = new double[secParams.getnRuns() + 1][secParams.getnPatients()];
		for (int i = 0; i < secParams.getnRuns() + 1; i++)
			for (int j = 0; j < secParams.getnPatients(); j++) {
				rnd[i][j] = rng.draw();
			}
	}
	
	@Override
	public void registerSecondOrderParameters() {
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables and increased according to the 
	 * state of the patient. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public long getTimeToDeath(Patient pat) {
		final int simulId = pat.getSimulation().getIdentifier();
		final double age = pat.getAge();
		// Taking into account modification of death due to the intervention
		final Modification modif = pat.getIntervention().getLifeExpectancyModification();
		if (Modification.Type.SET.equals(modif.getType()))
			return pat.getTs() + pat.getSimulation().getTimeUnit().convert(modif.getValue(simulId) - age, TimeUnit.YEAR);
		
		double imr = 1.0;
		for (final Manifestation state : pat.getDetailedState()) {
			final double newIMR = secParams.getIMR(state, simulId);
			if (newIMR > imr) {
				imr = newIMR;
			}
		}
		// TODO: Check that this works properly
		if (Modification.Type.RR.equals(modif.getType())) {
			imr *= modif.getValue(simulId);
		}
		final double time = Math.min(GompertzVariate.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), rnd[simulId][pat.getIdentifier()] / imr), BasicConfigParams.DEF_MAX_AGE - age);
		// TODO: Check that this works properly
		if (Modification.Type.DIFF.equals(modif.getType())) {
			return pat.getTs() + pat.getSimulation().getTimeUnit().convert(Math.max(0.0,  Math.min(time - modif.getValue(simulId), BasicConfigParams.DEF_MAX_AGE - age)), TimeUnit.YEAR);			
		}
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}
}
