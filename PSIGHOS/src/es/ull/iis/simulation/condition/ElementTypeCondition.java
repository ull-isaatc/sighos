package es.ull.iis.simulation.condition;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.ElementInstance;

/**
 * Condition used to check if an {@link Element} belongs to a specified {@link ElementType}.
 * @author Yeray Callero
 *
 */
public final class ElementTypeCondition extends Condition{

	/** Type for the comparison */
	private final ElementType type;
	
	/**
	 * Creates a new condition which compares {@link ElementType}s.
	 * @param type Type for the comparison
	 */
	public ElementTypeCondition(ElementType type) {
		this.type = type;
	}

	/**
	 * Checks the condition, returning <tt>true</tt> if the {@link ElementType} of the specified 
	 * {@link Element} is the one set in this condition, and <tt>false</tt> otherwise. 
	 * @param e {@link Element} to be checked with the condition.
	 * @return The result of the logical condition.
	 */
	public boolean check(ElementInstance fe) {
		if (type == fe.getElement().getType())
			return true;
		return false;
	}

	/**
	 * Returns the type used for the comparison.
	 * @return The {@link ElementType} used for the comparison
	 */
	public ElementType getType() {
		return type;
	}

}
