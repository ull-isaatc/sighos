/**
 * 
 */
package es.ull.iis.simulation.hta.progression.condition;

import es.ull.iis.simulation.hta.Patient;

/**
 * A condition that negates another pathway condition
 * @author Iván Castilla Rodríguez
 *
 */
public class NotCondition extends PathwayCondition {
	final PathwayCondition negCondition;
	
	/**
	 * 
	 */
	public NotCondition(PathwayCondition negCondition) {
		this.negCondition = negCondition;
	}

	@Override
	public boolean check(Patient pat) {
		return !negCondition.check(pat);
	}

}
