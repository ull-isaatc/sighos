/**
 * 
 */
package simkit.random;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpTransformVariate extends RandomVariateBase {
	RandomVariate innerRnd;

	/**
	 * 
	 */
	public ExpTransformVariate() {
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	@Override
	public double generate() {
		return Math.exp(innerRnd.generate());
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	@Override
	public Object[] getParameters() {
		return new Object[] {innerRnd};
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
        if (params.length != 1) {
            throw new IllegalArgumentException("Should be one parameters for ExpTransform: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof RandomVariate)) {
            throw new IllegalArgumentException("Parameter must be a RandomVariate");
        }
        else {
        	setInnerRnd((RandomVariate)params[0]);
        }
	}

	/**
	 * @return the innerRnd
	 */
	public RandomVariate getInnerRnd() {
		return innerRnd;
	}

	/**
	 * @param innerRnd the innerRnd to set
	 */
	public void setInnerRnd(RandomVariate innerRnd) {
		this.innerRnd = innerRnd;
	}

}
