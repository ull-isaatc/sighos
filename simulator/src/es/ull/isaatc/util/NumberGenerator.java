/**
 * 
 */
package es.ull.isaatc.util;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class NumberGenerator {
	public abstract double getNumber(double ts);
	
	public double getPositiveNumber(double ts) {
    	double res = getNumber(ts);
    	if (res < 0.0)
    		res = 0.0;
    	return res;
	}
}
