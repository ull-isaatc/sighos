/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.ArrayList;
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
 * Any stage, manifestation or sign that involves a progression in the development of a disease
 * @author Iván Castilla
 *
 */
public class DiseaseProgression extends HTAModelComponent implements Comparable<DiseaseProgression>, CostProducer, UtilityProducer, PrettyPrintable {
	/**
	 * The type of the disease progression. Currently distinguishes among chronic, acute manifestations and stages
	 */
	public enum Type {
		ACUTE_MANIFESTATION,
		CHRONIC_MANIFESTATION,
		STAGE,
		DEATH
	}
	/** Type of manifestation */
	private final Type type;
	/** Disease this progression is related to */
	private final Disease disease;
	private static int CURRENT_ID = 0; 
	/** An internal id to be able to use progressions within sets or other structures that need comparable contents */
	private final int internalId;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * disease progressions defined to be used within a simulation */ 
	private int ord = -1;

	/** The different ways to progress */
	private final ArrayList<DiseaseProgressionPathway> pathways;
	/** A set of labels that may be assigned to this manifestation. Labels serve to group related manifestations */
	private final TreeSet<Named> labels;
	// Names of the standard parameters that uses this disease
	public enum USED_PARAMETERS implements UsedParameter {
		INITIAL_PROPORTION,
		ANNUAL_COST,
		ONSET_COST,
		TREATMENT_COST,
		FOLLOW_UP_COST,
		ANNUAL_DISUTILITY,
		ONSET_DISUTILITY,
		END_AGE,
		ONSET_AGE,
		RISK_OF_DEATH,
		PROBABILITY_DIAGNOSIS,
		INCREASED_MORTALITY_RATE,
		LIFE_EXPECTANCY_REDUCTION
	}

	/**
	 * Creates a new progression for the specified disease 
	 * @param secParams Common parameters repository
	 * @param name Name of the progression
	 * @param description Full description of the progression
	 * @param disease The affected disease
	 */
	public DiseaseProgression(HTAModel model, String name, String description, Disease disease, Type type) {
		super(model, name, description);
		this.disease = disease;
		this.pathways = new ArrayList<>();
		internalId = CURRENT_ID++;
		this.labels = new TreeSet<Named>();
		this.type = type;
		if (!model.register(this))
			throw new IllegalArgumentException("Disease progression " + name + " already registered");		
		disease.addDiseaseProgression(this);
		setUsedParameterName(USED_PARAMETERS.ANNUAL_COST, StandardParameter.ANNUAL_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.ONSET_COST, StandardParameter.ONSET_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.TREATMENT_COST, StandardParameter.TREATMENT_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST.createName(this));
		setUsedParameterName(USED_PARAMETERS.ANNUAL_DISUTILITY, StandardParameter.ANNUAL_DISUTILITY.createName(this));
		setUsedParameterName(USED_PARAMETERS.ONSET_DISUTILITY, StandardParameter.ONSET_DISUTILITY.createName(this));
		setUsedParameterName(USED_PARAMETERS.END_AGE, StandardParameter.DISEASE_PROGRESSION_END_AGE.createName(this));
		setUsedParameterName(USED_PARAMETERS.ONSET_AGE, StandardParameter.DISEASE_PROGRESSION_ONSET_AGE.createName(this));
		setUsedParameterName(USED_PARAMETERS.INITIAL_PROPORTION, StandardParameter.DISEASE_PROGRESSION_INITIAL_PROPORTION.createName(this));
		setUsedParameterName(USED_PARAMETERS.RISK_OF_DEATH, StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH.createName(this));
		setUsedParameterName(USED_PARAMETERS.PROBABILITY_DIAGNOSIS, StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS.createName(this));
		setUsedParameterName(USED_PARAMETERS.INCREASED_MORTALITY_RATE, StandardParameter.INCREASED_MORTALITY_RATE.createName(this));
		setUsedParameterName(USED_PARAMETERS.LIFE_EXPECTANCY_REDUCTION, StandardParameter.LIFE_EXPECTANCY_REDUCTION.createName(this));
	}
	
