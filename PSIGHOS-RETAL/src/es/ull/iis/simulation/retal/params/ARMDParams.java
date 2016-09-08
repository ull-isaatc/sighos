/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.retal.Patient;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class ARMDParams extends ModelParams {
	/** True if the parameters are adjusted by calibration; else false */
	public final static boolean CALIBRATED = true;
	private final TimeToEARMParam timeToEARM;
	private final TimeToE1AMDParam timeToE1AMD;
	private final TimeToE2AMDParam timeToE2AMD;
	private final TimeToAMDFromEARMParam timeToAMDFromEARM;
	private final TimeToCNVFromGAParam timeToCNVFromGA;
	private final CNVStageParam timeToCNVStage;
	private final ClinicalPresentationARMDParam clinicalPresentation;
	
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
		timeToCNVStage = new CNVStageParam(baseCase);
		clinicalPresentation = new ClinicalPresentationARMDParam(baseCase);
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM(Patient pat) {
		return timeToEARM.getValidatedTimeToEvent(pat);
	}

	/**
	 * @return the timeToE1AMD
	 */
	public EyeStateAndValue getTimeToE1AMD(Patient pat) {
		return timeToE1AMD.getValidatedTimeToEventAndState(pat);
	}

	/**
	 * @return the timeToE2AMD
	 */
	public EyeStateAndValue getTimeToE2AMD(Patient pat) {
		return timeToE2AMD.getValidatedTimeToEventAndState(pat);
	}

	/**
	 * @return the timeToAMDFromEARM
	 */
	public EyeStateAndValue getTimeToAMDFromEARM(Patient pat, int eyeIndex) {
		return timeToAMDFromEARM.getValidatedTimeToEventAndState(pat, eyeIndex);
	}

	/**
	 * @return the timeToCNVFromGA
	 */
	public long getTimeToCNVFromGA(Patient pat, int eyeIndex) {
		return timeToCNVFromGA.getValidatedTimeToEvent(pat, eyeIndex);
	}

	/**
	 * Return the 
	 * @return
	 */
	public CNVStage getInitialCNVStage(Patient pat, int eyeIndex) {
		return timeToCNVStage.getInitialTypeAndPosition(pat, eyeIndex);
	}

	public CNVStageAndValue getTimeToNextCNVStage(Patient pat, int eyeIndex) {
		return timeToCNVStage.getValidatedTimeToEvent(pat, eyeIndex);
	}
	
	public double getProbabilityClinicalPresentation(Patient pat) {
		return clinicalPresentation.getProbability(pat);
	}
	
	public long getTimeToClinicalPresentation(Patient pat) {
		return clinicalPresentation.getValidatedTimeToEvent(pat);
	}
}