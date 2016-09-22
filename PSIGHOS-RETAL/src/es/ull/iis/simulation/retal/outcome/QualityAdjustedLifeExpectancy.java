/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class QualityAdjustedLifeExpectancy extends Outcome {

	/**
	 * @param simul
	 */
	public QualityAdjustedLifeExpectancy(RETALSimulation simul, double discountRate) {
		super(simul, "Quality Adjusted Life Expectancy", "QALY", discountRate);
	}

}
