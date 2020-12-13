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
 * A death submodel based on the Spanish 2016 Mortality risk from the Instituto Nacional de Estad�stica (INE). The
 * parameters are adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 18 to 100.
 * @author Iv�n Castilla Rodr�guez
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
		rnd = new double[secParams.getnRuns()][secParams.getnPatients()];
	}
	
	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public void generate() {
		for (int i = 0; i < secParams.getnRuns(); i++)
			for (int j = 0; j < secParams.getnPatients(); j++) {
				rnd[i][j] = rng.draw();
			}
	}

	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables and increased according to the 
	 * state of the patient. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public long getTimeToDeath(Patient pat) {
		double imr = 1.0;
		for (final Manifestation state : pat.getDetailedState()) {
			final double newIMR = secParams.getIMR(state, pat.getSimulation().getIdentifier());
			if (newIMR > imr) {
				imr = newIMR;
			}
		}
		final double time = Math.min(GompertzVariate.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), rnd[pat.getSimulation().getIdentifier()][pat.getIdentifier()] / imr), BasicConfigParams.DEF_MAX_AGE - pat.getAge());
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}
}
