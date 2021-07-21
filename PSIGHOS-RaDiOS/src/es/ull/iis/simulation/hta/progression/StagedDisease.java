/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A disease where the progression is defined by means of strict rules (described with {@link Transition transitions})
 * @author Iv�n Castilla
 *
 */
public abstract class StagedDisease extends Disease {

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public StagedDisease(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.progression.Disease#getProgression(es.ull.iis.simulation.hta.Patient)
	 */
	/**
	 * First checks the patient's current state. For each chronic manifestation in the state, recomputed times to new manifestations. 
	 */
	@Override
	public DiseaseProgression getProgression(Patient pat) {
		final DiseaseProgression prog = new DiseaseProgression();
		final TreeMap<Manifestation, Long> times2events = new TreeMap<>();
		final TreeMap<Manifestation, Long> previousTimes2events = new TreeMap<>();
		long limit = pat.getTimeToDeath();
		// Goes through the current manifestations and their potential transitions
		for (final Manifestation manif : pat.getDetailedState()) {
			for (final Transition trans : getTransitions(manif)) {
				final Manifestation destManif = trans.getDestManifestation();
				if (Manifestation.Type.ACUTE.equals(destManif.getType()) || !pat.getDetailedState().contains(destManif)) { 
					// If no previous transition to this new manifestation has been included
					if (!times2events.containsKey(destManif)) {
						final long previousTime = pat.getTimeToManifestation(destManif);
						previousTimes2events.put(destManif, previousTime);
						times2events.put(destManif, Long.MAX_VALUE);
					}
					// Anyway...
					long newTime2Event = trans.getTimeToEvent(pat, limit);
					if (newTime2Event != Long.MAX_VALUE) {
						// Always uses the lowest time to event (competitive risks)
						if (newTime2Event < times2events.get(destManif)) {
							times2events.put(destManif, newTime2Event);
						}
					}
				}
			}
		}
		// Now schedules and/or cancels events for each manifestation
		for (final Manifestation destManif : times2events.keySet()) {
			// If there is a new time for the event
			if (times2events.get(destManif) != Long.MAX_VALUE) {
				// TODO: Me entran muchas dudas con respecto a sustituir SIEMPRE el evento. En la versi�n anterior, asum�a que cualquier
				// efecto era contraproducente y solo sustitu�a si era menor, pero ahora debo ser menos restrictivo. �TIENE ESTO EFECTOS COLATERALES? 
				if (previousTimes2events.get(destManif) != Long.MAX_VALUE)
					prog.addCancelEvent(destManif);
				prog.addNewEvent(destManif, times2events.get(destManif));
				// FIXME: Esto habr�a que sacarlo fuera y recorrer la lista de nuevos eventos para 1) No planificar eventos de manifestaciones
				// excluyentes y 2) cancelar los eventos todav�a no ocurridos de manifestaciones excluyentes
				// Removes the events for excluded manifestations that are scheduled to happen later on than the new manifestation 
				for (Manifestation excluded : getExcluded(destManif)) {
					final long timeToExcluded = pat.getTimeToManifestation(excluded);
					if ((timeToExcluded != Long.MAX_VALUE) && (times2events.get(destManif) <= timeToExcluded)) {
						prog.addCancelEvent(excluded);
					}
				}
			}
		}
		return prog;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.progression.Disease#getAnnualCostWithinPeriod(es.ull.iis.simulation.hta.Patient, double, double)
	 */
	@Override
	public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
		double cost = 0.0;
		for (final Manifestation manif : pat.getDetailedState()) {
			cost += secParams.getCostsForManifestation(manif, pat.getSimulation().getIdentifier())[0];
		}
		if (pat.isDiagnosed())
			cost += getAnnualTreatmentAndFollowUpCosts(pat, initAge, endAge);
		return cost;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.progression.Disease#getDisutility(es.ull.iis.simulation.hta.Patient, es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod)
	 */
	@Override
	public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
		final int simulId = pat.getSimulation().getIdentifier();
		final Collection<Manifestation> state = pat.getDetailedState();
		// Uses the base disutility for the disease if available 
		double du = secParams.getDisutilityForDisease(this, simulId);
		for (final Manifestation manif : state) {
			du = method.combine(du, secParams.getDisutilityForManifestation(manif, simulId));
		}
		return du;
	}

}
