/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public interface PartOfStrategy extends Describable, Named {
	double getUnitCost(Patient pat);
}
