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
	public static double U_GENERAL_POP = 0.911400915;
	
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
		str.append("U_GENERAL_POP:\t").append(U_GENERAL_POP).append(System.lineSeparator());
		return str.toString();
	}

}
