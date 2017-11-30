/**
 * 
 */
package es.ull.iis.function;

import java.util.ArrayList;

/**
 * A time function that can be restarted several times and replicates the previous generation of values in the same order.
 * @author Iván Castilla
 *
 */
public class ReplicableTimeFunction extends TimeFunction {
	private TimeFunction innerTimeFunction;
	final private ArrayList<Double> genValues;
	private int counter;

	/**
	 * 
	 */
	public ReplicableTimeFunction(TimeFunction innerTimeFunction) {
		this();
		this.innerTimeFunction = innerTimeFunction;
	}

	public ReplicableTimeFunction() {
		genValues = new ArrayList<>();
		counter = 0;
	}
	
	public void reStart() {
		counter = 0;		
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(es.ull.iis.function.TimeFunctionParams)
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
		if (counter == genValues.size())
			genValues.add(innerTimeFunction.getValue(params));
		return genValues.get(counter++);
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
