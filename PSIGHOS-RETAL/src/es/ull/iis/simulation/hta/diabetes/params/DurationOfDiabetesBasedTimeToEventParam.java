/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;

/**
 * A parameter that computes time to event based on the duration of diabetes of the patient
 * @author Iv�n Castilla
 *
 */
public class DurationOfDiabetesBasedTimeToEventParam implements TimeToEventParam {
	/** -1/(annual risk of the event) */
	private final double[][] minusAvgTimeToEvent; 
	/** Relative risk calculator */
	private final RRCalculator rr;
	/** Random number generator */
	private final RandomNumber rng;
	/** The value generated for this parameter for each patient */
	private final double[][] generated;

	/**
	 * Creates a parameter that computes time to event based on the duration of diabetes of the patient
	 * @param rng random number generator
	 * @param nPatients Number of patients
	 * @param durationNRisks A duration-of-diabetes-ordered array of pairs {duration of diabetes, risk}
	 * @param rr Relative risk to apply whenever the effective time to event is computed
	 */
	public DurationOfDiabetesBasedTimeToEventParam(RandomNumber rng, int nPatients, double[][] durationNRisks, RRCalculator rr) {
		this.rng = rng;
		this.generated = new double[nPatients][durationNRisks.length];
		for (int i = 0; i < nPatients; i++) {
			Arrays.fill(generated[i], Double.NaN);	
		}
		minusAvgTimeToEvent = new double[durationNRisks.length][2] ;
		for (int i = 0; i < durationNRisks.length; i++) {
			minusAvgTimeToEvent[i][0] = durationNRisks[i][0];
			minusAvgTimeToEvent[i][1] = -1 / durationNRisks[i][1];
		}
		this.rr = rr;
	}

	@Override
	public Long getValue(DiabetesPatient pat) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double durationOfDiabetes = pat.getDurationOfDiabetes();
		double ref = 0.0;
		for (int i = 0; i < minusAvgTimeToEvent.length; i++) {
			if (minusAvgTimeToEvent[i][0] > durationOfDiabetes) {
				if (!Double.isInfinite(minusAvgTimeToEvent[i][1])) {
					final double newMinus = -1 / (1-Math.exp(Math.log(1+1/minusAvgTimeToEvent[i][1])*rr.getRR(pat)));
					final double time = newMinus * Math.log(draw(pat, i)) + ref;					
					if (time + durationOfDiabetes < minusAvgTimeToEvent[i][0])
						return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
				}
				ref = minusAvgTimeToEvent[i][0] - durationOfDiabetes;
			}
		}
		return Long.MAX_VALUE;
	}

	/**
	 * Returns a value in [0, 1] for the patient. Always returns the same value for the same patient.
	 * @param pat A patient
	 * @return a value in [0, 1] for the patient
	 */
	private double draw(DiabetesPatient pat, int order) {
		if (Double.isNaN(generated[pat.getIdentifier()][order])) {
			generated[pat.getIdentifier()][order] = rng.draw();
		}
		return generated[pat.getIdentifier()][order];
	}
	
}
