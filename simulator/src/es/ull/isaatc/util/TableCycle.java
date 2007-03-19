/**
 * 
 */
package es.ull.isaatc.util;

/**
 * Defines a set of timestamps when something happens.
 * @author Iván Castilla Rodríguez
 */
public class TableCycle extends Cycle {
	/** The timestamps when something happens. */
	protected double [] timestamps;

	/**
	 * 
	 */
	public TableCycle(double [] timestamps) {
		super();
		this.timestamps = timestamps;
	}

	/**
	 * @param subCycle
	 */
	public TableCycle(double [] timestamps, Cycle subCycle) {
		super(subCycle);
		this.timestamps = timestamps;
	}

	/**
	 * Returns the timestamps of this cycle.
	 * @return The timestamps when something happens.
	 */
	public double []getTimeStamps() {
		return timestamps;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("Table Cycle: ");
		for (double v : timestamps)
			str.append("\t" + v);
		return str.toString();		
	}
}
