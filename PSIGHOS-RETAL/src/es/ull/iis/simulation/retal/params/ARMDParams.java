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
	private final TimeToAMDParam timeToAMD;
	private final TimeToE2CNVParam timeToE2CNV;
	private final TimeToE2GAParam timeToE2GA;
	private final TimeToAMDFromEARMParam timeToAMDFromEARM;
	private final TimeToCNVFromGAParam timeToE1CNV;

	/**
	 * 
	 */
	public ARMDParams(boolean baseCase) {
		super(baseCase);
		timeToEARM = new TimeToEARMParam(baseCase);
		timeToAMD = new TimeToAMDParam(baseCase);
		timeToE2CNV = new TimeToE2CNVParam(baseCase);
		timeToE2GA = new TimeToE2GAParam(baseCase);
		timeToAMDFromEARM = new TimeToAMDFromEARMParam(baseCase);		
		timeToE1CNV = new TimeToCNVFromGAParam(baseCase);
	}

	/**
	 * @return the timeToEARM
	 */
	public TimeToEARMParam getTimeToEARM() {
		return timeToEARM;
	}

	/**
	 * @return the timeToAMD
	 */
	public TimeToAMDParam getTimeToAMD() {
		return timeToAMD;
	}

	/**
	 * @return the timeToE2CNV
	 */
	public TimeToE2CNVParam getTimeToE2CNV() {
		return timeToE2CNV;
	}

	/**
	 * @return the timeToE2GA
	 */
	public TimeToE2GAParam getTimeToE2GA() {
		return timeToE2GA;
	}

	/**
	 * @return the timeToAMDFromEARM
	 */
	public TimeToAMDFromEARMParam getTimeToAMDFromEARM() {
		return timeToAMDFromEARM;
	}

	/**
	 * @return the timeToE1CNV
	 */
	public TimeToCNVFromGAParam getTimeToE1CNV() {
		return timeToE1CNV;
	}

}
