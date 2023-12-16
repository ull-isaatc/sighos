/**
 * 
 */
package es.ull.iis.simulation.hta.params;

/**
 * Basic configuration parameters for the simulation study.
 * @author Iván Castilla Rodríguez
 *
 */
public class BasicConfigParams {
	public final static String STR_SEP = "----------------------------------------------------------------------------------------------------------------";
	
	/** Identifier code for men */
	public final static int MAN = 0;
	/** Identifier code for women */
	public final static int WOMAN = 1;

	/** Default number of runs for each simulation experiment */
	public final static int N_RUNS = 100;
	/** Default number of patients that will be created during a simulation */
	public final static int DEF_N_PATIENTS = 5000;
	/** Default minimum age of patients */
	public final static int DEF_MIN_AGE = 18;
	// TODO: Check the usage of this parameter. In most places, it should use the value for the population and not this maximum
	/** Maximum age reachable by patients */
	public final static int DEF_MAX_AGE = 100;
	/** Default discount rate (3% according to Spanish guidelines) */
	public final static double DEF_DISCOUNT_RATE = 0.03;
	/** If true, all the patients start with the same age; otherwise, uses a probability distribution to assign ages */ 
	public static boolean USE_FIXED_BASELINE_AGE = false;
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	public static double DEF_U_GENERAL_POP = 0.911400915;
	/** Default second order variation for different parameter types */
	public static class DEF_SECOND_ORDER_VARIATION {
		public final static double COST = 0.2;
		public final static double UTILITY = 0.2;
		public final static double PROBABILITY = 0.5;
	}
	/** Years for budget impact (in case it is enabled) */
	public final static int DEF_BI_YEARS = 10;

	
	/**
	 * This constructor should not be needed ever.
	 */
	private BasicConfigParams() {
	}
	
	/**
	 * Creates a string with the list of parameters included in this configuration class, and the corresponding value
	 * for each parameter
	 * @return a string with the list of parameters included in this configuration class, and the corresponding value
	 * for each parameter
	 */
	public static String printOptions() {
		final StringBuilder str = new StringBuilder("Basic Configuration Options:").append(System.lineSeparator());
		str.append("N_RUNS:\t").append(N_RUNS).append(System.lineSeparator());
		str.append("N_PATIENTS:\t").append(DEF_N_PATIENTS).append(System.lineSeparator());
		str.append("MIN_AGE:\t").append(DEF_MIN_AGE).append(System.lineSeparator());
		str.append("MAX_AGE:\t").append(DEF_MAX_AGE).append(System.lineSeparator());
		str.append("USE_FIXED_BASELINE_AGE:\t").append(USE_FIXED_BASELINE_AGE).append(System.lineSeparator());
		str.append("DEF_DISCOUNT_RATE:\t").append(DEF_DISCOUNT_RATE).append(System.lineSeparator());
		str.append("DEF_U_GENERAL_POP:\t").append(DEF_U_GENERAL_POP).append(System.lineSeparator());
		str.append("DEF_SECOND_ORDER_VARIATION (COST, UTIL, PROB):\t").append(DEF_SECOND_ORDER_VARIATION.COST+"\t").append(DEF_SECOND_ORDER_VARIATION.UTILITY+"\t").append(DEF_SECOND_ORDER_VARIATION.PROBABILITY).append(System.lineSeparator());
		return str.toString();
	}

}
