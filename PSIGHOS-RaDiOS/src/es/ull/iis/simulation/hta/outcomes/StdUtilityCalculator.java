/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.Collection;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A standard utility calculator that simply collects constant disutility values for each complication and then 
 * combines them according to the defined {@link DisutilityCombinationMethod} and the current health state of the patient.
 * Acute events and no complication utilities are defined in the constructor. 
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdUtilityCalculator implements UtilityCalculator {
	/** Utility assigned to the general population */
	private final double genPopUtility;
	/** Method used to combine the disutilities for different chronic complications */
	private final DisutilityCombinationMethod method;

	/**
	 * Creates an instance of a standard calculator that simply combines the disutilities for every complication that suffers the
	 * patient
	 * @param method Method to combine the disutilies
	 * @param genPopUtility Utility for general population
	 * @param duAcuteEvent Disutility of acute events
	 */
	public StdUtilityCalculator(DisutilityCombinationMethod method, double genPopUtility) {
		this.method = method;
		this.genPopUtility = genPopUtility;
	}
	
	@Override
	public double getPunctualDisutilityValue(Patient pat, Manifestation comp) {
		return comp.getDisutility(pat);
	}
	
	@Override	
	public double getUtilityValue(Patient pat) {
		final Collection<Manifestation> state = pat.getDetailedState();
		double du = 0.0;
		for (Manifestation comp : state) {
			du = method.combine(du, comp.getDisutility(pat));
		}
		return genPopUtility - du - pat.getIntervention().getDisutility(pat);
	}
}
