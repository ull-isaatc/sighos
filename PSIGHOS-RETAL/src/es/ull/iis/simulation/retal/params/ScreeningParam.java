/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla
 *
 */
public class ScreeningParam extends Param {

//	final private static double SENSITIVITY = 0.83;
	final private static double SPECIFICITY = 0.91;
//	final private static double[] SEVERE_MILD_SENSITIVITY = {0.9, 0.42};
//	final private static double EMD_SENSITIVITY = 0.90;
	final private static double[] EMD_SEVERE_MILD_SENSITIVITY = {0.97, 0.58};
//	final private static double CNV_SENSITIVITY = 0.83;
	final private static double[] CNV_SEVERE_MILD_SENSITIVITY = {0.96, 0.36};
	
	final public static int PERIODICITY_YEARS = 5;
	final public static int START_YEAR = 0;
	final private static double[] P_ATTENDING = {0,0};
	
	/**
	 * @param simul
	 * @param baseCase
	 */
	public ScreeningParam(boolean baseCase) {
		super(baseCase);
	}
	
	public double getSensitivity(Patient pat) {
		final EnumSet<EyeState> eye1 = pat.getEyeState(0);
		final EnumSet<EyeState> eye2 = pat.getEyeState(1);
		if (eye1.contains(EyeState.AMD_CNV) || eye2.contains(EyeState.AMD_CNV)) {
			final CNVStage.Position pos1 = (pat.getCurrentCNVStage(0) == null) ? null : pat.getCurrentCNVStage(0).getPosition();
			final CNVStage.Position pos2 = (pat.getCurrentCNVStage(1) == null) ? null : pat.getCurrentCNVStage(1).getPosition();
			// FIXME: Check with Rodrigo
			if (CNVStage.Position.SF.equals(pos1) || CNVStage.Position.SF.equals(pos2))
				return CNV_SEVERE_MILD_SENSITIVITY[0];
			else
				return CNV_SEVERE_MILD_SENSITIVITY[1];				
		}
		else if (eye1.contains(EyeState.CSME) || eye2.contains(EyeState.CSME) || eye1.contains(EyeState.HR_PDR) || eye2.contains(EyeState.HR_PDR)) {
			return EMD_SEVERE_MILD_SENSITIVITY[0];
		}
		else if (eye1.contains(EyeState.NON_HR_PDR) || eye2.contains(EyeState.NON_HR_PDR)) {
			return EMD_SEVERE_MILD_SENSITIVITY[1];
		}
		// We assume that any other state is not detected
		return 0.0;
	}
	
	public double getSpecificity(Patient pat) {
		return SPECIFICITY;		
	}
	
	public boolean isAttending(Patient pat) {
		return (pat.draw(RandomForPatient.ITEM.ATTENDING_SCREENING) < P_ATTENDING[pat.isDiabetic() ? 1 : 0]);
	}
}
