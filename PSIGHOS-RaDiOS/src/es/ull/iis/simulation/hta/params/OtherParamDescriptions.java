/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.NamedAndDescribed;
import simkit.random.RandomVariate;

/**
 * @author Iván Castilla
 *
 */
public enum OtherParamDescriptions implements DescribesParameter {
	END_AGE("END_AGE", "End age for", BasicConfigParams.DEF_MAX_AGE),
	INCREASED_MORTALITY_RATE("IMR", "Increased mortality rate for", 1.0),
	LIFE_EXPECTANCY_REDUCTION("LER", "Life expectancy reduction for", 0.0),
	ONSET_AGE("ONSET_AGE", "Onset age for", 0.0),
	RELATIVE_RISK("RR", "Relative risk of", 1.0),
	RESOURCE_USAGE("USAGE", "Use of", 1.0);

	private final String shortPrefix;
	private final String longPrefix;
	private final double defaultValue;

	/**
	 * 
	 */
	private OtherParamDescriptions(String shortPrefix, String longPrefix, double defaultValue) {
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
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue) {
		return this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue) {
		return this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue) {
		final ParameterDescription desc = new ParameterDescription(getParameterDescription(description), source);		
		return addParameter(secParams, name, desc, detValue, SecondOrderParamsRepository.ParameterType.OTHER);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed instance, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(secParams, instance.name(), instance.getDescription(), source, detValue, rnd);
	}
	
	public String  addParameter(SecondOrderParamsRepository secParams, NamedAndDescribed from, NamedAndDescribed to, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(secParams, DescribesParameter.getTransitionName(from, to), DescribesParameter.getTransitionDescription(from, to), source, detValue, rnd);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		final ParameterDescription desc = new ParameterDescription(getParameterDescription(description), source);		
		return addParameter(secParams, name, desc, detValue, rnd, SecondOrderParamsRepository.ParameterType.OTHER);
	}
	
	public String addParameter(SecondOrderParamsRepository secParams, Parameter param) {
		return addParameter(secParams, param, SecondOrderParamsRepository.ParameterType.OTHER);
	}
}
