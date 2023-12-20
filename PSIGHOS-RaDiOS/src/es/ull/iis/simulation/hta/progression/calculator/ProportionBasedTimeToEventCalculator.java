/**
 * 
 */
package es.ull.iis.simulation.hta.progression.calculator;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ProportionBasedTimeToEventCalculator implements TimeToEventCalculator {
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Name of the second order parameter that defines the proportion */
	private final String propParamName;
	
	public ProportionBasedTimeToEventCalculator(DiseaseProgression destManifestation, String propParamName) {
		this.destManifestation = destManifestation;
		this.propParamName = propParamName;	
	}

	@Override
	public TimeUnit getTimeUnit() {
		return TimeUnit.YEAR;
	}

	@Override
	public double getTimeToEvent(Patient pat) {
		final double proportion = destManifestation.getModel().getParameterValue(propParamName, pat);
		// Generates two random numbers: the first indicates whether the patient will suffer the problem; the second serves to compute time to event 
		List<Double> rndValues = pat.getRandomNumbersForIncidence(destManifestation, 2);
		// Do the patient suffers the problem?
		if (proportion > rndValues.get(0)) {
			final double age = pat.getAge();
			final double deathAge = pat.getAgeAtDeath();
			// Gets another random number
			// Only if lifetime of the patient is compatible with the manifestation
			final double endAge = destManifestation.getUsedParameterValue(StandardParameter.DISEASE_PROGRESSION_END_AGE, pat); 
			final double onsetAge = destManifestation.getUsedParameterValue(StandardParameter.DISEASE_PROGRESSION_ONSET_AGE, pat); 
			if (endAge > age && onsetAge < deathAge) {
				final double minRef = Math.max(age, onsetAge);
				final double maxRef = Math.min(deathAge, endAge);
				return rndValues.get(1) * (maxRef - minRef) + (minRef - age);
			}
		}				
		return Double.NaN;
	}

}
