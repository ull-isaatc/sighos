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
public class SimpleNEUComplicationSubmodel extends ComplicationSubmodel {
	public static T1DMHealthState NEU = new T1DMHealthState("NEU", "Neuropathy", MainComplications.NEU);
	public static T1DMHealthState LEA = new T1DMHealthState("LEA", "Low extremity amputation", MainComplications.NEU);
	public static T1DMHealthState[] NEUSubstates = new T1DMHealthState[] {NEU, LEA};
			
	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA,
		HEALTHY_LEA;		
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final NEUState[] states;

	/**
	 * 
	 */
	public SimpleNEUComplicationSubmodel(RandomNumber rng, int nPatients, double[] prob, ComplicationRR[] rr) {
		invProb = new double[prob.length];
		for (int i = 0; i < prob.length; i++) {
			invProb[i] = -1 /prob[i];
		}
		this.rr = rr;
		states = new NEUState[nPatients];
		for (int i = 0; i < nPatients; i++) {
			final double []rnd = new double[prob.length];
			for (int j = 0; j < prob.length; j++) {
				rnd[j] = rng.draw();
			}
			states[i] = new NEUState(rnd);
		}
	}

	@Override
	public T1DMProgression[] getNextComplication(T1DMPatient pat) {
		if (!enable)
			return null;
		final NEUState st = states[pat.getIdentifier()];
		T1DMProgression progToNEU = null;
		T1DMProgression progToLEA = null;
		// Nothing else to progress to
		if (!st.hasLEA()) {
			if (st.hasNEU()) {
				// RR from NEU to LEA
				final long timeToLEA = getAnnualBasedTimeToEvent(pat, NEUTransitions.NEU_LEA);
				progToLEA = (timeToLEA < pat.getTimeToDeath()) ? new T1DMProgression(LEA, timeToLEA) : null;				
			}
			else {
				// RR from healthy to LEA
				final long timeToLEA = getAnnualBasedTimeToEvent(pat, NEUTransitions.HEALTHY_LEA);
				progToLEA = (timeToLEA < pat.getTimeToDeath()) ? new T1DMProgression(LEA, timeToLEA) : null;				
				// RR from healthy to NEU
				final long timeToNEU = getAnnualBasedTimeToEvent(pat, NEUTransitions.HEALTHY_NEU);
				progToNEU = (timeToNEU < pat.getTimeToDeath()) ? new T1DMProgression(NEU, timeToNEU) : null;
			}
		}
		if (progToLEA == null && progToNEU == null) {
			return null;
		}
		if (progToLEA == null) {
			return new T1DMProgression[] {progToNEU};
		}
		if (progToNEU == null) {
			return new T1DMProgression[] {progToLEA};
		}
		return new T1DMProgression[] {progToNEU, progToLEA};
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, NEUTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], states[pat.getIdentifier()].getRnd(transition.ordinal()), rr[transition.ordinal()].getRR(pat));
		
	}
	@Override
	public void progress(T1DMPatient pat, T1DMProgression prog) {
		states[pat.getIdentifier()].progress(prog);
	}

	@Override
	public void reset() {
		for (NEUState st : states) {
			st.reset();
		}
	}

	@Override
	public int getNSubstates() {
		return NEUSubstates.length;
	}

	@Override
	public T1DMHealthState[] getSubstates() {
		return NEUSubstates;
	}

	@Override
	public T1DMHealthState[] hasComplication(T1DMPatient pat) {
		if (states[pat.getIdentifier()].hasLEA())
			return new T1DMHealthState[] {LEA};
		if (states[pat.getIdentifier()].hasNEU()) 
			return new T1DMHealthState[] {NEU};
		return null;
	}

	class NEUState implements PatientComplicationState {
		private final double [] rnd;
		private long timeToNEU; 
		private long timeToLEA; 

		public NEUState(double[] rnd) {
			this.rnd = rnd;
			timeToLEA = -1;
			timeToNEU = -1;
		}
		
		@Override
		public void progress(T1DMProgression prog) {
			if (NEU.equals(prog.getState())) {
				timeToNEU = prog.getTimeToEvent();
			}
			else if (LEA.equals(prog.getState())) {
				timeToLEA = prog.getTimeToEvent();
				// If the patient progressed directly to LEA
				if (timeToNEU == -1)
					timeToNEU = timeToLEA;
			}
		}

		public boolean hasNEU() {
			return timeToNEU != -1;
		}
		
		public boolean hasLEA() {
			return timeToLEA != -1;
		}
		
		public double getRnd(int index) {
			return rnd[index];
		}
		
		@Override
		public void reset() {
			timeToLEA = -1;
			timeToNEU = -1;
		}
		
	}
}
