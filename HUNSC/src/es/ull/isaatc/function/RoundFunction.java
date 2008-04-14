package es.ull.isaatc.function;

import es.ull.isaatc.util.ExtendedMath;


/**
 * @author Iván
 *
 */
public class RoundFunction extends TimeFunction {
	public enum Type {
		ROUND,
		CEIL,
		FLOOR
	}
	private Type type = Type.ROUND;
	private TimeFunction func;
	private double factor = 1.0;
	
	/**
	 * 
	 */
	public RoundFunction() {
	}

	public RoundFunction(Type type, TimeFunction func, double factor) {
		this.type = type;
		this.func = func;
		this.factor = factor;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#getValue(double)
	 */
	@Override
	public double getValue(double ts) {
		double val = func.getValue(ts);
		if (factor != 0.0) {
			switch(type) {
				case CEIL:					
					val = ExtendedMath.ceil(val, factor);
					break;
				case FLOOR:
					val = ExtendedMath.floor(val, factor);
					break;
				case ROUND:
				default:
					val = ExtendedMath.round(val, factor);
					break;
			}
		}
		return val;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 3) {
            throw new IllegalArgumentException("Should be three parameters for Round: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof Type))
            throw new IllegalArgumentException("Parameters must be a RoundFunction.Type");
        else if  (!(params[1] instanceof TimeFunction))    
            throw new IllegalArgumentException("Parameters must be a TimeFunction");
        else if  (!(params[2] instanceof Number))
            throw new IllegalArgumentException("Parameters must be a Number");
        else {
            setType((Type) params[0]);
            setFunc((TimeFunction) params[1]);
            setFactor(((Number) params[2]).doubleValue());
        }

	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the func
	 */
	public TimeFunction getFunc() {
		return func;
	}

	/**
	 * @param func the func to set
	 */
	public void setFunc(TimeFunction func) {
		this.func = func;
	}

	/**
	 * @return the factor
	 */
	public double getFactor() {
		return factor;
	}

	/**
	 * @param factor the factor to set
	 */
	public void setFactor(double factor) {
		this.factor = factor;
	}

}
