/**
 * 
 */
package es.ull.iis.simulation.whellChairs;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.ElementInstance;

/**
 * A time function that can be restarted several times and replicates the previous generation of values in the same order.
 * @author Iván Castilla
 *
 */
public class ElementReplicableTimeFunction extends TimeFunction {
	private TimeFunction innerTimeFunction;
	final private TreeMap<Integer,ArrayList<Double>> genValues;
	final private TreeMap<Integer,Integer> counters;

	/**
	 * 
	 */
	public ElementReplicableTimeFunction(TimeFunction innerTimeFunction) {
		this();
		this.innerTimeFunction = innerTimeFunction;
	}

	public ElementReplicableTimeFunction() {
		genValues = new TreeMap<Integer,ArrayList<Double>>();
		counters = new TreeMap<Integer,Integer>();
	}
	
	/**
	 * Restarts all the counters
	 */
	public void restart() {
		counters.clear();
	}

	public void reset() {
		counters.clear();
		genValues.clear();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(es.ull.iis.function.TimeFunctionParams)
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
		final int elemId = ((ElementInstance)params).getElement().getIdentifier();
		// Element not already included
		if (!counters.containsKey(elemId)) {
			counters.put(elemId, 0);
			genValues.put(elemId, new ArrayList<>());
		}
		int counter = counters.get(elemId);
		final ArrayList<Double> values = genValues.get(elemId);
		// Value not already included
		if (counter == values.size()) {
			values.add(innerTimeFunction.getValue(params));
		}
		counters.put(elemId, counter+1);
		return values.get(counter++);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 1) {
            throw new IllegalArgumentException("Should be one parameters for PositiveTimeFunction: " +
            params.length + " passed.");
        }
        if  (!(params[0] instanceof TimeFunction)) {    
            throw new IllegalArgumentException("Parameters must be a TimeFunction");
        }
        else {
        	setInnerTimeFunction((TimeFunction) params[0]);
        }
	}

	/**
	 * @return the innerTimeFunction
	 */
	public TimeFunction getInnerTimeFunction() {
		return innerTimeFunction;
	}

	/**
	 * @param innerTimeFunction the innerTimeFunction to set
	 */
	public void setInnerTimeFunction(TimeFunction innerTimeFunction) {
		this.innerTimeFunction = innerTimeFunction;
	}
}
