/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla
 *
 */
public class ScreeningParam extends Param {

	final private static double SENSITIVITY = 0.83;
	final private static double SPECIFICITY = 0.91;
	final private static double[] SEVERE_MILD_SENSITIVITY = {0.9, 0.42};
	final private static double[] SEVERE_MILD_SPECIFICITY = {0.91, 0.91};
	final private static double EMD_SENSITIVITY = 0.90;
	final private static double EMD_SPECIFICITY = 0.89;
	final private static double[] EMD_SEVERE_MILD_SENSITIVITY = {0.97, 0.58};
	final private static double[] EMD_SEVERE_MILD_SPECIFICITY = {0.89, 0.89};
	final private static double CNV_SENSITIVITY = 0.83;
	final private static double CNV_SPECIFICITY = 0.91;
	final private static double[] CNV_SEVERE_MILD_SENSITIVITY = {0.96, 0.36};
	final private static double[] CNV_SEVERE_MILD_SPECIFICITY = {0.91, 0.91};
	
	/**
	 * @param simul
	 * @param baseCase
	 */
	public ScreeningParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
	}
	
	public double getSensitivity(Patient pat) {
		return 1.0;
	}
	
	public double getSpecificity(Patient pat) {
		return 1.0;		
	}
}
