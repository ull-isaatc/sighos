package es.ull.iis.simulation.hta.params;

import java.util.ArrayList;

import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A parameter that models second order uncertainty. These are the parameters that uses {@link SecondOrderParamsRepository} 
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderParam {
	/** Short name and identifier of the parameter */
	private final String name;
	/** Full description of the parameter */
	private final String description;
	/** The reference from which this parameter was estimated/taken */
	private final String source;
	/** The probability distribution that characterizes the uncertainty on the parameter */
	private final RandomVariate rnd;
	/** All the generated values for this parameter. Index 0 is the deterministic/expected value */
	protected final ArrayList<Double> generatedValues;

	/**
	 * Creates a second-order parameter
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public SecondOrderParam(String name, String description, String source, double detValue, RandomVariate rnd) {
		this.name = name;
		this.description = description;
		this.source = source;
		this.rnd = rnd;
		generatedValues = new ArrayList<>();
		generatedValues.add(0, detValue);
	}
	
	/**
	 * Creates a second-order parameter by specifying the random distribution in a string 
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rndFunction Random function name, one of the ...Variate that accepts {@link RandomVariateFactory}
	 * @param params Parameters of the random distribution
	 */
	public SecondOrderParam(String name, String description, String source, double detValue, String rndFunction, Object... params) {
		this(name, description, source, detValue, RandomVariateFactory.getInstance(rndFunction, params));
	}
	
	/**
	 * Creates a second-order parameter with no uncertainty
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 */
	public SecondOrderParam(String name, String description, String source, double detValue) {
		this(name, description, source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue));
	}

	/**
	 * Returns a value for the parameter: if id = 0, returns the expected value (base case); otherwise returns a random-generated value. 
	 * Always returns the same value for the same id.
	 * @param id The identifier of the value to return (0: deterministic; other: random value)
	 * @return if id = 0, returns the expected value (base case); otherwise returns a random-generated value
	 */
	public double getValue(int id) {
		return generatedValues.get(id);
	}

	/**
	 * Generates n random values for the parameter
	 * @param n Number of random values to generate
	 */
	public void generate(int n) {
		generatedValues.ensureCapacity(n);
		for (int i = 1; i < n; i++)
			generatedValues.set(i, rnd.generate());
	}
	
	/**
	 * Returns the short name and identifier of the parameter
	 * @return the short name and identifier of the parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the full description of the parameter
	 * @return the full description of the parameter
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the reference from which this parameter was estimated/taken
	 * @return the reference from which this parameter was estimated/taken
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Returns the generated value identified by id for this parameter. Useful for printing
	 * @return the generated value identified by id for this parameter. Useful for printing
	 */
	public double getGeneratedValue(int id) {
		return generatedValues.get(id);
	}
}