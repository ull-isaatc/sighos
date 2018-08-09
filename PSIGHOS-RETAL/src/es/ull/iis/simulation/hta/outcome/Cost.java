/**
 * 
 */
package es.ull.iis.simulation.retal.outcome;

import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Cost extends Outcome {
	
	public Cost(RETALSimulation simul, double discountRate) {
		super(simul, "Cost", "€", discountRate);
	}

}
