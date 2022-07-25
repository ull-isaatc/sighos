/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public enum DefaultProbabilitySecondOrderParam implements DescribesSecondOrderParameter {
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
	private DefaultProbabilitySecondOrderParam(String shortPrefix, String longPrefix, double defaultValue) {
		this.shortPrefix = shortPrefix + SHORT_LINK;
		this.longPrefix = longPrefix + LONG_LINK;
		this.defaultValue = defaultValue;
	}
	
	public String getShortPrefix() {
		return shortPrefix;
	}

	public String getLongPrefix() {
		return longPrefix;
	}

	/**
	 * @return the defaultValue
	 */
	public double getParameterDefaultValue() {
		return defaultValue;
	}
	
	public double getValue(SecondOrderParamsRepository secParams, String name, DiseaseProgressionSimulation simul) {
		return secParams.getProbParam(getParameterName(name), defaultValue, simul);
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, Named instance, Describable description, String source, double detValue) {
		this.addParameter(secParams, instance.name(), description.getDescription(), source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue));
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue) {
		secParams.addProbParam(new SecondOrderParam(secParams, getParameterName(name), getParameterDescription(description), source, detValue, RandomVariateFactory.getInstance("ConstantVariate", detValue)));
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, Named instance, Describable description, String source, double detValue, RandomVariate rnd) {
		this.addParameter(secParams, instance.name(), description.getDescription(), source, detValue, rnd);
	}
	
	public void addParameter(SecondOrderParamsRepository secParams, String name, String description, String source, double detValue, RandomVariate rnd) {
		secParams.addProbParam(new SecondOrderParam(secParams, getParameterName(name), getParameterDescription(description), source, detValue, rnd));
	}
}
