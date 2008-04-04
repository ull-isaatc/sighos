/**
 * 
 */
package es.ull.isaatc.util;

/**
 * A simple package to get some basic statistics.
 * @author Iván Castilla Rodríguez
 *
 */
public class Statistics {
	public static double average(double[] values) {
		if (values.length == 0)
			return Double.NaN; 
		double acc = 0.0;
		for (double val : values)
			acc += val;
		return acc / (double)values.length;
	}
	
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
	
	public static double stdDev(double []values) {
		return stdDev(values, average(values));
	}

	public static double average(int[] values) {
		if (values.length == 0)
			return Double.NaN; 
		double acc = 0.0;
		for (int val : values)
			acc += val;
		return acc / (double)values.length;
	}
	
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
	
	public static double stdDev(int []values) {
		return stdDev(values, average(values));
	}
	
}
