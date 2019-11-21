/**
 * 
 */
package simkit.random;

/**
 * @author Iván Castilla
 *
 */
public class LimitedRandomVariate extends RandomVariateBase {
	private RandomVariate innerRnd;
	private double upperLimit;
	private double lowerLimit;

	/**
	 * 
	 */
	public LimitedRandomVariate() {
	}

	@Override
	public double generate() {
		return (Math.max(lowerLimit, Math.min(upperLimit, innerRnd.generate())));
	}

	@Override
	public Object[] getParameters() {
		return new Object[] {innerRnd, lowerLimit, upperLimit};
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length != 3) 
			throw new IllegalArgumentException("Need (random variate, lower limit, upper limit), received " +
		            params.length + " parameters");
        if (!(params[0] instanceof RandomVariate)) {
            throw new IllegalArgumentException("First parameter must be a RandomVariate");
        }
        if (!(params[1] instanceof Number) || !(params[2] instanceof Number)) {
            throw new IllegalArgumentException("Parameters 1 and 2 must be a Number");
        }
		else {
			setInnerRnd((RandomVariate)params[0]);
			setLowerLimit((Double)params[1]);
			setUpperLimit((Double)params[2]);
		}
	}

	/**
	 * @return the rnd
	 */
	public RandomVariate getInnerRnd() {
		return innerRnd;
	}

	/**
	 * @param rnd the rnd to set
	 */
	public void setInnerRnd(RandomVariate rnd) {
		this.innerRnd = rnd;
	}

	/**
	 * @return the upperLimit
	 */
	public double getUpperLimit() {
		return upperLimit;
	}

	/**
	 * @param upperLimit the upperLimit to set
	 */
	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	/**
	 * @return the lowerLimit
	 */
	public double getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * @param lowerLimit the lowerLimit to set
	 */
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

}
