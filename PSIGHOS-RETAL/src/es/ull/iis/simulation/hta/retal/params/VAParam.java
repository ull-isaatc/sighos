/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
// TODO: Add second-order estimates
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
	/** The VA at incidence of each CNV stage */
	private final static TreeMap<CNVStage, double[]> VA_AT_INCIDENCE_CNV = new TreeMap<CNVStage, double[]>();
	
	static {
		for (int i = 0; i < CNVStage.ALL_STAGES.length; i++)
			VA_AT_INCIDENCE_CNV.put(CNVStage.ALL_STAGES[i], VA_AT_INCIDENCE_CNV_VALUES[i]);
	}

//	private final VAProgressionForEF_JF progEF_JF;
//	private final VAProgressionForSF progSF;
	private final VAProgressionForCNV progCNV;
	private final VAProgressionForGA progGA;
	private final VAProgressionForDR progDR;
	
	/**
	 * @param simul
	 * @param secondOrder
	 */
	public VAParam() {
		super();
		progGA = new VAProgressionForGA();
		progCNV = new VAProgressionForCNV();
//		progEF_JF = new VAProgressionForEF_JF(secondOrder);
//		progSF = new VAProgressionForSF(secondOrder);
		progDR = new VAProgressionForDR();
	}

	/**
	 * Returns the initial VA when the eye is already affected.
	 * A priori, it should be used only for DR
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return
	 */
	public double getInitialVA(RetalPatient pat, int eyeIndex) {
		return progDR.getInitialVA(pat, eyeIndex);
	}
	
	/**
	 * Returns the visual acuity of an eye upon incidence of a new stage in the ARMD progression. 
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
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
	 * Merges two lists of visual acuity changes. Each list contains at least one change, due at the end of the studied period. 
	 * The resulting list uses the worst state possible between both lists, by calculating the missing values as a linear interpolation.
	 * @param vaAtStart Visual acuity before all the changes start
	 * @param changes1 Changes due to eye disease 1
	 * @param changes2 Changes due to eye disease 2
	 * @return The worst possible list of changes in visual acuity for a patient affected by two different problems.
	 */
	protected ArrayList<VAProgressionPair> mergeVAProgressions(RetalPatient pat, double vaAtStart, AbstractList<VAProgressionPair> changes1, AbstractList<VAProgressionPair> changes2) {
		final Iterator<VAProgressionPair> iter1 = changes1.iterator();
		final Iterator<VAProgressionPair> iter2 = changes2.iterator();
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>(changes1.size() + changes2.size() - 1);
		
		VAProgressionPair pair1 = iter1.next();
		VAProgressionPair pair2 = iter2.next();
		double lastVA1 = vaAtStart;
		double lastVA2 = vaAtStart;
		long t1 = pair1.timeToChange;
		long t2 = pair2.timeToChange;
		while (iter1.hasNext() || iter2.hasNext()) {
			if (t1 == 0) {
				lastVA1 = pair1.va;
				pair1 = iter1.next();
				t1 = pair1.timeToChange;
			}
			else if (t2 == 0) {
				lastVA2 = pair2.va;
				pair2 = iter2.next();
				t2 = pair2.timeToChange;
			}
			else if (t1 < t2) {
				// Update time of pair 2
				t2 = t2 - t1;
				// Interpolate VA in pair 2
				lastVA2 = lastVA2 + (pair2.va - lastVA2) / t1;
				// Take the worst VA
				changes.add(new VAProgressionPair(t1, (lastVA2 > pair1.va) ? lastVA2 : pair1.va));
				// Update pair 1
				lastVA1 = pair1.va;
				pair1 = iter1.next();
				t1 = pair1.timeToChange;
			}
			else if (t2 < t1) {
				// Update time of pair 1
				t1 = t1 - t2;
				// Interpolate VA in pair 1
				lastVA1 = lastVA1 + (pair1.va - lastVA1) / t2;
				// Take the worst VA
				changes.add(new VAProgressionPair(t2, (lastVA1 > pair2.va) ? lastVA1 : pair2.va));
				// Update pair 2
				lastVA2 = pair2.va;
				pair2 = iter2.next();
				t2 = pair2.timeToChange;
			}
			else {
				changes.add((pair1.va > pair2.va) ? pair1 : pair2);
				// Update pair 1
				if (iter1.hasNext()) {
					pair1 = iter1.next();
					lastVA1 = pair1.va;
					t1 = pair1.timeToChange;
				}
				// Update pair 2
				if (iter2.hasNext()) {
					pair2 = iter2.next();
					lastVA2 = pair2.va;
					t2 = pair2.timeToChange;
				}
			}
		}
		// This point is reached when both have no next pair
		changes.add(new VAProgressionPair(t2, (pair1.va > pair2.va) ? pair1.va : pair2.va));
		return changes;
	}

	private boolean checkChanges(ArrayList<VAProgressionPair> changes, long ts) {
		long sum = 0;
		for (VAProgressionPair pair : changes) {
			sum += pair.timeToChange;
		}
		return (sum == ts);
	}
	
	/**
	 * Returns a set of 
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return
	 */
	// FIXME: All progressions must be reviewed to start from last change in VA
	public ArrayList<VAProgressionPair> getVAProgression(RetalPatient pat, int eyeIndex, EyeState incidentState, CNVStage incidentCNVStage) {
		ArrayList<VAProgressionPair> changes = null;
		final double vaAtStart = pat.getVA(eyeIndex);
		// If the patient had the worst VA, it remains the same
		if (vaAtStart == VisualAcuity.MAX_LOGMAR) {
			changes = new ArrayList<VAProgressionPair>();
			changes.add(new VAProgressionPair(pat.getSimulation().getTs() - pat.getLastVAChangeTs(eyeIndex), vaAtStart));
		}
		else {				
			// Computes the new VA expected for the new eye state; if no new state is expected (death), uses the current VA 
			final double incidentVA = (incidentState == null) ? vaAtStart : Math.max(vaAtStart, getVAAtIncidence(incidentState, incidentCNVStage));
			
			final EnumSet<EyeState> affectedEye = pat.getEyeState(eyeIndex);
			// No progression but the incident VA is expected if the eye is healthy
			if (affectedEye.contains(EyeState.HEALTHY)) {
				changes = new ArrayList<VAProgressionPair>();
				changes.add(new VAProgressionPair(pat.getSimulation().getTs() - pat.getLastVAChangeTs(eyeIndex), incidentVA));
			}
			else {
				if (affectedEye.contains(EyeState.AMD_CNV)) {
					changes = progCNV.getVAProgression(pat, eyeIndex, incidentVA);
//					final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
//					// Subfoveal lesion
//					if (stage.getPosition() == CNVStage.Position.SF)  {
//						changes = progSF.getVAProgression(pat, eyeIndex, incidentVA);
//					}
//					// Juxtafoveal or extrafoveal lesion
//					else {
//						changes = progEF_JF.getVAProgression(pat, eyeIndex, incidentVA); 
//					}
				}
				else if (affectedEye.contains(EyeState.AMD_GA)) {
					changes = progGA.getVAProgression(pat, eyeIndex, incidentVA); 
				}
				else {
					// No progression but the incident VA is expected if the eye has EARM
					changes = new ArrayList<VAProgressionPair>();
					changes.add(new VAProgressionPair(pat.getSimulation().getTs() - pat.getLastVAChangeTs(eyeIndex), incidentVA));
				}
				if (affectedEye.contains(EyeState.CSME) || affectedEye.contains(EyeState.NPDR) || affectedEye.contains(EyeState.NON_HR_PDR)
						|| affectedEye.contains(EyeState.HR_PDR)) {
					ArrayList<VAProgressionPair> changesDR = progDR.getVAProgression(pat, eyeIndex, incidentVA);
					// If the patient was already affected by ARMD, both progressions have to be merged
					if (changes != null) {
						if (!checkChanges(changes, pat.getSimulation().getTs() - pat.getLastVAChangeTs(eyeIndex)))
							pat.error("Wrongly computed VA progression in an ARMD eye");
						if (!checkChanges(changesDR, pat.getSimulation().getTs() - pat.getLastVAChangeTs(eyeIndex)))
							pat.error("Wrongly computed VA progression in an DR eye");
						changes = mergeVAProgressions(pat, vaAtStart, changes, changesDR);
					}
					else {
						changes = changesDR;
					}
				}
			}
		}
		return changes;
	}
}
