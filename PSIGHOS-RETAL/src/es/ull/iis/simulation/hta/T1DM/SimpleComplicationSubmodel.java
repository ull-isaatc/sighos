/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import simkit.random.RandomNumber;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleComplicationSubmodel extends ComplicationSubmodel implements T1DMHealthState {
	private final String name;
	private final String description;
	private final double invProb;
	private final ComplicationRR rr;
	private final SimpleComplicationState[] states;
	
	/**
	 * 
	 */
	public SimpleComplicationSubmodel(String name, String description, RandomNumber rng, int nPatients, double prob, ComplicationRR rr) {
		this.invProb = -1/prob;
		this.rr = rr;
		this.states = new SimpleComplicationState[nPatients];
		for (int i = 0; i < states.length; i++)
			states[i] = new SimpleComplicationState(rng.draw());
		this.name = name;
		this.description = description;
	}

	@Override
	public T1DMProgression[] getNextComplication(T1DMPatient pat) {
		final double rrValue = rr.getRR(pat);
		final long time2NEU = CommonParams.getAnnualBasedTimeToEvent(pat, invProb, states[pat.getIdentifier()].getRnd(), rrValue);
		return (time2NEU < pat.getTimeToDeath()) ? new T1DMProgression[] {new T1DMProgression(this, time2NEU)} : null;
	}

	@Override
	public int getNSubstates() {
		return 1;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void progress(T1DMPatient pat, T1DMProgression prog) {
		states[pat.getIdentifier()].progress(prog);
	}

	@Override
	public void reset() {
		for (SimpleComplicationState st : states) {
			st.reset();
		}
	}

	class SimpleComplicationState implements PatientComplicationState {
		private final double rnd;
		private long activeFrom;
		
		public SimpleComplicationState(double rnd) {
			this.rnd = rnd;
			activeFrom = -1;
		}

		/**
		 * @return the rnd
		 */
		public double getRnd() {
			return rnd;
		}

		@Override
		public void progress(T1DMProgression prog) {
			activeFrom = prog.getTimeToEvent();			
		}

		@Override
		public void reset() {
			activeFrom = -1;
		}

		/**
		 * @return the activeFrom
		 */
		public long getActiveFrom() {
			return activeFrom;
		}
		
	}
}
