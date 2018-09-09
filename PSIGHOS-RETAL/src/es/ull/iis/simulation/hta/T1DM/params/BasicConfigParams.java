/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * Basic configuration parameters for the simulation study.
 * @author Iván Castilla Rodríguez
 *
 */
public class BasicConfigParams {
	/** Simulation time unit: defines the finest grain */
	public final static TimeUnit SIMUNIT = TimeUnit.DAY;
	/** The factor to expressed a simulation timestamp in years */ 
	public final static double YEAR_CONVERSION = SIMUNIT.convert(TimeStamp.getYear());
	/** Identifier code for men */
	public final static int MAN = 0;
	/** Identifier code for women */
	public final static int WOMAN = 1;
	/** Minimum time among consecutive events. */  
	public final static long MIN_TIME_TO_EVENT = SIMUNIT.convert(TimeStamp.getMonth());

	public static int N_RUNS = 100;
	public static int N_PATIENTS = 5000;
	/** Year of the assessment: useful for updating costs */
	public static int STUDY_YEAR = 2018;
	/** Minimum age of patients */
	public static int MIN_AGE = 18;
	/** Maximum age reachable by patients */
	public static int MAX_AGE = 100;
	/** If true, uses the utilities from the revision of Beaudet et al. 2014 */
	public static boolean USE_REVIEW_UTILITIES = true;
	/** If true, uses the simplest models for diseases */
	public static boolean USE_SIMPLE_MODELS = false;
	/** If true, all the patients start with the same age; otherwise, uses a probability distribution to assign ages */ 
	public static boolean USE_FIXED_BASELINE_AGE = false;
	/** If true, all the patients start with the same level of HbA1c; otherwise, uses a probability distribution to assign the level */ 
	public static boolean USE_FIXED_BASELINE_HBA1C = false;
	/** Default utility for general population: From adult Spanish population but those with DM */ 
	public static double DEF_U_GENERAL_POP = 0.911400915;
	/** Default cost for diabetes with no complications. From Crespo et al. 2013 */
	public static class DEF_C_DNC {
		/** Value computed by substracting the burden of complications from the global burden of DM1 in Spain; 
		 * finally divided by the prevalent DM1 population */
		public final static double VALUE = (5809000000d - 2143000000d) / 3282790d;
		/** The year of the default cost for diabetes with no complications: for updating with IPC */
		public final static int YEAR = 2012; 
		/** Description of the source */
		public final static String SOURCE = "Crespo et al. 2012: http://dx.doi.org/10.1016/j.avdiab.2013.07.007";
	}
	/** Default second order variation for different parameter types */
	public static class DEF_SECOND_ORDER_VARIATION {
		public final static double COST = 0.2;
		public final static double UTILITY = 0.2;
		public final static double PROBABILITY = 0.5;
	}
	public static final double[] DEF_DU_DNC = {USE_REVIEW_UTILITIES ? (DEF_U_GENERAL_POP - 0.785) : 0.0351, USE_REVIEW_UTILITIES ? ((0.889 - 0.681) / 3.92) : 0.0001};

	
	/**
	 * This constructor should not be needed.
	 */
	private BasicConfigParams() {
	}
	
	public static String printOptions() {
		final StringBuilder str = new StringBuilder("Basic Configuration Options:");
		str.append(System.lineSeparator()).append("SIMUNIT:\t").append(SIMUNIT).append(System.lineSeparator());
		str.append("MIN_TIME_TO_EVENT:\t").append(MIN_TIME_TO_EVENT).append(System.lineSeparator());
		str.append("N_RUNS:\t").append(N_RUNS).append(System.lineSeparator());
		str.append("N_PATIENTS:\t").append(N_PATIENTS).append(System.lineSeparator());
		str.append("STUDY_YEAR:\t").append(STUDY_YEAR).append(System.lineSeparator());
		str.append("MIN_AGE:\t").append(MIN_AGE).append(System.lineSeparator());
		str.append("MAX_AGE:\t").append(MAX_AGE).append(System.lineSeparator());
		str.append("USE_REVIEW_UTILITIES:\t").append(USE_REVIEW_UTILITIES).append(System.lineSeparator());
		str.append("USE_SIMPLE_MODELS:\t").append(USE_SIMPLE_MODELS).append(System.lineSeparator());
		str.append("USE_FIXED_BASELINE_AGE:\t").append(USE_FIXED_BASELINE_AGE).append(System.lineSeparator());
		str.append("USE_FIXED_BASELINE_HBA1C:\t").append(USE_FIXED_BASELINE_HBA1C).append(System.lineSeparator());
		str.append("DEF_U_GENERAL_POP:\t").append(DEF_U_GENERAL_POP).append(System.lineSeparator());
		str.append("DEF_C_DNC:\t").append(DEF_C_DNC.VALUE + " (" + DEF_C_DNC.YEAR + ")").append(System.lineSeparator());
		str.append("DEF_SECOND_ORDER_VARIATION (COST, UTIL, PROB):\t").append(DEF_SECOND_ORDER_VARIATION.COST+"\t").append(DEF_SECOND_ORDER_VARIATION.UTILITY+"\t").append(DEF_SECOND_ORDER_VARIATION.PROBABILITY).append(System.lineSeparator());
		return str.toString();
	}

}
