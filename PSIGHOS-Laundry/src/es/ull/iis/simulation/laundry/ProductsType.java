/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.model.ElementType;

/**
 * @author Iván Castilla
 *
 */
public enum ProductsType {
	BED_SHEETS(0),
	BLANKETS(1),
	BEDCOVERS(1),
	TOWELS(1),
	PYJAMAS(1);
	
	final private int priority;
	private ElementType et;

	private ProductsType(final int priority) {
		this.priority = priority;
	}
	
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the et
	 */
	public ElementType getElementType() {
		return et;
	}

	/**
	 * @param et the et to set
	 */
	public void setElementType(ElementType et) {
		this.et = et;
	}
}
