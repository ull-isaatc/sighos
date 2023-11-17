/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * An object that defines second order parameters and adds them to a repository.
 * @author Iván Castilla Rodríguez
 *
 */
public interface CreatesSecondOrderParameters {
	/**
	 * This method should register {@link Parameter second order parameters} into a {@link SecondOrderParamsRepository repository}
	 * @param secParams Repository
	 */
	void registerSecondOrderParameters(SecondOrderParamsRepository secParams);
	/**
	 * Returns the related repository for parameters
	 * @return the related repository for parameters
	 */
	public SecondOrderParamsRepository getRepository();
}
