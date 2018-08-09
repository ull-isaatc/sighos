/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.simulation.hta.params.EmpiricTimeToEventParam;
import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;

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

	/**
	 * 
	 */
	public TimeToE2AMDParam() {
		super(TimeUnit.YEAR);
		// TODO: should work differently when  = false
		
		// Initialize probability of fellow-eye developing CNV given EARM in first eye
		tuples.put(EyeState.EARM, new StructuredInfo(P_E2AMD_E1EARM, RandomForPatient.ITEM.TIME_TO_E2AMD_E1EARM));
		// Initialize probability of fellow-eye developing CNV given CNV in first eye
		tuples.put(EyeState.AMD_CNV, new StructuredInfo(P_E2AMD_E1CNV, RandomForPatient.ITEM.TIME_TO_E2AMD_E1CNV));
		// Initialize probability of fellow-eye developing CNV given GA in first eye
		tuples.put(EyeState.AMD_GA, new StructuredInfo(P_E2AMD_E1GA, RandomForPatient.ITEM.TIME_TO_E2AMD_E1GA));
		
		// FIXME: Check
		tuples.put(EyeState.HEALTHY, new StructuredInfo());

	}

	public EyeStateAndValue getTimeToEventAndState(RetalPatient pat) {
		final EnumSet<EyeState> otherEye = pat.getEyeState(0);
		final EyeStateAndValue timeAndState;
		
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
	
	public EyeStateAndValue getValidatedTimeToEventAndState(RetalPatient pat) {
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
		// Other eye is healthy or affected by a different disease
		else {
			info = tuples.get(EyeState.HEALTHY);
		}
		
		return info.getValidatedTimeToEventAndState(pat);
	}
	
	private final class StructuredInfo {
		/** Random number generator for this param */
		private final RandomForPatient.ITEM rngItem;
		/** An internal list of generated times to event to be used when creating validated times to event */
		private final LinkedList<EyeStateAndValue> queue;
		/** Age-adjusted Probabilities to progress to CNV or GA. Each row contains <low limit of time interval,  
		 * high limit of time interval, probability of CNV, accumulated probability of GA + CNV>*/
		private final double [][] probabilities;
		
		public StructuredInfo(double[][] probabilities, RandomForPatient.ITEM rngItem) {
			this.rngItem = rngItem;
			this.probabilities = probabilities;
			this.queue = new LinkedList<EyeStateAndValue>();
		}
		
		public StructuredInfo() {
			this.rngItem = null;
			this.probabilities = null;
			this.queue = null;
		}
		
		/**
		 * Returns a pair <time to event, state at event>
		 * @param pat
		 * @return
		 */
		public EyeStateAndValue getTimeToEvent(RetalPatient pat) {
			if (probabilities == null) {
				return null;
			}
			else {
				final double []rnd = pat.draw(rngItem, probabilities.length);
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
				return new EyeStateAndValue(stateAtEvent, pat.getTs() + Math.max(CommonParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(ageAtEvent, unit)));
			}
		}

		public EyeStateAndValue getValidatedTimeToEventAndState(RetalPatient pat) {
			final long timeToDeath = pat.getTimeToDeath();
			final long currentTime = pat.getTs();
			EyeStateAndValue timeAndState;

			// If there are no stored values in the queue, generate a new one
			if (queue.isEmpty()) {
				timeAndState = getTimeToEvent(pat);
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<EyeStateAndValue> iter = queue.iterator();
				do {
					timeAndState = iter.next();
					if (timeAndState.getValue() < timeToDeath)
						iter.remove();
					// Check if the stored time already passed --> If so, discharge
				} while (iter.hasNext() && ((timeAndState.getValue() >= timeToDeath) || (timeAndState.getValue() <= currentTime)));
				// If no valid event is found, generate a new one
				if ((timeAndState.getValue() >= timeToDeath) || (timeAndState.getValue() <= currentTime))
					timeAndState = getTimeToEvent(pat);
			}
			// Generate new times to event until we get a valid one
			while (timeAndState != null && timeAndState.getValue() >= timeToDeath) {
				queue.push(timeAndState);
				timeAndState = getTimeToEvent(pat);
			}
			return timeAndState;		
		}
	}
}
