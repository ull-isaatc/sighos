/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.CanDefineSecondOrderParameter;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public interface PartOfStrategy extends Describable, Named, CreatesSecondOrderParameters, CanDefineSecondOrderParameter {
	double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate);
	double[] getAnnualizedCostForPeriod(Patient pat, double startT, double endT, Discount discountRate);
}
