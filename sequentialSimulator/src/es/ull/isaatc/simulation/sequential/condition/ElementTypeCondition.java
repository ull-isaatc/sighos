package es.ull.isaatc.simulation.sequential.condition;

import es.ull.isaatc.simulation.sequential.BasicElement;
import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.ElementType;

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
	 * Check the condition. If the element (e) has a diferent type than
	 * the type attibute.
	 * @param e Element which want to check the condition.
	 * @return The result of the logical condition.
	 */
	public boolean check(BasicElement e) {
		if (type.compareTo(((Element) e).getElementType()) == 0)
			return true;
		return false;
	}

	/**
	 * Returns the type which the condition use to compare.
	 * @return A ElementType.
	 */
	public ElementType getType() {
		return type;
	}

	/**
	 * Set a new type to compare.
	 * @param type Type which will use to compare.
	 */
	public void setType(ElementType type) {
		this.type = type;
	}

}
