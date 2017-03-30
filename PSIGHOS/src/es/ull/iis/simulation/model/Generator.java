/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.function.TimeFunctionParams;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public abstract class Generator<INF extends Generator.GenerationInfo> extends SimulationObject implements TimeFunctionParams {
	/** Number of objects created each time this creator is invoked. */
	protected final TimeFunction nElem;
	/** Each flow that will be generated */
	protected final ArrayList<INF> genInfo = new ArrayList<INF>();

	/**
	 * Creates a creator of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public Generator(Simulation model, int id, TimeFunction nElem) {
		super(model, id, "GEN");
		this.nElem = nElem;
	}
	
	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public Generator(Simulation model, int id, int nElem) {
		this(model, id, TimeFunctionFactory.getInstance("ConstantVariate", nElem));
	}
	
	@Override
	public double getTime() {
		return getTs();
	}
	
	/**
	 * Adds a [element type, proportion] generation info.
	 * @param et Element type
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(INF info) {
		genInfo.add(info);
	}

	/**
	 * Allows a user to define actions to be executed before the elements are created. This
	 * method is invoked inside <code>create</code> and can be used to change the current 
	 * amount of elements to be generated. By default, returns the predefined value.
	 * @param n Computed number of elements to create.
	 * @return New number of elements to create.
	 */
	public int beforeCreateElements(int n) {
		return n; 
	}
	
	/**
	 * Allows a user to define actions to be executed after the elements are created.
	 * This method is invoked inside <code>create</code>. By default, does nothing.
	 */
	public void afterCreateElements() {}

	public abstract EventSource createEventSource(int ind, INF info);
	
	public EventSource[] create(long ts) {
		int n = getSampleNElem();
		n = beforeCreateElements(n);
		final EventSource[] elems = new EventSource[n];
        for (int i = 0; i < n; i++) {
            double p = Math.random();
            for (INF gt : genInfo) {
            	p -= gt.getProp();
            	if (p <= 0.0){
            		elems[i] = createEventSource(i, gt);
    	            final DiscreteEvent e = elems[i].onCreate(getTs());
    	            simul.addEvent(e);
    	            break;
            	}
            }
        }
        afterCreateElements();
		return elems;
	}
	
	public TimeFunction getNElem() {
		return nElem;
	}

	public int getSampleNElem() {
		return (int) nElem.getValue(this);
	}
	/**
	 * @return the genInfo
	 */
	public ArrayList<INF> getGenerationInfos() {
		return genInfo;
	}

	/**
	 * Description of a set of elements a generator can create.
	 * @author Iván Castilla Rodríguez
	 */
    public static class GenerationInfo {
		/** Proportion of elements corresponding to this flow. */
		protected final double prop;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param prop Proportion of elements corresponding to this flow.
		 */
		protected GenerationInfo(double prop) {
			this.prop = prop;
		}
		
		/**
		 * Returns the proportion of elements to be created of this kind of elements.
		 * @return Returns the proportion.
		 */
		public double getProp() {
			return prop;
		}    	
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "GEN";
	}
}
