/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public interface ParameterCalculator {
	public double getValue(Patient pat);
}
