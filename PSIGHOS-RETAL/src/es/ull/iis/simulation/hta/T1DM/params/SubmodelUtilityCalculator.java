/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.MainChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SubmodelUtilityCalculator implements UtilityCalculator {
	private final double duDNC;
	private final double genPopUtility;
	private final ChronicComplicationSubmodel[] submodels;
	private final AcuteComplicationSubmodel[] acuteSubmodels;
	private final DisutilityCombinationMethod method;

	/**
	 */
	public SubmodelUtilityCalculator(DisutilityCombinationMethod method, double duDNC, double genPopUtility, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		this.duDNC = duDNC;
		this.submodels = submodels;
		this.acuteSubmodels = acuteSubmodels;
		this.method = method;
		this.genPopUtility = genPopUtility;
	}

	public double getAcuteEventDisutilityValue(T1DMPatient pat, MainAcuteComplications comp) {
		return acuteSubmodels[comp.ordinal()].getDisutility(pat);
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		double du = duDNC;
		for (MainChronicComplications comp : MainChronicComplications.values()) {
			if (pat.hasComplication(comp)) {
				du = method.combine(du, submodels[comp.ordinal()].getDisutility(pat, method));
			}
		}
		return genPopUtility - du;
	}
}
