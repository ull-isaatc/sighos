/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ARMDParams extends ModelParams {

	private final static double [][] P_CNV = {
			{60, 65, 1, 1},
			{65, 70, 2, 50},
			{70, 75, 7, 10},
			{75, 80, 9, 14},
			{80, CommonParams.MAX_AGE, 9, 17}};
	
	/** Proportion of CNV (against GA) in first eye incident AMD */
	private final TreeMap<Integer, Double> pCNV = new TreeMap<Integer, Double>();
	
	private final TimeToEARMParam timeToEARM;
	private final TimeToAMDParam timeToAMD;
	private final TimeToE2CNVParam timeToE2CNV;
	private final TimeToE2GAParam timeToE2GA;
	private final TimeToAMDFromEARMParam timeToAMDFromEARM;
	private final TimeToE1CNVParam timeToE1CNV;

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
		timeToE1CNV = new TimeToE1CNVParam(baseCase);
		
		if (baseCase) {
			// Initialize proportion of CNV (against GA) in first eye incident AMD
			for (int i = 0; i < P_CNV.length; i++) {
				pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
			}
		}
		else {
			// FIXME: Replace by random distributions
			// Initialize proportion of CNV (against GA) in first eye incident AMD
			for (int i = 0; i < P_CNV.length; i++) {
				pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
			}
		}
	}

	/**
	 * 
	 * @param age
	 * @return
	 */
	public double getProbabilityCNV(double age) {
		final Map.Entry<Integer, Double> entry = pCNV.lowerEntry((int)age);
		if (entry != null)
			return entry.getValue();
		return 0.0;
	}

	/**
	 * @return Years to first eye incidence of early ARM; Double.MAX_VALUE if event is not happening
	 */
	public double getEARMTime(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToEARM.getTimeToEvent(age, firstEye, fellowEye);
	}
	
	/**
	 * @return the time to first eye incidence of AMD
	 */
	public double getAMDTime(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToAMD.getTimeToEvent(age, firstEye, fellowEye);
	}

	/**
	 * 
	 * @param age
	 * @param fellowEye
	 * @return the time to first eye progression from ARM to AMD
	 */
	public double getEARM2AMDTime(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToAMDFromEARM.getTimeToEvent(age, firstEye, fellowEye);
	}

	/**
	 * 
	 * @param age
	 * @param fellowEye
	 * @return
	 */
	public double getGA2CNVTime(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToE1CNV.getTimeToEvent(age, firstEye, fellowEye);
	}

	/**
	 * 
	 * @param age
	 * @param firstEye
	 * @return
	 */
	public double getE2Time2GA(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToE2GA.getTimeToEvent(age, firstEye, fellowEye);
	}

	/**
	 * 
	 * @param age
	 * @param firstEye
	 * @return
	 */
	public double getE2Time2CNV(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToE2CNV.getTimeToEvent(age, firstEye, fellowEye);
	}
}
