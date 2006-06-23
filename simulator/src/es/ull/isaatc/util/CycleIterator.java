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
 * @author Iván Castilla Rodríguez
 */
public class CycleIterator {
	protected CycleEntry []cycleTable;
	protected double ts;

	/**
	 * @param cycle
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
		cycleTable[0] = new CycleEntry(cycle, absStartTs, absEndTs);
//		ts = absStartTs + cycle.getStartTs();
		// The rest of entries are created		
		c = cycle.getSubCycle();
		for (int i = 1; i < levels; i++, c = c.getSubCycle())
			cycleTable[i] = new CycleEntry(c, ts, cycleTable[i - 1].nextTs);
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
				cycleTable[--i].next();
			} while ((i > 0) && (Double.isNaN(ts)));
			// This condition is skipped when the whole cycle is finished 
			if (!Double.isNaN(ts)) {
				for (; i < cycleTable.length - 1; i++)
					cycleTable[i + 1].reset(ts, cycleTable[i].nextTs);
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
	
	class CycleEntry {
		double nextTs;
		int iter;
		double end;
		Cycle cycle;
		
		/**
		 * @param cycle
		 * @param start
		 * @param end
		 */
		public CycleEntry(Cycle cycle, double start, double end) {
			this.cycle = cycle;
			reset(start, end);
		}
		
		public void reset(double start, double newEnd) {
			this.iter = cycle.getIterations();
			// NOTA: Si quisiera controlar subciclos mal definidos lo haría aquí
			if (!Double.isNaN(cycle.getEndTs()))
				end = start + cycle.getEndTs();
			else
				end = newEnd;
			// FIXME: DEBERIA PODER QUITAR LA PRIMERA CONDICION
			if (Double.isNaN(newEnd)) {
				System.out.println("Aquí no debería llegar nunca");
				nextTs = Double.NaN;
				ts = nextTs;
			}
			else {
				nextTs = start + cycle.getStartTs();
				// MOD 17/04/06 Para evitar comenzar un ciclo después de su final
				if (nextTs > end)
					nextTs = Double.NaN;
				// It becomes initialized at next iteration
				next();
			}
		}

		/**
		 * Returns the availability of a valid timestamp.
		 * @return True if there are a valid next timestamp. False in other case. 
		 */
		private boolean hasNext() {
			// If the next timestamp to be generated is not valid
			if (nextTs >= end)
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

		public void next() {
			ts = nextTs;
			if (!Double.isNaN(nextTs)) {
				if (!hasNext()) {
					nextTs = Double.NaN;
					ts = Double.NaN;
				}
				else {
					if (iter > 0)
						iter--;
					// Computes the next valid timestamp...
					nextTs += cycle.getPeriod().samplePositiveDouble();
				}
			}
		}
	}
}
