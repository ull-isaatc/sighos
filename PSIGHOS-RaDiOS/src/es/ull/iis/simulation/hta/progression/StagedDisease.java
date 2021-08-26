/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation.Type;

/**
 * A disease where the progression is defined by means of strict rules (described with {@link Transition transitions})
 * @author Iván Castilla
 *
 */
public abstract class StagedDisease extends Disease {
	/** Manifestations and their associated transitions for this disease */
	private final TreeMap<Manifestation, ArrayList<Transition>> transitions;
	/** Manifestations and their associated REVERSE transitions for this disease */
	private final TreeMap<Manifestation, ArrayList<Transition>> reverseTransitions;
	/** Default none manifestation, i.e., the patient would have the disease but he/she would be asymptomatic */
	private final Manifestation asymptomatic;

	/**
	 * @param secParams
	 * @param name
	 * @param description
	 */
	public StagedDisease(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
		this.asymptomatic = new Manifestation(secParams, "NONE", "No manifestations", this, Type.CHRONIC) {
			@Override
			public void registerSecondOrderParameters() {			
			}

			@Override
			public List<Double> getRandomValues(Patient pat, int n) {
				List<Double> list = new ArrayList<>();
				for (int i = 0; i < n; i++)
					list.add(0.0);
				return list;
			}

			@Override
			public double getRandomValue(Patient pat) {
				return 0;
			}
		};
		this.transitions = new TreeMap<>();
		this.transitions.put(asymptomatic, new ArrayList<>());
		this.reverseTransitions = new TreeMap<>();
		this.exclusions.put(asymptomatic, new TreeSet<>());
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.progression.Disease#getProgression(es.ull.iis.simulation.hta.Patient)
	 */
	/**
	 * First checks the patient's current state. For each chronic manifestation in the state, recomputes times to new manifestations. 
	 */
	@Override
	public DiseaseProgression getProgression(Patient pat) {
		final DiseaseProgression prog = new DiseaseProgression();
		final TreeMap<Manifestation, Long> times2events = new TreeMap<>();
		final TreeMap<Manifestation, Long> previousTimes2events = new TreeMap<>();
		long limit = pat.getTimeToDeath();
		// Goes through the current manifestations and their potential transitions
		for (final Manifestation manif : pat.getState()) {
			for (final Transition trans : getTransitions(manif)) {
				final Manifestation destManif = trans.getDestManifestation();
				if (Manifestation.Type.ACUTE.equals(destManif.getType()) || (!pat.getState().contains(destManif) && !pat.mustBeExcluded(destManif))) { 
					// If no previous transition to this new manifestation has been included
					if (!times2events.containsKey(destManif)) {
						final long previousTime = pat.getTimeToNextManifestation(destManif);
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
				// TODO: Me entran muchas dudas con respecto a sustituir SIEMPRE el evento. En la versión anterior, asumía que cualquier
				// efecto era contraproducente y solo sustituía si era menor, pero ahora debo ser menos restrictivo. ¿TIENE ESTO EFECTOS COLATERALES? 
//				if (previousTimes2events.get(destManif) > times2events.get(destManif)) {
				if (previousTimes2events.get(destManif) != Long.MAX_VALUE)
					prog.addCancelEvent(destManif);
				prog.addNewEvent(destManif, times2events.get(destManif));
//				}
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
		for (final Manifestation manif : pat.getState()) {
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
		final Collection<Manifestation> state = pat.getState();
		// Uses the base disutility for the disease if available 
		double du = secParams.getDisutilityForDisease(this, simulId);
		for (final Manifestation manif : state) {
			du = method.combine(du, secParams.getDisutilityForManifestation(manif, simulId));
		}
		return du;
	}

	@Override
	public void reset(int id) {
		super.reset(id);
		for (final Transition trans : transitions.get(asymptomatic))
			trans.reset(id);
		for (final Manifestation manif : manifestations) {
			for (final Transition trans : transitions.get(manif))
				trans.reset(id);
		}
	}
	
	/**
	 * Returns a "manifestation" that represents the absence of chronic manifestations of the disease (not necessarily excludes
	 * acute manifestations)
	 * @return An asymptomatic manifestation, i.e., absence of chronic manifestations
	 */
	public Manifestation getAsymptomaticManifestation() {
		return asymptomatic;
	}
	
	@Override
	public Manifestation addManifestation(Manifestation manif) {
		transitions.put(manif, new ArrayList<>());
		reverseTransitions.put(manif, new ArrayList<>());
		TreeSet<Manifestation> excManif = new TreeSet<>();
		if (Manifestation.Type.CHRONIC.equals(manif.getType())) {
			excManif.add(asymptomatic);
		}
		exclusions.put(manif, excManif);
		return super.addManifestation(manif);
	}
	
	/**
	 * Adds a new transition between two manifestations of this disease (or from "no manifestations" to any other manifestation)
	 * @param trans New transition between manifestations of this disease
	 * @return The transition added 
	 */
	public Transition addTransition(Transition trans) {
		transitions.get(trans.getSrcManifestation()).add(trans);
		reverseTransitions.get(trans.getDestManifestation()).add(trans);
		return trans;
	}

	/**
	 * Returns the potential transitions from a manifestation
	 * @param manif Source manifestation
	 * @return the potential transitions from a manifestation
	 */
	public ArrayList<Transition> getTransitions(Manifestation manif) {
		return transitions.get(manif);
	}


	/**
	 * Returns all the transitions defined within the disease
	 * @return the transitions for this disease
	 */
	public Transition[] getTransitions() {
		final ArrayList<Transition> trans = new ArrayList<>();
		for (final ArrayList<Transition> tt : transitions.values())
			trans.addAll(tt);
		final Transition[] array = new Transition[trans.size()];
		return (Transition[]) trans.toArray(array);
	}

	/**
	 * Returns the potential transitions to a manifestation
	 * @param manif Destination manifestation
	 * @return the potential transitions to a manifestation
	 */
	public ArrayList<Transition> getReverseTransitions(Manifestation manif) {
		return reverseTransitions.get(manif);
	}
	
	/**
	 * Returns the number of different transitions defined from one manifestation to another
	 * @return the number of different transitions defined from one manifestation to another
	 */
	public int getNTransitions() {
		return transitions.size();
	}
}
