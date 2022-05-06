/**
 * 
 */
package es.ull.iis.simulation.hta.progression.condition;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.hta.Patient;

/**
 * A logical AND condition among a collection of pathway conditions
 * @author Iván Castilla Rodríguez
 *
 */
public class AndCondition extends PathwayCondition {
	final Collection<PathwayCondition> conditionList;
	
	/**
	 * 
	 */
	public AndCondition(PathwayCondition cond1, PathwayCondition cond2) {
		this.conditionList = new ArrayList<>();
		this.conditionList.add(cond1);
		this.conditionList.add(cond2);
	}

	/**
	 * 
	 */
	public AndCondition(Collection<PathwayCondition> conditionList) {
		this.conditionList = new ArrayList<>();
		this.conditionList.addAll(conditionList);
	}

	@Override
	public boolean check(Patient pat) {
		for (PathwayCondition cond : conditionList)
			if (!cond.check(pat))
				return false;
		return true;
	}

}
