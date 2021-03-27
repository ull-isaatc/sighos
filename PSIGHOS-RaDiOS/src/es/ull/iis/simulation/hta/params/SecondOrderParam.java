package es.ull.iis.simulation.hta.params;

import java.util.Arrays;

import es.ull.iis.simulation.hta.progression.Modification;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * A parameter that models second order uncertainty. These are the parameters that uses {@link SecondOrderParamsRepository} 
 * @author Iván Castilla Rodríguez
 *
 */
public class SecondOrderParam {
	/** Common parameters repository */
	protected final SecondOrderParamsRepository secParams;
	/** Short name and identifier of the parameter */
	private final String name;
	/** Full description of the parameter */
	private final String description;
	/** The reference from which this parameter was estimated/taken */
	private final String source;
	/** The probability distribution that characterizes the uncertainty on the parameter */
	protected final RandomVariate rnd;
	/** All the generated values for this parameter. Index 0 is the deterministic/expected value */
	protected final double[] generatedValues;

	/**
	 * Creates a second-order parameter
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 */
	public SecondOrderParam(final SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		this.secParams = secParams;
		this.name = name;
		this.description = description;
		this.source = source;
		this.rnd = rnd;
		generatedValues = new double[secParams.getnRuns() + 1];
		Arrays.fill(generatedValues, Double.NaN);
		generatedValues[0] = detValue;
	}
	
	/**
	 * Creates a second-order parameter by specifying the random distribution in a string 
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param rndFunction Random function name, one of the ...Variate that accepts {@link RandomVariateFactory}
	 * @param params Parameters of the random distribution
	 */
	public SecondOrderParam(final SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, String rndFunction, Object... params) {
		this(secParams, name, description, source, detValue, RandomVariateFactory.getInstance(rndFunction, params));
	}
	
	/**
	 * Creates a second-order parameter with no uncertainty
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 */
	public SecondOrderParam(final SecondOrderParamsRepository secParams, String name, String description, String source, double detValue) {
		this(secParams, name, description, source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue));
	}

	/**
	 * Returns a value for the parameter: if id = 0, returns the expected value (base case); otherwise returns a random-generated value. 
	 * Always returns the same value for the same id.
	 * @param id The identifier of the value to return (0: deterministic; other: random value)
	 * @return if id = 0, returns the expected value (base case); otherwise returns a random-generated value
	 */
	public double getValue(int id) {
		if (Double.isNaN(generatedValues[id]))
			generatedValues[id] = rnd.generate(); // FIXME: if the rnd function is null it gives a NullPointerException
		return generatedValues[id];
	}

	public String getValuesAsString () {
		StringBuilder sb = new StringBuilder();
		if (!Double.isNaN(generatedValues[0])) {
			sb.append(generatedValues[0]).append(" :::");	
		}			
		sb.append(" ").append(rnd.toString()).append(" ");	
		return sb.toString();		
	}
	
	/**
	 * Returns a value for the parameter, modified according to a {@link Modification}
	 * Always returns the same value for the same id.
	 * @param id The identifier of the value to return (0: deterministic; other: random value)
	 * @param modif A modification that affects the value
	 * @return if id = 0, returns the expected value (base case); otherwise returns a random-generated value
	 */
	public double getValue(int id, Modification modif) {
		double value = getValue(id);
		switch(modif.getType()) {
		case DIFF:
			value -= modif.getValue(id);
			break;
		case RR:
			value *= modif.getValue(id);
			break;
		case SET:
			value = modif.getValue(id);
			break;
		default:
			break;		
		}
		return value;
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

}