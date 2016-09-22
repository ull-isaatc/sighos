/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
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
