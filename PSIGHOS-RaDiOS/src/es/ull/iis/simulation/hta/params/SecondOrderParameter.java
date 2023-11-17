/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderParameter extends Parameter {
	/** The probability distribution that characterizes the uncertainty on the parameter */
	protected final RandomVariate rnd;
	/** All the generated values for this parameter. Index 0 is the deterministic/expected value */
	protected final double[] generatedValues;

	/**
	 * 
	 */
	public SecondOrderParameter(final SecondOrderParamsRepository secParams, DescribesParameter type, String name, String description, String source, double detValue, RandomVariate rnd) {
		super(secParams, type, name, description, source);
		this.rnd = rnd;
		if (rnd == null)
			throw new IllegalArgumentException("rnd cannot be null");
		generatedValues = new double[secParams.getNRuns() + 1];
		Arrays.fill(generatedValues, Double.NaN);
		generatedValues[0] = detValue;
	}

	public SecondOrderParameter(final SecondOrderParamsRepository secParams, DescribesParameter type, String name, String description, String source, double detValue, String rndFunction, Object... params) {
		this(secParams, type, name, description, source, detValue, RandomVariateFactory.getInstance(rndFunction, params));		
	}

	@Override
	public double calculateValue(Patient pat) {
		return getValue(pat.getSimulation().getIdentifier());
	}

	/**
	 * Returns a value for the parameter: if id = 0, returns the expected value (base case); otherwise returns a random-generated value. 
	 * Always returns the same value for the same id.
	 * @param id The identifier of the value to return (0: deterministic; other: random value)
	 * @return if id = 0, returns the expected value (base case); otherwise returns a random-generated value
	 */
	public double getValue(int id) {
		if (Double.isNaN(generatedValues[id]))
			generatedValues[id] = rnd.generate();
		return generatedValues[id];
	}

}