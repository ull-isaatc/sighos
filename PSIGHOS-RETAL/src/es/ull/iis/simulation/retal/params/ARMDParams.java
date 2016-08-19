/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.RETALSimulation;

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
	private final VAParam vaParam;
	
	/**
	 * 
	 */
	public ARMDParams(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		timeToEARM = new TimeToEARMParam(simul, baseCase);
		timeToE1AMD = new TimeToE1AMDParam(simul, baseCase);
		timeToE2AMD = new TimeToE2AMDParam(simul, baseCase);
		timeToAMDFromEARM = new TimeToAMDFromEARMParam(simul, baseCase);		
		timeToCNVFromGA = new TimeToCNVFromGAParam(simul, baseCase);
		timeToCNVStage = new CNVStageParam(simul, baseCase);
		vaParam = new VAParam(simul, baseCase);
	}

	/**
	 * @return the timeToEARM
	 */
	public long getTimeToEARM(OphthalmologicPatient pat) {
		return timeToEARM.getValidatedTimeToEvent(pat);
	}

	/**
	 * @return the timeToE1AMD
	 */
	public EyeStateAndValue getTimeToE1AMD(OphthalmologicPatient pat) {
		return timeToE1AMD.getValidatedTimeToEventAndState(pat);
	}

	/**
	 * @return the timeToE2AMD
	 */
	public EyeStateAndValue getTimeToE2AMD(OphthalmologicPatient pat) {
		return timeToE2AMD.getValidatedTimeToEventAndState(pat);
	}

	/**
	 * @return the timeToAMDFromEARM
	 */
	public EyeStateAndValue getTimeToAMDFromEARM(OphthalmologicPatient pat, int eyeIndex) {
		return timeToAMDFromEARM.getValidatedTimeToEventAndState(pat, eyeIndex);
	}

	/**
	 * @return the timeToCNVFromGA
	 */
	public long getTimeToCNVFromGA(OphthalmologicPatient pat, int eyeIndex) {
		return timeToCNVFromGA.getValidatedTimeToEvent(pat, eyeIndex);
	}

	/**
	 * Return the 
	 * @return
	 */
	public CNVStage getInitialCNVStage(OphthalmologicPatient pat, int eyeIndex) {
		return timeToCNVStage.getInitialTypeAndPosition(pat, eyeIndex);
	}

	public CNVStageAndValue getTimeToNextCNVStage(OphthalmologicPatient pat, int eyeIndex) {
		return timeToCNVStage.getValidatedTimeToEvent(pat, eyeIndex);
	}
	
	public ArrayList<VAProgressionPair> getVAProgression(OphthalmologicPatient pat, int eyeIndex, EyeState incidentState) {
		return vaParam.getVAProgression(pat, eyeIndex, incidentState, null);
	}

	public ArrayList<VAProgressionPair> getVAProgression(OphthalmologicPatient pat, int eyeIndex, CNVStage incidentCNVStage) {
		return vaParam.getVAProgression(pat, eyeIndex, EyeState.AMD_CNV, incidentCNVStage);
	}

	public ArrayList<VAProgressionPair> getVAProgressionToDeath(OphthalmologicPatient pat, int eyeIndex) {
		return vaParam.getVAProgression(pat, eyeIndex, null, null);
	}
}
