/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.costs.DiagnosisStrategy;
import es.ull.iis.simulation.hta.costs.Strategy;
import es.ull.iis.simulation.hta.interventions.DiagnosisIntervention;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author masbe
 *
 */
public class StrategyBasedDisease extends StandardDisease {
	private Strategy diagnosisStrategy = null;
	private Strategy screeningStrategy = null;
	private Strategy treatmentStrategy = null;
	private Strategy followUpStrategy = null;
	private Strategy lineOfTherapy = null;
	
	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public StrategyBasedDisease(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
	}

	@Override
	public void registerSecondOrderParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
			Discount discountRate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDiagnosisCost(Patient pat, double time, Discount discountRate) {
		final Intervention interv = pat.getIntervention();
		// If the intervention is a diagnosis itself, the cost is computed within the intervention and not from the disease 
		if (interv instanceof DiagnosisIntervention)
			return 0.0;
	}

}
