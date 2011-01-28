/**
 * 
 */
package es.ull.isaatc.function;

/**
 * Defines a time function which consists of an array of other time functions. The time unit
 * is used to choose the function corresponding to the current timestamp. Therefore,
 * getValue will return a value of part[i], where i = (ts / timeStep) % functionArray.length 
 * @author Iván Castilla Rodríguez
 */
public class UniformlyDistributedSplitFunction extends TimeFunction {
	private TimeFunction [] part;
	private double timeStep;
	
	public UniformlyDistributedSplitFunction() {
	
	}
	
	
	/**
	 * @param part
	 * @param timeUnit
	 */
	public UniformlyDistributedSplitFunction(TimeFunction[] part, double timeUnit) {
		super();
		this.part = part;
		this.timeStep = timeUnit;
	}


	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#getValue(double)
	 */
	@Override
	public double getValue(double ts) {
		int unit = (int) (ts / timeStep);
		int index = unit % part.length;
		return part[index].getValue(ts);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 2)
			throw new IllegalArgumentException("Need 2, received " +
		            params.length + " parameters");
		if (!(params[0] instanceof TimeFunction[]) || !(params[1] instanceof Number))
            throw new IllegalArgumentException("Parameters must be TimeFunction[] and double");
		setPart((TimeFunction[])params[0]);
		setTimeStep((Double)params[1]);
	}


	/**
	 * @return the part
	 */
	public TimeFunction[] getPart() {
		return part;
	}


	/**
	 * @param part the part to set
	 */
	public void setPart(TimeFunction[] part) {
		this.part = part;
	}


	/**
	 * @return the time step
	 */
	public double getTimeStep() {
		return timeStep;
	}


	/**
	 * @param timeStep the time step to set
	 */
	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

}
