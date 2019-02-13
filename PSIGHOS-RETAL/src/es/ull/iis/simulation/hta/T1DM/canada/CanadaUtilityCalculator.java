/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator;

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

	@Override
	public double getAcuteEventDisutilityValue(T1DMPatient pat, T1DMAcuteComplications comp) {
		return duHypoEvent;
	}

	public double getUtilityValue(T1DMPatient pat) {
		final TreeSet<T1DMComplicationStage> state = pat.getDetailedState();
		double u = genPopUtility;
		u -= duDNC;
		if (state.contains(CanadaNPHSubmodel.ESRD)) {
			u = state.contains(CanadaCHDSubmodel.CHD) ? 0.447 : 0.490;
		}
		else if (state.contains(CanadaNEUSubmodel.LEA)) {
			u = state.contains(CanadaCHDSubmodel.CHD) ? 0.511 : 0.534;
		}			
		else if (state.contains(CanadaCHDSubmodel.CHD)) {
			if (state.contains(CanadaNPHSubmodel.NPH))
				u = 0.516;
			else if (state.contains(CanadaNEUSubmodel.NEU))
				u = 0.544;
			else if (state.contains(CanadaRETSubmodel.RET))
				u = 0.553;
			else if (state.contains(CanadaRETSubmodel.BLI))
				u = 0.569;
			else
				u = 0.685;
		}
		else if (state.contains(CanadaRETSubmodel.BLI)) {
			u = 0.569;
		}
		else if (state.contains(CanadaNPHSubmodel.NPH)) {
			u = state.contains(CanadaNEUSubmodel.NEU) ? 0.557 : 0.575;
		}
		else if (state.contains(CanadaRETSubmodel.RET)) {
			u = 0.612;
		}
		else if (state.contains(CanadaNEUSubmodel.NEU)) {
			u = 0.624;
		}			
		return u;
	}

}
