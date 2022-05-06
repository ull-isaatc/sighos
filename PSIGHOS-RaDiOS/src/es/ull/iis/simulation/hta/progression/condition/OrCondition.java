/**
 * 
 */
package es.ull.iis.simulation.hta.progression.condition;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.iis.simulation.hta.Patient;

/**
 * A logical OR condition among a collection of pathway conditions
 * @author Iván Castilla Rodríguez
 *
 */
public class OrCondition extends PathwayCondition {
	final Collection<PathwayCondition> conditionList;
	
	/**
	 * 
	 */
	public OrCondition(PathwayCondition cond1, PathwayCondition cond2) {
		this.conditionList = new ArrayList<>();
		this.conditionList.add(cond1);
		this.conditionList.add(cond2);
	}

	/**
	 * 
	 */
	public OrCondition(Collection<PathwayCondition> conditionList) {
		this.conditionList = new ArrayList<>();
		this.conditionList.addAll(conditionList);
	}

	@Override
	public boolean check(Patient pat) {
		for (PathwayCondition cond : conditionList)
			if (cond.check(pat))
				return true;
		return false;
	}

}
