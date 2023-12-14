package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A parameter that returns always the same value for each patient, independently on the simulation  
 * @author Iván Castilla Rodríguez
 *
 */
public class FirstOrderNatureParameter extends Parameter {
    /** The expression or probability distribution that characterizes the uncertainty on the parameter */
	private final ParameterExpression expression;
	/** All the generated values for this parameter. Index 0 is the deterministic/expected value */
	private final double[] generatedValues;

	/**
	 * 
	 */
	public FirstOrderNatureParameter(final SecondOrderParamsRepository secParams, String name, ParameterDescription desc, ParameterExpression expression) {
        super(secParams, name, desc);
		this.expression = expression;
		if (expression == null)
			throw new IllegalArgumentException("expression cannot be null");
		generatedValues = new double[secParams.getNPatients()];
		Arrays.fill(generatedValues, Double.NaN);
	}
	/**
	 * 
	 */
	public FirstOrderNatureParameter(final SecondOrderParamsRepository secParams, String name, ParameterDescription desc, RandomVariate rnd) {
		this(secParams, name, desc, new RandomParameterExpression(rnd));
	}

	public FirstOrderNatureParameter(final SecondOrderParamsRepository secParams, String name, ParameterDescription desc, String rndFunction, Object... params) {
		this(secParams, name, desc, new RandomParameterExpression(RandomVariateFactory.getInstance(rndFunction, params)));		
	}

	@Override
	public double getValue(Patient pat) {
		final int id = pat.getIdentifier();
		if (Double.isNaN(generatedValues[id]))
			generatedValues[id] = expression.getValue(pat);
		return generatedValues[id];
	}

}
