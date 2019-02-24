/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class LifeExpectancy extends Outcome {

	/**
	 * @param simul
	 * @param description
	 * @param unit
	 * @param discountRate
	 */
	public LifeExpectancy(HTASimulation simul, double discountRate) {
		super(simul, "Life Expectancy", "LY", discountRate);
	}

}
