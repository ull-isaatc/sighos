package es.ull.iis.simulation.hta.T1DM.params;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * A class to compute all the severe hypoglycemic events of a patient
 * @author Iván Castilla Rodríguez
 *
 */
public class SevereHypoglycemicEventParam implements ReseteableParam<SevereHypoglycemicEventParam.ReturnValue> {
	public static class ReturnValue {
		public final long timeToEvent;
		public final boolean causesDeath;
		
		public ReturnValue(long timeToEvent, boolean causesDeath) {
			this.timeToEvent = timeToEvent;
			this.causesDeath = causesDeath;
		}
	}
	private final RandomNumber rng;
	/** The list of generated random numbers for each patient. Each entry is a pair &lt;random number for time to event, random number for mortality&gt; */
	private final ArrayList<double[]>[] generated;

	private final double annualProb;
	private final double[] rr;
	private final double pDeath;
	/** Which event is trying to use each patient */
	private final int[] eventCounter;

	@SuppressWarnings("unchecked")
	public SevereHypoglycemicEventParam(int nPatients, double annualProb, double[] rr, double pDeath) {
		this.rng = RandomNumberFactory.getInstance();
		generated = (ArrayList<double[]>[])new ArrayList[nPatients];
		for (int i = 0; i < nPatients; i++) {
			generated[i] = new ArrayList<double[]>();
		}
		eventCounter = new int[nPatients];
		Arrays.fill(eventCounter, 0);
		this.annualProb = annualProb;
		this.rr = rr;
		this.pDeath = pDeath;
	}

	/**
	 * Returns the time to a new severe hypoglycemic event and whether it causes causes the death of the patient.
	 * @param pat A patient 
	 * @return the time to a new severe hypoglycemic event and whether it causes causes the death of the patient.
	 */
	@Override
	public ReturnValue getValue(T1DMPatient pat) {
		// New event for the patient
		if (eventCounter[pat.getIdentifier()] == generated[pat.getIdentifier()].size()) {
			generated[pat.getIdentifier()].add(new double[] {rng.draw(), rng.draw()});
		}
		final double[] rnd = generated[pat.getIdentifier()].get(eventCounter[pat.getIdentifier()]++);
		final long timeToDeath = pat.getTimeToDeath();
		final long timeToHypo = CommonParams.getAnnualBasedTimeToEvent(pat, -1 / annualProb, rnd[0], rr[pat.getnIntervention()]);
		if (timeToHypo >= timeToDeath)
			return new ReturnValue(Long.MAX_VALUE, false);
		return new ReturnValue(timeToHypo, rnd[1] < pDeath);
	}

	@Override
	public void reset() {
		Arrays.fill(eventCounter, 0);
	}

}
