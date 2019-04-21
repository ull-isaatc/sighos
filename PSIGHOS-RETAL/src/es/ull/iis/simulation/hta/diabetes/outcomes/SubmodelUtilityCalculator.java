/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.outcomes;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.interventions.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;

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
	public double getAcuteEventDisutilityValue(DiabetesPatient pat, DiabetesAcuteComplications comp) {
		return acuteSubmodels[comp.ordinal()].getDisutility(pat);
	}
	
	@Override
	public double getUtilityValue(DiabetesPatient pat) {
		double du = duDNC;
		for (DiabetesChronicComplications comp : DiabetesChronicComplications.values()) {
			if (pat.hasComplication(comp)) {
				du = method.combine(du, chronicSubmodels[comp.ordinal()].getDisutility(pat, method));
			}
		}
		return genPopUtility - du - ((DiabetesIntervention)pat.getIntervention()).getDisutility(pat);
	}
}
