/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.AcuteComplication;
import es.ull.iis.simulation.hta.ChronicComplication;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.progression.ChronicComplicationSubmodel;

/**
 * A calculator of utilities that relies on the submodels to compute each complication disutility, and then combines the
 * disutilities into a single value according to the {@link DisutilityCombinationMethod}. No complication utility is defined
 * in the constructor.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class SubmodelUtilityCalculator implements UtilityCalculator {
	/** Disutility for diabetes with no complications */
	private final double duDNC;
	/** Utility assigned to the general population, without diabetes */
	private final double genPopUtility;
	/** Submodels for each chronic complication */
	private final ChronicComplicationSubmodel[] chronicSubmodels;
	/** Submodels for each acute complication */
	private final AcuteComplicationSubmodel[] acuteSubmodels;
	/** Method used to combine the disutilities for different chronic complications */
	private final DisutilityCombinationMethod method;

	/**
	 * 
	 * @param method Method used to combine the disutilities for different chronic complications
	 * @param duDNC Disutility for diabetes with no complications
	 * @param genPopUtility Utility assigned to the general population, without diabetes
	 * @param chronicSubmodels Submodels for each chronic complication
	 * @param acuteSubmodels Submodels for each acute complication
	 */
	public SubmodelUtilityCalculator(DisutilityCombinationMethod method, double duDNC, double genPopUtility, ChronicComplicationSubmodel[] chronicSubmodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		this.duDNC = duDNC;
		this.chronicSubmodels = chronicSubmodels;
		this.acuteSubmodels = acuteSubmodels;
		this.method = method;
		this.genPopUtility = genPopUtility;
	}

	@Override
	public double getAcuteEventDisutilityValue(Patient pat, AcuteComplication comp) {
		return acuteSubmodels[comp.getInternalId()].getDisutility(pat);
	}
	
	@Override
	public double getUtilityValue(Patient pat) {
		double du = duDNC;
		for (ChronicComplication comp : ChronicComplication.values()) {
			if (pat.hasDisease(comp)) {
				du = method.combine(du, chronicSubmodels[comp.getInternalId()].getDisutility(pat, method));
			}
		}
		return genPopUtility - du - pat.getIntervention().getDisutility(pat);
	}
}
