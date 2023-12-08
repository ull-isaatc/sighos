/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 * TODO: remove and change all the related classes to use (or be) directly a Parameter
 */
public interface ParameterCalculator {
	/**
	 * Calculates and returns the value of a parameter for a patient at a specific simulation timestamp
	 * @param pat A patient
	 * @return the value of a parameter for a patient at a specific simulation timestamp
	 */
	public double getValue(Patient pat);
}
