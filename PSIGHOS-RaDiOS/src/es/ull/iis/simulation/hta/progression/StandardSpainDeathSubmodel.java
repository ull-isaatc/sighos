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
public class StandardSpainDeathSubmodel extends SecondOrderDeathSubmodel {
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43996654), Math.exp(-11.43877681)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093286762, 0.099683525};

	/**
	 * Creates a death submodel based on the Spanish 2016 Mortality risk
	 * @param rng A random number generator
	 * @param nPatients Number of simulated patients
	 */
	public StandardSpainDeathSubmodel(SecondOrderParamsRepository secParams) {
		super();
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
	}


	@Override
	public DeathSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return isEnabled() ? new Instance(secParams) : new DisabledDeathInstance(this);
	}

	public class Instance extends DeathSubmodel {
		/** A random value [0, 1] for each patient (useful for common numbers techniques) */
		private final double[] rnd;
		private final SecondOrderParamsRepository secParams;
		
		public Instance(SecondOrderParamsRepository secParams) {
			super(StandardSpainDeathSubmodel.this);
			this.secParams = secParams;
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();
			rnd = new double[nPatients];
			for (int i = 0; i < nPatients; i++) {
				rnd[i] = rng.draw();
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
			final double time = Math.min(GompertzVariate.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), rnd[pat.getIdentifier()] / imr), BasicConfigParams.DEF_MAX_AGE - pat.getAge());
			return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
		}
		
	}
}
