package es.ull.isaatc.simulation.condition;

import es.ull.isaatc.simulation.model.ElementType;

/**
 * Condition used to compare element types.
 * @author ycallero
 *
 */
public class ElementTypeCondition extends Condition{

	/** Type which is used to compare */
	ElementType type;
	
	/**
	 * Creates a new ElementType Contition.
	 * @param id Identifier
	 * @param type Type which will use to compare.
	 */
	public ElementTypeCondition(ElementType type) {
		this.type = type;
	}

	/**
	 * Returns the type which the condition use to compare.
	 * @return A ElementType.
	 */
	public ElementType getType() {
		return type;
	}
}
