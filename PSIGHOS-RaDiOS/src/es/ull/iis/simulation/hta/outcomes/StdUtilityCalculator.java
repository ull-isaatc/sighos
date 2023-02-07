/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.Collection;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A standard utility calculator that simply collects constant disutility values for each complication and then 
 * combines them according to the defined {@link DisutilityCombinationMethod} and the current health state of the patient.
 * Acute events and no complication utilities are defined in the constructor. 
 * 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class StdUtilityCalculator implements UtilityCalculator {
	/** Method used to combine the disutilities for different chronic complications */
	private final DisutilityCombinationMethod method;
	private final SecondOrderParamsRepository secParams;

	/**
	 * Creates an instance of a standard calculator that simply combines the disutilities for every complication that suffers the
	 * patient
	 * @param method Method to combine the disutilies
	 * @param duAcuteEvent Disutility of acute events
	 */
	public StdUtilityCalculator(SecondOrderParamsRepository secParams, DisutilityCombinationMethod method) {
		this.secParams = secParams;
		this.method = method;
	}
	
	@Override
	public double getDisutilityValueUponIncidence(Patient pat, Manifestation manif) {
		return secParams.getDisutilitiesForManifestation(manif, pat.getSimulation().getIdentifier())[1];
	}
	
	@Override	
	public double getUtilityValue(Patient pat) {
		final Collection<Manifestation> state = pat.getState();
		double du = 0.0;
		for (Manifestation manif : state) {
			du = method.combine(du, secParams.getDisutilitiesForManifestation(manif, pat.getSimulation().getIdentifier())[0]);
		}
		return secParams.getBaseUtility(pat.getSimulation().getIdentifier()) - du - pat.getIntervention().getDisutility(pat);
	}
}
