/**
 * 
 */
package es.ull.iis.util;

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
 * @author Iván Castilla Rodríguez
 */
public class CycleIterator {
	/** Subcycles traversed by this iterator */ 
	protected Cycle.IteratorLevel []cycleTable;
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
		cycleTable = new Cycle.IteratorLevel[levels];

		// First entry of the table is created
		// CHANGE 13/12/06: Instead of using absStartTs, using 0 for initial timestamp.
		cycleTable[0] = cycle.getIteratorLevel(0, absEndTs);
		ts = cycleTable[0].getCurrentTs();
		// The rest of entries are created		
		c = cycle.getSubCycle();
		for (int i = 1; i < levels; i++, c = c.getSubCycle()) {
			cycleTable[i] = c.getIteratorLevel(ts, Math.min(cycleTable[i - 1].getNextTs(), cycleTable[i - 1].getEndTs()));
			ts = cycleTable[i].getCurrentTs();
		}
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
					ts = cycleTable[i].next();
				else
					ts = Double.NaN;
			} while ((i > 0) && (Double.isNaN(ts)));
			// This condition is skipped when the whole cycle is finished 
			if (!Double.isNaN(ts)) {
				for (; i < cycleTable.length - 1; i++) {
					ts = cycleTable[i + 1].reset(ts, Math.min(cycleTable[i].getNextTs(), cycleTable[i].getEndTs()));
				}
			}
		}
		return auxTs;
	}

	/**
	 * @return Returns the cycle.
	 */
	public Cycle getCycle() {
		return cycleTable[0].getCycle();
	}

}
