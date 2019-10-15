/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;
import simkit.random.RandomNumber;

/**
 * A parameter that computes time to event based on the duration of diabetes of the patient
 * @author Iván Castilla
 *
 */
public class AgeBasedTimeToEventParam implements TimeToEventParam {
	/** Annual risks of the events */
	private final double[][] ageRisks; 
	/** Relative risk calculator */
	private final RRCalculator rr;
	/** Random number generator */
	private final RandomNumber rng;
	/** The natural logarithm of the values generated for this parameter for each patient */
	private final double[][] logGenerated;

	/**
	 * Creates a parameter that computes time to event based on the age of the patient
	 * @param rng random number generator
	 * @param nPatients Number of patients
	 * @param ageRisks An age-ordered array of pairs {age, risk}. The first element represents the maximum age the risk is applied to.
	 * @param rr Relative risk to apply whenever the effective time to event is computed
	 */
	public AgeBasedTimeToEventParam(RandomNumber rng, int nPatients, final double[][] ageRisks, RRCalculator rr) {
		this.rng = rng;
		this.logGenerated = new double[nPatients][ageRisks.length];
		for (int i = 0; i < nPatients; i++) {
			Arrays.fill(logGenerated[i], Double.NaN);	
		}
		this.ageRisks = ageRisks;
		this.rr = rr;
	}

	@Override
	public Long getValue(DiabetesPatient pat) {
		final double age = pat.getAge();
		final double lifetime = pat.getAgeAtDeath() - age;
		// Searches the corresponding age interval
		int interval = 0;
		while (age > ageRisks[interval][0])
			interval++;
		// Computes time to event within such interval
		double time = Statistics.getAnnualBasedTimeToEvent(ageRisks[interval][1], draw(pat, interval), rr.getRR(pat));
		
		// Checks if further intervals compute lower time to event
		for (; interval < ageRisks.length; interval++) {
			final double newTime = Statistics.getAnnualBasedTimeToEvent(ageRisks[interval][1], draw(pat, interval), rr.getRR(pat));
			if ((newTime != Double.MAX_VALUE) && (ageRisks[interval][0] - age + newTime < time))
				time = ageRisks[interval][0] - age + newTime;
		}
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}

	/**
	 * Returns a value in [0, 1] for the patient. Always returns the same value for the same patient.
	 * @param pat A patient
	 * @return a value in [0, 1] for the patient
	 */
	private double draw(DiabetesPatient pat, int order) {
		if (Double.isNaN(logGenerated[pat.getIdentifier()][order])) {
			logGenerated[pat.getIdentifier()][order] = Math.log(rng.draw());
		}
		return logGenerated[pat.getIdentifier()][order];
	}
	
}
