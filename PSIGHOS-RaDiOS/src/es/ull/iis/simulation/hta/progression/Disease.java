/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.GenerateSecondOrderInstances;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;

/**
 * A disease defines the progression of a patient. Includes several manifestations and defines how such manifestations are related to each other. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Disease implements Named, Describable, GenerateSecondOrderInstances {
	/** Absence of progression */
	private static final DiseaseProgression NULL_PROGRESSION = new DiseaseProgression(); 
	/** Absence of manifestations */
	private static final Manifestation[] NON_MANIFESTATIONS = new Manifestation[0]; 
	/** A Disease that represents a non-disease state, i.e., being healthy. Useful to avoid null comparisons. */
	public static final Disease HEALTHY = new Disease("HEALTHY", "Healthy") {

		@Override
		public DiseaseProgression getProgression(Patient pat) {
			return NULL_PROGRESSION;
		}

		@Override
		public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
			return 0;
		}

		@Override
		public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
			return 0;
		}

		@Override
		public Manifestation[] getManifestations() {
			return NON_MANIFESTATIONS;
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		}
	};
	
	/** Manifestations related to this disease */
	private final TreeSet<Manifestation> manifestations;
	/** Transitions among manifestations of this disease */
	private final TreeSet<Transition> transitions;
	/** Short name of the disease */
	private final String name;
	/** Full description of the disease */
	private final String description;
	
	/**
	 * Creates a submodel for a disease.
	 */
	public Disease(String name, String description) {
		this.manifestations = new TreeSet<>();
		this.transitions = new TreeSet<>();
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Returns the description of the disease
	 * @return the description of the disease
	 */
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public void generate(SecondOrderParamsRepository secParams) {
		for (final Manifestation manif : manifestations)
			manif.generate(secParams);
		for (final Transition trans : transitions)
			trans.generate(secParams);
	}
	
	/**
	 * Adds a manifestation to this disease
	 * @param manif New manifestation associated to this disease
	 */
	public void addManifestation(Manifestation manif) {
		manifestations.add(manif);
	}
	
	/**
	 * Adds a new transition between two manifestations of this disease (or from "no manifestations" to any other manifestation)
	 * @param trans New transition between manifestations of this disease 
	 */
	public void addTransition(Transition trans) {
		transitions.add(trans);
	}
	
	/**
	 * Returns the time to event for a patient
	 * @param pat A patient
	 * @param id The identifier of the transition. Transitions are expected to be ordered elsewhere.
	 * @param limit The upper limit for the occurrence of the event. If the computed time to event is higher or equal 
	 * than the limit, returns Long.MAX_VALUE
	 * @return The time to event for the patient; Long.MAX_VALUE if the event will never happen.
	 */
	public long getTimeToEvent(Patient pat, Transition trans, long limit) {		
		return trans.getTimeToEvent(pat, limit);
	}

	
	/**
	 * Returns how this patient will progress from its current state with regards to this
	 * chronic complication. The progress can include removal of events already scheduled, modification of 
	 * previously scheduled events and new events.
	 * @param pat A patient
	 * @return how this patient will progress from its current state with regards to this
	 * chronic complication
	 */
	public abstract DiseaseProgression getProgression(Patient pat);
	
	/**
	 * Adds progression actions in case they are needed. First checks if the new time to event is valid. Then checks
	 * if there was a previously scheduled event and adds a "cancel" action. Finally, adds a "new" action for the new time.
	 * @param prog Current progression of the patient
	 * @param stage Chronic complication stage
	 * @param timeToEvent New time to event
	 * @param previousTimeToEvent Previous time to event
	 */
	public void adjustProgression(DiseaseProgression prog, Manifestation stage, long timeToEvent, long previousTimeToEvent) {
		// Check previously scheduled events
		if (timeToEvent != Long.MAX_VALUE) {
			if (previousTimeToEvent < Long.MAX_VALUE) {
				prog.addCancelEvent(stage);
			}
			prog.addNewEvent(stage, timeToEvent);
		}
	}
	
	/**
	 * Returns the initial set of stages (one or more) that the patient will start with when this complication appears. 
	 * @param pat A patient
	 * @return the initial set of stages that the patient will start with when this complication appears
	 */
	public TreeSet<Manifestation> getInitialStage(Patient pat) {
		final TreeSet<Manifestation> init = new TreeSet<>();
		for (final Manifestation manif : manifestations) {
			if (manif.hasManifestationAtStart(pat))
				init.add(manif);
		}
		return init;
		
	}
	
	/**
	 * Returns the annual cost associated to the current state of the patient and during the defined period
	 * @param pat A patient
	 * @param initAge Starting time of the period (in years)
	 * @param endAge Ending time of the period
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public abstract double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge);
	
	/**
	 * Returns the disutility value associated to the current stage of this chronic complication
	 * @param pat A patient
	 * @param method Method used to compute the disutility of this chronic complication in case the 
	 * complication allows for several stages to be concurrently active
	 * @return The disutility value associated to the current stage of this chronic complication
	 */
	public abstract double getDisutility(Patient pat, DisutilityCombinationMethod method);
	
	/** 
	 * Returns the number of stages used to model this complication
	 * @return the number of stages used to model this complication
	 */
	public int getNManifestations() {
		return manifestations.size();
	}
	
	/**
	 * Returns the stages used to model this chronic complication
	 * @return An array containing the stages used to model this chronic complication 
	 */
	public Manifestation[] getManifestations() {
		return (Manifestation[]) manifestations.toArray();
	}

	/**
	 * Returns the number of different transitions defined from one manifestation to another
	 * @return the number of different transitions defined from one manifestation to another
	 */
	public int getNTransitions() {
		return transitions.size();
	}
	
	/**
	 * Adds the parameters corresponding to the second order uncertainty on the initial proportions for each stage of
	 * the complication
	 * @param secParams Second order parameters repository
	 */
	public void addSecondOrderInitProportion(SecondOrderParamsRepository secParams) {
		for (final Manifestation manif : getManifestations()) {
			if (BasicConfigParams.INIT_PROP.containsKey(manif.name())) {
				secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getInitProbString(manif), "Initial proportion of " + manif.name(), "",
						BasicConfigParams.INIT_PROP.get(manif.name())));
			}			
		}
		
	}
	
	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);
	
}
