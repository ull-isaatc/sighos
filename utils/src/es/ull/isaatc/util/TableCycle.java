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

	/* (non-Javadoc)
	 * @see es.ull.isaatc.util.Cycle#getIteratorLevel(double, double)
	 */
	@Override
	protected IteratorLevel getIteratorLevel(double start, double end) {
		return new TableIteratorLevel(start, end);
	}

	/**
	 * Represents a level in the cycle structure. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	protected class TableIteratorLevel extends Cycle.IteratorLevel {
		/** The current timestamp */
		int count;
		/** The reference start timestamp */
		double startTs;
		
		/**
		 * @param cycle Associated cycle.
		 * @param start The start timestamp.
		 * @param end The end timestamp.
		 */
		public TableIteratorLevel(double start, double end) {
			super(start, end);
		}
		
		@Override
		public double getNextTs() {
			if (hasNext())
				return timestamps[count];
			return Double.NaN;
		}

		@Override
		public double reset(double start, double newEnd) {
			this.startTs = start;
			currentTs = start;
			this.endTs = newEnd;
			// If the "supercycle" starts after the simulation end.
			if (Double.isNaN(newEnd))
				currentTs = Double.NaN;
			// If the cycle starts after the simulation end
			else if (hasNext())
				currentTs = next();
			return currentTs;
		}

		@Override
		public boolean hasNext() {
			if (count >= timestamps.length)
				return false;
			if (startTs + timestamps[count] >= endTs)
				return false;
			return true;
		}

		@Override
		public double next() {
			currentTs = startTs + timestamps[count++];
			return currentTs;
		}
	}

}
