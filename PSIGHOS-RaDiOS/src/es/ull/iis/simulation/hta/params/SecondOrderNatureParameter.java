package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * 
 * @author Iván Castilla Rodríguez
*/
public class SecondOrderNatureParameter extends Parameter {
    /** The expression or probability distribution that characterizes the uncertainty on the parameter */
	private final ParameterExpression expression;
	/** All the generated values for this parameter. Index 0 is the deterministic/expected value */
	private final double[] generatedValues;

    public SecondOrderNatureParameter(HTAModel model, String name, String description, String source, int year, ParameterType type, double detValue, ParameterExpression expression) {
        super(name, description, source, year, type);
		this.expression = expression;
		if (expression == null)
			throw new IllegalArgumentException("expression cannot be null");
		generatedValues = new double[model.getExperiment().getNRuns() + 1];
		Arrays.fill(generatedValues, Double.NaN);
		generatedValues[0] = detValue;
    }

    public SecondOrderNatureParameter(HTAModel model, String name, String description, String source, int year, ParameterType type, double detValue, RandomVariate rnd) {
		this(model, name, description, source, year, type, detValue, new RandomParameterExpression(rnd));
	}

    public SecondOrderNatureParameter(HTAModel model, String name, String description, String source, int year, ParameterType type, double detValue, String rndFunction, Object... params) {
        this(model, name, description, source, year, type, detValue, new RandomParameterExpression(RandomVariateFactory.getInstance(rndFunction, params)));
    }

	@Override
	public double getValue(Patient pat) {
		final int id = pat.getSimulation().getIdentifier();
		if (Double.isNaN(generatedValues[id]))
			generatedValues[id] = expression.getValue(pat);
		return generatedValues[id];
	}

	/**
	 * Returns a value for the parameter: if id = 0, returns the expected value (base case); otherwise returns a random-generated value. 
	 * Always returns the same value for the same id.
	 * @param id The identifier of the value to return (0: deterministic; other: random value)
	 * @return if id = 0, returns the expected value (base case); otherwise returns a random-generated value
	 */
	public double getValue(int id) {
		return generatedValues[id];
	}
}
