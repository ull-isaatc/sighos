/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.PrettyPrintable;
import es.ull.iis.simulation.hta.outcomes.CostProducer;
import es.ull.iis.simulation.hta.outcomes.Strategy;
import es.ull.iis.simulation.hta.outcomes.UtilityProducer;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * The second order characterization of an intervention. The {@link #registerSecondOrderParameters()} method 
 * must be invoked from the {@link SecondOrderParamsRepository} to register the second order parameters. 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Intervention implements NamedAndDescribed, CreatesSecondOrderParameters, Comparable<Intervention>, PrettyPrintable, CostProducer, UtilityProducer {
	/** A short name for the intervention */
	final private String name;
	/** A full description of the intervention */
	final private String description;
	/** An index to be used when this class is used in TreeMaps or other ordered structures. The order is unique among the
	 * interventions defined to be used within a simulation */ 
	private int ord = -1;
	/** Common parameters repository */
	private final SecondOrderParamsRepository secParams;

	private ParameterModifier lifeExpectancyModification;
	private ParameterModifier mortalityRiskModification;
	private ParameterModifier allParameterModification;
	private final Strategy strategy;
	
	/**
	 * Creates a second order characterization of an intervention
	 * @param name Short name
	 * @param description Full description
	 */
	public Intervention(final SecondOrderParamsRepository secParams, final String name, final String description) {
		this(secParams, name, description, null);
	}
	
	/**
	 * Creates a second order characterization of an intervention
	 * @param name Short name
	 * @param description Full description
	 */
	public Intervention(final SecondOrderParamsRepository secParams, final String name, final String description, Strategy strategy) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		lifeExpectancyModification = ParameterModifier.NULL_MODIFIER;
		mortalityRiskModification = ParameterModifier.NULL_MODIFIER;
		allParameterModification = ParameterModifier.NULL_MODIFIER;
		this.strategy = strategy;
		secParams.addIntervention(this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns a short name for the intervention 
	 * @return A short name for the intervention 
	 */
	@Override
	public String name() {
		return name;
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
		return name;
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0;
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return discountRate.applyAnnualDiscount(0.0, initT, endT);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
			Discount discountRate) {
		return discountRate.applyAnnualDiscount(0.0, initT, endT);
	}

	@Override
	public double getAnnualDisutility(Patient pat) {
		return UtilityParamDescriptions.DISUTILITY.forceValue(secParams, this, pat);
	}
	
	@Override
	public double getStartingDisutility(Patient pat) {
		return UtilityParamDescriptions.ONE_TIME_DISUTILITY.forceValue(secParams, this, pat);
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

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
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
	public String prettyPrint(String linePrefix) {
		final StringBuilder str = new StringBuilder(linePrefix).append("Intervention: ").append(name).append(System.lineSeparator());
		if (!"".equals(description))
			str.append(linePrefix + "\t").append(description).append(System.lineSeparator());
		return str.toString();
	}
}
