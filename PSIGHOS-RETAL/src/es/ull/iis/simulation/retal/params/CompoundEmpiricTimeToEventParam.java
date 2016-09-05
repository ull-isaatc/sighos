/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class CompoundEmpiricTimeToEventParam extends EmpiricTimeToEventParam {
	/** Probability of developing GA in fellow eye, given the state of the first eye */
	final protected EnumMap<EyeState, StructuredInfo> tuples = new EnumMap<EyeState, StructuredInfo>(EyeState.class);

	class StructuredInfo {
		/** Random number generator for this param */
		final RandomForPatient.ITEM rngItem;
		/** An internal list of generated times to event to be used when creating validated times to event */
		final LinkedList<Long> queue;
		/** First-eye incidence of EARM */
		final double [][] probabilities;
		
		public StructuredInfo(int nAgeGroups, RandomForPatient.ITEM rngItem) {
			this.rngItem = rngItem;
			this.probabilities = new double[nAgeGroups][3];
			this.queue = new LinkedList<Long>();
		}
		
		public StructuredInfo() {
			this.rngItem = null;
			this.probabilities = null;
			this.queue = null;
		}
		
		public long getTimeToEvent(Patient pat) {
			if (probabilities == null) {
				return Long.MAX_VALUE;
			}
			else {
				final double []rnd = pat.draw(rngItem, probabilities.length);
				final double time = CompoundEmpiricTimeToEventParam.getTimeToEvent(probabilities, pat.getAge(), rnd);
				return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : pat.getTs() + simul.getTimeUnit().convert(time, unit);
			}
		}
		
		public long getValidatedTimeToEvent(Patient pat) {
			final long timeToDeath = pat.getTimeToDeath();
			final long currentTime = pat.getTs();
			long timeToEvent;
			// If there are no stored values in the queue, generate a new one
			if (queue.isEmpty()) {
				timeToEvent = getTimeToEvent(pat);
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<Long> iter = queue.iterator();
				do {
					timeToEvent = iter.next();
					if (timeToEvent < timeToDeath)
						iter.remove();
					// Check if the stored time already passed --> If so, discharge
					if (timeToEvent <= currentTime)
						timeToEvent = Long.MAX_VALUE;
				} while (iter.hasNext() && timeToEvent >= timeToDeath);
				// If no valid event is found, generate a new one
				if (timeToEvent >= timeToDeath)
					timeToEvent = getTimeToEvent(pat);
			}
			// Generate new times to event until we get a valid one
			while (timeToEvent != Long.MAX_VALUE && timeToEvent >= timeToDeath) {
				queue.push(timeToEvent);
				timeToEvent = getTimeToEvent(pat);
			}
			return timeToEvent;
		}
	}
	/**
	 * 
	 */
	public CompoundEmpiricTimeToEventParam(RETALSimulation simul, boolean baseCase, TimeUnit unit) {
		super(simul, baseCase, unit);
	}

	/**
	 * Returns the "brute" simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @param firstEye True if the event applies to the first eye; false if the event applies to the fellow eye
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	protected long getTimeToEvent(Patient pat, int eye) {
		final EnumSet<EyeState> otherEye = pat.getEyeState(1 - eye);
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
	
}
