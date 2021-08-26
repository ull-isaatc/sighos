/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
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
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		final Collection<Manifestation> state = pat.getState();
		for (Manifestation st : state) {
			cost += secParams.getCostsForManifestation(st, pat.getSimulation().getIdentifier())[0];
		}
		return cost;
	}
	
	@Override
	public double getCostForAcuteManifestation(Patient pat, Manifestation manif) {
		return secParams.getCostForManifestation(manif, pat.getSimulation().getIdentifier());
	}

	@Override
	public double getCostOfManifestation(Patient pat, Manifestation newEvent) {
		return secParams.getCostsForManifestation(newEvent, pat.getSimulation().getIdentifier())[1];
	}

	@Override
	public double getAnnualInterventionCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return pat.getIntervention().getAnnualCost(pat);
	}

	@Override
	public double getCostForIntervention(Patient pat) {
		return pat.getIntervention().getStartingCost(pat);
	}
	
	@Override
	public TreeMap<Manifestation, Double> getAnnualManifestationCostWithinPeriod(Patient pat, double initAge, double endAge) {
		final TreeMap<Manifestation, Double> results = new TreeMap<>(); 
		final Collection<Manifestation> state = pat.getState();
		for (Manifestation st : state) {
			results.put(st, secParams.getCostsForManifestation(st, pat.getSimulation().getIdentifier())[0]);
		}
		return results;
	}

	@Override
	public double getAnnualDiseaseCostWithinPeriod(Patient pat, double initAge, double endAge) {
		double cost = 0.0;
		final Collection<Manifestation> state = pat.getState();
		for (Manifestation st : state) {
			cost += secParams.getCostsForManifestation(st, pat.getSimulation().getIdentifier())[0];
		}
		return cost;
	}
	
	@Override
	public double getStdManagementCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return 0.0;
		// TODO: Chequear si hay costes de la enfermedad sola y cómo los metemos
	}

}
