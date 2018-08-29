/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SubmodelUtilityCalculator implements UtilityCalculator {
	private final double duDNC;
	private final double genPopUtility;
	private final double duHypoEvent;
	private final ComplicationSubmodel[] submodels;
	private final DisutilityCombinationMethod method;

	/**
	 */
	public SubmodelUtilityCalculator(DisutilityCombinationMethod method, double duDNC, double genPopUtility, double duHypoEvent, ComplicationSubmodel[] submodels) {
		this.duDNC = duDNC;
		this.submodels = submodels;
		this.method = method;
		this.genPopUtility = genPopUtility;
		this.duHypoEvent = duHypoEvent;
	}

	public double getHypoEventDisutilityValue() {
		return duHypoEvent;
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		double du = duDNC;
		for (MainComplications comp : MainComplications.values()) {
			if (pat.hasComplication(comp)) {
				du = method.combine(du, submodels[comp.ordinal()].getDisutility(pat, method));
			}
		}
		return genPopUtility - du;
	}
}
