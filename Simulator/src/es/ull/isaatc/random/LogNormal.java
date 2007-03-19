/**
 * 
 */
package es.ull.isaatc.random;

/**
 * Extracted from "Some notes on random number generation with GAMS"
 * by Erwin Kalvelagen. Available: http://www.gams.com/~erwin/random.pdf
 * @author Iván Castilla Rodríguez
 *
 */
public class LogNormal extends RandomNumber {
	private double mean;
	private double variance;
	private static Normal nor;

	/**
	 * @param mean
	 * @param variance
	 */
	public LogNormal(double mean, double variance) {
		super();
		this.mean = mean;
		this.variance = variance;
		nor = new Normal(0, 1);
	}

    public double sampleDouble() {
    	double x = nor.sampleDouble();
    	double mean_2 = mean * mean;
    	double m = Math.log(mean_2 / Math.sqrt(mean_2 + (variance * variance)));
    	double var_mean = variance / mean;
    	double s = Math.sqrt(Math.log(var_mean * var_mean + 1));
    	return Math.exp(x * s + m); 
    }

    public int sampleInt() {
    	return (int)sampleDouble();
    }
}
