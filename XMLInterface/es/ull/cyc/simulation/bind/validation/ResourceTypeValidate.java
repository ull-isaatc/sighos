/**
 * 
 */
package es.ull.cyc.simulation.bind.validation;

import es.ull.cyc.simulation.bind.ResourceType;

/**
 * @author Roberto Muñoz
 *
 */
public class ResourceTypeValidate extends Validate {

	@Override
	public boolean validate(Object valObj) throws ModelException {
		ResourceType r = (ResourceType)valObj;
		boolean hasError = false;
		
		hasError |= checkId(r.getId());
		hasError |= checkDescription(r.getId(), r.getDescription());
		hasError |= has(r.getId(), r.getDescription());
		
		if (hasError) {
			throw new ModelException("Resource type error");
		}
		add(r.getId(), r.getDescription());
		return !hasError;
	}


}
