/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Arrays;
import java.util.Map;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.util.ExtendedMath;

/**
 * @author Iván Castilla
 *
 */
public class ExclusiveChoiceDiseaseProgressionPathway implements DiseaseProgressionPathway {
	/** Condition that must be met progress to a manifestation */
	private final Condition<Patient> condition;
	/** Calculator of the time to event if the condition is met */
	private final TimeToEventCalculator timeToEvent;
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;

	private final SingleSelector[] selectors;
	private final Map<DiseaseProgression, String> paramsByProgression;
	
	/**
	 * @param secParams
	 * @param nextProgression
	 * @param condition
	 * @param timeToEvent
	 */
	public ExclusiveChoiceDiseaseProgressionPathway(SecondOrderParamsRepository secParams, Map<DiseaseProgression, String> paramsByProgression, Condition<Patient> condition, TimeToEventCalculator timeToEvent) {
		this.secParams = secParams;
		this.condition = condition;
		this.timeToEvent = timeToEvent;
		this.paramsByProgression = paramsByProgression;
		this.selectors = new SingleSelector[secParams.getNRuns() + 1];
		Arrays.fill(this.selectors, null); 
	}

	/**
	 * @param secParams
	 * @param nextProgression
	 * @param timeToEvent
	 */
	public ExclusiveChoiceDiseaseProgressionPathway(SecondOrderParamsRepository secParams, Map<DiseaseProgression, String> paramsByProgression, TimeToEventCalculator timeToEvent) {
		this(secParams, paramsByProgression, new TrueCondition<Patient>(), timeToEvent);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
	}

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
	
	@Override
	public long getTimeToEvent(Patient pat, long limit) {
		if (condition.check(pat)) {
			final long time = timeToEvent.getTimeToEvent(pat);
			return (time >= limit) ? Long.MAX_VALUE : time;
		}
		return Long.MAX_VALUE;
	}

	@Override
	public Condition<Patient> getCondition() {
		return condition;
	}

	@Override
	public DiseaseProgression getNextProgression(Patient pat) {
		if (selectors[pat.getSimulation().getIdentifier()] == null) {
			selectors[pat.getSimulation().getIdentifier()] = new SingleSelector(pat);
		}
		return selectors[pat.getSimulation().getIdentifier()].getDiseaseProgression(pat);
	}

	/**
	 * A parameter to select between N different options (labeled 0, 1, ..., N - 1). In a single simulation replication, the selection will be always the same for each patient
	 * Adapted from "simkit.random.DiscreteIntegerVariate" (https://github.com/kastork/simkit-mirror/blob/master/src/simkit/random/DiscreteIntegerVariate.java)
	 * @author Iván Castilla
	 *
	 */
	public class SingleSelector {
		private final double[] cdf;
		private final DiseaseProgression[] progressions;
		private final String rndKey;

		/**
		 * @param rng Random number generator
		 * @param nPatients Number of patients simulated
		 */
		public SingleSelector(Patient pat) {
			progressions = (DiseaseProgression[])paramsByProgression.keySet().toArray();
			double[] frequencies = new double[progressions.length];
			String rndKey = "PROP";
	        for (int i = 1; i < progressions.length; i++) {
	        	frequencies[i] = secParams.getParameter(paramsByProgression.get(progressions[i]), pat.getSimulation());
	        	rndKey += "_" + progressions[i].name();
	        }
	        this.rndKey = rndKey;
			frequencies = ExtendedMath.normalize(frequencies);
	        cdf = new double[frequencies.length];
	        cdf[0] = frequencies[0];
	        for (int i = 1; i < frequencies.length; i++) {
	                cdf[i] += cdf[i - 1] + frequencies[i];
	        }
		}

		public DiseaseProgression getDiseaseProgression(Patient pat) {
			int index;
			final double rnd = pat.getRandomNumber(rndKey);
			for (index = 0; (rnd > cdf[index]) && (index < cdf.length - 1); index++) ;
			return progressions[index];
		}

	}
	
}
