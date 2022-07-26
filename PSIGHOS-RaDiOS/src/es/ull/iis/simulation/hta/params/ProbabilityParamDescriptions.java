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
	SPECIFICTY("SPEC", "Specificity for", 1.0);
	
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
	
	public void addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue) {
		this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue);
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue) {
		this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue);
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue) {
		secParams.addProbParam(new SecondOrderParam(secParams, getParameterName(name), getParameterDescription(description), source, detValue));
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue, RandomVariate rnd) {
		this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue, rnd);
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue, RandomVariate rnd) {
		this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue, rnd);
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		secParams.addProbParam(new SecondOrderParam(secParams, getParameterName(name), getParameterDescription(description), source, detValue, rnd));
	}
}
