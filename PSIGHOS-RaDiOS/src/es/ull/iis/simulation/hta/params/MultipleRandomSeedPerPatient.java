/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PatientCommonRandomNumbers;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MultipleRandomSeedPerPatient implements RandomSeedForPatients {
	/** The list of generated random numbers for each patient. */
	private final ArrayList<ArrayList<Double>> generated;
	/** Which event is trying to use each patient */
	private final int[] eventCounter;
	/** If true, stores Math.log of the random numbers generated, instead of the random number in [0,1] */
	private final boolean logRandom;

	/**
	 * 
	 * @param nPatients Number of patients simulated
	 * @param logRandom If true, stores Math.log of the random numbers generated, instead of the random number in [0,1]
	 */
	public MultipleRandomSeedPerPatient(int nPatients, boolean logRandom) {
		generated = new ArrayList<>(nPatients);
		for (int i = 0; i < nPatients; i++) {
			generated.add(new ArrayList<Double>());
		}
		eventCounter = new int[nPatients];
		this.logRandom = logRandom;
	}

	@Override
	public double draw(Patient pat) {
		// New event for the patient
		if (eventCounter[pat.getIdentifier()] == generated.get(pat.getIdentifier()).size()) {
			final double rnd = PatientCommonRandomNumbers.getRNG().draw();
			generated.get(pat.getIdentifier()).add(logRandom ? Math.log(rnd) : rnd);
		}
		return generated.get(pat.getIdentifier()).get(eventCounter[pat.getIdentifier()]++);
	}

	public void cancelLast(Patient pat) {		
		eventCounter[pat.getIdentifier()]--;
	}

	public void reset() {
		Arrays.fill(eventCounter, 0);
	}
	
}
