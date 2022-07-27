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
 * A standard cost calculator that assigns a constant cost to each complication, and then computes the final cost by 
 * aggregating all the costs for the different complications suffered by the patient.
 * Acute events and no complication costs are defined in the constructor. The cost for every stage of each chronic complication
 * is defined by using {@link #addCostForComplicationStage(Manifestation, double[])}
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StdCostCalculator implements CostCalculator {
	private final SecondOrderParamsRepository secParams;

	/**
	 * Creates an instance of the model parameters related to costs.
	 * @param costNoComplication Cost of diabetes with no complications 
	 * @param costAcuteEvent Cost of acute events
	 */
	public StdCostCalculator(SecondOrderParamsRepository secParams) {
		this.secParams = secParams;
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		final Collection<Manifestation> state = pat.getState();
		for (Manifestation manifestation : state) {
			cost += discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, manifestation, pat.getSimulation()), initT, endT);
		}
		return cost;
	}

	@Override
	public double getCostUponIncidence(Patient pat, Manifestation newEvent, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(CostParamDescriptions.ONE_TIME_COST.getValue(secParams, newEvent, pat.getSimulation()), time);
	}

	@Override
	public double getInterventionCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO: Review whether the discount should be applied here, or maybe change the method in intervention
		return pat.getIntervention().getAnnualCost(pat);
	}

	@Override
	public double getCostForIntervention(Patient pat, Discount discountRate) {
		// TODO: Review whether the discount should be applied here, or maybe change the method in intervention
		return pat.getIntervention().getStartingCost(pat);
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
		double cost = 0.0;
		final Collection<Manifestation> state = pat.getState();
		for (Manifestation manifestation : state) {
			cost += discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, manifestation, pat.getSimulation()), initT, endT);
		}
		return cost;
	}
	
	@Override
	public double getStdManagementCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0.0;
		// TODO: Chequear si hay costes de la enfermedad sola y cómo los metemos
	}

}
