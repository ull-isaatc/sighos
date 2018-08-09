/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.hta.retal.RetalPatient;

/**
 * @author Iván Castilla Rodríguez
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
	public ARMDParams() {
		super();
		timeToEARM = new TimeToEARMParam();
		timeToE1AMD = new TimeToE1AMDParam();
		timeToE2AMD = new TimeToE2AMDParam();
		timeToAMDFromEARM = new TimeToAMDFromEARMParam();		
		timeToCNVFromGA = new TimeToCNVFromGAParam();
		timeToCNVStage = new CNVStageParam();
		clinicalPresentation = new ClinicalPresentationARMDParam();
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM(RetalPatient pat) {
		return timeToEARM.getValidatedTimeToEvent(pat);
	}

	/**
	 * @return the timeToE1AMD
	 */
	public EyeStateAndValue getTimeToE1AMD(RetalPatient pat) {
		return timeToE1AMD.getValidatedTimeToEventAndState(pat);
	}

	/**
	 * @return the timeToE2AMD
	 */
	public EyeStateAndValue getTimeToE2AMD(RetalPatient pat) {
		return timeToE2AMD.getValidatedTimeToEventAndState(pat);
	}

	/**
	 * @return the timeToAMDFromEARM
	 */
	public EyeStateAndValue getTimeToAMDFromEARM(RetalPatient pat, int eyeIndex) {
		return timeToAMDFromEARM.getValidatedTimeToEventAndState(pat, eyeIndex);
	}

	/**
	 * @return the timeToCNVFromGA
	 */
	public long getTimeToCNVFromGA(RetalPatient pat, int eyeIndex) {
		return timeToCNVFromGA.getValidatedTimeToEvent(pat, eyeIndex);
	}

	/**
	 * Return the 
	 * @return
	 */
	public CNVStage getInitialCNVStage(RetalPatient pat, int eyeIndex) {
		return timeToCNVStage.getInitialTypeAndPosition(pat, eyeIndex);
	}

	public CNVStageAndValue getTimeToNextCNVStage(RetalPatient pat, int eyeIndex) {
		return timeToCNVStage.getValidatedTimeToEvent(pat, eyeIndex);
	}
	
	public double getProbabilityClinicalPresentation(RetalPatient pat) {
		return clinicalPresentation.getProbability(pat);
	}
	
	public long getTimeToClinicalPresentation(RetalPatient pat) {
		return clinicalPresentation.getValidatedTimeToEvent(pat);
	}
}
