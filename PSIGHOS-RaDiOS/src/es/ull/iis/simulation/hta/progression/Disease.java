/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation.Type;
import es.ull.iis.simulation.model.Describable;

/**
 * A disease defines the progression of a patient. Includes several manifestations and defines how such manifestations are related to each other. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Disease implements Named, Describable, CreatesSecondOrderParameters, Comparable<Disease> {
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * diseases defined to be used within a simulation */ 
	private int ord = -1;
	
	/** Manifestations related to this disease */
	private final ArrayList<Manifestation> manifestations;
	/** Manifestations and their associated transitions for this disease */
	private final TreeMap<Manifestation, ArrayList<Transition>> transitions;
	/** Manifestations and their associated REVERSE transitions for this disease */
	private final TreeMap<Manifestation, ArrayList<Transition>> reverseTransitions;
	/** Short name of the disease */
	private final String name;
	/** Full description of the disease */
	private final String description;
	private final Manifestation nullManifestation;
	
	/**
	 * Creates a submodel for a disease.
	 */
	public Disease(final SecondOrderParamsRepository secParams, String name, String description) {
		this.secParams = secParams;
		this.nullManifestation = new Manifestation(secParams, "NONE", "No manifestations", this, Type.CHRONIC) {
			@Override
			public void registerSecondOrderParameters() {			
			}
		};
		this.manifestations = new ArrayList<>();
		this.transitions = new TreeMap<>();
		this.transitions.put(nullManifestation, new ArrayList<>());
		this.reverseTransitions = new TreeMap<>();
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
	
	/**
	 * Returns the order assigned to this stage in a simulation.
	 * @return the order assigned to this stage in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this stage have in a simulation
	 * @param ord order that this stage have in a simulation
	 */
	public void setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
	}

	@Override
	public int compareTo(Disease o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
	
	public void reset(int id) {
		for (final Transition trans : transitions.get(nullManifestation))
			trans.reset(id);
		for (final Manifestation manif : manifestations) {
			manif.reset(id);
			for (final Transition trans : transitions.get(manif))
				trans.reset(id);
		}
	}
	
	/**
	 * @return the nullManifestation
	 */
	public Manifestation getNullManifestation() {
		return nullManifestation;
	}

	/**
	 * Adds a manifestation to this disease
	 * @param manif New manifestation associated to this disease
	 */
	public void addManifestation(Manifestation manif) {
		manifestations.add(manif);
		secParams.registerManifestation(manif);
		transitions.put(manif, new ArrayList<>());
		reverseTransitions.put(manif, new ArrayList<>());
	}
	
	/**
	 * Adds a new transition between two manifestations of this disease (or from "no manifestations" to any other manifestation)
	 * @param trans New transition between manifestations of this disease 
	 */
	public void addTransition(Transition trans) {
		transitions.get(trans.getSrcManifestation()).add(trans);
		reverseTransitions.get(trans.getDestManifestation()).add(trans);
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
		final Manifestation[] array = new Manifestation[manifestations.size()];
		return (Manifestation[]) manifestations.toArray(array);
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
	
	/**
	 * Adds the parameters corresponding to the second order uncertainty on the initial proportions for each stage of
	 * the complication
	 * @param secParams Second order parameters repository
	 */
	public void addSecondOrderInitProportion() {
		for (final Manifestation manif : getManifestations()) {
			if (BasicConfigParams.INIT_PROP.containsKey(manif.name())) {
				secParams.addProbParam(new SecondOrderParam(secParams, SecondOrderParamsRepository.getInitProbString(manif), "Initial proportion of " + manif.name(), "",
						BasicConfigParams.INIT_PROP.get(manif.name())));
			}			
		}		
	}
	
	@Override
	public String toString() {
		return name;
	}
}
