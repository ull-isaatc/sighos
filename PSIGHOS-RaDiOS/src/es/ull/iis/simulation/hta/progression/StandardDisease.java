/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.effectiveness.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A basic disease where progression is driven by @link {@link ManifestationPathway manifestation pathways}
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class StandardDisease extends Disease {
	
	/**
	 * Creates a new standard disease
	 * @param secParams Repository for common parameters
	 * @param name Name of the disease
	 * @param description Description of the disease
	 */
	public StandardDisease(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
	}

	@Override
	public DiseaseProgression getProgression(Patient pat) {
		final DiseaseProgression prog = new DiseaseProgression();
		long limit = pat.getTimeToDeath();
		final TreeSet<Manifestation> state = pat.getState();  
		for (final Manifestation destManif : secParams.getRegisteredManifestations()) {
			if (!state.contains(destManif)) {
				long prevTime = pat.getTimeToNextManifestation(destManif);
				long newTime = destManif.getTimeTo(pat, limit);
				// TODO: This condition requires further thinking. This condition works as long as we assume that the state of the patient can only get worse during the simulation
				// OLD COMMENT: We are working with competitive risks. Hence, if the new time to event is lower than the previously scheduled, we rescheduled
				if (newTime < prevTime) {
					// If there was a former pending event
					if (prevTime != Long.MAX_VALUE)
						prog.addCancelEvent(destManif);
					prog.addNewEvent(destManif, newTime);
				}
			}
		}
		return prog;
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		double cost = 0.0;
		for (final Manifestation manif : pat.getState()) {
			cost +=  discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, manif, pat.getSimulation()), initT, endT);
		}
		if (pat.isDiagnosed())
			cost += getTreatmentAndFollowUpCosts(pat, initT, endT, discountRate);
		return cost;
	}

	@Override
	public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
		final int simulId = pat.getSimulation().getIdentifier();
		final TreeSet<Manifestation> state = pat.getState();
		// Uses the base disutility for the disease if available 
		double du = secParams.getDisutilityForDisease(this, simulId);
		for (final Manifestation manif : state) {
			du = method.combine(du, secParams.getDisutilitiesForManifestation(manif, simulId)[0]);
		}
		return du;
	}

}
