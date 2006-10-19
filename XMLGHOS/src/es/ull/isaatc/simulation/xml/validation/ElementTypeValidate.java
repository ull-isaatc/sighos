/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import es.ull.isaatc.simulation.xml.ElementType;

/**
 * @author Roberto Muñoz
 */
public class ElementTypeValidate extends Validate {

    @Override
    public boolean validate(Object valObj) throws ModelException {

	ElementType et = (ElementType) valObj;
	boolean hasError = false;

	hasError |= checkId(et.getId());
	hasError |= checkDescription(et.getId(), et.getDescription());
	hasError |= has(et.getId(), et.getDescription());

	if (hasError) {
	    throw new ModelException("Element type error");
	}
	add(et.getId(), et.getDescription());
	return !hasError;
    }

}
