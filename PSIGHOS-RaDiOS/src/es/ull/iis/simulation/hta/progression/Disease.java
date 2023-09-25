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
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.Reseteable;
import es.ull.iis.simulation.hta.outcomes.CostProducer;
import es.ull.iis.simulation.hta.outcomes.UtilityProducer;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;

/**
 * A disease defines the progression of a patient. Includes several manifestations and defines how such manifestations are related to each other.
 * The disease also defines whether the onset of a chronic manifestation excludes other manifestations. By default, any chronic manifestation
 * excludes the "asymptomatic" chronic manifestation.
 * By default, the progression is driven by @link {@link ManifestationPathway manifestation pathways}, but it can be changed by modifying the
 * {@link #getProgression(Patient)} method. 
 * @author Iván Castilla Rodríguez
 */
public class Disease implements NamedAndDescribed, CreatesSecondOrderParameters, Comparable<Disease>, Reseteable, PrettyPrintable, CostProducer, UtilityProducer {
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;
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
	/** A collection of developments associated to the disease */
	protected final ArrayList<Development> developments;
	
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
		this.developments = new ArrayList<>();
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
	
	@Override
	public void reset(int id) {
		for (final Manifestation manif : manifestations.values()) {
			manif.reset(id);
		}
	}

	/**
	 * Adds a development to this disease and also to the repository.
	 * @param New development associated to this disease
	 * @return The development added
	 */
	public Development addDevelopment(Development development) {
		developments.add(development);
		secParams.addDevelopment(development);
		return development;
	}
	
	/**
	 * Adds a manifestation to this disease and also to the repository. 
	 * @param manif New manifestation associated to this disease
	 * @return The manifestation added
	 */
	public Manifestation addManifestation(Manifestation manif) {
		manifestations.put(manif.name(), manif);
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
	 * Returns the cost associated to the current state of the patient and during the defined period
	 * @param pat A patient
	 * @param initYear Starting time of the period (in years)
	 * @param endYear Ending time of the period
	 * @param discountRate The discount rate to apply to the cost
	 * @return the annual cost associated to the current state of the patient and during the defined period
	 */
	public double getCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		// The disease may involve a non-specific cost
		double cost =  discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, this, pat.getSimulation()), initYear, endYear);;
		// ... plus costs related to each manifestation
		for (final Manifestation manif : pat.getState()) {
			cost +=  discountRate.applyDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, manif, pat.getSimulation()), initYear, endYear);
		}
		// ... plus specific treatment or follow-up costs 
		if (pat.isDiagnosed())
			cost += getTreatmentAndFollowUpCosts(pat, initYear, endYear, discountRate);
		return cost;
	}
	
	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double []result = discountRate.applyAnnualDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, this, pat.getSimulation()), initYear, endYear);;
		for (final Manifestation manif : pat.getState()) {
			final double[] partial = discountRate.applyAnnualDiscount(CostParamDescriptions.ANNUAL_COST.getValue(secParams, manif, pat.getSimulation()), initYear, endYear);
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
		return discountRate.applyPunctualDiscount(CostParamDescriptions.DIAGNOSIS_COST.getValue(secParams, this, pat.getSimulation()), time);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		// If common costs are defined, uses them
		final double annualCost = CostParamDescriptions.TREATMENT_COST.getValue(secParams, this, pat.getSimulation()) + CostParamDescriptions.FOLLOW_UP_COST.getValue(secParams, this, pat.getSimulation());
		return discountRate.applyDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear,
			Discount discountRate) {
		final double annualCost = CostParamDescriptions.TREATMENT_COST.getValue(secParams, this, pat.getSimulation()) + CostParamDescriptions.FOLLOW_UP_COST.getValue(secParams, this, pat.getSimulation());
		return discountRate.applyAnnualDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double getAnnualDisutility(Patient pat) {
		return UtilityParamDescriptions.DISUTILITY.forceValue(secParams, this, pat.getSimulation());
	}

	@Override
	public double getStartingDisutility(Patient pat) {
		return UtilityParamDescriptions.ONE_TIME_DISUTILITY.forceValue(secParams, this, pat.getSimulation());
	}
	
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
				ProbabilityParamDescriptions.INITIAL_PROPORTION.addParameter(secParams, manif, "", BasicConfigParams.INIT_PROP.get(manif.name()));
			}			
		}		
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String prettyPrint(String linePrefix) {
		final StringBuilder str = new StringBuilder(linePrefix).append("Disease: ").append(name).append(System.lineSeparator());
		if (!"".equals(description))
			str.append(linePrefix + "\t").append(description).append(System.lineSeparator());
		if (developments.size() > 0) {
			str.append(linePrefix).append("DEVELOPMENTS").append(System.lineSeparator());
			for (Development development : developments) {
				str.append(development.prettyPrint(linePrefix + "\t"));
			}
		}
		if (manifestations.size() > 0) {
			str.append(linePrefix).append("MANIFESTATIONS").append(System.lineSeparator());
			for (Manifestation manif : manifestations.values())
				str.append(manif.prettyPrint(linePrefix + "\t"));
			if (exclusions.size() > 0) {
				str.append(linePrefix).append("EXCLUSIONS").append(System.lineSeparator());				
				for (Manifestation manif : exclusions.keySet()) {
					str.append(linePrefix).append(manif).append(": ");
					for (Manifestation excluded : exclusions.get(manif))
						str.append(excluded).append("\t");
					str.append(System.lineSeparator());
				}
			}
		}
		return str.toString();
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
	}

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
}
