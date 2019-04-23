/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import es.ull.iis.simulation.hta.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Cost extends Outcome {
	
	public Cost(int nInterventions, RETALSimulation simul, double discountRate) {
		super(nInterventions, simul, "Cost", "€", discountRate);
	}

}
