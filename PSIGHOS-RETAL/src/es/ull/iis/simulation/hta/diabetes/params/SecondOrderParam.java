package es.ull.iis.simulation.hta.diabetes.params;

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
	/** Deterministic/expected value */
	private final double detValue;
	/** The probability distribution that characterizes the uncertainty on the parameter */
	private final RandomVariate rnd;
	/** The last generated value for this parameter. Useful for printing */
	protected double lastGeneratedValue;

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
		this.detValue = detValue;
		this.rnd = rnd;
		lastGeneratedValue = Double.NaN;
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
	 * Returns a random-generated value for the parameter; the expected value if baseCase is true
	 * @param baseCase If true, returns the expected value; otherwise returns a random-generated value
	 * @return a random-generated value for the parameter; the expected value if baseCase is true
	 */
	public double getValue(boolean baseCase) {
		lastGeneratedValue = baseCase ? detValue : rnd.generate();
		return lastGeneratedValue;
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
	 * Returns the last generated value for this parameter. Useful for printing
	 * @return the last generated value for this parameter. Useful for printing
	 */
	public double getLastGeneratedValue() {
		return lastGeneratedValue;
	}
}