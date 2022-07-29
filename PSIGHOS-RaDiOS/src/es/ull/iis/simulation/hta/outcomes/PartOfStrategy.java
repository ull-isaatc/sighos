/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;

/**
 * @author Iván Castilla
 *
 */
public interface PartOfStrategy extends NamedAndDescribed, CreatesSecondOrderParameters {
	double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate);
	double[] getAnnualizedCostForPeriod(Patient pat, double startT, double endT, Discount discountRate);
}
