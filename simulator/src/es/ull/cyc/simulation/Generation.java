/**
 * 
 */
package es.ull.cyc.simulation;

import java.util.ArrayList;
import es.ull.cyc.random.*;
import es.ull.cyc.util.Cycle;
import es.ull.cyc.util.CycleIterator;

/**
 * Information about what to generate. It describes the way a number of
 * elements is distributed among several meta-descriptions (metaflows) of
 * the element flows.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class Generation {
	/** Total amount of elements which will be generated */
	protected RandomNumber nElem;
	/** Each metaflow that will be generated */
	protected ArrayList<GenerationPair> genPairs;

	/**
	 * Creates a generation object.
	 * @param nElem Number of elements which will be generated.
	 */
	public Generation(RandomNumber nElem) {
		this.nElem = nElem;
		this.genPairs = new ArrayList<GenerationPair>();
	}
	
	/**
	 * Creates a generation object with an initial list of [metaflows, proportions] 
	 * pairs.
	 * @param nElem Number of elements which will be generated.
	 * @param genPairs Initial list of [metaflows, proportions] pairs.
	 */
	public Generation(RandomNumber nElem, ArrayList<GenerationPair> genPairs) {
		this.nElem = nElem;
		this.genPairs = genPairs;
	}

	/**
	 * Adds a [metaflow, proportion] pair.
	 * @param meta Metaflow
	 * @param prop Proportion of elements corresponding to this metaflow
	 */
	public void add(MetaFlow meta, double prop) {
		genPairs.add(new GenerationPair(meta, prop));
	}
	
	/**
	 * Creates the generator associated to this generation information. The 
	 * generator is created and started.
	 * @param simul Simulation which the generator will be attached to.
	 * @param cycle 
	 */
	public void createGenerators(Simulation simul, Cycle cycle) {
		LogicalProcess lp = simul.getDefaultLogicalProcess();
		int n = (int)nElem.samplePositiveDouble();
		for (int i = 0; i < genPairs.size(); i++) {
			CycleIterator it = new CycleIterator(cycle, lp.getTs(), simul.getEndTs());
			new ElementGenerator(simul, lp, new Fixed(Math.round(n * genPairs.get(i).getProp())), it, genPairs.get(i).getMeta()).start();
		}
	}

	public class GenerationPair {
		protected MetaFlow meta;
		protected double prop;
		
		/**
		 * @param meta
		 * @param prop
		 */
		public GenerationPair(MetaFlow meta, double prop) {
			super();
			this.meta = meta;
			this.prop = prop;
		}
		
		/**
		 * @return Returns the meta.
		 */
		public MetaFlow getMeta() {
			return meta;
		}
		/**
		 * @return Returns the prop.
		 */
		public double getProp() {
			return prop;
		}
	}
}
