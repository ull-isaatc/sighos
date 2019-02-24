/**
 * 
 */
package es.ull.iis.simulation.hta.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class QualityAdjustedLifeExpectancy extends Outcome {

	/**
	 * @param simul
	 */
	public QualityAdjustedLifeExpectancy(HTASimulation simul, double discountRate) {
		super(simul, "Quality Adjusted Life Expectancy", "QALY", discountRate);
	}

}
