/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.CanDefineSecondOrderParameter;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public interface PartOfStrategy extends Describable, Named, CreatesSecondOrderParameters, CanDefineSecondOrderParameter {
	public static final String[] STR_UNIT_COST = {SecondOrderParamsRepository.STR_COST_PREFIX + "UNIT_", "Unit cost for "};  
	/**
	 * Returns a string to identify/describe the unit cost parameter associated to this part of strategy
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the unit cost parameter associated to this part of strategy
	 */
	public default String getUnitCostParameterString(boolean longText) {
		return longText ? (STR_UNIT_COST[1] + getDescription()) : (STR_UNIT_COST[0] + name());
	}
	
	double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate);
	double[] getAnnualizedCostForPeriod(Patient pat, double startT, double endT, Discount discountRate);
}
