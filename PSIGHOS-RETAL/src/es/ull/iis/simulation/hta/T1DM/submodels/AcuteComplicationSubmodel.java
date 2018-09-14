/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.ReseteableParam;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * An abstract class to represent acute complications. Acute complications are characterized by a
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class AcuteComplicationSubmodel extends ComplicationSubmodel implements ReseteableParam<AcuteComplicationSubmodel.Progression> {
	public static class Progression {
		public final long timeToEvent;
		public final boolean causesDeath;
		
		public Progression(long timeToEvent, boolean causesDeath) {
			this.timeToEvent = timeToEvent;
			this.causesDeath = causesDeath;
		}
	}
	private final RandomNumber rng;
	/** The list of generated random numbers for each patient. Each entry is a pair &lt;random number for time to event, random number for mortality&gt; */
	private final ArrayList<ArrayList<double[]>> generated;

	private final double annualProb;
	private final ComplicationRR rr;
	private final double pDeath;
	/** Which event is trying to use each patient */
	private final int[] eventCounter;

	/**
	 * 
	 */
	public AcuteComplicationSubmodel(int nPatients, double annualProb, ComplicationRR rr, double pDeath) {
		this.rng = RandomNumberFactory.getInstance();
		generated = new ArrayList<>(nPatients);
		for (int i = 0; i < nPatients; i++) {
			generated.add(new ArrayList<double[]>());
		}
		eventCounter = new int[nPatients];
		Arrays.fill(eventCounter, 0);
		this.annualProb = annualProb;
		this.rr = rr;
		this.pDeath = pDeath;
	}

	/**
	 * Returns the time to a new acute event and whether it causes causes the death of the patient.
	 * @param pat A patient 
	 * @return the time to a new acute event and whether it causes causes the death of the patient.
	 */
	@Override
	public Progression getValue(T1DMPatient pat) {
		// New event for the patient
		if (eventCounter[pat.getIdentifier()] == generated.get(pat.getIdentifier()).size()) {
			generated.get(pat.getIdentifier()).add(new double[] {rng.draw(), rng.draw()});
		}
		final double[] rnd = generated.get(pat.getIdentifier()).get(eventCounter[pat.getIdentifier()]++);
		final long timeToDeath = pat.getTimeToDeath();
		final double usedRR = rr.getRR(pat);
		final long timeToHypo = CommonParams.getAnnualBasedTimeToEvent(pat, -1 / annualProb, rnd[0], usedRR);
		if (timeToHypo >= timeToDeath)
			return new Progression(Long.MAX_VALUE, false);
		return new Progression(timeToHypo, rnd[1] < pDeath);
	}

	/**
	 * Tells the param to reuse the last values generated 
	 * @param pat A patient
	 */
	public void cancelLast(T1DMPatient pat) {		
		eventCounter[pat.getIdentifier()]--;
	}
	@Override
	public void reset() {
		Arrays.fill(eventCounter, 0);
	}
	
	public abstract double getCostOfComplication(T1DMPatient pat);
	public abstract double getDisutility(T1DMPatient pat);
}
