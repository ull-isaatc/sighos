/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A stepped strategy intended to involve different costs at different stages. For example, a treatment strategy may consist on starting with a drug, 
 * then switching to another, and finally staying with a different one. Strategies may require a previous condition to be applied. The simplest strategy just incurs
 * in a specific cost; more complex strategies involve a succession of stages or steps, each one consisting on one or several drugs, treatments, follow-up tests...  
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

	@Override
	public double getUnitCost(Patient pat) {
		return secParams.getCostParam(getUnitCostParameterString(false), pat.getSimulation());
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
}
