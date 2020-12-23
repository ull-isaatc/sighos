/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface RandomSeedForPatients {
	double draw(Patient pat);
	void reset();
}
