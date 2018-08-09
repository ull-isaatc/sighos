/**
 * 
 */
package es.ull.iis.simulation.hta.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Cost extends Outcome {
	
	public Cost(HTASimulation simul, double discountRate) {
		super(simul, "Cost", "€", discountRate);
	}

}
