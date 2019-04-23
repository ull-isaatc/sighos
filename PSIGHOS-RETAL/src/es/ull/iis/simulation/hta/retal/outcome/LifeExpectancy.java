/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import es.ull.iis.simulation.hta.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LifeExpectancy extends Outcome {

	/**
	 * @param simul
	 * @param description
	 * @param unit
	 * @param discountRate
	 */
	public LifeExpectancy(int nInterventions, RETALSimulation simul, double discountRate) {
		super(nInterventions, simul, "Life Expectancy", "LY", discountRate);
	}

}
