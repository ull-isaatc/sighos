/**
 * 
 */
package es.ull.isaatc.function;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class TimeFunction {
	public abstract double getValue(double ts);

	public double getPositiveValue(double ts) {
    	double res = getValue(ts);
    	if (res < 0.0)
    		res = 0.0;
    	return res;
	}
	
	public abstract void setParameters(Object... params);
}
