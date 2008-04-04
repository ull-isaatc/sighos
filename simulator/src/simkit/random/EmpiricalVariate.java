/**
 * 
 */
package simkit.random;

/**
 * Composed random generator which returns a value corresponding to one of several
 * random generators depending on a probability.
 * @author Iván Castilla Rodríguez
 */
public class EmpiricalVariate extends RandomVariateBase implements RandomVariate {
	private double [] prob;
	private RandomVariate [] values;
	
	public EmpiricalVariate() {
		
	}
	
	/**
	 * @return the values
	 */
	public RandomVariate[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(RandomVariate[] values) {
		this.values = values;
	}

	/**
	 * @return the prob
	 */
	public double[] getProb() {
		return prob;
	}

	/**
	 * @param prob the prob to set
	 */
	public void setProb(double[] prob) {
		this.prob = prob;
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#generate()
	 */
	public double generate() {
		double sample = rng.draw();
		double delta = 0.0;
		for(int i = 0; i < prob.length - 1; i++) {
			delta += prob[i];
			if (delta >= sample)
				return values[i].generate();
		}
		return values[values.length - 1].generate();
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#getParameters()
	 */
	public Object[] getParameters() {
		return new Object[] { getValues() };
	}

	/* (non-Javadoc)
	 * @see simkit.random.RandomVariate#setParameters(java.lang.Object[])
	 */
	public void setParameters(Object... params) {
        if (params.length != 2)
            throw new IllegalArgumentException("Need parameters length 2: " + params.length);
        if (params[0] instanceof double[])
            setProb((double[])params[0]);
        else 
            throw new IllegalArgumentException("Need type double[]: " + params[0].getClass().getName());
        if (params[1] instanceof RandomVariate[])
            setValues((RandomVariate[])params[1]);
        else if (params[1] instanceof double[]) {
        	values = new RandomVariate[((double[])params[1]).length];
        	for (int i = 0; i < values.length; i++) {
        		values[i] = new ConstantVariate();
        		values[i].setParameters(((double[])params[1])[i]);
        	}
        }
        else 
            throw new IllegalArgumentException("Need type double[]: " + params[1].getClass().getName());
	}

}
