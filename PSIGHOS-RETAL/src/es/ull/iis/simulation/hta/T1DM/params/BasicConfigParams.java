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
	/** Maximum age reachable by patients */
	public final static int MAX_AGE = 100;
	/** Identifier code for men */
	public final static int MAN = 0;
	/** Identifier code for women */
	public final static int WOMAN = 1;
	/** Minimum time among consecutive events. */  
	public final static long MIN_TIME_TO_EVENT = SIMUNIT.convert(TimeStamp.getMonth());

	/** Year of the assessment: useful for updating costs */
	public final static int STUDY_YEAR = 2018;
	
	/**
	 * This constructor should not be needed.
	 */
	private BasicConfigParams() {
	}

}
