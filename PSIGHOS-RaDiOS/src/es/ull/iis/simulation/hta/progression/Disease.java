/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.Describable;

/**
 * A disease defines the progression of a patient. Includes several manifestations and defines how such manifestations are related to each other.
 * The disease also defines whether the onset of a chronic manifestation excludes other manifestations. By default, any chronic manifestation
 * excludes the "asymptomatic" chronic manifestation. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Disease implements Named, Describable, CreatesSecondOrderParameters, Comparable<Disease> {
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * diseases defined to be used within a simulation */ 
	private int ord = -1;
	
	/** Manifestations related to this disease */
	protected final TreeMap<String, Manifestation> manifestations;
	/** Manifestations that exclude another manifestation (generally, because they are more advance stages of the same condition */
	protected final TreeMap<Manifestation, TreeSet<Manifestation>> exclusions;	
	/** Short name of the disease */
	private final String name;
	/** Full description of the disease */
	private final String description;
	/** A collection of manifestations with a specific label */
	protected final TreeMap<Named, ArrayList<Manifestation>> labeledManifestations;
	
	/**
	 * Creates a submodel for a disease.
	 */
	public Disease(final SecondOrderParamsRepository secParams, String name, String description) {
		this.secParams = secParams;
		this.manifestations = new TreeMap<>();
		this.exclusions = new TreeMap<>();
		this.name = name;
		this.description = description;
		this.labeledManifestations = new TreeMap<>();
		secParams.addDisease(this);
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
		for (final Manifestation manif : manifestations.values()) {
			manif.reset(id);
		}
	}

	/**
	 * Adds a manifestation to this disease and also to the repository. 
	 * @param manif New manifestation associated to this disease
	 * @return The manifestation added
	 */
	public Manifestation addManifestation(Manifestation manif) {
		manifestations.put(manif.getName(), manif);
		secParams.addManifestation(manif);
		TreeSet<Manifestation> excManif = new TreeSet<>();
		exclusions.put(manif, excManif);
		return manif;
	}
	
	/**
	 * Assigns a label to a manifestation
	 * @param label ÇA label that identifies related manifestations
	 * @param manif A manifestation of the disease
	 */
	public void assignLabel(Named label, Manifestation manif) {
		if (!labeledManifestations.containsKey(label))
			labeledManifestations.put(label, new ArrayList<>());
		labeledManifestations.get(label).add(manif);
		manif.addLabel(label);
	}
	
	/**
	 * Returns the manifestations labeled with label; an empty list in case the label has no related manifestations
	 * @param label A label that identifies related manifestations
	 * @return the manifestations labeled with label; an empty list in case the label has no related manifestations
	 */
	public ArrayList<Manifestation> getLabeledManifestations(Named label) {
		if (!labeledManifestations.containsKey(label))
			return new ArrayList<>();
		return labeledManifestations.get(label); 
	}
	
	/**
	 * Adds a new rule of exclusion for a manifestation, that precludes a patient from experiencing the "excluded" manifestation at the same time.
	 * @param manif The "exclusive" manifestation
	 * @param excluded The "excluded" manifestation
	 * @return This disease
	 */
	public Disease addExclusion(Manifestation manif, Manifestation excluded) {
		exclusions.get(manif).add(excluded);
		return this;
	}
	
	/**
	 * Adds a new rule of exclusion for a manifestation, that precludes a patient from experiencing the "excluded" manifestations at the same time.
	 * @param manif The "exclusive" manifestation
	 * @param excluded The collection of "excluded" manifestations
	 * @return This disease
	 */
	public Disease addExclusion(Manifestation manif, Collection<Manifestation> excluded) {
		for (Manifestation exc : excluded)
			exclusions.get(manif).add(exc);
		return this;
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
		final TreeSet<Manifestation> excluded = new TreeSet<>();
		for (final Manifestation manif : manifestations.values()) {
			if (manif.hasManifestationAtStart(pat)) {
				init.add(manif);
				excluded.addAll(getExcluded(manif));
			}
		}
		// Check and remove exclusive manifestations
		for (final Manifestation manifExcluded : excluded) {
			init.remove(manifExcluded);
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
	 * Returns the diagnosis cost for this disease
	 * @param pat A patient
	 * @return the diagnosis cost for this disease
	 */
	public abstract double getDiagnosisCost(Patient pat);
	
	/**
	 * Returns the treatment and follow up costs for this disease during the defined period (adjusted for a full year). These costs should only be applied to diagnosed patients 
	 * @param pat A patient
	 * @param initAge Starting time of the period (in years)
	 * @param endAge Ending time of the period
	 * @return the treatment and follow up costs for this disease
	 */
	public abstract double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge);
	
	/**
	 * Returns the disutility value associated to the current stage of this disease
	 * @param pat A patient
	 * @param method Method used to compute the disutility of this disease in case there are more 
	 * than one commorbility
	 * @param refUtility Reference utility
	 * @return The disutility value associated to the current stage of this disease
	 */
	public abstract double getDisutility(Patient pat, DisutilityCombinationMethod method, double refUtility);
	
	/** 
	 * Returns the number of stages used to model this complication
	 * @return the number of stages used to model this complication
	 */
	public int getNManifestations() {
		return manifestations.size();
	}
	
	/**
	 * Returns the manifestations used to model this disease
	 * @return An array containing the manifestations used to model this disease 
	 */
	public Manifestation[] getManifestations() {
		final Manifestation[] array = new Manifestation[manifestations.size()];
		return (Manifestation[]) manifestations.values().toArray(array);
	}
	
	/**
	 * Returns a specific manifestation identified by a short description
	 * @param name Short description of the manifestation used as id 
	 * @return a specific manifestation identified by a short description
	 */
	public Manifestation getManifestation(String name) {
		return manifestations.get(name);
	}
	
	/**
	 * Returns the manifestations that are excluded by the specified manifestation, i.e. can not happen at the same time    
	 * @param manif A manifestaion that may exclude others
	 * @return the manifestations excluded by the specified manifestation
	 */
	public TreeSet<Manifestation> getExcluded(Manifestation manif) {
		return exclusions.get(manif);
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
