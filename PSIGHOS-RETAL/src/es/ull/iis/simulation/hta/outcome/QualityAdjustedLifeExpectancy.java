/**
 * 
 */
package es.ull.iis.simulation.hta.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iván Castilla Rodríguez
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
