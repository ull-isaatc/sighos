/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;

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
	public double getAcuteEventDisutilityValue(T1DMPatient pat, T1DMAcuteComplications comp) {
		return acuteSubmodels[comp.ordinal()].getDisutility(pat);
	}
	
	@Override
	public double getUtilityValue(T1DMPatient pat) {
		double du = duDNC;
		for (T1DMChronicComplications comp : T1DMChronicComplications.values()) {
			if (pat.hasComplication(comp)) {
				du = method.combine(du, chronicSubmodels[comp.ordinal()].getDisutility(pat, method));
			}
		}
		return genPopUtility - du;
	}
}
