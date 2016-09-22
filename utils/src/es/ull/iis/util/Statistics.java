/**
 * 
 */
package es.ull.iis.util;

/**
 * A simple package to get some basic statistics.
 * @author Iván Castilla Rodríguez
 *
 */
public class Statistics {
	/** The factor to calculate 95% CI from SD */
	final static private double CI95FACTOR = 1.96;
	
	/**
	 * Returns the average of a set of values
	 * @param values Set of values.
	 * @return The average of a set of values
	 */
	public static double average(double[] values) {
		if (values.length == 0)
			return Double.NaN; 
		double acc = 0.0;
		for (double val : values)
			acc += val;
		return acc / (double)values.length;
	}
	
	/**
	 * Returns the standard deviation of a set of values
	 * @param values Set of values.
	 * @param av The precalculated average 
	 * @return The standard deviation of a set of values
	 */
	public static double stdDev(double []values, double av) {
		if (values.length == 0)
			return Double.NaN;
		else if (values.length == 1)
			return 0.0;
		double acc = 0.0;
		for (double val : values)
			acc += (val - av) * (val - av);
		return Math.sqrt(acc / (double)(values.length - 1));
	}
	
	/**
	 * Returns the standard deviation of a set of values
	 * @param values Set of values.
	 * @return The standard deviation of a set of values
	 */
	public static double stdDev(double []values) {
		return stdDev(values, average(values));
	}

	/**
	 * Returns the average of a set of values
	 * @param values Set of values.
	 * @return The average of a set of values
	 */
	public static double average(int[] values) {
		if (values.length == 0)
			return Double.NaN; 
		double acc = 0.0;
		for (int val : values)
			acc += val;
		return acc / (double)values.length;
	}
	
	/**
	 * Returns the standard deviation of a set of values
	 * @param values Set of values.
	 * @param av The precalculated average 
	 * @return The standard deviation of a set of values
	 */
	public static double stdDev(int []values, double av) {
		if (values.length == 0)
			return Double.NaN;
		else if (values.length == 1)
			return 0.0;
		double acc = 0.0;
		for (int val : values)
			acc += (val - av) * (val - av);
		return Math.sqrt(acc / (double)(values.length - 1));
	}
	
	/**
	 * Returns the standard deviation of a set of values
	 * @param values Set of values.
	 * @return The standard deviation of a set of values
	 */
	public static double stdDev(int []values) {
		return stdDev(values, average(values));
	}
	
	/**
	 * Returns the average of a set of values
	 * @param values Set of values.
	 * @return The average of a set of values
	 */
	public static double average(Double[] values) {
		if (values.length == 0)
			return Double.NaN; 
		double acc = 0.0;
		for (double val : values)
			acc += val;
		return acc / (double)values.length;
	}
	
	/**
	 * Returns the standard deviation of a set of values
	 * @param values Set of values.
	 * @param av The precalculated average 
	 * @return The standard deviation of a set of values
	 */
	public static double stdDev(Double []values, double av) {
		if (values.length == 0)
			return Double.NaN;
		else if (values.length == 1)
			return 0.0;
		double acc = 0.0;
		for (double val : values)
			acc += (val - av) * (val - av);
		return Math.sqrt(acc / (double)(values.length - 1));
	}
	
	/**
	 * Returns the standard deviation of a set of values
	 * @param values Set of values.
	 * @return The standard deviation of a set of values
	 */
	public static double stdDev(Double []values) {
		return stdDev(values, average(values));
	}

	/**
	 * Returns the relative error computed as abs(th -exp) / th. 
	 * @param th Theoretical value
	 * @param exp Experimental value
	 * @return The relative error.
	 */
	public static double relError(double th, double exp) {
		return (Math.abs(th - exp) / th);
	}

	/**
	 * Returns the relative error computed as abs(th -exp) / th * 100. 
	 * @param th Theoretical value
	 * @param exp Experimental value
	 * @return The relative error in percentage.
	 */
	public static double relError100(double th, double exp) {
		return relError(th, exp) * 100;
	}
	
	public static double[] normal95CI(double mean, double sd, int n) {
		final double ci = CI95FACTOR * sd / Math.sqrt(n);
		return new double[] {mean - ci, mean + ci};
	}
}
