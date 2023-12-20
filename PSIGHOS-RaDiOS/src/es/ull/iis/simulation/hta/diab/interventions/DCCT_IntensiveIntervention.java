/**
 * 
 */
package es.ull.iis.simulation.hta.diab.interventions;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.T1DMModel;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.SecondOrderNatureParameter;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.params.modifiers.DiffParameterModifier;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class DCCT_IntensiveIntervention extends Intervention {
	private static final String NAME = "INT";
	private static final double HBA1C_REDUCTION = 1.5;
	private static final double HBA1C_REDUCTION_SD = 1.1;
	
	/**
	 * @param model
	 */
	public DCCT_IntensiveIntervention(HTAModel model) {
		super(model, NAME, NAME);
	}

	@Override
	public void createParameters() {
		
		final Parameter modifier = new SecondOrderNatureParameter(model, SecondOrderParamsRepository.getModificationString(this, T1DMModel.STR_HBA1C + "_REDUX"), 
				T1DMModel.STR_HBA1C + " reduction", "DCCT Intensive", 2013, ParameterType.OTHER, HBA1C_REDUCTION, RandomVariateFactory.getInstance("NormalVariate", HBA1C_REDUCTION, HBA1C_REDUCTION_SD)); 
		model.addParameter(modifier);
		model.addParameterModifier(ParameterType.ATTRIBUTE.getParameter(T1DMModel.STR_HBA1C).name(), this, new DiffParameterModifier(modifier.name()));
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0.0;
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0.0;
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return discountRate.applyAnnualDiscount(0.0, initT, endT);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
			Discount discountRate) {
		return discountRate.applyAnnualDiscount(0.0, initT, endT);
	}

}
