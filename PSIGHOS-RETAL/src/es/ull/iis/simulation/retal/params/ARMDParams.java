/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Map;
import java.util.TreeMap;

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
	public TimeToE1CNVParam getTimeToE1CNV() {
		return timeToE1CNV;
	}

}
