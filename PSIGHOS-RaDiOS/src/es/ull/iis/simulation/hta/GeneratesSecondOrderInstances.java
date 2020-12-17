/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A class that generates instances (e.g. numbers following a random distribution) to be used in second order simulation 
 * @author Iván Castilla
 *
 */
public interface GeneratesSecondOrderInstances {
	/**
	 * Generates instances to be used in second order simulations.
	 * This method must be invoked before launching the simulation and after all the parameters have been registered in a {@link SecondOrderParamsRepository repository}
	 */
	void generate();
}
