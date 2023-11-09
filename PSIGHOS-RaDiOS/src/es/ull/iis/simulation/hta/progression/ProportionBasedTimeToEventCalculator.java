/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ProportionBasedTimeToEventCalculator implements TimeToEventCalculator {
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	/** Repository of second order parameters */
	private final SecondOrderParamsRepository secParams;
	/** Name of the second order parameter that defines the proportion */
	private final String paramName;
	
	public ProportionBasedTimeToEventCalculator(String paramName, SecondOrderParamsRepository secParams, DiseaseProgression destManifestation) {
		this.secParams = secParams;
		this.destManifestation = destManifestation;
		this.paramName = paramName;
		
	}

	@Override
	public long getTimeToEvent(Patient pat) {
		final double proportion = secParams.getParameter(paramName, pat.getSimulation());
		// Generates two random numbers: the first indicates whether the patient will suffer the problem; the second serves to compute time to event 
		List<Double> rndValues = pat.getRandomNumbersForIncidence(destManifestation, 2);
		// Do the patient suffers the problem?
		if (proportion > rndValues.get(0)) {
			final double age = pat.getAge();
			final double deathAge = pat.getAgeAtDeath();
			// Gets another random number
			// Only if lifetime of the patient is compatible with the manifestation
			final double endAge = destManifestation.getEndAge(pat); 
			final double onsetAge = destManifestation.getOnsetAge(pat); 
			if (endAge > age && onsetAge < deathAge) {
				final double minRef = Math.max(age, onsetAge);
				final double maxRef = Math.min(deathAge, endAge);
				final double time = rndValues.get(1) * (maxRef - minRef) + (minRef - age);
				return pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
			}
		}				
		return Long.MAX_VALUE;
	}

}
