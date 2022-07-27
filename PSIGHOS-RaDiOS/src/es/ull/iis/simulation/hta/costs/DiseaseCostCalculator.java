/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * A class to compute the cost related to a disease that makes use of the methods defined in the disease 
 * @author Iván Castilla Rodríguez
 *
 */
public class DiseaseCostCalculator implements CostCalculator {
	private final SecondOrderParamsRepository secParams;

	/**
	 * 
	 */
	public DiseaseCostCalculator(SecondOrderParamsRepository secParams) {
		this.secParams = secParams;
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		double cost = pat.getIntervention().getCostWithinPeriod(pat, initT, endT, discountRate);
		return cost + pat.getDisease().getCostWithinPeriod(pat, initT, endT, discountRate);
	}

	@Override
	public double getCostUponIncidence(Patient pat, Manifestation newEvent, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(CostParamDescriptions.ONE_TIME_COST.getValue(secParams, newEvent, pat.getSimulation()), time);
	}

	@Override
	public double getInterventionCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return pat.getIntervention().getCostWithinPeriod(pat, initT, endT, discountRate);
	}

	@Override
	public double getCostForIntervention(Patient pat, double time, Discount discountRate) {
		// TODO: Review whether the discount should be applied here, or maybe change the method in intervention
		return pat.getIntervention().getStartingCost(pat, time, discountRate);
	}

	@Override
	public TreeMap<Manifestation, Double> getManifestationCostWithinPeriod(Patient pat, double initT, double endT,
			Discount discountRate) {
		final TreeMap<Manifestation, Double> results = new TreeMap<>(); 
		final Collection<Manifestation> state = pat.getState();
		for (Manifestation manifestation : state) {
			results.put(manifestation, discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, manifestation, pat.getSimulation()), initT, endT));
		}
		return results;
	}

	@Override
	public double getDiseaseCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return pat.getDisease().getCostWithinPeriod(pat, initT, endT, discountRate);
	}

	@Override
	public double getStdManagementCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

}