	/**
	 * Returns the type of the manifestation (currently, ACUTE or CHRONIC)
	 * @return the type of the manifestation (currently, ACUTE or CHRONIC)
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Returns the {@link Disease} this manifestation is related to.
	 * @return the {@link Disease} this manifestation is related to
	 */
	public Disease getDisease() {
		return disease;
	}

	/**
	 * Adds a pathway to the manifestation
	 * @param pathway A new pathway that may lead to this manifestation 
	 */
	public void addPathway(DiseaseProgressionPathway pathway) {
		pathways.add(pathway);		
	}

	/**
	 * Returns the collection of pathways that lead to this progression
	 * @return the collection of pathways that lead to this progression
	 */
	public ArrayList<DiseaseProgressionPathway> getPathways() {
		return pathways;
	}
	
	/**
	 * Checks every suitable pathway for a patient to progress, and returns the lowest time from all the pathways. 
	 * @param pat A patient
	 * @param limit Threshold for the progressionto happen (in general, the expected death time for the patient)
	 * @return The lowest time for a patient to progress from all the suitable pathways
	 */
	public long getTimeTo(Patient pat, long limit) {
		long timeTo = Long.MAX_VALUE;
		for (DiseaseProgressionPathway path : pathways) {
			long newTime = path.getTimeToEvent(pat, limit);
			if (newTime < timeTo)
				timeTo = newTime;
		}
		return timeTo;
	}
	
	/**
	 * Returns the maximum age when this progression appears
	 * @param pat A patient
	 * @return the maximum age when this progression appears
	 */
	public double getEndAge(Patient pat) {
		return model.getParameterValue(getUsedParameterName(USED_PARAMETERS.END_AGE), pat);
	}
	
	/**
	 * Returns the minimum age when this progression appears
	 * @param pat A patient
	 * @return the minimum age when this progression appears
	 */
	public double getOnsetAge(Patient pat) {
		return model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ONSET_AGE), pat);
	}
	
	@Override
	public double getCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		return discountRate.applyDiscount(model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ANNUAL_COST), pat), initYear, endYear);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ONSET_COST), pat), time);
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		return discountRate.applyAnnualDiscount(model.getParameterValue(getUsedParameterName(USED_PARAMETERS.ANNUAL_COST), pat), initYear, endYear);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double annualCost = model.getParameterValue(getUsedParameterName(USED_PARAMETERS.TREATMENT_COST), pat) + model.getParameterValue(getUsedParameterName(USED_PARAMETERS.FOLLOW_UP_COST), pat);
		return discountRate.applyDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
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
	
	@Override
	public int compareTo(DiseaseProgression o) {
		if (internalId > o.internalId)
			return 1;
		if (internalId < o.internalId)
			return -1;
		return 0;
	}
	
	/**
	 * Returns the order assigned to this disease progression in a simulation.
	 * @return the order assigned to this disease progression in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this disease progression have in a simulation
	 * @param ord order that this disease progression have in a simulation
	 */
	public void setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
	}
	
	/**
	 * Adds a new label to this manifestation
	 * @param label New label
	 */
	public void addLabel(Named label) {
		labels.add(label);
	}
	
	/**
	 * Returns true if the manifestation defines the specified label
	 * @param label A label
	 * @return true if the manifestation defines the specified label
	 */
	public boolean definesLabel(Named label) {
		return (labels.contains(label));
	}

	@Override
	public String prettyPrint(String linePrefix) {
		
		final StringBuilder str = new StringBuilder(linePrefix).append(type.name()).append(" manifestation: ").append(name()).append(System.lineSeparator());
		if (!"".equals(getDescription()))
			str.append(linePrefix + "\t").append(getDescription()).append(System.lineSeparator());
		if (labels.size() > 0) {
			str.append(linePrefix).append("Labeled as: ");
			for (Named label : labels)
				str.append(label).append("\t");
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
}
