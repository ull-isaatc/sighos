/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import es.ull.iis.simulation.hta.HTASimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class Cost extends Outcome {
	
	public Cost(int nInterventions, HTASimulation simul, double discountRate) {
		super(nInterventions, simul, "Cost", "�", discountRate);
	}

}
