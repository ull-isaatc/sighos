/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.DefaultProbabilitySecondOrderParam;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.MultipleRandomSeedPerPatient;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A stepped strategy intended to involve different costs at different stages. For example, a treatment strategy may consist on starting with a drug, 
 * then switching to another, and finally staying with a different one. Strategies may require a previous condition to be applied. The simplest strategy just incurs
 * in a unit cost (either one-time or annual); more complex strategies involve a succession of stages or steps, each one consisting on one or several drugs, 
 * treatments, follow-up tests...
 * TODO: The computation of costs for complex structures is not yet implemented  
 * @author Iván Castilla Rodríguez
 *
 */
public class Strategy implements PartOfStrategy {
	private final Condition<Patient> condition;
	private final String description;
	private final String name;
	private final ArrayList<ArrayList<PartOfStrategy>> parts;
	private Strategy parent = null;
	protected final SecondOrderParamsRepository secParams;
	private final RandomSeedForPatients[] randomSeeds;
	
	/**
	 * 
	 */
	public Strategy(SecondOrderParamsRepository secParams,String name, String description) {
		this(secParams, name, description, new TrueCondition<Patient>());
	}

	/**
	 * 
	 */
	public Strategy(SecondOrderParamsRepository secParams, String name, String description, Condition<Patient> cond) {
		this.condition = cond;
		this.description = description;
		this.name = name;
		this.parts = new  ArrayList<>();
		this.secParams = secParams;
		this.randomSeeds = new RandomSeedForPatients[secParams.getNRuns() + 1];
		Arrays.fill(randomSeeds, null);
	}

	public void reset(int id) {
		randomSeeds[id].reset();
	}
	
	public RandomSeedForPatients getRandomSeedForPatients(int id) {
		if (randomSeeds[id] == null) {
			randomSeeds[id] = new MultipleRandomSeedPerPatient(secParams.getNPatients(), true);
		}
		return randomSeeds[id];
	}
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @return the parent
	 */
	public Strategy getParent() {
		return parent;
	}

	public void setParent(Strategy strategy) {
		this.parent = strategy;
	}

	public Condition<Patient> getCondition() {
		return condition;
	}
	
	/**
	 * @return the stages
	 */
	public ArrayList<ArrayList<PartOfStrategy>> getParts() {
		return parts;
	}

	private ArrayList<PartOfStrategy> getProperLevel(boolean nextLevel) {
		ArrayList<PartOfStrategy> level = null;
		if (parts.isEmpty() || nextLevel) {
			level = new ArrayList<>();
			parts.add(level);
		}
		else {
			level = parts.get(parts.size() - 1);
		}
		return level;
	}
	
	public void addPart(Strategy child, boolean nextLevel) {
		child.setParent(this);
		getProperLevel(nextLevel).add(child);
	}

	public void addPart(HealthTechnology child, boolean nextLevel) {
		getProperLevel(nextLevel).add(child);
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate) {
		double cost = 0.0;
		// If the patient does not meet the condition, the strategy cannot be applied
		if (condition.check(pat)) {
			final double unitCost = secParams.getCostParam(DefaultProbabilitySecondOrderParam.UNIT_COST.getParameterName(this), pat.getSimulation());
			// In case a unit cost is defined, it is used first to compute the cost of the strategy
			if (!Double.isNaN(unitCost)) {
				final boolean isAnnual = SecondOrderCostParam.TemporalBehavior.ANNUAL.equals(secParams.getTemporalBehaviorOfCostParam(DefaultProbabilitySecondOrderParam.UNIT_COST.getParameterName(this)));
				if (isAnnual) {
					cost += discountRate.applyDiscount(unitCost, startT, endT);
				}
				else {
					cost += discountRate.applyPunctualDiscount(unitCost, startT);
				}
			}
			// In any case, checks if the strategy is more complex
			/* TODO: Though the class allows to define complex structure with multiple levels, still assuming a single level. Should implement more levels but,
			 * how to store the level for a patient, when to consider that a level has failed or you must recheck conditions? 
			 */
			for (PartOfStrategy part : parts.get(0)) {
				cost += part.getCostForPeriod(pat, startT, endT, discountRate);
			}
		}
		return cost;
	}

	@Override
	public double[] getAnnualizedCostForPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}
}
