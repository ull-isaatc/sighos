/**
 * 
 */
package es.ull.isaatc.util;

/**
 * Allows to iterate over a cycle definition. The start and end timestamp of
 * the cycle regards on an initial time that the user must specify (absStartTs).
 * A cycle iterator can depend on an external final timestamp; in this case 
 * the user can specify a limit timestamp (absEndTs).
 * Every time you use next() a new timestamp is returned. A NaN value 
 * indicates that the end of the cycle has been reached. 
 * <p><b>IMPORTANT NOTE: 12/12/2006</b>
 * <br>This class has to change: The startTs and endTs of the parent cycle are
 * now considered as absolute. 
 * @author Iv�n Castilla Rodr�guez
 */
public class CycleIterator {
	/** Subcycles traversed by this iterator */ 
	protected CycleEntry []cycleTable;
	/** The current timestamp */
	protected double ts = Double.NaN;

	/**
	 * @param cycle Cycle followed by this iterator. 
	 * @param absStartTs Absolute start timestamp.
	 * @param absEndTs Absolute end timestamp.
	 */
	public CycleIterator(Cycle cycle, double absStartTs, double absEndTs) {
		Cycle c;
		int levels = 1;
		for (c = cycle; c.getSubCycle() != null; c = c.getSubCycle())
			levels++;
		cycleTable = new CycleEntry[levels];

		// First entry of the table is created
		// CHANGE 13/12/06: Instead of using absStartTs, using 0 for initial timestamp.
		cycleTable[0] = new CycleEntry(cycle, 0, absEndTs);
		// The rest of entries are created		
		c = cycle.getSubCycle();
		for (int i = 1; i < levels; i++, c = c.getSubCycle())
			cycleTable[i] = new CycleEntry(c, ts, Math.min(cycleTable[i - 1].nextTs, cycleTable[i - 1].endTs));
		// CHANGE 13/12/06 If the start timestamp is not zero, the real start timestamp
		// has to be recomputed.
		boolean found = false;
		while (!Double.isNaN(ts) && !found) {
			if (ts >= absStartTs)
				found = true;
			else
				next();
		}
	}

	/**
	 * Computes the next valid timestamp or Double.NaN if the next 
	 * timestamp is not valid.
	 * @return The next timestamp. Double.NaN if the next timestamp is not 
	 * a valid one.
	 */
	public double next() {
		double auxTs = ts;
		if (!Double.isNaN(ts)) {
			int i = cycleTable.length;
			do {
				if (cycleTable[--i].hasNext())
					cycleTable[i].next();
				else
					ts = Double.NaN;
			} while ((i > 0) && (Double.isNaN(ts)));
			// This condition is skipped when the whole cycle is finished 
			if (!Double.isNaN(ts)) {
				for (; i < cycleTable.length - 1; i++) {
					cycleTable[i + 1].reset(ts, Math.min(cycleTable[i].nextTs, cycleTable[i].endTs));
				}
			}
		}
		return auxTs;
	}

	/**
	 * @return Returns the cycle.
	 */
	public Cycle getCycle() {
		return cycleTable[0].cycle;
	}

	/**
	 * Represents a level in the cycle structure. Each level is a subcycle.
	 * @author Iv�n Castilla Rodr�guez
	 */
	class CycleEntry {
		/** The next timestamp. */
		double nextTs;
		/** The iterations left. */
		int iter;
		/** The end timestamp. */
		double endTs;
		/** Associated cycle. */
		Cycle cycle;
		
		/**
		 * @param cycle Associated cycle.
		 * @param start The start timestamp.
		 * @param end The end timestamp.
		 */
		public CycleEntry(Cycle cycle, double start, double end) {
			this.cycle = cycle;
			reset(start, end);
		}
		
		/**
		 * Resets this level. The iterations are reset and the end timestamp is recomputed.
		 * @param start The start timestamp.
		 * @param newEnd The end timestamp.
		 */
		public void reset(double start, double newEnd) {
			this.iter = cycle.getIterations();
			// NOTA: Si quisiera controlar subciclos mal definidos lo har�a aqu�
			if (!Double.isNaN(cycle.getEndTs()))
				endTs = start + cycle.getEndTs();
			else
				endTs = newEnd;
			// If the "supercycle" starts after the simulation end.
			if (Double.isNaN(newEnd)) {
				nextTs = Double.NaN;
				ts = nextTs;
			}
			else {
				nextTs = start + cycle.getStartTs();
				// If the cycle starts after the simulation end
				if (hasNext())
					next();
				else
					nextTs = Double.NaN;
			}
		}

		/**
		 * Returns the availability of a valid timestamp.
		 * @return True if there are a valid next timestamp. False in other case. 
		 */
		private boolean hasNext() {
			if (Double.isNaN(nextTs))
				return false;
			// If the next timestamp to be generated is not valid
			if (nextTs >= endTs)
				return false;
			// If there are infinite iterations
			if (cycle.getIterations() == 0)
				return true;
			// If there are n iterations and we have reached the last iteration
			if (iter == 0)
				return false;
			// In other case
			return true;
		}

		/**
		 * Computes the next valid timestamp and sets the value of the <code>ts</code>
		 * attribute.
		 */
		public void next() {
			ts = nextTs;
			if (iter > 0)
				iter--;
			// Computes the next valid timestamp...
			nextTs += cycle.getPeriod().samplePositiveDouble();
		}
	}
}
