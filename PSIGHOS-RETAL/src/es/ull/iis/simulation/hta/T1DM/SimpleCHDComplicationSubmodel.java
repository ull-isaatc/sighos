/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import simkit.random.DiscreteSelectorVariate;
import simkit.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleCHDComplicationSubmodel extends ComplicationSubmodel {
	public static T1DMHealthState ANGINA = new T1DMHealthState("ANGINA", "Angina", MainComplications.CHD);
	public static T1DMHealthState STROKE = new T1DMHealthState("STROKE", "Stroke", MainComplications.CHD);
	public static T1DMHealthState MI = new T1DMHealthState("MI", "Myocardial Infarction", MainComplications.CHD);
	public static T1DMHealthState HF = new T1DMHealthState("HF", "Heart Failure", MainComplications.CHD);
	public static T1DMHealthState[] CHDSubstates = new T1DMHealthState[] {ANGINA, STROKE, MI, HF}; 
	
	public enum CHDTransitions {
		HEALTHY_CHD,
		NPH_CHD,
		RET_CHD,
		NEU_CHD		
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final CHDState[] states;
	private final DiscreteSelectorVariate pCHDComplication;

	/**
	 * 
	 */
	public SimpleCHDComplicationSubmodel(RandomNumber rng, int nPatients, double[] prob, ComplicationRR[] rr, DiscreteSelectorVariate pCHDComplication) {
		invProb = new double[prob.length];
		for (int i = 0; i < prob.length; i++) {
			invProb[i] = -1 /prob[i];
		}
		this.rr = rr;
		states = new CHDState[nPatients];
		for (int i = 0; i < nPatients; i++) {
			final double []rnd = new double[prob.length];
			for (int j = 0; j < prob.length; j++) {
				rnd[j] = rng.draw();
			}
			states[i] = new CHDState(rnd);
		}
		this.pCHDComplication = pCHDComplication;
	}

	@Override
	public T1DMProgression[] getNextComplication(T1DMPatient pat) {
		if (!enable)
			return null;
		final CHDState st = states[pat.getIdentifier()];
		// Nothing else to progress to
		if (st.hasCHD()) {
			return null;
		}
		long timeToCHD = pat.getTimeToDeath();
		if (pat.hasComplication(MainComplications.NEU)) {
			long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NEU_CHD);
			if (newTimeToCHD < timeToCHD)
				timeToCHD = newTimeToCHD;
		}
		if (pat.hasComplication(MainComplications.NPH)) {
			long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NPH_CHD);
			if (newTimeToCHD < timeToCHD)
				timeToCHD = newTimeToCHD;
		}
		if (pat.hasComplication(MainComplications.RET)) {
			long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.RET_CHD);
			if (newTimeToCHD < timeToCHD)
				timeToCHD = newTimeToCHD;
		}
		long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.HEALTHY_CHD);
		if (newTimeToCHD < timeToCHD)
			timeToCHD = newTimeToCHD;
		if (timeToCHD < pat.getTimeToDeath()) {
			return new T1DMProgression[] {new T1DMProgression(CHDSubstates[pCHDComplication.generateInt()], timeToCHD)};
		}
		return null;
	}

	@Override
	public void progress(T1DMPatient pat, T1DMProgression prog) {
		states[pat.getIdentifier()].progress(prog);
	}

	@Override
	public int getNSubstates() {
		return CHDSubstates.length;
	}

	@Override
	public T1DMHealthState[] getSubstates() {
		return CHDSubstates;
	}

	@Override
	public void reset() {
		for (CHDState st : states) {
			st.reset();
		}
	}

	@Override
	public T1DMHealthState[] hasComplication(T1DMPatient pat) {
		if (!states[pat.getIdentifier()].hasCHD())
			return null;
		return new T1DMHealthState[] {states[pat.getIdentifier()].getSpecificCHD()};
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, CHDTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], states[pat.getIdentifier()].getRnd(transition.ordinal()), rr[transition.ordinal()].getRR(pat));
	}
	
	class CHDState implements PatientComplicationState {
		private final double [] rnd;
		private long timeToCHD;
		private T1DMHealthState substate; 

		public CHDState(double[] rnd) {
			this.rnd = rnd;
			substate = null;
			timeToCHD = -1;
		}
		
		@Override
		public void progress(T1DMProgression prog) {
			substate = prog.getState();
			timeToCHD = prog.getTimeToEvent();
		}

		public T1DMHealthState getSpecificCHD() {
			return substate;
		}
		
		public boolean hasCHD() {
			return substate != null;
		}
		
		public boolean hasAngina() {
			return ANGINA.equals(substate);
		}
		
		public boolean hasStroke() {
			return STROKE.equals(substate);
		}
		
		public boolean hasMI() {
			return MI.equals(substate);
		}
		
		public boolean hasHF() {
			return HF.equals(substate);
		}
		
		public double getRnd(int index) {
			return rnd[index];
		}
		
		@Override
		public void reset() {
			substate = null;
			timeToCHD = -1;
		}
		
	}
}
