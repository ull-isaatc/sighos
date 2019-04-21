/**
 * 
 */
package es.ull.iis.simulation.hta.retal.outcome;

import java.util.Arrays;

import es.ull.iis.simulation.hta.HTASimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Outcome {
	/** The simulation this outcome is used in */
	protected final HTASimulation simul;
	/** A textual description of the outcome */
	private final String description;
	/** The unit used to measure the outcome */
	protected final String unit;
	protected final double discountRate;
	protected final int nInterventions;
	protected final int nPatients;
	protected final double[]aggregated;
	protected final double[][]values;
	
	/**
	 * 
	 */
	public Outcome(int nInterventions, HTASimulation simul, String description, String unit, double discountRate) {
		this.simul = simul;
		this.description = description;
		this.unit = unit;
		this.discountRate = discountRate;
		this.nInterventions = nInterventions;
		this.nPatients = simul.getnPatients();
		this.aggregated = new double[nInterventions];
		this.values = new double[nInterventions][nPatients];
	}

	/**
	 * Updates the value of this outcome for a specified period
	 * @param pat A patient
	 * @param value A constant value during the period
	 * @param initAge Initial age when the value is applied
	 * @param endAge End age when the value is applied
	 */
	public void update(Patient pat, double value, double initAge, double endAge) {
		final int interventionId = pat.getnIntervention();
		value = applyDiscount(value, initAge, endAge);
		values[interventionId][pat.getIdentifier()] += value;
		aggregated[interventionId] += value;
	}
	/**
	 * Updates the value of this outcome at a specified age
	 * @param pat A patient
	 * @param value The value to update
	 * @param age The age at which the value is applied
	 */
	public void update(Patient pat, double value, double age) {
		final int interventionId = pat.getnIntervention();
		value = applyPunctualDiscount(value, age);
		values[interventionId][pat.getIdentifier()] += value;
		aggregated[interventionId] += value;
	}
	
	public void print(boolean detailed, boolean units) {
		if (detailed) {
			for (int i = 0; i < nPatients; i++) {
				System.out.print("[" + i + "]\t");
				for (int j = 0; j < nInterventions; j++) {
					System.out.print(values[j][i] + (units ? (" " + unit) : "") + "\t");
				}
				System.out.println();
			}
		}
		System.out.println(this + " summary:");
		for (int j = 0; j < nInterventions; j++) {
			System.out.print(aggregated[j] / nPatients + (units ? (" " + unit) : "") + "\t");
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
	 * Returns average, standard deviation, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5% for each intervention
	 * @return An array with n t-uples {average, standard deviation, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5%}, 
	 * with n the number of interventions.  
	 */
	public double[][] getResults() {
		final double[][] results = new double[aggregated.length][6];
		for (int i = 0; i < results.length; i++) {
			final double avg = getAverage(i);
			final double sd = Statistics.stdDev(values[i], avg);
			final double[] ci = Statistics.normal95CI(avg, sd, nPatients);
			final double[] cip = get95CI(i, true);
			results[i] = new double[] {avg, sd, ci[0], ci[1], cip[0], cip[1]};
		}
		return results;
	}
	
	public double getAverage(int intervention) {
		return aggregated[intervention] / nPatients;		
	}
	
	public double getSD(int intervention) {
		return Statistics.stdDev(values[intervention], getAverage(intervention));
	}
	
	public double[] get95CI(int intervention, boolean percentile) {
		if (!percentile)
			return Statistics.normal95CI(getAverage(intervention), getSD(intervention), nPatients);
		final double[] ordered = Arrays.copyOf(values[intervention], nPatients);
		Arrays.sort(ordered);
		final int index = (int)Math.ceil(nPatients * 0.025);
		return new double[] {ordered[index - 1], ordered[nPatients - index]}; 
	}
	
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return description;
	}
}
