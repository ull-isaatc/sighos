/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class UtilityParams extends ModelParams {
	public enum CombinationMethod {
		ADD,
		MIN,
		MULT
	}

	private final double[] duComplications;
	private final double duDNC;
	private final double genPopUtility;
	private final double duHypoEvent;
	
	private final CombinationMethod method;

	private final SecondOrderParams secParams;
	/**
	 */
	public UtilityParams(SecondOrderParams secParams) {
		super();
		this.secParams = secParams;
		this.duDNC = secParams.getNoComplicationDisutility();
		this.duComplications = secParams.getComplicationDisutilities();
		this.method = secParams.getUtilityCombinationMethod();
		this.genPopUtility = secParams.getGeneralPopulationUtility();
		this.duHypoEvent = secParams.getHypoEventDisutility();
	}

	public double getHypoEventDisutilityValue() {
		return duHypoEvent;
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		final EnumSet<Complication> state = pat.getState();
		double u = genPopUtility;
		if (secParams.isCanadaValidation()) {
			u -= duDNC;
			if (state.contains(Complication.ESRD)) {
				u = state.contains(Complication.CHD) ? 0.447 : 0.490;
			}
			else if (state.contains(Complication.LEA)) {
				u = state.contains(Complication.CHD) ? 0.511 : 0.534;
			}			
			else if (state.contains(Complication.CHD)) {
				if (state.contains(Complication.NPH))
					u = 0.516;
				else if (state.contains(Complication.NEU))
					u = 0.544;
				else if (state.contains(Complication.RET))
					u = 0.553;
				else if (state.contains(Complication.BLI))
					u = 0.569;
				else
					u = 0.685;
			}
			else if (state.contains(Complication.BLI)) {
				u = 0.569;
			}
			else if (state.contains(Complication.NPH)) {
				u = state.contains(Complication.NEU) ? 0.557 : 0.575;
			}
			else if (state.contains(Complication.RET)) {
				u = 0.612;
			}
			else if (state.contains(Complication.NEU)) {
				u = 0.624;
			}			
		}
		else {
			switch(method) {
			case ADD:
				u -= duDNC;
				for (Complication comp : state) {
					u -= duComplications[comp.ordinal()];
				}
				break;
			case MIN:
				double du = duDNC;
				for (Complication comp : state) {
					if (duComplications[comp.ordinal()] > du) {
						du = duComplications[comp.ordinal()];
					}
				}
				u -= du;
				break;
			case MULT:
				u -= duDNC;
				for (Complication comp : state) {
					u *= (genPopUtility - duComplications[comp.ordinal()]);
				}
				break;
			}
		}
		return u;
	}
}
