/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.outcomes.CostProducer;
import es.ull.iis.simulation.hta.outcomes.UtilityProducer;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.UsedParameter;

/**
 * A disease defines the progression of a patient. Includes several manifestations and defines how such manifestations are related to each other.
 * The disease also defines whether the onset of a chronic manifestation excludes other manifestations. By default, any chronic manifestation
 * excludes the "asymptomatic" chronic manifestation.
 * By default, the progression is driven by @link {@link DiseaseProgressionPathway manifestation pathways}, but it can be changed by modifying the
 * {@link #getProgression(Patient)} method. 
 * @author Iván Castilla Rodríguez
 */
public class Disease extends HTAModelComponent implements Comparable<Disease>, PrettyPrintable, CostProducer, UtilityProducer {
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * diseases defined to be used within a simulation */ 
	private int ord = -1;
	
	/** Progressions related to this disease */
	protected final TreeMap<String, DiseaseProgression> progressions;
	/** Disease progression that exclude another disease progression (generally, because they are more advance stages of the same condition */
	protected final TreeMap<DiseaseProgression, TreeSet<DiseaseProgression>> exclusions;	
	/** A collection of manifestations with a specific label */
	protected final TreeMap<Named, ArrayList<DiseaseProgression>> labeledProgressions;
	/** A collection of developments associated to the disease */
	protected final ArrayList<Development> developments;
	/** Parameters used by instances of this class */
	public enum USED_PARAMETERS implements UsedParameter {
		ANNUAL_COST,
		DISEASE_DIAGNOSIS_COST,
		TREATMENT_COST,
		FOLLOW_UP_COST,
		ANNUAL_DISUTILITY,
		ONSET_DISUTILITY
	}

