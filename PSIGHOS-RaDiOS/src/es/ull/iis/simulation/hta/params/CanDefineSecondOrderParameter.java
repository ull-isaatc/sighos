/**
 * 
 */
package es.ull.iis.simulation.hta.params;

/**
 * Classes implementing this interface define certain second order parameter. Which parameter? It is specified in interfaces extending this one 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface CanDefineSecondOrderParameter {
	public SecondOrderParamsRepository getRepository();
}
