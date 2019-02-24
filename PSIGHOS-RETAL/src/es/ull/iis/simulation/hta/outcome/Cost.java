/**
 * 
 */
package es.ull.iis.simulation.hta.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class Cost extends Outcome {
	
	public Cost(HTASimulation simul, double discountRate) {
		super(simul, "Cost", "�", discountRate);
	}

}
