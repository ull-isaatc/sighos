/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StandardSpainDeathSubmodel extends DeathSubmodel {
	// Parameters for death. Source: Spanish 2016 Mortality risk. INE
	// Adjusted using a Gompertz distribution with logs or the yearly mortality risks from age 18 to 100.
	/** Alpha parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double ALPHA_DEATH[] = new double[] {Math.exp(-10.43996654), Math.exp(-11.43877681)};
	/** Beta parameter for a Gompertz distribution on the mortality risk for men and women */
	private final static double BETA_DEATH[] = new double[] {0.093286762, 0.099683525};
	private final double[] rnd;
	private final TreeMap<T1DMHealthState, Double> imrs;

	/**
	 * 
	 */
	public StandardSpainDeathSubmodel(RandomNumber rng, int nPatients) {
		super();
		rnd = new double[nPatients];
		for (int i = 0; i < nPatients; i++) {
			rnd[i] = rng.draw();
		}
		imrs = new TreeMap<>();
	}

	public void addIMR(T1DMHealthState state, double imr) {
		imrs.put(state, imr);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.DeathSubmodel#getTimeToDeath(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	/**
	 * Returns the simulation time until the death of the patient, according to the Spanish mortality tables and increased according to the 
	 * state of the patient. 
	 * @param pat A patient
	 * @return Simulation time to death of the patient or to MAX_AGE 
	 */
	@Override
	public long getTimeToDeath(T1DMPatient pat) {
		double imr = 1.0;
		for (final T1DMHealthState state : pat.getDetailedState()) {
			if (imrs.containsKey(state)) {
				final double newIMR = imrs.get(state);
				if (newIMR > imr) {
					imr = newIMR;
				}
			}
		}
		final double time = Math.min(ModelParams.generateGompertz(ALPHA_DEATH[pat.getSex()], BETA_DEATH[pat.getSex()], pat.getAge(), rnd[pat.getIdentifier()] / imr), BasicConfigParams.MAX_AGE - pat.getAge());
		return pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR);
	}

}
