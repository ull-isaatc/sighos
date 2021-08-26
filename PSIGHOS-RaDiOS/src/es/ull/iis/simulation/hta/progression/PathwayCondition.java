/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.Patient;

/**
 * A condition that must be achieved to progress to a manifestation
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class PathwayCondition {
	/** A condition that is always true */
	public final static PathwayCondition TRUE_CONDITION = new PathwayCondition() {
		
		@Override
		public boolean check(Patient pat) {
			return true;
		}
	};

	/** A condition that is always false */
	public final static PathwayCondition FALSE_CONDITION = new PathwayCondition() {
		
		@Override
		public boolean check(Patient pat) {
			return false;
		}
	};

	/**
	 * Creates a new condition
	 */
	public PathwayCondition() {
	}

	/**
	 * Returns true if the condition is met; false otherwise
	 * @param pat A patient
	 * @return true if the condition is met; false otherwise
	 */
	public abstract boolean check(Patient pat);
}
