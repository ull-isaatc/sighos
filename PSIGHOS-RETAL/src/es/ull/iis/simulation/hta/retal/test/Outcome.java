/**
 * 
 */
package es.ull.iis.simulation.hta.retal.test;

import java.util.Arrays;

import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Outcome {
	protected final double discountRate;
	protected final double[]aggregated;
	protected final double[][]values;
	
	/**
	 * 
	 */
	public Outcome(int nInt, int nElem, double discountRate) {
		aggregated = new double[nInt];
		values = new double[nInt][nElem];
		this.discountRate = discountRate;
	}

	/**
	 * Updates the value of this outcome for a specified period
	 * @param pat A patient
	 * @param value A constant value during the period
	 * @param initAge Initial age when the value is applied
	 * @param endAge End age when the value is applied
	 */
	public void update(int interventionId, int elemId, double value, double initAge, double endAge) {
		value = applyDiscount(value, initAge, endAge);
		values[interventionId][elemId] += value;
		aggregated[interventionId] += value;
	}
	/**
	 * Updates the value of this outcome at a specified age
	 * @param pat A patient
	 * @param value The value to update
	 * @param age The age at which the value is applied
	 */
	public void update(int interventionId, int elemId, double value, double age) {
		value = applyPunctualDiscount(value, age);
		values[interventionId][elemId] += value;
		aggregated[interventionId] += value;
	}
	
	public void print(boolean detailed, boolean units) {
		if (detailed) {
			for (int i = 0; i < values[0].length; i++) {
				System.out.print("[" + i + "]\t");
				for (int j = 0; j < values.length; j++) {
					System.out.print(values[j][i] + "\t");
				}
				System.out.println();
			}
		}
		System.out.println(this + " summary:");
		for (int j = 0; j < aggregated.length; j++) {
			System.out.print(aggregated[j] / values[j].length + "\t");
		}
		System.out.println();
	}
	
	/**
	 * Apply a discount rate to a constant value over a time period. 
	 * @param value A constant value that applied each year
	 * @param initAge The age that the patient had when starting the period
	 * @param endAge The age that the patient had when ending the period
	 * @return A discounted value
	 */
	protected double applyDiscount(double value, double initAge, double endAge) {
		if (discountRate == 0.0)
			return value * (endAge - initAge);
		return value * (-1 / Math.log(1 + discountRate)) * (Math.pow(1 + discountRate, -endAge) - Math.pow(1 + discountRate, -initAge));
	}
	
	/**
	 * Apply a discount rate to a value at a specific moment of the simulation.
	 * @param value A value
	 * @param time The specific age when the discount is applied 
	 * @return A discounted value
	 */
	protected double applyPunctualDiscount(double value, double time) {
		if (discountRate == 0.0)
			return value;
		return value / Math.pow(1 + discountRate, time);
	}
	/**
	 * Returns average, SD, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5% for each intervention
	 * @return
	 */
	public double[][] getResults() {
		final double[][] results = new double[aggregated.length][6];
		for (int i = 0; i < results.length; i++) {
			final double avg = getAverage(i);
			final double sd = Statistics.stdDev(values[i], avg);
			final double[] ci = Statistics.normal95CI(avg, sd, values[0].length);
			final double[] cip = get95CI(i, true);
			results[i] = new double[] {avg, sd, ci[0], ci[1], cip[0], cip[1]};
		}
		return results;
	}
	
	public double getAverage(int intervention) {
		return aggregated[intervention] / values[0].length;		
	}
	
	public double getSD(int intervention) {
		return Statistics.stdDev(values[intervention], getAverage(intervention));
	}
	
	public double[] get95CI(int intervention, boolean percentile) {
		if (!percentile)
			return Statistics.normal95CI(getAverage(intervention), getSD(intervention), values[0].length);
		final double[] ordered = Arrays.copyOf(values[intervention], values[0].length);
		Arrays.sort(ordered);
		final int index = (int)Math.ceil(values[0].length * 0.025);
		return new double[] {ordered[index - 1], ordered[values[0].length - index]}; 
	}
	
}
