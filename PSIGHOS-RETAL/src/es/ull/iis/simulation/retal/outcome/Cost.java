/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class Cost extends Outcome {
	
	public Cost(RETALSimulation simul, double discountRate) {
		super(simul, "Cost", "�", discountRate);
	}

}
