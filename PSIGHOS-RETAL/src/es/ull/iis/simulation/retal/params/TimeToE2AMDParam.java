/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeToE2AMDParam extends EmpiricTimeToEventParam {

	/**
	 * <Minimum age. maximum age, probability of CNV, probability of GA> given EARM in first eye. 
	 * Source: Karnon model
	 */
	private final static double[][] P_E2AMD_E1EARM = {
			{60, 65, 0.014269706, 0.049910436},
			{65, 70, 0.021893842, 0.060853865},
			{70, CommonParams.MAX_AGE, 0.024646757, 0.079085662}
	};
	
	/**
	 * <Minimum age. maximum age, probability of CNV, probability of GA> given GA in first eye. 
	 * Source: Karnon model
	 */
	private final static double[][] P_E2AMD_E1GA = { 
			{60, 65, 0.0167547991254577, 0.0538734777861744},   
			{65, 70, 0.0254060050469566, 0.0667204941316263},   
			{70, CommonParams.MAX_AGE, 0.0287673496030733, 0.0850931858956147} 
	};

	/**
	 * <Minimum age. maximum age, probability of CNV, probability of GA> given CNV in first eye. 
	 * Source: Karnon model
	 */
	private final static double[][] P_E2AMD_E1CNV = { 
			{60, 65, 0.0492981931784431, 0.00591561395661435},   
			{65, 70, 0.0762051095873376, 0.00706997998864217},   
			{70, CommonParams.MAX_AGE, 0.0851567190055321, 0.00925261820611631} 
	};

	/** Probability of developing GA in fellow eye, given the state of the first eye */
	final protected EnumMap<EyeState, StructuredInfo> tuples = new EnumMap<EyeState, StructuredInfo>(EyeState.class);

	class StructuredInfo {
		/** Random number generator for this param */
		final Random rng;
		/** An internal list of generated times to event to be used when creating validated times to event */
		final LinkedList<long[]> queue;
		/** Age-adjusted Probabilities to progress to CNV or GA. Each row contains <low limit of time interval,  
		 * high limit of time interval, probability of CNV, accumulated probability of GA + CNV>*/
		final double [][] probabilities;
		
		public StructuredInfo(double[][] probabilities) {
			this.rng = new Random();
			this.probabilities = probabilities;
			this.queue = new LinkedList<long[]>();
		}
		
		public StructuredInfo() {
			this.rng = null;
			this.probabilities = null;
			this.queue = null;
		}
		
		/**
		 * Returns a pair <time to event, state at event>
		 * @param pat
		 * @return
		 */
		public long[] getTimeToEvent(OphthalmologicPatient pat) {
			if (probabilities == null) {
				return null;
			}
			else {
				final double []rnd = new double[probabilities.length];
				for (int j = 0; j < probabilities.length; j++)
					rnd[j] = rng.nextDouble();
				final double currentAge = pat.getAge();
				// Start by assigning "infinite" to ageAtEvent
				double ageAtEvent = Double.MAX_VALUE;
				EyeState stateAtEvent = EyeState.HEALTHY;
				for (int i = 0; i < probabilities.length; i++) {
					double[] entry = probabilities[i];
					if (currentAge <= entry[0]) {
						if (rnd[i] < entry[2]) {
							// Uniformly assign a random age at event within the current period
							ageAtEvent = entry[0] + Math.random() * (entry[1] - entry[0]) - currentAge;
							stateAtEvent = EyeState.AMD_CNV;
							break;
						}
						else if (rnd[i] < entry[3]) {
							// Uniformly assign a random age at event within the current period
							ageAtEvent = entry[0] + Math.random() * (entry[1] - entry[0]) - currentAge;
							stateAtEvent = EyeState.AMD_GA;
							break;
						}
					}
					// In case the age of the individual is included within the current interval
					else if (currentAge < entry[1]) {
						if (rnd[i] < entry[2]) {
							// Uniformly assign a random age at event within the current period, taking
							// into account the current age of the individual
							ageAtEvent = Math.random() * (entry[1] - currentAge);
							stateAtEvent = EyeState.AMD_CNV;
							break;
						}
						else if (rnd[i] < entry[3]) {
							// Uniformly assign a random age at event within the current period, taking
							// into account the current age of the individual
							ageAtEvent = Math.random() * (entry[1] - currentAge);
							stateAtEvent = EyeState.AMD_GA;
							break;
						}
					}
				}
				if (ageAtEvent == Double.MAX_VALUE) 
					return null;				
				return new long[] {pat.getTs() + pat.getSimulation().getTimeUnit().convert(ageAtEvent, unit), stateAtEvent.ordinal()};
			}
		}
	}
	/**
	 * 
	 */
	public TimeToE2AMDParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		// FIXME: should work differently when baseCase = false
		
		// Initialize probability of fellow-eye developing CNV given EARM in first eye
		tuples.put(EyeState.EARM, new StructuredInfo(P_E2AMD_E1EARM));
		// Initialize probability of fellow-eye developing CNV given CNV in first eye
		tuples.put(EyeState.AMD_CNV, new StructuredInfo(P_E2AMD_E1CNV));
		// Initialize probability of fellow-eye developing CNV given GA in first eye
		tuples.put(EyeState.AMD_GA, new StructuredInfo(P_E2AMD_E1GA));
		
		// FIXME: Check
		tuples.put(EyeState.HEALTHY, new StructuredInfo());

	}

	public long[] getTimeToEventAndState(OphthalmologicPatient pat) {
		final EnumSet<EyeState> otherEye = pat.getEyeState(0);
		final long[] timeAndState;
		
		// Other eye has CNV
		if (otherEye.contains(EyeState.AMD_CNV)) {
			timeAndState = tuples.get(EyeState.AMD_CNV).getTimeToEvent(pat);
		}
		// Other eye has GA
		else if (otherEye.contains(EyeState.AMD_GA)) {
			timeAndState = tuples.get(EyeState.AMD_GA).getTimeToEvent(pat);
		}
		// Other eye has EARM
		else if (otherEye.contains(EyeState.EARM)) {
			timeAndState = tuples.get(EyeState.EARM).getTimeToEvent(pat);
		}
		else if (otherEye.contains(EyeState.HEALTHY)) {
			timeAndState = tuples.get(EyeState.HEALTHY).getTimeToEvent(pat);
		}
		else {
			timeAndState = null;
		}			
		return timeAndState;		
	}
	
	public long[] getValidatedTimeToEventAndState(OphthalmologicPatient pat) {
		final long timeToDeath = pat.getTimeToDeath();
		long[] timeAndState;
		final EnumSet<EyeState> otherEye = pat.getEyeState(0);
		final StructuredInfo info;
		
		if (otherEye.contains(EyeState.AMD_CNV)) {
			 info = tuples.get(EyeState.AMD_CNV);
		}
		// Other eye has GA
		else if (otherEye.contains(EyeState.AMD_GA)) {
			info = tuples.get(EyeState.AMD_GA);
		}
		// Other eye has EARM
		else if (otherEye.contains(EyeState.EARM)) {
			info = tuples.get(EyeState.EARM);
		}
		else if (otherEye.contains(EyeState.HEALTHY)) {
			info = tuples.get(EyeState.HEALTHY);
		}
		else {
			pat.error("Invalid state in other eye when computing time from EARM to AMD");
			return null;
		}
		
		// If there are no stored values in the queue, generate a new one
		if (info.queue.isEmpty()) {
			timeAndState = info.getTimeToEvent(pat);
		}
		// If there are stored values in the queue, I try with them in the first place
		else {
			final Iterator<long[]> iter = info.queue.iterator();
			do {
				timeAndState = iter.next();
				if (timeAndState[0] < timeToDeath)
					iter.remove();
			} while (iter.hasNext() && timeAndState[0] >= timeToDeath);
			// If no valid event is found, generate a new one
			if (timeAndState[0] >= timeToDeath)
				timeAndState = info.getTimeToEvent(pat);
		}
		// Generate new times to event until we get a valid one
		while (timeAndState != null && timeAndState[0] >= timeToDeath) {
			info.queue.push(timeAndState);
			timeAndState = info.getTimeToEvent(pat);
		}
		return timeAndState;		
	}
	
}
