/**
 * 
 */
package es.ull.iis.simulation.hta.progression.condition;

import java.util.Arrays;
import java.util.Map;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.util.ExtendedMath;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ExclusiveChoiceCondition extends Condition<DiseaseProgressionPathway.ConditionInformation> {
	private final SingleSelector[] selectors;
	private final Map<DiseaseProgression, String> paramsByProgression;

	/**
	 * 
	 */
	public ExclusiveChoiceCondition(HTAModel model, Map<DiseaseProgression, String> paramsByProgression) {
		super();
		this.paramsByProgression = paramsByProgression;
		this.selectors = new SingleSelector[model.getExperiment().getNRuns() + 1];
		Arrays.fill(this.selectors, null); 		
	}

	@Override
	public boolean check(DiseaseProgressionPathway.ConditionInformation info) {
		final Patient pat = info.getPatient();
		if (selectors[pat.getSimulation().getIdentifier()] == null) {
			selectors[pat.getSimulation().getIdentifier()] = new SingleSelector(pat);
		}
		return (info.getProgression().compareTo(selectors[pat.getSimulation().getIdentifier()].getDiseaseProgression(pat)) == 0);
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
			progressions = new DiseaseProgression[paramsByProgression.size()];
			double[] frequencies = new double[progressions.length];
			String rndKey = "PROP";
	        for (int i = 0; i < progressions.length; i++) {
	        	progressions[i] = (DiseaseProgression)paramsByProgression.keySet().toArray()[i];
	        	frequencies[i] = pat.getSimulation().getModel().getParameterValue(paramsByProgression.get(progressions[i]), pat);
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
