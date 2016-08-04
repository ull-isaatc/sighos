/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class CompoundEmpiricTimeToEventParam extends EmpiricTimeToEventParam {
	/** Probability of developing GA in fellow eye, given the state of the first eye */
	final protected EnumMap<EyeState, StructuredInfo> tuples = new EnumMap<EyeState, StructuredInfo>(EyeState.class);

	class StructuredInfo {
		/** Random number generator for this param */
		final Random rng;
		/** An internal list of generated times to event to be used when creating validated times to event */
		final LinkedList<Long> queue;
		/** First-eye incidence of EARM */
		final double [][] probabilities;
		
		public StructuredInfo(int nAgeGroups) {
			this.rng = new Random();
			this.probabilities = new double[nAgeGroups][3];
			this.queue = new LinkedList<Long>();
		}
		
		public StructuredInfo() {
			this.rng = null;
			this.probabilities = null;
			this.queue = null;
		}
		
		public long getTimeToEvent(OphthalmologicPatient pat) {
			if (probabilities == null) {
				return Long.MAX_VALUE;
			}
			else {
				final double []rnd = new double[probabilities.length];
				for (int j = 0; j < probabilities.length; j++)
					rnd[j] = rng.nextDouble();
				final double time = CompoundEmpiricTimeToEventParam.getTimeToEvent(probabilities, pat.getAge(), rnd);
				return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : pat.getTs() + pat.getSimulation().getTimeUnit().convert(time, unit);
			}
		}
	}
	/**
	 * 
	 */
	public CompoundEmpiricTimeToEventParam(boolean baseCase, TimeUnit unit) {
		super(baseCase, unit);
	}

	/**
	 * Returns the "brute" simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @param firstEye True if the event applies to the first eye; false if the event applies to the fellow eye
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	public long getTimeToEvent(OphthalmologicPatient pat, boolean firstEye) {
		final EnumSet<EyeState> otherEye = (firstEye) ? pat.getEye2State() : pat.getEye1State();
		final long time;
		
		// Other eye has CNV
		if (otherEye.contains(EyeState.AMD_CNV)) {
			time = tuples.get(EyeState.AMD_CNV).getTimeToEvent(pat);
		}
		// Other eye has GA
		else if (otherEye.contains(EyeState.AMD_GA)) {
			time = tuples.get(EyeState.AMD_GA).getTimeToEvent(pat);
		}
		// Other eye has EARM
		else if (otherEye.contains(EyeState.EARM)) {
			time = tuples.get(EyeState.EARM).getTimeToEvent(pat);
		}
		else if (otherEye.contains(EyeState.HEALTHY)) {
			time = tuples.get(EyeState.HEALTHY).getTimeToEvent(pat);
		}
		else {
			time = Long.MAX_VALUE;
		}			
		return time;		
	}
	
	/**
	 * Returns the simulation time when a specific event will happen (expressed in simulation time units), and adjusted so 
	 * the time is coherent with the state and future/past events of the patient
	 * @param pat A patient
	 * @param firstEye True if the event applies to the first eye; false if the event applies to the fellow eye
	 * @return the simulation time when a specific event will happen (expressed in simulation time units), and adjusted so 
	 * the time is coherent with the state and future/past events of the patient
	 */
	public abstract long getValidatedTimeToEvent(OphthalmologicPatient pat, boolean firstEye);
	
}
