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
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		double cost = pat.getIntervention().getAnnualCost(pat);
		return cost + pat.getDisease().getAnnualCostWithinPeriod(pat, initAge, endAge);
	}

	@Override
	public double getCostOfComplication(Patient pat, Manifestation manif) {
		return secParams.getCostsForManifestation(manif, pat.getSimulation().getIdentifier())[1];
	}

	@Override
	public double getCostForAcuteEvent(Patient pat, Manifestation manif) {
		return secParams.getCostForManifestation(manif, pat.getSimulation().getIdentifier());
	}

	@Override
	public double getAnnualInterventionCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return pat.getIntervention().getAnnualCost(pat);
	}

	@Override
	public TreeMap<Manifestation, Double> getAnnualManifestationCostWithinPeriod(Patient pat, double initAge,
			double endAge) {
		final TreeMap<Manifestation, Double> results = new TreeMap<>(); 
		final Collection<Manifestation> state = pat.getDetailedState();
		for (Manifestation st : state) {
			results.put(st, secParams.getCostsForManifestation(st, pat.getSimulation().getIdentifier())[0]);
		}
		return results;
	}

	@Override
	public double getAnnualDiseaseCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return pat.getDisease().getAnnualCostWithinPeriod(pat, initAge, endAge);
	}

	@Override
	public double getStdManagementCostWithinPeriod(Patient pat, double initAge, double endAge) {
		return 0;
	}

}
