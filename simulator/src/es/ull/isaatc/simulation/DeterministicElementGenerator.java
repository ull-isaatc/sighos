/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.Orderable;
import es.ull.isaatc.util.OrderedList;

/**
 * Defines a set of timestamp-ordered elements which have to be created. Each "element" is defined 
 * through its type, its metaflow and the timestamp when the element arrives at the system. Thus, 
 * this generator is a deterministic generator. 
 * @author Iván Castilla Rodríguez
 */
public class DeterministicElementGenerator extends Generator {
	/** The ordered list of elements to generate */
	private OrderedList<GenerationTrio> genSet;
	/** The current position in the set of elements to generate */
	private int count = 0;
	
	/**
	 * Creates a deterministic generator including the elements it has to create. 
	 * @param simul Simulation where this generator works
	 * @param genSet The set of elements which the generator have to create.
	 */
	public DeterministicElementGenerator(Simulation simul, OrderedList<GenerationTrio> genTrio) {
		super(simul);
		this.genSet = genTrio;
	}

	/**
	 * Creates a deterministic generator with an empty set of elements to create. 
	 * @param simul Simulation where this generator works
	 */
	public DeterministicElementGenerator(Simulation simul) {
		super(simul);
		this.genSet = new OrderedList<GenerationTrio>();
	}

	/**
	 * Adds an element to the set of elements to be created. The order this element is added 
	 * is determined by its timestamp.
	 * @param et The element's type
	 * @param metaFlow The element's metaflow
	 * @param ts The timestamp when the element is going to start
	 */
	public void addElement(ElementType et, MetaFlow metaFlow, double ts) {
		genSet.add(new GenerationTrio(et, metaFlow, ts));		
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Generator#createElements()
	 */
	@Override
	public void createElements() {
		Element elem = new Element(elemCounter++, simul, genSet.get(count).getElementType());
		genSet.get(count).getMetaFlow().getFlow(null, elem);
        elem.start(defLP);
        count++;
	}

	@Override
	// MOD 18/12/06 Now it takes into account the initial simulation ts
	public double nextTs() {
		while (count != genSet.size()) {
			double newTs = genSet.get(count).getTs();
			if (newTs < defLP.getTs())
				count++;
			else
				return newTs;
		}
		return Double.NaN;
	}

	/**
	 * Definition of the elements to be generated.
	 * @author Iván Castilla Rodríguez
	 */
	public class GenerationTrio implements Orderable {
		/** The element's type */
		protected ElementType et;
		/** The element's metaflow */
		protected MetaFlow metaFlow;
		/** The timestamp when the element is going to start */
		protected double ts; 
		
		/**
		 * Creates a definition of an element to be generated.
		 * @param et The element's type
		 * @param metaFlow The element's metaflow
		 * @param ts The timestamp when the element is going to start
		 */
		public GenerationTrio(ElementType et, MetaFlow metaFlow, double ts) {
			this.et = et;
			this.metaFlow = metaFlow;
			this.ts = ts;
		}

		/**
		 * @return Returns the et.
		 */
		public ElementType getElementType() {
			return et;
		}

		/**
		 * @return Returns the metaFlow.
		 */
		public MetaFlow getMetaFlow() {
			return metaFlow;
		}

		/**
		 * @return Returns the ts.
		 */
		public double getTs() {
			return ts;
		}

		public Comparable getKey() {
			return ts;
		}

		public int compareTo(Orderable obj) {
			return compareTo(obj.getKey());		
		}

		public int compareTo(Object o) {
			return getKey().compareTo(o);		
		}
	}
}
