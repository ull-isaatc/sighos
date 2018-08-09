package es.ull.iis.simulation.hta.T1DM.params;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;

/**
 * A class to compute all the severe hypoglycemic events of a patient
 * @author Iv�n Castilla Rodr�guez
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
	private final static double PROB = 0.0982;
	private final static double[] INTERVENTION_RR = {1.0, 0.869};
	private final RandomNumber rng;
	/** The list of generated random numbers for each patient. Each entry is a pair &lt;random number for time to event, random number for mortality&gt; */
	private final ArrayList<double[]>[] generated;

	private final static double MORTALITY = 0.0063;
	/** Which event is trying to use each patient */
	private final int[] eventCounter;

	@SuppressWarnings("unchecked")
	public SevereHypoglycemicEventParam(int nPatients) {
		this.rng = RandomNumberFactory.getInstance();
		generated = (ArrayList<double[]>[])new ArrayList[nPatients];
		for (int i = 0; i < nPatients; i++) {
			generated[i] = new ArrayList<double[]>();
		}
		eventCounter = new int[nPatients];
		Arrays.fill(eventCounter, 0);
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
		final long timeToHypo = CommonParams.getAnnualBasedTimeToEvent(pat, -1 / PROB, rnd[0], INTERVENTION_RR);
		if (timeToHypo >= timeToDeath)
			return new ReturnValue(Long.MAX_VALUE, false);
		return new ReturnValue(timeToHypo, rnd[1] < MORTALITY);
	}

	@Override
	public void reset() {
		Arrays.fill(eventCounter, 0);
	}

}