	/**
	 * Creates a submodel for a disease.
	 */
	public Disease(HTAModel model, String name, String description) {
		super(model, name, description);
		this.progressions = new TreeMap<>();
		this.exclusions = new TreeMap<>();
		this.labeledProgressions = new TreeMap<>();
		this.developments = new ArrayList<>();
		if (!model.register(this))
			throw new IllegalArgumentException("Disease " + name + " already registered");
		setUsedParameterName(USED_PARAMETERS.ANNUAL_COST, StandardParameter.ANNUAL_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.DISEASE_DIAGNOSIS_COST, StandardParameter.DISEASE_DIAGNOSIS_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.TREATMENT_COST, StandardParameter.TREATMENT_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.ANNUAL_DISUTILITY, StandardParameter.ANNUAL_DISUTILITY.createName(this));
		setUsedParameterName(USED_PARAMETERS.ONSET_DISUTILITY, StandardParameter.ONSET_DISUTILITY.createName(this));
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

	/**
	 * Adds a development to this disease.
	 * @param New development associated to this disease
	 * @return The development added
	 */
	public Development addDevelopment(Development development) {
		developments.add(development);
		return development;
	}
	
	/**
	 * Adds a progression to this disease. 
	 * @param progression New progression associated to this disease
	 * @return The disease progression added
	 */
	public DiseaseProgression addDiseaseProgression(DiseaseProgression progression) {
		progressions.put(progression.name(), progression);
		TreeSet<DiseaseProgression> excManif = new TreeSet<>();
		exclusions.put(progression, excManif);
		return progression;
	}
	
	/**
	 * Assigns a label to a manifestation
	 * @param label ÇA label that identifies related manifestations
	 * @param manif A manifestation of the disease
	 */
	public void assignLabel(Named label, DiseaseProgression manif) {
		if (!labeledProgressions.containsKey(label))
			labeledProgressions.put(label, new ArrayList<>());
		labeledProgressions.get(label).add(manif);
		manif.addLabel(label);
	}
	
	/**
	 * Returns the manifestations labeled with label; an empty list in case the label has no related manifestations
	 * @param label A label that identifies related manifestations
	 * @return the manifestations labeled with label; an empty list in case the label has no related manifestations
	 */
	public ArrayList<DiseaseProgression> getLabeledManifestations(Named label) {
		if (!labeledProgressions.containsKey(label))
			return new ArrayList<>();
		return labeledProgressions.get(label); 
	}
	
	/**
	 * Adds a new rule of exclusion for a manifestation, that precludes a patient from experiencing the "excluded" manifestation at the same time.
	 * @param manif The "exclusive" manifestation
	 * @param excluded The "excluded" manifestation
	 * @return This disease
	 */
	public Disease addExclusion(DiseaseProgression manif, DiseaseProgression excluded) {
		exclusions.get(manif).add(excluded);
		return this;
	}
	
	/**
	 * Adds a new rule of exclusion for a manifestation, that precludes a patient from experiencing the "excluded" manifestations at the same time.
	 * @param manif The "exclusive" manifestation
	 * @param excluded The collection of "excluded" manifestations
	 * @return This disease
	 */
	public Disease addExclusion(DiseaseProgression manif, Collection<DiseaseProgression> excluded) {
		for (DiseaseProgression exc : excluded)
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
	public DiseaseProgressionEvents getProgression(Patient pat) {
		final DiseaseProgressionEvents prog = new DiseaseProgressionEvents();
		long limit = pat.getTimeToDeath();
		final TreeSet<DiseaseProgression> state = pat.getState();  
		for (final DiseaseProgression destManif : progressions.values()) {
			if (!state.contains(destManif)) {
				long prevTime = pat.getTimeToNextDiseaseProgression(destManif);
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
	
	/**
	 * Adds progression actions in case they are needed. First checks if the new time to event is valid. Then checks
	 * if there was a previously scheduled event and adds a "cancel" action. Finally, adds a "new" action for the new time.
	 * @param prog Current progression of the patient
	 * @param stage Chronic complication stage
	 * @param timeToEvent New time to event
	 * @param previousTimeToEvent Previous time to event
	 */
	public void adjustProgression(DiseaseProgressionEvents prog, DiseaseProgression stage, long timeToEvent, long previousTimeToEvent) {
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
	public TreeSet<DiseaseProgression> getInitialStage(Patient pat) {
		final TreeSet<DiseaseProgression> init = new TreeSet<>();
		final TreeSet<DiseaseProgression> excluded = new TreeSet<>();
		for (final DiseaseProgression manif : progressions.values()) {
			if (pat.startsWithDiseaseProgression(manif)) {
				init.add(manif);
				excluded.addAll(getExcluded(manif));
			}
		}
		// Check and remove exclusive manifestations
		for (final DiseaseProgression manifExcluded : excluded) {
			init.remove(manifExcluded);
		}		
		return init;
		
	}

	/**
	 * Returns the cost associated to the current state of the patient and during the defined period
	 * @param pat A patient
	 * @param initYear Starting time of the period (in years)
	 * @param endYear Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public double getCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		// The disease may involve a non-specific cost
		double cost =  discountRate.applyDiscount(model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ANNUAL_COST), pat), initYear, endYear);;
		// ... plus costs related to each manifestation
		for (final DiseaseProgression manif : pat.getState()) {
			cost +=  manif.getCostWithinPeriod(pat, initYear, endYear, discountRate);
		}
		// ... plus specific treatment or follow-up costs 
		if (pat.isDiagnosed())
			cost += getTreatmentAndFollowUpCosts(pat, initYear, endYear, discountRate);
		return cost;
	}
	
	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double []result = discountRate.applyAnnualDiscount(model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ANNUAL_COST), pat), initYear, endYear);;
		for (final DiseaseProgression manif : pat.getState()) {
			final double[] partial = manif.getAnnualizedCostWithinPeriod(pat, initYear, endYear, discountRate);
			for (int i = 0; i < result.length; i++)
				result[i] += partial[i];
		}
		if (pat.isDiagnosed()) {
			final double[] partial = getAnnualizedTreatmentAndFollowUpCosts(pat, initYear, endYear, discountRate);
			for (int i = 0; i < result.length; i++)
				result[i] += partial[i];
		}
		return result;
	}

	/**
	 * Returns the diagnosis cost for this disease
	 * @param pat A patient
	 * @param time Specific time when the cost is applied (in years)
	 * @param discountRate The discount rate to apply to the cost
	 * @return the diagnosis cost for this disease
	 */
	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(model.getParameterValue(getUsedParameterName(USED_PARAMETERS.DISEASE_DIAGNOSIS_COST), pat), time);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		// If common costs are defined, uses them
		final double annualCost = model.getParameterValue(getUsedParameterName(USED_PARAMETERS.TREATMENT_COST), pat) + model.getParameterValue(getUsedParameterName(USED_PARAMETERS.FOLLOW_UP_COST), pat);
		return discountRate.applyDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear,
			Discount discountRate) {
		final double annualCost = model.getParameterValue(getUsedParameterName(USED_PARAMETERS.TREATMENT_COST), pat) + model.getParameterValue(getUsedParameterName(USED_PARAMETERS.FOLLOW_UP_COST), pat);
		return discountRate.applyAnnualDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double getAnnualDisutility(Patient pat) {
		return model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ANNUAL_DISUTILITY), pat);
	}

	@Override
	public double getStartingDisutility(Patient pat) {
		return model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ONSET_DISUTILITY), pat);
	}
	
	/** 
	 * Returns the number of stages used to model this complication
	 * @return the number of stages used to model this complication
	 */
	public int getNManifestations() {
		return progressions.size();
	}
	
	/**
	 * Returns the manifestations used to model this disease
	 * @return An array containing the manifestations used to model this disease 
	 */
	public DiseaseProgression[] getDiseaseProgressions() {
		final DiseaseProgression[] array = new DiseaseProgression[progressions.size()];
		return (DiseaseProgression[]) progressions.values().toArray(array);
	}
	
	/**
	 * Returns a specific disease progression identified by a short description
	 * @param name Short description of the disease progression used as id 
	 * @return a specific disease progression identified by a short description
	 */
	public DiseaseProgression getDiseaseProgression(String name) {
		return progressions.get(name);
	}
	
	/**
	 * Returns the disease progressions that are excluded by the specified disease progression, i.e. can not happen at the same time    
	 * @param progression A disease progression that may exclude others
	 * @return the disease progressions excluded by the specified disease progression
	 */
	public TreeSet<DiseaseProgression> getExcluded(DiseaseProgression progression) {
		return exclusions.get(progression);
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		final StringBuilder str = new StringBuilder(linePrefix).append("Disease: ").append(name()).append(System.lineSeparator());
		if (!"".equals(getDescription()))
			str.append(linePrefix + "\t").append(getDescription()).append(System.lineSeparator());
		if (developments.size() > 0) {
			str.append(linePrefix).append("DEVELOPMENTS").append(System.lineSeparator());
			for (Development development : developments) {
				str.append(development.prettyPrint(linePrefix + "\t"));
			}
		}
		if (progressions.size() > 0) {
			str.append(linePrefix).append("MANIFESTATIONS").append(System.lineSeparator());
			for (DiseaseProgression manif : progressions.values())
				str.append(manif.prettyPrint(linePrefix + "\t"));
			if (exclusions.size() > 0) {
				str.append(linePrefix).append("EXCLUSIONS").append(System.lineSeparator());				
				for (DiseaseProgression manif : exclusions.keySet()) {
					str.append(linePrefix).append(manif).append(": ");
					for (DiseaseProgression excluded : exclusions.get(manif))
						str.append(excluded).append("\t");
					str.append(System.lineSeparator());
				}
			}
		}
		return str.toString();
	}
}
