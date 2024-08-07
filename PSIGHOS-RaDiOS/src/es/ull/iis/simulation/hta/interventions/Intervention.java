/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.outcomes.CostProducer;
import es.ull.iis.simulation.hta.outcomes.Strategy;
import es.ull.iis.simulation.hta.outcomes.UtilityProducer;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.ParameterTemplate;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * A clinical intervention of any kind, that may modify the outcomes of a patient by modifying the parameters that characterize his/her progression. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Intervention extends HTAModelComponent implements Comparable<Intervention>, PrettyPrintable, CostProducer, UtilityProducer {
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * interventions defined to be used within a simulation */ 
	private int ord = -1;

	private ParameterModifier lifeExpectancyModification;
	private ParameterModifier mortalityRiskModification;
	private ParameterModifier allParameterModification;
	private final Strategy strategy;
	
	/**
	 * Creates a second order characterization of an intervention
	 * @param name Short name
	 * @param description Full description
	 */
	public Intervention(HTAModel model, final String name, final String description) {
		this(model, name, description, null);
	}
	
	/**
	 * Creates a second order characterization of an intervention
	 * @param name Short name
	 * @param description Full description
	 */
	public Intervention(HTAModel model, final String name, final String description, Strategy strategy) {
		super(model, name, description);
		lifeExpectancyModification = ParameterModifier.NULL_MODIFIER;
		mortalityRiskModification = ParameterModifier.NULL_MODIFIER;
		allParameterModification = ParameterModifier.NULL_MODIFIER;
		this.strategy = strategy;
		if (!model.register(this))
			throw new IllegalArgumentException("Intervention " + name + " already registered");
		registerUsedParameter(StandardParameter.ANNUAL_DISUTILITY);
		registerUsedParameter(StandardParameter.ONSET_DISUTILITY);
		registerUsedParameter(StandardParameter.ANNUAL_UTILITY);
		registerUsedParameter(StandardParameter.ONSET_UTILITY);
		registerUsedParameter(StandardParameter.ANNUAL_COST);
		registerUsedParameter(StandardParameter.ONSET_COST);
		registerUsedParameter(StandardParameter.TREATMENT_COST);
		registerUsedParameter(StandardParameter.FOLLOW_UP_COST);
	}

	/**
	 * Returns the order assigned to this intervention in a simulation.
	 * @return the order assigned to this intervention in a simulation
	 */
	public int ordinal() {
		return ord;
	}
	
	/**
	 * Assigns the order that this stage have in a simulation
	 * @param ord order that this stage have in a simulation
	 */
	public Intervention setOrder(int ord) {
		if (this.ord == -1)
			this.ord = ord;
		return this;
	}

	@Override
	public int compareTo(Intervention o) {
		if (ord > o.ord)
			return 1;
		if (ord < o.ord)
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return name();
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		return discountRate.applyDiscount(getUsedParameterValue(StandardParameter.ANNUAL_COST, pat), initYear, endYear);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(getUsedParameterValue(StandardParameter.ONSET_COST, pat), time);
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initYear, double endYear, Discount discountRate) {
		return discountRate.applyAnnualDiscount(getUsedParameterValue(StandardParameter.ANNUAL_COST, pat), initYear, endYear);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double annualCost = getUsedParameterValue(StandardParameter.TREATMENT_COST, pat) + getUsedParameterValue(StandardParameter.FOLLOW_UP_COST, pat);
		return discountRate.applyDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initYear, double endYear, Discount discountRate) {
		final double annualCost = getUsedParameterValue(StandardParameter.TREATMENT_COST, pat) + getUsedParameterValue(StandardParameter.FOLLOW_UP_COST, pat);
		return discountRate.applyAnnualDiscount(annualCost, initYear, endYear);
	}

	@Override
	public double getAnnualDisutility(Patient pat) {
		return getUsedParameterValue(StandardParameter.ANNUAL_DISUTILITY, pat);
	}
	
	@Override
	public double getStartingDisutility(Patient pat) {
		return getUsedParameterValue(StandardParameter.ONSET_DISUTILITY, pat);
	}
	
	/**
	 * @return the lifeExpectancyModification
	 */
	public ParameterModifier getLifeExpectancyModification() {
		return lifeExpectancyModification;
	}

	/**
	 * @param lifeExpectancyModification the lifeExpectancyModification to set
	 * @return This intervention
	 */
	public Intervention setLifeExpectancyModification(ParameterModifier lifeExpectancyModification) {
		this.lifeExpectancyModification = lifeExpectancyModification;
		return this;
	}
	
	/**
	 * @return the mortalityRiskModification
	 */
	public ParameterModifier getMortalityRiskModification() {
		return mortalityRiskModification;
	}

	/**
	 * @param mortalityRiskModification the mortalityRiskModification to set
	 * @return This intervention
	 */
	public Intervention setMortalityRiskModification(ParameterModifier mortalityRiskModification) {
		this.mortalityRiskModification = mortalityRiskModification;
		return this;
	}

	/**
	 * @return the allParameterModification
	 */
	public ParameterModifier getAllParameterModification() {
		return allParameterModification;
	}

	/**
	 * @param allParameterModification the allParameterModification to set
	 * @return This intervention
	 */
	public Intervention setAllParameterModification(ParameterModifier allParameterModification) {
		this.allParameterModification = allParameterModification;
		return this;
	}

	/**
	 * @return the strategy
	 */
	public Strategy getStrategy() {
		return strategy;
	}
	
	/**
	 * Returns a collection of events that happens to patients that are treated with this intervention
	 * @param pat A patient
	 * @return A collection of events that happens to patients that are treated with this intervention
	 */
	public ArrayList<DiscreteEvent> getEvents(Patient pat) {
		return new ArrayList<>();
	}
	
	@Override
	public double getUsedParameterValue(ParameterTemplate param, Patient pat) {
		if (StandardParameter.ANNUAL_DISUTILITY.equals(param)) {
			return forceUtilityParameterValue(param, StandardParameter.ANNUAL_UTILITY, pat);
		}
		else if (StandardParameter.ANNUAL_UTILITY.equals(param)) {
			return forceUtilityParameterValue(param, StandardParameter.ANNUAL_DISUTILITY, pat);
		}
		else if (StandardParameter.ONSET_DISUTILITY.equals(param)) {
			return forceUtilityParameterValue(param, StandardParameter.ONSET_UTILITY, pat);
		}
		else if (StandardParameter.ONSET_UTILITY.equals(param)) {
			return forceUtilityParameterValue(param, StandardParameter.ONSET_DISUTILITY, pat);
		}
		return super.getUsedParameterValue(param, pat);
	}

	@Override
	public String prettyPrint(String linePrefix) {
		final StringBuilder str = new StringBuilder(linePrefix).append("Intervention: ").append(name()).append(System.lineSeparator());
		if (!"".equals(getDescription()))
			str.append(linePrefix + "\t").append(getDescription()).append(System.lineSeparator());
		return str.toString();
	}
}
