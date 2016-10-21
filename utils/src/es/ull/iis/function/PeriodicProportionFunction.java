/**
 * 
 */
package es.ull.iis.function;

import java.util.Collection;

/**
 * A tricky function used to return the percentage of a value depending on the time unit.
 * Requires a set of integer values, a set of proportions (whose addition must be 1) and a 
 * minor time unit. The major time unit is the size of the proportions array.
 * Lets suppose a valid ts: the returned value will be <code>nElem[indexv] * prop[indexp]</code>,
 * where <code>indexp = (ts / timeUnit) % prop.length</code>, and 
 * <code>indexv = ((ts / timeUnit) / prop.length) % nElem.length</code>. 
 * @author Iván Castilla Rodríguez
 *
 */
public class PeriodicProportionFunction extends TimeFunction {
	private int []nElem;
	private double []prop;
	private double timeUnit;
	
	public PeriodicProportionFunction() {		
	}
	
	/**
	 * @param elem
	 * @param prop
	 * @param timeUnit
	 */
	public PeriodicProportionFunction(int[] elem, double[] prop, double timeUnit) {
		super();
		nElem = elem;
		this.prop = prop;
		this.timeUnit = timeUnit;
	}

	/**
	 * @param elem
	 * @param prop
	 * @param timeUnit
	 */
	public PeriodicProportionFunction(Collection<Integer> elem, Collection<Double> prop, double timeUnit) {
		super();
		this.nElem = new int[elem.size()];
		int i = 0;
		for (int e : elem)
			this.nElem[i++] = e;
		this.prop = new double[prop.size()];
		i = 0;
		for (double p : prop)
			this.prop[i++] = p;
		this.timeUnit = timeUnit;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(double)
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
		int unit = (int) (params.getTime() / timeUnit);
		int indexp = unit % prop.length;
		int indexv = (unit / prop.length) % nElem.length;
		return nElem[indexv] * prop[indexp];
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 3) 
			throw new IllegalArgumentException("Need 3, received " +
		            params.length + " parameters");
		if (!(params[0] instanceof int[]) || !(params[1] instanceof double[]) || !(params[2] instanceof Number))
            throw new IllegalArgumentException("Parameters must be int[], double[] and double");
		setNElem((int[])params[0]);
		setProp((double[])params[1]);
		setTimeUnit((Double)params[2]);
	}

	/**
	 * @return the nElem
	 */
	public int[] getNElem() {
		return nElem;
	}

	/**
	 * @param elem the nElem to set
	 */
	public void setNElem(int[] elem) {
		nElem = elem;
	}

	/**
	 * @return the prop
	 */
	public double[] getProp() {
		return prop;
	}

	/**
	 * @param prop the prop to set
	 */
	public void setProp(double[] prop) {
		this.prop = prop;
	}

	/**
	 * @return the timeUnit
	 */
	public double getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @param timeUnit the timeUnit to set
	 */
	public void setTimeUnit(double timeUnit) {
		this.timeUnit = timeUnit;
	}

}
