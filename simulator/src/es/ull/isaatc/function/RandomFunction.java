/**
 * 
 */
package es.ull.isaatc.function;

import simkit.random.RandomVariate;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class RandomFunction extends TimeFunction {
	private RandomVariate rnd;
	
	/**
	 * 
	 */
	public RandomFunction(RandomVariate rnd) {
		super();
		this.rnd = rnd;
	}

	/**
	 * @return Returns the rnd.
	 */
	public RandomVariate getRandom() {
		return rnd;
	}

	/**
	 * @param rnd The rnd to set.
	 */
	public void setRandom(RandomVariate rnd) {
		this.rnd = rnd;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#getValue(double)
	 */
	public double getValue(double ts) {
		return rnd.generate();
	}

	@Override
	public void setParameters(Object... params) {
		if (params.length < 1) 
			throw new IllegalArgumentException("Need (RandomVariate), received " +
		            params.length + " parameters");
		else {
			setRandom((RandomVariate)params[0]);
		}
	}

}
