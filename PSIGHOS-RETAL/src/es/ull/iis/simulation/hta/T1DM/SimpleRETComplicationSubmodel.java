/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import simkit.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleRETComplicationSubmodel extends ComplicationSubmodel {
	public static T1DMHealthState RET = new T1DMHealthState("RET", "Retinopathy", MainComplications.RET);
	public static T1DMHealthState BLI = new T1DMHealthState("BLI", "Blindness", MainComplications.RET);
	public static T1DMHealthState[] RETSubstates = new T1DMHealthState[] {RET, BLI};
	
	public enum RETTransitions {
		HEALTHY_RET,
		RET_BLI,
		HEALTHY_BLI
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final RETState[] states;

	/**
	 * 
	 */
	public SimpleRETComplicationSubmodel(RandomNumber rng, int nPatients, double[] prob, ComplicationRR[] rr) {
		invProb = new double[prob.length];
		for (int i = 0; i < prob.length; i++) {
			invProb[i] = -1 /prob[i];
		}
		this.rr = rr;
		states = new RETState[nPatients];
		for (int i = 0; i < nPatients; i++) {
			final double []rnd = new double[prob.length];
			for (int j = 0; j < prob.length; j++) {
				rnd[j] = rng.draw();
			}
			states[i] = new RETState(rnd);
		}
	}

	@Override
	public T1DMProgression[] getNextComplication(T1DMPatient pat) {
		if (!enable)
			return null;
		final RETState st = states[pat.getIdentifier()];
		T1DMProgression progToRET = null;
		T1DMProgression progToBLI = null;
		// Nothing else to progress to
		if (!st.hasBLI()) {
			if (st.hasRET()) {
				// RR from RET to BLI
				final long timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.RET_BLI);
				progToBLI = (timeToBLI < pat.getTimeToDeath()) ? new T1DMProgression(BLI, timeToBLI) : null;				
			}
			else {
				// RR from healthy to BLI
				final long timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_BLI);
				progToBLI = (timeToBLI < pat.getTimeToDeath()) ? new T1DMProgression(BLI, timeToBLI) : null;				
				// RR from healthy to RET
				final long timeToRET = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_RET);
				progToRET = (timeToRET < pat.getTimeToDeath()) ? new T1DMProgression(RET, timeToRET) : null;
			}
		}
		if (progToBLI == null && progToRET == null) {
			return null;
		}
		if (progToBLI == null) {
			return new T1DMProgression[] {progToRET};
		}
		if (progToRET == null) {
			return new T1DMProgression[] {progToBLI};
		}
		return new T1DMProgression[] {progToRET, progToBLI};
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, RETTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], states[pat.getIdentifier()].getRnd(transition.ordinal()), rr[transition.ordinal()].getRR(pat));
		
	}
	@Override
	public void progress(T1DMPatient pat, T1DMProgression prog) {
		states[pat.getIdentifier()].progress(prog);
	}

	@Override
	public void reset() {
		for (RETState st : states) {
			st.reset();
		}
	}

	@Override
	public int getNSubstates() {
		return RETSubstates.length;
	}

	@Override
	public T1DMHealthState[] getSubstates() {
		return RETSubstates;
	}

	@Override
	public T1DMHealthState[] hasComplication(T1DMPatient pat) {
		if (states[pat.getIdentifier()].hasBLI())
			return new T1DMHealthState[] {BLI};
		if (states[pat.getIdentifier()].hasRET()) 
			return new T1DMHealthState[] {RET};
		return null;
	}

	class RETState implements PatientComplicationState {
		private final double [] rnd;
		private long timeToRET; 
		private long timeToBLI; 

		public RETState(double[] rnd) {
			this.rnd = rnd;
			timeToBLI = -1;
			timeToRET = -1;
		}
		
		@Override
		public void progress(T1DMProgression prog) {
			if (RET.equals(prog.getState())) {
				timeToRET = prog.getTimeToEvent();
			}
			else if (BLI.equals(prog.getState())) {
				timeToBLI = prog.getTimeToEvent();
				// If the patient progressed directly to BLI
				if (timeToRET == -1)
					timeToRET = timeToBLI;
			}
		}

		public boolean hasRET() {
			return timeToRET != -1;
		}
		
		public boolean hasBLI() {
			return timeToBLI != -1;
		}
		
		public double getRnd(int index) {
			return rnd[index];
		}
		
		@Override
		public void reset() {
			timeToBLI = -1;
			timeToRET = -1;
		}
		
	}
}
