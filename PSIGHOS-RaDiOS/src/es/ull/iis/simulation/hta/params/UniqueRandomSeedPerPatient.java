/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class UniqueRandomSeedPerPatient implements RandomSeedForPatients {
	/** The value generated for this parameter for each patient */
	private final double[] generated;
	/** If true, stores Math.log of the random numbers generated, instead of the random number in [0,1] */
	private final boolean logRandom;

	/**
	 * 
	 * @param nPatients Number of patients simulated
	 * @param logRandom If true, stores Math.log of the random numbers generated, instead of the random number in [0,1]
	 */
	public UniqueRandomSeedPerPatient(int nPatients, boolean logRandom) {
		this.generated = new double[nPatients];
		Arrays.fill(generated, Double.NaN);
		this.logRandom = logRandom;
	}

	@Override
	public double draw(Patient pat) {
		if (Double.isNaN(generated[pat.getIdentifier()])) {
			final double rnd = SecondOrderParamsRepository.getRNG_FIRST_ORDER().draw();
			generated[pat.getIdentifier()] = logRandom ? Math.log(rnd) : rnd;
		}
		return generated[pat.getIdentifier()];
	}

	@Override
	public void reset() {
	}

}
