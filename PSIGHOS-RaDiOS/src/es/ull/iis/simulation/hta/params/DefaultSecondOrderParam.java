/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository.ParameterType;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla
 *
 */
public enum DefaultSecondOrderParam {
	COST(ParameterType.COST, "C", "Cost for", 0.0),
	DISUTILITY(ParameterType.UTILITY, "DU", "Disutility for", 0.0),
	INCREASED_MORTALITY_RATE(ParameterType.OTHER, "IMR", "Increased mortality rate for", 1.0),
	INITIAL_PROBABILITY(ParameterType.PROBABILITY, "P_INIT", "Initial probability of", 0.0),
	LIFE_EXPECTANCY_REDUCTION(ParameterType.OTHER, "LER", "Life expectancy reduction for", 0.0),
	ONE_TIME_COST(ParameterType.COST, "TC", "One-time cost for", 0.0),
	ONE_TIME_DISUTILITY(ParameterType.UTILITY, "TDU", "One-time disutility for", 0.0),
	ONE_TIME_UTILITY(ParameterType.UTILITY, "TU", "One-time utility for", 1.0),
	PROBABILITY(ParameterType.PROBABILITY, "P", "Probability of", 0.0),
	PROBABILITY_DEATH(ParameterType.PROBABILITY, "P_DEATH", "Probability of death due to", 0.0),
	PROBABILITY_DIAGNOSIS(ParameterType.PROBABILITY, "P_DIAG", "Probability of diagnosis from", 0.0),
	RELATIVE_RISK(ParameterType.OTHER, "RR", "Relative risk of", 1.0),
	SENSITIVITY(ParameterType.PROBABILITY, "SENS", "Sensitivity for", 1.0),
	SPECIFICTY(ParameterType.PROBABILITY, "SPEC", "Specificity for", 1.0),
	UNIT_COST(ParameterType.COST, "C_UNIT", "Unit cost for", 0.0),
	UTILITY(ParameterType.UTILITY, "U", "Utility for", 1.0);
	
	private final static String SHORT_LINK = "_";
	private final static String LONG_LINK = " ";
	private final String shortPrefix;
	private final String longPrefix;
	private final double defaultValue;
	private final ParameterType type;

	/**
	 * 
	 */
	private DefaultSecondOrderParam(ParameterType type, String shortPrefix, String longPrefix, double defaultValue) {
		this.type = type;
		this.shortPrefix = shortPrefix + SHORT_LINK;
		this.longPrefix = longPrefix + LONG_LINK;
		this.defaultValue = defaultValue;
	}
	
	public String getName(Named instance) {
		return shortPrefix + instance.name();
	}

	
	public String getDescription(Describable instance) {
		return longPrefix + instance.getDescription();
	}

	
	public String getName(String name) {
		return shortPrefix + name;
	}

	
	public String getDescription(String description) {
		return longPrefix + description;
	}

	/**
	 * @return the type
	 */
	public ParameterType getType() {
		return type;
	}

	/**
	 * @return the defaultValue
	 */
	public double getDefaultValueForParameter() {
		return defaultValue;
	}
//	
//	public double getValue(SecondOrderParamsRepository secParams, Named name, int id) {
//		switch(type) {
//		case COST:
//			secParams.getCostParam()
//			break;
//		case OTHER:
//			break;
//		case PROBABILITY:
//			break;
//		case UTILITY:
//			break;
//		default:
//			break;
//		
//		}
//	}
}
