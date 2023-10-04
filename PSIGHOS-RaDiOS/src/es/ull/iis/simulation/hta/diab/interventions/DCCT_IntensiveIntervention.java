/**
 * 
 */
package es.ull.iis.simulation.hta.diab.interventions;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.diab.T1DMRepository;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.Modification.Type;
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
	 * @param secParams
	 */
	public DCCT_IntensiveIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, NAME, NAME);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		addAttributeModification(T1DMRepository.STR_HBA1C, new Modification(secParams, Type.DIFF, SecondOrderParamsRepository.getModificationString(this, T1DMRepository.STR_HBA1C + "_REDUX"), T1DMRepository.STR_HBA1C + " reduction",
				"DCCT Intensive", HBA1C_REDUCTION, RandomVariateFactory.getInstance("NormalVariate", HBA1C_REDUCTION, HBA1C_REDUCTION_SD)));
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
