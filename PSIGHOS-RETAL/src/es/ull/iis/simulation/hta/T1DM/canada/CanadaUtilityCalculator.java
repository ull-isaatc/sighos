/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaUtilityCalculator implements UtilityCalculator {
	private final double duDNC;
	private final double genPopUtility;
	private final double duHypoEvent;
	
	/**
	 */
	public CanadaUtilityCalculator(double duDNC, double genPopUtility, double duHypoEvent) {
		this.duDNC = duDNC;
		this.genPopUtility = genPopUtility;
		this.duHypoEvent = duHypoEvent;
	}

	public double getHypoEventDisutilityValue() {
		return duHypoEvent;
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		final EnumSet<MainComplications> state = pat.getState();
		double u = genPopUtility;
		u -= duDNC;
		if (state.contains(MainComplications.ESRD)) {
			u = state.contains(MainComplications.CHD) ? 0.447 : 0.490;
		}
		else if (state.contains(MainComplications.LEA)) {
			u = state.contains(MainComplications.CHD) ? 0.511 : 0.534;
		}			
		else if (state.contains(MainComplications.CHD)) {
			if (state.contains(MainComplications.NPH))
				u = 0.516;
			else if (state.contains(MainComplications.NEU))
				u = 0.544;
			else if (state.contains(MainComplications.BGRET))
				u = 0.553;
			else if (state.contains(MainComplications.BLI))
				u = 0.569;
			else
				u = 0.685;
		}
		else if (state.contains(MainComplications.BLI)) {
			u = 0.569;
		}
		else if (state.contains(MainComplications.NPH)) {
			u = state.contains(MainComplications.NEU) ? 0.557 : 0.575;
		}
		else if (state.contains(MainComplications.BGRET)) {
			u = 0.612;
		}
		else if (state.contains(MainComplications.NEU)) {
			u = 0.624;
		}			
		return u;
	}
}
