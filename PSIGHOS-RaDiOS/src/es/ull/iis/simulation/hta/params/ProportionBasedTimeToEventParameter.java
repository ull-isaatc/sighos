/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.List;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ProportionBasedTimeToEventParameter extends Parameter {
	/** Manifestation to which progress */
	private final DiseaseProgression destManifestation;
	public enum USED_PARAMETERS implements UsedParameter {
		PROPORTION,
	}
	
	/**
	 * 
	 * @param model
	 * @param paramName
	 * @param type
	 * @param destManifestation
	 * @param proportionParamName
	 */
	public ProportionBasedTimeToEventParameter(HTAModel model, String paramName, String description, String source, int year, DiseaseProgression destManifestation) {
		super(model, paramName, description, source, year, ParameterType.RISK);
		this.destManifestation = destManifestation;
		setUsedParameterName(USED_PARAMETERS.PROPORTION, StandardParameter.PROPORTION.createName(destManifestation));
		
	}

	@Override
	public double getValue(Patient pat) {
		final double proportion = destManifestation.getModel().getParameterValue(getUsedParameterName(USED_PARAMETERS.PROPORTION), pat);
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
				return rndValues.get(1) * (maxRef - minRef) + (minRef - age);
			}
		}				
		return Double.NaN;
	}

}
