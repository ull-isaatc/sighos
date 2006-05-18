/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.random.RandomNumber;
import es.ull.cyc.util.CycleIterator;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class ElementGenerator extends Generator {
	/** */
	protected MetaFlow meta;
	/** */
	protected RandomNumber nElem;

	/**
	 * @param simul
	 * @param lp
	 * @param nElem
	 * @param cycle
	 * @param meta
	 */
	public ElementGenerator(Simulation simul, LogicalProcess lp, RandomNumber nElem, CycleIterator cycleIter, MetaFlow meta) {
		super(simul, lp, cycleIter);
		this.meta = meta;
		this.nElem = nElem;
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.Generator#createElement()
	 */
	public void createElements() {
        int n = (int)nElem.samplePositiveDouble();
        for (int i = 0 ; i < n; i++) {
    		Element elem = new Element(elemCounter++, simul, defLP);
    		elem.setFlow(meta.getFlow(null, elem));
            elem.start();
        }            
	}

}
