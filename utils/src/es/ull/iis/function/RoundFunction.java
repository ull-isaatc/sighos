package es.ull.iis.function;

import es.ull.iis.util.ExtendedMath;


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
	private double scale = 1.0;
	private double shift = 0.0;
	
	/**
	 * 
	 */
	public RoundFunction() {
	}

	public RoundFunction(Type type, TimeFunction func, double scale) {
		this.type = type;
		this.func = func;
		this.scale = scale;
	}
	
	public RoundFunction(Type type, TimeFunction func, double scale, double shift) {
		this.type = type;
		this.func = func;
		this.scale = scale;
		this.shift = shift;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#getValue(double)
	 */
	@Override
	public double getValue(TimeFunctionParams params) {
		double val = func.getValue(params);
		if (scale != 0.0) {
			switch(type) {
				case CEIL:					
					val = ExtendedMath.ceil(val, scale) + shift;
					break;
				case FLOOR:
					val = ExtendedMath.floor(val, scale) + shift;
					break;
				case ROUND:
				default:
					val = ExtendedMath.round(val, scale) + shift;
					break;
			}
		}
		return val;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunction#setParameters(java.lang.Object[])
	 */
	@Override
	public void setParameters(Object... params) {
		if (params.length != 4) {
            throw new IllegalArgumentException("Should be four parameters for Round: " +
            params.length + " passed.");
        }
        if (!(params[0] instanceof Type))
            throw new IllegalArgumentException("Parameters must be a RoundFunction.Type");
        else if  (!(params[1] instanceof TimeFunction))    
            throw new IllegalArgumentException("Parameters must be a TimeFunction");
        else if  (!(params[2] instanceof Number))
            throw new IllegalArgumentException("Parameters must be a Number");
        else if  (!(params[3] instanceof Number))
            throw new IllegalArgumentException("Parameters must be a Number");
        else {
            setType((Type) params[0]);
            setFunc((TimeFunction) params[1]);
            setScale(((Number) params[2]).doubleValue());
            setShift(((Number) params[3]).doubleValue());
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
	public double getScale() {
		return scale;
	}

	/**
	 * @param scale the factor to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * @return the shift
	 */
	public double getShift() {
		return shift;
	}

	/**
	 * @param shift the shift to set
	 */
	public void setShift(double shift) {
		this.shift = shift;
	}

}
