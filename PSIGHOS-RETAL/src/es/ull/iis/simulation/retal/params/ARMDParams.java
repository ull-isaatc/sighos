/**
 * 
 */
package es.ull.iis.simulation.retal.params;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ARMDParams extends ModelParams {

	private final TimeToEARMParam timeToEARM;
	private final TimeToE1AMDParam timeToE1AMD;
	private final TimeToE2AMDParam timeToE2AMD;
	private final TimeToAMDFromEARMParam timeToAMDFromEARM;
	private final TimeToCNVFromGAParam timeToCNVFromGA;

	/**
	 * 
	 */
	public ARMDParams(boolean baseCase) {
		super(baseCase);
		timeToEARM = new TimeToEARMParam(baseCase);
		timeToE1AMD = new TimeToE1AMDParam(baseCase);
		timeToE2AMD = new TimeToE2AMDParam(baseCase);
		timeToAMDFromEARM = new TimeToAMDFromEARMParam(baseCase);		
		timeToCNVFromGA = new TimeToCNVFromGAParam(baseCase);
	}

	/**
	 * @return the timeToEARM
	 */
	public TimeToEARMParam getTimeToEARM() {
		return timeToEARM;
	}

	/**
	 * @return the timeToE1AMD
	 */
	public TimeToE1AMDParam getTimeToE1AMD() {
		return timeToE1AMD;
	}

	/**
	 * @return the timeToE2AMD
	 */
	public TimeToE2AMDParam getTimeToE2AMD() {
		return timeToE2AMD;
	}

	/**
	 * @return the timeToAMDFromEARM
	 */
	public TimeToAMDFromEARMParam getTimeToAMDFromEARM() {
		return timeToAMDFromEARM;
	}

	/**
	 * @return the timeToCNVFromGA
	 */
	public TimeToCNVFromGAParam getTimeToCNVFromGA() {
		return timeToCNVFromGA;
	}

}
