/**
 * 
 */
package es.ull.isaatc.random;

import java.lang.Math;

/**
 * Based on http://www.ee.ucl.ac.uk/~mflanaga/java/PsRandom.java by 
 * Michael Thomas Flanagan
 * @author Iván Castilla Rodríguez
 *
 */
public class Weibull extends RandomNumber {
	private double sigma;
	private double mu;
	private double invShape;

	/**
	 * 
	 */
	public Weibull(double mu, double sigma, double gamma) {
		super();
		this.invShape = 1 / gamma;
		this.sigma = sigma;
		this.mu = mu;
	}

	/**
	 * 
	 */
	public Weibull(double sigma, double gamma) {
		super();
		this.invShape = 1 / gamma;
		this.sigma = sigma;
		this.mu = 0.0;
	}

    public double sampleDouble() {
    	return Math.pow(-Math.log(1.0D - sample01()), invShape) * sigma + mu; 
    }

    public int sampleInt() {
    	return (int)sampleDouble();
    }
}
