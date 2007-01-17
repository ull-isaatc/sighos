/**
 * 
 */
package es.ull.isaatc.function;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.random.RandomNumber;


/**
 * Represents a polynomic function: a1·x^n-1 + a2·x^n-2 + ... + an
 * @author Roberto Muñoz
 *
 */
public class PolynomicFunction implements TimeFunction {
	private RandomNumber coefficients[];
	private int length;
	
	public PolynomicFunction(RandomNumber coefficients[]) {
	    this.length = coefficients.length;
	    this.coefficients = coefficients;
	}

	public PolynomicFunction(double coefficients[]) {
	    this.length = coefficients.length;
	    int i = 0;
	    this.coefficients = new Fixed[length];
	    for (double j : coefficients)
		this.coefficients[i++] = new Fixed(j);
	    
	}

	public PolynomicFunction() {}


	public double getValue(double ts) {
	    double value = 0;
	    for (int i = 0; i < length ; i++) {
		value += Math.pow(ts, length - i - 1) * coefficients[i].sampleDouble();
	    }
	    return value;
	}
}
