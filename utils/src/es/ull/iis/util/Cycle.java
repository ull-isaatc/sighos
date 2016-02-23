package es.ull.iis.util;

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
	
	/**
	 * Returns an iterator over this cycle.
	 * @param absStart Absolute start timestamp 
	 * @param absEnd Absolute end timestamp
	 * @return An iterator over this cycle.
	 */
	public DiscreteCycleIterator iterator(long absStart, long absEnd) {
		return new DiscreteCycleIterator(this, absStart, absEnd);
	}
	
	protected abstract Cycle.IteratorLevel getIteratorLevel(double start, double end); 

	
	protected abstract Cycle.DiscreteIteratorLevel getDiscreteIteratorLevel(long start, long end);
	
	/**
	 * Represents a level in a cycle iterator. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	abstract protected class IteratorLevel {
		double currentTs;
		/** The end timestamp. */
		double endTs;

		public IteratorLevel(double start, double end) {
			reset(start, end);
		}
		
		/**
		 * Resets this level. The iterations are reset and the end timestamp is recomputed.
		 * @param start The start timestamp.
		 * @param newEnd The end timestamp.
		 */
		public abstract double reset(double start, double newEnd);

		/**
		 * Returns the availability of a valid timestamp.
		 * @return True if there are a valid next timestamp. False in other case. 
		 */
		protected abstract boolean hasNext();
		
		/**
		 * Computes the next valid timestamp and returns the value of the current
		 * timestamp.
		 */
		public abstract double next();
		
		/**
		 * Returns the next valid timestamp but it doesn't change anything.
		 * @return The next valid timestamp.
		 */
		public abstract double getNextTs();

		/**
		 * Returns the cycle referenced by this entry.
		 * @return The cycle referenced by this entry.
		 */
		public Cycle getCycle() {
			return Cycle.this;
		}
		
		/**
		 * Returns the end timestamp.
		 * @return The end timestamp.
		 */
		public double getEndTs() {
			return endTs;
		}

		/**
		 * @return the currentTs
		 */
		public double getCurrentTs() {
			return currentTs;
		}

	}

	/**
	 * Represents a level in a cycle iterator. Each level is a subcycle.
	 * @author Iván Castilla Rodríguez
	 */
	abstract protected class DiscreteIteratorLevel {
		protected long currentTs;
		/** The end timestamp. */
		protected long endTs;

		/**
		 * Resets this level. The iterations are reset and the end timestamp is recomputed.
		 * @param start The start timestamp.
		 * @param newEnd The end timestamp.
		 */
		public abstract long reset(long start, long newEnd);

		/**
		 * Returns the availability of a valid timestamp.
		 * @return True if there are a valid next timestamp. False in other case. 
		 */
		protected abstract boolean hasNext();
		
		/**
		 * Computes the next valid timestamp and returns the value of the current
		 * timestamp.
		 */
		public abstract long next();
		
		/**
		 * Returns the next valid timestamp but it doesn't change anything.
		 * @return The next valid timestamp.
		 */
		public abstract long getNextTs();

		/**
		 * Returns the cycle referenced by this entry.
		 * @return The cycle referenced by this entry.
		 */
		public Cycle getCycle() {
			return Cycle.this;
		}
		
		/**
		 * Returns the end timestamp.
		 * @return The end timestamp.
		 */
		public long getEndTs() {
			return endTs;
		}

		/**
		 * @return the currentTs
		 */
		public long getCurrentTs() {
			return currentTs;
		}

	}
}
