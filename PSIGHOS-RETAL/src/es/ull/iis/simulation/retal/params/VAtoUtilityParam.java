/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * Based on Sheffield utilities
 * Source: Karnon page 52, table 58
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class VAtoUtilityParam extends Param {
	private static final double AGE_COEF = -0.00792;
	private static final double LOGMAR_COEF = -0.10872;
	private static final double CONSTANT_COEF = 1.078315;
	
	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAtoUtilityParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
	}

	public double getUtility(double age, double va) {
		return CONSTANT_COEF + AGE_COEF * age + LOGMAR_COEF * va;
	}
}
