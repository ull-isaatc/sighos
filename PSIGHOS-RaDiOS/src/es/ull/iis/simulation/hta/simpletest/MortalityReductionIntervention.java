/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MortalityReductionIntervention extends Intervention {
	private final static double ANNUAL_COST = 200.0; 

	/**
	 * @param secParams
	 */
	public MortalityReductionIntervention(SecondOrderParamsRepository secParams, Modification.Type type) {
		super(secParams, "MORT_RED", "Intervention that reduces mortality");
		double value = 0.0;
		switch(type) {
		case DIFF:
			// We increase 5 years the life expectancy
			value = -5.0;
			break;
		case RR:
			// We increase lifetime by a factor of 1.2
			value = 1.2;
			break;
		case SET:
			// We set lifetime to 100 years
			value = 90.0;
			break;
		default:
			break;		
		}
		setLifeExpectancyModification(new Modification(secParams, type, "MOD_MORT1", "Reduction of mortality due to intervention", "", value));
	}

	@Override
	public void registerSecondOrderParameters() {

	}

	@Override
	public double getAnnualCost(Patient pat) {
		return ANNUAL_COST;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return 0;
	}

}
