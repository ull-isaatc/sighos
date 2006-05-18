/**
 * 
 */
package es.ull.cyc.simulation.bind.validation;

import es.ull.cyc.simulation.bind.Activity;

/**
 * @author Roberto Muñoz
 *
 */
public class ActivityValidate extends Validate {
	

	@Override
	public boolean validate(Object valObj) throws ModelException {
		Activity act = (Activity)valObj;
		boolean hasError = false;
		
		hasError |= checkId(act.getId());
		hasError |= checkDescription(act.getId(), act.getDescription());
		hasError |= has(act.getId(), act.getDescription());
		
		if (hasError) {
			throw new ModelException("Activity error");
		}
		add(act.getId(), act.getDescription());
		return !hasError;
	}
}
