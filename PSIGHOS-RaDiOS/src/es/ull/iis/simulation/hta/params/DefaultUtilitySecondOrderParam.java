/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public enum DefaultUtilitySecondOrderParam implements DescribesSecondOrderParameter {
	DISUTILITY("DU", "Disutility for", 0.0),
	ONE_TIME_DISUTILITY("TDU", "One-time disutility for", 0.0),
	ONE_TIME_UTILITY("TU", "One-time utility for", 1.0),
	UTILITY("U", "Utility for", 1.0);
	
	private final String shortPrefix;
	private final String longPrefix;
	private final double defaultValue;

	/**
	 * 
	 */
	private DefaultUtilitySecondOrderParam(String shortPrefix, String longPrefix, double defaultValue) {
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

	public String getParameterName(Named instance) {
		return getParameterName(instance.name());
	}

	
	public String getParameterDescription(Describable instance) {
		return getParameterDescription(instance.getDescription());
	}

	
	public String getParameterName(String name) {
		return shortPrefix + name;
	}

	
	public String getParameterDescription(String description) {
		return longPrefix + description;
	}

	/**
	 * @return the defaultValue
	 */
	public double getParameterDefaultValue() {
		return defaultValue;
	}
	
	public double getValue(SecondOrderParamsRepository secParams, Named instance, DiseaseProgressionSimulation simul) {
		return getValue(secParams, instance.name(), simul);
	}
	
	public double getValue(SecondOrderParamsRepository secParams, String instance, DiseaseProgressionSimulation simul) {
		return secParams.getUtilityParam(getParameterName(instance), defaultValue, simul);
	}
}
