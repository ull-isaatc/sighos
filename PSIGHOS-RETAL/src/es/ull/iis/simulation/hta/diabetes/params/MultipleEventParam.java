/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import simkit.random.RandomNumber;

/**
 * A class to generate multiple events for patients, i.e., an event that happens more than once during the lifetime of the patient.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class MultipleEventParam<T> implements ReseteableParam<T>, CancellableParam<T> {
	/** Random number generator */
	private final RandomNumber rng;
	/** The list of generated random numbers for each patient. */
	private final ArrayList<ArrayList<Double>> generated;
	/** Which event is trying to use each patient */
	private final int[] eventCounter;

	/**
	 * Creates a parameter that represents a one-time event for a patient
	 * @param nPatients Number of patients simulated
	 */
	public MultipleEventParam(RandomNumber rng, int nPatients) {
		this.rng = rng;
		generated = new ArrayList<>(nPatients);
		for (int i = 0; i < nPatients; i++) {
			generated.add(new ArrayList<Double>());
		}
		eventCounter = new int[nPatients];
	}
	
	/**
	 * Returns a value in [0, 1] for the patient. Always returns the same value for the same patient.
	 * @param pat A patient
	 * @return a value in [0, 1] for the patient
	 */
	protected double draw(DiabetesPatient pat) {
		// New event for the patient
		if (eventCounter[pat.getIdentifier()] == generated.get(pat.getIdentifier()).size()) {
			generated.get(pat.getIdentifier()).add(rng.draw());
		}
		return generated.get(pat.getIdentifier()).get(eventCounter[pat.getIdentifier()]++);
	}

	@Override
	public void cancelLast(DiabetesPatient pat) {		
		eventCounter[pat.getIdentifier()]--;
	}
	
	@Override
	public void reset() {
		Arrays.fill(eventCounter, 0);
	}

}
