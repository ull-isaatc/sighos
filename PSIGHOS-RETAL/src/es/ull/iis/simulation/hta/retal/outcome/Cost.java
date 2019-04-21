/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Cost extends Outcome {
	
	public Cost(int nInterventions, HTASimulation simul, double discountRate) {
		super(nInterventions, simul, "Cost", "€", discountRate);
	}

}
