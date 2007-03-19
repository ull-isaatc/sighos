package es.ull.isaatc.util;

/**
 * Defines a repeated sequence of events. <p>
 * A cycle can be defined as containing a subcycle. The subcycle total duration 
 * should be restricted to the main cycle period. 
 * @author Iván Castilla Rodríguez
 */
public abstract class Cycle {
	/** Subcycle contained in this cycle. */
	protected Cycle subCycle = null;

	/**
	 * Creates an empty cycle
	 */
	public Cycle() {
	}

	/**
	 * Creates a cycle which contains a subcycle.
	 * @param subCycle Subcycle contained in this cycle.
	 */
	public Cycle(Cycle subCycle) {
		this.subCycle = subCycle;
	}
	/**
	 * Returns the subCycle contained in this cycle (if exists).
	 * @return The subCycle contained in this cycle (if exists).
	 */
	public Cycle getSubCycle() {
		return subCycle;
	}

	/**
	 * Returns an iterator over this cycle.
	 * @param absStart Absolute start timestamp 
	 * @param absEnd Absolute end timestamp
	 * @return An iterator over this cycle.
	 */
	public CycleIterator iterator(double absStart, double absEnd) {
		return new CycleIterator(this, absStart, absEnd);
	}
}
