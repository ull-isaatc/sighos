/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
// FIXME: Currently only using first-order estimates
public class VAParam extends Param {
	public static class VAProgressionPair {
		public long timeToChange;
		public double va;
		
		public VAProgressionPair(long ts, double va) {
			this.timeToChange = ts;
			this.va = va;
		}		
	}
	
	/**
	 * Source: Karnon model
	 */
	private final static double[] VA_AT_INCIDENCE_GA = {0.150298257, 0.086371351};
	/**
	 * Source: Karnon model
	 */
	private final static double[][] VA_AT_INCIDENCE_CNV_VALUES = {
			{0.255174268, 0.142468756},
			{0.400182418, 0.176357241},
			{0.763685389, 0.25813546},
			{0.255174268, 0.142468756},
			{0.400182418, 0.176357241},
			{0.763685389, 0.25813546},
			{0.245667743, 0.144480836},
			{0.404284814, 0.172330988},
			{0.757729834, 0.255845865}
		};
	private final static TreeMap<CNVStage, double[]> VA_AT_INCIDENCE_CNV = new TreeMap<CNVStage, double[]>();

	static {
		for (int i = 0; i < CNVStage.ALL_STAGES.length; i++)
			VA_AT_INCIDENCE_CNV.put(CNVStage.ALL_STAGES[i], VA_AT_INCIDENCE_CNV_VALUES[i]);
	}

	private final VAProgressionForEF_JF progEF_JF;
	private final VAProgressionForSF progSF;
	private final VAProgressionForGA progGA;
	
	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		progGA = new VAProgressionForGA(simul, baseCase);
		progEF_JF = new VAProgressionForEF_JF(simul, baseCase);
		progSF = new VAProgressionForSF(simul, baseCase);
	}

	/**
	 * 
	 * @param pat
	 * @param eyeIndex
	 * @return
	 */
	public double getVAAtIncidence(OphthalmologicPatient pat, int eyeIndex) {
		final EnumSet<EyeState> affectedEye = pat.getEyeState(eyeIndex);
		final double va;
		
		if (affectedEye.contains(EyeState.AMD_CNV)) {
			va = VA_AT_INCIDENCE_CNV.get(pat.getCurrentCNVStage(eyeIndex))[0];
		}
		else if (affectedEye.contains(EyeState.AMD_GA)) {	
			va = VA_AT_INCIDENCE_GA[0];
		}
		else {
			va = 0.0;
		}
		return va;
	}
	
	/**
	 * Returns a set of 
	 * @param pat
	 * @param eyeIndex
	 * @return
	 */
	public VAProgressionPair[] getVAProgression(OphthalmologicPatient pat, int eyeIndex) {
		final EnumSet<EyeState> affectedEye = pat.getEyeState(eyeIndex);
		
		if (affectedEye.contains(EyeState.AMD_CNV)) {
			final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
			// Subfoveal lesion
			if (stage.getPosition() == CNVStage.Position.SF)  {
				return progSF.getLevelChanges(pat, eyeIndex);
			}
			// Juxtafoveal or extrafoveal lesion
			else {
				final long endT = simul.getTs();
				return progEF_JF.getVA(pat, eyeIndex); 
			}
		}
		else if (affectedEye.contains(EyeState.AMD_GA)) {
			return progGA.getProgression(pat, eyeIndex); 
		}
		return null;
	}
}
