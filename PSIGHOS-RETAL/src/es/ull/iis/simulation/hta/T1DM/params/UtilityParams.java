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

	private final static double DET_DNC_DISUTILITY = 0.0351;
	private final static double[] DET_COMPLICATION_DISUTILITIES = {(0.0409 + 0.0412) / 2, 0.0244, 0.0527, 0.0156, (0.0379 + 0.0244) / 2, 0.0603, 0.0498};
	private final static double BASE_UTILITY = 0.911400915;
	private final double[] dComplications;
	private final double dDNC;
	private final double baseUtility;
	private final static double HYPO_EVENT_DISUTILITY = 0.0206;
	
	private final CombinationMethod method;
	/**
	 */
	public UtilityParams(CombinationMethod method) {
		super();
		this.dDNC = DET_DNC_DISUTILITY;
		this.dComplications = DET_COMPLICATION_DISUTILITIES;
		this.method = method;
		this.baseUtility = BASE_UTILITY;
	}

	/**
	 */
	public UtilityParams() {
		this(CombinationMethod.ADD);
	}
	
	public double getHypoEventDisutilityValue() {
		return HYPO_EVENT_DISUTILITY;
	}
	
	public double getUtilityValue(T1DMPatient pat) {
		final EnumSet<Complication> state = pat.getState();
		double u = baseUtility;
		if (CommonParams.CANADA) {
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
			u = baseUtility;
			switch(method) {
			case ADD:
				u -= dDNC;
				for (Complication comp : state) {
					u -= dComplications[comp.ordinal()];
				}
				break;
			case MIN:
				double du = dDNC;
				for (Complication comp : state) {
					if (dComplications[comp.ordinal()] > du) {
						du = dComplications[comp.ordinal()];
					}
				}
				u -= du;
				break;
			case MULT:
				u -= dDNC;
				for (Complication comp : state) {
					u *= (baseUtility - dComplications[comp.ordinal()]);
				}
				break;
			}
		}
		return u;
	}
}
