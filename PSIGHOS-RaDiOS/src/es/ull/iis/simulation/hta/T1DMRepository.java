/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseCostCalculator;
import es.ull.iis.simulation.hta.outcomes.DiseaseUtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMRepository extends SecondOrderParamsRepository {
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	public static double DEF_U_GENERAL_POP = 0.911400915;
	private final CostCalculator costCalc;
	private final UtilityCalculator utilCalc;

	protected T1DMRepository(int nRuns, int nPatients) {
		super(nRuns, nPatients);
		costCalc = new DiseaseCostCalculator(this);
		utilCalc = new DiseaseUtilityCalculator(this, DisutilityCombinationMethod.MAX, 0.911400915);
	}

	@Override
	public CostCalculator getCostCalculator() {
		return costCalc;
	}

	@Override
	public UtilityCalculator getUtilityCalculator() {
		return utilCalc;
	}

}
