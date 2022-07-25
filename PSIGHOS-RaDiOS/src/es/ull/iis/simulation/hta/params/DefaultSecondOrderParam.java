/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public enum DefaultSecondOrderParam {
	COST("C", "Cost for"),
	DISUTILITY("DU", "Disutility for"),
	INCREASED_MORTALITY_RATE("IMR", "Increased mortality rate for"),
	INITIAL_PROBABILITY("P_INIT", "Initial probability of"),
	LIFE_EXPECTANCY_REDUCTION("LER", "Life expectancy reduction for"),
	MODIFICATION("MOD", "Modification of parameter"),
	ONE_TIME_COST("TC", "One-time cost for"),
	ONE_TIME_DISUTILITY("TDU", "One-time disutility for"),
	ONE_TIME_UTILITY("TU", "One-time utility for"),
	PROBABILITY("P", "Probability of"),
	PROBABILITY_DEATH("P_DEATH", "Probability of death due to"),
	PROBABILITY_DIAGNOSIS("P_DIAG", "Probability of diagnosis from"),
	RELATIVE_RISK("RR", "Relative risk of"),
	SENSITIVITY("SENS", "Sensitivity for"),
	SPECIFICTY("SPEC", "Specificity for"),
	UTILITY("U", "Utility for");
	
	private final static String SHORT_LINK = "_";
	private final static String LONG_LINK = " ";
	private final String shortPrefix;
	private final String longPrefix;

	/**
	 * 
	 */
	private DefaultSecondOrderParam(String shortPrefix, String longPrefix) {
		this.shortPrefix = shortPrefix + SHORT_LINK;
		this.longPrefix = longPrefix + LONG_LINK;
	}
	
	public String getName(Named instance) {
		return shortPrefix + instance.name();
	}

	
	public String getDescription(Describable instance) {
		return longPrefix + instance.getDescription();
	}
}
