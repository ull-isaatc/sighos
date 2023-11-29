/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A parameter calculator that returns always the same value for each patient, independently on the simulation  
 * @author Iván Castilla Rodríguez
 *
 */
public class FirstOrderParameterCalculator implements ParameterCalculator {
	/** The probability distribution that characterizes the uncertainty on the parameter */
	private final RandomVariate rnd;
	/** All the generated values for this parameter. Index 0 is the deterministic/expected value */
	private final double[] generatedValues;

	/**
	 * 
	 */
	public FirstOrderParameterCalculator(final SecondOrderParamsRepository secParams, RandomVariate rnd) {
		this.rnd = rnd;
		if (rnd == null)
			throw new IllegalArgumentException("rnd cannot be null");
		generatedValues = new double[secParams.getNPatients()];
		Arrays.fill(generatedValues, Double.NaN);
	}

	public FirstOrderParameterCalculator(final SecondOrderParamsRepository secParams, String rndFunction, Object... params) {
		this(secParams, RandomVariateFactory.getInstance(rndFunction, params));		
	}

	@Override
	public double getValue(Patient pat) {
		final int id = pat.getIdentifier();
		if (Double.isNaN(generatedValues[id]))
			generatedValues[id] = rnd.generate();
		return generatedValues[id];
	}
}
