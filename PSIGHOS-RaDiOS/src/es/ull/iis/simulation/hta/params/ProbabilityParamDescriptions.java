/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import simkit.random.RandomVariate;

/**
 * Common probability-like parameters defined within the repository 
 * @author Iván Castilla Rodríguez
 *
 */
public enum ProbabilityParamDescriptions implements DescribesParameter {
	BIRTH_PREVALENCE("BIRTH_PREVALENCE", "Birth prevalence of", 0.0),
	INITIAL_PROBABILITY("P_INIT", "Probability of starting with", 0.0),
	INITIAL_PROPORTION("P_INIT", "Proportion starting with", 0.0),
	PREVALENCE("PREVALENCE", "Prevalence of", 0.0),
	PROBABILITY("P", "Probability of", 0.0),
	PROBABILITY_DEATH("P_DEATH", "Probability of death due to", 0.0),
	PROBABILITY_DIAGNOSIS("P_DIAG", "Probability of diagnosis from", 0.0),
	PROPORTION("P", "Proportion of", 0.0),
	SENSITIVITY("SENS", "Sensitivity for", 1.0),
	SPECIFICITY("SPEC", "Specificity for", 1.0);
	
	private final String shortPrefix;
	private final String longPrefix;
	private final double defaultValue;

	/**
	 * 
	 */
	private ProbabilityParamDescriptions(String shortPrefix, String longPrefix, double defaultValue) {
		this.shortPrefix = shortPrefix + SHORT_LINK;
		this.longPrefix = longPrefix + LONG_LINK;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String getShortPrefix() {
		return shortPrefix;
	}

	@Override
	public String getLongPrefix() {
		return longPrefix;
	}

	@Override
	public double getParameterDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public double getValue(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul) {
		return secParams.getProbParam(getParameterName(name), defaultValue, simul);
	}

	@Override
	public double getValueIfExists(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul) {
		return secParams.getProbParam(getParameterName(name), simul);
	}
	
	/**
	 * Adds a probability parameter with no uncertainty to the repository, and returns the name assigned to the parameter in the repository
	 * @param secParams Common parameters repository
	 * @param instance An object that defines both a name and a description for the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic (constant) value of the parameter
	 * @return The name assigned to the parameter in the repository
	 */
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue) {
		return this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue);
	}
	
	/**
	 * Adds a probability parameter with no uncertainty that describes a transition to the repository, and returns the name assigned to the parameter in the repository
	 * @param secParams Common parameters repository
	 * @param from An object that defines both a name and a description for the start of the transition 
	 * @param to An object that defines both a name and a description for the destination of the transition
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic (constant) value of the parameter
	 * @return The name assigned to the parameter in the repository
	 */
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue) {
		return this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue);
	}
	
	/**
	 * Adds a probability parameter with no uncertainty to the repository, and returns the name assigned to the parameter in the repository
	 * @param secParams Common parameters repository
	 * @param name The name of the parameter (which will be converted into the parameter name using the {@link #getParameterName(String)} method
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic (constant) value of the parameter
	 * @return The name assigned to the parameter in the repository
	 */
	public String addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue) {
		final String paramName = getParameterName(name);
		secParams.addProbParam(new SecondOrderParam(secParams, paramName, getParameterDescription(description), source, detValue));
		return paramName;
	}
	
	/**
	 * Adds a probability parameter with uncertainty to the repository, and returns the name assigned to the parameter in the repository
	 * @param secParams Common parameters repository
	 * @param instance An object that defines both a name and a description for the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic (constant) value of the parameter
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @return The name assigned to the parameter in the repository
	 */
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue, rnd);
	}

	/**
	 * Adds a probability parameter with uncertainty that describes a transition to the repository, and returns the name assigned to the parameter in the repository
	 * @param secParams Common parameters repository
	 * @param from An object that defines both a name and a description for the start of the transition 
	 * @param to An object that defines both a name and a description for the destination of the transition
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic (constant) value of the parameter
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @return The name assigned to the parameter in the repository
	 */
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue, rnd);
	}
	
	/**
	 * Adds a probability parameter with uncertainty to the repository, and returns the name assigned to the parameter in the repository
	 * @param secParams Common parameters repository
	 * @param name Name of the parameter to be appended after the predefined prefix
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic (constant) value of the parameter
	 * @param rnd The probability distribution that characterizes the uncertainty on the parameter
	 * @return The name assigned to the parameter in the repository
	 */	
	public String addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		final String paramName = getParameterName(name);
		secParams.addProbParam(new SecondOrderParam(secParams, paramName, getParameterDescription(description), source, detValue, rnd));
		return paramName;
	}
}
