/**
 * 
 */
package es.ull.iis.function;

/**
 * An abstract class to return values corresponding to a specified function, which can depend on time.
 * @author Iván Castilla Rodríguez
 */
public abstract class TimeFunction {
	/**
	 * Returns a value as indicated by the definition of this function. 
	 * @param ts Time parameter which can be used to determine the value to be returned.
	 * @return A value as indicated by the definition of this function.
	 */
	public abstract double getValue(double ts);

	/**
	 * Simply calls <code>getValue</code> to return a value. However, if the value is negative, returns 0.0
	 * @param ts Time parameter which can be used to determine the value to be returned.
	 * @return A positive value corresponding to the definition of this function.
	 */
	public double getPositiveValue(double ts) {
    	double res = getValue(ts);
    	if (res < 0.0)
    		res = 0.0;
    	return res;
	}
	
	/**
	 * Sets the parameters of this function.
	 * @param params Parameters required by this function.
	 */
	public abstract void setParameters(Object... params);
}
