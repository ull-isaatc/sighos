/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A utility calculator that relies on the specific disease to compute the disutility, and then 
 * combines it with the disutility from the intervention according to the defined {@link DisutilityCombinationMethod} and the current health state of the patient.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class DiseaseUtilityCalculator implements UtilityCalculator {
	/** Utility assigned to the general population */
	private final double genPopUtility;
	/** Method used to combine the disutilities for different chronic complications */
	private final DisutilityCombinationMethod method;
	private final SecondOrderParamsRepository secParams;

	/**
	 * Creates an instance of a standard calculator that simply combines the disutilities for every complication that suffers the
	 * patient
	 * @param method Method to combine the disutilies
	 * @param genPopUtility Utility for general population
	 * @param duAcuteEvent Disutility of acute events
	 */
	public DiseaseUtilityCalculator(SecondOrderParamsRepository secParams, DisutilityCombinationMethod method, double genPopUtility) {
		this.secParams = secParams;
		this.method = method;
		this.genPopUtility = genPopUtility;
	}
	
	@Override
	public double getDisutilityValueUponIncidence(Patient pat, Manifestation manif) {
		return secParams.getDisutilitiesForManifestation(manif, pat.getSimulation().getIdentifier(), genPopUtility)[1];
	}
	
	@Override	
	public double getUtilityValue(Patient pat) {
		return genPopUtility - method.combine(pat.getDisease().getDisutility(pat, method, genPopUtility), pat.getIntervention().getDisutility(pat));
	}
}
