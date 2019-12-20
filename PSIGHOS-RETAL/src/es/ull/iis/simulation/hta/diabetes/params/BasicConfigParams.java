/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * Basic configuration parameters for the simulation study.
 * @author Iván Castilla Rodríguez
 *
 */
public class BasicConfigParams {
	public final static String STR_SEP = "----------------------------------------------------------------------------------------------------------------";
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

	/** Default number of runs for each simulation experiment */
	public final static int N_RUNS = 100;
	/** Default number of patients that will be created during a simulation */
	public final static int DEF_N_PATIENTS = 5000;
	/** Year of the assessment: useful for updating costs */
	public static int STUDY_YEAR = 2019;
	/** Default minimum age of patients */
	public final static int DEF_MIN_AGE = 18;
	/** Maximum age reachable by patients */
	public final static int DEF_MAX_AGE = 100;
	public final static double DEFAULT_SBP = 110;
	public final static double DEFAULT_P_SMOKER = 0.2;
	public final static double DEFAULT_P_ATRIAL_FIB = 0.04;
	public final static double DEFAULT_LIPID_RATIO = 3;
	/** Default discount rate (3% according to Spanish guidelines) */
	public final static double DEF_DISCOUNT_RATE = 0.03;
	/** If true, uses the utilities from the revision of Beaudet et al. 2014 */
	public static boolean USE_REVIEW_UTILITIES = true;
	/** If true, uses the simplest models for diseases */
	public static boolean USE_SIMPLE_MODELS = false;
	/** If true, uses the CHD submodel that includes death by the acute events */
	public static boolean USE_CHD_DEATH_MODEL = true;
	/** If true, all the patients start with the same age; otherwise, uses a probability distribution to assign ages */ 
	public static boolean USE_FIXED_BASELINE_AGE = false;
	/** If true, all the patients start with the same duration of diabetes; otherwise, uses a probability distribution to assign the duration */ 
	public static boolean USE_FIXED_BASELINE_DURATION_OF_DIABETES = false;
	/** If true, all the patients start with the same level of HbA1c; otherwise, uses a probability distribution to assign the level */ 
	public static boolean USE_FIXED_BASELINE_HBA1C = false;
	/** If true, the change in HbA1c for all the patients is fixed; otherwise, uses a probability distribution to assign the level */ 
	public static boolean USE_FIXED_HBA1C_CHANGE = false;
	/** If true, uses the calibrated progression to BGRET equations in the Sheffield submodel; otherwise, uses the original values */
	public static boolean USE_CALIBRATED_SHEFFIELD_RET_SUBMODEL = false;
	/** The first calibration coefficient to BGRET equations in the Sheffield submodel */
	public static double CALIBRATION_COEF_BGRET = BasicConfigParams.USE_CALIBRATED_SHEFFIELD_RET_SUBMODEL ? 7.0 : 1.0;
	/** The second calibration coefficient to BGRET equations in the Sheffield submodel */
	public static double CALIBRATION_COEF_BETA_BGRET = BasicConfigParams.USE_CALIBRATED_SHEFFIELD_RET_SUBMODEL ? 1.5 : 1.0;
	/** Initial proportions for complication stages */
	public static Map<String, Double> INIT_PROP = new TreeMap<>();
	/** Uses the arm-specific probabilities of severe hypoglycemic event in the base case; otherwise, uses the aggregated value */
	public static boolean ENABLE_BATTELINO_HYPO_SCENARIO_1 = false;
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
	public final static double[] DEF_DU_DNC = {USE_REVIEW_UTILITIES ? (DEF_U_GENERAL_POP - 0.785) : 0.0351, USE_REVIEW_UTILITIES ? ((0.889 - 0.681) / 3.92) : 0.0001};

	
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
		final StringBuilder str = new StringBuilder("Basic Configuration Options:");
		str.append(System.lineSeparator()).append("SIMUNIT:\t").append(SIMUNIT).append(System.lineSeparator());
		str.append("MIN_TIME_TO_EVENT:\t").append(MIN_TIME_TO_EVENT).append(System.lineSeparator());
		str.append("N_RUNS:\t").append(N_RUNS).append(System.lineSeparator());
		str.append("N_PATIENTS:\t").append(DEF_N_PATIENTS).append(System.lineSeparator());
		str.append("STUDY_YEAR:\t").append(STUDY_YEAR).append(System.lineSeparator());
		str.append("MIN_AGE:\t").append(DEF_MIN_AGE).append(System.lineSeparator());
		str.append("MAX_AGE:\t").append(DEF_MAX_AGE).append(System.lineSeparator());
		str.append("USE_REVIEW_UTILITIES:\t").append(USE_REVIEW_UTILITIES).append(System.lineSeparator());
		str.append("USE_SIMPLE_MODELS:\t").append(USE_SIMPLE_MODELS).append(System.lineSeparator());
		str.append("USE_CHD_DEATH_MODEL:\t").append(USE_CHD_DEATH_MODEL).append(System.lineSeparator());
		str.append("USE_FIXED_BASELINE_AGE:\t").append(USE_FIXED_BASELINE_AGE).append(System.lineSeparator());
		str.append("USE_FIXED_BASELINE_HBA1C:\t").append(USE_FIXED_BASELINE_HBA1C).append(System.lineSeparator());
		str.append("USE_FIXED_HBA1C_CHANGE:\t").append(USE_FIXED_HBA1C_CHANGE).append(System.lineSeparator());
		str.append("USE_CALIBRATED_SHEFFIELD_RET_SUBMODEL:\t").append(USE_CALIBRATED_SHEFFIELD_RET_SUBMODEL).append(System.lineSeparator());
		str.append("CALIBRATION_COEF_BGRET:\t").append(CALIBRATION_COEF_BGRET).append(System.lineSeparator());
		str.append("CALIBRATION_COEF_BETA_BGRET:\t").append(CALIBRATION_COEF_BETA_BGRET).append(System.lineSeparator());
		for (Map.Entry<String, Double> initP : INIT_PROP.entrySet()) {
			str.append("P_INIT_").append(initP.getKey()).append("\t").append(initP.getValue()).append(System.lineSeparator());
		}
		str.append("DEF_DISCOUNT_RATE:\t").append(DEF_DISCOUNT_RATE).append(System.lineSeparator());
		str.append("DEF_U_GENERAL_POP:\t").append(DEF_U_GENERAL_POP).append(System.lineSeparator());
		str.append("DEF_C_DNC:\t").append(DEF_C_DNC.VALUE + " (" + DEF_C_DNC.YEAR + ")").append(System.lineSeparator());
		str.append("DEF_DU_DNC:\t").append(DEF_DU_DNC[0] + " (SD:" + DEF_DU_DNC[1] + ")").append(System.lineSeparator());
		str.append("DEF_SECOND_ORDER_VARIATION (COST, UTIL, PROB):\t").append(DEF_SECOND_ORDER_VARIATION.COST+"\t").append(DEF_SECOND_ORDER_VARIATION.UTILITY+"\t").append(DEF_SECOND_ORDER_VARIATION.PROBABILITY).append(System.lineSeparator());
		return str.toString();
	}

}
