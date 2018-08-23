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
public class SimpleNPHComplicationSubmodel extends ComplicationSubmodel {
	public static T1DMHealthState NPH = new T1DMHealthState("NPH", "Neuropathy", MainComplications.NPH);
	public static T1DMHealthState ESRD = new T1DMHealthState("ESRD", "End-Stage Renal Disease", MainComplications.NPH);
	public static T1DMHealthState[] NPHSubstates = new T1DMHealthState[] {NPH, ESRD};

	public enum NPHTransitions {
		HEALTHY_NPH,
		NPH_ESRD,
		HEALTHY_ESRD,
		NEU_NPH		
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final NPHState[] states;

	/**
	 * 
	 */
	public SimpleNPHComplicationSubmodel(RandomNumber rng, int nPatients, double[] prob, ComplicationRR[] rr) {
		invProb = new double[prob.length];
		for (int i = 0; i < prob.length; i++) {
			invProb[i] = -1 /prob[i];
		}
		this.rr = rr;
		states = new NPHState[nPatients];
		for (int i = 0; i < nPatients; i++) {
			final double []rnd = new double[prob.length];
			for (int j = 0; j < prob.length; j++) {
				rnd[j] = rng.draw();
			}
			states[i] = new NPHState(rnd);
		}
	}

	@Override
	public T1DMProgression[] getNextComplication(T1DMPatient pat) {
		if (!enable)
			return null;
		final NPHState st = states[pat.getIdentifier()];
		T1DMProgression progToNPH = null;
		T1DMProgression progToESRD = null;
		// Nothing else to progress to
		if (!st.hasESRD()) {
			if (st.hasNPH()) {
				// RR from NPH to ESRD
				final long timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.NPH_ESRD);
				progToESRD = (timeToESRD < pat.getTimeToDeath()) ? new T1DMProgression(ESRD, timeToESRD) : null;				
			}
			else {
				// RR from healthy to ESRD
				final long timeToESRD = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_ESRD);
				progToESRD = (timeToESRD < pat.getTimeToDeath()) ? new T1DMProgression(ESRD, timeToESRD) : null;				
				// RR from NEU to NPH
				if (pat.hasComplication(MainComplications.NEU)) {
					final long timeToNPH = getAnnualBasedTimeToEvent(pat, NPHTransitions.NEU_NPH);
					progToNPH = (timeToNPH < pat.getTimeToDeath()) ? new T1DMProgression(NPH, timeToNPH) : null;					
				}
				// RR from healthy to NPH
				else {
					final long timeToNPH = getAnnualBasedTimeToEvent(pat, NPHTransitions.HEALTHY_NPH);
					progToNPH = (timeToNPH < pat.getTimeToDeath()) ? new T1DMProgression(NPH, timeToNPH) : null;
				}
			}
		}
		if (progToESRD == null && progToNPH == null) {
			return null;
		}
		if (progToESRD == null) {
			return new T1DMProgression[] {progToNPH};
		}
		if (progToNPH == null) {
			return new T1DMProgression[] {progToESRD};
		}
		return new T1DMProgression[] {progToNPH, progToESRD};
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, NPHTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], states[pat.getIdentifier()].getRnd(transition.ordinal()), rr[transition.ordinal()].getRR(pat));
		
	}
	@Override
	public void progress(T1DMPatient pat, T1DMProgression prog) {
		states[pat.getIdentifier()].progress(prog);
	}

	@Override
	public void reset() {
		for (NPHState st : states) {
			st.reset();
		}
	}

	@Override
	public int getNSubstates() {
		return NPHSubstates.length;
	}

	@Override
	public T1DMHealthState[] getSubstates() {
		return NPHSubstates;
	}

	@Override
	public T1DMHealthState[] hasComplication(T1DMPatient pat) {
		if (states[pat.getIdentifier()].hasESRD())
			return new T1DMHealthState[] {ESRD};
		if (states[pat.getIdentifier()].hasNPH()) 
			return new T1DMHealthState[] {NPH};
		return null;
	}

	class NPHState implements PatientComplicationState {
		private final double [] rnd;
		private long timeToNPH; 
		private long timeToESRD; 

		public NPHState(double[] rnd) {
			this.rnd = rnd;
			timeToESRD = -1;
			timeToNPH = -1;
		}
		
		@Override
		public void progress(T1DMProgression prog) {
			if (NPH.equals(prog.getState())) {
				timeToNPH = prog.getTimeToEvent();
			}
			else if (ESRD.equals(prog.getState())) {
				timeToESRD = prog.getTimeToEvent();
				// If the patient progressed directly to ESRD
				if (timeToNPH == -1)
					timeToNPH = timeToESRD;
			}
		}

		public boolean hasNPH() {
			return timeToNPH != -1;
		}
		
		public boolean hasESRD() {
			return timeToESRD != -1;
		}
		
		public double getRnd(int index) {
			return rnd[index];
		}
		
		@Override
		public void reset() {
			timeToESRD = -1;
			timeToNPH = -1;
		}
		
	}
}
