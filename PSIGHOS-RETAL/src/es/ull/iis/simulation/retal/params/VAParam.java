/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;
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
	 * Returns the visual acuity of an eye upon incidence of a new stage in the ARMD progression. 
	 * @param pat
	 * @param eyeIndex
	 * @return The visual acuity of an eye upon incidence of a new stage in the ARMD progression.
	 */
	public double getVAAtIncidence(EyeState incidentState, CNVStage incidentCNVStage) {
		final double va;
		
		if (incidentState.equals(EyeState.AMD_CNV)) {
			va = VA_AT_INCIDENCE_CNV.get(incidentCNVStage)[0];
		}
		else if (incidentState.equals(EyeState.AMD_GA)) {	
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
	public ArrayList<VAProgressionPair> getVAProgression(OphthalmologicPatient pat, int eyeIndex, EyeState incidentState, CNVStage incidentCNVStage) {
		ArrayList<VAProgressionPair> changes;
		final double vaAtStart = pat.getVA(eyeIndex);
		// If the patient had the worst VA, it remains the same
		if (vaAtStart == VisualAcuity.MAX_LOGMAR) {
			changes = new ArrayList<VAProgressionPair>();
			changes.add(new VAProgressionPair(simul.getTs() - pat.getLastVAChangeTs(eyeIndex), vaAtStart));
		}
		else {				
			// Computes the new VA expected for the new eye state; if no new state is expected (death), uses the current VA 
			final double incidentVA = (incidentState == null) ? vaAtStart : Math.max(vaAtStart, getVAAtIncidence(incidentState, incidentCNVStage));
			
			final EnumSet<EyeState> affectedEye = pat.getEyeState(eyeIndex);
			if (affectedEye.contains(EyeState.AMD_CNV)) {
				final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
				// Subfoveal lesion
				if (stage.getPosition() == CNVStage.Position.SF)  {
					changes = progSF.getVAProgression(pat, eyeIndex, incidentVA);
				}
				// Juxtafoveal or extrafoveal lesion
				else {
					changes = progEF_JF.getVAProgression(pat, eyeIndex, incidentVA); 
				}
			}
			else if (affectedEye.contains(EyeState.AMD_GA)) {
				changes = progGA.getVAProgression(pat, eyeIndex, incidentVA); 
			}
			else {
				// No progression but the incident VA is expected if the eye is healthy or has EARM
				changes = new ArrayList<VAProgressionPair>();
				changes.add(new VAProgressionPair(simul.getTs() - pat.getLastVAChangeTs(eyeIndex), incidentVA));
			}
		}
		return changes;
	}
}
